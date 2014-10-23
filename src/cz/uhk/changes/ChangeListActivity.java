package cz.uhk.changes;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import cz.uhk.changes.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class ChangeListActivity extends SherlockFragmentActivity implements
		ChangeListFragment.Callbacks {
	

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_list);

		if (findViewById(R.id.change_detail_container) != null) {
			mTwoPane = true;

			((ChangeListFragment)getSupportFragmentManager().findFragmentById(
					R.id.change_list)).setActivateOnItemClick(true);
		}
		
		checkAndFireAlarm();
	}
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return ((ChangeListFragment)getSupportFragmentManager().findFragmentById(
				R.id.change_list)).onOptionsItemSelected(item);
	}

	/**
	 * Callback method from {@link ChangeListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putInt(ChangeDetailFragment.ARG_ITEM_ID, id);
			ChangeDetailFragment fragment = new ChangeDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.change_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ChangeDetailActivity.class);
			detailIntent.putExtra(ChangeDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
	
	/**
	 * zkontroluje nastaveni refreshAlarmu a pripadne jej zapne
	 */
	public void checkAndFireAlarm() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		Context context = getApplicationContext();
		Intent i = new Intent(context, RefreshAlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		if (PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_NO_CREATE)==null) {
			Integer period = Integer.valueOf(pref.getString("sync_freq", "0"));
			int delay = 3600000 * period;
			if (period>0) {
			    am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),delay, pi);
			}
		}
	}
	
}
