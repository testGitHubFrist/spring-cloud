package com.cloud.common.utils.netty.chapter05;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Date;

public class WebScoketServer {

    private static WebSocketServerHandshaker handshaker;

    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {

            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            //一般处理
                            ch.pipeline().addLast("http-codec", new HttpServerCodec());
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());

                            //协议处理
                            ch.pipeline().addLast("webSocket-handler", new WebSocketServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(8081).sync();
            System.out.println("web socket server is started ...");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


    static class WebSocketServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //添加连接
            System.out.println("客户端加入连接：" + ctx.channel());
            ChannelSupervise.addChannel(ctx.channel());
        }


        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            //断开连接
            System.out.println("客户端断开连接：" + ctx.channel());
            ChannelSupervise.removeChannel(ctx.channel());
        }


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            //传统http接入
            if (msg instanceof FullHttpRequest) {
                handleHttpRequest(ctx, (FullHttpRequest) msg);
            }

            //webSocket接入
            if (msg instanceof WebSocketFrame) {
                handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }


        private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {

            if (response.getStatus().code() != 200) {
                ByteBuf byteBuf = Unpooled.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8);
                response.content().writeBytes(byteBuf);
                byteBuf.release();
                HttpHeaders.setContentLength(response, response.content().readableBytes());
            }

            ChannelFuture f = ctx.channel().writeAndFlush(response);
            // 如果是非Keep-Alive，关闭连接
            if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        }

        private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
            // 判断是否关闭链路的指令
            if (frame instanceof CloseWebSocketFrame) {
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
                return;
            }
            // 判断是否ping消息
            if (frame instanceof PingWebSocketFrame) {
                ctx.channel().write(
                        new PongWebSocketFrame(frame.content().retain()));
                return;
            }
            // 本例程仅支持文本消息，不支持二进制消息
            if (!(frame instanceof TextWebSocketFrame)) {
                System.out.println("本例程仅支持文本消息，不支持二进制消息");
                throw new UnsupportedOperationException(String.format(
                        "%s frame types not supported", frame.getClass().getName()));
            }
            // 返回应答消息
            String request = ((TextWebSocketFrame) frame).text();
            System.out.println("服务端收到：" + request);
            TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
                    + ctx.channel().id() + "：" + request);
            // 群发
            ChannelSupervise.send2All(tws);
            // 返回【谁发的发给谁】
            // ctx.channel().writeAndFlush(tws);
        }

        /**
         * 唯一的一次http请求，用于创建websocket
         * */
        private void handleHttpRequest(ChannelHandlerContext ctx,
                                       FullHttpRequest req) {
            //要求Upgrade为websocket，过滤掉get/Post
            if (!req.decoderResult().isSuccess()
                    || (!"websocket".equals(req.headers().get("Upgrade")))) {
                //若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
                sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                return;
            }
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    "ws://localhost:8081/websocket", null, false);
            handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory
                        .sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), req);
            }
        }

    }


    static class ChannelSupervise {
        private static ChannelGroup GlobalGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        private static ConcurrentMap<String, ChannelId> ChannelMap = new ConcurrentHashMap<>();

        public static void addChannel(Channel channel) {
            GlobalGroup.add(channel);
            ChannelMap.put(channel.id().asShortText(), channel.id());
        }

        public static void removeChannel(Channel channel) {
            GlobalGroup.remove(channel);
            ChannelMap.remove(channel.id().asShortText());
        }

        public static Channel findChannel(String id) {
            return GlobalGroup.find(ChannelMap.get(id));
        }

        public static void send2All(TextWebSocketFrame tws) {
            GlobalGroup.writeAndFlush(tws);
        }
    }


}
