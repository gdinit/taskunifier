/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.networkshare.configuration.fields;

import javax.swing.JFileChooser;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class NetworkShareSharedFolderFieldType extends ConfigurationFieldType.FileChooser {
	
	public NetworkShareSharedFolderFieldType() {
		super(
				PluginApi.getUserSettings(),
				"plugin.networkshare.shared_folder",
				null,
				true,
				JFileChooser.DIRECTORIES_ONLY,
				null,
				null);
	}
	
}
