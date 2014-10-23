package cz.uhk.changes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PrefActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		String freqStr = sharedPreferences.getString("sync_freq", "0");
		int freq = Integer.valueOf(freqStr);
		setRefreshAlarmState(freq>0);
	}
	
	/**
	 * Registruje alarm pro pravidelny refresh
	 */
	private void setRefreshAlarmState(boolean enable) {
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Context context = getApplicationContext();
		Intent i = new Intent(context, RefreshAlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		if (enable) {
			int delay = 3600000 * Integer.valueOf(getPreferenceScreen().getSharedPreferences().getString("sync_freq", "1"));
			am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),delay, pi);
		} else {
			am.cancel(pi);
		}
	}
}
