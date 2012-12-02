package com.leclercb.taskunifier.gui.components.toolbar;

import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import com.explodingpixels.border.FocusStateMatteBorder;
import com.explodingpixels.macwidgets.MacColorUtils;
import com.explodingpixels.macwidgets.MacPainterFactory;
import com.explodingpixels.macwidgets.TriAreaComponent;
import com.explodingpixels.widgets.WindowDragger;
import com.explodingpixels.widgets.WindowUtils;
import com.jgoodies.forms.factories.Borders;

public class UnifiedToolBar {
	
	private final TriAreaComponent fUnifiedToolBar = new TriAreaComponent(4);
	
	public UnifiedToolBar() {
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
		this.fUnifiedToolBar.setBackgroundPainter(null);
	}
	
	private static void fixUnifiedToolBarOnMacIfNeccessary(
			TriAreaComponent unifiedToolBar) {
		unifiedToolBar.setBackgroundPainter(MacPainterFactory.createTexturedWindowWorkaroundPainter());
	}
	
	static void installUnifiedToolBarBorder(final JComponent component) {
		FocusStateMatteBorder border = new FocusStateMatteBorder(
				0,
				0,
				1,
				0,
				MacColorUtils.getTexturedWindowToolbarBorderFocusedColor(),
				MacColorUtils.getTexturedWindowToolbarBorderUnfocusedColor(),
				component);
		
		component.setBorder(BorderFactory.createCompoundBorder(
				border,
				component.getBorder()));
	}
	
}
