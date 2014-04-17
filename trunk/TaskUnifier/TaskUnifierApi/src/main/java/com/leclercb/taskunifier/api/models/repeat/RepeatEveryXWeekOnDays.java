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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RepeatEveryXWeekOnDays implements Repeat {

    private int value;
    private int[] days;

    public RepeatEveryXWeekOnDays(int value, int[] days) {
        this.setValue(value);
        this.setDays(days);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        CheckUtils.isPositive(value);
        this.value = value;
    }

    public int[] getDays() {
        return days;
    }

    public void setDays(int[] days) {
        CheckUtils.isNotNull(days);

        for (int day : days) {
            if (day < Calendar.SUNDAY || day > Calendar.SATURDAY)
                throw new IllegalArgumentException("Invalid day of week");
        }

        this.days = days;
    }

    @Override
    public Calendar getNextDate(Calendar calendar) {
        if (calendar == null)
            return null;

        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < this.days.length; i++)
            list.add(this.days[i]);

        Calendar c = DateUtils.cloneCalendar(calendar);
        c.add(Calendar.DAY_OF_MONTH, this.value * 7);

        if (Arrays.asList(this.days).contains(c.get(Calendar.DAY_OF_WEEK)))
            return c;
        else
            c.add(Calendar.DAY_OF_MONTH, -7);

        for (int i = 1; i <= 7; i++) {
            if (Arrays.asList(this.days).contains(c.get(Calendar.DAY_OF_WEEK)))
                break;

            c.add(Calendar.DAY_OF_MONTH, 1);
        }

        return c;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof RepeatEveryXWeekOnDays) {
            RepeatEveryXWeekOnDays r = (RepeatEveryXWeekOnDays) o;

            return new EqualsBuilder()
                    .append(this.value, r.value)
                    .append(this.days, r.days)
                    .isEquals();
        }

        return false;
    }

    @Override
    public final int hashCode() {
        HashCodeBuilder hashCode = new HashCodeBuilder();
        hashCode.append(this.value);
        hashCode.append(this.days);

        return hashCode.toHashCode();
    }

}
