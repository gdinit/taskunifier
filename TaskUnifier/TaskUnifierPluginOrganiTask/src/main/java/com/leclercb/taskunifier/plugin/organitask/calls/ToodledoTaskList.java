/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.util.ArrayList;

import com.leclercb.taskunifier.api.models.beans.TaskBean;

public class ToodledoTaskList extends ArrayList<TaskBean> {
	
	private int total;
	
	public ToodledoTaskList(int total) {
		this.total = total;
	}
	
	public int getTotal() {
		return this.total;
	}
	
}
