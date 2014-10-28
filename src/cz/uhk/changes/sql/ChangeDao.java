package cz.uhk.changes.sql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cz.uhk.changes.model.Change;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Základní DAO pro práci s objektem změny rozvrhu
 * @author Tomas Kozel
 *
 */
public class ChangeDao {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SqlHelper helper;

	public ChangeDao(Context context) {
		helper = SqlHelper.getInstance(context);
	}

	public List<Change> getAll() {
		String[] cols = { "title", "author", "tfrom", "tto", "descr" };
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.query("changes", cols, null, null, null, null, "tfrom");
		List<Change> res = new ArrayList<Change>();
		while (c.moveToNext()) {
			res.add(changeFromCursor(c));
		}
		db.close();
		return res;
	}

	private Change changeFromCursor(Cursor c) {
		Change ch = new Change();
		ch.setTitle(c.getString(0));
		ch.setAuthor(c.getString(1));
		try {
			ch.setStartDate(sdf.parse(c.getString(2)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			ch.setEndDate(sdf.parse(c.getString(3)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ch.setDescription(c.getString(4));
		return ch;
	}

	public void add(List<Change> list) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		for (Change ch : list) {
			ContentValues val = new ContentValues();
			val.put("title", ch.getTitle());
			val.put("author", ch.getAuthor());
			val.put("tfrom", sdf.format(ch.getStartDate()));
			val.put("tto", sdf.format(ch.getEndDate()));
			val.put("descr", ch.getDescription());
			db.insert("changes", "", val);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	public void clearAll() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		db.delete("changes", null, null);
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	public DataInfo getInfo() {
		SQLiteDatabase db = helper.getReadableDatabase();
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.HOUR_OF_DAY,23);
//		cal.set(Calendar.MINUTE,59);
//		cal.set(Calendar.SECOND,59);
//		Cursor c = db.rawQuery("select count(*) from changes", null);
//		int cnt = (c.moveToFirst())?c.getInt(0):0;
//
//		c = db.rawQuery("select count(*) from changes where tfrom<=?", new String[] {sdf.format(cal.getTime())});
//		int today = (c.moveToFirst())?c.getInt(0):0;
//
//		db.close();
		DataInfo info = new DataInfo();
		Cursor c = db.rawQuery("select date(tfrom),count(*) from changes where tfrom>=date(\'now\') group by date(tfrom)",null);
		if (c.moveToFirst()) {
			info.cnt1 = c.getInt(1);
			info.dat1 = c.getString(0);
		}
		if (c.moveToNext()) {
			info.cnt2 = c.getInt(1);
			info.dat2 = c.getString(0);
		}
		if (c.moveToNext()) {
			info.cnt3 = c.getInt(1);
			info.dat3 = c.getString(0);
			
		}
		
		db.close();
		return info;

	}

	public class DataInfo {
		
		public int cnt1, cnt2, cnt3;
		public String dat1="-", dat2="-", dat3="-";
	}
}
