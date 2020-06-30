package com.gitee.mutual.net;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


public class MayiClient implements Runnable{

	/*是否用户主动关闭连接的标志值*/
	private volatile boolean userClose = false;
	/*连接是否成功关闭的标志值*/
	private volatile boolean connected = false;

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	private final String host ;
	private final int port ;
	@SuppressWarnings("unused")
	private Channel channel;

	EventLoopGroup group = new NioEventLoopGroup();

	public MayiClient(String host, int port) {
		this.host = host;
		this.port = port;
	}


	public boolean isConnected() {
		return connected;
	}

	@Override
	public void run() {
		connect(host, port);
	}
	public void connect(String host, int port) {
		try {
			Bootstrap bs = new Bootstrap();
			bs.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_BROADCAST, true)
			//			.remoteAddress(new InetSocketAddress(host, port))
			.handler(new ChannelInitializerImpl());
			/*.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new NioProducerHandler());
					context = addLast.firstContext();
					System.out.println(context.toString());
				}
			});*/

			try {

				ChannelFuture sync = bs.connect(new InetSocketAddress(host, port)).sync();
				this.channel = sync.sync().channel();
				synchronized (this) {
					this.connected = true;
					this.notifyAll();
				}
				sync.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally{
			if(!userClose) {
				System.err.println("发现异常，可能发生了服务器异常或网络问题， 准备进行重连.....");
				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							System.out.println("重连中。。。");
							TimeUnit.SECONDS.sleep(2);
							connect(host,port);

						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
				});
			}else{
				/*用户主动关闭,释放资源*/
				channel = null;
				try {
					group.shutdownGracefully().sync();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				synchronized (this){
					this.connected = false;
					this.notifyAll();
				}
			}
		}

	}
	public static void main(String[] args) {
		new Thread(new MayiClient("127.0.0.1",9999)).start();
	}

	private static class ChannelInitializerImpl extends ChannelInitializer<Channel> {
		@Override
		protected void initChannel(Channel ch) throws Exception {

			//回车符做了分割
			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
			ch.pipeline().addLast(new StringEncoder());
			ch.pipeline().addLast(new MayiClientHandler());
			ch.pipeline().addLast(new StringDecoder());

		}
	}


}
