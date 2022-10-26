#!/bin/bash
echo "copio lo script di deploy sul server di produzione"
scp /home/outer-root/git/rischi/aggiornaRol.sh gtorre@at.univr.it:/home/gtorre/Script/
echo "copio il javadoc in una directory temporanea del server di produzione"
scp -r /home/outer-root/git/rischi/javadoc/ gtorre@at.univr.it:/home/gtorre/Temp/
echo "copio l'applicazione compilata dall'ambiente di sviluppo in una directory temporanea del server di produzione"
scp -r /home/outer-root/Programs/apache-tomcat-8.5.60/webapps/rischi/ gtorre@at.univr.it:/home/gtorre/Temp/
echo "============================================================"
echo "Per effettuare il deploy lanciare: './Script/aggiornaRol.sh'"
echo "============================================================"
echo "Entro in ssh."
ssh -l gtorre at.univr.it


