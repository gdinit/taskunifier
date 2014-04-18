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
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.FormBuilder;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class WeeklyPanel extends JPanel implements RepeatPanel {

    private JRadioButton everyXWeeks;
    private JSpinner everyXWeeks_X;
    private JRadioButton everyXWeeksOnDaysX;
    private JSpinner everyXWeeksOnDaysX_X;
    private JCheckBox everyXWeeksOnDaysX_MO;
    private JCheckBox everyXWeeksOnDaysX_TU;
    private JCheckBox everyXWeeksOnDaysX_WE;
    private JCheckBox everyXWeeksOnDaysX_TH;
    private JCheckBox everyXWeeksOnDaysX_FR;
    private JCheckBox everyXWeeksOnDaysX_SA;
    private JCheckBox everyXWeeksOnDaysX_SU;

    public WeeklyPanel() {
        this.initialize();
    }

    @Override
    public Repeat getRepeat() {
        if (this.everyXWeeks.isSelected())
            return new RepeatEveryX(Calendar.WEEK_OF_YEAR, (Integer) everyXWeeks_X.getValue());

        if (this.everyXWeeksOnDaysX.isSelected()) {
            int[] days = new int[0];

            if (everyXWeeksOnDaysX_MO.isSelected())
                days = ArrayUtils.add(days, Calendar.MONDAY);

            if (everyXWeeksOnDaysX_TU.isSelected())
                days = ArrayUtils.add(days, Calendar.TUESDAY);

            if (everyXWeeksOnDaysX_WE.isSelected())
                days = ArrayUtils.add(days, Calendar.WEDNESDAY);

            if (everyXWeeksOnDaysX_TH.isSelected())
                days = ArrayUtils.add(days, Calendar.THURSDAY);

            if (everyXWeeksOnDaysX_FR.isSelected())
                days = ArrayUtils.add(days, Calendar.FRIDAY);

            if (everyXWeeksOnDaysX_SA.isSelected())
                days = ArrayUtils.add(days, Calendar.SATURDAY);

            if (everyXWeeksOnDaysX_SU.isSelected())
                days = ArrayUtils.add(days, Calendar.SUNDAY);

            return new RepeatEveryXWeekOnDays((Integer) everyXWeeksOnDaysX_X.getValue(), days);
        }

        return null;
    }

    @Override
    public boolean setRepeat(Repeat repeat) {
        this.everyXWeeks_X.setValue(1);
        this.everyXWeeksOnDaysX_X.setValue(1);

        this.everyXWeeksOnDaysX_MO.setSelected(false);
        this.everyXWeeksOnDaysX_TU.setSelected(false);
        this.everyXWeeksOnDaysX_WE.setSelected(false);
        this.everyXWeeksOnDaysX_TH.setSelected(false);
        this.everyXWeeksOnDaysX_FR.setSelected(false);
        this.everyXWeeksOnDaysX_SA.setSelected(false);
        this.everyXWeeksOnDaysX_SU.setSelected(false);

        if (repeat == null)
            return false;

        if (repeat instanceof RepeatEveryX) {
            RepeatEveryX r = (RepeatEveryX) repeat;

            if (r.getType() == Calendar.WEEK_OF_YEAR) {
                this.everyXWeeks.setSelected(true);
                this.everyXWeeks_X.setValue(r.getValue());
                return true;
            }
        }

        if (repeat instanceof RepeatEveryXWeekOnDays) {
            RepeatEveryXWeekOnDays r = (RepeatEveryXWeekOnDays) repeat;

            this.everyXWeeksOnDaysX.setSelected(true);
            this.everyXWeeksOnDaysX_X.setValue(r.getValue());

            if (ArrayUtils.contains(r.getDays(), Calendar.MONDAY))
                this.everyXWeeksOnDaysX_MO.setSelected(true);

            if (ArrayUtils.contains(r.getDays(), Calendar.TUESDAY))
                this.everyXWeeksOnDaysX_TU.setSelected(true);

            if (ArrayUtils.contains(r.getDays(), Calendar.WEDNESDAY))
                this.everyXWeeksOnDaysX_WE.setSelected(true);

            if (ArrayUtils.contains(r.getDays(), Calendar.THURSDAY))
                this.everyXWeeksOnDaysX_TH.setSelected(true);

            if (ArrayUtils.contains(r.getDays(), Calendar.FRIDAY))
                this.everyXWeeksOnDaysX_FR.setSelected(true);

            if (ArrayUtils.contains(r.getDays(), Calendar.SATURDAY))
                this.everyXWeeksOnDaysX_SA.setSelected(true);

            if (ArrayUtils.contains(r.getDays(), Calendar.SUNDAY))
                this.everyXWeeksOnDaysX_SU.setSelected(true);

            return true;
        }

        return false;
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        FormBuilder builder = new FormBuilder("right:pref, 4dlu, fill:default:grow");

        ButtonGroup group = new ButtonGroup();
        JPanel panel;

        // Every X weeks
        everyXWeeks = new JRadioButton();
        group.add(everyXWeeks);

        everyXWeeks_X = new JSpinner();
        everyXWeeks_X.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        everyXWeeks_X.setEditor(new JSpinner.NumberEditor(everyXWeeks_X, "0"));

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_weeks.part1")));
        panel.add(everyXWeeks_X);
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_weeks.part2")));

        builder.append(everyXWeeks);
        builder.append(panel);

        // Every X weeks on days X
        everyXWeeksOnDaysX = new JRadioButton();
        group.add(everyXWeeksOnDaysX);

        everyXWeeksOnDaysX_X = new JSpinner();
        everyXWeeksOnDaysX_X.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        everyXWeeksOnDaysX_X.setEditor(new JSpinner.NumberEditor(everyXWeeksOnDaysX_X, "0"));

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_weeks_on.part1")));
        panel.add(everyXWeeksOnDaysX_X);
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_weeks_on.part2")));

        builder.append(everyXWeeksOnDaysX);
        builder.append(panel);

        this.everyXWeeksOnDaysX_MO = new JCheckBox(Translations.getString("date.monday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDaysX_MO);

        this.everyXWeeksOnDaysX_TU = new JCheckBox(Translations.getString("date.tuesday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDaysX_TU);

        this.everyXWeeksOnDaysX_WE = new JCheckBox(Translations.getString("date.wednesday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDaysX_WE);

        this.everyXWeeksOnDaysX_TH = new JCheckBox(Translations.getString("date.thursday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDaysX_TH);

        this.everyXWeeksOnDaysX_FR = new JCheckBox(Translations.getString("date.friday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDaysX_FR);

        this.everyXWeeksOnDaysX_SA = new JCheckBox(Translations.getString("date.saturday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDaysX_SA);

        this.everyXWeeksOnDaysX_SU = new JCheckBox(Translations.getString("date.sunday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDaysX_SU);

        this.add(builder.getPanel(), BorderLayout.CENTER);
    }

}
