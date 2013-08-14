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
package com.leclercb.taskunifier.gui.components.tasksearchertree.menu;

import com.leclercb.taskunifier.gui.actions.ActionAddTaskSearcher;
import com.leclercb.taskunifier.gui.actions.ActionDeleteTaskSearcher;
import com.leclercb.taskunifier.gui.actions.ActionDuplicateTaskSearcher;
import com.leclercb.taskunifier.gui.actions.ActionEditTaskSearcher;
import com.leclercb.taskunifier.gui.translations.Translations;

import javax.swing.*;

public class TaskSearcherTreeMenu extends JPopupMenu {

    public TaskSearcherTreeMenu() {
        super(Translations.getString("general.task_searcher"));

        this.initialize();
    }

    private void initialize() {
        this.add(new ActionAddTaskSearcher(16, 16));
        this.add(new ActionEditTaskSearcher(16, 16));
        this.add(new ActionDeleteTaskSearcher(16, 16));
        this.addSeparator();
        this.add(new ActionDuplicateTaskSearcher(16, 16));
    }

}
