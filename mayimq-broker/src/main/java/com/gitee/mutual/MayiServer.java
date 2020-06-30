package com.gitee.mutual;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class MayiServer implements Runnable{
	
	private final static Logger logger = LoggerFactory.getLogger(MayiServer.class);

	private final int port;

	public MayiServer(int port) {
		this.port = port;
	}
	@Override
	public void run() {
//		final NioServerHandler serverHandler = new NioServerHandler(null,null);
		EventLoopGroup group = new NioEventLoopGroup();

		ServerBootstrap bs = new ServerBootstrap();
		bs.group(group)
		.channel(NioServerSocketChannel.class)
		.localAddress(new InetSocketAddress(port))
		.childHandler(new ChannelInitializerImp());
		/*.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(serverHandler);
			}
		});*/

		try {
			ChannelFuture sync = bs.bind().sync();
			logger.info("MayiMq start SUCCESS");
			sync.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			try {
				group.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private static class ChannelInitializerImp extends ChannelInitializer<Channel> {

		@Override
		protected void initChannel(Channel ch) throws Exception {
			 //回车符做了分割
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
            ch.pipeline().addLast(new StringDecoder());
			
			ch.pipeline().addLast(new MayiServerHandler());
			ch.pipeline().addLast(new StringEncoder());
			
		}

	}

	public static void main(String[] args) throws InterruptedException {
		//		NioServer echoServer = new NioServer(9999);
		System.out.println("服务器即将启动");
		//		echoServer.start();
		MayiServer nioServer = new MayiServer(9999);
		new Thread(nioServer).start();
		System.out.println("服务器关闭");
	}


}
