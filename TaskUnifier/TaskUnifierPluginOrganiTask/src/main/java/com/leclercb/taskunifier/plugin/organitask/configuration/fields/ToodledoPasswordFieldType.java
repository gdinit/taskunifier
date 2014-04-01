/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class ToodledoPasswordFieldType extends ConfigurationFieldType.PasswordField {
	
	public ToodledoPasswordFieldType() {
		super(PluginApi.getUserSettings(), "toodledo.password");
	}
	
}
