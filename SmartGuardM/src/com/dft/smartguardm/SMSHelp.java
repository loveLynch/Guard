package com.dft.smartguardm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.gsm.SmsManager;
import android.util.Log;

@SuppressWarnings("deprecation")
public class SMSHelp
{
  public static boolean sendSMS(String paramString1, String paramString2, Context paramContext)
  {
    SmsManager localSmsManager = SmsManager.getDefault();
    boolean j;
    try
    {
      Intent localIntent = new Intent();
      PendingIntent localPendingIntent = PendingIntent.getBroadcast(paramContext, 0, localIntent, 0);
      localSmsManager.sendTextMessage(paramString1, null, paramString2, localPendingIntent, null);
      int i = Log.i("yb", "sendSMS-------" + paramString1 + "------ok");
      j = true;
      return j;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      j = false;
    }
	return j;
  }
}