/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.networkshare.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class NetworkShareEmailFieldType extends ConfigurationFieldType.TextField {
	
	public NetworkShareEmailFieldType() {
		super(PluginApi.getUserSettings(), "plugin.networkshare.email");
	}
	
}
