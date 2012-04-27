package com.youtu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Simple_liu
 * 
 *         照片陈列页面
 * 
 */
public class Photo_List extends Activity {

	private Button pl_home, pl_back, pl_add, pl_camera;
	private TextView pl_name, pl_attribute;
	private GridView pl_grid;
	private Bitmap bt, bit;;
	private String visit_name;

	ArrayList<HashMap<String, Object>> pl_listItem = new ArrayList<HashMap<String, Object>>();
	HashMap<String, Object> map = new HashMap<String, Object>();
	// SimpleAdapter listItemAdapter;

	MyAdapter listItemAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_list);
		MyApplication.getInstance().addActivity(this);

		pl_home = (Button) findViewById(R.id.pl_home);
		pl_back = (Button) findViewById(R.id.pl_back);
		pl_add = (Button) findViewById(R.id.pl_add);
		pl_camera = (Button) findViewById(R.id.pl_camera);

		pl_name = (TextView) findViewById(R.id.pl_name);
		pl_attribute = (TextView) findViewById(R.id.pl_attribute);

		pl_grid = (GridView) findViewById(R.id.pl_grid);

		// 获取当前存储游记的名字
		String dataN = "Fname";
		SharedPreferences sp = getSharedPreferences(dataN, MODE_PRIVATE);
		final String Fn = sp.getString("Fn", "");
		visit_name = Fn;

		Log.d("Photo_List", Fn);
		pl_name.setText(Fn);

		if (Fn != null) {

			// 删除"Fname"中的值
			sp.edit().clear();

			File file = new File("sdcard/YouTu" + "/" + Fn + "/");
			File[] pl_files = file.listFiles();

			// 自定义文件创建时间显示
			SimpleDateFormat Nformat = new SimpleDateFormat("yyyy.MM.dd");
			// 获取文件创建时间
			long time = file.lastModified();
			Date currentTime = new Date(time);
			String ing = Nformat.format(currentTime);
			pl_attribute.setText(ing + "/" + "(" + pl_files.length + ")");

			if (pl_files.length > 0) {

				// 文件按日期排序（最新的排在上面）
				Arrays.sort(pl_files, new CompratorByLastModified());

				for (File mFile : pl_files) {
					// 判断获得的内容是文件夹还是文件，如果是文件则列出来.
					if (mFile.isFile()) {
						if (new Modify().isJPEG(mFile.toString())) {

							/*
							 * 利用BitmapFactory.Options.
							 * inSampleSize方法将文件地址直接转码成bitmap. 压缩图片大小,防止bitmap
							 * size exceeds VM budget的发生
							 */
							BitmapFactory.Options opts = new BitmapFactory.Options();
							opts.inJustDecodeBounds = true;
							BitmapFactory.decodeFile(mFile.toString(), opts);

							opts.inSampleSize = new Modify().computeSampleSize(
									opts, -1, 240 * 320);
							opts.inJustDecodeBounds = false;

							try {
								bit = BitmapFactory.decodeFile(
										mFile.toString(), opts);
							} catch (OutOfMemoryError err) {
							}

							// 将图片添加进入listview的item中
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("ItemImage", bit);
							map.put("ItemName", mFile.getName().toString());
							pl_listItem.add(map);
						}
					}
				}
			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						"暂时还没有相片，赶紧拍一张吧！", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}

		// 生成动态数组，加入数据.

		// // 生成适配器的Item和动态数组对应的元素
		// listItemAdapter = new SimpleAdapter(this, pl_listItem,// 数据源
		//
		// R.layout.photo_item,// ListItem的XML实现
		// // 动态数组与ImageItem对应的子项
		// new String[] { "ItemImage" },
		// // ImageItem的XML文件里面的一个ImageView,两个TextView ID
		// new int[] { R.id.pi_img });

		// // 在Listview中实现显示图片.
		// listItemAdapter.setViewBinder(new ViewBinder() {
		//
		// @Override
		// public boolean setViewValue(View view, Object data,
		// String textRepresentation) {
		// // TODO Auto-generated method stub
		// if (view instanceof ImageView && data instanceof Bitmap) {
		// ImageView iv = (ImageView) view;
		// iv.setImageBitmap((Bitmap) data);
		// return true;
		// } else
		// return false;
		// }
		//
		// });

		listItemAdapter = new MyAdapter(this);
		// 添加并且显示
		pl_grid.setAdapter(listItemAdapter);

		// 点击进入照片查看页面
		pl_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				// 获取对应HashMap的数据内容
				final HashMap<String, Object> ma = pl_listItem.get(position);
				// 获取图片对应的名字
				String name = ma.get("ItemName").toString();
				Log.d("Photo_List", name);

				Intent photo_rename = new Intent();
				photo_rename.putExtra("Visit_Name", visit_name);
				photo_rename.putExtra("Photo_Position", position);
				photo_rename.setClass(Photo_List.this, Photo_Show.class);
				startActivity(photo_rename);

			}
		});

		/*
		 * 返回主页面
		 */
		pl_home.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent home = new Intent();
				home.setClass(Photo_List.this, Main.class);
				startActivity(home);
				finish();

			}
		});

		/*
		 * 返回游记陈列页面
		 */
		pl_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent back = new Intent();
				back.setClass(Photo_List.this, Visit_List.class);
				startActivity(back);
				finish();
			}
		});

		/*
		 * 导入更多图片操作
		 */
		pl_add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// 导入图片
				Intent nPhoto = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(nPhoto, 16);

			}
		});

		/*
		 * 进行拍照
		 */
		pl_camera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				String STORE_NAME = "VisitName";
				SharedPreferences set = getSharedPreferences(STORE_NAME,
						MODE_PRIVATE);
				SharedPreferences.Editor editor = set.edit();

				editor.putString("Vcontent", Fn);
				editor.commit();

				StartCamera();

			}
		});

	}

	public class MyAdapter extends BaseAdapter {

		// 定义context
		private Context mContext;

		private MyAdapter(Context c) {
			mContext = c;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pl_listItem.size();
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
			ImageView imageview;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder = null;

			if (convertView == null) {

				holder = new ViewHolder();

				// 根据自定义的Item布局加载布局
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.photo_item, null);

				holder.imageview = (ImageView) convertView
						.findViewById(R.id.pi_img);
				// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.imageview.setImageBitmap((Bitmap) pl_listItem.get(position)
					.get("ItemImage"));

			return convertView;
		}

	}

	// 调用系统相机
	private void StartCamera() {

		Intent ca_intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String SAVE_PATH_IN_SDCARD = "sdcard/YouTu/" + visit_name + "/";

		// 将图片暂时存储在SD上
		ca_intent2.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(SAVE_PATH_IN_SDCARD, "You.JPEG")));

		startActivityForResult(ca_intent2, 10);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 16) {

				Uri uri = data.getData();

				// 获取为游记图片命名所需的计数.
				SharedPreferences pcount = getSharedPreferences("VisitCount", 0);
				int num = pcount.getInt(visit_name, 0);
				num = num + 1;

				// 重新保存计数提交.
				SharedPreferences.Editor pe = pcount.edit();
				pe.putInt(visit_name, num);
				pe.commit();

				String add_name;

				if (0 < num && num < 10) {

					add_name = "00" + String.valueOf(num) + "_IMG";

				} else if (num < 100) {

					add_name = "0" + String.valueOf(num) + "_IMG";

				} else {

					add_name = String.valueOf(num) + "_IMG";

				}

				// 图片新路径
				String newPath = "sdcard/YouTu/" + visit_name + "/" + add_name
						+ ".JPEG".trim();

				File newFile = new File(newPath);
				try {
					FileOutputStream fos = new FileOutputStream(newFile);
					ContentResolver cr = getContentResolver();
					InputStream imgIS = cr.openInputStream(uri);

					byte[] ary = null;
					int byteRead = -1;
					do {
						ary = new byte[1024];
						byteRead = imgIS.read(ary);
						fos.write(ary);
						fos.flush();
					} while (byteRead != -1);
					imgIS.close();
					fos.close();

					/*
					 * 利用BitmapFactory.Options. inSampleSize方法将文件地址直接转码成bitmap.
					 * 压缩图片大小,防止bitmap size exceeds VM budget的发生
					 */
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(newPath, opts);

					opts.inSampleSize = new Modify().computeSampleSize(opts,
							-1, 240 * 320);
					opts.inJustDecodeBounds = false;

					try {
						bt = BitmapFactory.decodeFile(newPath, opts);
					} catch (OutOfMemoryError err) {
					}

					// 将图片添加进入listview的item中
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemImage", bt);
					map.put("ItemName", add_name + ".JPEG");
					pl_listItem.add(map);

					listItemAdapter.notifyDataSetChanged();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			if (requestCode == 10) {

				// 跳转到图片编辑界面.
				Intent pm = new Intent();
				pm.setClass(Photo_List.this, Photo_Edit.class);
				startActivity(pm);
			}
		}
		if (resultCode == RESULT_CANCELED)
			return;

		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 写Back键方法，直接返回Visit_List主页面
	 */
	@Override
	public void onBackPressed() {
		Intent back = new Intent();
		back.setClass(Photo_List.this, Visit_List.class);
		startActivity(back);
	}
}
