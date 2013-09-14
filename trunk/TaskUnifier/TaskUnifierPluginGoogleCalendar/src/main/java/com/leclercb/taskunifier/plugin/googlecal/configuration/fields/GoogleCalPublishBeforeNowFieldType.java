/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal.configuration.fields;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class GoogleCalPublishBeforeNowFieldType extends ConfigurationFieldType.ComboBox {
	
	public GoogleCalPublishBeforeNowFieldType() {
		super(
				getValues(),
				PluginApi.getUserSettings(),
				"plugin.googlecal.publish_before_now");
		
		this.setRenderer(new DefaultListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(
					JList list,
					Object value,
					int index,
					boolean isSelected,
					boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(
						list,
						value,
						index,
						isSelected,
						cellHasFocus);
				
				label.setText(PluginApi.getTranslation("date.x_days", value));
				
				return label;
			}
			
		});
	}
	
	@Override
	public Object getPropertyValue() {
		return PluginApi.getUserSettings().getIntegerProperty(
				"plugin.googlecal.publish_before_now",
				7);
	}
	
	@Override
	public void saveAndApplyConfig() {
		PluginApi.getUserSettings().setIntegerProperty(
				"plugin.googlecal.publish_before_now",
				(Integer) this.getFieldValue());
	}
	
	private static Integer[] getValues() {
		return new Integer[] { 7, 15, 30 };
	}
	
}
