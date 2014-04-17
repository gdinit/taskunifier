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
package com.leclercb.taskunifier.gui.components.repeat;

import com.leclercb.taskunifier.api.models.repeat.Repeat;
import com.leclercb.taskunifier.api.models.repeat.RepeatWithParent;
import com.leclercb.taskunifier.gui.components.repeat.panels.DailyPanel;
import com.leclercb.taskunifier.gui.components.repeat.panels.MonthlyPanel;
import com.leclercb.taskunifier.gui.components.repeat.panels.WeeklyPanel;
import com.leclercb.taskunifier.gui.components.repeat.panels.YearlyPanel;
import com.leclercb.taskunifier.gui.swing.TUDialogPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUOkButton;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class RepeatDialogPanel extends TUDialogPanel {

    private static RepeatDialogPanel INSTANCE;

    protected static RepeatDialogPanel getInstance() {
        if (INSTANCE == null)
            INSTANCE = new RepeatDialogPanel();

        return INSTANCE;
    }

    private ActionListener okListener;
    private ActionListener noRepeatListener;
    private ActionListener withParentListener;

    private JTabbedPane tabbedPane;

    private DailyPanel dailyPanel;
    private WeeklyPanel weeklyPanel;
    private MonthlyPanel monthlyPanel;
    private YearlyPanel yearlyPanel;

    private Repeat repeat;

    private RepeatDialogPanel() {
        this.repeat = null;

        this.initialize();
    }

    public Repeat getRepeat() {
        return this.repeat;
    }

    private Repeat getRepeatFromPanels() {
        switch (this.tabbedPane.getSelectedIndex()) {
            case 0:
                return dailyPanel.getRepeat();
            case 1:
                return weeklyPanel.getRepeat();
            case 2:
                return monthlyPanel.getRepeat();
            case 3:
                return yearlyPanel.getRepeat();
            default:
                return null;
        }
    }

    public void setRepeat(Repeat repeat) {
        if (dailyPanel.setRepeat(repeat)) {
            tabbedPane.setSelectedIndex(0);
            return;
        }

        if (weeklyPanel.setRepeat(repeat)) {
            tabbedPane.setSelectedIndex(1);
            return;
        }

        if (monthlyPanel.setRepeat(repeat)) {
            tabbedPane.setSelectedIndex(2);
            return;
        }

        if (weeklyPanel.setRepeat(repeat)) {
            tabbedPane.setSelectedIndex(3);
            return;
        }
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        this.tabbedPane = new JTabbedPane();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(this.tabbedPane, BorderLayout.CENTER);

        this.add(panel, BorderLayout.CENTER);

        this.initializeButtonsPanel();

        this.initializeDailyPanel();
        this.initializeWeeklyPanel();
        this.initializeMonthlyPanel();
        this.initializeYearlyPanel();
    }

    private void initializeButtonsPanel() {
        this.okListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                RepeatDialogPanel.this.repeat = RepeatDialogPanel.this.getRepeatFromPanels();
                RepeatDialogPanel.this.getDialog().setVisible(false);
            }

        };

        this.noRepeatListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                RepeatDialogPanel.this.repeat = null;
                RepeatDialogPanel.this.getDialog().setVisible(false);
            }

        };

        this.withParentListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                RepeatDialogPanel.this.repeat = new RepeatWithParent();
                RepeatDialogPanel.this.getDialog().setVisible(false);
            }

        };

        JButton okButton = new TUOkButton(this.okListener);

        JButton noRepeatButton = new JButton(Translations.getString("repeat.no_repeat"));
        noRepeatButton.addActionListener(this.noRepeatListener);

        JButton withParentButton = new JButton(Translations.getString("repeat.with_parent"));
        withParentButton.addActionListener(this.withParentListener);

        this.setButtons(okButton, okButton, noRepeatButton, withParentButton);
    }

    private void initializeDailyPanel() {
        this.dailyPanel = new DailyPanel();
        this.tabbedPane.addTab(
                Translations.getString("repeat.tab.daily"),
                ComponentFactory.createJScrollPane(
                        this.dailyPanel,
                        false,
                        true));
    }

    private void initializeWeeklyPanel() {
        this.weeklyPanel = new WeeklyPanel();
        this.tabbedPane.addTab(
                Translations.getString("repeat.tab.weekly"),
                ComponentFactory.createJScrollPane(
                        this.weeklyPanel,
                        false,
                        true));
    }

    private void initializeMonthlyPanel() {
        this.monthlyPanel = new MonthlyPanel();
        this.tabbedPane.addTab(
                Translations.getString("repeat.tab.monthly"),
                ComponentFactory.createJScrollPane(
                        this.monthlyPanel,
                        false,
                        true));
    }

    private void initializeYearlyPanel() {
        this.yearlyPanel = new YearlyPanel();
        this.tabbedPane.addTab(
                Translations.getString("repeat.tab.yearly"),
                ComponentFactory.createJScrollPane(
                        this.yearlyPanel,
                        false,
                        true));
    }

    @Override
    protected void dialogLoaded() {
        this.getDialog().getRootPane().registerKeyboardAction(
                this.okListener,
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_S,
                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.getDialog().getRootPane().registerKeyboardAction(
                this.noRepeatListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

}
