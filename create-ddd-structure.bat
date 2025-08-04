@echo off
set basePath=src\main\java\de\cloudypanda\huntcraft

REM Domain Layer
mkdir %basePath%\domain\core\model
mkdir %basePath%\domain\core\service
mkdir %basePath%\domain\player\model
mkdir %basePath%\domain\player\service
mkdir %basePath%\domain\deathtimer\model
mkdir %basePath%\domain\deathtimer\service
mkdir %basePath%\domain\deathtimer\repository
mkdir %basePath%\domain\notification\model
mkdir %basePath%\domain\notification\service
mkdir %basePath%\domain\notification\port

REM Application Layer
mkdir %basePath%\application\services
mkdir %basePath%\application\event

REM Infrastructure Layer
mkdir %basePath%\infrastructure\config\model
mkdir %basePath%\infrastructure\config\repository
mkdir %basePath%\infrastructure\persistence\file
mkdir %basePath%\infrastructure\integration\discord

echo Directory structure created successfully.