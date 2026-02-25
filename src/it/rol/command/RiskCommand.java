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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.db.DBWrapper;
import it.rol.Main;
import it.rol.SessionManager;
import it.rol.bean.CodeBean;
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
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class RiskCommand extends ItemBean implements Command, Constants {
    
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
     * Pagina a cui la command reindirizza per mostrare la lista dei rischi corruttivi
     */
    private static final String nomeFileElenco = "/jsp/riElenco.jsp";
    /**
     * Pagina a cui la command reindirizza per mostrare i dettagli di un rischio
     */
    private static final String nomeFileDettaglio = "/jsp/riRischio.jsp";
    /**
     * Pagina a cui la command fa riferimento per mostrare la form di ricerca
     */
    private static final String nomeFileInsertRisk = "/jsp/riRischioForm.jsp";
    /**
     * Pagina a cui la command fa riferimento per mostrare la form di aggiunta di un processo a un rischio
     */
    private static final String nomeFileAddProcess = "/jsp/riProcessoForm.jsp";
    /**
     * Pagina a cui la command fa riferimento per mostrare la form di aggiunta di un rischio a un processo
     */
    private static final String nomeFileAddRisk = "/jsp/riRischioProcessoForm.jsp";
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutte le pagine gestite da questa Command
     */    
    private static final HashMap<String, String> nomeFile = new HashMap<>();

    
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
        nomeFile.put(COMMAND_RISK,              nomeFileElenco);
        nomeFile.put(PART_INSERT_RISK,          nomeFileInsertRisk);
        nomeFile.put(PART_INSERT_RISK_PROCESS,  nomeFileAddProcess);
        nomeFile.put(PART_INSERT_PROCESS_RISK,  nomeFileAddRisk);
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
        /* -------------------------------------------------------------------- *
         *              Dichiara e inizializza variabili locali                 *
         * -------------------------------------------------------------------- */
        // Databound
        DBWrapper db = null;
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Utente loggato
        PersonBean user = null;
        // Rilevazione
        CodeBean survey = null;
        // Processo organizzativo specifico
        ProcessBean process = null;
        // Rischio corruttivo specifico
        RiskBean risk = null;
        // Elenco dei rischi corruttivi legati alla rilevazione
        ArrayList<RiskBean> risks = null;
        // Elenco processi collegati alla rilevazione
        ArrayList<ProcessBean> macros = null;
        // Tabella che conterrà i valori dei parametri passati dalle form
        HashMap<String, LinkedHashMap<String, String>> params = null;
        // Titolo pagina
        String tP = null;
        // Predispone le BreadCrumbs personalizzate per la Command corrente
        LinkedList<ItemBean> bC = null;
        // Variabile contenente l'indirizzo per la redirect da una chiamata POST a una chiamata GET
        String redirect = null;
        /* -------------------------------------------------------------------- *
         *                    Recupera parametri e attributi                    *
         * -------------------------------------------------------------------- */
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);        
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter(PARAM_SURVEY, DASH);
        // Recupera o inizializza 'tipo pagina'   
        String part = parser.getStringParameter("p", DASH);
        // Flag di scrittura
        Boolean writeAsObject = (Boolean) req.getAttribute("w");
        boolean write = writeAsObject.booleanValue();
        // Recupera o inizializza 'id rischio'
        int idRk = parser.getIntParameter("idR", DEFAULT_ID);
        // Recupera o inizializza 'id processo'
        int idP = parser.getIntParameter("pliv", DEFAULT_ID);
        /* -------------------------------------------------------------------- *
         *      Instanzia nuova classe DBWrapper per il recupero dei dati       *
         * -------------------------------------------------------------------- */
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new CommandException(FOR_NAME + "Non e\' disponibile un collegamento al database\n." + wse.getMessage(), wse);
        }
        /* -------------------------------------------------------------------- *
         *         Previene il rischio di attacchi di tipo Garden Gate          *
         * -------------------------------------------------------------------- */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            user = SessionManager.checkSession(req.getSession(IF_EXISTS_DONOT_CREATE_NEW));
        } catch (RuntimeException re) {
            throw new CommandException(FOR_NAME + "Problema a livello dell\'autenticazione utente!\n" + re.getMessage(), re);
        }
        /* ******************************************************************** *
         *                           Corpo del metodo                           *
         * ******************************************************************** */
        try {
            // Input check
            if (!codeSur.equals(DASH)) {
                // The survey code is valid, retrieve the whole survey object
                survey =  ConfigManager.getSurvey(codeSur);
                // Map of parameters passed by the forms
                params = new HashMap<>();
                // Load the forms actual values
                loadParams(part, parser, params);
                /* ======================= @PostMapping ======================= */
                if (write) {
                    // Check the action
                    if (nomeFile.containsKey(part)) {
                        // Switch with separate scopes
                        switch (part.toLowerCase()) {
                            /*                  INSERT new Risk                 */
                            case PART_INSERT_RISK: {
                                // Inserisce nel DB nuovo rischio corruttivo definito dall'utente
                                db.insertRisk(user, params);
                                // Prepara la redirect 
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_RISK + 
                                           AMPERSAND + 
                                           PARAM_SURVEY + EQ + codeSur;
                                break;
                            }
                            /* INSERT new relationship between Risk and Process */
                            case PART_INSERT_RISK_PROCESS: {
                                // Check if there already is the relationship
                                int check = db.getRiskProcess(user, params);
                                // Duplicate key value violates unique constraint 
                                if (check > NOTHING) {
                                    // Generate an error
                                    redirect = ConfigManager.getEntToken() + EQ + COMMAND_RISK + 
                                               AMPERSAND + "p" + EQ + PART_INSERT_RISK_PROCESS +
                                               AMPERSAND + "idR" + EQ + parser.getStringParameter("r-id", VOID_STRING) + 
                                               AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                               AMPERSAND + MESSAGE + EQ + "dupKey";
                                } else {
                                    // Insert the relationship in db table
                                    db.insertRiskProcess(user, params);
                                    // Redirect 
                                    redirect = ConfigManager.getEntToken() + EQ + COMMAND_RISK + 
                                               AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                               AMPERSAND + MESSAGE + EQ + "newRel";
                                }
                                break;
                            }
                            /* INSERT new relationship between Process and Risk */
                            // Contains the same actions of the case PART_INSERT_RISK_PROCESS: (different only for redirects)
                            case PART_INSERT_PROCESS_RISK: {
                                // Check if there already is the relationship
                                int check = db.getRiskProcess(user, params);
                                // Duplicate key value violates unique constraint 
                                if (check > NOTHING) {
                                    // Generate an error
                                    redirect = ConfigManager.getEntToken() + EQ + COMMAND_RISK + 
                                               AMPERSAND + "p" + EQ + part +
                                               AMPERSAND + "liv" + EQ + parser.getStringParameter("liv", VOID_STRING) + 
                                               AMPERSAND + "pliv" + EQ + idP + 
                                               AMPERSAND + "pliv1" + EQ + parser.getStringParameter("pliv1", VOID_STRING) +
                                               AMPERSAND + "pliv0" + EQ + parser.getStringParameter("pliv0", VOID_STRING) +
                                               AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                               AMPERSAND + MESSAGE + EQ + "dupKey";
                                } else {
                                    // Insert the relationship in db table
                                    db.insertRiskProcess(user, params);
                                    // Redirect 
                                    redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS + 
                                               AMPERSAND + "p" + EQ + PART_PROCESS +
                                               AMPERSAND + "pliv" + EQ + idP +
                                               AMPERSAND + "liv" + EQ + parser.getStringParameter("liv", VOID_STRING) + 
                                               AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                               AMPERSAND + MESSAGE + EQ + "newRel#rischi-fattori-misure";
                                }
                                break;
                            }
                            default:
                            // Azione di default
                            // do delete?
                        }
                    }
                /* ======================== @GetMapping ======================= */
                } else {
                    /* ------------------------------------------------ *
                     *                  Manage Risk Part                *
                     * ------------------------------------------------ */
                    if (nomeFile.containsKey(part)) {
                        // Recupera le breadcrumbs
                        LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                        fileJspT = nomeFile.get(part);
                        switch (part.toLowerCase()) {
                            /* -----     SHOW Form to INSERT new Risk     ----- */
                            case PART_INSERT_RISK: {
                                // Customize labels
                                tP = "Nuovo Rischio";
                                bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_1, tP);
                                break;
                            }
                            /* ----  SHOW Form to link a Process to a Risk ---- */
                            case PART_INSERT_RISK_PROCESS: {
                                // Retrieve the whole risk which a process has to be assigned
                                risk = db.getRisk(user, idRk, survey);
                                // Retrieve all the processes
                                macros = ProcessCommand.retrieveMacroAtBySurvey(user, codeSur, db);
                                // Customize labels
                                tP =  "Collegamento Processo";
                                bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_1, tP);
                                break;
                            }
                            /* ----  SHOW Form to link a Risk to a Process ---- */
                             case PART_INSERT_PROCESS_RISK: {
                                // Retrieve the process which the risk has to be assigned
                                process = db.getProcessById(user, idP, survey);
                                // Retrieve its already assigned risks...
                                ArrayList<RiskBean> risksByProcess = db.getRisksByProcess(user, process, survey);
                                // ...and set them into the retrieved process
                                process.setRischi(risksByProcess);
                                // Retrieve all the risks
                                risks = db.getRisks(user, survey.getId(), survey);
                                // Customize labels
                                tP = "Collegamento Rischio";
                                bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_1, tP);
                                break;
                             }
                        }
                    } else {
                        /* ------------------------------------------------ *
                         *          SELECT and SHOW a Specific Risk         *
                         * ------------------------------------------------ */
                        if (idRk > DEFAULT_ID) {
                            risk = db.getRisk(user, idRk, survey);
                            // Customize labels
                            tP = "Rischio";
                            bC = HomePageCommand.makeBreadCrumbs(ConfigManager.getAppName(), req.getQueryString(), tP);
                            // Page
                            fileJspT = nomeFileDettaglio;
                        } else {
                            /* ------------------------------------------------ *
                             *      SELECT and SHOW the full List of Risks      *
                             * ------------------------------------------------ */
                            risks = db.getRisks(user, survey.getId(), survey);
                            tP = "Registro dei rischi";
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
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
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
        // Imposta nella request elenco completo rischi corruttivi
        if (risks != null) {
            req.setAttribute("rischi", risks);
        }
        // Imposta nella request elenco completo processi
        if (macros != null) {
            req.setAttribute("processi", macros);
        }
        // Imposta nella request oggetto rischio corruttivo specifico
        if (risk != null) {
            req.setAttribute("rischio", risk);
        }
        // Imposta nella request oggetto processo specifico
        if (process != null) {
            req.setAttribute("processo", process);
        }
        // Imposta struttura contenente tutti i parametri di navigazione già estratti
        if (!params.isEmpty()) {
            req.setAttribute("params", params);
        }
        // Imposta l'eventuale indirizzo a cui redirigere
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
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
        LinkedHashMap<String, String> survey = new LinkedHashMap<>();
        LinkedHashMap<String, String> risk = new LinkedHashMap<>();
        /* ---------------------------------------------------- *
         *     Caricamento parametro di Codice Rilevazione      *
         * ---------------------------------------------------- */      
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter("r", DASH);
        // Recupera l'oggetto rilevazione a partire dal suo codice
        CodeBean surveyAsBean = ConfigManager.getSurvey(codeSur);        
        // Inserisce l'ìd della rilevazione come valore del parametro
        survey.put(PARAM_SURVEY, String.valueOf(surveyAsBean.getId()));
        // Oltre all'ID passa anche il codice (puo' essere utile)
        survey.put("code", codeSur);
        // Aggiunge data e ora, se le trova
        survey.put("d", parser.getStringParameter("d", VOID_STRING));
        survey.put("t", parser.getStringParameter("t", VOID_STRING));
        // Aggiunge il tutto al dizionario dei parametri
        formParams.put(PARAM_SURVEY, survey);
        /* ---------------------------------------------------- *
         *     Caricamento parametri di Scelta Struttura        *
         * ---------------------------------------------------- */        
        struct.put("liv1",  parser.getStringParameter("sliv1", VOID_STRING));
        struct.put("liv2",  parser.getStringParameter("sliv2", VOID_STRING));
        struct.put("liv3",  parser.getStringParameter("sliv3", VOID_STRING));
        struct.put("liv4",  parser.getStringParameter("sliv4", VOID_STRING));
        formParams.put(PART_SELECT_STR, struct);
        /* ---------------------------------------------------- *
         *      Caricamento parametri di Scelta Processo        *
         * ---------------------------------------------------- */
        proat.put("liv1",    parser.getStringParameter("pliv1", VOID_STRING));
        proat.put("liv2",    parser.getStringParameter("pliv2", VOID_STRING));
        proat.put("liv3",    parser.getStringParameter("pliv3", VOID_STRING));
        /* ---------------------------------------------------- *
         *     Caricamento parametri di Inserimento Rischio     *
         * ---------------------------------------------------- */
        if (part.equals(PART_INSERT_RISK)) {
            // Recupera gli estremi del rischio da inserire
            risk.put("risk", parser.getStringParameter("r-name", VOID_STRING));
            risk.put("desc", parser.getStringParameter("r-descr", VOID_STRING));
            formParams.put(part, risk);
        /* ---------------------------------------------------- *
         *  Caricamento parametri Associazione Rischio-Processo *
         * ---------------------------------------------------- */
        } else if (part.equals(PART_INSERT_RISK_PROCESS) || part.equals(PART_INSERT_PROCESS_RISK)) {
            // Recupera l'id del rischio cui associare il processo (proat)
            risk.put("risk", parser.getStringParameter("r-id", VOID_STRING));
            formParams.put(PART_INSERT_RISK_PROCESS, risk);
        /* ---------------------------------------------------- *
         *  Caricamento parametri Associazione Processo-Rischio *
         * ---------------------------------------------------- *
        } else if (part.equals(PART_INSERT_PROCESS_RISK)) {
            // Recupera l'id del rischio da associare al processo
            risk.put("risk", parser.getStringParameter("r-id", VOID_STRING));
            formParams.put(part, risk);*/
        /* ---------------------------------------------------- *
         *     Caricamento parametri Fattore-Processo-Rischio   *
         * ---------------------------------------------------- */
        } else if (part.equals(PART_INSERT_F_R_P)) {
            // Recupera gli estremi del rischio da inserire
            risk.put("risk", parser.getStringParameter("r-id", VOID_STRING));
            risk.put("fact", parser.getStringParameter("fliv1", VOID_STRING));
            risk.put("proc", parser.getStringParameter("pliv", VOID_STRING));
            formParams.put(part, risk);
        /* ---------------------------------------------------- *
         *    Caricamento parametri Aggiornamento Nota al PxI   *
         * ---------------------------------------------------- */
        } else if (part.equals(PART_PI_NOTE)) {
            // Recupera gli estremi della nota da aggiornare
            //String note = parser.getStringParameter("pi-note", VOID_STRING);
            risk.put("note", parser.getStringParameter("pi-note", VOID_STRING));
            formParams.put(part, risk);
        /* ---------------------------------------------------- *
         *      Parametri inserimento nuovo Macro/Processo      *
         * ---------------------------------------------------- */
        } else if (part.equals(PART_INSERT_PROCESS)) {
            // Il nome del Macro o Processo è stato già recuperato sopra
            proat.put("liv0", parser.getStringParameter("pliv0", VOID_STRING));
        }
        formParams.put(PART_PROCESS, proat);
    }
    
}