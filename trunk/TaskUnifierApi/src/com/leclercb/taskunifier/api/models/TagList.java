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
package com.leclercb.taskunifier.api.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TagList implements Cloneable, Serializable, Iterable<Tag> {
	
	private List<Tag> tags;
	
	public TagList() {
		this.tags = new ArrayList<Tag>();
	}
	
	@Override
	protected TagList clone() {
		TagList list = new TagList();
		list.addTags(this);
		return list;
	}
	
	@Override
	public Iterator<Tag> iterator() {
		return this.tags.iterator();
	}
	
	public List<Tag> asList() {
		return new ArrayList<Tag>(this.tags);
	}
	
	public int getIndexOf(Tag tag) {
		return this.tags.indexOf(tag);
	}
	
	public int getTagCount() {
		return this.tags.size();
	}
	
	public Tag getTag(int index) {
		return this.tags.get(index);
	}
	
	public boolean containsTag(Tag tag) {
		return this.tags.contains(tag);
	}
	
	public boolean containsTag(String tag) {
		if (!Tag.isValid(tag))
			return false;
		
		return this.containsTag(new Tag(tag));
	}
	
	public boolean addTag(Tag tag) {
		if (tag == null)
			return false;
		
		if (this.tags.contains(tag))
			return false;
		
		this.tags.add(tag);
		return true;
	}
	
	public boolean addTag(String tag) {
		if (!Tag.isValid(tag))
			return false;
		
		return this.addTag(new Tag(tag));
	}
	
	public void addTags(TagList tags) {
		if (tags == null)
			return;
		
		for (Tag tag : tags) {
			this.addTag(tag);
		}
	}
	
	public void addTags(String[] tags) {
		if (tags == null)
			return;
		
		for (String tag : tags) {
			this.addTag(tag);
		}
	}
	
	public void addTags(Collection<Tag> tags) {
		if (tags == null)
			return;
		
		for (Tag tag : tags) {
			this.addTag(tag);
		}
	}
	
	public boolean replaceTag(Tag oldTag, Tag newTag) {
		if (oldTag == null || newTag == null)
			return false;
		
		if (!this.tags.contains(oldTag))
			return false;
		
		if (this.tags.contains(newTag))
			return false;
		
		this.tags.set(this.tags.indexOf(oldTag), newTag);
		return true;
	}
	
	public boolean removeTag(Tag tag) {
		return this.tags.remove(tag);
	}
	
	@Override
	public String toString() {
		return StringUtils.join(this.tags, ", ");
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (o instanceof TagList) {
			TagList list = (TagList) o;
			
			return new EqualsBuilder().append(this.tags, list.tags).isEquals();
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hashCode = new HashCodeBuilder();
		hashCode.append(this.tags);
		
		return hashCode.toHashCode();
	}
	
	public static TagList fromString(String string) {
		if (string == null)
			return new TagList();
		
		String[] tags = string.split(",");
		TagList list = new TagList();
		list.addTags(tags);
		
		return list;
	}
	
}
