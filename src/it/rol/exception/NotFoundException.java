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
 * <p>Questa eccezione viene ritornata quando la chiave fornita alla
 * query non corrisponde a nessua riga nella tabella/e coinvolte.</p>
 * <p>Pu&ograve; inoltre essere utilizzata per sollevare eccezioni
 * nei casi in cui una risorsa specifica dell'applicazione non sia stata
 * trovata, o un valore atteso non sia stato riscontrato.</p>
 * <p>Estende {@link WebStorageException}, la quale a sua volta estende 
 * {@link Exception}; in questo modo la catena di ereditariet&agrave; 
 * con la classe padre di tutte le eccezioni &egrave; mantenuta.
 * Tutte le eccezioni devono essere, infatti, figlie - o discendenti, 
 * per l'appunto - da {@link Throwable}, che &egrave; il padre assoluto
 * di tutte le eccezioni possibili in Java.</p>
 * <small>V. {@link WebStorageException} per ulteriori informazioni.</small>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class NotFoundException extends WebStorageException {
    
    /**
	 * <p>Necessario per l'eccezione (cio&egrave; necessario in quanto
	 * estende WebStorageException, che estende Exception, che &egrave;
	 * figlia di Throwable, che implementa {@link java.io.Serializable}).</p>
	 */
	private static final long serialVersionUID = 1L;

	
    /**
     * <cite id="horton">
     * <p>Per convenzione, le eccezioni personalizzate devono includere 
     * un costruttore di default.<br />
     * Il messaggio memorizzato nella superclasse dell'eccezione personalizzata,
     * cio&egrave; in questo caso <code>{@link WebStorageException}</code> 
     * - ma di fatto in <code>Throwable</code>, che &egrave; la superclasse di 
     * <code>Exception</code> che &egrave; a sua volta estesa dalla
     * superclasse diretta della presente - sar&agrave; 
     * automaticamente inizializzato con
     * il nome della classe personalizzata 
     * (qui: <code>it.rol.exception.NotFoundException</code>), se il costruttore
     * della propria classe personalizzata sar&agrave; utilizzato.</p> 
     * </cite>
     */
	public NotFoundException() {
        super();
    }
    
	
    /**
     * <cite id="horton">
     * <p>Per convenzione, le eccezioni personalizzate devono includere, 
     * oltre a un costruttore di default, anche un costruttore che accetta
     * come parametro un oggetto <code>String</code>.</p>
     * <p>La String passata a questo secondo costruttore sar&agrave; aggiunta
     * al nome della classe per formare il messaggio memorizzato nell'oggetto
     * eccezione.</p>
     * </cite>
     * 
     * @param msg   una String che verr&agrave; aggiunta a tempo di esecuzione al nome della classe per formare il messaggio memorizzato nell'eccezione
     */
    public NotFoundException(String msg) {
        super(msg);
    }
    
    
    /**
     * <p>Oltre ai due costruttori standard, questa classe definische
     * anche un costruttore (da superclasse) che accetta uno specifico
     * messaggio di dettaglio e che richiama, a sua volta, il metodo:<br /> 
     * <code>
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/lang/Throwable.html#fillInStackTrace()">fillInStackTrace()</a>
     * </code><br /> per inizializzare i dati della traccia dello stack nell'oggetto
     * (figlio di) Throwable, appena creato.</p>
     * <p>Oltre a questo comportamento, identico a quello del costruttore
     * convenzionale:
     * <p><code>public CommandException(String msg)</code>,</p>
     * il presente costruttore aggiunge la stampa dello stack dell'eccezione
     * generata, passata come parametro, per l'identificazione esatta del 
     * punto del codice in cui la stessa si &egrave; verificata.
     * </p> 
     *<p>&Egrave; evidente che il richiamo del costruttore da superclasse fatto
     * in questo modo &egrave; pi&uacute; oneroso del richiamo standard 
     * convenzionale, ma il vantaggio che si ricava ottenendo il numero di
     * riga esatto in cui si &egrave; verificata l'eccezione (rispetto al 
     * generico numero di riga in cui viene gestita l'eccezione nel catch) 
     * in fase di debug &egrave; impagabile.</p>
     * 
     * @param msg   una String che verr&agrave; aggiunta a tempo di esecuzione al nome della classe per formare il messaggio memorizzato nell'eccezione
     * @param e     l'eccezione che si &egrave; verificata per l'aggiunta al messaggio della traccia dello stack
     */
    public NotFoundException(String msg, Throwable e) {
        super(msg + getLocalizedMessage(e));
    }
    
    
    /**
     * Oltre ai costruttori...
     * TODO: COMPLETA COMMENTO 
     * @param e
     */
    public NotFoundException(Throwable e) {
        super(getLocalizedMessage(e));
    }
    
}
