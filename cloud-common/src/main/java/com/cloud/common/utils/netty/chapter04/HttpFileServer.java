package com.cloud.common.utils.netty.chapter04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class HttpFileServer {

    private static final String DEFAULT_URL = "/src/com/cloud/";

    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //解码器
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            ch.pipeline().addLast(new HttpFileServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(8080).sync();
            System.out.println("http 文件目录服务器启动，网站是: http://localhost:8080" + DEFAULT_URL);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    static class HttpFileServerHandler extends ChannelInboundHandlerAdapter {


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            FullHttpRequest request =(FullHttpRequest)msg;

            if (request.getDecoderResult().isSuccess()) {
                String error = "404";
                sendError(ctx, error);
                return;
            }

            //method 校验
            if (request.getMethod() != HttpMethod.GET) {
                sendError(ctx, "method_not_allowed");
                return;
            }
            final String uri = request.getUri();
            final String path = sanitizeUri(uri);
            if (StringUtils.isEmpty(path)) {
                sendError(ctx, "forbidden");
                return;
            }
            File file = new File(path);
            ctx.writeAndFlush(file.getName());
        }

        private void sendError(ChannelHandlerContext ctx, String errorMsg) {

            ctx.writeAndFlush(errorMsg);
        }

        //uri解码
        private String sanitizeUri(String uri) {
            try {
                uri = URLDecoder.decode(uri, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                try {
                    uri = URLDecoder.decode(uri, "ISO-8859-1");
                } catch (UnsupportedEncodingException e1) {
                    throw new Error();
                }
            }
            uri = uri.replace("/", File.separator);
            if (uri.contains(File.separator + ".")
                    || uri.contains("." + File.separator)
                    || uri.startsWith(".")
                    || uri.endsWith(".")) {
                return null;
            }
            return System.getProperty("user.dir" + File.separator + uri);
        }

    }
}
