package com.example.offlinestore.main;

import java.util.List;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.offlinestore.interfaces.CacheModel;
import com.example.offlinestore.interfaces.OnlineListener;
import com.example.offlinestore.utils.CacheUtils;
/**
 * 离线缓存管理器
 * @author Administrator
 *
 */
public class NetRecoveryHandler {

	private Context context;
	private volatile static NetRecoveryHandler NetRecHandlerInstance;
	private CacheUtils cacheUtils;
	private OnlineListener onlineListener;
	private ConnectivityManager manager;
	private NetStatusReceiver netStatusReceiver;
	
	
	/**
	 * 单例模式
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
	 * 将所有需要缓存的对象添加进来
	 */
	public void addModels(List<CacheModel> cacheModels) {
		cacheUtils.addCacheModels(cacheModels);
	}
	
	/**
	 * 开始执行离线缓存功能
	 */
	public void execute() {
		netStatusReceiver.setOnlineListener(onlineListener);
		manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = manager.getActiveNetworkInfo();
		if (activeInfo != null) {
			if (activeInfo.isAvailable() && activeInfo.isConnected()) {
				//有网络状态
				try {
					if(onlineListener != null){
						onlineListener.onWebConnect(cacheUtils.getAllCache());
						cacheUtils.clearAllCache();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else {
				//无网络状态
				doWhenOffLine();

			}
		}else {
			doWhenOffLine();
		}
	}
	
	private void doWhenOffLine() {
		// TODO Auto-generated method stub
		Toast.makeText(context, "无网络", 1).show();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		filter.setPriority(1000);
		context.registerReceiver(netStatusReceiver, filter);
	}
	
	/**
	 * 停止监听网络状态
	 * 
	 */
	public void stop() {
		// TODO Auto-generated method stub
		context.unregisterReceiver(netStatusReceiver);
		
	}

}
