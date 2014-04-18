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
import com.leclercb.taskunifier.api.models.repeat.RepeatEveryXMonthOnDayX;
import com.leclercb.taskunifier.api.models.repeat.RepeatEveryXMonthOnWeekX;
import com.leclercb.taskunifier.gui.commons.values.StringValueDayOfWeek;
import com.leclercb.taskunifier.gui.commons.values.StringValueWeekOfMonth;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.FormBuilder;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class MonthlyPanel extends JPanel implements RepeatPanel {

    private JRadioButton everyXMonths;
    private JSpinner everyXMonths_X;
    private JRadioButton everyXMonthsDayY;
    private JSpinner everyXMonthsDayY_X;
    private JSpinner everyXMonthsDayY_Y;
    private JRadioButton everyXMonthsDayYWeekZ;
    private JSpinner everyXMonthsDayYWeekZ_X;
    private JComboBox everyXMonthsDayYWeekZ_Y;
    private JComboBox everyXMonthsDayYWeekZ_Z;

    public MonthlyPanel() {
        this.initialize();
    }

    @Override
    public Repeat getRepeat() {
        if (this.everyXMonths.isSelected())
            return new RepeatEveryX(Calendar.MONTH, (Integer) everyXMonths_X.getValue());

        if (this.everyXMonthsDayY.isSelected())
            return new RepeatEveryXMonthOnDayX(
                    (Integer) everyXMonthsDayY_X.getValue(),
                    (Integer) everyXMonthsDayY_Y.getValue());

        if (this.everyXMonthsDayYWeekZ.isSelected())
            return new RepeatEveryXMonthOnWeekX(
                    (Integer) everyXMonthsDayYWeekZ_X.getValue(),
                    (Integer) everyXMonthsDayYWeekZ_Z.getSelectedItem(),
                    (Integer) everyXMonthsDayYWeekZ_Y.getSelectedItem());

        return null;
    }

    @Override
    public boolean setRepeat(Repeat repeat) {
        this.everyXMonths_X.setValue(1);
        this.everyXMonthsDayY_X.setValue(1);
        this.everyXMonthsDayY_Y.setValue(1);
        this.everyXMonthsDayYWeekZ_X.setValue(1);
        this.everyXMonthsDayYWeekZ_Y.setSelectedItem(Calendar.MONDAY);
        this.everyXMonthsDayYWeekZ_Z.setSelectedItem(1);

        if (repeat == null)
            return false;

        if (repeat instanceof RepeatEveryX) {
            RepeatEveryX r = (RepeatEveryX) repeat;

            if (r.getType() == Calendar.YEAR) {
                this.everyXMonths.setSelected(true);
                this.everyXMonths_X.setValue(r.getValue());
                return true;
            }
        }

        if (repeat instanceof RepeatEveryXMonthOnDayX) {
            RepeatEveryXMonthOnDayX r = (RepeatEveryXMonthOnDayX) repeat;

            this.everyXMonthsDayY.setSelected(true);
            this.everyXMonthsDayY_X.setValue(r.getValue());
            this.everyXMonthsDayY_Y.setValue(r.getDay());

            return true;
        }

        if (repeat instanceof RepeatEveryXMonthOnWeekX) {
            RepeatEveryXMonthOnWeekX r = (RepeatEveryXMonthOnWeekX) repeat;

            this.everyXMonthsDayYWeekZ.setSelected(true);
            this.everyXMonthsDayYWeekZ_X.setValue(r.getValue());
            this.everyXMonthsDayYWeekZ_Y.setSelectedItem(r.getDay());
            this.everyXMonthsDayYWeekZ_Z.setSelectedItem(r.getWeek());

            return true;
        }

        return false;
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        FormBuilder builder = new FormBuilder("right:pref, 4dlu, fill:default:grow");

        ButtonGroup group = new ButtonGroup();
        JPanel panel;

        // Every X months
        everyXMonths = new JRadioButton();
        group.add(everyXMonths);

        everyXMonths_X = new JSpinner();
        everyXMonths_X.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        everyXMonths_X.setEditor(new JSpinner.NumberEditor(everyXMonths_X, "0"));

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_months.part1")));
        panel.add(everyXMonths_X);
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_months.part2")));

        builder.append(everyXMonths);
        builder.append(panel);

        // Every X months day Y
        everyXMonthsDayY = new JRadioButton();
        group.add(everyXMonthsDayY);

        everyXMonthsDayY_X = new JSpinner();
        everyXMonthsDayY_X.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        everyXMonthsDayY_X.setEditor(new JSpinner.NumberEditor(everyXMonthsDayY_X, "0"));

        everyXMonthsDayY_Y = new JSpinner();
        everyXMonthsDayY_Y.setModel(new SpinnerNumberModel(1, 1, 31, 1));
        everyXMonthsDayY_Y.setEditor(new JSpinner.NumberEditor(everyXMonthsDayY_Y, "0"));

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_months_day_y.part1")));
        panel.add(everyXMonthsDayY_Y);
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_months_day_y.part2")));
        panel.add(everyXMonthsDayY_X);
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_months_day_y.part3")));

        builder.append(everyXMonthsDayY);
        builder.append(panel);

        // Every X months day Y
        everyXMonthsDayYWeekZ = new JRadioButton();
        group.add(everyXMonthsDayYWeekZ);

        everyXMonthsDayYWeekZ_X = new JSpinner();
        everyXMonthsDayYWeekZ_X.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        everyXMonthsDayYWeekZ_X.setEditor(new JSpinner.NumberEditor(everyXMonthsDayYWeekZ_X, "0"));

        everyXMonthsDayYWeekZ_Y = this.getDaysOfWeekComboBox();
        everyXMonthsDayYWeekZ_Z = this.getWeeksOfMonthComboBox();

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_months_day_y_week_z.part1")));
        panel.add(everyXMonthsDayYWeekZ_Z);
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_months_day_y_week_z.part2")));
        panel.add(everyXMonthsDayYWeekZ_Y);
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_months_day_y_week_z.part3")));
        panel.add(everyXMonthsDayYWeekZ_X);
        panel.add(new JLabel(Translations.getString("repeat.daily.every_x_months_day_y_week_z.part4")));

        builder.append(everyXMonthsDayYWeekZ);
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

    private JComboBox getWeeksOfMonthComboBox() {
        JXComboBox cb = new JXComboBox(new Integer[]{1, 2, 3, 4, -1});

        cb.setRenderer(new DefaultListRenderer(StringValueWeekOfMonth.INSTANCE));

        return cb;
    }

}
