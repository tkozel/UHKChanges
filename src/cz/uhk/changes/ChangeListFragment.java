package cz.uhk.changes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;

import cz.uhk.changes.tools.ChangesManager;
import cz.uhk.changes.R;
import cz.uhk.changes.model.Change;
import cz.uhk.changes.tools.TimeUtils;
import cz.uhk.changes.widget.FimChangesWidgetProvider;
import cz.uhk.changes.KeywordsActivity;
import cz.uhk.changes.PrefActivity;
import android.annotation.SuppressLint;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment seznamu změn v rozcrhu
 * @author Tomas Kozel
 *
 */
public class ChangeListFragment extends SherlockListFragment {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	@SuppressLint("SimpleDateFormat")
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"E d.M. HH:mm");
	public static final String REFRESH_ACTION = "cz.uhk.fim.changes.REFRESH_ACTIVITY";
	private static final int REQUEST_FILTER = 1;
	private static final int REQUEST_SETUP = 1;
	protected ProgressDialog dial = null;

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(int id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(int id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ChangeListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		android.app.ActionBar actionBar = getSherlockActivity().getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		SpinnerAdapter spAd = ArrayAdapter.createFromResource(getSherlockActivity(), R.array.faculties, R.layout.faculty_item_layout);
				
		actionBar.setListNavigationCallbacks(spAd, new OnNavigationListener() {
			
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				String[] fs = getSherlockActivity().getResources().getStringArray(R.array.faculties);
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
				if (!pref.getString("faculty", "").equals(fs[itemPosition])) {
					pref.edit().putString("faculty", fs[itemPosition]).commit();
					refreshData();
				}
				return true;
			}
		});
		
		refreshView(ChangesManager.getInstance().getChanges(getActivity()));
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		mCallbacks.onItemSelected(position);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
	private void refreshData() {
		
		String url = String.format(getString(R.string.rss_url),PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("faculty", "FIM"));;
		
		new AsyncTask<String, Integer, List<Change>>() {

			@Override
			protected void onPreExecute() {
				dial = ProgressDialog.show(getActivity(),
						getString(R.string.progTitle),
						getString(R.string.progMsg), true);
			}

			@Override
			protected List<Change> doInBackground(String... params) {
				try {
					return ChangesManager.getInstance().refresh(getActivity(), params[0]);
				} catch (Exception e) {
					e.printStackTrace();
					publishProgress(-1);
					return new ArrayList<Change>();
				}
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				if (values[0] == -1) {
					Toast.makeText(getActivity(),
							getString(R.string.errorMsg), Toast.LENGTH_LONG)
							.show();
				}
			};

			@Override
			protected void onPostExecute(List<Change> result) {
				if (dial.isShowing())
					dial.dismiss();
				refreshView(result);
				updateWidget();
			}

		}.execute(url);
	}


	private void refreshView(List<Change> result) {
		
		setListAdapter(new ArrayAdapter<Change>(getActivity(),
				R.layout.row_layout, result) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inf = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				View v;
				if (convertView!=null) {
					v = convertView;
				} else {
					v = inf.inflate(R.layout.row_layout, parent, false);
				}
				Change ch = ChangesManager.getInstance().getChanges(getActivity()).get(
						position);
				TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
				TextView tvFrom = (TextView) v.findViewById(R.id.tvFrom);
				TextView tvAuthor = (TextView) v.findViewById(R.id.tvAuthor);
				tvTitle.setText(ch.getTitle());
				tvFrom.setText(DATE_FORMAT.format(ch.getStartDate()));
				tvAuthor.setText(ch.getAuthor());
				if (TimeUtils.isActiveToday(ch)) {
					v.setBackgroundColor(0xffffe0e0);
				} else {
					v.setBackgroundColor(getResources().getColor(R.color.gray));
				}
				return v;
			}
		});
		checkOrSetFacultyDropDown();
	}
	
	
	private void checkOrSetFacultyDropDown() {
		String[] fa = getSherlockActivity().getResources().getStringArray(R.array.faculties);
		int i = getSherlockActivity().getActionBar().getSelectedNavigationIndex();
		String prefVal = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity()).getString("faculty", "FIM");
		if (!fa[i].equals(prefVal)) {
			int j;
			for (j = 0; j<fa.length; j++) {
				if (fa[j].equals(prefVal)) {
					break;
				}
			}
			if (j<fa.length)
				getSherlockActivity().getActionBar().setSelectedNavigationItem(j);
		}
	}

	private void updateWidget() {
		AppWidgetManager aw = AppWidgetManager.getInstance(getActivity());
		int[] ids = aw.getAppWidgetIds(new ComponentName(getActivity(),
				FimChangesWidgetProvider.class));
		if (android.os.Build.VERSION.SDK_INT >= 11)
			aw.notifyAppWidgetViewDataChanged(ids, R.id.widgetList);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			refreshData();
			return true;
		case R.id.menu_about:
			showAboutDlg();
			return true;
		case R.id.menu_setup:
			setup();
			return true;
		case R.id.menu_filter:
			filtr();
			return true;
		}
		return false;
	}

	private void filtr() {
		Intent i = new Intent(getActivity(),KeywordsActivity.class);
		startActivityForResult(i, REQUEST_FILTER);
	}
	
	private void setup() {
		Intent intent = new Intent(getActivity(), PrefActivity.class);
		startActivityForResult(intent, REQUEST_SETUP);
	}
	
	@SuppressLint("InflateParams")
	private void showAboutDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.aboutTitle));
		builder.setIcon(R.drawable.ic_launcher);

		builder.setView(getActivity().getLayoutInflater()
				.inflate(R.layout.dialog_about, null));
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(REFRESH_ACTION);
		getActivity().registerReceiver(this.refreshReceiver, filter);
		refreshView(ChangesManager.getInstance().getChanges(getActivity()));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(refreshReceiver);
	}
	
	private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			refreshView(ChangesManager.getInstance().getChanges(context));
		}

	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_FILTER || requestCode == REQUEST_SETUP) {
			refreshData();
		}
	}
	
	@Override
	public void onDestroy() {
		if (dial != null)
			dial.dismiss();
		super.onDestroy();
	}

}
