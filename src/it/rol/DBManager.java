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

package it.rol;

import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


/**
 * <p>DBManager &egrave; la classe utilizzata per stabilire 
 * su quale database deve insistere un'istanza della web-application
 * <code>Rischi on Line (rol)</code> 
 * esponendo le relative informazioni attraverso metodi accessori.</p>
 * <p>Pensata per evitare riferimenti circolari tra il ConfigManager
 * ed il DBWrapper, questa classe supporta Request e Response
 * e pu&ograve; essere utilizzata per calcolare ulteriori variabili,
 * se necessario.</p>
 *
 * <p>Mon 30 Sep 2024 01:40:03 PM CEST</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class DBManager extends HttpServlet {

    /**
     * La serializzazione necessita della dichiarazione
     * di una costante di tipo long identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = -4075937531029421962L;
    /**
     * <p>Logger della classe per scrivere i messaggi di errore.</p>
     * <p>All logging goes through this logger.</p>
     * <p>To avoid the 'Read access to enclosing field Main.log
     * is emulated by a synthetic accessor method' warning,
     * the visibility is changed to 'friendly' (id est 'default',
     * id est 'visible from the same package').</p>
     * <p>In altre parole:</ul>
     * non &egrave; privata ma Default (friendly) per essere visibile
     * negli elementi ovverride implementati da questa classe.</p>
     */
    /* default */ static Logger log = Logger.getLogger(DBManager.class.getName());
    /**
     *  Nome di questa classe
     *  (utilizzato per contestualizzare i messaggi di errore)
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     * <p>Stringa per il puntamento al db di produzione</p>
     */
    private static StringBuffer contextDbName = new StringBuffer("java:comp/env/jdbc/rol");


    /**
     * Inizializza (staticamente) le variabili globali
     * che saranno utilizzate anche dalle altre classi:
     * <ul>
     * <li> connessione al database: <code>db</code> </li>
     * <li> tabella hash di tutte le classi richiamabili dall'applicazione </li>
     * <li> nome del parametro identificante la command corrente </li>
     * <li> pagina di errore </li>
     * etc...
     * </ul>
     *
     * @param config la configurazione usata dal servlet container per passare informazioni alla servlet <strong>durante l'inizializzazione</strong>
     * @throws ServletException una eccezione che puo' essere sollevata quando la servlet incontra difficolta'
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        /*
         *  Inizializzazione da superclasse
         */
        super.init(config);
        /*
         * Stabilisce su quale database insiste la connessione
         */
        log.info("==>" + getServletContext().getRealPath("/") + "<==");
        // Prima deve capire su quale database deve insistere
        // Di default va in produzione, ma se non siamo in produzione deve andare in locale
        if ( !getServletContext().getRealPath("/").equals("/var/lib/tomcat9/webapps/rischi/") ) {
            contextDbName = new StringBuffer("java:comp/env/jdbc/roldev");
        }
    }


    /**
     * <p>Restituisce la stringa necessaria al connettore del database.</p>
     * <p><cite id="https://stackoverrun.com/it/q/3104484">
     * java:comp/env is the node in the JNDI tree where you can find properties
     * for the current Java EE component (a webapp, or an EJB).<br />
     * <code>Context envContext = (Context)initContext.lookup("java:comp/env");</code>
     * allows defining a variable pointing directly to this node. It allows doing
     * <code>SomeBean s = (SomeBean) envContext.lookup("ejb/someBean");
     * DataSource ds = (DataSource) envContext.lookup("jdbc/dataSource");</code>
     * rather than
     * <code>SomeBean s = (SomeBean) initContext.lookup("java:comp/env/ejb/someBean");
     * DataSource ds = (DataSource) initContext.lookup("java:comp/env/jdbc/dataSource");</code>
     * Relative paths instead of absolute paths. That's what it's used for.<br />
     * It's an in-memory global hashtable where you can store global
     * variables by name.
     * The "java:" url scheme causes JNDI to look for a
     * javaURLContextFactory class, which is usually provided by your
     * app container, e.g. here is Tomcat's implementation javadoc.</cite></p>
     * <p>Metodo getter sulla variabile di classe.</p>
     *
     * @return <code>String</code> - il nome usato dal DbWrapper per realizzare il puntamento jdbc
     */
    public static String getDbName() {
        return new String(contextDbName);
    }
    
}
