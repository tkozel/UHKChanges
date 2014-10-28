package cz.uhk.changes.tools;

import java.util.Calendar;
import java.util.Date;

import cz.uhk.changes.model.Change;

/**
 * Pomocne metody pro praci s case a datem
 * @author Tomas Kozel
 *
 */
public class TimeUtils {
	/**
	 * Vraci Date zacatku dne
	 * @return date
	 */
	public static Date getDayDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	
	/**
	 * Zjisti, zda datum změny odpovídá dnešnímu datu. Bere v úvahu možnost týdenního
	 * opakování události v den týdne shodný s dnem prvního vypsaného data.
	 * @param date
	 * @return
	 */
	public static boolean isActiveToday(Change ch) {
		Calendar cal = Calendar.getInstance();
		Date now = getDayDate();
		cal.setTime(now);
		cal.add(Calendar.DATE, 1);
		Date tomorrow = cal.getTime();
		boolean startOnOrBefore = ch.getStartDate().compareTo(tomorrow) < 0;
		boolean endOnOrAfter = ch.getEndDate().compareTo(now)>=0;
		if (startOnOrBefore && endOnOrAfter) {
			cal.setTime(ch.getStartDate());
			int startDay = cal.get(Calendar.DAY_OF_WEEK);
			cal.setTime(now);
			int nowDay = cal.get(Calendar.DAY_OF_WEEK);
			
			return nowDay == startDay;
		} else {
			return false;
		}
	}

	/**
	 * U dlouhotrvajicich (opakujicich se) udalosti se posune zacatek na nejblizsi budouci
	 * @param ch
	 */
	public static void adjustStartDate(Change ch) {
		Date now = getDayDate();
		if (ch.getStartDate().before(now)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(ch.getStartDate());
			while (cal.getTimeInMillis()<now.getTime()) {
				cal.add(Calendar.WEEK_OF_MONTH, 1);
			}
			ch.setStartDate(cal.getTime());
		}
	}
}
