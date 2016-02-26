package com.javacodegeeks.android.lbs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.LocationManager;
import android.util.Log;

public class ProximityIntentReceiver extends BroadcastReceiver {
	
	private static final int NOTIFICATION_ID = 1000;
	SQLiteDatabase db;
	String abc;
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String key = LocationManager.KEY_PROXIMITY_ENTERING;

		Boolean entering = intent.getBooleanExtra(key, false);
		
		if (entering) {
			Log.d(getClass().getSimpleName(), "entering");
		}
		else {
			Log.d(getClass().getSimpleName(), "exiting");
		}
		db=context.openOrCreateDatabase("Task", Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS taskss(task VARCHAR);");
		Cursor c=db.rawQuery("SELECT task FROM taskss",null);
		if(c!=null)
		{
			if  (c.moveToFirst()) 
			{
				do {
					 abc = c.getString(c.getColumnIndex("task"));
					

				}while (c.moveToNext());
			}

		}
		else
		{
			abc="ENTERED";
		}
		NotificationManager notificationManager = 
			(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);		
		
		Notification notification = createNotification();
		notification.setLatestEventInfo(context, "Entered", abc, pendingIntent);
		
		notificationManager.notify(NOTIFICATION_ID, notification);
		
	}
	
	private Notification createNotification() {
		Notification notification = new Notification();
		
		notification.icon = R.drawable.ic_menu_notifications;
		notification.when = System.currentTimeMillis();
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		
		notification.ledARGB = Color.WHITE;
		notification.ledOnMS = 1500;
		notification.ledOffMS = 1500;
		
		return notification;
	}
	
}
