[Setup]
AppName=TaskUnifier
AppVersion=3.2.0
DefaultDirName={pf}\TaskUnifier
DefaultGroupName=TaskUnifier
UninstallDisplayIcon={app}\resources\icon.ico
Compression=lzma2
SolidCompression=yes
OutputDir=..\temp
ArchitecturesInstallIn64BitMode=x64
LicenseFile=..\temp\TaskUnifier\licence.txt

AppPublisher=TaskUnifier
AppPublisherURL=http://www.taskunifier.com

ChangesAssociations=yes

[Files]
Source: "..\temp\TaskUnifier\*"; DestDir: "{app}"; Flags: recursesubdirs
Source: "..\temp\TaskUnifier\readme.txt"; DestDir: "{app}"; Flags: isreadme

[Icons]
Name: "{group}\TaskUnifier"; Filename: "{app}\resources\icon.ico"

[Registry]
Root: HKCR; Subkey: ".tue"; ValueType: string; ValueName: ""; ValueData: "TaskUnifierExchangeFile"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "TaskUnifierExchangeFile"; ValueType: string; ValueName: ""; ValueData: "TaskUnifier Exchange File"; Flags: uninsdeletekey
Root: HKCR; Subkey: "TaskUnifierExchangeFile\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\TaskUnifier.exe,0"
Root: HKCR; Subkey: "TaskUnifierExchangeFile\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\TaskUnifier.exe"" ""%1"""
