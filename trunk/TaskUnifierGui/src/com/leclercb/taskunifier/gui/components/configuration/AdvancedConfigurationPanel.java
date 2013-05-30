/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *   - Neither the name of TaskUnifier or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.leclercb.taskunifier.gui.components.configuration;

import org.apache.commons.lang3.SystemUtils;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationField;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.DefaultConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.fields.advanced.CommunicatorPortFieldType;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;

public class AdvancedConfigurationPanel extends DefaultConfigurationPanel {
	
	public AdvancedConfigurationPanel(ConfigurationGroup configuration) {
		super(configuration, "configuration_advanced");
		
		this.initialize();
		this.pack();
	}
	
	private void initialize() {
		if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_WINDOWS) {
			this.addField(new ConfigurationField(
					"GROWL_ENABLED",
					null,
					true,
					new ConfigurationFieldType.CheckBox(
							Main.getSettings(),
							"general.growl.enabled",
							Translations.getString("configuration.advanced.growl_enabled"))));
		}
		
		if (SystemUtils.IS_OS_WINDOWS) {
			this.addField(new ConfigurationField(
					"SNARL_ENABLED",
					null,
					true,
					new ConfigurationFieldType.CheckBox(
							Main.getSettings(),
							"general.snarl.enabled",
							Translations.getString("configuration.advanced.snarl_enabled"))));
		}
		
		this.addField(new ConfigurationField(
				"SHOW_REMINDERS_ENABLED",
				null,
				false,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"general.show_reminders.enabled",
						Translations.getString("configuration.advanced.show_reminders_enabled"))));
		
		this.addField(new ConfigurationField(
				"HIDE_TOOLBAR",
				null,
				true,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"general.toolbar.hide",
						Translations.getString("configuration.advanced.hide_toolbar"))));
		
		this.addField(new ConfigurationField(
				"COMMUNICATOR_ENABLED",
				null,
				true,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"general.communicator.enabled",
						Translations.getString("configuration.advanced.communicator_enabled"))));
		
		this.addField(new ConfigurationField(
				"COMMUNICATOR_PORT",
				Translations.getString("configuration.advanced.communicator_port"),
				true,
				new CommunicatorPortFieldType()));
	}
	
}
