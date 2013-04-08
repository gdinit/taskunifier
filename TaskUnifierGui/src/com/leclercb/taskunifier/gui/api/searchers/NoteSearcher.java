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
package com.leclercb.taskunifier.gui.api.searchers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupport;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.templates.NoteTemplate;
import com.leclercb.taskunifier.gui.api.searchers.filters.NoteFilter;
import com.leclercb.taskunifier.gui.api.searchers.filters.NoteFilterElement;
import com.leclercb.taskunifier.gui.api.searchers.sorters.NoteSorter;
import com.leclercb.taskunifier.gui.api.searchers.sorters.NoteSorterElement;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class NoteSearcher implements Cloneable, PropertyChangeSupported, ListChangeListener, PropertyChangeListener {
	
	public static final String PROP_TYPE = "type";
	public static final String PROP_FOLDER = "folder";
	public static final String PROP_ORDER = "order";
	public static final String PROP_TITLE = "title";
	public static final String PROP_ICON = "icon";
	public static final String PROP_FILTER = "filter";
	public static final String PROP_SORTER = "sorter";
	public static final String PROP_TEMPLATE = "template";
	
	@XStreamOmitField
	private transient PropertyChangeSupport propertyChangeSupport;
	
	@XStreamAlias("id")
	private String id;
	
	@XStreamAlias("type")
	private NoteSearcherType type;
	
	@XStreamAlias("folder")
	private String folder;
	
	@XStreamAlias("order")
	private int order;
	
	@XStreamAlias("title")
	private String title;
	
	@XStreamAlias("icon")
	private String icon;
	
	@XStreamAlias("filter")
	private NoteFilter filter;
	
	@XStreamAlias("sorter")
	private NoteSorter sorter;
	
	@XStreamAlias("template")
	private NoteTemplate template;
	
	public NoteSearcher(
			NoteSearcherType type,
			String folder,
			int order,
			String title,
			NoteFilter filter,
			NoteSorter sorter) {
		this(type, folder, order, title, null, filter, sorter);
	}
	
	public NoteSearcher(
			NoteSearcherType type,
			String folder,
			int order,
			String title,
			String icon,
			NoteFilter filter,
			NoteSorter sorter) {
		this(type, folder, order, title, icon, filter, sorter, null);
	}
	
	public NoteSearcher(
			NoteSearcherType type,
			String folder,
			int order,
			String title,
			String icon,
			NoteFilter filter,
			NoteSorter sorter,
			NoteTemplate template) {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		
		this.setId(UUID.randomUUID().toString());
		this.setType(type);
		this.setFolder(folder);
		this.setOrder(order);
		this.setTitle(title);
		this.setIcon(icon);
		this.setFilter(filter);
		this.setSorter(sorter);
		this.setTemplate(template);
	}
	
	@Override
	public NoteSearcher clone() {
		return new NoteSearcher(
				this.type,
				this.folder,
				this.order,
				this.title,
				this.icon,
				this.filter.clone(),
				this.sorter.clone(),
				this.template);
	}
	
	public String getId() {
		return this.id;
	}
	
	private void setId(String id) {
		CheckUtils.isNotNull(id);
		this.id = id;
	}
	
	public NoteSearcherType getType() {
		return this.type;
	}
	
	public void setType(NoteSearcherType type) {
		CheckUtils.isNotNull(type);
		
		this.setFolder(null);
		
		NoteSearcherType oldType = this.type;
		this.type = type;
		this.propertyChangeSupport.firePropertyChange(PROP_TYPE, oldType, type);
	}
	
	public String[] getFolders() {
		return getFolders(this.folder);
	}
	
	public String getFolder() {
		return this.folder;
	}
	
	public void setFolder(String folder) {
		if (folder == null)
			folder = "";
		
		String oldFolder = this.folder;
		this.folder = folder;
		this.propertyChangeSupport.firePropertyChange(
				PROP_FOLDER,
				oldFolder,
				folder);
	}
	
	public int getOrder() {
		return this.order;
	}
	
	public void setOrder(int order) {
		int oldOrder = this.order;
		this.order = order;
		this.propertyChangeSupport.firePropertyChange(
				PROP_ORDER,
				oldOrder,
				order);
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		CheckUtils.isNotNull(title);
		String oldTitle = this.title;
		this.title = title;
		this.propertyChangeSupport.firePropertyChange(
				PROP_TITLE,
				oldTitle,
				title);
	}
	
	public String getIcon() {
		return this.icon;
	}
	
	public void setIcon(String icon) {
		String oldIcon = this.icon;
		this.icon = icon;
		this.propertyChangeSupport.firePropertyChange(PROP_ICON, oldIcon, icon);
	}
	
	public NoteFilter getFilter() {
		return this.filter;
	}
	
	public void setFilter(NoteFilter filter) {
		CheckUtils.isNotNull(filter);
		
		if (this.filter != null) {
			this.filter.removeListChangeListener(this);
			this.filter.removePropertyChangeListener(this);
		}
		
		NoteFilter oldFilter = this.filter;
		this.filter = filter;
		
		this.filter.addListChangeListener(this);
		this.filter.addPropertyChangeListener(this);
		
		this.propertyChangeSupport.firePropertyChange(
				PROP_FILTER,
				oldFilter,
				filter);
	}
	
	public NoteSorter getSorter() {
		return this.sorter;
	}
	
	public void setSorter(NoteSorter sorter) {
		CheckUtils.isNotNull(sorter);
		
		if (this.sorter != null) {
			this.sorter.removeListChangeListener(this);
			this.sorter.removePropertyChangeListener(this);
		}
		
		NoteSorter oldSorter = this.sorter;
		this.sorter = sorter;
		
		this.sorter.addListChangeListener(this);
		this.sorter.addPropertyChangeListener(this);
		
		this.propertyChangeSupport.firePropertyChange(
				PROP_SORTER,
				oldSorter,
				sorter);
	}
	
	public NoteTemplate getTemplate() {
		return this.template;
	}
	
	public void setTemplate(NoteTemplate template) {
		NoteTemplate oldTemplate = this.template;
		this.template = template;
		this.propertyChangeSupport.firePropertyChange(
				PROP_TEMPLATE,
				oldTemplate,
				template);
	}
	
	@Override
	public String toString() {
		return this.title;
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	@Override
	public void addPropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(
				propertyName,
				listener);
	}
	
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	@Override
	public void removePropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(
				propertyName,
				listener);
	}
	
	@Override
	public void listChange(ListChangeEvent evt) {
		this.fireChange(evt.getSource());
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		this.fireChange(evt.getSource());
	}
	
	private void fireChange(Object source) {
		if (source instanceof NoteFilter || source instanceof NoteFilterElement) {
			this.propertyChangeSupport.firePropertyChange(
					PROP_FILTER,
					null,
					this.filter);
		}
		
		if (source instanceof NoteSorter || source instanceof NoteSorterElement) {
			this.propertyChangeSupport.firePropertyChange(
					PROP_SORTER,
					null,
					this.sorter);
		}
	}
	
	public static String getFolder(String[] folders) {
		StringBuffer buffer = new StringBuffer();
		
		for (String folder : folders)
			buffer.append(folder + "/");
		
		if (folders.length != 0)
			buffer.delete(buffer.length() - 1, buffer.length());
		
		return buffer.toString();
	}
	
	public static String[] getFolders(String folder) {
		if (folder == null)
			return new String[0];
		
		String[] folders = folder.split("/");
		
		List<String> list = new ArrayList<String>();
		for (String f : folders) {
			if (f.trim().length() != 0)
				list.add(f.trim());
		}
		
		return list.toArray(new String[0]);
	}
	
}
