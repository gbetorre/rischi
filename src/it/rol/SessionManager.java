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

package it.rol;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import com.oreilly.servlet.ParameterParser;

import it.rol.bean.CodeBean;
import it.rol.bean.PersonBean;
import it.rol.command.HomePageCommand;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.CommandException;
import it.rol.exception.NotFoundException;
import it.rol.exception.WebStorageException;


/**
 * <p>SessionManager &egrave; la classe che si occupa dell'autenticazione
 * dell'utente, dell'impostazione dei privilegi, di eventuali cookies,
 * del logout e di quant'altro occorra alle funzioni di gestione utenti
 * della web-application 
 * <code>Rischi on Line (rol)</code>.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class SessionManager extends HttpServlet implements Constants {

    /**
     * La serializzazione necessita della dichiarazione
     * di una costante di tipo long identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = 1L;
    /**
     *  Nome di questa classe
     *  (utilizzato per contestualizzare i messaggi di errore)
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     * Logger della classe per scrivere i messaggi di errore
     */
    private Logger log = Logger.getLogger(SessionManager.class.getName());
    /**
     * Log per debug in produzione
     */
    protected static Logger LOG = Logger.getLogger(Main.class.getName());
    /**
     * <p>Nome e percorso della pagina di errore cui ridirigere in caso
     * di eccezioni rilevate.</p>
     */
    private static String errorJsp;
    /**
     * <p>Nome del parametro di inizializzazione, valorizzato nel
     * descrittore di deploy, che identifica la command cui la Servlet deve
     * girare la richiesta (storicamente valeva 'ent').</p>
     * <p><small>Il nome di questo parametro viene anche esposto dalla Main attraverso un
     * apposito metodo di classe, ma siccome la Servlet corrente preferisce
     * ricavarsi i valori di altri parametri direttamente dal descrittore di deploy,
     * tanto vale rendere indipendenti i due oggetti e non fare troppe assunzioni sulla
     * sequenza di inizializzazione.</small></p>
     */
    private static String entToken;
    /**
     * <p>Nome del template in cui vengono assemblati i vari 'pezzi'
     * che compongono l'output html finale.</p>
     */
    private static String templateJsp;
    /**
     * <p>Iterazioni per il costruttore PBEKeySpec.</p>
     */
    private static final int ITERATIONS = 65536;
    /**
     * <p>Lunghezza della chiave derivata dal costruttore PBEKeySpec.</p>
     */
    private static final int KEY_LENGTH = 128;
    /**
     * <p>Algoritmo utilizzato per il criptaggio della password.</p>
     */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    /**
     * <p>Costante random definita da un generatore di numeri casuali sicuro.</p>
     */
    private static final SecureRandom RAND = new SecureRandom();
    /**
     * <p>Costante indicante la lunghezza in termini di caratteri del seme,
     * usato per la criptazione della password.</p>
     */
    public static final int SALT_LENGTH = 128;


    /**
     * <p>Inizializza, staticamente, alcune variabili globali.</p>
     *
     * @param config la configurazione usata dal servlet container per passare informazioni alla servlet <strong>durante l'inizializzazione</strong>
     * @throws ServletException una eccezione che puo' essere sollevata quando la servlet incontra difficolta'
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        /*  Nome della pagina di errore  */
        errorJsp = getServletContext().getInitParameter("errorJsp");
        if (errorJsp == null)
            throw new ServletException(FOR_NAME + "\n\nManca il parametro di contesto 'errorJsp'!\n\n");
        /*  Nome del parametro che identifica la Command da interpellare  */
        entToken = getServletContext().getInitParameter("entToken");
        if (entToken == null) {
            throw new ServletException(FOR_NAME + "\n\nManca il parametro di contesto 'entToken'!\n\n");
        }
        /*
         * Nome del template da invocare per l'assemblaggio dei vari componenti
         * dell'output - nel contesto della servlet di autenticazione
         * serve piu' che altro per redirigere sulla maschera di login
         * in caso di invalidamento della sessione
         */
        templateJsp = getServletContext().getInitParameter("templateJsp");
        if (templateJsp == null) {
            throw new ServletException(FOR_NAME + "\n\nManca il parametro di contesto 'templateJsp'!\n\n");
        }
    }


    /**
     * <p>Gestisce le richieste del client effettuate con il metodo GET.</p>
     *
     * @param req la HttpServletRequest contenente la richiesta del client
     * @param res la HttpServletResponse contenente la risposta del server
     * @throws ServletException eccezione che viene sollevata se si verifica un problema nell'inoltro (forward) della richiesta/risposta
     * @throws IOException      eccezione che viene sollevata se si verifica un problema nell'inoltro (forward) della richiesta/risposta
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res)
                  throws ServletException, IOException {
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            HttpSession session = req.getSession(Constants.IF_EXISTS_DONOT_CREATE_NEW);
            PersonBean user = (PersonBean) session.getAttribute("usr");
            String msg = "Ha effettuato il logout l\'utente: " +
                         user.getNome() + BLANK_SPACE + user.getCognome() +
                         " in data"  + BLANK_SPACE + Utils.format(Utils.getCurrentDate()) +
                         " alle ore" + BLANK_SPACE + Utils.getCurrentTime() +
                         ".\n";
            log.info(msg);
            session.invalidate();
            final RequestDispatcher rd = getServletContext().getRequestDispatcher(templateJsp);
            rd.forward(req, res);
        } catch (IllegalStateException ise) {
            String msg = "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n" + ise.getMessage();
            log.severe(msg);
            throw new ServletException(msg, ise);
        } catch (NullPointerException npe) {
            String msg = "Problemi nel recupero della sessione.\n" + npe.getMessage();
            log.severe(msg);
            throw new ServletException(msg, npe);
        } catch (Exception e) {
            String msg = "Problemi nel metodo doGet.\n" + e.getMessage();
            log.severe(msg);
            throw new ServletException(msg, e);
        }
    }


    /**
     * <p>Gestisce le richieste del client effettuate con il metodo POST.</p>
     *
     * @param req la HttpServletRequest contenente la richiesta del client
     * @param res la HttpServletResponse contenente la risposta del server
     * @throws ServletException eccezione che viene sollevata se si verifica un problema nell'inoltro (forward) della richiesta/risposta
     * @throws IOException      eccezione che viene sollevata se si verifica un problema nell'inoltro (forward) della richiesta/risposta
     */
    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse res)
                   throws ServletException, IOException {
        DBWrapper db = null;                                // DataBound
        ParameterParser parser = new ParameterParser(req);  // Crea il parser per i parametri
        String username = null;                             // Credenziali
        String password = null;                             // Credenziali
        StringBuffer msg = new StringBuffer();              // Messaggio
        // Effettua la connessione al databound
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new ServletException(FOR_NAME + "Non riesco ad instanziare databound.\n" + wse.getMessage(), wse);
        }
        // Recupera le credenziali
        username = parser.getStringParameter("usr", new String());
        password = parser.getStringParameter("pwd", new String());
        // Flag sessione
        boolean authenticated = false;
        // Crea la sessione stessa, se non c'è già, altrimenti la recupera
        try {
            authenticated = authenticateEncrypted(username, password, req, db, msg);
            if (!authenticated) {
                authenticated = authenticate(username, password, req, db, msg);
            }
        } catch (InvalidKeySpecException ikse) {
            throw new ServletException(FOR_NAME + "Chiave specificata non valida.\n" + ikse.getMessage(), ikse);
        } catch (NoSuchAlgorithmException nsae) {
            throw new ServletException(FOR_NAME + "Algoritmo specificato non disponibile nell'ambiente.\n" + nsae.getMessage(), nsae);
        } catch (CommandException ce) {
            throw new ServletException(FOR_NAME + "Non riesco ad identificare l\'utente.\n" + ce.getMessage(), ce);
        }
        try {
            if (authenticated) {
                // Logga anzitutto l'accesso
                traceAccess(req, username, db);
                // Identifica l'ultima rilevazione
                CodeBean r = HomePageCommand.getLastSurvey();
                // Prepara l'indirizzo di landing
                String postAuthLand = "/?" + entToken + "=home&r=" + r.getNome();
                // Redirige sull'indirizzo
                //res.sendRedirect(res.encodeRedirectURL(getServletContext().getInitParameter("appName") + postAuthLand));
                res.sendRedirect(res.encodeRedirectURL("/rol"));
            }
            else {
                // Log dell'evento
                LOG.severe("Oggetto PersonBean non valorizzato; probabile errore nell\'immissione delle credenziali.\n");
                //throw new NotFoundException("Oggetto PersonBean non valorizzato; probabile errore nell\'immissione delle credenziali.\n");
                /* codice alternativo se si volesse gestire l'errore con una pagina apposita;
                 * la controindicazione in tal caso e' nel fatto che il browser "crede" che
                 * l'applicazione abbia risposto positivamente e chiede di aggiornare le
                 * credenziali; invece se l'utente non e' autenticato, o ha sbagliato a
                 * inserire le credenziali, oppure non ce le ha proprio.
                 */
                req.setAttribute("error", true);
                req.setAttribute("msg", msg);
                final RequestDispatcher rd = getServletContext().getRequestDispatcher(getServletContext().getInitParameter("appName"));
                rd.forward(req, res);
            }
        } catch (AttributoNonValorizzatoException anve) {
            throw new ServletException("Identificativo dell\'ultima rilevazione non trovato.\n" + anve.getMessage(), anve);
        } catch (NotFoundException nfe) {
            throw new ServletException("Errore nell'estrazione delle credenziali utente.\n" + nfe.getMessage(), nfe);
        } catch (IllegalStateException ise) {
            throw new ServletException("Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n" + ise.getMessage(), ise);
        } catch (NullPointerException npe) {
            throw new ServletException("Errore di puntamento a null.\n" + npe.getMessage(), npe);
        } catch (Exception e) {
            //req.setAttribute("error", true);
            //req.setAttribute("msg", msg);
            //Log dell'evento
            LOG.severe("Oggetto PersonBean non valorizzato; l\'username passato come parametro non ha associato alcun processo.\n" + e.getMessage());
            //final RequestDispatcher rd = getServletContext().getRequestDispatcher("/jsp/error.jsp"  + "?" + req.getQueryString());
            //rd.forward(req, res);
            // Solleva un'eccezione per far andare diretti sulla pagina di errore, senza nemmeno rispondere
            throw new IOException();
        }
    }


    /**
     * <p>Crea la sessione utente.<br>
     * Inserisce la sessione creata nella HttpServletRequest, modificandola
     * per riferimento (<code>ByRef</code>).</p>
     *
     * @param username nome utente inserito ai fini di login
     * @param password password inserita ai fini di login
     * @param req   HttpServletRequest per la creazione della sessione
     * @param db    DataBound per la query riguardo le credenziali
     * @param message messaggio per l'output circa l'esito della login
     * @return <code>boolean</code> - true se l'autenticazione e' andata a buon fine, false in caso contrario
     * @throws CommandException se si verifica un problema nel recupero dell'utente in base alle credenziali fornite
     */
    public static boolean authenticate(String username,
                                       String password,
                                       HttpServletRequest req,
                                       DBWrapper db,
                                       StringBuffer message)
                                throws CommandException {
        boolean authenticated = false;
        HttpSession session = req.getSession();
        // Se la sessione non è nuova ci sono già dentro dei valori
        if (session.getAttribute("usr") != null) {
            authenticated = true;
        }
        else {  // Se la sessione è nuova bisogna valorizzarla opportunamente
            // Interroga il database a proposito dell'utente
            try {
                PersonBean user = db.getUser(username, password);
                if (user != null) {
                    message.append("Benvenuto" + user.getNome());
                    session.setAttribute("msg", message);
                    session.setAttribute("error", false);
                    session.setAttribute("usr", user);
                    authenticated = true;
                } else {
                    message.append("Errore di autenticazione. Ricontrollare Username e Password." );
                    session.setAttribute("msg", message);
                    session.setAttribute("error", true);
                    authenticated = false;
                }
            } catch (WebStorageException wse) {
                String msg = FOR_NAME + "Non riesco a determinare l'utente";
                LOG.severe(msg);
                throw new CommandException(msg + wse.getMessage(), wse);
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Attributo della persona non valorizzato.\n";
                LOG.severe(msg);
                throw new CommandException(msg + anve.getMessage(), anve);
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Oggetto persona non valorizzato.\n";
                LOG.severe(msg);
                throw new CommandException(msg + npe.getMessage(), npe);
            }
        }
        return authenticated;
    }


    /**
     * <p>Crea la sessione utente.<br>
     * Inserisce la sessione creata nella HttpServletRequest, modificandola
     * per riferimento <code>ByRef</code>.</p>
     *
     * @param username nome utente inserito ai fini di login
     * @param password password inserita ai fini di login
     * @param req   HttpServletRequest per la creazione della sessione
     * @param db    DataBound per la query riguardo le credenziali
     * @param message messaggio per l'output circa l'esito della login
     * @return <code>boolean</code> - true se l'autenticazione e' andata a buon fine, false in caso contrario
     * @throws CommandException se si verifica un problema nel recupero dell'utente in base alle credenziali fornite
     * @throws InvalidKeySpecException   se la chiave non &egrave; valida (codifica non valida, lunghezza non valida, non inizializzata, ...)
     * @throws NoSuchAlgorithmException  se non &egrave; disponibile l'algoritmo di criptaggio nell'ambiente
     */
    public static boolean authenticateEncrypted(String username,
                                                String password,
                                                HttpServletRequest req,
                                                DBWrapper db,
                                                StringBuffer message)
                                         throws CommandException,
                                                NoSuchAlgorithmException,
                                                InvalidKeySpecException {
        boolean authenticated = false;
        HttpSession session = req.getSession();
        // Se la sessione non è nuova ci sono già dentro dei valori
        if (session.getAttribute("usr") != null) {
            authenticated = true;
        }
        else {
            // Interroga il database a proposito dell'utente
            try {
                CodeBean encryptedPassword = db.getEncryptedPassword(username);
                PersonBean user = null;
                if (encryptedPassword != null && verifyPassword(password, encryptedPassword)) {
                    user = db.getUser(username, encryptedPassword.getNome());
                }
                if (user != null) {
                    message.append("Benvenuto" + user.getNome());
                    session.setAttribute("msg", message);
                    session.setAttribute("error", false);
                    session.setAttribute("usr", user);
                    authenticated = true;
                } else {
                    authenticated = false;
                }
            } catch (WebStorageException wse) {
                String msg = FOR_NAME + "Non riesco a determinare l'utente";
                LOG.severe(msg);
                throw new CommandException(msg + wse.getMessage(), wse);
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Attributo della persona non valorizzato.\n";
                LOG.severe(msg);
                throw new CommandException(msg + anve.getMessage(), anve);
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Oggetto persona non valorizzato.\n";
                LOG.severe(msg);
                throw new CommandException(msg + npe.getMessage(), npe);
            }
        }
        return authenticated;
    }


    /**
     * <p>Prepara le informazioni da registrare nel database per tracciare
     * l'evento di login di un determinato utente, passato come argomento.<br>
     * Chiama il metodo del model, un cui riferimento viene passato come
     * argomento, che scriver&agrave; nel database i dati  dell'accesso
     * &ndash; o ne aggiorner&agrave; gli estremi nel caso in cui
     * l'utente si fosse precedentemente gi&agrave; loggato.</p>
     *
     * @param req HttpServletRequest contenente la richiesta fatta dal client
     * @param username nome utente loggato
     * @param db riferimento al model
     * @throws WebStorageException nel caso in cui, per un qualunque motivo, l'operazione di inserimento o aggiornamento non vada a buon fine
     * @throws UnknownHostException nel caso in cui il tentativo di risolvere l'host nel contesto della creazione di un InetAddress non vada a buon fine
     * @throws RuntimeException nel caso in cui si verifichi un problema in qualche tipo di puntamento (p.es. un puntamento a null)
     */
    private static void traceAccess(HttpServletRequest req,
                                    String username,
                                    DBWrapper db)
                             throws WebStorageException,
                                    UnknownHostException,
                                    RuntimeException {
        db.manageAccess(username);
    }


    /**
     * <p>Genera il seme necessario per la password criptata ad ogni cambio password dell'utente loggato.</p>
     *
     * @param length  lunghezza del seme
     * @return <code>String</code> - stringa contenente il seme generato
     */
    public static String generateSalt(final int length) {
        if (length < 1) {
          System.err.println("Error in generateSalt: length must be > 0");
          return VOID_STRING;
        }
        byte[] salt = new byte[length];
        //SecureRandom RAND = new SecureRandom();
        RAND.nextBytes(salt);
        return DatatypeConverter.printBase64Binary(salt);
    }


    /**
     * <p>Esegue il criptaggio della password dell'utente.</p>
     *
     * @param password    password inserita dall'utente
     * @param salt        seme univoco per ogni utente in base al quale la password viene criptata
     * @return <code>String</code> - ritorna una stringa contenente la password criptata
     * @throws NoSuchAlgorithmException  se non &egrave; disponibile l'algoritmo di criptaggio nell'ambiente
     * @throws InvalidKeySpecException   se la chiave non &egrave; valida (codifica non valida, lunghezza non valida, non inizializzata, ...)
     */
    public static String hashPassword(String password,
                                      String salt)
                               throws NoSuchAlgorithmException,
                                      InvalidKeySpecException {
        char[] chars = password.toCharArray();
        byte[] bytes = salt.getBytes();
        KeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);
        Arrays.fill(chars, Character.MIN_VALUE);
        try {
            SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] securePassword = fac.generateSecret(spec).getEncoded();
            return DatatypeConverter.printBase64Binary(securePassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.err.println("Exception encountered in hashPassword()" + ex.getMessage() + "\nCause: " + ex.getCause());
            return VOID_STRING;
        } finally {
            ((PBEKeySpec) spec).clearPassword();
        }
      }


    /**
     * <p>Verifica che la password inserita dall'utente e la password criptata nel
     * database corrispondano.</p>
     *
     * @param password             password inserita dall'utente
     * @param encryptedPassword    password e seme corrispondenti all'utente che ha richiesto l'accesso presenti sul database
     * @return <code>boolean</code> - true se la password corrisponde a quella presente nel database. False altrimenti.
     * @throws AttributoNonValorizzatoException se un campo obbligatorio del bean &egrave; stato trovato non valorizzato
     * @throws InvalidKeySpecException se la chiave non &egrave; valida (codifica non valida, lunghezza non valida, non inizializzata, ...)
     * @throws NoSuchAlgorithmException se non &egrave; disponibile l'algoritmo di criptaggio nell'ambiente
     */
    public static boolean verifyPassword(String password,
                                         CodeBean encryptedPassword)
                                  throws NoSuchAlgorithmException,
                                         InvalidKeySpecException,
                                         AttributoNonValorizzatoException {
        String salt = encryptedPassword.getInformativa();
        if (salt.equals(VOID_STRING)) {
            return false;
        }
        String optEncrypted = hashPassword(password, encryptedPassword.getInformativa());
        return optEncrypted.equals(encryptedPassword.getNome());
    }
    

    /**
     * <p>Verifica se la sessione utente, che accetta come argomento, &egrave;
     * valida o esistente. In tal caso, non solleva eccezione; 
     * in caso contrario, viene sollevata un'eccezione tramite cui 
     * il chiamante pu&ograve; interrompere l'esecuzione.</p>
     * 
     * @param session           sessione utente gia' creata (se l'utente non e' loggato vale null)
     * @throws CommandException se l'utente non e' ancora loggato o si verifica un problema di puntamento
     */
    public static void checkSession(HttpSession session)
                             throws CommandException {
        if (session == null) {
            throw new CommandException("Attenzione: controllare di aver effettuato l\'accesso!\n");    
        }
        try {
            // Bisogna essere autenticati 
            PersonBean user = (PersonBean) session.getAttribute("usr");
            // Cioè bisogna che l'utente corrente abbia una sessione valida
            if (user == null) {
                throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n");
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
    }
    
}
