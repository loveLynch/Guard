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
			show = "δ����";
		} else {
			show = "������";
		}
		map.put("COLUMN_1", "������������         (" + show + ")");
		map.put("COLUMN_2", "�޸��������������Ľ�������");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("COLUMN_1", "������������           (" + vol + ")");
		map.put("COLUMN_2", "������������������������С");
		list.add(map);

		map = new HashMap<String, Object>();
		if (vib.equals("")) {
			show = "����";
		} else {
			show = "����";
		}
		map.put("COLUMN_1", "����������          (" + show + ")");
		map.put("COLUMN_2", "�Ƿ�����/���÷����������񶯱���");
		list.add(map);

		map = new HashMap<String, Object>();
		if (sms.equals("")) {
			show = "δ����";
		} else {
			show = sms;
		}

		map.put("COLUMN_1", "���ű�������         (" + show + ")");
		map.put("COLUMN_2", "�����Զ����ͱ���֪ͨ���ŵ��ֻ�����");
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
				.setTitle("�����ý�������")
				.setView(view)
				.setPositiveButton("��������",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (pwdTxt.getText().length() != 4) {
									JBSettingActivity.this
											.showDialog("�������벻��4λ,���������á�");
									return;
								}
								write("pwd", pwdTxt.getText().toString());
								SimpleAdapter la = (SimpleAdapter) listView
										.getAdapter();
								Map<String, Object> map = (Map) la.getItem(0);
								map.put("COLUMN_1", "������������      �������ã�");
								map.put("COLUMN_2", "�޸��������������Ľ�������");
								la.notifyDataSetChanged();
							}
						})
				.setNegativeButton("ȡ������",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								write("pwd", "");
								SimpleAdapter la = (SimpleAdapter) listView
										.getAdapter();
								Map<String, Object> map = (Map) la.getItem(0);
								map.put("COLUMN_1", "������������      ��δ���ã�");
								map.put("COLUMN_2", "�޸��������������Ľ�������");
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
		new AlertDialog.Builder(this).setTitle("���ñ�������").setView(view)
				.setPositiveButton("����", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						vol = String.valueOf((seek.getProgress() / 7));
						audioManager.setStreamVolume(3, Integer.parseInt(vol),
								0);
						write("vol", vol);
						SimpleAdapter la = (SimpleAdapter) listView
								.getAdapter();
						Map<String, Object> map = (Map) la.getItem(1);
						map.put("COLUMN_1", "������������           (" + vol + ")");
						map.put("COLUMN_2", "������������������������С");
						la.notifyDataSetChanged();
					}
				}).show();
	}

	private void showVibDialog() {
		new AlertDialog.Builder(this)
				.setTitle("����������")
				.setMessage("�Ƿ�����/���÷����������񶯱���")
				.setPositiveButton("����", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						SimpleAdapter la = (SimpleAdapter) listView
								.getAdapter();
						Map<String, Object> map = (Map) la.getItem(2);
						map.put("COLUMN_1", "����������     ��������");
						map.put("COLUMN_2", "�Ƿ�����/���÷����������񶯱���");
						la.notifyDataSetChanged();
					}
				})
				.setNegativeButton("����", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						SimpleAdapter la = (SimpleAdapter) listView
								.getAdapter();
						Map<String, Object> map = (Map) la.getItem(2);
						map.put("COLUMN_1", "����������      �����ã�");
						map.put("COLUMN_2", "�Ƿ�����/���÷����������񶯱���");
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
				.setTitle("�����ý��ն��ŵ��ֻ�����")
				.setView(view)
				.setPositiveButton("���ö���",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (smsTxt.getText().length() != 11) {
									JBSettingActivity.this
											.showDialog("�ֻ����벻��11λ,���������á�");
									return;
								}
								write("sms", smsTxt.getText().toString());
								SimpleAdapter la = (SimpleAdapter) listView
										.getAdapter();
								Map<String, Object> map = (Map) la.getItem(3);
								map.put("COLUMN_1",
										"���ű�������      ��"
												+ JBSettingActivity.this.smsTxt
														.getText() + "��");
								map.put("COLUMN_2", "�����Զ����ͱ���֪ͨ���ŵ��ֻ�����");
								la.notifyDataSetChanged();
							}
						})
				.setNegativeButton("ȡ������",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								write("sms", "");
								SimpleAdapter la = (SimpleAdapter) listView
										.getAdapter();
								Map<String, Object> map = (Map) la.getItem(3);
								map.put("COLUMN_1", "���ű�������      ��δ���ã�");
								map.put("COLUMN_2", "�����Զ����ͱ���֪ͨ���ŵ��ֻ�����");
								la.notifyDataSetChanged();
							}
						}).show();
	}

	private void showDialog(String paramString) {
		new AlertDialog.Builder(this).setTitle("��ʾ��Ϣ").setMessage(paramString)
				.setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {
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