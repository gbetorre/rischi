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
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.DBWrapper;
import it.rol.Main;
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
 * <p><code>ReportCommand.java</code><br>
 * Implementa la logica per la generazione di informazioni aggregate
 * inerenti la mappatura dei rischi corruttivi cui possono essere esposti
 * i processi organizzativi (ROL).</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class ReportCommand extends ItemBean implements Command, Constants {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = 4885941293740116980L;
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
     * Pagina a cui la command reindirizza per mostrare la pagina iniziale della Command
     */
    private static final String nomeFileElenco = "/jsp/muElenco.jsp";
    /**
     * Pagina a cui la command fa riferimento per mostrare il report con processi e PxI
     */
    private static final String nomeFileProcessi = "/jsp/muProcessi.jsp";
    /**
    * Pagina a cui la command fa riferimento per mostrare il report tabellare con processi, strutture e giudizio sintetico
     */
    private static final String nomeFileStrutture = "/jsp/muStrutture.jsp";
    /**
     * Pagina a cui la command fa riferimento per mostrare il report tabellare con rischi e misure
     */
    private static final String nomeFileRischi = "/jsp/muRischi.jsp";
    /**
     * Pagina a cui la command fa riferimento per mostrare la form di ricerca
     */
    private static final String nomeFileSearch = "/jsp/muRicerca.jsp";
    /**
     * Pagina a cui la command fa riferimento per mostrare la  home dei grafici
     */
    private static final String nomeFileGraphics = "/jsp/muGrafici.jsp";
    /**
     * Struttura contenente le pagine a cui la Command fa riferimento
     */
    private static final HashMap<String, String> nomeFile = new HashMap<>();


    /**
     * Crea una nuova istanza di questa Command
     */
    public ReportCommand() {
        /*;*/   // It Doesn't Anything
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
        nomeFile.put(PART_SEARCH,       nomeFileSearch);
        nomeFile.put(PART_PROCESS,      nomeFileProcessi);
        nomeFile.put(PART_SELECT_STR,   nomeFileStrutture);
        nomeFile.put(PART_RISKS,        nomeFileRischi);
        nomeFile.put(PART_GRAPHICS,     nomeFileGraphics);
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
        String codeSur = parser.getStringParameter(PARAM_SURVEY, DASH);
        // Recupera o inizializza 'tipo pagina'
        String part = parser.getStringParameter("p", DASH);
        // Recupera o inizializza eventuale parametro messaggio
        String mess = parser.getStringParameter("msg", DASH);
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Dichiara elenco di macroprocessi anticorruttivi collegati alla rilevazione
        ArrayList<ProcessBean> matsWithIndicators = null;
        // Dichiara mappa di parametri di ricerca
        //HashMap<String, LinkedHashMap<String, String>> params = null;
        // Dichiara mappa di strutture indicizzate per id processo_at
        HashMap<Integer, ArrayList<DepartmentBean>> structs = null;
        // Dichiara mappa di soggetti contingenti indicizzati per id processo_at
        HashMap<Integer, ArrayList<DepartmentBean>> subjects = null;
        // Dichiara mappa ordinata di rischi indicizzati per processo_at
        LinkedHashMap<ProcessBean, ArrayList<RiskBean>> risks = null;
        // Preprara BreadCrumbs
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
         *                   Decide il valore della pagina                      *
         * ******************************************************************** */
        try {
            // Il parametro di navigazione 'rilevazione' è obbligatorio
            if (!codeSur.equals(DASH)) {
                /* @GetMapping */
                if (nomeFile.containsKey(part)) {
                    // Qualunque report si voglia generare, bisogna recuperare processi e indicatori
                    ArrayList<ProcessBean> matsWithoutIndicators = ProcessCommand.retrieveMacroAtBySurvey(user, codeSur, db);
                    // Controlla se deve forzare l'aggiornamento dei valori degli indicatori
                    if (mess.equals("refresh_ce")) {
                        ArrayList<ProcessBean> mats = computeIndicators(matsWithoutIndicators, user, codeSur, db);
                        refreshIndicators(mats, user, codeSur, db);
                    }
                    matsWithIndicators = retrieveIndicators(matsWithoutIndicators, user, codeSur, NOTHING, db);
                    if (part.equalsIgnoreCase(PART_PROCESS)) {
                        /* ************************************************ *
                         *           Generate report process-risks          *
                         * ************************************************ */
                        // Ha bisogno di personalizzare le breadcrumbs
                        LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                        bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_1, "Report PxI");
                    }
                    else if (part.equalsIgnoreCase(PART_SELECT_STR)) {
                        /* ************************************************ *
                         *   Generate report structures-PxI (tabella MDM)   *
                         * ************************************************ */
                        // Recupera le strutture indicizzate per identificativo di processo
                        structs = retrieveStructures(matsWithoutIndicators, user, codeSur, db);
                        // Recupera i soggetti indicizzati per identificativo di processo
                        subjects = retrieveSubjects(matsWithoutIndicators, user, codeSur, db);
                        // Recupera i rischi indicizzati per identificativo di processo
                        risks = retrieveRisksByProcess(matsWithoutIndicators, user, codeSur, db);
                        // Ha bisogno di personalizzare le breadcrumbs
                        LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                        bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_1, "Report strutture");
                    } else if (part.equalsIgnoreCase(PART_RISKS)) {
                        /* ************************************************ *
                         *           Generate report risks-measure          *
                         * ************************************************ */
                        // Recupera i rischi indicizzati per identificativo di processo
                        //risks = retrieveMitigatedRisksByProcess(matsWithoutIndicators, user, codeSur, db);
                        risks = retrieveRisksByProcess(matsWithoutIndicators, user, codeSur, db);
                        // Ha bisogno di personalizzare le breadcrumbs
                        LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                        bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_1, "Report misure");
                    }
                    // Imposta il valore della pagina abbinata al parametro 
                    fileJspT = nomeFile.get(part);
                } else {
                /* *********************************************************** *
                 *  Viene richiesta la visualizzazione della pagina di report  *
                 * *********************************************************** */
                    fileJspT = nomeFileElenco;
                }
            } else {    // Manca il codice rilevazione
                String msg = FOR_NAME + "Impossibile recuperare il codice della rilevazione.\n";
                LOG.severe(msg + "Qualcuno ha probabilmente alterato il codice rilevazione nell\'URI della pagina.\n");
                throw new CommandException(msg);
            }
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
        // Imposta in request, se ci sono, lista di macroprocessi con figli e indicatori
        if (matsWithIndicators != null) {
            req.setAttribute("macroProcessi", matsWithIndicators);
        }
        // Imposta in request, se ci sono, lista di strutture indicizzate per id processo
        if (structs != null) {
            req.setAttribute("strutture", structs);
        }
        // Imposta in request, se ci sono, lista di soggetti indicizzati per id processo
        if (subjects != null) {
            req.setAttribute("soggetti", subjects);
        }
        // Imposta in request, se ci sono, lista di rischi indicizzati per processo
        if (risks != null) {
            req.setAttribute("rischi", risks);
        }
        // Imposta nella request breadcrumbs in caso siano state personalizzate
        if (bC != null) {
            req.removeAttribute("breadCrumbs");
            req.setAttribute("breadCrumbs", bC);
        }
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }
    
    
    /* **************************************************************** *
     *               Metodi di recupero semplice dei dati               *                     
     *   o di recupero e di calcolo effettuato in base ai dati stessi   *
     * **************************************************************** */
    
    /**
     * <p>Seleziona da database i valori degli indicatori di rischio tramite una
     * estrazione dati.</p>
     * <p>Ricevuta una ArrayList (albero, vista gerarchica)
     * di tutti macroprocessi censiti a fini anticorruttivi
     * trovati in base a una rilevazione il cui identificativo 
     * viene accettato come argomento, ma i cui figli non contengono
     * i valori degli indicatori di rischio totalizzati, 
     * restituisce una ArrayList di macroprocessi dove ogni macroprocesso contiene 
     * internamente i suoi processi e questi gli indicatori di rischio
     * i cui valori vengono recuperati da database, ove sono stati memorizzati 
     * in base a un pre-calcolo (caching).</p>
     * 
     * @param mats          struttura contenente tutti i macroprocessi - e relativi processi figli - privi di indicatori di rischio 
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param codeSurvey    il codice della rilevazione
     * @param muffler       un intero da sottrarre ai valori effettivi degli indicatori (per non alterare i valori, impostare questo parametro pari a 0)
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - lista di macroprocessi contenenti i relativi figli e, questi, gli indicatori di rischio
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<ProcessBean> retrieveIndicators(final ArrayList<ProcessBean> mats,
                                                            PersonBean user,
                                                            String codeSurvey,
                                                            int muffler,
                                                            DBWrapper db)
                                                     throws CommandException {
        try {
            ArrayList<ProcessBean> macro = db.getIndicatorValues(user, mats, ConfigManager.getSurvey(codeSurvey), muffler);
            return macro;
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nell\'interazione col database (in metodi di lettura).\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (RuntimeException ce) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ce.getMessage(), ce);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }

    
    /**
     * <p>Calcola a runtime i valori degli indicatori di rischio tramite 
     * l'invocazione di un metodo che a sua volta richiama una serie 
     * di altri metodi, ciascuno implementate l'algoritmo di un diverso 
     * indicatore di rischio .</p>
     * <p>Ricevuta una ArrayList (albero, vista gerarchica)
     * di tutti macroprocessi censiti dall'anticorruzione 
     * trovati in base a una rilevazione il cui identificativo 
     * viene accettato come argomento, ma i cui figli non contengono
     * i valori degli indicatori di rischio totalizzati, 
     * restituisce una ArrayList di macroprocessi dove ogni macroprocesso contiene 
     * internamente i suoi processi e questi gli indicatori di rischio,
     * i cui valori vengono calcolati a runtime.</p>
     *
     * @param mats          struttura contenente tutti i macroprocessi - e relativi processi figli - privi di indicatori di rischio 
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param codeSurvey    il codice della rilevazione
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - lista di macroprocessi contenenti i loro processi figli e, questi, i valori degli indicatori di rischio
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<ProcessBean> computeIndicators(final ArrayList<ProcessBean> mats,
                                                           PersonBean user,
                                                           String codeSurvey,
                                                           DBWrapper db)
                                                    throws CommandException {
        // Pleonastica
        ArrayList<ProcessBean> macro = (ArrayList<ProcessBean>) mats.clone();
        // Visibilità a livello di metodo
        ArrayList<ProcessBean> processi = null;
        try {
            // Per ogni macroprocesso
            for (int i = 0; i < mats.size(); i++) {
                // Recupera il macroprocesso
                ProcessBean mat = mats.get(i);
                // Prepara la struttura
                processi = new ArrayList<>();
                // Recupera i suoi processi
                for (int j = 0; j < mat.getProcessi().size(); j++) {
                    // Processo corrente
                    ProcessBean pat = mat.getProcessi().get(j);
                    // Per ogni processo recupera le sue interviste complete di indicatori
                    ArrayList<InterviewBean> listaInterviste = ProcessCommand.retrieveInterviews(user, pat.getId(), pat.getLivello(), codeSurvey, db);
                    // Applica l'algoritmo decisionale in caso vi siano più valori per lo stesso indicatore
                    LinkedHashMap<String, InterviewBean> indicators = AuditCommand.compare(listaInterviste);
                    // Setta gli indicatori nel processo corrente
                    pat.setIndicatori(indicators);
                    // Aggiunge il processo completo di indicatori
                    processi.add(pat);
                }
                // Aggiorna il macroprocesso
                mat.setProcessi(processi);
                // Aggiorna l'elenco
                macro.set(i, mat);
                // Tentativo di velocizzare la liberazione della memoria sull'ultimo ciclo
                processi = null;
            }
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di attributi obbligatori.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + ce.getMessage(), ce);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return macro;
    }
    
    
    /**
     * <p>Ricevuta una ArrayList (albero, vista gerarchica)
     * di tutti macroprocessi censiti a fini anticorruttivi
     * nel contesto di una rilevazione, i cui figli non contengono
     * al proprio interno le fasi (e tantomeno, queste, le strutture 
     * che le erogano), restituisce una mappa di strutture associate a ciascun
     * processo tramite le sue fasi, indicizzata per identificativo di processo,
     * incapsulato in un Wrapper di tipo primitivo.</p>
     * 
     * @param mats          struttura contenente tutti i macroprocessi - e relativi processi figli - privi di strutture associate
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param codeSurvey    il codice della rilevazione
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>HashMap&lt;Integer, ArrayList&lt;DepartmentBean&gt;&gt;</code> - lista di strutture collegate al processo_at il cui identificativo e' incapsulato in chiave
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static HashMap<Integer, ArrayList<DepartmentBean>> retrieveStructures(final ArrayList<ProcessBean> mats,
                                                                                 PersonBean user,
                                                                                 String codeSurvey,
                                                                                 DBWrapper db)
                                                                          throws CommandException {
        try {
            return db.getStructures(user, mats, ConfigManager.getSurvey(codeSurvey));            
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nell\'interazione col database (in metodi di lettura).\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (RuntimeException ce) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ce.getMessage(), ce);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }


    /**
     * <p>Ricevuta una ArrayList (albero, vista gerarchica)
     * di tutti macroprocessi censiti a fini anticorruttivi 
     * nel contesto di una rilevazione, i cui figli non contengono
     * al proprio interno le fasi (e tantomeno, queste, i soggetti contingenti 
     * che le sovrintendono), restituisce una mappa di soggetti associati 
     * a ciascun processo figlio (quindi processo di livello 2, no macro no sub)
     * tramite le sue fasi; la mappa (Dictionary) &egrave; indicizzata 
     * per identificativo di processo at, incapsulato 
     * in un Wrapper di tipo primitivo.</p>
     * 
     * @param mats          struttura contenente tutti i macroprocessi - e relativi processi figli - privi di strutture associate
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param codeSurvey    il codice della rilevazione
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>HashMap&lt;Integer, ArrayList&lt;DepartmentBean&gt;&gt;</code> - lista di soggetti collegati al processo_at il cui identificativo e' incapsulato in chiave
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static HashMap<Integer, ArrayList<DepartmentBean>> retrieveSubjects(final ArrayList<ProcessBean> mats,
                                                                               PersonBean user,
                                                                               String codeSurvey,
                                                                               DBWrapper db)
                                                                        throws CommandException {
        try {
            return db.getSubjects(user, mats, ConfigManager.getSurvey(codeSurvey));            
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nell\'interazione col database (in metodi di lettura).\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (RuntimeException ce) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ce.getMessage(), ce);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
   /**
    * <p>Ricevuta una ArrayList (albero, vista gerarchica)
    * di tutti macroprocessi censiti a fini anticorruttivi
    * trovati in base a una rilevazione il cui identificativo 
    * viene accettato come argomento, ma i cui figli non contengono
    * al proprio interno i rischi, restituisce una mappa di rischi 
    * associati a ciascun processo, indicizzata per identificativo di processo,
    * incapsulato in un Wrapper di tipo primitivo.</p>
    * 
    * @param mats          struttura contenente tutti i macroprocessi - e relativi processi figli - privi di strutture associate
    * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
    * @param codeSurvey    il codice della rilevazione
    * @param db            WebStorage per l'accesso ai dati
    * @return <code>HashMap&lt;Integer, ArrayList&lt;DepartmentBean&gt;&gt;</code> - lista di strutture collegate al processo_at il cui identificativo e' incapsulato in chiave
    * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
    */
    public static HashMap<Integer, ArrayList<RiskBean>> retrieveRisks(final ArrayList<ProcessBean> mats,
                                                                      PersonBean user,
                                                                      String codeSurvey,
                                                                      DBWrapper db)
                                                               throws CommandException {
        ArrayList<RiskBean> risks = null;
        HashMap<Integer, ArrayList<RiskBean>> risksByPat = new HashMap<>();
        try {
            // Prepara l'oggetto rilevazione
            CodeBean survey = ConfigManager.getSurvey(codeSurvey);
            // Per ogni macroprocesso
            for (ProcessBean mat : mats) {
                // Recupera i suoi processi
                for (ProcessBean pat : mat.getProcessi()) {
                    // Estrae i rischi di un dato processo in una data rilevazione
                    risks = db.getRisksByProcess(user, pat, survey);
                    // Setta nella mappa la lista appena calcolata
                    risksByPat.put(new Integer(pat.getId()), risks);
                }
            }
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
        return risksByPat;
    }
    
    
    /**
     * <p>Ricevuta una ArrayList (albero, vista gerarchica)
     * di tutti macroprocessi censiti a fini anticorruttivi
     * trovati in base a una rilevazione il cui identificativo 
     * viene accettato come argomento, ma i cui figli non contengono
     * al proprio interno i rischi, restituisce una mappa di rischi 
     * associati a ciascun processo, indicizzata per il processo stesso.</p>
     * <p><strong>Attenzione:</strong> nel momento dell'implementazione
     * di questo metodo, non risultano processi con nomi duplicati, 
     * per&ograve; in realt&agrave; i processi potrebbero avere nomi uguali 
     * persino all'interno della stessa rilevazione (non c'&egrave;
     * nessun vincolo sull'unicit&agrave; del nome del processo nella relativa
     * tabella del db), per cui si &egrave; ritenuto opportuno gestire 
     * questa potenziale collisione effettuando l'override 
     * dei metodi di comparazione nel bean del processo e poi utilizzare 
     * direttamente il processo stesso come chiave. 
     * Si pu&ograve; valutare, in seguito, di sostituire completamente 
     * con il presente metodo con l'altro analogo, che imposta come 
     * chiave l'identificativo del processo 
     * (quello s&iacute; certamente univoco).</p>
     * 
     * @param mats          struttura contenente tutti i macroprocessi - e relativi processi figli - privi di strutture associate
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param codeSurvey    il codice della rilevazione
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>HashMap&lt;ProcessBean, ArrayList&lt;RiskBean&gt;&gt;</code> - lista di rischi collegati al processo_at incapsulato in chiave
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
     public static LinkedHashMap<ProcessBean, ArrayList<RiskBean>> retrieveRisksByProcess(final ArrayList<ProcessBean> mats,
                                                                                          PersonBean user,
                                                                                          String codeSurvey,
                                                                                          DBWrapper db)
                                                                                   throws CommandException {
         ArrayList<RiskBean> risks, mitigatingRisks = null;
         LinkedHashMap<ProcessBean, ArrayList<RiskBean>> risksByPat = new LinkedHashMap<>();
         try {
             // Prepara l'oggetto rilevazione
             CodeBean survey = ConfigManager.getSurvey(codeSurvey);
             // Per ogni macroprocesso
             for (ProcessBean mat : mats) {
                 // Recupera i suoi processi
                 for (ProcessBean pat : mat.getProcessi()) {
                     // Estrae i rischi di un dato processo in una data rilevazione
                     risks = db.getRisksByProcess(user, pat, survey);
                     // Setta nella mappa la lista appena calcolata
                     risksByPat.put(pat, risks);
                 }
             }
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
         return risksByPat;
     }
     
     
     /**
      * // TODO COMMENTO ^v
      * 
      * @param mats          struttura contenente tutti i macroprocessi - e relativi processi figli - privi di strutture associate
      * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
      * @param codeSurvey    il codice della rilevazione
      * @param db            WebStorage per l'accesso ai dati
      * @return <code>HashMap&lt;ProcessBean, ArrayList&lt;RiskBean&gt;&gt;</code> - lista di rischi collegati al processo_at incapsulato in chiave
      * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
      */
      public static LinkedHashMap<ProcessBean, ArrayList<RiskBean>> retrieveMitigatedRisksByProcess(final ArrayList<ProcessBean> mats,
                                                                                           PersonBean user,
                                                                                           String codeSurvey,
                                                                                           DBWrapper db)
                                                                                    throws CommandException {
          ArrayList<RiskBean> risks, mitigatingRisks = null;
          LinkedHashMap<ProcessBean, ArrayList<RiskBean>> risksByPat = new LinkedHashMap<>();
          try {
              // Prepara l'oggetto rilevazione
              CodeBean survey = ConfigManager.getSurvey(codeSurvey);
              // Per ogni macroprocesso
              for (ProcessBean mat : mats) {
                  // Recupera i suoi processi
                  for (ProcessBean pat : mat.getProcessi()) {
                      // Estrae i rischi di un dato processo in una data rilevazione
                      risks = db.getRisksByProcess(user, pat, survey);
                      // Resetta il vettore dei rischi mitigati
                      mitigatingRisks = new ArrayList<RiskBean>();
                      // Per ogni rischio
                      for (RiskBean risk : risks) {
                          if (risk.getMisure() != null) {
                              InterviewBean mitigatedPI = MeasureCommand.mitigate(pat.getIndicatori().get(PI), (ArrayList<MeasureBean>) risk.getMisure());
                              risk.setLivello(mitigatedPI.getInformativa());
                          } else {
                              risk.setLivello(pat.getIndicatori().get(PI).getInformativa());
                          }
                          mitigatingRisks.add(risk);
                      }
                      // Setta nella mappa la lista appena calcolata
                      risksByPat.put(pat, mitigatingRisks);
                  }
              }
          } catch (WebStorageException wse) {
              String msg = FOR_NAME + "Si e\' verificato un problema nel recupero dei rischi calmierati.\n";
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
          return risksByPat;
      }

    
    /* **************************************************************** *
     *           Metodi di riscrittura (aggiornamento) dei dati         *                     
     *                            (refresh)                             *
     * **************************************************************** */
    
    /**
     * <p>Ricevuta una ArrayList (albero, vista gerarchica)
     * di tutti macroprocessi censiti dall'anticorruzione 
     * trovati in base a una rilevazione il cui identificativo 
     * viene accettato come argomento ed i cui figli gi&agrave; contengono
     * i valori degli indicatori di rischio calcolati, 
     * cancella tutti i valori degli indicatori di rischio
     * relativi a tutti i processi censiti dall'anticorruzione eventualmente
     * presenti nella tabella dei risultati e inserisce nella stessa tabella
     * i nuovi valori ricevuti tramite il parametro.</p>
     * <p>Se si verifica un problema nell'inserimento, il risultato
     * sar&agrave; una cancellazione, perch&eacute; le due operazioni 
     * di scrittura non sono in transazione; ci&ograve; &egrave; voluto 
     * in quanto, anche se si eliminano i valori e non se ne inseriscono 
     * di nuovi, si stanno solo eliminando valori di caching pre-calcolati e, 
     * by default, il software non effettua questa operazione di ricalcolo, 
     * ma mostra i valori calcolati in precedenza.<br />
     * Eventualmente, in futuro si potrebbe valutare di rendere atomiche
     * la cancellazione e il successivo inserimento; ci&ograve; pu&ograve;
     * essere fatto in diversi modi:<ol>
     * <li>effettuare prima un test sul buon esito dell'inserimento 
     * tramite un accodamento delle nuove tuple e poi di effettuare 
     * la cancellazione totale e, infine, il nuovo inserimento;</li>
     * <li>procedere tramite un aggiornamento delle righe modificate piuttosto
     * che tramite una cancellazione non parametrica (la DELETE non parametrica,
     * usata attualmente nel metodo di eliminazione, va sulla tabella, 
     * non sulla riga);</li>
     * <li>mettere nella stessa transazione (nel DBWrapper) prima 
     * la cancellazione e poi l'inserimento</li></ol>
     * Probabilmente la soluzione più efficiente e semplice &egrave; l'ultima.</p>
     *
     * @param mats          struttura contenente tutti i macroprocessi - e relativi processi figli - corredati di indicatori di rischio valorizzati
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param codeSurvey    il codice della rilevazione
     * @param db            WebStorage per l'accesso ai dati
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static void refreshIndicators(final ArrayList<ProcessBean> mats,
                                         PersonBean user,
                                         String codeSurvey,
                                         DBWrapper db)
                                  throws CommandException {
        try {
            // Seleziona tutte le note dei giudizi sintetici e le conserva in memoria
            LinkedHashMap<Integer, ItemBean> notes = db.getIndicatorNotes(user, ConfigManager.getSurvey(codeSurvey));
            // Elimina i dati eventualmente già presenti in tabella
            db.deleteIndicatorProcessResults(user, notes);
            // Inserisce i valori degli indicatori ricevuti tramite il parametro
            db.insertIndicatorProcess(user, mats, notes, ConfigManager.getSurvey(codeSurvey));
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nell\'interazione col database (in metodi di scrittura).\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (RuntimeException re) {
            String msg = FOR_NAME + "Si e\' verificato un problema in un puntamento.\n";
            LOG.severe(msg);
            throw new CommandException(msg + re.getMessage(), re);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }

}
