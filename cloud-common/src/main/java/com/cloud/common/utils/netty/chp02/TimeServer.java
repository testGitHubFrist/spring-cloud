package com.cloud.common.utils.netty.chp02;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.math.BigInteger;
import java.util.Date;

public class TimeServer {

    public  static  void  bind(int port ) {
        //配置服务端的NIO线程组
        EventLoopGroup  bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childHandler(new ChildChannelHandler());
            ChannelFuture f = b.bind(port).sync();
            //等待服务端监听d端口关闭
            f.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //优雅推出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    static  class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            //解决半包问题
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
            ch.pipeline().addLast(new StringDecoder());
            //增加处理业务类
            ch.pipeline().addLast(new TimeServerHandler());
        }
    }

    static class TimeServerHandler extends ChannelInboundHandlerAdapter {

        int counter = 0;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            //处理接受数据
            String body =(String) msg;
            System.out.println("server receive "+body +" counter ："+counter++);

            //设置返回数据
            String current ="query time order ".equalsIgnoreCase(body)?new Date().toString():"bad order";
            current = current+System.getProperty("line.separator");
            ByteBuf resp = Unpooled.copiedBuffer(current.getBytes());
            ctx.writeAndFlush(resp);
        }
    }


    public static void main(String[] args) {
        bind(8080);
    }
}
