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
package com.leclercb.taskunifier.gui.components.tasktasks.table.highlighters;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.UIManager;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.components.tasktasks.TaskTasksColumn;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.utils.TaskUtils;

public class TaskAlternateHighlighter extends AbstractHighlighter {
	
	private boolean byImportance;
	private Color even = null;
	private Color odd = null;
	
	public TaskAlternateHighlighter() {
		super();
		
		this.resetColors();
		
		Main.getSettings().addPropertyChangeListener(
				new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getPropertyName().startsWith(
								"theme.color.importance.")
								|| evt.getPropertyName().equals(
										"theme.color.enabled")
								|| evt.getPropertyName().equals(
										"theme.color.even")
								|| evt.getPropertyName().equals(
										"theme.color.odd")) {
							TaskAlternateHighlighter.this.resetColors();
							TaskAlternateHighlighter.this.fireStateChanged();
						}
					}
					
				});
	}
	
	@Override
	protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
		if (adapter.isSelected())
			return renderer;
		
		if (this.byImportance)
			return this.doImportanceHighlight(renderer, adapter);
		else
			return this.doAlternateHighlight(renderer, adapter);
	}
	
	private Component doAlternateHighlight(
			Component renderer,
			ComponentAdapter adapter) {
		Color color = null;
		
		if (adapter.row % 2 == 0)
			color = this.even;
		else
			color = this.odd;
		
		renderer.setBackground(color);
		
		return renderer;
	}
	
	private Component doImportanceHighlight(
			Component renderer,
			ComponentAdapter adapter) {
		Object value = adapter.getFilteredValueAt(
				adapter.row,
				adapter.getColumnIndex(TaskTasksColumn.TASK));
		
		if (value == null || !(value instanceof Task))
			return renderer;
		
		int importance = TaskUtils.getImportance((Task) value);
		
		if (((Task) value).isCompleted()) {
			renderer.setBackground(Color.WHITE);
		} else {
			Color color = Main.getSettings().getColorProperty(
					"theme.color.importance." + importance);
			
			renderer.setBackground(color);
		}
		
		return renderer;
	}
	
	private void resetColors() {
		if (Main.getSettings().getBooleanProperty(
				"theme.color.importance.enabled"))
			this.byImportance = true;
		else
			this.byImportance = false;
		
		if (Main.getSettings().getBooleanProperty("theme.color.enabled")) {
			this.even = Main.getSettings().getColorProperty("theme.color.even");
			this.odd = Main.getSettings().getColorProperty("theme.color.odd");
		} else {
			this.even = UIManager.getColor("Table.background");
			this.odd = UIManager.getColor("Table.background");
		}
	}
	
}
