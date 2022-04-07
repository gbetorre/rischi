REM ==================================
REM per compilare il singolo file:
REM C:\Users\trrgnr59>C:\Programmi\Java\jdk1.8.0_212\bin\javac "D:\Users\trrgnr59\WebSrc\almalaurea\DBWrapper.java" -d "C:/Programs/apache-tomcat-8.5.31/webapps/almalaurea/WEB-INF/classes/"
REM ==================================
REM per compilare tutti i file java nella dir:
REM C:\Programmi\Java\jdk1.8.0_212\bin\javac D:\Users\trrgnr59\WebSrc\almalaurea\pm\src\it\alma\controller\*.java -d "C:/Programs/apache-tomcat-8.5.31/webapps/almalaurea/WEB-INF/classes/" -classpath "C:\Programs\apache-tomcat-8.5.31\lib\servlet-api.jar;C:\Programs\resin-pro-3.1.13\ext-lib\commons-lang.jar"
REM C:\Programmi\Java\jdk1.8.0_212\bin\javac D:\Users\trrgnr59\WebSrc\almalaurea\pm\src\it\alma\model\*.java -d "C:/Programs/apache-tomcat-8.5.31/webapps/almalaurea/WEB-INF/classes/" -classpath "C:\Programs\apache-tomcat-8.5.31\lib\servlet-api.jar;C:\Programs\resin-pro-3.1.13\ext-lib\commons-lang.jar"
REM ==================================
REM SE SI VOLESSERO SPOSTARE ANZICHE' COPIARE: 
REM DEL    D:\Users\trrgnr59\WebSrc\almalaurea\pm\htmls\*.*  /S
REM ==================================
REM ************** COMPILAZIONE INIZIATA. ************** 
REM ELIMINA TUTTE LE CLASSI COMPILATE IN PRECEDENZA
DEL    C:\Programs\apache-tomcat-8.5.31\webapps\processi\WEB-INF\classes\it\pcd\*.class  /S
REM NON SERVE EFFETTUARE ALTRE CANCELLAZIONI PERCHE' LA RIGA SOPRASTANTE CANCELLA TUTTO IN CASCATA

REM RICOPIA LE CLASSI DI ROOT
XCOPY D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\classes\it\pcd\*.class  C:\Programs\apache-tomcat-8.5.31\webapps\processi\WEB-INF\classes\it\pcd\  /S /Y

REM COMPILA TUTTE LE CLASSI JAVA DI PROCESSI
ECHO
C:\Programs\Java\jdk1.8.0_271\bin\javac D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\src\it\pcd\exception\*.java -g -d "C:/Programs/apache-tomcat-8.5.31/webapps/processi/WEB-INF/classes/" -classpath "C:\Programs\apache-tomcat-8.5.31\webapps\processi\WEB-INF\classes\;C:\Programs\apache-tomcat-8.5.31\lib\servlet-api.jar;C:\Programs\resin-pro-3.1.13\ext-lib\commons-lang.jar;C:\Programs\resin-pro-3.1.13\ext-lib\commons-beanutils-core.jar"
C:\Programs\Java\jdk1.8.0_271\bin\javac D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\src\it\pcd\bean\*.java -g -d "C:/Programs/apache-tomcat-8.5.31/webapps/processi/WEB-INF/classes/" -classpath "C:\Programs\apache-tomcat-8.5.31\webapps\processi\WEB-INF\classes\;C:\Programs\apache-tomcat-8.5.31\lib\servlet-api.jar;C:\Programs\resin-pro-3.1.13\ext-lib\commons-lang.jar;C:\Programs\resin-pro-3.1.13\ext-lib\commons-beanutils-core.jar" -Xlint:unchecked
C:\Programs\Java\jdk1.8.0_271\bin\javac D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\src\it\pcd\*.java -g -d "C:/Programs/apache-tomcat-8.5.31/webapps/processi/WEB-INF/classes/" -classpath "C:\Programs\apache-tomcat-8.5.31\webapps\processi\WEB-INF\classes\;C:\Programs\apache-tomcat-8.5.31\lib\servlet-api.jar;C:\Programs\resin-pro-3.1.13\ext-lib\commons-lang.jar;C:\Programs\resin-pro-3.1.13\ext-lib\commons-beanutils-core.jar;C:\Programs\apache-tomcat-8.5.31\ext-lib\cos.jar" -Xlint:unchecked
C:\Programs\Java\jdk1.8.0_271\bin\javac -g "D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\src\it\pcd\command\Command.java" -d "C:/Programs/apache-tomcat-8.5.31/webapps/processi/WEB-INF/classes/" -cp "C:\Programs\apache-tomcat-8.5.31\webapps\processi\WEB-INF\classes\;C:\Programs\apache-tomcat-8.5.31\lib\servlet-api.jar;C:\Programs\resin-pro-3.1.13\ext-lib\commons-lang.jar;C:\Programs\resin-pro-3.1.13\ext-lib\commons-beanutils-core.jar;C:\Programs\apache-tomcat-8.5.31\ext-lib\cos.jar" -J-Xmx512m 
REM C:\Programs\Java\jdk1.8.0_271\bin\javac -g "D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\src\it\pcd\command\ProcessCommand.java" -d "C:/Programs/apache-tomcat-8.5.31/webapps/processi/WEB-INF/classes/" -cp "C:\Programs\apache-tomcat-8.5.31\webapps\processi\WEB-INF\classes\;C:\Programs\apache-tomcat-8.5.31\lib\servlet-api.jar;C:\Programs\resin-pro-3.1.13\ext-lib\commons-lang.jar;C:\Programs\resin-pro-3.1.13\ext-lib\commons-beanutils-core.jar;C:\Programs\apache-tomcat-8.5.31\ext-lib\cos.jar" -J-Xmx512m -Xlint:unchecked

REM COPIA TUTTE LE RISORSE STATICHE DI PROCESSI
XCOPY  D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\web\style\*.*  C:\Programs\apache-tomcat-8.5.31\webapps\processi\web\style\  /S /Y
XCOPY  D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\web\js\*.*  C:\Programs\apache-tomcat-8.5.31\webapps\processi\web\js\  /S /Y
XCOPY  D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\web\html\*.*  C:\Programs\apache-tomcat-8.5.31\webapps\processi\web\html\  /S /Y
XCOPY  D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\web\img\*.*  C:\Programs\apache-tomcat-8.5.31\webapps\processi\web\img\  /S /Y
XCOPY  D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\web\jsp\*.*  C:\Programs\apache-tomcat-8.5.31\webapps\processi\jsp\  /S /Y
REM XCOPY  D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\web\json\*.*  C:\Programs\apache-tomcat-8.5.31\webapps\processi\web\json\  /S /Y
XCOPY  D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\WEB-INF\web.xml  C:\Programs\apache-tomcat-8.5.31\webapps\processi\WEB-INF\  /S /Y

REM FA UN MIRRORING DELL'APPLICAZIONE (JUST IN CASE)
XCOPY D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\classes\  D:\Users\trrgnr59\WebSrc\processi\classes\ /S /Y
XCOPY D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\src\    D:\Users\trrgnr59\WebSrc\processi\src\ /S /Y
XCOPY D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\web\  D:\Users\trrgnr59\WebSrc\processi\web\ /S /Y
XCOPY D:\Users\trrgnr59\WebSrc\gestproc\webapp\processi\WEB-INF\  D:\Users\trrgnr59\WebSrc\processi\WEB-INF /S /Y

REM ************** COMPILAZIONE DI Processi On Line TERMINATA. CONSULTARE LA CONSOLE PER ERRORI/WARNING. ************** 
TIME
