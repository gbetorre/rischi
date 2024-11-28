/*
 *   Rischi On Line (ROL-RMS), Applicazione web: 
 *   - per la gestione di sondaggi inerenti al rischio corruttivo 
 *   cui i processi organizzativi di una PA possono essere esposti, 
 *   - per la produzione di mappature e reportistica finalizzate 
 *   alla valutazione del rischio corruttivo nella pubblica amministrazione, 
 *   - per ottenere suggerimenti riguardo le misure di mitigazione 
 *   che possono calmierare specifici rischi 
 *   - e per effettuare il monitoraggio al fine di verificare quali misure
 *   proposte sono state effettivamente attuate dai soggetti interessati
 *   alla gestione dei processi a rischio.
 *
 *   Risk Mapping and Management Software (ROL-RMS),
 *   web application: 
 *   - to assess the amount and type of corruption risk to which each organizational process is exposed, 
 *   - to publish and manage, reports and information on risk
 *   - and to propose mitigation measures specifically aimed at reducing risk, 
 *   - also allowing monitoring to be carried out to see 
 *   which proposed mitigation measures were then actually implemented.
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

package it.rol;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
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
import it.rol.command.ReportCommand;
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
 * <p><ul><li>Nel primo caso, questa servlet fa a meno, legittimamente, 
 * del design (View), in quanto l'output prodotto consiste in pure tuple 
 * prive di presentazione (potenzialmente: fileset CSV, formati XML, dati 
 * con o senza metadati, serialization formats, JSON, <em>and so on</em>), 
 * oppure in output gi&agrave; autoformattati (p.es. testo formattato 
 * Rich Text Format).</li>
 * <li>Nel secondo caso, la servlet specifica una pagina di output, contenente
 * la formattazione, che per&ograve; verr&agrave; invocata asincronamente.</li>
 * </ul></p>
 * <p>Questa servlet estrae l'azione dall'URL, ne verifica la
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
 * sono deprecate da tempo e vanno assolutamente evitate 
 * in favore dell'uso di questa servlet.
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
    private static final String FOR_NAME = "\n" + Logger.getLogger(Data.class.getName()) + COLON + BLANK_SPACE;
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
     * Pagina a cui la classe inoltra per mostrare le fasi nel contesto dei processi
     */
    private static final String nomeFileProcessoAjax = "/jsp/prProcessoAjax.jsp";
    /**
     * Pagina a cui la classe inoltra per mostrare le fasi nel contesto dei processi
     */
    private static final String nomeFileLog = "/jsp/diff.jsp";
    /**
     * Codifica esadecimale di un'immagine di corredo
     */
    private static final String CAUTION_EXCLAMATION_MARK_SIGN_TRIANGLE = 
        "0100090000034a04000000002104000000000400000003010800050000000b0200000000050000\n" + 
        "000c0220002000030000001e000400000007010400040000000701040021040000410b2000cc00\n" + 
        "200020000000000020002000000000002800000020000000200000000100080000000000000000\n" + 
        "000000000000000000000000000000000000000000ffffff00fcfbfb00eae9e900e7e6e5007c77\n" + 
        "7200403a3300f6f6f6006e696300201810002e261f002d261e00b1aeac00261e17003f393200c8\n" + 
        "c6c400c5c3c100b6b3b000efefee0058524c0099959100989490005b554f00f0f0ef00a4a19e00\n" + 
        "211911004e484200e8e7e7004d464000221a1200a9a6a3004a433d00aaa7a4004b443e0094908d\n" + 
        "005b56500057514b00dddcda00271f1700bab7b500f2f1f100f3f2f200b3b0ad00fefefe008682\n" + 
        "7e006b666100f7f7f7006a656000f5f5f500645f5900d0cecc00352e2600e5e4e30038312a00c0\n" + 
        "bebc002b241c00d1cfcd0075706b0078736f00fbfafa00726d680079747000fbfbfb00c3c0be00\n" + 
        "37302800d3d2d000cecccb00332c250089858000fdfdfd00807c770069635e00d9d8d7003c352e\n" + 
        "00b7b5b20059534d008d898500a6a39f004d474100e3e2e100463f3800e7e7e6004c453f009c99\n" + 
        "950095918e006c676200f4f3f300ecebea00524c4500dedddb00413a3400b9b7b400231b130069\n" + 
        "645f00eeedec00eeeeed005d57520087837f00362f2700c4c2c000b0adab0048423b0049433c00\n" + 
        "28201800d2d0ce0077736e00edeceb00f8f8f8007a7571002f272000c7c5c300fafaf9006d6862\n" + 
        "0088848000b6b4b10076716c00716c6700e4e3e20030282100d5d4d200cbc9c70029211900adaa\n" + 
        "a700e6e5e400231c14002a221a009a96920096928f0047413a0000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000010101010101010101010101\n" + 
        "010101010101010101010101010101010101010101012b5e343434343434343434343434343434\n" + 
        "3434343434343434345f2b010101567f8035353535353535353535353535353535353535353535\n" + 
        "35356614560101225c7d353535353535353535353535353535353535353535353535265c7e0157\n" + 
        "21797a04343434343434343434347b7b343434343434343434347b537c5257750e767701010101\n" + 
        "0101010101012b5f122b0101010101010101010178370e756f7009712b0101010101010101012a\n" + 
        "661f72010101010101010101027309746f01630a626801010101010101015e210909215e010101\n" + 
        "0101010101106d0a6e01010239096902010101010101016a1f09091f6a010101010101016b2d09\n" + 
        "6c0201010138620b6301010101010101016465662a010101010101010127676268010101012b2c\n" + 
        "095d07010101010101012b5e5f2b01010101010101286009612b0101010101595a265b01010101\n" + 
        "0101010156560101010101010101205c5a590101010101010154091617010101010101292d5556\n" + 
        "01010101010157580914010101010101010151521d1e0101010101013435353401010101010153\n" + 
        "091c510101010101010101014d1d4e1b01010101013435353401010101014f501d200101010101\n" + 
        "0101010101174b091401010101013435353401010101014c09231701010101010101010101012a\n" + 
        "0d06250101010134353534010101014849264a0101010101010101010101010731094401010101\n" + 
        "343535340101014546094707010101010101010101010101013f0b404101010134353534010101\n" + 
        "42430a1001010101010101010101010101010239093a0201013435353401013b3c093d3e010101\n" + 
        "01010101010101010101010132330b100101343535340101363733380101010101010101010101\n" + 
        "01010101012b2c092d2e01292f2d29013031092c2b010101010101010101010101010101010125\n" + 
        "0626270101282901012a0d06250101010101010101010101010101010101010122092317010101\n" + 
        "01122409140101010101010101010101010101010101010101041f1d2001010101181921040101\n" + 
        "0101010101010101010101010101010101010118191a1b01011b1c1d1e01010101010101010101\n" + 
        "010101010101010101010101121309140101150916170101010101010101010101010101010101\n" + 
        "0101010101010c0d0e0f100e0d1101010101010101010101010101010101010101010101010107\n" + 
        "08090a0b0908070101010101010101010101010101010101010101010101010104050606050401\n" + 
        "010101010101010101010101010101010101010101010101010102030302010101010101010101\n" + 
        "010101010101010101010101010101010101010101010101010101010101010101010101010400\n" + 
        "00002701ffff030000000000\n";


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
        // Dictonary contenente i soli valori di entToken abilitati a generare CSV
        LinkedList<String> csvCommands = new LinkedList<>();
        // Struttura da restituire in Request
        AbstractList<?> lista = null;
        // Mappa da restituire in Request
        AbstractMap<?,?> mappa = null;
        // Message
        log.info("===> Log su servlet Data. <===");
        // Decodifica la richiesta
        try {
            // Lista delle Command abilitate a servire un output csv
            csvCommands.add(COMMAND_PROCESS);   //Estrazione processi   ("data?q=pr")
            csvCommands.add(COMMAND_STRUCTURES);//Estrazione strutture  ("data?q=st")
            csvCommands.add(COMMAND_AUDIT);     //Estrazione interviste ("data?q=in")
            csvCommands.add(COMMAND_RISK);      //Estrazione rischi     ("data?q=ri")
            csvCommands.add(COMMAND_REPORT);    //Estrazione rischi     ("data?q=mu")
            // Verifica se deve servire un output su file
            if (format != null && !format.isEmpty()) {
                // Output è Comma Separated Values
                if (format.equalsIgnoreCase(CSV)) {
                    // Verifica che la Command invocata sia abilitata a gestire output csv
                    if (csvCommands.contains(qToken)) {
                        // Recupero elementi in base alla richiesta
                        lista = retrieve(req, qToken);
                        // Passaggio in request per uso delle lista
                        req.setAttribute("lista", lista);
                        // Assegnazione di un valore di default se il parametro 'p' è nullo
                        String key = (part == null ? qToken : part);
                        // Genera il file CSV
                        makeCSV(req, res, key);
                        // Ha finito
                        return;
                    }
                }
                // Output è Rich Text Format
                else if (format.equalsIgnoreCase(RTF)) {
                    // Controlla che la command su cui si invoca la funzione sia quella abilitata
                    if (qToken.equalsIgnoreCase(COMMAND_REPORT)) {
                        // Recupero elementi in base alla richiesta
                        mappa = retrieve(req, qToken, part, format);
                        // Passaggio in request per uso delle lista
                        req.setAttribute("lista", mappa);
                        // Genera il file RTF
                        makeRTF(req, res, qToken);
                        // Ha finito
                        return;
                    }
                }
                // Output è HyperText Markup Language
                else if (format.equalsIgnoreCase(HTML)) {
                    // Al momento l'unica Command abilitata a gestire output html
                    if (qToken.equalsIgnoreCase(COMMAND_REPORT)) {
                        // Recupero elementi in base alla richiesta
                        mappa = retrieve(req, qToken, part, format);
                        // Passaggio in request per uso delle lista
                        req.setAttribute("lista", mappa);
                        // Genera il file RTF
                        makeHTML(req, res, part);
                        // Non ha finito: deve invocare la pagina dinamica 
                        // (che a sua volta fornirà l'output per l'html statico scaricato)
                    }
                }
                // Output è in finestra di popup
                else {
                    // Ultimo valore ammesso: pop
                    if (!format.equalsIgnoreCase("pop")) { 
                        // Valore del parametro 'out' non ammesso
                        String msg = FOR_NAME + "Valore del parametro \'out\' (" + format + ") non consentito. Impossibile visualizzare i risultati.\n";
                        log.severe(msg);
                        throw new ServletException(msg);
                    }
                }
            }
            // Se non è uscito, vuol dire che deve servire una richiesta asincrona...
            if (qToken.equalsIgnoreCase(COMMAND_PROCESS)) { //dettaglio processo via XHR ("data?q=pr&p=pro&pliv=#&liv=#&r=$")
                // Recupera le liste di elementi collegati al processo 
                ConcurrentHashMap<String, ArrayList<?>> processElements = new ConcurrentHashMap<>();            
                // Recupera gli indicatori corredati di note e valorizza per riferimento le altre liste
                HashMap<String, InterviewBean> indicators = retrieve(req, COMMAND_PROCESS, PART_PROCESS, processElements);
                // Imposta nella request liste di elementi collegati a processo
                req.setAttribute("listaInput", processElements.get(TIPI_LISTE[0]));
                req.setAttribute("listaFasi", processElements.get(TIPI_LISTE[1]));
                req.setAttribute("listaOutput", processElements.get(TIPI_LISTE[2]));
                req.setAttribute("listaRischi", processElements.get(TIPI_LISTE[3]));
                req.setAttribute("listaInterviste", processElements.get(TIPI_LISTE[4]));
                req.setAttribute("listaIndicatori", indicators);
                // Output in formato di default
                fileJsp = nomeFileProcessoAjax;
            //} else if (qToken.equalsIgnoreCase(COMMAND_MEASURE)) {
                //req.setAttribute("XHRResp", "test");
                //fileJsp = "/jsp/msMisuraForm.jsp";
                //res.setContentType("text/plain");
                //res.getWriter().write("test test");
                //return;
            // ...oppure deve invocare direttamente una pagina che genererà l'output per un file
            } else if (qToken.equalsIgnoreCase(COMMAND_REPORT)) {
                fileJsp = nomeFileLog;
            } else {
                String msg = FOR_NAME + "Valore del parametro \'q\' (" + qToken + ") non consentito. Impossibile visualizzare i risultati.\n";
                log.severe(msg);
                throw new ServletException(msg);
            }
        } catch (CommandException ce) {
            throw new ServletException(FOR_NAME + "Problema nel recupero dei dati richiesti.\n" + ce.getMessage(), ce);
        }
        // Forworda la richiesta, esito finale di tutto
        RequestDispatcher dispatcher = servletContext.getRequestDispatcher(fileJsp);
        dispatcher.forward(req, res);
    }

    /* **************************************************************** *
     *       Metodi per generare tuple prive di presentazione (CSV)     *
     * **************************************************************** */
    
    /**
     * <p>Restituisce un elenco generico di elementi 
     * (interviste, macroprocessi, strutture...)
     * relativi a una richiesta specifica. 
     * I dati ottenuti tramite questo metodo sono generalmente pensati 
     * per essere forniti in output sotto forma di tuple con ridotta presentazione
     * (elenchi csv, log html).</p>
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
                    ArrayList<InterviewBean> interviews = db.getInterviewsBySurvey(user, ConfigManager.getSurvey(codeSurvey));
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
                    // Scarta le domande con risposte vuote
                    answers = AuditCommand.filter(answers);
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
             // "data?q=mu"    
            } else if (qToken.equalsIgnoreCase(COMMAND_REPORT)) {
             // "&p=mes"
                if (part.equalsIgnoreCase(PART_MEASURES)) {
                    // Fa la query della navigazione per reportistica
                    ArrayList<ProcessBean> matsWithoutIndicators = ProcessCommand.retrieveMacroAtBySurvey(user, codeSurvey, db);
                    
                    ArrayList<ProcessBean> matsWithIndicators = ReportCommand.retrieveIndicators(matsWithoutIndicators, user, codeSurvey, NOTHING, db);
                    // Recupera i rischi indicizzati per identificativo di processo
                    LinkedHashMap<ProcessBean, ArrayList<RiskBean>> risks = ReportCommand.retrieveMitigatedRisksByProcess(matsWithIndicators, user, codeSurvey, db);
                    // Restituisce la lista gerarchica di strutture trovate in base alla rilevazione
                    java.util.Set listAsSet = risks.keySet();
                    list = new ArrayList<ProcessBean>(listAsSet);
                }
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
    
    /* **************************************************************** *
     *  Metodi per generare tuple con qualche presentazione (RTF, HTML) *
     * **************************************************************** */
    
    /**
     * <p>Restituisce una mappa contenente elenchi di elementi generici 
     * (macroprocessi, rischi, strutture...) estratti in base alla richiesta
     * e indicizzati per una chiave convenzionale, definita nelle costanti.
     * I dati ottenuti tramite questo metodo sono generalmente pensati 
     * per alimentare un report formattato (RTF o HTML).</p>
     *
     * @param req       HttpServletRequest contenente i parametri per contestualizzare l'estrazione
     * @param qToken    il token della commmand in base al quale bisogna preparare la lista di elementi
     * @param pToken    il token relativo alla parte di gestione da effettuare
     * @param out       parametro specificante il tipo di output richiesto
     * @return <code>HashMap&lt;String, HashMap&lt;Integer, ?&gt;&gt; - dictionary contenente le liste di elementi desiderati, indicizzati per una chiave convenzionale
     * @throws CommandException se si verifica un problema nella WebStorage (DBWrapper), nella Command interpellata, o in qualche puntamento
     */
    private static HashMap<String, HashMap<Integer, ?>> retrieve(HttpServletRequest req,
                                                                 String qToken,
                                                                 String pToken,
                                                                 String out)
                                                          throws CommandException {
        // Dichiara generico elenco di elementi da restituire
        HashMap<String, HashMap<Integer, ?>> list = null;
        // Ottiene i parametri della richiesta
        ParameterParser parser = new ParameterParser(req);
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
            // "data?q=mu"
            if (qToken.equalsIgnoreCase(COMMAND_REPORT)) {
                // "&p=str"
                if (pToken.equalsIgnoreCase(PART_SELECT_STR)) {
                    // "&out=rtf"
                    if (out.equalsIgnoreCase(RTF)) {
                        // Bisogna recuperare processi...
                        ArrayList<ProcessBean> matsWithoutIndicators = ProcessCommand.retrieveMacroAtBySurvey(user, codeSurvey, db);
                        // ...e indicatori
                        ArrayList<ProcessBean> matsWithIndicators = ReportCommand.retrieveIndicators(matsWithoutIndicators, user, codeSurvey, NOTHING, db);
                        // Trasforma processi e indicatori in una HashMap per rispettare il tipo
                        HashMap<Integer, ArrayList<ProcessBean>> listaMacroAt = new HashMap<>();
                        // Carica processi e indicatori
                        listaMacroAt.put(new Integer(NOTHING), matsWithIndicators);
                        // Recupera le strutture indicizzate per identificativo di processo
                        HashMap<Integer, ArrayList<DepartmentBean>> listaStrutture = ReportCommand.retrieveStructures(matsWithoutIndicators, user, codeSurvey, db);
                        // Recupera i soggetti indicizzati per identificativo di processo
                        HashMap<Integer, ArrayList<DepartmentBean>> listaSoggetti = ReportCommand.retrieveSubjects(matsWithoutIndicators, user, codeSurvey, db);
                        // Recupera i rischi indicizzati per identificativo di processo
                        HashMap<Integer, ArrayList<RiskBean>> listaRischi = ReportCommand.retrieveRisks(matsWithoutIndicators, user, codeSurvey, db);
                        // Istanzia la mappa in cui devono essere settate le liste
                        list = new HashMap<>();
                        // Imposta nella mappa le liste trovate
                        list.put(TIPI_LISTE[3], listaRischi);
                        list.put(TIPI_LISTE[5], listaMacroAt);
                        list.put(TIPI_LISTE[6], listaStrutture);
                        list.put(TIPI_LISTE[7], listaSoggetti);
                    // "&out=html"
                    } else if (out.equalsIgnoreCase(HTML)) {
                        // Bisogna recuperare processi...
                        ArrayList<ProcessBean> matsWithoutIndicators = ProcessCommand.retrieveMacroAtBySurvey(user, codeSurvey, db);
                        // Per avere due oggetti realmente distinti deve per forza rifare la query
                        ArrayList<ProcessBean> matsWithoutIndicators2 = ProcessCommand.retrieveMacroAtBySurvey(user, codeSurvey, db);
                        // ...e indicatori cached
                        ArrayList<ProcessBean> matsCached = ReportCommand.retrieveIndicators(matsWithoutIndicators, user, codeSurvey, NOTHING, db);
                        // Bisogna anche calcolare gli indicatori a runtime
                        ArrayList<ProcessBean> matsRuntime = ReportCommand.computeIndicators(matsWithoutIndicators2, user, codeSurvey, db);                     // <- to prod
                        //ArrayList<ProcessBean> matsRuntime = ReportCommand.retrieveIndicators(matsWithoutIndicators2, user, codeSurvey, ELEMENT_LEV_1, db);   // <- to test
                        list = new HashMap<>(); // Mappa in cui devono essere settate le liste
                        HashMap<Integer, LinkedHashMap<String, InterviewBean>> listaVecchi = new HashMap<>();
                        HashMap<Integer, LinkedHashMap<String, InterviewBean>> listaNuovi = new HashMap<>();
                        ArrayList<ProcessBean> pats = new ArrayList<>();
                        // Macro che vengono dalla query (disco)
                        for (ProcessBean cachedMat : matsCached) {
                            // Processi che vengono dalla query
                            for (ProcessBean cachedPat : cachedMat.getProcessi()) {
                                // Carica il processo da cache nella lista piatta dei processi
                                pats.add(cachedPat);
                                // Per ogni processo recupera i suoi indicatori
                                LinkedHashMap<String, InterviewBean> cachedIndicators = cachedPat.getIndicatori();
                                // Macro che vengono dai calcoli (memoria)
                                for (ProcessBean runtimeMat : matsRuntime) {
                                    // Processi che vengono dai calcoli
                                    for (ProcessBean runtimePat : runtimeMat.getProcessi()) {
                                        LinkedHashMap<String, InterviewBean> previousIndicators = new LinkedHashMap<>();
                                        LinkedHashMap<String, InterviewBean> changedIndicators = new LinkedHashMap<>();
                                        // Ha senso confrontare gli indicatori solo sullo stesso processo
                                        if (runtimePat.getId() == cachedPat.getId()) {
                                            // Ottiene gli indicatori del processo corrente calcolati a runtime
                                            LinkedHashMap<String, InterviewBean> runtimeIndicators = runtimePat.getIndicatori();
                                            // Esamina le differenze tra gli indicatori del processo a runtime e quelli da cache
                                            for (Map.Entry<String, InterviewBean> entry : runtimeIndicators.entrySet()) {
                                                String key = entry.getKey();
                                                InterviewBean value = entry.getValue();
                                                // Preleva il valore dell'indicatore da cache
                                                InterviewBean cachedIndicator = cachedIndicators.get(key);
                                                // Lo memorizza nella lista degli indicatori prelevati da cache
                                                previousIndicators.put(key, cachedIndicator);
                                                // Se NON è vero che i valori sono uguali (cioè se i valori sono diversi)
                                                if (!value.getInformativa().equals(cachedIndicator.getInformativa())) {
                                                    changedIndicators.put(key, value);
                                                }
                                            }
                                            listaVecchi.put(new Integer(runtimePat.getId()), previousIndicators);
                                            listaNuovi.put(new Integer(runtimePat.getId()), changedIndicators);
                                        }
                                    }
                                }
                            }
                        }
                        // Trasforma Arraylist processi in una HashMap per rispettare il tipo
                        HashMap<Integer, ArrayList<ProcessBean>> listaPat = new HashMap<>();
                        // Carica processi nella mappa
                        listaPat.put(new Integer(NOTHING), pats);
                        // Imposta nella mappa le liste trovate
                        list.put(TIPI_LISTE[8], listaPat);
                        list.put(TIPI_LISTE[9], listaVecchi);
                        list.put(TIPI_LISTE[10], listaNuovi);
                    }
                }
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
    
    /* **************************************************************** *
     *      Metodi utilizzati per servire richieste asincrone (XHR)     *
     * **************************************************************** */
    
    /**
     * <p>Restituisce una mappa contenente elenchi di indicatori contenenti 
     * anche le note al PxI e valorizza per riferimento liste di elementi generici 
     * (input, fasi, output...) estratti in base alla richiesta ricevuta
     * e indicizzati per una chiave convenzionale, definita nelle costanti.
     * I dati ottenuti tramite questo metodo sono generalmente pensati 
     * per servire una richiesta asincrona (XHR).</p>
     * 
     * @param req       HttpServletRequest contenente i parametri per contestualizzare l'estrazione
     * @param qToken    il token della commmand in base al quale bisogna preparare la lista di elementi
     * @param pToken    il token relativo alla parte di gestione da effettuare
     * @param elements  generica mappa in cui devono essere valorizzate, per riferimento, le liste di items afferenti al processo
     * @return <code>HashMap&lt;String,InterviewBean&gt; - dictionary contenente gli indicatori cached, contenenti anche le note
     * @throws CommandException se si verifica un problema nella WebStorage (DBWrapper), nella Command interpellata, o in qualche puntamento
     */
    private static HashMap<String, InterviewBean> retrieve(HttpServletRequest req,
                                                           String qToken,
                                                           String pToken,
                                                           ConcurrentHashMap<String, ArrayList<?>> elements)
                                                    throws CommandException {
        // Dichiara generico elenco di elementi da restituire
        HashMap<String, InterviewBean> indicators = null;
        // Ottiene i parametri della richiesta
        ParameterParser parser = new ParameterParser(req);
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
                if (pToken.equalsIgnoreCase(PART_PROCESS)) {
                    // Cerca l'identificativo del processo anticorruttivo
                    int idP = parser.getIntParameter("pliv", DEFAULT_ID);
                    // Cerca la granularità del processo anticorruttivo
                    int liv = parser.getIntParameter("liv", DEFAULT_ID);
                    // Valorizza liste necessarie a visualizzare  i dettagli di un processo, restituendo gli indicatori corredati con le note al PxI
                    indicators = ProcessCommand.retrieveProcess(user, idP, liv, elements, codeSurvey, db);
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
        return indicators;
    }

    /* **************************************************************** *
     *   Metodi per la preparazione e la generazione dei files di dati  *
     * **************************************************************** */

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
     * <p>Gestisce la generazione dell&apos;output in formato di uno stream RTF 
     * che sar&agrave; recepito come tale dal browser e trattato di conseguenza 
     * (normalmente, ma non necessariamente, con il download).</p>
     * <p>Usa altri metodi, interni, per ottenere il nome del file, che dev&apos;essere un
     * nome univoco, e per la stampa vera e propria nel PrintWriter.</p>
     *
     * @param req HttpServletRequest da passare al metodo di stampa
     * @param res HttpServletResponse per impostarvi i valori che la predispongono a servire csv anziche' html
     * @param qToken token della commmand in base al quale bisogna preparare la lista di elementi
     * @throws ServletException eccezione eventualmente proveniente dalla fprinf, da propagare
     * @throws IOException  eccezione eventualmente proveniente dalla fprinf, da propagare
     */
    private static void makeRTF(HttpServletRequest req, HttpServletResponse res, String qToken)
                         throws ServletException, IOException {
        // Genera un nome univoco per il file che verrà servito
        String fileName = makeFilename(qToken);
        // Configura il response per il browser
        res.setContentType("application/rtf");
        // Configura il characterEncoding (v. commento)
        res.setCharacterEncoding("ISO-8859-1");
        // Configura l'header
        res.setHeader("Content-Disposition","attachment;filename=" + fileName + DOT + RTF);
        // Stampa il file sullo standard output
        fprintf(req, res);
    }
    
    
    /**
     * <p>Gestisce la generazione dell&apos;output in formato di un log formattato 
     * che sar&agrave; recepito come tale dal browser e trattato di conseguenza.</p>
     * <p>Usa altri metodi, interni, per ottenere il nome del file, che dev&apos;essere un
     * nome univoco; a differenza dei metodi che preparano un file che dovr&agrave;
     * poi essere prodotto tramite lo standard output, questo metodo non ha 
     * bisogno di invocare il metodo di stampa, perch&eacute; l'output viene
     * preso in carico, a valle, da una pagina dinamica jsp attraverso 
     * la quale si genera a sua volta il contenuto di un file html corrispondente
     * all'output della jsp; l'output della pagina dinamica viene trasferito
     * in un file html grazie alla preparazione delle intestazioni della
     * HttpServletResponse effettuata al presente metodo, che evita 
     * quindi di scomodare il PrintWriter ottenendo per&ograve; 
     * un flusso &egrave; analogo.</p>
     *
     * @param req HttpServletRequest 
     * @param res HttpServletResponse per impostarvi i valori che la predispongono a servire html anziche' jsp
     * @param key String associata a un'etichetta che verra' usata come nome del file html
     * @throws ServletException eventuale eccezione da propagare
     * @throws IOException  eventuale eccezione da propagare
     */
    private static void makeHTML(HttpServletRequest req, HttpServletResponse res, String key)
                         throws ServletException, IOException {
        // Genera un nome univoco per il file che verrà servito
        String fileName = makeFilename(key);
        // Configura il response per il browser
        res.setContentType("text/html");
        // Configura il characterEncoding (v. commento)
        res.setCharacterEncoding("ISO-8859-1");
        // Configura l'header
        res.setHeader("Content-Disposition","attachment;filename=" + fileName + DOT + HTML);
        // Configura il basehref per i link assoluti
        String baseHref = ConfigManager.getBaseHref(req);
        // Setta nella request il valore del <base href... />
        req.setAttribute("baseHref", baseHref);
        // Setta nella request gli estremi temporali
        req.setAttribute("now", Utils.format(Utils.convert(Utils.getCurrentDate())) );
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
                ArrayList<ItemBean> list = (ArrayList<ItemBean>) req.getAttribute("lista");
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
        //?q=mu
        else if (req.getParameter(ConfigManager.getEntToken()).equalsIgnoreCase(COMMAND_REPORT)) {
            /* **************************************************************** *
             *          Contenuto files RTF per processi e indicatori           *
             * **************************************************************** */
            if (part.equalsIgnoreCase(PART_SELECT_STR)) {
                try {
                    Calendar now = Calendar.getInstance();
                    // Recupera mappa completa da Request
                    HashMap<String, HashMap<Integer, ArrayList<?>>> map = (HashMap<String, HashMap<Integer, ArrayList<?>>>) req.getAttribute("lista");
                    // Macroprocessi
                    ArrayList<ProcessBean> mats = (ArrayList<ProcessBean>) map.get(TIPI_LISTE[5]).get(Integer.valueOf(NOTHING));
                    // Rischi indicizzati per id processo_at
                    HashMap<Integer, ArrayList<?>> risks = map.get(TIPI_LISTE[3]);
                    // Strutture indicizzate per id processo_at
                    HashMap<Integer, ArrayList<?>> structs = map.get(TIPI_LISTE[6]);
                    // Soggetti contingenti indicizzati per id processo_at
                    HashMap<Integer, ArrayList<?>> subjects = map.get(TIPI_LISTE[7]);
                    // Preprazione file RTF
                    String prolog = "{\\rtf1 \\ansi \\ansicpg1252\\deff0\\nouicompat\\deflang1040 \n";
                    String header = 
                        "{\\fonttbl\n" + 
                          "\t{\\f0\\froman\\fprq2\\fcharset0 Times New Roman;}\n" + 
                          "\t{\\f1\\fswiss\\fprq2\\fcharset0 Calibri;}\n" +
                          "\t{\\f2\\fnil\\fcharset0 Calibri;}\n" + 
                        "}\n" +
                        "{\\info\n" +
                          "\t{\\title Report Strutture e Rischi}\n" +
                          "\t{\\author Giovanroberto Torre}\n" +
                          //"\t{\\company Athenaeum}\n" +
                          "\t{\\creatim\\yr" + new Integer(now.get(Calendar.YEAR)).toString() +  
                          "\\mo" + new Integer(now.get(Calendar.MONTH) + 1) + 
                          "\\dy" + String.format("%02d", new Integer(now.get(Calendar.DAY_OF_MONTH))) +
                          "\\hr" + String.format("%02d", new Integer(now.get(Calendar.HOUR_OF_DAY))) + 
                          "\\min" + String.format("%02d", new Integer(now.get(Calendar.MINUTE))) + 
                          "}\n" +
                          "\t{\\doccomm https://at.univr.it/rischi/}\n" +
                        "}\n" +
                        "{\\colortbl ;\\red255\\green255\\blue0;\\red255\\green0\\blue0;}\n" +
                        "{\\*\\generator Riched20 10.0.19041}\n";
                    String thead =
                        "\\viewkind4\\uc1\n" +
                        "\\trowd\\trgaph70\\trleft5\\trqc\\trbrdrl\\brdrs\\brdrw10 \n" +
                        "\\trbrdrt\\brdrs\\brdrw10 \\trbrdrr\\brdrs\\brdrw10 \\trbrdrb\\brdrs\\brdrw10 \n" + 
                        "\\trpaddl70\\trpaddr70\\trpaddfl3\\trpaddfr3\n" +
                        "\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx1810\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" + 
                        "\\cellx3427\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx4767\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx6476\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx7874\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx9182\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx10186 \n" +
                        "\\pard\\intbl\\widctlpar\\qc\\b\\f1\\fs22 \n" +
                        "Area di rischio \\cell \n" +
                        "Macroprocesso \\cell \n" + 
                        "Processo \\cell \n" +
                        "Rischi Potenziali \\cell \n" +
                        "Strutture \\cell \n" + 
                        "Soggetti \\cell \n" + 
                        "Giudizio sintetico \\cell \n" +
                        "\\row \n";
                    String tr = 
                        "\\trowd\\trgaph70\\trleft5\\trqc\\trrh5103\\trbrdrl\\brdrs\\brdrw10 \n" + 
                        "\\trbrdrt\\brdrs\\brdrw10 \\trbrdrr\\brdrs\\brdrw10 \\trbrdrb\\brdrs\\brdrw10 \n" + 
                        "\\trpaddl70\\trpaddr70\\trpaddfl3\\trpaddfr3\n" +
                        "\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx1810\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx3427\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx4767\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx6476\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx7874\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx9182\\clvertalc\\clbrdrl\\brdrw10\\brdrs\\clbrdrt\\brdrw10\\brdrs\\clbrdrr\\brdrw10\\brdrs\\clbrdrb\\brdrw10\\brdrs \n" +
                        "\\cellx10186 \n" +
                        "\\pard\\intbl\\widctlpar\\qc\\b0\\highlight0 \n";
                StringBuffer content = new StringBuffer();
                    content.append(prolog);         // RFT file starts
                    content.append(header);         // RTF header
                    content.append(thead);          // Table header
                    for (ProcessBean m : mats) {
                        for (ProcessBean p : m.getProcessi()) {
                            ArrayList<RiskBean> processRisks = (ArrayList<RiskBean>) risks.get(Integer.valueOf(p.getId()));
                            ArrayList<DepartmentBean> processStructs = (ArrayList<DepartmentBean>) structs.get(Integer.valueOf(p.getId()));
                            ArrayList<DepartmentBean> processSubjs = (ArrayList<DepartmentBean>) subjects.get(Integer.valueOf(p.getId()));
                            // <tr>
                            content.append(tr);
                            content.append(p.getAreaRischio());
                            content.append("\\cell ");
                            content.append(m.getNome()
                                            .replace(ENGLISH_SINGLE_QUOTE, APOSTROPHE)
                                            .replace(ENGLISH_DOUBLE_QUOTE_OPEN, QUOTE)
                                            .replace(ENGLISH_DOUBLE_QUOTE_CLOSE, QUOTE));
                            content.append("\\cell ");
                            content.append(p.getNome()
                                            .replace(ENGLISH_SINGLE_QUOTE, APOSTROPHE)
                                            .replace(ENGLISH_DOUBLE_QUOTE_OPEN, QUOTE)
                                            .replace(ENGLISH_DOUBLE_QUOTE_CLOSE, QUOTE));
                            content.append("\\cell \n");
                            content.append("\\pard\\intbl\\widctlpar\\sl240\\slmult1");
                            for (RiskBean r : processRisks) {
                                content.append("{\\pict{\\*\\picprop}\\wmetafile8\\picw847\\pich847\\picwgoal288\\pichgoal288 \n");
                                content.append(CAUTION_EXCLAMATION_MARK_SIGN_TRIANGLE);
                                content.append("} ");
                                content.append(r.getNome()
                                                .replace(ENGLISH_SINGLE_QUOTE, APOSTROPHE)
                                                .replace(ENGLISH_DOUBLE_QUOTE_OPEN, QUOTE)
                                                .replace(ENGLISH_DOUBLE_QUOTE_CLOSE, QUOTE));
                                content.append(BLANK_SPACE);
                                content.append("\\par\n");
                                content.append("\\pard\\intbl\\widctlpar\\par\n");
                            }
                            content.append("\\cell \n");
                            for (DepartmentBean s : processStructs) {
                                content.append(s.getPrefisso()).append(BLANK_SPACE);
                                content.append(s.getNome()
                                                .replace(ENGLISH_SINGLE_QUOTE, APOSTROPHE)
                                                .replace(ENGLISH_DOUBLE_QUOTE_OPEN, QUOTE)
                                                .replace(ENGLISH_DOUBLE_QUOTE_CLOSE, QUOTE));
                                content.append("\\par\n");
                                content.append("\\pard\\intbl\\widctlpar\\par\n");
                            }
                            content.append("\\cell \n");
                            for (DepartmentBean t : processSubjs) {
                                content.append(t.getNome()
                                                .replace(ENGLISH_SINGLE_QUOTE, APOSTROPHE)
                                                .replace(ENGLISH_DOUBLE_QUOTE_OPEN, QUOTE)
                                                .replace(ENGLISH_DOUBLE_QUOTE_CLOSE, QUOTE));
                                content.append("\\par\n");
                                content.append("\\pard\\intbl\\widctlpar\\par\n");
                            }
                            content.append("\\cell \n");
                            if (p.getIndicatori() != null && !p.getIndicatori().isEmpty()) {
                                content.append("\\qc");
                                content.append(getHighlight(p.getIndicatori().get("PxI").getInformativa()));
                                content.append(BLANK_SPACE);
                                content.append(p.getIndicatori().get("PxI").getInformativa());
                                content.append(BLANK_SPACE).append("\\highlight0").append(BLANK_SPACE);
                                content.append(BLANK_SPACE);
                                content.append(p.getIndicatori().get("PxI").getNote()
                                                .replace(ENGLISH_SINGLE_QUOTE, APOSTROPHE)
                                                .replace(ENGLISH_DOUBLE_QUOTE_OPEN, QUOTE)
                                                .replace(ENGLISH_DOUBLE_QUOTE_CLOSE, QUOTE));
                                content.append(BLANK_SPACE).append(BLANK_SPACE);
                            }
                            content.append("\\cell");
                            content.append("\\row \n");
                            // </tr>
                        }
                    }
                    content.append("\\pard\\sa200\\sl276\\slmult1\\f2\\lang16\\par");
                    content.append(BLANK_SPACE);
                    content.append("}");                // RTF file ends 
                    // Stampa il contenuto del file
                    out.println(String.valueOf(content));
                } catch (RuntimeException re) {
                    log.severe(FOR_NAME + "Si e\' verificato un problema nella scrittura del file che contiene la tabella MDM.\n" + re.getMessage());
                    out.println(re.getMessage());
                } catch (Exception e) {
                    log.severe(FOR_NAME + "Problema nella fprintf di Data" + e.getMessage());
                    out.println(e.getMessage());
                }
            } else if (part.equalsIgnoreCase(PART_MEASURES)) {
                /* ************************************************************ *
                 * Generazione contenuto files CSV di processi at e PxI mitigati*
                 * ************************************************************ */
                try {
                    // Recupera i macro at da Request
                    ArrayList<ProcessBean> list = (ArrayList<ProcessBean>) req.getAttribute("lista");
                    // Scrittura file CSV
                    StringBuffer headers = new StringBuffer("N." + SEPARATOR)
                            .append("Area Rischio").append(SEPARATOR)
                            .append("Codice Macroprocesso").append(SEPARATOR)
                            .append("Macroprocesso").append(SEPARATOR)
                            .append("Codice Processo").append(SEPARATOR)
                            .append("Processo").append(SEPARATOR)
                            .append("PxI").append(SEPARATOR)
                            .append("PxI mitigato (stima)").append(SEPARATOR);
                    out.println(headers);
                    if (list.size() > NOTHING) {
                        int itCounts = NOTHING, record = NOTHING;
                        do {
                            ProcessBean pat = list.get(itCounts);
                            StringBuffer tupla = new StringBuffer(++record + SEPARATOR)
                                .append(pat.getAreaRischio()).append(SEPARATOR)
                                .append(pat.getCodice().substring(NOTHING, pat.getCodice().lastIndexOf(DOT))).append(SEPARATOR)
                                .append(VOID_STRING).append(SEPARATOR)
                                .append(pat.getCodice()).append(SEPARATOR)
                                .append(pat.getNome()).append(SEPARATOR)
                                .append(pat.getIndicatori().get("PxI").getInformativa()).append(SEPARATOR)
                                .append(pat.getIndicatori().get("PxI (stima)").getInformativa()).append(SEPARATOR);
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
        }
        else {
            String msg = FOR_NAME + "La Servlet Data non accetta la stringa passata come valore di 'ent': " + req.getParameter(ConfigManager.getEntToken());
            log.severe(msg + "Tentativo di indirizzare alla Servlet Data una richiesta non gestita. Hacking test?\n");
            throw new IOException(msg);
        }
        return success;
    }

    
    /**
     * <p>Calcola il colore di highlight in Rich Text Format.</p>
     * <p>Purtroppo, questo formato, sviluppato da Microsoft, non 
     * implementa in modo efficace tutti gli attributi di formattazione
     * ed &egrave; molto dipendente dall'editor utilizzato per la visualizzazione
     * e dal sistema operativo che lo ospita, per cui le codifiche degli highlight,
     * che, almeno sulla carta, dovevano essere ben definite 
     * (<a href="https://www.biblioscape.com/rtf15_spec.htm#Heading45">v. p.es.</a>), 
     * non si comportano come atteso (oppure, l'implementazione richiederebbe 
     * maggiore approfondimento).<br>
     * In ogni caso, questo formato, che pure sembrava un ottimo compromesso
     * fra human readibility, formattazione ricca (appunto) e ampia fruibilit&egrave; 
     * e portabilit&agrave;, si rivela in realt&agrave; rigido, tutt'altro che "ricco",
     * ormai obsoleto e poco profittevole in termini di investimento di tempo di sviluppo.<br>
     * Preferibile, pertanto, puntare oggi direttamente su un formato estremamente
     * preciso, come PDF, oppure direttamente su librerie per la generazione in formati
     * pi&uacute; attuali, proprietari (e.g. .docx) o aperti (e.g. ODF).</p> 
     * 
     * @param riskLevel livello di rischio da evidenziare 
     * @return valore di highlight da stampare nel documento
     */
    public static String getHighlight(String riskLevel) {
        if (riskLevel.equals(LIVELLI_RISCHIO[0])) {
            return "\\highlight4";
        } else if (riskLevel.equals(LIVELLI_RISCHIO[1])) {
            return "\\highlight0";
        } else if (riskLevel.equals(LIVELLI_RISCHIO[2])) {
            return "\\highlight1";
        } else if (riskLevel.equals(LIVELLI_RISCHIO[3])) {
            return "\\highlight2";
        } else if (riskLevel.equals(LIVELLI_RISCHIO[4])) {
            return "\\highlight13";
        } else {
            return "\\highlight0";
        }
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
