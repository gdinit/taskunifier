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
    private JRadioButton everyXWeeksOnDayX;
    private JSpinner everyXWeeksOnDayX_X;
    private JCheckBox everyXWeeksOnDayX_MO;
    private JCheckBox everyXWeeksOnDayX_TU;
    private JCheckBox everyXWeeksOnDayX_WE;
    private JCheckBox everyXWeeksOnDayX_TH;
    private JCheckBox everyXWeeksOnDayX_FR;
    private JCheckBox everyXWeeksOnDayX_SA;
    private JCheckBox everyXWeeksOnDayX_SU;

    public WeeklyPanel() {
        this.initialize();
    }

    @Override
    public Repeat getRepeat() {
        if (this.everyXWeeks.isSelected())
            return new RepeatEveryX(Calendar.WEEK_OF_YEAR, (Integer) everyXWeeks_X.getValue());

        if (this.everyXWeeksOnDayX.isSelected()) {
            int[] days = new int[0];

            if (everyXWeeksOnDayX_MO.isSelected())
                days = ArrayUtils.add(days, Calendar.MONDAY);

            if (everyXWeeksOnDayX_TU.isSelected())
                days = ArrayUtils.add(days, Calendar.TUESDAY);

            if (everyXWeeksOnDayX_WE.isSelected())
                days = ArrayUtils.add(days, Calendar.WEDNESDAY);

            if (everyXWeeksOnDayX_TH.isSelected())
                days = ArrayUtils.add(days, Calendar.THURSDAY);

            if (everyXWeeksOnDayX_FR.isSelected())
                days = ArrayUtils.add(days, Calendar.FRIDAY);

            if (everyXWeeksOnDayX_SA.isSelected())
                days = ArrayUtils.add(days, Calendar.SATURDAY);

            if (everyXWeeksOnDayX_SU.isSelected())
                days = ArrayUtils.add(days, Calendar.SUNDAY);

            return new RepeatEveryXWeekOnDays((Integer) everyXWeeksOnDayX_X.getValue(), days);
        }

        return null;
    }

    @Override
    public boolean setRepeat(Repeat repeat) {
        this.everyXWeeks_X.setValue(1);
        this.everyXWeeksOnDayX_X.setValue(1);

        this.everyXWeeksOnDayX_MO.setSelected(false);
        this.everyXWeeksOnDayX_TU.setSelected(false);
        this.everyXWeeksOnDayX_WE.setSelected(false);
        this.everyXWeeksOnDayX_TH.setSelected(false);
        this.everyXWeeksOnDayX_FR.setSelected(false);
        this.everyXWeeksOnDayX_SA.setSelected(false);
        this.everyXWeeksOnDayX_SU.setSelected(false);

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

            this.everyXWeeksOnDayX.setSelected(true);
            this.everyXWeeksOnDayX_X.setValue(r.getValue());

            if (ArrayUtils.contains(r.getDays(), Calendar.MONDAY))
                this.everyXWeeksOnDayX_MO.setSelected(true);

            if (ArrayUtils.contains(r.getDays(), Calendar.TUESDAY))
                this.everyXWeeksOnDayX_TU.setSelected(true);

            if (ArrayUtils.contains(r.getDays(), Calendar.WEDNESDAY))
                this.everyXWeeksOnDayX_WE.setSelected(true);

            if (ArrayUtils.contains(r.getDays(), Calendar.THURSDAY))
                this.everyXWeeksOnDayX_TH.setSelected(true);

            if (ArrayUtils.contains(r.getDays(), Calendar.FRIDAY))
                this.everyXWeeksOnDayX_FR.setSelected(true);

            if (ArrayUtils.contains(r.getDays(), Calendar.SATURDAY))
                this.everyXWeeksOnDayX_SA.setSelected(true);

            if (ArrayUtils.contains(r.getDays(), Calendar.SUNDAY))
                this.everyXWeeksOnDayX_SU.setSelected(true);

            return true;
        }

        return false;
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        FormBuilder builder = new FormBuilder("right:pref, 4dlu, fill:default:grow");

        ButtonGroup group = new ButtonGroup();
        JPanel panel;

        // Every X days
        everyXWeeks = new JRadioButton();
        group.add(everyXWeeks);

        everyXWeeks_X = new JSpinner();
        everyXWeeks_X.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        everyXWeeks_X.setEditor(new JSpinner.NumberEditor(everyXWeeks_X, "0"));

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Every"));
        panel.add(everyXWeeks_X);
        panel.add(new JLabel("week(s)"));

        builder.append(everyXWeeks);
        builder.append(panel);

        // Every day of week
        everyXWeeksOnDayX = new JRadioButton();
        group.add(everyXWeeksOnDayX);

        everyXWeeksOnDayX_X = new JSpinner();
        everyXWeeksOnDayX_X.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        everyXWeeksOnDayX_X.setEditor(new JSpinner.NumberEditor(everyXWeeksOnDayX_X, "0"));

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Every"));
        panel.add(everyXWeeksOnDayX_X);

        builder.append(everyXWeeksOnDayX);
        builder.append(panel);

        this.everyXWeeksOnDayX_MO = new JCheckBox(Translations.getString("date.monday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDayX_MO);

        this.everyXWeeksOnDayX_TU = new JCheckBox(Translations.getString("date.tuesday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDayX_TU);

        this.everyXWeeksOnDayX_WE = new JCheckBox(Translations.getString("date.wednesday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDayX_WE);

        this.everyXWeeksOnDayX_TH = new JCheckBox(Translations.getString("date.thursday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDayX_TH);

        this.everyXWeeksOnDayX_FR = new JCheckBox(Translations.getString("date.friday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDayX_FR);

        this.everyXWeeksOnDayX_SA = new JCheckBox(Translations.getString("date.saturday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDayX_SA);

        this.everyXWeeksOnDayX_SU = new JCheckBox(Translations.getString("date.sunday"));
        builder.append(new JLabel());
        builder.append(this.everyXWeeksOnDayX_SU);

        this.add(builder.getPanel(), BorderLayout.CENTER);
    }

}
