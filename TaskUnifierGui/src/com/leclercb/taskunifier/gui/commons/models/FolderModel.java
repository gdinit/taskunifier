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
package com.leclercb.taskunifier.gui.commons.models;

import java.beans.PropertyChangeEvent;
import java.util.List;

import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.ModelParent;

public class FolderModel extends AbstractBasicModelSortedModel {
	
	private boolean includeArchived;
	
	public FolderModel(boolean firstNull, boolean includeArchived) {
		this.includeArchived = includeArchived;
		
		if (firstNull)
			this.addElement(null);
		
		List<Folder> folders = FolderFactory.getInstance().getList();
		for (Folder folder : folders)
			this.addElement(folder);
		
		FolderFactory.getInstance().addListChangeListener(
				new WeakListChangeListener(FolderFactory.getInstance(), this));
		FolderFactory.getInstance().addPropertyChangeListener(
				new WeakPropertyChangeListener(
						FolderFactory.getInstance(),
						this));
	}
	
	@Override
	public void addElement(Object element) {
		if (element != null)
			if (!this.includeArchived
					&& ((Folder) element).isSelfOrParentArchived())
				return;
		
		super.addElement(element);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		Folder folder = (Folder) event.getSource();
		
		if (event.getPropertyName().equals(BasicModel.PROP_MODEL_STATUS)) {
			if (folder.getModelStatus().isEndUserStatus()) {
				int index = this.getIndexOf(folder);
				if (index == -1)
					this.addElement(folder);
			} else {
				this.removeElement(folder);
			}
			
			return;
		}
		
		if (event.getPropertyName().equals(Folder.PROP_ARCHIVED)
				&& !this.includeArchived) {
			if (folder.isSelfOrParentArchived()) {
				this.removeElement(folder);
				
				List<Folder> children = folder.getAllChildren();
				for (Folder child : children) {
					this.removeElement(child);
				}
			} else {
				int index = this.getIndexOf(folder);
				if (index == -1)
					this.addElement(folder);
				
				List<Folder> children = folder.getAllChildren();
				for (Folder child : children) {
					if (child.isSelfOrParentArchived())
						continue;
					
					index = this.getIndexOf(child);
					if (index == -1)
						this.addElement(child);
				}
				
				this.fireStructureChanged(this);
			}
			
			return;
		}
		
		if (event.getPropertyName().equals(ModelParent.PROP_PARENT)) {
			this.fireStructureChanged(this);
			return;
		}
		
		int index = this.getIndexOf(folder);
		this.fireContentsChanged(this, index, index);
	}
	
}
