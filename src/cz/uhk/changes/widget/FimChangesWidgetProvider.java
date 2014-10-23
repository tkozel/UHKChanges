package cz.uhk.changes.widget;

import cz.uhk.changes.ChangeDetailActivity;
import cz.uhk.changes.ChangeDetailFragment;
import cz.uhk.changes.ChangeListActivity;
import cz.uhk.changes.R;
import cz.uhk.changes.WidgetUpdateService;
import cz.uhk.changes.tools.ChangesManager;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class FimChangesWidgetProvider extends AppWidgetProvider {
	public static final String DETAIL_ACTION = "cz.uhk.fim.changes.DETAIL";
	public static final String REFRESH_ACTION = "cz.uhk.fim.changes.REFRESH_ACTION";

	public FimChangesWidgetProvider() {
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(DETAIL_ACTION)) {
			int pos = intent.getIntExtra(WidgetUpdateService.ITEM_POS, 0);
			Intent intDetail = new Intent(context, ChangeDetailActivity.class);
			intDetail.putExtra(ChangeDetailFragment.ARG_ITEM_ID, pos);
			intDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intDetail);
		} else if (action.equals(REFRESH_ACTION)) {
			new Thread(new Runnable() {

				@SuppressLint("NewApi")
				@Override
				public void run() {
					try {
						ChangesManager.getInstance().refresh(context,
								context.getString(R.string.rss_url));
						AppWidgetManager aw = AppWidgetManager
								.getInstance(context);
						int[] appWidgetIds = aw
								.getAppWidgetIds(new ComponentName(context,
										FimChangesWidgetProvider.class));
						if (android.os.Build.VERSION.SDK_INT>=11) {
							aw.notifyAppWidgetViewDataChanged(appWidgetIds,
									R.id.widgetList);
						}
					} catch (Exception e) {
					}
				}
			}).start();

		}
		super.onReceive(context, intent);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int i = 0; i < appWidgetIds.length; i++) {
			RemoteViews rv = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);

			if (android.os.Build.VERSION.SDK_INT >= 11) {
				Intent intent = new Intent(context, WidgetUpdateService.class);
				rv.setRemoteAdapter(appWidgetIds[i], R.id.widgetList, intent);
				rv.setEmptyView(R.id.widgetList, R.id.widgetList);
				// klik na polozku
				Intent clickIntent = new Intent(context,
						FimChangesWidgetProvider.class);
				clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						appWidgetIds[i]);
				clickIntent.setAction(DETAIL_ACTION);
				clickIntent.setData(Uri.parse(clickIntent
						.toUri(Intent.URI_INTENT_SCHEME)));
				PendingIntent piClick = PendingIntent.getBroadcast(context, 0,
						clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				rv.setPendingIntentTemplate(R.id.widgetList, piClick);
			}
			// refresh
			Intent refreshIntent = new Intent(context,
					FimChangesWidgetProvider.class);
			refreshIntent.setAction(REFRESH_ACTION);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					0, refreshIntent, 0);
			rv.setOnClickPendingIntent(R.id.wdgRefreshButton, pendingIntent);

			// klik na logo -> start mainactivity
			Intent appIntent = new Intent(context, ChangeListActivity.class);
			PendingIntent piApp = PendingIntent.getActivity(context, 0,
					appIntent, 0);
			rv.setOnClickPendingIntent(R.id.wdgTitle, piApp);
			rv.setOnClickPendingIntent(R.id.wdgIcon, piApp);

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}

	}
}
