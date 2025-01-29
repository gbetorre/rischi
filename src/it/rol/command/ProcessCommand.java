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

package it.rol.command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.ParameterParser;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.DBWrapper;
import it.rol.Data;
import it.rol.Main;
import it.rol.Query;
import it.rol.SessionManager;
import it.rol.Utils;
import it.rol.bean.ActivityBean;
import it.rol.bean.CodeBean;
import it.rol.bean.DepartmentBean;
import it.rol.bean.InterviewBean;
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
import it.rol.bean.QuestionBean;
import it.rol.bean.RiskBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.CommandException;
import it.rol.exception.WebStorageException;


/**
 * <p><code>ProcessCommand.java</code><br />
 * Implementa la logica per la gestione dei processi censiti dall'anticorruzione
 * ai fini della valutazione del rischio corruttivo, on line (ROL).</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class ProcessCommand extends ItemBean implements Command, Constants {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = 4356663206440110257L;
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
     * Pagina a cui la command reindirizza per mostrare i macroprocessi
     */
    private static final String nomeFileElenco = "/jsp/prElenco.jsp";
    /**
     * Pagina per mostrare il dettaglio di un processo
     */
    private static final String nomeFileDettaglio = "/jsp/prProcessoAjax.jsp";
    /**
     * Pagina per mostrare l'elenco degli output
     */
    private static final String nomeFileOutputs = "/jsp/prOutputs.jsp";
    /**
     * Pagina per mostrare il dettaglio di un output
     */
    private static final String nomeFileOutput = "/jsp/prOutput.jsp";
    /**
     * Pagina per mostrare elenco dei fattori abilitanti
     */
    private static final String nomeFileFattori = "/jsp/prFattori.jsp";
    /**
     * Pagina a cui la command inoltra per mostrare la form di aggiunta di un 
     * fattore abilitante a un rischio entro il contesto del processo corrente
     */
    private static final String nomeFileAddFactor = "/jsp/prFattoreForm.jsp";
    /**
     * Pagina a cui la command inoltra per mostrare la form di aggiunta/modifica 
     * di una nota (giudizio sintetico) ad un indice PxI
     */
    private static final String nomeFileNote = "/jsp/prNotaForm.jsp";
    /** 
     * Pagina di scelta del tipo di nuovo elemento da aggiungere
     * (Macroprocesso  | Processo | Sottoprocesso)
     */
    private static final String nomeFileSceltaTipo = "/jsp/prTipoForm.jsp";
    /** 
     * Pagina contenente la form per inserimento macro/processo
     * (Macroprocesso  | Processo | Sottoprocesso)
     */
    private static final String nomeFileAddProcess = "/jsp/prProcessoForm.jsp";
    /**
     * Nome del file json della Command (dipende dalla pagina di default)
     */
    private String nomeFileJson = nomeFileElenco.substring(nomeFileElenco.lastIndexOf(SLASH), nomeFileElenco.indexOf(DOT));
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutte le pagine gestite da questa Command
     */    
    private static final HashMap<String, String> nomeFile = new HashMap<>();
    /**
     * Struttura a cui la command fa riferimento per generare i titoli pagina
     */    
    private static final HashMap<String, String> titleFile = new HashMap<>();


    /**
     * Crea una nuova istanza di ProcessCommand
     */
    public ProcessCommand() {
        /*;*/   // It doesn't anything
    }


    /**
     * <p>Raccoglie i valori dell'oggetto ItemBean
     * e li passa a questa classe command.</p>
	 *
	 * @param voceMenu la VoceMenuBean pari alla Command presente.
	 * @throws it.rol.exception.CommandException se l'attributo paginaJsp di questa command non e' stato valorizzato.
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
        nomeFile.put(PART_PROCESS,          nomeFileDettaglio);
        nomeFile.put(PART_OUTPUT,           nomeFileOutputs);
        nomeFile.put(PART_FACTORS,          nomeFileFattori);
        nomeFile.put(PART_INSERT_F_R_P,     nomeFileAddFactor);
        nomeFile.put(PART_PI_NOTE,          nomeFileNote);
        nomeFile.put(PART_INSERT_PROCESS,   nomeFileSceltaTipo);
        // Carica la hashmap contenente i titoli pagina
        titleFile.put(new Byte(ELEMENT_LEV_1).toString(),  "Nuovo Macroprocesso");
        titleFile.put(new Byte(ELEMENT_LEV_2).toString(),  "Nuovo Processo");
        titleFile.put(new Byte(ELEMENT_LEV_3).toString(),  "Nuovo Sottoprocesso");
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
         *           Crea e inizializza le variabili locali comuni              *
         * ******************************************************************** */
        // Databound
        DBWrapper db = null;
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        // Utente loggato
        PersonBean user = null;
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter("r", DASH);
        // Recupera o inizializza 'tipo pagina'
        String part = parser.getStringParameter("p", DASH);
        // Recupera o inizializza eventuale parametro referral
        String ref = parser.getStringParameter("ref", DASH);
        // Recupera o inizializza 'id processo'
        int idP = parser.getIntParameter("pliv", DEFAULT_ID);
        // Recupera o inizializza livello di granularità del processo anticorruttivo
        int liv = parser.getIntParameter("liv", DEFAULT_ID);
        // Recupera o inizializza 'id output'
        int idO = parser.getIntParameter("idO", DEFAULT_ID);
        // Recupera o inizializza eventuale 'id rischio'
        int idR = parser.getIntParameter("idR", DEFAULT_ID);
        // Recupera o inizializza eventuale 'id macroprocesso'
        String idM = parser.getStringParameter("pliv1", DASH);
        // Recupera l'oggetto rilevazione a partire dal suo codice
        CodeBean survey = ConfigManager.getSurvey(codeSur);
        // Prepara un oggetto contenente i parametri opzionali per i nodi
        ItemBean options = new ItemBean("#009966",  VOID_STRING,  VOID_STRING, VOID_STRING, PRO_PFX, NOTHING);
        // Prepara un output di rischio corruttivo
        ProcessBean output = null;
        // Prepara un rischio corruttivo cui è esposto un processo
        RiskBean risk = null;
        // Macroprocesso cui si vuole aggiungere un figlio
        ProcessBean macro = null;
        // Dichiara elenco di output
        AbstractList<ProcessBean> outputs = new ArrayList<>();
        // Dichiara elenco di fattori abilitanti
        AbstractList<CodeBean> factors = new ArrayList<>();
        // Dichiara elenco di aree di rischio
        AbstractList<ProcessBean> aree = new ArrayList<>();
        // Dichiara elenco di macroprocessi
        AbstractList<ProcessBean> macros = new ArrayList<>();
        // Dichiara generico elenco di elementi afferenti a un processo
        ConcurrentHashMap<String, ArrayList<?>> processElements = null;
   
        LinkedHashMap<ProcessBean, ArrayList<ProcessBean>> processIndexed = null;
        // Predispone le BreadCrumbs personalizzate per la Command corrente
        LinkedList<ItemBean> bC = null;
        // Tabella che conterrà i valori dei parametri passati dalle form
        HashMap<String, LinkedHashMap<String, String>> params = null;
        // Tabella degli indicatori con i valori elaborati relativi a un processo
        HashMap<String, InterviewBean> indicators = null;
        // Eventuale indicatore pxi di uno specifico processo
        ItemBean pxi = null;
        // Flag di scrittura
        Boolean writeAsObject = (Boolean) req.getAttribute("w");
        // Explicit unboxing
        boolean write = writeAsObject.booleanValue();
        // Titolo pagina
        String tP = null;
        // Variabile contenente l'indirizzo per la redirect da una chiamata POST a una chiamata GET
        String redirect = null;
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        /* ******************************************************************** *
         *      Instanzia nuova classe WebStorage per il recupero dei dati      *
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
            // Il parametro di navigazione 'rilevazione' è obbligatorio
            if (!codeSur.equals(DASH)) {
                // Creazione della tabella che conterrà i valori dei parametri passati dalle form
                params = new HashMap<>();
                // Carica in ogni caso i parametri di navigazione
                RiskCommand.loadParams(part, parser, params);
                /* ======================= @PostMapping ======================= */
                if (write) {
                    // Controlla quale azione vuole fare l'utente
                    if (nomeFile.containsKey(part)) {
                        if (part.equalsIgnoreCase(PART_INSERT_F_R_P)) {
                            /* ------------------------------------------------ *
                             * INSERT new relation between Risk Process Factor  *
                             * ------------------------------------------------ */
                            // Controlla che non sia già presente l'associazione 
                            int check = db.getFactorRiskProcess(user, params);
                            if (check > NOTHING) {  // Genera un errore
                                // Duplicate key value violates unique constraint 
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS + 
                                           AMPERSAND + "p" + EQ + PART_INSERT_F_R_P +
                                           AMPERSAND + "idR" + EQ + parser.getStringParameter("r-id", VOID_STRING) + 
                                           AMPERSAND + "pliv" + EQ + parser.getStringParameter("pliv2", VOID_STRING) + 
                                           AMPERSAND + "liv" + EQ + "2" +
                                           AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                           AMPERSAND + MESSAGE + EQ + "dupKey";
                            } else {
                                // Inserisce nel DB nuova associazione pxrxf
                                db.insertFactorRiskProcess(user, params);
                                // Prepara la redirect 
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS + 
                                           AMPERSAND + "p" + EQ + PART_PROCESS +
                                           AMPERSAND + "pliv" + EQ + parser.getStringParameter("pliv2", VOID_STRING) + 
                                           AMPERSAND + "liv" + EQ + "2" +
                                           AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                           AMPERSAND + MESSAGE + EQ + "newRel#rischi";
                            }
                        } else if (part.equalsIgnoreCase(PART_PI_NOTE)) {
                            /* ------------------------------------------------ *
                             *                UPDATE a note to PxI              *
                             * ------------------------------------------------ */
                            // Aggiorna la nota
                            db.updateNote(user, params);
                            // Prepara la redirect 
                            if (ref.equalsIgnoreCase(PART_PROCESS)) {
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS + 
                                           AMPERSAND + "p" + EQ + PART_PROCESS +
                                           AMPERSAND + "pliv" + EQ + parser.getStringParameter("pliv2", VOID_STRING) + 
                                           AMPERSAND + "liv" + EQ + "2" +
                                           AMPERSAND + PARAM_SURVEY + EQ + codeSur;     
                            } else if (ref.equalsIgnoreCase(PART_SELECT_STR)) {
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_REPORT + 
                                           AMPERSAND + "p" + EQ + PART_SELECT_STR +
                                           AMPERSAND + PARAM_SURVEY + EQ + codeSur;  
                            }
                        } else if (part.equalsIgnoreCase(PART_INSERT_PROCESS)) {
                            /* ------------------------------------------------ *
                             *         INPUT new process - STEP 1: type         *
                             * ------------------------------------------------ */
                            // Deve differenziare tra finire e continuare
                            String action = parser.getStringParameter("action", DASH);
                            
                            String idCodeArea = parser.getStringParameter("p-area", DASH);
                                
                            switch (action) {
                                case "save":
                                    // Al momento i sottoprocessi non sono gestiti
                                    if (liv == ELEMENT_LEV_1)
                                        db.insertMacroAt(user, params);
                                    else 
                                        db.insertMacroAt(user, params);
                                    redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS +
                                               AMPERSAND + PARAM_SURVEY + EQ + codeSur;
                                    break;
                                case "cont":
                                    switch (liv) {
                                        case ELEMENT_LEV_1:
                                            int nextId = db.insertMacroAt(user, params);
                                            redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS + 
                                                       AMPERSAND + "p" + EQ + PART_INSERT_PROCESS +
                                                       AMPERSAND + "liv" + EQ + ELEMENT_LEV_2 +
                                                       AMPERSAND + "pliv1" + EQ + nextId + 
                                                       AMPERSAND + "pliv0" + EQ + idCodeArea + 
                                                       AMPERSAND + PARAM_SURVEY + EQ + codeSur;
                                            break;
                                        case ELEMENT_LEV_2:
                                            redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS + 
                                                       AMPERSAND + "p" + EQ + PART_INSERT_PROCESS +
                                                       AMPERSAND + "pliv" + EQ + parser.getStringParameter("pliv2", VOID_STRING) + 
                                                       AMPERSAND + "liv" + EQ + ELEMENT_LEV_2 +
                                                       AMPERSAND + PARAM_SURVEY + EQ + codeSur;
                                            break;
                                        default:
                                            System.out.println("Unknown action");
                                            break;
                                    }
                                    break;
                                default:
                                    redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS + 
                                               AMPERSAND + "p" + EQ + PART_INSERT_PROCESS +
                                               AMPERSAND + "liv" + EQ + liv +
                                               AMPERSAND + PARAM_SURVEY + EQ + codeSur;
                                    break;
                                    
                            }

                            // Prepara la redirect 
/*
*/  
                        }
                    }
                /* ======================== @GetMapping ======================= */
                } else {
                    // Il parametro di navigazione 'p' permette di addentrarsi nelle funzioni
                    if (nomeFile.containsKey(part)) {
                        // Definisce un  default per la pagina jsp
                        fileJspT = nomeFile.get(part);
                        // Viene richiesta la visualizzazione del dettaglio di un processo
                        if (part.equalsIgnoreCase(PART_PROCESS)) {
                            /* ------------------------------------------------ *
                             *                  SELECT Process                  *
                             * ------------------------------------------------ */
                            // Istanzia generica tabella in cui devono essere settate le liste di items afferenti al processo
                            processElements = new ConcurrentHashMap<>();
                            // Valorizza tali liste necessarie a visualizzare  i dettagli di un processo, restituendo gli indicatori corredati con le note al PxI
                            indicators = retrieveProcess(user, idP, liv, processElements, codeSur, db);
                            // Ha bisogno di personalizzare le breadcrumbs
                            LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                            bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_4, "Processo");
                        } else if (part.equalsIgnoreCase(PART_OUTPUT)) {
                            /* ------------------------------------------------ *
                             *                  SELECT Output                   *
                             * ------------------------------------------------ */
                            if (idO > DEFAULT_ID) {
                                // Recupera un output specifico
                                output = db.getOutput(user, idO, survey);
                               /* Sovrascrive la jsp con un valore ad hoc!
                                * This approach utilizes string literals instead using constructor String() with the new operator.
                                * String literals are stored in the string pool. 
                                * This method is more memory-efficient because if the string fileJspT already exists in the pool, 
                                * it will reuse that reference instead of creating a new object.
                                * Both methods acknowledge that strings in Java are immutable. 
                                * However, using new String() does not change this fact; it merely creates 
                                * a new instance rather than modifying the existing one.  
                                * The first method is generally faster because it can take advantage of the string pool and avoids unnecessary object creation.
                                * The second method incurs overhead from creating a new object and is slower due to this additional allocation.
                                * The second approach (fileJspT = new String(nomeFileOutput)) explicitly creates a new String object in the heap memory, 
                                * which is less efficient as it always creates a new instance, regardless of whether an equivalent string already exists.
                                * Using string literals allows Java to optimize memory usage through the string pool, 
                                * making code cleaner and more efficient. In summary, always prefer reassigning with string literals over creating new instances 
                                * unless having a specific reason to use new String(). */
                                fileJspT = nomeFileOutput;
                            } else {
                                // Deve recuperare l'elenco degli output
                                outputs = db.getOutputs(user, survey);
                            }
                        } else if (part.equalsIgnoreCase(PART_FACTORS)) {
                            /* ------------------------------------------------ *
                             *                  SELECT Factors                  *
                             * ------------------------------------------------ */
                            // Deve recuperare l'elenco dei fattori abilitanti
                            factors = db.getFactors(user, survey);
                            // Ha bisogno di personalizzare le breadcrumbs
                            LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                            bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_2, "Fattori abilitanti");
                        } else if (part.equalsIgnoreCase(PART_INSERT_F_R_P)) {
                            /* ------------------------------------------------ *
                             *  SHOWS Form to LINK Factor TO Risk INTO Process  *
                             * ------------------------------------------------ */
                            // Deve recuperare l'elenco completo dei fattori abilitanti
                            factors = db.getFactors(user, survey);
                            // Prepara un ProcessBean di cui recuperare tutti i rischi
                            output = db.getProcessById(user, idP, survey);
                            // Recupera tutti i rischi del processo; tra questi ci sarà quello cui si vuole aggiungere il fattore
                            ArrayList<RiskBean> risks = db.getRisksByProcess(user, output, survey);
                            // Individua il rischio specifico a cui si vuole aggiungere il fattore abilitante
                            risk = decant(risks, idR);
                            // Ha bisogno di personalizzare le breadcrumbs
                            LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                            bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_2, "Nuovo legame P-R-F");
                        } else if (part.equalsIgnoreCase(PART_PI_NOTE)) {
                            /* ------------------------------------------------ *
                             * SHOWS Form to INSERT or UPDATE a note about PxI  *
                             * ------------------------------------------------ */
                            // Deve recuperare e mostrare la nota al giudizio sintetico PxI
                            pxi = db.getIndicatorPI(user, idP, survey);
                            // Ha bisogno di personalizzare le breadcrumbs
                            LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                            bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_2, "Nota");
                        } else if (part.equalsIgnoreCase(PART_INSERT_PROCESS)) {
                            /* ------------------------------------------------ *
                             *                  SHOWS a Form                    *
                             * ------------------------------------------------ */
                            if (liv > DEFAULT_ID) {
                                aree = db.getAree(user, survey);
                                processIndexed = decant((ArrayList<ProcessBean>) aree);
                                //macros = retrieveMacroAtBySurvey(user, codeSur, db);
                                // Se c'è un id macroprocesso vuol dire che bisogna aggiungere un processo
                                if (!idM.equals(DASH)) {
                                    int idMat = Integer.parseInt(idM);
                                    String areaRischio = parser.getStringParameter("pliv0", DASH);
                                    macro = db.getMacroSubProcessAtById(user, idMat, ELEMENT_LEV_1, survey);
                                    macro.setAreaRischio(areaRischio);
                                }
                                // Titolo pagina
                                tP = titleFile.get(String.valueOf(liv));
                                // Form to insert data of the process
                                fileJspT = nomeFileAddProcess;
                            }
                            // Ha bisogno di personalizzare le breadcrumbs
                            LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                            bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_2, "Nuovo Elemento");
                        }
                    } else {
                        // Viene richiesta la visualizzazione di un elenco di macroprocessi
                        ArrayList<ProcessBean> macrosat = retrieveMacroAtBySurvey(user, codeSur, db);
                        // Genera il file json contenente le informazioni strutturate
                        printJson(req, macrosat, nomeFileJson, options);
                        // Imposta la jsp
                        fileJspT = nomeFileElenco;
                    }
                }
            } else {    // Manca il codice rilevazione!!
                String msg = FOR_NAME + "Impossibile recuperare il codice della rilevazione.\n";
                LOG.severe(msg + "Qualcuno ha probabilmente alterato il codice rilevazione nell\'URL della pagina.\n");
                throw new CommandException(msg);
            }
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Si e\' verificato un problema nella gestione di una richiesta relativa ai macroprocessi.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ce.getMessage(), ce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
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
        // Imposta in request elenco completo input di processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[0]).isEmpty()) {
            req.setAttribute("listaInput", processElements.get(TIPI_LISTE[0]));
        }
        // Imposta in request elenco completo fasi di processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[1]).isEmpty()) {
            req.setAttribute("listaFasi", processElements.get(TIPI_LISTE[1]));
        }
        // Imposta in request elenco completo output di processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[2]).isEmpty()) {
            req.setAttribute("listaOutput", processElements.get(TIPI_LISTE[2]));
        }
        // Imposta in request elenco completo rischi di processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[3]).isEmpty()) {
            req.setAttribute("listaRischi", processElements.get(TIPI_LISTE[3]));
        }
        // Imposta in request elenco completo interviste che hanno sondato il processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[4]).isEmpty()) {
            req.setAttribute("listaInterviste", processElements.get(TIPI_LISTE[4]));
        }
        // Imposta in request elenco indicatori di rischio con i valori raffinati
        if (indicators != null) {
            req.setAttribute("listaIndicatori", indicators);
        }
        // Imposta in request elenco macroprocessi indicizzati per area di rischio
        if (processIndexed != null) {
            req.setAttribute("listaAree", processIndexed);
        }
        // Imposta in request elenco completo degli output
        if (outputs != null) {
            req.setAttribute("outputs", outputs);
        }
        // Imposta in request output di processo anticorruttivo
        if (output != null) {
            req.setAttribute("output", output);
        }
        // Imposta in request elenco completo dei fattori abilitanti
        if (factors != null) {
            req.setAttribute("fattori", factors);
        }
        // Imposta in request elenco completo delle aree di rischio
        if (aree != null) {
            req.setAttribute("aree", aree);
        }
        // Imposta in request specifico rischio cui aggiungere fattore abilitante
        if (risk != null) {
            req.setAttribute("rischio", risk);
        }
        // Imposta in request specifico macroprocesso cui aggiungere un processo
        if (macro != null) {
            req.setAttribute("mat", macro);
        }
        // Imposta in request specifico pxi di uno specifico processo at
        if (pxi != null) {
            req.setAttribute("pxi", pxi);
        }
        // Imposta l'eventuale indirizzo a cui redirigere
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }
        // Titolo pagina in caso sia significativo
        if (tP != null && !tP.equals(VOID_STRING)) {
            req.setAttribute("tP", tP);
        }
        // Imposta in request le breadcrumbs in caso siano state personalizzate
        if (bC != null) {
            req.removeAttribute("breadCrumbs");
            req.setAttribute("breadCrumbs", bC);
        }
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }
    
    
    /* **************************************************************** *
     *                  Metodi di recupero dei dati                     *                     
     *                            (retrieve)                            *
     * **************************************************************** */
    
    /**
     * <p>Restituisce un ArrayList (albero, vista gerarchica) 
     * di tutti macroprocessi censiti dall'anticorruzione 
     * trovati in base a una rilevazione il cui identificativo 
     * viene accettato come argomento; ogni macroprocesso contiene 
     * internamente i suoi processi e questi gli eventuali sottoprocessi.</p>
     *
     * @param codeSurvey    il codice della rilevazione
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - lista di macroprocessi recuperati
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<ProcessBean> retrieveMacroAtBySurvey(PersonBean user,
                                                                 String codeSurvey,
                                                                 DBWrapper db)
                                                          throws CommandException {
        ArrayList<ProcessBean> macro = null;
        try {
            // Estrae i macroprocessi anticorruttivi in una data rilevazione
            macro = db.getMacroAtBySurvey(user, codeSurvey);
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero dei macro/processi in base alla rilevazione.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return macro;
    }
    
    
    /**
     * <p>Restituisce un ArrayList (albero, vista gerarchica) 
     * di tutti processi figli di macroprocessi censiti dall'anticorruzione 
     * nel contesto di una data rilevazione il cui identificativo 
     * viene accettato come argomento; ogni processo contiene 
     * internamente gli estremi del padre, dei figli e degli indicatori
     * di rischio, se &egrave; stato oggetto di indagine (intervista).</p>
     *
     * @param codeSurvey    il codice della rilevazione
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - lista di macroprocessi recuperati
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<ProcessBean> retrieveProcessAtBySurvey(PersonBean user,
                                                                   String codeSurvey,
                                                                   DBWrapper db)
                                                            throws CommandException {
        AbstractList<ProcessBean> mats = null;
        AbstractList<ProcessBean> pats = new ArrayList<>();
        try {
            // Estrae i macroprocessi anticorruttivi in una data rilevazione
            mats = db.getMacroAtBySurvey(user, codeSurvey);
            // Destruttura l'albero (il ramo diventa tronco)
            for (ProcessBean mat : mats) {
                Vector<ProcessBean> children = (Vector<ProcessBean>) mat.getProcessi();
                for (ProcessBean pat : children) {
                    // Controllare se serve aggiungere il padre, o sta già "in pancia" del figlio
                    // TODO CHECK
                    // Calcola i valori degli indicatori di rischio per il processo corrente
                    pats.add(pat);
                }

                
            }
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero dei macro/processi in base alla rilevazione.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return (ArrayList<ProcessBean>) pats;
    }

    
    /**
     * <p>Interfaccia alternativa (semplificata) del metodo che restituisce 
     * di tutti processi figli di macroprocessi censiti dall'anticorruzione 
     * nel contesto di una data rilevazione senza necessit&agrave; 
     * che il chiamante passi un'istanza di WebStorage.
     * Aggiunge anche gli indicatori (recuperati da cache) ad ogni processo recuperato.</p>
     *
     * @param codeSurvey    il codice della rilevazione
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - lista di macroprocessi recuperati
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    private static ArrayList<ProcessBean> retrieveProcessAtBySurvey(PersonBean user,
                                                                    String codeSurvey)
                                                             throws CommandException {
        ArrayList<ProcessBean> patsWithoutIndicators = null;
        ArrayList<ProcessBean> patsWithIndicators = new ArrayList<>();
        try {
            // Crea una istanza di WebStorage
            DBWrapper db = new DBWrapper();
            // Usa il metodo apposito
            patsWithoutIndicators = retrieveProcessAtBySurvey(user, codeSurvey, db);
            // Cicla sui processi
            for (ProcessBean pat : patsWithoutIndicators) {
                // Recupera le interviste in cui il processo è stato esaminato
                ArrayList<InterviewBean> listaInterviste = retrieveInterviews(user, pat.getId(), ELEMENT_LEV_2, codeSurvey, db);
                // Recupera gli indicatori corretti (calcolati a runtime e privi solo delle note)
                LinkedHashMap<String, InterviewBean> listaIndicatori = AuditCommand.compare(listaInterviste);
                // Recupera le note e le aggiunge al PxI
                LinkedHashMap<String, InterviewBean> indicators = (LinkedHashMap<String, InterviewBean>) retrieveIndicators(user, listaIndicatori, pat.getId(), ELEMENT_LEV_2, codeSurvey, db);
                // Aggiunge gli indicatori al processo corrente
                pat.setIndicatori(indicators);
                // Aggiunge il processo corrente alla lista dei processi corredati di indicatori
                patsWithIndicators.add(pat);
            }
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nell\'istanziare una WebStorage.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return patsWithIndicators;
    }
    

    /**
     * <p>Restituisce un ArrayList di tutti gli input trovati in base all' 
     * identificativo di un processo o un sottoprocesso anticorruttivo e 
     * a quello di una rilevazione (per le regole di business implementate
     * gli input possono essere collegati direttamente solo al processo 
     * anticorruttivo o al sottoprocesso anticorruttivo, non al macroprocesso 
     * anticorruttivo, i cui input sono costituiti dall'unione di tutti gli input 
     * collegati ai suoi discendenti).</p>
     *
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param id            identificativo del processo o del sottoprocesso (si capisce dal livello)
     * @param level         valore specificante la tabella in cui cercare l'identificativo (2 = processo_at | 3 = sottoprocesso_at)
     * @param codeSurvey    il codice della rilevazione
     * @param db            istanza di WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - lista di input recuperati
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<ItemBean> retrieveInputs(PersonBean user,
                                                     int id,
                                                     int level,
                                                     String codeSurvey,
                                                     DBWrapper db)
                                              throws CommandException {
        ArrayList<ItemBean> inputs = null;
        try {
            // Estrae gli input di un dato processo in una data rilevazione
            inputs = db.getInputs(user, id, level, ConfigManager.getSurvey(codeSurvey));
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero degli input.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return inputs;
    }
    
    
    /**
     * <p>Restituisce un ArrayList di tutte le fasi di un processo 
     * (o sottoprocesso) anticorruttivo nel contesto di una rilevazione.</p>
     *
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param id            identificativo del processo o del sottoprocesso (si capisce dal livello)
     * @param level         valore specificante la tabella in cui cercare l'identificativo (2 = processo_at | 3 = sottoprocesso_at)
     * @param codeSurvey    il codice della rilevazione
     * @param db            istanza di WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;ActivityBean&gt;</code> - lista di fasi recuperate
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<ActivityBean> retrieveActivities(PersonBean user,
                                                             int id,
                                                             int level,
                                                             String codeSurvey,
                                                             DBWrapper db)
                                                      throws CommandException {
        ArrayList<ActivityBean> fasi = null;
        try {
            // Estrae le fasi di un dato processo in una data rilevazione
            fasi = db.getActivities(user, id, level, ConfigManager.getSurvey(codeSurvey));
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero delle attivita\'.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return fasi;
    }


    /**
     * <p>Restituisce un ArrayList di tutti gli output trovati in base all' 
     * identificativo di un processo o un sottoprocesso anticorruttivo e 
     * a quello di una rilevazione.</p>
     *
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param id            identificativo del processo o del sottoprocesso (si capisce dal livello)
     * @param level         valore specificante la tabella in cui cercare l'identificativo (2 = processo_at | 3 = sottoprocesso_at)
     * @param codeSurvey    il codice della rilevazione
     * @param db            istanza di WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - lista di output recuperati
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<ItemBean> retrieveOutputs(PersonBean user,
                                                      int id,
                                                      int level,
                                                      String codeSurvey,
                                                      DBWrapper db)
                                               throws CommandException {
        ArrayList<ItemBean> outputs = null;
        try {
            // Estrae gli output di un dato processo in una data rilevazione
            outputs = db.getOutputs(user, id, level, ConfigManager.getSurvey(codeSurvey));
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero degli output.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return outputs;
    }


    /**
     * <p>Restituisce un ArrayList di tutti i rischi trovati in base all' 
     * identificativo di un processo o un sottoprocesso anticorruttivo e 
     * a quello di una rilevazione.</p>
     *
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param id            identificativo del processo o del sottoprocesso (si capisce dal livello)
     * @param level         valore specificante la tabella in cui cercare l'identificativo (2 = processo_at | 3 = sottoprocesso_at)
     * @param codeSurvey    il codice della rilevazione
     * @param db            istanza di WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;RiskBean&gt;</code> - lista di rischi recuperati
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<RiskBean> retrieveRisks(PersonBean user,
                                                    int id,
                                                    int level,
                                                    String codeSurvey,
                                                    DBWrapper db)
                                             throws CommandException {
        ArrayList<RiskBean> risks = null;
        try {
            // Prepara l'oggetto rilevazione
            CodeBean survey = ConfigManager.getSurvey(codeSurvey);
            // Prepara il processo da passare
            ProcessBean p = new ProcessBean(id, VOID_STRING, VOID_STRING, level, survey.getId());
            // Estrae i rischi di un dato processo in una data rilevazione
            risks = db.getRisksByProcess(user, p, survey);
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero dei rischi.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return risks;
    }


    /**
     * <p>Restituisce un ArrayList di tutte le interviste che hanno fatto
     * riferimento al processo di cui viene passato l'identificativo ed 
     * il livello, nel contesto di una data rilevazione.<br />
     * Per ogni intervista rivolta al processo di dato id, calcola e aggiunge
     * gli indicatori all'intervista stessa.</p>
     *
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param id            identificativo del processo o del sottoprocesso (si capisce dal livello)
     * @param level         valore specificante la tabella in cui cercare l'identificativo (2 = processo_at | 3 = sottoprocesso_at)
     * @param codeSurvey    il codice della rilevazione
     * @param db            istanza di WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;InterviewBean&gt;</code> - lista di interviste recuperate
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<InterviewBean> retrieveInterviews(PersonBean user,
                                                              int id,
                                                              int level,
                                                              String codeSurvey,
                                                              DBWrapper db)
                                                       throws CommandException {
        ArrayList<InterviewBean> interviews = null;
        ArrayList<InterviewBean> processInterviews = new ArrayList<>();
        try {
            // Prepara l'oggetto rilevazione
            CodeBean survey = ConfigManager.getSurvey(codeSurvey);
            // Estrae tutte le interviste in una data rilevazione
            interviews = db.getInterviewsBySurvey(user, survey);
            // Filtra solo le interviste del processo di interesse
            for (InterviewBean interview : interviews) {
                // Per il momento getisce solo il caso del processo (no macro-, no sotto-)
                if (level == ELEMENT_LEV_2) {
                    Vector<ProcessBean> processi = (Vector<ProcessBean>) interview.getProcesso().getProcessi();
                    for (ProcessBean processo : processi) {
                        if (processo.getId() == id) {
                            // Recupera le risposte date ai quesiti dell'intervista
                            ArrayList<QuestionBean> answers = db.getAnswers(user, interview, survey);
                            // Recupera i quesiti
                            ArrayList<QuestionBean> questions = AuditCommand.retrieveQuestions(user, codeSurvey, Query.GET_ALL_BY_CLAUSE, Query.GET_ALL_BY_CLAUSE, db);
                            // Recupera gli indicatori
                            ArrayList<CodeBean> indicatorsAsList = db.getIndicators(user, survey);
                            // Indicizza ogni risposta per il rispettivo id Quesito
                            HashMap<Integer, QuestionBean> answersByQuestions = AuditCommand.decantAnswers(questions, answers);
                            // Calcola gli identificativi dei quesiti corrispondenti a tutti gli indicatori
                            HashMap<String, LinkedList<Integer>> questionsByIndicator = AuditCommand.retrieveQuestionsByIndicators(user, answers, survey, db);
                            // Recupera i processi collegati alla rilevazione corrente
                            ArrayList<ProcessBean> macrosat = retrieveMacroAtBySurvey(user, codeSurvey, db);
                            // Recupera le strutture collegate a tutti i processi at (necessarie per calcolare il valore di I3)
                            HashMap<Integer, ArrayList<DepartmentBean>> structsAsMap = ReportCommand.retrieveStructures(macrosat, user, codeSurvey, db);
                            // Recupera i soggetti terzi collegati a tutti i processi at (necessari per calcolare il valore di I3)
                            HashMap<Integer, ArrayList<DepartmentBean>> subjectsAsMap = ReportCommand.retrieveSubjects(macrosat, user, codeSurvey, db);
                            // Calcola tutti i valori degli indicatori e li restituisce in una mappa, indicizzati per nome
                            LinkedHashMap<String, InterviewBean> indicators = AuditCommand.compute(questionsByIndicator, answersByQuestions, AuditCommand.decantIndicators(indicatorsAsList), structsAsMap.get(new Integer(id)), subjectsAsMap.get(new Integer(id)));
                            // Aggiunge gli indicatori calcolati all'intervista in cui è coinvolto il processo
                            interview.setIndicatori(indicators);
                            // Aggiunge l'intervista con gli indicatori alla lista delle interviste che hanno riguardato il processo di dato id
                            processInterviews.add(interview);
                        }
                    }
                }
            }
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero delle interviste.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return processInterviews;
    }
    
    
    /**
     * <p>Prende in input un HashMap di tutti gli indicatori, con i valori
     * calcolati in riferimento al processo censito dall'anticorruzione 
     * di cui viene passato l'identificativo ed il livello, 
     * nel contesto di una data rilevazione, recupera le note al giudizio
     * sintetico del PxI e le aggiunge al relativo indicatore, aggiornando
     * poi la mappa.<br />
     * Il valore del PxI &egrave; uno solo anche nel caso in cui il processo 
     * di dato id sia stato esaminato nel contesto di pi&uacute; di un'intervista, 
     * producendo quindi valori molteplici, perch&eacute; la mappa che accetta 
     * come argomento contiene gi&agrave; il valore individuato tramite 
     * l'applicazione dell'algoritmo "In dubio pro peior", che sceglie sempre 
     * il valore peggiore (ovvero il rischio pi&uacute; alto) 
     * per ogni serie di valori contrastanti.</p>
     *
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param indicators    mappa di indicatori calcolati a runtime
     * @param id            identificativo del processo o del sottoprocesso (si capisce dal livello)
     * @param level         valore specificante la tabella in cui cercare l'identificativo (2 = processo_at | 3 = sottoprocesso_at)
     * @param codeSurvey    il codice della rilevazione
     * @param db            istanza di WebStorage per l'accesso ai dati
     * @return <code>HashMap&lt;String, InterviewBean&gt;</code> - lista di indicatori recuperati per il processo, con il PxI corredato di note
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static HashMap<String, InterviewBean> retrieveIndicators(PersonBean user,
                                                                    final HashMap<String, InterviewBean> indicators,
                                                                    int id,
                                                                    int level,
                                                                    String codeSurvey,
                                                                    DBWrapper db)
                                                             throws CommandException {
        HashMap<String, InterviewBean> richIndicators = indicators;
        try {
            // Recupera il PxI
            InterviewBean pi = indicators.get(PI);
            // Prepara l'oggetto rilevazione
            CodeBean survey = ConfigManager.getSurvey(codeSurvey);
            // Recupera la nota al giudizio sintetico
            ItemBean piNote = db.getIndicatorPI(user, id, survey);
            // Aggiunge le note al PxI
            pi.setNote(piNote.getInformativa());
            // Aggiunge il PxI "arricchito" agli indicatori precalcolati
            richIndicators.put(PI, pi);
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero della nota PxI.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return richIndicators;
    }
    
    
    /**
     * <p>In ossequio al paradigma DRY (Don't Repeat Yourself), centralizza 
     * il codice che effettua il recupero di tutte le liste e le informazioni 
     * necessarie per costruire la pagina dei dettagli di un processo.</p>
     * <p>Inoltre, il recupero delle informazioni viene svolto non in modo 
     * sequenziale ma parallelo, attraverso l'avviamento di threads separati, 
     * dedicati ciascuno al recupero di informazioni omogenee, al fine 
     * di velocizzare i tempi di computazione.</p>
     * 
     * @param user              utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param idP               identificativo del processo o del sottoprocesso (si capisce dal livello)
     * @param liv               valore specificante la tabella in cui cercare l'identificativo (2 = processo_at | 3 = sottoprocesso_at)
     * @param processElements   generica mappa in cui devono essere valorizzate, per riferimento, le liste di items afferenti al processo
     * @param codeSur           il codice della rilevazione
     * @param db                istanza di WebStorage per l'accesso ai dati
     * @return <code>HashMap&lt;String, InterviewBean&gt;</code> - lista di indicatori recuperati per il processo, con il PxI corredato di note
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento 
     */
    public static HashMap<String, InterviewBean> retrieveProcess(PersonBean user, 
                                                                 int idP, 
                                                                 int liv, 
                                                                 ConcurrentHashMap<String, ArrayList<?>> processElements, 
                                                                 String codeSur, 
                                                                 DBWrapper db) 
                                                                 throws CommandException {
        HashMap<String, InterviewBean> indicators = null;
        try {
            /* ------------------------------------------------ *
             *        Create threads for each computation       *
             * ------------------------------------------------ */
            // Inputs
            Thread threadInputs = new Thread(() -> {
                LOG.info("Thread del recupero degli Input partito...");
                ArrayList<ItemBean> listaInput = null;
                try {
                    listaInput = retrieveInputs(user, idP, liv, codeSur, db);
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del thread degli Input.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
                processElements.put(TIPI_LISTE[NOTHING], listaInput);
                LOG.info("Thread del recupero degli Input terminato...");
            });
            // Fasi
            Thread threadActivities = new Thread(() -> {
                LOG.info("Thread del recupero delle Fasi partito...");
                ArrayList<ActivityBean> listaFasi = null;
                try {
                    listaFasi = retrieveActivities(user, idP, liv, codeSur, db);
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del thread delle Fasi.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
                processElements.put(TIPI_LISTE[ELEMENT_LEV_1], listaFasi);
                LOG.info("Thread del recupero delle Fasi terminato...");
            });
            // Outputs
            Thread threadOutputs = new Thread(() -> {
                LOG.info("Thread del recupero degli Output partito...");
                ArrayList<ItemBean> listaOutput = null;
                try {
                    listaOutput = retrieveOutputs(user, idP, liv, codeSur, db);
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del thread degli Output.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
                processElements.put(TIPI_LISTE[ELEMENT_LEV_2], listaOutput);
                LOG.info("Thread del recupero degli Output terminato...");
            });
            // Rischi
            Thread threadRisks = new Thread(() -> {
                LOG.info("Thread del recupero dei Rischi partito...");
                ArrayList<RiskBean> listaRischi = null;
                try {
                    listaRischi = retrieveRisks(user, idP, liv, codeSur, db);
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del thread dei Rischi.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
                processElements.put(TIPI_LISTE[ELEMENT_LEV_3], listaRischi);
                LOG.info("Thread del recupero dei Rischi terminato...");
            });
            // Interviste
            Thread threadInterviews = new Thread(() -> {
                LOG.info("Thread del recupero Interviste partito...");
                ArrayList<InterviewBean> listaInterviste = null;
                try {
                    listaInterviste = retrieveInterviews(user, idP, liv, codeSur, db);
                } catch (CommandException ce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nell\'esecuzione del thread delle Interviste.\n";
                    LOG.severe(msg + ce.getLocalizedMessage());
                }
                processElements.put(TIPI_LISTE[ELEMENT_LEV_4], listaInterviste);
                LOG.info("Thread del recupero delle Interviste terminato...");
            });
            /* ------------------------------------------------------------ *
             *  Start all threads which are different from the mainthread   *
             * ------------------------------------------------------------ */
            threadInterviews.start();            
            threadInputs.start();
            threadActivities.start();
            threadOutputs.start();
            threadRisks.start();
            /* ------------------------------------------------ *
             *           Wait for all threads to finish         *
             * ------------------------------------------------ */
            try {
                threadInputs.join();
                threadActivities.join();
                threadOutputs.join();
                threadRisks.join();
                threadInterviews.join();
            } catch (InterruptedException ie) {
                String msg = FOR_NAME + "Si e\' verificato un problema nella join dei threads sul mainthread.\n";
                LOG.severe(msg + ie.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
            /* ************************************************ *
             *              Codice legacy sequenziale           *
             * ************************************************ *
            // Recupera Input estratti in base al processo
            //ArrayList<ItemBean> listaInput = retrieveInputs(user, idP, liv, codeSur, db);
            // Recupera Fasi estratte in base al processo
            //ArrayList<ActivityBean> listaFasi = retrieveActivities(user, idP, liv, codeSur, db);
            // Recupera Output estratti in base al processo
            //ArrayList<ItemBean> listaOutput = retrieveOutputs(user, idP, liv, codeSur, db);
            // Recupera Rischi estratti in base al processo
            //ArrayList<RiskBean> listaRischi = retrieveRisks(user, idP, liv, codeSur, db);
            // Recupera le interviste in cui il processo è stato esaminato  */
            ArrayList<InterviewBean> listaInterviste = (ArrayList<InterviewBean>) processElements.get(TIPI_LISTE[ELEMENT_LEV_4]);
            // Recupera gli indicatori corretti (calcolati a runtime e privi solo delle note)
            HashMap<String, InterviewBean> listaIndicatori = AuditCommand.compare(listaInterviste);
            // Recupera le note e le aggiunge al PxI
            indicators = retrieveIndicators(user, listaIndicatori, idP, liv, codeSur, db);
            // Imposta nella tabella le liste trovate
            //processElements.put(TIPI_LISTE[0], listaInput);
            //processElements.put(TIPI_LISTE[1], listaFasi);
            //processElements.put(TIPI_LISTE[2], listaOutput);
            //processElements.put(TIPI_LISTE[3], listaRischi);
            //processElements.put(TIPI_LISTE[4], listaInterviste);
        } catch (CommandException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di dati da metodi retrieve che invocano metodi di db.get.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return indicators;
    }
    
    /* **************************************************************** *
     *                   Metodi di travaso dei dati                     *                     
     *                             (decant)                             *
     * **************************************************************** */
    
    /**
     * <p>Travasa un Vector di ProcessBean in una corrispondente struttura di
     * tipo Dictionary, LinkedHashMap, in cui le chiavi sono rappresentate
     * da oggetti Wrapper di tipi primitivi interi (Integer) e i valori
     * sono rappresentati dai corrispettivi elementi del Vector.</p>
     * <p>&Egrave; utile per un accesso pi&uacute; diretto agli oggetti
     * memorizzati nella struttura rispetto a quanto garantito dai Vector.</p>
     *
     * @param processes Vector di ProcessBean da travasare in HashMap
     * @return <code>LinkedHashMap&lt;Integer&comma; ProcessBean&gt;</code> - Struttura di tipo Dictionary, o Mappa ordinata, avente per chiave un Wrapper dell'identificativo dell'oggetto, e per valore quest'ultimo
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    public static LinkedHashMap<Integer, ProcessBean> decant(Vector<ProcessBean> processes)
                                                      throws CommandException {
        LinkedHashMap<Integer, ProcessBean> userProcesses = new LinkedHashMap<>(7);
        for (int i = 0; i < processes.size(); i++) {
            try {
                ProcessBean process = processes.elementAt(i);
                Integer processIdAsInteger = new Integer(process.getId());
                userProcesses.put(processIdAsInteger, process);
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio del bean, probabilmente l\'id.\n" + anve.getMessage();
                LOG.severe(msg);
                throw new CommandException(msg, anve);
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
        }
        return userProcesses;
    }
    

    /** 
     * <p>Travasa una struttura vettoriale di aree di rischio in una 
     * corrispondente struttura LinkedHashMap di tipo Dictionary, 
     * in cui le chiavi sono rappresentate dalle aree stesse 
     * e i valori sono rappresentati dai macroprocessi aggregati da
     * ciascuna di esse.</p>
     * <p>&Egrave; utile per un accesso pi&uacute; diretto agli oggetti
     * memorizzati nella struttura rispetto a quanto garantito dalla
     * struttura vettoriale (in cui bisogna, per ogni area, scorrere tutti
     * i macroprocessi in essa contenuti).</p>
     *
     * @param areas ArrayList di ProcessBean (aree) contenenti ciascuna una struttura vettoriale di altri ProcessBean (macroprocessi) da travasare in HashMap
     * @return <code>LinkedHashMap&lt;ProcessBean&comma; ArrayList&lt;ProcessBean&gt;&gt;</code> - Struttura di tipo Mappa ordinata avente per chiave un oggetto rappresentante l'area e per valore l'elenco dei suoi macroprocessi
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */   
    public static LinkedHashMap<ProcessBean, ArrayList<ProcessBean>> decant(ArrayList<ProcessBean> areas)
                                                                     throws CommandException {
        LinkedHashMap<ProcessBean, ArrayList<ProcessBean>> matsByAreas = new LinkedHashMap<>(17);
        for (ProcessBean area: areas) {
            try {
                matsByAreas.put(area, (ArrayList<ProcessBean>) area.getProcessi());
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Si e\' verificato un problema di puntamento.\n" + npe.getMessage();
                LOG.severe(msg);
                throw new CommandException(msg, npe);
            } catch (Exception e) {
                String msg = FOR_NAME + "Si e\' verificato un problema nel travaso di un Vector in un Dictionary.\n" + e.getMessage();
                LOG.severe(msg);
                throw new CommandException(msg, e);
            }
        }
        return matsByAreas;
    }
    
    
    /**
     * <p>Estrae da una struttura di rischi, uno di essi,
     * identificato a partire da un identificativo, passato come parametro,
     * oppure null, se il rischio non &egrave; stato identificato.</p>
     *
     * @param risks     struttura vettoriale di RiskBean in cui individuare l'oggetto di interesse
     * @param riskId    identificativo del rischio corruttivo cercato
     * @return <code>RiskBean</code> - Rischio cercato, oppure null (se non e' stato trovato)
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    public static RiskBean decant(AbstractList<RiskBean> risks,
                                  int riskId)
                           throws CommandException {
        try {
            for (RiskBean risk : risks) {
                if (risk.getId() == riskId) {
                    return risk;
                }
            }
            return null;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio del bean, probabilmente l\'id.\n" + anve.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, anve);
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
            String msg = FOR_NAME + "Si e\' verificato un problema.\n" + e.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, e);
        }
    }
    
    
    /* **************************************************************** *
     *                          Metodi di stampa                        *                     
     *                              (print)                             *
     * **************************************************************** */
    
    /**
     * <p>Gestisce la creazione di un file JSON formattato per la libreria
     * orgchart.js (versione 1.0.5) contenente le informazioni relative alla
     * gerarchia dei macroprocessi censiti dall'anticorruzione.</p>
     * 
     * @param req HttpServletRequest    per recuperare la path assoluta della web application
     * @param proc                      lista gerarchica dei processi da rappresentare
     * @param filename                  nome del file json da generare, parametrizzato
     * @param options                   opzioni di formattazione aggiuntive
     * @throws CommandException         se si verifica un puntamento a null o in genere nei casi in cui nella gestione del flusso informativo di questo metodo si verifica un problema
     */
    public static void printJson(HttpServletRequest req,
                                 AbstractList<ProcessBean> proc,
                                 String filename,
                                 ItemBean options)
                          throws CommandException {
        FileWriter out = null;
        try {
            /* ---------------------------------------------------------------- *
             *   Controlla che la directory esista e, se non esiste, la crea    *
             * ---------------------------------------------------------------- */
            // Ottiene la path assoluta della root della web application (/)
            String appPath = req.getServletContext().getRealPath(VOID_STRING);
            LOG.info(FOR_NAME + "appPath vale: " + appPath);
            // Crea la documents root directory (/documenti) se essa non esiste
            String documentsRootName = appPath + ConfigManager.getDirDocuments();
            File documentsRoot = new File(documentsRootName);
            if (!documentsRoot.exists()) {
                if (documentsRoot.mkdir()) {
                    LOG.info(FOR_NAME + documentsRootName + " creata.\n");
                } else {
                    String msg = FOR_NAME + "Attenzione: impossibile creare la directory \'documenti\' sotto " + documentsRootName;
                    LOG.severe(msg);
                    // Inutile continuare
                    throw new CommandException(msg + " Verificare eventualmente di avere i diritti di scrittura nella root dell\'applicazione.\n");
                }
            }
            // Crea la sottodirectory dei file json (/documenti/json) se non esiste
            File jsonDir = new File(appPath + ConfigManager.getDirJson());
            if (!jsonDir.exists()) {
                jsonDir.mkdir();
                LOG.info(FOR_NAME + jsonDir + " creata.\n");
            }
            /* ------------------------------------------------ *
             *             Genera e scrive il json              *
             * ------------------------------------------------ */
            String givenName = filename;
            LOG.info(FOR_NAME + "Nome stabilito per il file: " + givenName);
            String extension = DOT + JSON;
            // Crea il FileWriter (il nome della variabile è quello standard del PrintWriter, finesse...)
            out = new FileWriter(jsonDir + File.separator + givenName + extension);
            // Json begins
            StringBuilder json = new StringBuilder("[");
            // First Node: the principal
            json.append(Data.getStructureJsonNode(COMMAND_PROCESS, "1000", null, "Processi Organizzativi", VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING, null, PRO_PFX, NOTHING)).append(",\n");
            int count = NOTHING;
            do {
                ProcessBean p1 = proc.get(count);
                // MACROPROCESSI
                json.append(Data.getStructureJsonNode(COMMAND_PROCESS, 
                                                      p1.getTag(), 
                                                      "1000", 
                                                      Utils.checkQuote(p1.getNome()), 
                                                      VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING,
                                                      options.getNome(), options.getIcona(), 
                                                      p1.getLivello())).append(",\n");
                // PROCESSI
                for (ProcessBean p2 : p1.getProcessi()) {
                    json.append(Data.getStructureJsonNode(COMMAND_PROCESS, 
                                                          p2.getTag(), 
                                                          p1.getTag(), 
                                                          Utils.checkQuote(p2.getNome()), 
                                                          VOID_STRING, "input :", p2.getVincoli(), "fasi :", p2.getDescrizioneStatoCorrente(),
                                                          options.getNome(), options.getIcona(),
                                                          p2.getLivello())).append(",\n");
                    // SOTTOPROCESSI
                    for (ProcessBean p3 : p2.getProcessi()) {
                        json.append(Data.getStructureJsonNode(COMMAND_PROCESS, 
                                                              p3.getTag(), 
                                                              p2.getTag(), 
                                                              Utils.checkQuote(p3.getNome()), 
                                                              VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING,
                                                              options.getNome(), options.getIcona(),
                                                              p3.getLivello())).append(",\n");
                    }
                }
                count++;
            } while (count < proc.size());
            json = new StringBuilder(json.substring(NOTHING, json.length() - 2));
            json.append("]");
            out.write(json.toString());
            LOG.info(FOR_NAME + "Salvataggio in file system effettuato.\n");
        } catch (IOException ioe) {
            throw new CommandException("Impossibile scrivere il file. Verificare nome e percorso.\n" + ioe.getMessage(), ioe);
        } catch (IllegalStateException ise) {
            throw new CommandException("Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n" + ise.getMessage(), ise);
        } catch (NullPointerException npe) {
            throw new CommandException("Errore nell'estrazione dei dipartimenti che gestiscono il corso.\n" + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        // Tenta in ogni caso di chiudere il file
        } finally {
            if (out != null) {
                try {
                    out.close();
                    LOG.info("File chiuso con successo.");
                } catch (IOException ioe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura del file!\n";
                    LOG.severe(msg);
                    throw new CommandException(msg + ioe.getMessage());
                } catch (Exception e) {
                    throw new CommandException(FOR_NAME + e.getMessage());
                }
            }
        }
    }

}
