package com.cloud.common.utils.netty.chp02;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class TimeClientHandler extends ChannelInboundHandlerAdapter {
    private  byte[] req;
    public TimeClientHandler() {
        req =("query time order "+System.getProperty("line.separator")).getBytes();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf msg =null;
        for (int i = 0;i<100;i++){
            msg= Unpooled.buffer(req.length);
            msg.writeBytes(req);
            ctx.writeAndFlush(msg);
        }

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //处理接受数据
       String body = (String)msg;
        System.out.println("client  receive: "+body);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       ctx.close();
    }
}