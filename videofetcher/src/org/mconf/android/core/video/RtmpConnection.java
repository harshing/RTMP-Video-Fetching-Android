package org.mconf.android.core.video;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.util.Log;

import com.flazr.rtmp.client.ClientHandler;
import com.flazr.rtmp.client.ClientOptions;

public abstract class RtmpConnection extends ClientHandler implements ChannelFutureListener {

	private static final Logger log = LoggerFactory.getLogger(RtmpConnection.class);
	
	public RtmpConnection(ClientOptions options) {
		super(options);
		Log.e("",options.toString());
		// TODO Auto-generated constructor stub
		//Log.e("",options.toString());
	}

	private ClientBootstrap bootstrap = null;
    private ChannelFuture future = null;
    private ChannelFactory factory = null;
	
	@Override
	public void operationComplete(ChannelFuture arg0) throws Exception {
		// TODO Auto-generated method stub
		
		if (future.isSuccess()){
			Log.e("", "jjjjjjjjjjjjjjjjjjjj");

			onConnectedSuccessfully();
		}
		else{
			Log.e("", "wwwweeeeeeeeeeeeeee");
			onConnectedUnsuccessfully();
		}
	}

	private void onConnectedUnsuccessfully() {
		// TODO Auto-generated method stub
		
	}

	private void onConnectedSuccessfully() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean connect() {
        	if(factory == null)
        		factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        	bootstrap = new ClientBootstrap(factory);
        	bootstrap.setPipelineFactory(pipelineFactory());
        	future = bootstrap.connect(new InetSocketAddress(options.getHost(),options.getPort()));
        	future.addListener(this);
        	
        	//Log.e("a>>>>>>>>>>>>>>>",future.getChannel().getRemoteAddress().toString());
        	return true;
        }

	abstract protected ChannelPipelineFactory pipelineFactory();

	public void disconnect() {
        if (future != null) {
                if (future.getChannel().isConnected()) {
                		log.debug("Channel is connected, disconnecting");
                        //future.getChannel().close(); //ClosedChannelException
                        future.getChannel().disconnect();
                        future.getChannel().getCloseFuture().awaitUninterruptibly();
                }
                future.removeListener(this);
                factory.releaseExternalResources();
                future = null; factory = null; bootstrap = null;
        	}
    	}
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
            String exceptionMessage = e.getCause().getMessage();
            if (exceptionMessage != null && exceptionMessage.contains("ArrayIndexOutOfBoundsException") && exceptionMessage.contains("bad value / byte: 101 (hex: 65)")) {
            		Log.e("","wwwwwwwwwwwwwww");
            		log.debug("Ignoring malformed metadata");
                    return;
            } else {
                	Log.e("",""+exceptionMessage);
            		super.exceptionCaught(ctx, e);
            }
    }	
}