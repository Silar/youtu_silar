package com.youtu;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Simple_liu
 * 
 *         游图主页面
 * 
 */
public class Main extends Activity {
	/** Called when the activity is first created. */

	private Button main_photo, main_newVisit, main_visitList;
	private ListView main_list;
	private Bitmap bt;
	private ImageView main_img;
	private TextView main_empty, main_see, main_blist;

	private ArrayList<String> visit_array = new ArrayList<String>();
	private ArrayList<String> visit_hold = new ArrayList<String>();

	private ListAdapter listAdapter;

	private String ph_save, youji;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		MyApplication.getInstance().addActivity(this);

		new Modify().MakeDirs("YouTu");

		main_blist = (TextView) findViewById(R.id.main_blist);
		main_photo = (Button) findViewById(R.id.main_photo);
		main_newVisit = (Button) findViewById(R.id.main_new_visit);
		main_visitList = (Button) findViewById(R.id.main_visit_list);
		main_img = (ImageView) findViewById(R.id.main_img);
		main_list = (ListView) findViewById(R.id.main_list);
		main_empty = (TextView) findViewById(R.id.main_empty);
		main_see = (TextView) findViewById(R.id.main_see);

		/* 显示正在进行的游记的名字 */
		String STORE_NAME = "VisitName";
		SharedPreferences set = getSharedPreferences(STORE_NAME, MODE_PRIVATE);
		String con = set.getString("Vcontent", "");
		Log.d("Main", con);
		ph_save = con;

		main_blist.setText(con);

		// 设置当前游记的背景图
		File fol = new File("sdcard/YouTu/" + con);
		File[] files = fol.listFiles();

		if (files.length > 0) {
			Arrays.sort(files, new CompratorByLastModified());

			File ph = files[0];
			String st1 = "sdcard/YouTu/" + con + "/"
					+ ph.getName().toString().trim();

			BitmapFactory.Options opts1 = new BitmapFactory.Options();
			opts1.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(st1, opts1);

			opts1.inSampleSize = new Modify().computeSampleSize(opts1, -1,
					320 * 240);
			opts1.inJustDecodeBounds = false;

			try {
				bt = BitmapFactory.decodeFile(st1, opts1);
			} catch (OutOfMemoryError err) {
			}
			main_img.setImageBitmap(bt);

		}

		listAdapter = new ListAdapter();

		main_list.setAdapter(listAdapter);

		// 游记列表
		main_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				if (visit_hold.size() > 0) {

					RelativeLayout rl = (RelativeLayout) view;
					TextView tv = (TextView) rl.getChildAt(0);
					String ff = (String) tv.getText();
					String fn = ff.substring(0, ff.lastIndexOf("(")).trim();
					main_blist.setText(fn);

					youji = fn;

					SharedPreferences set = getSharedPreferences("VisitName",
							MODE_PRIVATE);
					SharedPreferences.Editor editor = set.edit();
					editor.putString("Vcontent", fn);
					editor.commit();

					// 设置当前游记的背景图
					File folder = new File("sdcard/YouTu/" + fn);
					File[] files = folder.listFiles();

					if (files.length > 0) {
						Arrays.sort(files, new CompratorByLastModified());

						File ph = files[0];
						String st1 = "sdcard/YouTu/" + fn + "/"
								+ ph.getName().toString().trim();

						BitmapFactory.Options opts1 = new BitmapFactory.Options();
						opts1.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(st1, opts1);

						opts1.inSampleSize = new Modify().computeSampleSize(
								opts1, -1, 320 * 240);
						opts1.inJustDecodeBounds = false;

						try {
							bt = BitmapFactory.decodeFile(st1, opts1);
						} catch (OutOfMemoryError err) {
						}
						main_img.setImageBitmap(bt);
					} else {
						main_img.setImageBitmap(null);
						main_see.setVisibility(View.VISIBLE);
					}

					for (String visit_he : visit_array) {
						visit_hold.add(visit_he);
					}
					removeDuplicateWithOrder(visit_hold);
					visit_hold.remove(position);
					listAdapter.notifyDataSetChanged();
				}

			}
		});

		File file = new File("sdcard/YouTu");
		File[] folder = file.listFiles();
		if (folder.length > 0) {

			// 对文件夹进行时间的排序,最新创建的排在最上方
			Arrays.sort(folder, new CompratorByLastModified());

			for (File currentFolder : folder) {
				// 判断获得的内容是文件夹还是文件，如果是文件夹则列出来.
				if (currentFolder.isDirectory()) {

					// 计算每个游记里有多少张相片
					int Pcount = 0;
					File d = new File("sdcard/YouTu/"
							+ currentFolder.getName().toString());
					File flist[] = d.listFiles();
					for (int x = 0; x < flist.length; x++) {
						Pcount++;
					}

					visit_array.add(currentFolder.getName().toString() + "("
							+ Pcount + ")");
					if (visit_array.size() > 0) {

						// String name = visit_array.get(0).toString();
						// main_blist.setText(name);
						main_blist.setClickable(true);
						main_img.setClickable(true);
					} else {
						// main_blist.setText("暂无游记");
					}
				}
			}
		}

		if (con == null && folder.length > 0) {
			File first = folder[0];

			main_blist.setText(first.getName().toString());

			File[] firfol = first.listFiles();

			if (firfol.length > 0) {

				Arrays.sort(firfol, new CompratorByLastModified());
				String st1 = "sdcard/YouTu/" + first.getName().toString() + "/"
						+ firfol[0].toString();

				BitmapFactory.Options opts1 = new BitmapFactory.Options();
				opts1.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(st1, opts1);

				opts1.inSampleSize = new Modify().computeSampleSize(opts1, -1,
						320 * 240);
				opts1.inJustDecodeBounds = false;

				try {
					bt = BitmapFactory.decodeFile(st1, opts1);
				} catch (OutOfMemoryError err) {
				}
				main_img.setImageBitmap(bt);

			}

		}

		// 将所有游记的名字都存入visit_hold数组中.
		for (String visit_he : visit_array) {
			visit_hold.add(visit_he);
		}

		// 判断Visit_Name保存的游记名字与visit_hold中保存的游记数组做对比,删除相同的元素
		if (visit_hold.size() > 0) {

			for (int i = 0; i < visit_hold.size(); i++) {
				String visit = visit_hold.get(i).toString();

				String name = visit.substring(0, visit.lastIndexOf("(")).trim();
				if (name.equals(con)) {
					visit_hold.remove(i);
				}
			}

		}
		/*
		 * 弹出游记列表选择
		 */
		if (main_blist.isClickable() == true) {
			main_blist.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (main_list.getVisibility() == View.VISIBLE) {
						main_list.setVisibility(View.GONE);
						main_newVisit.setVisibility(View.VISIBLE);
						main_visitList.setVisibility(View.VISIBLE);

					} else if (main_list.getVisibility() == View.GONE) {
						main_list.setVisibility(View.VISIBLE);
						main_newVisit.setVisibility(View.GONE);
						main_visitList.setVisibility(View.GONE);
					}

					if (visit_hold.size() == 0) {
						if (main_empty.getVisibility() == View.GONE) {
							main_empty.setVisibility(View.VISIBLE);
						} else if (main_empty.getVisibility() == View.VISIBLE) {
							main_empty.setVisibility(View.GONE);
						}
					}
				}
			});
		}
		/*
		 * 拍照
		 */
		main_photo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartCamera();

			}
		});

		/*
		 * 跳转到新建游记页面
		 */

		main_newVisit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent new_visit = new Intent();
				new_visit.setClass(Main.this, New_Visit.class);
				startActivity(new_visit);

			}
		});

		/*
		 * 跳转到游记陈列页面
		 */
		main_visitList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent visit_list = new Intent();
				visit_list.setClass(Main.this, Visit_List.class);
				startActivity(visit_list);

			}
		});

		if (main_blist.isClickable() == true) {
			main_img.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					// 将点击的Item相应的游记名字暂时保存，用来显示这个游记里面的照片.
					String dataN = "Fname";
					SharedPreferences set = getSharedPreferences(dataN,
							MODE_PRIVATE);
					SharedPreferences.Editor editor = set.edit();
					if (youji == null) {
						editor.putString("Fn", ph_save);
					} else {
						editor.putString("Fn", youji);

					}
					editor.commit();
					Intent list = new Intent();
					list.setClass(Main.this, Photo_List.class);
					startActivity(list);

				}
			});
		}
	}

	public class ListAdapter extends ArrayAdapter<String> {

		public ListAdapter() {
			super(Main.this, R.layout.main_list_item, visit_hold);
			// TODO Auto-generated constructor stub
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.main_list_item, parent, false);

			}
			TextView label = (TextView) row.findViewById(R.id.list_name);

			label.setText(visit_hold.get(position));
			return row;
		}

	}

	// 调用系统相机

	private void StartCamera() {

		Intent ca_intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		String SAVE_PATH_IN_SDCARD = "sdcard/YouTu/" + ph_save + "/";
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
				pm.setClass(Main.this, Photo_Edit.class);
				startActivity(pm);
			}
		}
		if (resultCode == RESULT_CANCELED)
			return;

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 删除重复的list对象
	 * 
	 * @param arlList
	 */
	public void removeDuplicateWithOrder(ArrayList<String> arlList) {
		Set<String> set = new HashSet<String>();
		List<String> newList = new ArrayList<String>();
		for (Iterator<String> iter = arlList.iterator(); iter.hasNext();) {
			String element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		arlList.clear();
		arlList.addAll(newList);
	}

	/**
	 * 重写Back键方法，弹出对话框，确定是否要关闭程序
	 */

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("温馨提示").setMessage("您是否要退出游图？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						MyApplication.getInstance().exit();

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).show();

	}

	/* 覆写下面两个方法 */
	/* 添加菜单 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* menu.add(组ID，项ID，显示顺序，显示标题) */
		menu.add(0, 0, 0, "关于")
				.setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, 1, 1, "退出").setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	/* 处理菜单事件 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();// 得到当前选中MenuItem的ID
		switch (item_id) {
		case 0: {
			// 关于游图
			dialog();
		}
			break;
		case 1: {
			// 退出程序
			MyApplication.getInstance().exit();
		}
			break;
		}
		return true;
	}

	// 定义对话框
	protected void dialog() {

		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.about_visit,
				(ViewGroup) findViewById(R.id.av));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		AlertDialog alertDialog = builder.create();
		alertDialog.setButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				dialog.dismiss();

			}
		});
		alertDialog.show();
	}

}