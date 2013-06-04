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
package com.leclercb.taskunifier.gui.utils;

import java.util.ArrayList;
import java.util.List;

import com.leclercb.taskunifier.gui.api.searchers.TaskSearcher;
import com.leclercb.taskunifier.gui.api.searchers.filters.FilterLink;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilter;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilterElement;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.StringCondition;
import com.leclercb.taskunifier.gui.api.searchers.groupers.TaskGrouperElement;

public final class TaskGroupUtils {
	
	private TaskGroupUtils() {
		
	}
	
	public static List<TaskSearcher> getFilters(TaskSearcher searcher) {
		List<TaskSearcher> searchers = new ArrayList<TaskSearcher>();
		
		if (searcher == null || searcher.getGrouper().getElementCount() == 0)
			return searchers;
		
		TaskGrouperElement element = searcher.getGrouper().getElement(0);
		searcher.getGrouper().removeElement(element);
		
		TaskSearcher s;
		
		switch (element.getProperty().getType()) {
			case STAR:
			case BOOLEAN:
				s = searcher.clone();
				s.setTitle("True");
				addMainFilter(s, new TaskFilterElement(
						element.getProperty(),
						StringCondition.EQUALS,
						"true",
						false));
				searchers.add(s);
				
				s = searcher.clone();
				s.setTitle("False");
				addMainFilter(s, new TaskFilterElement(
						element.getProperty(),
						StringCondition.EQUALS,
						"false",
						false));
				searchers.add(s);
				
				break;
			default:
				searchers.add(searcher);
				
				break;
		}
		
		return searchers;
	}
	
	private static void addMainFilter(
			TaskSearcher searcher,
			TaskFilterElement element) {
		TaskFilter filter = new TaskFilter();
		filter.setLink(FilterLink.AND);
		filter.addElement(element);
		filter.addFilter(searcher.getFilter());
		searcher.setFilter(filter);
	}
	
}
