package com.example.offlinestore.interfaces;

import java.io.Serializable;

/**
 * 所有需要的缓存对象的接口
 * @author SSXT
 *
 */
public interface CacheModel extends Serializable{
	//返回该类的标识，可以是类名或者编号等作为区分类的标识
	String getMark();
}
