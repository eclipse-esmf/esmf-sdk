@echo off
setlocal

rem Get the name of the process that started the .exe (which started a cmd, which started the run.bat, which starts the powershell process)
rem The result should be either explorer.exe (when the .exe is started by double click) or cmd.exe (when run from a command shell)
rem or powershell.exe (when run from a powershell window)
for /f "usebackq tokens=*" %%i in (`powershell -NoProfile -Command "(Get-CimInstance Win32_Process -Filter ProcessId=$PID).ParentProcessId | %%{ (Get-CimInstance Win32_Process -Filter ProcessId=$_).ParentProcessId } | %%{ (Get-CimInstance Win32_Process -Filter ProcessId=$_).ParentProcessId } | %%{ (Get-CimInstance Win32_Process -Filter ProcessId=$_).ParentProcessId } | %%{ (Get-CimInstance Win32_Process -Filter ProcessId=$_).Name }"`) do set HostProcessName=%%i

rem Get the working directory of the .exe
for /f "usebackq tokens=*" %%a in (`powershell -NoProfile -Command "(Get-CimInstance Win32_Process -Filter ProcessId=$PID).ParentProcessId | %%{ (Get-CimInstance Win32_Process -Filter ProcessId=$_).ParentProcessId } | %%{ (Get-CimInstance Win32_Process -Filter ProcessId=$_).ParentProcessId } | %%{ (Get-Process -Id $_).Path } | Split-Path"`) do set ExeWorkingDir=%%a

if /i "%HostProcessName%"=="explorer.exe" (
  cd /d "%ExeWorkingDir%"
  for %%i in (%~dp0samm-cli-*.jar) do %~dp0jre\bin\java --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow -Dpolyglotimpl.DisableMultiReleaseCheck=true -jar "%%i"
  cmd /k
) else (
  for %%i in (%~dp0samm-cli-*.jar) do %~dp0jre\bin\java --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow -Dpolyglotimpl.DisableMultiReleaseCheck=true -jar "%%i" %*
)
