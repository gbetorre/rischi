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

package it.rol;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;

import it.rol.bean.CodeBean;


/**
 * <p>Questa &egrave; l'interfaccia contenente le costanti utilizzabili da tutti i 
 * consumer java della web-application &nbsp;<code>Processi on Line (prol)</code>.</p>
 * <p>Definisce costanti di utilit&agrave; e
 * insiemi di valori ammessi per parametri applicativi.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public interface Constants extends Serializable {
    /* ************************************************************************ *
     *        Costanti parlanti per valori interi di inizializzazione           *
     * ************************************************************************ */
    /**
     * <p>Costante parlante per i test che controllano
     * che interi abbiano un valore maggiore di zero.</p>
     * <p>Maggiormente visibile e chiara del valore che incapsula (0)
     * per questo motivo pu&ograve; essere utilizzata in inizializzazioni di
     * variabili e in test che controllano che specifici parametri
     * abbiano un valore significativo.</p>
     */
    public static final byte NOTHING = 0;
    /**
     * <p>Costante da utilizzare quando serve un valore per inizializzazione
     * in un Controller o da utilizzare come argomento.</p>
     * Incapsula un valore convenzionale per i test che controllano,
     * nelle Command, se &egrave; presente un valore significativo;
     * se in Request non &grave; presente tale valore, lo imposta a
     * un valore di default negativo (-1), in modo da facilitare i test a valle
     * del tentato recupero.
     * <p>Maggiormente visibile e chiara del valore che incapsula
     * <code>(-1) </code> per questo motivo pu&ograve; essere utilizzata
     * direttamente nel codice ad esempio in caso di inzializzazioni
     * di default effettuate dal
     * </code>{@link com.oreilly.servlet.ParameterParser ParameterParser}</code>.</p>
     */
    public static final int DEFAULT_ID = -1;
    /**
     * <p>Costante da utilizzare quando serve un valore per inizializzazione
     * in un Bean del Model o da utilizzare come argomento.</p>
     * <p>Incapsula un valore convenzionale definito nel bean padre CodeBean
     * per comodit&agrave; di accesso dal di fuori del package bean e delle
     * sottoclassi di CodeBean.</p>
     */
    public static final int BEAN_DEFAULT_ID = CodeBean.BEAN_DEFAULT_ID;
    /* ************************************************************************ *
     *              Costanti parlanti per valori boolean di flags               *
     * ************************************************************************ */
    /**
     * <p>Costante parlante per flag di recupero sessione utente.</p>
     */
    public static final boolean IF_EXISTS_DONOT_CREATE_NEW = false;
    /* ************************************************************************ *
     *      Costanti corrispondenti ai parametri ammessi sulla querystring      *
     * ************************************************************************ */
    /**
     * <p>Costante identificante il parametro della rilevazione.</p>
     */
    public static final String PARAM_SURVEY             = "r";
    /**
     * <p>Costante per il parametro identificante la HomePageCommand.</p>
     */
    public static final String COMMAND_HOME             = "home";
    /**
     * <p>Costante per il parametro identificante la Command delle persone.</p>
     */
    public static final String COMMAND_PERSON           = "pe";    
    /**
     * <p>Costante per il parametro identificante la Command dei processi.</p>
     */
    public static final String COMMAND_PROCESS          = "pr";
    /**
     * <p>Costante per il parametro identificante la Command dei rischi.</p>
     */
    public static final String COMMAND_RISK             = "ri";
      /**
     * <p>Costante per il parametro identificante la Command delle strutture.</p>
     */
    public static final String COMMAND_STRUCTURES       = "st";
    /**
     * <p>Costante per il parametro identificante la parte dei macroprocessi.</p>
     */
    public static final String PART_MACROPROCESS        = "mac";
    /**
     * <p>Costante per il parametro identificante parte applicazione legata a un processo.</p>
     */
    public static final String PART_PROCESS             = "pro";
    /**
     * <p>Costante per il parametro identificante la parte della multi-rilevazione.</p>
     */
    public static final String PART_MULTIFACT           = "mul";
    /**
     * <p>Costante per il parametro identificante la parte di ricerca sulle persone.</p>
     */
    public static final String PART_SEARCH_PERSON       = "pes";
    /**
     * <p>Costante per il parametro identificante la parte di selezione strutture.</p>
     */
    public static final String PART_SELECT_STR          = "str";
    /**
     * <p>Costante per il parametro identificante la parte di presentazione quesiti.</p>
     */
    public static final String PART_SELECT_QST          = "sqt";
    /**
     * <p>Costante per il parametro identificante la parte di conferma quesiti.</p>
     */
    public static final String PART_CONFIRM_QST         = "cqs";
    /**
     * <p>Costante per il parametro identificante la parte di riepilogo quesiti.</p>
     */
    public static final String PART_RESUME_QST          = "rqs";
    /**
     * <p>Costante per il parametro identificante la parte di elenco interviste.</p>
     */
    public static final String PART_SELECT_QSS          = "sqs";
    /**
     * <p>Costante per il parametro identificante la pagina dell'utente.</p>
     */
    public static final String PART_USR                 = "usr";
    /**
     * <p>Costante per il parametro identificante la pagina dei credits dell'applicazione.</p>
     */
    public static final String PART_CREDITS             = "cre";
    /* ************************************************************************ *
     *   Enumerativi statici per incapsulare i valori di enumerativi dinamici   *
     * ************************************************************************ */
    /**
     * <p>Valori possibili dell'attributo stato di struttura.</p>
     */
    static final String[] STATI_STRUTTURA = {"ATTIVA", "DISMESSA"};
    /**
     * <p>Valori possibili di valori convenzionali corrispondenti a tipi diversi di responsabilit&agrave;.</p>
     */
    static final String[] TIPI_RESPONSABILITA = {"R", "F", "T"};
    /**
     * <p>Valori possibili degli attributi probabilita, impatto, livello di rischio.</p>
     */
    static final String[] LIVELLI_RISCHIO = {"ALTO", "MEDIO", "BASSO"};
    /**
     * <p>Lista contenente i possibili valori dell'attributo stato della classe RiskBean.</p>
     */
    public static final LinkedList<String> STATI_STRUTTURA_AS_LIST = new LinkedList<String>(Arrays.asList(STATI_STRUTTURA));
    /**
     * <p>Lista contenente i possibili valori convenzionali corrispondenti a tipi diversi di responsabilit&agrave;.</p>
     */
    public static final LinkedList<String> TIPI_RESPONSABILITA_AS_LIST = new LinkedList<String>(Arrays.asList(TIPI_RESPONSABILITA));
    /**
     * <p>Lista contenente i possibili valori degli attributi probabilita, impatto, livello della classe RiskBean.</p>
     */
    public static final LinkedList<String> LIVELLI_RISCHIO_AS_LIST = new LinkedList<String>(Arrays.asList(LIVELLI_RISCHIO));
    /* ************************************************************************ *
     *   Costanti tipografiche per la generazione di output (p.es. csv) e URL   *
     * ************************************************************************ */
    /**
     * <p>Costante da utilizzare quando serve un valore per inizializzazione,
     * o da utilizzare come argomento, per effettuare test, etc.</p>
     * <p>Graficamente maggiormente visibile e chiara della stringa vuota
     * che contiene (doppi apici aperti chiusi) &ndash;
     * se quest'ultima &egrave; utilizzata direttamente nel codice &ndash;
     * &egrave; pi&uacute; ottimizzata rispetto al puntamento
     * a una nuova stringa attraverso
     * il richiamo del costruttore &nbsp;<code>new String("");</code><br />
     * (si veda, ad esempio:
     * <a href="http://www.precisejava.com/javaperf/j2se/StringAndStringBuffer.htm">
     * questa discussione</a>).
     */
    public static final String VOID_STRING = "";
    /**
     * <p>Costante da utilizzare quando serve uno spazio (l'equivalente,
     * in java, dell'html &quot;&nbsp;&quot;), generalmente usato
     * per separare pi&uacute; sottostostringhe in una stringa
     * da restituire come valore oppure messaggio.</p>
     */
    public static final char BLANK_SPACE = ' ';
    /**
     * <p>Costante da utilizzare quando serve un valore per inizializzazione
     * di default di un parametro mancante sulla querystring.</p>
     * <p>Incapsula il classico trattino (-) che viene utilizzato come default
     * per i valori mancanti di parametri richiesti tipicamente dalle
     * Command.</p>
     */
    public static final String DASH = "-";
    /**
     * Costante per l'uso del separatore trattino
     */
    public static final char HYPHEN = '-';
    /**
     * Costante per l'uso del separatore underscore
     */
    public static final char UNDERSCORE = '_';
    /**
     * Costante per l'uso del separatore punto
     */
    public static final String DOT = ".";
    /**
     * Costante per l'uso del separatore virgola
     */
    public static final String COMMA = ",";
    /**
     * Costante per l'uso del separatore semicolon 
     */
    public static final String SEPARATOR = ";";
    /**
     * Costante per l'uso del separatore '=' (p.es., tra parametro e valore) 
     */
    public static final char EQ = '=';
    /**
     * Costante per l'uso del separatore 'slash' (p.es. nei percorsi)
     */
    public static final char SLASH = '/';
    /**
     * Costante per l'uso del separatore '?' (question mark)
     */
    public static final char QM = '?';
    /**
     * Costante per il separatore che precede immediatamente la querystring (root question mark)
     */
    public static final String ROOT_QM = String.valueOf(SLASH) + String.valueOf(QM);
    /**
     * Costante per il separatore dei token querystring "&"
     */
    public static final String AMPERSAND = "&";
    /* ************************************************************************ *
     * Formati di files, tipi MIME, etc.
     * ************************************************************************ */
    /**
     * Costante per il tipo MIME html
     */
    public static final String MIME_TYPE_HTML = "text/html";
    /**
     * Costante per il tipo MIME testo semplice
     */
    public static final String MIME_TYPE_TEXT = "text/plain";
    /**
     * Costante per il formato di file "Comma Separated Values"
     */
    public static final String CSV = "csv";
    /**
     * Costante per il formato di file "JavaScript Object Notation"
     */
    public static final String JSON = "json";
    
    /* ************************************************************************ *
     * Costanti di tempo (p.es. formati di data, date significative, etc.) *
     * ************************************************************************ */
    /**
     * <p>Pattern che deve avere una data (oggetto java.util.Date o GregorianCalendar) per essere conforme al fornato SQL.</p>
     */
    public static final String DATA_SQL_PATTERN = "yyyy-MM-dd";
    /**
     * <p>Contiene la formattazione che deve avere una data all'interno dell'applicazione.</p>
     */
    public static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat(DATA_SQL_PATTERN);
    /**
     * <p>Pattern per un oggetto Time espresso su ore:minuti:secondi.</p> 
     */
    public static final String TIME_SQL_PATTERN = "hh:mm:ss";
    /**
     * <p>Pattern per un oggetto Time espresso su 12 ore.</p>
     */
    public static final String TWELWE_HOUR_FORMAT = "HH:mm";
    /**
     * <p>Numero di anni da spostare per l'estrazione degli elementi
     * nella visualizzazione di default.
     * P.es. un valore 1 implica un'estrazione di un anno, tra la data corrente
     * e la data corrente dell'anno prossimo.</p>
     */
    public static final int YEAR_SHIFT = 1;
    /**
     * Il TIMESTAMP dei sistemi corrisponde al numero di secondi trascorsi
     * da una data convezionale conosciuta come <em>Unix Epoch</em>
     * (1° gennaio 1970, quando tutto &egrave; cominciato)<sup>1</sup>.<br />
     * <small>1: Primo kernel UNIX del 1969, sviluppato dai Bell Laboratories
     * a partire dal 1970.</small>
     */
    public static final String UNIX_EPOCH = "1970-01-01";
    /**
     * Il 7 febbraio 2106 il tempo UNIX raggiunger&agrave; la cifra esadecimale
     * FFFFFFFF16 (corrispondente a 4.294.967.295 secondi) che, per i sistemi
     * a 32 bit, &egrave; il massimo computabile. Per sistemi del genere,
     * il successivo secondo sar&agrave; interpretato come:
     * 00:00:00 1 January 1970 UTC
     * (quindi, in pratica, gli orologi verranno resettati a UNIX EPOCH).
     */
    public static final String THE_END_OF_TIME = "2106-02-07";

}
