package org.mconf.android.core.video;

import android.content.Context;
import android.util.Log;

import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.message.Audio;

public class VoiceOverRtmp implements VoiceInterface {

  private BbbVoiceConnection connection;
  private RtmpAudioPlayer player = new RtmpAudioPlayer();
  private boolean onCall = false;
  
  public VoiceOverRtmp(Context context,ClientOptions opt) {
    connection = new BbbVoiceConnection(opt,null) {
      @Override
      protected void onAudio(Audio audio) {
    	Log.e("","'''''''''''''''''");
        player.onAudio(audio);
      }
    };
    
  }

  @Override
  public void start(Context context) {
    player.start(context);
    connection.start();
    onCall = true;
  }

  @Override
  public void stop() {
    onCall = false;
    connection.stop();
    player.stop();    
  }

  @Override
  public boolean isOnCall() {
    return onCall;
  }

}