package com.javacodegeeks.android.lbs;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProxAlertActivity extends Activity {

	private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; 
	private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; 

	private static final long POINT_RADIUS = 1000;  
	private static final long PROX_ALERT_EXPIRATION = -1; 

	private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
	private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";

	private static final String PROX_ALERT_INTENT = "com.javacodegeeks.android.lbs";


	private static final NumberFormat nf = new DecimalFormat("##.########");

	private LocationManager locationManager;
	SQLiteDatabase db;
	String[] adresss;


	private Button findCoordinatesButton;
	private Button savePointButton;
	ListView lst; 
	double lng,lat;

	boolean yey,yey1=false;
	ArrayAdapter<String> adapter;
	AutoCompleteTextView edt;
	String abc;
	String abcd;
	Button bt,bt1,bt2;
	NotificationManager notificationManager;
	Notification notification;
	TextView textt;
	int id=1000;
	TextView txt,txt1,txt2,latitudeEditText,longitudeEditText;
	List<Address> locationlist;

	ArrayList<String> ade=new ArrayList<String>();
	ProximityIntentReceiver pr=new ProximityIntentReceiver();
	MyLocationListener myn=new MyLocationListener();

	@Override
	public void onCreate(Bundle savedInstanceState) 

	{


		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


		latitudeEditText = (TextView) findViewById(R.id.point_latitude);
		longitudeEditText = (TextView) findViewById(R.id.point_longitude);
		findCoordinatesButton = (Button) findViewById(R.id.find_coordinates_button);
		savePointButton = (Button) findViewById(R.id.save_point_button);


		lst=(ListView)findViewById(R.id.lst);
		edt=(AutoCompleteTextView) findViewById(R.id.address);
		bt=(Button)findViewById(R.id.Find);
		bt1=(Button)findViewById(R.id.clear);
		bt2=(Button)findViewById(R.id.clear2);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, ade)
				{
			public View getView(int position, View convertView, ViewGroup parent) {

				View view = super.getView(position, convertView, parent);
				TextView text = (TextView) view.findViewById(android.R.id.text1);


				text.setTextColor(Color.BLACK);


				return view;
			}
				};

				lst.setAdapter(adapter); 


				// Assign adapter to ListView

				notificationManager = 
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

				PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, getIntent(), 0);	
				db=openOrCreateDatabase("Task", Context.MODE_PRIVATE, null);
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


				notification = createNotification();
				notification.setLatestEventInfo(getApplicationContext(), "Reminder", abc, pendingIntent);



				bt.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String abc=edt.getText().toString();
						ade.clear();
						getLatLongFromAddress(abc);
						adapter.notifyDataSetChanged();


						//				int latt=(int)lat;
						//				int lngg=(int)lng;
						//				if(latt==0&&lngg==0)
						//				{
						//					Toast.makeText(getApplicationContext(), "No Location found", Toast.LENGTH_SHORT).show();
						//				}
						//				else
						//				{
						//					latitudeEditText.setText(nf.format(lat));
						//					longitudeEditText.setText(nf.format(lng));
						//				}
					}

				});
				bt1.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(yey1)
						{
							latitudeEditText.setText("");
							longitudeEditText.setText("");
							unregisterReceiver(pr);
							SharedPreferences prefs = getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
							prefs.edit().clear().commit();
							locationManager.removeUpdates(myn);
							edt.setText("");
							ade.clear();
							adapter.notifyDataSetChanged();
							Toast.makeText(ProxAlertActivity.this, 
									"Location Cleared", Toast.LENGTH_LONG).show();
							yey1=false;
						}
						else
						{
							Toast.makeText(ProxAlertActivity.this, 
									"No location is set", Toast.LENGTH_LONG).show();
						}

					}
				});




				findCoordinatesButton.setOnClickListener(new OnClickListener() {			
					@Override
					public void onClick(View v) {
						locationManager.requestLocationUpdates(
								LocationManager.NETWORK_PROVIDER, 
								MINIMUM_TIME_BETWEEN_UPDATE, 
								MINIMUM_DISTANCECHANGE_FOR_UPDATE,
								myn
								);

						populateCoordinatesFromLastKnownLocation();
						ade.clear();
						getLatLongFromAddress2(latitudeEditText.getText().toString(),longitudeEditText.getText().toString());
						adapter.notifyDataSetChanged();




					}
				});

				savePointButton.setOnClickListener(new OnClickListener() {			
					@Override
					public void onClick(View v) {
						locationManager.requestLocationUpdates(
								LocationManager.NETWORK_PROVIDER, 
								MINIMUM_TIME_BETWEEN_UPDATE, 
								MINIMUM_DISTANCECHANGE_FOR_UPDATE,
								myn
								);

						saveProximityAlertPoint();
						yey=true;
						yey1=true;
					}
				});
				lst.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						// TODO Auto-generated method stub
						String abc1=edt.getText().toString();
						getLatLongFromAddress1(abc1,arg2);
						int latt=(int)lat;
						int lngg=(int)lng;
						if(latt==0&&lngg==0)
						{
							Toast.makeText(getApplicationContext(), "No Location found", Toast.LENGTH_SHORT).show();
						}
						else
						{
							latitudeEditText.setText(nf.format(lat));
							longitudeEditText.setText(nf.format(lng));
						}

					}
				});



	}
	protected void onPause() {
		super.onPause();
	//	Toast.makeText(this, "Location is updating", Toast.LENGTH_LONG).show();
	}
	protected void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(myn);
	//	Toast.makeText(this, "Location stopped updating Since App is closed", Toast.LENGTH_LONG).show();
	}
	private void saveProximityAlertPoint() {
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (location==null) {
			Toast.makeText(this, "No last known location. Aborting...", Toast.LENGTH_LONG).show();
			return;
		}
		saveCoordinatesInPreferences(Float.valueOf(latitudeEditText.getText().toString()),Float.valueOf(longitudeEditText.getText().toString()));
		addProximityAlert(Float.valueOf(latitudeEditText.getText().toString()),Float.valueOf(longitudeEditText.getText().toString()));
	}

	private void addProximityAlert(double latitude, double longitude) {

		Intent intent = new Intent(PROX_ALERT_INTENT);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

		locationManager.addProximityAlert(
				latitude, // the latitude of the central point of the alert region
				longitude, // the longitude of the central point of the alert region
				POINT_RADIUS, // the radius of the central point of the alert region, in meters
				PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration 
				proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
				);

		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);  
		registerReceiver(pr, filter);

	}

	private void populateCoordinatesFromLastKnownLocation() {
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (location!=null) {
			latitudeEditText.setText(nf.format(location.getLatitude()));
			longitudeEditText.setText(nf.format(location.getLongitude()));
			locationManager.removeUpdates(myn);

		}
	}

	private void saveCoordinatesInPreferences(float latitude, float longitude) {
		SharedPreferences prefs = this.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putFloat(POINT_LATITUDE_KEY, latitude);
		prefsEditor.putFloat(POINT_LONGITUDE_KEY, longitude);
		prefsEditor.commit();
	}

	private Location retrievelocationFromPreferences() {
		SharedPreferences prefs = this.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
		Location location = new Location("POINT_LOCATION");
		location.setLatitude(prefs.getFloat(POINT_LATITUDE_KEY, 0));
		location.setLongitude(prefs.getFloat(POINT_LONGITUDE_KEY, 0));
		return location;
	}

	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			Location pointLocation = retrievelocationFromPreferences();
			float distance = location.distanceTo(pointLocation);
			Toast.makeText(ProxAlertActivity.this, 
					"Distance from Location:"+distance+"meters", Toast.LENGTH_LONG).show();
			int distancee=(int)distance;
			if(distancee<POINT_RADIUS&&yey)
			{
				notificationManager.notify(id, notification);
				yey=false;
			}
			else

			{

			}
		}
		public void onStatusChanged(String s, int i, Bundle b) {			
		}
		public void onProviderDisabled(String s) {
		}
		public void onProviderEnabled(String s) {			
		}
	}
	public void getLatLongFromAddress(String youraddress) {
		String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
				youraddress + "&sensor=false";
		HttpGet httpGet = new HttpGet(uri);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
			JSONArray adress=jsonObject.getJSONArray("results");
			for(int i=0;i<adress.length();i++)
			{

				String address=adress.getJSONObject(i).getString("formatted_address");
				ade.add(address);
				Log.d("latitude", "" + ade.get(i).toString());

			}


			Log.d("latitude", "" + lat);
			Log.d("longitude", "" + lng);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	public void getLatLongFromAddress1(String youraddress,int a) {
		String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
				youraddress + "&sensor=false";
		HttpGet httpGet = new HttpGet(uri);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());


			lng = ((JSONArray)jsonObject.get("results")).getJSONObject(a)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			lat = ((JSONArray)jsonObject.get("results")).getJSONObject(a)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

			Log.d("latitude", "" + lat);
			Log.d("longitude", "" + lng);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	public void getLatLongFromAddress2(String string1, String string2) {
		String uri = "http://maps.google.com/maps/api/geocode/json?latlng="
				+ string1+","+string2+"&sensor=false";
		Log.d("ASDHASKDH", uri);
		HttpGet httpGet = new HttpGet(uri);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
			JSONArray adress=jsonObject.getJSONArray("results");
			String address2=adress.getJSONObject(0).getString("formatted_address");
			ade.add(address2);

		} catch (JSONException e) {
			e.printStackTrace();
		}

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