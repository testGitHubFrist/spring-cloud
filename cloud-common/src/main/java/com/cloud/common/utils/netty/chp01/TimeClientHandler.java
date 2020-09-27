package com.cloud.common.utils.netty.chp01;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

class TimeClientHandler extends ChannelInboundHandlerAdapter {
        private  final ByteBuf msg;
        public TimeClientHandler() {
            byte[] req =" hello server".getBytes();
             msg= Unpooled.buffer(req.length);
            msg.writeBytes(req);
        }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       ctx.writeAndFlush(msg);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //处理接受数据
        ByteBuf byteBuf = (ByteBuf)msg;
        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
        String body = new String(req,"UTF-8");
        System.out.println("client  receive: "+body);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       ctx.close();
    }
}