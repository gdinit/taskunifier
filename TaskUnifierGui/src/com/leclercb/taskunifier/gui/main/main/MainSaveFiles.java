package com.leclercb.taskunifier.gui.main.main;

import java.io.File;
import java.io.FileOutputStream;
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
import com.leclercb.taskunifier.gui.api.searchers.coders.NoteSearcherFactoryXMLCoder;
import com.leclercb.taskunifier.gui.api.searchers.coders.TaskSearcherFactoryXMLCoder;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;

public final class MainSaveFiles {
	
	private MainSaveFiles() {
		
	}
	
	public static void copyAllData(String folder) {
		copyAllData(folder, Constants.DEFAULT_SUFFIX);
	}
	
	public static void copyAllData(String folder, String suffix) {
		saveModels(folder, suffix);
		saveTaskTemplates(folder, suffix);
		saveTaskSearchers(folder, suffix);
		saveNoteSearchers(folder, suffix);
	}
	
	public static void saveAllData() {
		saveAllData(Constants.DEFAULT_SUFFIX);
	}
	
	public static void saveAllData(String suffix) {
		saveModels(Main.getUserFolder(), suffix);
		saveTaskTemplates(Main.getUserFolder(), suffix);
		saveTaskSearchers(Main.getUserFolder(), suffix);
		saveNoteSearchers(Main.getUserFolder(), suffix);
		saveInitSettings();
		saveSettings();
		saveUserSettings();
	}
	
	public static void saveInitSettings() {
		try {
			File f = new File(Main.getInitSettingsFile());
			
			if (!Main.isDeveloperMode() && f.exists() && f.canWrite()) {
				Main.getInitSettings().store(
						new FileOutputStream(Main.getInitSettingsFile()),
						Constants.TITLE + " Init Settings");
				
				GuiLogger.getLogger().log(Level.INFO, "Saving init settings");
			}
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving init settings",
					e);
		}
	}
	
	public static void saveSettings() {
		try {
			Main.getSettings().store(
					new FileOutputStream(Main.getSettingsFile()),
					Constants.TITLE + " Settings");
			
			GuiLogger.getLogger().log(Level.INFO, "Saving settings");
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving settings",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void saveUserSettings() {
		try {
			Main.getUserSettings().store(
					new FileOutputStream(Main.getUserSettingsFile()),
					Constants.TITLE + " User Settings");
			
			GuiLogger.getLogger().log(Level.INFO, "Saving user settings");
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving user settings",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static void saveModels(String folder, String suffix) {
		if (suffix == null)
			suffix = "";
		
		try {
			ContactFactory.getInstance().cleanFactory();
			ContextFactory.getInstance().cleanFactory();
			FolderFactory.getInstance().cleanFactory();
			GoalFactory.getInstance().cleanFactory();
			LocationFactory.getInstance().cleanFactory();
			NoteFactory.getInstance().cleanFactory();
			TaskFactory.getInstance().cleanFactory();
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while cleaning factories",
					e);
		}
		
		try {
			ContactFactory.getInstance().encodeToXML(
					new FileOutputStream(folder
							+ File.separator
							+ "contacts"
							+ suffix
							+ ".xml"));
			
			GuiLogger.getLogger().log(
					Level.INFO,
					"Saving contacts (" + folder + ")");
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving contacts",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			ContextFactory.getInstance().encodeToXML(
					new FileOutputStream(folder
							+ File.separator
							+ "contexts"
							+ suffix
							+ ".xml"));
			
			GuiLogger.getLogger().log(
					Level.INFO,
					"Saving contexts (" + folder + ")");
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving contexts",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			FolderFactory.getInstance().encodeToXML(
					new FileOutputStream(folder
							+ File.separator
							+ "folders"
							+ suffix
							+ ".xml"));
			
			GuiLogger.getLogger().log(
					Level.INFO,
					"Saving folders (" + folder + ")");
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving folders",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			GoalFactory.getInstance().encodeToXML(
					new FileOutputStream(folder
							+ File.separator
							+ "goals"
							+ suffix
							+ ".xml"));
			
			GuiLogger.getLogger().log(
					Level.INFO,
					"Saving goals (" + folder + ")");
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving goals",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			LocationFactory.getInstance().encodeToXML(
					new FileOutputStream(folder
							+ File.separator
							+ "locations"
							+ suffix
							+ ".xml"));
			
			GuiLogger.getLogger().log(
					Level.INFO,
					"Saving locations (" + folder + ")");
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving locations",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			NoteFactory.getInstance().encodeToXML(
					new FileOutputStream(folder
							+ File.separator
							+ "notes"
							+ suffix
							+ ".xml"));
			
			GuiLogger.getLogger().log(
					Level.INFO,
					"Saving notes (" + folder + ")");
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving notes",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			TaskFactory.getInstance().encodeToXML(
					new FileOutputStream(folder
							+ File.separator
							+ "tasks"
							+ suffix
							+ ".xml"));
			
			GuiLogger.getLogger().log(
					Level.INFO,
					"Saving tasks (" + folder + ")");
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving tasks",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static void saveTaskTemplates(String folder, String suffix) {
		if (suffix == null)
			suffix = "";
		
		try {
			TaskTemplateFactory.getInstance().encodeToXML(
					new FileOutputStream(folder
							+ File.separator
							+ "task_templates"
							+ suffix
							+ ".xml"));
			
			GuiLogger.getLogger().log(
					Level.INFO,
					"Saving task templates (" + folder + ")");
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving task templates",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static void saveTaskSearchers(String folder, String suffix) {
		if (suffix == null)
			suffix = "";
		
		try {
			new TaskSearcherFactoryXMLCoder().encode(new FileOutputStream(
					folder
							+ File.separator
							+ "task_searchers"
							+ suffix
							+ ".xml"));
			
			GuiLogger.getLogger().log(
					Level.INFO,
					"Saving task searchers (" + folder + ")");
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving task searchers",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static void saveNoteSearchers(String folder, String suffix) {
		if (suffix == null)
			suffix = "";
		
		try {
			new NoteSearcherFactoryXMLCoder().encode(new FileOutputStream(
					folder
							+ File.separator
							+ "note_searchers"
							+ suffix
							+ ".xml"));
			
			GuiLogger.getLogger().log(
					Level.INFO,
					"Saving note searchers (" + folder + ")");
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Error while saving note searchers",
					e);
			
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					Translations.getString("general.error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
}
