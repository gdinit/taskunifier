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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationField;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationTab;
import com.leclercb.taskunifier.gui.components.configuration.api.DefaultConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.fields.proxy.ProxyHostFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.proxy.ProxyLoginFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.proxy.ProxyPasswordFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.proxy.ProxyPortFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.proxy.ProxyTestConnectionFieldType;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;

public class ProxyConfigurationPanel extends DefaultConfigurationPanel {
	
	public ProxyConfigurationPanel(
			ConfigurationGroup configuration,
			boolean showAfterRestart) {
		super(
				ConfigurationTab.PROXY,
				configuration,
				showAfterRestart,
				"configuration_proxy");
		
		this.initialize();
		this.pack();
		this.disableFields();
	}
	
	private void initialize() {
		this.addField(new ConfigurationField(
				"USE_SYSTEM",
				null,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"proxy.use_system_proxies",
						Translations.getString("configuration.proxy.use_system_proxies"))));
		
		final JCheckBox useSystemProxiesField = ((ConfigurationFieldType.CheckBox) this.getField(
				"USE_SYSTEM").getType()).getFieldComponent();
		
		this.addField(new ConfigurationField(
				"ENABLED",
				null,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"proxy.enabled",
						Translations.getString("configuration.proxy.enabled"))));
		
		final JCheckBox proxyEnabledField = ((ConfigurationFieldType.CheckBox) this.getField(
				"ENABLED").getType()).getFieldComponent();
		
		this.addField(new ConfigurationField(
				"HOST",
				Translations.getString("configuration.proxy.host"),
				new ProxyHostFieldType()));
		
		this.addField(new ConfigurationField(
				"PORT",
				Translations.getString("configuration.proxy.port"),
				new ProxyPortFieldType()));
		
		this.addField(new ConfigurationField(
				"LOGIN",
				Translations.getString("configuration.proxy.login"),
				new ProxyLoginFieldType()));
		
		this.addField(new ConfigurationField(
				"PASSWORD",
				Translations.getString("configuration.proxy.password"),
				new ProxyPasswordFieldType()));
		
		this.addField(new ConfigurationField(
				"SEPARATOR",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"TEST_CONNECTION",
				null,
				new ProxyTestConnectionFieldType(this)));
		
		useSystemProxiesField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ProxyConfigurationPanel.this.disableFields();
			}
			
		});
		
		proxyEnabledField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ProxyConfigurationPanel.this.disableFields();
			}
			
		});
	}
	
	private void disableFields() {
		JCheckBox useSystemProxiesField = ((ConfigurationFieldType.CheckBox) this.getField(
				"USE_SYSTEM").getType()).getFieldComponent();
		
		JCheckBox proxyEnabledField = ((ConfigurationFieldType.CheckBox) this.getField(
				"ENABLED").getType()).getFieldComponent();
		
		this.setEnabled("ENABLED", !useSystemProxiesField.isSelected());
		
		boolean selected = !useSystemProxiesField.isSelected()
				&& proxyEnabledField.isSelected();
		
		this.setEnabled("HOST", selected);
		this.setEnabled("PORT", selected);
		this.setEnabled("LOGIN", selected);
		this.setEnabled("PASSWORD", selected);
	}
	
}
