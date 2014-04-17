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
package com.leclercb.taskunifier.api.models.templates;

import com.leclercb.commons.api.logger.ApiLogger;
import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.api.models.beans.converters.*;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.api.models.repeat.Repeat;
import com.leclercb.taskunifier.api.models.templates.converters.TaskTemplateConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;

@XStreamConverter(TaskTemplateConverter.class)
public class TaskTemplate extends AbstractBasicModel implements Template<Task, TaskBean>, PropertyChangeListener {

    public static final String PROP_TASK_TITLE = "taskTitle";
    public static final String PROP_TASK_TAGS = "taskTags";
    public static final String PROP_TASK_FOLDER_FORCE = "taskFolderForce";
    public static final String PROP_TASK_FOLDER = "taskFolder";
    public static final String PROP_TASK_CONTEXTS = "taskContexts";
    public static final String PROP_TASK_GOALS = "taskGoals";
    public static final String PROP_TASK_LOCATIONS = "taskLocations";
    public static final String PROP_TASK_PROGRESS = "taskProgress";
    public static final String PROP_TASK_COMPLETED = "taskCompleted";
    public static final String PROP_TASK_DUE_DATE = "taskDueDate";
    public static final String PROP_TASK_DUE_TIME = "taskDueTime";
    public static final String PROP_TASK_START_DATE = "taskStartDate";
    public static final String PROP_TASK_START_TIME = "taskStartTime";
    public static final String PROP_TASK_DUE_DATE_REMINDER = "taskDueDateReminder";
    public static final String PROP_TASK_START_DATE_REMINDER = "taskStartDateReminder";
    public static final String PROP_TASK_REPEAT = "taskRepeat";
    public static final String PROP_TASK_REPEAT_FROM = "taskRepeatFrom";
    public static final String PROP_TASK_STATUS_FORCE = "taskStatusForce";
    public static final String PROP_TASK_STATUS = "taskStatus";
    public static final String PROP_TASK_LENGTH = "taskLength";
    public static final String PROP_TASK_PRIORITY = "taskPriority";
    public static final String PROP_TASK_STAR = "taskStar";
    public static final String PROP_TASK_NOTE = "taskNote";

    @XStreamOmitField
    @XStreamAlias("taskcontextforce")
    // Backward compatibility
    private boolean taskContextForce;

    @XStreamOmitField
    @XStreamAlias("taskgoalforce")
    // Backward compatibility
    private boolean taskGoalForce;

    @XStreamOmitField
    @XStreamAlias("tasklocationforce")
    // Backward compatibility
    private boolean taskLocationForce;

    @XStreamAlias("tasktitle")
    private String taskTitle;

    @XStreamAlias("tasktags")
    private String taskTags;

    @XStreamAlias("taskfolderforce")
    private boolean taskFolderForce;

    @XStreamAlias("taskfolder")
    @XStreamConverter(FolderConverter.class)
    private Folder taskFolder;

    @XStreamAlias("taskcontexts")
    @XStreamConverter(ContextListConverter.class)
    private ModelList<Context> taskContexts;

    @XStreamAlias("taskgoals")
    @XStreamConverter(GoalListConverter.class)
    private ModelList<Goal> taskGoals;

    @XStreamAlias("tasklocations")
    @XStreamConverter(LocationListConverter.class)
    private ModelList<Location> taskLocations;

    @XStreamAlias("taskprogress")
    private Double taskProgress;

    @XStreamAlias("taskcompleted")
    private Boolean taskCompleted;

    @XStreamAlias("taskduedate")
    private Integer taskDueDate;

    @XStreamAlias("taskduetime")
    private Integer taskDueTime;

    @XStreamAlias("taskstartdate")
    private Integer taskStartDate;

    @XStreamAlias("taskstarttime")
    private Integer taskStartTime;

    @XStreamAlias("taskstartdatereminder")
    private Integer taskStartDateReminder;

    @XStreamAlias("taskreminder")
    private Integer taskDueDateReminder;

    @XStreamAlias("taskrepeat-v2")
    private Repeat taskRepeat;

    @XStreamAlias("taskrepeatfrom")
    private TaskRepeatFrom taskRepeatFrom;

    @XStreamAlias("taskstatusforce")
    private boolean taskStatusForce;

    @XStreamAlias("taskstatus-v2")
    @XStreamConverter(TaskStatusConverter.class)
    private TaskStatus taskStatus;

    @XStreamAlias("tasklength")
    private Integer taskLength;

    @XStreamAlias("taskpriority")
    private TaskPriority taskPriority;

    @XStreamAlias("taskstar")
    private Boolean taskStar;

    @XStreamAlias("tasknote")
    private String taskNote;

    public TaskTemplate() {
        this(new ModelId(), "");
    }

    public TaskTemplate(String title) {
        this(new ModelId(), title);
    }

    public TaskTemplate(ModelId modelId, String title) {
        super(modelId, title);

        this.setTaskTitle(null);
        this.setTaskTags(null);
        this.setTaskFolder(null, false);
        this.setTaskContexts(null);
        this.setTaskGoals(null);
        this.setTaskLocations(null);
        this.setTaskProgress(null);
        this.setTaskCompleted(null);
        this.setTaskDueDate(null);
        this.setTaskDueTime(null);
        this.setTaskStartDate(null);
        this.setTaskStartTime(null);
        this.setTaskDueDateReminder(null);
        this.setTaskStartDateReminder(null);
        this.setTaskRepeat(null);
        this.setTaskRepeatFrom(null);
        this.setTaskStatus(null, false);
        this.setTaskLength(null);
        this.setTaskPriority(null);
        this.setTaskStar(null);
        this.setTaskNote(null);
    }

    public TaskTemplate(Task task) {
        this(task.getTitle());

        this.setTaskTitle(task.getTitle());
        this.setTaskTags(task.getTags().toString());
        this.setTaskFolder(task.getFolder(), false);
        this.setTaskContexts(task.getContexts());
        this.setTaskGoals(task.getGoals());
        this.setTaskLocations(task.getLocations());
        this.setTaskProgress(task.getProgress());
        this.setTaskCompleted(task.isCompleted());
        this.setTaskDueDate(null);
        this.setTaskDueTime(null);
        this.setTaskStartDate(null);
        this.setTaskStartTime(null);
        this.setTaskDueDateReminder(task.getDueDateReminder());
        this.setTaskStartDateReminder(task.getStartDateReminder());
        this.setTaskRepeat(task.getRepeat());
        this.setTaskRepeatFrom(task.getRepeatFrom());
        this.setTaskStatus(task.getStatus(), false);
        this.setTaskLength(task.getLength());
        this.setTaskPriority(task.getPriority());
        this.setTaskStar(task.isStar());
        this.setTaskNote(task.getNote());
    }

    @Override
    public TaskTemplate clone(ModelId modelId) {
        TaskTemplate template = new TaskTemplate(modelId, this.getTitle());

        template.setTaskTitle(this.taskTitle);
        template.setTaskTags(this.taskTags);
        template.setTaskFolder(this.taskFolder, this.taskFolderForce);
        template.setTaskContexts(this.taskContexts);
        template.setTaskGoals(this.taskGoals);
        template.setTaskLocations(this.taskLocations);
        template.setTaskProgress(this.taskProgress);
        template.setTaskCompleted(this.taskCompleted);
        template.setTaskDueDate(this.taskDueDate);
        template.setTaskDueTime(this.taskDueTime);
        template.setTaskStartDate(this.taskStartDate);
        template.setTaskStartTime(this.taskStartTime);
        template.setTaskStartDateReminder(this.taskStartDateReminder);
        template.setTaskDueDateReminder(this.taskDueDateReminder);
        template.setTaskRepeat(this.taskRepeat);
        template.setTaskRepeatFrom(this.taskRepeatFrom);
        template.setTaskStatus(this.taskStatus, this.taskStatusForce);
        template.setTaskLength(this.taskLength);
        template.setTaskPriority(this.taskPriority);
        template.setTaskStar(this.taskStar);
        template.setTaskNote(this.taskNote);

        // After all other setXxx methods
        template.addProperties(this.getProperties());
        template.setModelStatus(this.getModelStatus());
        template.setModelCreationDate(Calendar.getInstance());
        template.setModelUpdateDate(Calendar.getInstance());

        return template;
    }

    @Override
    public void applyTo(Task task) {
        if (task == null)
            return;

        if (this.taskTitle != null && this.taskTitle.length() != 0)
            task.setTitle(this.taskTitle);

        if (this.taskTags != null && this.taskTags.length() != 0) {
            TagList tags = TagList.fromString(this.taskTags);
            tags.addTags(task.getTags());

            task.setTags(tags);
        }

        if (this.taskFolderForce || this.taskFolder != null)
            task.setFolder(this.taskFolder);

        if (this.taskContexts != null)
            task.getContexts().addAll(this.taskContexts.getList());

        if (this.taskGoals != null)
            task.getGoals().addAll(this.taskGoals.getList());

        if (this.taskLocations != null)
            task.getLocations().addAll(this.taskLocations.getList());

        if (this.taskProgress != null)
            task.setProgress(this.taskProgress);

        if (this.taskCompleted != null)
            task.setCompleted(this.taskCompleted);

        if (this.taskDueDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, this.taskDueDate);

            if (this.taskDueTime != null) {
                int hour = this.taskDueTime / 60;
                int minute = this.taskDueTime % 60;

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
            }

            task.setDueDate(calendar);
        }

        if (this.taskStartDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, this.taskStartDate);

            if (this.taskStartTime != null) {
                int hour = this.taskStartTime / 60;
                int minute = this.taskStartTime % 60;

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
            }

            task.setStartDate(calendar);
        }

        if (this.taskDueDateReminder != null)
            task.setDueDateReminder(this.taskDueDateReminder);

        if (this.taskStartDateReminder != null)
            task.setStartDateReminder(this.taskStartDateReminder);

        if (this.taskRepeat != null)
            task.setRepeat(this.taskRepeat);

        if (this.taskRepeatFrom != null)
            task.setRepeatFrom(this.taskRepeatFrom);

        if (this.taskStatusForce || this.taskStatus != null)
            task.setStatus(this.taskStatus);

        if (this.taskLength != null)
            task.setLength(this.taskLength);

        if (this.taskPriority != null)
            task.setPriority(this.taskPriority);

        if (this.taskStar != null)
            task.setStar(this.taskStar);

        if (this.taskNote != null && this.taskNote.length() != 0)
            task.setNote(this.taskNote);
    }

    @Override
    public void applyTo(TaskBean task) {
        if (task == null)
            return;

        if (this.taskTitle != null && this.taskTitle.length() != 0)
            task.setTitle(this.taskTitle);

        if (this.taskTags != null && this.taskTags.length() != 0) {
            TagList tags = TagList.fromString(this.taskTags);
            tags.addTags(task.getTags());

            task.setTags(tags);
        }

        if (this.taskFolderForce || this.taskFolder != null)
            task.setFolder(this.taskFolder.getModelId());

        if (this.taskContexts != null)
            task.setContexts(this.taskContexts.toModelBeanList());

        if (this.taskGoals != null)
            task.setGoals(this.taskGoals.toModelBeanList());

        if (this.taskLocations != null)
            task.setLocations(this.taskLocations.toModelBeanList());

        if (this.taskProgress != null)
            task.setProgress(this.taskProgress);

        if (this.taskCompleted != null)
            task.setCompleted(this.taskCompleted);

        if (this.taskDueDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, this.taskDueDate);

            if (this.taskDueTime != null) {
                int hour = this.taskDueTime / 60;
                int minute = this.taskDueTime % 60;

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
            }

            task.setDueDate(calendar);
        }

        if (this.taskStartDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, this.taskStartDate);

            if (this.taskStartTime != null) {
                int hour = this.taskStartTime / 60;
                int minute = this.taskStartTime % 60;

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
            }

            task.setStartDate(calendar);
        }

        if (this.taskDueDateReminder != null)
            task.setDueDateReminder(this.taskDueDateReminder);

        if (this.taskStartDateReminder != null)
            task.setStartDateReminder(this.taskStartDateReminder);

        if (this.taskRepeat != null)
            task.setRepeat(this.taskRepeat);

        if (this.taskRepeatFrom != null)
            task.setRepeatFrom(this.taskRepeatFrom);

        if (this.taskStatusForce || this.taskStatus != null)
            task.setStatus(this.taskStatus.getModelId());

        if (this.taskLength != null)
            task.setLength(this.taskLength);

        if (this.taskPriority != null)
            task.setPriority(this.taskPriority);

        if (this.taskStar != null)
            task.setStar(this.taskStar);

        if (this.taskNote != null && this.taskNote.length() != 0)
            task.setNote(this.taskNote);
    }

    public String getTaskTitle() {
        return this.taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        String oldTaskTitle = this.taskTitle;
        this.taskTitle = taskTitle;
        this.updateProperty(PROP_TASK_TITLE, oldTaskTitle, taskTitle);
    }

    public String getTaskTags() {
        return this.taskTags;
    }

    public void setTaskTags(String taskTags) {
        String oldTaskTags = this.taskTags;
        this.taskTags = taskTags;
        this.updateProperty(PROP_TASK_TAGS, oldTaskTags, taskTags);
    }

    public boolean isTaskFolderForce() {
        return this.taskFolderForce;
    }

    public void setTaskFolderForce(boolean force) {
        boolean oldTaskFolderForce = this.taskFolderForce;
        this.taskFolderForce = force;
        this.updateProperty(PROP_TASK_FOLDER_FORCE, oldTaskFolderForce, force);
    }

    public Folder getTaskFolder() {
        return this.taskFolder;
    }

    public void setTaskFolder(Folder taskFolder) {
        this.setTaskFolder(taskFolder, false);
    }

    public void setTaskFolder(Folder taskFolder, boolean force) {
        if (taskFolder != null) {
            if (taskFolder.getModelStatus().equals(ModelStatus.TO_DELETE)
                    || taskFolder.getModelStatus().equals(ModelStatus.DELETED)) {
                ApiLogger.getLogger().severe(
                        "You cannot assign a deleted model");
                taskFolder = null;
            }
        }

        if (this.taskFolder != null)
            this.taskFolder.removePropertyChangeListener(this);

        Folder oldTaskFolder = this.taskFolder;
        this.taskFolder = taskFolder;

        if (this.taskFolder != null)
            this.taskFolder.addPropertyChangeListener(this);

        boolean oldTaskFolderForce = this.taskFolderForce;
        this.taskFolderForce = force;

        this.updateProperty(PROP_TASK_FOLDER_FORCE, oldTaskFolderForce, force);

        this.updateProperty(PROP_TASK_FOLDER, oldTaskFolder, taskFolder);
    }

    public ModelList<Context> getTaskContexts() {
        return this.taskContexts;
    }

    public void setTaskContexts(ModelList<Context> taskContexts) {
        ModelList<Context> oldTaskContexts = this.taskContexts;
        this.taskContexts = taskContexts;

        this.updateProperty(PROP_TASK_CONTEXTS, oldTaskContexts, taskContexts);
    }

    public ModelList<Goal> getTaskGoals() {
        return this.taskGoals;
    }

    public void setTaskGoals(ModelList<Goal> taskGoals) {
        ModelList<Goal> oldTaskGoals = this.taskGoals;
        this.taskGoals = taskGoals;

        this.updateProperty(PROP_TASK_GOALS, oldTaskGoals, taskGoals);
    }

    public ModelList<Location> getTaskLocations() {
        return this.taskLocations;
    }

    public void setTaskLocations(ModelList<Location> taskLocations) {
        ModelList<Location> oldTaskLocations = this.taskLocations;
        this.taskLocations = taskLocations;

        this.updateProperty(
                PROP_TASK_LOCATIONS,
                oldTaskLocations,
                taskLocations);
    }

    public Double getTaskProgress() {
        return this.taskProgress;
    }

    public void setTaskProgress(Double taskProgress) {
        Double oldTaskProgress = this.taskProgress;
        this.taskProgress = taskProgress;
        this.updateProperty(PROP_TASK_PROGRESS, oldTaskProgress, taskProgress);
    }

    public Boolean getTaskCompleted() {
        return this.taskCompleted;
    }

    public void setTaskCompleted(Boolean taskCompleted) {
        Boolean oldTaskCompleted = this.taskCompleted;
        this.taskCompleted = taskCompleted;
        this.updateProperty(
                PROP_TASK_COMPLETED,
                oldTaskCompleted,
                taskCompleted);
    }

    public Integer getTaskDueDate() {
        return this.taskDueDate;
    }

    public void setTaskDueDate(Integer taskDueDate) {
        Integer oldTaskDueDate = this.taskDueDate;
        this.taskDueDate = taskDueDate;
        this.updateProperty(PROP_TASK_DUE_DATE, oldTaskDueDate, taskDueDate);
    }

    public Integer getTaskDueTime() {
        return this.taskDueTime;
    }

    public void setTaskDueTime(Integer taskDueTime) {
        Integer oldTaskDueTime = this.taskDueTime;
        this.taskDueTime = taskDueTime;
        this.updateProperty(PROP_TASK_DUE_TIME, oldTaskDueTime, taskDueTime);
    }

    public Integer getTaskStartDate() {
        return this.taskStartDate;
    }

    public void setTaskStartDate(Integer taskStartDate) {
        Integer oldTaskStartDate = this.taskStartDate;
        this.taskStartDate = taskStartDate;
        this.updateProperty(
                PROP_TASK_START_DATE,
                oldTaskStartDate,
                taskStartDate);
    }

    public Integer getTaskStartTime() {
        return this.taskStartTime;
    }

    public void setTaskStartTime(Integer taskStartTime) {
        Integer oldTaskStartTime = this.taskStartTime;
        this.taskStartTime = taskStartTime;
        this.updateProperty(
                PROP_TASK_START_TIME,
                oldTaskStartTime,
                taskStartTime);
    }

    public Integer getTaskDueDateReminder() {
        return this.taskDueDateReminder;
    }

    public void setTaskDueDateReminder(Integer taskDueDateReminder) {
        Integer oldTaskDueDateReminder = this.taskDueDateReminder;
        this.taskDueDateReminder = taskDueDateReminder;
        this.updateProperty(
                PROP_TASK_DUE_DATE_REMINDER,
                oldTaskDueDateReminder,
                taskDueDateReminder);
    }

    public Integer getTaskStartDateReminder() {
        return this.taskStartDateReminder;
    }

    public void setTaskStartDateReminder(Integer taskStartDateReminder) {
        Integer oldTaskStartDateReminder = this.taskStartDateReminder;
        this.taskStartDateReminder = taskStartDateReminder;
        this.updateProperty(
                PROP_TASK_START_DATE_REMINDER,
                oldTaskStartDateReminder,
                taskStartDateReminder);
    }

    public Repeat getTaskRepeat() {
        return this.taskRepeat;
    }

    public void setTaskRepeat(Repeat taskRepeat) {
        Repeat oldTaskRepeat = this.taskRepeat;
        this.taskRepeat = taskRepeat;
        this.updateProperty(PROP_TASK_REPEAT, oldTaskRepeat, taskRepeat);
    }

    public TaskRepeatFrom getTaskRepeatFrom() {
        return this.taskRepeatFrom;
    }

    public void setTaskRepeatFrom(TaskRepeatFrom taskRepeatFrom) {
        TaskRepeatFrom oldTaskRepeatFrom = this.taskRepeatFrom;
        this.taskRepeatFrom = taskRepeatFrom;
        this.updateProperty(
                PROP_TASK_REPEAT_FROM,
                oldTaskRepeatFrom,
                taskRepeatFrom);
    }

    public boolean isTaskStatusForce() {
        return this.taskStatusForce;
    }

    public void setTaskStatusForce(boolean force) {
        boolean oldTaskStatusForce = this.taskStatusForce;
        this.taskStatusForce = force;
        this.updateProperty(PROP_TASK_STATUS_FORCE, oldTaskStatusForce, force);
    }

    public TaskStatus getTaskStatus() {
        return this.taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.setTaskStatus(taskStatus, false);
    }

    public void setTaskStatus(TaskStatus taskStatus, boolean force) {
        if (taskStatus != null) {
            if (taskStatus.getModelStatus().equals(ModelStatus.TO_DELETE)
                    || taskStatus.getModelStatus().equals(ModelStatus.DELETED)) {
                ApiLogger.getLogger().severe(
                        "You cannot assign a deleted model");
                taskStatus = null;
            }
        }

        if (this.taskStatus != null)
            this.taskStatus.removePropertyChangeListener(this);

        TaskStatus oldTaskStatus = this.taskStatus;
        this.taskStatus = taskStatus;

        if (this.taskStatus != null)
            this.taskStatus.addPropertyChangeListener(this);

        boolean oldTaskStatusForce = this.taskStatusForce;
        this.taskStatusForce = force;

        this.updateProperty(PROP_TASK_STATUS_FORCE, oldTaskStatusForce, force);

        this.updateProperty(PROP_TASK_STATUS, oldTaskStatus, taskStatus);
    }

    public Integer getTaskLength() {
        return this.taskLength;
    }

    public void setTaskLength(Integer taskLength) {
        Integer oldTaskLength = this.taskLength;
        this.taskLength = taskLength;
        this.updateProperty(PROP_TASK_LENGTH, oldTaskLength, taskLength);
    }

    public TaskPriority getTaskPriority() {
        return this.taskPriority;
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        TaskPriority oldTaskPriority = this.taskPriority;
        this.taskPriority = taskPriority;
        this.updateProperty(PROP_TASK_PRIORITY, oldTaskPriority, taskPriority);
    }

    public Boolean getTaskStar() {
        return this.taskStar;
    }

    public void setTaskStar(Boolean taskStar) {
        Boolean oldTaskStar = this.taskStar;
        this.taskStar = taskStar;
        this.updateProperty(PROP_TASK_STAR, oldTaskStar, taskStar);
    }

    public String getTaskNote() {
        return this.taskNote;
    }

    public void setTaskNote(String taskNote) {
        String oldTaskNote = this.taskNote;
        this.taskNote = taskNote;
        this.updateProperty(PROP_TASK_NOTE, oldTaskNote, taskNote);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getSource() instanceof Folder
                && event.getPropertyName().equals(PROP_MODEL_STATUS)) {
            Folder taskFolder = (Folder) event.getSource();

            if (taskFolder.getModelStatus().equals(ModelStatus.TO_DELETE)
                    || taskFolder.getModelStatus().equals(ModelStatus.DELETED))
                this.setTaskFolder(null, true);
        }
    }

}
