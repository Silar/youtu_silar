package com.youtu;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author silar
 * 
 */
public class Photo_Show extends Activity {

	private Button pr_home, pr_back, pr_share, pr_rename, pr_delete;
	private TextView photo_name, photo_time;
	private Bitmap bt;
	private Gallery ps_ga;
	private String photoUri, photoName;
	private int i_position, curImgPo;
	private ImageAdapter curImageAdapter;
	private String visitName;

	// 照片的名字
	private ArrayList<String> imgNameList = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_show);
		MyApplication.getInstance().addActivity(this);

		pr_home = (Button) findViewById(R.id.pr_home);
		pr_back = (Button) findViewById(R.id.pr_back);
		pr_share = (Button) findViewById(R.id.pr_share);
		pr_rename = (Button) findViewById(R.id.pr_rename);
		pr_delete = (Button) findViewById(R.id.pr_delete);
		photo_name = (TextView) findViewById(R.id.pr_name);
		photo_time = (TextView) findViewById(R.id.pr_time);

		ps_ga = (NewGallery) findViewById(R.id.ps_ga);

		// 获取从Photo_List页面传来的参数
		Intent intent = getIntent();
		visitName = intent.getStringExtra("Visit_Name");
		i_position = intent.getIntExtra("Photo_Position", 0);
		Log.d("游记名字", visitName);

		// 到特定的游记目录中获取图片
		File imgDir = new File("sdcard/YouTu/" + visitName);
		File[] imgFiles = imgDir.listFiles();

		// 文件排序,最新的排在首位
		Arrays.sort(imgFiles, new CompratorByLastModified());
		for (File file : imgFiles) {
			// 判断获得的内容是文件夹还是文件，如果是文件则列出来
			if (file.isFile()) {
				if (new Modify().isJPEG(file.toString())) {

					// fileUri = Uri.parse(file.getAbsolutePath());
					// imgUriList.add(fileUri);

					imgNameList.add(file.getName().toString());

					Log.d("TAG_LIST", file.getName().toString());

				}
			}

		}

		curImageAdapter = new ImageAdapter(this);
		// 添加ImageAdapter给Gallery对象
		ps_ga.setAdapter(curImageAdapter);
		// 设置Gallery显示的第一张图片
		ps_ga.setSelection(i_position);

		// 设置Gallery对象的滑动监听事件
		ps_ga.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				curImgPo = position;

				// 图片名字
				String name = imgNameList.get(position).toString();

				Log.d("TAG_NAME", name);

				// 图片地址
				String ph_path = "sdcard/YouTu/" + visitName + "/" + name;

				photoUri = ph_path;

				String na = name.substring(0, name.lastIndexOf(".")).trim();
				photo_name.setText(na);

				File file = new File(ph_path);
				long time = file.lastModified();
				SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
				Date currentTime = new Date(time);
				String ing = df.format(currentTime);
				photo_time.setText(ing);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		pr_home.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent pr_home = new Intent();
				pr_home.setClass(Photo_Show.this, Main.class);
				startActivity(pr_home);
				finish();

			}
		});

		pr_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent pr_back = new Intent();
				pr_back.setClass(Photo_Show.this, Photo_List.class);
				startActivity(pr_back);
				finish();

			}
		});

		/*
		 * 分享
		 */

		pr_share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				File file = new File(photoUri);
				shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
				shareIntent.setType("image/jpeg");
				startActivity(Intent.createChooser(shareIntent, "分享到"));

			}
		});

		/*
		 * 重命名
		 */

		pr_rename.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				LayoutInflater inflater = LayoutInflater.from(Photo_Show.this);
				View layout = inflater.inflate(R.layout.new_dialog,
						(ViewGroup) findViewById(R.id.nd));

				final EditText edit = (EditText) layout
						.findViewById(R.id.nd_rename);
				edit.setHint(photoName);

				// AlertDialog.Builder builder = new AlertDialog.Builder(this);
				AlertDialog alertDialog = new AlertDialog.Builder(
						Photo_Show.this)
						.setTitle("重新编辑")
						.setView(layout)
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub

										if (edit.getText().toString() != null) {

											String newName = edit.getText()
													.toString().trim();
											String newPhotoUri = "sdcard/YouTu"
													+ "/" + visitName + "/"
													+ newName + ".JPEG";

											File file = new File(photoUri);
											file.renameTo(new File(newPhotoUri));
											photo_name.setText(newName);

											imgNameList.set(curImgPo, newName
													+ ".JPEG");
											curImageAdapter
													.notifyDataSetChanged();

										}
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub

									}
								}).create();
				alertDialog.show();

			}
		});

		pr_delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(Photo_Show.this)
						/* 弹窗口最上面的文字 */

						.setMessage("确定删除当前相片？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub

										// 到图片地址进行删除图片

										File f = new File(photoUri);
										Log.d("Photo_List", f.getName()
												.toString());
										f.delete();

										// 删除图片后,更改当前图片的名字
										Iterator<String> it = imgNameList
												.iterator();
										while (it.hasNext()
												&& it.next()
														.toString()
														.equals(imgNameList
																.get(curImgPo)
																.toString())) {
											Log.d("当前照片",
													imgNameList.get(curImgPo)
															.toString());

											if (!imgNameList.isEmpty()) {

												String neName = it.next()
														.toString();
												String tv = neName.substring(0,
														neName.lastIndexOf("."));

												photo_name.setText(tv);

											}

										}

										imgNameList.remove(curImgPo);
										curImageAdapter.notifyDataSetChanged();

										if (imgNameList.isEmpty()) {
											Intent ba = new Intent();
											ba.setClass(Photo_Show.this,
													Photo_List.class);
											startActivity(ba);
											finish();
										}

									}
								})

						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub

									}
								}).show();

			}
		});

	}

	public class ImageAdapter extends BaseAdapter {

		int mGalleryItemBackground;

		// 定义context
		private Context mContext;

		// 声明 ImageAdapter
		public ImageAdapter(Context c) {
			mContext = c;

			// // 设置Gallery的背景
			// 获取自定义样式
			TypedArray attr = mContext
					.obtainStyledAttributes(R.styleable.MyGallery);
			// 获取样式里的资源
			mGalleryItemBackground = attr.getResourceId(
					R.styleable.MyGallery_android_galleryItemBackground, 0);
			attr.recycle();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imgNameList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public class ViewHolder {

			ImageView imageView;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder viewHolder = null;

			if (convertView == null) {

				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.gallery_item, null);

				viewHolder = new ViewHolder();

				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.ga_img);
				convertView.setTag(viewHolder);

				// convertView.setBackgroundResource(mGalleryItemBackground);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			// 图片名字
			String name = imgNameList.get(position).toString();

			Log.d("TAG_NAME", name);

			// 图片地址
			String path = "sdcard/YouTu/" + visitName + "/" + name;

			/*
			 * 利用BitmapFactory.Options. inSampleSize方法将文件地址直接转码成bitmap.
			 * 压缩图片大小,防止bitmap size exceeds VM budget的发生
			 */
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);

			opts.inSampleSize = new Modify().computeSampleSize(opts, -1,
					960 * 640);
			opts.inJustDecodeBounds = false;

			try {
				bt = BitmapFactory.decodeFile(path, opts);
			} catch (OutOfMemoryError err) {
			}

			viewHolder.imageView.setImageBitmap(bt);

			return convertView;
		}
	}

}
