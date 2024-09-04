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
     *  - Se un valore vale -1 significa che è stato inizializzato da Command   *
     *  - Se un valore vale -2 significa che è stato inizializzato nel Bean     *
     *  - Se un valore vale  0 è la cardinalità di una struttura vuota          *    
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
    /**
     * <p>Costante da utilizzare quando serve un valore convenzionale 
     * per inizializzazione di un attributo di un Bean del Model di tipo
     * float o da utilizzare come argomento.</p>
     * <p>Incapsula un valore convenzionale definito nel bean padre CodeBean
     * per comodit&agrave; di accesso dal di fuori del package bean e delle
     * sottoclassi di CodeBean.</p>
     */
    public static final float BEAN_DEFAULT_FLOAT = CodeBean.BEAN_DEFAULT_FLOAT;
    /**
     * <p>Valore identificante la tabella contenente una struttura o un processo 
     * di primo livello (p.es. 1 = struttura_liv1 | 1 = macroprocesso_at)</p>
     */
    public static final byte ELEMENT_LEV_1 = 1;
    /**
     * <p>Valore identificante la tabella contenente una struttura o un processo
     * di secondo livello (2 = struttura_liv2 | 2 = processo_at)</p>
     */
    public static final byte ELEMENT_LEV_2 = 2;
    /**
     * <p>Valore identificante la tabella contenente una struttura o un processo
     * di terzo livello (3 = struttura_liv3 | 3 = sottoprocesso_at)</p>
     */
    public static final byte ELEMENT_LEV_3 = 3;
    /**
     * <p>Valore identificante la tabella contenente una struttura
     * di terzo livello (4 = struttura_liv4).
     * Per quanto riguarda i processi, il quarto livello pu&ograve;
     * identificare le fasi
     * (1 = macroprocesso_at | 2 = processo_at | 3 = sottoprocesso_at)</p>
     */
    public static final byte ELEMENT_LEV_4 = 4;
    /**
     * <p>Costante parlante per impostare il livello di voci di menu
     * che non hanno un livello superiore (sono padri di sottomenu)</p>
     */
    public static final int MAIN_MENU = 0;
    /**
     * <p>Costante parlante per impostare il livello di voci di sottomenu
     * che hanno un solo livello superiore (padre di livello 0)</p>
     */
    public static final int SUB_MENU = 1;
    /**
     * <p>Costante parlante per la mitigazione apportata da una misura generica</p>
     */
    public static final float WEIGHT_GENERIC = 0.5f;
    /**
     * <p>Costante parlante per la mitigazione apportata da una misura generica</p>
     */
    public static final float WEIGHT_SPECIFIC = 1.0f;
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
     * <p>Costante per il parametro identificante la Command delle strutture.</p>
     */
    public static final String COMMAND_STRUCTURES       = "st";    
    /**
     * <p>Costante per il parametro identificante la Command dei processi.</p>
     */
    public static final String COMMAND_PROCESS          = "pr";
    /**
     * <p>Costante per il parametro identificante la Command dei rischi.</p>
     */
    public static final String COMMAND_RISK             = "ri";
    /**
     * <p>Costante per il parametro identificante la Command delle interviste.</p>
     */
    public static final String COMMAND_AUDIT            = "in";
    /**
     * <p>Costante per il parametro identificante la Command della reportistica.</p>
     */
    public static final String COMMAND_REPORT           = "mu";
    /**
     * <p>Costante per il parametro identificante la Command delle misure di mitigazione.</p>
     */
    public static final String COMMAND_MEASURE          = "ms";
    /**
     * <p>Costante per il parametro identificante la parte dei macroprocessi.</p>
     */
    public static final String PART_MACROPROCESS        = "mac";
    /**
     * <p>Costante per il parametro identificante parte applicazione legata ad un processo.</p>
     */
    public static final String PART_PROCESS             = "pro";
    /**
     * <p>Costante per il parametro identificante parte applicazione legata ad output.</p>
     */
    public static final String PART_OUTPUT              = "out";
    /**
     * <p>Costante per il parametro identificante parte applicazione legata ai fattori abilitanti.</p>
     */
    public static final String PART_FACTORS             = "fat";
    /**
     * <p>Costante per il parametro identificante la parte di 
     * aggiunta/aggiornamento nota PxI (giudizio sintetico).</p>
     */
    public static final String PART_PI_NOTE             = "pin";
    /**
     * <p>Costante per il parametro identificante reportistica rischi.</p>
     */
    public static final String PART_RISKS               = "ris";
    /**
     * <p>Costante per il parametro identificante reportistica misure.</p>
     */
    public static final String PART_MEASURES            = "mes";
    /**
     * <p>Costante per il parametro identificante la form di ricerca.</p>
     */
    public static final String PART_SEARCH              = "ric";
    /**
     * <p>Costante per il parametro identificante le infografiche.</p>
     */
    public static final String PART_GRAPHICS            = "gra";
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
    public static final String PART_CREDITS             = "crd";
    /**
     * <p>Costante per il parametro identificante la funzione di aggiunta di un nuovo rischio.</p>
     */
    public static final String PART_INSERT_RISK         = "adr";
    /**
     * <p>Costante per il parametro identificante la funzione di aggiunta di una nuova misura.</p>
     */
    public static final String PART_INSERT_MEASURE      = "inm";
    /**
     * <p>Costante per il parametro identificante la funzione di associazione tra un rischio e un processo.</p>
     */
    public static final String PART_INSERT_RISK_PROCESS = "adp";
    /**
     * <p>Costante per il parametro identificante la funzione di associazione tra 
     * un rischio e un fattore abilitante nel contesto di un processo.</p>
     */
    public static final String PART_INSERT_F_R_P        = "adf";
    /**
     * <p>Costante per il parametro identificante la funzione di associazione tra 
     * un rischio e una misura di mitigazione nel contesto di un processo.</p>
     */
    public static final String PART_INSERT_M_R_P        = "adm";
    /**
     * <p>Costante per il parametro identificante la funzione di aggiornamento di una entit&agrave;.</p>
     */
    public static final String UPDATE                   = "upd";
    /**
     * <p>Costante per il parametro identificante la funzione di eliminazione di una entit&agrave;.</p>
     */
    public static final String DELETE                   = "del";
    /**
     * <p>Costante per il parametro identificante un messaggio (conferma, codice di errore, etc.).</p>
     */
    public static final String MESSAGE                  = "msg";
    /**
     * <p>Costante per il parametro identificante un referral (riferimento).
     * Una stessa funzionalit&agrave; (callee) pu&ograve; essere invocata 
     * da diversi chiamanti (caller) e non &egrave; detto che il risultato esatto
     * della callee debba essere lo stesso per tutti i caller. 
     * Questo parametro permette di identificare tra i diversi caller 
     * qual &egrave; quello che ha fatto la callee.
     * Se si dovesse mantenere traccia della coppia caller-callee potrebbero 
     * essere creati due parametri, p.es. CLR E CLE; per il momento &egrave;
     * sufficiente mantenere l'identificazione del chiamante (caller).</p>
     */
    public static final String REFERRAL                 = "ref";
    /* ************************************************************************ *
     *   Enumerativi statici per incapsulare i valori di enumerativi dinamici   *
     * ************************************************************************ */
    /**
     * <p>Valori possibili dell'attributo stato di struttura.</p>
     */
    static final String[] STATI_STRUTTURA = {"ATTIVA", "DISMESSA"};
    /**
     * <p>Valori possibili di valori convenzionali corrispondenti 
     * a tipi diversi di liste.<br>
     * Rispettivamente:<dl>
     * <dt>I</dt>
     * <dd>Lista Input</dd>
     * <dt>F</dt>
     * <dd>Lista Fasi</dd>
     * <dt>O</dt>
     * <dd>Lista Output</dd>
     * <dt>R</dt>
     * <dd>Lista Rischi</dd>
     * <dt>V</dt>
     * <dd>Lista Interviste (V = View)</dd>
     * <dt>M</dt>
     * <dd>Lista Macroprocessi AT</dd>
     * <dt>S</dt>
     * <dd>Lista Strutture</dd>
     * <dt>T</dt>
     * <dd>Lista Soggetti Interessati (o Terzi = T)</dd>
     * <dt>P</dt>
     * <dd>Lista Processi AT</dd>
     * <dt>C</dt>
     * <dd>Lista Valori Indicatori precedenti (o valori in cache = C)</dd>
     * <dt>A</dt>
     * <dd>Lista Valori Indicatori in memoria (o valori Attuali = A)</dd>
     * </dl>
     * </p>
     */
    static final String[] TIPI_LISTE = {"I", "F", "O", "R", "V", "M", "S", "T", "P", "C", "A"};
    /**
     * <p>Valori possibili degli attributi probabilita, impatto, livello di rischio.</p>
     */
    static final String[] LIVELLI_RISCHIO = {"MINIMO", "BASSO", "MEDIO", "ALTO", "MOLTO ALTO"};
    /**
     * <p>Indicatore di probabiit&agrave; 1.</p>
     */
    static final String P1 = "P1";
    /**
     * <p>Indicatore di probabiit&agrave; 2.</p>
     */
    static final String P2 = "P2";
    /**
     * <p>Indicatore di probabiit&agrave; 3.</p>
     */
    static final String P3 = "P3";
    /**
     * <p>Indicatore di probabiit&agrave; 4.</p>
     */
    static final String P4 = "P4";
    /**
     * <p>Indicatore di probabiit&agrave; 5.</p>
     */
    static final String P5 = "P5";
    /**
     * <p>Indicatore di probabiit&agrave; 6.</p>
     */
    static final String P6 = "P6";
    /**
     * <p>Indicatore di probabiit&agrave; 7.</p>
     */
    static final String P7 = "P7";
    /**
     * <p>Indicatore di impatto 1.</p>
     */
    static final String I1 = "I1";
    /**
     * <p>Indicatore di impatto 2.</p>
     */
    static final String I2 = "I2";
    /**
     * <p>Indicatore di impatto 3.</p>
     */
    static final String I3 = "I3";
    /**
     * <p>Indicatore di impatto 4.</p>
     */
    static final String I4 = "I4";
    /**
     * <p>Indice sintetico di probabilit&agrave;.</p>
     */
    static final String P = "P";
    /**
     * <p>Indice sintetico di impatto.</p>
     */
    static final String I = "I";
    /**
     * <p>Giudizio sintetico (P x I).</p>
     */
    static final String PI = "PxI";
    /**
     * <p>P x I mitigato (stima).</p>
     */
    static final String PIM = "PxI (stima)";
    /**
     * <p>P x I mitigato (monitoraggio).</p>
     */
    static final String PIR = "PxI (reale)";
    /**
     * <p>Messaggio di errore sul calcolo degli indicatori.</p>
     */
    static final String ERR = "Non determinabile";
    /**
     * <p>Lista contenente i possibili valori dell'attributo stato di struttura.</p>
     */
    public static final LinkedList<String> STATI_STRUTTURA_AS_LIST = new LinkedList<>(Arrays.asList(STATI_STRUTTURA));
    /**
     * <p>Lista contenente i possibili valori degli attributi probabilita, impatto, livello di rischio.</p>
     */
    public static final LinkedList<String> LIVELLI_RISCHIO_AS_LIST = new LinkedList<>(Arrays.asList(LIVELLI_RISCHIO));
    /**
     * <p>Etichetta per struttura capofila 1</p>
     */
    public static final String CP1 = "capofila 1";
    /**
     * <p>Etichetta per struttura capofila 2</p>
     */
    public static final String CP2 = "capofila 2";
    /**
     * <p>Etichetta per struttura capofila 3</p>
     */
    public static final String CP3 = "capofila 3";
    /**
     * <p>Etichetta per struttura capofila 1</p>
     */
    public static final String GR = "gregaria";
    /**
     * <p>Etichetta per valore indecidibile</p>
     */
    public static final String ND = "Non determinabile";
    /* ************************************************************************ *
     *   Costanti tipografiche per la generazione di output (p.es. csv) e URL   *
     * ************************************************************************ */
    /**
     * <p>Costante da utilizzare quando serve uno spazio (l'equivalente,
     * in Java, dell'html &quot;&nbsp;&quot;), generalmente usato
     * per separare pi&uacute; sottostostringhe in una stringa
     * da restituire come valore oppure messaggio.</p>
     */
    public static final char BLANK_SPACE = ' ';
    /**
     * Costante per l'uso del separatore trattino
     */
    public static final char HYPHEN = '-';
    /**
     * Costante per l'uso del separatore underscore
     */
    public static final char UNDERSCORE = '_';
    /**
     * Costante per l'uso del carattere punto e virgola 
     */
    public static final char SEMICOLON = ';';
    /**
     * Costante per l'uso del carattere due punti
     */
    public static final char COLON = ':';
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
     * Costante per far riferimento al carattere di virgoletta semplice, o apostrofo
     */
    public static final char APOSTROPHE = '\'';
    /**
     * Costante per far riferimento al carattere di virgoletta doppia
     */
    public static final char QUOTE = '\"';
    /**
     * Costante per far riferimento al carattere di percentuale
     */
    public static final char PER_CENT = '%';
    /**
     * Costante per far riferimento al carattere tipografico di virgoletta inglese
     */
    public static final char ENGLISH_SINGLE_QUOTE = '’';
    /**
     * Costante per il carattere tipografico di doppie virgolette inglesi aperte
     */
    public static final char ENGLISH_DOUBLE_QUOTE_OPEN = '“';
    /**
     * Costante per il carattere tipografico di doppie virgolette inglesi chiuse
     */   
    public static final char ENGLISH_DOUBLE_QUOTE_CLOSE = '”';
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
     * questa discussione</a>)</p>.
     */
    public static final String VOID_STRING = "";
    /**
     * <p>Costante da utilizzare quando serve un valore che, come dice il nome, 
     * funga da stringa vuota in linguaggio SQL, tipicamente da passare 
     * come parametro, specialmente nelle clausole di tipo ILIKE ().</p>
     * <p>Utile soprattutto perch&eacute; graficamente maggiormente visibile 
     * e chiara della stringa vuota SQL, che corrisponde a due apici singoli 
     * consecutivi, che per poter essere trattati da Java come stringa, 
     * devono a loro volta essere circondati da doppi apici.</p>
     */
    public static final String VOID_SQL_STRING = "''";
    /**
     * <p>Costante da utilizzare quando serve un valore per inizializzazione
     * di default di un parametro mancante sulla querystring.</p>
     * <p>Incapsula il classico trattino (-) che viene utilizzato come default
     * per i valori mancanti di parametri richiesti tipicamente dalle
     * Command.</p>
     */
    public static final String DASH = String.valueOf(HYPHEN);
    /**
     * Costante per l'uso del separatore semicolon 
     */
    public static final String SEPARATOR = String.valueOf(SEMICOLON);
    /**
     * Costante per l'uso del separatore punto
     */
    public static final String DOT = ".";
    /**
     * Costante per l'uso del separatore virgola
     */
    public static final String COMMA = ",";
    /**
     * Costante per il separatore che precede immediatamente la querystring (root question mark)
     */
    public static final String ROOT_QM = String.valueOf(SLASH) + String.valueOf(QM);
    /**
     * Costante per il separatore dei token querystring "&"
     */
    public static final String AMPERSAND = "&";
    /* ************************************************************************ *
     * Formati di files, naming di files, tipi MIME etc.
     * ************************************************************************ */
    /**
     * Costante per il tipo MIME testo semplice
     */
    public static final String MIME_TYPE_TEXT = "text/plain";   
    /**
     * Costante per il tipo MIME html
     */
    public static final String MIME_TYPE_HTML = "text/html";
    /**
     * Costante per il formato di file "Hyper Text Markup Language"
     */
    public static final String HTML = "html";
    /**
     * Costante per il formato di file "Comma Separated Values"
     */
    public static final String CSV = "csv";
    /**
     * Costante per il formato di file "Rich Text Format"
     */
    public static final String RTF = "rtf";
    /**
     * Costante per il formato di file "JavaScript Object Notation"
     */
    public static final String JSON = "json";
    /**
     * Costante per il formato di file "Java Server Pages"
     */
    public static final String JSP_EXT = "jsp";
    /**
     * Costante per i nomi di immagini relative a strutture
     */
    public static final String STR_PFX = "dept_l";
    /**
     * Costante per i nomi di immagini relative a processi
     */
    public static final String PRO_PFX = "procat_l";
    /**
     * Costante per i nomi di estrazioni dati interviste
     */
    public static final String INTERVIEWS = "interviste";
    /**
     * Costante per i nomi di estrazioni dati intervista
     */
    public static final String INTERVIEW = "intervista";
    /**
     * Costante per i nomi di estrazioni dati strutture
     */
    public static final String STRUCTURES = "organigramma";
    /**
     * Costante per i nomi di estrazioni dati processi anticorruzione
     */
    public static final String PROCESS = "processi";
    /**
     * Costante per i nomi di estrazioni dati registro rischi corruttivi
     */
    public static final String RISKS = "registrorischi";
    /**
     * Costante per i nomi di estrazioni dati "tabella MDM"
     */
    public static final String REPORT_MDM = "tabella_rischi";
    /**
     * Costante per il log delle differenze tra i valori indicatori in cache e quelli in memoria
     */
    public static final String LOG_IND = "log_indicatori";
    /* ************************************************************************ *
     * Costanti di tempo (p.es. formati di data, date significative, etc.) *
     * ************************************************************************ */
    /**
     * <p>Pattern che deve avere una data (oggetto java.util.Date o GregorianCalendar) per essere conforme al fornato SQL.</p>
     */
    public static final String DATA_SQL_PATTERN = "yyyy-MM-dd";
    /**
     * <p>Pattern che specifica il formato di una String che rappresenta una data
     * esprimendola relativamente per esteso; esempio: "Tue Jul 07 00:00:00 CEST 2020".</p>
     */
    public static final String DATA_GENERAL_PATTERN ="EEE MMM dd HH:mm:ss zzz yyyy";
    /**
     * <p>Contiene la formattazione che deve avere una data all'interno dell'applicazione.</p>
     */
    public static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat(DATA_SQL_PATTERN);
    /**
     * <p>Pattern per un oggetto Time espresso su ore:minuti:secondi.<br />
     * <strong>ATTENZIONE:</strong> nella localizzazione italiana 
     * la maschera di formattazione deve essere del tipo HH:mm e non hh:mm, 
     * altrimenti un orario come le 12:05 viene interpretato come 00:05! 
     * Capital H permette di specificare il reset delle ore 
     * su un totale di 24, non su un totale di 12.<br />
     * cfr. https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
     * <pre>
     * Letter   Date or Time Component
     *  H       Hour in day (0-23)    
     *  h       Hour in am/pm (1-12)   
     * </pre></p> 
     */
    public static final String TIME_SQL_PATTERN = "HH:mm:ss";
    /**
     * <p>Pattern per un oggetto Time espresso su 12 ore.</p>
     */
    public static final String TWELWE_HOUR_FORMAT = "hh:mm";
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
     * (quindi, in pratica, gli orologi dei sistemi a 32 bit verranno resettati a UNIX EPOCH).
     */
    public static final String THE_END_OF_TIME = "2106-02-07";

}
