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

package it.rol.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import it.rol.ConfigManager;
import it.rol.Constants;


/**
 * <p>DataUrl.java &egrave; una classe di servizio.</p> 
 * <p>Permette di costruire in modo semplice un url relativo o assoluto 
 * (comprensivo di context path) o la parte 'dati' di
 * un url sostituendo i caratteri speciali come ' ' e '&amp;' con caratteri
 * equivalenti adatti per URL.</p>
 * <p>Questa classe si basa sull'idea di una tabella, o mappa, in cui sono
 * memorizzati tutti i parametri possibili presenti sulla query string.<br />
 * Mette a disposizione metodi per inserire, aggiornare o eliminare tali
 * parametri, nonch&eacute; costruttori che inizializzano tale tabella, 
 * da zero oppure proprio a partire da una query string presente in una
 * richiesta <code>GET</code>.<br />
 * Dispone, inoltre, di metodi che permettono di generare URL http 'puliti',
 * cio&egrave; conformi alle regole di codifica degli URI e depurati di 
 * eventuali caratteri erronei in taluni contesti (come la context root '/'
 * dove gi&agrave; presente).<br />
 * Infine &ndash; ma non ultimo &ndash; costruire gli URL con i metodi forniti
 * da questa classe permette di ottenere sui valori degli <code>href</code> 
 * tutti gli eventuali parametri aggiuntivi opzionali presenti in query string 
 * (<em>in primis</em>, la eventuale lingua, che, nelle applicazioni
 * multilingua, &egrave; esplicitata facoltativamente - se non esplicitata
 * viene solitamente ricavata dalla lingua del sistema operativo, 
 * dalla localizzazione del client o da un valore di default).</p><hr>
 * <p>DataUrl generates correctly formatted URLs for the Italian servlet application.
 * Supports root paths and relative paths with mandatory parameters 
 * (entToken, commandName, survey code, 'p' parameter) 
 * plus optional additional parameters.
 * All constructors validate input parameters and build query strings 
 * according to app conventions.
 * Example usage:
 * <pre>
 * DataUrl url1 = new DataUrl("server");                    // /server/appname/
 * DataUrl url2 = new DataUrl("ENT1", "LIST", survey);     // /entToken=ENT1&cmd=LIST&r=123
 * DataUrl url3 = new DataUrl("ENT1", "LIST", "part1", survey); // /entToken=ENT1&cmd=LIST&p=part1&r=123
 * </pre>
 * </p>
 * <p>Created on  Thu 27 Feb 2025 12:45:24 PM CET 2025</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class DataUrl implements Constants {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
     */
    private static final long serialVersionUID = 1413064779905762448L;
    /** Token javax.servlet.GenericServlet.getServletContext().getContextPath() */
    private static String contextPathToken = ConfigManager.getContextPathToken();
    /**     * Token per definire la servlet centrale                            */
    private static String servletToken = ConfigManager.getAppName();
    /**     * Token per la classe command                                       */
    private static String commandToken = ConfigManager.getEntToken();
    /**     * Token per la classe command con '=' attaccato in fondo.           */
    private static String commandTokenEquals = commandToken + EQ;
    /**   * Token derivato composto da servletToken + '?' + commandTokenEquals  */
    private static String contextPathEquals = contextPathToken + 
                                              SLASH +  
                                              servletToken + 
                                              QM + 
                                              commandTokenEquals;
    /** Base server path (e.g., "server/appname/")                              */
    private String rootPath = servletToken;
    /** Entity token (mandatory for relative paths)                             */
    private String entToken = commandToken;
    /** Command name (mandatory for relative paths)                             */
    private String commandName;
    /** Survey code (mandatory when survey provided)                            */
    private String surveyCode;
    /** 'p' parameter (part identifier)                                         */
    private String part;
    /**   * Mappa dei parametri della query string                              */
    private LinkedHashMap<String, String> mappa;
    /** Additional optional parameters                                          */
    private final Map<String, String> additionalParams;
    /** Application name constant                                               */
    private static final String APP_NAME = ConfigManager.getAppName();
    /** Valid parameter name patterns                                           */
    private static final String ENT_TOKEN_PATTERN = "^[A-Z0-9]{3,10}$";
    private static final String CMD_PATTERN = "^[A-Z_]{2,20}$";
    private static final String PART_PATTERN = "^[a-zA-Z0-9_-]{1,20}$";
    private static final String SURVEY_CODE_PATTERN = "^[0-9]{1,10}$";

    
    /* **************************************************** *
     *                      Costruttori                     *
     * **************************************************** */
    
    /**
     * <p>Costruttore senza argomenti.</p>
     * <ul>
     * <li>Istanzia la mappa che conterr&agrave; i parametri della query string</li>
     * </ul> 
     */
    public DataUrl() {
        this.additionalParams = null;
        mappa = new LinkedHashMap<>();
    }

    
    /**
     * Costruttore parametro/valore:<br /> 
     * <cite id="howTo">Inserisci il parametro <code>'parametro'</code> 
     * con il valore <code>'valore'</code> nel
     * formato parametro '&amp;parametro=valore'</cite>
     * <ul>
     * <li>Istanzia la mappa che conterr&agrave; i parametri della query string</li>
     * <li>Inizializza la lingua a stringa vuota</li>
     * </ul> 
     * Se il parametro &egrave; significativo (diverso dalla lingua), 
     * viene aggiunto nella mappa dei parametri della query string; altrimenti,
     * valorizza la lingua stessa, cio&egrave; dereferenzia la stringa vuota e
     * valorizza questo parametro con il valore passato come secondo argomento.
     * 
     * @param param il nome del parametro di cui deve essere valutato il valore ai fini del parsing
     * @param value il valore da impostare come valore del parametro
     */
    public DataUrl(final String param, 
                   final String value) {
        this.additionalParams = null;
        mappa = new LinkedHashMap<>();
        mappa.put(DataUrl.encodeURL(param), DataUrl.encodeURL(value));
    }
    
    
    /**
     * Constructor 3: Root path only
     * Generates: server/appname/
     * 
     * @param root Server name (e.g., "localhost", "server01")
     * @throws IllegalArgumentException if root is invalid
     */
    public DataUrl(String root) {
        validateRoot(root);
        this.rootPath = normalizePath(root) + APP_NAME + SLASH;
        this.entToken = null;
        this.commandName = null;
        this.surveyCode = null;
        this.part = null;
        this.additionalParams = new LinkedHashMap<>();
    }
    
    
    /**
     * Constructor 4: Relative path with entToken, commandName, survey
     * Generates: /entToken=XXX&cmd=YYY&r=ZZZ
     * 
     * @param entToken Entity token (3-10 uppercase alphanum)
     * @param commandName Command name (uppercase letters/underscores)
     * @param survey Survey bean containing code
     * @throws IllegalArgumentException if any parameter invalid
     */
    public DataUrl(String entToken, String commandName, CodeBean survey) {
        this(null, entToken, commandName, null, survey);
    }
    
    
    /**
     * Constructor 5: Relative path with entToken, commandName, part, survey  
     * Generates: /entToken=XXX&cmd=YYY&p=ZZZ&r=WWW
     * 
     * @param entToken Entity token
     * @param commandName Command name
     * @param part Part identifier
     * @param survey Survey bean
     */
    public DataUrl(String entToken, String commandName, String part, CodeBean survey) {
        this(null, entToken, commandName, part, survey);
    }
    
    
    /**
     * Constructor 6: Relative path with entToken, commandName, part, extra param, survey
     * Generates: /entToken=XXX&cmd=YYY&p=ZZZ&extra=AAA&r=WWW
     * 
     * @param entToken Entity token
     * @param commandName Command name
     * @param part Part identifier
     * @param extra Additional parameter name
     * @param survey Survey bean
     */
    public DataUrl(String entToken, String commandName, String part, 
                   String extraParam, String extraValue, CodeBean survey) {
        this(null, entToken, commandName, part, survey);
        if (!extraParam.matches(PART_PATTERN) || extraValue == null) {
            throw new IllegalArgumentException("Invalid extra parameter: " + extraParam);
        }
        additionalParams.put(extraParam, extraValue);
    }
    
    
    /**
     * Constructor 7: Relative path with entToken, commandName, part, two extra params, survey
     * Generates: /entToken=XXX&cmd=YYY&p=ZZZ&extra1=AAA&extra2=BBB&r=WWW
     */
    public DataUrl(String entToken, String commandName, String part, 
                   String extra1, String extra1Value, 
                   String extra2, String extra2Value, CodeBean survey) {
        this(null, entToken, commandName, part, survey);
        validateExtraParam(extra1, extra1Value);
        validateExtraParam(extra2, extra2Value);
        additionalParams.put(extra1, extra1Value);
        additionalParams.put(extra2, extra2Value);
    }
    
    
    /**
     * Constructor 8: Full relative path with root prefix
     * Internal constructor used by other relative constructors
     */
    private DataUrl(String root, String entToken, String commandName, 
                   String part, CodeBean survey) {
        if (root != null) {
            validateRoot(root);
            this.rootPath = normalizePath(root) + APP_NAME + "/";
        } else {
            this.rootPath = "/";
        }
        validateEntToken(entToken);
        validateCommand(commandName);
        validateSurvey(survey);
        validatePart(part);
        this.entToken = entToken;
        this.commandName = commandName;
        this.part = part;
        this.surveyCode = survey.getCode();
        this.additionalParams = new LinkedHashMap<>();
    }
    
    
    public String getUrl() {
        // Definisce una stringa dinamica da costruire tramite il contenuto della mappa
        StringBuffer url = new StringBuffer();
        // Cicla sui parametri da restituire
        Iterator<Map.Entry<String, String>> iterator = mappa.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            // Chiave e valore
            String key = entry.getKey();
            String value = entry.getValue();
            url.append(key)
               .append(EQ)
               .append(value);
            // Check if there are more elements
            if (iterator.hasNext()) {
                url.append(AMPERSAND);
            }
        }
        return url.toString();
    }
    
    
    /**
     * Generates the complete URL path string
     * 
     * @return Formatted URL (absolute or relative based on constructor used)
     */
    public String getUrl(String rootPath) {
        StringBuilder url = new StringBuilder(rootPath);
        
        // Relative path components
        if (entToken != null) {
            appendParam(url, "entToken", entToken);
            appendParam(url, "cmd", commandName);
            if (part != null) {
                appendParam(url, "p", part);
            }
            appendParam(url, "r", surveyCode);
            
            // Additional parameters
            for (Map.Entry<String, String> entry : additionalParams.entrySet()) {
                appendParam(url, entry.getKey(), entry.getValue());
            }
        }
        
        return url.toString();
    }
    
    
    /**
     * Adds an additional parameter to existing URL
     * 
     * @param param Parameter name (must match PART_PATTERN)
     * @param value Parameter value (non-null)
     * @throws IllegalArgumentException if parameters invalid
     */
    public void add(final String param, 
                    final String value) {
        validateExtraParam(param, value);
        this.put(param, value);
        additionalParams.put(param, value);
    }
         
    
    /**
     * <p>Rimuove il parametro <code>'parametro'</code> se esiste, 
     * nulla altrimenti.</p>
     * 
     * @param parametro il parametro che deve essere eliminato
     * @return <code>DataUrl</code> - dataUrl depurato del parametro indicato
     */
    public DataUrl remove(final String parametro) {
        if (parametro != null) {
            mappa.remove(parametro);
        }
        return this;
    }
    
    
    /**
     * Clears all additional parameters
     */
    public void clearAdditionalParams() {
        additionalParams.clear();
    }
    
    
    /**
     * Validates root server name
     */
    private void validateRoot(String root) {
        if (root == null || root.trim().isEmpty() || !root.matches("^[a-zA-Z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid root: " + root);
        }
    }
    
    
    /**
     * Validates entity token format
     */
    private void validateEntToken(String entToken) {
        if (!entToken.matches(ENT_TOKEN_PATTERN)) {
            throw new IllegalArgumentException("Invalid entToken: " + entToken + 
                " (must be 3-10 uppercase alphanum)");
        }
    }
    
    
    /**
     * Validates command name format
     */
    private void validateCommand(String commandName) {
        if (!commandName.matches(CMD_PATTERN)) {
            throw new IllegalArgumentException("Invalid command: " + commandName + 
                " (must be uppercase letters/underscores)");
        }
    }
    
    
    /**
     * Validates survey bean
     */
    private void validateSurvey(CodeBean survey) {
        if (survey == null || survey.getCode() == null || 
            !survey.getCode().matches(SURVEY_CODE_PATTERN)) {
            throw new IllegalArgumentException("Invalid survey code: " + survey);
        }
    }
    
    
    /**
     * Validates part parameter (nullable)
     */
    private void validatePart(String part) {
        if (part != null && !part.matches(PART_PATTERN)) {
            throw new IllegalArgumentException("Invalid part: " + part);
        }
    }
    
    
    /**
     * Validates additional parameter name/value
     */
    private void validateExtraParam(String name, String value) {
        if (!name.matches(PART_PATTERN) || value == null) {
            throw new IllegalArgumentException("Invalid extra param: " + name + "=" + value);
        }
    }
    
    
    /**
     * Appends parameter to URL in correct format
     */
    private void appendParam(StringBuilder url, String name, String value) {
        if (url.length() > 1) { // Not first param
            url.append("&");
        }
        url.append(name).append("=").append(encodeValue(value));
    }
    
    
    /**
     * Normalizes and encodes URL path component
     */
    private String normalizePath(String path) {
        return path.replaceAll("[/\\\\]+", "/").replaceAll("/+$", "");
    }
    
    
    /**
     * URL-encodes parameter values (simplified for Italian charset)
     */
    private String encodeValue(String value) {
        return value.replace("&", "%26").replace("=", "%3D").replace(" ", "+");
    }
    
    
    /**
     * Required CodeBean interface/class for survey codes
     */
    public static class CodeBean {
        private String code;
        
        public CodeBean(String code) {
            if (!code.matches("^[0-9]{1,10}$")) {
                throw new IllegalArgumentException("Invalid code: " + code);
            }
            this.code = code;
        }
        
        public String getCode() {
            return code;
        }
    }

    
    /* **************************************************** *
     *                  Getter  e  Setter                   *
     * **************************************************** */
    
    /**
     * <p>Getter per il token usato per rappresentare la Main servlet</p>
     * 
     * @return <code>String</code> - nome della servlet nel context path
     */
    public static String getServletToken() {
        return servletToken;
    }

    
    /**
     * <p>Setter per il token usato per rappresentare la Main servlet.</p> 
     * Gli url prodotti da DataUrl sono formati da:
     * <pre>'context path' + servletToken + ...</pre>
     * <ul>
     * <li>
     * <code>'context path'</code> si definisce con {@link #setContextPath(String)}.
     * </li>
     * <li> 
     * Se <code>'context path' == ''</code> <strong>e</strong> 
     * <code>servletToken == ''</code>, si otterr&agrave; sempre un indirizzo
     * relativo.
     * 
     * @param servletToken nome da dare alla servlet centrale del server web se diverso da 'main'
     */
    public static void setServletToken(String servletToken) {
        if (servletToken != null) {
            DataUrl.servletToken = servletToken;
            String s = DataUrl.contextPathToken + DataUrl.servletToken;
            if (s.equals("//")) // Sanity check
                s = "/";
            DataUrl.contextPathEquals = s + "?" + DataUrl.commandTokenEquals;
        }
    }

    
    /**
     * <p>Setter per il context path da usare nel DataUrl.</p> 
     * Solitamente &egrave; sufficiente inizializzare la classe, 
     * chiamando questo metodo come 
     * <code>DataUrl.setContextPath(getServletContext().getContextPath());</code>
     * 
     * <cite id="http://docs.oracle.com/javaee/5/api/index.html?javax/servlet/ServletContext.html">
     * <p>ServletContext.getContextPath() restituisce il context path 
     * dell'applicazione web.</p>
     * <p>Il context path &egrave; la porzione dell'URI richiesto che &egrave;
     * usata per selezionare il contesto della richiesta.
     * Il context path &egrave; sempre la prima parte nella richiesta di un URI.
     * Il path inizia con un carattere di slash "/" ma non finisce con uno slash.
     * Per le servlet che si trovano nel contesto di default (root), questo metodo
     * restituisce stringa vuota ("").</p>
     * <p>&Egrave; possibile che in un servlet container uno stesso contesto 
     * possa corrispondere a pi&uacute; di un context path.
     * In tal caso, il metodo HttpServletRequest.getContextPath() 
     * restituir&agrave; l'attuale context path utilizzato dalla richiesta, 
     * e questo potrebbe differire dal path restituito dal metodo in questione.
     * Il context path restituito da questo metodo dovrebbe essere considerato 
     * come il context path preferito dell'applicazione web.</cite> 
     * <p>Di default il suo valore &egrave; stringa vuota ("").</p>
     * <p>vedi anche: http://docs.oracle.com/javaee/1.3/api/javax/servlet/http/HttpServletRequest.html</p>
     * 
     * @param contextPath valore da dare al context path
     */
    public static void setContextPath(String contextPath) {
        if (contextPath != null) {
            DataUrl.contextPathToken = contextPath;
            /* necessario per riaggiustare
             * DataUrl.contextPathAndservletTokenAndCommandTokenEquals:         */
            DataUrl.setServletToken(DataUrl.servletToken); 
        }
    }

    
    /* **************************************************** *
     *      Metodi per la manipolazione dei caratteri       *
     * **************************************************** */
    
    /**
     * <p>Metodo statico per convertire i caratteri IS0-8859-1 nel formato adatto
     * per gli URL.</p>
     * 
     * @param s Stringa da convertire
     * @return <code>Stringa</code> - la string input codificata correttamente per essere usata in un URL
     */
    public static String encodeURL(final String s) {
        String url = null;
        try {
            url = java.net.URLEncoder.encode(s, "UTF-8");
        } catch (final java.io.UnsupportedEncodingException ignored) {
            /* questa eccezione viene ignorata in quanto impossibile 
             * dal momento che UTF-8 e' una codifica obbligatoria    */
        }
        return url;
    }

    
    /**
     * Restituisce la stringa 'input' codificata in modo tale da poter inserire
     * la stringa all'interno di un documento html evitando l'interpretazione
     * dei caratteri speciali. Rimpiazza tutti i seguenti caratteri speciali
     * html con le loro entit&agrave;: 
     * <ul>
     * <li><code>&amp;</code> diventa <code>&amp;amp;</code></li> 
     * <li><code>"</code> diventa <code>&amp;quot;</code></li> 
     * <li>' diventa &amp;#039; </li>
     * <li>\n diventa &lt;br /&gt;</li> 
     * </ul>
     * Se il parametro
     * 'tutti' è true, allora anche i caratteri &lt; e &gt; vengono tradotti. In
     * questo modo la funzione converte eventuali tag html presenti in input che
     * vengono resi caratteri nella stringa restituita.
     * 
     * @param input
     *            stringa da codificare
     * @param tutti
     *            i caratteri &lt; e &gt; vengono tradotti come entit&agrave; html
     * @return <code>String</code> - la string input codificata in html
     */
    public static String encodeHTML(final String input, 
                                    final boolean tutti) {
        /* Uso le espressioni regolari per riuscire a catturare
         * il maggior numero di casi.                                           */
        String risultato = null;
        if (input != null) {
            // sostituisco tutti gli & con &amp;
            // per questioni di efficienza... prima rimpiazzo tutti gli & anche
            // se già
            // convertiti... e poi pulisco le doppie sostituzioni
            risultato = input.replaceAll("&", "&amp;").replaceAll("&amp;amp;", "&amp;");
            // sostituisco tutte le " con &quot;
            risultato = risultato.replaceAll("\"", "&quot;");
            // sostituisco tutte le ' con &#039;;
            risultato = risultato.replaceAll("'", "&#039;");
            if (tutti) {
                // sostituisco tutte le < con &lt;
                risultato = risultato.replaceAll("<", "&lt;");
                // sostituisco tutte le > con &gt;
                risultato = risultato.replaceAll(">", "&gt;");
            }
            // sostituisco tutte i new line con <br />
            risultato = risultato.replaceAll("\n|\r\n|\r", "<br />");
        }
        return (risultato);
    }

    
    /**
     * Overloading di encodeHTML(s,b) con b=true
     * 
     * @see #encodeHTML(String, boolean)
     * @param input stringa da codificare
     * @return stringa con i caratteri speciali HTML codificati come entità HTML
     */
    public static String encodeHTML(final String input) {
        return DataUrl.encodeHTML(input, true);
    }


    /* **************************************************** *
     *      Metodi per la manipolazione dei parametri       *
     * **************************************************** */
    
    /**
     * <p>Inserisce la coppia 
     * <pre>'parametro'='valore'</pre> sostituendo eventualmente il valore del
     * parametro, se gi&agrave; presente.</p>
     * 
     * @param parametro il parametro il cui valore deve essere inserito
     * @param valore    il valore da inserire nel parametro indicato
     * @return <code>DataUrl</code> - dataUrl modificato nel valore del parametro indicato
     */
    public DataUrl put(final String parametro, 
                       final String valore) {
        if (parametro != null)
            mappa.put(encodeURL(parametro), encodeURL(valore));
        return this;
    }
    

    /**
     * <p>Inserisce la coppia 
     * <pre>'parametro'='valore numerico'</pre> sostituendo eventualmente 
     * il valore numerico del parametro, se gi&agrave; presente.</p>
     * <p>Quantunque l'argomento passato come valore del parametro sia un numero,
     * esso viene inserito nella tabella dei parametri sotto forma di String.</p>
     * 
     * @param parametro il parametro il cui valore deve essere inserito
     * @param valore    il valore da inserire nel parametro indicato
     * @return <code>DataUrl</code> - dataUrl modificato nel valore del parametro indicato
     */
    public DataUrl put(final String parametro, 
                       final int valore) {
        if (parametro != null)
            mappa.put(encodeURL(parametro), Integer.toString(valore));
        return this;
    }

    
    /**
     * <p>Aggiunge la coppia <pre>'parametro'='valore'</pre>
     * dove <code>'valore'</code> &egrave; un numero.</p> 
     * <p>Quantunque l'argomento passato come valore del parametro sia un numero,
     * esso alla fine verr&agrave; inserito nella tabella dei parametri 
     * sotto forma di String.</p>
     * <p>
     * Facade di
     * {@link #put(String, int)}</p>
     * 
     * @param parametro il parametro il cui valore deve essere aggiunto
     * @param valore    il valore da inserire nel parametro indicato
     * @return <code>DataUrl</code> - dataUrl modificato nel valore del parametro indicato
     */
    public DataUrl add(final String parametro, 
                       final int valore) {
        this.put(parametro, valore);
        return this;
    }
    

    /**
     * <p>Imposta il parametro <code>'parametro'</code> 
     * al valore <code>'valore'</code></p>
     * 
     * @param parametro il parametro il cui valore deve essere aggiornato
     * @param valore    il valore da inserire nel parametro indicato
     * @return <code>DataUrl</code> - dataUrl modificato nel valore del parametro indicato
     */
    public DataUrl set(final String parametro, 
                       final String valore) {
        this.put(parametro, valore);
        return this;
    }

    
    /**
     * <p>Imposta il parametro <code>'parametro'</code> 
     * al valore <code>'valore'</code>
     * dove <code>'valore'</code> &egrave; un numero.</p>
     * <p>Quantunque l'argomento passato come valore del parametro sia un numero,
     * esso alla fine verr&agrave; inserito nella tabella dei parametri 
     * sotto forma di String.</p>
     * 
     * @param parametro il parametro il cui valore deve essere aggiornato
     * @param valore    il valore da inserire nel parametro indicato
     * @return <code>DataUrl</code> - dataUrl modificato nel valore del parametro indicato
     */
    public DataUrl set(final String parametro, final int valore) {
        this.put(parametro, valore);
        return this;
    }

    
    /**
     * <p>merge(DataUrl d) restituisce un DataUrl dato dall'unione del DataUrl 'd'
     * con i valori presenti nel data url attuale.</p> 
     * <p>Lo stato interno sia di <code><strong>this</strong></code>
     * sia di <code><strong>'d'</strong></code> non vengono modificati.</p>
     * 
     * @param dataU
     *            dataUrl in ingresso
     * @return <code>DataUrl</code> - DataUrl modificato
     */
    public DataUrl merge(final DataUrl dataU) {
        final LinkedHashMap<String, String> temp = new LinkedHashMap<>(mappa);
        temp.putAll(dataU.mappa);
        final DataUrl nuovo = new DataUrl();
        nuovo.mappa = temp;
        return nuovo;
    }


    /* **************************************************** *
     *         Altri Metodi  *
     * **************************************************** */
      
    /**
     * 
     * @param scheme
     * @return
     * @throws IllegalArgumentException
     */
    public static String parseScheme(String scheme)
                       throws IllegalArgumentException {
        if (!scheme.equals("http") && !scheme.equals("https")) {
            /* Se viene passato uno schema contenente i separatori di protocollo, 
             * ammetto la buona fede e ti grazio, facendo io la correzione
             * (mortacci tua)                                                   */
            if (scheme.equals("http://") || scheme.equals("https://")) {
                return scheme.substring(0,  scheme.indexOf("://"));
            }
            /* Altrimenti vuol dire che ci stai a provà, 
             * e ti sollevo un'eccezione!!!                                     */
            else
                throw new IllegalArgumentException("Valore dell'argomento \'scheme\' (" + scheme + ") non valido!!\n");
        }
        return scheme;
    }
    
    
    private String parseDomain(String domain) 
                        throws IllegalArgumentException {
        if (!domain.startsWith("http") && !domain.startsWith("www")) {
            // Che mi stai a passa'??
            throw new IllegalArgumentException("Valore dell'argomento \'dominio\' (" + domain + ") non valido!!\n");
        }
        // Recupera il dominio per la costruzione dell'URL assoluto a partire da 'www'
        String parsedDomain = domain.substring(domain.indexOf("www"), domain.length());
        return parsedDomain;
    }
    
}
