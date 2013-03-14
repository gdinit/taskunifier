package com.leclercb.taskunifier.gui.commons.comparators;

import java.util.Comparator;

import com.leclercb.commons.api.utils.CompareUtils;
import com.leclercb.taskunifier.gui.actions.ActionList;

public class ActionListComparator implements Comparator<ActionList> {
	
	public static final ActionListComparator INSTANCE = new ActionListComparator();
	
	private ActionListComparator() {
		
	}
	
	@Override
	public int compare(ActionList a1, ActionList a2) {
		String s1 = (a1 == null ? null : a1.getTitle());
		String s2 = (a2 == null ? null : a2.getTitle());
		
		return CompareUtils.compareStringIgnoreCase(s1, s2);
	}
	
}
