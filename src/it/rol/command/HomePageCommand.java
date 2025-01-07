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
 *   alla gestione dei processi a rischio e stabilire quantitativamente 
 *   in che grado questa attuazione di misure abbia effettivamente ridotto 
 *   i livelli di rischio.
 *
 *   Risk Mapping and Management Software (ROL-RMS),
 *   web application: 
 *   - to assess the amount and type of corruption risk to which each organizational process is exposed, 
 *   - to publish and manage, reports and information on risk
 *   - and to propose mitigation measures specifically aimed at reducing risk, 
 *   - also allowing monitoring to be carried out to see 
 *   which proposed mitigation measures were then actually implemented 
 *   and quantify how much that implementation of measures actually 
 *   reduced risk levels.
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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.DBManager;
import it.rol.DBWrapper;
import it.rol.Main;
import it.rol.Query;
import it.rol.Utils;
import it.rol.bean.CodeBean;
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.CommandException;
import it.rol.exception.WebStorageException;


/**
 * <p><code>HomePageCommand.java</code><br />
 * Gestisce la root dell'applicazione.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class HomePageCommand extends ItemBean implements Command, Constants {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = -4437906730411178543L;
    /**
     *  Nome di questa classe
     *  (utilizzato per contestualizzare i messaggi di errore)
     */
    /* friendly */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": "; //$NON-NLS-1$
    /* $NON-NLS-1$ silence a warning that Eclipse emits when it encounters
     * string literals.
     * The idea is that UI messages should not be embedded as string literals,
     * but rather sourced from a resource file
     * (so that they can be translated, proofed, etc).*/
    /**
     * Log per debug in produzione
     */
    protected static Logger LOG = Logger.getLogger(Main.class.getName());
    /**
     * Pagina a cui la command reindirizza per mostrare la form di login
     */
    private static final String nomeFileElenco = "/jsp/login.jsp";
    /**
     * Pagina a cui la command reindirizza per mostrare le scelte iniziali
     */
    private static final String nomeFileLanding = "/jsp/landing.jsp";
    /**
     * DataBound.
     */
    private static DBWrapper db;
    /**
     * Nome del database su cui insiste l'applicazione
     */
    private static String dbName = null;
    /**
     * Ultima rilevazione
     */
    private static CodeBean lastSurvey;


    /**
     * Crea una nuova istanza di HomePageCommand
     */
    public HomePageCommand() {
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
        try {
            // Attiva la connessione al database
            db = new DBWrapper();
            // Recupera l'ultima rilevazione
            lastSurvey = db.getSurvey(Query.GET_ALL_BY_CLAUSE, Query.GET_ALL_BY_CLAUSE);
        }
        catch (WebStorageException wse) {
            String msg = FOR_NAME + "Non e\' possibile avere una connessione al database.\n" + wse.getMessage();
            throw new CommandException(msg, wse);
        }
        catch (Exception e) {
            String msg = FOR_NAME + "Problemi nel caricare gli stati.\n" + e.getMessage();
            throw new CommandException(msg, e);
        }
    }


    /**
     * <p>Gestisce il flusso principale.</p>
     * <p>Prepara i bean.</p>
     * <p>Passa nella Request i valori che verranno utilizzati dall'applicazione.</p>
     *
     * @param req HttpServletRequest contenente parametri e attributi, e in cui settare attributi
     * @throws CommandException incapsula qualunque genere di eccezione che si possa verificare in qualunque punto del programma
     */
    @Override
    public void execute(HttpServletRequest req)
                 throws CommandException {
        /* ******************************************************************** *
         *                    Dichiarazioni e inizializzazioni                  *
         * ******************************************************************** */
        // Utente loggato
        PersonBean user = null;
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Dichiara un messaggio di errore
        String error = null;
        /* ******************************************************************** *
         *                 Recupero dei parametri di navigazione                *
         * ******************************************************************** */
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter(PARAM_SURVEY, DASH);
        /* ******************************************************************** *
         *      Instanzia nuova classe WebStorage per il recupero dei dati      *
         * ******************************************************************** */
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new CommandException(FOR_NAME + "Non e\' disponibile un collegamento al database\n." + wse.getMessage(), wse);
        }
        // Non controlla qui se l'utente è già loggato perché questa command deve rispondere anche PRIMA del login
        /* ******************************************************************** *
         *             Rami in cui occorre che l'utente sia loggato             *
         * ******************************************************************** */
        try {
            // Il parametro di navigazione 'rilevazione' è obbligatorio
            if (!codeSur.equals(DASH) && ConfigManager.getSurveys().containsKey(codeSur)) {
                // In questo punto la sessione deve esistere e l'utente deve esserne loggato
                try {
                    user = getLoggedUser(req);
                } catch (CommandException ce) {
                    String msg = "Si e\' tentato di accedere a una funzione senza essere loggati, o a sessione scaduta.\n";
                    LOG.severe("Problema a livello di autenticazione: " + msg + ce.getMessage());
                    throw (ce);
                }
                // Se l'utente è loggato ed esiste il parametro di rilevazione, mostra la pagina di landing
                fileJspT = nomeFileLanding;
            } else {
                if (isLoggedUser(req)) {
                    error = "Funzione non trovata.";
                }
                fileJspT = nomeFileElenco;
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
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema non meglio specificato.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        /* ******************************************************************** *
         *                          Recupera i parametri                        *
         * ******************************************************************** */
        /* Imposta una variabile di applicazione, se non è già stata valorizzata (singleton).
         * Il contenuto in sé della variabile è stato sicuramente creato, altrimenti
         * non sarebbe stato possibile arrivare a questo punto del codice,
         * ma, se questa è la prima richiesta che viene fatta all'applicazione
         * (e siamo quindi in presenza dell'"handicap del primo uomo")
         * non è detto che la variabile stessa sia stata memorizzata a livello
         * di application scope. Ci serve a questo livello per controllare,
         * in tutte le pagine dell'applicazione, che stiamo puntando al db giusto.  
         * ATTENZIONE: crea una variabile di applicazione                       */
        dbName = (String) req.getServletContext().getAttribute("dbName");
        if (dbName == null || dbName.isEmpty()) {
            // Uso la stessa stringa perché, se non valorizzata in application, non sarà mai empty ma sarà null
            dbName = DBManager.getDbName();
            // Attenzione: crea una variabile di APPLICAZIONE
            req.getServletContext().setAttribute("db", dbName);
        }
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
        if (error != null) {
            req.setAttribute("error", true);
            req.setAttribute("msg", error);
        }
    }


    /**
     * <p>Restituisce l'utente loggato, se lo trova nella sessione utente,
     * altrimenti lancia un'eccezione.</p>
     *
     * @param req HttpServletRequest contenente la sessione e i suoi attributi
     * @return <code>PersonBean</code> - l'utente loggatosi correntemente
     * @throws CommandException se si verifica un problema nel recupero della sessione o dei suoi attributi
     */
    public static PersonBean getLoggedUser(HttpServletRequest req)
                                    throws CommandException {
        // Utente loggato
        PersonBean user = null;
        /* ******************************************************************** *
         *                         Recupera la Sessione                         *
         * ******************************************************************** */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            if (ses == null) {
                String msg = FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n";
                LOG.severe(msg + "Sessione non trovata!\n");
                throw new CommandException();
            }
            user = (PersonBean) ses.getAttribute("usr");
            if (user == null) {
                String msg = FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n";
                LOG.severe(msg + "Attributo \'utente\' non trovato in sessione!\n");
                throw new CommandException(msg);
            }
            return user;
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
    }


    /**
     * <p>Restituisce true se l'utente &egrave; loggato e se lo trova nella sessione utente,
     * altrimenti restituisce false.</p>
     *
     * @param req HttpServletRequest contenente la sessione e i suoi attributi
     * @return <code>boolean</code> - flag di utente trovato in sessione
     * @throws CommandException se si verifica un problema di puntamento
     */
    public static boolean isLoggedUser(HttpServletRequest req)
                                throws CommandException {
        // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
        try {
            HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            if (ses == null) {
                return false;
            }
            PersonBean user = (PersonBean) ses.getAttribute("usr");
            if (user == null) {
                return false;
            }
            return true;
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null, probabilmente nel tentativo di recuperare l\'utente.\n";
            LOG.severe(msg);
            throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n" + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }


    /* ************************************************************************ *
     * Metodi di generazione di liste di voci (per MENU,submenu,breadcrumbs...) *
     * ************************************************************************ */

    /**
     * <p>Restituisce una <code>tabella hash</code> contenente i sottomenu
     * di voci di livello superiore, le quali svolgono anche la funzione
     * di chiavi della tabella stessa.</p>
     * <p>La <code>tabella hash</code> ha come chiave un oggetto
     * di tipo <code>ItemBean</code> che rappresenta la voce principale, e come valore una
     * struttura vettoriale ordinata, contenente le voci del sottomenu che alla chiave
     * fa riferimento.<br>
     * Naturalmente, l'oggetto che fa da chiave implementa <code>l'Override</code>
     * dei metodi necessari all'impiego come chiave, appunto, di tabella hash:
     * <pre>equals(), hashCode()</pre> &ndash;
     * oltre, a fare l'Override di altri metodi, utili per gli ordinamenti:
     * <pre>compareTo(), toString()</pre></p>
     *
     * @param appName nome della web application, seguente la root, per la corretta generazione dei link delle voci
     * @param surCode identificativo della rilevazione corrente oppure della rilevazione di default in caso di accesso utente a piu' di una rilevazione
     * @return <code>LinkedHashMap&lt;ItemBean, ArrayList&lt;ItemBean&gt;&gt; - tabella hash contenente il menu completo, costituita da una chiave che contiene i dati della voce principale ed un valore che contiene la lista delle sue voci
     * @throws CommandException se si verifica un problema
     */
    public static LinkedHashMap<ItemBean, ArrayList<ItemBean>> makeMegaMenu(String appName,
                                                                            String surCode)
                                                                     throws CommandException {
        ArrayList<ItemBean> vV = null;
        LinkedHashMap<ItemBean, ArrayList<ItemBean>> vO = null;
        try {
            vO = new LinkedHashMap<ItemBean, ArrayList<ItemBean>>(11);
            LinkedList<ItemBean> titles = makeMenuOrizzontale(appName, surCode);
            for (ItemBean title : titles) {
                vV = new ArrayList<ItemBean>();
                vO.put(title, vV);
            }
            return vO;
        } catch (ArrayStoreException ase) {     // AttributoNonValorizzatoException
            String msg = FOR_NAME + "Si e\' verificato un problema nell\'accesso ad un elemento di una struttura vettoriale.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ase.getMessage(), ase);
        } catch (TypeNotPresentException tnpe) {    // WebStorageException
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di un tipo.\n";
            LOG.severe(msg);
            throw new CommandException(msg + tnpe.getMessage(), tnpe);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }


    /**
     * <p>Restituisce una struttura vettoriale <cite>(by the way:
     * with insertion order)</cite> contenente le voci principali
     * del (mega)menu orizzontale del sito di gestione dei processi
     * on-line <em>(prol)</em>.
     *
     * @param appName nome della web application, seguente la root
     * @param surCode identificativo della rilevazione corrente oppure della rilevazione di default in caso di accesso utente a piu' di una rilevazione
     * @return <code>LinkedList&lt;ItemBean&gt;</code> - struttura vettoriale, rispettante l'ordine di inserimento, che contiene le voci di primo livello
     * @throws CommandException nel caso in cui si verifichi un problema nel recupero di un attributo obbligatorio, o in qualche altro tipo di puntamento
     */
    private static LinkedList<ItemBean> makeMenuOrizzontale(String appName,
                                                            String surCode)
                                                     throws CommandException {
        int nextId = MAIN_MENU;
        LinkedList<ItemBean> mO = new LinkedList<>();
        // MACROPROCESSI
        ItemBean vO = new ItemBean();
        vO.setId(++nextId);
        vO.setNome(COMMAND_PROCESS);
        vO.setNomeReale(PART_PROCESS);
        vO.setLabelWeb("Macroprocessi");
        vO.setInformativa("La navigazione per macroprocessi fornisce un quadro d\'insieme dei rischi");
        vO.setUrl(makeUrl(appName, vO, vO.getNomeReale(), surCode));
        vO.setIcona("pc.png");
        vO.setLivello(MAIN_MENU);
        mO.add(vO);
        // STRUTTURE
        vO = null;
        vO = new ItemBean();
        vO.setId(++nextId);
        vO.setNome(COMMAND_STRUCTURES);
        vO.setLabelWeb("Organigramma");
        vO.setInformativa("La navigazione per strutture fornisce un quadro delle produzioni");
        vO.setUrl(makeUrl(appName, vO, null, surCode));
        vO.setIcona("act.png");
        vO.setLivello(MAIN_MENU);
        mO.add(vO);
        // RISCHI
        vO = null;
        vO = new ItemBean();
        vO.setId(++nextId);
        vO.setNome(COMMAND_RISK);
        vO.setLabelWeb("Rischi Corruttivi");
        vO.setInformativa("La navigazione per rischio corruttivo fornisce un quadro delle problematiche relative");
        vO.setUrl(makeUrl(appName, vO, null, surCode));
        vO.setIcona("per.png");
        vO.setLivello(MAIN_MENU);
        mO.add(vO);
        // REPORT
        vO = null;
        vO = new ItemBean();
        vO.setId(++nextId);
        vO.setNome(COMMAND_PROCESS);
        vO.setNomeReale(PART_SEARCH);
        vO.setLabelWeb("Report");
        vO.setInformativa("La reportistica permette di incrociare varie dimensioni");
        vO.setUrl(makeUrl(appName, vO, vO.getNomeReale(), surCode));
        vO.setIcona("mon.png");
        vO.setLivello(MAIN_MENU);
        mO.add(vO);
        return mO;
    }


    /**
     * <p>Restituisce una String che rappresenta un url da impostare in una
     * voce di menu, il cui padre viene passato come argomento, come
     * analogamente la web application seguente la root ed un eventuale
     * parametro aggiuntivo.</p>
     *
     * @param appName nome della web application, seguente la root, per la corretta generazione dell'url
     * @param title voce di livello immediatamente superiore alla voce per la quale si vuol generare l'url
     * @param part eventuale valore del parametro 'p' della Querystring
     * @param surCode identificativo della rilevazione corrente oppure della rilevazione di default in caso di accesso utente a piu' di una rilevazione
     * @return <code>String</code> - url ben formato e valido, da applicare a una voce di menu
     * @throws CommandException se si verifica un problema nell'accesso a qualche parametro o in qualche altro puntamento
     */
    private static String makeUrl(String appName,
                                  ItemBean title,
                                  String part,
                                  String surCode)
                           throws CommandException {
        String entParam = SLASH + QM + ConfigManager.getEntToken() + EQ;
        StringBuffer url = new StringBuffer(appName);
        try {
            url.append(entParam).append(title.getNome());
            if (part != null) {
                url.append("&p=").append(part);
            }
            url.append("&r=").append(surCode);
            return String.valueOf(url);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema di natura non identificata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }


    /**
     * <p>Restituisce una struttura vettoriale <cite>(with insertion order)</cite>
     * contenente le breadcrumbs lasciate dall'utente nel percorso seguito fino
     * alla richiesta corrente.</p>
     * <p>Non si pu&ograve; passare direttamente la richiesta e lasciare che
     * il metodo si arrangi a recuperare i parametri (con getParameterNames)
     * perch&eacute; la richiesta conosciuta da questa classe non corrisponde
     * alla richiesta del chiamante. In linea teorica, si potrebbe effettuare
     * questa operazione, modificando questo metodo in modo che accetti la
     * richiesta come argomento, e passandola come parametro; tuttavia, 
     * la richiesta &egrave; un oggetto oneroso, e soprattutto
     * creerebbe confusione in una Command che &egrave;
     * creata a sua volta per gestire richieste...<br />
     * <small>NOTA: In questa classe vi sono metodi che accettano come parametro
     * una richiesta, ispezionandone e restituendone i valori, ma si tratta di
     * metodi di debug, utilizzati dal programmatore finalizzati appunto ad
     * ispezionare lo stato della richiesta, non metodi da utilizzare per
     * generare output: quindi non &egrave; la stessa cosa.</small></p>
     *
     * @param appName    nome della web application, seguente la root
     * @param pageParams la queryString contenente tutti i parametri di navigazione
     * @param extraInfo  parametro facoltativo; se significativo, permette di specificare una foglia ad hoc; in tal caso viene aggiunto il link alla ex-foglia
     * @return <code>LinkedList&lt;ItemBean&gt;</code> - struttura vettoriale, rispettante l'ordine di inserimento, che contiene le voci seguite dall'utente nella navigazione fino alla richiesta corrente
     * @throws CommandException se si verifica un problema nell'accesso a qualche parametro o in qualche altro puntamento
     */
    public static LinkedList<ItemBean> makeBreadCrumbs(String appName,
                                                       String pageParams,
                                                       String extraInfo)
                                                throws CommandException {
        int prime = 13;                 // per ottimizzare
        // Ottiene l'elenco delle Command
        Vector<ItemBean> classiCommand = ConfigManager.getClassiCommand();
        // Genera l'etichetta per nodo radice
        final String homeLbl = Utils.capitalize(COMMAND_HOME);
        // Dichiara la struttura per la lista di voci da usare per generare le breadcrumbs
        AbstractList<ItemBean> nav = new LinkedList<>();
        // Variabili di appoggio
        String codeSurvey, tokenSurvey = null;
        // Dictonary contenente i soli valori del token 'p' permessi
        LinkedHashMap<String, String> allowedParams = new LinkedHashMap<>(prime);
        // Lista dei token che NON devono generare breadcrumbs ("token vietati")
        LinkedList<String> deniedTokens = new LinkedList<>();
        // Aggiunge un tot di token vietati, caricati dinamicamente
        String deniedPattern1 = "sliv";
        String deniedPattern2 = "pliv";
        for (int i = 1; i <= 4; i++) {
            String patternToDeny1 = new String(deniedPattern1 + i);
            String patternToDeny2 = new String(deniedPattern2 + i);
            deniedTokens.add(patternToDeny1);
            deniedTokens.add(patternToDeny2);
        }
        // Aggiunge le esclusioni per data e ora ed eventuali altri parametri (p.es. id) che non devono essere marcati
        deniedTokens.add("d");
        deniedTokens.add("t");
        deniedTokens.add("idO");
        deniedTokens.add("msg");
        deniedTokens.add("mliv");
        // Aggiunge i valori del token 'p' che devono generare breadcrumb associandoli a un'etichetta da mostrare in breadcrumb
        allowedParams.put(PART_SEARCH,              "Ricerca");
        allowedParams.put(PART_SELECT_STR,          "Scelta Struttura");
        allowedParams.put(PART_PROCESS,             "Scelta Processi");
        allowedParams.put(PART_SELECT_QST,          "Quesiti");
        allowedParams.put(PART_CONFIRM_QST,         "Riepilogo");
        allowedParams.put(PART_SELECT_QSS,          "Interviste");
        allowedParams.put(PART_RESUME_QST,          "Risposte");
        allowedParams.put(PART_OUTPUT,              "Output");
        allowedParams.put(PART_FACTORS,             "Fattori abilitanti");
        allowedParams.put(PART_INSERT_MONITOR_DATA, "Dettagli");
        try {
            // Tokenizza la querystring in base all'ampersand
            String[] tokens = pageParams.split(AMPERSAND);
            // Prepara la lista dei parametri da esporre nelle breadcrumbs
            Map<String, String> tokensAsMap = new LinkedHashMap<>(prime);
            // Esamina ogni token
            for (int i = 0; i < tokens.length; i++) {
                // Ottiene la coppia: 'parametro=valore'
                String couple = tokens[i];
                // Estrae il solo parametro
                String paramName = couple.substring(NOTHING, couple.indexOf(EQ));
                // Estrae il solo valore
                String paramValue = couple.substring(couple.indexOf(EQ));
                // Test: il token trovato NON rientra in quelli da escludere? 
                if (!deniedTokens.contains(paramName)) {
                    // Allora il token genererà una breadcrumb
                    tokensAsMap.put(paramName, paramValue);
                    // Le variabili locali non servono più...
                    couple = paramName = paramValue = null;
                }
            }
            // Recupera il codice rilevazione
            codeSurvey = tokensAsMap.get(PARAM_SURVEY).substring(SUB_MENU);
            // Controllo sull'input (il codice rilevazione deve essere valido!)
            if (!ConfigManager.getSurveys().containsKey(codeSurvey)) {
                // "Se non dispone di un codice rilevazione, gliene verrà assegnato uno d'ufficio"...
                tokenSurvey = PARAM_SURVEY + EQ + ConfigManager.getSurveyList().get(MAIN_MENU).getNome();
            } else {
                // Se dispone di un codice rilevazione valido, verrà usato quello
                tokenSurvey = PARAM_SURVEY + tokensAsMap.get(PARAM_SURVEY);
            }
            // Il link alla home è fisso
            //final String homeLnk = appName + ROOT_QM + ConfigManager.getEntToken() + EQ + COMMAND_HOME + AMPERSAND + tokenSurvey;
            final String homeLnk = "/rol";
            // Crea un oggetto per incapsulare il link della root
            ItemBean root = new ItemBean(appName, homeLbl, homeLnk, MAIN_MENU);
            // Aggiunge la root alle breadcrumbs
            nav.add(root);
            // Scorre tutti i token calcolati per generare le corrispettive breadcrumbs
            for (java.util.Map.Entry<String, String> entry : tokensAsMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String labelWeb = null;
                if (key.equals(ConfigManager.getEntToken())) {
                    for (ItemBean command : classiCommand) {
                        if (command.getNome().equals((value.substring(SUB_MENU)))) {
                            labelWeb = command.getLabelWeb();
                            break;
                        }
                    }
                } else if (allowedParams.containsKey(value.substring(SUB_MENU))) {
                    labelWeb = allowedParams.get(value.substring(SUB_MENU));
                }
                String url = appName + ROOT_QM + key + value + AMPERSAND + tokenSurvey;
                if (!url.equals(homeLnk)) {
                    ItemBean item = new ItemBean(key, labelWeb, url, SUB_MENU);
                    if (!key.equals(PARAM_SURVEY)) {
                        nav.add(item);
                    }
                }
            }
            if (extraInfo != null) {
                nav.add(new ItemBean(extraInfo, extraInfo, extraInfo, SUB_MENU));
            }
            return (LinkedList<ItemBean>) nav;
        } catch (PatternSyntaxException pse) {
            String msg = FOR_NAME + "Si e\' verificato un problema di parsing della queryString.\n";
            LOG.severe(msg);
            throw new CommandException(msg + pse.getMessage(), pse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema di natura non identificata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Prende in input una struttura di breadcrumbs gi&agrave; formata 
     * (serie di link corenti con il percorso seguito dall'utente fino 
     * alla richiesta corrente) e restituisce una struttura di breadcrumbs 
     * basata su quella ottenuta come parametro ma avente,
     * come ultima foglia, un'etichetta passata come parametro, se questo
     * &egrave; significativo, dando anche la possibilit&agrave; di eliminare 
     * un numero di foglie a piacere, specificato tramite un parametro: <dl>
     * <dt>0</dt><dd> =&gt; non toglie nulla,</dd>
     * <dt>1</dt><dd> =&gt; toglie l'ultima foglia,</dd> 
     * <dt>2</dt><dd> =&gt; toglie l'ultima e la penultima foglia</dd>
     * </dl>etc.<br>
     * Esempio:
     * <pre>makeBreadCrumbs(nav, 0, "label")</pre>
     * aggiunge la foglia "label" alle breadcrumbs contenute in nav
     * (la foglia per definizione non ha link).</p>
     * 
     * @param nav       lista di breadcrumbs preesistente
     * @param items     numero di foglie da potare (opzionale)
     * @param extraInfo etichetta da aggiungere come ultima foglia (opzionale)
     * @return <code>LinkedList&lt;ItemBean&gt;</code> - struttura vettoriale, rispettante l'ordine di inserimento, rimaneggiata
     * @throws CommandException se si verifica un problema nell'accesso a qualche parametro o in qualche altro puntamento
     */
    public static LinkedList<ItemBean> makeBreadCrumbs(LinkedList<ItemBean> nav,
                                                       int items,
                                                       String extraInfo)
                                                throws CommandException {
        // Pota le foglie di Lorien
        if (items > NOTHING) {
            for (int i = 0; i < items; i++) {
                nav.removeLast();
            }
        }
        // Aggiunge una foglia di Valinor
        if (extraInfo != null && !extraInfo.equals(VOID_STRING)) {
            nav.add(new ItemBean(extraInfo, extraInfo, extraInfo, SUB_MENU));
        }
        // Restituisce l'albero potato e/o rimaneggiato
        return nav;
    }

    
    /**
     * <p>Prende in input una struttura di breadcrumbs gi&agrave; formata 
     * (serie di link corenti con il percorso seguito dall'utente fino 
     * alla richiesta corrente) e sostituisce la foglia di dato indice index
     * con un nuovo oggetto che riceve come parametro extraInfo.</p>
     * 
     * @param nav       lista di breadcrumbs preesistente
     * @param index     l'indice della foglia da sostituire
     * @param extraInfo la foglia sostitutiva
     * @return <code>LinkedList&lt;ItemBean&gt;</code> - struttura vettoriale, rispettante l'ordine originale, rimaneggiata
     * @throws CommandException se si verifica un problema nell'accesso a qualche parametro o in qualche altro puntamento
     */
    public static LinkedList<ItemBean> makeBreadCrumbs(final LinkedList<ItemBean> nav,
                                                       int index,
                                                       ItemBean extraInfo)
                                                throws CommandException {
        LinkedList<ItemBean> newNav = (LinkedList<ItemBean>) nav.clone();
        // Rimuove una foglia di Lorien
        if (index > NOTHING) {  
            // Non avrebbe senso rimuovere 'home'
            newNav.remove(index);
            // La sostituisce con una foglia di Valinor
            if (extraInfo != null) {
                newNav.set(index, extraInfo);
            }
        }
        // Restituisce l'albero rimaneggiato
        return newNav;
    }
    

    /* ************************************************************************ *
     *                             Metodi di debug                              *
     * ************************************************************************ */

    /**
     * <p>Restituisce i nomi e i valori degli attributi presenti in Request
     * in un dato momento e in un dato contesto, rappresentati dallo
     * stato del chiamante.</p>
     * <p>Pu&ograve; essere utilizzato per verificare rapidamente
     * quali attributi sono presenti in Request onde evitare duplicazioni
     * o ridondanze.</p>
     * </p>
     * Ad esempio, richiamando questo metodo dal ramo "didattica" del sito web
     * di ateneo, metodo <code>requestByPage</code>
     * e.g.: <pre>req.setAttribute("reqAttr", getAttributes(req));</pre>
     * e richiamandolo dalla pagina relativa, con la semplice:
     * <pre>${reqAttr}</pre>
     * si ottiene:
     * <pre style="border:solid gray;border-width:2px;padding:8px;">
     * <strong>dipartimento</strong> = it.univr.di.uol.bean.DipartimentoBean@518dd094
     * <strong>mO</strong> = {it.univr.di.uol.bean.SegnalibroBean@1ef0921d=[it.univr.di.uol.MenuVerticale@5ab38d6b, it.univr.di.uol.MenuVerticale@42099a52], it.univr.di.uol.bean.SegnalibroBean@4408bdc9=[it.univr.di.uol.MenuVerticale@4729f5d], it.univr.di.uol.bean.SegnalibroBean@19e3fa04=[it.univr.di.uol.MenuVerticale@13c94f3], it.univr.di.uol.bean.SegnalibroBean@463329e3=[it.univr.di.uol.MenuVerticale@3056de27]}
     * <strong>lingue</strong> = it.univr.di.uol.Lingue@3578ce60
     * <strong>FirstLanguage</strong> = it
     * <strong>flagsUrl</strong> = ent=home&page=didattica
     * <strong>SecondLanguage</strong> = en
     * <strong>logoFileDoc</strong> = [[it.univr.di.uol.bean.FileDocBean@5b11bbf9]]
     * <strong>currentYear</strong> = 2015
     * </pre></p>
     *
     * @param req HttpServletRequest contenente gli attributi che si vogliono conoscere
     * @return un unico oggetto contenente tutti i valori e i nomi degli attributi settati in request nel momento in cui lo chiede il chiamante
     */
    public static String getAttributes(HttpServletRequest req) {
        Enumeration<String> attributes = req.getAttributeNames();
        StringBuffer attributesName = new StringBuffer("<pre>");
        while (attributes.hasMoreElements()) {
            String attributeName = attributes.nextElement();
            attributesName.append("<strong><u>");
            attributesName.append(attributeName);
            attributesName.append("</u></strong>");
            attributesName.append(" = ");
            attributesName.append(req.getAttribute(attributeName));
            attributesName.append("<br />");
        }
        attributesName.append("</pre>");
        return String.valueOf(attributesName);
    }


    /**
     * <p>Restituisce i nomi e i valori dei parametri presenti in Request
     * in un dato momento e in un dato contesto, rappresentati dallo
     * stato del chiamante.</p>
     * <p>Pu&ograve; essere utilizzato per verificare rapidamente
     * quali parametri sono presenti in Request onde evitare duplicazioni
     * e/o ridondanze.</p>
     * <p>Esempi di richiamo:
     * String par = HomePageCommand.getParameters(req, MIME_TYPE_HTML);
     * String par = HomePageCommand.getParameters(req, MIME_TYPE_TEXT);
     * </p>
     * @param req HttpServletRequest contenente i parametri che si vogliono conoscere
     * @param mime argomento specificante il formato dell'output desiderato
     * @return un unico oggetto contenente tutti i valori e i nomi dei parametri settati in request nel momento in cui lo chiede il chiamante
     */
    public static String getParameters(HttpServletRequest req,
                                       String mime) {
        Enumeration<String> parameters = req.getParameterNames();
        StringBuffer parametersName = new StringBuffer();
        if (mime.equals(MIME_TYPE_HTML)) {
            parametersName.append("<pre>");
            while (parameters.hasMoreElements()) {
                String parameterName = parameters.nextElement();
                parametersName.append("<strong><u>");
                parametersName.append(parameterName);
                parametersName.append("</u></strong>");
                parametersName.append(" = ");
                parametersName.append(req.getParameter(parameterName));
                parametersName.append("<br />");
            }
            parametersName.append("</pre>");
        } else if (mime.equals(MIME_TYPE_TEXT)) {
            while (parameters.hasMoreElements()) {
                String parameterName = parameters.nextElement();
                parametersName.append(parameterName);
                parametersName.append(" = ");
                parametersName.append(req.getParameter(parameterName));
                parametersName.append("\n");
            }
        }
        return String.valueOf(parametersName);
    }


    /**
     * <p>Restituisce <code>true</code> se un nome di un parametro,
     * il cui valore viene passato come argomento del metodo, esiste
     * tra i parametri della HttpServletRequest; <code>false</code>
     * altrimenti.</p>
     * <p>Pu&ograve; essere utilizzato per verificare rapidamente
     * se un dato parametro sia stato passato in Request.</p>
     *
     * @param req HttpServletRequest contenente i parametri che si vogliono conoscere
     * @param paramName argomento specificante il nome del parametro cercato
     * @return un unico oggetto contenente tutti i valori e i nomi dei parametri settati in request nel momento in cui lo chiede il chiamante
     */
    public static boolean isParameter(HttpServletRequest req,
                                      String paramName) {
        Enumeration<String> parameters = req.getParameterNames();
        while (parameters.hasMoreElements()) {
            String parameterName = parameters.nextElement();
            if (parameterName.equalsIgnoreCase(paramName)) {
                return true;
            }
        }
        return false;
    }

    /* ************************************************************************ *
     *                    Getters sulle variabili di classe                     *
     * ************************************************************************ */

    /**
     * <p>Restituisce la rilevazione con data pi&uacute; recente.</p>
     *
     * @return <code>LinkedList&lt;CodeBean&gt;</code> - una lista ordinata di tutti i possibili valori con cui puo' essere descritta la complessita' di un elemento
     * @throws AttributoNonValorizzatoException se l'id del CodeBean che rappresenta l'ultima rilevazione non e' stato valorizzato (p.es. per un difetto della query)
     */
    public static CodeBean getLastSurvey()
                                  throws AttributoNonValorizzatoException {
        return new CodeBean(lastSurvey);
    }

}
