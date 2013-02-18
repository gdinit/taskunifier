set isx="C:\Program Files (x86)\Inno Setup 5\ISCC.exe"
set iwz="scripts\windows_taskunifier_installer_innosetup.iss"

%isx% /Q %iwz% /DTASKUNIFIER_VERSION=$1
