/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package org.push.push;



import java.nio.ByteBuffer;

import com.sh.camera.service.MainService;
import com.sh.camera.util.CameraUtil;
import com.sh.camera.util.Constants;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.content.Context;
public class Pusher {

	static {
		System.loadLibrary("stream");
		System.loadLibrary("rtmp");
	}
	/**
	 * 初始化
	 * @param serverIP   流媒体服务器IP
	 * @param serverPort 流媒体服务器端口
	 * @param streamName 流媒体文件名
	 * @param fps
	 * @param format
	 * @return
	 */

	/* 特别说明：本SDK商业用途，请与Car-eye 开源团队联系  */

	/* rtsp interface*/
	private Handler handle =null;	
	public native int    CarEyeInitNetWorkRTSP(Context context,String serverIP, String serverPort, String streamName, int videoformat, int fps,int audioformat, int audiochannel, int audiosamplerate);
	public native int 	 CarEyePusherIsReadyRTSP(int channel);
	public native long   CarEyeSendBufferRTSP(long time, byte[] data, int lenth, int type, int channel);	
	public native int    CarEyeStopNativeFileRTSP(int channel);	
	public native int    CarEyeStartNativeFileRTSPEX(Context context, String serverIP, String serverPort, String streamName,  String fileName,int start, int end);
	public native void  CarEyeStopPushNetRTSP(int index);
	// result： 0 文件传输结束  , 传输出错
	/* rtmp interface*/
	public native int    CarEyeInitNetWorkRTMP(Context context,String serverIP, String serverPort, String streamName, int videoformat, int fps,int audioformat, int audiochannel, int audiosamplerate);
	public native int 	 CarEyePusherIsReadyRTMP(int channel);
	public native long   CarEyeSendBufferRTMP(long time, byte[] data, int lenth, int type, int channel);
	public native int    CarEyeStopNativeFileRTMP(int channel);
	public native int    CarEyeStartNativeFileRTMPEX(Context context, String serverIP, String serverPort, String streamName,  String fileName,int start, int end);
	public native void  CarEyeStopPushNetRTMP(int index);

	public void  CarEyeCallBack(int channel, int Result){		
		
		Log.e("puser", "exit send file!");	
		if(handle != null){
			handle.sendMessage(handle.obtainMessage(1006));
		}else{
		}		
		Intent intent = new Intent("com.dss.camera.ACTION_END_VIDEO_PLAYBACK");
		intent.putExtra("EXTRA_ID", channel);
		MainService.getInstance().sendBroadcast(intent);
	}	
	/**
	 * 发送H264编码格式
	 * @param data
	 * @param timestamp
	 * @param type
	 * @param index
	 * @return
	 */

	/**
	 * 停止发送
	 * @param index
	 */

	public  long SendBuffer_org(final byte[] data,final int length, final long timestamp, final int type, final int index, int protocol)
	{
		long ret;
		//Log.e("puser", "timestamp:"+timestamp+"length:"+length);
		if(Constants.CAREYE_RTSP_PROTOCOL == protocol ) {
			ret = CarEyeSendBufferRTSP(timestamp, data, length, type, index);
		}else
		{
			ret =  CarEyeSendBufferRTMP(timestamp, data,length,type,index);

		}
		return ret;
		
	}	
	public  void stopPush(final int  index, final int protocol)
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(Constants.CAREYE_RTSP_PROTOCOL == protocol ) {
					CarEyeStopPushNetRTSP(index);
				}else
				{
					CarEyeStopPushNetRTMP(index);
				}
			}
		}).start();
	}
	/**
	 * 停止所有
	 */
	public int  startfilestream( final String serverIP, final String serverPort, final String streamName, final String fileName,final int splaysec,final int eplaysec,final Handler handler, int protocol){

		//StartNativeFileRTSP(serverIP,serverPort,streamName, fileName);
		handle = handler;
		int channel;
		if(Constants.CAREYE_RTSP_PROTOCOL == protocol ) {
			 channel = CarEyeStartNativeFileRTSPEX(MainService.application, serverIP, serverPort, streamName, fileName, splaysec, eplaysec);
		}else
		{
			 channel =  CarEyeStartNativeFileRTMPEX(MainService.application, serverIP,serverPort,streamName, fileName,splaysec,eplaysec);

		}
		return channel;
	}

	public void startfilestream(final String serverIP, final String serverPort, final String streamName, final String filePath,int protocol){

		if(Constants.CAREYE_RTSP_PROTOCOL == protocol )
		{
			CarEyeStartNativeFileRTSPEX(MainService.application,serverIP,serverPort,streamName, filePath,0,0);
		}else
		{
			CarEyeStartNativeFileRTMPEX(MainService.application,serverIP,serverPort,streamName, filePath,0,0);
		}
	}
}

