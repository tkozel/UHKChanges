package cz.uhk.changes;

import java.util.List;

import cz.uhk.changes.tools.KeywordHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class KeywordsActivity extends Activity {
	List<String> keywords = null;
	private EditText edKeyword;
	private ListView lv;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		keywords = KeywordHelper.getInstance(this).getKeywords();

		setContentView(R.layout.activity_keywords);

		lv = (ListView) findViewById(R.id.lvSetupKeyWords);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, keywords);
		lv.setAdapter(adapter);

		edKeyword = (EditText) findViewById(R.id.edSetupKeyWord);
		
		Button btAdd = (Button) findViewById(R.id.btSetupAdd);
		btAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				keywords.add(edKeyword.getText().toString().trim());
				edKeyword.setText("");
				adapter.notifyDataSetChanged();
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					final int pos, long id) {
				new AlertDialog.Builder(KeywordsActivity.this)
					.setMessage(R.string.msgConfirmDelete)
					.setPositiveButton(R.string.btYes,
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								keywords.remove(pos);
								adapter.notifyDataSetChanged();
							}
						})
					.setNegativeButton(R.string.btNo, new AlertDialog.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					}).create().show();
				
				return true;
			}
		});
	}


	@Override
	protected void onPause() {
		KeywordHelper.getInstance(this).setKeywords(keywords);
		super.onPause();
	}

}
