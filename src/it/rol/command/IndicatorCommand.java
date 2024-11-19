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

package it.rol.command;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.DBWrapper;
import it.rol.Main;
import it.rol.Query;
import it.rol.SessionManager;
import it.rol.Utils;
import it.rol.bean.ActivityBean;
import it.rol.bean.CodeBean;
import it.rol.bean.DepartmentBean;
import it.rol.bean.ItemBean;
import it.rol.bean.MeasureBean;
import it.rol.bean.PersonBean;
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
     * Pagina per mostrare la lista delle misure aventi dettagli di monitoraggio (registro delle misure monitorate)
     */
    private static final String nomeFileElenco = "/jsp/icElenco.jsp";
    /**
     * Pagina per mostrare la lista delle misure monitorate raggruppate per struttura (pagina iniziale monitoraggio)
     */
    private static final String nomeFileElencoMisure  = "/jsp/icMisure.jsp";
    /**
     * Pagina per mostrare i dettagli di una misura monitorata
     */
    private static final String nomeFileMisura = "/jsp/icMisura.jsp"; 
    /**
     * Pagina per mostrare la form di aggiunta dei dettagli di una misura monitorata
     */
    private static final String nomeFileInsertMisura = "/jsp/icMisuraForm.jsp";    
    /**
     * Pagina per mostrare la lista degli indicatori di una misura monitorata
     */
    private static final String nomeFileElencoIndicatori = "/jsp/icIndicatori.jsp";
    /**
     * Pagina per mostrare i dettagli di un indicatore di monitoraggio
     */
    private static final String nomeFileDettaglio = "/jsp/icIndicatore.jsp";
    /**
     * Pagina per mostrare la maschera di inserimento/modifica di un indicatore di monitoraggio
     */
    private static final String nomeFileInsertIndicatore = "/jsp/icIndicatoreForm.jsp";    
    /**
     * Pagina per mostrare la lista delle misurazioni (collegate agli indicatori) di una misura monitorata
     */
    private static final String nomeFileElencoMisurazioni = "/jsp/icMisurazioni.jsp";
    /**
     * Pagina per mostrare i dettagli di una misurazione
     */
    private static final String nomeFileMisurazione = "/jsp/icMisurazione.jsp";
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
        nomeFile.put(PART_INSERT_INDICATOR,     nomeFileInsertIndicatore);
        nomeFile.put(PART_INSERT_MEASUREMENT,   nomeFileMisurazione);
        nomeFile.put(PART_INSERT_MONITOR_DATA,  nomeFileInsertMisura);
        //nomeFile.put(PART_SELECT_MONITOR_DATA,  nomeFileResumeMeasure);
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
        // Fase specifica di una misura specifica
        ActivityBean phase = null;
        // Elenco di misure di prevenzione monitorate
        ArrayList<MeasureBean> measures = null;
        // Elenco strutture collegate alla rilevazione
        ArrayList<DepartmentBean> structs = null;
        // Elenco di rischi associati a una misura
        ArrayList<ItemBean> risksByMeasure = null;
        // Tabella che conterrà i valori dei parametri passati dalle form
        HashMap<String, LinkedHashMap<String, String>> params = null;
        // Predispone le BreadCrumbs personalizzate per la Command corrente
        LinkedList<ItemBean> bC = null;
        // Titolo pagina
        String tP = null;
        // Variabile contenente l'indirizzo per la redirect da una chiamata POST a una chiamata GET
        String redirect = null;
        // Data di oggi sotto forma di oggetto Date
        java.util.Date today = Utils.convert(Utils.getCurrentDate());
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
        // Recupera o inizializza 'id misurazione'
        String codeMon = parser.getStringParameter("nliv", DASH);
        // Recupera o inizializza 'id fase di attuazione'
        int idFas = parser.getIntParameter("idF", DEFAULT_ID);
        // Recupera o inizializza 'id indicatore'
        int idInd = parser.getIntParameter("idI", DEFAULT_ID);
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
                /* ======================= @PostMapping ======================= */
                if (write) {
                    // Controlla quale azione vuole fare l'utente
                    if (nomeFile.containsKey(part)) {
                        // Controlla quale richiesta deve gestire
                        if (part.equalsIgnoreCase(PART_INSERT_MONITOR_DATA)) {
                            /* ------------------------------------------------ *
                             * PROCESS Form to INSERT Measure Monitoring Details*
                             * ------------------------------------------------ */
                            db.insertMeasureDetails(user, params);
                            // Prepara la redirect 
                            redirect = ConfigManager.getEntToken() + EQ + COMMAND_INDICATOR + 
                                       AMPERSAND + "p" + EQ + PART_MEASURES +
                                       AMPERSAND + "mliv" + EQ + codeMis + 
                                       AMPERSAND + PARAM_SURVEY + EQ + codeSur;
                                       //+AMPERSAND + MESSAGE + EQ + "newMes";
                        } else if (part.equalsIgnoreCase(PART_INSERT_INDICATOR)) {
                            /* ------------------------------------------------ *
                             *         PROCESS Form to INSERT Indicator         *
                             * ------------------------------------------------ */
                            db.insertIndicatorMeasure(user, params);
                            // Prepara la redirect 
                            redirect = ConfigManager.getEntToken() + EQ + COMMAND_INDICATOR + 
                                       AMPERSAND + "p" + EQ + PART_MEASURES +
                                       AMPERSAND + "mliv" + EQ + codeMis + 
                                       AMPERSAND + PARAM_SURVEY + EQ + codeSur;
                                       //+AMPERSAND + MESSAGE + EQ + "newRel#rischi-fattori-misure";
                        }
                    } else {
                        // Azione di default
                        // do delete?
                    }
                /* ======================== @GetMapping ======================= */
                } else {
                    /* ------------------------------------------------ *
                     *                        Part                      *
                     * ------------------------------------------------ */
                    if (nomeFile.containsKey(part)) {
                        // Recupera le breadcrumbs
                        LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                        // Gestione Rami
                        if (part.equalsIgnoreCase(PART_MEASURES)) {
                            // Controlla la presenza dell'id di una misura
                            if (codeMis.equals(DASH)) {
                            /* ------------------------------------------------ *
                             *      Elenco Misure raggruppate per struttura     *
                             * ------------------------------------------------ */
                                structs = db.getMeasuresByStructs(user, survey);
                                // Aggiunge una foglia alle breadcrumbs
                                bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, NOTHING, "Misure-Struttura");
                                // Imposta la pagina
                                fileJspT = nomeFile.get(part);
                            } else {
                            /* ------------------------------------------------ *
                             *             Dettagli misura monitorata           *
                             * ------------------------------------------------ */
                                // Recupera la misura di prevenzione/mitigazione
                                measure = MeasureCommand.retrieveMeasure(user, codeMis, survey, db);
                                // Recupera i rischi cui è associata
                                risksByMeasure = db.getRisksByMeasure(user, codeMis, survey);
                                // Personalizza le breadcrumbs
                                bC = loadBreadCrumbs(breadCrumbs, part, survey); 
                                // Imposta la pagina
                                fileJspT = nomeFileMisura;
                            }
                        } else if (part.equalsIgnoreCase(PART_INDICATOR)) {
                            // Controlla la presenza dell'id di un indicatore
                            if (idInd > DEFAULT_ID) {
                            /* ------------------------------------------------ *
                             *       Dettagli di un indicatore di dato id       *
                             * ------------------------------------------------ */
                                measure = MeasureCommand.retrieveMeasure(user, codeMis, survey, db);
                                // Imposta la pagina
                                fileJspT = nomeFileDettaglio;
                            } else {
                            /* ------------------------------------------------ *
                             *          Elenco indicatori di una misura         *
                             * ------------------------------------------------ */
                                measure = MeasureCommand.retrieveMeasure(user, codeMis, survey, db);
                                // Personalizza le breadcrumbs
                                bC = loadBreadCrumbs(breadCrumbs, part, survey);
                                // Imposta la pagina
                                fileJspT = nomeFile.get(part);
                            }
                        } else if (part.equalsIgnoreCase(PART_MONITOR)) {
                            /* ------------------------------------------------ *
                             * Ramo elenco misurazioni di una misura monitorata *
                             * ------------------------------------------------ */
                            // Imposta la pagina
                            fileJspT = nomeFile.get(part);
                            /* ************************************************ *
                             *          Ramo dettagli di una misurazione        *
                             * ************************************************ */
                            
                            
                        } else if (part.equalsIgnoreCase(PART_INSERT_MONITOR_DATA)) {
                            /* ------------------------------------------------ *
                             * Form aggiunta dettagli monitoraggio a una misura *
                             * ------------------------------------------------ */
                            if (!codeMis.equals(DASH)) {
                                // Recupera la misura di prevenzione/mitigazione
                                measure = MeasureCommand.retrieveMeasure(user, codeMis, survey, db);
                                // Recupera i rischi cui è associata
                                risksByMeasure = db.getRisksByMeasure(user, codeMis, survey);
                                // Personalizza le breadcrumbs
                                bC = loadBreadCrumbs(breadCrumbs, part, survey); 
                                // Pagina
                                fileJspT = nomeFile.get(part);
                            }
                        //} else if (part.equalsIgnoreCase(VOID_STRING)) {
                            /* ************************************************ *
                             *  Pagina riepilogo dettagli monitoraggio inseriti *
                             * ************************************************ */
                        } else if (part.equalsIgnoreCase(PART_INSERT_INDICATOR)) {
                            /* ------------------------------------------------ *
                             *           Inserimento nuovo indicatore           *
                             * ------------------------------------------------ */
                            // Recupera la fase cui si vuol aggiungere l'indicatore
                            phase = db.getMeasureActivity(user, codeMis, idFas, survey);
                            // Pagina
                            fileJspT = nomeFile.get(part);
                        //} else if (part.equalsIgnoreCase(VOID_STRING)) {
                            /* ************************************************ *
                             *     Form inserimento misurazione di una misura   *
                             * ************************************************ */
                            
                        }

                    } else {
                        /* ------------------------------------------------ *
                         *            SELECT a Specific Indicator           *
                         * ------------------------------------------------ */
                        if (!codeMis.equals(DASH)) {
                            /* Recupera la misura di prevenzione/mitigazione
                            measure = db.getMeasures(user, codeMis, NOTHING, survey).get(NOTHING);
                            // Recupera i rischi cui è associata
                            risksByMeasure = db.getRisksByMeasure(user, codeMis, survey);
                            // Ha bisogno di personalizzare le breadcrumbs perché sull'indirizzo non c'è il parametro 'p'
                            bC = HomePageCommand.makeBreadCrumbs(ConfigManager.getAppName(), req.getQueryString(), "Misura");*/
                            fileJspT = nomeFileInsertIndicatore;
                        } else {
                            /* ------------------------------------------------ *
                             *   Ramo elenco solo misure monitorate (Registro)  *
                             * ------------------------------------------------ */
                            measures = MeasureCommand.filter(db.getMeasures(user, VOID_SQL_STRING, Query.GET_ALL_BY_CLAUSE, survey));
                            tP = "Registro delle misure monitorate";
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
        // Imposta nella request elenco tipologie di indicatori
        if (types != null) {
            req.setAttribute("tipi", types);
        }
        // Imposta nella request elenco strutture ciascuna con all'interno le sue misure
        if (structs != null) {
            req.setAttribute("strutture", structs);
        }
        // Imposta nella request elenco misure aventi dettagli monitoraggio
        if (measures != null) {
            req.setAttribute("misure", measures);
        }
        // Imposta nella request oggetto misura specifica
        if (measure != null) {
            req.setAttribute("misura", measure);
        }
        // Imposta in request elenco dei rischi cui una misura è applicata
        if (risksByMeasure != null) {
            req.setAttribute("rischi", risksByMeasure);
        }
        // Imposta nella request specifica fase di attuazione di una misura
        if (phase != null) {
            req.setAttribute("fase", phase);
        }
        // Imposta l'eventuale indirizzo a cui redirigere
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }   
        // Imposta struttura contenente tutti i parametri di navigazione già estratti
        if (!params.isEmpty()) {
            req.setAttribute("params", params);
        }
        // Titolo pagina in caso sia significativo
        if (tP != null && !tP.equals(VOID_STRING)) {
            req.setAttribute("tP", tP);
        }    
        // Imposta nella request le breadcrumbs in caso siano state personalizzate
        if (bC != null) {
            req.removeAttribute("breadCrumbs");
            req.setAttribute("breadCrumbs", bC);
        }
        // Imposta nella request data di oggi 
        req.setAttribute("now", today);
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
                            throws CommandException,
                                   AttributoNonValorizzatoException {
        LinkedHashMap<String, String> survey = new LinkedHashMap<>();
        LinkedHashMap<String, String> measure = new LinkedHashMap<>();
        LinkedHashMap<String, String> indicator = null;
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
        // Aggiunge il tutto al dizionario dei parametri
        formParams.put(PARAM_SURVEY, survey);
        /* ******************************************************** *
         *  Ramo di INSERT di ulteriori informazioni da aggiungere  *
         *      a una misura (dettagli relativi al monitoraggio)    *
         * ******************************************************** */
        if (part.equalsIgnoreCase(PART_INSERT_MONITOR_DATA)) {
            GregorianCalendar date = Utils.getCurrentDate();
            String dateAsString = Utils.format(date, DATA_SQL_PATTERN);
            measure.put("code",         parser.getStringParameter("ms-code", VOID_STRING));
            measure.put("data",         dateAsString);            
            measure.put("piao",         parser.getStringParameter("ms-piao", VOID_STRING));
            // Fasi di attuazione (Array)
            String[] fasi = req.getParameterValues("ms-fasi");
            // Aggiunge tutte le fasi di attuazione trovate
            decantStructures("fase", fasi, measure);
            // Aggiunge i dettagli monitoraggio al dizionario dei parametri
            formParams.put(part, measure);
        }
        /* **************************************************** *
         *       Ramo di INSERT / UPDATE di un Indicatore       *
         * **************************************************** */
        else if (part.equalsIgnoreCase(PART_INSERT_INDICATOR) /*|| part.equalsIgnoreCase(Query.MODIFY_PART)*/ ) {
            indicator = new LinkedHashMap<>();            
            GregorianCalendar date = Utils.getUnixEpoch();
            String dateAsString = Utils.format(date, DATA_SQL_PATTERN);
            indicator.put("fase",       parser.getStringParameter("ind-fase",       VOID_STRING));
            indicator.put("tipo",       parser.getStringParameter("ind-tipo",       VOID_STRING));
            indicator.put("nome",       parser.getStringParameter("ind-nome",       VOID_STRING));
            indicator.put("desc",       parser.getStringParameter("ind-descr",      VOID_STRING));
            indicator.put("base",       parser.getStringParameter("ind-baseline",   VOID_STRING));
            indicator.put("database",   parser.getStringParameter("ind-database",   dateAsString));
            indicator.put("targ",       parser.getStringParameter("ind-target",     VOID_STRING));
            indicator.put("datatarg",   parser.getStringParameter("ind-datatarget", dateAsString));
            formParams.put(part, indicator);
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
    
    
    private static LinkedList<ItemBean> loadBreadCrumbs(LinkedList<ItemBean> breadCrumbs, 
                                                        String part,
                                                        CodeBean survey) 
                                                 throws CommandException {
        LinkedList<ItemBean> bC = null;
        try {
            if (part.equalsIgnoreCase(PART_MEASURES)) {
                // Url da sostituire al posto di quello di una breadcrumb esistente
                String url = ConfigManager.getAppName() + ROOT_QM + ConfigManager.getEntToken() + EQ + COMMAND_MEASURE + AMPERSAND + PARAM_SURVEY + EQ + survey.getNome();
                // Preparazione nuova breadcrumb per puntare sulla command delle misure
                ItemBean crumb = new ItemBean("Misure", "Misure", url, SUB_MENU);
                // Sostituzione di una breadcrumb esistente con la nuova
                bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_1, crumb);
                // Aggiunta inoltre di una foglia
                bC = HomePageCommand.makeBreadCrumbs(bC, NOTHING, "Dettagli");
            } else if (part.equalsIgnoreCase(PART_INDICATOR)) {
                // Url da sostituire al posto di quello di una breadcrumb esistente
                String url = ConfigManager.getAppName() + ROOT_QM + ConfigManager.getEntToken() + EQ + COMMAND_INDICATOR + AMPERSAND + "p" + EQ + PART_MEASURES + AMPERSAND + PARAM_SURVEY + EQ + survey.getNome();
                // Preparazione nuova breadcrumb per puntare sulla command delle misure
                ItemBean crumb = new ItemBean("Monitoraggio", "Monitoraggio", url, SUB_MENU);
                // Sostituzione di una breadcrumb esistente con la nuova
                bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_1, crumb);
                // Aggiunta inoltre di una foglia
                bC = HomePageCommand.makeBreadCrumbs(bC, NOTHING, "Indicatori");
            } else if (part.equalsIgnoreCase(PART_INSERT_MONITOR_DATA)) {
                // Url da sostituire al posto di quello di una breadcrumb esistente
                String url = ConfigManager.getAppName() + ROOT_QM + ConfigManager.getEntToken() + EQ + COMMAND_MEASURE + AMPERSAND + PARAM_SURVEY + EQ + survey.getNome();
                // Preparazione nuova breadcrumb per puntare sulla command delle misure
                ItemBean crumb = new ItemBean("Misure", "Misure", url, SUB_MENU);
                // Sostituzione di una breadcrumb esistente con la nuova
                bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_1, crumb);
                // Aggiunta inoltre di una foglia
                bC = HomePageCommand.makeBreadCrumbs(bC, NOTHING, "Dettagli");
            }
            return bC;
        }  catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di valori da bean.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        }
    }
    
    
    /* **************************************************************** *
     *                   Metodi di travaso dei dati                     *                     
     *                    (decant, filter, purge)                       *
     * **************************************************************** */
    
    /**
     * Dati in input un array di valori e un livello numerico, distribuisce
     * tali valori in una struttura dictionary, passata come parametro, 
     * assegnandone ciascuno a una chiave diversa, costruita
     * in base a un progressivo ed un'etichetta passata come parametro.
     * In pratica, questo metodo serve a trasformare una serie di valori
     * di campi aventi nomi uguali in una form, che quindi vengono passati
     * in un unico array avente lo stesso nome dei campi omonimi, 
     * in una serie di parametri distinti, ciascuno avente per nome 
     * la stessa radice ma contraddistinto da un progressivo.
     * 
     * @param label     etichetta per i nomi dei parametri
     * @param values    valori dei campi selezionati
     * @param params    mappa dei parametri della richiesta
     */
    public static void decantStructures(String label,
                                        String[] values,
                                        LinkedHashMap<String, String> params) {
        int index = NOTHING;
        int size = ELEMENT_LEV_1; 
        if (values != null) { // <- Controllo sull'input
            while (index < values.length) {
                params.put(label + size,  values[index]);
                size++;
                index++;
            }
        }        
    }
    
}
