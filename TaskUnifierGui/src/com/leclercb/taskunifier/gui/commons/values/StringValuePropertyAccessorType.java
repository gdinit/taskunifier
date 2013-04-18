package com.leclercb.taskunifier.gui.commons.values;

import org.jdesktop.swingx.renderer.StringValue;

import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorType;
import com.leclercb.taskunifier.gui.translations.TranslationsUtils;

public class StringValuePropertyAccessorType implements StringValue {
	
	public static final StringValuePropertyAccessorType INSTANCE = new StringValuePropertyAccessorType();
	
	private StringValuePropertyAccessorType() {
		
	}
	
	@Override
	public String getString(Object value) {
		if (!(value instanceof PropertyAccessorType))
			return " ";
		
		return TranslationsUtils.translatePropertyAccessorType((PropertyAccessorType) value);
	}
	
}
