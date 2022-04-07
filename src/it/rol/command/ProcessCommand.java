/*
 *   Process Mapping Software: Modulo Applicazione web per la visualizzazione
 *   delle schede di indagine su allocazione risorse dell'ateneo,
 *   per la gestione dei processi on line (pms).
 *
 *   Process Mapping Software (pms)
 *   web applications to publish, and manage,
 *   processes, assessment and skill information.
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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import it.rol.Main;
import it.rol.bean.CodeBean;
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.CommandException;
import it.rol.exception.WebStorageException;


/**
 * <p><code>ProcessCommand.java</code><br />
 * Implementa la logica per la gestione dei processi on line (PROL).</p>
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
     * Pagina a cui la command reindirizza per mostrare i macroprocessi con vista alternativa
     */
    private static final String nomeFileElencoAlt = "/jsp/prElencoDC.jsp";
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutti gli attributi del progetto
     */
    private static final HashMap<String, String> nomeFile = new HashMap<String, String>();
    /**
     *  Processo di dato id
     */
    ProcessBean runtimeProcess = null;


    /**
     * Crea una nuova istanza di ProcessCommand
     */
    public ProcessCommand() {
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
        nomeFile.put(PART_MACROPROCESS, nomeFileElencoAlt);
        //nomeFile.put(Query.PART_PROJECT, this.getPaginaJsp());
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
        int idPr = parser.getIntParameter("id", -1);
        // Recupera o inizializza 'tipo pagina'
        String part = parser.getStringParameter("p", DASH);
        String pr = null;
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Dichiara elenco di processi
        AbstractList<ProcessBean> m = new ArrayList<ProcessBean>();
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
            // Il parametro di navigazione 'rilevazione' Ã¨ obbligatorio
            if (!codeSur.equals(DASH)) {
                // Estrae il set di macroprocessi
                m = retrieveMacroBySurvey(user, codeSur, db);
                // Il parametro di navigazione 'p' permette di addentrarsi nelle funzioni
                if (nomeFile.containsKey(part)) {
                    // Viene richiesta la visualizzazione alternativa dei macroprocessi
                    if (part.equals(PART_MACROPROCESS)) {
                        fileJspT = nomeFile.get(part);
                    }
                } else {
                    // Viene richiesta la visualizzazione di un elenco di macroprocessi
                    m = retrieveMacroBySurvey(user, codeSur, db);
                    // Recupera o inizializza il codice dell'eventuale (macro)processo che verrÃ  passato alla vista
                    pr = parser.getStringParameter("pr", DASH);
                    fileJspT = nomeFileElenco;
                }
            } else {    // Manca il codice rilevazione
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
        // Imposta nella request elenco macroprocessi
        req.setAttribute("macroprocessi", m);
        // Imposta nella request codice del (macro)processo
        if (pr != null)
            req.setAttribute("pr", pr);
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }


    /**
     * <p>Restituisce un ArrayList di tutti processi/macroprocessi trovati in base a
     * una rilevazione il cui identificativo viene passato come argomento.</p>
     *
     * @param codeSurvey    il codice della rilevazione
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - lista di macroprocessi recuperati
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<ProcessBean> retrieveMacroBySurvey(PersonBean user,
                                                               String codeSurvey,
                                                               DBWrapper db)
                                                        throws CommandException {
        ArrayList<ProcessBean> people = null;
        try {
            // Estrae le persone allocate su un dato macroprocesso in una data rilevazione
            people = db.getMacroBySurvey(user, codeSurvey);
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
        return people;
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
    public static ArrayList<ProcessBean> retrieveMacroByStruct(PersonBean user,
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
        LinkedHashMap<Integer, ProcessBean> userProcesses = new LinkedHashMap<Integer, ProcessBean>(7);
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


}
