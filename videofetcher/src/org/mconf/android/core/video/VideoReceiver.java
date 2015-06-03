package org.mconf.android.core.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.util.Log;

import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.message.Audio;
import com.flazr.rtmp.message.Video;

public class VideoReceiver {

        protected class VideoConnection extends VideoReceiverConnection {

                public VideoConnection(ClientOptions options) {    
                	super(options);
                	Log.e("","3333333333333333333");
                }

                @Override
                protected void onVideo(Video video) {
                        VideoReceiver.this.onVideo(video);
                }
                
                @Override
                protected void onAudio(Audio audio) {
                        VideoReceiver.this.onAudio(audio);
                }
        }
        
        private static final Logger log = LoggerFactory.getLogger(VideoReceiver.class);

        private VideoConnection videoConnection;
        
        public VideoReceiver(ClientOptions opt) {

                /*ClientOptions opt = new ClientOptions();
        		Log.e("","///////////////////////");
        		//opt.parseUrl("rtmp://10.129.200.81/PanTestRoom/room1");
                //opt.setStreamName("Test");
                opt.setHost("10.129.200.81");
                opt.setAppName("PanTestRoom/room1");
        		opt.setStreamName("Test");*/
                opt.setWriterToSave(null);
                videoConnection = new VideoConnection(opt);
                
        }
        
        protected void onVideo(Video video) {
                log.debug("received video package: {}", video.getHeader().getTime());
                Log.d("aaaaaaaaaaaaaa", "bcbcbbcbcb");
        }
        
        protected void onAudio(Audio audio) {
            log.debug("received video package: {}", audio.getHeader().getTime());
            Log.d("aaaaaaaaaaaaaa", "bcbcbbcbcb");
        }
        
        public void start() {
                if (videoConnection != null)
                	{Log.d("aaaaa","<<<<<<<<<<<<<");
                        videoConnection.connect();
                	}}
        
        public void stop() {
                if (videoConnection != null)
                        videoConnection.disconnect();
        }
        
        /*public String getUserId() {
                return userId;
        }
        
        public String getStreamName() {
                return streamName;
        }
        
        public float getAspectRatio() {
                return getAspectRatio(userId, streamName);
        }
        
        public static float getAspectRatio(String userId, String streamName) {
                if (streamName != null && streamName.contains(userId)) {
                        
                        
                         *         0.7 -> 120x1601
                         *         0.8 -> 120x1601-131292666
                         *         0.81 -> 120x160-1-131292666
                         
                        
                        
                         * 0.81 stream name format
                         
                        Pattern streamNamePattern = Pattern.compile("(\\d+)[x](\\d+)[-]\\d+[-]\\d+");
                        Matcher matcher = streamNamePattern.matcher(streamName);
                        if( matcher.matches() ) {
                                String widthStr = matcher.group(1);
                                String heightStr = matcher.group(2);
                                int width = Integer.parseInt(widthStr);
                                int height = Integer.parseInt(heightStr);
                                return width / (float) height;
                        }
                        
                        
                         * 0.7 or 0.8 stream name format
                         
                        streamNamePattern = Pattern.compile("(\\d+)[x](\\d+)([-]\\d+)?");
                        matcher = streamNamePattern.matcher(streamName);
                        if( matcher.matches() ) {                                
                                String widthStr = matcher.group(1);
                                String heightAndId = matcher.group(2);
                                String heightStr = heightAndId.substring(0, heightAndId.lastIndexOf(userId));
                                int width = Integer.parseInt(widthStr);
                                int height = Integer.parseInt(heightStr);                                
                                return width / (float) height;
                        }                        
                }                
                return -1;
        }*/
}