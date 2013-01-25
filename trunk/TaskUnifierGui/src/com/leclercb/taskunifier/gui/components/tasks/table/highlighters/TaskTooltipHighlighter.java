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
package com.leclercb.taskunifier.gui.components.tasks.table.highlighters;

import java.awt.Component;
import java.util.Calendar;

import javax.swing.JComponent;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.ToolTipHighlighter;

import com.leclercb.commons.api.utils.DateUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.Timer;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.commons.values.StringValuePercentage;
import com.leclercb.taskunifier.gui.commons.values.StringValueTime;
import com.leclercb.taskunifier.gui.commons.values.StringValueTimer;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;
import com.leclercb.taskunifier.gui.translations.Translations;

public class TaskTooltipHighlighter extends ToolTipHighlighter {
	
	public TaskTooltipHighlighter(HighlightPredicate predicate) {
		super(predicate);
	}
	
	@Override
	protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
		PropertyAccessor<Task> column = (PropertyAccessor<Task>) adapter.getColumnIdentifierAt(adapter.convertColumnIndexToModel(adapter.column));
		
		if (EqualsUtils.equals(
				column,
				TaskColumnList.getInstance().get(TaskColumnList.PROGRESS)))
			return this.doHighlightProgress(renderer, adapter);
		
		if (EqualsUtils.equals(
				column,
				TaskColumnList.getInstance().get(TaskColumnList.LENGTH)))
			return this.doHighlightLength(renderer, adapter);
		
		if (EqualsUtils.equals(
				column,
				TaskColumnList.getInstance().get(TaskColumnList.TIMER)))
			return this.doHighlightTimer(renderer, adapter);
		
		if (EqualsUtils.equals(
				column,
				TaskColumnList.getInstance().get(TaskColumnList.START_DATE)))
			return this.doHighlightDate(renderer, adapter, column);
		
		if (EqualsUtils.equals(
				column,
				TaskColumnList.getInstance().get(TaskColumnList.DUE_DATE)))
			return this.doHighlightDate(renderer, adapter, column);
		
		return this.doHighlightString(renderer, adapter, column);
	}
	
	protected Component doHighlightString(
			Component renderer,
			ComponentAdapter adapter,
			PropertyAccessor<Task> column) {
		Object value = adapter.getFilteredValueAt(
				adapter.row,
				adapter.getColumnIndex(TaskColumnList.getInstance().get(
						TaskColumnList.MODEL)));
		
		if (value == null || !(value instanceof Task))
			return renderer;
		
		final Task task = (Task) value;
		
		String toolTip = column.getPropertyAsString(task);
		
		if (toolTip != null && toolTip.trim().length() != 0)
			((JComponent) renderer).setToolTipText(toolTip.trim());
		
		return renderer;
	}
	
	protected Component doHighlightProgress(
			Component renderer,
			ComponentAdapter adapter) {
		Object value = adapter.getFilteredValueAt(
				adapter.row,
				adapter.getColumnIndex(TaskColumnList.getInstance().get(
						TaskColumnList.MODEL)));
		
		if (value == null || !(value instanceof Task))
			return renderer;
		
		final Task task = (Task) value;
		
		int nbChildren = 0;
		double progress = 0;
		for (Task child : task.getAllChildren()) {
			if (!child.getModelStatus().isEndUserStatus())
				continue;
			
			nbChildren++;
			
			if (child.isCompleted())
				progress += 1;
			else
				progress += child.getProgress();
		}
		
		String tooltip = null;
		
		if (nbChildren > 0)
			tooltip = String.format(
					"%1s (%2s: %3s)",
					StringValuePercentage.INSTANCE.getString(task.getProgress()),
					Translations.getString("general.subtasks"),
					StringValuePercentage.INSTANCE.getString(progress
							/ nbChildren));
		else
			tooltip = StringValuePercentage.INSTANCE.getString(task.getProgress());
		
		((JComponent) renderer).setToolTipText(tooltip);
		
		return renderer;
	}
	
	protected Component doHighlightLength(
			Component renderer,
			ComponentAdapter adapter) {
		Object value = adapter.getFilteredValueAt(
				adapter.row,
				adapter.getColumnIndex(TaskColumnList.getInstance().get(
						TaskColumnList.MODEL)));
		
		if (value == null || !(value instanceof Task))
			return renderer;
		
		final Task task = (Task) value;
		
		boolean atLeastOneChild = false;
		int length = task.getLength();
		for (Task child : task.getAllChildren()) {
			if (!child.getModelStatus().isEndUserStatus())
				continue;
			
			if (child.isCompleted())
				continue;
			
			atLeastOneChild = true;
			length += child.getLength();
		}
		
		String tooltip = null;
		
		if (atLeastOneChild)
			tooltip = String.format(
					"%1s (%2s: %3s)",
					StringValueTime.INSTANCE.getString(task.getLength()),
					Translations.getString("general.total"),
					StringValueTime.INSTANCE.getString(length));
		else
			tooltip = StringValueTime.INSTANCE.getString(task.getLength());
		
		((JComponent) renderer).setToolTipText(tooltip);
		
		return renderer;
	}
	
	protected Component doHighlightTimer(
			Component renderer,
			ComponentAdapter adapter) {
		Object value = adapter.getFilteredValueAt(
				adapter.row,
				adapter.getColumnIndex(TaskColumnList.getInstance().get(
						TaskColumnList.MODEL)));
		
		if (value == null || !(value instanceof Task))
			return renderer;
		
		final Task task = (Task) value;
		
		boolean hasChildren = false;
		long timer = task.getTimer().getTimerValue();
		for (Task child : task.getAllChildren()) {
			if (!child.getModelStatus().isEndUserStatus())
				continue;
			
			if (child.isCompleted())
				continue;
			
			hasChildren = true;
			timer += child.getTimer().getTimerValue();
		}
		
		String tooltip = null;
		
		if (hasChildren)
			tooltip = String.format(
					"%1s (%2s: %3s)",
					StringValueTimer.INSTANCE.getString(task.getTimer()),
					Translations.getString("general.total"),
					StringValueTimer.INSTANCE.getString(new Timer(timer)));
		else
			tooltip = StringValueTimer.INSTANCE.getString(task.getTimer());
		
		((JComponent) renderer).setToolTipText(tooltip);
		
		return renderer;
	}
	
	protected Component doHighlightDate(
			Component renderer,
			ComponentAdapter adapter,
			PropertyAccessor<Task> column) {
		Object value = adapter.getFilteredValueAt(
				adapter.row,
				adapter.getColumnIndex(TaskColumnList.getInstance().get(
						TaskColumnList.MODEL)));
		
		if (value == null || !(value instanceof Task))
			return renderer;
		
		final Task task = (Task) value;
		
		value = column.getProperty(task);
		
		if (value == null || !(value instanceof Calendar))
			return renderer;
		
		String toolTip = column.getPropertyAsString(task);
		
		toolTip = String.format("%1s (%2s)", toolTip, Translations.getString(
				"date.x_days",
				Math.abs((int) DateUtils.getDiffInDays(
						(Calendar) value,
						Calendar.getInstance(),
						false))));
		
		((JComponent) renderer).setToolTipText(toolTip);
		
		return renderer;
	}
	
}
