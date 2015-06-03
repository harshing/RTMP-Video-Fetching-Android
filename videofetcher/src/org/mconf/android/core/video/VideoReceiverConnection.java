package org.mconf.android.core.video;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.util.Log;

import com.flazr.rtmp.RtmpDecoder;
import com.flazr.rtmp.RtmpEncoder;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.client.ClientHandshakeHandler;
import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.message.Audio;
import com.flazr.rtmp.message.Command;
import com.flazr.rtmp.message.MessageType;
import com.flazr.rtmp.message.Video;

public abstract class VideoReceiverConnection extends RtmpConnection {
	
    private static final Logger log = LoggerFactory.getLogger(VideoReceiverConnection.class);

	public VideoReceiverConnection(ClientOptions options) {
		super(options);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ChannelPipelineFactory pipelineFactory() {
		return new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
		        final ChannelPipeline pipeline = Channels.pipeline();
		        pipeline.addLast("handshaker", new ClientHandshakeHandler(options));
		        pipeline.addLast("decoder", new RtmpDecoder());
		        pipeline.addLast("encoder", new RtmpEncoder());
		        pipeline.addLast("handler", VideoReceiverConnection.this);
		        return pipeline;
			}
		};
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Log.e("","videoreceiverchannelconnected");
        options.setArgs((Object[]) null);
        writeCommandExpectingResult(e.getChannel(), Command.connect(options));
	}
	
	//@Override
	protected void onMultimedia(Channel channel, RtmpMessage message) {
		super.onMultimedia(channel, message);
		if (message.getHeader().getMessageType() == MessageType.VIDEO) {
            Log.e("","1111111111111111111111111222222");
			onVideo((Video) message);
		}
		if (message.getHeader().getMessageType() == MessageType.AUDIO) {
            onAudio((Audio) message);
            Log.e("","pppppppppppppppppppp");
		}
	}
	
	abstract protected void onVideo(Video video);
	abstract protected void onAudio(Audio audio);
	
}
