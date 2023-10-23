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
 *   Copyright (C) 2022 renewed 2023 Giovanroberto Torre
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
import it.rol.bean.InterviewBean;
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
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
     * Pagina a cui la command fa riferimento per mostrare il report tabellare con processi e rischi
     */
    private static final String nomeFileProcessi = "/jsp/muProcessi.jsp";
    /**
    * Pagina a cui la command fa riferimento per mostrare il report tabellare con processi, strutture e giudizio sintetico
     */
    private static final String nomeFileStrutture = "/jsp/muStrutture.jsp";
    /**
     * Pagina a cui la command fa riferimento per mostrare la form di ricerca
     */
    private static final String nomeFileSearch = "/jsp/muRicerca.jsp";
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
        nomeFile.put(PART_SEARCH,     nomeFileSearch);
        nomeFile.put(PART_PROCESS,    nomeFileProcessi);
        nomeFile.put(PART_SELECT_STR, nomeFileStrutture);
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
        HashMap<String, LinkedHashMap<String, String>> params = null;
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
                    if (part.equalsIgnoreCase(PART_PROCESS)) {
                        /* ************************************************ *
                         *           Generate report process-risks          *
                         * ************************************************ */
                        ArrayList<ProcessBean> matsWithoutIndicators = ProcessCommand.retrieveMacroAtBySurvey(user, codeSur, db);
                        // Controlla se deve forzare l'aggiornamento dei valori degli indicatori
                        if (mess.equals("refresh_ce")) {
                            ArrayList<ProcessBean> mats = computeIndicators(matsWithoutIndicators, user, codeSur, db);
                            refreshIndicators(mats, user, codeSur, db);
                        }
                        matsWithIndicators = retrieveIndicators(matsWithoutIndicators, user, codeSur, db);
                    }
                    else if (part.equalsIgnoreCase(PART_SELECT_STR)) {
                        /* ************************************************ *
                         *   Generate report structures-PxI (tabella MDM)   *
                         * ************************************************ */
                    }
                    // Imposta il valore della pagina di ricerca
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
        // Imposta nella request le chiavi di ricerca, se presenti
        if (params != null) {
            req.setAttribute("tokens", params);
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
     * <p>Ricevuta una ArrayList (albero, vista gerarchica)
     * di tutti macroprocessi censiti dall'anticorruzione 
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
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - lista di macroprocessi contenenti i relativi figli e, questi, gli indicatori di rischio
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<ProcessBean> retrieveIndicators(final ArrayList<ProcessBean> mats,
                                                            PersonBean user,
                                                            String codeSurvey,
                                                            DBWrapper db)
                                                     throws CommandException {
        try {
            ArrayList<ProcessBean> macro = db.getIndicatorValues(user, mats, ConfigManager.getSurvey(codeSurvey));
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
            // Elimina i dati eventualmente già presenti in tabella
            db.deleteIndicatorProcessResults(user);
            // Inserisce i valori degli indicatori ricevuti tramite il parametro
            db.insertIndicatorProcess(user, mats, ConfigManager.getSurvey(codeSurvey));
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
