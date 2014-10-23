package cz.uhk.changes;


import cz.uhk.changes.tools.ChangesManager;
import cz.uhk.changes.widget.FimChangesWidgetProvider;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Broadcast receiver pro pravidelnou aktualizaci
 * @author Tomas Kozel
 *
 */
public class RefreshAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		new Thread(new Runnable() {

			@SuppressLint("NewApi")
			@Override
			public void run() {
				try {
					String url = String.format(context.getString(R.string.rss_url),PreferenceManager.getDefaultSharedPreferences(context).getString("faculty", "FIM"));;
					
					ChangesManager.getInstance().refresh(context,url);
					AppWidgetManager aw = AppWidgetManager.getInstance(context);
					int[] appWidgetIds = aw
							.getAppWidgetIds(new ComponentName(context,
									FimChangesWidgetProvider.class));
					if (android.os.Build.VERSION.SDK_INT>=11) {
						aw.notifyAppWidgetViewDataChanged(appWidgetIds,
								R.id.widgetList);
					}
					context.sendBroadcast(new Intent(ChangeListFragment.REFRESH_ACTION));
					
					Log.i("RefreshAlarmReceiver", "Data refreshed");
				} catch (Exception e) {
					Log.e("RefreshAlarmReceiver", "RefreshError");
					e.printStackTrace();
				}
			}
		}).start();
	}

}
