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

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.exception.CommandException;


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
 * dalla localizzazione del client o da un valore di default).</p> 
 * 
 * <p>Created on  Thu 27 Feb 2025 12:45:24 PM CET 2025</p>
 * 
 * @author Roberto Posenato
 * @author <a href="mailto:giovanroberto.torre@univr.it">Giovanroberto Torre</a>
 */
public class DataUrl implements Constants {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
     */
    private static final long serialVersionUID = 1413064779905762448L;
    /**
     * Token per riconoscere la lingua
     */
    private static String langToken = "lang";
    /**
     * ContextPath dell'applicazione privato dello '/' iniziale.<br>
     * Si ricorda che il context path inizia sempre con / e non termina mai con
     * /.<br>
     * Il valore da porre in questa variabile &egrave;
     * <code>
     * javax.servlet.GenericServlet.getServletContext().getContextPath()
     * </code>
     */
    private static String contextPathToken = VOID_STRING;
    /**
     * Token per definire la servlet centrale
     */
    private static String servletToken = ConfigManager.getAppName();
    /**
     * Token per la classe command
     */
    private static String commandToken = ConfigManager.getEntToken();

    /**
     * Token per la classe command con '=' attaccato in fondo.
     */
    private static String commandTokenEquals = commandToken + "=";
    
    /**
     * Token derivato composto da servletToken + '?' + commandTokenEquals
     */
    private static String contextPathAndservletTokenAndCommandTokenEquals = contextPathToken + 
                                                                            "/" + 
                                                                            servletToken + 
                                                                            "?" + 
                                                                            commandTokenEquals;
    
    /**
     * Rappresenta la lingua che deve essere esplicitata nell'url. Va sempre
     * propagato se esiste. Viene gestito in modo separato rispetto agli altri
     * parametri e non compare mai in {@link #mappa}.
     */
    private String lang;

    /**
     * Mappa dei parametri della query string
     */
    private LinkedHashMap<String, String> mappa;

    
    /* **************************************************** *
     *                      Costruttori                     *
     * **************************************************** */
    /**
     * <p>Costruttore senza argomenti.</p>
     * <ul>
     * <li>Istanzia la mappa che conterr&agrave; i parametri della query string</li>
     * <li>Inizializza la lingua a stringa vuota</li>
     * </ul> 
     */
    public DataUrl() {
        mappa = new LinkedHashMap<>();
        lang = VOID_STRING;
    }

    
    /**
     * Costruttore:<br /> 
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
     * @param parametro il nome del parametro di cui deve essere valutato il valore ai fini del parsing
     * @param valore il valore da impostare come valore del parametro
     */
    public DataUrl(final String parametro, 
                   final String valore) {
        mappa = new LinkedHashMap<>();
        lang = new String("");
        if (parametro.equalsIgnoreCase(langToken)) {
            lang = valore;
        } else {
            mappa.put(DataUrl.encodeURL(parametro), DataUrl.encodeURL(valore));
        }
    }

    
    /**
     * <p>Costruttore da query string.</p>
     * Costruisce l'oggetto prendendo i parametri e i valori dalla queryString, 
     * eliminando, eventualmente, la sottostringa iniziale 'ent=*&'
     * 
     * @param queryString la query string completa 'as is'
     */
    public DataUrl(String queryString) {
        mappa = new LinkedHashMap<>();
        lang = "";
        int i = 0, j = -1;
        String nome, valore;
        // Controllo sull'input
        if (queryString != null) {
            try {
                queryString = java.net.URLDecoder.decode(queryString, "UTF-8");
            } catch (final java.io.UnsupportedEncodingException ignored) {
                /* questa eccezione è ignorata in quanto impossibile 
                 * dal momento che UTF-8 è una codifica obbligatoria    */
            }
        }
        if (queryString != null && queryString.length() > 0) {
            if (queryString.startsWith(commandTokenEquals)) {
                i = queryString.indexOf('&'); // vado al primo parametro dopo ent=*
            }
            if (i != -1) {  // ci sono parametri da usare!
                if (i > 0)
                    i++;    // mi posiziono al primo carattere utile.
                while ((j = queryString.indexOf('=', i)) != -1) { // c'e` un
                    // parametro
                    // (string=string)
                    // System.err.println("Valori per nome di i, j "+i+", "+j);
                    nome = queryString.substring(i, j);
                    i = j + 1;
                    j = queryString.indexOf('&', i);
                    if (j == -1)
                        j = queryString.length();// e' l'ultimo
                    // parametro
                    // System.err.println("Valori per valore di i, j "+i+", "+j);
                    valore = queryString.substring(i, j);
                    i = j + 1;
                    if (nome.startsWith("?")) {
                        // Capita che un URL sia stato formato accodando due
                        // queryString (che iniziano con ?) per errore.
                        // Per esempio: URL che torna dalla banca per la
                        // notifica di pagamento con carta di credito.
                        // Aggiusto per semplificare il resto del codice.
                        // [24/05/2014] Posenato
                        nome = nome.substring(1);
                    }
                    nome = encodeURL(nome);
                    valore = encodeURL(valore);
                    if (nome.equalsIgnoreCase(langToken)) {
                        lang = valore;
                    } else {
                        mappa.put(nome, valore);
                    }
                }
            }
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
            DataUrl.contextPathAndservletTokenAndCommandTokenEquals = s + "?" + DataUrl.commandTokenEquals;
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
        if (parametro != null && parametro.equalsIgnoreCase(langToken)) {
            lang = valore;
        } else {
            if (parametro != null)
                mappa.put(encodeURL(parametro), encodeURL(valore));
        }
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
     * <p>Aggiunge la coppia <pre>'parametro'='valore'</pre></p>
     * <p>
     * Facade di
     * {@link #put(String, String)}</p>
     * 
     * @param parametro il parametro il cui valore deve essere aggiunto
     * @param valore    il valore da inserire nel parametro indicato
     * @return <code>DataUrl</code> - dataUrl modificato nel valore del parametro indicato
     */
    public DataUrl add(final String parametro, 
                       final String valore) {
        this.put(parametro, valore);
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
     * <p>Rimuove il parametro <code>'parametro'</code> se esiste, 
     * nulla altrimenti.</p>
     * 
     * @param parametro il parametro che deve essere eliminato
     * @return <code>DataUrl</code> - dataUrl depurato del parametro indicato
     */
    public DataUrl remove(final String parametro) {
        if (parametro != null) {
            if (parametro.equalsIgnoreCase(langToken))
                lang = new String("");
            else
                mappa.remove(parametro);
        }
        return this;
    }

    
    /**
     * <p>Restituisce il parametro <code>'parametro'</code> se esiste, 
     * stringa "" (vuota) altrimenti.</p> 
     * <p>Il formato della stringa restituita &egrave; pari a quello restituito da
     * {@link java.net.URLEncoder}.</p>
     * 
     * @param parametro
     *            da ricercare. Se null, ritorna "";
     * @return <code>String</code> - il valore di 'parametro' se esiste, stringa "" altrimenti.
     */
    public String getEncoded(final String parametro) {
        if (parametro == null)
            return "";
        if (parametro.equalsIgnoreCase(langToken))
            return lang;
        String value = mappa.get(parametro);
        if (value == null)
            return "";
        return value;
    }
    

    /**
     * <p>Restituisce il parametro <code>'parametro'</code> se esiste, 
     * stringa "" (vuota) altrimenti.</p>
     * 
     * @param parametro
     *            da ricercare. Se null, ritorna "";
     * @return <code>String</code> - il valore di 'parametro' se esiste, stringa "" altrimenti.
     */
    public String get(final String parametro) {
        String value = this.getEncoded(parametro);
        if (value.isEmpty())
            return value;
        try {
            return java.net.URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
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
     *         Metodi per la generazione degli URL          *
     * **************************************************** */
    /*
     * <p>Questo metodo viene deprecato perch&eacute; 
     * il nome &egrave; sbagliato.</p> 
     * <p>Usare {@link #getQueryString()}.</p>
     * 
     * @see #getQueryString
     * @return vedi {@link #getQueryString()}
     *
    @Deprecated
    public String getUrl() {
        return this.getQueryString();
    }*/
    
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
        /*
        for (java.util.Map.Entry<String, String> entry : mappa.entrySet()) {
            // Chiave e valore
            String key = entry.getKey();
            String value = entry.getValue();
            url.append(key)
               .append(EQ)
               .append(value);
            if (mappa.)
        }*/
        return url.toString();
    }


    /**
     * <p>Restituisce il contenuto di questo oggetto in codifica html:
     * <pre>param1=value1&amp;param2=value2&...</pre>
     * Se non ci sono parametri, restituisce stringa vuota ("").</p>
     * 
     * @return <code>DataUrl</code> - DataUrl corrente in formato HTML senza '?' inziale.
     */
    public String getQueryString() {
        final StringBuffer buffer = new StringBuffer();
        for (Entry<String, String> entry : mappa.entrySet()) {
            buffer.append(entry.getKey() + "=" + entry.getValue() + "&amp;");
        }
        // Il ciclo while ha aggiunto un &amp; di troppo
        if (mappa.size() > 0)
            buffer.setLength(buffer.length() - 5);
        buffer.append(this.printLang());
        return buffer.toString();
    }


    /**
     * <p>Restituisce l'url: 
     * <pre>DataUrl.contextPathAndservletTokenAndCommandTokenEquals + 
     *        valore di 'ent'</pre>
     * senza modificare lo stato interno.</p>
     * <p>In altri termini, genera link del tipo:
     * <pre>/main?ent=persona</pre>
     * che vanno a invocare la ServletCommand senza passarle ulteriori valori.</p>
     * 
     * @param ent valore del 'Token', cio&egrave; della ServletCommand richiesta
     * @return <code>String</code> - l'url: DataUrl.contextPathAndservletTokenAndCommandTokenEquals + 'ent' senza modificare lo stato interno
     */
    public String getUrl(final String ent) {
        return this.getUrl(ent, false);
    }


    /**
     * <p>Restituisce l'url 
     * <pre>DataUrl.contextPathAndservletTokenAndCommandTokenEquals +
     *        valore di 'ent' +
     *        valore di &quot;parametri interni&quot;</pre> 
     * senza modificare lo stato interno se 'ext' &egrave; <code>true</code>,<br />
     * <strong><em>oppure</em></strong> l'url 
     * <pre>DataUrl.contextPathAndservletTokenAndCommandTokenEquals + 
     *       'ent'</pre> 
     * altrimenti.</p>
     * <p>Esempio:
     * <ul>
     * <li>se contextPathToken = new String("");</li>
     * <li>e servletToken = "main"; //(Token per definire la servlet centrale)</li>
     * <li>e commandTokenEquals = "ent" + "="; //(Token per la classe command con '=' attaccato in fondo)</li>
     * <li>allora contextPathAndservletTokenAndCommandTokenEquals = "" + "/" + "main" + "?" + ent=;</li>
     * </ul>
     * cio&egrave; vale: <pre>/main?ent=</pre>
     * A questo campo, questo metodo aggiunge valore di 'ent' e valore 
     * di parametri interni se 'ext' &egrave; 'true'; quindi, nell'esempio, 
     * posti ent a "persona" e parmetri interni a "id=100", si avr&agrave; 
     * come valore di ritorno:
     * <pre>/main?ent=persona&amp;id=100</pre></p>
     * 
     * @param ent valore del 'Token', cio&egrave; della ServletCommand richiesta
     * @param ext boolean flag che specifica se aggiungere tutti i parametri aggiuntivi o meno
     * @return <code>String</code> - valore di un link ipertestuale costruito sulla base degli argomenti passati, da stampare come valore di un attributo html <code>href</code>
     */
    public String getUrl(final String ent, 
                         final boolean ext) {
        final StringBuffer temp = new StringBuffer(new String(""));
        if ((ent != null) && (ent.length() > 0)) {
            temp.append(contextPathAndservletTokenAndCommandTokenEquals);
            temp.append(encodeURL(ent));
            if (ext) {
                if (mappa.size() > 0) {
                    temp.append("&amp;");
                    temp.append(this.getQueryString());
                } else {
                    temp.append(this.printLang());
                }
            } else {
                temp.append(this.printLang());
            }
        }
        return temp.toString();
    }


    /**
     * <p>Restituisce la stringa: 
     * <pre>DataUrl.contextPathAndservletTokenAndCommandTokenEquals +
     *        valore di 'ent' +
     *        &quot;&amp;&quot; +
     *        'data' (valore di &quot;parametri interni&quot;)</pre>
     * in codifica html, senza modificare lo stato interno.</p>
     * 
     * @param ent valore del 'Token', cio&egrave; della ServletCommand richiesta
     * @param data
     *            una sequenza di parametro=valore separati da & (singolo
     *            carattere)
     * @return <code>String</code> - valore di un link ipertestuale costruito sulla base degli argomenti passati, da stampare come valore di un attributo html <code>href</code>
     */
    public String getUrl(final String ent, 
                         final String data) {
        final DataUrl tempData = new DataUrl(data);
        // tempData viene utilizzato per gestire data ignorando la lang
        // (se data ha lang al suo interno ora viene gestito in tempData.lang)
        tempData.remove(langToken);
        if ((ent != null) && (ent.length() > 0)) {
            if ((data != null) && (data.length() > 0))
                return DataUrl.contextPathAndservletTokenAndCommandTokenEquals
                        + encodeURL(ent) + "&amp;" + tempData.getQueryString()
                        + this.printLang();
            return DataUrl.contextPathAndservletTokenAndCommandTokenEquals
                    + encodeURL(ent) + this.printLang();
        }
        if ((data != null) && (data.length() > 0))
            return tempData.getQueryString() + this.printLang();
        return new String("");
    }


    /**
     * <p>Restituisce la stringa: 
     * <pre>DataUrl.contextPathAndservletTokenAndCommandTokenEquals +
     *        valore di 'ent' +
     *        &quot;&amp;&quot; +
     *        'data' (valore di &quot;parametri interni&quot;) +
     *        &quot;buffer interno&quot;
     *        </pre>
     * senza modificare lo stato interno se
     * <pre>'ext' &egrave; true</pre> 
     * <strong><em>oppure</em></strong> la string 
     * <pre>DataUrl.contextPathAndservletTokenAndCommandTokenEquals +
     *        valore di 'ent' +
     *        &quot;&amp;&quot; +
     *        'data' (valore di &quot;parametri interni&quot;)</pre>
     * altrimenti.</p>
     * 
     * @param ent valore del 'Token', cio&egrave; della ServletCommand richiesta
     * @param data
     *            una sequenza di parametro=valore separati da & (singolo
     *            carattere).
     * @param ext boolean flag che specifica se aggiungere tutti i parametri aggiuntivi o meno
     * @return <code>String</code> - valore di un link ipertestuale costruito sulla base degli argomenti passati, da stampare come valore di un attributo html <code>href</code>
     */
    public String getUrl(final String ent, 
                         final String data, 
                         final boolean ext) {
        if (ext) {
            final DataUrl temp = this.merge(new DataUrl(data));
            temp.lang = lang;
            return temp.getUrl(ent, true);
        }
        return this.getUrl(ent, data);
    }


    /**
     * <p>Restituisce la stringa: 
     * <pre>DataUrl.servletToken +
     *        &quot;?&quot; +
     * DataUrl.commandToken +
     *        encodeURL(ent) + 
     *        &quot;parametri interni&quot;</pre>
     * senza modificare lo stato interno se
     * <pre>'ext' &egrave; true</pre> 
     * <strong><em>oppure</em></strong> la string 
     * <pre>DataUrl.servletToken +
     *        &quot;?&quot; +
     * DataUrl.commandToken +
     *        encodeURL(ent) + 
     *        &quot;ent&quot;</pre>
     * altrimenti.</p>
     * 
     * @param ent valore del 'Token', cio&egrave; della ServletCommand richiesta
     * @param ext boolean flag che specifica se aggiungere tutti i parametri aggiuntivi o meno
     * @return <code>String</code> - l'url DataUrl.servletToken + "?" + DataUrl.commandToken + "=" +
     *         encodeURL(ent) + "parametri interni" senza modificare lo stato
     *         interno se 'ext' è true, l'url DataUrl.servletToken + "?" +
     *         DataUrl.commandToken + "=" + encodeURL(ent) + 'ent' altrimenti
     */
    public String getUrlWithoutContext(final String ent, 
                                       final boolean ext) {
        final StringBuffer temp = new StringBuffer("");
        if ((ent != null) && (ent.length() > 0)) {
            temp.append(DataUrl.servletToken + "?" + DataUrl.commandToken + "="
                    + encodeURL(ent));
            if (ext) {
                if (mappa.size() > 0) {
                    temp.append("&amp;" + this.getQueryString());
                } else {
                    temp.append(this.printLang());
                }
            } else {
                temp.append(this.printLang());
            }
        }
        return temp.toString();
    }


    /**
     * <p>Restituisce la stringa: 
     * <pre>DataUrl.servletToken +
     *        &quot;?&quot; +
     * DataUrl.commandToken +
     *        &quot;=&quot; +
     *        encodeURL(ent) +
     *        &quot;&amp;&quot; +   
     *        &quot;data&quot;</pre>
     * in codifica html senza modificare lo stato interno.</p>
     * 
     * @param ent valore del 'Token', cio&egrave; della ServletCommand richiesta
     * @param data
     *            una sequenza di parametro=valore separati da & (singolo
     *            carattere).
     * @return <code>String</code> - la stringa DataUrl.servletToken + "?" + DataUrl.commandToken +
     *         "=" + encodeURL(ent) + "&" + 'data' in codifica html, senza
     *         modificare lo stato interno.
     */
    public String getUrlWithoutContext(final String ent, 
                                       final String data) {
        final DataUrl tempData = new DataUrl(data);
        // tempData viene utilizzato per gestire data ignorando la lang
        // (se data ha lang al suo interno ora viene gestito in tempData.lang)
        tempData.remove(langToken);
        if ((ent != null) && (ent.length() > 0)) {
            if ((data != null) && (data.length() > 0))
                return DataUrl.servletToken + "?" + DataUrl.commandToken + "="
                        + encodeURL(ent) + "&amp;" + tempData.getQueryString()
                        + this.printLang();
            return DataUrl.servletToken + "?" + DataUrl.commandToken + "="
                    + encodeURL(ent) + this.printLang();
        }
        if ((data != null) && (data.length() > 0))
            return tempData.getQueryString() + this.printLang();
        return new String("");
    }

    
    /**
     * <p>Restituisce la stringa: 
     * <pre>DataUrl.servletToken +
     *        &quot;?&quot; +
     * DataUrl.commandToken +
     *        &quot;=&quot; +
     *        encodeURL(ent) + 
     *        &quot;&amp;&quot; +
     *        &quot;data&quot; +
     *        &quot;buffer interno&quot;</pre>
     * senza modificare lo stato interno se
     * <pre>'ext' &egrave; true</pre> 
     * <strong><em>oppure</em></strong> la string 
     * <pre>DataUrl.servletToken +
     *        &quot;?&quot; +
     * DataUrl.commandToken +
     *        &quot;=&quot; +
     *        encodeURL(ent) + 
     *        &quot;ent&quot; +
     *        &quot;&amp;&quot; +
     *        &quot;data&quot;</pre>
     * altrimenti.</p>
     * 
     * @param ent valore del 'Token', cio&egrave; della ServletCommand richiesta
     * @param data
     *            una sequenza di parametro=valore separati da & (singolo
     *            carattere).
     * @param ext boolean flag che specifica se aggiungere tutti i parametri aggiuntivi o meno
     * @return <code>String</code> - DataUrl.servletToken + "?" + DataUrl.commandToken + "=" +
     *         encodeURL(ent) + "&" + 'data' + "buffer interno" senza modificare
     *         lo stato interno se 'ext' è true, la string DataUrl.servletToken
     *         + "?" + DataUrl.commandToken + "=" + encodeURL(ent) + 'ent' +
     *         "&" + 'data' altrimenti.
     */
    public String getUrlWithoutContext(final String ent, 
                                       final String data,
                                       final boolean ext) {
        if (ext) {
            final DataUrl temp = this.merge(new DataUrl(data));
            temp.lang = lang;
            return temp.getUrlWithoutContext(ent, true);
        }
        return this.getUrlWithoutContext(ent, data);
    }

  
    /**
     * NON pu' usare setContextPath o setServletToken altrimenti cambierebbe
     * lo stato interno del dataurl, che se venisse usato da quel momento
     * in poi genererebbe link con una logica diversa da quella usata 
     * fino a prima della chiamata di questo metodo.
     * Per questo motivo, questo metodo non è proprio elegantissimo
     * (diciamo pure che è un po' "tarocco") perché si basa sull'
     * assunto che servletToken sia già stata inizializzata a / e per essere
     * sicuro che la navigazione vada in main la cabla (d'altra parte, se la
     * va a prendere dal web.xml, sotto dol e fol, che non usano più "main", 
     * non funzionerebbe; se la va a prendere dalla variabile statica
     * della presente classe, siccome esiste un setter per tale variabile, 
     * non è detto che il valore non ne sia stato alterato: per questo cambiamo
     * la logica, e nel momento in cui cambieranno le regole di navigazione
     * di aol, cambieremo di nuovo il corpo del metodo!
     * 
     * @param ent
     * @param ext
     * @param contextPathAndservletTokenAndCommandTokenEquals
     * @return
     */
    private String getUrl(final String ent, 
                          final boolean ext,
                          final String contextPathAndservletTokenAndCommandTokenEquals) {
        final StringBuffer temp = new StringBuffer(new String(""));
        if ((ent != null) && (ent.length() > 0)) {
            temp.append(contextPathAndservletTokenAndCommandTokenEquals);
            temp.append(encodeURL(ent));
            if (ext) {
                if (mappa.size() > 0) {
                    temp.append("&amp;");
                    temp.append(this.getQueryString());
                } else {
                    temp.append(this.printLang());
                }
            } else {
                temp.append(this.printLang());
            }
        }
        return temp.toString();
    }

    
    private String getUrl(final String ent, 
                          final String data, 
                          final String contextPathAndservletTokenAndCommandTokenEquals) {
        final DataUrl tempData = new DataUrl(data);
        // tempData viene utilizzato per gestire data ignorando la lang
        // (se data ha lang al suo interno ora viene gestito in tempData.lang)
        tempData.remove(langToken);
        if ((ent != null) && (ent.length() > 0)) {
            if ((data != null) && (data.length() > 0))
                return contextPathAndservletTokenAndCommandTokenEquals +
                       encodeURL(ent) + 
                       "&amp;" + 
                       tempData.getQueryString() + 
                       this.printLang();
            return contextPathAndservletTokenAndCommandTokenEquals + 
                   encodeURL(ent) + 
                   this.printLang();
        }
        if ((data != null) && (data.length() > 0))
            return tempData.getQueryString() + this.printLang();
        return new String("");
    }


    /**
     * 
     * 
     * @param ent
     * @param ext
     * @param scheme
     * @return
     * @throws CommandException
     */
    public String getAbsoluteAolUrl(final String ent, 
                                    final boolean ext,
                                    String scheme) 
                             throws CommandException {
        String protocol = null;
        // Controllo sull'input
        try {
            protocol = parseScheme(scheme);
        } catch (IllegalArgumentException iae) {
            throw new CommandException(iae.getMessage(), iae);
        }
        String contextPathAolToken = protocol + "://";
        String contextPathAndservletTokenAndCommandTokenEquals = contextPathAolToken + 
                                                                 "/" +
                                                                 "main" + 
                                                                 "?" + 
                                                                 commandTokenEquals;
        return this.getUrl(ent, ext, contextPathAndservletTokenAndCommandTokenEquals);
    }

    
    public String getAbsoluteAolUrl(final String ent, 
                                    final String data,
                                    String scheme) 
                             throws CommandException {
        String protocol = null;
        // Controllo sull'input
        try {
        	protocol = parseScheme(scheme);
        } catch (IllegalArgumentException iae) {
            throw new CommandException(iae.getMessage(), iae);
        }
        String contextPathAolToken = protocol + "://";
        String contextPathAndservletTokenAndCommandTokenEquals = contextPathAolToken + 
                                                                 "/" +
                                                                 "main" + 
                                                                 "?" + 
                                                                 commandTokenEquals;
        return this.getUrl(ent, data, contextPathAndservletTokenAndCommandTokenEquals);
    }

    
    public String getAbsoluteFolUrl(final String ent, 
                                    final boolean ext,
                                    String scheme,
                                    String domain) 
                             throws CommandException {
        String protocol = null;
        // Controllo sull'input
        try {
        	protocol = parseScheme(scheme);
        } catch (IllegalArgumentException iae) {
            throw new CommandException(iae.getMessage(), iae);
        }
        String contextPathFolToken = protocol + "://" + parseDomain(domain);
        String contextPathAndservletTokenAndCommandTokenEquals = contextPathFolToken + 
                                                                 "/" +
                                                                 "?" + 
                                                                 commandTokenEquals;
        return this.getUrl(ent, ext, contextPathAndservletTokenAndCommandTokenEquals);
    }


    public String getAbsoluteFolUrl(final String ent, 
                                    final String data,
                                    String scheme, 
                                    String domain) 
                             throws CommandException {
        String protocol = null;
        // Controllo sull'input
        try {
        	protocol = parseScheme(scheme);
        } catch (IllegalArgumentException iae) {
            throw new CommandException(iae.getMessage(), iae);
        }
        String contextPathFolToken = protocol + "://" + parseDomain(domain);
        String contextPathAndservletTokenAndCommandTokenEquals = contextPathFolToken + 
                                                                 "/" +
                                                                 "?" + 
                                                                 commandTokenEquals;
        return this.getUrl(ent, data, contextPathAndservletTokenAndCommandTokenEquals);
    }


    public String getAbsoluteDolUrl(final String ent, 
                                    final boolean ext,
                                    String scheme,
                                    String domain) 
                             throws CommandException {
        String protocol = null;
        // Controllo sull'input
        try {
        	protocol = parseScheme(scheme);
        } catch (IllegalArgumentException iae) {
            throw new CommandException(iae.getMessage(), iae);
        }
        String contextPathDolToken = protocol + "://" + parseDomain(domain);
        String contextPathAndservletTokenAndCommandTokenEquals = contextPathDolToken + 
                                                                 "/" +
                                                                 "?" + 
                                                                 commandTokenEquals;
        return this.getUrl(ent, ext, contextPathAndservletTokenAndCommandTokenEquals);
    }


    public String getAbsoluteDolUrl(final String ent, 
                                    final String data,
                                    String scheme, 
                                    String domain) 
                             throws CommandException {
        String protocol = null;
        // Controllo sull'input
        try {
        	protocol = parseScheme(scheme);
        } catch (IllegalArgumentException iae) {
            throw new CommandException(iae.getMessage(), iae);
        }
        String contextPathDolToken = protocol + "://" + parseDomain(domain);
        String contextPathAndservletTokenAndCommandTokenEquals = contextPathDolToken + 
                                                                 "/" +
                                                                 "?" + 
                                                                 commandTokenEquals;
        return this.getUrl(ent, data, contextPathAndservletTokenAndCommandTokenEquals);
    }

    
    /**
     * Restituisce la stringa formata dalla coppia 
     * <pre>&quot;&amp;langToken=lang&quot;</pre> se
     * l'attributo lang &egrave; !=null e !="", 
     * <strong><em>oppure</em></strong> la string "" altrimenti.
     * 
     * @return <code>String</code> - valore calcolato del parametro "lang", se esiste nella GET
     */
    private String printLang() {
        return (lang != null && !lang.equals("") ? "&amp;" + langToken + "="
                + lang : "");
    }
    
    
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
