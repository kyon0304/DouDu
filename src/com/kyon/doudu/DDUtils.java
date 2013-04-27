package com.kyon.doudu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;

public final class DDUtils{
	public final static String TAG = "com.kyon.doudu";
	
	public static String CacheDir;
	public static String CacheCoverDir;
	
	public static String offlineJson;
	
	public static void Init(Context context, boolean isExternal) {
		setCacheDir(context, isExternal);
		readJson(context);
	}
	/**
	 * 设置缓存路径
	 * @param context
	 * @param isExternal
	 */
	private static void setCacheDir(Context context, boolean isExternal) {
		File cacheDir = null;
		if (isExternal) {
			cacheDir = context.getExternalCacheDir();
			if(cacheDir == null) {
				cacheDir = context.getCacheDir();
			}
		} else {
			cacheDir = context.getCacheDir();
		}

		if (cacheDir == null) {
			return;
		}

		//清理过期缓存
		//测试时注释掉
		//clearOldCache(cacheDir, 2);
		
		CacheDir = cacheDir.getAbsolutePath() + File.separator;
		CacheCoverDir = CreateOrGetDir(CacheDir + "coverImg") + File.separator;
		return;
	}

	/**
	 * 清理过时缓存
	 * @param dir
	 * @param outTimeDays
	 */
/*	private static void clearOldCache(File dir, int outTimeDays) {
		if (dir.isDirectory()) {
			File[] fileList = dir.listFiles();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, outTimeDays * -1);
			long purgeTime = cal.getTimeInMillis();
			for(File file : fileList ){
				if (file.isDirectory()) {
					clearOldCache(file, outTimeDays);
				}else {
					if (file.lastModified() < purgeTime) {
						file.delete();
					}
				}
			}
		}
	}
*/
	
	/**
	 * 创建文件夹或获取已有文件夹
	 * @param dirPath
	 * @return
	 */
	private static String CreateOrGetDir(String dirPath){
		File dir = new File(dirPath);
		if (dir.exists() && dir.isFile()) {
			dir.delete();
		}
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dirPath;
	}

	public static void readJson(Context context) {
		try {
			InputStream is = context.getAssets().open("isbn.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			offlineJson = new String(buffer);
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * 加载图片，如果图片太大则进行压缩
	 */
	public static Bitmap loadBitmapFile(String imageLocalPath, int ivWidth,
			int ivHeight) {
		System.gc();
		// First decode with inJustDecodeBounds=true to check dimensions
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageLocalPath, options);
		// Calculate inSampleSize
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
//		Log.i(ARUtils.TAG, "height: "+ivHeight);
//		Log.i(ARUtils.TAG, "width: "+ivWidth);
		if(ivHeight == Integer.MAX_VALUE) {
			options.inSampleSize = calculateInSampleSize(width, ivWidth);			
		}else {
			options.inSampleSize = calculateInSampleSize(width, height, ivWidth, ivHeight);
		}
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		
		Bitmap bm = null;
		try{
			bm =  BitmapFactory.decodeFile(imageLocalPath, options);
		}catch(OutOfMemoryError e) {
			Log.e(TAG, e.getMessage());
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
		}
		return bm;
	}
/**
 * 
 * @param imageLocalPath
 * @param ivWidth
 * @return
 */
	public static Bitmap loadBitmapFile(String imageLocalPath, int ivWidth) {
		return loadBitmapFile(imageLocalPath, ivWidth, Integer.MAX_VALUE); 
	}
	
	/*
	 * 计算用于图片缩小的抽样率
	 */
	private static int calculateInSampleSize(int imgWidth, int imgHeight,
			int reqWidth, int reqHeight) {
		int inSampleSize = 1;

		if (imgHeight > reqHeight || imgWidth > reqWidth) {
			if (imgWidth > imgHeight) {
				inSampleSize = Math
						.round((float) imgHeight / (float) reqHeight);
//				Log.i(ARUtils.TAG, "scale by height");
			} else {
				inSampleSize = Math.round((float) imgWidth / (float) reqWidth);
			}
		}
		return inSampleSize;
	}
	
	private static int calculateInSampleSize(int imgWidth, int reqWidth){
		int inSampleSize = 1;
		if(imgWidth > reqWidth) {
			inSampleSize = Math.round((float) imgWidth / (float) reqWidth);
		}
		return inSampleSize;
	}
	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return px;
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return dp;
	}
}