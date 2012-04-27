package com.youtu;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Simple_liu
 * 
 *         创建新的游记
 * 
 */
public class New_Visit extends Activity {

	private Button nv_home, nv_new, nv_cencel;
	private TextView nv_time;
	private EditText nv_edit;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_visit);
		MyApplication.getInstance().addActivity(this);

		nv_home = (Button) findViewById(R.id.nv_home);
		nv_new = (Button) findViewById(R.id.nv_new);
		nv_cencel = (Button) findViewById(R.id.nv_cencle);
		nv_time = (TextView) findViewById(R.id.nv_time);
		nv_edit = (EditText) findViewById(R.id.nv_edit);

		// 获取当前时间
		Calendar ca = Calendar.getInstance();
		Date now = ca.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd  HH:mm");
		String now_time = df.format(now);
		nv_time.setText(now_time);

		nv_home.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent inhome = new Intent();
				inhome.setClass(New_Visit.this, Main.class);
				startActivity(inhome);

			}
		});

		nv_new.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String name = nv_edit.getText().toString().trim();

				// 游记的名字不能为空
				if ("".equals(name)) {
					Toast.makeText(getApplicationContext(),
							"游记还没有名字，赶快给它起一个吧！", Toast.LENGTH_SHORT).show();
				} else {

					// 保存新建游记的名字到SharedPreferences--"Visit_Name"中

					SharedPreferences set = getSharedPreferences("VisitName",
							MODE_PRIVATE);
					SharedPreferences.Editor editor = set.edit();
					editor.putString("Vcontent", name);
					editor.commit();

					// 创建游记文件夹
					new Modify().MakeDirs("YouTu/" + name);

					// 获取游记名字，为保存图片命名用于计数.
					int num = 0;
					SharedPreferences pcount = getSharedPreferences(
							"VisitCount", 0);
					SharedPreferences.Editor pe = pcount.edit();
					pe.putInt(name, num);
					pe.commit();

					// 跳转页面
					Intent nv_intent1 = new Intent();
					nv_intent1.setClass(New_Visit.this, Visit_List.class);
					startActivity(nv_intent1);

					Toast.makeText(New_Visit.this, "创建成功", Toast.LENGTH_SHORT)
							.show();

				}

			}
		});

		nv_cencel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent back = new Intent();
				back.setClass(New_Visit.this, Main.class);
				startActivity(back);
				finish();

			}
		});

	}

}
