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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
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
     * Pagina a cui la command fa riferimento per mostrare la form di ricerca
     */
    private static final String nomeFileSearch = "/jsp/muRicerca.jsp";
    /**
     * Struttura contenente le pagine a cui la Command fa riferimento
     */
    private static final HashMap<String, String> nomeFile = new HashMap<>();
    /**
     * DataBound.
     *
    private static DBWrapper db;
    /**
     *  Elenco di macroprocessi anticorruttivi collegati alla rilevazione
     */
    ConcurrentHashMap<String, ArrayList<ProcessBean>> macroBySurvey = new ConcurrentHashMap<>();


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
        /* Fa il caching in memoria degli indicatori
        try {
            // Genera un utente con privilegi assoluti
            PersonBean user = new PersonBean();
            // Attiva la connessione al database
            db = new DBWrapper();
            // Recupera tutte le rilevazioni
            ArrayList<CodeBean> surveys = db.getSurveys(Query.GET_ALL_BY_CLAUSE, Query.GET_ALL_BY_CLAUSE);
            // Per ogni rilevazione
            for (CodeBean s : surveys) {
                // Calcola i macroprocessi della rilevazione corrente
                ArrayList<ProcessBean> macro = ProcessCommand.retrieveMacroAtBySurvey(user, s.getNome(), db);
                // Calcola i valori degli indicatori di rischio 
                ArrayList<ProcessBean> mats = retrieveIndicatorsByMacroAt(macro, user, s.getNome(), db);
                // Carica in un'unica tabella i macroprocessi indicizzati per codice rilevazione
                macroBySurvey.put(s.getNome(), mats);
            }
        }
        catch (WebStorageException wse) {
            String msg = FOR_NAME + "Non e\' possibile avere una connessione al database.\n" + wse.getMessage();
            throw new CommandException(msg, wse);
        }
        catch (Exception e) {
            String msg = FOR_NAME + "Problemi nel caricare gli stati.\n" + e.getMessage();
            throw new CommandException(msg, e);
        }*/
        // Carica la hashmap contenente le pagine da includere in funzione dei parametri sulla querystring
        nomeFile.put(PART_SEARCH,     nomeFileSearch);
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
        // Recupera o inizializza 'id persona'
        int idPe = parser.getIntParameter("idp", DEFAULT_ID);
        // Recupera o inizializza 'tipo pagina'
        String part = parser.getStringParameter("p", DASH);
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Dichiara elenco di macroprocessi anticorruttivi collegati alla rilevazione
        AbstractList<ProcessBean> mats = null;
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
                // Il parametro di navigazione 'p' permette di addentrarsi nelle funzioni
                if (nomeFile.containsKey(part)) {
                    // Imposta il valore della pagina di ricerca
                    fileJspT = nomeFile.get(part);
                } else {
                /* *********************************************************** *
                 *  Viene richiesta la visualizzazione della pagina di report  *
                 * *********************************************************** */
                    ArrayList<ProcessBean> macro = ProcessCommand.retrieveMacroAtBySurvey(user, codeSur, db);
                    mats = retrieveIndicatorsByMacroAt(macro, user, codeSur, db);
                    //pats = ProcessCommand.retrieveProcessAtBySurvey(user, codeSur, db);
                    /*ArrayList<CodeBean> surveys = db.getSurveys(Query.GET_ALL_BY_CLAUSE, Query.GET_ALL_BY_CLAUSE);
                    // Per ogni rilevazione
                    for (CodeBean s : surveys) {
                        // Calcola i macroprocessi della rilevazione corrente
                        ArrayList<ProcessBean> macro = ProcessCommand.retrieveMacroAtBySurvey(user, s.getNome(), db);
                        // Calcola i valori degli indicatori di rischio 
                        ArrayList<ProcessBean> mats2 = retrieveIndicatorsByMacroAt(macro, user, s.getNome(), db);
                        // Carica in un'unica tabella i macroprocessi indicizzati per codice rilevazione
                        macroBySurvey.put(s.getNome(), mats2);
                    }
                    
                    mats = macroBySurvey.get(codeSur);
                    */
                    fileJspT = nomeFileElenco;
                }
            } else {    // manca il codice rilevazione
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
        // Imposta in request, se ci sono, lista di processi anticorruttivi
        if (mats != null) {
            req.setAttribute("macroProcessi", mats);
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
     *                  Metodi di recupero dei dati                     *                     
     *                            (retrieve)                            *
     * **************************************************************** */
    
    /**
     * <p>Restituisce un ArrayList (albero, vista gerarchica) 
     * di tutti macroprocessi censiti dall'anticorruzione 
     * trovati in base a una rilevazione il cui identificativo 
     * viene accettato come argomento; ogni macroprocesso contiene 
     * internamente i suoi processi e questi gli indicatori di rischio.</p>
     *
     * @param codeSurvey    il codice della rilevazione
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - lista di macroprocessi recuperati
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<ProcessBean> retrieveIndicatorsByMacroAt(ArrayList<ProcessBean> mats,
                                                                     PersonBean user,
                                                                     String codeSurvey,
                                                                     DBWrapper db)
                                                              throws CommandException {
        // Fa una copia del parametro altrimenti lo si modificherebbe (malissimo!)
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
                    HashMap<String, InterviewBean> indicators = AuditCommand.compare(listaInterviste);
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


}
