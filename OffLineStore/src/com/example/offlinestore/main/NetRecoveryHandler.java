package com.example.offlinestore.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.offlinestore.interfaces.CacheModel;
import com.example.offlinestore.interfaces.OnlineListener;
import com.example.offlinestore.utils.CacheUtils;
/**
 * manager that provide main interface for client to use the function of offline caching.
 * @author yanyinan
 *
 */
public class NetRecoveryHandler {

	private Context context;
	private volatile static NetRecoveryHandler NetRecHandlerInstance;
	private CacheUtils cacheUtils;
	private OnlineListener onlineListener;
	private ConnectivityManager manager;
	private NetStatusReceiver netStatusReceiver;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ArrayList<CacheModel> cacheModels = (ArrayList<CacheModel>) msg.obj;
			onlineListener.onWebConnect(cacheModels);
			cacheUtils.clearAllCache();
		}
	};
	
	/**
	 * single instance pattern
	 * @param context
	 * @return
	 */
	public static NetRecoveryHandler getInstance(Context context,OnlineListener onlineListener)
	{
		if (NetRecHandlerInstance == null)
		{
			synchronized (NetRecoveryHandler.class)
			{
				if (NetRecHandlerInstance == null)
				{
					NetRecHandlerInstance = new NetRecoveryHandler(context,onlineListener);
					
				}
			}
		}
		return NetRecHandlerInstance;
	}
	
	
	private NetRecoveryHandler(Context context,OnlineListener onlineListener) {
		this.context = context;
		this.onlineListener = onlineListener;
		cacheUtils = CacheUtils.getInstance(context);
		netStatusReceiver = new NetStatusReceiver();
	}
	/**
	 * add all model to which you transfer your data to cacheUtils
	 */
	public void addModels(List<CacheModel> cacheModels) {
		cacheUtils.addCacheModels(cacheModels);
	}
	
	/**
	 * start to execute offline caching
	 */
	public void execute(final List<CacheModel> cacheModels) {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				cacheUtils.addCacheModels(cacheModels);
				netStatusReceiver.setOnlineListener(onlineListener);
				manager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeInfo = manager.getActiveNetworkInfo();
				if (activeInfo != null) {
					if (activeInfo.isAvailable() && activeInfo.isConnected()) {
						//ÓÐÍøÂç×´Ì¬
						try {
							if(onlineListener != null){
								ArrayList<CacheModel> cacheModels =  cacheUtils.getAllCache();
								Message msg = Message.obtain();
								msg.obj = cacheModels;
								handler.sendMessage(msg);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else {
						//offline
						doWhenOffLine();

					}
				}else {
					doWhenOffLine();
				}
			}
		});
		thread.start();
	}
	
	private void doWhenOffLine() {
	
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		filter.setPriority(1000);
		context.registerReceiver(netStatusReceiver, filter);
	}
	
	/**
	 * stop listening the state of network
	 * 
	 */
	public void stop() {
		// TODO Auto-generated method stub
		context.unregisterReceiver(netStatusReceiver);
		
	}

}
