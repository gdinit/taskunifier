; -- TaskUnifier.iss --

[Setup]
AppName=TaskUnifier
AppVersion=1.5
DefaultDirName={pf}\TaskUnifier
DefaultGroupName=TaskUnifier
UninstallDisplayIcon={app}\TaskUnifier.exe
Compression=lzma2
SolidCompression=yes
OutputDir=userdocs:Inno Setup Examples Output

[Files]
Source: "MyProg.exe"; DestDir: "{app}"
Source: "MyProg.chm"; DestDir: "{app}"
Source: "Readme.txt"; DestDir: "{app}"; Flags: isreadme

[Icons]
Name: "{group}\TaskUnifier"; Filename: "{app}\resources\icon.ico"
