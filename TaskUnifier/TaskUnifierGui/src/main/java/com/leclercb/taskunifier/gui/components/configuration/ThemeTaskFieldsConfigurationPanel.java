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

import java.util.Collections;
import java.util.List;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.commons.comparators.PropertyAccessorComparator;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationField;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationTab;
import com.leclercb.taskunifier.gui.components.configuration.api.DefaultConfigurationPanel;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;

public class ThemeTaskFieldsConfigurationPanel extends DefaultConfigurationPanel {
	
	public ThemeTaskFieldsConfigurationPanel(ConfigurationGroup configuration) {
		super(
				ConfigurationTab.THEME_TASK_FIELDS,
				configuration,
				"configuration_theme_fields");
		
		this.initialize();
		this.pack();
	}
	
	private void initialize() {
		this.addField(new ConfigurationField(
				"MULTIPLE_CONTEXTS",
				null,
				true,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"theme.task.field.contexts.multiple",
						Translations.getString("configuration.theme.allow_multiple_contexts"))));
		
		this.addField(new ConfigurationField(
				"MULTIPLE_GOALS",
				null,
				true,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"theme.task.field.goals.multiple",
						Translations.getString("configuration.theme.allow_multiple_goals"))));
		
		this.addField(new ConfigurationField(
				"MULTIPLE_LOCATIONS",
				null,
				true,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"theme.task.field.locations.multiple",
						Translations.getString("configuration.theme.allow_multiple_locations"))));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_1",
				null,
				new ConfigurationFieldType.Separator()));
		
		List<PropertyAccessor<Task>> columns = TaskColumnList.getInstance().getUsableAccessors();
		
		Collections.sort(columns, PropertyAccessorComparator.INSTANCE);
		
		for (PropertyAccessor<Task> column : columns) {
			this.addField(new ConfigurationField(
					column.getId(),
					null,
					new ConfigurationFieldType.CheckBox(
							Main.getSettings(),
							"task.field."
									+ column.getId().toLowerCase()
									+ ".used",
							column.getLabel())));
		}
	}
	
}
