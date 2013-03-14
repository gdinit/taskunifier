package com.leclercb.taskunifier.gui.commons.comparators;

import java.util.Comparator;

import com.leclercb.commons.api.utils.CompareUtils;
import com.leclercb.commons.gui.swing.lookandfeel.LookAndFeelDescriptor;

public class LookAndFeelDescriptorComparator implements Comparator<LookAndFeelDescriptor> {
	
	public static final LookAndFeelDescriptorComparator INSTANCE = new LookAndFeelDescriptorComparator();
	
	private LookAndFeelDescriptorComparator() {
		
	}
	
	@Override
	public int compare(LookAndFeelDescriptor l1, LookAndFeelDescriptor l2) {
		String s1 = (l1 == null ? null : l1.getName());
		String s2 = (l2 == null ? null : l2.getName());
		
		return CompareUtils.compareStringIgnoreCase(s1, s2);
	}
	
}
