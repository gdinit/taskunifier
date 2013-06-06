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
package com.leclercb.taskunifier.gui.api.accessor;

import java.util.ArrayList;
import java.util.List;

import com.leclercb.commons.api.utils.EqualsUtils;

public class PropertyAccessorList<T> {
	
	private String noteAccessorName;
	private List<PropertyAccessor<T>> list;
	
	public PropertyAccessorList() {
		this(null);
	}
	
	public PropertyAccessorList(String noteAccessorName) {
		this.noteAccessorName = noteAccessorName;
		this.list = new ArrayList<PropertyAccessor<T>>();
	}
	
	public void add(PropertyAccessor<T> accessor) {
		if (this.list.contains(accessor))
			throw new IllegalArgumentException("This accessor already exists");
		
		this.list.add(accessor);
	}
	
	public PropertyAccessor<T> get(String id) {
		for (PropertyAccessor<T> accessor : this.list) {
			if (EqualsUtils.equals(accessor.getId(), id))
				return accessor;
		}
		
		return null;
	}
	
	public PropertyAccessor<T> parsePropertyName(String propertyName) {
		if (propertyName == null)
			return null;
		
		for (PropertyAccessor<T> accessor : this.list) {
			if (EqualsUtils.equals(accessor.getPropertyName(), propertyName))
				return accessor;
		}
		
		return null;
	}
	
	public int getSize() {
		return this.list.size();
	}
	
	public int indexOf(PropertyAccessor<T> accessor) {
		return this.list.indexOf(accessor);
	}
	
	public PropertyAccessor<T> getAccessor(int index) {
		return this.list.get(index);
	}
	
	public List<PropertyAccessor<T>> getAccessors() {
		return new ArrayList<PropertyAccessor<T>>(this.list);
	}
	
	public List<PropertyAccessor<T>> getUsableAccessors() {
		return this.getUsableAccessors(true);
	}
	
	public List<PropertyAccessor<T>> getUsableAccessors(boolean includeNote) {
		List<PropertyAccessor<T>> list = new ArrayList<PropertyAccessor<T>>();
		
		for (PropertyAccessor<T> accessor : this.list) {
			if (accessor.isUsable())
				list.add(accessor);
		}
		
		if (!includeNote && this.noteAccessorName != null)
			list.remove(this.get(this.noteAccessorName));
		
		return list;
	}
	
	public List<PropertyAccessor<T>> getUsedAccessors() {
		return this.getUsedAccessors(true);
	}
	
	public List<PropertyAccessor<T>> getUsedAccessors(boolean includeNote) {
		List<PropertyAccessor<T>> list = new ArrayList<PropertyAccessor<T>>();
		
		for (PropertyAccessor<T> accessor : this.list) {
			if (accessor.isUsable() && accessor.isUsed())
				list.add(accessor);
		}
		
		if (!includeNote && this.noteAccessorName != null)
			list.remove(this.get(this.noteAccessorName));
		
		return list;
	}
	
	public List<PropertyAccessor<T>> getGroupableAccessors() {
		List<PropertyAccessor<T>> list = new ArrayList<PropertyAccessor<T>>();
		
		for (PropertyAccessor<T> accessor : this.list) {
			if (accessor.getType().isGroupable())
				list.add(accessor);
		}
		
		return list;
	}
	
}
