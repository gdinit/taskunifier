/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo;

import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.gui.actions.ActionCreateAccount;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationField;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.DefaultConfigurationPanel;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.toodledo.configuration.fields.ToodledoEmailFieldType;
import com.leclercb.taskunifier.plugin.toodledo.configuration.fields.ToodledoEnableSSLFieldType;
import com.leclercb.taskunifier.plugin.toodledo.configuration.fields.ToodledoPasswordFieldType;

public class ToodledoConfigurationPanel extends DefaultConfigurationPanel {
	
	public ToodledoConfigurationPanel(
			ConfigurationGroup configuration,
			boolean welcome) {
		super(configuration, "plugin_toodledo_configuration");
		this.initialize(welcome);
		this.pack();
	}
	
	private void initialize(boolean welcome) {
		this.addField(new ConfigurationField(
				"EMAIL",
				PluginApi.getTranslation("general.email"),
				new ToodledoEmailFieldType()));
		
		this.addField(new ConfigurationField(
				"PASSWORD",
				PluginApi.getTranslation("general.password"),
				new ToodledoPasswordFieldType()));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_1",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"CREATE_ACCOUNT_LABEL",
				null,
				new ConfigurationFieldType.Label(PluginApi.getTranslation(
						"configuration.synchronization.create_account",
						ToodledoApi.getInstance().getApiName()))));
		
		this.addField(new ConfigurationField(
				"CREATE_ACCOUNT",
				null,
				new ConfigurationFieldType.Button(new ActionCreateAccount(
						22,
						22) {
					
					@Override
					public void createAccount() {
						ToodledoConfigurationPanel.this.saveAndApplyConfig();
						
						String email = PluginApi.getUserSettings().getStringProperty(
								"toodledo.email");
						String password = PluginApi.getUserSettings().getStringProperty(
								"toodledo.password");
						
						try {
							if (email == null)
								throw new Exception(
										PluginApi.getTranslation("error.empty_email"));
							
							if (password == null)
								throw new Exception(
										PluginApi.getTranslation("error.empty_password"));
							
							PluginApi.initializeProxy(PluginApi.getPlugin(ToodledoPlugin.ID));
							ToodledoApi.getInstance().createAccount(
									email,
									password);
							
							JOptionPane.showMessageDialog(
									PluginApi.getCurrentWindow(),
									PluginApi.getTranslation("action.create_account.account_created"),
									PluginApi.getTranslation("general.information"),
									JOptionPane.INFORMATION_MESSAGE);
						} catch (final SynchronizerException e) {
							ErrorInfo info = new ErrorInfo(
									PluginApi.getTranslation("general.error"),
									e.getMessage(),
									null,
									null,
									(e.isExpected() ? e : null),
									null,
									null);
							
							JXErrorPane.showDialog(
									PluginApi.getCurrentWindow(),
									info);
							
							return;
						} catch (Exception e) {
							ErrorInfo info = new ErrorInfo(
									PluginApi.getTranslation("general.error"),
									e.getMessage(),
									null,
									null,
									e,
									null,
									null);
							
							JXErrorPane.showDialog(
									PluginApi.getCurrentWindow(),
									info);
							
							return;
						}
					}
					
				})));
		
		this.addField(new ConfigurationField(
				"ENABLE_SSL",
				null,
				new ToodledoEnableSSLFieldType()));
	}
	
}
