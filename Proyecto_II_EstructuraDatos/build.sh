#!/bin/sh
set -e
rm -rf out && mkdir out
javac -encoding UTF-8 -d out $(find src -name '*.java')
printf 'Main-Class: clustering.Main\n' > manifest.txt
jar cfm ClusteringSystem.jar manifest.txt -C out .
rm manifest.txt
echo "Listo: ClusteringSystem.jar"
