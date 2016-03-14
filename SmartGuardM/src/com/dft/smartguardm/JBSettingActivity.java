package com.dft.smartguardm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;

public class JBSettingActivity extends Activity {
	private AudioManager audioManager;
	@SuppressWarnings("rawtypes")
	private List list;
	private ListView listView;
	private MediaPlayer mediaPlayer;
	private String pwd;
	private EditText pwdTxt;
	private SeekBar seek;
	private String sms;
	private EditText smsTxt;
	private String vib;
	private String vol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jbsetting);
		listView = ((ListView) findViewById(R.id.setting_list));

		mediaPlayer = new MediaPlayer();
		audioManager = ((AudioManager) getSystemService("audio"));

		pwd = read("pwd");
		vol = read("vol");
		{

			if (vol.equals("")) {
				vol = String.valueOf(audioManager.getStreamVolume(3));
			}
		}
		vib = read("vib");
		sms = read("sms");

		SimpleAdapter adapter = new SimpleAdapter(this, getData(), 17367044,
				new String[] { "COLUMN_1", "COLUMN_2" }, new int[] { 16908308,
						16908309 });
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(onItemClick);
		audioManager.setStreamVolume(3, Integer.parseInt(vol), 0);
		return;

	}

	private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
				View paramAnonymousView, int item, long paramAnonymousLong) {
			switch (item) {

			case 0:
				JBSettingActivity.this.showPwdDialog();
				break;
			case 1:
				JBSettingActivity.this.showVolDailog();
				break;
			case 2:
				JBSettingActivity.this.showVibDialog();
				break;
			case 3:
				JBSettingActivity.this.showSmsDialog();
				break;

			}
		}
	};

	private List<Map<String, Object>> getData() {
		String show = "";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map = new HashMap<String, Object>();
		if (pwd.equals("")) {
			show = "未设置";
		} else {
			show = "已设置";
		}
		map.put("COLUMN_1", "解锁密码设置         (" + show + ")");
		map.put("COLUMN_2", "修改您防盗报警器的解锁密码");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("COLUMN_1", "报警音量设置           (" + vol + ")");
		map.put("COLUMN_2", "调整您防盗报警器的音量大小");
		list.add(map);

		map = new HashMap<String, Object>();
		if (vib.equals("")) {
			show = "禁用";
		} else {
			show = "启用";
		}
		map.put("COLUMN_1", "报警振动设置          (" + show + ")");
		map.put("COLUMN_2", "是否启用/禁用防盗报警器振动报警");
		list.add(map);

		map = new HashMap<String, Object>();
		if (sms.equals("")) {
			show = "未设置";
		} else {
			show = sms;
		}

		map.put("COLUMN_1", "短信报警设置         (" + show + ")");
		map.put("COLUMN_2", "设置自动发送报警通知短信的手机号码");
		list.add(map);

		return list;

	}

	private void showPwdDialog() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.alert_dialog_pwd, null);
		pwdTxt = (EditText) view.findViewById(R.id.pwd_edit);
		pwd = read("pwd");
		if (!pwd.equals("")) {
			pwdTxt.setText(pwd);
		}
		new AlertDialog.Builder(this)
				.setTitle("请设置解锁密码")
				.setView(view)
				.setPositiveButton("设置密码",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (pwdTxt.getText().length() != 4) {
									JBSettingActivity.this
											.showDialog("解锁密码不是4位,请重新设置。");
									return;
								}
								write("pwd", pwdTxt.getText().toString());
								SimpleAdapter la = (SimpleAdapter) listView
										.getAdapter();
								Map<String, Object> map = (Map) la.getItem(0);
								map.put("COLUMN_1", "解锁密码设置      （已设置）");
								map.put("COLUMN_2", "修改您防盗报警器的解锁密码");
								la.notifyDataSetChanged();
							}
						})
				.setNegativeButton("取消设置",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								write("pwd", "");
								SimpleAdapter la = (SimpleAdapter) listView
										.getAdapter();
								Map<String, Object> map = (Map) la.getItem(0);
								map.put("COLUMN_1", "解锁密码设置      （未设置）");
								map.put("COLUMN_2", "修改您防盗报警器的解锁密码");
								la.notifyDataSetChanged();
							}
						}).show();
	}

	private void showVolDailog() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.alert_dialog_vol, null);
		int maxVol = audioManager.getStreamMaxVolume(3);
		seek = (SeekBar) view.findViewById(R.id.seek);
		seek.setProgress((Integer.parseInt(vol) * 7));
		new AlertDialog.Builder(this).setTitle("设置报警音量").setView(view)
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						vol = String.valueOf((seek.getProgress() / 7));
						audioManager.setStreamVolume(3, Integer.parseInt(vol),
								0);
						write("vol", vol);
						SimpleAdapter la = (SimpleAdapter) listView
								.getAdapter();
						Map<String, Object> map = (Map) la.getItem(1);
						map.put("COLUMN_1", "报警音量设置           (" + vol + ")");
						map.put("COLUMN_2", "调整您防盗报警器的音量大小");
						la.notifyDataSetChanged();
					}
				}).show();
	}

	private void showVibDialog() {
		new AlertDialog.Builder(this)
				.setTitle("报警振动设置")
				.setMessage("是否启用/禁用防盗报警器振动报警")
				.setPositiveButton("启动", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						SimpleAdapter la = (SimpleAdapter) listView
								.getAdapter();
						Map<String, Object> map = (Map) la.getItem(2);
						map.put("COLUMN_1", "报警振动设置     （启动）");
						map.put("COLUMN_2", "是否启用/禁用防盗报警器振动报警");
						la.notifyDataSetChanged();
					}
				})
				.setNegativeButton("禁用", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						SimpleAdapter la = (SimpleAdapter) listView
								.getAdapter();
						Map<String, Object> map = (Map) la.getItem(2);
						map.put("COLUMN_1", "报警振动设置      （禁用）");
						map.put("COLUMN_2", "是否启用/禁用防盗报警器振动报警");
						la.notifyDataSetChanged();
					}
				}).show();
	}

	private void showSmsDialog() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.alert_dialog_sms, null);
		smsTxt = (EditText) view.findViewById(R.id.sms_edit);
		sms = read("sms");
		if (!sms.equals("")) {
			smsTxt.setText(sms);
		}
		new AlertDialog.Builder(this)
				.setTitle("请设置接收短信的手机号码")
				.setView(view)
				.setPositiveButton("设置短信",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (smsTxt.getText().length() != 11) {
									JBSettingActivity.this
											.showDialog("手机号码不是11位,请重新设置。");
									return;
								}
								write("sms", smsTxt.getText().toString());
								SimpleAdapter la = (SimpleAdapter) listView
										.getAdapter();
								Map<String, Object> map = (Map) la.getItem(3);
								map.put("COLUMN_1",
										"短信报警设置      （"
												+ JBSettingActivity.this.smsTxt
														.getText() + "）");
								map.put("COLUMN_2", "设置自动发送报警通知短信的手机号码");
								la.notifyDataSetChanged();
							}
						})
				.setNegativeButton("取消设置",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								write("sms", "");
								SimpleAdapter la = (SimpleAdapter) listView
										.getAdapter();
								Map<String, Object> map = (Map) la.getItem(3);
								map.put("COLUMN_1", "短信报警设置      （未设置）");
								map.put("COLUMN_2", "设置自动发送报警通知短信的手机号码");
								la.notifyDataSetChanged();
							}
						}).show();
	}

	private void showDialog(String paramString) {
		new AlertDialog.Builder(this).setTitle("提示信息").setMessage(paramString)
				.setNegativeButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface paramAnonymousDialogInterface,
							int paramAnonymousInt) {
					}
				}).show();
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

}