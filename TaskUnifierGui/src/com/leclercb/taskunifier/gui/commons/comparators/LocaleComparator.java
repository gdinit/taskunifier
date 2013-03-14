package com.leclercb.taskunifier.gui.commons.comparators;

import java.util.Comparator;
import java.util.Locale;

import com.leclercb.commons.api.utils.CompareUtils;

public class LocaleComparator implements Comparator<Locale> {
	
	public static final LocaleComparator INSTANCE = new LocaleComparator();
	
	private LocaleComparator() {
		
	}
	
	@Override
	public int compare(Locale l1, Locale l2) {
		String s1 = (l1 == null ? null : l1.getDisplayName());
		String s2 = (l2 == null ? null : l2.getDisplayName());
		
		return CompareUtils.compareStringIgnoreCase(s1, s2);
	}
	
}
