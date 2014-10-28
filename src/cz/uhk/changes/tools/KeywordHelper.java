package cz.uhk.changes.tools;

import java.util.*;

import cz.uhk.changes.sql.KeywordDao;
import android.content.Context;

/**
 * Helper pro práci s klíčovými slovy filtru - singleton
 * @author Tomas Kozel
 *
 */
public class KeywordHelper {
	private static KeywordHelper _instance;
	private List<String> lst;
	KeywordDao dao;
	
	protected KeywordHelper(Context context) {
		dao = new KeywordDao(context);
		lst = dao.getKeywords();
	}
	
	public static KeywordHelper getInstance(Context c) {
		if (_instance==null) {
			_instance = new KeywordHelper(c);
		}
		return _instance;
	}

	public List<String> getKeywords() {
		return lst;
	}
	
	public void setKeywords(List<String> keywords) {
		lst = keywords;
		dao.storeKeywords(lst);
	}
}
