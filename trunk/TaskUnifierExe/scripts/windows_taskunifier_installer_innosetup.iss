; -- TaskUnifier.iss --

[Setup]
AppName=TaskUnifier
AppVersion=1.5
DefaultDirName={pf}\TaskUnifier
DefaultGroupName=TaskUnifier
UninstallDisplayIcon={app}\resources\icon.ico
Compression=lzma2
SolidCompression=yes
OutputDir=..\temp
ArchitecturesInstallIn64BitMode=x64
LicenseFile=..\temp\license.txt

AppPublisher=TaskUnifier
AppPublisherURL=http://www.taskunifier.com

[Files]
Source: "..\temp\*"; DestDir: "{app}"
Source: "..\temp\readme.txt"; DestDir: "{app}"; Flags: isreadme

[Icons]
Name: "{group}\TaskUnifier"; Filename: "{app}\resources\icon.ico"
