package org.mconf.android.core.video;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;

import com.flazr.rtmp.client.ClientOptions;

public class MainActivity extends Activity {

	static Logger logger=LoggerFactory.getLogger(MainActivity.class);
	private MainRtmpConnection mainConnection;
	private VoiceInterface voiceItf=null;
	private VideoDialog mVideoDialog;

	private BroadcastReceiver closeVideo = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			//log.debug("Client.closeVideo.onReceive()");

			Bundle extras = intent.getExtras();
				//log.debug("Client.closeVideo.onReceive().dismissing()");
				mVideoDialog.dismiss();
				mVideoDialog = null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BasicConfigurator.configure();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		//RtmpHandshake rh=new RtmpHandshake(options);
        
        ClientOptions op=new ClientOptions();
        op.setHost("10.129.200.81");
        op.setAppName("PanTestRoom/room1");
		op.setStreamName("Test");
        /*op.parseUrl("rtmp://10.129.200.81/PanTest/Test");
		ClientOptions options=new ClientOptions("rtmp://10.129.200.81/","PanTest","Test","abc.flv");
		options.publishLive();
		mainConnection=new MainRtmpConnection(op);
		if(mainConnection.connect())
			Log.e("Aaaaaaa","ppppp");
		Log.e("",options.getPublishType().asString());
		*/
        logger.info("Main");
        mVideoDialog=new VideoDialog(this,op);
		mVideoDialog.show();
		//if (voiceItf != null)
			//  voiceItf.stop();
		voiceItf = new VoiceOverRtmp(this,op);
		voiceItf.start(this);

	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mVideoDialog != null && mVideoDialog.isShowing())
            mVideoDialog.pause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        if (mVideoDialog != null && mVideoDialog.isShowing())
                mVideoDialog.resume();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
