/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.toodledo.translations.PluginTranslations;

public class ToodledoEnableSSLFieldType extends ConfigurationFieldType.CheckBox {
	
	public ToodledoEnableSSLFieldType() {
		super(
				PluginApi.getUserSettings(),
				"toodledo.enable_ssl",
				PluginTranslations.getString("enable_ssl"));
	}
	
	@Override
	public Boolean getPropertyValue() {
		if (PluginApi.getUserSettings().getBooleanProperty(
				"toodledo.enable_ssl") == null)
			return true;
		
		return super.getPropertyValue();
	}
	
}
