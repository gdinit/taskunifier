﻿<Global.Microsoft.VisualBasic.CompilerServices.DesignerGenerated()> _
Partial Class Settings
    Inherits System.Windows.Forms.Form

    'Form overrides dispose to clean up the component list.
    <System.Diagnostics.DebuggerNonUserCode()> _
    Protected Overrides Sub Dispose(ByVal disposing As Boolean)
        Try
            If disposing AndAlso components IsNot Nothing Then
                components.Dispose()
            End If
        Finally
            MyBase.Dispose(disposing)
        End Try
    End Sub

    'Required by the Windows Form Designer
    Private components As System.ComponentModel.IContainer

    'NOTE: The following procedure is required by the Windows Form Designer
    'It can be modified using the Windows Form Designer.  
    'Do not modify it using the code editor.
    <System.Diagnostics.DebuggerStepThrough()> _
    Private Sub InitializeComponent()
        Me.tabs = New System.Windows.Forms.TabControl()
        Me.TabPage1 = New System.Windows.Forms.TabPage()
        Me.lbCategories = New System.Windows.Forms.Label()
        Me.cbCategories = New System.Windows.Forms.ComboBox()
        Me.cbTaskNote = New System.Windows.Forms.CheckBox()
        Me.cbTaskDueDate = New System.Windows.Forms.CheckBox()
        Me.cbTaskStartDate = New System.Windows.Forms.CheckBox()
        Me.cbTaskTitle = New System.Windows.Forms.CheckBox()
        Me.TabPage2 = New System.Windows.Forms.TabPage()
        Me.cbNoteNote = New System.Windows.Forms.CheckBox()
        Me.cbNoteFolder = New System.Windows.Forms.CheckBox()
        Me.cbNoteTitle = New System.Windows.Forms.CheckBox()
        Me.TabPage3 = New System.Windows.Forms.TabPage()
        Me.lbDoNotChange = New System.Windows.Forms.Label()
        Me.lbSocketPort = New System.Windows.Forms.Label()
        Me.tfSocketPort = New System.Windows.Forms.MaskedTextBox()
        Me.btCancel = New System.Windows.Forms.Button()
        Me.btOk = New System.Windows.Forms.Button()
        Me.tabs.SuspendLayout()
        Me.TabPage1.SuspendLayout()
        Me.TabPage2.SuspendLayout()
        Me.TabPage3.SuspendLayout()
        Me.SuspendLayout()
        '
        'tabs
        '
        Me.tabs.Controls.Add(Me.TabPage1)
        Me.tabs.Controls.Add(Me.TabPage2)
        Me.tabs.Controls.Add(Me.TabPage3)
        Me.tabs.Location = New System.Drawing.Point(12, 12)
        Me.tabs.Name = "tabs"
        Me.tabs.SelectedIndex = 0
        Me.tabs.Size = New System.Drawing.Size(362, 183)
        Me.tabs.TabIndex = 8
        Me.tabs.Tag = ""
        '
        'TabPage1
        '
        Me.TabPage1.Controls.Add(Me.lbCategories)
        Me.TabPage1.Controls.Add(Me.cbCategories)
        Me.TabPage1.Controls.Add(Me.cbTaskNote)
        Me.TabPage1.Controls.Add(Me.cbTaskDueDate)
        Me.TabPage1.Controls.Add(Me.cbTaskStartDate)
        Me.TabPage1.Controls.Add(Me.cbTaskTitle)
        Me.TabPage1.Location = New System.Drawing.Point(4, 22)
        Me.TabPage1.Name = "TabPage1"
        Me.TabPage1.Padding = New System.Windows.Forms.Padding(3)
        Me.TabPage1.Size = New System.Drawing.Size(354, 157)
        Me.TabPage1.TabIndex = 0
        Me.TabPage1.Text = "Tasks"
        Me.TabPage1.UseVisualStyleBackColor = True
        '
        'lbCategories
        '
        Me.lbCategories.AutoSize = True
        Me.lbCategories.Location = New System.Drawing.Point(24, 127)
        Me.lbCategories.Name = "lbCategories"
        Me.lbCategories.Size = New System.Drawing.Size(116, 13)
        Me.lbCategories.TabIndex = 6
        Me.lbCategories.Text = "Outlook categories as: "
        '
        'cbCategories
        '
        Me.cbCategories.FormattingEnabled = True
        Me.cbCategories.Items.AddRange(New Object() {"First category as Context", "First category as Folder", "First category as Tag", "All categories as Contexts", "All categories as Tags"})
        Me.cbCategories.Location = New System.Drawing.Point(146, 124)
        Me.cbCategories.Name = "cbCategories"
        Me.cbCategories.Size = New System.Drawing.Size(202, 21)
        Me.cbCategories.TabIndex = 5
        '
        'cbTaskNote
        '
        Me.cbTaskNote.AutoSize = True
        Me.cbTaskNote.Checked = True
        Me.cbTaskNote.CheckState = System.Windows.Forms.CheckState.Checked
        Me.cbTaskNote.Location = New System.Drawing.Point(24, 92)
        Me.cbTaskNote.Name = "cbTaskNote"
        Me.cbTaskNote.Size = New System.Drawing.Size(49, 17)
        Me.cbTaskNote.TabIndex = 4
        Me.cbTaskNote.Text = "Note"
        Me.cbTaskNote.UseVisualStyleBackColor = True
        '
        'cbTaskDueDate
        '
        Me.cbTaskDueDate.AutoSize = True
        Me.cbTaskDueDate.Checked = True
        Me.cbTaskDueDate.CheckState = System.Windows.Forms.CheckState.Checked
        Me.cbTaskDueDate.Location = New System.Drawing.Point(24, 69)
        Me.cbTaskDueDate.Name = "cbTaskDueDate"
        Me.cbTaskDueDate.Size = New System.Drawing.Size(72, 17)
        Me.cbTaskDueDate.TabIndex = 2
        Me.cbTaskDueDate.Text = "Due Date"
        Me.cbTaskDueDate.UseVisualStyleBackColor = True
        '
        'cbTaskStartDate
        '
        Me.cbTaskStartDate.AutoSize = True
        Me.cbTaskStartDate.Checked = True
        Me.cbTaskStartDate.CheckState = System.Windows.Forms.CheckState.Checked
        Me.cbTaskStartDate.Location = New System.Drawing.Point(24, 45)
        Me.cbTaskStartDate.Name = "cbTaskStartDate"
        Me.cbTaskStartDate.Size = New System.Drawing.Size(74, 17)
        Me.cbTaskStartDate.TabIndex = 1
        Me.cbTaskStartDate.Text = "Start Date"
        Me.cbTaskStartDate.UseVisualStyleBackColor = True
        '
        'cbTaskTitle
        '
        Me.cbTaskTitle.AutoSize = True
        Me.cbTaskTitle.Checked = True
        Me.cbTaskTitle.CheckState = System.Windows.Forms.CheckState.Checked
        Me.cbTaskTitle.Location = New System.Drawing.Point(24, 21)
        Me.cbTaskTitle.Name = "cbTaskTitle"
        Me.cbTaskTitle.Size = New System.Drawing.Size(46, 17)
        Me.cbTaskTitle.TabIndex = 0
        Me.cbTaskTitle.Text = "Title"
        Me.cbTaskTitle.UseVisualStyleBackColor = True
        '
        'TabPage2
        '
        Me.TabPage2.Controls.Add(Me.cbNoteNote)
        Me.TabPage2.Controls.Add(Me.cbNoteFolder)
        Me.TabPage2.Controls.Add(Me.cbNoteTitle)
        Me.TabPage2.Location = New System.Drawing.Point(4, 22)
        Me.TabPage2.Name = "TabPage2"
        Me.TabPage2.Padding = New System.Windows.Forms.Padding(3)
        Me.TabPage2.Size = New System.Drawing.Size(354, 157)
        Me.TabPage2.TabIndex = 1
        Me.TabPage2.Text = "Notes"
        Me.TabPage2.UseVisualStyleBackColor = True
        '
        'cbNoteNote
        '
        Me.cbNoteNote.AutoSize = True
        Me.cbNoteNote.Checked = True
        Me.cbNoteNote.CheckState = System.Windows.Forms.CheckState.Checked
        Me.cbNoteNote.Location = New System.Drawing.Point(23, 67)
        Me.cbNoteNote.Name = "cbNoteNote"
        Me.cbNoteNote.Size = New System.Drawing.Size(49, 17)
        Me.cbNoteNote.TabIndex = 5
        Me.cbNoteNote.Text = "Note"
        Me.cbNoteNote.UseVisualStyleBackColor = True
        '
        'cbNoteFolder
        '
        Me.cbNoteFolder.AutoSize = True
        Me.cbNoteFolder.Checked = True
        Me.cbNoteFolder.CheckState = System.Windows.Forms.CheckState.Checked
        Me.cbNoteFolder.Location = New System.Drawing.Point(23, 44)
        Me.cbNoteFolder.Name = "cbNoteFolder"
        Me.cbNoteFolder.Size = New System.Drawing.Size(55, 17)
        Me.cbNoteFolder.TabIndex = 3
        Me.cbNoteFolder.Text = "Folder"
        Me.cbNoteFolder.UseVisualStyleBackColor = True
        '
        'cbNoteTitle
        '
        Me.cbNoteTitle.AutoSize = True
        Me.cbNoteTitle.Checked = True
        Me.cbNoteTitle.CheckState = System.Windows.Forms.CheckState.Checked
        Me.cbNoteTitle.Location = New System.Drawing.Point(23, 20)
        Me.cbNoteTitle.Name = "cbNoteTitle"
        Me.cbNoteTitle.Size = New System.Drawing.Size(46, 17)
        Me.cbNoteTitle.TabIndex = 2
        Me.cbNoteTitle.Text = "Title"
        Me.cbNoteTitle.UseVisualStyleBackColor = True
        '
        'TabPage3
        '
        Me.TabPage3.Controls.Add(Me.lbDoNotChange)
        Me.TabPage3.Controls.Add(Me.lbSocketPort)
        Me.TabPage3.Controls.Add(Me.tfSocketPort)
        Me.TabPage3.Location = New System.Drawing.Point(4, 22)
        Me.TabPage3.Name = "TabPage3"
        Me.TabPage3.Padding = New System.Windows.Forms.Padding(3)
        Me.TabPage3.Size = New System.Drawing.Size(354, 157)
        Me.TabPage3.TabIndex = 2
        Me.TabPage3.Text = "Advanced"
        Me.TabPage3.UseVisualStyleBackColor = True
        '
        'lbDoNotChange
        '
        Me.lbDoNotChange.AutoSize = True
        Me.lbDoNotChange.Location = New System.Drawing.Point(50, 53)
        Me.lbDoNotChange.Name = "lbDoNotChange"
        Me.lbDoNotChange.Size = New System.Drawing.Size(259, 13)
        Me.lbDoNotChange.TabIndex = 5
        Me.lbDoNotChange.Text = "Do not change unless you know what you are doing !"
        '
        'lbSocketPort
        '
        Me.lbSocketPort.AutoSize = True
        Me.lbSocketPort.Location = New System.Drawing.Point(18, 21)
        Me.lbSocketPort.Name = "lbSocketPort"
        Me.lbSocketPort.Size = New System.Drawing.Size(29, 13)
        Me.lbSocketPort.TabIndex = 0
        Me.lbSocketPort.Text = "Port:"
        '
        'tfSocketPort
        '
        Me.tfSocketPort.Location = New System.Drawing.Point(53, 18)
        Me.tfSocketPort.Mask = "9999"
        Me.tfSocketPort.Name = "tfSocketPort"
        Me.tfSocketPort.Size = New System.Drawing.Size(62, 20)
        Me.tfSocketPort.TabIndex = 4
        Me.tfSocketPort.TextAlign = System.Windows.Forms.HorizontalAlignment.Right
        '
        'btCancel
        '
        Me.btCancel.Location = New System.Drawing.Point(299, 201)
        Me.btCancel.Name = "btCancel"
        Me.btCancel.Size = New System.Drawing.Size(75, 23)
        Me.btCancel.TabIndex = 10
        Me.btCancel.Text = "Cancel"
        Me.btCancel.UseVisualStyleBackColor = True
        '
        'btOk
        '
        Me.btOk.Location = New System.Drawing.Point(218, 201)
        Me.btOk.Name = "btOk"
        Me.btOk.Size = New System.Drawing.Size(75, 23)
        Me.btOk.TabIndex = 9
        Me.btOk.Text = "OK"
        Me.btOk.UseVisualStyleBackColor = True
        '
        'Settings
        '
        Me.AcceptButton = Me.btOk
        Me.AutoScaleDimensions = New System.Drawing.SizeF(6.0!, 13.0!)
        Me.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font
        Me.ClientSize = New System.Drawing.Size(386, 235)
        Me.Controls.Add(Me.btCancel)
        Me.Controls.Add(Me.btOk)
        Me.Controls.Add(Me.tabs)
        Me.Name = "Settings"
        Me.Text = "Settings"
        Me.tabs.ResumeLayout(False)
        Me.TabPage1.ResumeLayout(False)
        Me.TabPage1.PerformLayout()
        Me.TabPage2.ResumeLayout(False)
        Me.TabPage2.PerformLayout()
        Me.TabPage3.ResumeLayout(False)
        Me.TabPage3.PerformLayout()
        Me.ResumeLayout(False)

    End Sub
    Friend WithEvents tabs As System.Windows.Forms.TabControl
    Friend WithEvents TabPage1 As System.Windows.Forms.TabPage
    Friend WithEvents lbCategories As System.Windows.Forms.Label
    Friend WithEvents cbCategories As System.Windows.Forms.ComboBox
    Friend WithEvents cbTaskNote As System.Windows.Forms.CheckBox
    Friend WithEvents cbTaskDueDate As System.Windows.Forms.CheckBox
    Friend WithEvents cbTaskStartDate As System.Windows.Forms.CheckBox
    Friend WithEvents cbTaskTitle As System.Windows.Forms.CheckBox
    Friend WithEvents TabPage2 As System.Windows.Forms.TabPage
    Friend WithEvents cbNoteNote As System.Windows.Forms.CheckBox
    Friend WithEvents cbNoteFolder As System.Windows.Forms.CheckBox
    Friend WithEvents cbNoteTitle As System.Windows.Forms.CheckBox
    Friend WithEvents TabPage3 As System.Windows.Forms.TabPage
    Friend WithEvents lbDoNotChange As System.Windows.Forms.Label
    Friend WithEvents lbSocketPort As System.Windows.Forms.Label
    Friend WithEvents tfSocketPort As System.Windows.Forms.MaskedTextBox
    Friend WithEvents btCancel As System.Windows.Forms.Button
    Friend WithEvents btOk As System.Windows.Forms.Button
End Class
