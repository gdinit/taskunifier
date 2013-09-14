/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.util.ArrayList;

import com.leclercb.taskunifier.api.models.beans.NoteBean;

public class ToodledoNoteList extends ArrayList<NoteBean> {
	
	private int total;
	
	public ToodledoNoteList(int total) {
		this.total = total;
	}
	
	public int getTotal() {
		return this.total;
	}
	
}
