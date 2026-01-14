@echo off
REM ============================================================================
REM Script de conversion Excel vers JSON - Version Windows
REM Usage: excel-to-json.bat <fichier_excel.xlsx> <fichier_sortie.json>
REM ============================================================================

setlocal EnableDelayedExpansion

REM Configuration
set JAR_NAME=excel-to-json-converter.jar
set JAR_PATH=target\%JAR_NAME%

REM Codes couleur Windows (si disponible)
set "ESC="
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

REM ============================================================================
REM Fonction d'affichage des messages
REM ============================================================================
goto :main

:print_error
    echo %RED%ERREUR: %~1%NC%
    exit /b 1

:print_success
    echo %GREEN%%~1%NC%
    exit /b 0

:print_info
    echo %BLUE%%~1%NC%
    exit /b 0

:print_warning
    echo %YELLOW%AVERTISSEMENT: %~1%NC%
    exit /b 0

REM ============================================================================
REM Fonction d'affichage de l'usage
REM ============================================================================
:print_usage
    echo.
    echo ================================================================
    echo          SCRIPT DE CONVERSION EXCEL VERS JSON
    echo ================================================================
    echo.
    echo Usage:
    echo     %~nx0 ^<fichier_excel.xlsx^> ^<fichier_sortie.json^>
    echo.
    echo Arguments:
    echo     fichier_excel.xlsx    Chemin vers le fichier Excel a convertir
    echo     fichier_sortie.json   Chemin du fichier JSON de sortie
    echo.
    echo Exemples:
    echo     %~nx0 data\input.xlsx output\result.json
    echo     %~nx0 C:\Users\user\rapport.xlsx C:\temp\rapport.json
    echo.
    echo Options:
    echo     /h, /help, /?        Afficher cette aide
    echo     /v, /version         Afficher la version
    echo     /b, /build           Compiler l'application avant execution
    echo.
    exit /b 0

REM ============================================================================
REM Fonction de verification de Java
REM ============================================================================
:check_java
    where java >nul 2>&1
    if errorlevel 1 (
        call :print_error "Java n'est pas installe ou n'est pas dans le PATH"
        echo     Installez Java 17 ou superieur depuis: https://adoptium.net/
        exit /b 1
    )

    REM Recuperer la version de Java
    for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
        set JAVA_VERSION_STRING=%%g
    )
    set JAVA_VERSION_STRING=!JAVA_VERSION_STRING:"=!

    REM Extraire le numero de version majeure
    for /f "tokens=1 delims=." %%a in ("!JAVA_VERSION_STRING!") do set JAVA_MAJOR=%%a

    if !JAVA_MAJOR! LSS 17 (
        call :print_error "Java 17 ou superieur est requis (version detectee: !JAVA_MAJOR!)"
        exit /b 1
    )

    call :print_info "Java version: !JAVA_VERSION_STRING!"
    exit /b 0

REM ============================================================================
REM Fonction de verification du JAR
REM ============================================================================
:check_jar
    if not exist "%JAR_PATH%" (
        call :print_error "Le fichier JAR n'existe pas: %JAR_PATH%"
        echo     Executez: mvn clean package
        echo     Ou utilisez l'option /build pour compiler automatiquement
        exit /b 1
    )
    exit /b 0

REM ============================================================================
REM Fonction de compilation
REM ============================================================================
:build_project
    call :print_info "Compilation du projet..."

    where mvn >nul 2>&1
    if errorlevel 1 (
        call :print_error "Maven n'est pas installe ou n'est pas dans le PATH"
        echo     Installez Maven depuis: https://maven.apache.org/download.cgi
        exit /b 1
    )

    call mvn clean package -DskipTests

    if errorlevel 1 (
        call :print_error "La compilation a echoue"
        exit /b 1
    )

    call :print_success "Compilation reussie"
    exit /b 0

REM ============================================================================
REM Fonction de validation du fichier d'entree
REM ============================================================================
:validate_input_file
    set INPUT_FILE=%~1

    if "%INPUT_FILE%"=="" (
        call :print_error "Le chemin du fichier d'entree est vide"
        exit /b 1
    )

    if not exist "%INPUT_FILE%" (
        call :print_error "Le fichier d'entree n'existe pas: %INPUT_FILE%"
        exit /b 1
    )

    REM Verification de l'extension
    set "FILE_EXT=%INPUT_FILE:~-5%"
    if /i not "!FILE_EXT!"==".xlsx" (
        call :print_error "Le fichier doit avoir l'extension .xlsx"
        exit /b 1
    )

    REM Affichage de la taille du fichier
    for %%A in ("%INPUT_FILE%") do set FILE_SIZE=%%~zA
    set /a FILE_SIZE_KB=!FILE_SIZE! / 1024
    call :print_info "Taille du fichier: !FILE_SIZE_KB! KB"

    exit /b 0

REM ============================================================================
REM Fonction de validation du chemin de sortie
REM ============================================================================
:validate_output_path
    set OUTPUT_FILE=%~1

    if "%OUTPUT_FILE%"=="" (
        call :print_error "Le chemin du fichier de sortie est vide"
        exit /b 1
    )

    REM Verification de l'extension
    set "FILE_EXT=%OUTPUT_FILE:~-5%"
    if /i not "!FILE_EXT!"==".json" (
        call :print_error "Le fichier de sortie doit avoir l'extension .json"
        exit /b 1
    )

    REM Verification/creation du repertoire de sortie
    for %%F in ("%OUTPUT_FILE%") do set OUTPUT_DIR=%%~dpF

    if not exist "!OUTPUT_DIR!" (
        call :print_warning "Le repertoire de sortie n'existe pas: !OUTPUT_DIR!"
        call :print_info "Creation du repertoire..."
        mkdir "!OUTPUT_DIR!" 2>nul

        if errorlevel 1 (
            call :print_error "Impossible de creer le repertoire: !OUTPUT_DIR!"
            exit /b 1
        )
    )

    exit /b 0

REM ============================================================================
REM Fonction principale
REM ============================================================================
:main

REM Gestion des options
if "%~1"=="/h" goto :show_help
if "%~1"=="/help" goto :show_help
if "%~1"=="/?" goto :show_help
if "%~1"=="/v" goto :show_version
if "%~1"=="/version" goto :show_version
if "%~1"=="/b" goto :build_first
if "%~1"=="/build" goto :build_first

goto :process_args

:show_help
    call :print_usage
    exit /b 0

:show_version
    echo Version 1.0.0
    exit /b 0

:build_first
    call :build_project
    if errorlevel 1 exit /b 1
    shift
    goto :process_args

:process_args

REM Verification du nombre d'arguments
if "%~1"=="" (
    call :print_error "Nombre d'arguments incorrect"
    echo.
    call :print_usage
    exit /b 1
)

if "%~2"=="" (
    call :print_error "Nombre d'arguments incorrect"
    echo.
    call :print_usage
    exit /b 1
)

set INPUT_FILE=%~1
set OUTPUT_FILE=%~2

echo.
echo ================================================================
echo          CONVERTISSEUR EXCEL vers JSON - v1.0.0
echo ================================================================
echo.

REM Verifications prealables
call :print_info "Verification de l'environnement..."
call :check_java
if errorlevel 1 exit /b 1

call :check_jar
if errorlevel 1 exit /b 1

echo.
call :print_info "Validation des parametres..."
call :validate_input_file "%INPUT_FILE%"
if errorlevel 1 exit /b 1

call :validate_output_path "%OUTPUT_FILE%"
if errorlevel 1 exit /b 1

echo.
call :print_info "Fichier d'entree  : %INPUT_FILE%"
call :print_info "Fichier de sortie : %OUTPUT_FILE%"
echo.

REM Conversion des chemins en chemins absolus
for %%F in ("%INPUT_FILE%") do set INPUT_FILE_ABS=%%~fF
for %%F in ("%OUTPUT_FILE%") do set OUTPUT_FILE_ABS=%%~fF

REM Execution de la conversion
call :print_info "Lancement de la conversion..."
echo.

REM Mesure du temps de debut
set START_TIME=%TIME%

REM Execution de l'application Java
java -jar "%JAR_PATH%" "%INPUT_FILE_ABS%" "%OUTPUT_FILE_ABS%"
set EXIT_CODE=%ERRORLEVEL%

REM Mesure du temps de fin
set END_TIME=%TIME%

echo.

if %EXIT_CODE%==0 (
    REM Calcul de la duree (simplifie)
    call :print_success "Conversion terminee avec succes"

    if exist "%OUTPUT_FILE_ABS%" (
        for %%A in ("%OUTPUT_FILE_ABS%") do set OUTPUT_SIZE=%%~zA
        set /a OUTPUT_SIZE_KB=!OUTPUT_SIZE! / 1024
        call :print_info "Fichier cree: %OUTPUT_FILE_ABS% (!OUTPUT_SIZE_KB! KB)"
    )

    exit /b 0
) else (
    call :print_error "La conversion a echoue (code de sortie: %EXIT_CODE%)"
    exit /b %EXIT_CODE%
)

REM ============================================================================
REM Fin du script
REM ============================================================================
endlocal