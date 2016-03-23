package com.example.offlinestore.main;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.offlinestore.interfaces.CacheModel;
import com.example.offlinestore.interfaces.OnlineListener;
import com.example.offlinestore.utils.CacheUtils;
/**
 * broadcast receiver that can get the state of network while the state of network change 
 * @author yanyinan
 *
 */
public class NetStatusReceiver extends BroadcastReceiver {
	private CacheUtils cacheUtils;
	private Context context;
	private List<CacheModel> cacheModels;
	private OnlineListener onlineListener;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ArrayList<CacheModel> cacheModels = (ArrayList<CacheModel>) msg.obj;
			onlineListener.onWebConnect(cacheModels);
			cacheUtils.clearAllCache();
		}
	};
	/**
	 * a flag that indicate the state of network
	 *  true:connected        
	 *  false:disconnected
	 * in case of invoking onReceive() moe than one time when the state of network change just one time
	 * 
	 */
	private static boolean isNetAvailable = false;
	private ConnectivityManager manager;
	

	
	
	public NetStatusReceiver() {
		super();
		
	}
	
	public void setOnlineListener(OnlineListener onlineListener) {
		this.onlineListener = onlineListener;
	}

	@Override
	public void onReceive(Context context, Intent arg1) {
	
		cacheUtils = CacheUtils.getInstance(context);

		manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
 

		NetworkInfo activeInfo = manager.getActiveNetworkInfo();

		// network is available
		if (activeInfo != null) {
			if (activeInfo.isAvailable() && activeInfo.isConnected()) {
				//if the network is available
Toast.makeText(context, "newwork connect", 1).show();
				if (isNetAvailable) {
					return;
				}
				isNetAvailable = true;
		
				

				if (cacheUtils.getCacheCount() > 0) {
					
					if(onlineListener != null){
						/**
						 * start up a new thread in order to avoid blocking main thread when 
						 * there are a large number of data to handle
						 */
						
							Thread thread = new Thread(new Runnable() {
								
								@Override
								public void run() {
									try {
										ArrayList<CacheModel> cacheModels =  cacheUtils.getAllCache();
										Message msg = Message.obtain();
										msg.obj = cacheModels;
										handler.sendMessage(msg);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									
								}
							});
							thread.start();
						
						
					}
					
				}

				
			}
		}else {
			
			isNetAvailable = false;
		}
	}

}
