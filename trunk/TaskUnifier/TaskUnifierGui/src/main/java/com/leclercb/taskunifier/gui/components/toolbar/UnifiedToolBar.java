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
package com.leclercb.taskunifier.gui.components.toolbar;

import com.explodingpixels.border.FocusStateMatteBorder;
import com.explodingpixels.macwidgets.MacColorUtils;
import com.explodingpixels.macwidgets.MacPainterFactory;
import com.explodingpixels.macwidgets.TriAreaComponent;
import com.explodingpixels.util.PlatformUtils;
import com.explodingpixels.widgets.WindowDragger;
import com.explodingpixels.widgets.WindowUtils;
import com.jgoodies.forms.factories.Borders;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;

public class UnifiedToolBar {

    private TriAreaComponent fUnifiedToolBar;

    public UnifiedToolBar() {
        try {
            Constructor<TriAreaComponent> c = TriAreaComponent.class.getConstructor(Integer.class);
            this.fUnifiedToolBar = c.newInstance(4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fixUnifiedToolBarOnMacIfNeccessary(this.fUnifiedToolBar);
        this.fUnifiedToolBar.getComponent().setBorder(
                Borders.createEmptyBorder("3dlu, 4dlu, 3dlu, 4dlu"));
        installUnifiedToolBarBorder(this.fUnifiedToolBar.getComponent());
        WindowUtils.installJComponentRepainterOnWindowFocusChanged(this.fUnifiedToolBar.getComponent());
    }

    public void addComponentToLeft(JComponent toolToAdd) {
        this.fUnifiedToolBar.addComponentToLeft(toolToAdd);
    }

    public void addComponentToCenter(JComponent toolToAdd) {
        this.fUnifiedToolBar.addComponentToCenter(toolToAdd);
    }

    public void addComponentToRight(JComponent toolToAdd) {
        this.fUnifiedToolBar.addComponentToRight(toolToAdd);
    }

    public void installWindowDraggerOnWindow(Window window) {
        new WindowDragger(window, this.getComponent());
    }

    public JComponent getComponent() {
        return this.fUnifiedToolBar.getComponent();
    }

    public void disableBackgroundPainter() {
        // TODO
        //this.fUnifiedToolBar.setBackgroundPainter(null);
    }

    private static void fixUnifiedToolBarOnMacIfNeccessary(
            TriAreaComponent unifiedToolBar) {
        // TODO
        //unifiedToolBar.setBackgroundPainter(MacPainterFactory.createTexturedWindowWorkaroundPainter());
    }

    static void installUnifiedToolBarBorder(final JComponent component) {
        FocusStateMatteBorder border = new FocusStateMatteBorder(
                0,
                0,
                1,
                0,
                new Color(64, 64, 64),
                new Color(135, 135, 135),
                component);

        component.setBorder(BorderFactory.createCompoundBorder(
                border,
                component.getBorder()));
    }

}
