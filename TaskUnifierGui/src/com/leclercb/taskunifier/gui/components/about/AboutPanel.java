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
package com.leclercb.taskunifier.gui.components.about;

import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.SystemUtils;
import org.jdesktop.swingx.JXEditorPane;

import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.resources.Resources;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class AboutPanel extends JPanel {
	
	public AboutPanel() {
		this.initialize();
	}
	
	private void initialize() {
		this.setLayout(new MigLayout());
		
		JLabel icon = new JLabel(
				ImageUtils.getResourceImage("logo.png", 48, 48));
		JLabel title = new JLabel(Constants.TITLE + " - " + Constants.VERSION);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JXEditorPane aboutMessage = new JXEditorPane();
		aboutMessage.setContentType("text/html");
		aboutMessage.setEditable(false);
		aboutMessage.setText(this.getAboutMessage());
		aboutMessage.setCaretPosition(0);
		
		JXEditorPane systemMessage = new JXEditorPane();
		systemMessage.setContentType("text/html");
		systemMessage.setEditable(false);
		systemMessage.setText(this.getSystemMessage());
		systemMessage.setCaretPosition(0);
		
		tabbedPane.addTab(
				Translations.getString("general.about"),
				ImageUtils.getResourceImage("logo.png", 16, 16),
				ComponentFactory.createJScrollPane(aboutMessage, true));
		
		tabbedPane.addTab(
				Translations.getString("general.system"),
				ImageUtils.getResourceImage("settings.png", 16, 16),
				ComponentFactory.createJScrollPane(systemMessage, true));
		
		this.add(icon, "gap 0px 20px");
		this.add(title, "wrap 20px");
		this.add(tabbedPane, "span");
	}
	
	private String getAboutMessage() {
		try {
			Properties properties = new Properties();
			properties.load(Resources.class.getResourceAsStream("about_message.properties"));
			return (String) properties.get("about.message");
		} catch (Exception e) {
			return null;
		}
	}
	
	private String getSystemMessage() {
		StringBuffer s = new StringBuffer();
		s.append("<html>");
		
		s.append("<b>Java Version: </b>" + SystemUtils.JAVA_VERSION + "<br />");
		s.append("<b>Java Home: </b>" + SystemUtils.JAVA_HOME + "<br />");
		s.append("<b>OS Name: </b>" + SystemUtils.OS_NAME + "<br />");
		s.append("<b>OS Version: </b>" + SystemUtils.OS_VERSION + "<br />");
		s.append("<b>OS Architecture: </b>" + SystemUtils.OS_ARCH + "<br />");
		s.append("<b>User Name: </b>" + SystemUtils.USER_NAME + "<br />");
		s.append("<b>User Directory: </b>" + SystemUtils.USER_DIR + "<br />");
		s.append("<b>User Home: </b>" + SystemUtils.USER_HOME + "<br />");
		
		s.append("</html>");
		
		return s.toString();
	}
	
}
