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
package com.leclercb.taskunifier.gui.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.properties.events.WeakSavePropertiesListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.main.Main;

public final class TaskStatusList implements PropertyChangeListener, SavePropertiesListener {
	
	private static TaskStatusList INSTANCE;
	
	public static TaskStatusList getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TaskStatusList();
		
		return INSTANCE;
	}
	
	private EventList<String> statuses;
	
	public TaskStatusList() {
		this.statuses = new BasicEventList<String>();
		
		this.initialize();
		
		Main.getUserSettings().addPropertyChangeListener(
				"plugin.synchronizer.id",
				new WeakPropertyChangeListener(Main.getUserSettings(), this));
		
		Main.getUserSettings().addSavePropertiesListener(
				new WeakSavePropertiesListener(Main.getUserSettings(), this));
	}
	
	private void initialize() {
		for (String status : this.getStatuses()) {
			this.removeStatus(status);
		}
		
		String[] statuses = SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getStatusValues();
		
		if (statuses == null) {
			String value = Main.getSettings().getStringProperty("taskstatuses");
			statuses = value.split(";");
		}
		
		for (String status : statuses) {
			this.addStatus(status);
		}
	}
	
	public EventList<String> getEventList() {
		return GlazedLists.readOnlyList(this.statuses);
	}
	
	public boolean isEditable() {
		return SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getStatusValues() == null;
	}
	
	public List<String> getStatuses() {
		return new ArrayList<String>(this.statuses);
	}
	
	public void addStatus(String status) {
		CheckUtils.isNotNull(status);
		
		if (this.statuses.contains(status))
			return;
		
		this.statuses.add(status);
	}
	
	public void removeStatus(String status) {
		CheckUtils.isNotNull(status);
		this.statuses.remove(status);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		this.initialize();
	}
	
	@Override
	public void saveProperties() {
		Main.getSettings().setStringProperty(
				"taskstatuses",
				StringUtils.join(this.statuses, ";"));
	}
	
}
