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

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.logging.Logger;

import it.rol.exception.CommandException;
import it.rol.exception.NotFoundException;


/**
 * <p>Classe contenitore di metodi di utilit&agrave; e alcune costanti
 * di uso comune</p>
 *
 * @author  <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class Utils implements Constants {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = 962412073250304247L;
    /**
     * <p>Logger della classe per scrivere i messaggi di errore.
     * All logging goes through this logger.</p>
     * <p>Non &egrave; privata ma Default (friendly) per essere visibile
     * negli oggetti ovverride implementati da questa classe.</p>
     */
    static Logger log = Logger.getLogger(Utils.class.getName());
    /**
     * <p>Nome di questa classe
     * (viene utilizzato per contestualizzare i messaggi di errore).</p>
     * <p>Non &egrave; privata ma Default (friendly) per essere visibile
     * negli oggetti ovverride implementati da questa classe.</p>
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";


    /**
     * Costruttore vuoto.
     * Svolge la stessa funzione del costruttore di default.
     */
    public Utils() {
        /*;*/   // It doesn't anything
    }


    /**
     * <p>Controlla se una HashMap, passata come argomento,
     * contiene almeno una stringa non vuota.</p>
     * <p>Restituisce il valore boolean <code> true</code> se la HashMap
     * contiene almeno un valore significativo, <code> false</code> altrimenti.</p>
     *
     * @param params - HashMap da controllare
     * @return <code>true</code> se la HashMap contiene almeno un valore diverso da stringa vuota
     * @throws NotFoundException se il valore dell'argomento vale null
     */
    public static boolean containsValues(HashMap<String, String> params)
                                  throws NotFoundException {
        // Controllo sull'input
        if (params == null) {
            String msg = FOR_NAME + "Si e\' tentato di invocare il metodo di controllo su un oggetto non pronto. Controllare il valore dell\'argomento.\n";
            throw new NotFoundException(msg);
        }
        // Algoritmo decisionale
        Iterator<String> it = params.values().iterator();
        while (it.hasNext()) {
            if (!it.next().equals(VOID_STRING)) {
                return true;
            }
        }
        return false;
    }


    /**
     * <p>Controlla se una stringa contiene doppi apici.
     * In questo caso, fa l'escape di ognuno di essi e restituisce la String
     * modificata. Altrimenti, restituisce la stringa originale.</p>
     *
     * @param s la String da controllare
     * @return <code>String</code> la String con i doppi apici trattati, o la stringa originale se questi non sono stati trovati
     */
    public static String checkQuote(String s) {
        final String doubleQuote = "\"";
        String s1 = null;
        if (s.indexOf(doubleQuote) > DEFAULT_ID) {
            s1 = s.replace(doubleQuote, "\\\"");
            return s1;
        }
        return s;
    }
    
    
    /**
     * <p>Data in input una stringa qualsiasi, restituisce la stringa 
     * avente lo stesso contenuto dell'originale ma con la prima lettera
     * maiuscola.</p>
     *
     * @param s la String da capitalizzare
     * @return <code>String</code> la String da capitalizzare
     */
    public static String capitalize(String s) {
        String s1 = s.substring(0, 1).toUpperCase();
        return new String(s1 + s.substring(1));
    }
    

    /**
     * <p>Controlla se una stringa corrisponde a un valore intero.</p>
     *
     * @param s la String da controllare
     * @return <code>true</code> se la String e' convertibile in intero, false altrimenti
     */
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            log.warning(": si e\' verificato un problema nella conversione da String a numero.\n" + nfe.getLocalizedMessage());
            return false;
        } catch (NullPointerException npe) {
            log.warning(": si e\' verificato un problema nel puntamento a qualche parametro.\n" + npe.getLocalizedMessage());
            return false;
        }
        // Only got here if we didn't return false
        return true;
    }
    

    /**
     * <p>Converte un valore di tipo primitivo float in uno di tipo primitivo
     * int.</p>
     *
     * @param f il valore da convertire
     * @return <code>int</code> il valore convertito, se tutto e' andato a buon fine
     */
    public static int parseInt(float f) {
        int result = NOTHING;
        try {
            // I float possono avere una parte decimale significativa
            result = Math.round(f);
        } catch (NumberFormatException nfe) {
            log.warning(": si e\' verificato un problema nella conversione da float a intero.\n" + nfe.getLocalizedMessage());
        } catch (NullPointerException npe) {
            log.warning(": si e\' verificato un problema in qualche puntamento.\n" + npe.getLocalizedMessage());
        }
        return result;
    }
    

    /**
     * <p>Converte un valore di tipo primitivo int in un oggetto di tipo String
     * avente un formato predefinito.</p>
     * <p>In particolare, se l'intero passato &egrave; strettamente minore di 10,
     * aggiunge uno zero prima della cifra intera da convertire in String.</p>
     *
     * @param i il valore numerico da convertire
     * @return <code>String</code> il valore convertito, se tutto e' andato a buon fine
     */
    public static String parseString(int i) {
        String result = null;
        try {
            String convert = String.valueOf(i);
            result = (i < 10) ? String.valueOf(NOTHING) + convert : convert;
        } catch (NullPointerException npe) {
            log.warning(": si e\' verificato un problema in qualche puntamento.\n" + npe.getLocalizedMessage());
        } catch (Exception e) {
            log.warning(": si e\' verificato un problema nel metodo di conversione da intero a stringa formattata.\n" + e.getLocalizedMessage());
        }
        return result;
    }

    /* ************************************************************************ *
     *   Metodi di utilita' per la definizione e la manipolazione delle date    *
     * ************************************************************************ */

    /**
     * <p>Restituisce una data di default da utilizzare nell'estrazione
     * di elementi soggetti a una scansione temporale.</p>
     * <p>Ad esempio, elementi che hanno una data di inizio
     * e una data di fine pubblicazione.<br />
     * Lo stesso vale in genere per tutti gli elementi
     * che vengono mostrati a vario titolo, sotto forma di claim, di elenco,
     * di lista, di menu, etc., nelle pagine del sito.</p>
     * <p>La data di default restituita in questione &egrave; quella che
     * la command utilizza come data di inizio ricerca di tutti gli elementi
     * da mostrare nelle pagine intermedie <em>e corrisponde, sostanzialmente,
     * alla data corrente</em>.</p>
     * <p>Questo valore &egrave; molto utile nel caso in cui si debbano
     * effettuare nell'intervallo di default estrazioni di elementi storicizzati
     * dall'esterno del metodo in cui tali date determinanti l'intervallo
     * di default sono calcolate, evitando quindi
     * di dover riprodurre tutto il codice del calcolo di tali date
     * (p.es., dal metodo pubblico che restituisce il numero di iniziative
     * attive nell'intervallo di default).</p>
     * <p>
     * Il valore restituito &egrave; un
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/GregorianCalendar.html">
     * GregorianCalendar</a> piuttosto che una
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/Date.html">
     * java.util.Date</a>.<br />
     * Il motivo per cui &egrave; preferito un GregorianCalendar a una Date
     * &egrave, che la
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/Date.html">
     * java.util.Date</a>
     * <cite id="stackoverflow">ha molti metodi di trasformazione deprecati,
     * mentre il Calendar ha una localizzazione pi&uacute; agevole
     * <a href="http://stackoverflow.com/questions/1404210/java-date-vs-calendar">
     * (v.)</a></cite>
     * Siccome per motivi di retrocompatibilit&agrave; spesso non va bene
     * usare un Calendar ma ci vuole una Date (il DBWrapper usa le java.sql.Date),
     * basta applicare una semplice trasformazione al valore restituito,
     * quale la seguente:
     * <pre>
     * java.util.Date startdate = getDefaultStartDate().getTime();
     * </pre>
     * (o, se si preferisce, per ancor maggiore comodit&agrave;, utilizzare
     * il metodo di utilit&agrave; <code>convert()</code>,
     * definito pi&uacute; avanti).</p>
     *
     * @return <code>GregorianCalendar</code> - la data corrispondente al limite inferiore dell'intervallo di default di ricerca di elementi storicizzati
     */
    public static GregorianCalendar getCurrentDate() {
        /* Variabile privata di tipo GregorianCalendar
         * necessaria per fornire un default in mancanza di
         * date di inzio e di fine ricerca
         * personalizzate dall'utente.<br />
         * Viene definita a livello di istanza e non a livello locale
         * perch&eacute; utile a diversi metodi di istanza
         * (metodi di utilit&agrave;). */
        GregorianCalendar calendar = new GregorianCalendar();
        /* Oggi (giorno corrente) sotto forma numerica.
         * Indica il giorno del mese (p.es. '22' del 22/04/1970*)
         * <p><small>* First <em>Earth Day</em></small></p> */
        int day = calendar.get(Calendar.DATE);
        /* Questo mese (mese corrente) sotto forma numerica.
         * Indica il mese dell'anno (p.es. '3' del 22/04/1970*)
         * <p><small>* Calendar.MONTH parte da zero</small></p> */
        int month = calendar.get(Calendar.MONTH);
        /* Quest'anno (mese corrente) sotto forma numerica.
         * <p>Indica l'anno corrente (p.es. '1970' del 22/04/1970)</p> */
        int year = calendar.get(Calendar.YEAR);
        // Costruisce la data di default
        GregorianCalendar dateConverted = new GregorianCalendar(year, month, day);
        // Restituisce al chiamante la data di default
        return dateConverted;
    }


    /**
     * <p>Restituisce una data di default, <em>pari alla data corrente traslata
     * di un anno</em> (la stessa data di oggi, ma fra un anno),
     * da utilizzare nell'estrazione di elementi
     * soggetti a una scansione temporale.</p>
     * <p>Questo valore &egrave; molto utile nel caso in cui si debbano
     * effettuare nell'intervallo di default estrazioni
     * di contenuti storicizzati, dall'esterno
     * del metodo in cui tali date determinanti l'intervallo
     * di default sono calcolate, evitando quindi
     * di dover riprodurre tutto il codice del calcolo di tali date
     * (p.es., dal metodo pubblico che restituisce il numero di iniziative
     * attive nell'intervallo di default).</p>
     * <p>
     * Il valore restituito &egrave; un
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/GregorianCalendar.html">
     * GregorianCalendar</a> piuttosto che una
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/Date.html">
     * java.util.Date</a>.<br />
     * Il motivo per cui &egrave; preferito un GregorianCalendar a una Date
     * &egrave, che la
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/Date.html">
     * java.util.Date</a>
     * <cite id="stackoverflow">ha molti metodi di trasformazione deprecati,
     * mentre il Calendar ha una localizzazione pi&uacute; agevole
     * <a href="http://stackoverflow.com/questions/1404210/java-date-vs-calendar">
     * (v.)</a></cite>
     * Siccome per motivi di retrocompatibilit&agrave; spesso non va bene
     * usare un Calendar ma ci vuole una Date (la WebStorage usa le Date),
     * basta applicare una semplice trasformazione al valore restituito,
     * quale la seguente:
     * <pre>
     * java.util.Date enddate = getDefaultEndDate().getTime();
     * </pre>
     * o, ancor pi&uacute; comodamente, utilizzare
     * il metodo di utilit&agrave; <code>convert()</code>.</p>
     *
     * @return <code>GregorianCalendar</code> - la data corrispondente al limite superiore dell'intervallo di default di ricerca di elementi storicizzati
     */
    public static GregorianCalendar getCurrentDateNextYear() {
        GregorianCalendar calendar = new GregorianCalendar();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        GregorianCalendar dateConverted = new GregorianCalendar(year + YEAR_SHIFT, month, day);
        return dateConverted;
    }


    /**
     * <p>Restituisce una data di default (vale a dire una data da usare
     * nel caso in cui non esista una data costruita a tempo di esecuzione
     * sulla base di parametri inseriti dall'utente) che la command utilizza
     * come data di fine ricerca di elementi soggetti a una scansione
     * temporale, pi&uacute; uno shift a piacere passato come argomento
     * (in pratica, equivale alla data odierna tra <em>'tot'</em> anni,
     * con <em>'tot'</em> passato come argomento).</p>
     * <p>Questo valore &egrave; molto utile nel caso in cui si debbano
     * effettuare estrazioni di contenuti nell'intervallo di default dall'esterno
     * del metodo in cui tali date sono calcolate, evitando quindi
     * di dover riprodurre tutto il codice del calcolo di tali date
     * (p.es., dal metodo pubblico che restituisce il numero di contenuti
     * attivi nell'intervallo di default).<br />
     * Il motivo per il quale viene utilizzato uno shift &egrave;
     * che non &egrave; sempre detto che l'anno di fine ricerca
     * sia pari all'anno di inizio ricerca incrementato di 1. Potrebbe
     * essere desiderabile effettuare una ricerca in un intervallo pi&uacute;
     * lungo, p.es. se si volesse mostrare il numero totale di
     * iniziative attive nei prossimi <em>tot</em> anni, con <em>tot</em>
     * passato come argomento.</p>
     * <p>
     * Il valore restituito &egrave; un
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/GregorianCalendar.html">
     * GregorianCalendar</a> piuttosto che una
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/Date.html">
     * java.util.Date</a>.<br />
     * Il motivo per cui &egrave; preferito un GregorianCalendar a una Date
     * &egrave, che la
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/Date.html">
     * java.util.Date</a>
     * <cite id="stackoverflow">ha molti metodi di trasformazione deprecati,
     * mentre il Calendar ha una localizzazione pi&uacute; agevole
     * <a href="http://stackoverflow.com/questions/1404210/java-date-vs-calendar">
     * (v.)</a></cite>
     * Siccome per motivi di retrocompatibilit&agrave; spesso non va bene
     * usare un Calendar ma ci vuole una Date (la WebStorage usa le Date),
     * basta applicare una semplice trasformazione al valore restituito,
     * quale la seguente:
     * <pre>
     * java.util.Date enddate = getDefaultEndDate(10).getTime();
     * </pre>
     * dove 10 &egrave, in questo caso, ovviamente, un valore arbitrario
     * (sta per un intervallo di ricerca di 10 anni!).</p>
     *
     * @param shift intero corrispondente al numero di anni da sommare all'anno di inizio ricerca per ottenere l'anno di fine ricerca
     * @return <code>GregorianCalendar</code> - la data corrispondente al limite superiore dell'intervallo di default di ricerca di elementi storicizzati, con l'intervallo determinato dallo shift
     */
    public static GregorianCalendar getCurrentDateInAFewYears(int shift) {
        GregorianCalendar calendar = new GregorianCalendar();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        GregorianCalendar dateConverted = new GregorianCalendar(year + shift, month, day);
        return dateConverted;
    }


    /**
     * <p>Dato un anno in input, restituisce la data del 31 dicembre 
     * dell'anno stesso sotto forma di
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/GregorianCalendar.html">
     * GregorianCalendar</a>.</p>
     *
     * @param year intero corrispondente a un anno
     * @return <code>GregorianCalendar</code> - la data corrispondente al 31 dicembre di quell'anno
     */
    public static GregorianCalendar getLastDayOfYear(int year) {
        // Costruisce l'ultimo giorno dell'anno passato, sotto forma di GregorianCalendar
        GregorianCalendar dateConverted = new GregorianCalendar(year, 11, 31);
        // Restituisce al chiamante la data di default
        return dateConverted;
    }


    /**
     * <p>Restituisce la data corrente,
     * addizionata o sottratta di:
     * <em><ul>
     * <li>tot giorni</li>
     * <li>tot mesi</li>
     * <li>tot anni</li>
     * </ul></em>
     * con giorni, mesi e anni passati come argomenti.</p>
     * <p><ol>
     * <li>Se si vuol <em>sottrarre</em> anzich&eacute; <em>aggiungere</em>
     * basta passare valori negativi dei parametri.</li>
     * <li>Se si vuol sottrarre o aggiungere solo uno o solo un paio di
     * parametri, ma non tutti e tre, basta passare zero <code>(0)</code>
     * come valore dei parametri non interessanti.</li>
     * </ol></p>
     * <p>
     * Questo metodo risulta utile quando bisogna passare ad una query
     * una data come limite di periodo di estrazione.</p>
     * <p>
     * Notare che per ottenere qualsiasi data sarebbe sufficiente
     * aggiungere o sottrarre il numero giusto di soli giorni.
     * Tuttavia in alcuni casi pu&ograve; essere pi&uacute;
     * comodo passare lo shift giusto di mesi e anni piuttosto che
     * ricavare la data voluta attraverso il numero di giorni in pi&uacute;
     * o in meno, e per questo motivo il metodo ha 3 possibili parametri
     * (anche se nulla vieta di utilizzare solo il primo per ottenere
     * tutte le date volute).</p>
     *
     * @param days   numero di giorni da aggiungere o togliere alla data corrente per ottenere una data desiderata
     * @param months numero di mesi   da aggiungere o togliere alla data corrente per ottenere una data desiderata
     * @param years  numero di anni   da aggiungere o togliere alla data corrente per ottenere una data desiderata
     * @return <code>GregorianCalendar</code> - la data desiderata, pari alla data corrente aggiunta o sottratta di giorni e/o mesi e/o anni specificati dai parametri
     */
    public static GregorianCalendar getDate(int days, int months, int years) {
        GregorianCalendar date = getCurrentDate();
        if (days != 0) {
            // Aggiunge, o toglie, 'days'
            date.add(Calendar.DATE, days);
        }
        if (months != 0) {
            // Aggiunge, o toglie, 'months'
            date.add(Calendar.MONTH, months);
        }
        if (years != 0) {
            // Aggiunge, o toglie, 'years'
            date.add(Calendar.YEAR, years);
        }
        return date;
    }


    /**
     * <p>Restituisce la data passata come argomento,
     * addizionata o sottratta di:
     * <em><ul>
     * <li>tot giorni</li>
     * <li>tot mesi</li>
     * <li>tot anni</li>
     * </ul></em>
     * con giorni, mesi e anni passati come argomenti.</p>
     * <p><ol>
     * <li>Se si vuol <em>sottrarre</em> anzich&eacute; <em>aggiungere</em>
     * basta passare valori negativi dei parametri.</li>
     * <li>Se si vuol sottrarre o aggiungere solo uno o solo un paio di
     * parametri, ma non tutti e tre, basta passare zero <code>(0)</code>
     * come valore dei parametri non interessanti.</li>
     * </ol></p>
     * <p>
     * Notare che per ottenere qualsiasi data sarebbe sufficiente
     * aggiungere o sottrarre il numero giusto di soli giorni.
     * Tuttavia in alcuni casi pu&ograve; essere pi&uacute;
     * comodo passare lo shift giusto di mesi e anni piuttosto che
     * ricavare la data voluta attraverso il numero di giorni in pi&uacute;
     * o in meno, e per questo motivo il metodo ha 3 possibili parametri
     * (anche se nulla vieta di utilizzare solo il primo per ottenere
     * tutte le date volute).</p>
     *
     * @param date   data sulla quale applicare la somma o sottrazione dei giorni/mesi/anni passati
     * @param days   numero di giorni da aggiungere o togliere alla data corrente per ottenere una data desiderata
     * @param months numero di mesi   da aggiungere o togliere alla data corrente per ottenere una data desiderata
     * @param years  numero di anni   da aggiungere o togliere alla data corrente per ottenere una data desiderata
     * @return <code>GregorianCalendar</code> - la data desiderata, pari alla data corrente aggiunta o sottratta di giorni e/o mesi e/o anni specificati dai parametri
     * @throws NotFoundException se si generano eccezioni sul controllo della data in input
     */
    public static GregorianCalendar getDate(GregorianCalendar date,
                                            int days,
                                            int months,
                                            int years)
                                     throws NotFoundException {
        // Controlli sulla data in input
        if (date == null) {
            String msg = FOR_NAME + "Si e\' tentato di invocare il metodo di controllo su un oggetto non pronto. Controllare il valore dell\'argomento.\n";
            throw new NotFoundException(msg);
        }
        try {
            if (days != 0) {
                // Aggiunge, o toglie, 'days'
                date.add(Calendar.DATE, days);
            }
            if (months != 0) {
                // Aggiunge, o toglie, 'months'
                date.add(Calendar.MONTH, months);
            }
            if (years != 0) {
                // Aggiunge, o toglie, 'years'
                date.add(Calendar.YEAR, years);
            }
            return date;
        }  catch (NullPointerException npe) {
            StackTraceElement[] stackTrace = npe.getStackTrace();
            StringBuffer trace = new StringBuffer("\n");
            for (StackTraceElement stack : stackTrace)
                trace.append(stack.toString()).append("\n");
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a: " + npe.getMessage() + trace.toString();
            log.warning(msg + "Problema probabilmente legato al costruttore di SimpleDateFormat, che restituisce una NullPointerException se la maschera in base a cui l\'oggetto viene generato e\' null.\n");
            throw new NotFoundException(msg, npe);
        }
    }


    /**
     * Formatta una data di tipo <code>java.util.GregorianCalendar</code>
     * secondo il formato standard italiano, ovvero in base alla maschera:
     * <pre>"gg/mm/aaaa"</pre>
     * e la restituisce sotto forma di oggetto String.
     *
     * @param date un java.util.GregorianCalendar da formattare e convertire
     * @return <code>String</code> - una rappresentazione String della data originale
     */
    public static String format(GregorianCalendar date) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        String dateFormatted = fmt.format(date.getTime());
        return dateFormatted;
    }


    /**
     * Formatta una data di tipo <code>java.util.Date</code>
     * secondo il formato standard italiano, ovvero in base alla maschera:
     * <pre>"gg/mm/aaaa"</pre>
     * e la restituisce sotto forma di oggetto String.
     *
     * @param date una java.util.Date da formattare e convertire
     * @return <code>String</code> - una rappresentazione String della data originale
     */
    public static String format(java.util.Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        String dateFormatted = fmt.format(date.getTime());
        return dateFormatted;
    }


    /**
     * <p>Formatta una data di tipo <code>java.util.GregorianCalendar</code>
     * secondo un filtro in base a maschera, ovvero in base a un pattern
     * passato come argomento
     * e la restituisce sotto forma di oggetto String.</p>
     * <p>
     * Per un elenco dei valori di pattern ammessi, v. la classe
     * {@link SimpleDateFormat}
     * (<a href="http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html">
     * javadoc</a>)</p>
     *
     * @param date un java.util.GregorianCalendar da formattare e convertire
     * @param mask la maschera in base a cui formattare la data
     * @return <code>String</code> - una rappresentazione String della data originale
     * @throws CommandException se il pattern
     */
    public static String format(GregorianCalendar date,
                                String mask)
                         throws CommandException {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat(mask);
            String dateFormatted = fmt.format(date.getTime());
            return dateFormatted;
        } catch (NullPointerException npe) {
            StackTraceElement[] stackTrace = npe.getStackTrace();
            StringBuffer trace = new StringBuffer("\n");
            for (StackTraceElement stack : stackTrace)
                trace.append(stack.toString()).append("\n");
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a: " + npe.getMessage() + trace.toString();
            log.warning(msg + "Problema probabilmente legato al costruttore di SimpleDateFormat, che restituisce una NullPointerException se la maschera in base a cui l\'oggetto viene generato e\' null.\n");
            throw new CommandException(msg, npe);
        } catch (IllegalArgumentException iae) {
            String msg = FOR_NAME + "Si e\' verificato un problema nella generazione di un oggetto per la formattazione delle date.\n" + iae.getMessage();
            log.warning(msg + "Problema legato con ogni probabilita\' al costruttore di SimpleDateFormat, che restituisce una IllegalArgumentException se il pattern di formattazione fornito non e\' valido.\n");
            throw new CommandException(msg, iae);
        } catch (MissingResourceException mre) {
            String msg = FOR_NAME + "Problema nel recupero di un valore.\n" + mre.getMessage();
            log.warning(msg + "Si e\' verificato un problema nel metodo di formattazione della data.\n");
            throw new CommandException(msg, mre);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema. Impossibile visualizzare i risultati.\n" + e.getLocalizedMessage();
            log.warning(msg + "Attenzione: si e\' verificato un problema nel metodo di formattazione della data.\n");
            throw new CommandException(msg, e);
        }
    }


    /**
     * <p>Formatta una data che riceve sotto forma di oggetto <code>String</code>
     * basandosi su un parametro che ne indica il formato di partenza
     * e su un parametro che indica il formato che dovr&agrave; avere
     * un oggetto di tipo <code>java.util.Date</code>
     * restituito come tipo di ritorno.</p>
     * <p>Per un elenco dei valori di pattern ammessi, v. la classe
     * {@link SimpleDateFormat}
     * (<a href="http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html">
     * javadoc</a>)</p>
     * <p>Esempio:<pre><ul> 
     * <li><strong>"Tue Jul 07 00:00:00 CEST 2020"</strong></li>
     * <li> EEE MMM dd HH:mm:ss zzz  yyyy</li></ul></pre></p>
     *
     * @param date una String che deve essere convertita
     * @param initDateFormat il formato con cui la String e' formattata
     * @param endDateFormat il formato che l'oggetto Date restituito dovra' avere
     * @return <code>java.util.Date</code> - un oggetto Date costruito a partire dalla String ricevuta e formattato secondo il formato indicato
     * @throws CommandException se si verifica un problema nella conversione di tipo o in qualche tipo di puntamento
     *
     */
    public static Date format(String date,
                              String initDateFormat,
                              String endDateFormat)
                       throws CommandException {
        Date returnDate = null;
        try {
            Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
            String parsedDate = formatter.format(initDate);
            returnDate = formatter.parse(parsedDate);
        } catch (ParseException pe) {
            String msg = FOR_NAME + "Si e\' verificato un problema. Verificare che il formato della String da convertire (\'" + date + "\') corrisponda a quello del pattern di formattazione (" + initDateFormat + ").\n";
            log.warning(msg + "Attenzione: si e\' verificato un problema nel metodo di formattazione della data.\n" + pe.getLocalizedMessage());
            throw new CommandException(msg, pe);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema. Impossibile visualizzare i risultati.\n" + npe.getLocalizedMessage();
            log.warning(msg + "Attenzione: si e\' verificato un problema nel puntamento a qualche parametro.\n");
            throw new CommandException(msg, npe);
        }
        return returnDate;
    }

    
    /**
     * <p>Formatta una data che riceve sotto forma di oggetto <code>String</code>
     * basandosi su un formato default come formato di partenza
     * e su un formato di default per un oggetto di tipo 
     * <code>java.util.Date</code>
     * restituito come tipo di ritorno.</p>
     *
     * @param date una data sotto forma di String che deve essere convertita
     * @return <code>java.util.Date</code> - un oggetto Date costruito a partire dalla String ricevuta e formattato secondo un formato predefinito
     * @throws CommandException se si verifica un problema nella conversione di tipo o in qualche tipo di puntamento
     *
     */
    public static Date format(String date)
                       throws CommandException {
        Date returnDate = null;
        try {
            returnDate = format(date, DATA_SQL_PATTERN, DATA_SQL_PATTERN);
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Si e\' verificato un problema. Impossibile convertire la String \'" + date + "\' in una java.util.Date.\n" + ce.getLocalizedMessage();
            log.warning(msg + "Attenzione: si e\' verificato un problema nella conversione di tipi. Impossibile usare il pattern predefinito!\n");
            throw new CommandException(msg, ce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema. Impossibile visualizzare i risultati.\n" + npe.getLocalizedMessage();
            log.warning(msg + "Attenzione: si e\' verificato un problema nel puntamento a qualche parametro.\n");
            throw new CommandException(msg, npe);
        }
        return returnDate;
    }

    
    /**
     * <p>Formatta un orario che riceve sotto forma di oggetto <code>String</code>
     * basandosi su un parametro che ne indica il formato di ingresso.</p>
     *
     * @param time un orario sotto forma di String, che deve essere convertito
     * @param timeFormat il formato con cui la String da converire e' formattata
     * @return <code>java.sql.Time</code> - un oggetto Time costruito a partire dalla String ricevuta
     * @throws CommandException se si verifica un problema nella conversione di tipo o in qualche tipo di puntamento
     */
    public static Time format(String time,
                              String timeFormat)
                       throws CommandException {
        Time returnTime = null;
        try {
            DateFormat format = new SimpleDateFormat(timeFormat);
            Date initDate = format.parse(time);
            returnTime = new Time(initDate.getTime());
        } catch (ParseException pe) {
            String msg = FOR_NAME + "Si e\' verificato un problema: l\'ora fornita in input " + time + " non e\' convertibile!\n" + pe.getLocalizedMessage();
            log.warning(msg + "Attenzione: si e\' verificato un problema nel metodo di formattazione della data.\n");
            throw new CommandException(msg, pe);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema. Impossibile visualizzare i risultati.\n" + npe.getLocalizedMessage();
            log.warning(msg + "Attenzione: si e\' verificato un problema nel puntamento a qualche parametro.\n");
            throw new CommandException(msg, npe);
        }
        return returnTime;
    }


    /**
     * <p>Converte un'istanza di <code>java.util.GregorianCalendar</code>
     * in un'istanza di <code>java.util.Date</code></p>
     * <p>
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/GregorianCalendar.html">
     * java.util.GregorianCalendar</a> rispetto a
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/Date.html">
     * java.util.Date</a> &egrave; un oggetto preferibile per
     * la gestione del tempo, perch&eacute; la {@link java.util.Date}
     * <cite id="stackoverflow">ha molti metodi di trasformazione deprecati,
     * mentre il Calendar ha una localizzazione pi&uacute; agevole
     * <a href="http://stackoverflow.com/questions/1404210/java-date-vs-calendar">
     * (v.)</a></cite></p>
     * Siccome per motivi di retrocompatibilit&agrave; spesso non va bene
     * usare un Calendar ma ci vuole una Date (la {@link DBWrapper} usa le Date),
     * viene messo a disposizione questo metodo, che implementa la semplice
     * trasformazione da {@link GregorianCalendar} a {@link Date}.
     *
     * @param date un java.util.GregorianCalendar da trasformare in una java.util.Date
     * @return <code>java.util.Date</code> - una java.util.Date in cui il GregorianCalendar passato come argomento e' stato trasformato
     */
    public static java.util.Date convert(GregorianCalendar date) {
        return date.getTime();
    }


    /**
     * <p>Converte un'istanza di <code>java.util.Date</code>
     * in un'istanza di <code>java.sql.Date</code></p>
     * <p>
     * Per rappresentare le date
     * nelle interrogazioni SQL standard non va bene
     * un'istanza di {@link java.util.Date}
     * ma ci vuole piuttosto un'istanza di {@link java.sql.Date}.</p>
     *
     * @param date una java.util.Date da trasformare in una java.sql.Date
     * @return <code>java.sql.Date</code> - una java.sql.Date in cui la java.util.Date passata come argomento e' stato trasformata
     */
    public static java.sql.Date convert(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }


    /**
     * <p>Restituisce l'anno corrente sotto forma di intero primitivo.</p>
     * <p>Questo metodo pu&ograve; essere utilizzato ad esempio nei footer,
     * per l'indicazione del copyright (&copy;).</p>
     *
     * @return <code>String</code> - l'anno corrente sotto forma di intero primitivo
     */
    public static int getCurrentYearAsInt() {
        int yearPosition = Calendar.YEAR;
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(yearPosition);
        return year;
    }


    /**
     * <p>Restituisce l'anno corrente in formato String.</p>
     * <p>Questo metodo &egrave; generalmente utilizzato ad esempio nei footer,
     * per l'indicazione del copyright (&copy;).</p>
     *
     * @return <code>String</code> - l'anno corrente sotto forma di oggetto String
     */
    public static String getCurrentYear() {
        int yearPosition = Calendar.YEAR;
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(yearPosition);
        Integer yearWrapper = new Integer(year);
        return yearWrapper.toString();
    }


    /**
     * <p>Restituisce l'ora corrente in formato String.</p>
     * <p>L'ora restituita &egrave; in formato h-24.</p>
     *
     * @return <code>String</code> - l'ora corrente, in formato h-24, sotto forma di oggetto String
     */
    public static String getCurrentHour() {
        int hourPosition = Calendar.HOUR_OF_DAY;
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(hourPosition);
        Integer hourWrapper = new Integer(hour);
        return hourWrapper.toString();
    }


    /**
     * <p>Restituisce i minuti correnti in formato String.</p>
     * <p>I minuti restituiti sono in formato m-60.<br />
     * E.g., <code>at 10:04:15.250 PM the MINUTE is 4.</code><br />
     * But: the method will return:
     * <code>"04"</code> (as a String) .</p>
     * <p><small>Notice that this method makes use of Dynamic String - as known as
     * "StringBuffer", because:<br />
     * <cite id="Java-API">"Strings are constant;
     * their values cannot be changed after they are created.<br />
     * String buffers support mutable strings.<br />
     * Because String objects are immutable they can be shared."</cite></small>
     * </p>
     *
     * @return <code>String</code> - i minuti correnti, in formato "mm", sotto forma di oggetto String
     */
    public static String getCurrentMinutes() {
        int minutePosition = Calendar.MINUTE;
        Calendar rightNow = Calendar.getInstance();
        int minutes = rightNow.get(minutePosition);
        Integer minuteWrapper = new Integer(minutes);
        StringBuffer minutesAsDynamicString = new StringBuffer(minuteWrapper.toString());
        if (minutes < 10) {
            minutesAsDynamicString.insert(0, "0");
        }
        return minutesAsDynamicString.toString();
    }


    /**
     * <p>Restituisce l'orario corrente sotto forma di oggetto
     * {@link java.sql.Time Time}.</p>
     * <p>L'orario restituito avr&agrave; il formato <code>"hh:mm:ss"</code>
     * dove i secondi non saranno gli effettivi secondi correnti
     * al momento del calcolo, ma i secondi allo scoccar del minuto
     * (cio&egrave; azzerati).</p>
     *
     * @return <code>Time</code> - l'ora corrente, in formato "hh:mm:ss", con i secondi arbitrariamente impostati a zero
     */
    public static Time getCurrentTime() {
        return Time.valueOf(getCurrentHour() + ":" + getCurrentMinutes() + ":00");
    }


    /**
     * Restituisce lo UNIX EPOCH sotto forma di GregorianCalendar.
     *
     * @return <code>GregorianCalendar</code> - lo UNIX EPOCH sotto forma di oggetto GregorianCalendar
     */
    public static GregorianCalendar getUnixEpoch() {
        return new GregorianCalendar(1970, 0, 1);
    }


    /**
     * <p>Restituisce il numero di anni che intercorrono tra due date passate come argomento.
     * Se le date sono la data di nascita di una persona e la data odierna, restituisce
     * l'et&agrave; della persona.</p>
     *
     * @param start data iniziale
     * @param end   data finale
     * @return  <code>int</code> - il numero di anni che intercorrono tra end e start
     */
    public static int getYearsInBetween(Date start, Date end) {
        GregorianCalendar startAsCalendar = new GregorianCalendar();
        startAsCalendar.setTime(start);
        GregorianCalendar endAsCalendar = new GregorianCalendar();
        endAsCalendar.setTime(end);
        int diff = endAsCalendar.get(Calendar.YEAR) - startAsCalendar.get(Calendar.YEAR);
        return diff;
    }

}
