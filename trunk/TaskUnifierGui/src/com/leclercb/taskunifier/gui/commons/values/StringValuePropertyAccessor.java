package com.leclercb.taskunifier.gui.commons.values;

import org.jdesktop.swingx.renderer.StringValue;

import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;

public class StringValuePropertyAccessor implements StringValue {
	
	public static final StringValuePropertyAccessor INSTANCE = new StringValuePropertyAccessor();
	
	private StringValuePropertyAccessor() {
		
	}
	
	@Override
	public String getString(Object value) {
		if (!(value instanceof PropertyAccessor<?>))
			return " ";
		
		PropertyAccessor<?> accessor = (PropertyAccessor<?>) value;
		
		return accessor.getLabel()
				+ " ("
				+ StringValuePropertyAccessorType.INSTANCE.getString(accessor.getType())
				+ ")";
	}
	
}
