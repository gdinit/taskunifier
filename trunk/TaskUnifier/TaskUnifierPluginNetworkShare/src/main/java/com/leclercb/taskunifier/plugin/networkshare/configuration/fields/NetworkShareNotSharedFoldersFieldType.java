/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.networkshare.configuration.fields;

import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class NetworkShareNotSharedFoldersFieldType extends ConfigurationFieldType.ModelListField<Folder> {
	
	public NetworkShareNotSharedFoldersFieldType() {
		super(
				PluginApi.getUserSettings(),
				"plugin.networkshare.not_shared_folders",
				ModelType.FOLDER);
	}
	
}
