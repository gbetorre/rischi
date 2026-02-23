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
 *   Copyright (C) 2022-2026 Giovanroberto Torre
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

package it.rol.command;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.db.DBWrapper;
import it.rol.Main;
import it.rol.db.Query;
import it.rol.SessionManager;
import it.rol.util.Utils;
import it.rol.bean.CodeBean;
import it.rol.bean.DepartmentBean;
import it.rol.bean.InterviewBean;
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
import it.rol.bean.QuestionBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.CommandException;
import it.rol.exception.WebStorageException;


/** 
 * <p><code>AuditCommand.java</code><br>
 * Implementa la logica per la presentazione dei quesiti (interviste) 
 * e per la gestione dell'inserimento, dell'aggiornamento e della visualizzazione 
 * delle risposte, nel contesto della raccolta e dell'analisi dei dati
 * relativi alla mappatura effettuata tramite la web-application 
 * <code>Rischi on Line (rol)</code> .
 * Si occupa, inoltre, del calcolo dei valori degli indicatori di rischio.</p>
 * 
 * <p>Created on Tue 12 Apr 2022 09:46:04 AM CEST</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class AuditCommand extends ItemBean implements Command, Constants {
    
    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
     */
    private static final long serialVersionUID = 6619103818860851921L;
    /**
     *  Nome di questa classe 
     *  (utilizzato per contestualizzare i messaggi di errore)
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     * Log per debug in produzione
     */
    protected static Logger LOG = Logger.getLogger(Main.class.getName());
    /**
     * Pagina a cui la command fa riferimento per permettere la scelta di una struttura
     */
    private static final String nomeFileSelectStruct = "/jsp/inStruttura.jsp";
    /**
     * Pagina a cui la command fa riferimento per permettere la scelta di un processo
     */
    private static final String nomeFileSelectProcess = "/jsp/inProcesso.jsp";
    /**
     * Pagina a cui la command fa riferimento per permettere la compilazione dei quesiti
     */
    private static final String nomeFileCompileQuest = "/jsp/inQuestionario.jsp";
    /**
     * Pagina a cui la command fa riferimento per mostrare la lista delle interviste
     */
    private static final String nomeFileElencoQuest = "/jsp/inElenco.jsp";
    /**
     * Pagina a cui la command fa riferimento per permettere l'aggiornamento delle risposte ai dei quesiti
     */
    private static final String nomeFileResumeQuest = "/jsp/inEpilogo.jsp";
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutte le pagine gestite da questa Command
     */    
    private static final HashMap<String, String> nomeFile = new HashMap<>();
    /**
     *  Tabella chiave/valore contenente il numero di quesiti per ogni rilevazione
     */
    private static ConcurrentHashMap<String, Integer> questionAmounts; 

    
    /** 
     * Crea una nuova istanza di WbsCommand 
     */
    public AuditCommand() {
        /*;*/   // It doesn't anything
    }
  
    
    /** 
     * <p>Raccoglie i valori dell'oggetto ItemBean
     * e li passa a questa classe command.</p>
     *
     * @param voceMenu la VoceMenuBean pari alla Command presente.
     * @throws CommandException se l'attributo paginaJsp di questa command non e' stato valorizzato.
     */
    @Override
    public void init(ItemBean voceMenu) throws CommandException {
        this.setId(voceMenu.getId());
        this.setNome(voceMenu.getNome());
        this.setLabelWeb(voceMenu.getLabelWeb());
        this.setNomeClasse(voceMenu.getNomeClasse());
        this.setPaginaJsp(voceMenu.getPaginaJsp());
        this.setInformativa(voceMenu.getInformativa());
        if (this.getPaginaJsp() == null) {
          String msg = FOR_NAME + "La voce menu' " + this.getNome() + " non ha il campo paginaJsp. Impossibile visualizzare i risultati.\n";
          throw new CommandException(msg);
        }
        // Carica la hashmap contenente le pagine da includere in funzione dei parametri sulla querystring
        nomeFile.put(COMMAND_AUDIT,             nomeFileElencoQuest);
        nomeFile.put(PART_SELECT_STR,           nomeFileSelectStruct);
        nomeFile.put(PART_PROCESS,              nomeFileSelectProcess);
        nomeFile.put(PART_SELECT_QST,           nomeFileCompileQuest);
        nomeFile.put(PART_RESUME_QST,           nomeFileResumeQuest);
        nomeFile.put(PART_SELECT_QSS,           nomeFileElencoQuest);
    }  
  
    
    /**
     * <p>Gestisce il flusso principale.</p>
     * <p>Prepara i bean.</p>
     * <p>Passa nella Request i valori che verranno utilizzati dall'applicazione.</p>
     * 
     * @param req la HttpServletRequest contenente la richiesta del client
     * @throws CommandException se si verifica un problema, tipicamente nell'accesso a campi non accessibili o in qualche altro tipo di puntamento 
     */
    @Override
    public void execute(HttpServletRequest req) 
                 throws CommandException {
        /* ******************************************************************** *
         *              Dichiara e inizializza variabili locali                 *
         * ******************************************************************** */
        // Databound
        DBWrapper db = null;
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Utente loggato
        PersonBean user = null;
        // Elenco strutture collegate alla rilevazione
        ArrayList<DepartmentBean> structs = null;
        // Elenco processi collegati alla rilevazione
        ArrayList<ProcessBean> macros = null;
        // Elenco quesiti collegati alla rilevazione
        ArrayList<QuestionBean> questions = null;
        // Elenco risposte ai quesiti collegati all'intervista
        ArrayList<QuestionBean> answers = null;
        // Elenco interviste collegate alla rilevazione
        ArrayList<InterviewBean> interviews = null;
        // Elenco strutture collegate alla rilevazione indicizzate per codice
        HashMap<String, Vector<DepartmentBean>> flatStructs = null;
        // Elenco quesiti collegati alla rilevazione raggruppati per ambito
        HashMap<ItemBean, ArrayList<QuestionBean>> flatQuestions = null;
        // Tabella che conterrà i valori dei parametri passati dalle form
        HashMap<String, LinkedHashMap<String, String>> params = null;
        // Tabella che conterrà i valori degli indicatori calcolati
        HashMap<String, InterviewBean> indicators = null;
        // Predispone le BreadCrumbs personalizzate per la Command corrente
        //LinkedList<ItemBean> bC = null;
        // Variabile contenente l'indirizzo per la redirect da una chiamata POST a una chiamata GET
        String redirect = null;
        /* ******************************************************************** *
         *                    Recupera parametri e attributi                    *
         * ******************************************************************** */
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter(PARAM_SURVEY, DASH);
        // Recupera o inizializza 'tipo pagina'   
        String part = parser.getStringParameter("p", DASH);
        // Recupera o inizializza eventuale parametro messaggio
        String mess = parser.getStringParameter("msg", DASH);
        // Flag di scrittura
        Boolean writeAsObject = (Boolean) req.getAttribute("w");
        boolean write = writeAsObject.booleanValue();
        // Dichiara data ed ora di una intervista cercata
        Date questDate = null;
        Time questTime = null;
        /* ******************************************************************** *
         *      Instanzia nuova classe DBWrapper per il recupero dei dati       *
         * ******************************************************************** */
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new CommandException(FOR_NAME + "Non e\' disponibile un collegamento al database\n." + wse.getMessage(), wse);
        }
        /* ******************************************************************** *
         *         Previene il rischio di attacchi di tipo Garden Gate          *
         * ******************************************************************** */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            user = SessionManager.checkSession(req.getSession(IF_EXISTS_DONOT_CREATE_NEW));
        } catch (RuntimeException re) {
            throw new CommandException(FOR_NAME + "Problema a livello dell\'autenticazione utente!\n" + re.getMessage(), re);
        }
        /* ******************************************************************** *
         *                           Corpo del metodo                           *
         * ******************************************************************** */
        // Decide il valore della pagina
        try {
            // Controllo sull'input
            if (!codeSur.equals(DASH)) {
                // Creazione della tabella che conterrà i valori dei parametri passati dalle form
                params = new HashMap<>();
                // Carica in ogni caso i parametri di navigazione
                loadParams(part, parser, params);
                /* ======================= @PostMapping ======================= */
                if (write) {
                    // Controlla quale azione vuole fare l'utente
                    if (nomeFile.containsKey(part)) {
                        // Stringa dinamica per contenere i parametri di scelta strutture
                        StringBuffer paramsStruct = new StringBuffer();
                        // Dizionario dei parametri delle strutture scelte dall'utente
                        LinkedHashMap<String, String> struct = params.get(PART_SELECT_STR);
                        // Cicla sul dizionario dei parametri per ricostruire l'URL
                        for (Map.Entry<String, String> set : struct.entrySet()) {
                            // Printing all elements of a Map
                            paramsStruct.append(AMPERSAND);
                            paramsStruct.append("s" + set.getKey() + EQ + set.getValue());
                        }
                        // Controlla quale richiesta deve gestire
                        if (part.equalsIgnoreCase(PART_SELECT_STR)) {
                            /* ************************************************ *
                             *              CHOOSING Structure Part             *
                             * ************************************************ */
                            // Prepara la redirect 
                            redirect = "q=" + COMMAND_AUDIT + "&p=" + PART_PROCESS + paramsStruct.toString() + "&r=" + codeSur;
                        } else if (part.equalsIgnoreCase(PART_PROCESS)) {
                            /* ************************************************ *
                             *               CHOOSING Process Part              *
                             * ************************************************ */
                            // Dizionario dei parametri dei processi scelti dall'utente
                            LinkedHashMap<String, String> macro = params.get(PART_PROCESS);
                            // Stringa dinamica per contenere i parametri di scelta processi
                            StringBuffer paramsProc = new StringBuffer();
                            for (Map.Entry<String, String> set : macro.entrySet()) {
                                // Printing all elements of a Map
                                paramsProc.append(AMPERSAND);
                                paramsProc.append("p" + set.getKey() + EQ + set.getValue());
                            }
                            redirect = "q=" + COMMAND_AUDIT + "&p=" + PART_SELECT_QST + paramsStruct.toString() + paramsProc.toString() + "&r=" + codeSur;
                        } else if (part.equalsIgnoreCase(PART_SELECT_QST)) {
                            /* ************************************************ *
                             *                INSERT Answers Part               *
                             * ************************************************ */
                            // Recupera il numero di quesiti associati alla rilevazione
                            Integer itemsAsInteger = questionAmounts.get(codeSur);
                            // Unboxing
                            int items = itemsAsInteger.intValue();
                            // Inserisce nel DB le risposte a tutti gli item quesiti
                            db.insertAnswers(user, params, items);
                            // Prepara il passaggio dei parametri identificativi dei processi
                            LinkedHashMap<String, String> macro = params.get(PART_PROCESS);
                            // Stringa dinamica per contenere i parametri di scelta processi
                            StringBuffer paramsProc = new StringBuffer();
                            for (Map.Entry<String, String> set : macro.entrySet()) {
                                // Printing all elements of a Map
                                paramsProc.append(AMPERSAND);
                                paramsProc.append("p" + set.getKey() + "=" + set.getValue());
                            }
                            redirect = "q=" + COMMAND_AUDIT + "&p=" + PART_SELECT_QSS + "&r=" + codeSur;
                        } else if (part.equalsIgnoreCase(PART_RESUME_QST)) {
                            /* ************************************************ *
                             *                UPDATE Answers Part               *
                             * ************************************************ */
                            /* Riduce il rischio di attacchi di tipo Cross-site request forgery (CSRF)
                             * perche' tutte le funzioni che modificano lo stato degli oggetti sono invocate tramite POST (infatti
                             * qui siamo nel ramo @PostMapping). In altre parole, avrei potuto passare l'id del quesito da
                             * aggiornare come parametro dell'action, e inviare la form stessa tramite metodo GET (in tal caso qui non
                             * ci saremmo arrivati e saremmo andati nel ramo @GetMapping). Siccome pero' questa form induce
                             * un aggiornamento dei dati, cio' avrebbe esposto al rischio di attacchi CSRF. Invece, la form viene
                             * gestita tramite POST e l'id del quesito da aggiornare viene passato tramite un campo hidden.
                             * Cio' non impedisce attacchi di tipo "fake form" (non so bene come si chiamino) pero' in tal caso
                             * il browser che invia la fake form con il valore del campo hidden modificato deve essere loggato,
                             * cioe' e' l'hacker stesso che deve essere loggato, il che non e' banale ed e' ben diverso da inviare
                             * una gif (con i parametri) a un utente che ha gia' il browser loggato nell'applicazione e ci clicca sopra. */
                            // Aggiorna il quesito
                            db.updateAnswer(user, params);
                            // Prepara il passaggio dei parametri identificativi dei processi
                            LinkedHashMap<String, String> macro = params.get(PART_PROCESS);
                            // Stringa dinamica per contenere i parametri di scelta processi
                            StringBuffer paramsProc = new StringBuffer();
                            for (Map.Entry<String, String> set : macro.entrySet()) {
                                // Printing all elements of a Map
                                paramsProc.append(AMPERSAND);
                                paramsProc.append("p" + set.getKey() + "=" + set.getValue());
                            }
                            // Prepara il passaggio dei parametri identificativi del timestamp della rilevazione
                            LinkedHashMap<String, String> dateTime = params.get(PARAM_SURVEY);
                            // Stringa dinamica per contenere i parametri di scelta processi
                            StringBuffer dateTimeProc = new StringBuffer();
                            for (Map.Entry<String, String> set : dateTime.entrySet()) {
                                // Printing all elements of a Map but 'r'
                                if (!set.getKey().equals(PARAM_SURVEY)) {
                                    dateTimeProc.append(AMPERSAND);
                                    dateTimeProc.append(set.getKey() + EQ + set.getValue());
                                }
                            }
                            // Controlla il valore del parametro facoltativo
                            String msg = (mess.equalsIgnoreCase("getAll")) ? AMPERSAND + MESSAGE + EQ + mess : VOID_STRING;
                            // Costruisce l'indirizzo a cui redirezionare
                            redirect =  "q" + EQ + COMMAND_AUDIT + AMPERSAND +
                                        "p" + EQ + PART_RESUME_QST + 
                                        paramsStruct.toString() + 
                                        paramsProc.toString() +
                                        dateTimeProc.toString() + AMPERSAND +
                                        PARAM_SURVEY + EQ + codeSur
                                        + msg;
                        }
                    } else {
                        // Azione di default
                        // do delete?
                    }
                /* ======================== @GetMapping ======================= */
                } else {
                    /* ************************************************ *
                     *                Manage Interview Part             *
                     * ************************************************ */
                    if (nomeFile.containsKey(part)) {
                        // Recupera le strutture della rilevazione corrente
                        structs = DepartmentCommand.retrieveStructures(codeSur, user, db);
                        macros = ProcessCommand.retrieveMacroAtBySurvey(user, codeSur, db);
                        if (part.equalsIgnoreCase(PART_SELECT_STR)) {
                            /* ************************************************ *
                             *              SELECT Structure Part               *
                             * ************************************************ */
                            flatStructs = (HashMap<String, Vector<DepartmentBean>>) decant(structs, part);
                        } else if (part.equalsIgnoreCase(PART_SELECT_QST)) {
                            /* ************************************************ *
                             *              SELECT Questions Part               *
                             * ************************************************ */
                            questions = retrieveQuestions(user, codeSur, Query.GET_ALL_BY_CLAUSE, Query.GET_ALL_BY_CLAUSE, db);
                            ArrayList<ItemBean> ambits = db.getAmbits(user, ConfigManager.getSurvey(codeSur));
                            flatQuestions = decantQuestions(questions, ambits);
                        } else if (part.equalsIgnoreCase(PART_CONFIRM_QST)) {
                            /* ************************************************ *
                             *                   Confirm Part                   *
                             * ************************************************ */
                            // TODO IMPLEMENTARE O ELIMINARE se non necessaria conferma
                        } else if (part.equalsIgnoreCase(PART_RESUME_QST)) {
                            /* ************************************************ *
                             *  SELECT Set of Answers belonging to an Inverview *
                             * ************************************************ */
                            // Recupera le risposte date ai quesiti dell'intervista
                            answers = db.getAnswers(user, params, ConfigManager.getSurvey(codeSur));
                            // Recupera i quesiti
                            questions = retrieveQuestions(user, codeSur, Query.GET_ALL_BY_CLAUSE, Query.GET_ALL_BY_CLAUSE, db);
                            // Recupera gli indicatori vuoti (solo gli estremi, non i valori)
                            ArrayList<CodeBean> indicatorsAsList = db.getIndicators(user, ConfigManager.getSurvey(codeSur));
                            // Indicizza ogni risposta per il rispettivo id Quesito
                            HashMap<Integer, QuestionBean> answersByQuestions = decantAnswers(questions, answers);
                            // Calcola gli identificativi dei quesiti corrispondenti a tutti gli indicatori
                            HashMap<String, LinkedList<Integer>> questionsByIndicator = retrieveQuestionsByIndicators(user, answers, ConfigManager.getSurvey(codeSur), db);
                            // Recupera le strutture collegate a tutti i processi at (necessarie per calcolare il valore di I3)
                            HashMap<Integer, ArrayList<DepartmentBean>> structsAsMap = ReportCommand.retrieveStructures(macros, user, codeSur, db);
                            // Recupera i soggetti terzi collegati a tutti i processi at (necessari per calcolare il valore di I3)
                            HashMap<Integer, ArrayList<DepartmentBean>> subjectsAsMap = ReportCommand.retrieveSubjects(macros, user, codeSur, db);
                            // Recupera la strutture collegate al processo dell'intervista corrente
                            ArrayList<DepartmentBean> structsAsArray = filter(structsAsMap, params);
                            // Recupera i soggetti terzi collegati al processo dell'intervista corrente
                            ArrayList<DepartmentBean> subjectsAsArray = filter(subjectsAsMap, params);
                            // Calcola tutti i valori degli indicatori e li restituisce in una mappa, indicizzati per nome
                            indicators = compute(questionsByIndicator, answersByQuestions, decantIndicators(indicatorsAsList), structsAsArray, subjectsAsArray);
                            // Condiziona il recupero di tutte le risposte o solo quelle valide a eventuale parametro
                            if (!mess.equalsIgnoreCase("getAll")) {
                                answers = filter(answers);
                            }
                            // Prepara data intervista di cui si vogliono visualizzare le risposte
                            if (params.get(PARAM_SURVEY).get("d") != null && !params.get(PARAM_SURVEY).get("d").equals(VOID_STRING)) {
                                questDate = Utils.format(params.get(PARAM_SURVEY).get("d"));
                            }
                            // Prepara ora intervista di cui si vogliono visualizzare le risposte
                            if (params.get(PARAM_SURVEY).get("t") != null && !params.get(PARAM_SURVEY).get("t").equals(VOID_STRING)) {
                                String questTimeAsString = params.get(PARAM_SURVEY).get("t").replaceAll("_", ":");
                                questTime = Utils.format(questTimeAsString, TIME_SQL_PATTERN);
                            }                            
                        } else if (part.equalsIgnoreCase(PART_SELECT_QSS)) {
                            /* ************************************************ *
                             *          SELECT List of Interview Part           *
                             * ************************************************ */
                            interviews = db.getInterviewsBySurvey(user, ConfigManager.getSurvey(codeSur));
                        }
                        fileJspT = nomeFile.get(part);
                    } else {
                        /* ************************************************ *
                         *          SELECT List of Interview Part           *
                         * ************************************************ */
                        interviews = db.getInterviewsBySurvey(user, ConfigManager.getSurvey(codeSur));
                        fileJspT = nomeFileElencoQuest;
                    }
                }
            } else {
                // Se siamo qui vuol dire che l'identificativo della rilevazione non è significativo, il che vuol dire che qualcuno ha pasticciato con l'URL
                HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
                ses.invalidate();
                String msg = FOR_NAME + "Qualcuno ha tentato di inserire un indirizzo nel browser avente un codice rilevazione non valido!.\n";
                LOG.severe(msg);
                throw new CommandException("Attenzione: indirizzo richiesto non valido!\n");
            }
        }  catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di valori dal db.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Si e\' verificato un problema in una conversione di tipo.\n";
            LOG.severe(msg);
            throw new CommandException(msg + cce.getMessage(), cce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        /* ******************************************************************** *
         *              Settaggi in request dei valori calcolati                *
         * ******************************************************************** */
        // Imposta nella request elenco completo strutture
        if (structs != null) {
            req.setAttribute("strutture", structs);
        }
        // Imposta nella request elenco completo strutture sotto forma di dictionary
        if (flatStructs != null) {
            req.setAttribute("elencoStrutture", flatStructs);
        }
        // Imposta nella request elenco completo processi
        if (macros != null) {
            req.setAttribute("processi", macros);
        }
        // Imposta nella request elenco completo quesiti raggruppati per ambito
        if (flatQuestions != null) {
            req.setAttribute("elencoQuesiti", flatQuestions);
        }
        // Imposta l'eventuale lista di inteviste trovate
        if (interviews != null) {
            req.setAttribute("elencoInterviste", interviews);
        }
        // Imposta nella request elenco completo domande e risposte di un'intervista
        if (answers != null) {
            req.setAttribute("elencoRisposte", answers);
        }
        // Imposta nella request elenco completo valori degli indicatori
        if (indicators != null) {
            req.setAttribute("indicatori", indicators);
        }
        // Imposta l'eventuale indirizzo a cui redirigere
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }
        // Imposta struttura contenente tutti i parametri di navigazione già estratti
        if (!params.isEmpty()) {
            req.setAttribute("params", params);
        }
        // Imposta nella request data di un'intervista cercata
        if (questDate != null) {
            req.setAttribute("dataRisposte", questDate);
        }
        // Imposta nella request ora di un'intervista cercata
        if (questTime != null) {
            req.setAttribute("oraRisposte", questTime);
        }
        /* Imposta nella request le breadcrumbs in caso siano state personalizzate
        if (bC != null) {
            req.removeAttribute("breadCrumbs");
            req.setAttribute("breadCrumbs", bC);
        }*/
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }
    
    
    /* **************************************************************** *
     *  Metodi di caricamento dei parametri in strutture indicizzabili  *                     
     *                              (load)                              *
     * **************************************************************** */
    
    /**
     * <p>Valorizza per riferimento una mappa contenente tutti i valori 
     * parametrici riscontrati sulla richiesta.</p>
     * 
     * @param part          la sezione del sito corrente
     * @param parser        oggetto per la gestione assistita dei parametri di input, gia' pronto all'uso
     * @param formParams    mappa da valorizzare per riferimento (ByRef)
     * @throws CommandException se si verifica un problema nella gestione degli oggetti data o in qualche tipo di puntamento
     * @throws AttributoNonValorizzatoException se si fa riferimento a un attributo obbligatorio di bean che non viene trovato
     */
    public static void loadParams(String part, 
                                  ParameterParser parser,
                                  HashMap<String, LinkedHashMap<String, String>> formParams)
                           throws CommandException, 
                                  AttributoNonValorizzatoException {
        LinkedHashMap<String, String> struct = new LinkedHashMap<>();
        LinkedHashMap<String, String> proat = new LinkedHashMap<>();
        LinkedHashMap<String, String> answs = new LinkedHashMap<>();
        LinkedHashMap<String, String> survey = new LinkedHashMap<>();
        LinkedHashMap<String, String> quest = new LinkedHashMap<>();
        //LinkedHashMap<String, String> risk = new LinkedHashMap<>();
        /* **************************************************** *
         *     Caricamento parametro di Codice Rilevazione      *
         * **************************************************** */      
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter("r", DASH);
        // Recupera l'oggetto rilevazione a partire dal suo codice
        CodeBean surveyAsBean = ConfigManager.getSurvey(codeSur);
        // Inserisce l'ìd della rilevazione come valore del parametro
        survey.put(PARAM_SURVEY, String.valueOf(surveyAsBean.getId()));
        // Aggiunge data e ora, se le trova
        survey.put("d", parser.getStringParameter("d", VOID_STRING));
        survey.put("t", parser.getStringParameter("t", VOID_STRING));
        // Aggiunge il tutto al dizionario dei parametri
        formParams.put(PARAM_SURVEY, survey);
        /* **************************************************** *
         *     Caricamento parametri di Scelta Struttura        *
         * **************************************************** */        
        struct.put("liv1",  parser.getStringParameter("sliv1", VOID_STRING));
        struct.put("liv2",  parser.getStringParameter("sliv2", VOID_STRING));
        struct.put("liv3",  parser.getStringParameter("sliv3", VOID_STRING));
        struct.put("liv4",  parser.getStringParameter("sliv4", VOID_STRING));
        formParams.put(PART_SELECT_STR, struct);
        /* **************************************************** *
         *      Caricamento parametri di Scelta Processo        *
         * **************************************************** */
        proat.put("liv1",    parser.getStringParameter("pliv1", VOID_STRING));
        proat.put("liv2",    parser.getStringParameter("pliv2", VOID_STRING));
        proat.put("liv3",    parser.getStringParameter("pliv3", VOID_STRING));
        formParams.put(PART_PROCESS, proat);
        /* **************************************************** *
         *    Caricamento parametri di Compilazione Quesiti     *
         * **************************************************** */
        if (part.equals(PART_SELECT_QST)) {
            // Carica la tabella che ad ogni rilevazione associa il numero di quesiti corrispondenti
            questionAmounts = ConfigManager.getQuestionAmount();
            // Recupera il numero di quesiti associati alla rilevazione
            int limit = questionAmounts.get(codeSur).intValue();
            for (int i = NOTHING; i < limit; i++) {
                answs.put("quid" + String.valueOf(i),  parser.getStringParameter("Q" + String.valueOf(i) + "-id", VOID_STRING));
                answs.put("risp" + String.valueOf(i),  parser.getStringParameter("Q" + String.valueOf(i), VOID_STRING));
                answs.put("note" + String.valueOf(i),  parser.getStringParameter("Q" + String.valueOf(i) + "-note", VOID_STRING));
            }
            formParams.put(PART_SELECT_QST, answs);
        }
        if (part.equals(PART_RESUME_QST)) {
            // Recupera gli estremi del quesito di cui aggiornare la risposta
            quest.put("quid",    parser.getStringParameter("q-id", VOID_STRING));
            quest.put("risp",    parser.getStringParameter("q-risp", VOID_STRING));
            quest.put("note",    parser.getStringParameter("q-note", VOID_STRING));
            formParams.put(PART_RESUME_QST, quest);
        }
    }
    
    
    /**
     * <p>Restituisce una mappa contenente gli estremi identificanti
     * una singola intervista.</p> 
     * 
     * @param codeSur       codice rilevazione
     * @param interview     oggetto intervista, contenente tutti i parametri al proprio interno, da estrarre e inserire nella mappa restituita
     * @return <code>HashMap&lt;String, LinkedHashMap&lt;String, String&gt;&gt;</code> - struttura di tipo Dictionary, o Mappa ordinata, avente per chiave il valore del parametro p, e per valore una mappa contenente i relativi parametri
     * @throws CommandException se si verifica un problema nella gestione degli oggetti data o in qualche tipo di puntamento
     * @throws AttributoNonValorizzatoException se si fa riferimento a un attributo obbligatorio di bean che non viene trovato
     */
    public static HashMap<String, LinkedHashMap<String, String>> loadInterviewParams(String codeSur,
                                                                                     InterviewBean interview)
                                                                              throws CommandException, 
                                                                                     AttributoNonValorizzatoException {
        LinkedHashMap<String, String> struct = new LinkedHashMap<>();
        LinkedHashMap<String, String> proat = new LinkedHashMap<>();
        String liv2, liv3, liv4;
        String pro2, pro3;
        LinkedHashMap<String, String> survey = new LinkedHashMap<>();
        HashMap<String, LinkedHashMap<String, String>> formParams = new HashMap<>();
        /* **************************************************** *
         *     Caricamento parametro di Codice Rilevazione      *
         * **************************************************** */      
        // Recupera l'oggetto rilevazione a partire dal suo codice
        CodeBean surveyAsBean = ConfigManager.getSurvey(codeSur);
        // Inserisce l'ìd della rilevazione come valore del parametro
        survey.put(PARAM_SURVEY, String.valueOf(surveyAsBean.getId()));
        // Aggiunge data e ora
        survey.put("d", interview.getDataUltimaModifica().toString());
        survey.put("t", interview.getOraUltimaModifica().toString());
        // Aggiunge il tutto al dizionario dei parametri
        formParams.put(PARAM_SURVEY, survey);
        /* **************************************************** *
         *     Caricamento parametri di Struttura Scelta        *
         * **************************************************** */        
        struct.put("liv1",  interview.getStruttura().getInformativa());
        liv2 = (interview.getStruttura().getFiglie() != null) ? interview.getStruttura().getFiglie().get(0).getInformativa() : VOID_STRING; 
        struct.put("liv2", liv2);
        struct.put("liv3", VOID_STRING);
        struct.put("liv4", VOID_STRING);
        if (liv2 != VOID_STRING) {
            liv3 = (interview.getStruttura().getFiglie().get(0).getFiglie() != null) ? interview.getStruttura().getFiglie().get(0).getFiglie().get(0).getInformativa() : VOID_STRING;
            struct.replace("liv3",  liv3);
            if (liv3 != VOID_STRING) {
                liv4 = (interview.getStruttura().getFiglie().get(0).getFiglie().get(0).getFiglie() != null) ? interview.getStruttura().getFiglie().get(0).getFiglie().get(0).getFiglie().get(0).getInformativa() : VOID_STRING;
                struct.replace("liv4",  liv4);
            }
        }
        formParams.put(PART_SELECT_STR, struct);
        /* **************************************************** *
         *       Caricamento parametri di Processo Scelto       *
         * **************************************************** */
        proat.put("liv1",    interview.getProcesso().getInformativa());
        pro2 = (interview.getProcesso().getProcessi() != null) ? interview.getProcesso().getProcessi().get(0).getInformativa() : VOID_STRING;
        proat.put("liv2", pro2);
        proat.put("liv3", VOID_STRING);
        if (pro2 != VOID_STRING) {
            pro3 = (interview.getProcesso().getProcessi().get(0).getProcessi() != null) ? interview.getProcesso().getProcessi().get(0).getProcessi().get(0).getInformativa() : VOID_STRING;
            proat.put("liv3", pro3);
        }
        formParams.put(PART_PROCESS, proat);
        return formParams;
    }
    
    
    /* **************************************************************** *
     *                  Metodi di recupero dei dati                     *                     
     *                            (retrieve)                            *
     * **************************************************************** */
    
    /**
     * <p>Estrae l'elenco dei quesiti e, per ogni quesito figlio trovato,
     * lo valorizza con gli attributi aggiuntivi (tipo, formulazione, etc.)</p>
     * 
     * @param user          utente loggato
     * @param codeSurvey    codice testuale della rilevazione
     * @param idQ           identificativo del quesito 
     * @param getAll        flag specificante, se vale -1, che si vogliono recuperare tutte le strutture collegate a tutti i macro/processi
     * @param db            databound gia' istanziato
     * @return <code>ArrayList&lt;QuestionBean&gt;</code> - ArrayList di quesiti trovati completi di quesiti figli (quesiti "di cui")
     * @throws CommandException se si verifica un problema nella query o nell'estrazione, nel recupero di valori o in qualche altro tipo di puntamento
     */
    public static ArrayList<QuestionBean> retrieveQuestions(PersonBean user,
                                                            String codeSurvey,
                                                            int idQ,
                                                            int getAll,
                                                            DBWrapper db)
                                                     throws CommandException {
        // Recupera l'oggetto rilevazione a partire dal suo codice
        CodeBean survey = ConfigManager.getSurvey(codeSurvey);
        // Dichiara la lista di quesiti finiti
        ArrayList<QuestionBean> richQuestions = new ArrayList<>();
        try {
            // Chiama il metodo (ricorsivo) del databound che estrae i quesiti valorizzati
            ArrayList<QuestionBean> questions = db.getQuestions(user, survey, idQ, getAll);
            // Cicla sui quesiti trovati
            for (QuestionBean current : questions) {
                // Per ogni quesito verifica se ha figli
                ArrayList<QuestionBean> childQuestions = current.getChildQuestions();
                // Se li ha:
                if (childQuestions != null) {
                    // Per ogni figlio (i figli hanno solo gli id)
                    for (int i = NOTHING; i < childQuestions.size(); i++) {
                        // Recupera il figlio
                        QuestionBean child = childQuestions.get(i);
                        // Lo arricchisce con il tipo e gli altri attributi
                        QuestionBean richChild = db.getQuestions(user, survey, child.getId(), child.getId()).get(NOTHING);
                        // Sostituisce il tipo "arricchito" al tipo "povero"
                        childQuestions.set(i, richChild);
                    }
                    current.setChildQuestions(childQuestions);
                }
                richQuestions.add(current);
            }
            return richQuestions;
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di valori dal db.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }

    
    /**
     * <p>Estrae gli identificativi dei quesiti 
     * e li indicizza per codice di indicatore.</p>
     * 
     * @param user          utente loggato
     * @param answers       lista delle risposte
     * @param survey        oggetto rilevazione
     * @param db            databound gia' istanziato
     * @return <code>HashMap&lt;String&comma; LinkedList&lt;Integer&gt;&gt;</code> - mappa degli identificativi dei quesiti corrispondenti a ciascun indicatore
     * @throws WebStorageException se si verifica un problema a livello di query o di estrazione
     * @throws CommandException se si verifica un problema nel recupero di valori o in qualche altro tipo di puntamento
     */
    public static HashMap<String, LinkedList<Integer>> retrieveQuestionsByIndicators(PersonBean user, 
                                                                                     ArrayList<QuestionBean> answers, 
                                                                                     CodeBean survey, 
                                                                                     DBWrapper db)
                                                                              throws WebStorageException, 
                                                                                     CommandException {
        // Recupera i quesiti per il calcolo degli indicatori
        return db.getQuestionsByIndicator(user, survey);
    }
    
    
    /* **************************************************************** *
     *                   Metodi di travaso dei dati                     *                     
     *                    (decant, filter, purge)                       *
     * **************************************************************** */
    
    /**
     * <p>Travasa una struttura vettoriale in una corrispondente struttura 
     * di tipo Dictionary, HashMap, in cui le chiavi sono rappresentate 
     * da oggetti String e i valori sono rappresentati dalle elenchi di figlie 
     * associate alla chiave.</p>
     * 
     * @param objects struttura vettoriale contenente gli oggetti da travasare
     * @param part    parametro di navigazione identificante la tipologia di oggetti da considerare
     * @return <code>HashMap&lt;?&comma; ?&gt;</code> - struttura di tipo Dictionary, o Mappa ordinata, avente per chiave il codice del nodo, e per valore il Vector delle sue figlie
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    private static HashMap<?,?> decant(ArrayList<?> objects, 
                                       String part)
                                throws CommandException {
        if (part.equals(PART_SELECT_STR)) {
            return decantStructs((ArrayList<DepartmentBean>) objects);
        /*} else if(part.equals(PART_SELECT_QST)) {
            return decantQuestions((ArrayList<QuestionBean>) objects);*/
        }
        String msg = FOR_NAME + "Valore di \'part\' non gestito.\n";
        LOG.severe(msg);
        throw new CommandException(msg);
    }
    
    
    /**
     * <p>Travasa una struttura vettoriale matriciale (Vector con Vector con Vector etc.) 
     * di DepartmentBean, ciascuno dei quali pu&ograve; contenere un Vector di 
     * figlie, in una corrispondente struttura di tipo Dictionary, HashMap, 
     * in cui le chiavi sono rappresentate da oggetti String e i valori
     * sono rappresentati dalle figlie del nodo avente codice corrispondente
     * alla chiave.</p>
     * <p>&Egrave; utile per un accesso pi&uacute; diretto alle figlie
     * di ogni struttura, evitando di dover ogni volta ciclare la struttura
     * matriciale fino al nodo di cui si vogliono ottenere le strutture figlie.</p>
     *
     * @param structs   struttura vettoriale di DepartmentBean da travasare in HashMap
     * @return <code>HashMap&lt;String&comma; Vector&lt;DepartmentBean&gt;&gt;</code> - struttura di tipo Dictionary, o Mappa ordinata, avente per chiave il codice del nodo, e per valore il Vector delle sue figlie
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    public static HashMap<String, Vector<DepartmentBean>> decantStructs(ArrayList<DepartmentBean> structs)
                                                                 throws CommandException {
        HashMap<String, Vector<DepartmentBean>> flatStructs = new HashMap<>();
        try {
            for (DepartmentBean l1 : structs) {
                String keyL1 = l1.getExtraInfo().getCodice(); 
                Vector<DepartmentBean> vL2 = l1.getFiglie();
                flatStructs.put(keyL1, vL2);
                for (DepartmentBean l2 : vL2) {
                    String keyL2 = l2.getExtraInfo().getCodice(); 
                    Vector<DepartmentBean> vL3 = l2.getFiglie();
                    flatStructs.put(keyL2, vL3);
                    for (DepartmentBean l3 : vL3) {
                        String keyL3 = l3.getExtraInfo().getCodice(); 
                        Vector<DepartmentBean> vL4 = l3.getFiglie();
                        flatStructs.put(keyL3, vL4);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento fuori tabella.\n" + aiobe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, aiobe);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Si e\' verificato un problema di conversione di tipo.\n" + cce.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, cce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento.\n" + npe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel travaso di un Vector in un Dictionary.\n" + e.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, e);
        }
        return flatStructs;
    }
    
    
    /**
     * <p>Prende in input una struttura vettoriale di QuestionBean 
     * e una di ambiti cui i quesiti appartengono, e riconduce ogni quesito
     * al proprio ambito, restituendo una struttura associativa (dictionary)
     * in cui la chiave è costituita dall'ambito e il valore l'array
     * di quesiti associati.</p>
     *
     * @param questions ArrayList di QuestionBean da indicizzare per ambito
     * @param ambits    ArrayList di ambiti cui ricondurre i quesiti
     * @return <code>LinkedHashMap&lt;ItemBean&comma; ArrayList&lt;QuestionBean&gt;&gt;</code> - struttura di tipo Dictionary, o Mappa ordinata, avente per chiave l'ambito e per valore il Vector dei suoi quesiti
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    private static LinkedHashMap<ItemBean, ArrayList<QuestionBean>> decantQuestions(ArrayList<QuestionBean> questions, 
                                                                                    ArrayList<ItemBean> ambits)
                                                                             throws CommandException {
        LinkedHashMap<ItemBean, ArrayList<QuestionBean>> questionsByAmbit = new LinkedHashMap<>();
        try {
            for (ItemBean ambit : ambits) {
                int loadedQs = NOTHING;
                int key = ambit.getId();
                ArrayList<QuestionBean> qs = new ArrayList<>();
                for (QuestionBean q : questions) {
                    if (q.getCod1() == key) {
                        qs.add(q);
                        loadedQs += ELEMENT_LEV_1;
                        // Se abbiamo caricato tutti i questiti dell'ambito...
                        if (loadedQs == ambit.getLivello()) {
                            break;  // ...non ha senso continuare i confronti
                        }
                    }
                }
                questionsByAmbit.put(ambit, qs);
            }
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento fuori tabella.\n" + aiobe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, aiobe);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Si e\' verificato un problema di conversione di tipo.\n" + cce.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, cce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento.\n" + npe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel travaso di un Vector in un Dictionary.\n" + e.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, e);
        }
        return questionsByAmbit;
    }
    
    
    /**
     * <p>Prende in input una struttura vettoriale di QuestionBean 
     * contenenti internamente le risposte e scarta le domande aventi 
     * risposta e note entrambe vuote.</p>
     *
     * @param questions ArrayList di QuestionBean da indicizzare per ambito
     * @return <code>ArrayList&lt;QuestionBean&gt;</code> - lista contenente solo le domande con risposte
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    public static ArrayList<QuestionBean> filter(ArrayList<QuestionBean> questions)
                                          throws CommandException {
        ArrayList<QuestionBean> questionsWithAnswers = new ArrayList<>();
        try {
            for (QuestionBean q : questions) {
                if ((q.getAnswer().getNome() != null && !(q.getAnswer().getNome().equals(VOID_STRING))) || 
                    (q.getAnswer().getInformativa() != null && !(q.getAnswer().getInformativa().equals(VOID_STRING)))) {
                    questionsWithAnswers.add(q);
                }
            }
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento fuori tabella.\n" + aiobe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, aiobe);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Si e\' verificato un problema di conversione di tipo.\n" + cce.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, cce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento.\n" + npe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel travaso di un Vector in un Dictionary.\n" + e.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, e);
        }
        return questionsWithAnswers;
    }
    
    
    /**
     * <p>Prende in input una struttura vettoriale di quesiti, ciascuno
     * contenente internamente la relativa risposta e restituisce 
     * una mappa di risposte, ciascuna indicizzata per identificativo 
     * quesito.</p>
     * 
     * @param questions ArrayList di QuestionBean rappresentanti ciascuno una domanda con risposta 
     * @param answers   ArrayList di QuestionBean rappresentanti ciascuno una risposta con domanda
     * @return <code>HashMap&lt;Integer&comma; QuestionBean&gt;</code> - struttura di tipo Dictionary, o Mappa ordinata, avente per chiave l'id quesito e per valore l'oggetto risposta
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    public static HashMap<Integer, QuestionBean> decantAnswers(ArrayList<QuestionBean> questions, 
                                                               ArrayList<QuestionBean> answers)
                                                        throws CommandException {
        HashMap<Integer, QuestionBean> answersByQuestions = new HashMap<>();
        try {
            for (QuestionBean question : questions) {
                int idQ = question.getId();
                for (QuestionBean answer : answers) {
                    if (answer.getAnswer().getLivello() == idQ) {
                        answersByQuestions.put(new Integer(idQ), answer);
                        break;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento fuori tabella.\n" + aiobe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, aiobe);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Si e\' verificato un problema di conversione di tipo.\n" + cce.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, cce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento.\n" + npe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel travaso di un Vector in un Dictionary.\n" + e.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, e);
        }
        return answersByQuestions;
    }
    
    
    /**
     * <p>Prende in input una struttura vettoriale di indicatori 
     * (solo gli estremi, non i valori)
     * li indicizza per codice e li restituisce in una tabella indicizzata.</p>
     * 
     * @param indicators ArrayList di CodeBean rappresentanti ciascuno un indicatore 
     * @return <code>HashMap&lt;String&comma; CodeBean&gt;</code> - struttura di tipo Dictionary, o Mappa ordinata, avente per chiave il codice indicatore e per valore l'oggetto indicatore
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    public static HashMap<String, CodeBean> decantIndicators(ArrayList<CodeBean> indicators)
                                                      throws CommandException {
        HashMap<String, CodeBean> indicatorsByCode = new HashMap<>();
        try {
            for (CodeBean value : indicators) {
                String key = value.getNome();
                indicatorsByCode.put(key, value);
            }
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento fuori tabella.\n" + aiobe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, aiobe);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Si e\' verificato un problema di conversione di tipo.\n" + cce.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, cce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento.\n" + npe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel travaso di un ArrayList in un Dictionary.\n" + e.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, e);
        }
        return indicatorsByCode;
    }
    
    
    /**
     * <p>Prende in input una mappa di elementi indicizzati per id di
     * processo at (livello 2) e una mappa di parametri, estrae da quest'ultima
     * l'identificativo del processo at (livello 2) e recupera dalla mappa
     * che accetta come argomento soltanto gli elementi strutturali collegati al
     * processo in questione, tramite le sue fasi.</p>
     * 
     * @param map       tabella di elementi strutturali (strutture di organigramma, soggetti contingenti...) indicizzati per id di processo_at incapsulato in un Wrapper di tipo primitivo
     * @param params    mappa contenente i parametri di navigazione, indicizzati per valore del parametro di navigazione
     * @return <code>ArrayList&lt;DepartmentBean&gt;&gt;</code> - lista ordinata di strutture collegate all'id di processo contenuto nella mappa di parametri
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    private static ArrayList<DepartmentBean> filter(final HashMap<Integer, ArrayList<DepartmentBean>> map,
                                                    final HashMap<String, LinkedHashMap<String, String>> params)
                                             throws CommandException {
        ArrayList<DepartmentBean> elements = null;
        try {
            // Dizionario dei parametri dei processi collegati all'intervista
            LinkedHashMap<String, String> proc = params.get(PART_PROCESS);
            String idAsString = proc.get("liv2").substring(NOTHING, proc.get("liv2").indexOf(DOT));
            int idPat = Integer.parseInt(idAsString);
            Integer idPatAsInteger = new Integer(idPat);
            elements = map.get(idPatAsInteger);
        } catch (NumberFormatException nfe) {
            String msg = FOR_NAME + "Si e\' verificato un problema nella conversione di interi.\n" + nfe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, nfe);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Si e\' verificato un problema di conversione di tipo.\n" + cce.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, cce);
        } catch (RuntimeException re) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento.\n" + re.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, re);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n" + e.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, e);
        }
        return elements;
    }
    
    
    /* **************************************************************** *
     *              Metodi di calcolo degli indicatori                  *                     
     *                          (compute)                               *
     * **************************************************************** */
    
    /**
     * <p>Implementa il controllo lato server sulle risposte che sono necessarie
     * per il calcolo degli indicatori.</p> 
     * 
     * @param algorythmIds      lista id dei quesiti che l'algoritmo di calcolo chiamante stabilisce necessari per effettuare il calcolo del suo valore
     * @param allowedIds        lista id dei quesiti che da db risultano associati all'indicatore chiamante
     * @param answerByQuestion  tabella delle risposte indicizzate per identificativo quesito
     * @param reason            motivo dell'errore
     * @return <code>boolean</code> - true se tutti i controlli sono andati a buon fine, false in caso almeno uno dei controlli sia vero
     * @throws CommandException se si verifica un problema nel recupero di un attributo di un bean o in qualche tipo di puntamento
     */
    private static boolean validateAnswers(LinkedList<Integer> algorythmIds,
                                           LinkedList<Integer> allowedIds,
                                           HashMap<Integer, QuestionBean> answerByQuestion,
                                           StringBuffer reason)
                                    throws CommandException {
        try {
            // I controllo: gli id quesiti che servono all'algoritmo devono corrispondere agli id quesiti associati all'indicatore
            for (Integer idQ : algorythmIds) {
                if (!allowedIds.contains(idQ)) {
                    reason.append("Il quesito ID" + idQ + " non risulta tra quelli associati all\'indicatore ");
                    return false;
                }
            }
            // Controlli sull'esistenza e coerenza della risposta
            for (Integer idQ : algorythmIds) {
                // Per ogni quesito recupera il tipo di quesito
                QuestionBean question = answerByQuestion.get(idQ);
                // II controllo: nessun quesito puo' valere null
                if (question == null) {
                    reason.append("Quesito ID " + idQ + " nullo ");
                    return false;
                }
                // III controllo: se il quesito non è un "di cui"
                if (question.getParentQuestion() == null) {
                    // IV controllo: se non è un "di cui" nessuna risposta puo' valere (null)
                    if (question.getNome() == null) {
                        reason.append("Risposta al quesito " + question.getCodice() + " nulla ");
                        return false;
                    }
                    // V controllo: la risposta deve essere congruente con il tipo di quesito
                    if (question.getTipo().getId() == ELEMENT_LEV_1) {        // Se il quesito è di tipo Si/No
                        // ...si applica il principio del terzo escluso
                        if (!(question.getAnswer().getNome().equalsIgnoreCase("SI") || question.getAnswer().getNome().equalsIgnoreCase("NO"))) {
                            // cioè la risposta dev'essere o "SI" o "NO", non c'è una terza possibilità, che pertanto resta esclusa
                            reason.append("Risposta al quesito " + question.getCodice() + " non valida ");
                            return false;
                        }
                    } else if (question.getTipo().getId() == ELEMENT_LEV_2) { // Se il quesito è di tipo numerico
                        // ...la risposta deve essere convertibile in un numero
                        try {
                            Integer.parseInt(question.getAnswer().getNome());
                            // Se la risposta non è numerica, e il tipo di quesito è numerico, non puo' calcolare il valore dell'indicatore
                        } catch (NumberFormatException nfe) {
                            String msg = "Risposta al quesito " + question.getCodice() + " non numerica ";
                            reason.append(msg);
                            LOG.severe(msg + nfe.getMessage());
                            return false;
                        }
                    } else if (question.getTipo().getId() == ELEMENT_LEV_3) { // Se il quesito è di tipo percentuale, non solo deve essere convertibile ma amche compreso tra 0.00 e 100.00
                        // ...la risposta deve essere convertibile in un numero
                        try {
                            float value = Float.parseFloat(question.getAnswer().getNome());
                            // Non solo numerico, ma compreso tra 0 e 100
                            if ((value < NOTHING) || (value > 100) ) {
                                reason.append("Risposta al quesito " + question.getCodice() + " non compresa tra 0.00 e 100.00 ");
                                return false;
                            }
                        } catch (NumberFormatException nfe) {
                            String msg = "Risposta al quesito " + question.getCodice() + " non percentuale ";
                            reason.append(msg);
                            LOG.severe(msg + nfe.getMessage());
                            return false;
                        }
                    } else if (question.getTipo().getId() == ELEMENT_LEV_4) { // Se il quesito è di tipo descrittivo
                        // ...non puo' essere stringa vuota
                        if (question.getAnswer().getNome().equals(VOID_STRING)) {
                            reason.append("Risposta al quesito " + question.getCodice() + " non valorizzata ");
                            return false;
                        }
                    }
                }
            }
            // Se nessuno dei controlli precedenti è vero, allora, per esclusione, la risposta è valida
            return true;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Calcola e restituisce una tabella avente come chiavi i codici degli
     * indicatori e come valori corrispondenti oggetti valorizzati con ogni
     * possibile attributo che sia utile per la visualizzazione dell'indicatore 
     * stesso.<br />
     * Siccome la logica di calcolo di ogni indicatore &egrave; diversa da quella
     * di ogni altro indicatore, il presente metodo richiama lo specifico metodo 
     * di calcolo di ciascun indicatore di probabilit&agrave; e di ciascun 
     * indicatore di impatto.<br />
     * Acquisite queste informazioni, richiama i metodi di calcolo degli 
     * indici sintetici P ed I e infine, sulla base di questi, calcola il valore 
     * del giudizio sintetico P x I, che memorizza in un oggetto 
     * associato alla chiave PI.</p>
     * <p>Il metodo &egrave; pensato per essere richiamato nel contesto di 
     * uno specifico processo organizzativo censito a fini anticorruttivi
     * (processo_at); l'hoverhead legato al richiamo ciclico, laddove &ndash;
     * come nei report aggregati &ndash; sia necessario calcolare gli indicatori 
     * di ogni processo, viene gestito attraverso un apposito meccanismo
     * di caching su disco (db).</p>
     * <p>Di pi&uacute;, al fine di velocizzare i tempi di computazione, viene 
     * implementato il calcolo parallelo per i singoli indicatori, 
     * eseguendo la computazione di ogni indicatore in un thread separato.<br />
     * La tipologia di struttura usata per contenere i valori di tutti 
     * gli indicatori durante l'esecuzione in parallelo dei singoli  
     * threads &egrave; sincronizzata anche se l'uso di una 
     * struttura non sincronizzata (p.es. una LinkedHashMap anzich&eacute;
     * la ConcurrentHashMap adottata) non dovrebbe rappresentare
     * un problema perch&eacute; i vari threads n&eacute; 
     * modificano la struttura di tale mappa, n&eacute; aggiungono 
     * o cancellano elementi indicizzati da una stessa chiave (v. p.es.
     * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashMap.html">
     * <cite>Note that this implementation is not synchronized.</cite></a>).<br />
     * Tuttavia, se si usa direttamente la struttura non sincronizzata, 
     * l'ordine di inserimento delle chiavi dipender&agrave; dal tempo impiegato 
     * da ciascun thread a completare la propria elaborazione, e questo 
     * non &egrave; certo un buon criterio di ordinamento! 
     * I valori caricati dai vari threads nella mappa sincronizzata 
     * senza <cite>order insertion</cite> vengono quindi, alla fine, 
     * dopo aver riunificato tutti i threads, caricati in una mappa ordinata, 
     * che viene passata come valore restituito.</p> 
     * 
     * @param questByIndicator  tabella degli identificativi dei quesiti che, da db, risultano associati a ogni specifico codice indicatore, usato come chiave
     * @param answerByQuestion  tabella in cui ogni id quesito permette di ottenere la relativa risposta
     * @param indicatorByCode   tabella in cui ogni oggetto contenente i dati di un indicatore e' ricavabile utilizzando come chiave il codice dell'indicatore stesso
     * @param structs           lista di strutture coinvolte nell'elaborazione del processo di cui si vogliono calcolare gli indicatori
     * @param subjects          lista di soggetti interessati nell'elaborazione del processo di cui si vogliono calcolare gli indicatori
     * @return LinkedHashMap&lt;String&comma; InterviewBean&gt; - tabella ordinata contenente tutti i valori degli indicatori e delle dimensioni di rischio, indicizzati per nome
     * @throws CommandException se si verifica un problema nel calcolo di un indicatore, nel recupero di dati o in qualche tipo di puntamento
     * @see java.util.LinkedHashMap
     * @see java.util.concurrent.ConcurrentHashMap
     */
    public static LinkedHashMap<String, InterviewBean> compute(HashMap<String, LinkedList<Integer>> questByIndicator, 
                                                               HashMap<Integer, QuestionBean> answerByQuestion,
                                                               HashMap<String, CodeBean> indicatorByCode, 
                                                               ArrayList<DepartmentBean> structs,
                                                               ArrayList<DepartmentBean> subjects) 
                                                        throws CommandException {
        try {
            // Mappa sincronizzata
            ConcurrentHashMap<String, InterviewBean> syncIndicators = new ConcurrentHashMap<>();
            // Mappa per contenere tutti gli indicatori indicizzati per nome
            LinkedHashMap<String, InterviewBean> indicators = new LinkedHashMap<>();
            // Totali di ciascun livello di rischio ottenuti in base agli indicatori di probabilità
            LinkedHashMap<String, Integer> pLev = new LinkedHashMap<>();
            // Totali di ciascun livello di rischio ottenuti in base agli indicatori di impatto
            LinkedHashMap<String, Integer> iLev = new LinkedHashMap<>();
            // Dimensione P
            InterviewBean p = null;
            // Dimensione I
            InterviewBean i = null;
            // Giudizio Sintetico
            InterviewBean pi = null;
            // Create threads for each computation: each indicator is computated with a specific algorithm, implemented in a specific method
            Thread threadP1 = new Thread(() -> {
                try {
                    syncIndicators.put(P1, computeP1(questByIndicator.get(P1), answerByQuestion, indicatorByCode));
                    LOG.info(FOR_NAME + "P1 thread ha finito.\n");
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del 1° thread.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
            });
            Thread threadP2 = new Thread(() -> {
                try {
                    syncIndicators.put(P2, computeP2(questByIndicator.get(P2), answerByQuestion, indicatorByCode));
                    LOG.info(FOR_NAME + "P2 thread ha finito.\n");
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del 2° thread.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
            });
            Thread threadP3 = new Thread(() -> {
                try {
                    syncIndicators.put(P3, computeP3(questByIndicator.get(P3), answerByQuestion, indicatorByCode));
                    LOG.info(FOR_NAME + "P3 thread ha finito.\n");
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del 3° thread.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
            });
            Thread threadP4 = new Thread(() -> {
                try {
                    syncIndicators.put(P4, computeP4(questByIndicator.get(P4), answerByQuestion, indicatorByCode));
                    LOG.info(FOR_NAME + "P4 thread ha finito.\n");
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del 4° thread.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
            });
            Thread threadP5 = new Thread(() -> {
                try {
                    syncIndicators.put(P5, computeP5(questByIndicator.get(P5), answerByQuestion, indicatorByCode));
                    LOG.info(FOR_NAME + "P5 thread ha finito.\n");
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del 5° thread.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
            });
            Thread threadP6 = new Thread(() -> {
                try {
                    syncIndicators.put(P6, computeP6(questByIndicator.get(P6), answerByQuestion, indicatorByCode));
                    LOG.info(FOR_NAME + "P6 thread ha finito.\n");
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del 6° thread.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
            });
            Thread threadP7 = new Thread(() -> {
                try {
                    syncIndicators.put(P7, computeP7(questByIndicator.get(P7), answerByQuestion, indicatorByCode));
                    LOG.info(FOR_NAME + "P7 thread ha finito.\n");
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del 7° thread.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
            });
            Thread threadI1 = new Thread(() -> {
                try {
                    syncIndicators.put(I1, computeI1(questByIndicator.get(I1), answerByQuestion, indicatorByCode));
                    LOG.info(FOR_NAME + "I1 thread ha finito.\n");
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del I1 thread.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
            });
            Thread threadI2 = new Thread(() -> {
                try {
                    syncIndicators.put(I2, computeI2(questByIndicator.get(I2), answerByQuestion, indicatorByCode));
                    LOG.info(FOR_NAME + "I2 thread ha finito.\n");
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del I2 thread.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
            });
            Thread threadI3 = new Thread(() -> {
                try {
                    syncIndicators.put(I3, computeI3(structs, subjects, indicatorByCode));
                    LOG.info(FOR_NAME + "I3 thread ha finito.\n");
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del I3 thread.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
            });
            Thread threadI4 = new Thread(() -> {
                try {
                    syncIndicators.put(I4, computeI4(questByIndicator.get(I4), answerByQuestion, indicatorByCode));
                    LOG.info(FOR_NAME + "I4 thread ha finito.\n");
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del I4 thread.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
            });            
            // Start all threads
            threadP1.start();
            threadP2.start();
            threadP3.start();
            threadP4.start();
            threadP5.start();
            threadP6.start();
            threadP7.start();
            threadI1.start();
            threadI2.start();
            threadI3.start();
            threadI4.start();
            // Wait for all threads to finish
            try {
                threadP1.join();
                threadP2.join();
                threadP3.join();
                threadP4.join();
                threadP5.join();
                threadP6.join();
                threadP7.join();
                threadI1.join();
                threadI2.join();
                threadI3.join();
                threadI4.join();
            } catch (InterruptedException ie) {
                String msg = FOR_NAME + "Si e\' verificato un problema nella join dei threads.\n";
                LOG.severe(msg + ie.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
            /* At this point, all computations are done and stored in the 'sincIndicators' map */
            // Decant syncMap into a map not synchronized but having insertion order
            indicators.put(P1, syncIndicators.get(P1));
            indicators.put(P2, syncIndicators.get(P2));
            indicators.put(P3, syncIndicators.get(P3));
            indicators.put(P4, syncIndicators.get(P4));
            indicators.put(P5, syncIndicators.get(P5));
            indicators.put(P6, syncIndicators.get(P6));
            indicators.put(P7, syncIndicators.get(P7));
            indicators.put(I1, syncIndicators.get(I1));
            indicators.put(I2, syncIndicators.get(I2));
            indicators.put(I3, syncIndicators.get(I3));
            indicators.put(I4, syncIndicators.get(I4));
            /* Computazione legacy:
            //indicators.put(P2, computeP2(questByIndicator.get(P2), answerByQuestion, indicatorByCode));
            //indicators.put(P3, computeP3(questByIndicator.get(P3), answerByQuestion, indicatorByCode));
            //indicators.put(P4, computeP4(questByIndicator.get(P4), answerByQuestion, indicatorByCode));
            //indicators.put(P5, computeP5(questByIndicator.get(P5), answerByQuestion, indicatorByCode));
            //indicators.put(P6, computeP6(questByIndicator.get(P6), answerByQuestion, indicatorByCode));
            //indicators.put(P7, computeP7(questByIndicator.get(P7), answerByQuestion, indicatorByCode));
            //indicators.put(I1, computeI1(questByIndicator.get(I1), answerByQuestion, indicatorByCode));
            //indicators.put(I2, computeI2(questByIndicator.get(I2), answerByQuestion, indicatorByCode));            
            //indicators.put(I3, computeI3(structs, subjects, indicatorByCode));
            //indicators.put(I4, computeI4(questByIndicator.get(I4), answerByQuestion, indicatorByCode));*/
            // Dopo aver calcolato gli indicatori, ripartisce i totali parziali
            pLev = count(indicators, P);
            iLev = count(indicators, I);
            // In base ai totali parziali, calcola P ed I
            p = computeP(pLev, indicatorByCode);
            i = computeI(iLev, indicatorByCode);
            // In base a P ed I calcola PxI
            pi = computePI(p, i);
            // Imposta i valori calcolati nella mappa
            indicators.put(P, p);
            indicators.put(I, i);
            indicators.put(PI, pi);
            return indicators;
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di valori o attributi.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ce.getMessage(), ce);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Restituisce il valore dell'indicatore di probabilit&agrave; P1.<br />
     * Ogni indicatore ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni indicatore viene calcolato in un metodo dedicato.</p> 
     * 
     * @param allowedIds        elenco degli identificativi dei quesiti associati all'indicatore considerato
     * @param answerByQuestion  tabella delle risposte indicizzate per identificativo quesito
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio dell'indicatore, calcolato dall'algoritmo in base alle risposte date ai quesiti associati, e informazioni attinenti
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeP1(LinkedList<Integer> allowedIds, 
                                           HashMap<Integer, QuestionBean> answerByQuestion,
                                           HashMap<String, CodeBean> indicatorByCode) 
                                    throws CommandException {
        try {
            InterviewBean p1 = new InterviewBean();
            ArrayList<QuestionBean> quesiti = new ArrayList<>();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            StringBuffer reason = new StringBuffer(VOID_STRING);
            // Oggetti id quesiti necessari per il calcolo dell'indicatore
            Integer id20 = new Integer(20);
            Integer id21 = new Integer(21);
            LinkedList<Integer> algorythmIds = new LinkedList<>();
            algorythmIds.add(id20);
            algorythmIds.add(id21);
            // Prepara la lista di quesiti che permettono di calcolare l'indicatore
            quesiti.add(answerByQuestion.get(id20));
            quesiti.add(answerByQuestion.get(id21));
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(P);
            // Controlli lato server sulla validità delle risposte
            if (!validateAnswers(algorythmIds, allowedIds, answerByQuestion, reason)) {
                extraInfo.setDescrizioneStatoCorrente(String.valueOf(reason));
                result = ERR;
            } else {
                // 1° test
                if (answerByQuestion.get(id20).getAnswer().getNome().equalsIgnoreCase("SI")) {
                    try {
                        int answerId21AsInt = Integer.parseInt(answerByQuestion.get(id21).getAnswer().getNome());
                        // Se vero 2° test
                        if (answerId21AsInt < 70) {
                            result = LIVELLI_RISCHIO[2];
                        } else {
                            result = LIVELLI_RISCHIO[3];
                        }
                    // Se la risposta non è numerica, non puo' calcolare il valore dell'indicatore
                    } catch (NumberFormatException nfe) {
                        String msg = "Risposta al quesito " + answerByQuestion.get(id21).getCodice() + " non valida ";
                        LOG.severe(FOR_NAME + msg + nfe.getMessage());
                        result = ERR;
                        extraInfo.setDescrizioneStatoCorrente(msg + answerByQuestion.get(id21).getAnswer().getNome());
                    }
                } else {
                    result = LIVELLI_RISCHIO[1];
                }
            }
            // Valorizza e restituisce l'oggetto per l'indicatore
            p1.setNome(P1);
            p1.setInformativa(result);
            p1.setDescrizione(indicatorByCode.get(P1).getInformativa());
            p1.setRisposte(quesiti);
            p1.setProcesso(extraInfo);
            return p1;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Restituisce il valore dell'indicatore di probabilit&agrave; P2.<br />
     * Ogni indicatore ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni indicatore viene calcolato in un metodo dedicato.</p> 
     * 
     * @param allowedIds        elenco degli identificativi dei quesiti associati all'indicatore considerato
     * @param answerByQuestion  tabella delle risposte indicizzate per identificativo quesito
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio per l'indicatore, calcolato dall'algoritmo in base alle risposte date ai quesiti associati, e informazioni attinenti
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeP2(LinkedList<Integer> allowedIds, 
                                           HashMap<Integer, QuestionBean> answerByQuestion,
                                           HashMap<String, CodeBean> indicatorByCode) 
                                    throws CommandException {
        try {
            InterviewBean p2 = new InterviewBean();
            ArrayList<QuestionBean> quesiti = new ArrayList<>();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            StringBuffer reason = new StringBuffer(VOID_STRING);
            // Oggetti id quesiti necessari per il calcolo dell'indicatore
            Integer id1 = new Integer(1);
            Integer id2 = new Integer(2);
            LinkedList<Integer> algorythmIds = new LinkedList<>();
            algorythmIds.add(id1);
            algorythmIds.add(id2);
            // Prepara la lista di quesiti che permettono di calcolare l'indicatore
            quesiti.add(answerByQuestion.get(id1));
            quesiti.add(answerByQuestion.get(id2));
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(P);
            // Controlli lato server sulla validità delle risposte
            if (!validateAnswers(algorythmIds, allowedIds, answerByQuestion, reason)) {
                extraInfo.setDescrizioneStatoCorrente(String.valueOf(reason));
                result = ERR;
            } else {
                // 1° test
                if (answerByQuestion.get(id1).getAnswer().getNome().equalsIgnoreCase("SI")) {
                    // Se vero 2° test
                    if (answerByQuestion.get(id2).getAnswer().getNome().equalsIgnoreCase("SI")) {
                        result = LIVELLI_RISCHIO[1];
                    } else {
                        result = LIVELLI_RISCHIO[2];
                    }
                } else {
                    // Se falso 3° test
                    if (answerByQuestion.get(id2).getAnswer().getNome().equalsIgnoreCase("SI")) {
                        result = LIVELLI_RISCHIO[2];
                    } else {
                        result = LIVELLI_RISCHIO[3];
                    }
                }
            }
            p2.setNome(P2);
            p2.setInformativa(result);
            p2.setDescrizione(indicatorByCode.get(P2).getInformativa());
            p2.setRisposte(quesiti);
            p2.setProcesso(extraInfo);
            return p2;
        } catch (RuntimeException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Restituisce il valore dell'indicatore di probabilit&agrave; P3.<br />
     * Ogni indicatore ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni indicatore viene calcolato in un metodo dedicato.</p> 
     * 
     * @param allowedIds        elenco degli identificativi dei quesiti associati all'indicatore considerato
     * @param answerByQuestion  tabella delle risposte indicizzate per identificativo quesito
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio nell'indicatore e informazioni attinenti
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeP3(LinkedList<Integer> allowedIds, 
                                           HashMap<Integer, QuestionBean> answerByQuestion,
                                           HashMap<String, CodeBean> indicatorByCode) 
                                    throws CommandException {
        try {
            InterviewBean p3 = new InterviewBean();
            ArrayList<QuestionBean> quesiti = new ArrayList<>();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            StringBuffer reason = new StringBuffer(VOID_STRING);
            // Oggetti id quesiti necessari per il calcolo dell'indicatore
            Integer id3 = new Integer(3);
            Integer id4 = new Integer(4);
            Integer id5 = new Integer(5);
            Integer id11 = new Integer(11);
            Integer id12 = new Integer(12);
            LinkedList<Integer> algorythmIds = new LinkedList<>();
            algorythmIds.add(id3);
            algorythmIds.add(id4);
            algorythmIds.add(id5);
            algorythmIds.add(id11);
            algorythmIds.add(id12);
            // Prepara la lista di quesiti che permettono di calcolare l'indicatore
            quesiti.add(answerByQuestion.get(id3));
            quesiti.add(answerByQuestion.get(id4));
            quesiti.add(answerByQuestion.get(id5));
            quesiti.add(answerByQuestion.get(id11));
            quesiti.add(answerByQuestion.get(id12));
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(P);
            // Controlli lato server sulla validità delle risposte
            if (!validateAnswers(algorythmIds, allowedIds, answerByQuestion, reason)) {
                extraInfo.setDescrizioneStatoCorrente(String.valueOf(reason));
                result = ERR;
            } else {
                // Controlla che i quesiti dichiarati risultino associati all'indicatore
                if (allowedIds.contains(id3) && 
                    allowedIds.contains(id4) &&
                    allowedIds.contains(id5) &&
                    allowedIds.contains(id11) &&
                    allowedIds.contains(id12)) {
                    // 1° test
                    if (answerByQuestion.get(id5).getAnswer().getNome().equalsIgnoreCase("SI")) {
                        // Se vero ha finito
                        result = LIVELLI_RISCHIO[3];
                    } else {
                        // 2° test
                        if (answerByQuestion.get(id3).getAnswer().getNome().equalsIgnoreCase("NO") &&
                            answerByQuestion.get(id4).getAnswer().getNome().equalsIgnoreCase("NO") &&
                            answerByQuestion.get(id11).getAnswer().getNome().equalsIgnoreCase("NO") && 
                            answerByQuestion.get(id12).getAnswer().getNome().equalsIgnoreCase("NO")) {
                            result = LIVELLI_RISCHIO[1];
                        } else {
                            result = LIVELLI_RISCHIO[2];
                        }
                    }
                // Se i quesiti associati a P1 non risultano essere quelli considerati c'è un problema da qualche parte
                } else {
                    result = ERR;
                }
            }
            // Valorizza e restituisce l'oggetto per l'indicatore
            p3.setNome(P3);
            p3.setInformativa(result);
            p3.setDescrizione(indicatorByCode.get(P3).getInformativa());
            p3.setRisposte(quesiti);
            p3.setProcesso(extraInfo);
            return p3;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Restituisce il valore dell'indicatore di probabilit&agrave; P4.<br />
     * Ogni indicatore ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni indicatore viene calcolato in un metodo dedicato.<br />
     * I casi mappati per l’indicatore P4 devono essere generati dall'algoritmo
     * implementato dal presente metodo e derivano dalla seguente tabella.<br />
     * <strong>(Nuova tabella di decisione 23.11 v. 1.54 e superiori)</strong>
     * <pre>
     *                       P4
     * (grado di discrezionalità del decisore interno alla PA)
     * RIGA     ID1     ID2     ID13    ID14   RISCHIO
     * --------------------------------------------------
     * 1a       SI      SI      SI|NO   >2      BASSO
     * 2a       NO      SI      NO      >2      BASSO
     * 3a       SI      NO      NO      >2      BASSO
     * 4a       SI      NO      SI      >=2     BASSO
     * 5a       SI|NO   SI      SI      >=2     BASSO
     * 6a       SI      SI|NO   NO      <=2     MEDIO
     * 7a       NO      SI      NO      <=2     MEDIO
     * 8a       NO      SI      SI      <2      MEDIO
     * 9a       NO      NO      SI      *       MEDIO
     * 10a      SI      SI|NO   SI      <2      MEDIO
     * 11a      NO      NO      NO      *       ALTO
     * </pre>
     * <p>Recuerda: n ∈ {N | n<3 ∧ n>=2} means {2}</p>
     * Espandendo i possibili valori si ottengono le disposizioni di valori 
     * (un sottoinsieme di D'(n,k) = n<sup>k</sup>) riportate nella tabella seguente:
     * <pre>
     * ID1 ID2 ID13 ID14    RESULT
     * ------------------------------
     * NO   NO   NO   *      ALTO
     * NO   NO   SI   *      MEDIO
     * NO   SI   SI   >=2    BASSO
     * NO   SI   SI   <2     MEDIO
     * NO   SI   NO   <=2    MEDIO
     * NO   SI   NO   >2     BASSO
     * SI   SI   SI   >=2    BASSO
     * SI   NO   SI   >=2    BASSO
     * SI   SI   SI   <2     MEDIO
     * SI   NO   SI   <2     MEDIO
     * SI   SI   NO   <=2    MEDIO
     * SI   NO   NO   <=2    MEDIO
     * SI   SI   NO   >2     BASSO
     * SI   NO   NO   >2     BASSO    
     * ------------------------------
     * </pre> 
     * L'implementazione del metodo deve essere in grado di generare
     * un risultato consistente per ognuna delle disposizioni classificate.<br /> 
     * L'architettura e l'implementazione dell'algoritmo fanno s&iacute; che
     * possano esistere solo valori computati o, eventualmente, valori erronei 
     * (p.es. una risposta non numerica ad un quesito numerico, oppure 
     * una risposta diversa da {SI,NO} ad un quesito di tipo On/Off) 
     * ma non disposizioni indecidibili.</p> 
     * 
     * @param allowedIds        elenco degli identificativi dei quesiti associati all'indicatore considerato
     * @param answerByQuestion  tabella delle risposte indicizzate per identificativo quesito
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio nell'indicatore, calcolato dall'algoritmo in base alle risposte date ai quesiti associati, e informazioni attinenti 
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeP4(LinkedList<Integer> allowedIds, 
                                           HashMap<Integer, QuestionBean> answerByQuestion,
                                           HashMap<String, CodeBean> indicatorByCode) 
                                    throws CommandException {
        try {
            InterviewBean p4 = new InterviewBean();
            ArrayList<QuestionBean> quesiti = new ArrayList<>();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            StringBuffer reason = new StringBuffer(VOID_STRING);
            // Oggetti id quesiti necessari per il calcolo dell'indicatore
            Integer id1 = new Integer(1);
            Integer id2 = new Integer(2);
            Integer id13 = new Integer(13);
            Integer id14 = new Integer(14);
            LinkedList<Integer> algorythmIds = new LinkedList<>();
            algorythmIds.add(id1);
            algorythmIds.add(id2);
            algorythmIds.add(id13);
            algorythmIds.add(id14);
            // Prepara la lista di quesiti che permettono di calcolare l'indicatore
            quesiti.add(answerByQuestion.get(id1));
            quesiti.add(answerByQuestion.get(id2));
            quesiti.add(answerByQuestion.get(id13));
            quesiti.add(answerByQuestion.get(id14));
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(P);
            // Controlli lato server sulla validità delle risposte
            if (!validateAnswers(algorythmIds, allowedIds, answerByQuestion, reason)) {
                extraInfo.setDescrizioneStatoCorrente(String.valueOf(reason));
                result = ERR;
            } else {
                if (answerByQuestion.get(id1).getAnswer().getNome().equalsIgnoreCase("NO")) {
                    if (answerByQuestion.get(id2).getAnswer().getNome().equalsIgnoreCase("NO")) {
                        if (answerByQuestion.get(id13).getAnswer().getNome().equalsIgnoreCase("NO")) {
                            result = LIVELLI_RISCHIO[3];
                        } else {
                            result = LIVELLI_RISCHIO[2];
                        }
                    } else {
                        if (answerByQuestion.get(id13).getAnswer().getNome().equalsIgnoreCase("SI")) {
                            if (Integer.parseInt(answerByQuestion.get(id14).getAnswer().getNome()) >= ELEMENT_LEV_2) {
                                result = LIVELLI_RISCHIO[1];
                            } else {
                                result = LIVELLI_RISCHIO[2];
                            }
                        } else {
                            if (Integer.parseInt(answerByQuestion.get(id14).getAnswer().getNome()) <= ELEMENT_LEV_2) {
                                result = LIVELLI_RISCHIO[2];
                            } else {
                                result = LIVELLI_RISCHIO[1];
                            }
                        }
                    }
                } else {
                    if (answerByQuestion.get(id13).getAnswer().getNome().equalsIgnoreCase("SI")) {
                        if (Integer.parseInt(answerByQuestion.get(id14).getAnswer().getNome()) >= ELEMENT_LEV_2) {
                            result = LIVELLI_RISCHIO[1];
                        } else {
                            result = LIVELLI_RISCHIO[2];
                        }
                    } else {
                        if (Integer.parseInt(answerByQuestion.get(id14).getAnswer().getNome()) <= ELEMENT_LEV_2) {
                            result = LIVELLI_RISCHIO[2];
                        } else {
                            result = LIVELLI_RISCHIO[1];
                        }
                    }
                }
            }
            // Valorizza e restituisce l'oggetto per l'indicatore
            p4.setNome(P4);
            p4.setInformativa(result);
            p4.setDescrizione(indicatorByCode.get(P4).getInformativa());
            p4.setRisposte(quesiti);
            p4.setProcesso(extraInfo);
            return p4;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }

    
    /**
     * @deprecated
     * <p>Restituisce il valore dell'indicatore di probabilit&agrave; P4.<br />
     * Ogni indicatore ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni indicatore viene calcolato in un metodo dedicato.<br />
     * I casi mappati per l’indicatore P4 devono essere generati dall'algoritmo
     * implementato dal presente metodo e derivano dalla seguente tabella:
     * <pre>
     *                       P4
     * (grado di discrezionalità del decisore interno alla PA)
     * RIGA     ID1      ID2     ID13    ID14    RISCHIO
     * -----------------------------------------
     * 1a       SI       SI|NO   SI|NO   >=2     BASSO
     * 2a       NO       SI      SI|NO   >=2     BASSO
     * 3a       SI|NO    SI      NO      >=2     BASSO
     * 4a       SI       SI|NO   NO      <2      MEDIO
     * 5a       SI|NO    SI      NO      <2      MEDIO
     * 6a       SI       SI|NO   SI      <2      MEDIO
     * 7a       NO       NO      NO      *       ALTO
     * </pre>
     * Espandendo i possibili valori si ottengono le disposizioni di valori 
     * (un sottoinsieme di D'(n,k) = n<sup>k</sup>) riportate nella tabella seguente:
     * <pre>
     * ID1 ID2 ID13 ID14    RESULT
     * ------------------------------
     * (disposizioni 1a riga)
     * SI  SI  SI   >=2     BASSO
     * SI  SI  NO   >=2     BASSO
     * SI  NO  SI   >=2     BASSO
     * SI  NO  NO   >=2     BASSO
     * (disposizioni 2a riga)
     * NO  SI  SI   >=2     BASSO
     * NO  SI  NO   >=2     BASSO
     * (disposizioni 3a riga)
     * SI  SI  NO   >=2     BASSO
     * NO  SI  NO   >=2     rientra nelle disposizioni 2a riga (BASSO)
     * (disposizioni indecidibili)
     * NO  NO  SI   >=2     NON DETERMINABILE
     * ------------------------------
     * (disposizioni 4a riga)
     * SI  SI  NO   <2      MEDIO
     * SI  NO  NO   <2      MEDIO
     * (disposizioni 5a riga)
     * SI  SI  NO   <2      rientra nelle disposizioni 4a riga (MEDIO)
     * NO  SI  NO   <2      MEDIO
     * (disposizioni 6a riga)
     * SI  SI  SI   <2      MEDIO
     * SI  NO  SI   <2      MEDIO
     * ------------------------------
     * (disposizioni 7a riga)
     * NO  NO  NO   *       ALTO
     * </pre>
     * L'implementazione del metodo deve essere in grado di generare
     * un risultato consistente per ognuna di queste disposizioni, nonch&eacute;
     * per eventuali altre non determinate (e che rientrano nelle disposizioni
     * indecidibili).</p> 
     * 
     * @see AuditCommand#computeP4(LinkedList, HashMap, HashMap)
     * @param allowedIds        elenco degli identificativi dei quesiti associati all'indicatore considerato
     * @param answerByQuestion  tabella delle risposte indicizzate per identificativo quesito
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @param deprecated        parametro fittizio che ha lo scopo di cambiare la firma del metodo, in quanto deprecato
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio nell'indicatore, calcolato dall'algoritmo in base alle risposte date ai quesiti associati, e informazioni attinenti 
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     * @deprecated l'algoritmo di calcolo implementato non e' valido perche' non gestisce la granularita' di 3 operatori dell'ufficio; e' necessario raffinarlo facendo riferimento a questo criterio.
     */
    @SuppressWarnings("unused")
    @Deprecated
    private static InterviewBean computeP4(LinkedList<Integer> allowedIds, 
                                           HashMap<Integer, QuestionBean> answerByQuestion,
                                           HashMap<String, CodeBean> indicatorByCode,
                                           boolean deprecated) 
                                    throws CommandException {
        try {
            InterviewBean p4 = new InterviewBean();
            ArrayList<QuestionBean> quesiti = new ArrayList<>();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            StringBuffer reason = new StringBuffer(VOID_STRING);
            // Oggetti id quesiti necessari per il calcolo dell'indicatore
            Integer id1 = new Integer(1);
            Integer id2 = new Integer(2);
            Integer id13 = new Integer(13);
            Integer id14 = new Integer(14);
            LinkedList<Integer> algorythmIds = new LinkedList<>();
            algorythmIds.add(id1);
            algorythmIds.add(id2);
            algorythmIds.add(id13);
            algorythmIds.add(id14);
            // Prepara la lista di quesiti che permettono di calcolare l'indicatore
            quesiti.add(answerByQuestion.get(id1));
            quesiti.add(answerByQuestion.get(id2));
            quesiti.add(answerByQuestion.get(id13));
            quesiti.add(answerByQuestion.get(id14));
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(P);
            // Controlli lato server sulla validità delle risposte
            if (!validateAnswers(algorythmIds, allowedIds, answerByQuestion, reason)) {
                extraInfo.setDescrizioneStatoCorrente(String.valueOf(reason));
                result = ERR;
            } else {
                // Disposizioni di valori generanti rischio ALTO
                if (answerByQuestion.get(id1).getAnswer().getNome().equalsIgnoreCase("NO") &&
                    answerByQuestion.get(id2).getAnswer().getNome().equalsIgnoreCase("NO") &&
                    answerByQuestion.get(id13).getAnswer().getNome().equalsIgnoreCase("NO")) {
                        result = LIVELLI_RISCHIO[3];
                }
                // Disposizioni di valori generanti rischio MEDIO
                else if (Integer.parseInt(answerByQuestion.get(id14).getAnswer().getNome()) < 2 &&
                        (answerByQuestion.get(id1).getAnswer().getNome().equalsIgnoreCase("SI") ||
                         answerByQuestion.get(id2).getAnswer().getNome().equalsIgnoreCase("SI") || 
                         answerByQuestion.get(id13).getAnswer().getNome().equalsIgnoreCase("SI"))) {
                            result = LIVELLI_RISCHIO[2];
                }
                // Disposizioni di valori generanti rischio BASSO
                else if (Integer.parseInt(answerByQuestion.get(id14).getAnswer().getNome()) >= 2) {
                    if (answerByQuestion.get(id1).getAnswer().getNome().equalsIgnoreCase("SI")) {
                        result = LIVELLI_RISCHIO[1];
                    } else if (answerByQuestion.get(id1).getAnswer().getNome().equalsIgnoreCase("NO") && 
                               answerByQuestion.get(id2).getAnswer().getNome().equalsIgnoreCase("SI")) {
                                result = LIVELLI_RISCHIO[1];
                    } else {
                        extraInfo.setDescrizioneStatoCorrente("La combinazione di ID1 ID2 ID13 ID14 (" + 
                                answerByQuestion.get(id1).getAnswer().getNome() + BLANK_SPACE + 
                                answerByQuestion.get(id2).getAnswer().getNome() + BLANK_SPACE + 
                                answerByQuestion.get(id13).getAnswer().getNome() + BLANK_SPACE + 
                                answerByQuestion.get(id14).getAnswer().getNome() + BLANK_SPACE + 
                               ") non permette di determinare il livello di rischio");
                        result = ERR;
                    }
                // Qui sotto finiscono combinazioni residue, p.es. (NO, NO, SI, >=2)
                } else {
                    extraInfo.setDescrizioneStatoCorrente("Combinazione ID1 ID2 ID13 ID14 (" + 
                                                           answerByQuestion.get(id1).getAnswer().getNome() + BLANK_SPACE + 
                                                           answerByQuestion.get(id2).getAnswer().getNome() + BLANK_SPACE + 
                                                           answerByQuestion.get(id13).getAnswer().getNome() + BLANK_SPACE + 
                                                           answerByQuestion.get(id14).getAnswer().getNome() + BLANK_SPACE + 
                                                          ") non erronea ma indecidibile");
                    result = ERR;
                }
            }
            // Valorizza e restituisce l'oggetto per l'indicatore
            p4.setNome(P4);
            p4.setInformativa(result);
            p4.setDescrizione(indicatorByCode.get(P4).getInformativa());
            p4.setRisposte(quesiti);
            p4.setProcesso(extraInfo);
            return p4;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Restituisce il valore dell'indicatore di probabilit&agrave; P5.<br />
     * Ogni indicatore ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni indicatore viene calcolato in un metodo dedicato.</p> 
     * 
     * @param allowedIds        elenco degli identificativi dei quesiti associati all'indicatore considerato
     * @param answerByQuestion  tabella delle risposte indicizzate per identificativo quesito
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio nell'indicatore, calcolato dall'algoritmo in base alle risposte date ai quesiti associati, e informazioni attinenti 
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeP5(LinkedList<Integer> allowedIds, 
                                           HashMap<Integer, QuestionBean> answerByQuestion,
                                           HashMap<String, CodeBean> indicatorByCode) 
                                    throws CommandException {
        try {
            InterviewBean p5 = new InterviewBean();
            ArrayList<QuestionBean> quesiti = new ArrayList<>();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            StringBuffer reason = new StringBuffer(VOID_STRING);
            // Oggetti id quesiti necessari per il calcolo dell'indicatore
            Integer id15 = new Integer(15);
            Integer id17 = new Integer(17);
            Integer id18 = new Integer(18);
            Integer id19 = new Integer(19);
            LinkedList<Integer> algorythmIds = new LinkedList<>();
            algorythmIds.add(id15);
            algorythmIds.add(id17);
            algorythmIds.add(id18);
            algorythmIds.add(id19);
            // Prepara la lista di quesiti che permettono di calcolare l'indicatore
            quesiti.add(answerByQuestion.get(id15));
            quesiti.add(answerByQuestion.get(id17));
            quesiti.add(answerByQuestion.get(id18));
            quesiti.add(answerByQuestion.get(id19));
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(P);
            // Controlli lato server sulla validità delle risposte
            if (!validateAnswers(algorythmIds, allowedIds, answerByQuestion, reason)) {
                extraInfo.setDescrizioneStatoCorrente(String.valueOf(reason));
                result = ERR;
            } else {
             // Vengono gestite tutte le combinazioni
                if (Integer.parseInt(answerByQuestion.get(id18).getAnswer().getNome()) < ELEMENT_LEV_2) {
                    if (Integer.parseInt(answerByQuestion.get(id19).getAnswer().getNome()) < ELEMENT_LEV_2) {
                        if (answerByQuestion.get(id15).getAnswer().getNome().equalsIgnoreCase("SI") && 
                            answerByQuestion.get(id17).getAnswer().getNome().equalsIgnoreCase("SI")) {
                            result = LIVELLI_RISCHIO[2];
                        } else {
                            result = LIVELLI_RISCHIO[3];
                        } // ID19 è >= 2
                    } else if (answerByQuestion.get(id15).getAnswer().getNome().equalsIgnoreCase("SI") && 
                               answerByQuestion.get(id17).getAnswer().getNome().equalsIgnoreCase("SI")) {
                                result = LIVELLI_RISCHIO[1];
                    } else {
                        result = LIVELLI_RISCHIO[2];
                    } // ID18 è >= 2
                } else if (Integer.parseInt(answerByQuestion.get(id19).getAnswer().getNome()) < ELEMENT_LEV_2) {
                    if (answerByQuestion.get(id15).getAnswer().getNome().equalsIgnoreCase("SI") && 
                        answerByQuestion.get(id17).getAnswer().getNome().equalsIgnoreCase("SI")) {
                        result = LIVELLI_RISCHIO[1];
                    } else {
                        result = LIVELLI_RISCHIO[2];
                    } // ID18 >=2 && ID19 >= 2
                } else if (answerByQuestion.get(id15).getAnswer().getNome().equalsIgnoreCase("SI")) {
                    result = LIVELLI_RISCHIO[1];
                } else {
                    result = LIVELLI_RISCHIO[2];
                }
            }   
            // Valorizza e restituisce l'oggetto per l'indicatore
            p5.setNome(P5);
            p5.setInformativa(result);
            p5.setDescrizione(indicatorByCode.get(P5).getInformativa());
            p5.setRisposte(quesiti);
            p5.setProcesso(extraInfo);
            return p5;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Restituisce il valore dell'indicatore di probabilit&agrave; P6.<br />
     * Ogni indicatore ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni indicatore viene calcolato in un metodo dedicato.</p> 
     * 
     * @param allowedIds        elenco degli identificativi dei quesiti associati all'indicatore considerato
     * @param answerByQuestion  tabella delle risposte indicizzate per identificativo quesito
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio nell'indicatore, calcolato dall'algoritmo in base alle risposte date ai quesiti associati, e informazioni attinenti 
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeP6(LinkedList<Integer> allowedIds, 
                                           HashMap<Integer, QuestionBean> answerByQuestion,
                                           HashMap<String, CodeBean> indicatorByCode) 
                                    throws CommandException {
        try {
            // Dichiara e inizializza variabili locali
            InterviewBean p6 = new InterviewBean();
            ArrayList<QuestionBean> quesiti = new ArrayList<>();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            StringBuffer reason = new StringBuffer(VOID_STRING);
            // Prepara oggetti id quesiti necessari per il calcolo dell'indicatore
            Integer id31 = new Integer(31);
            Integer id32 = new Integer(32);
            Integer id35 = new Integer(35);
            Integer id36 = new Integer(36);
            LinkedList<Integer> algorythmIds = new LinkedList<>();
            algorythmIds.add(id31);
            algorythmIds.add(id32);
            algorythmIds.add(id35);
            algorythmIds.add(id36);
            // Prepara la lista di quesiti che permettono di calcolare l'indicatore
            quesiti.add(answerByQuestion.get(id31));
            quesiti.add(answerByQuestion.get(id32));
            quesiti.add(answerByQuestion.get(id35));
            quesiti.add(answerByQuestion.get(id36));
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(P);
            // Controlli lato server sulla validità delle risposte
            if (!validateAnswers(algorythmIds, allowedIds, answerByQuestion, reason)) {
                extraInfo.setDescrizioneStatoCorrente(String.valueOf(reason));
                result = ERR;
            } else {
             // Vengono gestite tutte le combinazioni
                if (answerByQuestion.get(id31).getAnswer().getNome().equalsIgnoreCase("SI")) {
                // ID36 è un "di cui" pertanto la sua correttezza formale NON è stata controllata sopra
                    try {
                        int answerId32AsInt = Integer.parseInt(answerByQuestion.get(id32).getAnswer().getNome());
                        if (answerId32AsInt >= ELEMENT_LEV_1) {
                            if (answerByQuestion.get(id35).getAnswer().getNome().equalsIgnoreCase("SI")) {
                                result = LIVELLI_RISCHIO[1];
                            } else { // ID31 = 'SI' AND ID32 >= 1 AND ID35 ≠ 'SI'
                                if (answerByQuestion.get(id36).getAnswer().getNome().equalsIgnoreCase("SI")) {
                                    result = LIVELLI_RISCHIO[1];
                                } else {
                                    result = LIVELLI_RISCHIO[2];
                                }
                            }
                        } else {    // ID31 = 'SI' AND ID32 < 1
                            extraInfo.setDescrizioneStatoCorrente(String.valueOf("ID31 = 'SI' e ID32 < 1!"));
                            result = ERR;
                        }
                    // Assunta la risposta "SI" al padre, se la risposta al "di cui" non è numerica non puo' calcolare il valore dell'indicatore
                    } catch (NumberFormatException nfe) {
                        String msg = "Risposta al quesito " + answerByQuestion.get(id32).getCodice() + " non valida ";
                        LOG.severe(FOR_NAME + msg + nfe.getMessage());
                        result = ERR;
                        extraInfo.setDescrizioneStatoCorrente(msg + "(\"" + answerByQuestion.get(id32).getAnswer().getNome() + "\")");
                    }   
                } else { // ID31 = 'SI' -> false
                    if (answerByQuestion.get(id36).getAnswer().getNome().equalsIgnoreCase("SI")) {
                        result = LIVELLI_RISCHIO[2];
                    } else {
                        result = LIVELLI_RISCHIO[3];
                    }
                }
            }   
            // Valorizza e restituisce l'oggetto per l'indicatore
            p6.setNome(P6);
            p6.setInformativa(result);
            p6.setDescrizione(indicatorByCode.get(P6).getInformativa());
            p6.setRisposte(quesiti);
            p6.setProcesso(extraInfo);
            return p6;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Restituisce il valore dell'indicatore di probabilit&agrave; P7.<br />
     * Ogni indicatore ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni indicatore viene calcolato in un metodo dedicato.</p> 
     * 
     * @param allowedIds        elenco degli identificativi dei quesiti associati all'indicatore considerato
     * @param answerByQuestion  tabella delle risposte indicizzate per identificativo quesito
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio per l'indicatore, calcolato dall'algoritmo in base alle risposte date ai quesiti associati, e informazioni attinenti
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeP7(LinkedList<Integer> allowedIds, 
                                           HashMap<Integer, QuestionBean> answerByQuestion,
                                           HashMap<String, CodeBean> indicatorByCode) 
                                    throws CommandException {
        try {
            InterviewBean p7 = new InterviewBean();
            ArrayList<QuestionBean> quesiti = new ArrayList<>();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            StringBuffer reason = new StringBuffer(VOID_STRING);
            // Oggetti id quesiti necessari per il calcolo dell'indicatore
            Integer id27 = new Integer(27);
            Integer id29 = new Integer(29);
            LinkedList<Integer> algorythmIds = new LinkedList<>();
            algorythmIds.add(id27);
            algorythmIds.add(id29);
            // Prepara la lista di quesiti che permettono di calcolare l'indicatore
            quesiti.add(answerByQuestion.get(id27));
            quesiti.add(answerByQuestion.get(id29));
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(P);
            // Controlli lato server sulla validità delle risposte
            if (!validateAnswers(algorythmIds, allowedIds, answerByQuestion, reason)) {
                extraInfo.setDescrizioneStatoCorrente(String.valueOf(reason));
                result = ERR;
            } else {
                // 1° test
                if (answerByQuestion.get(id27).getAnswer().getNome().equalsIgnoreCase("SI")) {
                    // Se vero 2° test
                    if (answerByQuestion.get(id29).getAnswer().getNome().equalsIgnoreCase("SI")) {
                        result = LIVELLI_RISCHIO[2];
                    } else {
                        result = LIVELLI_RISCHIO[1];
                    }
                } else {
                    // Se falso 3° test
                    if (answerByQuestion.get(id29).getAnswer().getNome().equalsIgnoreCase("SI")) {
                        result = LIVELLI_RISCHIO[3];
                    } else {
                        result = LIVELLI_RISCHIO[2];
                    }
                }
            }
            p7.setNome(P7);
            p7.setInformativa(result);
            p7.setDescrizione(indicatorByCode.get(P7).getInformativa());
            p7.setRisposte(quesiti);
            p7.setProcesso(extraInfo);
            return p7;
        } catch (RuntimeException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Restituisce il valore dell'indicatore di impatto I1.<br />
     * Ogni indicatore ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni indicatore viene calcolato in un metodo dedicato.</p> 
     * 
     * @param allowedIds        elenco degli identificativi dei quesiti associati all'indicatore considerato
     * @param answerByQuestion  tabella delle risposte indicizzate per identificativo quesito
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio dell'indicatore, calcolato dall'algoritmo in base alle risposte date ai quesiti associati, e informazioni attinenti
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeI1(LinkedList<Integer> allowedIds, 
                                           HashMap<Integer, QuestionBean> answerByQuestion,
                                           HashMap<String, CodeBean> indicatorByCode) 
                                    throws CommandException {
        try {
            InterviewBean i1 = new InterviewBean();
            ArrayList<QuestionBean> quesiti = new ArrayList<>();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            StringBuffer reason = new StringBuffer(VOID_STRING);
            // Oggetti id quesiti necessari per il calcolo dell'indicatore
            Integer id22 = new Integer(22);
            LinkedList<Integer> algorythmIds = new LinkedList<>();
            algorythmIds.add(id22);
            // Prepara la lista di quesiti che permettono di calcolare l'indicatore
            quesiti.add(answerByQuestion.get(id22));
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(I);
            // Controlli lato server sulla validità delle risposte
            if (!validateAnswers(algorythmIds, allowedIds, answerByQuestion, reason)) {
                extraInfo.setDescrizioneStatoCorrente(String.valueOf(reason));
                result = ERR;
            } else {
                // Test
                if (answerByQuestion.get(id22).getAnswer().getNome().equalsIgnoreCase("SI")) {
                    result = LIVELLI_RISCHIO[3];
                } else {
                    result = LIVELLI_RISCHIO[1];
                }
            }
            // Valorizza e restituisce l'oggetto per l'indicatore
            i1.setNome(I1);
            i1.setInformativa(result);
            i1.setDescrizione(indicatorByCode.get(I1).getInformativa());
            i1.setRisposte(quesiti);
            i1.setProcesso(extraInfo);
            return i1;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }


    /**
     * <p>Restituisce il valore dell'indicatore di impatto I2.<br />
     * Ogni indicatore ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni indicatore viene calcolato in un metodo dedicato.</p> 
     * 
     * @param allowedIds        elenco degli identificativi dei quesiti associati all'indicatore considerato
     * @param answerByQuestion  tabella delle risposte indicizzate per identificativo quesito
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio nell'indicatore e informazioni attinenti
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeI2(LinkedList<Integer> allowedIds, 
                                           HashMap<Integer, QuestionBean> answerByQuestion,
                                           HashMap<String, CodeBean> indicatorByCode) 
                                    throws CommandException {
        try {
            InterviewBean i2 = new InterviewBean();
            ArrayList<QuestionBean> quesiti = new ArrayList<>();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            StringBuffer reason = new StringBuffer(VOID_STRING);
            // Oggetti id quesiti necessari per il calcolo dell'indicatore
            Integer id9 = new Integer(9);
            Integer id7 = new Integer(7);
            LinkedList<Integer> algorythmIds = new LinkedList<>();
            algorythmIds.add(id9);
            algorythmIds.add(id7);
            // Prepara la lista di quesiti che permettono di calcolare l'indicatore
            quesiti.add(answerByQuestion.get(id9));
            quesiti.add(answerByQuestion.get(id7));
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(I);
            // Controlli lato server sulla validità delle risposte
            if (!validateAnswers(algorythmIds, allowedIds, answerByQuestion, reason)) {
                extraInfo.setDescrizioneStatoCorrente(String.valueOf(reason));
                result = ERR;
            } else {
                // 1° test
                if (answerByQuestion.get(id9).getAnswer().getNome().equalsIgnoreCase("SI")) {
                    // Se vero ha finito
                    result = LIVELLI_RISCHIO[3];
                } else {
                    // 2° test
                    int answerId7AsInt = Integer.parseInt(answerByQuestion.get(id7).getAnswer().getNome());
                    if (answerId7AsInt >= ELEMENT_LEV_1) {
                        result = LIVELLI_RISCHIO[2];
                    } else {
                        result = LIVELLI_RISCHIO[1];
                    }
                }
            }
            // Valorizza e restituisce l'oggetto per l'indicatore
            i2.setNome(I2);
            i2.setInformativa(result);
            i2.setDescrizione(indicatorByCode.get(I2).getInformativa());
            i2.setRisposte(quesiti);
            i2.setProcesso(extraInfo);
            return i2;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Restituisce il valore dell'indicatore di impatto I3.<br />
     * Ogni indicatore ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni indicatore viene calcolato in un metodo dedicato.
     * Questo &egrave; l'unico indicatore che non dipende dai quesiti
     * ma si basa solo sul numero di strutture e soggetti terzi coinvolti.</p>
     * <p>Se risultano esserci zero strutture E zero soggetti terzi coinvolti,
     * restituisce errore (indicatore non determinabile) in quanto non &egrave; 
     * possibile che nessuna struttura e nessun soggetto siano interessati da un
     * processo, un processo &egrave; sempre erogato dall'attività di una struttura 
     * o almeno di un soggetto terzo.</p>
     * 
     * @param structsByProcess  lista vettoriale di strutture collegate a un determinato processo tramite le sue fasi
     * @param subjectsByProcess lista vettoriale di soggetti (terzi aka contingenti aka interessati), collegati a un determinato processo tramite le sue fasi
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio dell'indicatore, calcolato dall'algoritmo in base al numero di soggetti e strutture coinvolti
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeI3(final ArrayList<DepartmentBean> structsByProcess,
                                           final ArrayList<DepartmentBean> subjectsByProcess,
                                           final HashMap<String, CodeBean> indicatorByCode) 
                                    throws CommandException {
        try {
            InterviewBean i3 = new InterviewBean();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;           
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(I);
            // Totale strutture associate al processo contestuale (invisibile ma sottinteso)
            int strAmount = structsByProcess.size();
            // Vettore dei soli id dei soggetti contingenti
            Vector<Integer> subjIds = new Vector<>();
            // Flag di "tutte le direzioni..."
            boolean allDirections = false;
            // Travasa gli id dei soggetti contingenti in un vector
            for (DepartmentBean s : subjectsByProcess) {
                subjIds.add(new Integer(s.getId()));
            }
            // Verifica se c'è un soggetto pari a "tutte le strutture..."
            if (subjIds.contains(Integer.valueOf(13)) || 
                subjIds.contains(Integer.valueOf(14)) ||
                subjIds.contains(Integer.valueOf(15))) {
                allDirections = true;
            }
            // Verifica se aggiungere al computo delle strutture eventuali strutture "mascherate" da soggetti
            if (subjIds.contains(Integer.valueOf(29)) || 
                subjIds.contains(Integer.valueOf(41)) ||
                subjIds.contains(Integer.valueOf(42))) {
                strAmount++;
            }
            // Non è possibile che ci siano 0 strutture AND 0 soggetti coinvolti
            if (structsByProcess.size() == NOTHING && subjectsByProcess.size() == NOTHING) {
                result = ERR;
                extraInfo.setDescrizioneStatoCorrente("Non &egrave; possibile che non ci siano n&eacute; strutture n&eacute; soggetti coinvolti nelle fasi del processo");
            } else {
                // Calcolo del valore dell'indicatore
                if (allDirections) {
                    result = LIVELLI_RISCHIO[3];
                } else {
                    if (strAmount >= ELEMENT_LEV_3) {
                        result = LIVELLI_RISCHIO[3];
                    } else {
                        if (strAmount == ELEMENT_LEV_2) {
                            result = LIVELLI_RISCHIO[2];
                        } else {
                            if (subjectsByProcess.size() >= ELEMENT_LEV_3) {
                                result = LIVELLI_RISCHIO[2];
                            } else {
                                result = LIVELLI_RISCHIO[1];
                            }
                        }
                    }
                }
            }
            // Valorizza e restituisce l'oggetto per l'indicatore
            i3.setNome(I3);
            i3.setInformativa(result);
            i3.setDescrizione(indicatorByCode.get(I3).getInformativa());
            i3.setAutoreUltimaModifica("Strutture " + strAmount + "; Soggetti "  + subjectsByProcess.size());
            i3.setProcesso(extraInfo);
            return i3;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }

    
    /**
     * <p>Restituisce il valore dell'indicatore di impatto I4.<br />
     * Ogni indicatore ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni indicatore viene calcolato in un metodo dedicato.</p> 
     * 
     * @param allowedIds        elenco degli identificativi dei quesiti associati all'indicatore considerato
     * @param answerByQuestion  tabella delle risposte indicizzate per identificativo quesito
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio dell'indicatore, calcolato dall'algoritmo in base alle risposte date ai quesiti associati, e informazioni attinenti
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeI4(LinkedList<Integer> allowedIds, 
                                           HashMap<Integer, QuestionBean> answerByQuestion,
                                           HashMap<String, CodeBean> indicatorByCode) 
                                    throws CommandException {
        try {
            InterviewBean i4 = new InterviewBean();
            ArrayList<QuestionBean> quesiti = new ArrayList<>();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            StringBuffer reason = new StringBuffer(VOID_STRING);
            // Oggetti id quesiti necessari per il calcolo dell'indicatore
            Integer id12 = new Integer(12);
            LinkedList<Integer> algorythmIds = new LinkedList<>();
            algorythmIds.add(id12);
            // Prepara la lista di quesiti che permettono di calcolare l'indicatore
            quesiti.add(answerByQuestion.get(id12));
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(I);
            // Controlli lato server sulla validità delle risposte
            if (!validateAnswers(algorythmIds, allowedIds, answerByQuestion, reason)) {
                extraInfo.setDescrizioneStatoCorrente(String.valueOf(reason));
                result = ERR;
            } else {
                // Test
                if (answerByQuestion.get(id12).getAnswer().getNome().equalsIgnoreCase("SI")) {
                    result = LIVELLI_RISCHIO[3];
                } else {
                    result = LIVELLI_RISCHIO[1];
                }
            }
            // Valorizza e restituisce l'oggetto per l'indicatore
            i4.setNome(I4);
            i4.setInformativa(result);
            i4.setDescrizione(indicatorByCode.get(I4).getInformativa());
            i4.setRisposte(quesiti);
            i4.setProcesso(extraInfo);
            return i4;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }

    
    /**
     * <p>Restituisce il valore della dimensione di probabilit&agrave; P.<br />
     * Riceve in input una mappa in cui ad ogni valore di rischio &egrave;
     * associato un valore numerico che deriva dal conteggio dei valori di tutti 
     * gli indicatori afferenti alla dimensione P (ovvero alla
     * probabilit&agrave; che l'evento corruttivo si verifichi)
     * ripartiti per valore.</p>
     * <p>Ogni dimensione ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni dimensione viene calcolato in un metodo dedicato.</p> 
     * 
     * @param weights           tabella delle frequenze di ogni valore di rischio
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio dell'indicatore, calcolato dall'algoritmo in base alle frequenze dei valori di rischio riscontrati
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeP(LinkedHashMap<String, Integer> weights,
                                          final HashMap<String, CodeBean> indicatorByCode) 
                                   throws CommandException {
        try {
            InterviewBean p = new InterviewBean();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(P);
            // Almeno 1 dei P* è ALTO ?
            if (weights.get(LIVELLI_RISCHIO[3]).intValue() >= ELEMENT_LEV_1) {
                // E' uno soltanto ?
                if (weights.get(LIVELLI_RISCHIO[3]).intValue() == ELEMENT_LEV_1) {
                    // Ci sono 4 o più MEDI ?
                    if (weights.get(LIVELLI_RISCHIO[2]).intValue() >= ELEMENT_LEV_4) {
                        result = LIVELLI_RISCHIO[3];
                    } else { // C'è un solo ALTO e ci sono meno di 4 MEDI !
                        result = LIVELLI_RISCHIO[2];
                    }
                } else { // E' più di uno: non stiamo a fare altri controlli
                    result = LIVELLI_RISCHIO[3];
                }
            } else {    // Non c'è nessun P* = ALTO
                // Ci sono 6 o più BASSI ?
                if (weights.get(LIVELLI_RISCHIO[1]).intValue() >= 6) {
                    result = LIVELLI_RISCHIO[1];
                // Ci sono 5 o più MEDI ?
                } else if (weights.get(LIVELLI_RISCHIO[2]).intValue() >= 5) {
                    result = LIVELLI_RISCHIO[3];
                } else {    // Qua finiscono tutti gli altri casi
                    result = LIVELLI_RISCHIO[2];
                }
            }
            // Valorizza e restituisce l'oggetto per l'indicatore
            p.setNome(P);
            p.setInformativa(result);
            p.setDescrizione(indicatorByCode.get(P).getInformativa());
            p.setAutoreUltimaModifica("P1 P2 P3 P4 P5 P6 P7");
            p.setProcesso(extraInfo);
            return p;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    

    /**
     * <p>Restituisce il valore della dimensione di impatto I.<br />
     * Riceve in input una mappa in cui ad ogni valore di rischio &egrave;
     * associato un valore numerico che deriva dal conteggio dei valori di tutti 
     * gli indicatori afferenti alla dimensione I (ovvero all'impatto
     * che l'evento corruttivo ha sull'organizzazione, nel caso in cui 
     * si verifichi) ripartiti per valore.</p>
     * <p>Ogni dimensione ha un proprio algoritmo di calcolo, pertanto 
     * il valore di ogni dimensione viene calcolato in un metodo dedicato.</p> 
     * 
     * @param weights           tabella delle frequenze di ogni valore di rischio
     * @param indicatorByCode   tabella degli indicatori indicizzati per codice indicatore
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio dell'indicatore, calcolato dall'algoritmo in base alle frequenze dei valori di rischio riscontrati
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computeI(LinkedHashMap<String, Integer> weights,
                                          final HashMap<String, CodeBean> indicatorByCode) 
                                   throws CommandException {
        try {
            InterviewBean i = new InterviewBean();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(I);
            // Almeno 1 degli I* è ALTO ?
            if (weights.get(LIVELLI_RISCHIO[3]).intValue() >= ELEMENT_LEV_1) {
                // E' uno soltanto ?
                if (weights.get(LIVELLI_RISCHIO[3]).intValue() == ELEMENT_LEV_1) {
                    // Ci sono 2 o più MEDI ?
                    if (weights.get(LIVELLI_RISCHIO[2]).intValue() >= ELEMENT_LEV_2) {
                        result = LIVELLI_RISCHIO[3];
                    } else { // C'è un solo ALTO e ci sono meno di 2 MEDI !
                        result = LIVELLI_RISCHIO[2];
                    }
                } else { // E' più di uno: non stiamo a fare altri controlli
                    result = LIVELLI_RISCHIO[3];
                }
            } else {    // Non c'è nessun I* = ALTO
                // Ci sono 2 o più MEDI ?
                if (weights.get(LIVELLI_RISCHIO[2]).intValue() >= ELEMENT_LEV_2) {
                    result = LIVELLI_RISCHIO[2];
                } else {    
                    result = LIVELLI_RISCHIO[1];
                }
            }
            // Valorizza e restituisce l'oggetto per l'indicatore
            i.setNome(I);
            i.setInformativa(result);
            i.setDescrizione(indicatorByCode.get(I).getInformativa());
            i.setAutoreUltimaModifica("I1 I2 I3 I4");
            i.setProcesso(extraInfo);
            return i;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Restituisce il valore del giudizio sintetico, o PxI, che incrocia 
     * la dimensione di probabilit&agrave; P con la dimensione di impatto I.<br />
     * Riceve in input i valori di P ed I e computa il risultato
     * in base alla combinazione dei due.
     * Considerando che P ed I hanno ciascuno 3 valori possibili, 
     * matematicamente il calcolo degli incroci 
     * dei valori di P e dei valori di I &egrave; il calcolo 
     * delle disposizioni (con ripetizione) di 3 elementi presi a 2 a 2.
     * Nel nostro caso abbiamo 3 elementi (ALTO, MEDIO, BASSO) presi a 2 a 2, 
     * da cui:<pre>
     * D’(3,2) = 3<sup>2</sup> = 9</pre>
     * Di seguito viene riportata la tabella di decisione dell’algoritmo 
     * per il calcolo del PxI, con i 9 valori possibili derivanti 
     * dalle disposizioni con ripetizione dei 3 valori possibili del P 
     * e dei 3 valori possibili di I.<pre>
     * --------------------------
     *  P        I       P x I
     * --------------------------
     *  ALTO     ALTO    ALTISSIMO
     *  ALTO     MEDIO   ALTO
     *  ALTO     BASSO   MEDIO
     *  MEDIO    ALTO    ALTO
     *  MEDIO    MEDIO   MEDIO
     *  MEDIO    BASSO   BASSO
     *  BASSO    ALTO    MEDIO
     *  BASSO    MEDIO   BASSO
     *  BASSO    BASSO   MINIMO
     * --------------------------
     * </pre></p>
     * <p>Ogni indicatore ed ogni dimensione hanno un proprio algoritmo di 
     * calcolo, pertanto il valore di ogni indicatore e di ogni dimensione 
     * viene calcolato in un metodo dedicato; il giudizio sintetico 
     * non fa eccezione.</p> 
     * 
     * @param p il valore della dimensione di probabilita' per il processo contestuale
     * @param i il valore della dimensione di impatto per il processo contestuale
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio dell'indice PxI, calcolato dall'algoritmo in base ai valori delle dimensioni di rischio ricevute
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    private static InterviewBean computePI(final InterviewBean p,
                                           final InterviewBean i) 
                                    throws CommandException {
        try {
            InterviewBean pi = new InterviewBean();
            ProcessBean extraInfo = new ProcessBean();
            String result = null;
            // Memorizza il tipo dell'indicatore corrente
            extraInfo.setTipo(PI);
            // P è ALTO ?
            if (p.getInformativa().equals(LIVELLI_RISCHIO[3])) {
                // I è ALTO ?
                if (i.getInformativa().equals(LIVELLI_RISCHIO[3])) {
                    // Il rischio è ALTISSIMO !
                    result = LIVELLI_RISCHIO[4];
                } else {
                    // I è MEDIO ?
                    if (i.getInformativa().equals(LIVELLI_RISCHIO[2])) {
                        // Il rischio è ALTO
                        result = LIVELLI_RISCHIO[3];
                    } else {
                        result = LIVELLI_RISCHIO[2];
                    }
                }
            // P è MEDIO ?    
            } else if (p.getInformativa().equals(LIVELLI_RISCHIO[2])) {
                // I è ALTO ?
                if (i.getInformativa().equals(LIVELLI_RISCHIO[3])) {
                    // Il rischio è ALTO
                    result = LIVELLI_RISCHIO[3];
                } else {
                    // I è MEDIO ?
                    if (i.getInformativa().equals(LIVELLI_RISCHIO[2])) {
                        // Il rischio è MEDIO
                        result = LIVELLI_RISCHIO[2];
                    } else {
                        // Il rischio è BASSO
                        result = LIVELLI_RISCHIO[1];
                    }
                }
            } else {    // Se non è alto, e non è medio, P dev'essere BASSO
                // I è ALTO ?
                if (i.getInformativa().equals(LIVELLI_RISCHIO[3])) {
                    result = LIVELLI_RISCHIO[2];
                } else if (i.getInformativa().equals(LIVELLI_RISCHIO[2])) {
                    result = LIVELLI_RISCHIO[1];
                } else {
                    result = LIVELLI_RISCHIO[0];
                }
            }
            // Valorizza e restituisce l'oggetto per l'indicatore
            pi.setNome(PI);
            pi.setInformativa(result);
            pi.setDescrizione("Giudizio Sintetico");
            pi.setAutoreUltimaModifica("P I");
            pi.setProcesso(extraInfo);
            return pi;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio dal bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Implementa l'algoritmo "In dubio pro peior" (nel dubbio si
     * consideri il caso peggiore, ovvero, in questo caso, il rischio 
     * pi&uacute; alto) laddove esistano pi&uacute; valori per lo stesso 
     * indicatore, in particolare valori diversi raccolti nel contesto 
     * di interviste diverse.<br />
     * Controlla se effettivamente vi possono essere più valori per lo stesso 
     * indicatore; se vi sono pi&uacute; interviste, procede alla scelta.</p>
     * <p>Effettua una ricerca lineare dal momento che il numero di interviste
     * in cui lo stesso processo pu&ograve; essere esaminato sar&agrave; sempre
     * molto basso (in genere per ogni processo c'&egrave; al pi&uacute; 
     * un'intervista) per cui, essendo il presente metodo pi&uacute; 
     * implementato a fini precauzionali (pu&ograve; sempre verificarsi 
     * il caso in cui lo stesso processo sia esaminato nel contesto di due 
     * interviste rivolte a due differenti strutture) non si &egrave; ritenuto 
     * necessario implementare un algoritmo di ricerca binario <cite>(divide
     * et impera)</cite>.<br /> 
     * Per velocizzare i confronti, piuttosto che effettuare un ordinamento 
     * (sorting) prima del confronto, si utilizza
     * in partenza strutture con insertion order (in questo caso, 
     * LinkedHashMap piuttosto che HashMap, cfr. commit [ec57a9c]).
     * 
     * @param interviews    elenco di interviste sui cui indicatori applicare l'algoritmo
     * @return <code>LindekHashMap&lt;String&comma; InterviewBean&gt;</code> - dictionary, con insertion order, contenente i valori scelti per gli indicatori, indicizzati per codice
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    public static LinkedHashMap<String, InterviewBean> compare(ArrayList<InterviewBean> interviews) 
                                                        throws CommandException {
        LinkedHashMap<String, InterviewBean> indicators = null;
        // Controllo sull'input
        if (interviews == null || interviews.isEmpty()) {
            return indicators;
        }
        try {
            // Recupera la tabella della prima intervista
            LinkedHashMap<String, InterviewBean> indicatorsOrig = interviews.get(NOTHING).getIndicatori();
            // La clona altrimenti, manipolandola, si manipolerebbe (malissimo!) il valore del parametro 
            indicators = (LinkedHashMap<String, InterviewBean>) indicatorsOrig.clone();
            // Se il processo di dato id è stato sondato in solo 1 intervista i valori già li abbiamo
            if (interviews.size() > ELEMENT_LEV_1) {
                // ...se invece c'è più di un'intervista ci sono tanti valori di indicatori quante sono le interviste
                for (InterviewBean interview : interviews) {
                    HashMap<String, InterviewBean> indics = interview.getIndicatori();
                    Set<String> keys = indics.keySet();
                    for (String key : keys) {
                        // Indicatore dell'intervista corrente
                        InterviewBean value = indics.get(key);
                        // Indicatore della prima intervista
                        InterviewBean first = indicators.get(key);
                        // Recupera gli indici del rischio corrente... 
                        int riskIndexCurrent = Arrays.asList(LIVELLI_RISCHIO).indexOf(value.getInformativa());
                        // ...e di quello della prima intervista
                        int riskIndexFirst = Arrays.asList(LIVELLI_RISCHIO).indexOf(first.getInformativa());
                        // Considera il rischio più alto
                        if (riskIndexFirst < riskIndexCurrent) {
                            // Fa lo switch (sostituisce i valori iniziali con quelli correnti)
                            indicators.put(key, value);
                        }
                    }
                }
            }
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio da un bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return indicators;
    }


    /**
     * <p>Calcola le frequenze di ogni valore di rischio e le indicizza
     * per il valore stesso, conteggiando i valori degli indicatori
     * afferenti alla dimensione specificata e restituendo una tabella
     * delle frequenze dei valori indicizzate per il valore stesso.
     * Il risultato &egrave; una tipica distribuzione di frequenza.</p>
     * 
     * @param indicatorsByName  tabella contenente tutti i valori degli indicatori e anche quelli degli indici generali, indicizzati per nome
     * @param type              dimensione (P o I) i cui indicatori afferenti devono essere contati
     * @return <code>LindekHashMap&lt;String&comma; Integer&gt;</code> - dictionary, con insertion order, contenente le frequenze dei valori degli indicatori afferenti alla dimensione specificata, indicizzati per valore
     * @throws CommandException se un attributo obbligatorio non risulta valorizzato o se si verifica un problema in qualche tipo di puntamento
     */
    public static LinkedHashMap<String, Integer> count(final LinkedHashMap<String, InterviewBean> indicatorsByName,
                                                       final String type) 
                                                throws CommandException {
        int nLo, nMe, nHi; 
        nLo = nMe = nHi = NOTHING;
        LinkedHashMap<String, Integer> results = new LinkedHashMap<>();
        try {
            // Cicla sugli indicatori per nome
            Set<String> keys = indicatorsByName.keySet();
            for (String key : keys) {
                if (key.startsWith(type)) {
                    if (indicatorsByName.get(key).getInformativa().equals(LIVELLI_RISCHIO[1])) {
                        ++nLo;
                    } else if (indicatorsByName.get(key).getInformativa().equals(LIVELLI_RISCHIO[2])) {
                        ++nMe;
                    } else if (indicatorsByName.get(key).getInformativa().equals(LIVELLI_RISCHIO[3])) {
                        ++nHi;
                    }
                }
            }
            results.put(LIVELLI_RISCHIO[1], new Integer(nLo));
            results.put(LIVELLI_RISCHIO[2], new Integer(nMe));
            results.put(LIVELLI_RISCHIO[3], new Integer(nHi));
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio da un bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return results;
    }

}
