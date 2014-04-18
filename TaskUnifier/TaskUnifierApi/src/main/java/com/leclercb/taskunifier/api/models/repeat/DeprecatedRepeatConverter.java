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

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeprecatedRepeatConverter {

    public static Repeat getRepeat(String repeat) {
        if (!isValidRepeatValue(repeat))
            return null;

        return getRepeatFromString(repeat);
    }

    public static boolean isValidRepeatValue(String repeat) {
        if (repeat == null || repeat.length() == 0)
            return false;

        String regex = null;

        repeat = repeat.toLowerCase();

        if (repeat.equals("with parent"))
            return true;

        regex = "^(daily|weekly|biweekly|monthly|bimonthly|quarterly|semiannually|yearly)$".toLowerCase();
        if (repeat.matches(regex))
            return true;

        regex = "^(every [0-9]+ (day|days|week|weeks|month|months|year|years))$".toLowerCase();
        if (repeat.matches(regex))
            return true;

        regex = "^((on )?the ([1-4]|1st|first|2nd|second|3rd|third|4th|fourth|last) (mon|tue|wed|thu|fri|sat|sun|monday|tuesday|wednesday|thursday|friday|saturday|sunday) of each month)$".toLowerCase();
        if (repeat.matches(regex))
            return true;

        String daysRegex = "(mon|tue|wed|thu|fri|sat|sun|monday|tuesday|wednesday|thursday|friday|saturday|sunday|weekend|weekday)";
        regex = "^(every ("
                + daysRegex
                + "(, ?"
                + daysRegex
                + ")*))$".toLowerCase();
        if (repeat.matches(regex))
            return true;

        return false;
    }

    private static Repeat getRepeatFromString(String repeat) {
        if (repeat == null || repeat.length() == 0)
            return null;

        String regex = null;

        repeat = repeat.toLowerCase();

        if (repeat.equals("with parent"))
            return getRepeatFromString1(repeat);

        regex = "^(daily|weekly|biweekly|monthly|bimonthly|quarterly|semiannually|yearly)$".toLowerCase();
        if (repeat.matches(regex))
            return getRepeatFromString2(repeat);

        regex = "^(every ([0-9]+) (day|days|week|weeks|month|months|year|years))$".toLowerCase();
        if (repeat.matches(regex))
            return getRepeatFromString3(repeat);

        regex = "^((on )?the ([1-4]|1st|first|2nd|second|3rd|third|4th|fourth|last) (mon|tue|wed|thu|fri|sat|sun|monday|tuesday|wednesday|thursday|friday|saturday|sunday) of each month)$".toLowerCase();
        if (repeat.matches(regex))
            return getRepeatFromString4(repeat);

        String daysRegex = "(mon|tue|wed|thu|fri|sat|sun|monday|tuesday|wednesday|thursday|friday|saturday|sunday|weekend|weekday)";
        regex = "^(every ("
                + daysRegex
                + "(, ?"
                + daysRegex
                + ")*))$".toLowerCase();
        if (repeat.matches(regex))
            return getRepeatFromString5(repeat);

        return null;
    }

    private static Repeat getRepeatFromString1(String repeat) {
        return new RepeatWithParent();
    }

    private static Repeat getRepeatFromString2(String repeat) {
        if (repeat.equals("daily"))
            return new RepeatEveryX(Calendar.DAY_OF_MONTH, 1);
        else if (repeat.equals("weekly"))
            return new RepeatEveryX(Calendar.WEEK_OF_YEAR, 1);
        else if (repeat.equals("biweekly"))
            return new RepeatEveryX(Calendar.WEEK_OF_YEAR, 2);
        else if (repeat.equals("monthly"))
            return new RepeatEveryX(Calendar.MONTH, 1);
        else if (repeat.equals("bimonthly"))
            return new RepeatEveryX(Calendar.MONTH, 2);
        else if (repeat.equals("quarterly"))
            return new RepeatEveryX(Calendar.MONTH, 3);
        else if (repeat.equals("semiannually"))
            return new RepeatEveryX(Calendar.MONTH, 6);
        else if (repeat.equals("yearly"))
            return new RepeatEveryX(Calendar.YEAR, 1);

        return null;
    }

    private static Repeat getRepeatFromString3(String repeat) {
        String regex = "^(every ([0-9]+) (day|days|week|weeks|month|months|year|years))$".toLowerCase();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(repeat);

        if (!matcher.find())
            return null;

        int value = Integer.parseInt(matcher.group(2));
        String strType = matcher.group(3);
        int type = Calendar.DAY_OF_MONTH;

        if (strType.startsWith("day"))
            type = Calendar.DAY_OF_MONTH;
        else if (strType.startsWith("week"))
            type = Calendar.WEEK_OF_YEAR;
        else if (strType.startsWith("month"))
            type = Calendar.MONTH;
        else if (strType.startsWith("year"))
            type = Calendar.YEAR;

        return new RepeatEveryX(type, value);
    }

    private static Repeat getRepeatFromString4(String repeat) {
        String regex = "^((on )?the ([1-4]|1st|first|2nd|second|3rd|third|4th|fourth|last) (mon|tue|wed|thu|fri|sat|sun|monday|tuesday|wednesday|thursday|friday|saturday|sunday) of each month)$".toLowerCase();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(repeat);

        if (!matcher.find())
            return null;

        String strWeek = matcher.group(3);
        int week = 1;
        String strDay = matcher.group(4);
        int day = Calendar.MONDAY;

        if (strWeek.matches("^(1|1st|first)$")) {
            week = 1;
        } else if (strWeek.matches("^(2|2nd|second)$")) {
            week = 2;
        } else if (strWeek.matches("^(3|3rd|third)$")) {
            week = 3;
        } else if (strWeek.matches("^(4|4th|fourth)$")) {
            week = 4;
        } else if (strWeek.equals("last")) {
            week = -1;
        }

        if (strDay.startsWith("mon")) {
            day = Calendar.MONDAY;
        } else if (strDay.startsWith("tue")) {
            day = Calendar.TUESDAY;
        } else if (strDay.startsWith("wed")) {
            day = Calendar.WEDNESDAY;
        } else if (strDay.startsWith("thu")) {
            day = Calendar.THURSDAY;
        } else if (strDay.startsWith("fri")) {
            day = Calendar.FRIDAY;
        } else if (strDay.startsWith("sat")) {
            day = Calendar.SATURDAY;
        } else if (strDay.startsWith("sun")) {
            day = Calendar.SUNDAY;
        }

        return new RepeatEveryXMonthOnWeekX(1, week, day);
    }

    private static Repeat getRepeatFromString5(String repeat) {
        String daysRegex = "(mon|tue|wed|thu|fri|sat|sun|monday|tuesday|wednesday|thursday|friday|saturday|sunday|weekend|weekday)";
        String regex = "^(every ("
                + daysRegex
                + "(, ?"
                + daysRegex
                + ")*))$".toLowerCase();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(repeat);

        if (!matcher.find())
            return null;

        String[] strDays = matcher.group(2).split(",");
        Set<Integer> days = new HashSet<Integer>();

        for (String strField : strDays) {
            strField = strField.trim();

            if (strField.startsWith("mon")) {
                days.add(Calendar.MONDAY);
            } else if (strField.startsWith("tue")) {
                days.add(Calendar.TUESDAY);
            } else if (strField.startsWith("wed")) {
                days.add(Calendar.WEDNESDAY);
            } else if (strField.startsWith("thu")) {
                days.add(Calendar.THURSDAY);
            } else if (strField.startsWith("fri")) {
                days.add(Calendar.FRIDAY);
            } else if (strField.startsWith("sat")) {
                days.add(Calendar.SATURDAY);
            } else if (strField.startsWith("sun")) {
                days.add(Calendar.SUNDAY);
            } else if (strField.equals("weekend")) {
                days.add(Calendar.SATURDAY);
                days.add(Calendar.SUNDAY);
            } else if (strField.equals("weekday")) {
                days.add(Calendar.MONDAY);
                days.add(Calendar.TUESDAY);
                days.add(Calendar.WEDNESDAY);
                days.add(Calendar.THURSDAY);
                days.add(Calendar.FRIDAY);
            }
        }

        if (days.size() == 0)
            return null;

        int[] daysArray = new int[days.size()];
        int i = 0;

        for (Integer day : days) {
            daysArray[i++] = day;
        }

        return new RepeatEveryXWeekOnDays(1, daysArray);
    }

}
