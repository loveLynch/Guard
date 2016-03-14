package com.dft.smartguardm;



import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * …Ë÷√
 * 
 * @author dft
 *
 */
public class JKSettingActivity extends PreferenceActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.jksetting);
	}
}
