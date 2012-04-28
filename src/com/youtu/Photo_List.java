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
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.youtu.AsyncImageLoader.ImageCallback;

public class Photo_List extends Activity {

	private Button pl_home, pl_back, pl_add, pl_camera;
	private TextView pl_name, pl_attribute;
	private GridView pl_grid;
	private String visit_name;

	private List<ImageAndText> list = new ArrayList<ImageAndText>();
	GridAdapter listItemAdapter;

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

							// 将图片的地址和图片名字存入list中
							list.add(new ImageAndText(mFile.toString(), mFile
									.getName().toString()));

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

		listItemAdapter = new GridAdapter(this, pl_grid);
		pl_grid.setAdapter(listItemAdapter);

		// 点击进入照片查看页面
		pl_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				// 获取图片对应的名字
				ImageAndText text = list.get(position);
				String name = text.getText();

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

			ImageAndText imageAndText = list.get(position);
			String imageUrl = imageAndText.getImgUrl();
			holder.imageview.setTag(imageUrl);

			Drawable cacheImage = asyncImageLoader.loadDrawable(imageUrl,
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

			return convertView;
		}

		public class ViewHolder {
			ImageView imageview;
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

					list.add(new ImageAndText(newPath, add_name + ".JPEG"));

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
