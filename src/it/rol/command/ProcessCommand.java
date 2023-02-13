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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import it.rol.Data;
import it.rol.Main;
import it.rol.Utils;
import it.rol.bean.ActivityBean;
import it.rol.bean.CodeBean;
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
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
     * Nome del file json della Command (dipende dalla pagina di default)
     */
    private String nomeFileJson = nomeFileElenco.substring(nomeFileElenco.lastIndexOf('/'), nomeFileElenco.indexOf(DOT));
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutte le pagine gestite da questa Command
     */    
    private static final HashMap<String, String> nomeFile = new HashMap<>();


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
        nomeFile.put(PART_PROCESS, nomeFileDettaglio);
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
        // Recupera o inizializza 'id processo'
        int idP = parser.getIntParameter("pliv", DEFAULT_ID);
        // Recupera o inizializza 'tipo pagina'
        String part = parser.getStringParameter("p", DASH);
        // Recupera o inizializza livello di granularità del processo anticorruttivo
        int liv = parser.getIntParameter("liv", DEFAULT_ID);
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Dichiara elenco di processi
        AbstractList<ProcessBean> macrosat = new ArrayList<>();
        // Dichiara generico elenco di elementi afferenti a un processo
        HashMap<String, ArrayList<?>> processElements = null;
        // Prepara un oggetto contenente i parametri opzionali per i nodi
        ItemBean options = new ItemBean("#009966",  VOID_STRING,  VOID_STRING, VOID_STRING, PRO_PFX, NOTHING);
        // Predispone le BreadCrumbs personalizzate per la Command corrente
        LinkedList<ItemBean> bC = null;
        /* ******************************************************************** *
         *      Instanzia nuova classe WebStorage per il recupero dei dati      *
         * ******************************************************************** */
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new CommandException(FOR_NAME + "Non e\' disponibile un collegamento al database\n." + wse.getMessage(), wse);
        }
        /* ******************************************************************** *
         *                         Recupera la Sessione                         *
         * ******************************************************************** */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            user = (PersonBean) ses.getAttribute("usr");
            if (user == null) {
                throw new CommandException(FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n");
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
         *                           Corpo del metodo                           *
         * ******************************************************************** */
        // Decide il valore della pagina
        try {
            // Il parametro di navigazione 'rilevazione' è obbligatorio
            if (!codeSur.equals(DASH)) {
                // Il parametro di navigazione 'p' permette di addentrarsi nelle funzioni
                if (nomeFile.containsKey(part)) { // Viene richiesta la visualizzazione del dettaglio di un processo
                    // Recupera Input estratti in base al processo
                    ArrayList<ItemBean> listaInput = retrieveInputs(user, idP, liv, codeSur, db);
                    // Recupera Fasi estratte in base al processo
                    ArrayList<ActivityBean> listaFasi = retrieveActivities(user, idP, liv, codeSur, db);
                    // Recupera Output estratti in base al processo
                    ArrayList<ItemBean> listaOutput = retrieveOutputs(user, idP, liv, codeSur, db);
                    // Recupera Rischi estratti in base al processo
                    ArrayList<RiskBean> listaRischi = retrieveRisks(user, idP, liv, codeSur, db);
                    // Istanzia generica tabella in cui devono essere settate le liste di items afferenti al processo
                    processElements = new HashMap<>();
                    // Imposta nella tabella le liste trovate
                    processElements.put(TIPI_LISTE[0], listaInput);
                    processElements.put(TIPI_LISTE[1], listaFasi);
                    processElements.put(TIPI_LISTE[2], listaOutput);
                    processElements.put(TIPI_LISTE[3], listaRischi);
                    // Ha bisogno di personalizzare le breadcrumbs
                    LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                    bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_4, "Processo");
                    // Imposta la jsp
                    fileJspT = nomeFile.get(part);
                } else {
                    // Viene richiesta la visualizzazione di un elenco di macroprocessi
                    macrosat = retrieveMacroAtBySurvey(user, codeSur, db);
                    // Genera il file json contenente le informazioni strutturate
                    printJson(req, macrosat, nomeFileJson, options);
                    // Imposta la jsp
                    fileJspT = nomeFileElenco;
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
        // Imposta nella request elenco completo input di processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[0]).isEmpty()) {
            req.setAttribute("listaInput", processElements.get(TIPI_LISTE[0]));
        }
        // Imposta nella request elenco completo fasi di processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[1]).isEmpty()) {
            req.setAttribute("listaFasi", processElements.get(TIPI_LISTE[1]));
        }
        // Imposta nella request elenco completo output di processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[2]).isEmpty()) {
            req.setAttribute("listaOutput", processElements.get(TIPI_LISTE[2]));
        }
        // Imposta nella request elenco completo rischi di processo, se presenti
        if (processElements != null && !processElements.get(TIPI_LISTE[3]).isEmpty()) {
            req.setAttribute("listaRischi", processElements.get(TIPI_LISTE[3]));
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
     * <p>Restituisce un ArrayList (albero, vista gerarchica) 
     * di tutti macroprocessi censiti dall'anticorruzione 
     * trovati in base a una rilevazione il cui identificativo 
     * viene passato come argomento; ogni macroprocesso contiene 
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
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - lista di input recuperati
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
            risks = db.getRiskByProcess(user, p, survey);
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
     * <p>Restituisce un ArrayList di tutti processi/macroprocessi trovati in base a
     * una struttura il cui identificativo viene passato come argomento,
     * nel contesto di una rilevazione, il cui codice viene passato come argomento.</p>
     * <p>Siccome vengono estratti i macroprocessi collegati non solo alla struttura
     * ma anche a tutte le sue eventuali sottostrutture, viene passato anche il livello
     * della struttura stessa, per poter effettuare ricerche ramificate.</p>
     *
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param idS           identificativo della struttura le cui persone sono allocate sul macroprocesso
     * @param level         livello della struttura, per identificare la query
     * @param codeSurvey    codice della rilevazione
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - lista di macroprocessi recuperati
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     * @see it.rol.DBWrapper#getMacroByStruct(it.rol.bean.PersonBean, int, byte, it.rol.bean.CodeBean)
     */
    private static ArrayList<ProcessBean> retrieveMacroByStruct(PersonBean user,
                                                                int idS,
                                                                byte level,
                                                                String codeSurvey,
                                                                DBWrapper db)
                                                         throws CommandException {
        ArrayList<ProcessBean> mP = null;
        CodeBean survey = ConfigManager.getSurvey(codeSurvey);
        try {
            // Estrae i macroprocessi allocati su una struttura di dato id in una data rilevazione
            mP = db.getMacroByStruct(user, idS, level, survey);
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero dei macro/processi in base alla struttura.\n";
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
        return mP;
    }


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
     * <p>Restituisce:<dl>
     * <dt>'true'</dt> <dd>se almeno uno dei dipartimenti, il cui identificativo
     * viene usato come chiave di una mappa sincronizzata passata come
     * argomento, ha processi associati per l'utente loggato.</dd>
     * <dt>'False'</dt>
     * <dd>se neanche un dipartimento di cui sopra ha almeno
     * un processo associato per l'utente loggato.</dd></dl></p>
     * <p>Attenzione: 'false' di questo metodo non significa che nessun
     * dipartimento ha alcun processo, cio&egrave; che non esistono processi,
     * ma semplicemente che l'utente che si &egrave; loggato (in base al
     * quale la mappa sincronizzata viene costruita) non ha diritto di vederne
     * alcuno.</p>
     * <p>Pi&uacute; in generale, questo metodo potrebbe essere usato per
     * verificare se in una mappa esiste almeno un valore significativo
     * (quantunque le chiavi esistano, ed &egrave; per questo che c'&egrave;
     * bisogno di un metodo di calcolo, in quanto il test JSTL fatto nella
     * pagina JSP con ${not empty map} non funziona: perch&eacute; in effetti
     * la map non &egrave; empty!).</p>
     *
     * @param map una mappa sincronizzata contenente i processi indicizzati per Wrapper di identificativo dipartimentale
     * @return <code>boolean</code> - true se e' stato estratto almeno un processo in uno dei possibili dipartimenti, false altrimenti
     * @throws CommandException se si verifica un problema nello scorrimento di liste o in qualche tipo di puntamento
     */
    private static boolean decant(ConcurrentHashMap<Integer, Vector<ProcessBean>> map)
                           throws CommandException {
        try {
            Iterator<Map.Entry<Integer, Vector<ProcessBean>>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Vector<ProcessBean>> entry = it.next();
                if (entry.getValue().size() > 0) {
                    return true;
                }
            }
            return false;
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
            String msg = FOR_NAME + "Si e\' verificato un problema nel calcolo di un boolean da un Dictionary.\n" + e.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, e);
        }
    }
    

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
            /* **************************************************************** *
             *     Controlla che la directory esista; se non esiste, la crea   *
             * **************************************************************** */
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
            /* ************************************************************ *
             *                    Genera e scrive il json                   *
             * ************************************************************ */
            String givenName = filename;
            LOG.info(FOR_NAME + "Nome stabilito per il file: " + givenName);
            String extension = DOT + JSON;
            // Crea il FileWriter (il nome della variabile è quello standard del PrintWriter, finesse...)
            out = new FileWriter(jsonDir + File.separator + givenName + extension);
            // Json begins
            StringBuilder json = new StringBuilder("[");
            // First Node: the principal
            json.append(Data.getStructureJsonNode(COMMAND_PROCESS, "1000", null, "Processi Anticorruttivi", VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING, null, PRO_PFX, NOTHING)).append(",\n");
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
