/*
 *   Risk Mapping Software: Applicazione web per la gestione di 
 *   sondaggi inerenti al rischio corruttivo cui i processi organizzativi
 *   dell'ateneo possono essere esposti e per la gestione di reportistica
 *   e mappature per la gestione dei "rischi on line" (rol).
 *
 *   Risk Mapping Software (rms)
 *   web applications to make survey about the amount and kind of risk
 *   which each process is exposed, and to publish, and manage,
 *   report and risk information.
 *   Copyright (C) renewed 2022 Giovanroberto Torre
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

package it.rol;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.rol.bean.ActivityBean;
import it.rol.bean.DepartmentBean;
import it.rol.bean.InterviewBean;
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
import it.rol.bean.QuestionBean;
import it.rol.bean.RiskBean;
import it.rol.command.AuditCommand;
import it.rol.command.DepartmentCommand;
import it.rol.command.ProcessCommand;
import it.rol.command.RiskCommand;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.CommandException;


/**
 * <p><code>Data</code> &egrave; la servlet della web-application rol
 * che pu&ograve; essere utilizzata in pi&uacute; contesti:<ol>
 * <li>su una richiesta sincrona: per produrre output con contentType differenti da
 * 'text/html'</li>
 * <li>su una richiesta asincrona: per ottenere tuple da mostrare asincronamente
 * nelle pagine</li></ol></p>
 * <p>Nel primo caso, questa servlet fa a meno, legittimamente, del design (View),
 * in quanto l'output prodotto consiste in pure tuple prive di presentazione
 * (potenzialmente: fileset CSV, formati XML, dati con o senza metadati, RDF,
 * JSON, <cite>and so on</cite>).<br />
 * <p>
 * Questa servlet estrae l'azione dall'URL, ne verifica la
 * correttezza, quindi in base al valore del parametro <code>'entToken'</code> ricevuto
 * (qui chiamato 'qToken' per motivi storici, ma non importa)
 * richiama le varie Command che devono eseguire i comandi specifici.
 * Infine, recupera l'output dai metodi delle Command stesse richiamati
 * e li restituisce a sua volta al cliente sotto forma non necessariamente di outputstream
 * 'text/html' (come nel funzionamento standard delle applicazioni web),
 * quanto sotto forma di file nel formato richiesto (.csv, .xml, ecc.),
 * oppure passando il nome di una risorsa da richiamare in modo asincrono
 * per mostrare le tuple estratte, e passate in Request.<br />
 * In caso di richieste di output non di tipo text/html,
 * elabora anche un nome univoco per ogni file generato, basandosi sul
 * timestamp dell'estrazione/richiesta.
 * </p>
 * <p>
 * La classe che realizza l'azione deve implementare l'interfaccia
 * Command e, dopo aver eseguito le azioni necessarie, restituire
 * un set di risultati che dovr&agrave; essere utilizzato per
 * visualizzare i dati all'interno dei files serviti, ai quali
 * sarà fatto un forward.
 * </p>
 * L'azione presente nell'URL deve avere il seguente formato:
 * <pre>&lt;entToken&gt;=&lt;nome&gt;</pre>
 * dove 'nome' è il valore del parametro 'entToken' che identifica
 * l'azione da compiere al fine di generare i record.<br />
 * Oltre al parametro <code>'entToken'</code> possono essere presenti anche
 * eventuali altri parametri, ma essi non hanno interesse nel contesto
 * della presente classe, venendo incapsulati nella HttpServletRequest
 * e quindi inoltrati alla classe Command che deve fare il lavoro di
 * estrazione. Normalmente, tali altri parametri possono essere presenti
 * sotto forma di parametri sulla querystring, ma anche direttamente
 * settati nella request; ci&ograve; non interessa alcunch&eacute; ai fini
 * del funzionamento della presente classe.
 * </p>
 * <p>
 * Altre modalit&agrave; di generazione di output differenti da 'text/html'
 * (chiamate a pagine .jsp che incorporano la logica di preparazione del CSV,
 * chiamate a pagina .jsp che si occupano di presentare il metadato...)
 * vanno assolutamente evitate in favore dell'uso di questa servlet.
 * </p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class Data extends HttpServlet implements Constants {

    /**
     * La serializzazione necessita della dichiarazione
     * di una costante di tipo long identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = -7053908837630394953L;
    /**
     * Nome di questa classe
     * (viene utilizzato per contestualizzare i messaggi di errore)
     */
    private static final String FOR_NAME = "\n" + Logger.getLogger(Data.class.getName()) + ": ";
    /**
     * Logger della classe per scrivere i messaggi di errore.
     * All logging goes through this logger.
     */
    private static Logger log = Logger.getLogger(Data.class.getName());
    /**
     * Serve per inizializzare i rendirizzamenti con il servletToken
     */
    private ServletContext servletContext;
    /**
     * Parametro della query string identificante una Command.
     */
    private String qToken;
    /**
     * Parametro della query string per richiedere un certo formato di output.
     */
    private String format;
    /**
     * Pagina a cui la command reindirizza per mostrare le fasi nel contesto dei processi
     */
    private static final String nomeFileProcessoAjax = "/jsp/prProcessoAjax.jsp";
    /**
     * Pagina a cui la command reindirizza per mostrare i processi nel contesto di una struttura
     */
    //private static final String nomeFileProcessiStruttureAjax = "/jsp/stElencoAjax.jsp";


    /**
     * Inizializza (staticamente) le variabili globali e i parametri di inizializzazione.
     *
     * @param config la configurazione usata dal servlet container per passare informazioni alla servlet <strong>durante l'inizializzazione</strong>
     * @throws ServletException una eccezione che puo' essere sollevata quando la servlet incontra difficolta'
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        /*
         *  Inizializzazione da superclasse
         */
        super.init(config);
        /*
         *  Inizializzazione del servletToken
         */
        servletContext = getServletContext();
    }


    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @SuppressWarnings("javadoc")
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
                    throws ServletException, IOException {
        // La pagina della servlet e' sganciata dal template, anzi ne costituisce un frammento
        String fileJsp = null;
        // Recupera valore di ent (servito da un ConfigManager esterno alla Data)
        qToken = req.getParameter(ConfigManager.getEntToken());
        // Recupera il formato dell'output, se specificato
        format = req.getParameter(ConfigManager.getOutToken());
        // Recupera o inizializza parametro per identificare la pagina
        String part = req.getParameter("p");
        // Struttura da restituire in Request
        AbstractList<?> lista = null;
        // Message
        log.info("===> Log su servlet Data. <===");
        // Decodifica la richiesta
        try {
            // Gestione estrazione interviste ("data?q=in")
            if (qToken.equalsIgnoreCase(COMMAND_AUDIT)) {
                // Verifica se deve servire un output csv
                if (format != null && !format.isEmpty() && format.equalsIgnoreCase(CSV)) {
                    // Recupero elementi in base alla richiesta
                    lista = retrieve(req, COMMAND_AUDIT);
                    // Passaggio in request per uso delle lista
                    req.setAttribute("lista", lista);
                    // Assegnazione di un valore di default se il parametro 'p' è nullo
                    String key = (part == null ? COMMAND_AUDIT : part);
                    // Genera il file CSV
                    makeCSV(req, res, key);
                    // Ha finito
                    return;
                }
            // Gestione estrazione rischi ("data?q=ri")
            } else if (qToken.equalsIgnoreCase(COMMAND_RISK)) {
                // Verifica se deve servire un output csv
                if (format != null && !format.isEmpty() && format.equalsIgnoreCase(CSV)) {
                    // Recupero elementi in base alla richiesta
                    lista = retrieve(req, COMMAND_RISK);
                    // Passaggio in request per uso delle lista
                    req.setAttribute("lista", lista);
                    // Assegnazione di un valore di default se il parametro 'p' è nullo
                    String key = (part == null ? COMMAND_RISK : part);
                    // Genera il file CSV
                    makeCSV(req, res, key);
                    // Ha finito
                    return;
                }
            // Gestione estrazione processi ("data?q=pr")
            } else if (qToken.equalsIgnoreCase(COMMAND_PROCESS)) {
                // Verifica se deve servire un output csv
                if (format != null && !format.isEmpty() && format.equalsIgnoreCase(CSV)) {
                    // Macro/Sotto/Processi estratti in base alla rilevazione
                    lista = retrieve(req, COMMAND_PROCESS);
                    // Passaggio in request per uso delle lista
                    req.setAttribute("listaProcessi", lista);
                    // Genera il file CSV
                    makeCSV(req, res, COMMAND_PROCESS);
                    // Esce
                    return;
                } // Gestione dettaglio processo via XHR
                // Se non è uscito, vuol dire che deve servire una richiesta asincrona ("data?q=pr&p=pro&pliv=#&liv=#&r=$")
                HashMap<String, ArrayList<?>> processElements = retrieve(req, COMMAND_PROCESS, PART_PROCESS);
                // Imposta nella request liste di elementi collegati a processo
                req.setAttribute("listaInput", processElements.get(TIPI_LISTE[0]));
                req.setAttribute("listaFasi", processElements.get(TIPI_LISTE[1]));
                req.setAttribute("listaOutput", processElements.get(TIPI_LISTE[2]));
                req.setAttribute("listaRischi", processElements.get(TIPI_LISTE[3]));
                // Output in formato di default
                fileJsp = nomeFileProcessoAjax;
            // Gestione estrazione strutture ("data?q=st")
            } else if (qToken.equalsIgnoreCase(COMMAND_STRUCTURES)) {
                // Verifica se deve servire un output csv
                if (format != null && !format.isEmpty() && format.equalsIgnoreCase(CSV)) {
                    // Strutture estratte in base alla rilevazione
                    lista = retrieve(req, COMMAND_STRUCTURES);
                    // Passaggio in request per uso delle lista
                    req.setAttribute("listaOrganigramma", lista);
                    // Genera il file CSV
                    makeCSV(req, res, COMMAND_STRUCTURES);
                    // Esce
                    return;
                }/* inserire qui il codice invocato sulla richiesta asincrona
                // Processi estratti in base alla struttura
                lista = retrieve(req, COMMAND_STRUCTURES);
                req.setAttribute("listaProcessi", lista);
                // Persone estratte in base al processo
                //lista = retrieve(req, Query.COMMAND_PERSON);
                //req.setAttribute("listaPersone", lista);
                // Output in formato di default
                fileJsp = nomeFileProcessiStruttureAjax;
                 */
            } else {
                String msg = FOR_NAME + "Valore del parametro \'q\' (" + qToken + ") non consentito. Impossibile visualizzare i risultati.\n";
                log.severe(msg);
                throw new ServletException(msg);
            }
        } catch (CommandException ce) {
            throw new ServletException(FOR_NAME + "Problema nel recupero dei dati richiesti.\n" + ce.getMessage(), ce);
        }
        /*
         * Forworda la richiesta, esito finale di tutto
         */
        RequestDispatcher dispatcher = servletContext.getRequestDispatcher(fileJsp);
        dispatcher.forward(req, res);
    }


    /**
     * <p>Restituisce un elenco generico di elementi 
     * (interviste, macroprocessi, strutture...)
     * relativi a una richiesta specifica.</p>
     *
     * @param req HttpServletRequest contenente i parametri per contestualizzare l'estrazione
     * @param qToken il token della commmand in base al quale bisogna preparare la lista di elementi
     * @return <code>ArrayList&lt;?&gt; - lista contenente gli elementi trovati
     * @throws CommandException se si verifica un problema nella WebStorage (DBWrapper), nella Command interpellata, o in qualche puntamento
     */
    private static ArrayList<?> retrieve(HttpServletRequest req,
                                         String qToken)
                                  throws CommandException {
        // Dichiara generico elenco di elementi da restituire
        ArrayList<?> list = null;
        // Ottiene i parametri della richiesta
        ParameterParser parser = new ParameterParser(req);
        // Recupera o inizializza parametro per identificare la pagina
        String part = parser.getStringParameter("p", VOID_STRING);
        // Recupera o inizializza parametro per identificare la rilevazione
        String codeSurvey = parser.getStringParameter("r", VOID_STRING);
        // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
        HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
        PersonBean user = (PersonBean) ses.getAttribute("usr");
        if (user == null) {
            throw new CommandException(FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n");
        }
        // Gestisce la richiesta
        try {
            // Istanzia nuovo Databound
            DBWrapper db = new DBWrapper();
            // "data?q=in"
            if (qToken.equalsIgnoreCase(COMMAND_AUDIT)) {
                // "&p=sqs"
                if (part.equalsIgnoreCase(PART_SELECT_QSS)) {
                    // Recupera dal Databound elenco di interviste in base a rilevazione
                    ArrayList<InterviewBean> interviews = db.getInterviewsBySurvey(user, new HashMap<String, LinkedHashMap<String, String>>(), ConfigManager.getSurvey(codeSurvey));
                    // Dichiara elenco di interviste corredate di risposte
                    ArrayList<InterviewBean> interviewsWithAnswer = new ArrayList<>();
                    // Per ogni intervista
                    for (InterviewBean interview : interviews) {
                        // Recupera tutti i parametri identificanti l'intervista
                        HashMap<String, LinkedHashMap<String, String>> interviewParams = AuditCommand.loadInterviewParams(codeSurvey, interview);
                        // Recupera tutte le risposte identificate in base ai parametri dell'intervista
                        ArrayList<QuestionBean> answers = db.getAnswers(user, interviewParams, ConfigManager.getSurvey(codeSurvey));
                        // Imposta le risposte dell'intervista corrente 
                        interview.setRisposte(answers);
                        // Calcola quanti quesiti hanno ricevuto effettivamente risposta
                        interview.setInformativa(String.valueOf(db.getQuestionsAmountWithAnswerByInterview(interviewParams, ConfigManager.getSurvey(codeSurvey))));
                        // Aggiunge l'intervista completa di risposte alla lista di interviste corredate di risposte
                        interviewsWithAnswer.add(interview);
                    }
                    list = interviewsWithAnswer;
                // "&p=rqs"
                } else if (part.equalsIgnoreCase(PART_RESUME_QST)) {
                    // Tabella dei parametri
                    HashMap<String, LinkedHashMap<String, String>> params = new HashMap<>();
                    // Recupera tutti i parametri identificanti l'intervista
                    RiskCommand.loadParams(part, parser, params);
                    // Recupera l'ora del sondaggio
                    String questTimeAsString = params.get(PARAM_SURVEY).get("t").replaceAll(String.valueOf(UNDERSCORE), String.valueOf(COLON));
                    Time questTime = Utils.format(questTimeAsString, TIME_SQL_PATTERN);
                    // Crea una lista (di una)
                    ArrayList<InterviewBean> interviews = new ArrayList<>();
                    // Crea un'intervista per fare da contenitore alle risposte
                    InterviewBean interview = new InterviewBean(DEFAULT_ID, VOID_STRING, VOID_STRING, Utils.format(params.get(PARAM_SURVEY).get("d")), Utils.format(params.get(PARAM_SURVEY).get("d")), questTime);
                    // Elenco risposte ai quesiti collegati all'intervista
                    ArrayList<QuestionBean> answers = null;
                    // Recupera dal Databound elenco di risposte in base a intervista
                    answers = db.getAnswers(user, params, ConfigManager.getSurvey(codeSurvey));
                    // Imposta le risposte nell'intervista
                    interview.setRisposte(answers);
                    // Aggiunge l'intervista alla lista (di una)
                    interviews.add(interview);
                    // Casta elenco di risposte a lista generica
                    list = interviews;
                // Non c'è il parametro 'p'
                } else {
                    // chiamata di sola in senza parametri
                }
            // "data?q=ri"
            } else if (qToken.equalsIgnoreCase(COMMAND_RISK)) {
                // Recupera tutti i rischi della rilevazione corrente
                ArrayList<RiskBean> risks = db.getRisks(user, ConfigManager.getSurvey(codeSurvey).getId(), ConfigManager.getSurvey(codeSurvey));
                // Prepara una lista di rischi contenenti ciascuno l'elenco dei processi che sono esposti al rischio stesso
                ArrayList<RiskBean> risksWithProcess = new ArrayList<>();
                // Per ogni rischio trovato...
                for (RiskBean risk : risks) {
                    // ...Ne recupera i processi esposti e li carica nel rischio stesso
                    risk.setProcessi(db.getProcessByRisk(user, risk, ConfigManager.getSurvey(codeSurvey)));
                    // Carica il rischio valorizzato con i processi alla lista dei rischi
                    risksWithProcess.add(risk);
                }
                // In questo modo restituisce solo i rischi aventi processi esposti, ma per le regole di business, non devono esserci rischi nel registro non associati a processi
                list = risksWithProcess;
            // "data?q=pr"
            } else if (qToken.equalsIgnoreCase(COMMAND_PROCESS)) {
                // Cerca l'identificativo del processo anticorruttivo
                int idP = parser.getIntParameter("pliv", DEFAULT_ID);
                // Cerca la granularità del processo anticorruttivo
                byte liv = parser.getByteParameter("liv", NOTHING);
                // Deve recuperare uno specifico sottoinsieme di processi identificato dai parametri di navigazione oppure tutti i macro_at con relativi figli e nipoti
                ArrayList<ItemBean> mats = db.getMacroSubProcessAtBySurvey(user, idP, liv, ConfigManager.getSurvey(codeSurvey));
                // Restituisce la lista gerarchica di processi trovati in base alla rilevazione
                list = mats;
            // "data?q=st"
            } else if (qToken.equalsIgnoreCase(COMMAND_STRUCTURES)) {
                // Fa la stessa query della navigazione per strutture
                ArrayList<DepartmentBean> structures = DepartmentCommand.retrieveStructures(codeSurvey, user, db);
                // Restituisce la lista gerarchica di strutture trovate in base alla rilevazione
                list = structures;
            }
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Si e\' verificato un problema. Impossibile visualizzare i risultati.\n" + ce.getLocalizedMessage();
            log.severe(msg);
            throw new CommandException(msg);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n" + e.getLocalizedMessage();
            log.severe(msg);
            throw new CommandException(msg);
        }
        return list;
    }


    /**
     * <p>Restituisce una mappa contenente elenchi di elementi generici 
     * (input, fasi, output...) estratti in base alla richiesta ricevuta
     * e indicizzati per una chiave convenzionale, definita nelle costanti.</p>
     *
     * @param req HttpServletRequest contenente i parametri per contestualizzare l'estrazione
     * @param qToken il token della commmand in base al quale bisogna preparare la lista di elementi
     * @param pToken il token relativo alla parte di gestione da effettuare
     * @return <code>HashMap&lt;String,ArrayList&lt;?&gt;&gt; - dictionary contenente le liste di elementi desiderati, indicizzati per una chiave convenzionale
     * @throws CommandException se si verifica un problema nella WebStorage (DBWrapper), nella Command interpellata, o in qualche puntamento
     */
    private static HashMap<String, ArrayList<?>> retrieve(HttpServletRequest req,
                                                          String qToken,
                                                          String pToken)
                                                   throws CommandException {
        // Dichiara generico elenco di elementi da restituire
        HashMap<String,ArrayList<?>> list = null;
        // Ottiene i parametri della richiesta
        ParameterParser parser = new ParameterParser(req);
        // Recupera o inizializza parametro per identificare la pagina
        String part = parser.getStringParameter("p", VOID_STRING);
        // Recupera o inizializza parametro per identificare la rilevazione
        String codeSurvey = parser.getStringParameter("r", VOID_STRING);
        // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
        HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
        PersonBean user = (PersonBean) ses.getAttribute("usr");
        if (user == null) {
            throw new CommandException(FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n");
        }
        // Gestisce la richiesta
        try {
            // Istanzia nuovo Databound
            DBWrapper db = new DBWrapper();
            // "data?q=pr"
            if (qToken.equalsIgnoreCase(COMMAND_PROCESS)) {
                // "&p=pro"
                if (part.equalsIgnoreCase(PART_PROCESS)) {
                    // Cerca l'identificativo del processo anticorruttivo
                    int idP = parser.getIntParameter("pliv", DEFAULT_ID);
                    // Cerca la granularità del processo anticorruttivo
                    int liv = parser.getIntParameter("liv", DEFAULT_ID);
                    // Recupera Input estratti in base al processo
                    ArrayList<ItemBean> listaInput = ProcessCommand.retrieveInputs(user, idP, liv, codeSurvey, db);
                    // Recupera Fasi estratte in base al processo
                    ArrayList<ActivityBean> listaFasi = ProcessCommand.retrieveActivities(user, idP, liv, codeSurvey, db);
                    // Recupera Output estratti in base al processo
                    ArrayList<ItemBean> listaOutput = ProcessCommand.retrieveOutputs(user, idP, liv, codeSurvey, db);
                    // Recupera Rischi estratti in base al processo
                    ArrayList<RiskBean> listaRischi = ProcessCommand.retrieveRisks(user, idP, liv, codeSurvey, db);
                    // Istanzia la tabella in cui devono essere settate le liste
                    list = new HashMap<>();
                    // Imposta nella tabella le liste trovate
                    list.put(TIPI_LISTE[0], listaInput);
                    list.put(TIPI_LISTE[1], listaFasi);
                    list.put(TIPI_LISTE[2], listaOutput);
                    list.put(TIPI_LISTE[3], listaRischi);
                }
            // "data?q=st"
            } else if (qToken.equalsIgnoreCase(COMMAND_STRUCTURES)) {
                // Fa la stessa query della navigazione per strutture
                //ArrayList<DepartmentBean> structures = DepartmentCommand.retrieveStructures(codeSurvey, user, db);
                // Restituisce la lista gerarchica di strutture trovate in base alla rilevazione
                //list = structures;
            }
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Si e\' verificato un problema. Impossibile visualizzare i risultati.\n" + ce.getLocalizedMessage();
            log.severe(msg);
            throw new CommandException(msg);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n" + e.getLocalizedMessage();
            log.severe(msg);
            throw new CommandException(msg);
        }
        return list;
    }
    

    /**
     * <p>Gestisce la generazione dell&apos;output in formato di uno stream CSV 
     * che sar&agrave; recepito come tale dal browser e trattato di conseguenza 
     * (normalmente con il download).</p>
     * <p>Usa altri metodi, interni, per ottenere il nome del file, che dev&apos;essere un
     * nome univoco, e per la stampa vera e propria nel PrintWriter.</p>
     * <p>Un&apos;avvertenza importante riguarda il formato del character encoding!
     * Il db di processi anticorruttivi &egrave; codificato in UTF&ndash;8;
     * pertanto nell'implementazione potrebbe sembrare ovvio che
     * il characterEncoding migliore da impostare sia il medesimo, cosa che si fa
     * con la seguente istruzione:
     * <pre>res.setCharacterEncoding("UTF-8");</pre>
     * Tuttavia, le estrazioni sono destinate ad essere visualizzate
     * attraverso fogli di calcolo che, per impostazione predefinita,
     * assumono che il charset dei dati sia il latin1, non UTF-8,
     * perlomeno per la nostra utenza e per la maggior parte
     * delle piattaforme, per cui i dati, se espressi in formato UTF-8,
     * risultano codificati in maniera imprecisa, perch&eacute;, come al solito,
     * quando un dato UTF-8 viene codificato in latin1
     * (quest'ultimo anche identificato come: l1, csISOLatin1, iso-ir-100,
     * IBM819, CP819, o &ndash; ultimo ma non ultimo &ndash; ISO-8859-1)
     * i caratteri che escono al di fuori dei primi 128 caratteri,
     * che sono comuni (in quanto UTF-8 usa un solo byte per
     * codificare i primi 128 caratteri) non vengono visualizzati
     * correttamente ma vengono espressi con caratteri che in ASCII sono
     * non corrispondenti.</p>
     *
     * @param req HttpServletRequest da passare al metodo di stampa
     * @param res HttpServletResponse per impostarvi i valori che la predispongono a servire csv anziche' html
     * @param qToken token della commmand in base al quale bisogna preparare la lista di elementi
     * @throws ServletException eccezione eventualmente proveniente dalla fprinf, da propagare
     * @throws IOException  eccezione eventualmente proveniente dalla fprinf, da propagare
     */
    private static void makeCSV(HttpServletRequest req, HttpServletResponse res, String qToken)
                         throws ServletException, IOException {
        // Genera un nome univoco per il file che verrà servito
        String fileName = makeFilename(qToken);
        // Configura il response per il browser
        res.setContentType("text/x-comma-separated-values");
        // Configura il characterEncoding (v. commento)
        res.setCharacterEncoding("ISO-8859-1");
        // Configura l'header
        res.setHeader("Content-Disposition","attachment;filename=" + fileName + DOT + CSV);
        // Stampa il file sullo standard output
        fprintf(req, res);
    }


    /**
     * <p>Genera un nome univoco a partire da un prefisso dato come parametro.</p>
     *
     * @param label il prefisso che costituira' una parte del nome del file generato
     * @return <code>String</code> - il nome univoco generato
     */
    private static String makeFilename(String label) {
        // Crea un nome univoco per il file che andrà ad essere generato
        Calendar now = Calendar.getInstance();
        String fileName = ConfigManager.getLabels().get(label) + UNDERSCORE +
                          new Integer(now.get(Calendar.YEAR)).toString() + HYPHEN +
                          String.format("%02d", new Integer(now.get(Calendar.MONTH) + 1)) + HYPHEN +
                          String.format("%02d", new Integer(now.get(Calendar.DAY_OF_MONTH))) + UNDERSCORE +
                          String.format("%02d", new Integer(now.get(Calendar.HOUR_OF_DAY))) +
                          String.format("%02d", new Integer(now.get(Calendar.MINUTE))) +
                          String.format("%02d", new Integer(now.get(Calendar.SECOND)));
        return fileName;
    }


    /**
     * <p>Genera il contenuto dello stream, che questa classe tratta
     * sotto forma di file, che viene trasmesso sulla risposta in output,
     * a seconda del valore di <code>'q'</code> che riceve in input.</p>
     * <p>
     * Storicamente, in programmazione <code> C, C++ </code> e affini,
     * le funzioni che scrivono sull'outputstream si chiamano tutte
     * <code>printf</code>, precedute da vari prefissi a seconda di
     * quello che scrivono e di dove lo scrivono.<br />
     * <code>fprintf</code> &egrave; la funzione della libreria C che
     * invia output formattati allo stream, identificato con un puntatore
     * a un oggetto FILE passato come argomento
     * (<small>per approfondire,
     * <a href="http://www.tutorialspoint.com/c_standard_library/c_function_fprintf.htm">
     * v. p.es. qui</a></small>).<br />
     * Qui per analogia, pi&uacute; che altro nella forma di una "dotta"
     * citazione (per l'ambito informatico) il metodo della Data che
     * scrive il contenuto vero e proprio del file che viene passato
     * al client, viene chiamato allo stesso modo di questa "storica" funzione,
     * ma il contesto degli oggetti e degli argomenti
     * &egrave; ovviamente molto diverso.</p>
     *
     * @param req la HttpServletRequest contenente il valore di 'ent' e gli altri parametri necessari a formattare opportunamente l'output
     * @param res la HttpServletResponse utilizzata per ottenere il 'Writer' su cui stampare il contenuto, cioe' il file stesso
     * @return <code>int</code> - un valore intero restituito per motivi storici.
     *                            Tradizionalmente, tutte le funzioni della famiglia x-printf restituiscono un intero,
     *                            che vale il numero dei caratteri scritti - qui il numero delle righe scritte - in caso di successo
     *                            oppure -1 (o un altro numero negativo) in caso di fallimento
     * @throws ServletException   java.lang.Throwable.Exception.ServletException che viene sollevata se manca un parametro di configurazione considerato obbligatorio o per via di qualche altro problema di puntamento
     * @throws IOException        java.io.IOException che viene sollevata se si verifica un puntamento a null o in genere nei casi in cui nella gestione del flusso informativo di questo metodo si verifica un problema
     */
    @SuppressWarnings("unchecked")
    private static int fprintf(HttpServletRequest req, HttpServletResponse res)
                        throws ServletException, IOException {
        // Genera l'oggetto per lo standard output
        PrintWriter out = res.getWriter();
        // Tradizionalmente, ogni funzione della famiglia x-printf restituisce un intero
        int success = DEFAULT_ID;
        // Ottiene i parametri della richiesta
        ParameterParser parser = new ParameterParser(req);
        // Recupera o inizializza parametro per identificare la pagina
        String part = parser.getStringParameter("p", VOID_STRING);
        /* **************************************************************** *
         *  Gestione elaborazione contenuto CSV per interviste con risposte *
         * **************************************************************** */
        if (req.getParameter(ConfigManager.getEntToken()).equalsIgnoreCase(COMMAND_AUDIT)) {
            /* ************************************************************ *
             *    Generazione contenuto files CSV di tutte le interviste    *
             * ************************************************************ */
            if (part.equalsIgnoreCase(PART_SELECT_QSS)) {
                try {
                    // Recupera le interviste da Request
                    ArrayList<InterviewBean> list = (ArrayList<InterviewBean>) req.getAttribute("lista");
                    // Ottiene struttura contenente il numero di quesiti per ciascuna rilevazione
                    ConcurrentHashMap<String, Integer> questionAmounts = ConfigManager.getQuestionAmount();
                    // Ottiene il numero di quesiti della prima rilevazione (al massimo le colonne rimanenti resteranno senza intestazione)
                    int questionsAmount = (questionAmounts.get(list.get(NOTHING).getDescrizione()).intValue());
                    // Scrittura file CSV
                    StringBuffer headers = new StringBuffer("N.Intervista" + SEPARATOR)
                            .append("N.Quesiti").append(SEPARATOR)
                            .append("N.Quesiti con Risposta").append(SEPARATOR)
                            .append("Data").append(SEPARATOR)
                            .append("Ora").append(SEPARATOR)
                            .append("Macroprocesso").append(SEPARATOR)
                            .append("Processo").append(SEPARATOR)
                            .append("Sottoprocesso").append(SEPARATOR)
                            .append("Ramo Strutture").append(SEPARATOR)
                            .append("Struttura").append(SEPARATOR)
                            .append("Sottostruttura").append(SEPARATOR)
                            .append("Ufficio").append(SEPARATOR);
                    for (int i = 0; i < questionsAmount; i++) {
                            /*headers.append("Ambito").append(SEPARATOR);*/
                            headers.append("Quesito").append(SEPARATOR);
                            headers.append("Risposta").append(SEPARATOR);
                    }
                    out.println(headers);
                    if (list.size() > NOTHING) {
                        int itCounts = NOTHING, record = NOTHING;
                        do {
                            InterviewBean iw = list.get(itCounts);
                            String processo = (iw.getProcesso().getProcessi() != null ? iw.getProcesso().getProcessi().get(0).getNome() : VOID_STRING);
                            String subprocesso = (iw.getProcesso().getProcessi().get(0).getProcessi() != null ? iw.getProcesso().getProcessi().get(0).getProcessi().get(0).getNome() : VOID_STRING);
                            String strutturaL3 = (iw.getStruttura().getFiglie().get(0).getFiglie() != null ? iw.getStruttura().getFiglie().get(0).getFiglie().get(0).getNome() : VOID_STRING);
                            // Puo' cercare una struttura di livello 4 se esiste una struttura di livello 3, cosa che non è detta
                            String strutturaL4 = null;  // NON si inizializza a VOID_STRING perché String è immutabile!
                            if (!strutturaL3.equals(VOID_STRING)) {
                                strutturaL4 = (iw.getStruttura().getFiglie().get(0).getFiglie().get(0).getFiglie() != null ? iw.getStruttura().getFiglie().get(0).getFiglie().get(0).getFiglie().get(0).getNome() : VOID_STRING);    
                            } else {
                                strutturaL4 = VOID_STRING;
                            }
                            StringBuffer tupla = new StringBuffer(++record + SEPARATOR)
                                .append(iw.getRisposte().size()).append(SEPARATOR)
                                .append(iw.getInformativa()).append(SEPARATOR)
                                .append(iw.getDataUltimaModifica()).append(BLANK_SPACE).append(SEPARATOR)
                                .append(iw.getOraUltimaModifica()).append(SEPARATOR)
                                .append(iw.getProcesso().getNome()).append(SEPARATOR)
                                .append(processo).append(SEPARATOR)
                                .append(subprocesso).append(SEPARATOR)
                                .append(iw.getStruttura().getNome()).append(SEPARATOR)
                                .append(iw.getStruttura().getFiglie().get(0).getNome()).append(SEPARATOR)
                                .append(strutturaL3).append(SEPARATOR)
                                .append(strutturaL4).append(SEPARATOR);
                            for (QuestionBean aw : iw.getRisposte()) {
                                /*if (aw.getAmbito() != null) {
                                    tupla.append(aw.getAmbito().getNome()).append(SEPARATOR);
                                }*/
                                if (aw.getFormulazione() != null) {
                                    tupla.append(aw.getFormulazione().replace(SEMICOLON, HYPHEN)).append(SEPARATOR);
                                }
                                if (aw.getAnswer().getNome() != null) {
                                    tupla.append(aw.getAnswer().getNome().replace(SEMICOLON, HYPHEN)).append(SEPARATOR);
                                }
                            }
                            out.println(String.valueOf(tupla));
                            itCounts++;
                        } while (itCounts < list.size());
                        success = itCounts;
                    }
                } catch (RuntimeException re) {
                    log.severe(FOR_NAME + "Si e\' verificato un problema nella scrittura del file che contiene l\'elenco delle strutture collegate ai macroprocessi.\n" + re.getMessage());
                    out.println(re.getMessage());
                } catch (Exception e) {
                    log.severe(FOR_NAME + "Problema nella fprintf di Data" + e.getMessage());
                    out.println(e.getMessage());
                }
            }
            /* ************************************************************ *
             *   Generazione contenuto files CSV di una singola intervista  *
             * ************************************************************ */
            else if (part.equalsIgnoreCase(PART_RESUME_QST)) {
                try {
                    // Recupera le interviste da Request
                    ArrayList<InterviewBean> list = (ArrayList<InterviewBean>) req.getAttribute("lista");
                    // Bisogna recuperare una singola intervista (incapsulata in un elenco di uno)
                    InterviewBean interview = list.get(NOTHING);
                    // Scrittura file CSV
                    StringBuffer headers = new StringBuffer("Intervista del: ")
                            .append(interview.getData())
                            .append(" alle ore: ").append(interview.getOraUltimaModifica()).append(SEPARATOR)
                            .append("Ambito").append(SEPARATOR)
                            .append("Quesito").append(SEPARATOR)
                            .append("Risposta").append(SEPARATOR)
                            .append("Annotazioni").append(SEPARATOR);
                    out.println(headers);
                    if (interview.getRisposte().size() > NOTHING) {
                        int itCounts = NOTHING, record = NOTHING;
                        do {
                            QuestionBean aw = interview.getRisposte().get(itCounts); 
                            StringBuffer tupla = new StringBuffer("Domanda ")
                                .append(++record).append(SEPARATOR)
                                .append(aw.getAmbito().getNome()).append(SEPARATOR)
                                .append(aw.getFormulazione().replace(SEMICOLON, HYPHEN)).append(SEPARATOR)
                                .append(aw.getAnswer().getNome().replace(SEMICOLON, HYPHEN)).append(SEPARATOR)
                                .append(aw.getAnswer().getInformativa().replace(SEMICOLON, HYPHEN)).append(SEPARATOR);
                            out.println(String.valueOf(tupla));
                            itCounts++;
                        } while (itCounts < interview.getRisposte().size());
                        success = itCounts;
                    }
                } catch (RuntimeException re) {
                    log.severe(FOR_NAME + "Si e\' verificato un problema nella scrittura del file che contiene l\'elenco delle strutture collegate ai macroprocessi.\n" + re.getMessage());
                    out.println(re.getMessage());
                } catch (Exception e) {
                    log.severe(FOR_NAME + "Problema nella fprintf di Data" + e.getMessage());
                    out.println(e.getMessage());
                }
            }
        }
        /* **************************************************************** *
         *   Gestione elaborazione contenuto CSV per registro dei rischi    *
         * **************************************************************** */
        else if (req.getParameter(ConfigManager.getEntToken()).equalsIgnoreCase(COMMAND_RISK)) {
            try {
                // Recupera i macro at da Request
                ArrayList<RiskBean> list = (ArrayList<RiskBean>) req.getAttribute("lista");
                // Scrittura file CSV
                StringBuffer headers = new StringBuffer()
                        .append("Rischio").append(SEPARATOR)
                        //.append("N. Processi esposti").append(SEPARATOR)
                        .append("Codice Macroprocesso").append(SEPARATOR)
                        .append("Macroprocesso").append(SEPARATOR)
                        .append("Codice Processo").append(SEPARATOR)
                        .append("Processo").append(SEPARATOR);
                out.println(headers);
                if (list.size() > NOTHING) {
                    int itCounts = NOTHING;
                    do {
                        RiskBean item = list.get(itCounts);
                        // Stampa rischi che hanno processi associati
                        for (ProcessBean pat : item.getProcessi()) {
                            String labelR = item.getNome().replace(SEMICOLON, BLANK_SPACE).replace(ENGLISH_SINGLE_QUOTE, APOSTROPHE);
                            StringBuffer tupla = new StringBuffer()
                                .append(labelR).append(SEPARATOR)
                                .append(pat.getPadre().getCodice()).append(SEPARATOR)
                                .append(pat.getPadre().getNome()).append(SEPARATOR)
                                .append(pat.getCodice()).append(SEPARATOR)
                                .append(pat.getNome()).append(SEPARATOR);
                            out.println(String.valueOf(tupla));
                        }
                        itCounts++;
                    } while (itCounts < list.size());
                    success = itCounts;
                }
            } catch (RuntimeException re) {
                log.severe(FOR_NAME + "Si e\' verificato un problema nella scrittura del file che contiene l\'elenco delle strutture collegate ai macroprocessi.\n" + re.getMessage());
                out.println(re.getMessage());
            } catch (Exception e) {
                log.severe(FOR_NAME + "Problema nella fprintf di Data" + e.getMessage());
                out.println(e.getMessage());
            }
        }
        /* **************************************************************** *
         *  Gestione elaborazione contenuto CSV per processi anticorruttivi *
         * **************************************************************** */
        else if (req.getParameter(ConfigManager.getEntToken()).equalsIgnoreCase(COMMAND_PROCESS)) {
            /* ************************************************************ *
             * Generazione contenuto files CSV di uno o tutti i processi at *
             * ************************************************************ */
            try {
                // Recupera i macro at da Request
                ArrayList<ItemBean> list = (ArrayList<ItemBean>) req.getAttribute("listaProcessi");
                // Scrittura file CSV
                StringBuffer headers = new StringBuffer("N." + SEPARATOR)
                        .append("Codice Area Rischio").append(SEPARATOR)
                        .append("Area Rischio").append(SEPARATOR)
                        .append("Codice Macroprocesso").append(SEPARATOR)
                        .append("Macroprocesso").append(SEPARATOR)
                        .append("Codice Processo").append(SEPARATOR)
                        .append("Processo").append(SEPARATOR)
                        .append("N. Input Processo").append(SEPARATOR)
                        .append("N. Fasi Processo").append(SEPARATOR)
                        .append("N. Output Processo").append(SEPARATOR)                           
                        .append("Input Processo").append(SEPARATOR)
                        .append("Fase Processo").append(SEPARATOR)
                        .append("Output Processo").append(SEPARATOR)
                        .append("Codice Sottoprocesso").append(SEPARATOR)
                        .append("Sottoprocesso").append(SEPARATOR);
                out.println(headers);
                if (list.size() > NOTHING) {
                    int itCounts = NOTHING, record = NOTHING;
                    do {
                        ItemBean item = list.get(itCounts);
                        StringBuffer tupla = new StringBuffer(++record + SEPARATOR)
                            .append(item.getCodice()).append(SEPARATOR)
                            .append(item.getNome()).append(SEPARATOR)
                            .append(item.getInformativa()).append(SEPARATOR)
                            .append(item.getNomeReale()).append(SEPARATOR)
                            .append(item.getUrl()).append(SEPARATOR)
                            .append(item.getLabelWeb()).append(SEPARATOR)
                            .append(item.getCod1()).append(SEPARATOR)
                            .append(item.getCod2()).append(SEPARATOR)
                            .append(item.getCod3()).append(SEPARATOR);
                        String input = !(item.getExtraInfo1().equals(item.getExtraInfo2())) ? item.getExtraInfo1() : VOID_STRING;
                        tupla
                            .append(input).append(SEPARATOR)
                            .append(item.getExtraInfo3()).append(SEPARATOR)
                            .append(item.getExtraInfo4()).append(SEPARATOR)
                            .append(item.getIcona()).append(SEPARATOR)
                            .append(item.getExtraInfo()).append(SEPARATOR);
                        out.println(String.valueOf(tupla));
                        itCounts++;
                    } while (itCounts < list.size());
                    success = itCounts;
                }
            } catch (RuntimeException re) {
                log.severe(FOR_NAME + "Si e\' verificato un problema nella scrittura del file che contiene l\'elenco dei macroprocessi.\n" + re.getMessage());
                out.println(re.getMessage());
            } catch (Exception e) {
                log.severe(FOR_NAME + "Problema nella fprintf di Data" + e.getMessage());
                out.println(e.getMessage());
            }
        }
        /* **************************************************************** *
         *        Contenuto files CSV per strutture in organigramma         *
         * **************************************************************** */
        else if (req.getParameter(ConfigManager.getEntToken()).equalsIgnoreCase(COMMAND_STRUCTURES)) {
            try {
                ArrayList<ItemBean> l = (ArrayList<ItemBean>) req.getAttribute("listaOrganigramma");
                // Scrittura file CSV
                out.println("N." + SEPARATOR +
                            "Struttura liv.1" + SEPARATOR +
                            "Struttura liv.2" + SEPARATOR +
                            "Struttura liv.3" + SEPARATOR +
                            "Struttura liv.4"
                            );
                if (l.size() > NOTHING) {
                    int itCounts = NOTHING, record = NOTHING;
                    do {
                        ItemBean tupla = l.get(itCounts);
                        String label_l2, label_l3, label_l4 = null;
                        String uo_l2 = tupla.getExtraInfo();
                        String uo_l3 = tupla.getLabelWeb();
                        String uo_l4 = tupla.getPaginaJsp();
                        if (uo_l4.equals(uo_l3)) {
                            label_l4 = VOID_STRING;
                        } else {
                            label_l4 = uo_l4.replace(';', ' ');
                        }
                        out.println(
                                    ++record + SEPARATOR +
                                    tupla.getInformativa() + " " + SEPARATOR +
                                    uo_l2.replace(';', ' ') + " " + SEPARATOR +
                                    uo_l3.replace(';', ' ') + " " + SEPARATOR +
                                    label_l4
                                   );
                        itCounts++;
                    } while (itCounts < l.size());
                    success = itCounts;
                }
            } catch (RuntimeException re) {
                log.severe(FOR_NAME + "Si e\' verificato un problema nella scrittura del file che contiene l\'elenco delle strutture in organigramma.\n" + re.getMessage());
                out.println(re.getMessage());
            } catch (Exception e) {
                log.severe(FOR_NAME + "Problema nella fprintf di Data" + e.getMessage());
                out.println(e.getMessage());
            }
        }
        else {
            String msg = FOR_NAME + "La Servlet Data non accetta la stringa passata come valore di 'ent': " + req.getParameter(ConfigManager.getEntToken());
            log.severe(msg + "Tentativo di indirizzare alla Servlet Data una richiesta non gestita. Hacking test?\n");
            throw new IOException(msg);
        }
        return success;
    }

    
    /**
     * <p>Genera il nodo JSON</p>
     *
     * @param tipo          valore che serve a differenziare tra tipi diversi di nodi per poter applicare formattazioni o attributi diversi
     * @param codice        codice del nodo corrente
     * @param codicePadre   codice del nodo padre del nodo corrente
     * @param nome          etichetta del nodo
     * @param descr         descrizione del nodo
     * @param lbl1          label aggiuntiva
     * @param txt1          testo relativo alla label
     * @param lbl2          label aggiuntiva
     * @param txt2          testo relativo alla label
     * @param bgColor       parametro opzionale specificante il colore dei box/nodi in formato esadecimale
     * @param icona         parametro opzionale specificante il nome del file da mostrare come stereotipo
     * @param livello       livello gerarchico del nodo
     * @return <code>String</code> - il nodo in formato String
     */
    public static String getStructureJsonNode(String tipo,
                                              String codice,
                                              String codicePadre,
                                              String nome,
                                              String descr,
                                              String lbl1,
                                              String txt1,
                                              String lbl2,
                                              String txt2,
                                              String bgColor,
                                              String icona,
                                              int livello) {
        /* ------------------------ *
         *   Controlli sull'input   *
         * ------------------------ */
        String codiceGest = (codicePadre == null ? "null" : "\"" + codicePadre + "\"");
        String nodeImage = (icona == null ? "logo2.gif" : icona + livello + ".png");
        String height =  (descr.length() > 100) ? String.valueOf(descr.length()) : String.valueOf(146);
        Color backgroundColor = null;
        if (bgColor != null && !bgColor.equals(VOID_STRING)) {
            backgroundColor = Color.decode(bgColor);
        } else {
            backgroundColor = new Color(51,182,208);
        }
        /* ------------------------ */
        // Generazione nodo
        return "{\"nodeId\":\"" + codice + "\"," +
                "  \"parentNodeId\":" + codiceGest + "," +
                "  \"width\":342," +
                "  \"height\":" + height +"," +
                "  \"borderWidth\":1," +
                "  \"borderRadius\":5," +
                "  \"borderColor\":{\"red\":15,\"green\":140,\"blue\":121,\"alpha\":1}," +
                "  \"backgroundColor\":{\"red\":" + backgroundColor.getRed() + ",\"green\":" + backgroundColor.getGreen() + ",\"blue\":" + backgroundColor.getBlue() + ",\"alpha\":1}," +
                "  \"nodeImage\":{\"url\":\"web/img/" + nodeImage + "\",\"width\":50,\"height\":50,\"centerTopDistance\":0,\"centerLeftDistance\":0,\"cornerShape\":\"CIRCLE\",\"shadow\":false,\"borderWidth\":0,\"borderColor\":{\"red\":19,\"green\":123,\"blue\":128,\"alpha\":1}}," +
                "  \"nodeIcon\":{\"icon\":\"\",\"size\":30}," +
                "  \"template\":\"<div>\\n <div style=\\\"margin-left:15px;\\n margin-right:15px;\\n text-align: center;\\n margin-top:10px;\\n font-size:20px;\\n font-weight:bold;\\n \\\">" + nome + "</div>\\n <div style=\\\"margin-left:80px;\\n margin-right:15px;\\n margin-top:3px;\\n font-size:16px;\\n \\\">" + descr + "</div>\\n\\n <div style=\\\"margin-left:270px;\\n  margin-top:15px;\\n  font-size:13px;\\n  position:absolute;\\n  bottom:5px;\\n \\\">\\n<div>" + lbl1 + " " + txt1 +"</div>\\n<div style=\\\"margin-top:5px\\\">" + lbl2 + " " + txt2 + "</div>\\n</div>     </div>\"," +
                "  \"connectorLineColor\":{\"red\":220,\"green\":189,\"blue\":207,\"alpha\":1}," +
                "  \"connectorLineWidth\":5," +
              //"  \"dashArray\":\"\"," +
                "  \"expanded\":false }";
    }
    
    
    /**
     * <p>Genera la descrizione del nodo JSON</p>
     *
     * @param list          struttura vettoriale contenente informazioni
     * @param livello       livello gerarchico del nodo
     * @return <code>String</code> - il nodo in formato String
     * @throws AttributoNonValorizzatoException eccezione che viene propagata se si tenta di accedere a un dato obbligatorio non valorizzato del bean
     */
    public static String makeDescrJsonNode(ArrayList<?> list,
                                           int livello) 
                                    throws AttributoNonValorizzatoException {
        StringBuffer descr = new StringBuffer();
        descr.append("<ul>");
        for (int i = 0; i < list.size(); i++) {
            PersonBean p = (PersonBean) list.get(i);
            descr.append("<li>");
            descr.append(p.getNome());
            descr.append(BLANK_SPACE);
            descr.append(p.getCognome());
            descr.append(BLANK_SPACE + DASH + BLANK_SPACE);
            descr.append(p.getNote());
            descr.append("</li>");
        }
        descr.append("</ul>");
        // Generazione descr
        return descr.toString();
    }

}
