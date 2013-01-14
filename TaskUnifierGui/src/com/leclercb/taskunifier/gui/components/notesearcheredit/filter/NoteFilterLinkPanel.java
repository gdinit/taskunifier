/*
 * TaskUnifier
 * Copyright (c) 2011, Benjamin Leclerc
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
package com.leclercb.taskunifier.gui.components.notesearcheredit.filter;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.gui.api.searchers.filters.FilterLink;
import com.leclercb.taskunifier.gui.api.searchers.filters.NoteFilter;
import com.leclercb.taskunifier.gui.commons.values.StringValueFilterLink;
import com.leclercb.taskunifier.gui.utils.FormBuilder;

public class NoteFilterLinkPanel extends JPanel {
	
	private NoteFilter filter;
	
	private JXComboBox filterLink;
	
	public NoteFilterLinkPanel() {
		this.initialize();
		this.setFilter(null);
	}
	
	public NoteFilter getFilter() {
		return this.filter;
	}
	
	public void setFilter(NoteFilter filter) {
		this.filter = filter;
		
		FilterLink link = null;
		
		if (this.filter != null)
			link = this.filter.getLink();
		
		this.filterLink.setEnabled(this.filter != null);
		this.filterLink.setSelectedItem(link);
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		FormBuilder builder = new FormBuilder("fill:default");
		
		builder.appendI15d("searcheredit.filter.operator", true);
		
		// Column
		this.filterLink = new JXComboBox(FilterLink.values());
		this.filterLink.setRenderer(new DefaultListRenderer(
				StringValueFilterLink.INSTANCE));
		this.filterLink.setEnabled(false);
		this.filterLink.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent evt) {
				if (NoteFilterLinkPanel.this.filter == null)
					return;
				
				FilterLink selectedLink = (FilterLink) NoteFilterLinkPanel.this.filterLink.getSelectedItem();
				
				if (EqualsUtils.equals(
						NoteFilterLinkPanel.this.filter.getLink(),
						selectedLink))
					return;
				
				NoteFilterLinkPanel.this.filter.setLink(selectedLink);
			}
			
		});
		
		builder.append(this.filterLink);
		
		// Lay out the panel
		panel.add(builder.getPanel(), BorderLayout.CENTER);
		
		this.add(panel, BorderLayout.CENTER);
	}
	
}
