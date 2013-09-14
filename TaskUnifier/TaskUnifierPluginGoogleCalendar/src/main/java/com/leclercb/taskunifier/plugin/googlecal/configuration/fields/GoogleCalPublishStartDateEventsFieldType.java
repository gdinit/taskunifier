/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.googlecal.translations.PluginTranslations;

public class GoogleCalPublishStartDateEventsFieldType extends ConfigurationFieldType.CheckBox {
	
	public GoogleCalPublishStartDateEventsFieldType() {
		super(
				PluginApi.getUserSettings(),
				"plugin.googlecal.publish_start_date_events",
				PluginTranslations.getString("publish_start_date_events"));
	}
	
	@Override
	public Boolean getPropertyValue() {
		if (PluginApi.getUserSettings().getBooleanProperty(
				"plugin.googlecal.publish_start_date_events") == null)
			return true;
		
		return super.getPropertyValue();
	}
	
}
