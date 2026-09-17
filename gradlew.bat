@echo off
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_HOME=%DIRNAME%
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
if not exist "%CLASSPATH%" (
    echo Gradle wrapper JAR not found. Please run the Gradle wrapper setup.
    exit /b 1
)

@rem This script is a Windows batch file for running Gradle with the wrapper.
java -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
