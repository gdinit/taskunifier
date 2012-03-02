package com.leclercb.taskunifier.gui.api.models.beans;

import java.util.List;

import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.beans.NoteBean;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class ComNoteBean extends GuiNoteBean {
	
	@XStreamAlias("foldertitle")
	private String folderTitle;
	
	public ComNoteBean() {
		this((ModelId) null);
	}
	
	public ComNoteBean(ModelId modelId) {
		super(modelId);
		
		this.setFolderTitle(null);
	}
	
	public ComNoteBean(NoteBean bean) {
		super(bean);
		
		if (bean instanceof ComNoteBean)
			this.setFolderTitle(((ComNoteBean) bean).getFolderTitle());
	}
	
	public String getFolderTitle() {
		return this.folderTitle;
	}
	
	public void setFolderTitle(String folderTitle) {
		this.folderTitle = folderTitle;
	}
	
	public void loadTitles(boolean removeModelId) {
		if (this.getFolder() != null) {
			Folder folder = FolderFactory.getInstance().get(this.getFolder());
			if (folder != null)
				this.setFolderTitle(folder.getTitle());
			
			if (removeModelId)
				this.setFolder(null);
		}
	}
	
	public void loadModels(boolean removeTitle) {
		if (this.getFolder() == null) {
			if (this.getFolderTitle() != null) {
				List<Folder> models = FolderFactory.getInstance().getList();
				for (Folder model : models) {
					if (!model.getModelStatus().isEndUserStatus())
						continue;
					
					if (model.getTitle().equalsIgnoreCase(this.getFolderTitle())) {
						this.setFolder(model.getModelId());
						break;
					}
				}
				
				if (removeTitle)
					this.setFolderTitle(null);
			}
		}
	}
	
}
