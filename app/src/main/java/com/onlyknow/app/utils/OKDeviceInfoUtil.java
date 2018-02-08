package com.onlyknow.app.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OKDeviceInfoUtil {
	private Context context;

	public OKDeviceInfoUtil(Context context) {
		this.context = context;
	}

	// 获得手机型号
	public String getModel() {
		Build bd = new Build();
		@SuppressWarnings("static-access")
        String model = bd.MODEL;
		return model;
	}

	// 获得屏幕尺寸大小
	@SuppressWarnings("deprecation")
	public String getWindowSize() {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int w = manager.getDefaultDisplay().getWidth();
		int h = manager.getDefaultDisplay().getHeight();
		return w + "x" + h;
	}

	// 获得当前系统年月日
	public String getTime() {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");// ���Է�����޸����ڸ�ʽ
		String shijian = dateFormat.format(now);
		return shijian;
	}

	// 获取本机串号imei
	public String getIMIE() {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	// 判断SD卡是否存在
	public boolean ExistSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	// SD卡剩余空间
	@SuppressWarnings("deprecation")
	public long getSDFreeSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs statFs = new StatFs(path.getPath());
		long blocksize = statFs.getBlockSize();
		long freeblock = statFs.getAvailableBlocks();
		return (blocksize * freeblock) / 1024 / 1024;
	}

	// SD卡总容量
	@SuppressWarnings("deprecation")
	public long getSDALLSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs statFs = new StatFs(path.getPath());
		long blocksize = statFs.getBlockSize();
		long allblock = statFs.getBlockCount();
		return (blocksize * allblock) / 1024 / 1024;
	}

	// 判断wifi是否可用 turn 是 false 否
	public boolean WifiAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		} else {
			return false;
		}
	}

	// 判断GPS是否打开
	public boolean ExistGPS() {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

	// 判断移动网络是否可用
	public boolean MobileAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		} else {
			return false;
		}
	}

	// 判断网络是否可用
	public boolean NetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return true;
		} else {
			return false;
		}
	}

}
