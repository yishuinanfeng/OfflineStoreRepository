package com.example.offlinestore.main;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.offlinestore.interfaces.CacheModel;
import com.example.offlinestore.interfaces.OnlineListener;
import com.example.offlinestore.utils.CacheUtils;

public class NetStatusReceiver extends BroadcastReceiver {
	private CacheUtils cacheUtils;
	private Context context;
	private List<CacheModel> cacheModels;
	private OnlineListener onlineListener;
	/**
	 * 网络标识位，true表示有网络；false表示没有网络
	 * 防止在网络已经连上的情况下多次调用onReceive()
	 * 由于是各个对象共用的，故使用static
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
		// TODO 自动生成的方法存根
	//	this.context = cont;
		cacheUtils = CacheUtils.getInstance(context);
// 移动数据链接
		manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
 
//		NetworkInfo mobileInfo = manager
//				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//	
//		// wifi链接
//		NetworkInfo wifiInfo = manager
//				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo activeInfo = manager.getActiveNetworkInfo();

		// 当有网的时候
		if (activeInfo != null) {
			if (activeInfo.isAvailable() && activeInfo.isConnected()) {
				//如果本来就有网络，则不再执行
Toast.makeText(context, "网络恢复", 1).show();
				if (isNetAvailable) {
					return;
				}
				isNetAvailable = true;
		// 将存储的cacheUtils提出来
				
//Toast.makeText(context, "网络已连接", 1).show();
//int count = cacheUtils.getCacheCount();
				if (cacheUtils.getCacheCount() > 0) {
					
					if(onlineListener != null){
						try {
							onlineListener.onWebConnect(cacheUtils.getAllCache());
							cacheUtils.clearAllCache();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				}

				
			}
		}else {
			//没网的时候，置标识位为false
			isNetAvailable = false;
		}
	}

}
