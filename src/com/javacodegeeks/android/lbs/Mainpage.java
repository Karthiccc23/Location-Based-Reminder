
package com.javacodegeeks.android.lbs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Mainpage extends Activity {
	EditText etask;
	TextView view;
	ImageView imag;
	SQLiteDatabase db;
	Button add,remove,set;
	String abc="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainpage);

		etask=(EditText)findViewById(R.id.task);
		add=(Button)findViewById(R.id.bt);
		imag=(ImageView)findViewById(R.id.imgg);
		remove=(Button)findViewById(R.id.bt2);
		set=(Button)findViewById(R.id.bt3);
		view=(TextView)findViewById(R.id.view);
		view.setText("");
		db=openOrCreateDatabase("Task", Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS taskss(task VARCHAR);");
		Cursor c=db.rawQuery("SELECT task FROM taskss",null);
		if(c!=null)
		{
			if  (c.moveToFirst()) 
			{
				do {
					String abc = c.getString(c.getColumnIndex("task"));
					view.setText(abc);

				}while (c.moveToNext());
			}

		}
		else
		{
			abc="";
		}

		add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String gettask=etask.getText().toString();
				view.setText(gettask);
				db.execSQL("INSERT INTO taskss VALUES('"+gettask+"');");
				Toast.makeText(getApplicationContext(), "Task Added Successfully", Toast.LENGTH_SHORT).show();

			}
		});
		remove.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				db.execSQL("DELETE FROM taskss");
				view.setText("");
				Toast.makeText(getApplicationContext(), "Task Removed Successfully", Toast.LENGTH_SHORT).show();

			}
		});
		set.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(view.getText().equals(""))
				{
					Toast.makeText(getApplicationContext(), "Please Add Reminder to set location",Toast.LENGTH_SHORT).show();
				
				}
				else
				{
					Intent inten=new Intent(Mainpage.this,ProxAlertActivity.class);
					startActivity(inten);
				}
			}
		});
	}
}

