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
package com.leclercb.taskunifier.gui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXLabel;

import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelList;
import com.leclercb.taskunifier.gui.api.models.GuiModel;
import com.leclercb.taskunifier.gui.commons.painters.SearchPainter;
import com.leclercb.taskunifier.gui.commons.values.IconValueModel;
import com.leclercb.taskunifier.gui.commons.values.StringValueModel;

public class TUModelListLabel extends JPanel {
	
	private ModelList<?> modelList;
	private List<JXLabel> labels;
	
	public TUModelListLabel() {
		this(null);
	}
	
	public TUModelListLabel(ModelList<?> modelList) {
		this.initialize();
		this.setModelList(modelList);
	}
	
	private void initialize() {
		this.labels = new ArrayList<JXLabel>();
		
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		this.setOpaque(false);
	}
	
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		
		for (Component component : this.getComponents())
			component.setFont(font);
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		
		for (Component component : this.getComponents())
			component.setForeground(fg);
	}
	
	public ModelList<?> getModelList() {
		return this.modelList;
	}
	
	public void setModelList(ModelList<?> modelList) {
		this.modelList = modelList;
		
		this.removeAll();
		
		this.labels.clear();
		
		if (modelList != null) {
			for (Model model : modelList) {
				JXLabel label = new JXLabel(
						StringValueModel.INSTANCE.getString(model));
				
				label.setBackgroundPainter(new SearchPainter());
				
				if (model instanceof GuiModel)
					label.setIcon(IconValueModel.INSTANCE.getIcon(model));
				
				this.add(label);
				this.add(Box.createHorizontalStrut(3));
				
				this.labels.add(label);
			}
		}
		
		this.revalidate();
		this.repaint();
	}
	
	public void highlightSearchText(String searchText) {
		for (JXLabel label : this.labels) {
			((SearchPainter) label.getBackgroundPainter()).setSearchText(searchText);
		}
	}
	
}
