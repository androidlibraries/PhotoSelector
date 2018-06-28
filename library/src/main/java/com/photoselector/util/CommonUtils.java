package com.photoselector.util;

import java.io.File;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.photoselector.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;

/**
 * 通用工具类
 * 
 * @author chenww
 * 
 */
public class CommonUtils {

	/**
	 * 开启activity
	 */
	public static void launchActivity(Context context, Class<?> activity) {
		Intent intent = new Intent(context, activity);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		context.startActivity(intent);
	}

	/**
	 * 开启activity(带参数)
	 */
	public static void launchActivity(Context context, Class<?> activity,
			Bundle bundle) {
		Intent intent = new Intent(context, activity);
		intent.putExtras(bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		context.startActivity(intent);
	}

	/**
	 * 开启activity(带参数)
	 */
	public static void launchActivity(Context context, Class<?> activity,
			String key, int value) {
		Bundle bundle = new Bundle();
		bundle.putInt(key, value);
		launchActivity(context, activity, bundle);
	}

	public static void launchActivity(Context context, Class<?> activity,
			String key, String value) {
		Bundle bundle = new Bundle();
		bundle.putString(key, value);
		launchActivity(context, activity, bundle);
	}

	public static void launchActivityForResult(Activity context,
			Class<?> activity, int requestCode) {
		Intent intent = new Intent(context, activity);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		context.startActivityForResult(intent, requestCode);
	}

	// public static void launchFragmentActivityForResult(FragmentActivity
	// fragmentActivity,
	// Class<?> activity, int requestCode) {
	// Intent intent = new Intent(fragmentActivity, activity);
	// intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	// fragmentActivity.startActivityForResult(intent, requestCode);
	// }
	// public static void launchFragmentActivityForResult(FragmentActivity
	// fragmentActivity,
	// Intent intent, int requestCode) {
	// fragmentActivity.startActivityForResult(intent, requestCode);
	// }
	public static void launchActivityForResult(Activity activity,
			Intent intent, int requestCode) {
		activity.startActivityForResult(intent, requestCode);
	}

	/** 启动一个服务 */
	public static void launchService(Context context, Class<?> service) {
		Intent intent = new Intent(context, service);
		context.startService(intent);
	}

	public static void stopService(Context context, Class<?> service) {
		Intent intent = new Intent(context, service);
		context.stopService(intent);
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param text
	 * @return true null false !null
	 */
	public static boolean isNull(CharSequence text) {
		if (text == null || "".equals(text.toString().trim())
				|| "null".equals(text))
			return true;
		return false;
	}

	/** 获取屏幕宽度 */
	public static int getWidthPixels(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/** 获取屏幕高度 */
	public static int getHeightPixels(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/** 通过Uri获取图片路径 */
	public static String query(Context context, Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { ImageColumns.DATA }, null, null, null);
		cursor.moveToNext();
		return cursor.getString(cursor.getColumnIndex(ImageColumns.DATA));
	}

	/**
	 * 方法说明：初始化ImageLoader
	 * @author Aotu-JS ,E-mail:js@jiangjiesheng.com
	 * @version 创建时间：2017年1月11日 上午10:02:46 
	 *
	 * @param ctx
	 */
	public static void initImageLoader(Context ctx) {
		if (ctx != null) {
			DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder() //
					.considerExifParams(true) // 调整图片方向
					.resetViewBeforeLoading(true) // 载入之前重置ImageView
					.showImageOnLoading(R.drawable.ic_picture_loading) // 载入时图片设置为黑色
					.showImageOnFail(R.drawable.ic_picture_loadfailed) // 加载失败时显示的图片
					.cacheInMemory(false)// 161121 JS 新增调用 OOM问题
					.cacheOnDisk(false)// 161121 JS 新增调用 OOM问题
					.delayBeforeLoading(0) // 载入之前的延迟时间
					.build(); //
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					ctx).defaultDisplayImageOptions(defaultDisplayImageOptions)
					.memoryCacheExtraOptions(480, 800).threadPoolSize(5)
					.build();
			ImageLoader.getInstance().init(config);
		}
	}

}
