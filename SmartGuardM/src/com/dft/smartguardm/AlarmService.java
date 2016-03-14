package com.dft.smartguardm;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AlarmService extends Service {
	private MediaPlayer Player;
	private float X = 0.0F;
	private float Y = 0.0F;
	private float Z = 0.0F;
	private RelativeLayout bgLayout;
	private boolean flag = true;
	private ImageView img;
	private boolean isAlarm = false;
	private boolean locking = false;
	private MediaPlayer mediaPlayer;
	private int miao = 0;
	private String msg = "y";
	private String pwd = "";
	private boolean runing = false;
	private String sens = "";
	private Sensor sensor = null;
	private SensorManager sensorMgr;
	private TextView show;
	private String showpwd = "";
	private boolean smsFlag = false;
	private String smsTel = "";
	private int smstime = 20;
	private boolean soundFlag = true;
	private int teltime = 40;
	private boolean vibFlag = true;
	private Vibrator vibrator = null;
	private float x;
	private String xyz = "";
	private float y;
	private float z;
	SensorEventListener listener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor paramAnonymousSensor,
				int paramAnonymousInt) {
		}

		public void onSensorChanged(SensorEvent paramAnonymousSensorEvent) {
			AlarmService.this.x = paramAnonymousSensorEvent.values[0];
			AlarmService.this.y = paramAnonymousSensorEvent.values[1];
			AlarmService.this.z = paramAnonymousSensorEvent.values[2];
			AlarmService.this.xyz = ("x=" + AlarmService.this.x + "    y="
					+ AlarmService.this.y + "    z=" + AlarmService.this.z);
			if (AlarmService.this.X == 0.0F) {
				AlarmService.this.X = AlarmService.this.x;
				AlarmService.this.Y = AlarmService.this.y;
				AlarmService.this.Z = AlarmService.this.z;
				Log.i("yb", "************ x=" + AlarmService.this.x + "    y="
						+ AlarmService.this.y + "    z=" + AlarmService.this.z);
			}
			Log.i("yb", AlarmService.this.xyz);
			if (AlarmService.this.flag) {
				/*
				 * if ((!AlarmService.this.sens.equals("0")) &&
				 * (!AlarmService.this.sens.equals(""))) { if
				 * ((!AlarmService.this.sens.equals("2")) ||
				 * (AlarmService.this.x == 0.0F)) { AlarmService.this.isAlarm =
				 * true; AlarmService.this.flag = false; } }
				 */
				if ((AlarmService.this.X + 3.0F < AlarmService.this.x)
						|| (AlarmService.this.X - 3.0F > AlarmService.this.x)
						|| (AlarmService.this.Y + 3.0F < AlarmService.this.y)
						|| (AlarmService.this.Y - 3.0F > AlarmService.this.y)) {
					AlarmService.this.isAlarm = true;
					AlarmService.this.flag = false;
				}
			}
			// else {
			AlarmService.this.isAlarm = false;
			if (AlarmService.this.isAlarm) {
				if (((AlarmService.this.smsFlag))
						|| (AlarmService.this.smsFlag)
						|| (!AlarmService.this.runing)) {
					AlarmService.this.runing = true;
					new Thread() {
						public void run() {
							{
								if (!AlarmService.this.locking) {
									return;
								} else {
									AlarmService localAlarmService1;
									try {
										sleep(300L);
										if (AlarmService.this.smstime <= 0) {
											localAlarmService1 = AlarmService.this;
											localAlarmService1.smstime -= 1;
										}

									} catch (InterruptedException localInterruptedException) {
										AlarmService localAlarmService11;
										localInterruptedException
												.printStackTrace();

									}
									if (!AlarmService.this.msg.equals("y")) {
										AlarmService.this.msg = "n";
									}

									if ((AlarmService.this.smstime == 0)
											&& (AlarmService.this.smsFlag)) {
										SMSHelp.sendSMS(
												AlarmService.this.smsTel,
												"您家可能有异常情况，请打开监控查看!",
												AlarmService.this);
										Log.i("yb", "----------SMS Send");
										localAlarmService1 = AlarmService.this;
										localAlarmService1.smstime -= 1;
									}
								}
								AlarmService.this.msg = "n";
							}
						}
					}.start();
				}
				if (!AlarmService.this.soundFlag) {
				}
			}
			try {
				if ((AlarmService.this.Player != null)
						&& (!AlarmService.this.Player.isPlaying())) {
					AlarmService.this.Player.stop();
					AlarmService.this.Player.prepare();
					AlarmService.this.Player.setLooping(true);
					AlarmService.this.Player.start();
				}
				if (AlarmService.this.vibFlag) {
					AlarmService.this.vibrator.vibrate(new long[] { 100L, 100L,
							100L, 1000L }, 0);
					Log.i("yb", "----------vibrator ok");
				}
				AlarmService.this.bgLayout.setBackgroundResource(R.drawable.bg);
				AlarmService.this.isAlarm = false;
				return;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// }

	};

	public IBinder onBind(Intent paramIntent) {
		Log.i("yb", "--------service onBind----------");
		return null;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate();
		Log.i("yb", "--------service onCreate----------");
		AlarmService.ServiceReceiver sr = new AlarmService.ServiceReceiver();
		IntentFilter filter1 = new IntentFilter();
		filter1.addAction("service");
		registerReceiver(sr, filter1);
		vibrator = (Vibrator) getApplication().getSystemService("vibrator");
		sensorMgr = (SensorManager) getSystemService("sensor");
		if (soundFlag) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer = MediaPlayer.create(this, R.raw.alarmsound);
			Player = new MediaPlayer();
			Player = MediaPlayer.create(this, R.raw.blipblip);
		}
		String p = read("pwd");
		if (p != null) {
			pwd = p;
		}
		super.onCreate();
	}

	public void onDestroy() {
		Log.i("yb", "--------service onDestroy----------");
		unregister();
		super.onDestroy();
	}

	public void onStart(Intent paramIntent, int paramInt) {
		Log.i("yb", "--------service onStart----------");
		register();
		super.onStart(paramIntent, paramInt);
	}

	boolean register() {
		Log.i("yb", "--------register--------");
		sens = read("sensor");
		if ((sens.equals("0")) || (sens.equals(""))) {
			sensor = sensorMgr.getDefaultSensor(1);
		} else if (sens.equals("2")) {
			sensor = sensorMgr.getDefaultSensor(5);
		}
		return sensorMgr.registerListener(listener, sensor, 1);
	}

	void unregister() {
		Log.i("yb", "--------unregister--------");
		this.sensorMgr.unregisterListener(this.listener);
		this.X = 0.0F;
		this.Y = 0.0F;
		this.Z = 0.0F;
		this.isAlarm = false;
		this.flag = true;
		this.smstime = 20;
		this.miao = 0;
	}

	public void write(String name, String data) {
		try {
			SharedPreferences preferences = getSharedPreferences("code", 0);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(name, data);
			editor.commit();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String read(String name) {
		String data = "";
		try {
			@SuppressWarnings("unused")
			SharedPreferences preferences = getSharedPreferences("code", 0);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public class ServiceReceiver extends BroadcastReceiver {
		public ServiceReceiver() {
		}

		public void onReceive(Context paramContext, Intent paramIntent) {
			String str1 = paramIntent.getStringExtra("cmd");
			String str2 = "--------ServiceReceiver onReceive----------/" + str1;
			int i = Log.i("yb", str2);
		}
	}

}
