# OfflineStoreRepository
A project that can store your datas when you are posting some datas to server if network is in the state of disconnection,and post data to server as soon as network connect automatically.The stored data will not be lost even the application close or phone power off.


Note that you have to add permissions :
          <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/> 
	        <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
	        <uses-permission android:name="android.permission.INTERNET"/>
	        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/> 
in manifest file of your project.
