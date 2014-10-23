package cz.uhk.changes;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cz.uhk.changes.model.Change;
import cz.uhk.changes.R;
import cz.uhk.changes.tools.ChangesManager;

/**
 * A fragment representing a single Change detail screen. This fragment is
 * either contained in a {@link ChangeListActivity} in two-pane mode (on
 * tablets) or a {@link ChangeDetailActivity} on handsets.
 */
public class ChangeDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Change change;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ChangeDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			int index = getArguments().getInt(ARG_ITEM_ID, 0);
	        change = ChangesManager.getInstance().getChanges(getActivity()).get(index);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_change_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (change != null) {
			TextView tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
	        TextView tvAuthor = (TextView) rootView.findViewById(R.id.tvAuthor);
	        TextView tvFrom = (TextView) rootView.findViewById(R.id.tvFrom);
	        TextView tvTo = (TextView) rootView.findViewById(R.id.tvTo);
	        TextView tvDesc = (TextView) rootView.findViewById(R.id.tvDesc);
	        tvTitle.setText(change.getTitle());
	        tvAuthor.setText(change.getAuthor());
	        tvFrom.setText(ChangesManager.DATE_FORMAT.format(change.getStartDate()));
	        tvTo.setText(ChangesManager.DATE_FORMAT.format(change.getEndDate()));
	        tvDesc.setText(change.getDescription());
		}

		return rootView;
	}
}
