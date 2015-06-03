package org.mconf.android.core.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpReader;
import com.flazr.rtmp.RtmpWriter;
import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.message.Audio;
import com.flazr.util.Utils;

public class BbbVoiceConnection extends VoiceConnection{

	private static final Logger log = LoggerFactory.getLogger(BbbVoiceConnection.class);

    public BbbVoiceConnection(ClientOptions options,RtmpReader reader) {
            super(options);
            //options = new ClientOptions();
            //options.setClientVersionToUse(Utils.fromHex("00000000"));
            //options.setHost(context.getJoinService().getApplicationService().getServerUrl());
            //options.setAppName("sip");
            //options.setHost("10.129.200.81");
            //options.setAppName("PanTestRoom/room1");
            if (reader != null) {
                    options.publishLive();
                    options.setReaderToPublish(reader);
            }
    }
    
    public void setLoop(boolean loop) {
            options.setLoop(loop ? Integer.MAX_VALUE : 0);
    }

    public boolean start() {
            return connect();
    }

    public void stop() {
            disconnect();
    }
    
    @Override
    protected void onAudio(Audio audio) {
            log.debug("received audio package: {}", audio.getHeader().getTime());
    }
}
