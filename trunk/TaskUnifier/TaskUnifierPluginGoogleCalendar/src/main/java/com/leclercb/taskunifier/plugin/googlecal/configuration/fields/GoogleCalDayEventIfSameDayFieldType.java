/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.googlecal.translations.PluginTranslations;

public class GoogleCalDayEventIfSameDayFieldType extends ConfigurationFieldType.CheckBox {
	
	public GoogleCalDayEventIfSameDayFieldType() {
		super(
				PluginApi.getUserSettings(),
				"plugin.googlecal.day_event_if_same_day",
				PluginTranslations.getString("day_event_if_same_day"));
	}
	
	@Override
	public Boolean getPropertyValue() {
		if (PluginApi.getUserSettings().getBooleanProperty(
				"plugin.googlecal.day_event_if_same_day") == null)
			return true;
		
		return super.getPropertyValue();
	}
	
}
