package com.dft.smartguardm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SocketCameraActivity extends Activity implements
		SensorEventListener, SurfaceHolder.Callback, Camera.PreviewCallback {
	private SurfaceView mSurfaceview = null; // SurfaceView����(��ͼ���)��Ƶ��ʾ
	private SurfaceHolder mSurfaceHolder = null; // SurfaceHolder����(����ӿ�)SurfaceView֧����
	private Camera mCamera = null; // Camera�������Ԥ��

	/** ��������ַ */
	private String pUsername = null;
	/** ��������ַ */
	private String serverUrl = null;
	/** �������˿� */
	private int serverPort = 6666;
	/** ��Ƶˢ�¼�� */
	private int VideoPreRate = 1;
	/** ��ǰ��Ƶ��� */
	private int tempPreRate = 0;
	/** ��Ƶ���� */
	private int VideoQuality = 85;

	/** ������Ƶ��ȱ��� */
	private float VideoWidthRatio = 1;
	/** ������Ƶ�߶ȱ��� */
	private float VideoHeightRatio = 1;

	/** ������Ƶ��� */
	private int VideoWidth = 320;
	/** ������Ƶ�߶� */
	private int VideoHeight = 240;
	/** ��Ƶ��ʽ���� */
	private int VideoFormatIndex = 0;
	/** �Ƿ�����Ƶ */
	private boolean startSendVideo = false;
	/** �Ƿ��������� */
	private boolean connectedServer = false;
	/** �Ƿ������� */
	// private boolean openalarmsignal = false;
	private boolean switchON = false;
	private Button btn1, btn2, btn3;
	private Button menu_btn1, menu_btn2, menu_btn3;
	private SensorManager mSensorManager;
	private Canvas canvas;
	private Paint paint;
	private SurfaceHolder surfaceHolder;
	private final float FILTERING_VALAUE = 0.1f;
	private float lowX, lowY, lowZ, highX, highY, highZ, sumX, sumY, sumZ;
	double CurrTime, LastTime;
	float lastVirb = 0;
	float AccThres = 3; // ���ٶ���ֵ
	float AccTime = 10; // ʱ������ֵ����λ����
	private int n = 0;
	private MediaPlayer mPlayer = null;

	private TextView show;
	private String showpwd = "";
	private boolean smsFlag = false;
	private String smsTel = "";
	private int smstime = 20;
	private String pwd = "";
	private boolean vibFlag = true;
	private boolean locking = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawleft);

		// ��ֹ��Ļ����
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mSurfaceview = (SurfaceView) findViewById(R.id.camera_preview);
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		btn3 = (Button) findViewById(R.id.button3);
		menu_btn1 = (Button) findViewById(R.id.menu_btn1);
		menu_btn2 = (Button) findViewById(R.id.menu_btn2);
		menu_btn3 = (Button) findViewById(R.id.menu_btn3);
		show = (TextView) findViewById(R.id.show_tv);

		// ��ʼ����������ť
		btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Common.SetGPSConnected(LoginActivity.this, false);
				if (connectedServer) {// ֹͣ����������ͬʱ�Ͽ�����
					startSendVideo = false;
					connectedServer = false;
					// openalarmsignal = false;
					btn2.setEnabled(false);
					// btn3.setEnabled(false);
					btn1.setText("��ʼ����");
					btn2.setText("��ʼ����");
					// btn3.setText("��������");
					// �Ͽ�����
					Thread th = new MySendCommondThread("PHONEDISCONNECT|"
							+ pUsername + "|");
					th.start();
				} else// ��������
				{
					// �����̷߳�������PHONECONNECT
					Thread th = new MySendCommondThread("PHONECONNECT|"
							+ pUsername + "|");
					th.start();
					connectedServer = true;
					btn2.setEnabled(true);
					// btn3.setEnabled(true);
					btn1.setText("ֹͣ����");
				}
			}
		});

		btn2.setEnabled(false);
		btn2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (startSendVideo)// ֹͣ������Ƶ
				{
					startSendVideo = false;
					btn2.setText("��ʼ����");

				} else { // ��ʼ������Ƶ
					startSendVideo = true;
					btn2.setText("ֹͣ����");
				}
			}
		});

		// final TextView text = (TextView)findViewById(R.id.textView1);
		// text.setText(String.valueOf(count));

		// final Button btn3 = (Button) findViewById(R.id.button3);
		// final Button btn2=(Button)findViewById(R.id.button2);
		// show.setText("����δ����������");
		// btn3.setText("��������");
		// btn2.setText("����");

		LastTime = System.currentTimeMillis();

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		btn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (switchON) {
					switchON = false;
					btn3.setText("��������");
					show.setText("����δ����������");

					// btn2.setEnabled(false);
				} else {
					switchON = true;
					btn3.setText("�������");
					show.setText("�����ѿ���������");
					// btn2.setEnabled(true);

				}

			}

		});

		menu_btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) // �������
			{
				Intent intent = new Intent();
				intent.setClass(SocketCameraActivity.this,
						JKSettingActivity.class);

				// ������ͼ.����ͼ���͸�androidϵͳ��ϵͳ������ͼ���������
				startActivity(intent);
			}
		});
		menu_btn2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) // �������
			{
				Intent intent = new Intent();
				intent.setClass(SocketCameraActivity.this,
						JBSettingActivity.class);

				// ������ͼ.����ͼ���͸�androidϵͳ��ϵͳ������ͼ���������
				startActivity(intent);
			}
		});

		menu_btn3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) // �������
			{
				Intent intent = new Intent();
				intent.setClass(SocketCameraActivity.this,
						SoftDescription.class);

				// ������ͼ.����ͼ���͸�androidϵͳ��ϵͳ������ͼ���������
				startActivity(intent);
			}
		});

	}

	@Override
	public void onStart()// ����������ʱ��
	{
		mSurfaceHolder = mSurfaceview.getHolder(); // ��SurfaceView��ȡ��SurfaceHolder����
		mSurfaceHolder.addCallback(this); // SurfaceHolder����ص��ӿ�
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// ������ʾ�����ͣ�setType��������
		// ��ȡ�����ļ�
		SharedPreferences preParas = PreferenceManager
				.getDefaultSharedPreferences(SocketCameraActivity.this);
		pUsername = preParas.getString("Username", "DFT");
		serverUrl = preParas.getString("ServerUrl", "0.0.0.0");
		String tempStr = preParas.getString("ServerPort", "6666");
		serverPort = Integer.parseInt(tempStr);
		tempStr = preParas.getString("VideoPreRate", "1");
		VideoPreRate = Integer.parseInt(tempStr);
		tempStr = preParas.getString("VideoQuality", "85");
		VideoQuality = Integer.parseInt(tempStr);
		tempStr = preParas.getString("VideoWidthRatio", "100");
		VideoWidthRatio = Integer.parseInt(tempStr);
		tempStr = preParas.getString("VideoHeightRatio", "100");
		VideoHeightRatio = Integer.parseInt(tempStr);
		VideoWidthRatio = VideoWidthRatio / 100f;
		VideoHeightRatio = VideoHeightRatio / 100f;

		super.onStart();
	}

	/** ��ʼ������ͷ */
	private void InitCamera() {
		try {
			mCamera = Camera.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			if (mCamera != null) {
				mCamera.setPreviewCallback(null); // �������������ǰ����Ȼ�˳�����
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if (mCamera == null) {
			return;
		}
		mCamera.stopPreview();
		mCamera.setPreviewCallback(this);
		mCamera.setDisplayOrientation(90); // ���ú���¼��
		// ��ȡ����ͷ����
		Camera.Parameters parameters = mCamera.getParameters();
		Size size = parameters.getPreviewSize();
		VideoWidth = size.width;
		VideoHeight = size.height;
		VideoFormatIndex = parameters.getPreviewFormat();

		mCamera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(mSurfaceHolder);
				mCamera.startPreview();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (null != mCamera) {
			mCamera.setPreviewCallback(null); // �������������ǰ����Ȼ�˳�����
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		// ���û��ָ�����Ƶ�����Ȳ���
		if (!startSendVideo)
			return;
		if (tempPreRate < VideoPreRate) {
			tempPreRate++;
			return;
		}
		tempPreRate = 0;
		try {
			if (data != null) {
				YuvImage image = new YuvImage(data, VideoFormatIndex,
						VideoWidth, VideoHeight, null);
				if (image != null) {
					ByteArrayOutputStream outstream = new ByteArrayOutputStream();
					// �ڴ�����ͼƬ�ĳߴ������
					image.compressToJpeg(new Rect(0, 0,
							(int) (VideoWidthRatio * VideoWidth),
							(int) (VideoHeightRatio * VideoHeight)),
							VideoQuality, outstream);
					outstream.flush();
					// �����߳̽�ͼ�����ݷ��ͳ�ȥ
					Thread th = new MySendFileThread(outstream, pUsername,
							serverUrl, serverPort);
					th.start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** �����˵� */

	/**
	 * ���������߳�
	 */
	class MySendCommondThread extends Thread {
		private String commond;

		public MySendCommondThread(String commond) {
			this.commond = commond;
		}

		public void run() {
			// ʵ����Socket
			try {
				Socket socket = new Socket(serverUrl, serverPort);
				PrintWriter out = new PrintWriter(socket.getOutputStream());
				out.println(commond);
				out.flush();
				socket.close();
			} catch (UnknownHostException e) {
			} catch (IOException e) {
			}

		}

	}

	/** �����ļ��߳� */
	class MySendFileThread extends Thread {
		private String username;
		private String ipname;
		private int port;
		private byte byteBuffer[] = new byte[1024];
		private OutputStream outsocket;
		private ByteArrayOutputStream myoutputstream;

		public MySendFileThread(ByteArrayOutputStream myoutputstream,
				String username, String ipname, int port) {
			this.myoutputstream = myoutputstream;
			this.username = username;
			this.ipname = ipname;
			this.port = port;
			try {
				myoutputstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				// ��ͼ������ͨ��Socket���ͳ�ȥ
				Socket tempSocket = new Socket(ipname, port);
				outsocket = tempSocket.getOutputStream();
				// д��ͷ��������Ϣ
				String msg = java.net.URLEncoder.encode("PHONEVIDEO|"
						+ username + "|", "utf-8");
				byte[] buffer = msg.getBytes();
				outsocket.write(buffer);

				ByteArrayInputStream inputstream = new ByteArrayInputStream(
						myoutputstream.toByteArray());
				int amount;
				while ((amount = inputstream.read(byteBuffer)) != -1) {
					outsocket.write(byteBuffer, 0, amount);
				}
				myoutputstream.flush();
				myoutputstream.close();
				tempSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void onResume() {
		super.onResume();
		InitCamera();

		List<Sensor> sensors = mSensorManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);

		// sensor1
		for (Sensor s : sensors) {
			mSensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_NORMAL);
		}

		String vol = read("vol");
		if (vol.equals("")) {
			write("vol", "14");
			write("vib", "1");
		}

		String p = read("pwd");
		if (p != null) {
			pwd = p;
		}

		String vib = read("vib");
		if (vib.equals("1")) {
			vibFlag = true;
		} else {
			vibFlag = false;
		}

		String sms = read("sms");
		if (!sms.equals("")) {
			smsFlag = true;
			smsTel = sms;
		} else {
			smsFlag = false;
			smsTel = "";
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent e) {
		// TODO Auto-generated method stub
		if (switchON == true) {
			sumX = 0;
			sumY = 0;
			sumZ = 0;
			for (int i = 0; i < 100; i++) {
				float x = e.values[SensorManager.DATA_X];
				float y = e.values[SensorManager.DATA_Y];
				float z = e.values[SensorManager.DATA_Z];

				// Low-Pass Filter
				lowX = x * FILTERING_VALAUE + lowX * (1.0f - FILTERING_VALAUE);
				lowY = y * FILTERING_VALAUE + lowY * (1.0f - FILTERING_VALAUE);
				lowZ = z * FILTERING_VALAUE + lowZ * (1.0f - FILTERING_VALAUE);

				// High-pass filter
				highX = x - lowX;
				highY = y - lowY;
				highZ = z - lowZ;

				// output

				sumX += highX;
				sumY += highY;
				sumZ += highZ;
			}

			TextView textX = (TextView) findViewById(R.id.x);
			textX.setText("x:" + String.valueOf(highX));
			TextView textY = (TextView) findViewById(R.id.y);
			textY.setText("y:" + String.valueOf(highY));
			TextView textZ = (TextView) findViewById(R.id.z);
			textZ.setText("z:" + String.valueOf(highZ));

			onSignal();
		} else {
			switchON = false;

		}

	}

	public void onSignal() {

		float N = (float) (6 * (Math.sqrt(Math.pow(sumX / 100, 2)
				+ Math.pow(sumY / 100, 2) + Math.pow(sumZ / 100, 2))));
		if (N > 6) {

			mPlayer = MediaPlayer.create(this, R.raw.alarmsound);
			mPlayer.setLooping(true);
			mPlayer.start();

		}
	}

	/*
	 * public void collect() { File file = new
	 * File(Environment.getExternalStorageDirectory(), "jiasudu.txt"); //
	 * ����Ҫ�������ļ� if (!file.getParentFile().exists()) {
	 * file.getParentFile().mkdirs(); // �������ļ���·�� }// ����Ҫ�������ļ�
	 * BufferedOutputStream bos = null; try {
	 * 
	 * bos = new BufferedOutputStream(new FileOutputStream(file, true)); String
	 * jsd = " " + lastVirb; byte[] buffer = jsd.getBytes(); bos.write(buffer);
	 * 
	 * } catch (IOException e) { e.printStackTrace(); } finally { try {
	 * bos.flush(); bos.close(); } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * } }
	 */
	public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
		if (this.locking) {
			Toast.makeText(this, "�����Ƚ����������", 0).show();
			return true;
		}
		return super.dispatchKeyEvent(paramKeyEvent);
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

	// �˳�����
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_HOME))
				&& event.getRepeatCount() == 0) {
			dialog_Exit(SocketCameraActivity.this);
		}
		return false;

		// end onKeyDown
	}

	public static void dialog_Exit(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("ȷ��Ҫ�˳���?");
		builder.setTitle("Smart GuardM");
		builder.setIcon(R.drawable.iconm_ts);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

}