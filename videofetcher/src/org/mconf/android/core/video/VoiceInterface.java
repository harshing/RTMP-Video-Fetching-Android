package org.mconf.android.core.video;

import android.content.Context;

public interface VoiceInterface {
	
	public static final int E_OK = 0;
    public static final int E_INVALID_NUMBER = 1;
    public static final int E_TIMEOUT = 2; 
    
    public void start(Context context);
    public void stop();
    public boolean isOnCall();

}
