package com.photoselector.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.photoselector.R;
import com.photoselector.domain.PhotoSelectorDomain;
import com.photoselector.model.AlbumModel;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoItem.onItemClickListener;
import com.photoselector.ui.PhotoItem.onPhotoItemCheckedListener;
import com.photoselector.util.AnimationUtil;
import com.photoselector.util.CommonUtils;

public class PhotoSelectorActivity extends Activity implements
		onItemClickListener, onPhotoItemCheckedListener, OnItemClickListener,
		OnClickListener {

	public static final int REQUEST_PHOTO = 0;
	private static final int REQUEST_CAMERA = 1;
	/**
	 * 文件夹名 "/AAJiang/001/",
	 * 注意文件夹两端带"/",不需要带Environment.getExternalStorageState();
	 */
	public static String FOLDER_NAME = "FOLDER_NAME";// 文件夹路径
	/**
	 * 文件名 前缀,后面默认会加时间
	 */
	public static String PREFIX_NAME = "PREFIX_NAME";// 文件名前缀
	private String folder_name_save;
	private String prefix_name_save;

	public static final String RECCENT_PHOTO = "最近照片";

	private GridView gvPhotos;
	private ListView lvAblum;
	private Button btnOk;
	private TextView tvAlbum, tvPreview, tvTitle;
	private PhotoSelectorDomain photoSelectorDomain;
	private PhotoSelectorAdapter photoAdapter;
	private AlbumAdapter albumAdapter;
	private RelativeLayout layoutAlbum;
	private ArrayList<PhotoModel> selected;
	private Uri mPhotoUri;
	private String fileFullPath;
	private String fileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_photoselector);

		CommonUtils.initImageLoader(getApplicationContext());

		photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());

		selected = new ArrayList<PhotoModel>();

		tvTitle = (TextView) findViewById(R.id.tv_title_lh);
		gvPhotos = (GridView) findViewById(R.id.gv_photos_ar);
		lvAblum = (ListView) findViewById(R.id.lv_ablum_ar);
		btnOk = (Button) findViewById(R.id.btn_right_lh);
		tvAlbum = (TextView) findViewById(R.id.tv_album_ar);
		tvPreview = (TextView) findViewById(R.id.tv_preview_ar);
		layoutAlbum = (RelativeLayout) findViewById(R.id.layout_album_ar);

		btnOk.setOnClickListener(this);
		tvAlbum.setOnClickListener(this);
		tvPreview.setOnClickListener(this);

		photoAdapter = new PhotoSelectorAdapter(getApplicationContext(),
				new ArrayList<PhotoModel>(), CommonUtils.getWidthPixels(this),
				this, this, this);
		gvPhotos.setAdapter(photoAdapter);

		albumAdapter = new AlbumAdapter(getApplicationContext(),
				new ArrayList<AlbumModel>());
		lvAblum.setAdapter(albumAdapter);
		lvAblum.setOnItemClickListener(this);

		findViewById(R.id.bv_back_lh).setOnClickListener(this); // 返回
		Log.e("test", "照片 1 获取最近照片");

		photoSelectorDomain.getReccent(reccentListener); // 更新最近照片,20161117JS增加加文件是否存在的判断
		Log.e("test", "照片 3 更新最近照片");

		photoSelectorDomain.updateAlbum(albumListener); // 跟新相册信息
		Log.e("test", "照片 4 更新相册信息");

		Intent intent = getIntent();
		if (intent != null) {
			if (intent.getStringExtra(FOLDER_NAME) != null
					&& !intent.getStringExtra(FOLDER_NAME).trim().isEmpty()) {
				folder_name_save = intent.getStringExtra(FOLDER_NAME);
			}
			if (intent != null && intent.getStringExtra(PREFIX_NAME) != null
					&& !intent.getStringExtra(PREFIX_NAME).trim().isEmpty()) {
				prefix_name_save = intent.getStringExtra(PREFIX_NAME);
			}
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_right_lh)
			ok(); // 选完照片
		else if (v.getId() == R.id.tv_album_ar)
			album();
		else if (v.getId() == R.id.tv_preview_ar)
			priview();
		else if (v.getId() == R.id.tv_camera_vc)
			catchPicture();
		else if (v.getId() == R.id.bv_back_lh)
			finish();
	}

	/** 拍照 */
	private void catchPicture() {
		String path;
		if (folder_name_save != null && !folder_name_save.trim().isEmpty()
				&& folder_name_save.startsWith("/")
				&& folder_name_save.endsWith("/")) {
			path = Environment.getExternalStorageDirectory() + folder_name_save;
		} else {
			path = Environment.getExternalStorageDirectory() + "/Aotu/";
		}
		File f = new File(path);
		if (!hasSdcard()) {
			Log.e("test", "无可用内存");
			return;
		}
		if (!f.exists()) {
			f.mkdirs();
		}
		long currentTimeMillis = System.currentTimeMillis();
		long currentTimeMillisShort = currentTimeMillis / 1000;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");// 好像不能有空格，会导致不上传
		Date curDate = new Date(currentTimeMillis);// 获取当前时间
		String prefixName;
		if (prefix_name_save == null || prefix_name_save.trim().isEmpty()) {
			prefixName = "Aotu_" + formatter.format(curDate);// 文件名称前缀 (前期时间)
		} else {
			prefixName = prefix_name_save + formatter.format(curDate);// 文件名称前缀
																		// (前期时间)
		}

		fileName = prefixName + ".jpg";

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (intent.resolveActivity(getPackageManager()) != null) {
			ContentValues contentValues = new ContentValues();// 注意大小
			fileFullPath = path + fileName;

			contentValues.put(MediaStore.Images.Media.DATA, fileFullPath);
			// 如果想拍完存在系统相机的默认目录,改为
			// contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,
			// "111111.jpg");

			contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

			contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME,
					prefixName);
			/**
			 * 以下代码可能导致部分相机拍照出现时间为1970年，并且照片出现旋转
			 */
			// contentValues.put(MediaStore.Images.Media.DATE_ADDED,
			// currentTimeMillisShort);
			// contentValues.put(MediaStore.Images.ImageColumns.DATE_TAKEN,
			// currentTimeMillisShort);
			// contentValues.put(MediaStore.Images.ImageColumns.DATE_MODIFIED,
			// currentTimeMillisShort);

			mPhotoUri = getContentResolver()
					.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							contentValues);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
			// startActivityForResult(intent, 111);
			CommonUtils.launchActivityForResult(this, intent, REQUEST_CAMERA);
		}

	}

	/**
	 * 检查设备是否存在SDCard的工具方法
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// 有存储的SDCard
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("test", "照片 5 onActivityResult requestCode=" + requestCode);

		Log.e("test", "检查的路径" + fileFullPath);
		if (resultCode == RESULT_OK && isFileExistAndCanRead(fileFullPath)) {// &&
																				// resultCode
																				// ==
																				// RESULT_OK
																				// 解决拍照时按返回键崩溃
			// 其次把文件插入到系统图库
			try {

				MediaStore.Images.Media.insertImage(getApplicationContext()
						.getContentResolver(), fileFullPath, fileName, null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			// 最后通知图库更新
			getApplicationContext().sendBroadcast(
					new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
							.parse("file://" + fileFullPath)));// 实际取值 file：///
			// 如果需要返回到这一页，直接调用一下更新的页面
			PhotoModel photoModel0 = new PhotoModel(fileFullPath);
			selected.clear();
			selected.add(photoModel0);
			ok();
		} else {
			Log.e("test", "文件出错,不可读或未拍照");
			selected.clear();
			ok();
		}
		/**
		 * 以下报错
		 */
		// if (data == null) {
		// Log.e("test", "照片 data == null");// 为空
		// return;
		// }
		// if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
		//
		// if (data != null) {
		// PhotoModel photoModel = new PhotoModel(CommonUtils.query(
		// getApplicationContext(), data.getData()));
		// selected.clear();
		// selected.add(photoModel);
		// ok();
		//
		// }
		//
		// }
	}

	/** 完成 */
	private void ok() {
		if (selected.isEmpty()) {
			setResult(RESULT_CANCELED);
		} else {
			Intent data = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("photos", selected);
			data.putExtras(bundle);
			setResult(RESULT_OK, data);
		}
		finish();
	}

	/** 预览照片 */
	private void priview() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("photos", selected);
		CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
	}

	private void album() {
		if (layoutAlbum.getVisibility() == View.GONE) {
			popAlbum();
		} else {
			hideAlbum();
		}
	}

	/** 弹出相册列表 */
	private void popAlbum() {
		layoutAlbum.setVisibility(View.VISIBLE);
		new AnimationUtil(getApplicationContext(), R.anim.translate_up_current)
				.setLinearInterpolator().startAnimation(layoutAlbum);
	}

	/** 隐藏相册列表 */
	private void hideAlbum() {
		new AnimationUtil(getApplicationContext(), R.anim.translate_down)
				.setLinearInterpolator().startAnimation(layoutAlbum);
		layoutAlbum.setVisibility(View.GONE);
	}

	/** 清空选中的图片 */
	private void reset() {
		selected.clear();
		tvPreview.setText("预览");
		tvPreview.setEnabled(false);
	}

	@Override
	/** 点击查看照片 */
	public void onItemClick(int position) {
		Bundle bundle = new Bundle();
		if (tvAlbum.getText().toString().equals(RECCENT_PHOTO))
			bundle.putInt("position", position - 1);
		else
			bundle.putInt("position", position);
		bundle.putString("album", tvAlbum.getText().toString());
		CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
	}

	@Override
	/** 照片选中状态改变之后 */
	public void onCheckedChanged(PhotoModel photoModel,
			CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			selected.add(photoModel);
			tvPreview.setEnabled(true);
		} else {
			selected.remove(photoModel);
		}
		tvPreview.setText("预览(" + selected.size() + ")"); // 修改预览数量

		if (selected.isEmpty()) {
			tvPreview.setEnabled(false);
			tvPreview.setText("预览");
		}
	}

	@Override
	public void onBackPressed() {
		if (layoutAlbum.getVisibility() == View.VISIBLE) {
			hideAlbum();
		} else
			super.onBackPressed();
	}

	@Override
	/** 相册列表点击事件 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		AlbumModel current = (AlbumModel) parent.getItemAtPosition(position);
		for (int i = 0; i < parent.getCount(); i++) {
			AlbumModel album = (AlbumModel) parent.getItemAtPosition(i);
			if (i == position)
				album.setCheck(true);
			else
				album.setCheck(false);
		}
		albumAdapter.notifyDataSetChanged();
		hideAlbum();
		tvAlbum.setText(current.getName());
		tvTitle.setText(current.getName());

		// 更新照片列表
		if (current.getName().equals(RECCENT_PHOTO))
			photoSelectorDomain.getReccent(reccentListener);
		else
			photoSelectorDomain.getAlbum(current.getName(), reccentListener); // 获取选中相册的照片
	}

	/** 获取本地图库照片回调 */
	public interface OnLocalReccentListener {
		public void onPhotoLoaded(List<PhotoModel> photos);
	}

	/** 获取本地相册信息回调 */
	public interface OnLocalAlbumListener {
		public void onAlbumLoaded(List<AlbumModel> albums);
	}

	private OnLocalAlbumListener albumListener = new OnLocalAlbumListener() {
		@Override
		public void onAlbumLoaded(List<AlbumModel> albums) {
			albumAdapter.update(albums);
		}
	};

	private OnLocalReccentListener reccentListener = new OnLocalReccentListener() {
		@Override
		public void onPhotoLoaded(List<PhotoModel> photos) {
			if (tvAlbum.getText().equals(RECCENT_PHOTO))
				photos.add(0, new PhotoModel());
			photoAdapter.update(photos);
			gvPhotos.smoothScrollToPosition(0); // 滚动到顶端
			reset();
		}
	};

	private boolean isFileExistAndCanRead(String path) {

		if (path == null || path.trim().isEmpty()) {
			return false;
		}
		File f = new File(path);
		if (f.exists() && f.canRead()) {
			return true;
		} else {
			return false;
		}

	}
}
