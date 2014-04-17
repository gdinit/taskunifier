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
package com.leclercb.taskunifier.gui.commons.values;

import com.leclercb.taskunifier.gui.translations.Translations;
import org.jdesktop.swingx.renderer.StringValue;

import java.util.Calendar;

public class StringValueDayOfWeek implements StringValue {

    public static final StringValueDayOfWeek INSTANCE = new StringValueDayOfWeek();

    private StringValueDayOfWeek() {

    }

    @Override
    public String getString(Object value) {
        if (value == null || !(value instanceof Integer))
            return " ";

        int day = (Integer) value;

        if (day == Calendar.MONDAY)
            return Translations.getString("date.monday");

        if (day == Calendar.TUESDAY)
            return Translations.getString("date.tuesday");

        if (day == Calendar.WEDNESDAY)
            return Translations.getString("date.wednesday");

        if (day == Calendar.THURSDAY)
            return Translations.getString("date.thursday");

        if (day == Calendar.FRIDAY)
            return Translations.getString("date.friday");

        if (day == Calendar.SATURDAY)
            return Translations.getString("date.saturday");

        if (day == Calendar.SUNDAY)
            return Translations.getString("date.sunday");

        return " ";
    }

}
