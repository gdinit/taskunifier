package com.leclercb.commons.gui.utils;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * Usage: RepaintManager.setCurrentManager(new
 * CheckThreadViolationRepaintManager())
 */
public class CheckThreadViolationRepaintManager extends RepaintManager {
	
	private boolean completeCheck = true;
	
	public CheckThreadViolationRepaintManager() {
		
	}
	
	public boolean isCompleteCheck() {
		return this.completeCheck;
	}
	
	public void setCompleteCheck(boolean completeCheck) {
		this.completeCheck = completeCheck;
	}
	
	@Override
	public synchronized void addInvalidComponent(JComponent component) {
		this.checkThreadViolations(component);
		super.addInvalidComponent(component);
	}
	
	@Override
	public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
		this.checkThreadViolations(component);
		super.addDirtyRegion(component, x, y, w, h);
	}
	
	private void checkThreadViolations(JComponent c) {
		if (!SwingUtilities.isEventDispatchThread()
				&& (this.completeCheck || c.isShowing())) {
			Exception exception = new Exception();
			boolean repaint = false;
			boolean fromSwing = false;
			
			StackTraceElement[] stackTrace = exception.getStackTrace();
			for (StackTraceElement st : stackTrace) {
				if (repaint && st.getClassName().startsWith("javax.swing.")) {
					fromSwing = true;
				}
				
				if ("repaint".equals(st.getMethodName())) {
					repaint = true;
				}
			}
			
			if (repaint && !fromSwing) {
				return;
			}
			
			exception.printStackTrace();
		}
	}
	
}
