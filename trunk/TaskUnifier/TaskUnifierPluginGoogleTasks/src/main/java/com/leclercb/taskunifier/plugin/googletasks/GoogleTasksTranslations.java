/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googletasks;

import java.util.Calendar;
import java.util.TimeZone;

public final class GoogleTasksTranslations {
	
	private GoogleTasksTranslations() {
		
	}
	
	public static Calendar translateGMTDateUserInput(long timeStamp) {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		c.setTimeInMillis(timeStamp);
		
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(
				c.get(Calendar.YEAR),
				c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY),
				c.get(Calendar.MINUTE),
				c.get(Calendar.SECOND));
		
		return calendar;
	}
	
	public static long translateGMTDateUserInput(Calendar calendar) {
		if (calendar == null)
			return 0;
		
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		c.clear();
		c.set(
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND));
		
		return c.getTimeInMillis();
	}
	
}
