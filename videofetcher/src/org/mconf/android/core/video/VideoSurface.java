package org.mconf.android.core.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.message.Audio;
import com.flazr.rtmp.message.Video;

public class VideoSurface extends GLSurfaceView {

	private static final Logger log = LoggerFactory.getLogger(VideoSurface.class);
	public static final int BUFFER_SIZE = 1024;
	private VideoRenderer mRenderer;
	private Codec codec = new Speex();
	private AudioTrack audioTrack;
	private int mu, maxjitter;
	private boolean running = false;
	private short[] decodedBuffer = new short[BUFFER_SIZE];
	private byte[] pktBuffer = new byte[BUFFER_SIZE + 12];
	private boolean inDialog;
	private boolean showing = false;
	public static final float DEFAULT_ASPECT_RATIO = 4 / (float) 3;
	private float aspectRatio = DEFAULT_ASPECT_RATIO;
	private VideoReceiver videoReceiver;

	public VideoSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		NativeLibsLoader.loadPlaybackLibs(context.getPackageName());

		mRenderer = new VideoRenderer(this);
		setRenderer(mRenderer);
	}

	public int[] getDisplayParameters(int width, int height){
		int[] params = new int[2];

		int h = 0, w = 0;
		float displayAspectRatio = width / (float) height;
		if (displayAspectRatio < aspectRatio) {
			w = width;
			h = (int) (w / aspectRatio);
		} else {
			h = height;
			w = (int) (h * aspectRatio);                        
		}

		params[0] = w;
		params[1] = h;                
		return params;                
	}

	public void start(ClientOptions opt, Context context) {

		if (showing)
			stop();
		synchronized (this) {
		codec.init();
		mu = codec.samp_rate()/8000;
		maxjitter = AudioTrack.getMinBufferSize(codec.samp_rate(), 
				AudioFormat.CHANNEL_OUT_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);
		if (maxjitter < 2*2*BUFFER_SIZE*3*mu)
			maxjitter = 2*2*BUFFER_SIZE*3*mu;
		audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, codec.samp_rate(), AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				maxjitter, AudioTrack.MODE_STREAM);
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		/*if (Integer.parseInt(Build.VERSION.SDK) >= 5)
			am.setSpeakerphoneOn(true);
		else*/
			am.setMode(AudioManager.MODE_IN_COMMUNICATION);
		audioTrack.play();

		running = true;
		}
		//BigBlueButtonClient bbb = ((BigBlueButton) getContext().getApplicationContext()).getHandler();
		videoReceiver = new VideoReceiver(opt) {
			@Override
			protected void onVideo(Video video) {
				//VideoChild vc=new VideoChild(video);
				Log.d("video","video received");
				byte[] data = video.getBody();
				enqueueFrame(data,data.length);
			}
			@Override
			protected void onAudio(Audio audio) {
				//VideoChild vc=new VideoChild(video);

				byte[] audioData = audio.getByteArray();

				int offset = 1;

				//      byte[] tmpBuffer = new byte[audioData.length - offset];
				//      System.arraycopy(audioData, offset, tmpBuffer, 0, tmpBuffer.length);
				//      pkt.setPayload(tmpBuffer, tmpBuffer.length);
				//      int decodedSize = codec.decode(pktBuffer, decodedBuffer, pkt.getPayloadLength());

				System.arraycopy(audioData, offset, pktBuffer, 12, audioData.length - offset);
				int decodedSize = codec.decode(pktBuffer, decodedBuffer, audioData.length - offset);
				write(decodedBuffer, 0, decodedSize);
			}
		};

		//float tmp = videoReceiver.getAspectRatio();
		aspectRatio = DEFAULT_ASPECT_RATIO;

		updateLayoutParams(inDialog);                

		videoReceiver.start();

		showing = true;
	}
	
	private void write(short a[],int b,int c) {
	     synchronized (this) {
	       audioTrack.write(a,b,c);
	     }
	   }

	public void updateLayoutParams (boolean inDialog) {
		LayoutParams layoutParams = getLayoutParams();
		DisplayMetrics metrics = getDisplayMetrics(getContext());
		log.debug("Maximum display resolution: {} X {}\n", metrics.widthPixels, metrics.heightPixels);
		if(inDialog){
			metrics.widthPixels -= 40;
			metrics.heightPixels -= 40;
		}                
		int[] params = getDisplayParameters(metrics.widthPixels, metrics.heightPixels);
		layoutParams.width = params[0];
		layoutParams.height = params[1];
		setLayoutParams(layoutParams);

		if(showing)
			nativeResize(metrics.widthPixels, metrics.heightPixels, params[0], params[1], 0, 0);
		else 
			initDrawer(metrics.widthPixels, metrics.heightPixels, params[0], params[1], 0, 0);       
	}

	public void stop() {
		if (showing) {
			videoReceiver.stop();
			codec.close();
			audioTrack.stop();
			audioTrack.release();

			log.debug("VideoSurface.stop().endDrawer()");
			endDrawer();

			showing = false;
		}
	}

	static public DisplayMetrics getDisplayMetrics(Context context){
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		display.getMetrics(metrics);
		return metrics;
	}

	private native int initDrawer(int screenW, int screenH, int displayAreaW, int displayAreaH, int displayPositionX, int displayPositionY);
	private native int nativeResize(int screenW, int screenH, int displayAreaW, int displayAreaH, int displayPositionX, int displayPositionY);
	private native int endDrawer();
	private native int enqueueFrame(byte[] data, int length);        
}