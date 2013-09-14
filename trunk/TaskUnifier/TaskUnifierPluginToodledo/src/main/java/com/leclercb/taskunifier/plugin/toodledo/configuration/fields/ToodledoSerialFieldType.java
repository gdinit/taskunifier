/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class ToodledoSerialFieldType extends ConfigurationFieldType.TextField {
	
	public ToodledoSerialFieldType() {
		super(PluginApi.getUserSettings(), "toodledo.serial");
	}
	
}
