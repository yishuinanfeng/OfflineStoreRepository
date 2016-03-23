package com.example.offlinestore.interfaces;

import java.util.List;

/**
 *the method onWebConnect() will be invoke  when network connect as a callback. 
 * @author yanyinan
 *
 */
public interface OnlineListener {
	/**
	 * 将断网时无法提交服务器的数据提交上去
	 * @param cacheModels  返回给客户端的缓存对象集合
	 */
	void onWebConnect(List<CacheModel> cacheModels);
}
