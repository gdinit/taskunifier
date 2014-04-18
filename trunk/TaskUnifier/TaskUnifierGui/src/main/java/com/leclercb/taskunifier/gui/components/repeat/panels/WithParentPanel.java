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
import com.leclercb.taskunifier.api.models.repeat.RepeatWithParent;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.FormBuilder;

import javax.swing.*;
import java.awt.*;

public class WithParentPanel extends JPanel implements RepeatPanel {

    private JRadioButton withParent;

    public WithParentPanel() {
        this.initialize();
    }

    @Override
    public Repeat getRepeat() {
        if (this.withParent.isSelected())
            return new RepeatWithParent();

        return null;
    }

    @Override
    public boolean setRepeat(Repeat repeat) {
        if (repeat == null)
            return false;

        if (repeat instanceof RepeatWithParent) {
            this.withParent.setSelected(true);
            return true;
        }

        return false;
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        FormBuilder builder = new FormBuilder("right:pref, 4dlu, fill:default:grow");

        ButtonGroup group = new ButtonGroup();
        JPanel panel;

        // With parent
        withParent = new JRadioButton();
        group.add(withParent);

        builder.append(withParent);
        builder.append(new JLabel(Translations.getString("repeat.with_parent")));

        this.add(builder.getPanel(), BorderLayout.CENTER);
    }

}
