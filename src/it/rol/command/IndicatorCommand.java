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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.rol.Utils;
import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.DBWrapper;
import it.rol.Main;
import it.rol.Query;
import it.rol.bean.CodeBean;
import it.rol.bean.DepartmentBean;
import it.rol.bean.InterviewBean;
import it.rol.bean.ItemBean;
import it.rol.bean.MeasureBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
import it.rol.bean.RiskBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.CommandException;
import it.rol.exception.WebStorageException;


/** 
 * <p><code>IndicatorCommand.java</code> (ic)<br />
 * Implementa la logica per la gestione degli indicatori di monitoraggio
 * e relative misurazioni.</p>
 * 
 * <p>Created on 14:01 18/09/2024 Wed Sep 18 14:01:46 CEST 2024</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class IndicatorCommand extends ItemBean implements Command, Constants {

    /* ******************************************************************** *
     *  Dichiara e/o inizializza variabili di classe e variabili d'istanza  *
     * ******************************************************************** */
    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
     */
    private static final long serialVersionUID = -7546430466772067442L;
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
     * Pagina per mostrare la lista degli indicatori di monitoraggio (registro degli indicatori)
     */
    private static final String nomeFileElenco = "/jsp/icElenco.jsp";
    /**
     * Pagina per mostrare la lista delle misure monitorate (pagina iniziale monitoraggio)
     */
    private static final String nomeFileElencoMisure  = "/jsp/icMisure.jsp";
    /**
     * Pagina per mostrare la lista degli indicatori di una misura monitorata
     */
    private static final String nomeFileElencoIndicatori = "/jsp/icIndicatori.jsp";
    /**
     * Pagina per mostrare la lista delle misurazioni (collegate agli indicatori) di una misura monitorata
     */
    private static final String nomeFileElencoMisurazioni = "/jsp/icMisurazioni.jsp";
    /**
     * Pagina per mostrare i dettagli di un indicatore di monitoraggio
     */
    private static final String nomeFileDettaglio = "/jsp/icIndicatore.jsp";    
    /**
     * Pagina per mostrare i dettagli di una misura monitorata
     */
    private static final String nomeFileMisura = "/jsp/icMisura.jsp";    
    /**
     * Pagina per mostrare i dettagli di una misurazione
     */
    private static final String nomeFileMisurazione = "/jsp/icMisurazione.jsp";
    /**
     * Pagina per mostrare la form di aggiunta dei dettagli di una misura monitorata
     */
    private static final String nomeFileInsertMeasure = "/jsp/icMisuraForm.jsp";
    /**
     * Pagina per mostrare il riepilogo dei dettagli di una misura monitorata
     */ 
    private static final String nomeFileResumeMeasure = "/jsp/icEpilogo.jsp";
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutte le pagine gestite da questa Command
     */    
    private static final HashMap<String, String> nomeFile = new HashMap<>();
    /** 
     * Lista dei tipi di indicatore
     */
    private static ArrayList<CodeBean> types;
    /** 
     * Lista di indicatori di monitoraggio
     */
    private static ArrayList<MeasureBean> indicators;

  
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
        nomeFile.put(COMMAND_INDICATOR,         nomeFileElenco);
        nomeFile.put(PART_MEASURES,             nomeFileElencoMisure);
        nomeFile.put(PART_INDICATOR,            nomeFileElencoIndicatori);
        nomeFile.put(PART_MONITOR,              nomeFileElencoMisurazioni);
        nomeFile.put(PART_INSERT_INDICATOR,     nomeFileDettaglio);
        nomeFile.put(PART_INSERT_MEASUREMENT,   nomeFileMisurazione);
        nomeFile.put(PART_INSERT_MONITOR_DATA,  nomeFileInsertMeasure);
        nomeFile.put(PART_SELECT_MONITOR_DATA,  nomeFileResumeMeasure);
    }
    
    
    /* ******************************************************************** *
     *                          Costruttore (vuoto)                         *
     * ******************************************************************** */
    /** 
     * Crea una nuova istanza di  questa Command 
     */
    public IndicatorCommand() {
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
        // Misura monitorata specifica
        MeasureBean measure = null;
        // Elenco di misure di prevenzione monitorate
        ArrayList<MeasureBean> measures = null;
        // Tabella che conterrà le misure suggerite raggruppate per tipo
        LinkedHashMap<String, ArrayList<MeasureBean>> measuresByType = null;
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
        // Explicit Unboxing
        boolean write = writeAsObject.booleanValue();
        // Recupera o inizializza 'id misura'
        String codeMis = parser.getStringParameter("mliv", DASH);
        // Recupera o inizializza 'id misura'
        String codeM = parser.getStringParameter("mliv", DASH);
        // Recupera o inizializza 'id indicatore'
        int idI = parser.getIntParameter("idI", DEFAULT_ID);
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
            // Recupera i tipi di indicatore
            types = ConfigManager.getIndicatorTypes();
            // Recupera l'elenco completo degli indicatori di monitoraggio
            //indicators = db.getMeasures(user, VOID_SQL_STRING, Query.GET_ALL_BY_CLAUSE, survey);
            // Controllo sull'input
            if (!codeSur.equals(DASH)) {
                // Creazione della tabella che conterrà i valori dei parametri passati dalle form
                params = new HashMap<>();
                // Carica in ogni caso i parametri di navigazione
                loadParams(part, req, params);
                /* @PostMapping */
                if (write) {
                    // Controlla quale azione vuole fare l'utente
                    if (nomeFile.containsKey(part)) {
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
                            boolean check = db.isMeasureRiskProcess(user, params, indicators);
                            if (check) {  // Genera un errore
                                // Duplicate key value violates unique constraint 
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_MEASURE + 
                                           AMPERSAND + "p" + EQ + PART_INSERT_M_R_P +
                                           AMPERSAND + "idR" + EQ + parser.getStringParameter("r-id", VOID_STRING) + 
                                           AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                           AMPERSAND + MESSAGE + EQ + "dupKey";
                            } else {
                                // Inserisce nel DB nuova associazione tra rischio e misura
                                db.insertMeasureRiskProcess(user, params);
                                // Prepara la redirect 
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS + 
                                           AMPERSAND + "p" + EQ + PART_PROCESS +
                                           AMPERSAND + "pliv" + EQ + parser.getStringParameter("pliv2", VOID_STRING) + 
                                           AMPERSAND + "liv" + EQ + ELEMENT_LEV_2 +
                                           AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                           AMPERSAND + MESSAGE + EQ + "newRel#rischi-fattori-misure";
                            }
                        }
                    } else {
                        // Azione di default
                        // do delete?
                    }
                /* @GetMapping */
                } else {
                    /* ************************************************ *
                     *               Manage Indicator Part              *
                     * ************************************************ */
                    if (nomeFile.containsKey(part)) {
                        if (part.equalsIgnoreCase(PART_MEASURES)) {
                            // Controlla la presenza dell'id di una misura
                            if (codeMis.equals(DASH)) {
                            /* ************************************************ *
                             *                Elenco Monitoraggio               *
                             * ************************************************ */
                                structs = db.getMeasuresByStructs(user, survey);
                                // Imposta la jsp
                                fileJspT = nomeFile.get(part);
                            //} else {
                            /* ************************************************ *
                             *            Ramo dettagli misura monitorata       *
                             * ************************************************ */
                            }
                        } else if (part.equalsIgnoreCase(PART_INDICATOR)) {
                            // Controlla la presenza dell'id di un indicatore
                            if (idI > DEFAULT_ID) {
                            /* ************************************************ *
                             *        Ramo elenco indicatori di una misura      *
                             * ************************************************ */
                            }
                            /* ************************************************ *
                             *             Ramo modifica indicatore             *
                             * ************************************************ */
                            
                        } else if (part.equalsIgnoreCase(PART_MONITOR)) {
                            /* ************************************************ *
                             * Ramo elenco misurazioni di una misura monitorata *
                             * ************************************************ */
                            
                            /* ************************************************ *
                             *          Ramo dettagli di una misurazione        *
                             * ************************************************ */
                            
                            
                        //} else if (part.equalsIgnoreCase(VOID_STRING)) {
                            /* ************************************************ *
                             * Form aggiunta dettagli monitoraggio a una misura *
                             * ************************************************ */
                        //} else if (part.equalsIgnoreCase(VOID_STRING)) {
                            /* ************************************************ *
                             *  Pagina riepilogo dettagli monitoraggio inseriti *
                             * ************************************************ */
                        //} else if (part.equalsIgnoreCase(VOID_STRING)) {
                            /* ************************************************ *
                             *      Form inserimento indicatore di una misura   *
                             * ************************************************ */
                        //} else if (part.equalsIgnoreCase(VOID_STRING)) {
                            /* ************************************************ *
                             *     Form inserimento misurazione di una misura   *
                             * ************************************************ */
                            
                        }

                    } else {
                        /* ************************************************ *
                         *            SELECT a Specific Indicator           *
                         * ************************************************ */
                        if (!codeMis.equals(DASH)) {
                            /* Recupera la misura di prevenzione/mitigazione
                            measure = db.getMeasures(user, codeMis, NOTHING, survey).get(NOTHING);
                            // Recupera i rischi cui è associata
                            risksByMeasure = db.getRisksByMeasure(user, codeMis, survey);
                            // Ha bisogno di personalizzare le breadcrumbs perché sull'indirizzo non c'è il parametro 'p'
                            bC = HomePageCommand.makeBreadCrumbs(ConfigManager.getAppName(), req.getQueryString(), "Misura");*/
                            fileJspT = nomeFileDettaglio;
                        } else {
                            /* ************************************************ *
                             *   Ramo elenco solo misure monitorate (Registro)  *
                             * ************************************************ */
                            measures = db.getMonitoredMeasures(user, VOID_SQL_STRING, Query.GET_ALL_BY_CLAUSE, survey);
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
        // Imposta nella request elenco misure raggruppate per strutture
        if (structs != null) {
            req.setAttribute("strutture", structs);
        }
        // Imposta l'eventuale indirizzo a cui redirigere
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }
        // Imposta struttura contenente tutti i parametri di navigazione già estratti
        if (!params.isEmpty()) {
            req.setAttribute("params", params);
        }
        // Imposta nella request le breadcrumbs in caso siano state personalizzate
        if (bC != null) {
            req.removeAttribute("breadCrumbs");
            req.setAttribute("breadCrumbs", bC);
        }
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }
    
    
    /* **************************************************************** *
     *  Metodi di caricamento dei parametri in strutture indicizzabili  *                     
     *                              (load)                              *
     * **************************************************************** */
    
    /**
     * Valorizza per riferimento una mappa contenente tutti i valori 
     * parametrici riscontrati sulla richiesta.
     * 
     * @param part          la sezione corrente del sito
     * @param req           la HttpServletRequest contenente la richiesta del client
     * @param formParams    mappa da valorizzare per riferimento (ByRef)
     * @throws CommandException se si verifica un problema nella gestione degli oggetti data o in qualche tipo di puntamento
     * @throws AttributoNonValorizzatoException se si fa riferimento a un attributo obbligatorio di bean che non viene trovato
     */
    private static void loadParams(String part, 
                                   HttpServletRequest req,
                                   HashMap<String, LinkedHashMap<String, String>> formParams)
                            throws CommandException {
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
        //survey.put(PARAM_SURVEY, String.valueOf(surveyAsBean.getId()));
        // Aggiunge il tutto al dizionario dei parametri
        formParams.put(PARAM_SURVEY, survey);
        /* **************************************************** *
         *       Ramo di INSERT / UPDATE di un Indicatore       *
         * **************************************************** *
        if (part.equalsIgnoreCase(Query.ADD_TO_PROJECT) || part.equalsIgnoreCase(Query.MODIFY_PART)) {
            GregorianCalendar date = Utils.getUnixEpoch();
            String dateAsString = Utils.format(date, DATA_SQL_PATTERN);
            HashMap<String, String> ind = new HashMap<String, String>();
            ind.put("ind-id",           parser.getStringParameter("ind-id", VOID_STRING));
            ind.put("ind-nome",         parser.getStringParameter("ind-nome", VOID_STRING));
            ind.put("ind-descr",        parser.getStringParameter("ind-descr", VOID_STRING));
            ind.put("ind-baseline",     parser.getStringParameter("ind-baseline", VOID_STRING));
            ind.put("ind-database",     parser.getStringParameter("ind-database", dateAsString));
            ind.put("ind-annobase",     parser.getStringParameter("ind-annobase", VOID_STRING));
            ind.put("ind-target",       parser.getStringParameter("ind-target", VOID_STRING));
            ind.put("ind-datatarget",   parser.getStringParameter("ind-datatarget", dateAsString));
            ind.put("ind-annotarget",   parser.getStringParameter("ind-annotarget",  VOID_STRING));
            ind.put("ind-tipo",         parser.getStringParameter("ind-tipo", VOID_STRING));
            ind.put("ind-wbs",          parser.getStringParameter("ind-wbs", VOID_STRING));
            formParams.put(Query.ADD_TO_PROJECT, ind);
            formParams.put(Query.MODIFY_PART, ind);
        }
        /* **************************************************** *
         *  Ramo di INSERT di una misurazione su un Indicatore  *
         * **************************************************** *
        else if (part.equalsIgnoreCase(Query.MONITOR_PART)) {
            GregorianCalendar date = Utils.getUnixEpoch();
            String dateAsString = Utils.format(date, Query.DATA_SQL_PATTERN);
            HashMap<String, String> ind = new HashMap<String, String>();
            ind.put("ind-id",           parser.getStringParameter("ind-id", VOID_STRING));
            ind.put("prj-id",           parser.getStringParameter("prj-id", VOID_STRING));
            ind.put("mon-nome",         parser.getStringParameter("mon-nome", VOID_STRING));
            ind.put("mon-descr",        parser.getStringParameter("mon-descr", VOID_STRING));
            ind.put("mon-data",         parser.getStringParameter("mon-data", dateAsString));
            ind.put("mon-milestone",    parser.getStringParameter("mon-milestone", VOID_STRING));
            formParams.put(Query.MONITOR_PART, ind);
        }
        /* ******************************************************** *
         *  Ramo di INSERT di ulteriori informazioni da aggiungere  *
         *      a un Indicatore (p.es.: target rivisto, etc.)       *
         * ******************************************************** *
        else if (part.equalsIgnoreCase(Query.EXTRAINFO_PART)) {
            GregorianCalendar date = Utils.getUnixEpoch();
            String dateAsString = Utils.format(date, Query.DATA_SQL_PATTERN);
            HashMap<String, String> ind = new HashMap<String, String>();
            ind.put("ind-id",           parser.getStringParameter("ind-id", Utils.VOID_STRING));
            ind.put("prj-id",           parser.getStringParameter("prj-id", Utils.VOID_STRING));
            ind.put("ext-target",       parser.getStringParameter("ext-target", Utils.VOID_STRING));
            ind.put("ext-datatarget",   parser.getStringParameter("ext-datatarget", dateAsString));
            ind.put("ext-annotarget",   parser.getStringParameter("ext-annotarget",  Utils.VOID_STRING));
            ind.put("ext-note",         parser.getStringParameter("ext-note", Utils.VOID_STRING));
            ind.put("ext-data",         parser.getStringParameter("ext-data", dateAsString));
            formParams.put(Query.EXTRAINFO_PART, ind);
        }
        /* ******************************************************** *
         *  Ramo di UPDATE di ulteriori informazioni da aggiungere  *
         *      a un Indicatore (p.es.: target rivisto, etc.)       *
         * ******************************************************** *
        else if (part.equalsIgnoreCase(Query.UPDATE_PART)) {
            GregorianCalendar date = Utils.getUnixEpoch();
            String dateAsString = Utils.format(date, Query.DATA_SQL_PATTERN);
            HashMap<String, String> ind = new HashMap<String, String>();
            ind.put("ind-id",           parser.getStringParameter("ind-id", Utils.VOID_STRING));
            ind.put("prj-id",           parser.getStringParameter("prj-id", Utils.VOID_STRING));
            ind.put("ext-target",       parser.getStringParameter("modext-target", Utils.VOID_STRING));
            ind.put("ext-datatarget",   parser.getStringParameter("ext-datatarget", dateAsString));
            ind.put("ext-annotarget",   parser.getStringParameter("ext-annotarget",  Utils.VOID_STRING));
            ind.put("ext-note",         parser.getStringParameter("modext-note", Utils.VOID_STRING));
            ind.put("modext-auto",      parser.getStringParameter("modext-auto", dateAsString));
            formParams.put(Query.UPDATE_PART, ind);
        }*/
    }
    
    
    /* **************************************************************** *
     *                   Metodi di travaso dei dati                     *                     
     *                    (decant, filter, purge)                       *
     * **************************************************************** */
    

    
}