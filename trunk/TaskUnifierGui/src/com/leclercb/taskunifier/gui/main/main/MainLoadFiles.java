package com.leclercb.taskunifier.gui.main.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.api.models.ContactFactory;
import com.leclercb.taskunifier.api.models.ContextFactory;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.LocationFactory;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.api.models.templates.TaskTemplateFactory;
import com.leclercb.taskunifier.gui.actions.ActionResetGeneralSearchers;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcherFactory;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcherFactory;
import com.leclercb.taskunifier.gui.api.searchers.coders.NoteSearcherFactoryXMLCoder;
import com.leclercb.taskunifier.gui.api.searchers.coders.TaskSearcherFactoryXMLCoder;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.settings.ModelVersion;
import com.leclercb.taskunifier.gui.translations.Translations;

public final class MainLoadFiles {
	
	private MainLoadFiles() {
		
	}
	
	public static void loadAllData(String folder) {
		loadAllData(folder, Constants.DEFAULT_SUFFIX);
	}
	
	public static void loadAllData(String folder, String suffix) {
		loadModels(folder, suffix);
		loadTaskTemplates(folder, suffix);
		loadTaskSearchers(folder, suffix);
		loadNoteSearchers(folder, suffix);
	}
	
	private static void loadModels(String folder, String suffix) {
		if (suffix == null)
			suffix = "";
		
		try {
			ContactFactory.getInstance().deleteAll();
			
			ContactFactory.getInstance().decodeFromXML(
					new FileInputStream(folder
							+ File.separator
							+ "contacts"
							+ suffix
							+ ".xml"));
		} catch (FileNotFoundException e) {
			
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading contacts",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			ContextFactory.getInstance().deleteAll();
			
			ContextFactory.getInstance().decodeFromXML(
					new FileInputStream(folder
							+ File.separator
							+ "contexts"
							+ suffix
							+ ".xml"));
		} catch (FileNotFoundException e) {
			
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading contexts",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			FolderFactory.getInstance().deleteAll();
			
			FolderFactory.getInstance().decodeFromXML(
					new FileInputStream(folder
							+ File.separator
							+ "folders"
							+ suffix
							+ ".xml"));
		} catch (FileNotFoundException e) {
			
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading folders",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			GoalFactory.getInstance().deleteAll();
			
			GoalFactory.getInstance().decodeFromXML(
					new FileInputStream(folder
							+ File.separator
							+ "goals"
							+ suffix
							+ ".xml"));
		} catch (FileNotFoundException e) {
			
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading goals",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			LocationFactory.getInstance().deleteAll();
			
			LocationFactory.getInstance().decodeFromXML(
					new FileInputStream(folder
							+ File.separator
							+ "locations"
							+ suffix
							+ ".xml"));
		} catch (FileNotFoundException e) {
			
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading locations",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			NoteFactory.getInstance().deleteAll();
			
			NoteFactory.getInstance().decodeFromXML(
					new FileInputStream(folder
							+ File.separator
							+ "notes"
							+ suffix
							+ ".xml"));
		} catch (FileNotFoundException e) {
			
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading notes",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			TaskFactory.getInstance().deleteAll();
			
			TaskFactory.getInstance().decodeFromXML(
					new FileInputStream(folder
							+ File.separator
							+ "tasks"
							+ suffix
							+ ".xml"));
		} catch (FileNotFoundException e) {
			
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading tasks",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		ModelVersion.updateModels();
	}
	
	private static void loadTaskTemplates(String folder, String suffix) {
		if (suffix == null)
			suffix = "";
		
		try {
			TaskTemplateFactory.getInstance().deleteAll();
			
			TaskTemplateFactory.getInstance().decodeFromXML(
					new FileInputStream(folder
							+ File.separator
							+ "task_templates"
							+ suffix
							+ ".xml"));
		} catch (FileNotFoundException e) {
			
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading task templates",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static void loadTaskSearchers(String folder, String suffix) {
		if (suffix == null)
			suffix = "";
		
		try {
			TaskSearcherFactory.getInstance().deleteAll();
			
			new TaskSearcherFactoryXMLCoder().decode(new FileInputStream(folder
					+ File.separator
					+ "task_searchers"
					+ suffix
					+ ".xml"));
		} catch (FileNotFoundException e) {
			ActionResetGeneralSearchers.resetGeneralSearchers();
		} catch (Throwable e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading task searchers",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static void loadNoteSearchers(String folder, String suffix) {
		if (suffix == null)
			suffix = "";
		
		try {
			NoteSearcherFactory.getInstance().deleteAll();
			
			new NoteSearcherFactoryXMLCoder().decode(new FileInputStream(folder
					+ File.separator
					+ "note_searchers"
					+ suffix
					+ ".xml"));
		} catch (FileNotFoundException e) {
			
		} catch (Throwable e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while loading note searchers",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
}
