package com.youtu;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.youtu.AsyncImageLoader.ImageCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Visit_List extends Activity {

	private Button vl_home, vl_back, vl_create;
	private GridView vl_grid;
	private List<Visit_Help> list = new ArrayList<Visit_Help>();
	private GridAdapter grid;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visit_list);
		MyApplication.getInstance().addActivity(this);

		vl_home = (Button) findViewById(R.id.vl_home);
		vl_back = (Button) findViewById(R.id.vl_back);
		vl_create = (Button) findViewById(R.id.vl_create);
		vl_grid = (GridView) findViewById(R.id.vl_grid);

		// 返回主页面
		vl_home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent home = new Intent();
				home.setClass(Visit_List.this, Main.class);
				startActivity(home);
			}
		});

		// 返回主页面
		vl_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent back = new Intent();
				back.setClass(Visit_List.this, Main.class);
				startActivity(back);
			}
		});

		// 创建新游记
		vl_create.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent();
				create.setClass(Visit_List.this, New_Visit.class);
				startActivity(create);

			}
		});

		/*
		 * 获取所有游记信息
		 */
		// 获取游记名字
		File file = new File("sdcard/YouTu");

		// 文件夹按日期排序（最新的排在上面）

		File[] files = file.listFiles();
		if (files.length > 0) {

			Arrays.sort(files, new CompratorByLastModified());
			// 生成动态数组，加入数据.

			for (File mCurrentFile : files) {
				// 判断获得的内容是文件夹还是文件，如果是文件夹则列出来.
				if (mCurrentFile.isDirectory()) {

					// 计算每个游记里有多少张相片

					int Pcount = 0;

					File d = new File("sdcard/YouTu/"
							+ mCurrentFile.getName().toString());
					File flist[] = d.listFiles();
					for (int x = 0; x < flist.length; x++) {
						Pcount++;
					}

					String vn = null;
					if (flist.length > 0) {
						File f1 = flist[0];
						vn = f1.getName().toString();
					}

					String path = "sdcard/YouTu/"
							+ mCurrentFile.getName().toString() + "/" + vn;

					// 将首张图片的地址,游记的名字,照片的数量存入list中
					list.add(new Visit_Help(path, mCurrentFile.getName()
							.toString(), Pcount));

				}
			}

			// 添加并且显示
			grid = new GridAdapter(this, vl_grid);
			vl_grid.setAdapter(grid);

			vl_grid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub

					TextView name = (TextView) view.findViewById(R.id.vi_name);
					name.setSelected(true);

					// 获取进入相册的名称并且将名称传给Photo_List.
					RelativeLayout rl = (RelativeLayout) view;
					TextView tv = (TextView) rl.getChildAt(1);
					String fn = (String) tv.getText();
					Log.d("游记", fn);

					// 将点击的Item相应的游记名字暂时保存，用来显示这个游记里面的照片.
					String dataN = "Fname";
					SharedPreferences set = getSharedPreferences(dataN,
							MODE_PRIVATE);
					SharedPreferences.Editor editor = set.edit();
					editor.putString("Fn", fn);
					editor.commit();

					Intent photo = new Intent();
					photo.setClass(Visit_List.this, Photo_List.class);
					startActivity(photo);

				}
			});
			// 长按显示是否删除游记
			vl_grid.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

				@Override
				public void onCreateContextMenu(ContextMenu menu, View v,
						ContextMenuInfo menuInfo) {
					// TODO Auto-generated method stub

					vl_grid.performHapticFeedback(
							HapticFeedbackConstants.LONG_PRESS,
							HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);

					menu.setHeaderTitle("温馨提示");
					menu.add(0, 0, 0, "删除");
					menu.add(0, 1, 0, "重命名");

				}
			});

		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"暂时还没有游记，赶快去创建一个吧！", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// AdapterView.AdapterContextMenuInfo来获取单元的信息。
		final AdapterView.AdapterContextMenuInfo Linfo = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		final int pos = Linfo.position;

		Visit_Help visit = list.get(pos);

		// 获取对应相册名字
		final String str = visit.getName().toString();

		// 获取相应游记的地址
		final String st = "sdcard/YouTu/" + str;

		switch (item.getItemId()) {
		// 删除游记文件夹
		case 0:

			new AlertDialog.Builder(Visit_List.this)
					/* 弹窗口最上面的文字 */

					.setMessage("确定删除当前游记？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

									// listview删除该Item
									list.remove(pos);

									// 到游记地址去删除游记
									new Modify().delFolder(st);
									// 更新gridView
									grid.notifyDataSetChanged();
									// 对比Main主页面继续本次旅行后对应的游记名字，如果删除的相册名字和它一样，则也把继续本次旅行后对应的名字删掉
									// 删除之后，将排在第一行的游记名字保存到“VisitName”中.
									SharedPreferences sh = getSharedPreferences(
											"VisitName", MODE_PRIVATE);
									String con = sh.getString("Vcontent", "");

									Log.d("Hello", con);
									if (con.equals(str)) {
										sh.edit().clear().commit();

										// 获取排在第一行游记的名字,并保存到“VisitName”中.
										File fo = new File("sdcard/YouTu");
										File[] fi = fo.listFiles();
										if (fi.length > 0) {
											Arrays.sort(
													fi,
													new CompratorByLastModified());
											File fil = fi[0];
											String filen = fil.getName()
													.toString();
											SharedPreferences.Editor edi = sh
													.edit();
											edi.putString("Vcontent", filen);
											edi.commit();
										}

									}

									// 删除游记相关描述信息
									SharedPreferences sha = getSharedPreferences(
											"Visit_Message", 0);
									sha.edit().remove(str).commit();

									// 删除SharedPreferences(Album)中的相关信息
									SharedPreferences shar = getSharedPreferences(
											"Album", 0);
									shar.edit().remove(str).commit();

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

			break;

		case 1:
			Intent modify = new Intent();
			modify.putExtra("Visit_Name", str);
			modify.setClass(Visit_List.this, Visit_Rename.class);
			startActivity(modify);
			break;

		}

		return true;
	}

	public class GridAdapter extends BaseAdapter {

		private Context mContext;
		private GridView gridView;
		private AsyncImageLoader asyncImageLoader;

		public GridAdapter(Context c, GridView gridview1) {
			mContext = c;
			gridView = gridview1;
			asyncImageLoader = new AsyncImageLoader();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder;

			if (convertView == null) {

				holder = new ViewHolder();

				// 根据自定义的Item布局加载布局
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.visit_item, null);

				holder.imageview = (ImageView) convertView
						.findViewById(R.id.vi_img);
				holder.count = (TextView) convertView
						.findViewById(R.id.vi_count);
				holder.name = (TextView) convertView.findViewById(R.id.vi_name);

				// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Visit_Help imageAndText = list.get(position);
			String path = imageAndText.getPath();
			holder.imageview.setTag(path);

			Drawable cacheImage = asyncImageLoader.loadDrawable(path,
					new ImageCallback() {

						@Override
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {
							// TODO Auto-generated method stub

							ImageView imageViewByTag = (ImageView) gridView
									.findViewWithTag(imageUrl);

							if (imageViewByTag != null) {
								imageViewByTag.setImageDrawable(imageDrawable);
							}

						}
					});
			if (cacheImage == null) {
				holder.imageview.setImageResource(R.drawable.logo);
			} else {
				holder.imageview.setImageDrawable(cacheImage);
			}

			holder.name.setText(imageAndText.getName());
			holder.count.setText(String.valueOf(imageAndText.getCount()));

			return convertView;
		}

		public class ViewHolder {
			ImageView imageview;
			TextView count;
			TextView name;
		}
	}

	/**
	 * 写Back键方法，直接返回主页面
	 */
	@Override
	public void onBackPressed() {
		Intent back = new Intent();
		back.setClass(Visit_List.this, Main.class);
		startActivity(back);
	}
}
