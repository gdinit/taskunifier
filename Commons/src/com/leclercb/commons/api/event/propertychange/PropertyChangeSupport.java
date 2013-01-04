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
package com.leclercb.commons.api.event.propertychange;

public class PropertyChangeSupport extends java.beans.PropertyChangeSupport {
	
	private Object source;
	
	public PropertyChangeSupport(Object source) {
		super(source);
		
		this.source = source;
	}
	
	public void firePropertyChange(
			String propertyName,
			Object oldValue,
			Object newValue,
			boolean silent) {
		if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
			this.firePropertyChange(new PropertyChangeEventExtended(
					this.source,
					propertyName,
					oldValue,
					newValue,
					silent));
		}
	}
	
	public void firePropertyChange(
			String propertyName,
			int oldValue,
			int newValue,
			boolean silent) {
		if (oldValue != newValue) {
			this.firePropertyChange(
					propertyName,
					Integer.valueOf(oldValue),
					Integer.valueOf(newValue),
					silent);
		}
	}
	
	public void firePropertyChange(
			String propertyName,
			boolean oldValue,
			boolean newValue,
			boolean silent) {
		if (oldValue != newValue) {
			this.firePropertyChange(
					propertyName,
					Boolean.valueOf(oldValue),
					Boolean.valueOf(newValue),
					silent);
		}
	}
	
	public void fireIndexedPropertyChange(
			String propertyName,
			int index,
			Object oldValue,
			Object newValue,
			boolean silent) {
		if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
			this.firePropertyChange(new IndexedPropertyChangeEventExtended(
					this.source,
					propertyName,
					oldValue,
					newValue,
					index,
					silent));
		}
	}
	
	public void fireIndexedPropertyChange(
			String propertyName,
			int index,
			int oldValue,
			int newValue,
			boolean silent) {
		if (oldValue != newValue) {
			this.fireIndexedPropertyChange(
					propertyName,
					index,
					Integer.valueOf(oldValue),
					Integer.valueOf(newValue),
					silent);
		}
	}
	
	public void fireIndexedPropertyChange(
			String propertyName,
			int index,
			boolean oldValue,
			boolean newValue,
			boolean silent) {
		if (oldValue != newValue) {
			this.fireIndexedPropertyChange(
					propertyName,
					index,
					Boolean.valueOf(oldValue),
					Boolean.valueOf(newValue),
					silent);
		}
	}
	
}
