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
package com.leclercb.taskunifier.gui.api.searchers.coders;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.leclercb.commons.api.coder.AbstractFactoryXMLCoder;
import com.leclercb.commons.api.coder.exc.FactoryCoderException;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcher;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcherFactory;

public class TaskSearcherFactoryXMLCoder extends AbstractFactoryXMLCoder {
	
	private static final TaskSearcherXMLCoder TASK_SEARCHER_XML_CODER = new TaskSearcherXMLCoder();
	
	public TaskSearcherFactoryXMLCoder() {
		super("tasksearchers");
	}
	
	@Override
	public void decode(Node root) throws FactoryCoderException {
		CheckUtils.isNotNull(root);
		
		try {
			NodeList nTaskSearchers = root.getChildNodes();
			
			for (int i = 0; i < nTaskSearchers.getLength(); i++) {
				if (!nTaskSearchers.item(i).getNodeName().equals("searcher"))
					continue;
				
				Node nTaskSearcher = nTaskSearchers.item(i);
				
				TaskSearcher searcher = TASK_SEARCHER_XML_CODER.decode(nTaskSearcher);
				TaskSearcherFactory.getInstance().register(searcher);
			}
		} catch (Exception e) {
			throw new FactoryCoderException(e.getMessage(), e);
		}
	}
	
	@Override
	public void encode(Document document, Element root) {
		List<TaskSearcher> searchers = TaskSearcherFactory.getInstance().getList();
		
		for (TaskSearcher taskSearcher : searchers) {
			Element searcher = document.createElement("searcher");
			root.appendChild(searcher);
			
			TASK_SEARCHER_XML_CODER.encode(document, searcher, taskSearcher);
		}
	}
	
}
