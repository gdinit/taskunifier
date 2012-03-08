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
package com.leclercb.taskunifier.gui.api.plugins;

import java.awt.Image;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.progress.DefaultProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.CompareUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.FileUtils;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.api.plugins.exc.PluginException;
import com.leclercb.taskunifier.gui.api.plugins.exc.PluginException.PluginExceptionType;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.api.synchronizer.dummy.DummyGuiPlugin;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.MainFrame;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.gui.swing.TUWorker;
import com.leclercb.taskunifier.gui.swing.TUWorkerDialog;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.HttpUtils;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;

public class PluginsUtils {
	
	static {
		Main.getApiPlugins().addListChangeListener(new ListChangeListener() {
			
			@Override
			public void listChange(ListChangeEvent evt) {
				SynchronizerGuiPlugin plugin = (SynchronizerGuiPlugin) evt.getValue();
				
				if (evt.getChangeType() == ListChangeEvent.VALUE_REMOVED) {
					if (EqualsUtils.equals(
							Main.getUserSettings().getStringProperty(
									"plugin.synchronizer.id"),
							plugin.getId()))
						SynchronizerUtils.setSynchronizerPlugin(DummyGuiPlugin.getInstance());
				}
			}
			
		});
	}
	
	public static final Plugin DUMMY_PLUGIN = new Plugin(
			PluginStatus.INSTALLED,
			DummyGuiPlugin.getInstance().getId(),
			DummyGuiPlugin.getInstance().isPublisher(),
			DummyGuiPlugin.getInstance().isSynchronizer(),
			DummyGuiPlugin.getInstance().getName(),
			DummyGuiPlugin.getInstance().getAuthor(),
			DummyGuiPlugin.getInstance().getVersion(),
			DummyGuiPlugin.getInstance().getSynchronizerApi().getApiWebSite(),
			DummyGuiPlugin.getInstance().getSynchronizerApi().getApiWebSite(),
			"Version " + DummyGuiPlugin.getInstance().getVersion(),
			ImageUtils.getResourceImage("do_not_synchronize.png"),
			null);
	
	public static SynchronizerGuiPlugin loadPlugin(File file)
			throws PluginException {
		if (!file.isFile()
				|| !FileUtils.getExtention(file.getAbsolutePath()).equals("jar"))
			return null;
		
		try {
			File tmpFile = File.createTempFile("taskunifier_plugin_", ".jar");
			
			try {
				tmpFile.deleteOnExit();
			} catch (Throwable t) {
				
			}
			
			org.apache.commons.io.FileUtils.copyFile(file, tmpFile);
			
			List<SynchronizerGuiPlugin> plugins = Main.getApiPlugins().loadJar(
					tmpFile,
					file,
					false);
			
			if (plugins.size() == 0) {
				try {
					file.delete();
				} catch (Throwable t) {
					
				}
				
				throw new PluginException(PluginExceptionType.NO_VALID_PLUGIN);
			}
			
			if (plugins.size() > 1) {
				try {
					file.delete();
				} catch (Throwable t) {
					
				}
				
				throw new PluginException(
						PluginExceptionType.MORE_THAN_ONE_PLUGIN);
			}
			
			try {
				if (!EqualsUtils.equals(
						Constants.PLUGIN_API_VERSION,
						plugins.get(0).getPluginApiVersion()))
					throw new PluginException(
							PluginExceptionType.OUTDATED_PLUGIN);
			} catch (PluginException pexc) {
				try {
					file.delete();
				} catch (Throwable t) {
					
				}
				
				throw pexc;
			} catch (Throwable t1) {
				try {
					file.delete();
				} catch (Throwable t2) {
					
				}
				
				throw new PluginException(PluginExceptionType.OUTDATED_PLUGIN);
			}
			
			SynchronizerGuiPlugin plugin = plugins.get(0);
			
			List<SynchronizerGuiPlugin> existingPlugins = new ArrayList<SynchronizerGuiPlugin>(
					Main.getApiPlugins().getPlugins());
			
			// "existingPlugins" does not contain the new plugin yet
			Main.getApiPlugins().addPlugin(file, plugin);
			
			GuiLogger.getLogger().info(
					"Plugin loaded: "
							+ plugin.getName()
							+ " - "
							+ plugin.getVersion());
			
			SynchronizerGuiPlugin loadedPlugin = plugin;
			
			for (SynchronizerGuiPlugin p : existingPlugins) {
				if (EqualsUtils.equals(p.getId(), plugin.getId())) {
					SynchronizerGuiPlugin pluginToDelete = null;
					if (CompareUtils.compare(
							p.getVersion(),
							plugin.getVersion()) < 0) {
						pluginToDelete = p;
					} else {
						pluginToDelete = plugin;
						loadedPlugin = null;
					}
					
					deletePlugin(pluginToDelete);
					break;
				}
			}
			
			if (loadedPlugin != null)
				loadedPlugin.loadPlugin();
			
			return loadedPlugin;
		} catch (PluginException e) {
			throw e;
		} catch (Throwable t) {
			PluginLogger.getLogger().log(
					Level.WARNING,
					"Cannot install plugin",
					t);
			
			throw new PluginException(
					PluginExceptionType.ERROR_INSTALL_PLUGIN,
					t);
		}
	}
	
	public static void deletePlugin(SynchronizerGuiPlugin plugin) {
		if (plugin.getId().equals(DummyGuiPlugin.getInstance().getId()))
			return;
		
		plugin.deletePlugin();
		
		File file = Main.getApiPlugins().getFile(plugin);
		file.delete();
		
		Main.getApiPlugins().removePlugin(plugin);
		
		GuiLogger.getLogger().info(
				"Plugin deleted: "
						+ plugin.getName()
						+ " - "
						+ plugin.getVersion());
	}
	
	public static void installPlugin(
			final Plugin plugin,
			final boolean use,
			final ProgressMonitor monitor) throws Exception {
		File file = null;
		
		try {
			if (plugin.getId().equals(DummyGuiPlugin.getInstance().getId()))
				return;
			
			if (monitor != null)
				monitor.addMessage(new DefaultProgressMessage(
						Translations.getString(
								"manage_plugins.progress.start_plugin_installation",
								plugin.getName())));
			
			file = new File(Main.getPluginsFolder()
					+ File.separator
					+ UUID.randomUUID().toString()
					+ ".jar");
			
			file.createNewFile();
			
			if (monitor != null)
				monitor.addMessage(new DefaultProgressMessage(
						Translations.getString(
								"manage_plugins.progress.downloading_plugin",
								plugin.getName())));
			
			if (!Main.getSettings().getBooleanProperty(
					"proxy.use_system_proxies")
					&& Main.getSettings().getBooleanProperty("proxy.enabled")) {
				FileUtils.copyURLToFile(
						new URL(plugin.getDownloadUrl()),
						file,
						Main.getSettings().getStringProperty("proxy.host"),
						Main.getSettings().getIntegerProperty("proxy.port"),
						Main.getSettings().getStringProperty("proxy.login"),
						Main.getSettings().getStringProperty("proxy.password"));
			} else {
				FileUtils.copyURLToFile(new URL(plugin.getDownloadUrl()), file);
			}
			
			if (monitor != null)
				monitor.addMessage(new DefaultProgressMessage(
						Translations.getString(
								"manage_plugins.progress.installing_plugin",
								plugin.getName())));
			
			final File finalFile = file;
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					SynchronizerGuiPlugin loadedPlugin;
					try {
						loadedPlugin = PluginsUtils.loadPlugin(finalFile);
						
						if (loadedPlugin != null)
							GuiLogger.getLogger().info(
									"Plugin installed: "
											+ loadedPlugin.getName()
											+ " - "
											+ loadedPlugin.getVersion());
						
						if (use)
							SynchronizerUtils.setSynchronizerPlugin(loadedPlugin);
						
						if (loadedPlugin != null)
							loadedPlugin.installPlugin();
						
						if (monitor != null)
							monitor.addMessage(new DefaultProgressMessage(
									Translations.getString(
											"manage_plugins.progress.plugin_installed",
											plugin.getName())));
						
						plugin.setStatus(PluginStatus.INSTALLED);
					} catch (PluginException e) {
						finalFile.delete();
					}
				}
				
			});
		} catch (Exception e) {
			file.delete();
			
			PluginLogger.getLogger().log(
					Level.WARNING,
					"Cannot install plugin",
					e);
			
			throw e;
		}
	}
	
	public static void updatePlugin(Plugin plugin, ProgressMonitor monitor)
			throws Exception {
		if (plugin.getId().equals(DummyGuiPlugin.getInstance().getId()))
			return;
		
		if (monitor != null)
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.start_plugin_update",
							plugin.getName())));
		
		boolean use = false;
		if (plugin.getId().equals(
				SynchronizerUtils.getSynchronizerPlugin().getId()))
			use = true;
		
		deletePlugin(plugin, monitor);
		installPlugin(plugin, use, monitor);
		
		if (monitor != null)
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.plugin_updated",
							plugin.getName())));
	}
	
	public static void deletePlugin(
			final Plugin plugin,
			final ProgressMonitor monitor) {
		if (monitor != null)
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.start_plugin_deletion",
							plugin.getName())));
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					List<SynchronizerGuiPlugin> existingPlugins = new ArrayList<SynchronizerGuiPlugin>(
							Main.getApiPlugins().getPlugins());
					for (SynchronizerGuiPlugin existingPlugin : existingPlugins) {
						if (existingPlugin.getId().equals(plugin.getId())) {
							existingPlugin.deletePlugin();
							
							File file = Main.getApiPlugins().getFile(
									existingPlugin);
							file.delete();
							Main.getApiPlugins().removePlugin(existingPlugin);
							
							GuiLogger.getLogger().info(
									"Plugin deleted: "
											+ existingPlugin.getName()
											+ " - "
											+ existingPlugin.getVersion());
							
							plugin.setStatus(PluginStatus.DELETED);
						}
					}
					
				}
			});
		} catch (InterruptedException e) {
			
		} catch (InvocationTargetException e) {
			
		}
		
		if (monitor != null)
			monitor.addMessage(new DefaultProgressMessage(
					Translations.getString(
							"manage_plugins.progress.plugin_deleted",
							plugin.getName())));
	}
	
	private static Plugin[] loadPluginsFromXML(
			ProgressMonitor monitor,
			boolean includePublishers,
			boolean includeSynchronizers,
			boolean includeDummyPlugin) throws Exception {
		try {
			if (monitor != null)
				monitor.addMessage(new DefaultProgressMessage(
						Translations.getString("manage_plugins.progress.retrieve_plugin_database")));
			
			HttpResponse response = null;
			
			response = HttpUtils.getHttpGetResponse(new URI(
					Constants.PLUGINS_FILE));
			
			if (!response.isSuccessfull()) {
				throw new PluginException(
						PluginExceptionType.ERROR_LOADING_PLUGIN_DB);
			}
			
			if (monitor != null)
				monitor.addMessage(new DefaultProgressMessage(
						Translations.getString("manage_plugins.progress.analysing_plugin_database")));
			
			String content = response.getContent();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			
			factory.setIgnoringComments(true);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(IOUtils.toInputStream(content));
			
			document.getDocumentElement().normalize();
			
			if (!document.getChildNodes().item(0).getNodeName().equals(
					"plugins"))
				throw new Exception("Root name must be \"plugins\"");
			
			Node root = document.getChildNodes().item(0);
			
			NodeList nPlugins = root.getChildNodes();
			
			List<Plugin> plugins = new ArrayList<Plugin>();
			
			for (int i = 0; i < nPlugins.getLength(); i++) {
				if (!nPlugins.item(i).getNodeName().equals("plugin"))
					continue;
				
				NodeList nPlugin = nPlugins.item(i).getChildNodes();
				
				String id = null;
				String minVersion = null;
				String maxVersion = null;
				boolean publisher = false;
				boolean synchronizer = false;
				String name = null;
				String author = null;
				String version = null;
				String serviceProvider = null;
				String downloadUrl = null;
				String history = null;
				String historyUrl = null;
				ImageIcon logo = null;
				String logoUrl = null;
				String price = null;
				
				for (int j = 0; j < nPlugin.getLength(); j++) {
					Node element = nPlugin.item(j);
					
					if (element.getNodeName().equals("id"))
						id = element.getTextContent();
					
					if (element.getNodeName().equals("minVersion"))
						minVersion = element.getTextContent();
					
					if (element.getNodeName().equals("maxVersion"))
						maxVersion = element.getTextContent();
					
					if (element.getNodeName().equals("publisher"))
						publisher = Boolean.parseBoolean(element.getTextContent());
					
					if (element.getNodeName().equals("synchronizer"))
						synchronizer = Boolean.parseBoolean(element.getTextContent());
					
					if (element.getNodeName().equals("name"))
						name = element.getTextContent();
					
					if (element.getNodeName().equals("author"))
						author = element.getTextContent();
					
					if (element.getNodeName().equals("version"))
						version = element.getTextContent();
					
					if (element.getNodeName().equals("serviceProvider"))
						serviceProvider = element.getTextContent();
					
					if (element.getNodeName().equals("downloadUrl"))
						downloadUrl = element.getTextContent();
					
					if (element.getNodeName().equals("historyUrl"))
						historyUrl = element.getTextContent();
					
					if (element.getNodeName().equals("logoUrl"))
						logoUrl = element.getTextContent();
					
					if (element.getNodeName().equals("price"))
						price = element.getTextContent();
				}
				
				// Check min version
				if (minVersion != null && minVersion.length() != 0) {
					if (Constants.VERSION.compareTo(minVersion) < 0)
						continue;
				}
				
				// Check max version
				if (maxVersion != null && maxVersion.length() != 0) {
					if (Constants.VERSION.compareTo(maxVersion) > 0)
						continue;
				}
				
				// Check publisher & synchronizer
				if (!((includePublishers && publisher) || (includeSynchronizers && synchronizer)))
					continue;
				
				if (historyUrl != null) {
					try {
						response = HttpUtils.getHttpGetResponse(new URI(
								historyUrl));
						
						if (response.isSuccessfull()) {
							history = response.getContent();
						}
					} catch (Throwable t) {}
				}
				
				if (logoUrl != null) {
					try {
						response = HttpUtils.getHttpGetResponse(new URI(logoUrl));
						
						if (response.isSuccessfull()) {
							Image img = new ImageIcon(response.getBytes()).getImage();
							img = img.getScaledInstance(
									240,
									60,
									Image.SCALE_SMOOTH);
							logo = new ImageIcon(img);
						}
					} catch (Throwable t) {}
				}
				
				Plugin plugin = new Plugin(
						PluginStatus.TO_INSTALL,
						id,
						publisher,
						synchronizer,
						name,
						author,
						version,
						serviceProvider,
						downloadUrl,
						history,
						logo,
						price);
				
				plugins.add(plugin);
			}
			
			if (monitor != null)
				monitor.addMessage(new DefaultProgressMessage(
						Translations.getString("manage_plugins.progress.plugin_database_retrieved")));
			
			if (includeSynchronizers && includeDummyPlugin)
				plugins.add(0, DUMMY_PLUGIN);
			
			return plugins.toArray(new Plugin[0]);
		} catch (Exception e) {
			PluginLogger.getLogger().log(
					Level.WARNING,
					"Cannot load plugin database",
					e);
			
			throw new PluginException(
					PluginExceptionType.ERROR_LOADING_PLUGIN_DB,
					e);
		}
	}
	
	public static Plugin[] loadAndUpdatePluginsFromXML(
			final boolean includePublishers,
			final boolean includeSynchronizers,
			final boolean includeDummyPlugin,
			final boolean silent) {
		Plugin[] plugins = null;
		
		if (silent) {
			try {
				plugins = PluginsUtils.loadPluginsFromXML(
						null,
						includePublishers,
						includeSynchronizers,
						includeDummyPlugin);
			} catch (Exception e) {
				GuiLogger.getLogger().warning("Cannot load plugins from XML");
			}
		} else {
			TUWorkerDialog<Plugin[]> dialog = new TUWorkerDialog<Plugin[]>(
					MainFrame.getInstance().getFrame(),
					Translations.getString("general.loading_plugins"));
			
			ProgressMonitor monitor = new ProgressMonitor();
			monitor.addListChangeListener(dialog);
			
			dialog.setWorker(new TUWorker<Plugin[]>(monitor) {
				
				@Override
				protected Plugin[] longTask() throws Exception {
					return PluginsUtils.loadPluginsFromXML(
							this.getWorkerMonitor(),
							includePublishers,
							includeSynchronizers,
							includeDummyPlugin);
				}
				
			});
			
			dialog.setVisible(true);
			
			plugins = dialog.getResult();
		}
		
		if (plugins == null) {
			if (!includeDummyPlugin)
				return new Plugin[0];
			
			plugins = new Plugin[] { DUMMY_PLUGIN };
		}
		
		List<SynchronizerGuiPlugin> loadedPlugins = Main.getApiPlugins().getPlugins();
		for (SynchronizerGuiPlugin p : loadedPlugins) {
			for (int i = 0; i < plugins.length; i++) {
				if (p.getId().equals(plugins[i].getId())) {
					if (p.getVersion().compareTo(plugins[i].getVersion()) < 0)
						plugins[i].setStatus(PluginStatus.TO_UPDATE);
					else
						plugins[i].setStatus(PluginStatus.INSTALLED);
				}
			}
		}
		
		return plugins;
	}
	
}
