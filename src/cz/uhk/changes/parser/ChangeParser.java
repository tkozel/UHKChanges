package cz.uhk.changes.parser;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import cz.uhk.changes.model.Change;

public class ChangeParser {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"d.M.yyyy H:mm");

	public static List<Change> parseHTMLSource(String url)
			throws ParserConfigurationException, SAXException, IOException {

		Document doc = Jsoup.parse(new URL(url), 30000);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		List<Change> result = new ArrayList<Change>();
		Elements trs = doc
				.select("table#p_lt_ctl02_pageplaceholder_p_lt_ctl02_WebPartZone1_WebPartZone1_zone_GRID_Grid")
				.select("tr");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date today = cal.getTime();
		for (int i = 1; i < trs.size() - 1; i++) {
			Element element = trs.get(i);
			Elements chn = element.children();
			Date dateOd = decodeDate(chn.get(1).text());
			Date dateDo = decodeDate(chn.get(2).text());
			if (dateOd!=null && dateDo!=null) {
				Change ch = new Change(chn.get(0).text(), chn.get(3).text(),
						dateOd, dateDo, chn.get(4).text());
				if (ch.getStartDate().after(today) || ch.getEndDate().after(today)) {
					result.add(ch);
				}
			}

		}

		sort(result);
		return result;
	}

	private static void sort(List<Change> data) {
		Collections.sort(data, new Comparator<Change>() {

			@Override
			public int compare(Change lhs, Change rhs) {
				return (lhs.getStartDate().after(rhs.getStartDate())) ? 1 : -1;
			}
		});
	}

	
	private static Date decodeDate(String dateStr) {
		try {
			return DATE_FORMAT.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

}
