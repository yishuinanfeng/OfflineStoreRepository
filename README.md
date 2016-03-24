# OfflineStoreRepository
A android application module that support following functions :
1.It can store your datas when you are posting some datas to server if network is in the state of disconnection.
2.It will post data to server as soon as network connect automatically.
3.Above all,It will make sure that the stored data will not be lost even the application close or phone power off.


It is very convenient for programmer to use,waht you need is just 4 steps:
1.Transfer your datas that you want to post to server into a Entity class,put all these classes into an ArrayList.
		One one = new One();
		Two two = new Two();
		Three three = new Three();
		List<CacheModel> cacheModels = new ArrayList<CacheModel>();
		cacheModels.add(one);
		cacheModels.add(two);
		cacheModels.add(three);
2.Create a onlineListener's anonymous subclass like the following programs,do how you want to do to post the datas to the server.
OnlineListener onlineListener = new OnlineListener() {
			
			@Override
			public void onWebConnect(List<CacheModel> cacheModels) {
				for (int i = 0; i < cacheModels.size(); i++) {
					switch (cacheModels.get(i).getMark()) {
					case "one":
						//post cacheModel which return mark "one" to server
						break;
					case "two":
						//post cacheModel which return mark "two" to server
						break;
					case "three":
						//post cacheModel which return mark "three" to server
						break;
					default:
						break;
					}
				}
				
			}
		};

3.Create a NetRecoveryHandler class using the onlineListener's anonymous subclass above:
	NetRecoveryHandler netRecoveryHandler = NetRecoveryHandler.getInstance(this, onlineListener);

4.Invoke NetRecoveryHandler's method execute();
	netRecoveryHandler.execute(cacheModels);



Note that you have to add permissions in manifest file of your project:


          uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"
	  uses-permission android:name="android.permission.ACCESS_WIFI_STATE"
	  uses-permission android:name="android.permission.INTERNET"
	  uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"
	  
	  

