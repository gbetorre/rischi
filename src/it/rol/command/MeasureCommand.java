/*
 *   Rischi On Line (ROL): Applicazione web per la gestione di 
 *   sondaggi inerenti al rischio corruttivo cui i processi organizzativi
 *   di una PA possono essere esposti e per la produzione di mappature
 *   e reportistica finalizzate alla valutazione del rischio corruttivo
 *   nella pubblica amministrazione.
 *
 *   Risk Mapping Software (ROL)
 *   web applications to assess the amount, and kind, of risk
 *   which each process is exposed, and to publish, and manage,
 *   report and risk information.
 *   Copyright (C) 2022-2024 Giovanroberto Torre
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.DBWrapper;
import it.rol.Main;
import it.rol.Query;
import it.rol.bean.CodeBean;
import it.rol.bean.DepartmentBean;
import it.rol.bean.ItemBean;
import it.rol.bean.MeasureBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
import it.rol.bean.RiskBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.CommandException;
import it.rol.exception.WebStorageException;


/** 
 * <p><code>MeasureCommand.java</code> (ms)<br />
 * Implementa la logica per la gestione delle misure di mitigazione
 * dei rischi corruttivi (ROL).</p>
 * 
 * <p>Created on Tue Mar  5 12:27:01 CET 2024</p>
 * 
 * @author <a href="mailto:giovanroberto.torre@univr.it">Giovanroberto Torre</a>
 */
public class MeasureCommand extends ItemBean implements Command, Constants {
    
    /* ******************************************************************** *
     *  Dichiara e/o inizializza variabili di classe e variabili d'istanza  *
     * ******************************************************************** */
    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
     */
    private static final long serialVersionUID = -2677911856070653623L;
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
     * Pagina a cui la command reindirizza per mostrare la lista delle misure di mitigazione
     */
    private static final String nomeFileElenco = "/jsp/msElenco.jsp";
    /**
     * Pagina a cui la command reindirizza per mostrare i dettagli di una misura
     */
    private static final String nomeFileDettaglio = "/jsp/msMisura.jsp";
    /**
     * Pagina a cui la command fa riferimento per mostrare la form di aggiunta misura
     */
    private static final String nomeFileInsertMeasure = "/jsp/msMisuraForm.jsp";
    /**
     * Pagina a cui la command fa riferimento per mostrare la form di aggiunta di una misura a un rischio
     */
    private static final String nomeFileAssignMeasure = "/jsp/msPRMisuraForm.jsp";
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutte le pagine gestite da questa Command
     */    
    private static final HashMap<String, String> nomeFile = new HashMap<>();
    /** 
     * Lista delle tipologie delle misure
     */
    private static ArrayList<CodeBean> types;
    /** 
     * Lista di tutte le misure inserite
     */
    private static ArrayList<MeasureBean> allMeasures;

  
    /* ******************************************************************** *
     *                    Routine di inizializzazione                       *
     * ******************************************************************** */
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
        nomeFile.put(COMMAND_MEASURE,       nomeFileElenco);
        nomeFile.put(PART_INSERT_MEASURE,   nomeFileInsertMeasure);
        nomeFile.put(PART_INSERT_M_R_P,     nomeFileAssignMeasure);
    }
    
    
    /* ******************************************************************** *
     *                          Costruttore (vuoto)                         *
     * ******************************************************************** */
    /** 
     * Crea una nuova istanza di  questa Command 
     */
    public MeasureCommand() {
        /*;*/   // It doesn't anything
    }
  
    
    /* ******************************************************************** *
     *                   Implementazione dell'interfaccia                   *
     * ******************************************************************** */
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
        // Processo nel cui contesto si deve eventualmente applicare la misura
        ProcessBean process = null;
        // Rischio a cui si deve applicare la misura (nel contesto del processo)
        RiskBean risk = null;
        // Elenco di misure di prevenzione
        ArrayList<MeasureBean> measures = null;
        // Elenco parziale delle misure sulla base di alcuni fattori abilitanti
        ArrayList<MeasureBean> suggestedMeasures = null;
        // Elenco di misure depurate di altre liste (suggerite, già applicate...)
        ArrayList<MeasureBean> lessMeasures = null;
        // Elenco dei caratteri delle misure
        ArrayList<CodeBean> characters = null;
        // Elenco strutture collegate alla rilevazione
        ArrayList<DepartmentBean> structs = null;
        // Elenco strutture collegate alla rilevazione indicizzate per codice
        HashMap<String, Vector<DepartmentBean>> flatStructs = null;
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
        // Recupera o inizializza 'id misura'
        int idMs = parser.getIntParameter("idM", DEFAULT_ID);
        // Recupera o inizializza 'id processo' (nel cui contesto si applica la misura)
        int idP = parser.getIntParameter("pliv", DEFAULT_ID);
        // Recupera o inizializza 'id rischio' (cui si deve applicare una misura)
        int idR = parser.getIntParameter("idR", DEFAULT_ID);
        /* ******************************************************************** *
         *      Instanzia nuova classe DBWrapper per il recupero dei dati       *
         * ******************************************************************** */
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new CommandException(FOR_NAME + "Non e\' disponibile un collegamento al database\n." + wse.getMessage(), wse);
        }
        /* ******************************************************************** *
         *                  Controllo per evitare Garden Gate                   *
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
            // Recupera l'oggetto rilevazione
            CodeBean survey = ConfigManager.getSurvey(codeSur);
            // Recupera le tipologie delle misure
            types = ConfigManager.getMeasureTypes();
            // Recupera l'elenco completo delle misure di prevenzione
            allMeasures = db.getMeasures(user, survey);
            // Controllo sull'input
            if (!codeSur.equals(DASH)) {
                // Creazione della tabella che conterrà i valori dei parametri passati dalle form
                params = new HashMap<>();
                // Carica in ogni caso i parametri di navigazione
                loadParams(part, req, params);
                /* @PostMapping */
                if (write) {
                    // Controlla quale azione vuole fare l'utente
                    if (nomeFile.containsKey(part)) {/*
                        // Stringa dinamica per contenere i parametri di scelta strutture
                        StringBuffer paramsStruct = new StringBuffer();
                        // Dizionario dei parametri delle strutture scelte dall'utente
                        LinkedHashMap<String, String> struct = params.get(PART_SELECT_STR);
                        // Cicla sul dizionario dei parametri per ricostruire l'URL
                        for (Map.Entry<String, String> set : struct.entrySet()) {
                            // Printing all elements of a Map
                            paramsStruct.append(AMPERSAND);
                            paramsStruct.append("s" + set.getKey() + EQ + set.getValue());
                        }*/
                        // Controlla quale richiesta deve gestire
                        if (part.equalsIgnoreCase(PART_INSERT_MEASURE)) {
                            /* ************************************************ *
                             *        PROCESS Form to INSERT new Measure        *
                             * ************************************************ */
                            db.insertMeasure(user, params);
                            // Prepara la redirect 
                            redirect = ConfigManager.getEntToken() + EQ + COMMAND_MEASURE + 
                                       AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                       AMPERSAND + MESSAGE + EQ + "newMes";
                        } else if (part.equalsIgnoreCase(PART_INSERT_M_R_P)) {
                            /* ************************************************ *
                             *   INSERT new relation between Risk and Measure   *
                             * ************************************************ */
                            // Controlla che non sia già presente l'associazione 
                            boolean check = db.isMeasureRiskProcess(user, params, allMeasures);
                            if (check) {  // Genera un errore
                                // Duplicate key value violates unique constraint 
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_MEASURE + 
                                           AMPERSAND + "p" + EQ + PART_INSERT_M_R_P +
                                           AMPERSAND + "idR" + EQ + parser.getStringParameter("r-id", VOID_STRING) + 
                                           AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                           AMPERSAND + MESSAGE + EQ + "dupKey";
                            } else {
                                // Inserisce nel DB nuova associazione tra rischio e processo
                                db.insertMeasureRiskProcess(user, params);
                                // Prepara la redirect 
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS + 
                                           AMPERSAND + "p" + EQ + PART_PROCESS +
                                           AMPERSAND + "pliv" + EQ + parser.getStringParameter("pliv2", VOID_STRING) + 
                                           AMPERSAND + "liv" + EQ + ELEMENT_LEV_2 +
                                           AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                           AMPERSAND + MESSAGE + EQ + "newRel";
                            }
                        }
                    } else {
                        // Azione di default
                        // do delete?
                    }
                /* @GetMapping */
                } else {
                    /* ************************************************ *
                     *                Manage Measure Part               *
                     * ************************************************ */
                    if (nomeFile.containsKey(part)) {
                        //macros = ProcessCommand.retrieveMacroAtBySurvey(user, codeSur, db);
                        if (part.equalsIgnoreCase(PART_INSERT_MEASURE)) {
                            /* ************************************************ *
                             *            BUILD UP Form for new Measure         *
                             * ************************************************ */
                            // Recupera i caratteri
                            characters = db.getMeasureCharacters();
                            // Recupera le strutture della rilevazione corrente
                            structs = DepartmentCommand.retrieveStructures(codeSur, user, db);
                            // Travasa le strutture in una mappa piatta indicizzata per codice
                            flatStructs = AuditCommand.decantStructs(structs);
                            // Ha bisogno di personalizzare le breadcrumbs
                            LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                            bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_1, "Nuova Misura");
                        } else if (part.equalsIgnoreCase(PART_INSERT_M_R_P)) {
                            /* ***************************************************** *
                             * SHOWS Form to LINK A Measure TO A Risk INTO A Process *
                             * ***************************************************** */
                            // Prepara un ProcessBean di cui recuperare tutti i rischi
                            process = db.getProcessById(user, idP, survey);
                            // Recupera tutti i rischi del processo; tra questi ci sarà quello cui si vuole applicare la misura
                            ArrayList<RiskBean> risks = db.getRisksByProcess(user, process, survey);
                            // Individua il rischio specifico a cui si vuole applicare la misura
                            risk = ProcessCommand.decant(risks, idR);
                            // 
                            suggestedMeasures = db.getMeasuresByFactors(user, risk, Query.GET_ALL, survey);
                            // Recupera le misure suggerite, tolte quelle già applicate al rischio e al processo correnti
                            lessMeasures = db.getMeasuresByFactors(user, risk, !Query.GET_ALL, survey);
                            // Toglie dall'elenco totale delle misure tutte quelle suggerite e quelle già applicate
                            measures = purge(allMeasures, suggestedMeasures, risk);
                            //measures.removeAll(suggestedMeasures);
                            //measures.removeAll(risk.getMisure());
                            //measures = allMeasures;
                            //, risk);
                            // Ha bisogno di personalizzare le breadcrumbs
                            LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                            bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_2, "Nuovo legame P-R-M");

                        }
                        // Imposta la jsp
                        fileJspT = nomeFile.get(part);
                    } else {
                        /* ************************************************ *
                         *             SELECT a Specific Measure            *
                         * ************************************************ */
                        if (idMs > DEFAULT_ID) {
                            // TODO IMPLEMENTARE
                            // Recupera la misura di prevenzione/mitigazione
                            //risk = db.getRisk(user, idMs, ConfigManager.getSurvey(codeSur));
                            // Ha bisogno di personalizzare le breadcrumbs perché sull'indirizzo non c'è il parametro 'p'
                            bC = HomePageCommand.makeBreadCrumbs(ConfigManager.getAppName(), req.getQueryString(), "Rischio");
                            fileJspT = nomeFileDettaglio;
                        } else {
                            /* ************************************************ *
                             *              SELECT List of Measures             *
                             * ************************************************ */
                            measures = db.getMeasures(user, survey);
                            fileJspT = nomeFileElenco;                            
                        }
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
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Si e\' tentato di effettuare un\'operazione non andata a buon fine.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ce.getMessage(), ce);
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un puntamento a null.\n";
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
        // Imposta nella request elenco tipologie di misure
        if (types != null) {
            req.setAttribute("tipiMisure", types);
        }
        // Imposta nella request elenco caratteri delle misure
        if (characters != null) {
            req.setAttribute("caratteriMisure", characters);
        }
        // Imposta nella request elenco misure di prevenzione dei rischi
        if (measures != null) {
            req.setAttribute("misure", measures);
        }
        // Imposta in request processo anticorruttivo
        if (process != null) {
            req.setAttribute("processo", process);
        }
        // Imposta in request specifico rischio cui aggiungere misure di prevenzione
        if (risk != null) {
            req.setAttribute("rischio", risk);
        }        
        // Imposta in request elenco delle misure suggerite in base ai fattori abilitanti, meno le misure già applicate
        if (suggestedMeasures != null) {
            req.setAttribute("suggerimenti", lessMeasures);
        }
        // Imposta in request elenco completo delle misure suggerite in base ai fattori abilitanti
        if (suggestedMeasures != null) {
            req.setAttribute("misureDaFattori", suggestedMeasures);
        }
        // Imposta nella request elenco completo strutture
        if (structs != null) {
            req.setAttribute("strutture", structs);
        }
        // Imposta nella request elenco completo strutture sotto forma di dictionary
        if (flatStructs != null) {
            req.setAttribute("elencoStrutture", flatStructs);
        }
        // Imposta l'eventuale indirizzo a cui redirigere
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }
        // Imposta struttura contenente tutti i parametri di navigazione già estratti
        if (!params.isEmpty()) {
            req.setAttribute("params", params);
        }
        /* Imposta nella request oggetto misura di prevenzione specifica
        if (measure != null) {
            req.setAttribute("misura", misura);
        }*/
        // Imposta nella request le breadcrumbs in caso siano state personalizzate
        if (bC != null) {
            req.removeAttribute("breadCrumbs");
            req.setAttribute("breadCrumbs", bC);
        }
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }
    
    
    /* ******************************************************************** *
     *                                Metodi                                *
     * ******************************************************************** */
    
    /**
     * <p>Valorizza per riferimento una mappa contenente tutti i valori 
     * parametrici riscontrati sulla richiesta.</p>
     * 
     * @param part          la sezione del sito corrente
     * @param req           la HttpServletRequest contenente la richiesta del client
     * @param formParams    mappa da valorizzare per riferimento (ByRef)
     * @throws CommandException se si verifica un problema nella gestione degli oggetti data o in qualche tipo di puntamento
     * @throws AttributoNonValorizzatoException se si fa riferimento a un attributo obbligatorio di bean che non viene trovato
     */
    public static void loadParams(String part, 
                                  HttpServletRequest req,
                                  HashMap<String, LinkedHashMap<String, String>> formParams)
                           throws CommandException, 
                                  AttributoNonValorizzatoException {
        LinkedHashMap<String, String> survey = new LinkedHashMap<>();
        LinkedHashMap<String, String> measure = new LinkedHashMap<>();
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
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
        //survey.put("d", parser.getStringParameter("d", VOID_STRING));
        //survey.put("t", parser.getStringParameter("t", VOID_STRING));
        // Aggiunge il tutto al dizionario dei parametri
        formParams.put(PARAM_SURVEY, survey);
        /* **************************************************** *
         *     Caricamento parametri di Inserimento Misura      *
         * **************************************************** */
        if (part.equals(PART_INSERT_MEASURE)) {
            // Nome e descrizione misura
            measure.put("nome", parser.getStringParameter("ms-name", VOID_STRING));
            measure.put("desc", parser.getStringParameter("ms-desc", VOID_STRING));
            // Carattere misura
            measure.put("char", parser.getStringParameter("ms-char", VOID_STRING));
            // Sostenibilità economica
            measure.put("econ", parser.getStringParameter("ms-eco", VOID_STRING));
            // Tipologie misura
            for (CodeBean type : types) {
                measure.put("tip-" + type.getId(), parser.getStringParameter("ms-type" + type.getId(), VOID_STRING));
            }
            // Struttura Capofila 1 (sc1)
            measure.put("sc1-1",  parser.getStringParameter("sliv1", VOID_STRING));
            measure.put("sc1-2",  parser.getStringParameter("sliv2", VOID_STRING));
            measure.put("sc1-3",  parser.getStringParameter("sliv3", VOID_STRING));
            measure.put("sc1-4",  parser.getStringParameter("sliv4", VOID_STRING));
            // Struttura Capofila 2 (sc2)
            measure.put("sc2-1",  parser.getStringParameter("sliv5", VOID_STRING));
            measure.put("sc2-2",  parser.getStringParameter("sliv6", VOID_STRING));
            measure.put("sc2-3",  parser.getStringParameter("sliv7", VOID_STRING));
            measure.put("sc2-4",  parser.getStringParameter("sliv8", VOID_STRING));
            // Struttura Capofila 3 (sc3)
            measure.put("sc3-1",  parser.getStringParameter("sliv9", VOID_STRING));
            measure.put("sc3-2",  parser.getStringParameter("sliv10", VOID_STRING));
            measure.put("sc3-3",  parser.getStringParameter("sliv11", VOID_STRING));
            measure.put("sc3-4",  parser.getStringParameter("sliv12", VOID_STRING));
            // Strutture Gregarie (sg1-1 = struttura gregaria 1^ riga 1° livello...)
            String[] deputyLiv1 = req.getParameterValues("sgliv1");
            String[] deputyLiv2 = req.getParameterValues("sgliv2");
            String[] deputyLiv3 = req.getParameterValues("sgliv3");
            String[] deputyLiv4 = req.getParameterValues("sgliv4");
            // Aggiunge tutte le strutture gregarie di I livello
            decantStructures(ELEMENT_LEV_1, deputyLiv1, measure);
            // Aggiunge tutte le strutture gregarie di II livello
            decantStructures(ELEMENT_LEV_2, deputyLiv2, measure);
            // Aggiunge tutte le strutture gregarie di III livello
            decantStructures(ELEMENT_LEV_3, deputyLiv3, measure);
            // Aggiunge tutte le strutture gregarie di IV livello
            decantStructures(ELEMENT_LEV_4, deputyLiv4, measure);
            formParams.put(part, measure);
            
        /* **************************************************** *
         *  Caricamento parametri Associazione Processo-Rischio *
         * **************************************************** *
        } else if (part.equals(PART_INSERT_RISK_PROCESS)) {
            // Recupera gli estremi del rischio da inserire
            risk.put("risk",    parser.getStringParameter("r-id", VOID_STRING));
            formParams.put(part, risk);*/
        /* **************************************************** *
         *     Caricamento parametri Fattore-Processo-Rischio   *
         * **************************************************** */
        } else if (part.equals(PART_INSERT_M_R_P)) {
            // Recupera gli estremi del rischio da inserire
            measure.put("risk",    parser.getStringParameter("r-id", VOID_STRING));
            measure.put("proc",    parser.getStringParameter("pliv2", VOID_STRING));
            // Misure suggerite (se le trova ne setta il valore, void string altrimenti)
            int count = NOTHING;
            int index = NOTHING;
            while (index < allMeasures.size()) {
                MeasureBean ms = allMeasures.get(index);
                ++count;
                measure.put("adv-" + count, parser.getStringParameter("ms-adv" + ms.getCodice(), VOID_STRING));
                ++index;
            }
            // Misura richiesta (o aggiuntiva)
            measure.put("meas",    parser.getStringParameter("mrp", VOID_STRING));
            // Ammontare complessivo misure per la rilevazione corrente
            measure.put("size",    String.valueOf(allMeasures.size()));
            formParams.put(part, measure);
        /* **************************************************** *
         *    Caricamento parametri Aggiornamento Nota al PxI   *
         * **************************************************** *
        } else if (part.equals(PART_PI_NOTE)) {
            // Recupera gli estremi della nota da aggiornare
            //String note = parser.getStringParameter("pi-note", VOID_STRING);
            risk.put("note",    parser.getStringParameter("pi-note", VOID_STRING));
            formParams.put(part, risk);*/
        }
    }
    
    // TODO COMMENTO
    private static void decantStructures(int level,
                                         String[] structures,
                                         LinkedHashMap<String, String> params) {
        int index = NOTHING;
        int size = ELEMENT_LEV_1; 
        if (structures != null) {
            while (index < structures.length) {
                params.put("sg" + size + DASH + level,  structures[index]);
                size++;
                index++;
            }
        }        
    }
    
    
    // TODO COMMENTO
    private static ArrayList<MeasureBean> purge(ArrayList<MeasureBean> fullList, 
                                                ArrayList<MeasureBean> partList,
                                                RiskBean risk) {
        ArrayList<MeasureBean> results = (ArrayList<MeasureBean>) fullList.clone();
        /* Implementazione "ingenua"
        for (MeasureBean full : fullList) {
            for (MeasureBean part : partList) {
                if (full.equals(part)) {
                    results.remove(full);
                }
            }
        }*/
        for (MeasureBean part : partList) {
            if (results.contains(part)) {
                results.remove(part);
            }
        }
        return results;
    }
    
}