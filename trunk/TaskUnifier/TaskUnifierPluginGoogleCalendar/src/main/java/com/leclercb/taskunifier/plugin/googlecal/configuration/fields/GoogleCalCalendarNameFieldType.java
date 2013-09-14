/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class GoogleCalCalendarNameFieldType extends ConfigurationFieldType.TextField {
	
	public GoogleCalCalendarNameFieldType() {
		super(PluginApi.getUserSettings(), "plugin.googlecal.calendar_name");
	}
	
	@Override
	public String getPropertyValue() {
		if (PluginApi.getUserSettings().getStringProperty(
				"plugin.googlecal.calendar_name") == null)
			return "TaskUnifier";
		
		return super.getPropertyValue();
	}
	
}
