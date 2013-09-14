/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class GoogleCalSerialFieldType extends ConfigurationFieldType.TextField {
	
	public GoogleCalSerialFieldType() {
		super(PluginApi.getUserSettings(), "plugin.googlecal.serial");
	}
	
}
