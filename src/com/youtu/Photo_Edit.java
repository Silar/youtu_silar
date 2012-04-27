package com.youtu;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Photo_Edit extends Activity {

	private Button pe_sure, pe_photo;
	private EditText pe_edit;
	private ImageView pe_img;
	private Bitmap bt;
	private String ph_path, ph_add;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_edit);
		MyApplication.getInstance().addActivity(this);

		pe_sure = (Button) findViewById(R.id.pe_sure);
		pe_photo = (Button) findViewById(R.id.pe_photo);
		pe_edit = (EditText) findViewById(R.id.pe_edit);
		pe_img = (ImageView) findViewById(R.id.pe_img);

		/* 获取正在进行的游记的名字 */
		String STORE_NAME = "VisitName";
		SharedPreferences set = getSharedPreferences(STORE_NAME, MODE_PRIVATE);
		String con = set.getString("Vcontent", "");
		Log.d("Main", con);
		ph_add = con;
		ph_path = "sdcard/YouTu/" + con + "/You.JPEG";

		/*
		 * 利用BitmapFactory.Options.inSampleSize方法将文件地址直接转码成bitmap.压缩图片大小,
		 * 防止bitmap size exceeds VM budget的发生
		 */
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(ph_path, opts);

		opts.inSampleSize = new Modify().computeSampleSize(opts, -1, 960 * 640);
		opts.inJustDecodeBounds = false;

		try {
			bt = BitmapFactory.decodeFile(ph_path, opts);
		} catch (OutOfMemoryError err) {
		}

		pe_img.setImageBitmap(bt);

		pe_sure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 获取游记目录去保存图片.
				String STORE_NAME = "VisitName";
				SharedPreferences set = getSharedPreferences(STORE_NAME,
						MODE_PRIVATE);
				String con = set.getString("Vcontent", "");

				// 获取为游记图片命名所需的计数.
				SharedPreferences pcount = getSharedPreferences("VisitCount", 0);
				int num = pcount.getInt(con, 0);
				num = num + 1;

				// 重新保存计数提交.
				SharedPreferences.Editor pe = pcount.edit();
				pe.putInt(con, num);
				pe.commit();

				// String photo_name = pm_et1.getText().toString().trim();

				String photo_name = null;
				if (0 < num && num < 10) {

					if (pe_edit.getText().toString().equals("")) {

						photo_name = "00" + String.valueOf(num) + "_IMG";

					} else
						photo_name = "00" + String.valueOf(num)
								+ pe_edit.getText().toString().trim();

				} else if (num < 100) {
					if (pe_edit.getText().toString().equals("")) {

						photo_name = "0" + String.valueOf(num) + "_IMG";

					} else

						photo_name = "0" + String.valueOf(num)
								+ pe_edit.getText().toString().trim();

				} else {
					if (pe_edit.getText().toString().equals("")) {

						photo_name = String.valueOf(num) + "_IMG";

					} else
						photo_name = String.valueOf(num)
								+ pe_edit.getText().toString().trim();
				}

				// 将点击的Item相应的游记名字暂时保存，用来显示这个游记里面的照片.
				String dataN = "Fname";
				SharedPreferences pl = getSharedPreferences(dataN, MODE_PRIVATE);
				SharedPreferences.Editor editor = pl.edit();
				editor.putString("Fn", con);
				editor.commit();

				// 保存图片
				String fs = "sdcard/YouTu/" + con + "/";

				File photo = new File(ph_path);
				photo.renameTo(new File(fs + photo_name + ".JPEG"));

				// 跳转到相片列表页面
				Intent pm_intent3 = new Intent();
				pm_intent3.setClass(Photo_Edit.this, Photo_List.class);
				startActivity(pm_intent3);
				finish();
			}

		});

		pe_photo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				StartCamera();
				// 获取游记目录去保存图片.
				String STORE_NAME = "VisitName";
				SharedPreferences set = getSharedPreferences(STORE_NAME,
						MODE_PRIVATE);
				String con = set.getString("Vcontent", "");

				// 获取为游记图片命名所需的计数.
				SharedPreferences pcount = getSharedPreferences("VisitCount", 0);
				int num = pcount.getInt(con, 0);
				num = num + 1;

				// 重新保存计数提交.
				SharedPreferences.Editor pe = pcount.edit();
				pe.putInt(con, num);
				pe.commit();

				// String photo_name = pm_et1.getText().toString().trim();

				String photo_name = null;
				if (0 < num && num < 10) {

					if (pe_edit.getText().toString().equals("")) {

						photo_name = "00" + String.valueOf(num) + "_IMG";

					} else
						photo_name = "00" + String.valueOf(num)
								+ pe_edit.getText().toString().trim();

				} else if (num < 100) {
					if (pe_edit.getText().toString().equals("")) {

						photo_name = "0" + String.valueOf(num) + "_IMG";

					} else

						photo_name = "0" + String.valueOf(num)
								+ pe_edit.getText().toString().trim();

				} else {
					if (pe_edit.getText().toString().equals("")) {

						photo_name = String.valueOf(num) + "_IMG";

					} else
						photo_name = String.valueOf(num)
								+ pe_edit.getText().toString().trim();
				}

				// 将点击的Item相应的游记名字暂时保存，用来显示这个游记里面的照片.
				String dataN = "Fname";
				SharedPreferences pl = getSharedPreferences(dataN, MODE_PRIVATE);
				SharedPreferences.Editor editor = pl.edit();
				editor.putString("Fn", con);
				editor.commit();

				// 保存图片
				String fs = "sdcard/YouTu/" + con + "/";

				File photo = new File(ph_path);
				photo.renameTo(new File(fs + photo_name + ".JPEG"));

			}
		});

	}

	// 调用系统相机

	private void StartCamera() {

		Intent ca_intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		String SAVE_PATH_IN_SDCARD = "sdcard/YouTu/" + ph_add + "/";
		// 将图片暂时存储在SD上
		ca_intent2.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(SAVE_PATH_IN_SDCARD, "You.JPEG")));

		startActivityForResult(ca_intent2, 10);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			if (requestCode == 10) {

				// 跳转到图片编辑界面.
				Intent pm = new Intent();
				pm.setClass(Photo_Edit.this, Photo_Edit.class);
				startActivity(pm);
			}
		}
		if (resultCode == RESULT_CANCELED)
			return;

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 重写Back键方法，直接返回Main主页面.
	 */

	@Override
	public void onBackPressed() {

		new AlertDialog.Builder(Photo_Edit.this).setTitle("温馨提示")
				.setMessage("确定放弃该图片？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Intent pm_intent2 = new Intent();
						pm_intent2.setClass(Photo_Edit.this, Main.class);
						startActivity(pm_intent2);
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				}).show();
	}

}
