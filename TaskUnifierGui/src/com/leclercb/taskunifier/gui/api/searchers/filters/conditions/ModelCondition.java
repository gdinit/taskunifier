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
package com.leclercb.taskunifier.gui.api.searchers.filters.conditions;

import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelList;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;

public enum ModelCondition implements Condition<Model, Object> {
	
	EQUALS,
	NOT_EQUALS;
	
	private ModelCondition() {
		
	}
	
	@Override
	public Class<?> getValueType() {
		return Model.class;
	}
	
	@Override
	public Class<?> getModelValueType() {
		return Object.class;
	}
	
	@Override
	public boolean include(
			PropertyAccessor<?> accessor,
			Object objectValue,
			Object objectModelValue) {
		Model value = (Model) objectValue;
		Object modelValue = objectModelValue;
		
		if (modelValue != null
				&& !(modelValue instanceof Model)
				&& !(modelValue instanceof ModelList<?>)) {
			throw new IllegalArgumentException(
					"The property is incompatible with this condition");
		}
		
		if (modelValue instanceof ModelList<?>)
			if (((ModelList<?>) modelValue).size() == 0)
				modelValue = null;
		
		if (value == null && modelValue == null) {
			switch (this) {
				case EQUALS:
					return true;
				default:
					return false;
			}
		}
		
		if (value == null || modelValue == null) {
			switch (this) {
				case NOT_EQUALS:
					return true;
				default:
					return false;
			}
		}
		
		if (modelValue instanceof Model) {
			switch (this) {
				case EQUALS:
					return modelValue.equals(value);
				case NOT_EQUALS:
					return !(modelValue.equals(value));
					
			}
		}
		
		if (modelValue instanceof ModelList<?>) {
			switch (this) {
				case EQUALS:
					return ((ModelList<?>) modelValue).contains(value);
				case NOT_EQUALS:
					return !((ModelList<?>) modelValue).contains(value);
					
			}
		}
		
		return false;
	}
	
}
