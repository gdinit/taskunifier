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
package com.leclercb.taskunifier.gui.components.notesearchertree.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.api.models.templates.NoteTemplate;
import com.leclercb.taskunifier.gui.api.models.GuiModel;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcher;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcherType;
import com.leclercb.taskunifier.gui.api.searchers.filters.FilterLink;
import com.leclercb.taskunifier.gui.api.searchers.filters.NoteFilter;
import com.leclercb.taskunifier.gui.api.searchers.filters.NoteFilterElement;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.ModelCondition;
import com.leclercb.taskunifier.gui.components.notes.NoteColumnList;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.TUColorBadgeIcon;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.NoteUtils;

public class FolderItem extends DefaultMutableTreeNode implements SearcherNode, PropertyChangeListener {
	
	private NoteSearcher searcher;
	private BadgeCount badgeCount;
	
	public FolderItem(Folder folder) {
		super(folder);
		
		this.initializeNoteSearcher();
		this.updateBadgeCount();
	}
	
	public Folder getFolder() {
		return (Folder) this.getUserObject();
	}
	
	@Override
	public void setUserObject(Object userObject) {
		if (this.getUserObject() != null) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"User object has already been defined");
			return;
		}
		
		super.setUserObject(userObject);
	}
	
	private void initializeNoteSearcher() {
		final Folder folder = this.getFolder();
		final NoteTemplate template = new NoteTemplate("ModelTemplate");
		
		template.setNoteFolder(folder);
		
		NoteSearcher defaultNoteSearcher = Constants.getDefaultNoteSearcher();
		
		NoteFilter filter = new NoteFilter();
		filter.setLink(FilterLink.AND);
		filter.addElement(new NoteFilterElement(
				NoteColumnList.getInstance().get(NoteColumnList.FOLDER),
				ModelCondition.EQUALS,
				folder,
				false));
		filter.addFilter(defaultNoteSearcher.getFilter());
		
		String title = Translations.getString("searcherlist.none");
		
		if (folder != null)
			title = folder.getTitle();
		
		this.searcher = new NoteSearcher(
				NoteSearcherType.FOLDER,
				null,
				0,
				title,
				null,
				filter,
				defaultNoteSearcher.getSorter(),
				template);
		
		if (folder != null) {
			folder.addPropertyChangeListener(new WeakPropertyChangeListener(
					folder,
					this));
		}
	}
	
	@Override
	public NoteSearcher getNoteSearcher() {
		return this.searcher;
	}
	
	@Override
	public Icon getIcon() {
		if (this.getFolder() != null && this.getFolder() instanceof GuiModel)
			return new TUColorBadgeIcon(
					((GuiModel) this.getFolder()).getColor(),
					14,
					14);
		else
			return new TUColorBadgeIcon(null, 14, 14);
	}
	
	@Override
	public String getText() {
		return (this.getFolder() == null ? Translations.getString("searcherlist.none") : this.getFolder().getTitle());
	}
	
	@Override
	public void updateBadgeCount() {
		if (!Main.getSettings().getBooleanProperty("notesearcher.show_badges")) {
			this.badgeCount = null;
			return;
		}
		
		List<Note> notes = NoteFactory.getInstance().getList();
		NoteSearcher searcher = this.getNoteSearcher();
		
		int count = 0;
		for (Note note : notes) {
			if (NoteUtils.showNote(note, null, searcher.getFilter())) {
				count++;
			}
		}
		
		this.badgeCount = new BadgeCount(count);
	}
	
	@Override
	public BadgeCount getBadgeCount() {
		return this.badgeCount;
	}
	
	@Override
	public boolean getAllowsChildren() {
		return true;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(BasicModel.PROP_TITLE)) {
			this.searcher.setTitle(this.getFolder().getTitle());
			return;
		}
	}
	
}
