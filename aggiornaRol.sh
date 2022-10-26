cd /home/gtorre/Temp/

#nome = $1;

rm -Rf rischi.ORI
#mv almalaurea pm

#if ["$nome" == "rol"]; then
	#echo "Entro in webapps";
	#cd /opt/tomcat/apache-tomcat-10.0.22/webapps/ROOT/rischi/;
	echo "Faccio una copia di sicurezza dell'applicazione corrente";
	sudo cp -Rf /var/lib/tomcat9/webapps/rischi  /var/lib/tomcat9/webapps/rischi.ORI;
	#echo "Elimino l'applicazione corrente"
	#sudo rm -Rf  /var/lib/tomcat8/webapps/pm
	echo "Sposto risorse di interesse dall'applicazione proveniente dall'ambiente di sviluppo alla dir di deploy"
	sudo rm -Rf /var/lib/tomcat9/webapps/rischi/web
	sudo cp -Rf /home/gtorre/Temp/rischi/web  /var/lib/tomcat9/webapps/rischi/
	echo "Risorse statiche copiate"
	sudo rm -Rf /var/lib/tomcat9/webapps/rischi/jsp
	sudo cp -Rf /home/gtorre/Temp/rischi/jsp  /var/lib/tomcat9/webapps/rischi/
	echo "Pagine dinamiche copiate"
	sudo rm -Rf /var/lib/tomcat9/webapps/rischi/WEB-INF/classes
	sudo cp -Rf /home/gtorre/Temp/rischi/WEB-INF/classes  /var/lib/tomcat9/webapps/rischi/WEB-INF/
	sudo cp -Rf /home/gtorre/Temp/rischi/WEB-INF/lib  	/var/lib/tomcat9/webapps/rischi/WEB-INF/
	sudo cp     /home/gtorre/Temp/rischi/WEB-INF/web.xml  /var/lib/tomcat9/webapps/rischi/WEB-INF/
	echo "Classi Java copiate"
	sudo rm -Rf /var/lib/tomcat9/webapps/ROOT/javadoc
	sudo cp -Rf /home/gtorre/Temp/javadoc  /var/lib/tomcat9/webapps/ROOT/
	echo "Javadoc aggiornato"
	mv /home/gtorre/Temp/rischi  /home/gtorre/Temp/rischi.ORI
	echo "Ultima applicazione compilata archiviata"
	rm -Rf /home/gtorre/Temp/javadoc
	rm -Rf /home/gtorre/Temp/rischi
	echo "Directory temporanee eliminate"
	#echo "Sovrascrivo il file di configurazione, sostituendo quello copiato"
	#sudo cp -f  /var/lib/tomcat8/webapps/pm.ORI/WEB-INF/web.xml  /var/lib/tomcat8/webapps/pm/WEB-INF/web.xml
	#echo "Richiamo lo script che fa il backup dei log"
	#cd ~/Script 
	# ./LogBackup
	echo "Riavvio il container JSP"
	sudo service tomcat9 restart
#fi
