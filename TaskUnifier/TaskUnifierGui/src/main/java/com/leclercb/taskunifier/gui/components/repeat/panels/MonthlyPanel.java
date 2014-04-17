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
import com.leclercb.taskunifier.gui.utils.FormBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class MonthlyPanel extends JPanel implements RepeatPanel {

    private JRadioButton everyXMonths;
    private JSpinner everyXMonths_X;

    public MonthlyPanel() {
        this.initialize();
    }

    @Override
    public Repeat getRepeat() {
        if (this.everyXMonths.isSelected())
            return new RepeatEveryX(Calendar.MONTH, (Integer) everyXMonths_X.getValue());

        return null;
    }

    @Override
    public boolean setRepeat(Repeat repeat) {
        this.everyXMonths_X.setValue(1);

        if (repeat instanceof RepeatEveryX) {
            RepeatEveryX r = (RepeatEveryX) repeat;

            if (r.getType() == Calendar.YEAR) {
                this.everyXMonths.setSelected(true);
                this.everyXMonths_X.setValue(r.getValue());
                return true;
            }
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
        panel.add(new JLabel("Every"));
        panel.add(everyXMonths_X);
        panel.add(new JLabel("month(s)"));

        builder.append(everyXMonths);
        builder.append(panel);

        this.add(builder.getPanel(), BorderLayout.CENTER);
    }

}
