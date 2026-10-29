@echo off
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
del sources.txt
echo Main-Class: clustering.Main> manifest.txt
jar cfm ClusteringSystem.jar manifest.txt -C out .
del manifest.txt
echo Listo: ClusteringSystem.jar
