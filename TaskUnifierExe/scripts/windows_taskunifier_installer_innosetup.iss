[Setup]
AppName=TaskUnifier
AppVersion={#TASKUNIFIER_VERSION}
DefaultDirName={pf}\TaskUnifier
DefaultGroupName=TaskUnifier
UninstallDisplayIcon={app}\resources\icon.ico
Compression=lzma2
SolidCompression=yes
OutputDir=..\temp
ArchitecturesInstallIn64BitMode=x64
LicenseFile=..\temp\TaskUnifier\license.txt

AppPublisher=TaskUnifier
AppPublisherURL=http://www.taskunifier.com

ChangesAssociations=yes 

[Files]
Source: "..\temp\TaskUnifier\*"; DestDir: "{app}"; Flags: recursesubdirs
Source: "..\temp\TaskUnifier\readme.txt"; DestDir: "{app}"; Flags: isreadme

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Icons]
Name: "{group}\TaskUnifier"; Filename: "{app}\TaskUnifier.exe"    
Name: "{commondesktop}\TaskUnifier"; Filename: "{app}\TaskUnifier.exe"; Tasks: desktopicon

[Run]
Filename: "{app}\TaskUnifier"; Description: "{cm:LaunchProgram,TaskUnifier}"; Flags: nowait postinstall skipifsilent

[Registry]
Root: HKCR; Subkey: ".tue"; ValueType: string; ValueName: ""; ValueData: "TaskUnifierExchangeFile"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "TaskUnifierExchangeFile"; ValueType: string; ValueName: ""; ValueData: "TaskUnifier Exchange File"; Flags: uninsdeletekey
Root: HKCR; Subkey: "TaskUnifierExchangeFile\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\TaskUnifier.exe,0"
Root: HKCR; Subkey: "TaskUnifierExchangeFile\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\TaskUnifier.exe"" ""%1"""
