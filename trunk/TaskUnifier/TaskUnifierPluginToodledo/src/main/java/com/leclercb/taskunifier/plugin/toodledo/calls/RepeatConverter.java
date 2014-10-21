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

package com.leclercb.taskunifier.plugin.toodledo.calls;

import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.api.models.repeat.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepeatConverter {

    public static String getRepeat(Repeat repeat, TaskRepeatFrom repeatFrom) {
        String fromComp = (repeatFrom == TaskRepeatFrom.COMPLETION_DATE ? ";FROMCOMP" : "");

        if (repeat == null)
            return "";

        if (repeat instanceof RepeatWithParent)
            return "PARENT";

        if (repeat instanceof RepeatEveryX) {
            RepeatEveryX r = (RepeatEveryX) repeat;

            switch (r.getType()) {
                case Calendar.DAY_OF_MONTH:
                    return "FREQ=DAILY;INTERVAL=" + r.getValue() + fromComp;
                case Calendar.WEEK_OF_YEAR:
                    return "FREQ=WEEKLY;INTERVAL=" + r.getValue() + fromComp;
                case Calendar.MONTH:
                    return "FREQ=MONTHLY;INTERVAL=" + r.getValue() + fromComp;
                case Calendar.YEAR:
                    return "FREQ=YEARLY;INTERVAL=" + r.getValue() + fromComp;
            }

            return "";
        }

        if (repeat instanceof RepeatEveryXWeekOnDays) {
            RepeatEveryXWeekOnDays r = (RepeatEveryXWeekOnDays) repeat;

            String[] days = new String[r.getDays().length];

            for (int i = 0; i < r.getDays().length; i++) {
                switch (r.getDays()[i]) {
                    case Calendar.MONDAY:
                        days[i] = "MO";
                        break;
                    case Calendar.TUESDAY:
                        days[i] = "TU";
                        break;
                    case Calendar.WEDNESDAY:
                        days[i] = "WE";
                        break;
                    case Calendar.THURSDAY:
                        days[i] = "TH";
                        break;
                    case Calendar.FRIDAY:
                        days[i] = "FR";
                        break;
                    case Calendar.SATURDAY:
                        days[i] = "SA";
                        break;
                    case Calendar.SUNDAY:
                        days[i] = "SU";
                        break;
                }
            }

            return "FREQ=WEEKLY;BYDAY=" + StringUtils.join(days, ",") + ";INTERVAL=" + r.getValue() + fromComp;
        }

        if (repeat instanceof RepeatEveryXMonthOnDayX) {
            return "";
        }

        if (repeat instanceof RepeatEveryXMonthOnWeekX) {
            RepeatEveryXMonthOnWeekX r = (RepeatEveryXMonthOnWeekX) repeat;

            String day = "MO";

            switch (r.getDay()) {
                case Calendar.MONDAY:
                    day = "MO";
                    break;
                case Calendar.TUESDAY:
                    day = "TU";
                    break;
                case Calendar.WEDNESDAY:
                    day = "WE";
                    break;
                case Calendar.THURSDAY:
                    day = "TH";
                    break;
                case Calendar.FRIDAY:
                    day = "FR";
                    break;
                case Calendar.SATURDAY:
                    day = "SA";
                    break;
                case Calendar.SUNDAY:
                    day = "SU";
                    break;
            }

            return "FREQ=MONTHLY;BYDAY=" + r.getWeek() + day + fromComp;
        }

        return "";
    }

    public static Repeat getRepeat(String repeat) {
        if (!isValidRepeatValue(repeat))
            return null;

        return getRepeatFromString(repeat);
    }

    public static TaskRepeatFrom getRepeatFrom(String repeat) {
        if (!isValidRepeatValue(repeat))
            return TaskRepeatFrom.DUE_DATE;

        return getRepeatFromFromString(repeat);
    }

    public static boolean isValidRepeatValue(String repeat) {
        if (repeat == null || repeat.length() == 0)
            return false;

        String regex;

        if (repeat.equals("PARENT"))
            return true;

        regex = "^FREQ=(DAILY|WEEKLY|MONTHLY|YEARLY)(;FROMCOMP)?$";
        if (repeat.matches(regex))
            return true;

        regex = "^FREQ=(DAILY|WEEKLY|MONTHLY|YEARLY);INTERVAL=([1-9][0-9]{0,2})(;FROMCOMP)?$";
        if (repeat.matches(regex))
            return true;

        regex = "^FREQ=WEEKLY;BYDAY=((MO|TU|WE|TH|FR|SA|SU)(,(MO|TU|WE|TH|FR|SA|SU))*)(;FROMCOMP)?$";
        if (repeat.matches(regex))
            return true;

        regex = "^FREQ=MONTHLY;BYDAY=((-?[1-4])(MO|TU|WE|TH|FR|SA|SU))(;FROMCOMP)?$";
        if (repeat.matches(regex))
            return true;

        return false;
    }

    private static Repeat getRepeatFromString(String repeat) {
        if (repeat == null || repeat.length() == 0)
            return null;

        String regex;

        if (repeat.equals("PARENT"))
            return new RepeatWithParent();

        regex = "^FREQ=(DAILY|WEEKLY|MONTHLY|YEARLY)(;FROMCOMP)?$";
        if (repeat.matches(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(repeat);

            if (!matcher.find())
                return null;

            if ("DAILY".equals(matcher.group(1))) {
                return new RepeatEveryX(Calendar.DAY_OF_MONTH, 1);
            }

            if ("WEEKLY".equals(matcher.group(1))) {
                return new RepeatEveryX(Calendar.WEEK_OF_YEAR, 1);
            }

            if ("MONTHLY".equals(matcher.group(1))) {
                return new RepeatEveryX(Calendar.MONTH, 1);
            }

            if ("YEARLY".equals(matcher.group(1))) {
                return new RepeatEveryX(Calendar.YEAR, 1);
            }
        }

        regex = "^FREQ=(DAILY|WEEKLY|MONTHLY|YEARLY);INTERVAL=([1-9][0-9]{0,2})(;FROMCOMP)?$";
        if (repeat.matches(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(repeat);

            if (!matcher.find())
                return null;

            if ("DAILY".equals(matcher.group(1))) {
                return new RepeatEveryX(Calendar.DAY_OF_MONTH, Integer.parseInt(matcher.group(2)));
            }

            if ("WEEKLY".equals(matcher.group(1))) {
                return new RepeatEveryX(Calendar.WEEK_OF_YEAR, Integer.parseInt(matcher.group(2)));
            }

            if ("MONTHLY".equals(matcher.group(1))) {
                return new RepeatEveryX(Calendar.MONTH, Integer.parseInt(matcher.group(2)));
            }

            if ("YEARLY".equals(matcher.group(1))) {
                return new RepeatEveryX(Calendar.YEAR, Integer.parseInt(matcher.group(2)));
            }
        }

        regex = "^FREQ=WEEKLY;BYDAY=((MO|TU|WE|TH|FR|SA|SU)(,(MO|TU|WE|TH|FR|SA|SU))*)(;FROMCOMP)?$";
        if (repeat.matches(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(repeat);

            if (!matcher.find())
                return null;

            List<Integer> days = new ArrayList<Integer>();
            String[] split = matcher.group(1).split(",");

            for (String token : split) {
                token = token.trim();

                if ("MO".equals(token))
                    days.add(Calendar.MONDAY);
                else if ("TU".equals(token))
                    days.add(Calendar.TUESDAY);
                else if ("WE".equals(token))
                    days.add(Calendar.WEDNESDAY);
                else if ("TH".equals(token))
                    days.add(Calendar.THURSDAY);
                else if ("FR".equals(token))
                    days.add(Calendar.FRIDAY);
                else if ("SA".equals(token))
                    days.add(Calendar.SATURDAY);
                else if ("SU".equals(token))
                    days.add(Calendar.SUNDAY);
            }

            int[] dayArray = new int[days.size()];
            for (int i = 0; i < days.size(); i++)
                dayArray[i] = days.get(i);

            return new RepeatEveryXWeekOnDays(1, dayArray);
        }

        regex = "^FREQ=MONTHLY;BYDAY=((-?[1-4])(MO|TU|WE|TH|FR|SA|SU))(;FROMCOMP)?$";
        if (repeat.matches(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(repeat);

            if (!matcher.find())
                return null;

            int day = Calendar.MONDAY;
            String token = matcher.group(3);

            if ("MO".equals(token))
                day = Calendar.MONDAY;
            else if ("TU".equals(token))
                day = Calendar.TUESDAY;
            else if ("WE".equals(token))
                day = Calendar.WEDNESDAY;
            else if ("TH".equals(token))
                day = Calendar.THURSDAY;
            else if ("FR".equals(token))
                day = Calendar.FRIDAY;
            else if ("SA".equals(token))
                day = Calendar.SATURDAY;
            else if ("SU".equals(token))
                day = Calendar.SUNDAY;

            return new RepeatEveryXMonthOnWeekX(1, Integer.parseInt(matcher.group(2)), day);
        }

        return null;
    }

    private static TaskRepeatFrom getRepeatFromFromString(String repeat) {
        if (repeat == null || repeat.length() == 0)
            return TaskRepeatFrom.DUE_DATE;

        String regex = ";FROMCOMP$";
        if (repeat.matches(regex))
            return TaskRepeatFrom.COMPLETION_DATE;

        return TaskRepeatFrom.DUE_DATE;
    }

}
