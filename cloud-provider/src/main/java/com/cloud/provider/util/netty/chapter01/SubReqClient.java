package com.cloud.provider.util.netty.chapter01;

import com.cloud.entity.proto.SubscribeReqProto;
import com.cloud.entity.proto.SubscribeRespProto;
import com.google.common.collect.Lists;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.List;

public class SubReqClient {

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(new ProtobufDecoder(SubscribeRespProto.SubscribeResp.getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                            //处理业务
                            ch.pipeline().addLast(new SubReqClientHandler());


                        }
                    });
            ChannelFuture f = b.connect("127.0.0.1", 8080).sync();
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            group.shutdownGracefully();
        }
    }

    static class SubReqClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

            for (int i = 0; i < 30; i++) {
                ctx.write(createSubscribeReq(i));
            }
            ctx.flush();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            SubscribeRespProto.SubscribeResp resp = (SubscribeRespProto.SubscribeResp) msg;
            System.out.println("client receiver: " + resp.toString());
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

        private static SubscribeReqProto.SubscribeReq createSubscribeReq(int id) {
            SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();

            builder.setSubReqID(id);
            builder.setUserName("zhangsan");
            builder.setProductName("netty book");
            List<String> addressList = Lists.newArrayList();
            addressList.add("beijing");
            addressList.add("nanjing");
            builder.addAllAddress(addressList);
            return builder.build();
        }
    }
}
