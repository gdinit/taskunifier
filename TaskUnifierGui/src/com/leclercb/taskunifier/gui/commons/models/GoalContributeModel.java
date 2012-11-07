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

import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.models.ModelParent;
import com.leclercb.taskunifier.api.models.enums.GoalLevel;

public class GoalContributeModel extends GoalModel {
	
	public GoalContributeModel(boolean firstNull) {
		super(firstNull);
	}
	
	@Override
	public void addElement(Object element) {
		if (element != null)
			if (!((Goal) element).getLevel().equals(GoalLevel.LIFE_TIME))
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
		
		if (event.getPropertyName().equals(Goal.PROP_CONTRIBUTES)) {
			if (goal.getLevel().equals(GoalLevel.LIFE_TIME)) {
				this.removeElement(goal);
			} else {
				int index = this.getIndexOf(goal);
				if (index == -1)
					this.addElement(goal);
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
