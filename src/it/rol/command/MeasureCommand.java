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
        // Misura di prevenzione specifica
        MeasureBean measure = null;
        // Elenco di misure di prevenzione
        ArrayList<MeasureBean> measures = null;
        // Elenco parziale delle misure sulla base di alcuni fattori abilitanti
        ArrayList<MeasureBean> suggestedMeasures = null;
        // Elenco di misure depurate di altre liste (suggerite, già applicate...)
        ArrayList<MeasureBean> lessMeasures = null;
        // Elenco di rischi associati a una misura
        ArrayList<ItemBean> risksByMeasure = null;
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
            allMeasures = db.getMeasures(user, VOID_SQL_STRING, Query.GET_ALL_BY_CLAUSE, survey);
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
                            boolean check = db.isMeasureRiskProcess(user, params, allMeasures);
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
                            // Recupera tutte le misure suggerite in base ai fattori abilitanti del rischio
                            suggestedMeasures = db.getMeasuresByFactors(user, risk, Query.GET_ALL, survey);
                            // Recupera le misure suggerite, tolte quelle già applicate al rischio e al processo correnti
                            lessMeasures = db.getMeasuresByFactors(user, risk, !Query.GET_ALL, survey);
                            // Toglie dall'elenco totale delle misure tutte quelle suggerite e quelle già applicate
                            measures = purge(allMeasures, suggestedMeasures, risk);
                            // Raggruppa le misure suggerite per tipologia
                            measuresByType = decantMeasures(types, lessMeasures);
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
                        if (!codeMis.equals(DASH)) {
                            // Recupera la misura di prevenzione/mitigazione
                            measure = retrieveMeasure(user, codeMis, survey, db);
                            // Recupera i rischi cui è associata
                            risksByMeasure = db.getRisksByMeasure(user, codeMis, survey);
                            // Ha bisogno di personalizzare le breadcrumbs perché sull'indirizzo non c'è il parametro 'p'
                            bC = HomePageCommand.makeBreadCrumbs(ConfigManager.getAppName(), req.getQueryString(), "Misura");
                            fileJspT = nomeFileDettaglio;
                        } else {
                            /* ************************************************ *
                             *              SELECT List of Measures             *
                             * ************************************************ */
                            measures = allMeasures;
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
        // Imposta in request elenco delle misure suggerite in base ai fattori abilitanti, raggruppate per nome del tipo
        if (measuresByType != null) {
            req.setAttribute("misureTipo", measuresByType);
        }
        // Imposta nella request elenco completo strutture
        if (structs != null) {
            req.setAttribute("strutture", structs);
        }
        // Imposta nella request elenco completo strutture sotto forma di dictionary
        if (flatStructs != null) {
            req.setAttribute("elencoStrutture", flatStructs);
        }
        // Imposta nella request oggetto misura di prevenzione specifica
        if (measure != null) {
            req.setAttribute("misura", measure);
        }
        // Imposta in request elenco dei rischi cui una misura è applicata
        if (risksByMeasure != null) {
            req.setAttribute("rischi", risksByMeasure);
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
        }
    }
    
    
    /* **************************************************************** *
     *                  Metodi di recupero dei dati                     *                     
     *                          (retrieve)                              *
     * **************************************************************** */
    
    /**
     * <p>Estrae i dettagli di una misura del registro (quindi priva 
     * dei dettagli necessari al monitoraggio) dato il codice 
     * e la rilevazione.</p>
     * 
     * @param user      utente loggato
     * @param code      codice della misura cercata
     * @param survey    oggetto rilevazione
     * @param db        databound gia' istanziato
     * @return <code>MeasureBean</code> - misura, da registro, desiderata
     * @throws WebStorageException se si verifica un problema a livello di query o di estrazione
     * @throws CommandException se si verifica un problema nel recupero di valori o in qualche altro tipo di puntamento
     */
    public static MeasureBean retrieveMeasure(PersonBean user, 
                                              String code, 
                                              CodeBean survey, 
                                              DBWrapper db)
                                       throws WebStorageException, 
                                              CommandException {
        // Recupera la misura di dato codice e data rilevazione
        return db.getMeasures(user, code, NOTHING, survey).get(NOTHING);
    }


    /* **************************************************************** *
     *                   Metodi di travaso dei dati                     *                     
     *                    (decant, filter, purge)                       *
     * **************************************************************** */
    
    /**
     * Dati in input un array di valori e un livello numerico, distribuisce
     * tali valori in una struttura dictionary, passata come parametro, 
     * assegnandone ciascuno a una chiave diversa, costruita
     * in base a un progressivo ed un valore intero passato come parametro.
     * 
     * @param level      livello della struttura in organigramma
     * @param structures valori delle strutture selezionate
     * @param params     mappa dei parametri della richiesta
     */
    private static void decantStructures(int level,
                                         String[] structures,
                                         LinkedHashMap<String, String> params) {
        int index = NOTHING;
        int size = ELEMENT_LEV_1; 
        if (structures != null) { // <- Controllo sull'input
            while (index < structures.length) {
                params.put("sg" + size + DASH + level,  structures[index]);
                size++;
                index++;
            }
        }        
    }
    
    
    /**
     * Dati in input un array di misure di prevenzione e l'elenco completo
     * delle tipologie di misura, suddivide le misure dell'array in base alla loro
     * tipologia e le restituisce partizionate per tipologia in una tabella, 
     * appunto, indicizzata per nome di tipologia di misura.<br />
     * NOTA: siccome la relazione tra la misura e la tipologia e' molti a molti,
     * la stessa misura potrebbe saltare fuori in pi&uacute; sottoliste e 
     * il metodo dovrebbe gestire, o quanto meno segnalare, questo aspetto.
     * 
     * @param measureTypes elenco delle tipologie di misure di prevenzione
     * @param measures  elenco delle misure di prevenzione
     * @return <code>LinkedHashMap&lt;String, ArrayList&lt;MeasureBean&gt;&gt;</code> - tabella contenente le stesse misure ottenute in argomento ma raggruppate in sottoliste indicizzate per nome tipologia
     * @throws CommandException se si verifica un problema nel recupero di un valore obbligatorio di un bean o in qualche puntamento
     * @deprecated  il metodo non effettua controlli riguardo le ridondanze nelle sottoliste
     */
    @Deprecated
    private static LinkedHashMap<String, ArrayList<MeasureBean>> decantMeasures(ArrayList<CodeBean> measureTypes,
                                                                                ArrayList<MeasureBean> measures) 
                                                                         throws CommandException {
        LinkedHashMap<String, ArrayList<MeasureBean>> measuresByTypes = new LinkedHashMap<>();
        try {
            for (CodeBean type : measureTypes) {
                ArrayList<MeasureBean> measuresByType = new ArrayList<>();
                for (MeasureBean meas : measures) {
                    ArrayList<CodeBean> typologies = meas.getTipologie();
                    CodeBean typology = typologies.get(NOTHING);
                    if ( (type.getId() == typology.getId()) &&
                         (!measuresByType.contains(meas)) ) {
                        measuresByType.add(meas);
                    }
                }
                measuresByTypes.put(type.getNome(), measuresByType);
            }
            return measuresByTypes;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Attributo obbligatorio non recuperabile.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        } 
    }
    
    
    /**
     * Dato un elenco complessivo di misure, data una lista parziale 
     * e data una lista di misure gi&agrave; applicate ad un rischio 
     * (e contenute nel rischio stesso), sottrae dalla lista completa 
     * le altre due e restituisce l'elenco risultante.
     * 
     * @param fullList  elenco complessivo di misure di prevenzione
     * @param partList  elenco parziale di misure di prevenzione
     * @param risk      rischio corruttivo contenente le misure che gia' gli sono state applicate
     * @return lista vettoriale ottenuta per differenza tra l'elenco complessivo e gli altri due
     * @see    it.rol.bean.MeasureBean#equals(Object)
     */
    private static ArrayList<MeasureBean> purge(ArrayList<MeasureBean> fullList, 
                                                ArrayList<MeasureBean> partList,
                                                RiskBean risk) {
        ArrayList<MeasureBean> results = (ArrayList<MeasureBean>) fullList.clone();
        for (MeasureBean part : partList) {
            if (results.contains(part)) {
                results.remove(part);
            }
        }
        return results;
    }
    
    
    /**
     * Dato un array ed un suo elemento, restituisce il valore dell'indice
     * corrispondente all'elemento indicato.
     * NOTA: esistono vari modi pi&uacute; efficienti per effettuare questa
     * ricerca. Ad esempio, si pu&ograve; utilizzare il metodo:
     * //int weight = Arrays.binarySearch(LIVELLI_RISCHIO, indicator.getInformativa());
     * Putroppo per&ograve; questo metodo non funziona bene se l'array &egrave;
     * un array di Stringhe, probabilmente perch&eacute; effettua il controllo
     * sul valore dell'elemento tramite il puntamento diretto 
     * (String == anotherString) e non tramite il controllo del contenuto 
     * (String.equals(anotherString)). A me trovava sistematicamente la 
     * corrispondenza con il primo valore, restituendo l'indice corrispondente,
     * ma non con gli altri valori, che pure erano elementi dell'array.
     * Rinunciando alla ricerca binaria, esistono altri metodi di confronto
     * pi&ucute; raffinati di questo, come ArrayUtils.indexOf della libreria
     * Commons Lang di Apache; tuttavia, essendo il contenuto dell'array 
     * formato da oggetti e non da tipi primitivi, per maggior sicurezza
     * implemento questo metodo in cui ho il controllo direttamente 
     * del criterio di confronto.
     * 
     * @param array     l'array in cui trovare l'elemento specificato
     * @param element   elemento dell'array di cui individuare la posizione
     * @return <code>int</code> - l'indice in cui si trova l'elemento cercato
     */
    private static int getIndex(String[] array, String element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(element)) {
               return i;
            }
         }
         return -1; // Element not found
    }
    
    
    /* **************************************************************** *
     *     Metodi di implementazione degli algoritmi di mitigazione     *                     
     * **************************************************************** */
    
    /**
     * Implementa l'algoritmo di mitigazione in funzione delle misure
     * applicate.
     * Ricevuto un indicatore contenente il valore del PxI di un rischio
     * ed una lista di misure applicate al rischio stesso, ricalcola
     * il valore del PxI sottraendo una quantit&agrave; predefinita per
     * ogni misura trovata, in funzione del carattere della misura stessa.
     * L'operazione &egrave; possibile data una funzione di trasformazione
     * che associa ad ogni possibile valore del PxI un numero intero.
     * La differenza tra il valore numerico del PxI trasformato e la somma 
     * delle misure pu&ograve; essere sufficiente a determinare un decremento
     * del valore del PxI originario, oppure no. Ci&ograve; dipende dal fatto
     * che sia stato raggiunto o meno l'intero inferiore rispetto a quello
     * del PxI originale trasformato in numero.
     * Esempio: se il PxI originale &egrave; MEDIO, esso in base alla
     * funzione di trasformazione vale 2; se a seguito dell'applicazione
     * delle misure si ottiene una somma algebrica di 1.5 o 1.0 allora 
     * il rischio &egrave; stato ridotto, passando a MEDIO a BASSO; 
     * altrimenti, se la parte intera non &egrave; strettamente minore 
     * di 2, il rischio resta MEDIO.
     * Sulla base di questo esempio si evince che l'algoritmo considera
     * sempre la parte intera, mai la parte decimale.
     * 
     * @param indicator oggetto contenente il valore del PxI assegnato al rischio
     * @param measures  lista di misure applicate al rischio
     * @return <code>InterviewBean</code> - oggetto contenente il valore del PxI del rischio, eventualmente mitigato
     * @throws CommandException se si verifica un problema nel recupero di un attributo del bean o in qualche operazione o puntamento
     */
    public static InterviewBean mitigate(final InterviewBean indicator,
                                         final ArrayList<MeasureBean> measures)
                                  throws CommandException {
        try {
            // Controllo sull'input
            if (measures == null || measures.isEmpty()) {
                // Se non sono state applicate misure, inutile continuare
                return indicator;
            }
            // Mitigated Indicator
            InterviewBean mIndicator = new InterviewBean();
            // Initialize reduction to zero
            float reduction = NOTHING;
            // Recupera il valore indice corrispondente al valore PxI corrente
            int weight = getIndex(LIVELLI_RISCHIO, indicator.getInformativa());
            // Nuovo indice da considerare ai fini del calcolo del PxI mitigato
            int index = weight;
            // Cicla sulle misure applicate 
            for (MeasureBean measure : measures) {
                // Per ogni misura generica toglie uno 0,5
                if (measure.getCarattere().getInformativa().equals("G")) {
                    reduction += WEIGHT_GENERIC;
                // Per ogni misura specifica toglie un 1    
                } else if (measure.getCarattere().getInformativa().equals("S")) {
                    reduction += WEIGHT_SPECIFIC;
                // Una misura puo' solo essere o generica o specifica
                } else {
                    String msg = FOR_NAME + "Si e\' verificato un problema nel recupero del carattere della misura.\n";
                    LOG.severe(msg);
                    throw new CommandException(msg);
                }
            }
            // Applica la riduzione (nuovo indice = [peso indicatore - riduzione])
            float f = weight - reduction;
            // Controllo che l'indice non sia sceso al di sotto del minimo
            if (f < NOTHING) {
                // Se le misure porterebbero al di sotto del minimo, vale il minimo
                index = NOTHING;
            } else {
                // Se l'indice calcolato è intero se lo tiene così
                if (f % 1 == 0) {
                    index = (int) f;
                } else {
                    // Altrimenti tiene la parte intera e la incrementa di 1
                    index = ((int) f) + 1; 
                }
            }
            // Converte l'indice calcolato in valore nominale applicando la funzione di corrispondenza
            String result = LIVELLI_RISCHIO[index];
            // Valorizza e restituisce l'indicatore mitigato
            mIndicator.setNome(indicator.getNome());
            mIndicator.setInformativa(result);
            //mIndicator.setDescrizione(indicatorByCode.get(indicator.getNome()).getInformativa());
            mIndicator.setRisposte(indicator.getRisposte());
            mIndicator.setProcesso(indicator.getProcesso());
            return mIndicator;
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
     * Implementa l'algoritmo di ricalcolo del PxI complessivo a partire
     * dai valori dei PxI dei rischi, calmierati e arrotondati.
     * Dato in input un elenco di rischi, ciascuno che deve contenere
     * un valore di PxI del rischio mitigato in funzione delle misure
     * applicate, ottiene un valore unico del PxI per il processo
     * cui i rischi calmierati sono collegati; il metodo pu&ograve;
     * essere applicato alla fase della stima o a quella del monitoraggio,
     * in quanto l'algoritmo di ricalcolo resta il medesimo e il nome
     * del PxI (stima o monitoraggio) viene passato come parametro.
     * 
     * @param PIN il nome dell'indicatore (PxI stima o PxI effettivo)
     * @param mitigatingRisks   elenco di rischi cui sono state applicate le misure di mitigazione
     * @return <code>InterviewBean</code> - oggetto contenente il livello di rischio dell'indice PxI, calcolato dall'algoritmo in base ai valori dei PxI calmierati ricevuti
     * @throws CommandException se si verifica un problema nel recupero di un attributo di un bean o un puntamento erroneo
     */
    public static InterviewBean computePIMitigated(final String PIN,
                                                   final ArrayList<RiskBean> mitigatingRisks)
                                            throws CommandException {
        InterviewBean mitigatedPI = new InterviewBean();
        String result = null;
        int sum = NOTHING;
        try {
            // Cicla sui rischi mitigati
            for (RiskBean risk : mitigatingRisks) {
                // Recupera il valore indice corrispondente al valore PxI corrente
                int weight = getIndex(LIVELLI_RISCHIO, risk.getLivello());
                // Lo somma agli altri
                sum += weight;
            }
            // Divide i pesi dei PxI mitigati per il numero di rischi trovati
            float average = (float) sum / (float) mitigatingRisks.size();
            // Arrotonda le medie ottenute all'intero più prossimo
            int index = Math.round(average);
            // Ottiene il corrispettivo valore testuale
            result = LIVELLI_RISCHIO[index];
            // Valorizza e restituisce l'oggetto per l'indicatore
            mitigatedPI.setNome(PIN);
            mitigatedPI.setInformativa(result);
            mitigatedPI.setDescrizione("PxI Mitigato");
            mitigatedPI.setAutoreUltimaModifica("PxI Rischi");
            //mitigatedPI.setProcesso(extraInfo);
            return mitigatedPI;
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
    
}