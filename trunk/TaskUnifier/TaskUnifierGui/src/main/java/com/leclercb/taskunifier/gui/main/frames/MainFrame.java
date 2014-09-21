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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.SystemUtils;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXStatusBar;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.properties.events.WeakSavePropertiesListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.commons.gui.swing.lookandfeel.LookAndFeelUtils;
import com.leclercb.commons.gui.utils.ScreenUtils;
import com.leclercb.taskunifier.gui.actions.ActionAddTab;
import com.leclercb.taskunifier.gui.actions.ActionCloseTab;
import com.leclercb.taskunifier.gui.actions.ActionRenameTab;
import com.leclercb.taskunifier.gui.components.menubar.MenuBar;
import com.leclercb.taskunifier.gui.components.statusbar.DefaultStatusBar;
import com.leclercb.taskunifier.gui.components.statusbar.MacStatusBar;
import com.leclercb.taskunifier.gui.components.statusbar.StatusBar;
import com.leclercb.taskunifier.gui.components.toolbar.DefaultToolBar;
import com.leclercb.taskunifier.gui.components.toolbar.MacToolBar;
import com.leclercb.taskunifier.gui.components.views.ViewItem;
import com.leclercb.taskunifier.gui.components.views.ViewList;
import com.leclercb.taskunifier.gui.components.views.ViewType;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class MainFrame extends JXFrame implements FrameView, SavePropertiesListener, PropertyChangeSupported {
	
	private boolean firstTimeVisible;
	
	private int frameId;
	private String propertyName;
	private JTabbedPane mainTabbedPane;
	private ViewItem oldSelectedView;
	
	private JPopupMenu tabPopupMenu;
	
	private ListChangeListener viewListListChangeListener;
	private PropertyChangeListener viewListPropertyChangeListener;
	
	protected MainFrame(int frameId, String propertyName) {
		CheckUtils.isNotNull(propertyName);
		
		this.firstTimeVisible = true;
		this.frameId = frameId;
		this.propertyName = propertyName;
	}
	
	@Override
	public int getFrameId() {
		return this.frameId;
	}
	
	@Override
	public void setVisible(boolean b) {
		if (this.firstTimeVisible) {
			this.initialize();
			this.firstTimeVisible = false;
		}
		
		super.setVisible(b);
		
		if (b)
			this.repaint();
	}
	
	@Override
	public void dispose() {
		this.saveProperties();
		this.setVisible(false);
		
		super.dispose();
	}
	
	private void initialize() {
		Main.getSettings().addSavePropertiesListener(
				new WeakSavePropertiesListener(Main.getSettings(), this));
		
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setIconImage(ImageUtils.getResourceImage("logo.png").getImage());
		
		if (Main.isProVersion())
			this.setTitle(Constants.TITLE_PRO + " - " + Constants.getVersion());
		else
			this.setTitle(Constants.TITLE + " - " + Constants.getVersion());
		
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent event) {
				FrameUtils.deleteFrameView(MainFrame.this, true);
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				if (Main.getSettings().getBooleanProperty(
						"window.minimize_to_system_tray")) {
					MainFrame.this.setVisible(false);
				}
			}
			
			@Override
			public void windowDeiconified(WindowEvent event) {
				MainFrame.this.setVisible(true);
				MainFrame.this.setState(Frame.NORMAL);
			}
			
		});
		
		this.mainTabbedPane = new JTabbedPane();
		this.add(this.mainTabbedPane, BorderLayout.CENTER);
		
		this.initializeTabPopupMenu();
		this.initializeViews();
		
		this.mainTabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				ViewItem view = MainFrame.this.getSelectedView();
				MainFrame.this.setSelectedView(view);
			}
			
		});
		
		this.initializeMenuBar();
		this.initializeToolBar();
		this.initializeStatusBar();
		
		this.pack();
		this.loadWindowSettings();
	}
	
	private void initializeTabPopupMenu() {
		this.tabPopupMenu = new JPopupMenu();
		this.tabPopupMenu.add(new ActionRenameTab(16, 16));
		this.tabPopupMenu.add(new ActionCloseTab(16, 16));
		this.tabPopupMenu.addSeparator();
		
		JMenu addTabMenu = new JMenu(new ActionAddTab(16, 16));
		this.tabPopupMenu.add(addTabMenu);
		for (ViewType type : ViewType.values()) {
			addTabMenu.add(new JMenuItem(new ActionAddTab(16, 16, type)));
		}
	}
	
	private void initializeViews() {
		this.viewListListChangeListener = new ListChangeListener() {
			
			@Override
			public void listChange(ListChangeEvent event) {
				ViewItem view = (ViewItem) event.getValue();
				
				if (event.getChangeType() == ListChangeEvent.VALUE_ADDED) {
					MainFrame.this.addViewTab(view);
				}
				
				if (event.getChangeType() == ListChangeEvent.VALUE_REMOVED) {
					if (MainFrame.this.getFrameId() != view.getFrameId())
						return;
					
					int index = 0;
					for (int i = 0; i < event.getIndex(); i++) {
						if (MainFrame.this.getFrameId() == ViewList.getInstance().getView(
								i).getFrameId()) {
							index++;
						}
					}
					
					MainFrame.this.mainTabbedPane.removeTabAt(index);
					
					if (MainFrame.this.mainTabbedPane.getTabCount() == 0) {
						FrameUtils.deleteFrameView(MainFrame.this, false);
					}
				}
			}
			
		};
		
		ViewList.getInstance().addListChangeListener(
				new WeakListChangeListener(
						ViewList.getInstance(),
						this.viewListListChangeListener));
		
		this.viewListPropertyChangeListener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				ViewItem view = (ViewItem) event.getNewValue();
				MainFrame.this.setSelectedView(view);
			}
			
		};
		
		ViewList.getInstance().addPropertyChangeListener(
				ViewList.PROP_CURRENT_VIEW,
				new WeakPropertyChangeListener(
						ViewList.getInstance(),
						this.viewListPropertyChangeListener));
		
		this.addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent event) {
				
			}
			
			@Override
			public void windowGainedFocus(WindowEvent event) {
				FrameUtils.setCurrentWindow(MainFrame.this);
				
				ViewItem view = MainFrame.this.getSelectedView();
				
				if (view == null)
					return;
				
				ViewList.getInstance().setCurrentView(view);
			}
			
		});
	}
	
	@Override
	public Frame getFrame() {
		return this;
	}
	
	private void addViewTab(final ViewItem view) {
		CheckUtils.isNotNull(view);
		
		if (this.getFrameId() != view.getFrameId())
			return;
		
		this.mainTabbedPane.addTab("", view.getView().getViewContent());
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		
		final JLabel label = new JLabel(
				view.getLabel(),
				view.getIcon(),
				SwingConstants.LEFT);
		
		panel.add(label, BorderLayout.CENTER);
		
		panel.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent event) {
				MainFrame.this.setSelectedView(view);
				
				if (event.isPopupTrigger()
						|| event.getButton() == MouseEvent.BUTTON3) {
					MainFrame.this.tabPopupMenu.show(
							(Component) event.getSource(),
							0,
							0);
				}
			}
			
		});
		
		view.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ViewItem.PROP_ICON))
					label.setIcon(view.getIcon());
				
				if (evt.getPropertyName().equals(ViewItem.PROP_LABEL))
					label.setText(view.getLabel());
			}
			
		});
		
		this.mainTabbedPane.setTabComponentAt(
				this.mainTabbedPane.getTabCount() - 1,
				panel);
	}
	
	@Override
	public ViewItem getSelectedView() {
		int selectedIndex = this.mainTabbedPane.getSelectedIndex();
		
		if (selectedIndex == -1)
			return null;
		
		int currentFrameIndex = 0;
		int viewIndex = 0;
		
		int listLength = ViewList.getInstance().getViewCount();
		for (int i = 0; i < listLength; i++) {
			if (selectedIndex == currentFrameIndex
					&& MainFrame.this.getFrameId() == ViewList.getInstance().getView(
							i).getFrameId())
				break;
			
			viewIndex++;
			
			if (MainFrame.this.getFrameId() == ViewList.getInstance().getView(i).getFrameId()) {
				currentFrameIndex++;
			}
		}
		
		return ViewList.getInstance().getView(viewIndex);
	}
	
	@Override
	public void setSelectedView(ViewItem view) {
		if (view == null)
			return;
		
		if (this.getFrameId() != view.getFrameId())
			return;
		
		this.requestFocus();
		
		ViewList.getInstance().setCurrentView(view);
		
		int listIndex = ViewList.getInstance().getIndexOf(view);
		int index = 0;
		for (int i = 0; i < listIndex; i++) {
			if (MainFrame.this.getFrameId() == ViewList.getInstance().getView(i).getFrameId()) {
				index++;
			}
		}
		
		this.mainTabbedPane.setSelectedIndex(index);
		
		if (Main.isProVersion())
			this.setTitle(Constants.TITLE_PRO
					+ " - "
					+ Constants.getVersion()
					+ " - "
					+ view.getLabel());
		else
			this.setTitle(Constants.TITLE
					+ " - "
					+ Constants.getVersion()
					+ " - "
					+ view.getLabel());
		
		this.firePropertyChange(PROP_SELECTED_VIEW, this.oldSelectedView, view);
		this.oldSelectedView = view;
	}
	
	private void loadWindowSettings() {
		try {
			int extendedState = Main.getSettings().getIntegerProperty(
					this.propertyName + ".extended_state",
					Frame.MAXIMIZED_BOTH);
			int width = Main.getSettings().getIntegerProperty(
					this.propertyName + ".width",
					1024);
			int height = Main.getSettings().getIntegerProperty(
					this.propertyName + ".height",
					768);
			int locationX = Main.getSettings().getIntegerProperty(
					this.propertyName + ".location_x",
					0);
			int locationY = Main.getSettings().getIntegerProperty(
					this.propertyName + ".location_y",
					0);
			
			if (SystemUtils.IS_OS_MAC
					&& EqualsUtils.equalsStringIgnoreCase(
							System.getProperty("com.leclercb.taskunifier.mac_app_store"),
							"true"))
				this.setExtendedState(Frame.NORMAL);
			else
				this.setExtendedState(extendedState);
			
			this.setSize(width, height);
			
			if (ScreenUtils.isLocationInScreen(new Point(locationX, locationY)))
				this.setLocation(locationX, locationY);
			else
				this.setLocation(0, 0);
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Cannot load window settings",
					e);
		}
	}
	
	@Override
	public void saveProperties() {
		try {
			if (this.isVisible()) {
				Main.getSettings().setIntegerProperty(
						this.propertyName + ".extended_state",
						this.getExtendedState());
				Main.getSettings().setIntegerProperty(
						this.propertyName + ".width",
						this.getWidth());
				Main.getSettings().setIntegerProperty(
						this.propertyName + ".height",
						this.getHeight());
				Main.getSettings().setIntegerProperty(
						this.propertyName + ".location_x",
						(int) this.getLocationOnScreen().getX());
				Main.getSettings().setIntegerProperty(
						this.propertyName + ".location_y",
						(int) this.getLocationOnScreen().getY());
				
				FrameUtils.saveFrameViewTabs(this.getFrameId());
			}
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Cannot save window settings",
					e);
		}
	}
	
	private void initializeMenuBar() {
		this.setJMenuBar(new MenuBar());
	}
	
	private void initializeToolBar() {
		if (Main.getSettings().getBooleanProperty("general.toolbar.hide"))
			return;
		
		if (SystemUtils.IS_OS_MAC && LookAndFeelUtils.isSytemLookAndFeel()) {
			this.add(new MacToolBar().getComponent(), BorderLayout.NORTH);
		} else {
			this.add(new DefaultToolBar(), BorderLayout.NORTH);
		}
	}
	
	private void initializeStatusBar() {
		StatusBar statusBar = null;
		
		if (SystemUtils.IS_OS_MAC && LookAndFeelUtils.isSytemLookAndFeel())
			statusBar = new MacStatusBar(this.getFrameId());
		else
			statusBar = new DefaultStatusBar(this.getFrameId());
		
		if (statusBar.getStatusBar() instanceof JXStatusBar)
			this.setStatusBar((JXStatusBar) statusBar.getStatusBar());
		else
			this.add(statusBar.getStatusBar(), BorderLayout.SOUTH);
	}
	
}
