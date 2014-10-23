package cz.uhk.changes.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SqlHelper extends android.database.sqlite.SQLiteOpenHelper {

	private static final String SQL_CREATE = "CREATE TABLE changes (title TEXT, author TEXT, tfrom TEXT, tto TEXT, descr TEXT)";
	private static final String SQL_CREATE_KW = "CREATE TABLE keywords (word TEXT)";
	private static final int DB_VERSION = 2;
	private static final String DB_NAME = "FIMChanges";
	private static SqlHelper _instance;

	protected SqlHelper(Context context) {
		super(context,DB_NAME,null,DB_VERSION);
	}
	
	public static SqlHelper getInstance(Context c) {
		if (_instance==null) {
			_instance = new SqlHelper(c);
		}
		return _instance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
		db.execSQL(SQL_CREATE_KW);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion==1 && newVersion==2) {
			db.execSQL(SQL_CREATE_KW);
		}
	}

}
