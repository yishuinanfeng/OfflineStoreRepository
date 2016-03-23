package com.example.offlinestore.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.offlinestore.interfaces.CacheModel;

/**
 * 只要对象是实现CacheModel的都可以缓存起来
 * 以SharePreference来存储一些可序列化的缓存对象（key从1算起），作为一种缓存工具，以类似队列的方式存储。
 * 可以增加一个缓存对象，删除一个缓存对象（队列形式），设置某个缓存对象为null,获取所有缓存对象，获取缓存对象的总数。
 * @author yyn
 *
 */
public class CacheUtils {

	
	private Context context;
	private volatile static CacheUtils CacheUtilInstance;
	
	/**
	 * 单例模式
	 * @param context
	 * @return
	 */
	public static CacheUtils getInstance(Context context)
	{
		if (CacheUtilInstance == null)
		{
			synchronized (CacheUtils.class)
			{
				if (CacheUtilInstance == null)
				{
					CacheUtilInstance = new CacheUtils(context);
					
				}
			}
		}
		return CacheUtilInstance;
	}
	
	
	
	private CacheUtils(Context context) {
		
		//this.message = message;
		this.context = context;
		
		
	}

	/**
     * 以队列形式增加新的缓存对象
     * @param cacheModel 缓存对象
     * 
     */
    public void addCacheModel(CacheModel cacheModel){
    	
    	int count = getCacheCount();
    	//addSmsCount(++count);
    	saveCacheModel(cacheModel, ++count);
    }
	
    /**
     * 添加缓存对象集合
     * @param cacheModels
     */
    public void addCacheModels(List<CacheModel> cacheModels) {
    	for (int i = 0; i < cacheModels.size(); i++) {
			addCacheModel(cacheModels.get(i));
		}
	}
	
	/**
	 * 序列化对象
	 * @param message
	 * @return
	 * @throws IOException
	 */
	private String serialize(CacheModel cacheModel) throws IOException {  
       
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(  
                byteArrayOutputStream);  
        objectOutputStream.writeObject(cacheModel);  
        String serStr = byteArrayOutputStream.toString("ISO-8859-1");  
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8");  
        objectOutputStream.close();  
        byteArrayOutputStream.close();  
       
        return serStr;  
    }  
  
    /** 
     * 反序列化对象 ,将字符串转化为缓存的对象
     *  
     * @param str 将要转化为对象的字符串
     * @return 
     * @throws IOException 
     * @throws ClassNotFoundException 
     */  
	private  CacheModel deSerialization(String str) throws IOException,  
            ClassNotFoundException {  
        if(str == null || str == ""){
        	return null;
        }
        String redStr = java.net.URLDecoder.decode(str, "UTF-8");  
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(  
                redStr.getBytes("ISO-8859-1"));  
        ObjectInputStream objectInputStream = new ObjectInputStream(  
                byteArrayInputStream);  
        CacheModel message = (CacheModel) objectInputStream.readObject();  
        objectInputStream.close();  
        byteArrayInputStream.close();  
        
        return message;  
    }  
    /**
     * 保存缓存对象,key从1算起
     * @param strObject 序列化后转化为String的短信对象
     * @param num  短信序号
     */
    private  void  saveCacheModel(CacheModel model,int num) {
    	String strObject;
		try {
			String i = String.valueOf(num);
			strObject = serialize(model);
			SharedPreferences sp = context.getSharedPreferences("Cache", 0);  
		    Editor edit = sp.edit();  
		    edit.putString(i, strObject);  
		    edit.commit(); 
		    
	    	setCacheCount(num);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }  
    
    /**
     * 设置对应索引的缓存对象为空
     * @param index
     */
    public synchronized void setCacheModelToNull(int index) {
		// TODO Auto-generated method stub
    	String strObject;
		try {
			String i = String.valueOf(index);
			strObject = serialize(null);
			SharedPreferences sp = context.getSharedPreferences("Cache", 0);  
		    Editor edit = sp.edit();  
		    edit.putString(i, strObject);  
		    edit.commit(); 
		    
	    	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
  
    
    /**
     * 以队列形式删除某个缓存对象（排在后面的对象依次前移补位）
     * @throws Exception 
     */
    public synchronized void deleteCacheByQueue(int index) throws Exception {
    	int sum = getCacheCount();
    	if(index >  sum){
    		throw new Exception("index is greater than sum");
    		
    	}
    	
    	if(sum == index){
    		setCacheCount(--sum);
    		return;
    	}
    	for(int i = index;i < sum;i++ ){
    		CacheModel cacheModel = getCacheModel(i+1);
    		saveCacheModel(cacheModel, i);
    	}
    		
//    		int count = getSmsCount();
//    	    	setSmsCount(--count);
	}
    /**
     * 弹出第一个缓存对象（出列）
     * @throws Exception 
     */
    public synchronized CacheModel pollCache() throws Exception {
		// TODO Auto-generated method stub
    	CacheModel cacheModel = getCacheModel(1);
    	deleteCacheByQueue(1);
    	return cacheModel;
	}
    
    /**
     * 获得某个缓存对象
     * @param index 缓存对象索引
     * @return
     * @throws Exception 
     */
    public CacheModel getCacheModel(int index) throws Exception  {  
        SharedPreferences sp = context.getSharedPreferences("Cache", 0); 
        CacheModel cacheModel;
        int sum = getCacheCount();
        if(index > sum){
        	throw new Exception("index is greater than sum");
        }
        String i = String.valueOf(index);
		try {
			cacheModel = deSerialization(sp.getString(i, null));
			return cacheModel; 
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}
    }  
    
    /**
     * 获得全部存储的对象
     * @return
     * @throws Exception 
     */
    public List<CacheModel> getAllCache() throws Exception {
		List<CacheModel> cacheModels = new ArrayList<CacheModel>();
		for(int i = 1;i <= getCacheCount();i++){
			CacheModel cacheModel = getCacheModel(i);
			cacheModels.add(cacheModel);
		}
    	return cacheModels;
	}
    
    /**
     * 清除所有缓存
     * @throws Exception 
     */
    public synchronized void clearAllCache()  {
		
    	
    	SharedPreferences sp = context.getSharedPreferences("Cache", 0);  
	    Editor edit = sp.edit();
	    edit.clear().commit();
	    setCacheCount(0);
	}
    
    
    /**
     * 设置缓存对象的个数
     * @param count
     */
    private synchronized void setCacheCount(int count) {
    	SharedPreferences sp = context.getSharedPreferences("Cache", 0);  
	    Editor edit = sp.edit();  
	    edit.putInt("cacheCount", count);
	    edit.commit();  
	    

    }
    
    /**
     * 获得存储的缓存对象的总个数
     * @return
     */
   public  int getCacheCount() {
	   SharedPreferences sp = context.getSharedPreferences("Cache", 0); 
	   int count = sp.getInt("cacheCount", 0);
	   return count;
	}
   
}
