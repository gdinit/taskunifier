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

package com.leclercb.taskunifier.api.models.repeat;

import com.leclercb.commons.api.utils.DateUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RepeatEveryXMonthOnWeekX implements Repeat {

    @XStreamAlias("value")
    private int value;

    @XStreamAlias("day")
    private int day;

    @XStreamAlias("week")
    private int week;

    public RepeatEveryXMonthOnWeekX() {
        this(1, 1, Calendar.MONDAY);
    }

    public RepeatEveryXMonthOnWeekX(int value, int week, int day) {
        this.setValue(value);
        this.setWeek(week);
        this.setDay(day);
    }

    public int getValue() {
        return value;
    }

    private void setValue(int value) {
        if (value < 1 || value > 1000)
            throw new IllegalArgumentException("Invalid value");

        this.value = value;
    }

    public int getDay() {
        return day;
    }

    private void setDay(int day) {
        if (day < Calendar.SUNDAY || day > Calendar.SATURDAY)
            throw new IllegalArgumentException("Invalid day of week");

        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    private void setWeek(int week) {
        if (week < -1 || week > 4 || week == 0)
            throw new IllegalArgumentException("Invalid week of month");

        this.week = week;
    }

    @Override
    public Calendar getNextDate(Calendar calendar) {
        if (calendar == null)
            return null;

        Calendar c = DateUtils.cloneCalendar(calendar);
        int week = 0;

        switch (this.week) {
            case 1:
                week = 0;
                break;
            case 2:
                week = 1;
                break;
            case 3:
                week = 2;
                break;
            case 4:
                week = 3;
                break;
            case -1:
                week = 4;
                break;
        }

        List<Calendar> days = this.getDayOfWeekFromMonth(this.day, c);
        c = DateUtils.cloneCalendar(days.get(week));

        if (calendar.compareTo(c) < 0 || calendar.get(Calendar.MONTH) != c.get(Calendar.MONTH)) {
            c = DateUtils.cloneCalendar(calendar);
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.add(Calendar.MONTH, this.value - 1);
            days = this.getDayOfWeekFromMonth(this.day, c);
            c = DateUtils.cloneCalendar(days.get(week));
        } else {
            c = DateUtils.cloneCalendar(calendar);
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.add(Calendar.MONTH, this.value);
            days = this.getDayOfWeekFromMonth(this.day, c);
            c = DateUtils.cloneCalendar(days.get(week));
        }

        return c;
    }

    private List<Calendar> getDayOfWeekFromMonth(int day, Calendar calendar) {
        Calendar c = DateUtils.cloneCalendar(calendar);
        int month = c.get(Calendar.MONTH);
        List<Calendar> days = new ArrayList<Calendar>();

        c.set(Calendar.DAY_OF_MONTH, 1);

        while (c.get(Calendar.DAY_OF_WEEK) != day) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }

        while (c.get(Calendar.MONTH) == month) {
            days.add(DateUtils.cloneCalendar(c));
            c.add(Calendar.DAY_OF_MONTH, 7);
        }

        if (days.size() == 4) {
            days.add(days.get(3));
        }

        return days;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof RepeatEveryXMonthOnWeekX) {
            RepeatEveryXMonthOnWeekX r = (RepeatEveryXMonthOnWeekX) o;

            return new EqualsBuilder()
                    .append(this.value, r.value)
                    .append(this.day, r.day)
                    .append(this.week, r.week)
                    .isEquals();
        }

        return false;
    }

    @Override
    public final int hashCode() {
        HashCodeBuilder hashCode = new HashCodeBuilder();
        hashCode.append(this.value);
        hashCode.append(this.day);
        hashCode.append(this.week);

        return hashCode.toHashCode();
    }

}
