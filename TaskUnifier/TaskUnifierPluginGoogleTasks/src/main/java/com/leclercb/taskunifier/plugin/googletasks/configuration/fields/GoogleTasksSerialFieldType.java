/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googletasks.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class GoogleTasksSerialFieldType extends ConfigurationFieldType.TextField {
	
	public GoogleTasksSerialFieldType() {
		super(PluginApi.getUserSettings(), "plugin.googletasks.serial");
	}
	
}
