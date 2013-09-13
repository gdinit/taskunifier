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
package com.leclercb.taskunifier.cl.main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Main {
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0 || args.length > 2) {
			exitAndPrintUsage();
			return;
		}
		
		String title = args[0];
		int port = 4576;
		boolean xmlFormat = title.endsWith(".xml");
		
		if (args.length == 2) {
			if (args[1].matches("[0-9]{1,4}")) {
				port = Integer.parseInt(args[1]);
			} else {
				System.err.println("Port is invalid: [0-9]{1,4}");
				exitAndPrintUsage();
			}
		}
		
		String xml = null;
		
		if (xmlFormat) {
			BufferedReader reader = null;
			
			try {
				StringBuffer xmlBuffer = new StringBuffer();
				String currentLine;
				reader = new BufferedReader(new FileReader(title));
				
				while ((currentLine = reader.readLine()) != null)
					xmlBuffer.append(currentLine);
				
				xml = xmlBuffer.toString();
			} catch (Exception e) {
				System.err.println("An error occured while reading the xml file");
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			try {
				xml = createXml(title);
			} catch (Exception e) {
				System.err.println("An error occured during the creation of the xml");
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		try {
			sendXml(xml, port);
		} catch (Exception e) {
			System.err.println("Please check that TaskUnifier is started");
			System.exit(1);
		}
		
		System.exit(0);
	}
	
	private static void exitAndPrintUsage() {
		System.err.println("Usage: \"quick task\" [port]");
		System.err.println("Usage: file.xml [port]");
		System.exit(1);
	}
	
	private static void sendXml(String xml, int port) throws Exception {
		Socket socket = new Socket("127.0.0.1", port);
		BufferedOutputStream stream = new BufferedOutputStream(
				socket.getOutputStream());
		stream.write(xml.getBytes());
		
		stream.close();
		socket.close();
	}
	
	private static String createXml(String title) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation implementation = builder.getDOMImplementation();
		
		Document document = implementation.createDocument(null, null, null);
		Element root = document.createElement("com");
		document.appendChild(root);
		
		Element applicationNameElement = document.createElement("applicationName");
		applicationNameElement.setTextContent("TaskUnifier");
		
		Element quickTasksElement = document.createElement("quicktasks");
		
		Element quickTaskElement = document.createElement("quicktask");
		
		Element titleElement = document.createElement("title");
		titleElement.setTextContent(title);
		
		root.appendChild(applicationNameElement);
		root.appendChild(quickTasksElement);
		quickTasksElement.appendChild(quickTaskElement);
		quickTaskElement.appendChild(titleElement);
		
		DOMSource domSource = new DOMSource(document);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount",
				"4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		StreamResult sr = new StreamResult(output);
		transformer.transform(domSource, sr);
		
		return output.toString("UTF-8");
	}
	
}
