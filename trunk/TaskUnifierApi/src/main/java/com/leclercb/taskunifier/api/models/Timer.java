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

import java.util.Calendar;

import com.leclercb.commons.api.utils.DateUtils;

public class Timer implements Comparable<Timer> {
	
	private long value;
	private Calendar startDate;
	
	public Timer() {
		this(0, null);
	}
	
	public Timer(long value) {
		this(value, null);
	}
	
	public Timer(long value, Calendar startDate) {
		this.value = value;
		this.startDate = DateUtils.cloneCalendar(startDate);
	}
	
	public Timer(Timer timer) {
		this(
				(timer == null ? 0 : timer.getValue()),
				(timer == null ? null : timer.getStartDate()));
	}
	
	public long getValue() {
		return this.value;
	}
	
	public Calendar getStartDate() {
		return DateUtils.cloneCalendar(this.startDate);
	}
	
	public long getTimerValue() {
		if (this.startDate == null)
			return this.value;
		
		Calendar now = Calendar.getInstance();
		long second = now.getTimeInMillis() - this.startDate.getTimeInMillis();
		second = second / 1000;
		
		return this.value + second;
	}
	
	public void setValue(long value) {
		this.value = value;
		
		if (this.startDate != null)
			this.startDate = Calendar.getInstance();
	}
	
	public boolean isStarted() {
		return this.startDate != null;
	}
	
	public void start() {
		if (this.isStarted())
			return;
		
		this.startDate = Calendar.getInstance();
	}
	
	public void stop() {
		if (!this.isStarted())
			return;
		
		this.value = this.getTimerValue();
		this.startDate = null;
	}
	
	@Override
	public String toString() {
		return this.getTimerValue() + "";
	}
	
	@Override
	public int compareTo(Timer timer) {
		if (timer == null)
			return 1;
		
		return ((Long) this.getTimerValue()).compareTo(timer.getTimerValue());
	}
	
}
