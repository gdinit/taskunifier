package com.leclercb.taskunifier.gui.commons.comparators;

import java.util.Comparator;

import com.leclercb.commons.api.utils.CompareUtils;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;

public class PropertyAccessorComparator implements Comparator<PropertyAccessor<?>> {
	
	public static final PropertyAccessorComparator INSTANCE = new PropertyAccessorComparator();
	
	private PropertyAccessorComparator() {
		
	}
	
	@Override
	public int compare(PropertyAccessor<?> a1, PropertyAccessor<?> a2) {
		String s1 = (a1 == null ? null : a1.getLabel());
		String s2 = (a2 == null ? null : a2.getLabel());
		
		return CompareUtils.compareStringIgnoreCase(s1, s2);
	}
	
}
