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
package com.leclercb.taskunifier.gui.api.accessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.Model;

public class PropertyAccessorList<M extends Model, PA extends AbstractPropertyAccessor<M>> {
	
	private Map<String, PA> map;
	
	public PropertyAccessorList() {
		this.map = new HashMap<String, PA>();
	}
	
	public void add(String name, PA properties) {
		this.map.put(name, properties);
	}
	
	public PA get(String name) {
		return this.map.get(name);
	}
	
	public PA parsePropertyName(String propertyName) {
		if (propertyName == null)
			return null;
		
		for (PA mp : this.map.values()) {
			if (EqualsUtils.equals(mp.getPropertyName(), propertyName))
				return mp;
		}
		
		return null;
	}
	
	public List<PA> getUsableColumns() {
		return this.getUsedColumns(true);
	}
	
	public List<PA> getUsableColumns(boolean includeNote) {
		List<PA> list = new ArrayList<PA>();
		
		for (PA mp : this.map.values()) {
			if (mp.isUsable())
				list.add(mp);
		}
		
		if (!includeNote)
			list.remove(this.get("NOTE"));
		
		return list;
	}
	
	public List<PA> getUsedColumns() {
		return this.getUsedColumns(true);
	}
	
	public List<PA> getUsedColumns(boolean includeNote) {
		List<PA> list = new ArrayList<PA>();
		
		for (PA mp : this.map.values()) {
			if (mp.isUsable() && mp.isUsed())
				list.add(mp);
		}
		
		if (!includeNote)
			list.remove(this.get("NOTE"));
		
		return list;
	}
	
}
