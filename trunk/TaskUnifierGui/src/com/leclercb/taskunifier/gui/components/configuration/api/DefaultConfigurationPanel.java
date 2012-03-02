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
package com.leclercb.taskunifier.gui.components.configuration.api;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.gui.components.help.Help;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.FormBuilder;

public abstract class DefaultConfigurationPanel extends ConfigurationPanel {
	
	private boolean showAfterRestart;
	private JButton helpButton;
	private List<ConfigurationField> fields;
	
	public DefaultConfigurationPanel(ConfigurationGroup configurationGroup) {
		this(configurationGroup, true, (JButton) null);
	}
	
	public DefaultConfigurationPanel(
			ConfigurationGroup configurationGroup,
			boolean showAfterRestart) {
		this(configurationGroup, showAfterRestart, (JButton) null);
	}
	
	public DefaultConfigurationPanel(
			ConfigurationGroup configurationGroup,
			String helpId) {
		this(configurationGroup, true, helpId);
	}
	
	public DefaultConfigurationPanel(
			ConfigurationGroup configurationGroup,
			boolean showAfterRestart,
			String helpId) {
		this(configurationGroup, showAfterRestart, Help.getHelpButton(helpId));
	}
	
	public DefaultConfigurationPanel(
			ConfigurationGroup configurationGroup,
			boolean showAfterRestart,
			JButton helpButton) {
		super(configurationGroup);
		this.showAfterRestart = showAfterRestart;
		this.helpButton = helpButton;
		this.fields = new ArrayList<ConfigurationField>();
	}
	
	public boolean containsId(String id) {
		ConfigurationField field = this.getField(id);
		
		return (field != null);
	}
	
	public Object getValue(String id) {
		ConfigurationField field = this.getField(id);
		
		if (field == null)
			throw new IllegalArgumentException("Id not found");
		
		return field.getType().getFieldValue();
	}
	
	public void setEnabled(String id, boolean enabled) {
		ConfigurationField field = this.getField(id);
		
		if (field == null)
			throw new IllegalArgumentException("Id not found");
		
		((JComponent) field.getType().getFieldComponent()).setEnabled(enabled);
	}
	
	public ConfigurationField getField(String id) {
		CheckUtils.isNotNull(id);
		
		for (ConfigurationField field : this.fields)
			if (EqualsUtils.equals(id, field.getId()))
				return field;
		
		return null;
	}
	
	public List<ConfigurationField> getFields() {
		return Collections.unmodifiableList(new ArrayList<ConfigurationField>(
				this.fields));
	}
	
	public void addField(ConfigurationField field) {
		CheckUtils.isNotNull(field);
		
		if (this.getField(field.getId()) != null)
			throw new IllegalArgumentException(
					"A field with the same id already exists");
		
		this.fields.add(field);
	}
	
	public void removeField(ConfigurationField field) {
		this.fields.remove(field);
	}
	
	public void pack() {
		this.removeAll();
		this.setLayout(new BorderLayout(0, 5));
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		FormBuilder builder = new FormBuilder(
				"right:pref, 4dlu, fill:default:grow");
		
		JLabel label = null;
		Component component = null;
		
		if (this.helpButton != null) {
			builder.append("", new TUButtonsPanel(this.helpButton));
		}
		
		boolean afterRestartFound = false;
		
		for (ConfigurationField field : this.fields) {
			if (field.getLabel() == null)
				label = new JLabel("");
			else
				label = new JLabel(field.getLabel() + ":");
			
			if (this.showAfterRestart && field.isAfterRestart()) {
				afterRestartFound = true;
				label.setForeground(Color.RED);
			}
			
			field.getType().initializeFieldComponent();
			component = field.getType().getFieldComponent();
			
			builder.append(label);
			builder.append(component);
		}
		
		// Lay out the panel
		this.add(builder.getPanel(), BorderLayout.CENTER);
		
		if (this.showAfterRestart && afterRestartFound) {
			JLabel afterRestartLabel = new JLabel(
					Translations.getString("configuration.general.settings_changed_after_restart"));
			afterRestartLabel.setForeground(Color.RED);
			
			this.add(afterRestartLabel, BorderLayout.SOUTH);
		}
	}
	
	@Override
	public void saveAndApplyConfig() {
		for (ConfigurationField field : this.fields) {
			((ConfigurationFieldType<?, ?>) field.getType()).saveAndApplyConfig();
		}
	}
	
	@Override
	public void cancelConfig() {
		for (ConfigurationField field : this.fields) {
			field.getType().initializeFieldComponent();
		}
	}
	
}
