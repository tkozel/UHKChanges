package cz.uhk.changes.tools;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import android.annotation.SuppressLint;
import android.content.Context;
import cz.uhk.changes.parser.ChangeParser;
import cz.uhk.changes.sql.ChangeDao;
import cz.uhk.changes.sql.KeywordDao;
import cz.uhk.changes.model.Change;

@SuppressLint("SimpleDateFormat")
public class ChangesManager {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d.M.yyyy H:mm");
	private static ChangesManager _instance = null;
	
	private List<Change> cache = null;
	
	public static ChangesManager getInstance() {
		if (_instance==null) {
			_instance = new ChangesManager();
		}
		return _instance;
	}
	
	public List<Change> getChanges(Context c) {
		if (cache == null) {
			ChangeDao dao = new ChangeDao(c);
			cache = dao.getAll();
		} 
		return cache;
	}
	
	public ChangeDao.DataInfo getChangeInfo(Context c) {
		return new ChangeDao(c).getInfo();
	}
	
	private boolean itemSuitable(Change ch, List<String> keywords) {
		String title = ch.getTitle().toLowerCase();
		if (keywords==null || keywords.size()==0) return true;
		for (String keyw : keywords) {
			if (title.contains(keyw.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	private void filterData(List<Change> data, List<String> keywords) {
		for (int i=0; i<data.size(); i++) {
			Change ch = data.get(i);
			if (!itemSuitable(ch,keywords)) {
				data.remove(i--);
			}
		}
	}
	
	/**
	 * Nacteni dat z RSS kanalu
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public List<Change> refresh(Context c, String url) throws ParserConfigurationException, SAXException, IOException {
		List<Change> data = ChangeParser.parseHTMLSource(url);
		KeywordDao keywordDao = new KeywordDao(c);
		ChangeDao changeDao = new ChangeDao(c);
		filterData(data,keywordDao.getKeywords());
		changeDao.clearAll();
		changeDao.add(data);
		cache = data;
		return data;
	}
}
