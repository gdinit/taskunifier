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
package com.leclercb.taskunifier.api.models.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.ListChangeSupport;
import com.leclercb.commons.api.event.listchange.ListChangeSupported;
import com.leclercb.commons.api.utils.IgnoreCaseString;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.api.models.TaskList;
import com.leclercb.taskunifier.api.models.TaskList.TaskItem;

public final class TaskTaskLinkList implements ListChangeSupported, ListChangeListener, PropertyChangeListener {
	
	private static TaskTaskLinkList INSTANCE;
	
	public static TaskTaskLinkList getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TaskTaskLinkList();
		
		return INSTANCE;
	}
	
	private ListChangeSupport listChangeSupport;
	
	private List<IgnoreCaseString> links;
	private SortedSet<IgnoreCaseString> sortedLinks;
	
	private TaskTaskLinkList() {
		this.listChangeSupport = new ListChangeSupport(this);
		this.links = new ArrayList<IgnoreCaseString>();
		this.sortedLinks = new TreeSet<IgnoreCaseString>();
		
		this.initialize();
	}
	
	public int getIndexOf(String link) {
		IgnoreCaseString oLink = new IgnoreCaseString(link);
		
		int index = 0;
		for (IgnoreCaseString l : this.sortedLinks) {
			if (l.equals(oLink))
				return index;
			
			index++;
		}
		
		return -1;
	}
	
	public int getLinkCount() {
		return this.sortedLinks.size();
	}
	
	public String getLink(int index) {
		return this.getLinks()[index];
	}
	
	public String[] getLinks() {
		List<String> links = new ArrayList<String>();
		for (IgnoreCaseString link : this.sortedLinks)
			links.add(link.toString());
		
		return links.toArray(new String[0]);
	}
	
	private List<IgnoreCaseString> getLinks(TaskList tasks) {
		List<IgnoreCaseString> links = new ArrayList<IgnoreCaseString>();
		for (TaskItem item : tasks)
			if (item.getLink() != null)
				links.add(new IgnoreCaseString(item.getLink()));
		
		return links;
	}
	
	private void initialize() {
		List<Task> tasks = TaskFactory.getInstance().getList();
		
		for (Task task : tasks) {
			if (!task.getModelStatus().isEndUserStatus()) {
				continue;
			}
			
			task.getTasks().addListChangeListener(this);
			task.getTasks().addPropertyChangeListener(this);
			
			this.links.addAll(this.getLinks(task.getTasks()));
			this.sortedLinks.addAll(this.links);
		}
		
		TaskFactory.getInstance().addListChangeListener(this);
		TaskFactory.getInstance().addPropertyChangeListener(this);
	}
	
	@Override
	public void listChange(ListChangeEvent evt) {
		if (evt.getValue() instanceof Task) {
			Task task = (Task) evt.getValue();
			
			if (evt.getChangeType() == ListChangeEvent.VALUE_ADDED) {
				if (task.getModelStatus().isEndUserStatus()) {
					task.getTasks().addListChangeListener(this);
					task.getTasks().addPropertyChangeListener(this);
					this.addLinks(task.getTasks());
				}
			}
			
			if (evt.getChangeType() == ListChangeEvent.VALUE_REMOVED) {
				task.getTasks().removeListChangeListener(this);
				task.getTasks().removePropertyChangeListener(this);
				this.removeLinks(task.getTasks());
			}
		}
		
		if (evt.getValue() instanceof TaskItem) {
			TaskItem item = (TaskItem) evt.getValue();
			
			if (evt.getChangeType() == ListChangeEvent.VALUE_ADDED) {
				this.addLink(item.getLink());
			}
			
			if (evt.getChangeType() == ListChangeEvent.VALUE_REMOVED) {
				this.removeLink(item.getLink());
			}
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof Task) {
			Task task = (Task) evt.getSource();
			
			if (evt.getPropertyName().equals(BasicModel.PROP_MODEL_STATUS)) {
				ModelStatus oldStatus = (ModelStatus) evt.getOldValue();
				ModelStatus newStatus = (ModelStatus) evt.getNewValue();
				
				if (!oldStatus.isEndUserStatus() && newStatus.isEndUserStatus()) {
					task.getTasks().addListChangeListener(this);
					task.getTasks().addPropertyChangeListener(this);
					this.addLinks(task.getTasks());
				} else if (oldStatus.isEndUserStatus()
						&& !newStatus.isEndUserStatus()) {
					task.getTasks().removeListChangeListener(this);
					task.getTasks().removePropertyChangeListener(this);
					this.removeLinks(task.getTasks());
				}
			}
		}
		
		if (evt.getSource() instanceof TaskItem) {
			if (evt.getPropertyName().equals(TaskItem.PROP_LINK)) {
				String oldLink = (String) evt.getOldValue();
				String newLink = (String) evt.getNewValue();
				
				this.removeLink(oldLink);
				this.addLink(newLink);
			}
		}
	}
	
	private void addLinks(TaskList tasks) {
		for (TaskItem item : tasks) {
			this.addLink(item.getLink());
		}
	}
	
	private void addLink(String link) {
		if (link == null)
			return;
		
		IgnoreCaseString oLink = new IgnoreCaseString(link);
		this.links.add(oLink);
		
		if (!this.sortedLinks.contains(oLink)) {
			this.sortedLinks.add(oLink);
			
			this.listChangeSupport.fireListChange(
					ListChangeEvent.VALUE_ADDED,
					this.getIndexOf(link),
					link);
		}
	}
	
	private void removeLinks(TaskList tasks) {
		for (TaskItem item : tasks) {
			this.removeLink(item.getLink());
		}
	}
	
	private void removeLink(String link) {
		if (link == null)
			return;
		
		IgnoreCaseString oLink = new IgnoreCaseString(link);
		this.links.remove(oLink);
		
		if (!this.links.contains(oLink)) {
			if (this.sortedLinks.contains(oLink)) {
				int index = this.getIndexOf(link);
				this.sortedLinks.remove(oLink);
				this.listChangeSupport.fireListChange(
						ListChangeEvent.VALUE_REMOVED,
						index,
						link);
			}
		}
	}
	
	@Override
	public void addListChangeListener(ListChangeListener listener) {
		this.listChangeSupport.addListChangeListener(listener);
	}
	
	@Override
	public void removeListChangeListener(ListChangeListener listener) {
		this.listChangeSupport.removeListChangeListener(listener);
	}
	
}
