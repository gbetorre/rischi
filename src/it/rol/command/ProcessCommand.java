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
import it.rol.util.Utils;
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
import it.rol.util.DataUrl;


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
     * Pagina per mostrare l'elenco degli input
     */
    private static final String nomeFileInputs = "/jsp/prInputs.jsp";
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
     * Pagina contenente la form per inserimento input di processo_at
     */
    private static final String nomeFileAddInput = "/jsp/prInputForm.jsp";
    /** 
     * Pagina contenente la form per inserimento fasi di processo_at
     */
    private static final String nomeFileAddActivity = "/jsp/prFasiForm.jsp";
    /** 
     * Pagina contenente la form per assegnazione strutture/soggetti a fasi
     */
    private static final String nomeFileAddStructs = "/jsp/prStruttureForm.jsp";
    /** 
     * Pagina contenente la form per inserimento output di processo_at
     */
    private static final String nomeFileAddOutput = "/jsp/prOutputForm.jsp";
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
        /*;*/   // It doesn't do anything
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
        nomeFile.put(PART_PROCESS,              nomeFileDettaglio);
        nomeFile.put(PART_INPUT,                nomeFileInputs);
        nomeFile.put(PART_OUTPUT,               nomeFileOutputs);
        nomeFile.put(PART_FACTORS,              nomeFileFattori);
        nomeFile.put(PART_INSERT_F_R_P,         nomeFileAddFactor);
        nomeFile.put(PART_PI_NOTE,              nomeFileNote);
        nomeFile.put(PART_INSERT_PROCESS,       nomeFileSceltaTipo);
        nomeFile.put(PART_INSERT_INPUT,         nomeFileAddInput);
        nomeFile.put(PART_INSERT_ACTIVITY,      nomeFileAddActivity);
        nomeFile.put(PART_INSERT_ACT_STRUCTS,   nomeFileAddStructs);
        nomeFile.put(PART_INSERT_OUTPUT,        nomeFileAddOutput);
        // Carica la hashmap contenente i titoli pagina
        titleFile.put(PART_PROCESS,                         "Dettagli Processo");
        titleFile.put(PART_INPUT,                           "Input Processo");
        titleFile.put(PART_OUTPUT,                          "Output Processo");
        titleFile.put(PART_FACTORS,                         "Fattori Abilitanti");
        titleFile.put(PART_INSERT_F_R_P,                    "Fattore-Rischio-Processo");
        titleFile.put(PART_PI_NOTE,                         "Nota Giudizio Sintetico");       
        titleFile.put(new Byte(ELEMENT_LEV_1).toString(),   "Nuovo Macroprocesso");
        titleFile.put(new Byte(ELEMENT_LEV_2).toString(),   "Nuovo Processo");
        titleFile.put(new Byte(ELEMENT_LEV_3).toString(),   "Nuovo Sottoprocesso");
        titleFile.put(PART_INSERT_INPUT,                    "Aggiunta Input");
        titleFile.put(PART_INSERT_ACTIVITY,                 "Gestione Fasi");
        titleFile.put(PART_INSERT_ACT_STRUCTS,              "Assegnazione Strutture/Soggetti");
        titleFile.put(PART_INSERT_OUTPUT,                   "Aggiunta Output");
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
         *                         Classi di servizio                           *
         * -------------------------------------------------------------------- */
        // Databound
        DBWrapper db = null;
        // Dataurl
        DataUrl dataUrl = new DataUrl();
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        // Utente loggato
        PersonBean user = null;
        /* -------------------------------------------------------------------- *
         *                      Parametri della richiesta                       *
         * -------------------------------------------------------------------- */
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter("r", DASH);
        // Recupera o inizializza 'tipo pagina'
        String part = parser.getStringParameter("p", DASH);
        // Recupera o inizializza eventuale parametro referral
        String ref = parser.getStringParameter("ref", DASH);
        // Id processo, nelle chiamate GET
        int idP = parser.getIntParameter("pliv", DEFAULT_ID);
        // Id processo, nelle chiamate POST
        String pliv = parser.getStringParameter("pliv2", VOID_STRING);
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
        // Flag di scrittura
        Boolean writeAsObject = (Boolean) req.getAttribute("w");
        // Explicit unboxing
        boolean write = writeAsObject.booleanValue();
        /* -------------------------------------------------------------------- *
         *                       Parametri della risposta                       *
         * -------------------------------------------------------------------- */
        // Titolo pagina
        String tP = null;
        // Variabile contenente l'indirizzo per la redirect da una chiamata POST a una chiamata GET
        String redirect = null;
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        /* -------------------------------------------------------------------- *
         *                              Oggetti                                 *
         * -------------------------------------------------------------------- */
        // Prepara un oggetto contenente i parametri opzionali per i nodi
        ItemBean options = new ItemBean("#009966",  VOID_STRING,  VOID_STRING, VOID_STRING, PRO_PFX, NOTHING);
        // Prepara un output di rischio corruttivo
        ProcessBean output = null;
        // Prepara un rischio corruttivo cui è esposto un processo
        RiskBean risk = null;
        // Macroprocesso cui si vuole aggiungere un figlio
        ProcessBean macro = null;
        // Processo cui si vuole aggiungere dati
        ProcessBean pat = null;
        // Eventuale indicatore pxi di uno specifico processo
        ItemBean pxi = null;
        /* -------------------------------------------------------------------- *
         *                          Elenchi di Oggetti                          *
         * -------------------------------------------------------------------- */
        // Dichiara elenco di inpput
        ArrayList<ItemBean> inputs = new ArrayList<>();
        // Dichiara elenco di output
        AbstractList<ProcessBean> outputs = new ArrayList<>();
        // Dichiara elenco di fattori abilitanti
        AbstractList<CodeBean> factors = new ArrayList<>();
        // Dichiara elenco di aree di rischio
        AbstractList<ProcessBean> aree = new ArrayList<>();
        // Dichiara generico elenco di elementi afferenti a un processo
        ConcurrentHashMap<String, ArrayList<?>> processElements = null;
        // Dichiara mappa ordinata con: chiavi = aree di rischio, valori = processi aggregati
        LinkedHashMap<ProcessBean, ArrayList<ProcessBean>> processIndexed = null;
        // Dichiara elenco gerarchico strutture collegate alla rilevazione
        ArrayList<DepartmentBean> structs = null;
        // Dichiara elenco di soggetti contingenti
        ArrayList<ItemBean> subjects = null;
        // Predispone le BreadCrumbs personalizzate per la Command corrente
        LinkedList<ItemBean> bC = null;
        // Tabella che conterrà i valori dei parametri passati dalle form
        HashMap<String, LinkedHashMap<String, String>> params = null;
        // Tabella degli indicatori con i valori elaborati relativi a un processo
        HashMap<String, InterviewBean> indicators = null;
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
                        // In alcuni rami deve differenziare tra finire e continuare
                        String action = parser.getStringParameter("action", DASH);
                        // Indirizzo pagina di dettaglio processo
                        String prProcessoAjax = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS +
                                                AMPERSAND + "p" + EQ + PART_PROCESS +
                                                AMPERSAND + "pliv" + EQ + pliv +
                                                AMPERSAND + "liv" + EQ + ELEMENT_LEV_2 +
                                                AMPERSAND + PARAM_SURVEY + EQ + codeSur;
                        // Indirizzo navigazione albero dei processi
                        String prElenco =       ConfigManager.getEntToken() + EQ + COMMAND_PROCESS +
                                                AMPERSAND + PARAM_SURVEY + EQ + codeSur;
                        // Estrae l'azione dal parametro 'p'
                        /* ------------------------------------------------ *
                         * INSERT new relation between Risk Process Factor  *
                         * ------------------------------------------------ */                       
                        if (part.equalsIgnoreCase(PART_INSERT_F_R_P)) {
                            // Controlla che non sia già presente l'associazione 
                            int check = db.getFactorRiskProcess(user, params);
                            if (check > NOTHING) {  // Genera un errore
                                // Duplicate key value violates unique constraint 
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS + 
                                           AMPERSAND + "p" + EQ + PART_INSERT_F_R_P +
                                           AMPERSAND + "idR" + EQ + parser.getStringParameter("r-id", VOID_STRING) + 
                                           AMPERSAND + "pliv" + EQ + pliv + 
                                           AMPERSAND + "liv" + EQ + ELEMENT_LEV_2 +
                                           AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                           AMPERSAND + MESSAGE + EQ + "dupKey";
                                
                            } else {
                                // Inserisce nel DB nuova associazione pxrxf
                                db.insertFactorRiskProcess(user, params);
                                // Prepara la redirect 
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS + 
                                           AMPERSAND + "p" + EQ + PART_PROCESS +
                                           AMPERSAND + "pliv" + EQ + pliv + 
                                           AMPERSAND + "liv" + EQ + ELEMENT_LEV_2 +
                                           AMPERSAND + PARAM_SURVEY + EQ + codeSur +
                                           AMPERSAND + MESSAGE + EQ + "newRel#rischi";
                            }
                        /* ------------------------------------------------ *
                         *                UPDATE a note to PxI              *
                         * ------------------------------------------------ */                            
                        } else if (part.equalsIgnoreCase(PART_PI_NOTE)) {
                            // Aggiorna la nota
                            db.updateNote(user, params);
                            // Prepara la redirect 
                            if (ref.equalsIgnoreCase(PART_PROCESS)) {
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_PROCESS + 
                                           AMPERSAND + "p" + EQ + PART_PROCESS +
                                           AMPERSAND + "pliv" + EQ + pliv + 
                                           AMPERSAND + "liv" + EQ + ELEMENT_LEV_2 +
                                           AMPERSAND + PARAM_SURVEY + EQ + codeSur;     
                            } else if (ref.equalsIgnoreCase(PART_SELECT_STR)) {
                                redirect = ConfigManager.getEntToken() + EQ + COMMAND_REPORT + 
                                           AMPERSAND + "p" + EQ + PART_SELECT_STR +
                                           AMPERSAND + PARAM_SURVEY + EQ + codeSur;  
                            }
                        /* ------------------------------------------------ *
                         *        INPUT new process - type and data         *
                         * ------------------------------------------------ */                            
                        } else if (part.equalsIgnoreCase(PART_INSERT_PROCESS)) {
                            // Dizionario dei parametri contenente gli estremi dell'area di rischio
                            LinkedHashMap<String, String> proat = params.get(PART_PROCESS);
                            // Codice Area di Rischio
                            String idCodeArea = proat.get("liv0");
                            // Differenzia l'inoltro in funzione del bottone cliccato
                            switch (action) {
                                case "save":
                                    if (liv == ELEMENT_LEV_1) {
                                        db.insertMacroAt(user, params);
                                    } else { // <- Al momento i sottoprocessi non sono gestiti
                                        db.insertProcessAt(user, params);
                                    }
                                    redirect = prElenco;
                                    break;
                                case "cont":
                                    switch (liv) {
                                        case ELEMENT_LEV_1: {
                                            ProcessBean mat = db.insertMacroAt(user, params);
                                            dataUrl.put(ConfigManager.getEntToken(), COMMAND_PROCESS)
                                                   .put("p", PART_INSERT_PROCESS)
                                                   .put("liv", ELEMENT_LEV_2)
                                                   .put("pliv1", mat.getId() + DOT + mat.getCodice())
                                                   .put("pliv0", idCodeArea)
                                                   .put(PARAM_SURVEY, codeSur);
                                            redirect = dataUrl.getUrl();
                                            break;
                                        }
                                        case ELEMENT_LEV_2: {
                                            pat = db.insertProcessAt(user, params);
                                            dataUrl.put(ConfigManager.getEntToken(), COMMAND_PROCESS)
                                                   .put("p", PART_INSERT_INPUT)
                                                   .put("liv", ELEMENT_LEV_2)
                                                   .put("pliv", pat.getId() /*DOT + pat.getCodice()*/)
                                                   .put("pliv1", pat.getPadre().getTag())
                                                   .put("pliv0", idCodeArea)
                                                   .put(PARAM_SURVEY, codeSur);
                                            redirect = dataUrl.getUrl();
                                            break;
                                        }
                                        // TODO
                                        // case ELEMENT_LEV_3 : Gestione sottoprocesso 
                                        default:
                                            System.out.println("Unknown action");
                                            break;
                                    }
                                    break;
                                default:
                                    dataUrl.put(ConfigManager.getEntToken(), COMMAND_PROCESS)
                                           .put("p", PART_INSERT_PROCESS)
                                           .put("liv", liv)
                                           .put(PARAM_SURVEY, codeSur);
                                    redirect = dataUrl.getUrl();
                                    break;
                            }
                        /* ------------------------------------------------ *
                         *                  INSERT Inputs                   *
                         * ------------------------------------------------ */                            
                        } else if (part.equalsIgnoreCase(PART_INSERT_INPUT)) {
                            // Deve aggiungere al dizionario dei parametri quelli di input
                            loadParams(part, req, params);
                            // Inserimento input(s) (Salva...)
                            db.insertInputs(user, params);
                            // Differenzia l'inoltro in funzione del bottone cliccato
                            switch (action) {
                                case "cont":
                                    // ...e Continua (-> Fasi)
                                    dataUrl.put(ConfigManager.getEntToken(), COMMAND_PROCESS)
                                           .put("p", PART_INSERT_ACTIVITY)
                                           .put("pliv", pliv)
                                           .put("liv", ELEMENT_LEV_2)
                                           .put(PARAM_SURVEY, codeSur);
                                    redirect = dataUrl.getUrl();
                                    break;
                                default:
                                    // ...ed Esci (-> Processo)
                                    redirect = prProcessoAjax;
                                    break;
                            }
                        /* ------------------------------------------------ *
                         *                 INSERT Activities                *
                         * ------------------------------------------------ */ 
                        } else if (part.equalsIgnoreCase(PART_INSERT_ACTIVITY)) {
                            // Deve aggiungere al dizionario dei parametri quelli delle fasi
                            loadParams(part, req, params);
                            // Differenzia le operazioni in funzione del bottone cliccato
                            switch (action) {
                                case "ordb":
                                    // Ordina attività
                                    db.updateActivities(user, params);
                                    // Determina l'inoltro in funzione del chiamante
                                    String p = ref.equalsIgnoreCase(PART_PROCESS) ? PART_PROCESS : PART_INSERT_ACTIVITY;
                                    dataUrl.put(ConfigManager.getEntToken(), COMMAND_PROCESS)
                                           .put("p", p)
                                           .put("pliv", pliv)
                                           .put("liv", ELEMENT_LEV_2)
                                           .put(PARAM_SURVEY, codeSur);
                                    redirect = dataUrl.getUrl();
                                    break;
                                case "cont":
                                    // Salva...
                                    db.insertActivities(user, params);
                                    // ...e Continua (-> Strutture/Soggetti)
                                    dataUrl.put(ConfigManager.getEntToken(), COMMAND_PROCESS)
                                           .put("p", PART_INSERT_ACT_STRUCTS)
                                           .put("pliv", pliv)
                                           .put("liv", ELEMENT_LEV_2)
                                           .put(PARAM_SURVEY, codeSur);
                                    redirect = dataUrl.getUrl();
                                    break;
                                default:    
                                    // Salva...
                                    db.insertActivities(user, params);
                                    // ...ed Esci
                                    redirect = prProcessoAjax;
                                    break;
                            }
                        /* ------------------------------------------------ *
                         *  INSERT Relation between Str/Subj and Activities *
                         * ------------------------------------------------ */ 
                        } else if (part.equalsIgnoreCase(PART_INSERT_ACT_STRUCTS)) {
                            String msg = checkActivities(req);
                            // Testa i parametri immessi nella form; se il check passa:
                            if (msg == null) {
                                // Recupera dalla richiesta le relazioni da inserire
                                ArrayList<ItemBean> activities = decantActivities(req);
                                // Inserisce comunque le relazioni
                                db.insertActivitiesStructures(user, activities, params);
                            } else {    
                                // Se il check non passa forza il valore di action
                                action = "err";
                            }
                            // Differenzia i redirect in funzione del bottone cliccato
                            switch (action) {
                                case "err":// C'è dell'errore nella ragione
                                    dataUrl.put(ConfigManager.getEntToken(), COMMAND_PROCESS)
                                           .put("p", PART_INSERT_ACT_STRUCTS)
                                           .put("pliv", pliv)
                                           .put("liv", ELEMENT_LEV_2)
                                           .put(PARAM_SURVEY, codeSur)
                                           .put(MESSAGE, msg);
                                    redirect = dataUrl.getUrl();
                                    break;
                                case "cont":// Continua (-> Output)
                                    dataUrl.put(ConfigManager.getEntToken(), COMMAND_PROCESS)
                                           .put("p", PART_INSERT_OUTPUT)
                                           .put("pliv", pliv)
                                           .put("liv", ELEMENT_LEV_2)
                                           .put(PARAM_SURVEY, codeSur);
                                    redirect = dataUrl.getUrl();
                                    break;
                                case "load":// Salva (-> Reload)
                                    dataUrl.put(ConfigManager.getEntToken(), COMMAND_PROCESS)
                                           .put("p", PART_INSERT_ACT_STRUCTS)
                                           .put("pliv", pliv)
                                           .put("liv", ELEMENT_LEV_2)
                                           .put(PARAM_SURVEY, codeSur);
                                    redirect = dataUrl.getUrl();
                                    break;
                                default:    // Esci
                                    redirect = prProcessoAjax;
                                    break;
                            }
                        /* ------------------------------------------------ *
                         *                 INSERT Outputs                   *
                         * ------------------------------------------------ */                            
                        } else if (part.equalsIgnoreCase(PART_INSERT_OUTPUT)) {
                            // Deve aggiungere al dizionario dei parametri quelli di output
                            loadParams(part, req, params);
                            // Inserimento output(s) (Salva...)
                            db.insertOutputs(user, params);
                            // Differenzia l'inoltro in funzione del bottone cliccato
                            switch (action) {
                                case "cont":
                                    // ...e Continua (-> Nuova Intervista)
                                    dataUrl.put(ConfigManager.getEntToken(), COMMAND_AUDIT)
                                           .put("p", PART_SELECT_STR)
                                           .put(PARAM_SURVEY, codeSur);
                                    redirect = dataUrl.getUrl();
                                    break;
                                default:
                                    // ...ed Esci
                                    redirect = prProcessoAjax;
                                    break;
                            }
                        }
                    }
                /* ======================== @GetMapping ======================= */
                } else {
                    // Il parametro di navigazione 'p' permette di addentrarsi nelle funzioni
                    if (nomeFile.containsKey(part)) {
                        // Definisce un  default per la pagina jsp
                        fileJspT = nomeFile.get(part);
                        // Titolo pagina
                        tP = titleFile.get(part);
                        /* ------------------------------------------------ *
                         * Viene richiesta la visualizzazione del dettaglio * 
                         *         di un processo [SELECT Process]          *
                         * ------------------------------------------------ */                       
                        if (part.equalsIgnoreCase(PART_PROCESS)) {
                            // Istanzia generica tabella in cui devono essere settate le liste di items afferenti al processo
                            processElements = new ConcurrentHashMap<>();
                            // Valorizza tali liste necessarie a visualizzare  i dettagli di un processo, restituendo gli indicatori corredati con le note al PxI
                            indicators = retrieveProcess(user, idP, liv, processElements, codeSur, db);
                            // Ha bisogno di personalizzare le breadcrumbs
                            LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                            bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_4, "Processo");
                        /* ------------------------------------------------ *
                         *                  SELECT Input                    *
                         * ------------------------------------------------ */ 
                        } else if (part.equalsIgnoreCase(PART_INPUT)) {
                            // Istanzia generica tabella in cui mettere le liste di items afferenti al processo
                            processElements = new ConcurrentHashMap<>();
                            // Recupera l'elenco degli output
                            inputs = db.getInputs(user, survey);
                            // Imposta nella tabella la lista ricavata
                            retrieveProcess(user, inputs, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), processElements, survey);
                        /* ------------------------------------------------ *
                         *                  SELECT Output                   *
                         * ------------------------------------------------ */                            
                        } else if (part.equalsIgnoreCase(PART_OUTPUT)) {
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
                        /* ------------------------------------------------ *
                         *                  SELECT Factors                  *
                         * ------------------------------------------------ */                          
                        } else if (part.equalsIgnoreCase(PART_FACTORS)) {
                            // Deve recuperare l'elenco dei fattori abilitanti
                            factors = db.getFactors(user, survey);
                            // Ha bisogno di personalizzare le breadcrumbs
                            LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                            bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_2, "Fattori abilitanti");
                        /* ------------------------------------------------ *
                         *  SHOWS Form to LINK Factor TO Risk INTO Process  *
                         * ------------------------------------------------ */                           
                        } else if (part.equalsIgnoreCase(PART_INSERT_F_R_P)) {
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
                        /* ------------------------------------------------ *
                         * SHOWS Form to INSERT or UPDATE a note about PxI  *
                         * ------------------------------------------------ */  
                        } else if (part.equalsIgnoreCase(PART_PI_NOTE)) {
                            // Deve recuperare e mostrare la nota al giudizio sintetico PxI
                            pxi = db.getIndicatorPI(user, idP, survey);
                            // Ha bisogno di personalizzare le breadcrumbs
                            LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                            bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_2, "Nota");
                        /* ------------------------------------------------ *
                         *             SHOWS Form Macro/Process             *
                         * ------------------------------------------------ */                            
                        } else if (part.equalsIgnoreCase(PART_INSERT_PROCESS)) {
                            // Controlla che esista il livello
                            if (liv > DEFAULT_ID) {
                                aree = db.getAree(user, survey);
                                processIndexed = decant((ArrayList<ProcessBean>) aree);
                                // Se c'è un id macroprocesso vuol dire che bisogna aggiungere un processo
                                if (!idM.equals(DASH)) {
                                    int idMat = Integer.parseInt(idM.substring(NOTHING, idM.indexOf(DOT)));
                                    String areaRischio = parser.getStringParameter("pliv0", DASH);
                                    macro = db.getMacroSubProcessAtByIdOrCode(user, idMat, VOID_STRING, ELEMENT_LEV_1, survey);
                                    macro.setAreaRischio(areaRischio);
                                }
                                // Sovrascrive Titolo pagina
                                tP = titleFile.get(String.valueOf(liv));
                                // Form to insert data of the process
                                fileJspT = nomeFileAddProcess;
                            }
                            // Ha bisogno di personalizzare le breadcrumbs
                            LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                            bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_2, "Nuovo Elemento");
                        /* ------------------------------------------------ *
                         *                 SHOWS Form Input                 *
                         * ------------------------------------------------ */                           
                        } else if (part.equalsIgnoreCase(PART_INSERT_INPUT)) {
                             // Istanzia generica tabella in cui devono essere settate le liste di items afferenti al processo
                             processElements = new ConcurrentHashMap<>();
                             // Recupera tutti gli Input
                             ArrayList<ItemBean> listaInput = db.getInputs(user, survey);
                             // Se c'è un id processo
                             if (idP > NOTHING) {
                                 // Controlla che il processo esista
                                 pat = db.getProcessById(user, idP, survey);
                                 // Recupera Input estratti in base al processo
                                 ArrayList<ItemBean> inputsByPat = db.getInputs(user, idP, ELEMENT_LEV_2, survey);
                                 // Visto che ci sono, ne approfitta per impostare gli input nel processo
                                 pat.setInputs(inputsByPat);
                                 // Scarta dalla lista degli input quelli già collegati al processo corrente
                                 listaInput = filter(listaInput, inputsByPat);
                             }
                             // Imposta nella tabella la lista ricavata
                             retrieveProcess(user, listaInput, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), processElements, survey);
                        /* ------------------------------------------------ *
                         *              SHOWS Form Activities               *
                         * ------------------------------------------------ */                           
                        } else if (part.equalsIgnoreCase(PART_INSERT_ACTIVITY)) {
                            // Istanzia generica tabella in cui devono essere settate le liste di items afferenti al processo
                            processElements = new ConcurrentHashMap<>();
                            // Recupera il processo corrente
                            pat = db.getProcessById(user, idP, survey);
                            // Recupera Attività estratte in base al processo
                            ArrayList<ActivityBean> activitiesByPat = db.getActivities(user, idP, ELEMENT_LEV_2, survey);
                            // Imposta nella tabella la lista ricavata
                            retrieveProcess(user, new ArrayList<>(), activitiesByPat, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), processElements, survey);
                        /* ------------------------------------------------ *
                         * SHOWS Form to Link Structures to Empty Activities*
                         * ------------------------------------------------ */                           
                        } else if (part.equalsIgnoreCase(PART_INSERT_ACT_STRUCTS)) {
                            // Istanzia generica tabella in cui devono essere settate le liste di items afferenti al processo
                            processElements = new ConcurrentHashMap<>();
                            // Recupera il processo corrente
                            pat = db.getProcessById(user, idP, survey);
                            // Recupera Attività estratte in base al processo
                            ArrayList<ActivityBean> activitiesByPat = db.getActivities(user, idP, ELEMENT_LEV_2, survey);
                            // Imposta nella tabella la lista ricavata
                            retrieveProcess(user, new ArrayList<>(), activitiesByPat, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), processElements, survey);
                            // Recupera le strutture della rilevazione corrente
                            structs = DepartmentCommand.retrieveStructures(codeSur, user, db);
                            // Recupera solo i soggetti contingenti di tipo master
                            subjects = db.getSubjects(user, true, !Query.GET_ALL, survey);
                        /* ------------------------------------------------ *
                         *                 SHOWS Form Output                *
                         * ------------------------------------------------ */    
                        } else if (part.equalsIgnoreCase(PART_INSERT_OUTPUT)) {
                            // Istanzia generica tabella in cui devono essere settate le liste di items afferenti al processo
                            processElements = new ConcurrentHashMap<>();
                            // Recupera tutti gli Output
                            ArrayList<ProcessBean> listaOutput = db.getOutputs(user, survey);
                            // Li travasa in una lista di oggetti generici
                            ArrayList<ItemBean> listaItems = decantOutputs(listaOutput);
                            // Se c'è un id processo
                            if (idP > NOTHING) {
                                // Controlla che il processo esista
                                pat = db.getProcessById(user, idP, survey);
                                // Recupera Output estratti in base al processo
                                ArrayList<ItemBean> outputsByPat = db.getOutputs(user, idP, ELEMENT_LEV_2, survey);
                                // Visto che ci sono, ne approfitta per impostare gli output nel processo
                                pat.setOutputs(outputsByPat);
                                // Scarta dalla lista degli output quelli già collegati al processo corrente
                                listaItems = filter(listaItems, outputsByPat);
                            }
                            // Imposta nella tabella la lista ricavata
                            retrieveProcess(user, new ArrayList<>(), new ArrayList<>(), listaItems, new ArrayList<>(), new ArrayList<>(), processElements, survey);
                        }
                    } else {
                        // Viene richiesta la visualizzazione di un elenco di macroprocessi
                        ArrayList<ProcessBean> macrosat = retrieveMacroAtBySurvey(user, codeSur, db);
                        // Genera il file json contenente le informazioni strutturate
                        printJson(req, macrosat, nomeFileJson, options);
                        // Imposta la jsp
                        fileJspT = nomeFileElenco;
                        // Imposta il titolo
                        tP = "Processi Organizzativi";
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
        if (processElements != null && !processElements.get(TIPI_LISTE[NOTHING]).isEmpty()) {
            req.setAttribute("listaInput", processElements.get(TIPI_LISTE[NOTHING]));
        }
        // Imposta in request elenco completo fasi di processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[ELEMENT_LEV_1]).isEmpty()) {
            req.setAttribute("listaFasi", processElements.get(TIPI_LISTE[ELEMENT_LEV_1]));
        }
        // Imposta in request elenco completo output di processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[ELEMENT_LEV_2]).isEmpty()) {
            req.setAttribute("listaOutput", processElements.get(TIPI_LISTE[ELEMENT_LEV_2]));
        }
        // Imposta in request elenco completo rischi di processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[ELEMENT_LEV_3]).isEmpty()) {
            req.setAttribute("listaRischi", processElements.get(TIPI_LISTE[ELEMENT_LEV_3]));
        }
        // Imposta in request elenco completo interviste che hanno sondato il processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[ELEMENT_LEV_4]).isEmpty()) {
            req.setAttribute("listaInterviste", processElements.get(TIPI_LISTE[ELEMENT_LEV_4]));
        }
        // Imposta nella request elenco completo strutture
        if (structs != null) {
            req.setAttribute("strutture", structs);
        }
        // Imposta nella request elenco soggetti contingenti
        if (subjects != null) {
            req.setAttribute("soggetti", subjects);
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
        // Imposta in request specifico processo cui aggiungere dati
        if (pat != null) {
            req.setAttribute("pat", pat);
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
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        // Estrae l'azione dal parametro 'p'
        switch (part) {
            /* ---------------------------------------------------- *
             *              Parametri inserimento Input             *
             * ---------------------------------------------------- */
            case (PART_INSERT_INPUT): {
                LinkedHashMap<String, String> input = new LinkedHashMap<>();
                // Suffissi convenzionali per i parametri di input
                String inputName = "n";
                String inputDesc = "d";
                String inputFlag = "f";
                String inputExist = "e";
                // Input da inserire
                String[] input1 = req.getParameterValues("in-newn");
                String[] input2 = req.getParameterValues("in-desc");
                String[] input3 = req.getParameterValues("in-type");
                String[] input0 = req.getParameterValues("in-name");
                // Aggiunge tutti gli elementi dei nuovi input definiti dall'utente
                decantItems("in", inputName, input1, input);
                decantItems("in", inputDesc, input2, input);
                decantItems("in", inputFlag, input3, input);
                // Aggiunge i nomi e gli id di input esistenti, da collegare
                decantItems("in", inputExist, input0, input);
                // Aggiunge il numero di input da collegare
                String inputToLink = (input0[NOTHING].equals(VOID_STRING)) ? String.valueOf(NOTHING) : String.valueOf(input0.length);
                input.put("ine", inputToLink);
                // Aggiunge tutti i parametri degli input ai parametri della richiesta
                formParams.put(part, input);
                break;
            }
            /* ---------------------------------------------------- *
             *       Parametri aggiornamento/inserimento Fasi       *
             * ---------------------------------------------------- */
            case (PART_INSERT_ACTIVITY): { 
                LinkedHashMap<String, String> activities = new LinkedHashMap<>();
                String action = parser.getStringParameter("action", DASH);
                // Controlla se deve fare l'aggiornamento delle fasi
                if (action.equals("ordb")) {
                    // Fasi da aggiornare
                    String[] idActs = req.getParameterValues("ac-id");
                    String[] dbActs = req.getParameterValues("ac-ordb");
                    // Abbina gli id ai numeri d'ordine scelti dall'utente
                    decantActivities(idActs, dbActs, activities);
                } else {
                    // Ordinale di partenza
                    String dbAct = parser.getStringParameter("ac-ordb", "100");
                    // Codice ultima fase inserita (se non ci sono fasi = COD.PROD.00)
                    String defaultCode = req.getParameter("pat-code") + DOT + "10";
                    String cdAct = parser.getStringParameter("ac-code", defaultCode);
                    // Fasi da inserire
                    String[] acts = req.getParameterValues("ac-name");
                    // Genera i codici e li abbina ai nomi scelti dall'utente
                    decantActivities(cdAct, acts, activities);
                    // Aggiunge alla mappa l'ordinale di partenza
                    activities.put("ordb", dbAct);
                }
                // Aggiunge i parametri attività ai parametri della richiesta
                formParams.put(part, activities);
                break;
            }
            /* ---------------------------------------------------- *
             *             Parametri inserimento Output             *
             * ---------------------------------------------------- */
            case (PART_INSERT_OUTPUT): {
                LinkedHashMap<String, String> output = new LinkedHashMap<>();
                // Suffissi convenzionali per i parametri di output
                String outputName = "n";
                String outputDesc = "d";
                String outputFlag = "f";
                String outputExist = "e";
                // Output da inserire
                String[] output1 = req.getParameterValues("ou-newn");
                String[] output2 = req.getParameterValues("ou-desc");
                String[] output3 = req.getParameterValues("ou-type");
                String[] output0 = req.getParameterValues("ou-name");
                // Aggiunge tutti gli elementi dei nuovi output definiti dall'utente
                decantItems("ou", outputName, output1, output);
                decantItems("ou", outputDesc, output2, output);
                decantItems("ou", outputFlag, output3, output);
                // Aggiunge i nomi e gli id di output esistenti, da collegare
                decantItems("ou", outputExist, output0, output);
                // Aggiunge il numero di output da collegare
                String outputToLink = (output0[NOTHING].equals(VOID_STRING)) ? String.valueOf(NOTHING) : String.valueOf(output0.length);
                output.put("oue", outputToLink);
                // Aggiunge tutti i parametri degli output ai parametri della richiesta
                formParams.put(part, output);
                break;
            }
        }
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
        // Controllo sull'input
        if (indicators == null || indicators.isEmpty()) {
            return new HashMap<>();
        }
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
     * <p>In rispetto del paradigma DRY (Don't Repeat Yourself), centralizza 
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
    
    
    /**
     * Valorizza per riferimento una mappa sincronizzata contenente tutti
     * gli elementi appartenenti ad uno stesso processo.
     * 
     * @param user              Utente corrente
     * @param listaInput        Elenco input di processo
     * @param listaFasi         Elenco fasi in cui e' strutturato il processo
     * @param listaOutput       Elenco output di processo
     * @param listaRischi       Elenco rischi cui il processo e' esposto
     * @param listaInterviste   Elenco interviste in cui il processo e' stato oggetto di indagine
     * @param processElements   Dictionary da valorizzare ByRef
     * @param survey            Oggetto rilevazione
     * @throws CommandException se si verifica un puntamento a null
     */
    public static void retrieveProcess(PersonBean user, 
                                       ArrayList<ItemBean> listaInput,
                                       ArrayList<ActivityBean> listaFasi,
                                       ArrayList<ItemBean> listaOutput,
                                       ArrayList<RiskBean> listaRischi,
                                       ArrayList<InterviewBean> listaInterviste,
                                       ConcurrentHashMap<String, ArrayList<?>> processElements, 
                                       CodeBean survey) 
                                       throws CommandException {
        // Controllo sull'input
        if (processElements == null) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Controllare il valore dei parametri!\n";
            LOG.severe(msg);
            throw new CommandException(msg);
        }
        // Imposta nella tabella le liste ricevute
        processElements.put(TIPI_LISTE[NOTHING], listaInput);
        processElements.put(TIPI_LISTE[ELEMENT_LEV_1], listaFasi);
        processElements.put(TIPI_LISTE[ELEMENT_LEV_2], listaOutput);
        processElements.put(TIPI_LISTE[ELEMENT_LEV_3], listaRischi);
        processElements.put(TIPI_LISTE[ELEMENT_LEV_4], listaInterviste);
    }
    
    /* **************************************************************** *
     *                   Metodi di travaso dei dati                     *                     
     *                    (decant, filter, purge)                       *
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
        for (ProcessBean area : areas) {
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

    
    /**
     * <p>Travasa una struttura vettoriale ordinata di output, 
     * incapsulati sotto forma di processi (ProcessBean), in una 
     * struttura vettoriale ordinata di output, 
     * rappresentati come oggetti generici (ItemBean).</p>
     * <p>&Egrave; utile per un accesso pi&uacute; generale agli oggetti
     * memorizzati nella struttura rispetto a quanto garantito da
     * oggetti specifici come i ProcessBean.</p>
     *
     * @param outputs Struttura vettoriale di ProcessBean da travasare in ItemBean
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - Struttura di ItemBean in cui sono stati travasati i valori dei ProcessBean
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    public static ArrayList<ItemBean> decantOutputs(final ArrayList<ProcessBean> outputs)
                                             throws CommandException {
        ArrayList<ItemBean> outputsAsItems = new ArrayList<>();
        for (ProcessBean o : outputs) {
            try {
                ItemBean item = new ItemBean();
                item.setId(o.getId());
                item.setNome(o.getNome());
                item.setInformativa(o.getDescrizione());
                item.setOrdinale(o.getOrdinale());
                item.setExtraInfo1(String.valueOf(o.getDataUltimaModifica()));
                item.setExtraInfo2(String.valueOf(o.getOraUltimaModifica()));
                item.setExtraInfo3(String.valueOf(o.getIdRilevazione()));
                item.setExtraInfo4(String.valueOf(o.getLivello()));
                outputsAsItems.add(item);
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un attributo obbligatorio del bean.\n" + anve.getMessage();
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
        return outputsAsItems;
    }
    
    
    /**
     * Dati in input un array di valori e un livello numerico, distribuisce
     * tali valori in una struttura dictionary, passata come parametro, 
     * assegnandone ciascuno a una chiave diversa, costruita
     * in base a un progressivo ed un valore intero passato come parametro.
     * 
     * @param prefix    prefisso delle chiavi
     * @param suffix    livello di informazione convenzionale
     * @param items     valori inseriti nella form
     * @param params    mappa dei parametri della richiesta
     */
    private static void decantItems(String prefix,
                                    String suffix,
                                    String[] items,
                                    LinkedHashMap<String, String> params) {
        int index = NOTHING;
        int size = ELEMENT_LEV_1;
        if (items != null) { // <- Controllo sull'input
            while (index < items.length) {
                params.put(prefix + suffix + DASH + size,  items[index]);
                size++;
                index++;
            }
        }        
    }
    
    
    /**
     * Dati in input un array di id e un array di numeri d'ordine corrispondenti, 
     * associa gli id ai numeri d'ordine, distribuendoli
     * in una struttura dictionary, passata come parametro, che valorizza
     * per riferimento (ByRef).
     * 
     * @param ids       array di id
     * @param dbs       ordinali inseriti nella form
     * @param params    mappa dei parametri della richiesta
     */
    private static void decantActivities(String[] ids,
                                         String[] dbs,
                                         LinkedHashMap<String, String> params) {
        if (ids != null && dbs != null) { // <- Controllo sull'input
            for (int i = 0; i < ids.length; i++) {
                params.put(ids[i],  dbs[i]);
            }
        }        
    }
    
    
    /**
     * Dati in input un array nomi di attivit&agrave; e:<ol>
     * <li>o il codice massimo delle attivit&agrave; gi&agrave; 
     * collegate a un processo, se presenti (p.es. RIC.02.02.03
     * assumendo che per il processo RIC.02.02 siano state definite
     * solo 3 attivit&agrave;);</li>
     * <li>o un codice di default composto dal codice processo
     * pi&uacute; il suffisso 00 (p.es. CVS.01.02.00 assumendo
     * che rispetto al processo CSV.01.02 non siano ancora state definite
     * attivit&agrave;);</li></ul> 
     * genera un nuovo codice univoco per ogni nome dell'array
     * e lo associa ad ogni nome corrispondente, 
     * inserendo il tutto in una struttura dictionary, 
     * passata come parametro, che valorizza
     * per riferimento (ByRef).
     * 
     * @param currentCode   codice da cui partire per generare i successivi
     * @param attivita      attivita' da inserire, definite dall'utente
     * @param params        mappa dei parametri della richiesta
     * @throws CommandException se l'ordinale da cui partire e/o le attivita' non sono in formato corretto
     */
    private static void decantActivities(String currentCode,
                                         String[] attivita,
                                         LinkedHashMap<String, String> params)
                                  throws CommandException {
        if (attivita != null && currentCode != null) { // <- Controllo sull'input
            try {
                //String progAsString = lastActCode.substring(lastActCode.lastIndexOf(DOT) + ELEMENT_LEV_1);
                //int prog = Integer.parseInt(progAsString);
                
                // Separa il codice attività passato ( oppure  se non esistevano fasi) in prefisso e parte numerica
                String[] parts = currentCode.split("\\.");
                // Controllo sull'input (formato XYZ.##.##.##)
                if (parts.length != ELEMENT_LEV_4) {
                    throw new IllegalArgumentException("Invalid code format. Expected format: PREFIX.NUMBER.NUMBER.NUMBER");
                }
                // Assegnazione dei tre token estratti alle variabili
                String prefix = parts[NOTHING]; // e.g., "PER" or "RIC"
                String matPart = parts[ELEMENT_LEV_1]; // e.g., "02" or "04"
                String patPart = parts[ELEMENT_LEV_2]; // e.g., "02" or "04"
                String actPart = parts[ELEMENT_LEV_3];  // come sopra
                int number = Integer.parseInt(actPart);
                // Cicla sui nomi delle nuove attività
                if (attivita.length > NOTHING) {
                    for (int i = 0; i < attivita.length; i++) {
                        // Non considera i campi vuoti
                        if (attivita[i].equals(VOID_STRING)) {
                            continue;
                        }
                        // Incrementa la parte numerica                 
                        number += ELEMENT_LEV_1; 
                        // Determina quanti zeri c'erano nel codice originale
                        int leadingZeros = actPart.length() - Integer.toString(number).length();
                        // Costruisce una nuova parte numerica aggiungendo uno zero iniziale sse <10
                        String newNumberPart = String.format("%0" + (leadingZeros + Integer.toString(number).length()) + "d", new Integer(number));
                        // Genera il nuovo codice
                        String nextCode = prefix + DOT + matPart + DOT + patPart + DOT + newNumberPart;
                        // Abbina il nuovo codice al nome definito dall'utente
                        params.put(nextCode,  attivita[i]);
                    }
                } else {
                    String msg = FOR_NAME + "Attenzione: attivita\' da inserire non definite!\n";
                    LOG.severe(msg);
                    throw new CommandException(msg + " Verificare i valori ricevuti come parametro.\n");
                }
            } catch (NumberFormatException nfe) {
                String msg = FOR_NAME + "Attenzione: impossibile ottenere un ordinale valido!\n";
                LOG.severe(msg);
                throw new CommandException(msg + " Verificare il numero d\'ordine rievuto come parametro.\n" + nfe.getMessage(), nfe);
            }
        } else {
            String msg = FOR_NAME + "Attenzione: parametri di input non accettati!\n";
            LOG.severe(msg);
            throw new CommandException(msg + " Verificare i valori ricevuti come parametro.\n");
        }        
    }
    
    
    /**
     * Processa la richiesta per verificare se i parametri immessi sono corretti.
     * 
     * @param  req   la HttpServletRequest contenente la richiesta del client
     * @return <code>boolean</code> -  true se i parametri immessi hanno passato i controlli, false altrimenti
     * @throws CommandException se si verifica un puntamento a null, un problema nella conversione di tipi o altro tipo di problematica
     */
    private static String checkActivities(HttpServletRequest req)
                                    throws CommandException {
        String msg = null;
        String ids = req.getParameter("ids");
        String[] parts = ids.split("\\.");
        for (int i = 0; i < parts.length; i++) {
            String actId = parts[i];
            if (!actId.equals(VOID_STRING)) {
                boolean existsL1 = false;
                boolean existsL2 = false;
                boolean existsL3 = false;
                boolean existsL4 = false;
                boolean existsSj = false;
                String liv1 = req.getParameter("liv1" + DASH + actId);
                String liv2 = req.getParameter("liv2" + DASH + actId);
                String liv3 = req.getParameter("liv3" + DASH + actId);
                String liv4 = req.getParameter("liv4" + DASH + actId);
                String subj = req.getParameter("sc" + DASH + actId);
                // Valorizza i flag
                if (liv1 != null && !liv1.equals(VOID_STRING)) {
                    existsL1 = true;
                }
                if (liv2 != null && !liv2.equals(VOID_STRING)) {
                    existsL2 = true;
                }
                if (liv3 != null && !liv3.equals(VOID_STRING)) {
                    existsL3 = true;
                }
                if (liv4 != null && !liv4.equals(VOID_STRING)) {
                    existsL4 = true;
                }
                // Soggetto
                if (subj != null && !subj.equals(VOID_STRING)) {
                    existsSj = true;
                }
                // Test: non possono essere presenti 2 strutture sulla stessa riga
                if ((existsL1 && existsL2) || (existsL1 && existsL3) || (existsL1 && existsL4) || (existsL2 && existsL3) || (existsL2 && existsL4) || (existsL3 && existsL4)) {
                    msg = "Selezionare soltanto una struttura alla volta!";
                }
                // Test: non possono essere presenti una struttura ed un soggetto
                if ((existsL1 || existsL2 || existsL3 || existsL4) && existsSj) {
                    msg = "Non selezionare contemporaneamente una struttura e un soggetto!";
                }
                // Test: la struttura selezionata deve avere un id valido
                if (existsL1 && (liv1.indexOf(')') == DEFAULT_ID) ) {
                    msg = "La struttura " + liv1 + " non ha un formato corretto!";
                }
                if (existsL2 && (liv2.indexOf(')') == DEFAULT_ID) ) {
                    msg = "La struttura " + liv2 + " non ha un formato corretto!";
                }
                if (existsL3 && (liv3.indexOf(')') == DEFAULT_ID) ) {
                    msg = "La struttura " + liv3 + " non ha un formato corretto!";
                }
                if (existsL4 && (liv4.indexOf(')') == DEFAULT_ID) ) {
                    msg = "La struttura " + liv4 + " non ha un formato corretto!";
                }
                if (existsSj && (subj.indexOf(')') == DEFAULT_ID) ) {
                    msg = "Il soggetto " + subj + " non ha un formato corretto!";
                }
            }
        }
        return msg;
    }
    
    
    /**
     * Processa la richiesta per organizzare in una struttura vettoriale
     * lista di relazioni tra attivit&agrave; e strutture/soggetti contingenti
     * da passare ai metodi di inserimento del Model.
     * 
     * @param  req   la HttpServletRequest contenente la richiesta del client
     * @return <code>ArrayList&lt;ItemBean&gt;</code> -  lista vettoriale ottenuta travasando i parametri passati dalla form in oggetti
     * @throws CommandException se si verifica un puntamento a null, un problema nella conversione di tipi o altro tipo di problematica
     */
    private static ArrayList<ItemBean> decantActivities(HttpServletRequest req)
                                                 throws CommandException {
        ArrayList<ItemBean> acts = new ArrayList<>();
        ItemBean act = null;
        String ids = req.getParameter("ids");
        String[] parts = ids.split("\\.");
        for (int i = 0; i < parts.length; i++) {
            String actId = parts[i];
            if (!actId.equals(VOID_STRING)) {
                act = new ItemBean();
                boolean atLeast1 = false;
                String liv1 = req.getParameter("liv1" + DASH + actId);
                String liv2 = req.getParameter("liv2" + DASH + actId);
                String liv3 = req.getParameter("liv3" + DASH + actId);
                String liv4 = req.getParameter("liv4" + DASH + actId);
                String subj = req.getParameter("sc" + DASH + actId);
                act.setId(Integer.parseInt(actId));
                // Recupero strutture da collegare
                if (liv1 != null && !liv1.equals(VOID_STRING)) {
                    String liv1IdAsString = liv1.substring(liv1.lastIndexOf("(") + ELEMENT_LEV_1, liv1.lastIndexOf(")"));
                    int liv1Id = Integer.parseInt(liv1IdAsString);
                    act.setCod1(liv1Id);
                    atLeast1 = true;
                }
                if (liv2 != null && !liv2.equals(VOID_STRING)) {
                    String liv2IdAsString = liv2.substring(liv2.lastIndexOf("(") + ELEMENT_LEV_1, liv2.lastIndexOf(")"));
                    int liv2Id = Integer.parseInt(liv2IdAsString);
                    act.setCod2(liv2Id);
                    atLeast1 = true;
                }
                if (liv3 != null && !liv3.equals(VOID_STRING)) {
                    String liv3IdAsString = liv3.substring(liv3.lastIndexOf("(") + ELEMENT_LEV_1, liv3.lastIndexOf(")"));
                    int liv3Id = Integer.parseInt(liv3IdAsString);
                    act.setCod3(liv3Id);
                    atLeast1 = true;
                }
                if (liv4 != null && !liv4.equals(VOID_STRING)) {
                    String liv4IdAsString = liv4.substring(liv4.lastIndexOf("(") + ELEMENT_LEV_1, liv4.lastIndexOf(")"));
                    int liv4Id = Integer.parseInt(liv4IdAsString);
                    act.setCod4(liv4Id);
                    atLeast1 = true;
                }
                // Recupero soggetto da collegare
                if (subj != null && !subj.equals(VOID_STRING)) {
                    String subjIdAsString = subj.substring(subj.lastIndexOf("(") + ELEMENT_LEV_1, subj.lastIndexOf(")"));
                    int subjId = Integer.parseInt(subjIdAsString);
                    act.setValue1(subjId);
                    atLeast1 = true;
                }
                if (atLeast1) {
                    acts.add(act);
                }
            }
        }
        return acts;
    }
      
    
    /**
     * Dato un elenco complessivo di oggetti e un elenco dato di oggetti, 
     * scarta dall'elenco complessivo gli oggetti presenti nell'elento dato.
     * 
     * @param allObjects elenco complessivo di oggetti
     * @param someObject elenco specifico di oggetti
     * @return <code>ArrayList&lt;ItemBean&gt;</code> -  lista vettoriale ottenuta scartando dall'elenco maggiore gli elementi dell'elenco minore
     */
    public static ArrayList<ItemBean> filter(final ArrayList<ItemBean> allObjects,
                                             final ArrayList<ItemBean> someObject) {
        ArrayList<ItemBean> results = allObjects;
        for (ItemBean o : someObject) {
            if (allObjects.contains(o)) {
                results.remove(o);
            }
        }
        return results;
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
