package com.example.offlinestore.interfaces;

import java.io.Serializable;

/**
 * all model that want to be stored have to imlements this interface
 * @author yanyinan
 *
 */
public interface CacheModel extends Serializable{
	/**
	 * @return return a mark sa a identifier to differentiate different kinds of model.
	 * 		   you can decide the identifier by yourself
	 */
	String getMark();
}
