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

package it.rol.exception;


/**
 * <p>Questa eccezione viene ritornata quando si verifica un generico errore
 * SQL. In questo modo si maschera agli strati superiori dell'applicazione
 * il fatto che si sta usando un database relazionale.</p>
 * <cite id="horton">
 * <p>Tutte le eccezioni personalizzate devono sempre avere <code>Throwable</code> 
 * come superclasse, altrimenti esse non definiranno un'eccezione.<br />
 * Sebbene una eccezione personalizzata possa essere derivata da una qualsiasi
 * delle eccezioni standard, la miglior strategia &egrave; derivarla direttamente
 * dalla classe <code>Exception</code>.<br />
 * Ci&ograve; permetter&agrave; al compilatore di tener traccia di dove tali
 * eccezioni siano lanciate nel flusso del programma, e di identificare se esse
 * devono essere catturate oppure dichiarate come propagabili in un metodo.<br />
 * Se si usasse <code>RuntimeException</code> o una delle sue sottoclassi, il 
 * controllo del compilatore nei blocchi <code>catch</code> per le eccezioni
 * personalizzate sarebbe soppresso.</p>  
 * </cite><br />
 *
 * @author <a href="mailto:gianroberto.torre@gmail.comt">Giovanroberto Torre</a>
 */
public class WebStorageException extends Exception {
    
    /**
     * <p>Necessario in quanto estende Exception.</p>
     */
	private static final long serialVersionUID = 1L;

	
    /**
     * <cite id="horton">
     * <p>Per convenzione, le eccezioni personalizzate devono includere 
     * un costruttore di default.<br />
     * Il messaggio memorizzato nella superclasse dell'eccezione personalizzata,
     * cio&egrave; <code>Exception</code> - di
     * fatto in <code>Throwable</code>, che &egrave; la superclasse di 
     * <code>Exception</code> - sar&agrave; automaticamente inizializzato con
     * il nome della classe personalizzata 
     * (qui: <code>it.univr.di.uol.CommandException</code>), se il costruttore
     * della propria classe personalizzata sar&agrave; utilizzato.</p> 
     * </cite>
     */
	public WebStorageException() {
        super();
    }
    
	
    /**
     * <cite id="horton">
     * <p>Per convenzione, le eccezioni personalizzate devono includere, 
     * oltre a un costruttore di default, anche un costruttore che accetta
     * come parametro un oggetto String.</p>
     * <p>La String passata a questo secondo costruttore sar&agrave; aggiunta
     * al nome della classe per formare il messaggio memorizzato nell'oggetto
     * eccezione.</p>
     * </cite>
     * @param msg   una String che verr&agrave; aggiunta a tempo di esecuzione al nome della classe per formare il messaggio memorizzato nell'eccezione
     */
    public WebStorageException(String msg) {
        super(msg);
    }
    
    
    /**
     * <p>Richiama il costruttore della superclasse che accetta uno specifico
     * messaggio di dettaglio e richiama a sua volta il metodo 
     * <code>
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/lang/Throwable.html#fillInStackTrace()">fillInStackTrace()</a>
     * </code> per inizializzare i dati della traccia dello stack nell'oggetto
     * (figlio di) Throwable, appena creato.</p>
     * <p>Oltre a questo comportamento, identico a quello del costruttore
     * convenzionale:
     * <p><code>public WebStorageException(String msg)</code>,</p>
     * il presente costruttore aggiunge la stampa dello stack dell'eccezione
     * generata, passata come parametro, per l'identificazione esatta del 
     * punto del codice in cui la stessa si &egrave; verificata.
     * </p> 
     *<p>&Egrave; evidente che il richiamo del costruttore da superclasse fatto
     * in questo modo &egrave; pi&uacute; oneroso del richiamo standard 
     * convenzionale, ma il vantaggio che si ricava ottenendo il numero di
     * riga esatto in cui si &egrave; verificata l'eccezione (rispetto al 
     * generico numero di riga in cui viene gestita l'eccezione nel catch) 
     * in fase di debug pu&ograve; fare veramente la differenza.</p>
     * 
     * @param msg   una String che verr&agrave; aggiunta a tempo di esecuzione al nome della classe per formare il messaggio memorizzato nell'eccezione
     * @param e     l'eccezione che si &egrave; verificata per l'aggiunta al messaggio della traccia dello stack
     */
    public WebStorageException(String msg, Throwable e) {
        super(msg + getLocalizedMessage(e));
    }
    
    
    /**
     * <p>Recupera la traccia dello stack di esecuzione di un oggetto 
     * <code>Throwable</code> passato come argomento e la restituisce
     * al chiamante sotto forma di oggetto String.</p>
     * 
     * @param e     un'eccezione di cui si vuol recuperare la traccia dello stack di esecuzione
     * @return      una String contenente la traccia dello stack di esecuzione dell'oggetto Throwable passato come argomento
     */
    public static String getLocalizedMessage(Throwable e) {
        StringBuffer trace = new StringBuffer("\n");
        trace.append(e.getMessage());
        trace.append("\n");
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement stack : stackTrace) 
            trace.append(stack.toString()).append("\n");
        return trace.toString();
    }
    
}
