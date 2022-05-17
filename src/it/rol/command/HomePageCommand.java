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
import it.rol.DBWrapper;
import it.rol.Main;
import it.rol.Query;
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
     * Costante parlante per impostare il livello di voci di menu
     * che non hanno un livello superiore (sono padri di sottomenu)
     */
    public static final int MAIN_MENU = 0;
    /**
     * Costante parlante per impostare il livello di voci di sottomenu
     * che hanno un solo livello superiore (padre di livello 0)
     */
    public static final int SUB_MENU = 1;
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
        String codeSur = parser.getStringParameter("r", DASH);
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
                    error = "Codice di rilevazione non valido.";
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
            dbName = ConfigManager.getDbName();
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
     *              Metodi di generazione di liste di voci di MENU              *
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
        LinkedList<ItemBean> mO = new LinkedList<ItemBean>();
        // MACROPROCESSI
        ItemBean vO = new ItemBean();
        vO.setId(++nextId);
        vO.setNome(COMMAND_PROCESS);
        vO.setNomeReale(PART_MACROPROCESS);
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
        // PERSONE
        vO = null;
        vO = new ItemBean();
        vO.setId(++nextId);
        vO.setNome(COMMAND_PERSON);
        vO.setLabelWeb("Risorse Umane");
        vO.setInformativa("La navigazione per persone fornisce un quadro delle allocazioni");
        vO.setUrl(makeUrl(appName, vO, null, surCode));
        vO.setIcona("per.png");
        vO.setLivello(MAIN_MENU);
        mO.add(vO);
        // REPORT
        vO = null;
        vO = new ItemBean();
        vO.setId(++nextId);
        vO.setNome(COMMAND_PROCESS);
        vO.setNomeReale(PART_MULTIFACT);
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
        String entParam = "/?" + ConfigManager.getEntToken() + "=";
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
     * questa operazione, ma la richiesta &egrave; un oggetto oneroso, e soprattutto
     * creerebbe confusione in una Command che &egrave;
     * creata a sua volta per gestire richieste...<br />
     * <small>NOTA: I metodi di questa classe
     * che gestiscono le richieste sono metodi di debug, utilizzati dal programmatore
     * per ispezionare lo stato della richiesta, non metodi da utilizzare per
     * generare output, quindi non &egrave; la stessa cosa.</small></p>
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
        AbstractList<ItemBean> nav = new LinkedList<>();
        int prime = 13;                 // per ottimizzare
        final String homeLbl = "Home";
        String codeSurvey, tokenSurvey = null;
        Vector<ItemBean> classiCommand = ConfigManager.getClassiCommand();
        LinkedHashMap<String, String> allowedParams = new LinkedHashMap<String, String>(prime);
        String deniedPattern1 = "sliv";
        String deniedPattern2 = "pliv";
        allowedParams.put(PART_SEARCH_PERSON, "Ricerca");
        allowedParams.put(PART_SELECT_STR, "Scelta Struttura");
        allowedParams.put(PART_PROCESS, "Scelta Processi");
        allowedParams.put(PART_SELECT_QST, "Quesiti");
        try {
            String[] tokens = pageParams.split(AMPERSAND);
            Map<String, String> tokensAsMap = new LinkedHashMap<String, String>(prime);
            for (int i = 0; i < tokens.length; i++) {
                String couple = tokens[i];
                String paramName = couple.substring(NOTHING, couple.indexOf(EQ));
                String paramValue = couple.substring(couple.indexOf(EQ));
                if (!paramName.startsWith(deniedPattern1) && !paramName.startsWith(deniedPattern2)) {
                    tokensAsMap.put(paramName, paramValue);
                    couple = paramName = paramValue = null;
                }
            }
            codeSurvey = tokensAsMap.get(PARAM_SURVEY).substring(SUB_MENU);
            // Controllo sull'input (il codice rilevazione deve essere valido!)
            if (!ConfigManager.getSurveys().containsKey(codeSurvey)) {
                // Se non dispone di un avvocato, gliene verrà assegnato uno di ufficio...
                tokenSurvey = PARAM_SURVEY + EQ + ConfigManager.getSurveyList().get(MAIN_MENU).getNome();
            } else {
                tokenSurvey = PARAM_SURVEY + tokensAsMap.get(PARAM_SURVEY);
            }
            final String homeLnk = appName + ROOT_QM + ConfigManager.getEntToken() + EQ + COMMAND_HOME + AMPERSAND + tokenSurvey;
            ItemBean root = new ItemBean(appName, homeLbl, homeLnk, MAIN_MENU);
            nav.add(root);

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
