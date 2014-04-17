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

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.DateUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Calendar;

public class RepeatEveryXMonthOnDayX implements Repeat {

    private int value;
    private int day;

    public RepeatEveryXMonthOnDayX(int value, int day) {
        this.setValue(value);
        this.setDay(day);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        CheckUtils.isPositive(value);
        this.value = value;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        if (day < 1 || day > 31)
            throw new IllegalArgumentException("Invalid day of week");

        this.day = day;
    }

    @Override
    public Calendar getNextDate(Calendar calendar) {
        if (calendar == null)
            return null;

        Calendar c = DateUtils.cloneCalendar(calendar);
        c.set(Calendar.DAY_OF_MONTH, this.day);

        if (calendar.get(Calendar.DAY_OF_MONTH) < this.day || calendar.get(Calendar.MONTH) != c.get(Calendar.MONTH))
            c.add(Calendar.MONTH, this.value - 1);
        else
            c.add(Calendar.MONTH, this.value);

        return c;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof RepeatEveryXMonthOnDayX) {
            RepeatEveryXMonthOnDayX r = (RepeatEveryXMonthOnDayX) o;

            return new EqualsBuilder()
                    .append(this.value, r.value)
                    .append(this.day, r.day)
                    .isEquals();
        }

        return false;
    }

    @Override
    public final int hashCode() {
        HashCodeBuilder hashCode = new HashCodeBuilder();
        hashCode.append(this.value);
        hashCode.append(this.day);

        return hashCode.toHashCode();
    }

}
