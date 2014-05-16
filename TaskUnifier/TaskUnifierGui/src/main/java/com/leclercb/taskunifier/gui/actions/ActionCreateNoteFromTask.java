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
package com.leclercb.taskunifier.gui.actions;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.beans.NoteBean;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

import java.awt.event.ActionEvent;

public class ActionCreateNoteFromTask extends AbstractViewTaskSelectionAction {

    public ActionCreateNoteFromTask(int width, int height) {
        super(
                Translations.getString("action.create_note_from_task"),
                ImageUtils.getResourceImage("note.png", width, height));

        this.putValue(
                SHORT_DESCRIPTION,
                Translations.getString("action.create_note_from_task"));
    }

    @Override
    public boolean shouldBeEnabled() {
        if (!super.shouldBeEnabled())
            return false;

        return ViewUtils.getSelectedTasks().length == 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionCreateNoteFromTask.createNoteFromTask();
    }

    public static void createNoteFromTask() {
        Task[] tasks = ViewUtils.getSelectedTasks();

        if (tasks.length != 1)
            return;

        createNoteFromTask(tasks[0]);
    }

    public static void createNoteFromTask(Task task) {
        if (task == null)
            return;

        NoteBean noteBean = new NoteBean();
        noteBean.setTitle(task.getTitle());
        noteBean.setNote(task.getNote());

        ActionAddNote.addNote(noteBean, true);
    }

}
