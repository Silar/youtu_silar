package com.youtu;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Simple_liu
 * 
 *         修改游记
 */
public class Visit_Rename extends Activity {

	private Button vr_home, vr_rename;
	private TextView time;
	private EditText edit;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visit_rename);
		MyApplication.getInstance().addActivity(this);

		vr_home = (Button) findViewById(R.id.nv_home);
		vr_rename = (Button) findViewById(R.id.vr_rename);
		time = (TextView) findViewById(R.id.nv_time);
		edit = (EditText) findViewById(R.id.vr_edit);

		Intent intent = getIntent();
		final String visitName = intent.getStringExtra("Visit_Name");

		edit.setHint(visitName);

		/*
		 * 返回主页面
		 */
		vr_home.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generatced method stub

				Intent home = new Intent();
				home.setClass(Visit_Rename.this, Main.class);
				startActivity(home);

			}
		});

		/*
		 * 确认修改游记的名字,并且跳转回到游记陈列页面
		 */
		vr_rename.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// 修改游记的名字
				String newName = edit.getText().toString().trim();
				String newFileUri = "sdcard/YouTu/" + newName;

				File oldFile = new File("sdcard/YouTu/" + visitName);
				oldFile.renameTo(new File(newFileUri));

				// 跳转到游记陈列页面
				Intent visit = new Intent();
				visit.setClass(Visit_Rename.this, Visit_List.class);
				startActivity(visit);

				Toast.makeText(Visit_Rename.this, "修改成功!", Toast.LENGTH_SHORT)
						.show();

			}
		});

		// 获取当前时间
		Calendar ca = Calendar.getInstance();
		Date now = ca.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd  HH:mm");
		String now_time = df.format(now);
		time.setText(now_time);
	}

	/**
	 * 写Back键方法，直接返回Visit_List主页面
	 */
	@Override
	public void onBackPressed() {
		Intent back = new Intent();
		back.setClass(Visit_Rename.this, Visit_List.class);
		startActivity(back);
	}
}
