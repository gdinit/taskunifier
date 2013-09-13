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
package com.leclercb.taskunifier.gui.api.searchers.filters;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.ListChangeSupport;
import com.leclercb.commons.api.event.listchange.ListChangeSupported;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupport;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.gui.translations.TranslationsUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public abstract class Filter<M extends Model, F extends Filter<M, F, FE>, FE extends FilterElement<M, F, FE>> implements ListChangeListener, PropertyChangeListener, ListChangeSupported, PropertyChangeSupported {
	
	public static final String PROP_LINK = "link";
	
	@XStreamOmitField
	private transient ListChangeSupport listChangeSupport;
	
	@XStreamOmitField
	private transient PropertyChangeSupport propertyChangeSupport;
	
	@XStreamAlias("parent")
	private F parent;
	
	@XStreamAlias("link")
	private FilterLink link;
	
	@XStreamAlias("filters")
	private List<F> filters;
	
	@XStreamAlias("elements")
	private List<FE> elements;
	
	public Filter() {
		this.listChangeSupport = new ListChangeSupport(this);
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		
		this.setParent(null);
		this.setLink(FilterLink.AND);
		
		this.filters = new ArrayList<F>();
		this.elements = new ArrayList<FE>();
	}
	
	public F getParent() {
		return this.parent;
	}
	
	protected void setParent(F parent) {
		this.parent = parent;
	}
	
	public FilterLink getLink() {
		return this.link;
	}
	
	public void setLink(FilterLink link) {
		CheckUtils.isNotNull(link);
		FilterLink oldLink = this.link;
		this.link = link;
		this.propertyChangeSupport.firePropertyChange(PROP_LINK, oldLink, link);
	}
	
	public int getIndexOf(FE element) {
		return this.elements.indexOf(element);
	}
	
	public int getElementCount() {
		return this.elements.size();
	}
	
	public FE getElement(int index) {
		return this.elements.get(index);
	}
	
	public List<FE> getElements() {
		return new ArrayList<FE>(this.elements);
	}
	
	@SuppressWarnings("unchecked")
	public void addElement(FE element) {
		CheckUtils.isNotNull(element);
		this.elements.add(element);
		
		if (element.getParent() != null) {
			element.getParent().removeElement(element);
		}
		
		element.setParent((F) this);
		element.addPropertyChangeListener(this);
		int index = this.elements.indexOf(element);
		this.listChangeSupport.fireListChange(
				ListChangeEvent.VALUE_ADDED,
				index,
				element);
	}
	
	public void removeElement(FE element) {
		CheckUtils.isNotNull(element);
		
		int index = this.elements.indexOf(element);
		if (this.elements.remove(element)) {
			element.setParent(null);
			element.removePropertyChangeListener(this);
			this.listChangeSupport.fireListChange(
					ListChangeEvent.VALUE_REMOVED,
					index,
					element);
		}
	}
	
	public void clearElement() {
		List<FE> elements = this.getElements();
		for (FE element : elements) {
			this.removeElement(element);
		}
	}
	
	public int getIndexOf(F filter) {
		return this.filters.indexOf(filter);
	}
	
	public int getFilterCount() {
		return this.filters.size();
	}
	
	public F getFilter(int index) {
		return this.filters.get(index);
	}
	
	public List<F> getFilters() {
		return new ArrayList<F>(this.filters);
	}
	
	@SuppressWarnings("unchecked")
	public void addFilter(F filter) {
		CheckUtils.isNotNull(filter);
		this.filters.add(filter);
		
		if (filter.getParent() != null) {
			filter.getParent().removeFilter(filter);
		}
		
		filter.setParent((F) this);
		filter.addListChangeListener(this);
		filter.addPropertyChangeListener(this);
		int index = this.filters.indexOf(filter);
		this.listChangeSupport.fireListChange(
				ListChangeEvent.VALUE_ADDED,
				index,
				filter);
	}
	
	public void removeFilter(F filter) {
		CheckUtils.isNotNull(filter);
		
		int index = this.filters.indexOf(filter);
		if (this.filters.remove(filter)) {
			filter.setParent(null);
			filter.removeListChangeListener(this);
			filter.removePropertyChangeListener(this);
			this.listChangeSupport.fireListChange(
					ListChangeEvent.VALUE_REMOVED,
					index,
					filter);
		}
	}
	
	public void clearFilters() {
		List<F> filters = this.getFilters();
		for (F filter : filters) {
			this.removeFilter(filter);
		}
	}
	
	public boolean include(M model, M comparedModel) {
		if (this.link == FilterLink.AND) {
			for (FE element : this.elements) {
				if (!element.include(model, comparedModel))
					return false;
			}
			
			for (F filter : this.filters) {
				if (!filter.include(model, comparedModel))
					return false;
			}
			
			return true;
		} else {
			if (this.getElementCount() == 0 && this.getFilterCount() == 0)
				return true;
			
			for (FE element : this.elements) {
				if (element.include(model, comparedModel))
					return true;
			}
			
			for (F filter : this.filters) {
				if (filter.include(model, comparedModel))
					return true;
			}
			
			return false;
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
	public void listChange(ListChangeEvent event) {
		this.listChangeSupport.fireListChange(event);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		this.propertyChangeSupport.firePropertyChange(event);
	}
	
	@Override
	public String toString() {
		if (this.elements.size() == 0 && this.filters.size() == 0)
			return "";
		
		StringBuffer buffer = new StringBuffer();
		
		if (this.elements.size() != 0) {
			buffer.append(StringUtils.join(this.elements, " "
					+ TranslationsUtils.translateFilterLink(this.link)
					+ " "));
		}
		
		if (this.elements.size() != 0 && this.filters.size() != 0) {
			buffer.append(" "
					+ TranslationsUtils.translateFilterLink(this.link)
					+ " ");
		}
		
		if (this.filters.size() != 0) {
			buffer.append("(");
			buffer.append(StringUtils.join(this.filters, " "
					+ TranslationsUtils.translateFilterLink(this.link)
					+ " "));
			buffer.append(")");
		}
		
		return buffer.toString();
	}
	
}
