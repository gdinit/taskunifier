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
package com.leclercb.taskunifier.gui.api.searchers.filters.conditions;

import java.util.Calendar;

import com.leclercb.commons.api.utils.DateUtils;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;

public enum DaysCondition implements Condition<Integer, Calendar> {
	
	TODAY,
	EQUALS,
	GREATER_THAN,
	GREATER_THAN_OR_EQUALS,
	GREATER_THAN_USING_TIME,
	LESS_THAN,
	LESS_THAN_OR_EQUALS,
	LESS_THAN_USING_TIME,
	MONTH_EQUALS,
	MONTH_NOT_EQUALS,
	NOT_EQUALS,
	WEEK_EQUALS,
	WEEK_NOT_EQUALS;
	
	private DaysCondition() {
		
	}
	
	@Override
	public Class<?> getValueType() {
		return Object.class;
	}
	
	@Override
	public Class<?> getModelValueType() {
		return Calendar.class;
	}
	
	@Override
	public boolean include(
			PropertyAccessor<?> accessor,
			Object objectValue,
			Object objectModelValue) {
		Calendar modelValue = (Calendar) objectModelValue;
		
		if (objectValue != null
				&& !(objectValue instanceof Integer)
				&& !(objectValue instanceof Calendar)) {
			throw new IllegalArgumentException(
					"The property is incompatible with this condition");
		}
		
		if (objectValue == null || objectValue instanceof Integer) {
			Integer value = (Integer) objectValue;
			
			if (value == null && modelValue == null) {
				switch (this) {
					case EQUALS:
					case WEEK_EQUALS:
					case MONTH_EQUALS:
						return true;
					default:
						return false;
				}
			}
			
			if (value == null || modelValue == null) {
				switch (this) {
					case TODAY:
						if (modelValue == null)
							return false;
						break;
					case NOT_EQUALS:
					case WEEK_NOT_EQUALS:
					case MONTH_NOT_EQUALS:
						return true;
					default:
						return false;
				}
			}
			
			if (this == WEEK_EQUALS)
				return DateUtils.getDiffInWeeks(
						Calendar.getInstance(),
						modelValue) == value;
			
			if (this == MONTH_EQUALS)
				return DateUtils.getDiffInMonths(
						Calendar.getInstance(),
						modelValue) == value;
			
			if (this == WEEK_NOT_EQUALS)
				return DateUtils.getDiffInWeeks(
						Calendar.getInstance(),
						modelValue) != value;
			
			if (this == MONTH_NOT_EQUALS)
				return DateUtils.getDiffInMonths(
						Calendar.getInstance(),
						modelValue) != value;
			
			boolean useTime = (this == GREATER_THAN_USING_TIME || this == LESS_THAN_USING_TIME);
			double diffDays = DateUtils.getDiffInDays(
					Calendar.getInstance(),
					modelValue,
					useTime);
			
			switch (this) {
				case TODAY:
					return diffDays == 0;
				case EQUALS:
					return diffDays == value;
				case GREATER_THAN:
					return diffDays > value;
				case GREATER_THAN_OR_EQUALS:
				case GREATER_THAN_USING_TIME:
					return diffDays >= value;
				case LESS_THAN:
					return diffDays < value;
				case LESS_THAN_OR_EQUALS:
				case LESS_THAN_USING_TIME:
					return diffDays <= value;
				case NOT_EQUALS:
					return diffDays != value;
				default:
					return false;
			}
		} else {
			Calendar value = (Calendar) objectValue;
			
			if (modelValue == null) {
				switch (this) {
					case TODAY:
						return false;
					case NOT_EQUALS:
					case WEEK_NOT_EQUALS:
					case MONTH_NOT_EQUALS:
						return true;
					default:
						return false;
				}
			}
			
			if (this == WEEK_EQUALS)
				return DateUtils.getDiffInWeeks(value, modelValue) == 0;
			
			if (this == MONTH_EQUALS)
				return DateUtils.getDiffInMonths(value, modelValue) == 0;
			
			if (this == WEEK_NOT_EQUALS)
				return DateUtils.getDiffInWeeks(value, modelValue) != 0;
			
			if (this == MONTH_NOT_EQUALS)
				return DateUtils.getDiffInMonths(value, modelValue) != 0;
			
			boolean useTime = (this == GREATER_THAN_USING_TIME || this == LESS_THAN_USING_TIME);
			double diffDays = DateUtils.getDiffInDays(
					value,
					modelValue,
					useTime);
			
			switch (this) {
				case TODAY:
					return diffDays == 0;
				case EQUALS:
					return diffDays == 0;
				case GREATER_THAN:
					return diffDays > 0;
				case GREATER_THAN_OR_EQUALS:
				case GREATER_THAN_USING_TIME:
					return diffDays >= 0;
				case LESS_THAN:
					return diffDays < 0;
				case LESS_THAN_OR_EQUALS:
				case LESS_THAN_USING_TIME:
					return diffDays <= 0;
				case NOT_EQUALS:
					return diffDays != 0;
				default:
					return false;
			}
		}
	}
	
}
