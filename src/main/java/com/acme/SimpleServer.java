package com.acme;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class SimpleServer
{
    public static void main( String[] args ) throws Exception
    {
       SimpleServer simpleServer = new SimpleServer();
        simpleServer.doit();

    }

    public void doit() throws Exception{
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(eventLoopGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.localAddress(new InetSocketAddress(5151));
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(
                            new MyServerHandler());
                }
            });

            ChannelFuture channelFuture = bootstrap.bind().sync();
            System.out.println("Server is started and listen on " + channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }


    public class MyServerHandler extends
            ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx,
                                Object msg) {
            ByteBuf in = (ByteBuf) msg;
            System.out.println("got Message: " + in.toString(CharsetUtil.UTF_8));
            ctx.write("Server Response ==>>"+in);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx,
                                    Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }

    }


}


