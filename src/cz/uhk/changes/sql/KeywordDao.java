package cz.uhk.changes.sql;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class KeywordDao {
	
	private SqlHelper helper;

	public KeywordDao(Context c) {
		helper = SqlHelper.getInstance(c);
	}
	
	public List<String> getKeywords() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.query("keywords", null, null, null, null,null,null);
		List<String> res = new ArrayList<String>();
		while (c.moveToNext()) {
			res.add(c.getString(0));
		}
		c.close();
		db.close();
		return res;
	}
	
	public void storeKeywords(List<String> keywords) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("keywords", null, null);
		db.beginTransaction();
		for (String s : keywords) {
			ContentValues cv = new ContentValues();
			cv.put("word", s);
			db.insert("keywords", "", cv);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
}
