/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.networkshare;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationField;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.DefaultConfigurationPanel;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.networkshare.configuration.fields.NetworkShareEmailFieldType;
import com.leclercb.taskunifier.plugin.networkshare.configuration.fields.NetworkShareNotSharedFoldersFieldType;
import com.leclercb.taskunifier.plugin.networkshare.configuration.fields.NetworkShareSharedFolderFieldType;
import com.leclercb.taskunifier.plugin.networkshare.translations.PluginTranslations;

public class NetworkShareConfigurationPanel extends DefaultConfigurationPanel {
	
	public NetworkShareConfigurationPanel(
			ConfigurationGroup configuration,
			boolean welcome) {
		super(configuration, "plugin_networkshare_configuration");
		
		this.initialize(welcome);
		this.pack();
	}
	
	private void initialize(boolean welcome) {
		this.addField(new ConfigurationField(
				"EMAIL",
				PluginApi.getTranslation("general.email"),
				new NetworkShareEmailFieldType()));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_1",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"SHARED_FOLDER",
				PluginTranslations.getString("general.shared_folder"),
				new NetworkShareSharedFolderFieldType()));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_2",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"NOT_SHARED_FOLDERS",
				PluginTranslations.getString("general.not_shared_folders"),
				new NetworkShareNotSharedFoldersFieldType()));
	}
	
}
