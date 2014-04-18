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
package com.leclercb.taskunifier.gui.components.repeat.panels;

import com.leclercb.taskunifier.api.models.repeat.Repeat;
import com.leclercb.taskunifier.api.models.repeat.RepeatEveryX;
import com.leclercb.taskunifier.api.models.repeat.RepeatEveryXWeekOnDays;
import com.leclercb.taskunifier.gui.commons.values.StringValueDayOfWeek;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.FormBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class DailyPanel extends JPanel implements RepeatPanel {

    private JRadioButton everyDay;
    private JRadioButton everyWeekday;
    private JRadioButton everyWeekend;
    private JRadioButton everyXDays;
    private JSpinner everyXDays_X;
    private JRadioButton everyDayOfWeek;
    private JComboBox everyDayOfWeek_X;

    public DailyPanel() {
        this.initialize();
    }

    @Override
    public Repeat getRepeat() {
        if (this.everyDay.isSelected())
            return new RepeatEveryX(Calendar.DAY_OF_MONTH, 1);

        if (this.everyWeekday.isSelected())
            return new RepeatEveryXWeekOnDays(1, new int[]{2, 3, 4, 5, 6});

        if (this.everyWeekend.isSelected())
            return new RepeatEveryXWeekOnDays(1, new int[]{7, 1});

        if (this.everyXDays.isSelected())
            return new RepeatEveryX(Calendar.DAY_OF_MONTH, (Integer) everyXDays_X.getValue());

        if (this.everyDayOfWeek.isSelected())
            return new RepeatEveryXWeekOnDays(1, new int[]{(Integer) everyDayOfWeek_X.getSelectedItem()});

        return null;
    }

    @Override
    public boolean setRepeat(Repeat repeat) {
        this.everyXDays_X.setValue(1);
        this.everyDayOfWeek_X.setSelectedItem(Calendar.MONDAY);

        if (repeat == null)
            return false;

        if (repeat instanceof RepeatEveryX) {
            RepeatEveryX r = (RepeatEveryX) repeat;

            if (r.getType() == Calendar.DAY_OF_MONTH) {
                if (r.getValue() == 1) {
                    this.everyDay.setSelected(true);
                    return true;
                }

                this.everyXDays.setSelected(true);
                this.everyXDays_X.setValue(r.getValue());
                return true;
            }
        }

        if (repeat instanceof RepeatEveryXWeekOnDays) {
            RepeatEveryXWeekOnDays r = (RepeatEveryXWeekOnDays) repeat;

            if (r.getValue() == 1) {
                if (r.getDays().length == 5 &&
                        ArrayUtils.contains(r.getDays(), 2) &&
                        ArrayUtils.contains(r.getDays(), 3) &&
                        ArrayUtils.contains(r.getDays(), 4) &&
                        ArrayUtils.contains(r.getDays(), 5) &&
                        ArrayUtils.contains(r.getDays(), 6)) {
                    this.everyWeekday.setSelected(true);
                    return true;
                }

                if (r.getDays().length == 2 &&
                        ArrayUtils.contains(r.getDays(), 7) &&
                        ArrayUtils.contains(r.getDays(), 1)) {
                    this.everyWeekend.setSelected(true);
                    return true;
                }

                if (r.getDays().length == 1) {
                    this.everyDayOfWeek.setSelected(true);
                    this.everyDayOfWeek_X.setSelectedItem(r.getDays()[0]);
                    return true;
                }
            }
        }

        return false;
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        FormBuilder builder = new FormBuilder("right:pref, 4dlu, fill:default:grow");

        ButtonGroup group = new ButtonGroup();
        JPanel panel;

        // Every day
        everyDay = new JRadioButton();
        group.add(everyDay);

        builder.append(everyDay);
        builder.append(new JLabel(Translations.getString("repeat.daily.every_day")));

        // Every weekday
        everyWeekday = new JRadioButton();
        group.add(everyWeekday);

        builder.append(everyWeekday);
        builder.append(new JLabel(Translations.getString("repeat.daily.every_weekday")));

        // Every weekend
        everyWeekend = new JRadioButton();
        group.add(everyWeekend);

        builder.append(everyWeekend);
        builder.append(new JLabel(Translations.getString("repeat.daily.every_weekend")));

        // Every X days
        everyXDays = new JRadioButton();
        group.add(everyXDays);

        everyXDays_X = new JSpinner();
        everyXDays_X.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        everyXDays_X.setEditor(new JSpinner.NumberEditor(everyXDays_X, "0"));

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_days.part1")));
        panel.add(everyXDays_X);
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_days.part2")));

        builder.append(everyXDays);
        builder.append(panel);

        // Every day of week
        everyDayOfWeek = new JRadioButton();
        group.add(everyDayOfWeek);

        everyDayOfWeek_X = this.getDaysOfWeekComboBox();

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(Translations.getString("repeat.daily.every_day_of_week.part1")));
        panel.add(everyDayOfWeek_X);
        panel.add(new JLabel(Translations.getString("repeat.daily.every_day_of_week.part2")));

        builder.append(everyDayOfWeek);
        builder.append(panel);

        this.add(builder.getPanel(), BorderLayout.CENTER);
    }

    private JComboBox getDaysOfWeekComboBox() {
        JXComboBox cb = new JXComboBox(new Integer[]{
                Calendar.MONDAY,
                Calendar.TUESDAY,
                Calendar.WEDNESDAY,
                Calendar.THURSDAY,
                Calendar.FRIDAY,
                Calendar.SATURDAY,
                Calendar.SUNDAY
        });

        cb.setRenderer(new DefaultListRenderer(StringValueDayOfWeek.INSTANCE));

        return cb;
    }

}
