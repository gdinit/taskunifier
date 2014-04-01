/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.configuration.fields;

import javax.swing.JOptionPane;

import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.plugin.toodledo.translations.PluginTranslations;

public class ToodledoEmailFieldType extends ConfigurationFieldType.TextField {
	
	public ToodledoEmailFieldType() {
		super(PluginApi.getUserSettings(), "toodledo.email", true);
	}
	
	@Override
	public void saveAndApplyConfig() {
		String currentEmail = this.getPropertyValue();
		
		if (!EqualsUtils.equalsStringIgnoreCase(
				currentEmail,
				this.getFieldValue())) {
			com.leclercb.taskunifier.plugin.toodledo.OrganiTaskApi.getInstance().resetConnectionParameters(
					PluginApi.getUserSettings());
			
			if (currentEmail != null && currentEmail.length() != 0) {
				String[] options = new String[] {
						Translations.getString("general.ok"),
						Translations.getString("general.cancel") };
				
				int result = JOptionPane.showOptionDialog(
						PluginApi.getCurrentWindow(),
						PluginTranslations.getString("login_has_changed_should_remove_data"),
						Translations.getString("general.question"),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE,
						null,
						options,
						options[0]);
				
				if (result == 0) {
					PluginApi.resetAllSynchronizersAndDeleteModels();
				}
			}
		}
		
		super.saveAndApplyConfig();
	}
	
}
