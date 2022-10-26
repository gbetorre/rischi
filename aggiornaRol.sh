#!/bin/bash
scp -r /home/outer-root/Programs/apache-tomcat-8.5.60/webapps/rischi/ gtorre@at.univr.it:/home/gtorre/Temp/
echo "Entro in ssh. Lanciare './Script/aggiornaRol.sh'"
ssh -l gtorre at.univr.it


