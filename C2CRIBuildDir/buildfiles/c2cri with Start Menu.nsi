;c2cri.nsi
;
; This script is perhaps one of the simplest NSIs you can make. All of the
; optional settings are left to their default settings. The installer simply 
; prompts the user asking them where to install, and drops a copy of example1.nsi
; there. 
;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------
;--------------------------------

; The name of the installer
Name "C2C RI"

; The file to write
OutFile "c2cri_Installer.exe"

; The default installation directory
InstallDir C:\C2CRI

;Get installation folder from registry if available
InstallDirRegKey HKCU "Software\C2C RI" ""

; Request application privileges for Windows Vista
RequestExecutionLevel user

XPStyle               on

;--------------------------------
;Variables

  Var StartMenuFolder


;--------------------------------
;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
; Pages

  !insertmacro MUI_PAGE_WELCOME
; !insertmacro MUI_PAGE_LICENSE "${NSISDIR}\Docs\Modern UI\License.txt"
;  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY

  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU"
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\C2C RI"
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"

  !insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder



  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH

  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH



;Page directory
;;Page instfiles

;UninstPage uninstConfirm
;UninstPage instfiles

;--------------------------------
;--------------------------------
;Languages

  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
; The stuff to install
Section "" ;No components page, name is not important

  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; Put file there
;  File /r    /x   CVS  c:\tmp\RIDistribution\*.*
  File /r    /x   CVS  $%PACKAGEDIR%\*.*

  ;Store installation folder
  WriteRegStr HKCU "Software\C2C RI" "" $INSTDIR

;  File c2cri.nsi
  CreateDirectory $INSTDIR
  WriteUninstaller "$INSTDIR\Uninst.exe"

   !insertmacro MUI_STARTMENU_WRITE_BEGIN Application

    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\C2C RI.lnk" "$INSTDIR\c2cri.exe"
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk" "$INSTDIR\Uninst.exe"

  !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd ; end the section

;--------------------------------
; Uninstaller
Var          Info                ;Declare

Section "Uninstall"

     StrCpy $Info 'C2C RI Application uninstalled successfully.'
     Delete "$INSTDIR\c2cri.nsi"
     Delete "$INSTDIR\*.*"
     Delete "$INSTDIR\Uninst.exe"
     RmDir /r $INSTDIR

  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder

  Delete "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\C2C RI.lnk"
  RMDir "$SMPROGRAMS\$StartMenuFolder"

  DeleteRegKey /ifempty HKCU "Software\Modern UI Test"


SectionEnd

Function un.OnUninstSuccess

     HideWindow
     MessageBox MB_OK "$Info"
     
FunctionEnd

Function .onInit
        # the plugins dir is automatically deleted when the installer exits
        InitPluginsDir
        File /oname=$PLUGINSDIR\splash.bmp "${NSISDIR}\Contrib\Graphics\Header\nsis.bmp"
        #optional
        #File /oname=$PLUGINSDIR\splash.wav "C:\myprog\sound.wav"

 ;       MessageBox MB_OK "Fading"

        advsplash::show 1000 600 400 -1 $PLUGINSDIR\splash

        Pop $0          ; $0 has '1' if the user closed the splash screen early,
                        ; '0' if everything closed normally, and '-1' if some error occurred.


        Delete $PLUGINSDIR\splash.bmp
FunctionEnd