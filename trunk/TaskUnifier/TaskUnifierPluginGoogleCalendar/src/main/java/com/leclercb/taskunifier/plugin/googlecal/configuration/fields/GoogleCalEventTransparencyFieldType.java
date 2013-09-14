/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.googlecal.translations.PluginTranslations;

public class GoogleCalEventTransparencyFieldType extends ConfigurationFieldType.CheckBox {
	
	public GoogleCalEventTransparencyFieldType() {
		super(
				PluginApi.getUserSettings(),
				"plugin.googlecal.event_transparency",
				PluginTranslations.getString("event_transparency"));
	}
	
	@Override
	public Boolean getPropertyValue() {
		if (PluginApi.getUserSettings().getBooleanProperty(
				"plugin.googlecal.event_transparency") == null)
			return true;
		
		return super.getPropertyValue();
	}
	
}
