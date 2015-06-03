package org.mconf.android.core.video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.client.ClientOptions;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;


public class VideoDialog extends Dialog{
	
	private static final Logger log = LoggerFactory.getLogger(VideoDialog.class);
    
    private VideoSurface videoWindow;
    public boolean isPreview;
    private String stream;
    private ClientOptions options;
    private Context context;
    
    public VideoDialog(Context context,ClientOptions opt) {
            super(context);
            this.options=opt;
            this.context=context;
            if(1==2){
                    isPreview = true;
            } else {
                    isPreview = false;
            }
            
            requestWindowFeature(Window.FEATURE_NO_TITLE); //Removes the title from the Dialog
            
            if(isPreview){
                    //setContentView(R.layout.video_capture);
            } else {
                    setContentView(R.layout.video_window);
                    
                    videoWindow = (VideoSurface) findViewById(R.id.video_window);
            }
            
            android.view.WindowManager.LayoutParams windowAttributes = getWindow().getAttributes();                
            windowAttributes.flags = android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON; //Makes the video bright
//            windowAttributes.flags = android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //Makes it possible to interact with the window behind, but the video should be closed properly when the screen changes
//            windowAttributes.flags = android.view.WindowManager.LayoutParams.FLAG_SCALED; //Removes the title from the dialog and removes the border also                 
            getWindow().setAttributes(windowAttributes);
            
            //setTitle(name);
            setCancelable(true);
            
            this.stream = stream;
    }
    
    private void sendBroadcastRecreateCaptureSurface() {
            log.debug("sendBroadcastRecreateCaptureSurface()");
            
            //Intent intent= new Intent(Client.CLOSE_DIALOG_PREVIEW);
            //getContext().sendBroadcast(intent);
    }
    
    @Override
    protected void onStart() {
    		super.onStart();
            resume();
    }
    
    @Override
    protected void onStop() {
            pause();
            super.onStop();
    }

    public void pause() {
            if(isPreview){
                    //sendBroadcastRecreateCaptureSurface();
            } else {
                    videoWindow.stop();
            }
    }
    
    public void resume() {
            if(isPreview){
                    //VideoCaptureLayout videocaplayout = (VideoCaptureLayout) findViewById(R.id.video_capture_layout);
                    //videocaplayout.show(40);
            } else {
                    videoWindow.start(options,context);
            }
    }

}
