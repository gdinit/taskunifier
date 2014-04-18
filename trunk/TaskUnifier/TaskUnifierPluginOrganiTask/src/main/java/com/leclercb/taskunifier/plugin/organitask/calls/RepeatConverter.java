/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leclercb.taskunifier.api.models.repeat.*;

import java.util.Calendar;

public class RepeatConverter {

    public static String getRepeat(Repeat repeat) {
        if (repeat == null)
            return "";

        if (repeat instanceof RepeatWithParent) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();

            node.put("unit", "p");

            return node.toString();
        }

        if (repeat instanceof RepeatEveryX) {
            RepeatEveryX r = (RepeatEveryX) repeat;

            String unit = "d";

            switch (r.getType()) {
                case Calendar.DAY_OF_MONTH:
                    unit = "d";
                    break;
                case Calendar.WEEK_OF_YEAR:
                    unit = "w";
                    break;
                case Calendar.MONTH:
                    unit = "m";
                    break;
                case Calendar.YEAR:
                    unit = "y";
                    break;
            }

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();

            node.put("unit", unit);
            node.put("value", r.getValue());

            return node.toString();
        }

        if (repeat instanceof RepeatEveryXWeekOnDays) {
            RepeatEveryXWeekOnDays r = (RepeatEveryXWeekOnDays) repeat;

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            ArrayNode daysNode = mapper.createArrayNode();

            for (int day : r.getDays())
                daysNode.add(day - 1);

            node.put("unit", "w");
            node.put("value", r.getValue());
            node.put("days", daysNode);

            return node.toString();
        }

        if (repeat instanceof RepeatEveryXMonthOnDayX) {
            RepeatEveryXMonthOnDayX r = (RepeatEveryXMonthOnDayX) repeat;

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();

            node.put("unit", "m");
            node.put("value", r.getValue());
            node.put("day", r.getDay());

            return node.toString();
        }

        if (repeat instanceof RepeatEveryXMonthOnWeekX) {
            RepeatEveryXMonthOnWeekX r = (RepeatEveryXMonthOnWeekX) repeat;

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();

            node.put("unit", "m");
            node.put("value", r.getValue());
            node.put("day", r.getDay() - 1);
            node.put("week", r.getWeek());

            return node.toString();
        }

        return "";
    }

    public static Repeat getRepeat(String repeat) {
        if (repeat == null || repeat.length() == 0)
            return null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(repeat);

            if (root.get("unit").asText().equals("p")) {
                return new RepeatWithParent();
            }

            if (root.get("unit").asText().equals("d")) {
                return new RepeatEveryX(Calendar.DAY_OF_MONTH, root.get("value").asInt());
            }

            if (root.get("unit").asText().equals("w")) {
                if (root.get("days") != null) {
                    ArrayNode daysNode = (ArrayNode) root.get("days");

                    int[] days = new int[daysNode.size()];

                    for (int i = 0; i < daysNode.size(); i++)
                        days[i] = daysNode.get(i).asInt() + 1;

                    return new RepeatEveryXWeekOnDays(root.get("value").asInt(), days);
                }

                return new RepeatEveryX(Calendar.WEEK_OF_YEAR, root.get("value").asInt());
            }

            if (root.get("unit").asText().equals("m")) {
                if (root.get("day") != null && root.get("week") != null) {
                    return new RepeatEveryXMonthOnWeekX(root.get("value").asInt(), root.get("week").asInt(), root.get("day").asInt() + 1);
                }

                if (root.get("day") != null) {
                    return new RepeatEveryXMonthOnDayX(root.get("value").asInt(), root.get("day").asInt());
                }

                return new RepeatEveryX(Calendar.MONTH, root.get("value").asInt());
            }

            if (root.get("unit").asText().equals("y")) {
                return new RepeatEveryX(Calendar.YEAR, root.get("value").asInt());
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
