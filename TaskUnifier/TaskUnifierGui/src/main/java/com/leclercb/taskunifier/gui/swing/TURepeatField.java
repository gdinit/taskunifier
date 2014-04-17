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
package com.leclercb.taskunifier.gui.swing;

import com.leclercb.taskunifier.api.models.repeat.Repeat;
import com.leclercb.taskunifier.gui.commons.values.StringValueRepeat;
import com.leclercb.taskunifier.gui.components.repeat.RepeatDialog;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TURepeatField extends JPanel {

    public static final String PROP_REPEAT = "repeat";

    private JTextField label;
    private JButton button;
    private Repeat repeat;

    public TURepeatField() {
        this.initialize();
    }

    public Repeat getRepeat() {
        return this.repeat;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
        this.label.setText(StringValueRepeat.INSTANCE.getString(repeat));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.label.setEnabled(enabled);
        this.button.setEnabled(enabled);
    }

    private void initialize() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        this.label = new JTextField();
        this.label.setEditable(false);

        this.button = new JButton(ImageUtils.getResourceImage("repeat.png", 16, 16));
        this.button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Repeat oldRepeat = TURepeatField.this.repeat;

                RepeatDialog dialog = new RepeatDialog();
                dialog.setRepeat(TURepeatField.this.repeat);
                dialog.setVisible(true);
                TURepeatField.this.repeat = dialog.getRepeat();
                dialog.dispose();

                TURepeatField.this.label.setText(StringValueRepeat.INSTANCE.getString(TURepeatField.this.repeat));

                TURepeatField.this.firePropertyChange(TURepeatField.PROP_REPEAT, oldRepeat, TURepeatField.this.repeat);
            }

        });

        this.add(this.label, BorderLayout.CENTER);
        this.add(this.button, BorderLayout.EAST);
    }

}
