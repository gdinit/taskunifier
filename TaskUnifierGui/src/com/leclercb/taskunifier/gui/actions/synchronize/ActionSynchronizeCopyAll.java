package com.leclercb.taskunifier.gui.actions.synchronize;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;

import com.leclercb.taskunifier.api.models.Contact;
import com.leclercb.taskunifier.api.models.ContactFactory;
import com.leclercb.taskunifier.api.models.Context;
import com.leclercb.taskunifier.api.models.ContextFactory;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.Location;
import com.leclercb.taskunifier.api.models.LocationFactory;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.gui.actions.ActionSynchronize;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;

public class ActionSynchronizeCopyAll extends ActionSynchronize {
	
	private ConfigurationGroup configurationGroup;
	
	public ActionSynchronizeCopyAll(ConfigurationGroup configurationGroup) {
		super(22, 22, false);
		
		this.putValue(
				Action.NAME,
				Translations.getString("action.synchronize_copy_all"));
		
		this.configurationGroup = configurationGroup;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (this.configurationGroup != null) {
			this.configurationGroup.saveAndApplyConfig();
		}
		
		List<Contact> contacts = ContactFactory.getInstance().getList();
		for (Contact contact : contacts) {
			if (contact.getModelStatus().isEndUserStatus()) {
				contact.setModelStatus(ModelStatus.TO_UPDATE);
				SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().flagAsNew(
						contact);
			}
		}
		
		List<Context> contexts = ContextFactory.getInstance().getList();
		for (Context context : contexts) {
			if (context.getModelStatus().isEndUserStatus()) {
				context.setModelStatus(ModelStatus.TO_UPDATE);
				SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().flagAsNew(
						context);
			}
		}
		
		List<Folder> folders = FolderFactory.getInstance().getList();
		for (Folder folder : folders) {
			if (folder.getModelStatus().isEndUserStatus()) {
				folder.setModelStatus(ModelStatus.TO_UPDATE);
				SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().flagAsNew(
						folder);
			}
		}
		
		List<Goal> goals = GoalFactory.getInstance().getList();
		for (Goal goal : goals) {
			if (goal.getModelStatus().isEndUserStatus()) {
				goal.setModelStatus(ModelStatus.TO_UPDATE);
				SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().flagAsNew(
						goal);
			}
		}
		
		List<Location> locations = LocationFactory.getInstance().getList();
		for (Location location : locations) {
			if (location.getModelStatus().isEndUserStatus()) {
				location.setModelStatus(ModelStatus.TO_UPDATE);
				SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().flagAsNew(
						location);
			}
		}
		
		List<Note> notes = NoteFactory.getInstance().getList();
		for (Note note : notes) {
			if (note.getModelStatus().isEndUserStatus()) {
				note.setModelStatus(ModelStatus.TO_UPDATE);
				SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().flagAsNew(
						note);
			}
		}
		
		List<Task> tasks = TaskFactory.getInstance().getList();
		for (Task task : tasks) {
			if (task.getModelStatus().isEndUserStatus()) {
				task.setModelStatus(ModelStatus.TO_UPDATE);
				SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().flagAsNew(
						task);
			}
		}
		
		super.actionPerformed(event);
	}
	
}
