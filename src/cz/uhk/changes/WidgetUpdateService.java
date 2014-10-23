package cz.uhk.changes;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import cz.uhk.changes.model.Change;
import cz.uhk.changes.tools.ChangesManager;
import cz.uhk.changes.tools.TimeUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

@SuppressLint("NewApi")
public class WidgetUpdateService extends RemoteViewsService {
	public static final String ITEM_POS = "cz.uhk.fim.changes.ITEM_POS";
	@SuppressLint("SimpleDateFormat")
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("E dd.MM.");
	
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		
		return new ChangesRemoteViewFactory(this);
	}
	
	public class ChangesRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
		private Context context;
		private List<Change> data;

		public ChangesRemoteViewFactory(Context c) {
			context = c;
		}
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public RemoteViews getLoadingView() {
			return null;
		}

		@Override
		public RemoteViews getViewAt(int position) {
			Change ch = data.get(position);
			RemoteViews rv = new RemoteViews(context.getPackageName(),R.layout.widget_row_layout2);
//			if (TimeUtils.isActiveToday(ch)) {
//				rv = new RemoteViews(context.getPackageName(),R.layout.widget_row_layout2_high);
//			} else {
//				rv = new RemoteViews(context.getPackageName(),R.layout.widget_row_layout2);
//			}
			rv.setTextViewText(R.id.wdgTitle, ch.getTitle());
			rv.setTextViewText(R.id.wdgDate, DATE_FORMAT.format(ch.getStartDate()));
			if (TimeUtils.isActiveToday(ch)) {
				rv.setTextColor(R.id.wdgTitle, Color.RED);
				rv.setTextColor(R.id.wdgDate, Color.RED);
			} else {
				rv.setTextColor(R.id.wdgTitle, Color.BLACK);
				rv.setTextColor(R.id.wdgDate, Color.BLACK);
			}
			//rv.setTextViewText(R.id.wdgAuthor, ch.getAuthor());
			Intent intent = new Intent();
			intent.putExtra(ITEM_POS,position);
			rv.setOnClickFillInIntent(R.id.wdgLinLayout, intent);
			return rv;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public void onCreate() {
			onDataSetChanged();
		}

		@Override
		public void onDataSetChanged() {
			data = ChangesManager.getInstance().getChanges(context);
		}

		@Override
		public void onDestroy() {}
		
	}

}
