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
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.ModelArchive;
import com.leclercb.taskunifier.api.models.ModelParent;

public class GoalModel extends AbstractBasicModelSortedModel {
	
	protected boolean includeArchived;
	
	public GoalModel(boolean firstNull, boolean includeArchived) {
		this.includeArchived = includeArchived;
		
		if (firstNull)
			this.addElement(null);
		
		List<Goal> goals = GoalFactory.getInstance().getList();
		for (Goal goal : goals)
			this.addElement(goal);
		
		GoalFactory.getInstance().addListChangeListener(
				new WeakListChangeListener(GoalFactory.getInstance(), this));
		GoalFactory.getInstance().addPropertyChangeListener(
				new WeakPropertyChangeListener(GoalFactory.getInstance(), this));
	}
	
	@Override
	public void addElement(Object element) {
		if (element != null)
			if (!this.includeArchived
					&& ((Goal) element).isSelfOrParentArchived())
				return;
		
		super.addElement(element);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		Goal goal = (Goal) event.getSource();
		
		if (event.getPropertyName().equals(BasicModel.PROP_MODEL_STATUS)) {
			if (goal.getModelStatus().isEndUserStatus()) {
				int index = this.getIndexOf(goal);
				if (index == -1)
					this.addElement(goal);
			} else {
				this.removeElement(goal);
			}
			
			return;
		}
		
		if (event.getPropertyName().equals(ModelArchive.PROP_ARCHIVED)
				&& !this.includeArchived) {
			if (goal.isSelfOrParentArchived()) {
				this.removeElement(goal);
				
				List<Goal> children = goal.getAllChildren();
				for (Goal child : children) {
					this.removeElement(child);
				}
			} else {
				int index = this.getIndexOf(goal);
				if (index == -1)
					this.addElement(goal);
				
				List<Goal> children = goal.getAllChildren();
				for (Goal child : children) {
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
		
		int index = this.getIndexOf(goal);
		this.fireContentsChanged(this, index, index);
	}
	
}
