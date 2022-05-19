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

package it.rol.command;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
import it.rol.bean.QuestionBean;
import it.rol.bean.RiskBean;
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
public class RiskCommand extends ItemBean implements Command, Constants {
    
    /**
     * 
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
     * Pagina a cui la command reindirizza per mostrare la lista dei rischi del progetto
     */
    private static final String nomeFileElenco = "/jsp/riElenco.jsp";
    /**
     * Pagina a cui la command fa riferimento per permettere la scelta di una struttura
     */
    private static final String nomeFileSelectStruct = "/jsp/riStruttura.jsp";
    /**
     * Pagina a cui la command fa riferimento per permettere la scelta di un processo
     */
    private static final String nomeFileSelectProcess = "/jsp/riProcesso.jsp";
    /**
     * Pagina a cui la command fa riferimento per permettere la compilazione dei quesiti
     */
    private static final String nomeFileCompileQuest = "/jsp/riQuestionario.jsp";
    /**
     * Pagina a cui la command fa riferimento per riepilogare le risposte ai quesiti
     */
    private static final String nomeFileConfirmQuest = "/jsp/riEpilogo.jsp";
    /**
     * Pagina a cui la command fa riferimento per permettere l'aggiornamento delle risposte ai dei quesiti
     */
    private static final String nomeFileResumeQuest = "/jsp/riAggiornamento.jsp";
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutti gli attributi del progetto
     */    
    private static final HashMap<String, String> nomeFile = new HashMap<>();
    /**
     *  Tabella chiave/valore contenente il numero di quesiti per ogni rilevazione
     */
    private static ConcurrentHashMap<String, Integer> questionAmounts; 

    
    /** 
     * Crea una nuova istanza di WbsCommand 
     */
    public RiskCommand() {
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
        nomeFile.put(COMMAND_RISK, nomeFileElenco);
        nomeFile.put(PART_SELECT_STR, nomeFileSelectStruct);
        nomeFile.put(PART_PROCESS, nomeFileSelectProcess);
        nomeFile.put(PART_SELECT_QST, nomeFileCompileQuest);
        nomeFile.put(PART_CONFIRM_QST, nomeFileConfirmQuest);
        //nomeFile.put(PART_RESUME_QST, nomeFileResumeQuest);
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
        // Data di oggi sotto forma di oggetto String
        GregorianCalendar today = Utils.getCurrentDate();
        // Elenco strutture collegate alla rilevazione
        ArrayList<DepartmentBean> structs = null;
        // Elenco processi collegati alla rilevazione
        ArrayList<ProcessBean> macros = null;
        // Elenco quesiti collegati alla rilevazione
        ArrayList<QuestionBean> questions = null;
        // Elenco risposte ai quesiti collegati alla rilevazione
        ArrayList<ItemBean> answers = null;
        // Elenco strutture collegate alla rilevazione indicizzate per codice
        HashMap<String, Vector<DepartmentBean>> flatStructs = null;
        // Elenco quesiti collegati alla rilevazione raggruppati per ambito
        HashMap<ItemBean, ArrayList<QuestionBean>> flatQuestions = null;
        // Parametri identificanti le strutture 
        LinkedHashMap<String, String> paramsNav = new LinkedHashMap<>();
        // Tabella che conterrà i valori dei parametri passati dalle form
        HashMap<String, LinkedHashMap<String, String>> params = null;
        // Variabile contenente l'indirizzo per la redirect da una chiamata POST a una chiamata GET
        String redirect = null;
        /* ******************************************************************** *
         *                    Recupera parametri e attributi                    *
         * ******************************************************************** */
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter("r", DASH);
        // Recupera o inizializza 'tipo pagina'   
        String part = parser.getStringParameter("p", DASH);
        // Flag di scrittura
        boolean write = (boolean) req.getAttribute("w");
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
                // Verifica se deve gestire una chiamata POST
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
                            redirect = "q=" + COMMAND_RISK + "&p=" + PART_PROCESS + paramsStruct.toString() + "&r=" + codeSur;
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
                            redirect = "q=" + COMMAND_RISK + "&p=" + PART_SELECT_QST + paramsStruct.toString() + paramsProc.toString() + "&r=" + codeSur;
                        } else if (part.equalsIgnoreCase(PART_SELECT_QST)) {
                            /* ************************************************ *
                             *                INSERT Answers Part               *
                             * ************************************************ */
                            // Recupera il numero di quesiti associati alla rilevazione
                            int items = Integer.valueOf(questionAmounts.get(codeSur));
                            // Inserisce nel DB le risposte a tutti gli item quesiti
                            db.insertQuest(user, params, items);
                            // Prepara il passaggio dei parametri identificativi dei processi
                            LinkedHashMap<String, String> macro = params.get(PART_PROCESS);
                            // Stringa dinamica per contenere i parametri di scelta processi
                            StringBuffer paramsProc = new StringBuffer();
                            for (Map.Entry<String, String> set : macro.entrySet()) {
                                // Printing all elements of a Map
                                paramsProc.append("&");
                                paramsProc.append("p" + set.getKey() + "=" + set.getValue());
                            }
                            redirect = "q=" + COMMAND_RISK + "&p=" + PART_CONFIRM_QST + paramsStruct.toString() + paramsProc.toString() + "&r=" + codeSur;
                        }
                    } else {
                        // Deve eseguire una eliminazione
                    }
                } else {
                    /* ************************************************ *
                     *                  SELECT Risk Part                *
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
                             *              Creazione Questionario              *
                             * ************************************************ */
                            questions = retrieveQuestions(user, codeSur, Query.GET_ALL_BY_CLAUSE, Query.GET_ALL_BY_CLAUSE, db);
                            ArrayList<ItemBean> ambits = db.getAmbits(user);
                            flatQuestions = decantQuestions(questions, ambits);
                        } else if (part.equalsIgnoreCase(PART_CONFIRM_QST)) {
                            /* ************************************************ *
                             *                Riepilogo Risposte                *
                             * ************************************************ */
                            // TODO IMPLEMENTARE
                            //answers = retrieveAnswers(user, codeSur, Query.GET_ALL_BY_CLAUSE, Query.GET_ALL_BY_CLAUSE, db);
                            //answers = db.getAnswers(user, fields, survey);
                            //ArrayList<ItemBean> ambits = db.getAmbits(user);
                            //flatQuestions = decantQuestions(questions, ambits);
                        }
                        fileJspT = nomeFile.get(part);
                    } else {
                        //riskOfRuntimeProject = db.getRisks(idPrj, user);
                        fileJspT = nomeFileElenco;
                    }
                }
            } else {
                // Se siamo qui vuol dire che l'id del progetto non è > zero, il che è un guaio
                HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
                ses.invalidate();
                String msg = FOR_NAME + "Qualcuno ha tentato di inserire un indirizzo nel browser avente un id progetto non valido!.\n";
                LOG.severe(msg);
                throw new CommandException("Attenzione: indirizzo richiesto non valido!\n");
            }
        /*}  catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di valori dal db.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);*/
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
        // Imposta l'eventuale indirizzo a cui redirigere
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }
        if (!params.isEmpty()) {
            req.setAttribute("params", params);
        }
        // Imposta nella request data di oggi 
        req.setAttribute("now", Utils.format(today));
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }
    
    
    /**
     * <p>Estrae l'elenco dei quesiti e, per ogni quesito figlio trovato,
     * lo valorizza con gli attributi aggiuntivi (tipo, formulazione, etc.)</p>
     *TODO COMMENTO
     * @param type          valore identificante se si vuol fare la query sulle strutture collegate a processi o a macroprocessi
     * @param id            identificativo del processo o macroprocesso
     * @param getAll        flag specificante, se vale -1, che si vogliono recuperare tutte le strutture collegate a tutti i macro/processi
     * @param codeSurvey    codice testuale della rilevazione
     * @param user          utente loggato
     * @param db            databound gia' istanziato
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - ArrayList di strutture collegate alla rilevazione e al macro/processo specifico, oppure a tutti i macro/processi
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
            // Chiama il metodo del databound che estrae i quesiti valorizzati
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
     * <p>Valorizza per riferimento una mappa contenente i valori relativi  
     * ad una attivit&agrave; eventualmente da aggiornare.</p> 
     * 
     * @param part la sezione del sito che si vuole aggiornare
     * @param parser oggetto per la gestione assistita dei parametri di input, gia' pronto all'uso
     * @param formParams mappa da valorizzare per riferimento (ByRef)
     * @throws CommandException se si verifica un problema nella gestione degli oggetti data o in qualche tipo di puntamento
     * @throws AttributoNonValorizzatoException se si fa riferimento a un attributo obbligatorio di bean che non viene trovato
     */
    private static void loadParams(String part, 
                                   ParameterParser parser,
                                   HashMap<String, LinkedHashMap<String, String>> formParams)
                            throws CommandException, 
                                   AttributoNonValorizzatoException {
        LinkedHashMap<String, String> struct = new LinkedHashMap<>();
        LinkedHashMap<String, String> proat = new LinkedHashMap<>();
        LinkedHashMap<String, String> answs = new LinkedHashMap<>();
        LinkedHashMap<String, String> survey = new LinkedHashMap<>();
        /* **************************************************** *
         *     Caricamento parametro di Codice Rilevazione      *
         * **************************************************** */      
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter("r", DASH);
        // Recupera l'oggetto rilevazione a partire dal suo codice
        CodeBean surveyAsBean = ConfigManager.getSurvey(codeSur);
        survey.put(PARAM_SURVEY, String.valueOf(surveyAsBean.getId()));
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
            int limit = Integer.valueOf(questionAmounts.get(codeSur));
            for (int i = NOTHING; i < limit; i++) {
                answs.put("quid" + String.valueOf(i),  parser.getStringParameter("Q" + String.valueOf(i) + "-id", VOID_STRING));
                answs.put("risp" + String.valueOf(i),  parser.getStringParameter("Q" + String.valueOf(i), VOID_STRING));
                answs.put("note" + String.valueOf(i),  parser.getStringParameter("Q" + String.valueOf(i) + "-note", VOID_STRING));
            }
            formParams.put(PART_SELECT_QST, answs);
        }
    }
    
    
    /**
     * <p>Travasa una struttura vettoriale in una corrispondente struttura 
     * di tipo Dictionary, HashMap, in cui le chiavi sono rappresentate 
     * da oggetti String e i valori sono rappresentati dalle elenchi di figlie 
     * associate alla chiave.</p>
     *
     * @param 
     * @return <code>HashMap&lt;String&comma; Vector&lt;DepartmentBean&gt;&gt;</code> - Struttura di tipo Dictionary, o Mappa ordinata, avente per chiave il codice del nodo, e per valore il Vector delle sue figlie
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    private static HashMap<?,?> decant(ArrayList<?> objects, 
                                                     String part)
                                              throws CommandException {
        if (part.equals(PART_SELECT_STR)) {
            return decantStructs((ArrayList<DepartmentBean>) objects);
        /*} else if(part.equals(PART_SELECT_QST)) {
            return decantQuestions((ArrayList<QuestionBean>) objects);*/
        } else {
            String msg = FOR_NAME + "Valore di \'part\' non gestito.\n";
            LOG.severe(msg);
            throw new CommandException(msg);
        }
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
     * <p>TODO COMMENTO</p>
     *
     * @param questions Vector di DepartmentBean da travasare in HashMap
     * @return <code>HashMap&lt;String&comma; Vector&lt;DepartmentBean&gt;&gt;</code> - Struttura di tipo Dictionary, o Mappa ordinata, avente per chiave il codice del nodo, e per valore il Vector delle sue figlie
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