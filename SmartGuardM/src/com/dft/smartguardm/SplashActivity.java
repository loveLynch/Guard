package com.dft.smartguardm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = 3000; // 延迟三秒

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉标题
		getWindow().setFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR,
				WindowManager.LayoutParams.TYPE_STATUS_BAR); // 全屏
		setContentView(R.layout.act_welcome);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent = new Intent(SplashActivity.this,
						SocketCameraActivity.class);
				startActivity(mainIntent);
				finish();
			}

		}, SPLASH_DISPLAY_LENGHT);
	}

}