/*
 * TaskUnifier
 * Copyright (c) 2011, Benjamin Leclerc
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.taskunifier.gui.actions.ActionManageSynchronizerPlugins;
import com.leclercb.taskunifier.gui.actions.ActionPluginConfiguration;
import com.leclercb.taskunifier.gui.actions.synchronize.ActionSynchronizeAll;
import com.leclercb.taskunifier.gui.actions.synchronize.ActionSynchronizeCopyAll;
import com.leclercb.taskunifier.gui.actions.synchronize.ActionSynchronizePushAll;
import com.leclercb.taskunifier.gui.actions.synchronize.ActionSynchronizeResetAll;
import com.leclercb.taskunifier.gui.api.synchronizer.dummy.DummyGuiPlugin;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationField;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.DefaultConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.fields.synchronization.ChoiceFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.synchronization.KeepTasksForFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.synchronization.SchedulerSleepTimeFieldType;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;

public class SynchronizationConfigurationPanel extends DefaultConfigurationPanel implements PropertyChangeListener {
	
	private boolean welcome;
	
	public SynchronizationConfigurationPanel(
			ConfigurationGroup configuration,
			boolean welcome) {
		super(configuration, !welcome, "configuration_synchronization");
		
		this.welcome = welcome;
		
		this.initialize();
		this.pack();
		this.disableFields();
	}
	
	private void initialize() {
		this.addField(new ConfigurationField(
				"MANAGE_PLUGINS",
				Translations.getString("configuration.synchronization.synchronize_with"),
				new ConfigurationFieldType.Button(
						SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName(),
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								if (SynchronizationConfigurationPanel.this.getConfigurationGroup() != null) {
									SynchronizationConfigurationPanel.this.getConfigurationGroup().saveAndApplyConfig();
								}
								
								ActionManageSynchronizerPlugins.manageSynchronizerPlugins();
							}
							
						})));
		
		this.addField(new ConfigurationField(
				"CONFIGURE_PLUGIN",
				null,
				new ConfigurationFieldType.Button(
						new ActionPluginConfiguration(24, 24))));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_1",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"CHOICE",
				Translations.getString("configuration.synchronization.choice"),
				new ChoiceFieldType()));
		
		this.addField(new ConfigurationField(
				"KEEP",
				Translations.getString("configuration.synchronization.keep_tasks_for"),
				new KeepTasksForFieldType()));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_2",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"SCHEDULER_SLEEP_TIME",
				Translations.getString("configuration.synchronization.scheduler_sleep_time"),
				new SchedulerSleepTimeFieldType()));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_3",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"SYNC_START",
				null,
				new ConfigurationFieldType.CheckBox(
						Main.getUserSettings(),
						"synchronizer.sync_start",
						Translations.getString("configuration.synchronization.sync_start"))));
		
		this.addField(new ConfigurationField(
				"SYNC_EXIT",
				null,
				new ConfigurationFieldType.CheckBox(
						Main.getUserSettings(),
						"synchronizer.sync_exit",
						Translations.getString("configuration.synchronization.sync_exit"))));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_4",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"SYNC_REMINDER_FIELD",
				null,
				new ConfigurationFieldType.CheckBox(
						Main.getUserSettings(),
						"synchronizer.sync_reminder_field",
						Translations.getString("configuration.synchronization.sync_reminder_field"))));
		
		if (!this.welcome) {
			this.addField(new ConfigurationField(
					"SEPARATOR_5",
					null,
					new ConfigurationFieldType.Separator()));
			
			this.addField(new ConfigurationField(
					"SYNCHRONIZE_ALL_LABEL",
					null,
					new ConfigurationFieldType.Label(
							Translations.getString(
									"configuration.synchronization.synchronize_all",
									SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName()))));
			
			ActionSynchronizeAll actionSynchronizeAll = new ActionSynchronizeAll(
					this.getConfigurationGroup());
			
			this.addField(new ConfigurationField(
					"SYNCHRONIZE_ALL",
					null,
					new ConfigurationFieldType.Button(actionSynchronizeAll)));
			
			this.addField(new ConfigurationField(
					"SEPARATOR_6",
					null,
					new ConfigurationFieldType.Separator()));
			
			this.addField(new ConfigurationField(
					"PUSH_ALL_LABEL",
					null,
					new ConfigurationFieldType.Label(
							Translations.getString(
									"configuration.synchronization.push_all",
									SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName(),
									SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName()))));
			
			ActionSynchronizePushAll actionPushAll = new ActionSynchronizePushAll(
					this.getConfigurationGroup());
			
			this.addField(new ConfigurationField(
					"PUSH_ALL",
					null,
					new ConfigurationFieldType.Button(actionPushAll)));
			
			this.addField(new ConfigurationField(
					"SEPARATOR_7",
					null,
					new ConfigurationFieldType.Separator()));
			
			this.addField(new ConfigurationField(
					"RESET_ALL_LABEL",
					null,
					new ConfigurationFieldType.Label(
							Translations.getString(
									"configuration.synchronization.reset_all",
									SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName()))));
			
			ActionSynchronizeResetAll actionResetAll = new ActionSynchronizeResetAll(
					this.getConfigurationGroup());
			
			this.addField(new ConfigurationField(
					"RESET_ALL",
					null,
					new ConfigurationFieldType.Button(actionResetAll)));
			
			this.addField(new ConfigurationField(
					"SEPARATOR_8",
					null,
					new ConfigurationFieldType.Separator()));
			
			this.addField(new ConfigurationField(
					"COPY_ALL_LABEL",
					null,
					new ConfigurationFieldType.Label(
							Translations.getString(
									"configuration.synchronization.copy_all",
									SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName(),
									SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName()))));
			
			ActionSynchronizeCopyAll actionCopyAll = new ActionSynchronizeCopyAll(
					this.getConfigurationGroup());
			
			this.addField(new ConfigurationField(
					"COPY_ALL",
					null,
					new ConfigurationFieldType.Button(actionCopyAll)));
		}
		
		Main.getUserSettings().addPropertyChangeListener(
				"plugin.synchronizer.id",
				new WeakPropertyChangeListener(Main.getUserSettings(), this));
	}
	
	private void disableFields() {
		// Disable fields for DUMMY service
		boolean enabled = !SynchronizerUtils.getSynchronizerPlugin().getId().equals(
				DummyGuiPlugin.getInstance().getId());
		
		if (this.containsId("CONFIGURE_PLUGIN"))
			this.setEnabled("CONFIGURE_PLUGIN", enabled);
		
		if (this.containsId("CHOICE"))
			this.setEnabled("CHOICE", enabled);
		
		if (this.containsId("KEEP"))
			this.setEnabled("KEEP", enabled);
		
		if (this.containsId("SCHEDULER_SLEEP_TIME"))
			this.setEnabled("SCHEDULER_SLEEP_TIME", enabled);
		
		if (this.containsId("SYNC_START"))
			this.setEnabled("SYNC_START", enabled);
		
		if (this.containsId("SYNC_EXIT"))
			this.setEnabled("SYNC_EXIT", enabled);
		
		if (this.containsId("SYNC_REMINDER_FIELD"))
			this.setEnabled("SYNC_REMINDER_FIELD", enabled);
		
		if (this.containsId("SYNCHRONIZE_ALL"))
			this.setEnabled("SYNCHRONIZE_ALL", enabled);
		
		if (this.containsId("PUSH_ALL"))
			this.setEnabled("PUSH_ALL", enabled);
		
		if (this.containsId("RESET_ALL"))
			this.setEnabled("RESET_ALL", enabled);
		
		if (this.containsId("COPY_ALL"))
			this.setEnabled("COPY_ALL", enabled);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		this.disableFields();
		
		String apiName = SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName();
		
		JButton managePluginsButton = (JButton) this.getField("MANAGE_PLUGINS").getType().getFieldComponent();
		
		managePluginsButton.setText(apiName);
		
		if (!this.welcome) {
			JLabel synchronizeAllLabel = (JLabel) this.getField(
					"SYNCHRONIZE_ALL_LABEL").getType().getFieldComponent();
			
			JLabel pushAllLabel = (JLabel) this.getField("PUSH_ALL_LABEL").getType().getFieldComponent();
			
			JLabel resetAllLabel = (JLabel) this.getField("RESET_ALL_LABEL").getType().getFieldComponent();
			
			JLabel copyAllLabel = (JLabel) this.getField("COPY_ALL_LABEL").getType().getFieldComponent();
			
			synchronizeAllLabel.setText(Translations.getString(
					"configuration.synchronization.synchronize_all",
					apiName));
			
			pushAllLabel.setText(Translations.getString(
					"configuration.synchronization.push_all",
					apiName,
					apiName));
			
			resetAllLabel.setText(Translations.getString(
					"configuration.synchronization.reset_all",
					apiName));
			
			copyAllLabel.setText(Translations.getString(
					"configuration.synchronization.copy_all",
					apiName,
					apiName));
		}
	}
	
}
