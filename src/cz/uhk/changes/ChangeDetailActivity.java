package cz.uhk.changes;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

/**
 * An activity representing a single Change detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link ChangeListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link ChangeDetailFragment}.
 */
public class ChangeDetailActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putInt(ChangeDetailFragment.ARG_ITEM_ID, getIntent()
					.getIntExtra(ChangeDetailFragment.ARG_ITEM_ID,0));
			ChangeDetailFragment fragment = new ChangeDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.add(R.id.change_detail_container, fragment).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpTo(this, new Intent(this,
					ChangeListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
