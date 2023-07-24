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
 *   Copyright (C) 2022 renewed 2023 Giovanroberto Torre
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.DBWrapper;
import it.rol.Main;
import it.rol.Query;
import it.rol.Utils;
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
 * <p><code>RiskCommand.java</code><br />
 * Implementa la logica per la gestione dei rischi corruttivi (ROL).</p>
 * 
 * <p>Created on Tue 12 Apr 2022 09:46:04 AM CEST</p>
 * 
 * @author <a href="mailto:giovanroberto.torre@univr.it">Giovanroberto Torre</a>
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
        // Predispone le BreadCrumbs personalizzate per la Command corrente
        LinkedList<ItemBean> bC = null;
        // Variabile contenente l'indirizzo per la redirect da una chiamata POST a una chiamata GET
        String redirect = null;
        /* ******************************************************************** *
         *                    Recupera parametri e attributi                    *
         * ******************************************************************** */
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter(PARAM_SURVEY, DASH);
        // Recupera o inizializza 'tipo pagina'   
        String part = parser.getStringParameter("p", DASH);
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
         *                         Controllo Garden Gate                        *
         * ******************************************************************** */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            if (ses == null) {
                throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n");
            }
            // Bisogna essere autenticati 
            user = (PersonBean) ses.getAttribute("usr");
            // Cioè bisogna che l'utente corrente abbia una sessione valida
            if (user == null) {
                throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n");
            }
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + ": Si e\' verificato un problema in una conversione di tipo.\n";
            LOG.severe(msg);
            throw new CommandException(msg + cce.getMessage(), cce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null, probabilmente nel tentativo di recuperare l\'utente.\n";
            LOG.severe(msg);
            throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n" + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        /* ******************************************************************** *
         *                          Corpo del programma                         *
         * ******************************************************************** */
        // Decide il valore della pagina
        try {
            // Controllo sull'input
            if (!codeSur.equals(DASH)) {
                // Creazione della tabella che conterrà i valori dei parametri passati dalle form
                params = new HashMap<>();
                // Carica in ogni caso i parametri di navigazione
                loadParams(part, parser, params);
                // @PostMapping
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
                            // Costruisce l'indirizzo a cui redirezionare
                            redirect =  "q" + EQ + COMMAND_AUDIT + AMPERSAND +
                                        "p" + EQ + PART_RESUME_QST + 
                                        paramsStruct.toString() + 
                                        paramsProc.toString() +
                                        dateTimeProc.toString() + AMPERSAND +
                                        PARAM_SURVEY + EQ + codeSur;
                        }
                    } else {
                        // Azione di default
                        // do delete?
                    }
                // @GetMapping
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
                            ArrayList<ItemBean> ambits = db.getAmbits(user);
                            flatQuestions = decantQuestions(questions, ambits);
                        } else if (part.equalsIgnoreCase(PART_CONFIRM_QST)) {
                            /* ************************************************ *
                             *                   Confirm Part                   *
                             * ************************************************ */
                            // TODO IMPLEMENTARE
                            //answers = retrieveAnswers(user, codeSur, Query.GET_ALL_BY_CLAUSE, Query.GET_ALL_BY_CLAUSE, db);
                            answers = db.getAnswers(user, params, ConfigManager.getSurvey(codeSur));
                            //ArrayList<ItemBean> ambits = db.getAmbits(user);
                            //flatQuestions = decantQuestions(questions, ambits);
                        } else if (part.equalsIgnoreCase(PART_RESUME_QST)) {
                            /* ************************************************ *
                             *  SELECT Set of Answers belonging to an Inverview *
                             * ************************************************ */
                            //answers = retrieveAnswers(user, codeSur, Query.GET_ALL_BY_CLAUSE, Query.GET_ALL_BY_CLAUSE, db);
                            answers = db.getAnswers(user, params, ConfigManager.getSurvey(codeSur));
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
                            interviews = db.getInterviewsBySurvey(user, params, ConfigManager.getSurvey(codeSur));
                        }
                        fileJspT = nomeFile.get(part);
                    } else {
                        /* ************************************************ *
                         *          SELECT List of Interview Part           *
                         * ************************************************ */
                        interviews = db.getInterviewsBySurvey(user, params, ConfigManager.getSurvey(codeSur));
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
        // Imposta nella request le breadcrumbs in caso siano state personalizzate
        if (bC != null) {
            req.removeAttribute("breadCrumbs");
            req.setAttribute("breadCrumbs", bC);
        }
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }
    
    
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
        LinkedHashMap<String, String> risk = new LinkedHashMap<>();
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
     * @return <code>HashMap&lt;String, LinkedHashMap&lt;String, String&gt;&gt;</code> - Struttura di tipo Dictionary, o Mappa ordinata, avente per chiave il valore del parametro p, e per valore una mappa contenente i relativi parametri
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
        HashMap<String, LinkedHashMap<String, String>> formParams = new HashMap<String, LinkedHashMap<String, String>>();
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
    
    
    /**
     * <p>Travasa una struttura vettoriale in una corrispondente struttura 
     * di tipo Dictionary, HashMap, in cui le chiavi sono rappresentate 
     * da oggetti String e i valori sono rappresentati dalle elenchi di figlie 
     * associate alla chiave.</p>
     * 
     * @param objects struttura vettoriale contenente gli oggetti da travasare
     * @param part    parametro di navigazione identificante la tipologia di oggetti da considerare
     * @return <code>HashMap&lt;?&comma; ?&gt;</code> - Struttura di tipo Dictionary, o Mappa ordinata, avente per chiave il codice del nodo, e per valore il Vector delle sue figlie
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
     * @param structs Vector di DepartmentBean da travasare in HashMap
     * @return <code>HashMap&lt;String&comma; Vector&lt;DepartmentBean&gt;&gt;</code> - Struttura di tipo Dictionary, o Mappa ordinata, avente per chiave il codice del nodo, e per valore il Vector delle sue figlie
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    private static HashMap<String, Vector<DepartmentBean>> decantStructs(ArrayList<DepartmentBean> structs)
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
     * @param questions Vector di QuestionBean da indicizzare per ambito
     * @param ambits    Vector di ambiti cui ricondurre i quesiti
     * @return <code>HashMap&lt;ItemBean&comma; ArrayList&lt;QuestionBean&gt;&gt;</code> - Struttura di tipo Dictionary, o Mappa ordinata, avente per chiave l'ambito e per valore il Vector dei suoi quesiti
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    private static HashMap<ItemBean, ArrayList<QuestionBean>> decantQuestions(ArrayList<QuestionBean> questions, 
                                                                              ArrayList<ItemBean> ambits)
                                                                       throws CommandException {
        HashMap<ItemBean, ArrayList<QuestionBean>> questionsByAmbit = new HashMap<>();
        try {
            for (ItemBean ambit : ambits) {
                int key = ambit.getId();
                ArrayList<QuestionBean> qs = new ArrayList<>();
                for (QuestionBean q : questions) {
                    if (q.getCod1() == key) {
                        qs.add(q);
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
    
}