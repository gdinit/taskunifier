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
package com.leclercb.taskunifier.gui.commons.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.leclercb.taskunifier.gui.commons.values.StringValueTime;
import com.leclercb.taskunifier.gui.swing.TUSpinnerTimeEditor;
import com.leclercb.taskunifier.gui.swing.TUSpinnerTimeModel;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class TimeEditor extends AbstractCellEditor implements TableCellEditor {
	
	private JPanel panel;
	private JSpinner timeSpinner;
	private JButton button;
	private JPopupMenu popupMenu;
	
	public TimeEditor() {
		this.panel = new JPanel();
		this.panel.setLayout(new BorderLayout());
		
		this.timeSpinner = new JSpinner();
		this.timeSpinner.setModel(new TUSpinnerTimeModel());
		this.timeSpinner.setEditor(new TUSpinnerTimeEditor(this.timeSpinner));
		
		this.button = new JButton(ImageUtils.getResourceImage(
				"down.png",
				14,
				14));
		this.button.setBorderPainted(false);
		this.button.setContentAreaFilled(false);
		this.button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					TimeEditor.this.timeSpinner.commitEdit();
				} catch (ParseException e) {
					
				}
				
				TimeEditor.this.popupMenu.show(
						(Component) event.getSource(),
						0,
						0);
			}
			
		});
		
		this.popupMenu = new JPopupMenu();
		this.popupMenu.add(this.createAction(0));
		this.popupMenu.add(this.createAction(15));
		this.popupMenu.add(this.createAction(30));
		this.popupMenu.add(this.createAction(45));
		this.popupMenu.add(this.createAction(60));
		this.popupMenu.add(this.createAction(90));
		this.popupMenu.add(this.createAction(120));
		this.popupMenu.add(this.createAction(180));
		this.popupMenu.add(this.createAction(240));
		this.popupMenu.add(this.createAction(300));
		
		this.panel.add(this.timeSpinner, BorderLayout.CENTER);
		this.panel.add(this.button, BorderLayout.EAST);
	}
	
	@Override
	public Component getTableCellEditorComponent(
			JTable table,
			Object value,
			boolean isSelected,
			int row,
			int col) {
		this.timeSpinner.setValue(value);
		return this.panel;
	}
	
	@Override
	public Object getCellEditorValue() {
		this.popupMenu.setVisible(false);
		
		try {
			this.timeSpinner.commitEdit();
		} catch (ParseException e) {
			
		}
		
		return this.timeSpinner.getValue();
	}
	
	@Override
	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			MouseEvent event = (MouseEvent) anEvent;
			
			if (event.getClickCount() != 1)
				return false;
		}
		
		return super.isCellEditable(anEvent);
	}
	
	private Action createAction(final int minutes) {
		Action action = new AbstractAction(
				StringValueTime.INSTANCE.getString(minutes)) {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				TimeEditor.this.timeSpinner.setValue(minutes);
				
				try {
					TimeEditor.this.timeSpinner.commitEdit();
				} catch (ParseException e) {
					
				}
			}
			
		};
		
		return action;
	}
	
}
