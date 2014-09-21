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
package com.leclercb.taskunifier.gui.main.frames;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.actions.ActionQuit;
import com.leclercb.taskunifier.gui.components.quickaddtask.QuickAddTaskDialog;
import com.leclercb.taskunifier.gui.components.traypopup.TrayPopup;
import com.leclercb.taskunifier.gui.components.views.ViewItem;
import com.leclercb.taskunifier.gui.components.views.ViewList;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import org.apache.commons.lang3.SystemUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class FrameUtils {

    private FrameUtils() {

    }

    private static Provider PROVIDER;

    private static Window CURRENT_WINDOW;

    private static int FRAME_ID = 0;

    private static List<FrameView> FRAMES = new ArrayList<FrameView>();

    public static int getFrameCount() {
        return FRAMES.size();
    }

    public static List<FrameView> getFrameViews() {
        return new ArrayList<FrameView>(FRAMES);
    }

    public static FrameView createFrameView() {
        boolean isFirstWindow = (FRAME_ID == 0);

        String propertyName = "window.main";

        if (!isFirstWindow)
            propertyName = "window.sub." + FRAME_ID;

        MainFrame frame = new MainFrame(FRAME_ID, propertyName);

        FRAME_ID++;

        FRAMES.add(frame);

        frame.setVisible(true);
        frame.requestFocus();

        if (isFirstWindow) {
            initializeGlobalHotKey();
            initializeSystemTray();
        }

        return frame;
    }

    public static void deleteFrameView(FrameView frame, boolean confirmQuit) {
        if (!FRAMES.contains(frame))
            return;

        if (getFrameCount() == 1 && confirmQuit)
            if (!ActionQuit.confirmQuit())
                return;

        FRAMES.remove(frame);

        saveFrameViewTabs(frame.getFrameId());

        for (ViewItem view : ViewList.getInstance().getViews()) {
            if (frame.getFrameId() == view.getFrameId()) {
                ViewList.getInstance().removeView(view);
            }
        }

        frame.getFrame().dispose();

        if (getFrameCount() == 0)
            ActionQuit.quit();
    }

    public static Window getCurrentWindow() {
        return CURRENT_WINDOW;
    }

    public static void setCurrentWindow(Window window) {
        CURRENT_WINDOW = window;
    }

    public static Frame getCurrentFrame() {
        if (getCurrentFrameView() != null)
            return getCurrentFrameView().getFrame();

        return null;
    }

    public static FrameView getCurrentFrameView() {
        if (ViewList.getInstance().getCurrentView() == null)
            return null;

        int frameId = ViewList.getInstance().getCurrentView().getFrameId();
        return getFrameView(frameId);
    }

    public static FrameView getFrameView(int frameId) {
        for (FrameView frame : getFrameViews()) {
            if (frameId == frame.getFrameId())
                return frame;
        }

        return null;
    }

    private static void initializeGlobalHotKey() {
        if (!SystemUtils.IS_OS_WINDOWS && !SystemUtils.IS_OS_MAC)
            return;

        try {
            PROVIDER = Provider.getCurrentProvider(true);

            registerGlobalHotKey();

            Main.getSettings().addPropertyChangeListener(
                    "general.global_hot_key.quick_task",
                    new PropertyChangeListener() {

                        @Override
                        public void propertyChange(PropertyChangeEvent event) {
                            unregisterGlobalHotKey();
                            registerGlobalHotKey();
                        }

                    });

            Main.getActionSupport().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    if (event.getActionCommand().equals("BEFORE_EXIT")) {
                        if (PROVIDER != null) {
                            PROVIDER.reset();
                            PROVIDER.stop();
                        }
                    }
                }

            });
        } catch (Throwable t) {
            GuiLogger.getLogger().log(
                    Level.WARNING,
                    "Cannot initialize global hot key provider",
                    t);
        }
    }

    private static void registerGlobalHotKey() {
        try {
            ShortcutKey key = Main.getSettings().getObjectProperty(
                    "general.global_hot_key.quick_task",
                    ShortcutKey.class);

            if (key == null || PROVIDER == null)
                return;

            PROVIDER.register(key.getKeyStroke(), new HotKeyListener() {

                @Override
                public void onHotKey(HotKey key) {
                    try {
                        QuickAddTaskDialog.getInstance().setVisible(true);
                    } catch (Exception e) {
                        GuiLogger.getLogger().log(
                                Level.WARNING,
                                "Cannot open quick add task dialog",
                                e);
                    }
                }

            });

            GuiLogger.getLogger().info("Global hot key registered");
        } catch (Throwable t) {
            GuiLogger.getLogger().log(
                    Level.WARNING,
                    "Cannot register global hot key",
                    t);
        }
    }

    private static void unregisterGlobalHotKey() {
        try {
            if (PROVIDER != null)
                PROVIDER.reset();

            GuiLogger.getLogger().info("Global hot key unregistered");
        } catch (Throwable t) {
            GuiLogger.getLogger().log(
                    Level.WARNING,
                    "Cannot unregister global hot key",
                    t);
        }
    }

    private static void initializeSystemTray() {
        if (!SystemTray.isSupported()
                || !Main.getSettings().getBooleanProperty(
                "window.minimize_to_system_tray")) {
            return;
        }

        final SystemTray tray = SystemTray.getSystemTray();
        final TrayIcon trayIcon = new TrayIcon(ImageUtils.getResourceImage(
                "logo.png",
                (int) tray.getTrayIconSize().getWidth(),
                (int) tray.getTrayIconSize().getHeight()).getImage());

        trayIcon.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (FrameView frame : getFrameViews()) {
                    frame.getFrame().setVisible(true);
                    frame.getFrame().setState(Frame.NORMAL);
                }
            }

        });

        trayIcon.setPopupMenu(new TrayPopup());

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            GuiLogger.getLogger().log(Level.WARNING, "Cannot add tray icon", e);
        }
    }

    public static void saveFrameViewTabs(int frameId) {
        ViewItem[] viewArray = ViewList.getInstance().getViews(frameId);

        if (viewArray.length == 0)
            return;

        StringBuffer views = new StringBuffer();
        StringBuffer labels = new StringBuffer();

        for (ViewItem view : viewArray) {
            views.append(view.getViewType().name() + ";");
            labels.append(view.getLabel() + ";");
        }

        int newFrameId = 0;
        for (FrameView frame : FRAMES)
            if (frame.getFrameId() < frameId)
                newFrameId++;

        Main.getSettings().setStringProperty(
                "window.views." + newFrameId + ".types",
                views.toString());

        Main.getSettings().setStringProperty(
                "window.views." + newFrameId + ".labels",
                labels.toString());
    }

}
