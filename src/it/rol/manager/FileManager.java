/*
 *   Rischi On Line (ROL-RMS), Applicazione web: 
 *   - per la gestione di sondaggi inerenti al rischio corruttivo 
 *     cui i processi organizzativi di una PA possono essere esposti, 
 *   - per la produzione di mappature e reportistica finalizzate 
 *     alla valutazione del rischio corruttivo nella pubblica amministrazione, 
 *   - per ottenere suggerimenti riguardo le misure di mitigazione 
 *     che possono calmierare specifici rischi 
 *   - e per effettuare il monitoraggio al fine di verificare quali misure
 *     proposte sono state effettivamente attuate dai soggetti interessati
 *     alla gestione dei processi a rischio e stabilire quantitativamente 
 *     in che grado questa attuazione di misure abbia effettivamente ridotto 
 *     i livelli di rischio.
 *
 *   Risk Mapping and Management Software (ROL-RMS),
 *   web application: 
 *   - to assess the amount and type of corruption risk to which each organizational process is exposed, 
 *   - to publish and manage, reports and information on risk
 *   - and to propose mitigation measures specifically aimed at reducing risk, 
 *   - also allowing monitoring to be carried out to see 
 *     which proposed mitigation measures were then actually implemented 
 *     and quantify how much that implementation of measures actually 
 *     reduced risk levels.
 *   
 *   Copyright (C) 2022-2025 Giovanroberto Torre
 *   all right reserved
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA<br>
 *
 *   Giovanroberto Torre <gianroberto.torre@gmail.com>
 *   Universita' degli Studi di Verona
 *   Via Dell'Artigliere, 8
 *   37129 Verona (Italy)
 */

package it.rol.manager;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.oreilly.servlet.ParameterParser;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.DBWrapper;
import it.rol.bean.PersonBean;
import it.rol.exception.CommandException;
import it.rol.exception.NotFoundException;
import it.rol.exception.WebStorageException;
import it.rol.util.Utils;


/**
 * <p>FileManager &egrave; la classe che si occupa della gestione dei files
 * caricati dall'utente, della generazione di link per il download, 
 * l'impostazione dei privilegi, secondo eventuali policy, 
 * dell'aggiornamento e di quant'altro occorra alle funzioni di gestione allegati 
 * della web-application <code>Rol</code>.</p>
 * 
 * @author <a href="mailto:giovanroberto.torre@univr.it">Giovanroberto Torre</a>
 */
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
                 maxFileSize=1024*1024*50,      // 50MB
                 maxRequestSize=1024*1024*50)   // 50MB

public class FileManager extends HttpServlet implements Constants {
    /**
     * Talking const for the preferred, standard separator
     * between entity names and attribute names.
     */
    public static final char STD_SPACER = '_';
    /**
     * La serializzazione necessita della dichiarazione 
     * di una costante di tipo long identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
     */
    private static final long serialVersionUID = 1L;
    /**
     *  The name of the present class (useful for talking error messages).
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
     /**
     * Logger della classe per scrivere i messaggi di errore.
     */
    private static Logger log = Logger.getLogger(FileManager.class.getName());
    /**
     * Name of the directory where uploaded files will be saved, relative to
     * the web application directory.
     */
    private static final String SAVE_DIR = "upload";
    /**
     * Suffix for the directory where uploaded files will be saved, relative to
     * the web application specific functionality.
     */
    private static final String STANDARD_DIR_SUFFIX = "_all";
    /**
     * <p>Nome e percorso della pagina di errore cui ridirigere in caso
     * di eccezioni rilevate.</p>
     */
    private static String errorJsp;
    /**
     * <p>Nome del template in cui vengono assemblati i vari 'pezzi'
     * che compongono l'output html finale.</p>
     */
    private static String templateJsp;
    /**
     * Attributo per il valore del parametro obbligatorio 'q'
     */
    private static String entToken;
    /**
     * Lista contenente tutti i token di interesse, ovvero ammesi a generare files
     */
    private LinkedList<String> entTokens = new LinkedList<>();
    /**
     * Lista contenente tutti i token di interesse, ovvero ammesi a generare files
     */
    private static LinkedList<String> forbiddenExt = null;
    /**
     *  Carica la lista con le estensioni vietate
     */
    public static final String[] FORBIDDEN_EXTENSIONS = {"apk", "AppImage",
                                                         "bash","bat",
                                                         "cmd","com","cpl",
                                                         "deb", "dll", "docm","dotm",
                                                         "ebuild", "exe",
                                                         "flatpack",
                                                         "gadget",
                                                         "hta","htm","html",
                                                         "inf","info",
                                                         "jar","js","jse",
                                                         "lnk",
                                                         "msc","msh","msh1","msh2","mshxml","msh1xml","msh2xml","msi","msp",
                                                         "nfo",
                                                         "pkg", "potm","ppam","ppsm","pptm","ps1","ps1xml","ps2","ps2xml","psc1","psc2","py",
                                                         "reg", "rpm",
                                                         "scf","scr","scf","sfx","sldm","snap", "sh",
                                                         "tmp",
                                                         "vb","vbe","vbs",
                                                         "ws","wsc","wsf","wsh",
                                                         "xlam","xlsm","xltm"};    
    
    
    /** 
     * <p>Inizializza, staticamente, alcune variabili globali.</p> 
     * 
     * @param config la configurazione usata dal servlet container per passare informazioni alla servlet <strong>durante l'inizializzazione</strong>
     * @throws ServletException una eccezione che puo' essere sollevata quando la servlet incontra difficolta'
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        /*
         * Inizializzazione da superclasse
         */
        super.init(config);
        /*
         * Nome della pagina di errore
         *
        errorJsp = getServletContext().getInitParameter("errorJsp");
        if (errorJsp == null)
            throw new ServletException(FOR_NAME + "\n\nManca il parametro di contesto 'errorJsp'!\n\n");
        /*
         * Nome del template da invocare per l'assemblaggio dei vari componenti 
         * dell'output - nel contesto della servlet di autenticazione 
         * serve piu' che altro per redirigere sulla maschera di login
         * in caso di invalidamento della sessione 
         *
        templateJsp = getServletContext().getInitParameter("templateJsp");
        if (templateJsp == null) {
            throw new ServletException(FOR_NAME + "\n\nManca il parametro di contesto 'templateJsp'!\n\n");
        }
        /*
         *  Caricamento degli 'ent' ammessi alla generazione di upload di file
         */
        //entTokens.add(Query.PROJECT_PART);
        //entTokens.add(Query.MONITOR_PART);
        entTokens.add(COMMAND_INDICATOR);
        /*
         * Lista contenente tutti i token di interesse, ovvero ammesi a generare files
         */
        // Arrays.asList(String[]) returns a List<String>!
        forbiddenExt = new LinkedList<>(Arrays.asList(FORBIDDEN_EXTENSIONS));
    }
    
    
    /** 
     * <p>Gestisce le richieste del client effettuate con il metodo GET.</p>
     * 
     * @param req la HttpServletRequest contenente la richiesta del client
     * @param res la HttpServletResponse contenente la risposta del server
     * @throws ServletException eccezione che viene sollevata se si verifica un problema nell'inoltro (forward) della richiesta/risposta
     * @throws IOException      eccezione che viene sollevata se si verifica un problema nell'inoltro (forward) della richiesta/risposta
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, 
                         HttpServletResponse res)
                  throws ServletException, IOException {
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            HttpSession session = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            PersonBean user = (PersonBean) session.getAttribute("usr");
            String msg = "Ha effettuato il logout l\'utente: " + 
                         user.getNome() + BLANK_SPACE + user.getCognome() + 
                         " in data"  + BLANK_SPACE + Utils.format(Utils.getCurrentDate()) +
                         " alle ore" + BLANK_SPACE + Utils.getCurrentTime() +
                         ".\n";
            log.info(msg);
            session.invalidate();
            final RequestDispatcher rd = getServletContext().getRequestDispatcher(templateJsp);
            rd.forward(req, res);
        } catch (IllegalStateException ise) {
            String msg = "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n" + ise.getMessage();
            log.severe(msg);
            throw new ServletException(msg, ise);
        } catch (Exception e) {
            String msg = "Problemi nel metodo doGet.\n" + e.getMessage();
            log.severe(msg);
            throw new ServletException(msg, e);
        }
    }
    
    
    /**
     * <p>Handles file upload.</p>
     * <p>Utilizza un approccio di programmazione difensiva per garantire
     * la correttezza dell'input.</p>
     *  
     * @param req la HttpServletRequest contenente la richiesta del client
     * @param res la HttpServletResponse contenente la risposta del server
     * @throws ServletException eccezione che viene sollevata se si verifica un problema nell'inoltro (forward) della richiesta/risposta
     * @throws IOException      eccezione che viene sollevata se si verifica un problema nell'inoltro (forward) della richiesta/risposta
     */
    @Override
    protected void doPost(HttpServletRequest req, 
                          HttpServletResponse res) 
                   throws ServletException, IOException {
        HttpSession ses = null;                             // Sessione
        ParameterParser parser = new ParameterParser(req);  // Crea il parser per i parametri
        PersonBean user = null;                             // Utente loggato
        /* ******************************************************************** *
         *              Variabili per gestione upload da pagina                 *
         * ******************************************************************** */
        // Recupera o inizializza 'q' 
        entToken = ConfigManager.getEntToken();
        // Recupera o inizializza 'id progetto' (upload da Status)
        //int idPrj = parser.getIntParameter("prj-id", DEFAULT_ID);
        // Recupera o inizializza 'id dipartimento'     (upload da Monitor)
        int idDip = parser.getIntParameter("dip-id", DEFAULT_ID);
        // Recupera o inizializza 'p'     (upload da Monitor Ateneo)
        String part = parser.getStringParameter("p", VOID_STRING);
        /* ******************************************************************** *
         *           I - Control :: the user Session MUST BE valid              *
         *              to avoid the "garden gate situation"                    *
         * ******************************************************************** */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            if (ses == null) {
                throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n");
            }
            user = (PersonBean) ses.getAttribute("usr");
            if (user == null) {
                log.severe("Utente punta a null!\n");
                throw new ServletException("Attenzione: controllare di essere autenticati nell\'applicazione!\n");
            }
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            log.severe(msg);
            throw new ServletException(msg + ise.getMessage(), ise);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null, probabilmente nel tentativo di recuperare l\'utente.\n";
            log.severe(msg);
            throw new ServletException("Attenzione: controllare di essere autenticati nell\'applicazione!\n" + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            log.severe(msg);
            throw new ServletException(msg + e.getMessage(), e);
        }
        /* ******************************************************************** *
         *  II - Control :: user MUST HAVE writable project(s) or writable dept *
         * ******************************************************************** *
        // Recupera i progetti su cui l'utente ha diritti di scrittura
        @SuppressWarnings("unchecked")  // I'm confident about the types...
        Vector<ProjectBean> writablePrj = (Vector<ProjectBean>) ses.getAttribute("writableProjects"); 
        // Recupera i dipartimenti in cui l'utente ha un ruolo superiore a TL
        @SuppressWarnings("unchecked")
        Vector<DepartmentBean> writableDepts = (Vector<DepartmentBean>) ses.getAttribute("writableDeparments");
        // Se non ci sono progetti scrivibili ed è stata inviata una form post multipart c'è qualcosa che non va!!!
        if (writablePrj == null) {
            ses.invalidate();
            String msg = FOR_NAME + "L\'utente ha tentato di caricare un file pero\' non sono stati trovati progetti scrivibili: problema!.\n";
            log.severe(msg);
            throw new ServletException("Attenzione: controllare di essere autenticati nell\'applicazione!\n");
        }
        // Se è stato passato un id dipart ma l'utente non ha dipartimenti scrivibili, c'è qualcosa che non va!!
        if (idDip > DEFAULT_ID && writableDepts == null) {
            ses.invalidate();
            String msg = FOR_NAME + "L\'utente ha tentato di caricare un file pero\' non sono stati trovati dipartimenti monitorabili: problema!.\n";
            log.severe(msg);
            throw new ServletException("Attenzione: controllare di essere autenticati nell\'applicazione!\n");
        }
        /* ******************************************************************** *
         *         III - Control :: token MUST BE among allowed tokens          *
         * ******************************************************************** */
        // Controlla che il token corrente sia abilitato all'upload
        if (!entTokens.contains(entToken)) {
            String msg = FOR_NAME + "Hai chiamato il FileManager ma non sei in un token ammesso: allora: che vuoi?\n";
            log.severe(msg + "Non sei piu\' rock? Non sei piu\' metal?\n");
            throw new ServletException(msg);
        }
        /* ******************************************************************** *
         *       IV - Control :: there's an id and MUST BE a valid id          *
         * ******************************************************************** */
       /* if (entToken.equalsIgnoreCase(Query.PROJECT_PART) && idPrj > DEFAULT_ID) {
        	// Gestisce il caricamento degli allegati allo status di progetto
            uploadStatus(req, res, parser, user, writablePrj);
        } else if (entToken.equalsIgnoreCase(Query.MONITOR_PART) && idDip > DEFAULT_ID) {
        	// Gestisce il caricamento degli allegati al monitoraggio MIUR
            uploadMonitor(req, res, parser, user, writableDepts);
        } else if (entToken.equalsIgnoreCase(Query.MONITOR_PART) && part.equalsIgnoreCase(Query.MONITOR_ATE)) {
        	// Gestisce il caricamento degli allegati al monitoraggio di ateneo
            uploadMonitorAte(req, res, parser, user);
        } else*/
        if (entToken.equalsIgnoreCase(COMMAND_INDICATOR) && part.equalsIgnoreCase(PART_SELECT_MEASUREMENT)) {
        	// Gestisce il caricamento degli allegati a corredo della misurazione 
            uploadGathering(req, res, parser, user);
        } else {    
        	// Se non riesce a recuperare nessuna delle precedenti, qualcosa non va, ed esce
            String msg = "Problemi nel recupero di un identificativo necessario.\n";
            log.severe(msg);
            throw new ServletException(msg);
        }
    }

    
    /**
     * <p>Inoltra la richiesta ad una pagina passata come argomento.</p>
     * 
     * @param req  HttpServletRequest contenente i parametri sulla QueryString
     * @param res  HttpServletResponse per inoltrare la chiamata
     * @param fileJspT pagina JSP a cui puntare nell'inoltro per forward
     * @param q parametro della querystring a cui puntare nell'inoltro per redirect
     * @throws ServletException se si verifica un'eccezione nella redirezione
     * @throws IOException se si verifica un problema di input/output
     * @throws IllegalStateException se la Response era committata o se un URL parziale e' fornito e non puo' essere convertito in un URL valido (v. {@link HttpServletResponse#sendRedirect(String)})
     */
    private void flush(HttpServletRequest req,
                       HttpServletResponse res,
                       String fileJspT,
                       String q) 
                throws ServletException, 
                       IOException,
                       IllegalStateException {
        if (q == null || q.isEmpty()) {
            final RequestDispatcher rd = getServletContext().getRequestDispatcher(fileJspT + "?" + req.getQueryString());
            rd.forward(req, res);
            return;
        }
        res.sendRedirect(getServletContext().getInitParameter("appName") + q);
    }

//    
//    /**
//     * <p>Gestisce il caricamento di file allegati a un determinato status
//     * di progetto e li rappresenta con altrettanti fileset i cui estremi
//     * memorizza anche nel db, in una tabella degli allegati di status.</p>  
//     * 
//     * @param req la HttpServletRequest contenente il valore di 'q' e gli altri parametri necessari a gestire il caricamento e la memorizzazione
//     * @param res la HttpServletResponse utilizzata per indirizzare le risposte
//     * @param parser ParameterParser contenente i parametri provenienti dalla pagina
//     * @param user  utente loggato, per la memorizzazione dell'autore del caricamento
//     * @param writablePrj   elenco di progetti scribili dall'utente loggato, per controlli di autorizzazione
//     * @throws ServletException java.lang.Throwable.Exception.ServletException che viene sollevata se manca un parametro di configurazione considerato obbligatorio o per via di qualche altro problema di puntamento
//     * @throws IOException      java.io.IOException che viene sollevata se si verifica un puntamento a null o in genere nei casi in cui nella gestione del flusso informativo di questo metodo si verifica un problema
//     */
//    private void uploadStatus(HttpServletRequest req, 
//                              HttpServletResponse res, 
//                              ParameterParser parser,
//                              PersonBean user,
//                              Vector<ProjectBean> writablePrj)
//                       throws ServletException, IOException {
//        DBWrapper db = null;                                // DataBound
//        String entityName = null;
//        String attributeName = null;
//        // Struttura contenente gli estremi di ogni allegato
//        HashMap<String, Object> params = new HashMap<String, Object>(13);
//        // Recupera o inizializza 'q' 
//        entToken = parser.getStringParameter("q", VOID_STRING);
//        /* ******************************************************************** *
//         *              Variabili per gestione upload da pagina                 *
//         * ******************************************************************** */
//        // Recupera o inizializza 'id progetto' (upload da Status)
//        int idPrj = parser.getIntParameter("prj-id", DEFAULT_ID);
//        // Recupera o inizializza "id status"   (upload da Status)
//        int idSts = parser.getIntParameter("sts-id", DEFAULT_ID);
//        /* ******************************************************************** *
//         *                      Effettua la connessione                         *
//         * ******************************************************************** */
//        // Effettua la connessione al databound
//        try {
//            db = new DBWrapper();
//        } catch (WebStorageException wse) {
//            throw new ServletException(FOR_NAME + "Non riesco ad instanziare databound.\n" + wse.getMessage(), wse);
//        }
//        /* ******************************************************************** *
//         *                           IV - Control                               * 
//         *      if the upload is about a project, the id of current project     * 
//         *                MUST BE among those writable by user                  *
//         * ******************************************************************** */
//        // Controllo per upload da status
//        try {
//            // Se non riesce a trovare 'id progetto' tra quelli scrivibili, anche qui c'è qualcosa che non va, ed esce
//            if (!db.userCanWrite(idPrj, writablePrj)) {
//                String msg = FOR_NAME + "Si e\' verificato un problema l\'id del progetto corrente non e\' tra quelli scrivibili dall\'utente.\n";
//                log.severe(msg);
//                throw new ServletException(msg);
//            }
//        } catch (WebStorageException wse) {
//            String msg = FOR_NAME + "Si e\' verificato un problema nel controllo che l\'id del progetto corrente sia tra quelli scrivibili dall\'utente.\n" + wse.getMessage();
//            log.severe(msg);
//            throw new ServletException(msg, wse);
//        }
//        /* ******************************************************************** *
//         *              ONLY IF previous controls are ok, go on                 * 
//         *        (Se i controlli vanno a buon fine gestisce l'upload)          *
//         * ******************************************************************** */
//        // Nome della sottodirectory che conterrà i files del sottoprogetto
//        final String PRJ_DIR = String.valueOf(idPrj);
//        // Nome della sottodirectory che conterrà gli allegati specifici
//        String ALL_DIR = null;
//        // Controlla che il token corrente sia abilitato all'upload
//        if (entTokens.contains(entToken)) {
//            if (entToken.equals(Query.PROJECT_PART)) {
//                entityName = "avanzamento";
//                attributeName = "all";
//                ALL_DIR = entityName + STANDARD_DIR_SUFFIX;
//            }
//        } else {
//            String msg = FOR_NAME + "Hai chiamato il FileManager per gestire l'upload dallo status ma non sei nel token ammesso: allora: che vuoi??\n";
//            log.severe(msg + "Non sei piu\' rock? Non sei piu\' metal?\n");
//            throw new ServletException(msg);
//        }
//        try {
//            /* **************************************************************** *
//             *     Checks if the directories do exist; if not, it makes them    *
//             * **************************************************************** */
//            // Gets absolute path of the web application (/)
//            String appPath = req.getServletContext().getRealPath(VOID_STRING);
//            log.info(FOR_NAME + "appPath vale: " + appPath);
//            // Creates the documents root directory (/documents) if it does not exists
//            String documentsRootName = appPath + getServletContext().getInitParameter("urlDirectoryDocumenti");
//            File documentsRoot = new File(documentsRootName);
//            if (!documentsRoot.exists()) {
//                documentsRoot.mkdir();
//                log.info(FOR_NAME + documentsRootName + " created.\n");
//            }
//            // Constructs path of the directory to save uploaded file 
//            String uploadPath =  documentsRootName + File.separator + SAVE_DIR;
//            // Creates the save directory (/documents/upload) if it does not exists
//            File uploadDir = new File(uploadPath);
//            if (!uploadDir.exists()) {
//                uploadDir.mkdir();
//                log.info(FOR_NAME + uploadPath + " created.\n");
//            }
//            // Constructs path of the directory to save attached file(s) 
//            String attachPath =  uploadPath + File.separator + ALL_DIR;
//            // Creates the save directory (/documents/upload/status_attachs) if it does not exists
//            File attachDir = new File(attachPath);
//            if (!attachDir.exists()) {
//                attachDir.mkdir();
//                log.info(FOR_NAME + attachPath + " created.\n");
//            }
//            // Constructs path of the directory to save specific uploaded file 
//            String projPath =  attachPath + File.separator + PRJ_DIR;
//            // Creates the save directory if it does not exists
//            File projDir = new File(projPath);
//            if (!projDir.exists()) {
//                projDir.mkdir();
//                log.info(FOR_NAME + projPath + " created.\n");
//            }
//            /* **************************************************************** *
//             *              Finally it may manage streams as files!             *
//             * **************************************************************** */
//            // Retrieve and manage the document title
//            String title = parser.getStringParameter("doc-name", VOID_STRING);
//            // Recupera e scrive tutti gli allegati
//            for (Part part : req.getParts()) {
//                String fileName = extractFileName(part);
//                if (fileName.equals(VOID_STRING)) {
//                    continue;
//                }
//                // Creates the file object
//                File file = new File(fileName);
//                // Refines the fileName in case it is an absolute path
//                fileName = null;
//                fileName = file.getName();
//                /* ************************************************************ *
//                 *                     Resolves MIME type(s)                    *
//                 * ************************************************************ */
//                // Get the default library (JRE_HOME/lib/content-types.properties)               
//                FileNameMap fileNameMap = URLConnection.getFileNameMap();
//                // Tries to get the MIME type from default library
//                String mimeType = fileNameMap.getContentTypeFor(file.getName());
//                // If not, tries to get the MIME type guessing that from a 
//                // service-provider loading facility - see {@link Files.probeContentType}
//                if (mimeType == null) {
//                    Path path = new File(fileName).toPath();
//                    mimeType = Files.probeContentType(path);
//                }
//                /* ************************************************************ *
//                 *                Retrieve and manage file extension            *
//                 * ************************************************************ */
//                String extension = fileName.substring(fileName.lastIndexOf("."));
//                if (forbiddenExt.contains(extension)) {
//                    String msg = FOR_NAME + "Si e\' verificato un problema di estensione non ammessa; l\'utente ha tentato di caricare un file non consentito.\n";
//                    log.severe(msg);
//                    throw new ServletException("Attenzione: il formato del file caricato non e\' consentito.\n");
//                }
//                /* ************************************************************ *
//                 *                          Given name                          *
//                 * ************************************************************ */
//                String givenName = this.makeName(entityName, attributeName, db);
//                log.info(FOR_NAME + "Nome autogenerato per il file: " + givenName);
//                /* ************************************************************ *
//                 *      Prepares the table hash with parameters to insert       *
//                 * ************************************************************ */
//                log.info(FOR_NAME + "Salvataggio previsto in tabella relativa all\'entita\': " + givenName);
//                // Entity name
//                params.put("nomeEntita", entityName);
//                // Attribute name
//                params.put("nomeAttributo", attributeName);
//                // Real name of the uploaded file in the file system
//                params.put("file", givenName);
//                // Extension of the original file
//                params.put("ext", extension);
//                // Name of the original file chosen by user (named user file)
//                params.put("nome", fileName);
//                // Which one entity instance
//                params.put("belongs", idSts);
//                // The title chosen by user for the attachment
//                params.put("titolo", checkPrime(title));              
//                // Calculated size for now is zero
//                params.put("dimensione", getFileSizeBytes(file));
//                // Calculated mime
//                params.put("mime", mimeType);
//                // Logged user
//                params.put("usr", user);
//                // Write to the database
//                db.setFileDoc(params);
//                // Write to the filesystem
//                part.write(projPath + File.separator + givenName + extension);
//                log.info(FOR_NAME + "Salvataggio in file system effettuato.\n");
//                // Read the file just written
//                File fileVolume = new File(projPath + File.separator + givenName + extension);
//                // Calculated size from file system
//                params.put("dimensione", getFileSizeBytes(fileVolume));
//                // Post Update FileDoc
//                db.postUpdateFileDoc(params);
//            }
//            req.setAttribute("message", "Upload has been done successfully!");
//            flush(req, res, templateJsp, "?q=pol&p=sts&id=" + idPrj + "&ids=" + idSts);
//        } catch (IllegalStateException e) {
//            throw new ServletException("Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n" + e.getMessage(), e);
//        } catch (NullPointerException e) {
//            throw new ServletException("Errore nell'estrazione dei dipartimenti che gestiscono il corso.\n" + e.getMessage(), e);
//        } catch (Exception e) {
//            req.setAttribute("javax.servlet.jsp.jspException", e);
//            req.setAttribute("message", e.getMessage());
//            // Log dell'evento
//            log.severe("Problema: " + e);
//            e.printStackTrace();
//            // Flusso
//            flush(req, res, errorJsp, VOID_STRING);
//            return;
//        }
//    }
    

    /**
     * <p>Gestisce il caricamento di file allegati a un determinato monitoraggio
     * di dipartimento e li rappresenta con altrettanti fileset i cui estremi
     * memorizza anche nel db, in una tabella degli allegati di monitoraggio.</p>  
     * 
     * @param req la HttpServletRequest contenente il valore di 'q' e gli altri parametri necessari a gestire il caricamento e la memorizzazione
     * @param res la HttpServletResponse utilizzata per indirizzare le risposte
     * @param parser ParameterParser contenente i parametri provenienti dalla pagina
     * @param user  utente loggato, per la memorizzazione dell'autore del caricamento
     * @param writableDepts   elenco di progetti scribili dall'utente loggato, per controlli di autorizzazione
     * @throws ServletException java.lang.Throwable.Exception.ServletException che viene sollevata se manca un parametro di configurazione considerato obbligatorio o per via di qualche altro problema di puntamento
     * @throws IOException      java.io.IOException che viene sollevata se si verifica un puntamento a null o in genere nei casi in cui nella gestione del flusso informativo di questo metodo si verifica un problema
     */
//    private void uploadMonitor(HttpServletRequest req, 
//                               HttpServletResponse res, 
//                               ParameterParser parser,
//                               PersonBean user,
//                               Vector<DepartmentBean> writableDepts)
//                        throws ServletException, IOException {
//        DBWrapper db = null;                                // DataBound
//        String entityName = null;
//        String attributeName = null;
//        // Struttura contenente gli estremi di ogni allegato
//        HashMap<String, Object> params = new HashMap<String, Object>(13);
//        // Recupera o inizializza 'q' 
//        entToken = parser.getStringParameter("q", VOID_STRING);
//        /* ******************************************************************** *
//         *              Variabili per gestione upload da pagina                 *
//         * ******************************************************************** */
//        // Recupera o inizializza 'id dipartimento'     (upload da Monitor)
//        int idDip = parser.getIntParameter("dip-id", DEFAULT_ID);
//        // Recupera o inizializza 'id monitoraggio'     (upload da Monitor)
//        int idMon = parser.getIntParameter("mon-id", DEFAULT_ID);
//        // Recupera o inizializza 'anno monitoragigo'
//        int year = parser.getIntParameter("y", DEFAULT_ID);
//        // Recupera o inizializza "quadro monitoraggio" (upload da Monitor)
//        String qM = parser.getStringParameter("qd-id", VOID_STRING);
//        /* ******************************************************************** *
//         *                      Effettua la connessione                         *
//         * ******************************************************************** */
//        // Effettua la connessione al databound
//        try {
//            db = new DBWrapper();
//        } catch (WebStorageException wse) {
//            throw new ServletException(FOR_NAME + "Non riesco ad instanziare databound.\n" + wse.getMessage(), wse);
//        }
//        /* ******************************************************************** *
//         *                           IV - Control                               * 
//         *      if the upload is about a dept, the id of current department     * 
//         *             ad it MUST BE among those writable by user               *
//         * ******************************************************************** */
//        // Controllo per upload da monitor
//        try {
//            // Se non riesce a trovare 'id dipartimento' tra quelli scrivibili, c'è qualcosa che non va, ed esce
//            if (!db.userCanMonitor(idDip, writableDepts)) {
//                String msg = FOR_NAME + "Si e\' verificato un problema l\'id del dipartimento corrente non e\' tra quelli scrivibili dall\'utente.\n";
//                log.severe(msg);
//                throw new ServletException(msg);
//            }
//        } catch (WebStorageException wse) {
//            String msg = FOR_NAME + "Si e\' verificato un problema nel controllo che l\'id del dipartimento corrente sia tra quelli scrivibili dall\'utente.\n" + wse.getMessage();
//            log.severe(msg);
//            throw new ServletException(msg, wse);
//        }
//        /* ******************************************************************** *
//         *              ONLY IF previous controls are ok, go on                 * 
//         *        (Se i controlli vanno a buon fine gestisce l'upload)          *
//         * ******************************************************************** */
//        // Nome della sottodirectory che conterrà i files del sottoprogetto
//        final String DEP_DIR = String.valueOf(idDip);
//        // Nome della sottodirectory che conterrà gli allegati specifici
//        String ALL_DIR = null;
//        // Controlla che il token corrente sia abilitato all'upload
//        if (entTokens.contains(entToken)) {
//           if (entToken.equals(Query.MONITOR_PART)) {
//               entityName = "monitoraggio";
//               attributeName = qM;
//               String peculiarDirSuffix = String.valueOf(STD_SPACER) + qM;
//               ALL_DIR = entityName + peculiarDirSuffix;
//            }
//        } else {
//            String msg = FOR_NAME + "Hai chiamato il FileManager per gestire l'upload da monitor ma non sei nel token ammesso: allora: che vuoi??\n";
//            log.severe(msg + "Non sei piu\' rock? Non sei piu\' metal?\n");
//            throw new ServletException(msg);
//        }
//        try {
//            /* **************************************************************** *
//             *     Checks if the directories do exist; if not, it makes them    *
//             * **************************************************************** */
//            // Gets absolute path of the web application (/)
//            String appPath = req.getServletContext().getRealPath(VOID_STRING);
//            log.info(FOR_NAME + "appPath vale: " + appPath);
//            // Creates the documents root directory (/documents) if it does not exists
//            String documentsRootName = appPath + getServletContext().getInitParameter("urlDirectoryDocumenti");
//            File documentsRoot = new File(documentsRootName);
//            if (!documentsRoot.exists()) {
//                documentsRoot.mkdir();
//                log.info(FOR_NAME + documentsRootName + " created.\n");
//            }
//            // Constructs path of the directory to save uploaded file 
//            String uploadPath =  documentsRootName + File.separator + SAVE_DIR;
//            // Creates the save directory (/documents/upload) if it does not exists
//            File uploadDir = new File(uploadPath);
//            if (!uploadDir.exists()) {
//                uploadDir.mkdir();
//                log.info(FOR_NAME + uploadPath + " created.\n");
//            }
//            // Constructs path of the directory to save attached file(s) 
//            String attachPath =  uploadPath + File.separator + ALL_DIR;
//            // Creates the save directory (/documents/upload/status_attachs) if it does not exists
//            File attachDir = new File(attachPath);
//            if (!attachDir.exists()) {
//                attachDir.mkdir();
//                log.info(FOR_NAME + attachPath + " created.\n");
//            }
//            // Constructs path of the directory to save specific uploaded file 
//            String deptPath =  attachPath + File.separator + DEP_DIR;
//            // Creates the save directory if it does not exists
//            File deptDir = new File(deptPath);
//            if (!deptDir.exists()) {
//                deptDir.mkdir();
//                log.info(FOR_NAME + deptDir + " created.\n");
//            }
//            /* **************************************************************** *
//             *              Finally it may manage streams as files!             *
//             * **************************************************************** */
//            // Retrieve and manage the document title
//            String title = parser.getStringParameter("doc-name", VOID_STRING);
//            // Recupera e scrive tutti gli allegati
//            for (Part part : req.getParts()) {
//                String fileName = extractFileName(part);
//                if (fileName.equals(VOID_STRING)) {
//                    continue;
//                }
//                // Creates the file object
//                File file = new File(fileName);
//                // Refines the fileName in case it is an absolute path
//                fileName = null;
//                fileName = file.getName();
//                /* ************************************************************ *
//                 *                     Resolves MIME type(s)                    *
//                 * ************************************************************ */
//                // Get the default library (JRE_HOME/lib/content-types.properties)               
//                FileNameMap fileNameMap = URLConnection.getFileNameMap();
//                // Tries to get the MIME type from default library
//                String mimeType = fileNameMap.getContentTypeFor(file.getName());
//                // If not, tries to get the MIME type guessing that from a 
//                // service-provider loading facility - see {@link Files.probeContentType}
//                if (mimeType == null) {
//                    Path path = new File(fileName).toPath();
//                    mimeType = Files.probeContentType(path);
//                }
//                /* ************************************************************ *
//                 *                Retrieve and manage file extension            *
//                 * ************************************************************ */
//                String extension = fileName.substring(fileName.lastIndexOf("."));
//                if (forbiddenExt.contains(extension)) {
//                    String msg = FOR_NAME + "Si e\' verificato un problema di estensione non ammessa; l\'utente ha tentato di caricare un file non consentito.\n";
//                    log.severe(msg);
//                    throw new ServletException("Attenzione: il formato del file caricato non e\' consentito.\n");
//                }
//                /* ************************************************************ *
//                 *                          Given name                          *
//                 * ************************************************************ */
//                String givenName = this.makeName(entityName, attributeName, db);
//                log.info(FOR_NAME + "Nome autogenerato per il file: " + givenName);
//                /* ************************************************************ *
//                 *      Prepares the table hash with parameters to insert       *
//                 * ************************************************************ */
//                log.info(FOR_NAME + "Salvataggio previsto in tabella relativa all\'entita\': " + givenName);
//                // Entity name
//                params.put("nomeEntita", entityName);
//                // Attribute name
//                params.put("nomeAttributo", attributeName);
//                // Real name of the uploaded file in the file system
//                params.put("file", givenName);
//                // Extension of the original file
//                params.put("ext", extension);
//                // Name of the original file chosen by user (named user file)
//                params.put("nome", fileName);
//                // Which one entity instance
//                params.put("belongs", idMon);
//                // The title chosen by user for the attachment
//                params.put("titolo", checkPrime(title));              
//                // Calculated size for now is zero
//                params.put("dimensione", getFileSizeBytes(file));
//                // Calculated mime
//                params.put("mime", mimeType);
//                // Logged user
//                params.put("usr", user);
//                // Write to the database
//                db.setFileDoc(params);
//                // Write to the filesystem
//                part.write(deptPath + File.separator + givenName + extension);
//                log.info(FOR_NAME + "Salvataggio in file system effettuato.\n");
//                // Read the file just written
//                File fileVolume = new File(deptPath + File.separator + givenName + extension);
//                // Calculated size from file system
//                params.put("dimensione", getFileSizeBytes(fileVolume));
//                // Post Update FileDoc
//                db.postUpdateFileDoc(params);
//            }
//            req.setAttribute("message", "Upload has been done successfully!");
//            flush(req, res, templateJsp, "?q=mon&y=" + year + "&dip=" + idDip);
//        } catch (IllegalStateException e) {
//            throw new ServletException("Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n" + e.getMessage(), e);
//        } catch (NullPointerException e) {
//            throw new ServletException("Errore nell'estrazione dei dipartimenti che gestiscono il corso.\n" + e.getMessage(), e);
//        } catch (Exception e) {
//            req.setAttribute("javax.servlet.jsp.jspException", e);
//            req.setAttribute("message", e.getMessage());
//            // Log dell'evento
//            log.severe("Problema: " + e);
//            e.printStackTrace();
//            // Flusso
//            flush(req, res, errorJsp, VOID_STRING);
//            return;
//        }
//    }
    
    
    /**
     * <p>Gestisce il caricamento di file allegati a un determinato monitoraggio
     * di ateneo e li rappresenta con altrettanti fileset i cui estremi
     * memorizza anche nel db, in una tabella degli allegati di monitoraggio
     * di ateneo.</p>  
     * 
     * @param req la HttpServletRequest contenente il valore di 'q' e gli altri parametri necessari a gestire il caricamento e la memorizzazione
     * @param res la HttpServletResponse utilizzata per indirizzare le risposte
     * @param parser ParameterParser contenente i parametri provenienti dalla pagina
     * @param user  utente loggato, per la memorizzazione dell'autore del caricamento
     * @throws ServletException java.lang.Throwable.Exception.ServletException che viene sollevata se manca un parametro di configurazione considerato obbligatorio o per via di qualche altro problema di puntamento
     * @throws IOException      java.io.IOException che viene sollevata se si verifica un puntamento a null o in genere nei casi in cui nella gestione del flusso informativo di questo metodo si verifica un problema
     */
//    private void uploadMonitorAte(HttpServletRequest req, 
//                                  HttpServletResponse res, 
//                                  ParameterParser parser,
//                                  PersonBean user)
//                           throws ServletException, IOException {
//        DBWrapper db = null;                                // DataBound
//        String entityName = null;
//        String attributeName = null;
//        // Struttura contenente gli estremi di ogni allegato
//        HashMap<String, Object> params = new HashMap<String, Object>(13);
//        // Recupera o inizializza 'q' 
//        entToken = parser.getStringParameter("q", VOID_STRING);
//        /* ******************************************************************** *
//         *              Variabili per gestione upload da pagina                 *
//         * ******************************************************************** */
//        // Recupera o inizializza 'p'     (upload da Monitor Ateneo)
//        String ate = parser.getStringParameter("p", VOID_STRING);
//        // Recupera o inizializza 'id monitoraggio'     (upload da Monitor)
//        int idMon = parser.getIntParameter("mon-id", DEFAULT_ID);
//        // Recupera o inizializza 'anno monitoragigo'
//        int year = parser.getIntParameter("y", DEFAULT_ID);
//        /* ******************************************************************** *
//         *                      Effettua la connessione                         *
//         * ******************************************************************** */
//        // Effettua la connessione al databound
//        try {
//            db = new DBWrapper();
//        } catch (WebStorageException wse) {
//            throw new ServletException(FOR_NAME + "Non riesco ad instanziare databound.\n" + wse.getMessage(), wse);
//        }
//        /* ******************************************************************** *
//         *                           IV - Control                               * 
//         *      if the upload is about a dept, the id of current department     * 
//         *             ad it MUST BE among those writable by user               *
//         * ******************************************************************** */
//        // Controllo per upload da monitor
//        try {
//            // Se non riesce a trovare i diritti dell'utente pari a quelli di pmo ate c'è qualcosa che non va
//            if (!user.isPmoAteneo()) {
//                String msg = FOR_NAME + "Si e\' verificato un problema: l\'utente non dispone dei permessi per allegare un file del monitoraggio ateneo.\n";
//                log.severe(msg);
//                throw new ServletException(msg);
//            }
//        } catch (NullPointerException npe) {
//            String msg = FOR_NAME + "Si e\' verificato un problema nel puntamento all'oggetto utente.\n" + npe.getMessage();
//            log.severe(msg);
//            throw new ServletException(msg, npe);
//        }
//        /* ******************************************************************** *
//         *              ONLY IF previous controls are ok, go on                 * 
//         *        (Se i controlli vanno a buon fine gestisce l'upload)          *
//         * ******************************************************************** */
//        // Nome della sottodirectory che conterrà gli allegati specifici
//        String ALL_DIR = null;
//        // Controlla che il token corrente sia abilitato all'upload
//        if (entTokens.contains(entToken)) {
//           if (entToken.equals(Query.MONITOR_PART)) {
//               entityName = "monitoraggio";
//               attributeName = "all";
//               ALL_DIR = entityName + STANDARD_DIR_SUFFIX;
//            }
//        } else {
//            String msg = FOR_NAME + "Hai chiamato il FileManager per gestire l'upload da monitor ma non sei nel token ammesso: allora: che vuoi??\n";
//            log.severe(msg + "Non sei piu\' rock? Non sei piu\' metal?\n");
//            throw new ServletException(msg);
//        }
//        try {
//            /* **************************************************************** *
//             *     Checks if the directories do exist; if not, it makes them    *
//             * **************************************************************** */
//            // Gets absolute path of the web application (/)
//            String appPath = req.getServletContext().getRealPath(VOID_STRING);
//            log.info(FOR_NAME + "appPath vale: " + appPath);
//            // Creates the documents root directory (/documents) if it does not exists
//            String documentsRootName = appPath + getServletContext().getInitParameter("urlDirectoryDocumenti");
//            File documentsRoot = new File(documentsRootName);
//            if (!documentsRoot.exists()) {
//                documentsRoot.mkdir();
//                log.info(FOR_NAME + documentsRootName + " created.\n");
//            }
//            // Constructs path of the directory to save uploaded file 
//            String uploadPath =  documentsRootName + File.separator + SAVE_DIR;
//            // Creates the save directory (/documents/upload) if it does not exists
//            File uploadDir = new File(uploadPath);
//            if (!uploadDir.exists()) {
//                uploadDir.mkdir();
//                log.info(FOR_NAME + uploadPath + " created.\n");
//            }
//            // Constructs path of the directory to save attached file(s) 
//            String attachPath =  uploadPath + File.separator + ALL_DIR;
//            // Creates the save directory (/documents/upload/monitoraggio_all) if it does not exists
//            File attachDir = new File(attachPath);
//            if (!attachDir.exists()) {
//                attachDir.mkdir();
//                log.info(FOR_NAME + attachPath + " created.\n");
//            }
//            /* **************************************************************** *
//             *              Finally it may manage streams as files!             *
//             * **************************************************************** */
//            // Retrieve and manage the document title
//            String title = parser.getStringParameter("doc-name", VOID_STRING);
//            // Recupera e scrive tutti gli allegati
//            for (Part part : req.getParts()) {
//                String fileName = extractFileName(part);
//                if (fileName.equals(VOID_STRING)) {
//                    continue;
//                }
//                // Creates the file object
//                File file = new File(fileName);
//                // Refines the fileName in case it is an absolute path
//                fileName = null;
//                fileName = file.getName();
//                /* ************************************************************ *
//                 *                     Resolves MIME type(s)                    *
//                 * ************************************************************ */
//                // Get the default library (JRE_HOME/lib/content-types.properties)               
//                FileNameMap fileNameMap = URLConnection.getFileNameMap();
//                // Tries to get the MIME type from default library
//                String mimeType = fileNameMap.getContentTypeFor(file.getName());
//                // If not, tries to get the MIME type guessing that from a 
//                // service-provider loading facility - see {@link Files.probeContentType}
//                if (mimeType == null) {
//                    Path path = new File(fileName).toPath();
//                    mimeType = Files.probeContentType(path);
//                }
//                /* ************************************************************ *
//                 *                Retrieve and manage file extension            *
//                 * ************************************************************ */
//                String extension = fileName.substring(fileName.lastIndexOf("."));
//                if (forbiddenExt.contains(extension)) {
//                    String msg = FOR_NAME + "Si e\' verificato un problema di estensione non ammessa; l\'utente ha tentato di caricare un file non consentito.\n";
//                    log.severe(msg);
//                    throw new ServletException("Attenzione: il formato del file caricato non e\' consentito.\n");
//                }
//                /* ************************************************************ *
//                 *                          Given name                          *
//                 * ************************************************************ */
//                String givenName = this.makeName(entityName + "ate", attributeName, db);
//                log.info(FOR_NAME + "Nome autogenerato per il file: " + givenName);
//                /* ************************************************************ *
//                 *      Prepares the table hash with parameters to insert       *
//                 * ************************************************************ */
//                log.info(FOR_NAME + "Salvataggio previsto in tabella relativa all\'entita\': " + givenName);
//                // Entity name
//                params.put("nomeEntita", entityName + "ate");
//                // Attribute name
//                params.put("nomeAttributo", attributeName);
//                // Real name of the uploaded file in the file system
//                params.put("file", givenName);
//                // Extension of the original file
//                params.put("ext", extension);
//                // Name of the original file chosen by user (named user file)
//                params.put("nome", fileName);
//                // Which one entity instance
//                params.put("belongs", idMon);
//                // The title chosen by user for the attachment
//                params.put("titolo", checkPrime(title));              
//                // Calculated size for now is zero
//                params.put("dimensione", getFileSizeBytes(file));
//                // Calculated mime
//                params.put("mime", mimeType);
//                // Logged user
//                params.put("usr", user);
//                // Write to the database
//                db.setFileDoc(params);
//                // Write to the filesystem
//                part.write(attachPath + File.separator + givenName + extension);
//                log.info(FOR_NAME + "Salvataggio in file system effettuato.\n");
//                // Read the file just written
//                File fileVolume = new File(attachPath + File.separator + givenName + extension);
//                // Calculated size from file system
//                params.put("dimensione", getFileSizeBytes(fileVolume));
//                // Post Update FileDoc
//                db.postUpdateFileDoc(params);
//            }
//            req.setAttribute("message", "Upload has been done successfully!");
//            flush(req, res, templateJsp, "?q=mon&p=" + ate + "&y=" + year);
//        } catch (IllegalStateException e) {
//            throw new ServletException("Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n" + e.getMessage(), e);
//        } catch (NullPointerException e) {
//            throw new ServletException("Errore nell'estrazione dei dipartimenti che gestiscono il corso.\n" + e.getMessage(), e);
//        } catch (Exception e) {
//            req.setAttribute("javax.servlet.jsp.jspException", e);
//            req.setAttribute("message", e.getMessage());
//            // Log dell'evento
//            log.severe("Problema: " + e);
//            e.printStackTrace();
//            // Flusso
//            flush(req, res, errorJsp, VOID_STRING);
//            return;
//        }
//    }
    
    
    /**
     * <p>Gestisce il caricamento di file allegati a una determinata misurazione
     * di indicatore (entit&agrave; 'indicatoregestione') e li rappresenta con 
     * altrettanti fileset i cui estremi memorizza anche nel db, in una tabella 
     * degli allegati di misurazioni ('indicatoregestione_all').</p>
     * 
     * @param req la HttpServletRequest contenente il valore di 'q' e gli altri parametri necessari a gestire il caricamento e la memorizzazione
     * @param res la HttpServletResponse utilizzata per indirizzare le risposte
     * @param parser ParameterParser contenente i parametri provenienti dalla pagina
     * @param user  utente loggato, per la memorizzazione dell'autore del caricamento
     * @param writablePrj   elenco di progetti scribili dall'utente loggato, per controlli di autorizzazione
     * @throws ServletException java.lang.Throwable.Exception.ServletException che viene sollevata se manca un parametro di configurazione considerato obbligatorio o per via di qualche altro problema di puntamento
     * @throws IOException      java.io.IOException che viene sollevata se si verifica un puntamento a null o in genere nei casi in cui nella gestione del flusso informativo di questo metodo si verifica un problema
     */
    private void uploadGathering(HttpServletRequest req, 
                              	 HttpServletResponse res, 
                              	 ParameterParser parser,
                              	 PersonBean user)
                          throws ServletException, IOException {
        DBWrapper db = null;
        String entityName = null;
        String attributeName = null;
        // Struttura contenente gli estremi di ogni allegato
        HashMap<String, Object> params = new HashMap<String, Object>(13);
        // Recupera o inizializza 'q' 
        entToken = parser.getStringParameter("q", VOID_STRING);
        /* ******************************************************************** *
         *              Variabili per gestione upload da pagina                 *
         * ******************************************************************** */
        // Recupera o inizializza 'id progetto' (upload da Gathering)
        int idPrj = parser.getIntParameter("prj-id", DEFAULT_ID);
        // Recupera o inizializza "id indicatore" (upload da Gathering)
        int idMis = parser.getIntParameter("mis-id", DEFAULT_ID);
        /* ******************************************************************** *
         *                      Effettua la connessione                         *
         * ******************************************************************** */
        // Effettua la connessione al databound
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new ServletException(FOR_NAME + "Non riesco ad instanziare databound.\n" + wse.getMessage(), wse);
        }
        /* ******************************************************************** *
         *  L'indicatore da misurare deve far parte degli indicatori afferenti	*
         *  ai progetti dell'utente ma non serve controllare: chi si metterebbe *
         *  infatti a cambiare l'id dell'indicatore sulla querystring sapendo 	*
         *  che gli estremi dell'utente verranno salvati in allegato alla 		*
         *  misurazione stessa? Mi sembra un deterrente sufficiente.            *     							*
         * ******************************************************************** *
         * ******************************************************************** *
         *        Gestisce l'upload												*
         * ******************************************************************** */
        // Nome della sottodirectory che conterrà gli allegati della misurazione
        final String PRJ_DIR = String.valueOf(idPrj);
        // Nome della sottodirectory che conterrà gli allegati specifici
        String ALL_DIR = null;
        // Controlla che il token corrente sia abilitato all'upload
        if (entTokens.contains(entToken)) {
            if (entToken.equals(COMMAND_INDICATOR)) {
                entityName = "misurazione";
                attributeName = "all";
                ALL_DIR = entityName + STANDARD_DIR_SUFFIX;
            }
        } else {
            String msg = FOR_NAME + "Hai chiamato il FileManager per gestire l'upload ma non sei nel token ammesso: allora: che vuoi?\n";
            log.severe(msg);
            throw new ServletException(msg);
        }
        try {
            /* **************************************************************** *
             *     Checks if the directories do exist; if not, it makes them    *
             * **************************************************************** */
            // Gets absolute path of the web application (/)
            String appPath = req.getServletContext().getRealPath(VOID_STRING);
            log.info(FOR_NAME + "appPath vale: " + appPath);
            // Creates the documents root directory (/documents) if it does not exists
            String documentsRootName = appPath + getServletContext().getInitParameter("urlDirectoryDocumenti");
            File documentsRoot = new File(documentsRootName);
            if (!documentsRoot.exists()) {
                documentsRoot.mkdir();
                log.info(FOR_NAME + documentsRootName + " created.\n");
            }
            // Constructs path of the directory to save uploaded file 
            String uploadPath =  documentsRootName + File.separator + SAVE_DIR;
            // Creates the save directory (/documents/upload) if it does not exists
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
                log.info(FOR_NAME + uploadPath + " created.\n");
            }
            // Constructs path of the directory to save attached file(s) 
            String attachPath =  uploadPath + File.separator + ALL_DIR;
            // Creates the save directory (/documents/upload/status_attachs) if it does not exists
            File attachDir = new File(attachPath);
            if (!attachDir.exists()) {
                attachDir.mkdir();
                log.info(FOR_NAME + attachPath + " created.\n");
            }
            // Constructs path of the directory to save specific uploaded file 
            String projPath =  attachPath + File.separator + PRJ_DIR;
            // Creates the save directory if it does not exists
            File projDir = new File(projPath);
            if (!projDir.exists()) {
                projDir.mkdir();
                log.info(FOR_NAME + projPath + " created.\n");
            }
            /* **************************************************************** *
             *              Finally it may manage streams as files!             *
             * **************************************************************** */
            // Retrieve and manage the document title
            String title = parser.getStringParameter("doc-name", VOID_STRING);
            // Recupera e scrive tutti gli allegati
            for (Part part : req.getParts()) {
                String fileName = extractFileName(part);
                if (fileName.equals(VOID_STRING)) {
                    continue;
                }
                // Creates the file object
                File file = new File(fileName);
                // Refines the fileName in case it is an absolute path
                fileName = null;
                fileName = file.getName();
                /* ************************************************************ *
                 *                     Resolves MIME type(s)                    *
                 * ************************************************************ */
                // Get the default library (JRE_HOME/lib/content-types.properties)               
                FileNameMap fileNameMap = URLConnection.getFileNameMap();
                // Tries to get the MIME type from default library
                String mimeType = fileNameMap.getContentTypeFor(file.getName());
                // If not, tries to get the MIME type guessing that from a 
                // service-provider loading facility - see {@link Files.probeContentType}
                if (mimeType == null) {
                    Path path = new File(fileName).toPath();
                    mimeType = Files.probeContentType(path);
                }
                /* ************************************************************ *
                 *                Retrieve and manage file extension            *
                 * ************************************************************ */
                String extension = fileName.substring(fileName.lastIndexOf("."));
                if (forbiddenExt.contains(extension)) {
                    String msg = FOR_NAME + "Si e\' verificato un problema di estensione non ammessa; l\'utente ha tentato di caricare un file non consentito.\n";
                    log.severe(msg);
                    throw new ServletException("Attenzione: il formato del file caricato non e\' consentito.\n");
                }
                /* ************************************************************ *
                 *                          Given name                          *
                 * ************************************************************ */
                String givenName = this.makeName(entityName, attributeName, db);
                log.info(FOR_NAME + "Nome autogenerato per il file: " + givenName);
                /* ************************************************************ *
                 *      Prepares the table hash with parameters to insert       *
                 * ************************************************************ */
                log.info(FOR_NAME + "Salvataggio previsto in tabella relativa all\'entita\': " + givenName);
                // Entity name
                params.put("nomeEntita", entityName);
                // Attribute name
                params.put("nomeAttributo", attributeName);
                // Real name of the uploaded file in the file system
                params.put("file", givenName);
                // Extension of the original file
                params.put("ext", extension);
                // Name of the original file chosen by user (named user file)
                params.put("nome", fileName);
                // Which one entity instance
                params.put("belongs", idMis);
                // The title chosen by user for the attachment
                params.put("titolo", checkPrime(title));              
                // Calculated size for now is zero
                params.put("dimensione", getFileSizeBytes(file));
                // Calculated mime
                params.put("mime", mimeType);
                // Logged user
                params.put("usr", user);
                // Write to the database
                db.setFileDoc(params);
                // Write to the filesystem
                part.write(projPath + File.separator + givenName + extension);
                log.info(FOR_NAME + "Salvataggio in file system effettuato.\n");
                // Read the file just written
                File fileVolume = new File(projPath + File.separator + givenName + extension);
                // Calculated size from file system
                params.put("dimensione", getFileSizeBytes(fileVolume));
                // Post Update FileDoc
                db.postUpdateFileDoc(params);
            }
            req.setAttribute("message", "Upload has been done successfully!");
            flush(req, res, templateJsp, "?q=ind&p=mon&id=" + idPrj + "&idm=" + idMis + "#Allegati");
        } catch (IllegalStateException e) {
            throw new ServletException("Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n" + e.getMessage(), e);
        } catch (NullPointerException e) {
            throw new ServletException("Errore nell'estrazione dei dipartimenti che gestiscono il corso.\n" + e.getMessage(), e);
        } catch (Exception e) {
            req.setAttribute("javax.servlet.jsp.jspException", e);
            req.setAttribute("message", e.getMessage());
            // Log dell'evento
            log.severe("Problema: " + e);
            e.printStackTrace();
            // Flusso
            flush(req, res, errorJsp, VOID_STRING);
            return;
        }
    }
    

    /**
     * <p>Extracts file name from HTTP header content-disposition.</p>
     * 
     * @param part : the {@link javax.servlet.http.Part Part} of the {@link javax.servlet.http.HttpServletRequest Request}
     * @return <code>String</code> - the name of the file which might be in Request
     */
    private static String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }
        return VOID_STRING;
    }
    
    
    /**
     * <p>Genera un nome univoco da inserire come chiave (di fatto)
     * nella tabella degli allegati a una entit&agrave;.</p>
     * <p>Per avere la certezza di evitare duplicazioni, eventualit&agrave;
     * scongiurata peraltro dai vincoli inseriti sulla tabella (nome dell'allegato
     * <code>UNIQUE NOT NULL</code>, quindi di fatto quasi una chiave primaria...)  
     * viene effettuata una query di selezione per controllare che quel nome non 
     * sia gi&agrave; presente nella tabella degli allegati.</p>
     * 
     * @param tableName     radice dell'entit&agrave; relativamente alla quale si vuol generare un nome univoco dell'allegato 
     * @param attributeName suffisso dell'entit&agrave; relativamente alla quale si vuol generare un nome univoco dell'allegato
     * @param db    databound, per effettuare il controllo di esistenza del nome e avere la certezza di evitare duplicazioni
     * @return <code>String</code> - il generato per il file da inserire come allegato logico riferimento all'allegato fisico
     * @throws ServletException se si verifica tipicamente un problema SQL
     * @throws IOException se si verifica un problema in qualche sorta di input/output
     * @throws IllegalStateException se si verifica un problema di chiamata di metodo fuori contesto di flusso appropriato
     */
    @SuppressWarnings("static-method")
	private String makeName(String tableName, 
                            String attributeName, 
                            DBWrapper db) 
                     throws ServletException, 
                            IOException,
                            IllegalStateException {
        GregorianCalendar calendar = Utils.getCurrentDate();
        // Gets current year in two-digits format
        int shortYear = calendar.get(Calendar.YEAR) - 2000;
        assert(shortYear > NOTHING);
        // Gets current month in started-from-one format
        int month = calendar.get(Calendar.MONTH) + 1;
        StringBuffer monthAsStringBuffer = new StringBuffer(String.valueOf(month));
        if (month < 10) {
            monthAsStringBuffer.insert(0, '0');
        }
        // Gets current day in two-digits format
        int day = calendar.get(Calendar.DATE);
        StringBuffer dayAsStringBuffer = new StringBuffer(String.valueOf(day));
        if (day < 10) {
            dayAsStringBuffer.insert(0, '0');
        }
        // Gets current hour in two-digits format
        String hour = Utils.getCurrentHour();
        // Gets current minutes in two-digits format
        String minutes = Utils.getCurrentMinutes();
        // Values about the desired range
        int max = 99, min = 01;
        // Makes a two-digits pseudo-random numeric suffix
        int progressive = (int) Math.round(Math.random() * ((max - min) + 1) + min);
        // Makes the given name
        StringBuffer givenName = new StringBuffer(String.valueOf(shortYear).concat(String.valueOf(monthAsStringBuffer).concat(String.valueOf(dayAsStringBuffer).concat(hour).concat(minutes).concat(String.valueOf(progressive)))));
        // Check if the calculated name already exists
        try {
            if (db.existsFileName(tableName, attributeName, String.valueOf(givenName))) {
                // If the name already exists, it changes that 
                progressive = (int) Math.round(Math.random() * ((max - min) + 1) + min);
                givenName.append(progressive);
            }
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel codice che effettua il recupero di valori dal db, a livello di SQL o nel calcolo di valori.\n";
            log.severe(msg);
            throw new ServletException(msg + wse.getMessage(), wse);
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            log.severe(msg);
            throw new IllegalStateException(msg + ise.getMessage(), ise);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
            log.severe(msg);
            throw new ServletException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            log.severe(msg);
            throw new ServletException(msg + e.getMessage(), e);
        }
        return givenName.toString();
    }
    
    
    /**
     * <p>Returns a String replacing all the sequence of characters 
     * matching regex and replacement String.</p>
     * 
     * @param toCheck the String to check
     * @return <code>String</code> - the String replaced
     */
    public static String checkPrime(String toCheck) {
        String pattern = toCheck.replaceAll("(?<!')'(?!')", "''");
        return pattern;
    }
    
    
    /**
     * <p>Restituisce il peso in bytes di un file 
     * che accetta come argomento.</p>
     * 
     * @param file File di cui bisogna restituire il peso in B
     * @return <code>long</code> - il peso del file in B
     */
    public static long getFileSizeBytes(File file) {
        return file.length();
    }    
    
    
    /**
     * <p>Restituisce il peso in KiloBytes di un file 
     * che accetta come argomento.</p>
     * 
     * @param file File di cui bisogna restituire il peso in KB
     * @return <code>double</code> - il peso del file in Kappa
     */
    public static double getFileSizeKiloBytes(File file) {
        return (double) file.length() / 1024;
    }    
    
    
    /**
     * <p>Restituisce il peso in MegaBytes di un file 
     * che accetta come argomento.</p>
     * 
     * @param file File di cui bisogna restituire il peso in MB
     * @return <code>double</code> - il peso del file in Mega
     */
    public static double getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024);
    }

    
    /**
     * Legge un file (di configurazione)
     * 
     * @param path
     * @return
     */
    public static String getContent(String path)
                             throws NotFoundException, CommandException {
        StringBuffer content = new StringBuffer();
        Scanner sc = null;
        try {
            URL file = new URL(path);
            try {
                sc = new Scanner(file.openStream());
                while (sc.hasNext()) {
                    // Unnecessary Cast: sc Already returns a String
                    content.append((String) sc.next());
                }
            } catch (IOException ioe) {
                String msg = FOR_NAME + "Si e\' verificato un problema nel puntamento al file remoto.\n";
                log.severe(msg);
                throw new NotFoundException(msg, ioe);
            } finally {
                try {
                    if (sc != null)
                        sc.close();
                } catch (IllegalStateException ise) {
                    String msg = FOR_NAME + "Probabile problema nel tentare di eseguire l\'operazione dopo che lo scanner e\' stato chiuso.\n";
                    log.severe(msg); 
                    throw new CommandException(msg, ise);
                }
            }
        } catch (MalformedURLException mue) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento all\'url, probabilmente il percorso e\' errato o il browsing non abilitato.\n";
            log.severe(msg);
            throw new NotFoundException("Attenzione: controllare che la risorsa sia raggiungibile!\n" + mue.getMessage(), mue);
        }        
        return new String(content);
    }
    
}