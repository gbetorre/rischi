/*
 *   Risk Mapping Software: Applicazione web per la gestione di 
 *   sondaggi inerenti al rischio corruttivo cui i processi organizzativi
 *   dell'ateneo possono essere esposti e per la gestione di reportistica
 *   e mappature per la gestione dei "rischi on line" (rol).
 *
 *   Risk Mapping Software (rms)
 *   web applications to make survey about the amount and kind of risk
 *   which each process is exposed, and to publish, and manage,
 *   report and risk information.
 *   Copyright (C) 2022 renewed 2023 Giovanroberto Torre
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import it.rol.bean.ActivityBean;
import it.rol.bean.BeanUtil;
import it.rol.bean.CodeBean;
import it.rol.bean.DepartmentBean;
import it.rol.bean.InterviewBean;
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
import it.rol.bean.QuestionBean;
import it.rol.bean.RiskBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.CommandException;
import it.rol.exception.WebStorageException;


/**
 * <p><code>DBWrapper.java</code> &egrave; la classe che implementa
 * l'accesso ai database utilizzati dall'applicazione nonch&eacute;
 * l'esecuzione delle query e la gestione dei risultati restituiti,
 * che impacchetta in oggetti di tipo JavaBean e restituisce al chiamante.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class DBWrapper implements Query, Constants {

    /**
     * <p>La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.<br />
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera
     * automatica dalla JVM, e questo potrebbe portare a errori
     * riguardo alla serializzazione).</p>
     * <p><small>I moderni IDE, come Eclipse, permettono di assegnare
     * questa costante in due modi diversi:
     *  <ul>
     *  <li>o attraverso un valore di default assegnato a questa costante<br />
     *  (p.es. <code>private static final long serialVersionUID = 1L;</code>)
     *  </li>
     *  <li>o attraverso un valore calcolato tramite un algoritmo implementato
     *  internamente<br /> (p.es.
     *  <code>private static final long serialVersionUID = -8762739881448133461L;</code>)
     *  </li></small></p>
     */
    private static final long serialVersionUID = -8762739881448133461L;
    /**
     * <p>Logger della classe per scrivere i messaggi di errore.
     * All logging goes through this logger.</p>
     * <p>Non &egrave; privata ma Default (friendly) per essere visibile
     * negli oggetti ovverride implementati da questa classe.</p>
     */
    protected static Logger LOG = Logger.getLogger(DBWrapper.class.getName());
    /**
     * <p>Nome di questa classe
     * (viene utilizzato per contestualizzare i messaggi di errore).</p>
     * <p>Non &egrave; privata ma Default (friendly) per essere visibile
     * negli oggetti ovverride implementati da questa classe.</p>
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     * <p>Connessione db postgres di pol.</p>
     */
    protected static DataSource prol_manager = null;
    /**
     * <p>Recupera da Servlet la stringa opportuna per il puntamento del DataSource.</p>
     */
    private static String contextDbName = ConfigManager.getDbName();


    /**
     * <p>Costruttore. Prepara le connessioni.</p>
     * <p>Viene usata l'interfaccia
     * <a href="https://docs.oracle.com/javase/7/docs/api/javax/sql/DataSource.html">DataSource</a>
     * per ottenere le connessioni.</p>
     * <p>Usa il pattern Singleton per evitare di aprire connessioni inutili
     * in caso di connessioni gi&agrave; aperte.</p>
     *
     * @throws WebStorageException in caso di mancata connessione al database per errore password o dbms down
     * @see DataSource
     */
    public DBWrapper() throws WebStorageException {
        if (prol_manager == null) {
            try {
                prol_manager = (DataSource) ((Context) new InitialContext()).lookup(contextDbName);
                if (prol_manager == null)
                    throw new WebStorageException(FOR_NAME + "La risorsa " + contextDbName + "non e\' disponibile. Verificare configurazione e collegamenti.\n");
            } catch (NamingException ne) {
                throw new WebStorageException(FOR_NAME + "Problema nel recuperare la risorsa jdbc/prol per problemi di naming: " + ne.getMessage());
            } catch (Exception e) {
                throw new WebStorageException(FOR_NAME + "Errore generico nel costruttore: " + e.getMessage(), e);
            }
        }
    }

    /* ********************************************************** *
     *               Metodi generali dell'applicazione            *
     * ********************************************************** */

    /**
     * <p>Restituisce un Vector di Command.</p>
     *
     * @return <code>Vector&lt;ItemBean&gt;</code> - lista di ItemBean rappresentanti ciascuno una Command dell'applicazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public Vector<ItemBean> lookupCommand()
                                   throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ItemBean cmd = null;
        Vector<ItemBean> commands = new Vector<>();
        try {
            con = prol_manager.getConnection();
            pst = con.prepareStatement(LOOKUP_COMMAND);
            pst.clearParameters();
            rs = pst.executeQuery();
            while (rs.next()) {
                cmd = new ItemBean();
                BeanUtil.populate(cmd, rs);
                commands.add(cmd);
            }
            return commands;
        } catch (SQLException sqle) {
            throw new WebStorageException(FOR_NAME + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = "Connessione al database in stato inconsistente!\nAttenzione: la connessione vale " + con + "\n";
                LOG.severe(msg);
                throw new WebStorageException(FOR_NAME + msg + npe.getMessage(), npe);
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage(), sqle);
            }
        }
    }


    /**
     * <p>Restituisce
     * <ul>
     * <li>il massimo valore del contatore identificativo di una
     * tabella il cui nome viene passato come argomento</li>
     * <li>oppure zero se nella tabella non sono presenti record.</li>
     * </ul></p>
     *
     * @param table nome della tabella di cui si vuol recuperare il max(id)
     * @return <code>int</code> - un intero che rappresenta il massimo valore trovato, oppure zero se non sono stati trovati valori
     * @throws WebStorageException se si verifica un problema nella query o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "static-method", "null" })
    public int getMax(String table)
               throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            int count = 0;
            String query = SELECT_MAX_ID + table;
            con = prol_manager.getConnection();
            pst = con.prepareStatement(query);
            pst.clearParameters();
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        }  catch (SQLException sqle) {
            String msg = FOR_NAME + "Impossibile recuperare il max(id).\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Restituisce
     * <ul>
     * <li>il minimo valore del contatore identificativo di una
     * tabella il cui nome viene passato come argomento</li>
     * <li>oppure zero se nella tabella non sono presenti record.</li>
     * </ul></p>
     *
     * @param table nome della tabella di cui si vuol recuperare il min(id)
     * @return <code>int</code> - un intero che rappresenta il minimo valore trovato, oppure zero se non sono stati trovati valori
     * @throws WebStorageException se si verifica un problema nella query o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "static-method", "null" })
    public int getMin(String table)
               throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            int count = 0;
            String query = SELECT_MIN_ID + table;
            con = prol_manager.getConnection();
            pst = con.prepareStatement(query);
            pst.clearParameters();
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        }  catch (SQLException sqle) {
            String msg = FOR_NAME + "Impossibile recuperare il max(id).\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }

    
    /**
     * <p>Restituisce
     * <ul>
     * <li>il massimo codice del rischio se per tale categoria di codice  
     * ne sono stati gi&agrave; inseriti</li>
     * <li>oppure il primo codice se nella tabella non sono presenti rischi 
     * inseriti nella categoria identificata da quel tipo di codice.</li>
     * </ul></p>
     *
     * @param code prefisso del codice di cui si vuol recuperare il primo codice nuovo utile all'inserimento
     * @return <code>String</code> - il codice utile all'inserimento
     * @throws WebStorageException se si verifica un problema nella query o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public String getMaxRiskCode(String code)
                          throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            CodeBean prevCode = null;
            String nextCode = null;
            StringBuffer codeFormatted = new StringBuffer()
                    .append(code)
                    .append(PER_CENT);
            try {
                pst = con.prepareStatement(SELECT_MAX_RISK_CODE);
                pst.clearParameters();
                pst.setString(1, String.valueOf(codeFormatted));
                rs = pst.executeQuery();
                if (rs.next()) {
                    prevCode = new CodeBean();
                    BeanUtil.populate(prevCode, rs);
                    String prevCodeAsString = prevCode.getNome();
                    if (prevCodeAsString != null && !prevCodeAsString.equals(VOID_STRING)) {
                        // Identifica l'ultimo progressivo per la tipologia di codice data
                        int start = prevCodeAsString.indexOf(DOT);
                        int maxCode = Integer.parseInt(prevCodeAsString.substring(++start, prevCodeAsString.length()));
                        // Lo incrementa di un'unità
                        ++maxCode;
                        // Genera il nuovo codice
                        nextCode = code + DOT + Utils.parseString(maxCode);
                    } else {
                        nextCode = code + DOT + "01";
                    }
                } else {
                    nextCode = code + DOT + "01";
                }
                return nextCode;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Probabile problema nel recupero del codice del rischio.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (ArrayIndexOutOfBoundsException aiobe) {
                String msg = FOR_NAME + "Si e\' verificato un problema nello scorrimento di stringhe.\n" + aiobe.getMessage();
                LOG.severe(msg);
                throw new WebStorageException(msg, aiobe);
            } catch (NumberFormatException nfe) {
                String msg = FOR_NAME + "Problema nella conversione da String a intero.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + nfe.getMessage(), nfe);
            }  catch (SQLException sqle) {
                String msg = FOR_NAME + "Impossibile recuperare il max(code).\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    

    /**
     * <p>Restituisce il primo valore trovato data una query 
     * passata come parametro</p>
     *
     * @param query da eseguire
     * @return <code>String</code> - stringa restituita
     * @throws WebStorageException se si verifica un problema nella query o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public String get(String query)
               throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String value = null;
        try {
            con = prol_manager.getConnection();
            pst = con.prepareStatement(query);
            pst.clearParameters();
            rs = pst.executeQuery();
            if (rs.next()) {
                value = rs.getString(1);
            }
            return value;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Impossibile recuperare un valore.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Restituisce un CodeBean contenente la password criptata e il seme
     * per poter verificare le credenziali inserite dall'utente.</p>
     *
     * @param username   username della persona che ha richiesto il login
     * @return <code>CodeBean</code> - CodeBean contenente la password criptata e il seme
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public CodeBean getEncryptedPassword(String username)
                                  throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        CodeBean password = null;
        int nextInt = 0;
        try {
            con = prol_manager.getConnection();
            pst = con.prepareStatement(GET_ENCRYPTEDPASSWORD);
            pst.clearParameters();
            pst.setString(++nextInt, username);
            rs = pst.executeQuery();
            if (rs.next()) {
                password = new CodeBean();
                BeanUtil.populate(password, rs);
            }
            return password;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto PersonBean non valorizzato; problema nella query dell\'utente.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Restituisce un PersonBean rappresentante un utente loggato.</p>
     *
     * @param username  username della persona che ha eseguito il login
     * @param password  password della persona che ha eseguito il login
     * @return <code>PersonBean</code> - PersonBean rappresentante l'utente loggato
     * @throws it.rol.exception.WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'id della persona non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    @SuppressWarnings({ "null", "static-method" })
    public PersonBean getUser(String username,
                              String password)
                       throws WebStorageException, AttributoNonValorizzatoException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs, rs1 = null;
        PersonBean usr = null;
        int nextInt = 0;
        Vector<CodeBean> vRuoli = new Vector<>();
        try {
            con = prol_manager.getConnection();
            pst = con.prepareStatement(GET_USR);
            pst.clearParameters();
            pst.setString(++nextInt, username);
            pst.setString(++nextInt, password);
            pst.setString(++nextInt, password);
            rs = pst.executeQuery();
            if (rs.next()) {
                usr = new PersonBean();
                BeanUtil.populate(usr, rs);
                // Se ha trovato l'utente, ne cerca il ruolo
                pst = null;
                pst = con.prepareStatement(GET_RUOLOUTENTE);
                pst.clearParameters();
                pst.setString(1, username);
                rs1 = pst.executeQuery();
                while(rs1.next()) {
                    CodeBean ruolo = new CodeBean();
                    BeanUtil.populate(ruolo, rs1);
                    vRuoli.add(ruolo);
                }
                usr.setRuoli(vRuoli);
            }
            // Just tries to engage the Garbage Collector
            pst = null;
            // Get Out
            return usr;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto PersonBean non valorizzato; problema nella query dell\'utente.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Problema in una conversione di tipi nella query dell\'utente.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + cce.getMessage(), cce);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Verifica se per l'utente loggato esiste una tupla che indica
     * un precedente login.
     * <ul>
     * <li>Se non esiste una tupla per l'utente loggato, la inserisce.</li>
     * <li>Se esiste una tupla per l'utente loggato, la aggiorna.</li>
     * </ul>
     * In questo modo, il metodo gestisce nella tabella degli accessi
     * sempre l'ultimo accesso e non quelli precedenti.</p>
     *
     * @param username      login dell'utente (username usato per accedere)
     * @throws WebStorageException se si verifica un problema SQL o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null" })
    public void manageAccess(String username)
                      throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        CodeBean accessRow = null;
        int nextParam = NOTHING;
        try {
            // Ottiene la connessione
            con = prol_manager.getConnection();
            // Verifica se la login abbia già fatto un accesso
            pst = con.prepareStatement(GET_ACCESSLOG_BY_LOGIN);
            pst.clearParameters();
            pst.setString(++nextParam, username);
            rs = pst.executeQuery();
            if (rs.next()) {    // Esiste già un accesso: lo aggiorna
                accessRow = new CodeBean();
                BeanUtil.populate(accessRow, rs);
                pst = null;
                con.setAutoCommit(false);
                pst = con.prepareStatement(UPDATE_ACCESSLOG_BY_USER);
                pst.clearParameters();
                pst.setString(nextParam, username);
                // Campi automatici: ora ultimo accesso, data ultimo accesso
                pst.setDate(++nextParam, Utils.convert(Utils.convert(Utils.getCurrentDate()))); // non accetta un GregorianCalendar né una data java.util.Date, ma java.sql.Date
                pst.setTime(++nextParam, Utils.getCurrentTime());   // non accetta una Stringa, ma un oggetto java.sql.Time
                pst.setInt(++nextParam, accessRow.getId());
                pst.executeUpdate();
                con.commit();
            } else {            // Non esiste un accesso: ne crea uno nuovo
                // Chiude e annulla il PreparedStatement rimasto inutilizzato
                pst.close();
                pst = null;
                // BEGIN;
                con.setAutoCommit(false);
                pst = con.prepareStatement(INSERT_ACCESSLOG_BY_USER);
                pst.clearParameters();
                int nextVal = getMax("access_log") + 1;
                pst.setInt(nextParam, nextVal);
                pst.setString(++nextParam, username);
                pst.setDate(++nextParam, Utils.convert(Utils.convert(Utils.getCurrentDate())));
                pst.setTime(++nextParam, Utils.getCurrentTime());
                pst.executeUpdate();
                // END;
                con.commit();
            }
            String msg = "Si e\' loggato l\'utente: " + username +
                         " in data:" + Utils.format(Utils.getCurrentDate()) +
                         " alle ore:" + Utils.getCurrentTime() +
                         ".\n";
            LOG.info(msg);
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Probabile problema nel recupero dell'id dell\'ultimo accesso\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + anve.getMessage(), anve);
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Tupla non aggiornata correttamente; problema nella query che inserisce o in quella che aggiorna ultimo accesso al sistema.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } catch (NumberFormatException nfe) {
            String msg = FOR_NAME + "Tupla non aggiornata correttamente; problema nella query che inserisce o in quella che aggiorna ultimo accesso al sistema.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + nfe.getMessage(), nfe);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Tupla non aggiornata correttamente; problema nella query che inserisce o in quella che aggiorna ultimo accesso al sistema.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + npe.getMessage(), npe);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }

    /* ********************************************************** *
     *                     Metodi di SELEZIONE                    *
     * ********************************************************** */
    
    /**
     * <p>Seleziona i dati della rilevazione.</p>
     * <p>A seconda del valore dei parameri ricevuti, assume i seguenti comportamenti:
     * <dl><dt>se viene passato l'id rilevazione sul primo e secondo parametro</dt>
     * <dd>restituisce i dati della specifica rilevazione, avente tale valore
     * come identificativo,</dd>
     * <dt>se viene passato un valore qualunque sul primo parametro e -1 sul secondo parametro</dt>
     * <dd>restituisce i dati dell'ultima rilevazione che ha trovato,
     * in base all'ordine di data_rilevazione</dd></dl></p>
     * <p>Ritorna un CodeBean rappresentante la rilevazione desiderata, oppure quella che si
     * trova in testa alla lista di rilevazioni ordinate per data rilevazione discendente.</p>
     *
     * @param idSurvey  intero rappresentante l'identificativo dell rilevazione desiderato (oppure un valore qualunque se non si cerca una specifica rilevazione)
     * @param getAll    intero rappresentante il valore convenzionale -1 se si desidera ottenere l'ultima rilevazione (o un valore qualunque se si cerca una specifica rilevazione)
     * @return <code>CodeBean</code> - CodeBean rappresentante la rilevazione rispondente ai criteri di selezione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public CodeBean getSurvey(int idSurvey,
                              int getAll)
                       throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        CodeBean survey = null;
        try {
            con = prol_manager.getConnection();
            pst = con.prepareStatement(GET_SURVEY);
            pst.clearParameters();
            pst.setInt(1, idSurvey);
            pst.setInt(2, getAll);
            rs = pst.executeQuery();
            if (rs.next()) {
                survey = new CodeBean();
                BeanUtil.populate(survey, rs);
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Get out
            return survey;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto CodeBean non valorizzato; problema nella query dell\'ultima rilevazione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Problema in una conversione di tipi nella query dell\'ultima rilevazione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + cce.getMessage(), cce);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Seleziona i dati di una o pi&uacute; rilevazioni.</p>
     * <p>A seconda del valore dei parameri ricevuti, assume i seguenti comportamenti:
     * <dl><dt>se viene passato l'id rilevazione sul primo e secondo parametro</dt>
     * <dd>restituisce una struttura vettoriale contenente un oggetto valorizzato con
     * i dati della specifica rilevazione, avente tale valore come identificativo,</dd>
     * <dt>se viene passato un valore qualunque sul primo parametro e -1 sul secondo parametro</dt>
     * <dd>restituisce una struttura vettoriale contenente un oggetto valorizzato per
     * ogni rilevazione che ha trovato, in base all'ordine specificato nella query</dd></dl></p>
     * <p>Ritorna un ArrayList di CodeBean rappresentante la rilevazione desiderata,
     * oppure tutte le rilevazioni trovate, ordinate per ordine specificato nella query.</p>
     *
     * @param idSurvey  intero rappresentante l'identificativo dell rilevazione desiderato (oppure un valore qualunque se non si cerca una specifica rilevazione)
     * @param getAll    intero rappresentante il valore convenzionale -1 se si desidera ottenere l'ultima rilevazione (o un valore qualunque se si cerca una specifica rilevazione)
     * @return <code>ArrayList&lt;CodeBean&gt;</code> - ArrayList di CodeBean rappresentante la/le rilevazione/i rispondente/i ai criteri di selezione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public ArrayList<CodeBean> getSurveys(int idSurvey,
                                          int getAll)
                                   throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        CodeBean survey = null;
        ArrayList<CodeBean> surveys = new ArrayList<>();
        try {
            con = prol_manager.getConnection();
            pst = con.prepareStatement(GET_SURVEY);
            pst.clearParameters();
            pst.setInt(1, idSurvey);
            pst.setInt(2, getAll);
            rs = pst.executeQuery();
            while (rs.next()) {
                survey = new CodeBean();
                BeanUtil.populate(survey, rs);
                surveys.add(survey);
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Let's go away
            return surveys;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Struttura di CodeBean non valorizzata; problema nella query dell\'ultima rilevazione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Problema in una conversione di tipi nella query delle rilevazioni.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + cce.getMessage(), cce);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }
    

    /**
     * <p>Data una rilevazione, restituisce un ArrayList di macroprocessi
     * ad essa afferenti, contenenti ciascuno i processi figli al proprio interno
     * (albero, o struttura gerarchica).</p>
     * <p>Recupera solo i macroprocessi su cui un utente, il cui username viene
     * passato come argomento, ha i diritti di accesso (in base al ruolo <em>per se</em>).</p>
     *
     * @param user oggetto rappresentante la persona loggata
     * @param codeSurvey identificativo della rilevazione di cui si vogliono recuperare i macroprocessi
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - un Vector di ProcessBean, che rappresentano i processi su cui l'utente ha i diritti di lettura
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "null" })
    public ArrayList<ProcessBean> getMacroBySurvey(PersonBean user,
                                                   String codeSurvey)
                                            throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs, rs1 = null;
        ProcessBean macro = null;
        ProcessBean processo = null;
        CodeBean rilevazione = null;
        AbstractList<ProcessBean> macroprocessi = new ArrayList<>();
        AbstractList<ProcessBean> sottoprocessi = null;
        try {
            // TODO: Controllare se user è superuser
            con = prol_manager.getConnection();
            pst = con.prepareStatement(GET_MACRO_BY_SURVEY);
            pst.clearParameters();
            pst.setString(1, codeSurvey);
            rs = pst.executeQuery();
            while (rs.next()) {
                // Crea un macroprocesso vuoto
                macro = new ProcessBean();
                // Istanzia una struttura vettoriale per contenere i suoi sottoprocessi
                sottoprocessi = new Vector<>();
                // Valorizza il macroprocesso col contenuto della query
                BeanUtil.populate(macro, rs);
                // Recupera la rilevazione
                rilevazione = getSurvey(macro.getIdRilevazione(), macro.getIdRilevazione());
                // Imposta la rilevazione
                macro.setRilevazione(rilevazione);
                // Recupera i sottoprocessi
                pst = null;
                pst = con.prepareStatement(GET_PROCESSI_BY_MACRO);
                pst.clearParameters();
                pst.setInt(1, macro.getId());
                rs1 = pst.executeQuery();
                while (rs1.next()) {
                    processo = new ProcessBean();
                    BeanUtil.populate(processo, rs1);
                    sottoprocessi.add(processo);
                }
                // Imposta i sottoprocessi
                macro.setProcessi(sottoprocessi);
                // Aggiunge il macroprocesso valorizzato all'elenco
                macroprocessi.add(macro);
                rs1 = null;
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Let's go away (leva di ùlo)
            return (ArrayList<ProcessBean>) macroprocessi;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Oggetto ProcessBean.some non valorizzato; problema nel metodo di estrazione dei processi/macroprocessi.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + anve.getMessage(), anve);
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto ProcessBean non valorizzato; problema nella query dei processi/macroprocessi..\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /* (non-Javadoc)
     * @see it.rol.Query#getQueryMacroByStruct(int, byte, java.lang.String)
     */
    @SuppressWarnings("javadoc")
    @Override
    public String getQueryMacroByStruct(int idS, byte level, String codeSur) {
        String tableFrom = "struttura_liv" + String.valueOf(level);
        String tableWhere = "id_struttura_liv" + String.valueOf(level);
        final String GET_MACROPROCESSI_BY_STRUCT =
                "SELECT DISTINCT" +
                        "       M.id                AS \"id\"" +
                        "   ,   M.codice            AS \"codice\"" +
                        "   ,   M.nome              AS \"nome\"" +
                        "   ,   M.descrizione       AS \"descrizione\"" +
                        "   ,   M.ordinale          AS \"ordinale\"" +
                        "   ,   M.id_rilevazione    AS \"idRilevazione\"" +
                        "   ,   ROUND(100 * SUM(AM.quotaparte) OVER (PARTITION BY M.id) / (SUM(AM.quotaparte) OVER ())::numeric, 2) AS \"quotaParte\"" +
                        "   ,   ROUND((SUM(AM.quotaparte * (A.perc_parttime / 100.0)) OVER (PARTITION BY M.id) / 100.0), 2)         AS \"fte\"" +
                        "   FROM macroprocesso M" +
                        "       INNER JOIN rilevazione R ON M.id_rilevazione = R.id" +
                        "       INNER JOIN allocazione_macroprocesso AM ON AM.id_macroprocesso = M.id" +
                        "       INNER JOIN afferenza A ON R.id = A.id_rilevazione AND A.id_persona = AM.id_persona" +
                        "       INNER JOIN " + tableFrom + " ST ON ST.id_rilevazione = R.id" +
                        "   WHERE R.codice ILIKE '" + codeSur + "'" +
                        "       AND ST.id = " + idS +
                        "       AND AM.id_persona IN" +
                        "           (SELECT P.id FROM persona P" +
                        "               INNER JOIN afferenza AF ON AF.id_persona = P.id" +
                        "               WHERE " + tableWhere + " = " + idS + ")" +
                        "   ORDER BY M.codice";
        return GET_MACROPROCESSI_BY_STRUCT;
    }


    /**
     * <p>Dato un identificativo di una struttura, passato come parametro,
     * restituisce un ArrayList di macroprocessi ad essa afferenti,
     * contenenti ciascuno i processi figli al proprio interno; l'estrazione ovviamente
     * tiene conto anche della rilevazione.</p>
     * <p>Recupera solo i macroprocessi su cui un utente, il cui username viene
     * passato come argomento, ha i diritti di accesso (in base al ruolo <em>per se</em>).</p>
     *
     * @param user   oggetto rappresentante la persona loggata
     * @param idS    identificativo della struttura di cui si vogliono recuperare i macroprocessi
     * @param level  livello della struttura di cui si vogliono recuperare i macroprocessi
     * @param survey CodeBean incapsulante la rilevazione di cui si vogliono recuperare i macroprocessi
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - un Vector di ProcessBean, che rappresentano i processi su cui l'utente ha i diritti di lettura
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "null" })
    public ArrayList<ProcessBean> getMacroByStruct(PersonBean user,
                                                   int idS,
                                                   byte level,
                                                   CodeBean survey)
                                            throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs, rs1 = null;
        int nextParam = NOTHING;
        ProcessBean macro = null;
        ProcessBean processo = null;
        String codeSurvey = null;
        AbstractList<ProcessBean> macroprocessi = new ArrayList<>();
        AbstractList<ProcessBean> sottoprocessi = null;
        DepartmentBean struttura = null;
        int[] sl = null;
        // TODO: Controllare se user è superuser
        // Recupera gli estremi della struttura (è uguale per tutti i macroprocessi trovati)
        try {
            codeSurvey = survey.getNome();
            struttura = getStructure(user, idS, level, codeSurvey);
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Impossibile recuperare il codice rilevazione; problema nel metodo di estrazione macroprocessi in base alla struttura.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + anve.getMessage(), anve);
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Problema nel metodo di estrazione dei macroprocessi in base alla struttura.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + wse.getMessage(), wse);
        }
        // Estrae i macroprocessi in base alla struttura
        try {
            con = prol_manager.getConnection();
            // Determina la query da fare in base al livello
            switch(level) {
                case 1:
                    pst = con.prepareStatement(GET_MACROPROCESSI_BY_STRUCT_L1);
                    pst.clearParameters();
                    pst.setString(++nextParam, codeSurvey);
                    pst.setInt(++nextParam, idS);
                    pst.setInt(++nextParam, idS);
                    pst.setInt(++nextParam, idS);
                    pst.setInt(++nextParam, idS);
                    pst.setInt(++nextParam, idS);
                    sl = new int[]{idS, DEFAULT_ID, DEFAULT_ID, DEFAULT_ID};
                    break;
                case 2:
                    pst = con.prepareStatement(GET_MACROPROCESSI_BY_STRUCT_L2);
                    pst.clearParameters();
                    pst.setString(++nextParam, codeSurvey);
                    pst.setInt(++nextParam, idS);
                    pst.setInt(++nextParam, idS);
                    pst.setInt(++nextParam, idS);
                    pst.setInt(++nextParam, idS);
                    sl = new int[]{DEFAULT_ID, idS, DEFAULT_ID, DEFAULT_ID};
                    break;
                case 3:
                    pst = con.prepareStatement(GET_MACROPROCESSI_BY_STRUCT_L3);
                    pst.clearParameters();
                    pst.setString(++nextParam, codeSurvey);
                    pst.setInt(++nextParam, idS);
                    pst.setInt(++nextParam, idS);
                    pst.setInt(++nextParam, idS);
                    sl = new int[]{DEFAULT_ID, DEFAULT_ID, idS, DEFAULT_ID};
                    break;
                case 4:
                    pst = con.prepareStatement(GET_MACROPROCESSI_BY_STRUCT_L4);
                    pst.clearParameters();
                    pst.setString(++nextParam, codeSurvey);
                    pst.setInt(++nextParam, idS);
                    pst.setInt(++nextParam, idS);
                    sl = new int[]{DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, idS};
                    break;
            }
            rs = pst.executeQuery();
            while (rs.next()) {
                // Crea un macroprocesso vuoto
                macro = new ProcessBean();
                // Istanzia una struttura vettoriale per contenere i suoi sottoprocessi
                sottoprocessi = new Vector<>();
                // Valorizza il macroprocesso col contenuto della query
                BeanUtil.populate(macro, rs);
                // Imposta la rilevazione nel macroprocesso
                macro.setRilevazione(survey);
                // Recupera le persone allocate sul macroprocesso
                //macro.setPersone(getPeopleByStructureAndMacro(user, macro.getId(), survey.getId(), sl));
                // Recupera i sottoprocessi
                pst = null;
                pst = con.prepareStatement(GET_PROCESSI_BY_MACRO);
                pst.clearParameters();
                pst.setInt(1, macro.getId());
                rs1 = pst.executeQuery();
                while (rs1.next()) {
                    processo = new ProcessBean();
                    BeanUtil.populate(processo, rs1);
                    //processo.setPersone(getPeopleByStructureAndProcess(user, processo.getId(), survey.getId(), sl));
                    sottoprocessi.add(processo);
                }
                // Imposta i sottoprocessi
                macro.setProcessi(sottoprocessi);
                // Imposta la struttura
                macro.setDipart(struttura);
                // Aggiunge il macroprocesso valorizzato all'elenco
                macroprocessi.add(macro);
                rs1 = null;
            }
            // Just tries to engage the Garbage Collector
            pst = null;
            // Get out
            return (ArrayList<ProcessBean>) macroprocessi;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Oggetto ProcessBean.some non valorizzato; problema nel metodo di estrazione dei processi/macroprocessi.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + anve.getMessage(), anve);
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto ProcessBean non valorizzato; problema nella query dei processi/macroprocessi..\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Data una rilevazione e l'identificativo di una persona, passati come argomenti,
     * restituisce un ArrayList di macroprocessi ad essa afferenti, 
     * contenenti ciascuno i processi figli al proprio interno.</p>
     * <p>Recupera solo i macroprocessi su cui un utente, il cui username viene
     * passato come argomento, ha i diritti di accesso (in base al ruolo <em>per se</em>).</p>
     *
     * @param user oggetto rappresentante la persona loggata
     * @param person persona di cui si vogliono recuperare i macroprocessi di allocazione
     * @param survey oggetto incapsulante la rilevazione rispetto a cui si vogliono recuperare i macroprocessi
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - un Vector di ProcessBean, che rappresentano i processi su cui l'utente ha i diritti di lettura
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "null" })
    public ArrayList<ProcessBean> getMacroByPerson(PersonBean user,
                                                   PersonBean person,
                                                   CodeBean survey)
                                            throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs, rs1 = null;
        int nextParam = NOTHING;
        ProcessBean macro = null;
        ProcessBean processo = null;
        CodeBean rilevazione = null;
        AbstractList<ProcessBean> macroprocessi = new ArrayList<>();
        AbstractList<ProcessBean> sottoprocessi = null;
        try {
            // TODO: Controllare se user è superuser
            con = prol_manager.getConnection();
            pst = con.prepareStatement(GET_MACRO_BY_PERSON);
            pst.clearParameters();
            pst.setString(++nextParam, survey.getNome());
            pst.setInt(++nextParam, person.getId());
            rs = pst.executeQuery();
            while (rs.next()) {
                // Riporta a zero il contatore
                nextParam = NOTHING;
                // Crea un macroprocesso vuoto
                macro = new ProcessBean();
                // Istanzia una struttura vettoriale per contenere i suoi sottoprocessi
                sottoprocessi = new Vector<>();
                // Valorizza il macroprocesso col contenuto della query
                BeanUtil.populate(macro, rs);
                // Recupera la rilevazione
                rilevazione = getSurvey(macro.getIdRilevazione(), macro.getIdRilevazione());
                // Imposta la rilevazione
                macro.setRilevazione(rilevazione);
                // Recupera i sottoprocessi
                pst = null;
                pst = con.prepareStatement(GET_PROCESSI_BY_MACRO_AND_PERSON);
                pst.clearParameters();
                pst.setString(++nextParam, survey.getNome());
                pst.setInt(++nextParam, macro.getId());
                pst.setInt(++nextParam, person.getId());
                rs1 = pst.executeQuery();
                while (rs1.next()) {
                    processo = new ProcessBean();
                    BeanUtil.populate(processo, rs1);
                    sottoprocessi.add(processo);
                }
                // Imposta i sottoprocessi
                macro.setProcessi(sottoprocessi);
                // Aggiunge il macroprocesso valorizzato all'elenco
                macroprocessi.add(macro);
                rs1 = null;
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Get out
            return (ArrayList<ProcessBean>) macroprocessi;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Oggetto ProcessBean.some non valorizzato; problema in un metodo di estrazione dei processi/macroprocessi.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + anve.getMessage(), anve);
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto ProcessBean non valorizzato; problema nella query dei processi/macroprocessi..\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Data una rilevazione e l'identificativo di un macroprocesso, restituisce
     * un ArrayList di persone ad essi afferenti.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idType    identifica se si devono recuperare macroprocessi o processi
     * @param id        identificativo del macroprocesso o processo di cui si vogliono recuperare le persone
     * @param idSurvey  identificativo della rilevazione di cui si vogliono recuperare le allocazioni
     * @return <code>ArrayList&lt;PersonBean&gt;</code> - un Vector di PersonBean, che rappresentano le persone allocate sul macroprocesso nel contesto della rilevazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({"null", "static-method"})
    public ArrayList<PersonBean> getPeopleByMacroOrProcess(PersonBean user,
                                                           String idType,
                                                           int id,
                                                           int idSurvey)
                                                    throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int nextParam = NOTHING;
        PersonBean person = null;
        AbstractList<PersonBean> people = new ArrayList<>();
        try {
            // TODO: Controllare se user è superuser
            con = prol_manager.getConnection();
            if (idType.equals(PART_MACROPROCESS))
                pst = con.prepareStatement(GET_PEOPLE_BY_MACRO);
            else
                pst = con.prepareStatement(GET_PEOPLE_BY_PROCESS);
            pst.clearParameters();
            pst.setInt(++nextParam, id);
            pst.setInt(++nextParam, idSurvey);
            rs = pst.executeQuery();
            while (rs.next()) {
                // Crea una persona vuota
                person = new PersonBean();
                // La valorizza col contenuto della query
                BeanUtil.populate(person, rs);
                // Aggiunge all'elenco
                people.add(person);
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Let's go away
            return (ArrayList<PersonBean>) people;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto PersonBean non valorizzato; problema nella query delle persone su macroprocesso.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }

    
    /**
     * <p>Data una rilevazione e l'identificativo di un macroprocesso, restituisce
     * un ArrayList di persone ad essi afferenti.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idMacro   identificativo del macroprocesso di cui si vogliono recuperare le persone
     * @param idSurvey  identificativo della rilevazione di cui si vogliono recuperare le allocazioni
     * @return <code>ArrayList&lt;PersonBean&gt;</code> - un Vector di PersonBean, che rappresentano le persone allocate sul macroprocesso nel contesto della rilevazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public ArrayList<PersonBean> getPeopleByMacro(PersonBean user, int idMacro, int idSurvey) throws WebStorageException {
        return getPeopleByMacroOrProcess(user, PART_MACROPROCESS, idMacro, idSurvey);
    }


    /**
     * <p>Data una rilevazione e l'identificativo di un processo, restituisce
     * un ArrayList di persone ad essi afferenti.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idProcess identificativo del processo di cui si vogliono recuperare le persone
     * @param idSurvey  identificativo della rilevazione di cui si vogliono recuperare le allocazioni
     * @return <code>ArrayList&lt;PersonBean&gt;</code> - un Vector di PersonBean, che rappresentano le persone allocate sul macroprocesso nel contesto della rilevazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public ArrayList<PersonBean> getPeopleByProcess(PersonBean user,
                                                    int idProcess,
                                                    int idSurvey)
                                             throws WebStorageException {
        return getPeopleByMacroOrProcess(user, PART_PROCESS, idProcess, idSurvey);
    }


    /* (non-Javadoc)
     * @see it.rol.Query#getQueryPeopleByStructureAndMacro(int, int, int[])
     */
    @SuppressWarnings("javadoc")
    @Override
    public String getQueryPeopleByStructureAndMacro(int idM,
                                                    int idR,
                                                    int[] idl) {
        String idL4 = (idl[3] == DEFAULT_ID ? "IS NULL" : " = " + String.valueOf(idl[3]));
        String idL3 = (idl[2] == DEFAULT_ID ? "IS NULL" : " = " + String.valueOf(idl[2]));
        String idL2 = (idl[1] == DEFAULT_ID ? "IS NULL" : " = " + String.valueOf(idl[1]));
        String idL1 = (idl[0] == DEFAULT_ID ? "IS NULL" : " = " + String.valueOf(idl[0]));
        return  "SELECT " +
                "       P.id            AS \"id\"" +
                "   ,   P.nome          AS \"nome\"" +
                "   ,   P.cognome       AS \"cognome\"" +
                "   ,   P.sesso         AS \"sesso\"" +
                "   ,   AM.quotaparte   AS \"note\"" +
                "   FROM persona P" +
                "       INNER JOIN allocazione_macroprocesso AM ON AM.id_persona = P.id" +
                "       INNER JOIN afferenza AF ON AF.id_persona = P.id" +
                "   WHERE AM.id_macroprocesso = " + idM +
                "       AND AF.id_rilevazione = " + idR +
                "       AND AF.id_struttura_liv4 " + idL4 +
                "       AND AF.id_struttura_liv3 " + idL3 +
                "       AND AF.id_struttura_liv2 " + idL2 +
                "       AND AF.id_struttura_liv1 " + idL1 +
                "   ORDER BY P.cognome";
    }


    /* (non-Javadoc)
     * @see it.rol.Query#getQueryPeopleByStructureAndProcess(int, int, int[])
     */
    @SuppressWarnings("javadoc")
    @Override
    public String getQueryPeopleByStructureAndProcess(int idP,
                                                      int idR,
                                                      int[] idl) {
        String idL4 = (idl[3] == DEFAULT_ID ? "IS NULL" : " = " + String.valueOf(idl[3]));
        String idL3 = (idl[2] == DEFAULT_ID ? "IS NULL" : " = " + String.valueOf(idl[2]));
        String idL2 = (idl[1] == DEFAULT_ID ? "IS NULL" : " = " + String.valueOf(idl[1]));
        String idL1 = (idl[0] == DEFAULT_ID ? "IS NULL" : " = " + String.valueOf(idl[0]));
        return  "SELECT " +
                "       P.id            AS \"id\"" +
                "   ,   P.nome          AS \"nome\"" +
                "   ,   P.cognome       AS \"cognome\"" +
                "   ,   P.sesso         AS \"sesso\"" +
                "   ,   AM.quotaparte   AS \"note\"" +
                "   FROM persona P" +
                "       INNER JOIN allocazione_processo AM ON AM.id_persona = P.id" +
                "       INNER JOIN afferenza AF ON AF.id_persona = P.id" +
                "   WHERE AM.id_processo = " + idP +
                "       AND AF.id_rilevazione = " + idR +
                "       AND AF.id_struttura_liv4 " + idL4 +
                "       AND AF.id_struttura_liv3 " + idL3 +
                "       AND AF.id_struttura_liv2 " + idL2 +
                "       AND AF.id_struttura_liv1 " + idL1 +
                "   ORDER BY P.cognome";
    }


    /**
     * <p>Data una rilevazione e l'identificativo di un macroprocesso, restituisce
     * un ArrayList di persone ad essi afferenti.</p>
     *
     * @param user     oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idType   tipo (macroprocesso o processo) dell'ID passato
     * @param id       identificativo del macroprocesso di cui si vogliono recuperare le persone
     * @param idSurvey identificativo della rilevazione di cui si vogliono recuperare le allocazioni
     * @param idl      identificativi delle strutture di livello 4, 3, 2 e 1 (-1 se null)
     * @return <code>ArrayList&lt;PersonBean&gt;</code> - un Vector di PersonBean, che rappresentano le persone allocate sul macroprocesso nel contesto della rilevazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "null" })
    public ArrayList<PersonBean> getPeopleByStructureAndMacroOrProcess(PersonBean user,
                                                                       String idType,
                                                                       int id,
                                                                       int idSurvey,
                                                                       int[] idl)
                                                                throws WebStorageException {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        PersonBean person = null;
        AbstractList<PersonBean> people = new ArrayList<>();
        try {
            // TODO: Controllare se user è superuser
            // Chiama il metodo che costruisce la query
            String query = null;
            if (idType.equals(PART_MACROPROCESS))
                query = getQueryPeopleByStructureAndMacro(id, idSurvey, idl);
            if (idType.equals(PART_PROCESS))
                query = getQueryPeopleByStructureAndProcess(id, idSurvey, idl);
            con = prol_manager.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                // Crea una persona vuota
                person = new PersonBean();
                // La valorizza col contenuto della query
                BeanUtil.populate(person, rs);
                // Aggiunge all'elenco
                people.add(person);
            }
            // Tries (just tries) to engage the Garbage Collector
            st = null;
            // Let's go away
            return (ArrayList<PersonBean>) people;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto PersonBean non valorizzato; problema nella query delle persone su struttura.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }

    
    /**
     * <p>Data una rilevazione e l'identificativo di un macroprocesso, restituisce
     * un ArrayList di persone ad essi afferenti.</p>
     *
     * @param user     oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idMacro  identificativo del macroprocesso di cui si vogliono recuperare le persone
     * @param idSurvey identificativo della rilevazione di cui si vogliono recuperare le allocazioni
     * @param idl      identificativi delle strutture di livello 4, 3, 2 e 1 (-1 se null)
     * @return <code>ArrayList&lt;PersonBean&gt;</code> - un Vector di PersonBean, che rappresentano le persone allocate sul macroprocesso nel contesto della rilevazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public ArrayList<PersonBean> getPeopleByStructureAndMacro(PersonBean user,
                                                              int idMacro,
                                                              int idSurvey,
                                                              int[] idl)
                                                       throws WebStorageException {
        return getPeopleByStructureAndMacroOrProcess(user, PART_MACROPROCESS, idMacro, idSurvey, idl);
    }


    /**
     * <p>Data una rilevazione e l'identificativo di un processo, restituisce
     * un ArrayList di persone ad essi afferenti.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idProcess identificativo del macroprocesso di cui si vogliono recuperare le persone
     * @param idSurvey  identificativo della rilevazione di cui si vogliono recuperare le allocazioni
     * @param idl       identificativi delle strutture di livello 4, 3, 2 e 1 (-1 se null)
     * @return <code>ArrayList&lt;PersonBean&gt;</code> - un Vector di PersonBean, che rappresentano le persone allocate sul macroprocesso nel contesto della rilevazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public ArrayList<PersonBean> getPeopleByStructureAndProcess(PersonBean user,
                                                                int idProcess,
                                                                int idSurvey,
                                                                int[] idl)
                                                         throws WebStorageException {
        return getPeopleByStructureAndMacroOrProcess(user, PART_PROCESS, idProcess, idSurvey, idl);
    }


    /* (non-Javadoc)
     * @see it.rol.Query#getQueryStructures(int, int, int, int, int)
     */
    @SuppressWarnings("javadoc")
    @Override
    public String getQueryStructures(int idR, int idl4, int idl3, int idl2, int idl1) {
        String tableFrom = null;
        String tableWhere = null;
        byte level = (byte) DEFAULT_ID;
        // Se passa -1 su questo parametro vuol recuperare tutte le strutture di livello 1
        if (idl1 == DEFAULT_ID) {
            level = (byte) 1;
            tableFrom = "struttura_liv1";
            tableWhere = DEFAULT_ID + " = " + idl1 ;
        // Altrimenti, se passa un id, vuol recuperare tutte le strutture di livello 2 che afferiscono alla struttura di livello 1 di tale id
        } else if (idl1 > NOTHING) {
            level = (byte) 2;
            tableFrom = "struttura_liv2";
            tableWhere = "id_struttura_liv1 = " + idl1;
        }
        // Liv 2
        if (idl2 == DEFAULT_ID) {       // Tutte le Liv 2
            level = (byte) 2;
            tableFrom = "struttura_liv2";
            tableWhere = DEFAULT_ID + " = " + idl2 ;
        } else if (idl2 > NOTHING) {    // Solo le Liv 3 afferenti a una specifica Liv 2
            level = (byte) 3;
            tableFrom = "struttura_liv3";
            tableWhere = "id_struttura_liv2 = " + idl2;
        }
        // Liv 3
        if (idl3 == DEFAULT_ID) {       // Tutte le Liv 3
            level = (byte) 3;
            tableFrom = "struttura_liv3";
            tableWhere = DEFAULT_ID + " = " + idl3 ;
        } else if (idl3 > NOTHING) {    // Solo le Liv 4 afferenti a una specifica Liv 3
            level = (byte) 4;
            tableFrom = "struttura_liv4";
            tableWhere = "id_struttura_liv3 = " + idl3;
        }
        // Liv 4
        if (idl4 == DEFAULT_ID) {       // Tutte le Liv 4
            level = (byte) 4;
            tableFrom = "struttura_liv4";
            tableWhere = DEFAULT_ID + " = " + idl4 ;
        }
        final String GET_STRUCTURE_BY_STRUCTURE =
                "SELECT " +
                "       D.id                 AS \"id\"" +
                "   ,   D.nome               AS \"nome\"" +
                "   ,   D.codice             AS \"informativa\"" +
                "   ,   D.ordinale           AS \"ordinale\"" +
                "   ,   D.prefisso           AS \"prefisso\"" +
                "   ,   D.acronimo           AS \"acronimo\"" +
                "   ,   D.indirizzo_sede     AS \"indirizzo\"" +
                "   ," + level + "::SMALLINT AS \"livello\"" +
                "   FROM " + tableFrom + " D" +
                "   WHERE D.id_rilevazione = " + idR +
                "       AND " + tableWhere +
                "       AND id_stato = 1" + // Prende solo le strutture attive
                "   ORDER BY D.ordinale, D.nome";
        return GET_STRUCTURE_BY_STRUCTURE;
    }


    /**
     * <p>Data una rilevazione, restituisce la lista gerarchica delle strutture
     * rilevate.</p>
     * <p>Calcola il numero totale di nodi subordinati in base al seguente
     * algoritmo:<dl>
     * <dt>strutture di IV livello</dt>
     * <dd>0</dd>
     * <dt>strutture di III livello</dt>
     * <dd>num.nodi subordinati = num.figli diretti</dd>
     * <dt>strutture di II livello</dt>
     * <dd>num.nodi subordinati = num.figli diretti + num.figli di figli </dd>
     * <dt>strutture di I livello</dt>
     * <dd>num.nodi subordinati = num.figli diretti + num.figli II livello + num.figli III livello</dd>
     * </dl></p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param survey    oggetto contenente i dati della rilevazione rispetto a cui si vogliono recuperare le strutture
     * @return <code>ArrayList&lt;DepartmentBean&gt;</code> - ArrayList di strutture coinvolte nel contesto della rilevazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "null" })
    public ArrayList<DepartmentBean> getStructures(PersonBean user,
                                                   CodeBean survey)
                                            throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs, rs1, rs2, rs3 = null;
        DepartmentBean s1, s2, s3, s4 = null;
        AbstractList<DepartmentBean> structs = new ArrayList<>();
        Vector<DepartmentBean> vS2 = null;
        Vector<DepartmentBean> vS3 = null;
        Vector<DepartmentBean> vS4 = null;
        int totalChildren = NOTHING;
        int idThru = NOTHING;   // id globale
        try {
            // TODO: Controllare se user è superuser
            // Chiama il metodo che costruisce la query
            StringBuffer query = new StringBuffer(getQueryStructures(survey.getId(), NOTHING, NOTHING, NOTHING, DEFAULT_ID));
            con = prol_manager.getConnection();
            pst = con.prepareStatement(String.valueOf(query));
            pst.clearParameters();
            rs = pst.executeQuery();
            while (rs.next()) {
                // Incrementa l'id globale
                ++idThru;
                // Crea una struttura di I livello vuota
                s1 = new DepartmentBean();
                // Crea un contenitore di informazioni aggiuntive
                ItemBean infoS1 = new ItemBean();
                // Valorizza la struttura di I livello col contenuto della query
                BeanUtil.populate(s1, rs);
                // Vi memorizza l'id globale
                infoS1.setCodice(String.valueOf(idThru + DOT + s1.getId() + DASH + s1.getLivello()));
                // Ne imposta il livello
                s1.setLivello((byte) 1);
                // Prepara il recupero dei figli di livello 2
                pst = null;
                // Crea una struttura per i figli
                vS2 = new Vector<>();
                // Prepara la query
                query = new StringBuffer(getQueryStructures(survey.getId(), NOTHING, NOTHING, NOTHING, s1.getId()));
                // Recupera le strutture figlie
                pst = con.prepareStatement(String.valueOf(query));
                pst.clearParameters();
                rs1 = pst.executeQuery();
                while (rs1.next()) {
                    // Incrementa l'id globale
                    ++idThru;
                    // Crea una struttura di II livello vuota
                    s2 = new DepartmentBean();
                    // Crea un contenitore di informazioni aggiuntive
                    ItemBean infoS2 = new ItemBean();
                    // Valorizza la struttura di II livello tramite la query
                    BeanUtil.populate(s2, rs1);
                    // Vi memorizza l'id globale
                    infoS2.setCodice(String.valueOf(idThru + DOT + s2.getId() + DASH + s2.getLivello()));
                    // Ne imposta il livello
                    s2.setLivello((byte) 2);
                    // Ne imposta il padre
                    s2.setPadre(s1);
                    // Ne recupera i figli di livello 3 (nipoti)
                    pst = null;
                    vS3 = new Vector<>();
                    query = new StringBuffer(getQueryStructures(survey.getId(), NOTHING, NOTHING, s2.getId(), NOTHING));
                    pst = con.prepareStatement(String.valueOf(query));
                    pst.clearParameters();
                    rs2 = pst.executeQuery();
                    while (rs2.next()) {
                        // Incrementa l'id globale
                        ++idThru;
                        // Crea una struttura di III livello vuota
                        s3 = new DepartmentBean();
                        // Crea un contenitore di informazioni aggiuntive
                        ItemBean infoS3 = new ItemBean();
                        // Valorizza la struttura di III livello tramite la query
                        BeanUtil.populate(s3, rs2);
                        // Vi memorizza l'id globale
                        infoS3.setCodice(String.valueOf(idThru + DOT + s3.getId() + DASH + s3.getLivello()));
                        // Ne imposta il livello
                        s3.setLivello((byte) 3);
                        // Ne imposta il padre
                        s3.setPadre(s2);
                        // Ne recupera i figli di livello 4 (pronipoti)
                        pst = null;
                        vS4 = new Vector<>();
                        query = new StringBuffer(getQueryStructures(survey.getId(), NOTHING, s3.getId(), NOTHING, NOTHING));
                        pst = con.prepareStatement(String.valueOf(query));
                        pst.clearParameters();
                        rs3 = pst.executeQuery();
                        while (rs3.next()) {
                            // Incrementa l'id globale
                            ++idThru;
                            // Crea una struttura di IV livello vuota
                            s4 = new DepartmentBean();
                            // Crea un contenitore di informazioni aggiuntive
                            ItemBean infoS4 = new ItemBean();
                            // Valorizza la struttura di IV livello tramite query
                            BeanUtil.populate(s4, rs3);
                            // Ne imposta il livello
                            s4.setLivello((byte) 4);
                            // Ne imposta il padre
                            s4.setPadre(s3);
                            // Memorizza l'id globale nel contenitore informativo
                            infoS4.setCodice(String.valueOf(idThru + DOT + s4.getId() + DASH + s4.getLivello()));
                            // Valorizza il numero di subordinati
                            infoS4.setExtraInfo(String.valueOf(NOTHING));
                            // Salva le informazioni aggiuntive
                            s4.setExtraInfo(infoS4);
                            // La aggiunge alla lista dei IV livello
                            vS4.add(s4);
                        }
                        // Aggiunge i IV livelli alla struttura di III livello
                        s3.setFiglie(vS4);
                        // Inizializza il numero totale di subordinati
                        totalChildren = vS4.size();
                        // Valorizza il numero di subordinati
                        infoS3.setExtraInfo(String.valueOf(totalChildren));
                        // Salva le informazioni aggiuntive
                        s3.setExtraInfo(infoS3);
                        // Aggiunge il III livello alla lista dei III livello
                        vS3.add(s3);
                    }
                    // Aggiunge i III livelli alla struttura di II livello
                    s2.setFiglie(vS3);
                    // Incrementa il numero totale di subordinati
                    totalChildren = totalChildren + vS3.size();
                    // Valorizza il numero totale di subordinati
                    infoS2.setExtraInfo(String.valueOf(totalChildren));
                    // Salva le informazioni aggiuntive
                    s2.setExtraInfo(infoS2);
                    // Aggiunge il II livello alla lista dei II livello
                    vS2.add(s2);
                }
                // Aggiunge i II livelli alla struttura di I livello
                s1.setFiglie(vS2);
                // Incrementa il numero totale di subordinati
                totalChildren = totalChildren + vS2.size();
                // Memorizza il numero totale di subordinati
                infoS1.setExtraInfo(String.valueOf(totalChildren));
                // Salva le informazioni aggiuntive
                s1.setExtraInfo(infoS1);
                // Aggiunge il I livello all'elenco dei I livello
                structs.add(s1);
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Get out
            return (ArrayList<DepartmentBean>) structs;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Si e\' verificato un problema nell\'accesso ad un attributo obbligatorio di un bean.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + anve.getMessage(), anve);
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto DepartmentBean non valorizzato; problema nella query delle strutture in rilevazione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Data una rilevazione e l'identificativo di un macroprocesso o di un processo,
     * restituisce un ArrayList di strutture ad essi collegate,
     * oppure l'elenco di tutte le strutture collegate a tutti i macroprocessi
     * o a tutti i processi (in funzione del flag type) se il valore del flag getAll
     * vale -1.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param type    identifica il tipo (macroprocesso o processo) di cui si vogliono recuperare le strutture
     * @param id        identificativo del macroprocesso di cui si vogliono recuperare le strutture
     * @param getAll    intero rappresentante il valore convenzionale -1 se si desidera ottenere l'ultima rilevazione (o un valore qualunque se si cerca una specifica rilevazione)
     * @param idSurvey  identificativo della rilevazione nel contesto della quale si vogliono recuperare le strutture
     * @return <code>ArrayList&lt;DepartmentBean&gt;</code> - un Vector di DepartmentBean, che rappresentano le strutture allocate sul macroprocesso nel contesto della rilevazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public ArrayList<ItemBean> getStructsByMacroOrProcess(PersonBean user,
                                                          String type,
                                                          int id,
                                                          int getAll,
                                                          int idSurvey)
                                                   throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int nextParam = NOTHING;
        ItemBean item = null;
        AbstractList<ItemBean> items = new ArrayList<>();
        try {
            // TODO: Controllare se user è superuser
            con = prol_manager.getConnection();
            if (type.equals(PART_MACROPROCESS))
                pst = con.prepareStatement(GET_STRUCTS_BY_MACRO);
            else if (type.equals(PART_PROCESS))
                pst = con.prepareStatement(GET_STRUCTS_BY_PROCESS);
            pst.clearParameters();
            pst.setInt(++nextParam, idSurvey);
            pst.setInt(++nextParam, idSurvey);
            pst.setInt(++nextParam, idSurvey);
            pst.setInt(++nextParam, idSurvey);
            pst.setInt(++nextParam, idSurvey);
            pst.setInt(++nextParam, id);
            pst.setInt(++nextParam, getAll);
            rs = pst.executeQuery();
            while (rs.next()) {
                // Crea una tupla vuota
                item = new ItemBean();
                // La valorizza col contenuto della query
                BeanUtil.populate(item, rs);
                // Aggiunge all'elenco
                items.add(item);
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Let's go away
            return (ArrayList<ItemBean>) items;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query delle strutture su macroprocesso.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Data una rilevazione e l'identificativo di un macroprocesso, restituisce
     * un ArrayList di strutture ad essi collegate.</p>
     *
     * @param user     oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idMacro  identificativo del macroprocesso di cui si vogliono recuperare le strutture
     * @param idSurvey identificativo della rilevazione nel contesto della quale si vogliono recuperare le strutture
     * @return <code>ArrayList&lt;DepartmentBean&gt;</code> - un Vector di DepartmentBean, che rappresentano le strutture allocate sul macroprocesso nel contesto della rilevazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public ArrayList<ItemBean> getStructsByMacro(PersonBean user,
                                                 int idMacro,
                                                 int idSurvey)
                                          throws WebStorageException {
        return getStructsByMacroOrProcess(user, PART_MACROPROCESS, idMacro, idMacro, idSurvey);
    }


    /**
     * <p>Data una rilevazione e l'identificativo di un processo, restituisce
     * un ArrayList di strutture ad essi collegate.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idProcess identificativo del macroprocesso di cui si vogliono recuperare le strutture
     * @param idSurvey  identificativo della rilevazione nel contesto della quale si vogliono recuperare le strutture
     * @return <code>ArrayList&lt;DepartmentBean&gt;</code> - un Vector di DepartmentBean, che rappresentano le strutture allocate sul macroprocesso nel contesto della rilevazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public ArrayList<ItemBean> getStructsByProcess(PersonBean user,
                                                   int idProcess,
                                                   int idSurvey)
                                            throws WebStorageException {
        return getStructsByMacroOrProcess(user, PART_PROCESS, idProcess, idProcess, idSurvey);
    }


    /* (non-Javadoc)
     * @see it.rol.Query#getQueryStructure(int, byte)
     */
    @SuppressWarnings("javadoc")
    @Override
    public String getQueryStructure(int id,
                                    byte level) {
        String table = null;
        switch(level) {
            case 1:
                table = "struttura_liv1";
                break;
            case 2:
                table = "struttura_liv2";
                break;
            case 3:
                table = "struttura_liv3";
                break;
            case 4:
                table = "struttura_liv4";
                break;
        }
        final String GET_STRUCTURE_BY_ID =
                "SELECT " +
                "       D.id        AS \"id\"" +
                "   ,   D.nome      AS \"nome\"" +
                "   ,   D.codice    AS \"informativa\"" +
                "   ,   D.prefisso  AS \"prefisso\"" +
                "   ,   D.acronimo  AS \"acronimo\"" +
                "   ,   D.ordinale  AS \"ordinale\"" +
                "   FROM " + table + " D" +
                "   WHERE D.id = " + id +
                "   ORDER BY D.ordinale";
        return GET_STRUCTURE_BY_ID;
    }


    /**
     * <p>Dato un id e un livello, restituisce una struttura selezionata 
     * in base a quell'id nella tabella identificata in base al livello.</p>
     *
     * @param user          oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param id            identificativo della struttura che si vuol recuperare
     * @param level         identificativo dell'insieme in cui ricercare la struttura
     * @param codeSurvey    riservato
     * @return <code>DepartmentBean</code> - la struttura cercata
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "null" })
    public DepartmentBean getStructure(PersonBean user,
                                       int id,
                                       byte level,
                                       String codeSurvey)
                                throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        DepartmentBean uo = null;
        // TODO: Controllare se user è superuser
        try {
            con = prol_manager.getConnection();
            String query = getQueryStructure(id, level);
            pst = con.prepareStatement(query);
            pst.clearParameters();
            rs = pst.executeQuery();
            while (rs.next()) {
                // Crea una struttura vuota
                uo = new DepartmentBean();
                // La valorizza col contenuto della query
                BeanUtil.populate(uo, rs);
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Let's go away
            return uo;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query della struttura in base all'id.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Restituisce l'elenco delle aree funzionali, depurate di valori non significativi
     * per l'utente (p.es. "DATO ERRATO", "NON DEFINITO"...).</p>
     *
     * @param user  oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @return <code></code> - la lista di aree funzionali cercata
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public ArrayList<ItemBean> getAreeFunzionali(PersonBean user)
                                          throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ItemBean a = null;
        ArrayList<ItemBean> aree = new ArrayList<>();
        // TODO: Controllare se user è superuser
        try {
            con = prol_manager.getConnection();
            pst = con.prepareStatement(GET_AREE_FUNZ);
            pst.clearParameters();
            rs = pst.executeQuery();
            while (rs.next()) {
                // Crea una struttura vuota
                a = new ItemBean();
                // La valorizza col contenuto della query
                BeanUtil.populate(a, rs);
                // La aggiunge all'elenco
                aree.add(a);
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Get out
            return aree;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query di selezione delle aree funzionali.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Restituisce la lista dei ruoli giuridici.</p>
     *
     * @param user  oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - la lista di ruoli giuridici cercata
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public ArrayList<ItemBean> getRuoliGiuridici(PersonBean user)
                                          throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ItemBean rg = null;
        ArrayList<ItemBean> qualifiche = new ArrayList<>();
        // TODO: Controllare se user è superuser
        try {
            con = prol_manager.getConnection();
            pst = con.prepareStatement(GET_ROLES);
            pst.clearParameters();
            rs = pst.executeQuery();
            while (rs.next()) {
                // Crea una struttura vuota
                rg = new ItemBean();
                // La valorizza col contenuto della query
                BeanUtil.populate(rg, rs);
                // La aggiunge all'elenco
                qualifiche.add(rg);
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Get out
            return qualifiche;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query di selezione dei ruoli giuridici.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }

    
    /**
     * <p>Dato il suo id, restituisce un soggetto contingente (ente esterno,
     * o soggetto interessato).</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idSubject identificativo del soggetto contingente cercato
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>DepartmentBean</code> - il soggetto contingente cercato
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public DepartmentBean getSubject(PersonBean user,
                                     int idSubject,
                                     CodeBean survey)
                                     throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            int nextParam = NOTHING;
            ItemBean rawSubj = null;
            DepartmentBean subject = null;
            Vector<DepartmentBean> structBySubj = null;
            // TODO: Controllare se user è superuser
            try {
                pst = con.prepareStatement(GET_SUBJECT);
                pst.clearParameters();
                pst.setInt(++nextParam, idSubject);                
                pst.setInt(++nextParam, survey.getId());
                rs = pst.executeQuery();
                while (rs.next()) {
                    rawSubj = new ItemBean();
                    // Crea una struttura vuota
                    subject = new DepartmentBean();
                    // Valorizza l'oggetto grezzo col contenuto della query
                    BeanUtil.populate(rawSubj, rs);
                    // Prepara le strutture collegate
                    structBySubj = new Vector<>();
                    // Vi aggiunge le strutture trovate
                    if (rawSubj.getCod1() != BEAN_DEFAULT_ID) {
                        structBySubj.add(getStructure(user, rawSubj.getCod1(), ELEMENT_LEV_1, survey.getNome()));
                    }
                    if (rawSubj.getCod2() != BEAN_DEFAULT_ID) {
                        structBySubj.add(getStructure(user, rawSubj.getCod2(), ELEMENT_LEV_2, survey.getNome()));
                    }
                    if (rawSubj.getCod3() != BEAN_DEFAULT_ID) {
                        structBySubj.add(getStructure(user, rawSubj.getCod3(), ELEMENT_LEV_3, survey.getNome()));
                    }
                    if (rawSubj.getCod4() != BEAN_DEFAULT_ID) {
                        structBySubj.add(getStructure(user, rawSubj.getCod4(), ELEMENT_LEV_4, survey.getNome()));
                    }
                    // Travasa gli altri estremi, precedentemente estratti
                    subject.setId(rawSubj.getId());
                    subject.setNome(rawSubj.getNome());
                    subject.setInformativa(rawSubj.getInformativa());
                    subject.setOrdinale(rawSubj.getOrdinale());
                    // TODO: è possibile recuperare qui il tipo inserendolo in extraInfo
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get Out
                return subject;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nell\'accesso ad un attributo obbligatorio del bean; verificare identificativo della rilevazione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query del soggetto contingente.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    

    /**
     * <p>Dato un identificativo di un'attivit&grave;, restituisce una lista 
     * di soggetti contingenti (enti esterni o soggetti interessati) collegati
     * ad essa.</p>
     *
     * @param user          oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idActivity    identificativo dell'attivita' cui il soggetto contingente e' collegato
     * @param survey        oggetto contenente i dati della rilevazione
     * @return <code>Vector&lt;DepartmentBean&gt;</code> - elenco di soggetti contingenti cercati
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public Vector<DepartmentBean> getSubjects(PersonBean user,
                                              int idActivity,
                                              CodeBean survey)
                                       throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            int nextParam = NOTHING;
            ItemBean rawSubj = null;
            DepartmentBean subject = null;
            Vector<DepartmentBean> subjects = new Vector<>();
            Vector<DepartmentBean> structBySubj = null;
            // TODO: Controllare se user è superuser
            try {
                pst = con.prepareStatement(GET_SUBJECT_BY_ACTIVITY);
                pst.clearParameters();
                pst.setInt(++nextParam, idActivity);                
                pst.setInt(++nextParam, survey.getId());
                rs = pst.executeQuery();
                while (rs.next()) {
                    rawSubj = new ItemBean();
                    // Crea una struttura vuota
                    subject = new DepartmentBean();
                    // Valorizza l'oggetto grezzo col contenuto della query
                    BeanUtil.populate(rawSubj, rs);
                    // Prepara le strutture collegate
                    structBySubj = new Vector<>();
                    // Vi aggiunge le strutture trovate
                    if (rawSubj.getCod1() != BEAN_DEFAULT_ID) {
                        structBySubj.add(getStructure(user, rawSubj.getCod1(), ELEMENT_LEV_1, survey.getNome()));
                    }
                    if (rawSubj.getCod2() != BEAN_DEFAULT_ID) {
                        structBySubj.add(getStructure(user, rawSubj.getCod2(), ELEMENT_LEV_2, survey.getNome()));
                    }
                    if (rawSubj.getCod3() != BEAN_DEFAULT_ID) {
                        structBySubj.add(getStructure(user, rawSubj.getCod3(), ELEMENT_LEV_3, survey.getNome()));
                    }
                    if (rawSubj.getCod4() != BEAN_DEFAULT_ID) {
                        structBySubj.add(getStructure(user, rawSubj.getCod4(), ELEMENT_LEV_4, survey.getNome()));
                    }
                    // Travasa gli altri estremi, precedentemente estratti
                    subject.setId(rawSubj.getId());
                    subject.setNome(rawSubj.getNome());
                    subject.setInformativa(rawSubj.getInformativa());
                    subject.setOrdinale(rawSubj.getOrdinale());
                    // TODO: è possibile recuperare qui il tipo inserendolo in extraInfo
                    subjects.add(subject);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get Out
                return subjects;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nell\'accesso ad un attributo obbligatorio del bean; verificare identificativo della rilevazione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query del soggetto contingente.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }


    /**
     * <p>Estrae un elenco di input in base agli estremi 
     * del sotto/processo anticorruttivo.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idp       identificativo del processo/sottoprocesso anticorruttivo
     * @param level     identificativo del livello di processo di cui si devono recuperare gli input
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - la lista di input cercati
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<ItemBean> getInputs(PersonBean user,
                                         int idp,
                                         int level,
                                         CodeBean survey)
                                  throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            int nextParam = NOTHING;
            ItemBean input = null;
            AbstractList<ItemBean> inputs = new ArrayList<>();
            // TODO: Controllare se user è superuser
            try {
                // Calcola la query in funzione del livello (2 = processo_at)
                String query = (level == 2 ? GET_INPUT_BY_PROCESS_AT : GET_INPUT_BY_SUBPROCESS_AT);
                pst = con.prepareStatement(query);
                pst.clearParameters();
                pst.setInt(++nextParam, idp);                
                pst.setInt(++nextParam, survey.getId());
                rs = pst.executeQuery();
                while (rs.next()) {
                    // Crea una struttura vuota
                    input = new ItemBean();
                    // La valorizza col contenuto della query
                    BeanUtil.populate(input, rs);
                    // La aggiunge alla lista di elementi trovati
                    inputs.add(input);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get Out
                return (ArrayList<ItemBean>) inputs;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nell\'accesso ad un attributo obbligatorio del bean; verificare identificativo della rilevazione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query degli input in base all'id del processo at.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }


    /**
     * <p>Estrae un elenco di fasi in base agli estremi 
     * del sotto/processo anticorruttivo.</p>
     * <p>NOTA: fare attenzione al fatto che l'invocazione della populate() 
     * altera i valori di inizializzazione degli attributi dei bean; 
     * per questo motivo non &egrave; possibile fare i test controllando se
     * i valori inizializzati sono maggiori di -2 o -2.0f (che sono i valori
     * di inizializzazione originali nei costruttori dei bean) ma, per verificare
     * se dalla query &egrave; arrivato qualche valore significativo, 
     * bisogner&agrave; testare per valori strettamente maggiori di 0 o di 0.0f.
     * Se si testasse per valori maggiori di -2 o -2.0f il test risulterebbe
     * sempre verificato anche se valori significativi dalla query non 
     * sono arrivati (0 > -2).</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idp       identificativo del processo/sottoprocesso anticorruttivo
     * @param level     identificativo del livello di processo di cui si devono recuperare le fasi
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>ArrayList&lt;ActivityBean&gt;</code> - la lista di fasi cercate
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public ArrayList<ActivityBean> getActivities(PersonBean user,
                                                 int idp,
                                                 int level,
                                                 CodeBean survey)
                                          throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs, rs1 = null;
            int nextParam = NOTHING;
            ItemBean rawActivity = null;
            ActivityBean activity = null;
            AbstractList<ActivityBean> activities = new ArrayList<>();
            ArrayList<DepartmentBean> depts = null;
            Vector<DepartmentBean> subjects = null;
            // TODO: Controllare se user è superuser
            try {
                // Calcola la query in funzione del livello (2 = processo_at)
                String query = (level == 2 ? GET_ACTIVITIES_BY_PROCESS_AT : GET_ACTIVITIES_BY_SUBPROCESS_AT);
                pst = con.prepareStatement(query);
                pst.clearParameters();
                pst.setInt(++nextParam, idp);                
                pst.setInt(++nextParam, survey.getId());
                rs = pst.executeQuery();
                while (rs.next()) {
                    // Crea un oggetto per l'attività
                    activity = new ActivityBean();
                    // Crea una struttura vettoriale per contenere le sue strutture
                    depts = new ArrayList<>();
                    // Crea una struttura vettoriale per contenere i suoi soggetti contingenti
                    subjects = new Vector<>();
                    // Valorizza l'attività col contenuto della query
                    BeanUtil.populate(activity, rs);
                    // Per ogni attività recupera le strutture e i soggetti interessati
                    pst = null;
                    nextParam = NOTHING;
                    pst = con.prepareStatement(GET_STRUCTS_BY_ACTIVITY);
                    pst.clearParameters();
                    pst.setInt(++nextParam, activity.getId());
                    pst.setInt(++nextParam, survey.getId());
                    rs1 = pst.executeQuery();
                    while (rs1.next()) {
                        // Crea una struttura vuota per l'attività "grezza"
                        rawActivity = new ItemBean();
                        // Valorizza il processo col contenuto della query
                        BeanUtil.populate(rawActivity, rs1);
                        // Recupera gli oggetti correlati all'attività in base agli id
                        if (rawActivity.getCod1() > NOTHING) {
                            depts.add(getStructure(user, rawActivity.getCod1(), ELEMENT_LEV_1, survey.getNome()));
                        }
                        if (rawActivity.getCod2() > NOTHING) {
                            depts.add(getStructure(user, rawActivity.getCod2(), ELEMENT_LEV_2, survey.getNome()));
                        }
                        if (rawActivity.getCod3() > NOTHING) {
                            depts.add(getStructure(user, rawActivity.getCod3(), ELEMENT_LEV_3, survey.getNome()));
                        }
                        if (rawActivity.getCod4() > NOTHING) {
                            depts.add(getStructure(user, rawActivity.getCod4(), ELEMENT_LEV_4, survey.getNome()));
                        }
                        // Recupera i soggetti contingenti
                        int idSubj = Utils.parseInt(rawActivity.getValue1());
                        if (idSubj > NOTHING) {
                            subjects.add(getSubject(user, idSubj, survey));
                        }
                        /* Setta tutti gli altri dati
                        activity.setId(rawActivity.getId());
                        activity.setCodice(rawActivity.getCodice());
                        activity.setNome(rawActivity.getNome());
                        activity.setDescrizione(rawActivity.getInformativa());
                        activity.setOrdinale(rawActivity.getOrdinale());
                        activity.setMandatory(rawActivity.isPrivato());*/
                    }
                    // Setta nell'attività le liste appena calcolate
                    activity.setStrutture(depts);
                    activity.setSoggetti(subjects);
                    // La aggiunge alla lista di elementi trovati
                    activities.add(activity);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get Out
                return (ArrayList<ActivityBean>) activities;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nell\'accesso ad un attributo obbligatorio del bean; verificare identificativo della rilevazione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query degli input in base all'id del processo at.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }


    /**
     * <p>Estrae un elenco di output in base agli estremi 
     * del sotto/processo anticorruttivo.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idp       identificativo del processo/sottoprocesso anticorruttivo
     * @param level     identificativo del livello di processo di cui si devono recuperare gli output
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - la lista di output cercati
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<ItemBean> getOutputs(PersonBean user,
                                          int idp,
                                          int level,
                                          CodeBean survey)
                                   throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            int nextParam = NOTHING;
            ItemBean output = null;
            AbstractList<ItemBean> outputs = new ArrayList<>();
            // TODO: Controllare se user è superuser
            try {
                // Calcola la query in funzione del livello (2 = processo_at)
                String query = (level == 2 ? GET_OUTPUT_BY_PROCESS_AT : GET_OUTPUT_BY_SUBPROCESS_AT);
                pst = con.prepareStatement(query);
                pst.clearParameters();
                pst.setInt(++nextParam, idp);                
                pst.setInt(++nextParam, survey.getId());
                rs = pst.executeQuery();
                while (rs.next()) {
                    // Crea un beam vuoto
                    output = new ItemBean();
                    // Lo valorizza col contenuto della query
                    BeanUtil.populate(output, rs);
                    // Lo aggiunge alla lista di elementi trovati
                    outputs.add(output);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get Out
                return (ArrayList<ItemBean>) outputs;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nell\'accesso ad un attributo obbligatorio del bean; verificare identificativo della rilevazione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query degli output in base all'id del processo at.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Estrae l'elenco degli output indipendentemente dal contesto
     * di uno specifico sotto/processo anticorruttivo.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - la lista di output trovati
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<ProcessBean> getOutputs(PersonBean user,
                                             CodeBean survey)
                                      throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            int nextParam = NOTHING;
            ProcessBean output = null;
            AbstractList<ProcessBean> outputs = new ArrayList<>();
            // TODO: Controllare se user è superuser
            try {
                pst = con.prepareStatement(GET_OUTPUTS);
                pst.clearParameters();             
                pst.setInt(++nextParam, survey.getId());
                rs = pst.executeQuery();
                while (rs.next()) {
                    // Crea un bean vuoto
                    output = new ProcessBean();
                    // Lo valorizza col contenuto della query
                    BeanUtil.populate(output, rs);
                    // Lo aggiunge alla lista di elementi trovati
                    outputs.add(output);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get Out
                return (ArrayList<ProcessBean>) outputs;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nell\'accesso ad un attributo obbligatorio del bean; verificare identificativo della rilevazione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query del registro degli output.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    

    /**
     * <p>Dato un id e una rilevazione, restituisce un output selezionato
     * in base a tali parametri, comprensivo dei processi da esso generati.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param id        identificativo dell'output che si vuol recuperare
     * @param survey    oggetto contenente gli estremi della rilevazione
     * @return <code>ProcessBean</code> - l'output desiderato
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings("static-method")
    public ProcessBean getOutput(PersonBean user,
                                 int id,
                                 CodeBean survey)
                          throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs, rs1, rs2 = null;
            int nextParam = NOTHING;
            ProcessBean out = null;
            AbstractList<ProcessBean> processi = null; 
            // TODO: Controllare se user è superuser
            try {
                pst = con.prepareStatement(GET_OUTPUT);
                pst.clearParameters();
                pst.setInt(++nextParam, id);                
                pst.setInt(++nextParam, survey.getId());
                rs = pst.executeQuery();
                if (rs.next()) {
                    // Crea un output vuoto
                    out = new ProcessBean();
                    // La valorizza col contenuto della query
                    BeanUtil.populate(out, rs);
                    // Crea la lista di processi per l'output corrente
                    processi = new ArrayList<>();
                    // Recupera i processi generati dall'input prodotto dall'output
                    nextParam = NOTHING;
                    pst = null;
                    pst = con.prepareStatement(GET_PROCESS_AT_BY_OUTPUT);
                    pst.clearParameters();
                    pst.setInt(++nextParam, out.getId());
                    pst.setInt(++nextParam, survey.getId());
                    rs1 = pst.executeQuery();
                    while (rs1.next()) {
                        // Crera un processo vuoto
                        ProcessBean pat = new ProcessBean();
                        // Lo valorizza col risultato della query
                        BeanUtil.populate(pat, rs1);
                        /* == Ne recupera il macroprocesso_at padre == */
                        pst = null;
                        pst = con.prepareStatement(GET_MACRO_AT_BY_CHILD);
                        pst.clearParameters();
                        pst.setInt(1, pat.getId());
                        pst.setInt(2,survey.getId());
                        rs2 = pst.executeQuery();
                        // Punta al padre (se c'è, è solo uno!)
                        if (rs2.next()) {
                            // Prepara il padre
                            ProcessBean mat = new ProcessBean();
                            // Valorizza il padre
                            BeanUtil.populate(mat, rs2);
                            // Aggiunge il padre al figlio
                            pat.setPadre(mat);
                        }
                        // Aggiunge il processo alla lista di processi per l'output trovato
                        processi.add(pat);
                    }
                    out.setProcessi(processi);
                }
                // Tries (just tries) to engage the Garbage Collector
                pst = null;
                // Get_out(put)
                return out;
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Oggetto non valorizzato; problema nel metodo di recupero output.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nell\'accesso ad un attributo obbligatorio del bean.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (NumberFormatException nfe) {
                String msg = FOR_NAME + "Si e\' verificato un problema in una conversione in numero.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + nfe.getMessage(), nfe);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }        
    }
    
    
    /**
     * <p>Restituisce un Integer corrispondente al wrapper del numero di
     * quesiti trovati dato un identificativo di rilevazione, passato come
     * parametro.</p>
     *
     * @param idSurvey identificativo della rilevazione corrente 
     * @return <code>Integer</code> - il numero di quesiti collegati ad ambiti generici trovati dato l'identificativo della rilevazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public Integer getQuestionsAmountBySurvey(int idSurvey)
                                       throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            Integer tot = null;
            try {
                // TODO: Controllare se user è superuser
                pst = con.prepareStatement(GET_QUESTION_AMOUNT_BY_SURVEY);
                pst.clearParameters();
                pst.setInt(1, idSurvey);
                rs = pst.executeQuery();
                if (rs.next()) {
                    // Prepara bean di appoggio per il valore di count(*)
                    CodeBean amount = new CodeBean();
                    // Valorizza il bean di appoggio
                    BeanUtil.populate(amount, rs);
                    // Incapsula il valore in un Wrapper di tipo primitivo
                    tot = new Integer(amount.getInformativa());
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get out
                return tot;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Oggetto CodeBean.informativa non valorizzato; problema nel metodo di estrazione del numero di quesiti data una rilevazione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (NumberFormatException nfe) {
                String msg = FOR_NAME + "Si e\' verificato un problema in una conversione in numero; verificare che il valore dell\'informativa sia convertibile in numero.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + nfe.getMessage(), nfe);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "QuestionBean non valorizzato; problema nella query dei quesiti.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce un Integer corrispondente al wrapper del numero di
     * quesiti trovati dati gli estremi di un'intervista, passati come
     * parametri.</p>
     * <p>Nota: l'intervista non &egrave; stata pensata come un'entit&agrave;
     * fisica ma come un set di risposte, accomunate tutte dalla stessa
     * data ed ora. Per gli scopi dell'applicazione rol questa soluzione
     * in una prima fase si sviluppo va bene perch&eacute; l'inserimento 
     * delle risposte non &egrave distribuito ma centralizzato. 
     * In tale fase non esiste quindi un identificativo dell'intervista 
     * da passare, ma solo una data ed un'ora, cui ogni risposta
     * accomunata dalla stessa intervista deve fare capo. Successivamente
     * ha senso concepire l'intervista come un'entit&agrave; dotata di un
     * suo identificativo, e considerare le risposte accoumunate non in base 
     * a una marca temporale ma piuttosto in base alla chiave esterna 
     * di tale identificativo. Ci&ograve; per prevenire l'evenienza di
     * un improbabile, ma non impossibile, salvataggio di due distinti 
     * set di risposte nella stessa data ed ora.</p>
     * 
     * @param params    mappa contenente i parametri di navigazione
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>Integer</code> - il numero di quesiti collegati ad una stessa marca temporale richiesta
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public Integer getQuestionsAmountWithAnswerByInterview(HashMap<String, LinkedHashMap<String, String>> params,
                                                           CodeBean survey)
                                                    throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            Integer tot = null;
            // Dizionario dei parametri temporali
            LinkedHashMap<String, String> surv = params.get(PARAM_SURVEY);
            try {
                // TODO: Controllare se user è superuser
                // Recupera dai parametri la data della marca temporale delle risposte
                String dateAsString = (!surv.get("d").equals(VOID_STRING) ? surv.get("d") : VOID_STRING);
                // Recupera dai parametri l'ora della marca temporale delle risposte
                String time = (!surv.get("t").equals(VOID_STRING) ? surv.get("t").replaceAll("_", ":") : VOID_STRING);
                // Converte la data recuperata come String in una java.util.Date
                java.util.Date dateAsDate = Utils.format(dateAsString, DATA_GENERAL_PATTERN, DATA_SQL_PATTERN);
                // Imposta la query
                pst = con.prepareStatement(GET_QUESTION_AMOUNT_WITH_ANSWER_BY_INTERVIEW);
                pst.clearParameters();
                // Converte la data trasformata in java.util.Date in una java.sql.Date
                pst.setDate(1, Utils.convert(dateAsDate));
                // Converte l'ora recuperata come String in un oggetto di tipo java.sql.Time
                pst.setTime(2, Utils.format(time, TIME_SQL_PATTERN));
                pst.setInt(3, survey.getId());
                rs = pst.executeQuery();
                if (rs.next()) {
                    // Prepara bean di appoggio per il valore di count(*)
                    CodeBean amount = new CodeBean();
                    // Valorizza il bean di appoggio
                    BeanUtil.populate(amount, rs);
                    // Incapsula il valore in un Wrapper di tipo primitivo
                    tot = new Integer(amount.getInformativa());
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get out
                return tot;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Oggetto CodeBean.informativa non valorizzato; problema nel metodo di estrazione del numero di quesiti data una rilevazione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (CommandException ce) {
                String msg = FOR_NAME + "Si e\' verificato un problema in una conversione di tipi.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + ce.getMessage(), ce);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "QuestionBean non valorizzato; problema nella query dei quesiti.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    

    /**
     * <p>Data una rilevazione, restituisce un ArrayList di macroprocessi 
     * censiti dall'anticorruzione ad essa afferenti, contenenti ciascuno 
     * i processi figli al proprio interno. Ogni figlio conterr&agrave;
     * i relativi figli.</p>
     * <p>Deve recuperare solo i macroprocessi su cui un utente, 
     * il cui username viene passato come argomento, ha i diritti di accesso 
     * (in base al ruolo <em>per se</em>).</p>
     *
     * @param user oggetto rappresentante la persona loggata
     * @param codeSurvey identificativo della rilevazione di cui si vogliono recuperare i macroprocessi
     * @return <code>ArrayList&lt;ProcessBean&gt;</code> - un Vector di ProcessBean, che rappresentano i macroprocessi su cui l'utente ha i diritti di lettura
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "null" })
    public ArrayList<ProcessBean> getMacroAtBySurvey(PersonBean user,
                                                     String codeSurvey)
                                              throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs, rs1, rs2 = null;
        ProcessBean macro = null;
        ProcessBean processo = null;
        ProcessBean sottoprocesso = null;
        CodeBean rilevazione = null;
        AbstractList<ProcessBean> macroprocessi = new ArrayList<>();
        AbstractList<ProcessBean> processi = null;
        AbstractList<ProcessBean> sottoprocessi = null;
        int idThru = NOTHING;   // id globale
        try {
            // TODO: Controllare se user è superuser
            con = prol_manager.getConnection();
            pst = con.prepareStatement(GET_MACRO_AT_BY_SURVEY);
            pst.clearParameters();
            pst.setString(1, codeSurvey);
            rs = pst.executeQuery();
            while (rs.next()) {
                // Incrementa l'id globale
                ++idThru;
                // Crea un macroprocesso vuoto
                macro = new ProcessBean();
                // Istanzia una struttura vettoriale per contenere i suoi processi
                processi = new Vector<>();
                // Valorizza il macroprocesso col contenuto della query
                BeanUtil.populate(macro, rs);
                // Ne imposta il livello
                macro.setLivello(1);
                // Recupera la rilevazione
                rilevazione = getSurvey(macro.getIdRilevazione(), macro.getIdRilevazione());
                // Imposta la rilevazione
                macro.setRilevazione(rilevazione);
                // Genera il codice globale
                macro.setTag(String.valueOf(idThru + DOT + macro.getId() + DASH + macro.getLivello()));
                // Recupera i processi del macroprocesso
                pst = null;
                pst = con.prepareStatement(GET_PROCESSI_AT_BY_MACRO);
                pst.clearParameters();
                pst.setInt(1, macro.getId());
                pst.setString(2, codeSurvey);
                rs1 = pst.executeQuery();
                while (rs1.next()) {
                    processo = new ProcessBean();
                    // Istanzia una struttura vettoriale per contenere i suoi sottoprocessi
                    sottoprocessi = new Vector<>();
                    // Valorizza il processo col contenuto della query
                    BeanUtil.populate(processo, rs1);
                    // Ne imposta il livello
                    processo.setLivello(2);
                    // Genera il codice globale
                    processo.setTag(String.valueOf(idThru + DOT + processo.getId() + DASH + processo.getLivello()));
                    // Recupera i sottoprocessi del processo
                    pst = null;
                    pst = con.prepareStatement(GET_SOTTOPROCESSI_AT_BY_PROCESS);
                    pst.clearParameters();
                    pst.setInt(1, processo.getId());
                    pst.setString(2, codeSurvey);
                    rs2 = pst.executeQuery();
                    while (rs2.next()) {
                        // Crea un sottoprocesso vuoto
                        sottoprocesso = new ProcessBean();
                        // Lo valorizza col contenuto della query
                        BeanUtil.populate(sottoprocesso, rs2);
                        // Ne imposta il livello
                        sottoprocesso.setLivello(3);
                        // Genera il codice globale
                        sottoprocesso.setTag(String.valueOf(idThru + DOT + sottoprocesso.getId() + DASH + sottoprocesso.getLivello()));
                        // Lo aggiunge alla lista dei sottoprocessi trovati
                        sottoprocessi.add(sottoprocesso);
                    }
                    // Imposta i sottoprocessi
                    processo.setProcessi(sottoprocessi);
                    // Aggiunge il processo valorizzato all'elenco
                    processi.add(processo);
                }
                // Imposta i processi
                macro.setProcessi(processi);
                // Aggiunge il macroprocesso valorizzato all'elenco
                macroprocessi.add(macro);
                // It tries to dereference some object
                rs1 = rs2 = null;
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Get out
            return (ArrayList<ProcessBean>) macroprocessi;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Oggetto ProcessBean.some non valorizzato; problema nel metodo di estrazione dei processi/macroprocessi.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + anve.getMessage(), anve);
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto ProcessBean non valorizzato; problema nella query dei processi/macroprocessi..\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }

    
    /* (non-Javadoc)
     * @see it.rol.Query#getQueryMacroByStruct(int, byte, java.lang.String)
     */
    @SuppressWarnings("javadoc")
    @Override
    public String getQueryMacroSubProcessAtBySurvey(int idP, byte level, int idSur) {
        String tableClause = null;
        switch(level) {
            case ELEMENT_LEV_1:
                tableClause = " AND MAT.id = " + idP;
                break;
            case ELEMENT_LEV_2:
                tableClause = " AND PAT.id = " + idP;
                break;
            case ELEMENT_LEV_3:
                tableClause = " AND SAT.id = " + idP;
                break;
            default:
                tableClause = VOID_STRING;
                break;
        }
        final String GET_MACRO_AT_BY_SURVEY_AS_LIST =
                "SELECT DISTINCT" +
                        "       AR.codice               AS \"codice\"" +
                        "   ,   AR.nome                 AS \"nome\"" +
                        "   ,   AR.ordinale" +
                        "   ,   MAT.id                  AS \"id\"" +
                        "   ,   MAT.codice              AS \"informativa\"" +
                        "   ,   MAT.nome                AS \"nomeReale\"" +
                        "   ,   MAT.ordinale            AS \"ordinale\"" +
                        "   ,   MAT.id_rilevazione" +
                        "   ,   PAT.id" +
                        "   ,   PAT.codice              AS \"url\"" +
                        "   ,   PAT.nome                AS \"labelWeb\"" +
                        "   ,   PAT.ordinale" +
                        "   ,   PAT.id_rilevazione" +
                        "   ,   (SELECT count(*) FROM input_processo_at INPAT WHERE INPAT.id_processo_at = PAT.id AND INPAT.id_rilevazione = PAT.id_rilevazione)    AS \"cod1\"" +
                        "   ,   (SELECT count(*) FROM attivita A WHERE A.id_processo_at = PAT.id AND A.id_rilevazione = PAT.id_rilevazione)                         AS \"cod2\"" +
                        "   ,   (SELECT count(*) FROM output_processo_at OUTPAT WHERE OUTPAT.id_processo_at = PAT.id AND OUTPAT.id_rilevazione = PAT.id_rilevazione) AS \"cod3\"" +
                        "   ,   I.nome                  AS \"extraInfo1\"" +
                        "   ,   lag(I.nome,1) over(ORDER BY AR.ordinale, MAT.codice, MAT.ordinale, PAT.ordinale, SAT.ordinale, I.nome)  AS \"extraInfo2\"" +
                        "   ,   A.nome                  AS \"extraInfo3\"" +
                        "   ,   O.nome                  AS \"extraInfo4\"" +
                        "   ,   SAT.id" +
                        "   ,   SAT.codice              AS \"icona\"" +
                        "   ,   SAT.nome                AS \"extraInfo\"" +
                        "   ,   SAT.ordinale" +
                        "   FROM macroprocesso_at MAT" +
                        "       INNER JOIN rilevazione R ON MAT.id_rilevazione = R.id" +
                        "       LEFT JOIN area_rischio AR ON MAT.id_area_rischio = AR.id" +
                        "       LEFT JOIN processo_at PAT ON PAT.id_macroprocesso_at = MAT.id" +
                        "       LEFT JOIN input_processo_at INPAT ON INPAT.id_processo_at = PAT.id" + 
                        "       LEFT JOIN input I ON INPAT.id_input = I.id" +
                        "       LEFT JOIN attivita A ON A.id_processo_at = PAT.id" +
                        "       LEFT JOIN output_processo_at OUTPAT ON OUTPAT.id_processo_at = PAT.id" +
                        "       LEFT JOIN output O ON OUTPAT.id_output = O.id" +
                        "       LEFT JOIN sottoprocesso_at SAT ON SAT.id_processo_at = PAT.id" +
                        "   WHERE R.id = " + idSur + 
                        "       AND PAT.id_rilevazione = " + idSur +
                                tableClause +
                        "   ORDER BY AR.ordinale, MAT.codice, MAT.ordinale, PAT.ordinale, SAT.ordinale";
        return GET_MACRO_AT_BY_SURVEY_AS_LIST;
    }
    
    
    /**
     * <p>Data una rilevazione, restituisce un ArrayList di macroprocessi 
     * censiti dall'anticorruzione ad essa afferenti, corredati, sullo
     * stesso record, degli estremi dei processi e sottoprocessi anticorrutivi
     * ad essi collegati, delle aree di rischio e di altri indicatori.</p>
     * <p>Questa estrazione, ridondante e non normalizzata, &egrave;
     * adatta a generare file csv e simili set di tuple denormalizzate.</p>
     *
     * @param user      oggetto rappresentante la persona loggata
     * @param idMPSAT   identificativo di un macroprocesso, di un processo o di un sottoprocesso anticorruttivo 
     * @param level     insieme in cui cercare l'identificativo (1 = macroprocesso_at | 2 = processo_at | 3 = sottoprocesso_at)
     * @param survey    oggetto rilevazione di cui si vogliono recuperare i macroprocessi, i processi e relative informazioni
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - un Vector di ItemBean, ciascuno rappresentante una tupla completa
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public ArrayList<ItemBean> getMacroSubProcessAtBySurvey(PersonBean user,
                                                            int idMPSAT,
                                                            byte level,
                                                            CodeBean survey)
                                                     throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            ItemBean item = null;
            AbstractList<ItemBean> macroprocessi = new ArrayList<>();
            try {
                // TODO: Controllare se user è superuser
                String query = getQueryMacroSubProcessAtBySurvey(idMPSAT, level, survey.getId());
                pst = con.prepareStatement(query);
                pst.clearParameters();
                rs = pst.executeQuery();
                while (rs.next()) {
                    // Crea un item vuoto
                    item = new ItemBean();
                    // Valorizza l'item col contenuto della query
                    BeanUtil.populate(item, rs);
                    // Aggiunge l'item all'elenco
                    macroprocessi.add(item);
                }
                // Tries to engage the Garbage Collector
                pst = null;
                // Get out
                return (ArrayList<ItemBean>) macroprocessi;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Attributo identificativo della rilevazione non valorizzato; problema nel metodo di estrazione dei processi/macroprocessi denormalizzati.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query dei macroprocessi denormalizzati.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }


    /**
     * <p>Restituisce un ArrayList contenente tutti gli ambiti di analisi trovati.</p>
     *
     * @param user     oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - un vettore ordinato di ItemBean, che rappresentano gli ambiti di analisi trovati
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<ItemBean> getAmbits(PersonBean user)
                                  throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            AbstractList<ItemBean> ambits = new ArrayList<>();
            try {
                // TODO: Controllare se user è superuser
                pst = null;
                pst = con.prepareStatement(GET_AMBIT);
                pst.clearParameters();
                pst.setInt(1, GET_ALL_BY_CLAUSE);
                pst.setInt(2, GET_ALL_BY_CLAUSE);
                rs = pst.executeQuery();
                // Punta all'àmbito
                while (rs.next()) {
                    // Prepara l'àmbito
                    ItemBean ambit = new ItemBean();
                    // Valorizza l'àmbito
                    BeanUtil.populate(ambit, rs);
                    // Aggiunge l'àmbito al quesito
                    ambits.add(ambit);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get out
                return (ArrayList<ItemBean>) ambits;
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "QuestionBean non valorizzato; problema nella query dei quesiti.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Data una rilevazione restituisce
     * un ArrayList di domande da sottoporre per rilevare i rischi.</p>
     * <p><strong>Il metodo &egrave; ricorsivo</strong> e funziona 
     * richiamando se stesso per arricchire in una passata successiva 
     * la lista di quesiti trovati.</p>
     * <p>La prima volta che viene eseguito, riceve come identificativi interi 
     * il valore convenzionale -1 e quindi recupera un elenco; 
     * per ogni quesito trovato, recupera gli identificativi di risorse 
     * collegate al quesito, inserendoli in attributi temporanei; 
     * quindi utilizza immediatamente tali valori per ottenere oggetti 
     * completi, che inserisce come attributi a loro volta del bean 
     * che inizialmente conteneva i valori temporanei.<br />
     * Quando richiama se stesso, non passa pi&uacute; il valore -1 ma 
     * il vero id del quesito corrente, di cui si vuol recuperare 
     * il quesito padre, se esiste, aggiungendolo al quesito corrente 
     * e aggiungendo finalmente questo all'elenco.<br /> 
     * Quindi termina l'esecuzione.</p>
     * <p>Il metodo quindi pu&ograve; essere utilizzato
     * <ul>
     * <li>sia per ottenere una lista di quesiti,</li> 
     * <li>sia per ottenere uno specifico quesito (sia pure
     * sotto forma di lista, perch&eacute; ovviamente il tipo restituito 
     * pu&ograve; essere solo uno).</li></ul> 
     * <p><dl><dt>Se si vuol ottenere una lista</dt>
     * <dd>bisogna passare un valore qualunque
     * sul primo parametro e -1 sul secondo parametro.</dd>
     * <dt>Se si vuol ottenere un quesito</dt>
     * <dd>bisogna passare l'identificativo
     * del quesito sia sul primo sia sul secondo parametro.</dd></dl></p>
     *
     * @param user          oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param survey        oggetto contenente i dati della rilevazione
     * @param idQuestion    identificativo di uno specifico quesito
     * @param getAll        valore convenzionale; se vale -1 comporta il recupero di tutti i quesiti, indipendentemente dall'id passato come parametro
     * @return <code>ArrayList&lt;QuestionBean&gt;</code> - un Vector di QuestionBean, che rappresentano le domande atte a rilevare i rischi corruttivi
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public ArrayList<QuestionBean> getQuestions(PersonBean user,
                                                CodeBean survey,
                                                int idQuestion,
                                                int getAll)
                                         throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs, rs1, rs2, rs3, rs4 = null;
            QuestionBean question = null;
            AbstractList<QuestionBean> questions = new ArrayList<>();
            int nextParam = NOTHING;
            try {
                // TODO: Controllare se user è superuser
                pst = con.prepareStatement(GET_QUESTIONS);
                pst.clearParameters();
                pst.setInt(++nextParam, survey.getId());
                pst.setInt(++nextParam, idQuestion);
                pst.setInt(++nextParam, getAll);
                rs = pst.executeQuery();
                while (rs.next()) {
                    // Azzera il conto dei parametri
                    nextParam = NOTHING;
                    // Crea una domanda vuota
                    question = new QuestionBean();
                    // La valorizza col contenuto della query
                    BeanUtil.populate(question, rs);
                    /* == Recupera l'àmbito == */
                    pst = null;
                    pst = con.prepareStatement(GET_AMBIT);
                    pst.clearParameters();
                    pst.setInt(++nextParam, question.getCod1());
                    pst.setInt(++nextParam, question.getCod1());
                    rs1 = pst.executeQuery();
                    // Punta all'àmbito
                    if (rs1.next()) {
                        // Prepara l'àmbito
                        CodeBean ambit = new CodeBean();
                        // Valorizza l'àmbito
                        BeanUtil.populate(ambit, rs1);
                        // Aggiunge l'àmbito al quesito
                        question.setAmbito(ambit);
                    }
                    /* == Recupera il tipo di quesito == */
                    nextParam = NOTHING;
                    pst = null;
                    pst = con.prepareStatement(GET_QUESTION_TYPE);
                    pst.clearParameters();
                    pst.setInt(++nextParam, question.getCod2());
                    pst.setInt(++nextParam, question.getCod2());
                    rs2 = pst.executeQuery();
                    // Punta al tipo di quesito
                    if (rs2.next()) {
                        // Prepara il tipo di quesito
                        CodeBean type = new CodeBean();
                        // Valorizza il tipo di quesito
                        BeanUtil.populate(type, rs2);
                        // Aggiunge il tipo al quesito
                        question.setTipo(type);
                    }
                    /* == Recupera il tipo di formulazione == */
                    nextParam = NOTHING;
                    pst = null;
                    pst = con.prepareStatement(GET_QUESTION_WORDING);
                    pst.clearParameters();
                    pst.setInt(++nextParam, question.getCod3());
                    pst.setInt(++nextParam, question.getCod3());
                    rs3 = pst.executeQuery();
                    // Punta al tipo di formulazione
                    if (rs3.next()) {
                        // Prepara il tipo di formulazione
                        ItemBean wording = new ItemBean();
                        // Valorizza il tipo di formulazione
                        BeanUtil.populate(wording, rs3);
                        // Aggiunge il tipo di formulazione al quesito
                        question.setTipoFormulazione(wording);
                    }
                    /* == Recupera i quesiti figli == */
                    nextParam = NOTHING;
                    pst = null;
                    pst = con.prepareStatement(GET_QUESTIONS_BY_QUESTION);
                    pst.clearParameters();
                    pst.setInt(++nextParam, survey.getId());
                    pst.setInt(++nextParam, question.getId());
                    rs4 = pst.executeQuery();
                    ArrayList<QuestionBean> children = null;
                    // Punta ai quesiti figli
                    while (rs4.next()) {
                        // Prepara la lista di figli
                        children = new ArrayList<>();
                        // Prepara il quesito figlio
                        QuestionBean child = new QuestionBean();
                        // Valorizza il quesito figlio
                        BeanUtil.populate(child, rs4);
                        // Aggiunge il figlio ai figli
                        children.add(child);
                    }
                    question.setChildQuestions(children);
                    /* == Recupera il quesito padre (richiamando se stesso) == */
                    ArrayList<QuestionBean> fatherAsList = getQuestions(user, survey, question.getCod4(), question.getCod4());
                    if (fatherAsList != null && !fatherAsList.isEmpty()) {
                        // Recupera ricorsivamente il padre
                        QuestionBean father = fatherAsList.get(NOTHING);
                        // Aggiunge il padre al quesito
                        question.setParentQuestion(father);
                        // Aggiunge il quesito valorizzato all'elenco dei quesiti
                        questions.add(question);
                    } else {
                        // Deve aggiungere il quesito perché alla prossima esce
                        questions.add(question);
                        // Condizione di uscita
                        continue;
                    }
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get out
                return (ArrayList<QuestionBean>) questions;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nell\'accesso ad un attributo obbligatorio del bean; verificare identificativo della rilevazione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "QuestionBean non valorizzato; problema nella query dei quesiti.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /* (non-Javadoc)
     * @see it.rol.Query#getQueryStructures(int, int, int, int, int)
     */
    @SuppressWarnings("javadoc")
    @Override
    public String getQueryAnswers(HashMap<String, LinkedHashMap<String, String>> params,
                                  int idSurvey,
                                  int numOfQuest,
                                  int idQuest,
                                  boolean getAll) {
        // Dizionario dei parametri delle strutture scelte dall'utente
        LinkedHashMap<String, String> struct = params.get(PART_SELECT_STR);
        // Dizionario dei parametri dei processi scelti dall'utente
        LinkedHashMap<String, String> proc = params.get(PART_PROCESS);
        // Dizionario dei parametri temporali
        LinkedHashMap<String, String> surv = params.get(PARAM_SURVEY);
        // Preparazione dei parametri
        String idStrLiv1 = struct.get("liv1").substring(struct.get("liv1").indexOf('.') + 1, struct.get("liv1").indexOf('-'));
        String idStrLiv2 = struct.get("liv2").substring(struct.get("liv2").indexOf('.') + 1, struct.get("liv2").indexOf('-'));
        String idStrLiv3 = (!struct.get("liv3").equals(VOID_STRING) ? (struct.get("liv3").substring(struct.get("liv3").indexOf('.') + 1, struct.get("liv3").indexOf('-'))) : VOID_STRING);
        String idStrLiv4 = (!struct.get("liv4").equals(VOID_STRING) ? (struct.get("liv4").substring(struct.get("liv4").indexOf('.') + 1, struct.get("liv4").indexOf('-'))) : VOID_STRING);
        String idProLiv1 = proc.get("liv1").substring(NOTHING, proc.get("liv1").indexOf('.'));
        String idProLiv2 = (!proc.get("liv2").equals(VOID_STRING) ? (proc.get("liv2").substring(NOTHING,proc.get("liv2").indexOf('.'))) : VOID_STRING);
        String idProLiv3 = (!proc.get("liv3").equals(VOID_STRING) ? (proc.get("liv3").substring(NOTHING,proc.get("liv3").indexOf('.'))) : VOID_STRING);
        String date = (!surv.get("d").equals(VOID_STRING) ? surv.get("d") : VOID_STRING);
        String time = (!surv.get("t").equals(VOID_STRING) ? surv.get("t").replaceAll("_", ":") : VOID_STRING);
        // Clausole
        StringBuffer clause = new StringBuffer("R.id_rilevazione = " + idSurvey);
        // Filtro per id quesito
        String clauseOnQuestion = (getAll ? BLANK_SPACE + "OR -1 = " + GET_ALL_BY_CLAUSE + ")" : ")");
        clause.append(BLANK_SPACE).append("AND (R.id_quesito = " + idQuest);
        clause.append(clauseOnQuestion);
        // Filtro per id struttura_*
        if (!idStrLiv1.equals(VOID_STRING)) {
            clause.append(BLANK_SPACE).append("AND R.id_struttura_liv1 = " + idStrLiv1);
        }
        if (!idStrLiv2.equals(VOID_STRING)) {
            clause.append(BLANK_SPACE).append("AND R.id_struttura_liv2 = " + idStrLiv2);
        }
        if (!idStrLiv3.equals(VOID_STRING)) {
            clause.append(BLANK_SPACE).append("AND R.id_struttura_liv3 = " + idStrLiv3);
        }
        if (!idStrLiv4.equals(VOID_STRING)) {
            clause.append(BLANK_SPACE).append("AND R.id_struttura_liv4 = " + idStrLiv4);
        }
        // Filtro per id processo
        if (!idProLiv1.equals(VOID_STRING)) {
            clause.append(BLANK_SPACE).append("AND R.id_macroprocesso_at = " + idProLiv1);
        }
        if (!idProLiv2.equals(VOID_STRING)) {
            clause.append(BLANK_SPACE).append("AND R.id_processo_at = " + idProLiv2);
        }
        if (!idProLiv3.equals(VOID_STRING)) {
            clause.append(BLANK_SPACE).append("AND R.id_sottoprocesso_at = " + idProLiv3);
        }
        // Filtro per data e ora
        if (!date.equals(VOID_STRING)) {
            clause.append(BLANK_SPACE).append("AND R.data_ultima_modifica = '" + date + "'");
        }
        if (!time.equals(VOID_STRING)) {
            //String timeFormat = time.replaceAll("_", ":")
            clause.append(BLANK_SPACE).append("AND R.ora_ultima_modifica = '" + time + "'");
        }
        // Query
        final String GET_ANSWERS =
                "SELECT DISTINCT" +
                "       R.id                        AS \"id\"" +
                "   ,   R.valore                    AS \"nome\"" +
                "   ,   R.note                      AS \"informativa\"" +
                "   ,   R.ordinale                  AS \"ordinale\"" +
                "   ,   R.id_struttura_liv1         AS \"value1\"" +
                "   ,   R.id_struttura_liv2         AS \"value2\"" +
                "   ,   R.id_struttura_liv3         AS \"value3\"" +
                "   ,   R.id_struttura_liv4         AS \"value4\"" +
                "   ,   R.id_macroprocesso_at       AS \"cod1\""   +
                "   ,   R.id_processo_at            AS \"cod2\""   +
                "   ,   R.id_sottoprocesso_at       AS \"cod3\""   +
                "   ,   R.id_rilevazione            AS \"cod4\"" + 
                "   ,   R.id_quesito                AS \"livello\"" +
                "   ,   R.data_ultima_modifica      AS \"extraInfo\"" +
                "   ,   R.ora_ultima_modifica       AS \"labelWeb\"" +
                "   ,   AM.id" +
                "   FROM risposta R" +
                "       INNER JOIN quesito Q ON R.id_quesito = Q.id" +
                "       INNER JOIN ambito_analisi AM ON Q.id_ambito_analisi = AM.id" +
                "   WHERE " + clause +
                "   ORDER BY R.data_ultima_modifica DESC, R.ora_ultima_modifica DESC, AM.id"; 
              //"   LIMIT " + numOfQuest;
        return GET_ANSWERS;
    }
    
    
    /**
     * <p>Estrae un elenco di risposte ad una serie di quesiti 
     * associati a una data rilevazione.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param params    mappa contenente i parametri di navigazione
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>ArrayList&lt;QuestionBean&gt;</code> - la lista di risposte trovate
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public ArrayList<QuestionBean> getAnswers(PersonBean user,
                                              HashMap<String, LinkedHashMap<String, String>> params,
                                              CodeBean survey)
                                       throws WebStorageException { 
        // Resource 'con' should be managed by try-with-resource
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            QuestionBean question = null;
            QuestionBean answer = null;
            AbstractList<QuestionBean> answers = new ArrayList<>();
            // Carica la tabella che ad ogni rilevazione associa il numero di quesiti corrispondenti
            ConcurrentHashMap<String, Integer> questionAmounts = ConfigManager.getQuestionAmount();
            // TODO: Controllare se user è superuser
            try {
                // Recupera il numero di quesiti associati alla rilevazione
                int limit = questionAmounts.get(survey.getNome()).intValue();
                // Costruisce la query di selezione delle risposte
                String query = getQueryAnswers(params, survey.getId(), limit, GET_ALL_BY_CLAUSE, GET_ALL);
                // Prepara l'estrazione
                pst = con.prepareStatement(query);
                pst.clearParameters();
                rs = pst.executeQuery();
                // Per ogni record
                while (rs.next()) {
                    // Crea un oggetto per la risposta
                    answer = new QuestionBean();
                    // Lo valorizza col contenuto della query
                    BeanUtil.populate(answer, rs);
                    // Recupera il quesito della risposta sotto forma di lista tramite il metodo ricorsivo
                    ArrayList<QuestionBean> questionAsList = getQuestions(user, survey, answer.getLivello(), answer.getLivello());
                    // Assunzione: la relazione tra quesito e risposta è 1 : 1
                    question = questionAsList.get(NOTHING);
                    // Aggiunge al quesito la risposta corrente
                    question.setAnswer(answer);
                    // Lo aggiunge alla lista di risposte trovate
                    answers.add(question);
                }
                // Closes the statement
                pst.close();
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get out
                return (ArrayList<QuestionBean>) answers;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Attributo obbligatorio non recuperabile; problema nel metodo di estrazione delle risposte.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query SQL o nella chiusura dello statement.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /* (non-Javadoc)
     * @see it.rol.Query#getQueryStructureBySurvey(int, int, int, int, int)
     */
    @SuppressWarnings("javadoc")
    @Override
    public String getQueryStructureBySurvey(int idR, int idl4, int idl3, int idl2, int idl1) {
        String tableFrom = null;
        String idD = null;
        byte level = (byte) DEFAULT_ID;
        // Verifica se l'id di livello 4 è non impostato
        if (idl4 == BEAN_DEFAULT_ID) { // it means Liv 4 is null
            // Verifica se l'id di livello 3 non è impostato
            if (idl3 == BEAN_DEFAULT_ID) { // it means Liv 3 is null
                // Verifica se l'id di livello 3 non è impostato
                if (idl2 == BEAN_DEFAULT_ID) { // it means Liv 2 is null
                    // Verifica se c'è un errore (almeno una struttura di Liv 1 dev'esserci)
                    if (idl1 == BEAN_DEFAULT_ID) { // it means Liv 1 is null! Something's wrong
                        return DASH; // "ERR!";
                    }
                    // Clausole per recuperare la struttura di Liv 1
                    level = (byte) 1;
                    tableFrom = "struttura_liv1";
                    idD = String.valueOf(idl1);
                // Clausole per recuperare la struttura di Liv 2
                } else {       
                    level = (byte) 2;
                    tableFrom = "struttura_liv2";
                    idD = String.valueOf(idl2);
                }
            // Clausole per recuperare la struttura di Liv 3
            } else {
                level = (byte) 3;
                tableFrom = "struttura_liv3";
                idD = String.valueOf(idl3);
            }
        // Clausole per recuperare la struttura di Liv 4
        } else {
            level = (byte) 4;
            tableFrom = "struttura_liv4";
            idD = String.valueOf(idl4);
        }
        final String GET_STRUCTURE_BY_INTERVIEW =
                "SELECT " +
                "       D.id                AS \"id\"" +
                "   ,   D.nome              AS \"nome\"" +
                "   ,   concat('0.',D.id::VARCHAR,'-'," + level + ")" +
                "                           AS \"informativa\"" +
                "   ,   D.ordinale          AS \"ordinale\"" +
                "   ,   D.prefisso          AS \"prefisso\"" +
                "   ,   D.acronimo          AS \"acronimo\"" +
                "   ,   D.indirizzo_sede    AS \"indirizzo\"" +
                "   ," + level + "::SMALLINT AS \"livello\"" +
                "   FROM " + tableFrom + " D" +
                "   WHERE D.id_rilevazione = " + idR +
                "       AND D.id = " + idD +
                "   ORDER BY D.nome";
        return GET_STRUCTURE_BY_INTERVIEW;
    }
    
    
    /* (non-Javadoc)
     * @see it.rol.Query#getQueryProcessBySurvey(int, int, int, int)
     */
    @SuppressWarnings("javadoc")
    @Override
    public String getQueryProcessBySurvey(int idR, int idS, int idP, int idM) {
        String tableFrom = null;
        String idD = null;
        byte level = (byte) DEFAULT_ID;
        // Verifica se l'id di sottoprocesso non è impostato
        if (idS == BEAN_DEFAULT_ID) { // it means sub is null
            // Verifica se l'id di processo non è impostato
            if (idP == BEAN_DEFAULT_ID) { // it means proc is null
                // Verifica se c'è un errore (almeno un macroprocesso dev'esserci)
                if (idM == BEAN_DEFAULT_ID) { // it means Liv 1 is null! Something's wrong
                    return DASH; // "ERR!";
                }
                // Clausole per recuperare il macroprocesso anticorruttivo
                level = (byte) 1;
                tableFrom = "macroprocesso_at";
                idD = String.valueOf(idM);
            // Clausole per recuperare il processo anticorruttivo
            } else {       
                level = (byte) 2;
                tableFrom = "processo_at";
                idD = String.valueOf(idP);
            }
        // Clausole per recuperare il sottoprocesso anticorruttivo
        } else {
            level = (byte) 3;
            tableFrom = "sottoprocesso_at";
            idD = String.valueOf(idS);
        }
        final String GET_PROCESS_BY_INTERVIEW =
                "SELECT " +
                "       PAT.id                  AS \"id\"" +
                "   ,   PAT.nome                AS \"nome\"" +
                "   ,   PAT.codice              AS \"codice\"" +
                "   ,   concat(PAT.id::VARCHAR,'.',PAT.codice)" +
                "                               AS \"informativa\"" +
                "   ,   PAT.ordinale            AS \"ordinale\"" +
                "   ,   PAT.smartworking        AS \"smartWorking\"" +
                "   ,   PAT.attivita            AS \"descrizione\"" +
                "   ," + level + "::SMALLINT    AS \"livello\"" +
                "   FROM " + tableFrom + " PAT" +
                "   WHERE PAT.id_rilevazione = " + idR +
                "       AND PAT.id = " + idD +
                "   ORDER BY PAT.nome";
        return GET_PROCESS_BY_INTERVIEW;
    }
    
    
    /**
     * <p>Estrae un elenco di interviste definita ciascuna come un insieme 
     * di riposte a quesiti associati a una data rilevazione.<br />
     * L'insieme di risposte viene identificato a partire da una chiave di fatto,
     * costituita dai seguenti campi:<ul>
     * <li>id struttura liv1, liv2, liv3, liv4</li>
     * <li>id macroprocesso, processo, sottoprocesso</li>
     * <li>id rilevazione</li>
     * <li>stessa data e ora per tutte le risposte</li></ul></p>
     * <p>Per ogni intervista, utilizza un ItemBean per memorizzare gli id
     * degli oggetti correlati all'intervista; quindi utilizza tali id dell'item
     * per estrarre, a partire dall'id in questione, il relativo oggetto completo,
     * che memorizza nell'apposito bean per l'intervista (InterviewBean).<br />
     * Infine, restituisce la lista di InterviewBean.</p> 
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param params    mappa contenente i parametri di navigazione
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>ArrayList&lt;InterviewBean&gt;</code> - la lista di interviste trovate
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public ArrayList<InterviewBean> getInterviewsBySurvey(PersonBean user,
                                                          HashMap<String, LinkedHashMap<String, String>> params,
                                                          CodeBean survey)
                                                   throws WebStorageException {
        PreparedStatement pst = null;
        ResultSet rs, rs1, rs2, rs3, rs4, rs5, rs6, rs7 = null;
        ItemBean item = null;
        InterviewBean interview = null;
        AbstractList<InterviewBean> interviews = new ArrayList<>();
        Vector<DepartmentBean> vS2child = null;
        Vector<DepartmentBean> vS3child = null;
        Vector<DepartmentBean> vS4child = null;
        AbstractList<ProcessBean> vMchild = null;
        AbstractList<ProcessBean> vPchild = null;
        // Resource 'con' implements AutoCloseable interface, so we can use try-with-resource...
        try (Connection con = prol_manager.getConnection()) {
            // TODO: Controllare se user è superuser
            try {
                // Prepara l'estrazione delle interviste, da arricchire in dettagli
                pst = con.prepareStatement(GET_INTERVIEWS);
                pst.clearParameters();
                pst.setInt(1, survey.getId());
                pst.setInt(2, survey.getId());
                rs = pst.executeQuery();
                while (rs.next()) {
                    // Crea una intervista raffinata vuota
                    interview = new InterviewBean();
                    // Crea una intervista grezza vuota
                    item = new ItemBean();
                    // Valorizza quest'ultima col contenuto della query
                    BeanUtil.populate(item, rs);
                    /* === Ricerca della struttura intervistata === */
                    String queryL1 = getQueryStructureBySurvey(survey.getId(), BEAN_DEFAULT_ID, BEAN_DEFAULT_ID, BEAN_DEFAULT_ID, item.getCod1());
                    // Controlla la significatività della query
                    if (!queryL1.equals(DASH)) {
                        // Cerca gli estremi della struttura di I livello
                        pst = null;
                        pst = con.prepareStatement(queryL1);
                        pst.clearParameters();
                        rs1 = pst.executeQuery();
                        // Punta alla struttura L1
                        if (rs1.next()) {
                            // Prepara la struttura L1
                            DepartmentBean s1 = new DepartmentBean();
                            // Valorizza la struttura L1
                            BeanUtil.populate(s1, rs1);
                            // Ne cerca i figli
                            vS2child = new Vector<>();
                            pst = null;
                            pst = con.prepareStatement(getQueryStructureBySurvey(survey.getId(), BEAN_DEFAULT_ID, BEAN_DEFAULT_ID, item.getCod2(), item.getCod1()));
                            pst.clearParameters();
                            rs2 = pst.executeQuery();
                            // Punta alla struttura L2
                            if (rs2.next()) {
                                // Prepara la struttura L2
                                DepartmentBean s2 = new DepartmentBean();
                                // Valorizza la struttura L2
                                BeanUtil.populate(s2, rs2);
                                // Ne cerca i figli
                                vS3child = new Vector<>();
                                pst = null;
                                pst = con.prepareStatement(getQueryStructureBySurvey(survey.getId(), BEAN_DEFAULT_ID, item.getCod3(), item.getCod2(), item.getCod1()));
                                pst.clearParameters();
                                rs3 = pst.executeQuery();
                                // Punta alla struttura L3
                                if (rs3.next()) {
                                    // Prepara la struttura L3
                                    DepartmentBean s3 = new DepartmentBean();
                                    // Valorizza la struttura L3
                                    BeanUtil.populate(s3, rs3);
                                    // Ne cerca i figli 
                                    vS4child = new Vector<>();
                                    pst = null;
                                    pst = con.prepareStatement(getQueryStructureBySurvey(survey.getId(), item.getCod4(), item.getCod3(), item.getCod2(), item.getCod1()));
                                    pst.clearParameters();
                                    rs4 = pst.executeQuery();
                                    // Punta alla struttura L4
                                    if (rs4.next()) {
                                        // Prepara la struttura L4
                                        DepartmentBean s4 = new DepartmentBean();
                                        // Valorizza la struttura L4
                                        BeanUtil.populate(s4, rs4);
                                        // Valorizza il padre di L4
                                        s4.setPadre(s3);
                                        // Aggiunge L4 alla lista di figlie di L3
                                        vS4child.add(s4);
                                        // Aggiunge le figlie (sempre una sola) a L3
                                        s3.setFiglie(vS4child);
                                    }
                                    // Valorizza il padre di L3
                                    s3.setPadre(s2);
                                    // Aggiunge L3 alla lista di figlie di L2
                                    vS3child.add(s3);
                                    // Aggiunge le figlie (sempre una sola) a L2
                                    s2.setFiglie(vS3child);
                                }
                                // Valorizza il padre di L2
                                s2.setPadre(s1);
                                // Aggiunge L2 alla lista di figlie di L1
                                vS2child.add(s2);
                                // Aggiunge le figlie (sempre una sola) a L1
                                s1.setFiglie(vS2child);
                            }
                            interview.setStruttura(s1);
                        }
                        // Che sia presente una struttura L1 è un'assunzione forte, ma non eccessiva
                    }
                    /* === Ricerca del processo anticorruttivo sondato === */
                    // Casting from float to int
                    int idM = (int) item.getValue1();
                    int idP = (int) item.getValue2();
                    int idS = (int) item.getValue3();
                    // MAT means "Macroprocesso Anticorruzione Trasparenza"
                    String queryMAT = getQueryProcessBySurvey(survey.getId(), BEAN_DEFAULT_ID, BEAN_DEFAULT_ID, idM);
                    // Controlla la significatività della query
                    if (!queryMAT.equals(DASH)) {
                        // Cerca gli estremi del macroprocesso
                        pst = null;
                        pst = con.prepareStatement(queryMAT);
                        pst.clearParameters();
                        rs5 = pst.executeQuery();
                        // Punta al macroprocesso censito a fini anticorruttivi
                        if (rs5.next()) {
                            // Prepara il macroprocesso
                            ProcessBean m = new ProcessBean();
                            // Valorizza il macroprocesso
                            BeanUtil.populate(m, rs5);
                            // Ne cerca i figli
                            vMchild = new Vector<>();
                            pst = null;
                            pst = con.prepareStatement(getQueryProcessBySurvey(survey.getId(), BEAN_DEFAULT_ID, idP, idM));
                            pst.clearParameters();
                            rs6 = pst.executeQuery();
                            // Punta al processo censito a fini anticorruttivi
                            if (rs6.next()) {
                                // Prepara il processo
                                ProcessBean p = new ProcessBean();
                                // Valorizza il processo
                                BeanUtil.populate(p, rs6);
                                // Ne cerca i figli
                                vPchild = new Vector<>();
                                pst = null;
                                pst = con.prepareStatement(getQueryProcessBySurvey(survey.getId(), idS, idP, idM));
                                pst.clearParameters();
                                rs7 = pst.executeQuery();
                                // Punta al sottoprocesso censito a fini anticorruttivi
                                if (rs7.next()) {
                                    // Prepara il sottoprocesso
                                    ProcessBean s = new ProcessBean();
                                    // Valorizza il sottoprocesso
                                    BeanUtil.populate(s, rs7);
                                    // Aggiunge il sottoprocesso alla lista di figlie del processo
                                    vPchild.add(s);
                                    // Aggiunge le figlie (sempre una sola) al processo
                                    p.setProcessi(vPchild);
                                }
                                // Aggiunge il processo alla lista di figlie del macroprocesso
                                vMchild.add(p);
                                // Aggiunge le figlie (sempre una sola) al macroprocesso
                                m.setProcessi(vMchild);
                            }
                            interview.setProcesso(m);
                        }
                        // Che sia presente un macroprocesso è un'assunzione forte, ma non eccessiva
                    }
                    // Propaga data di ultima modifica
                    interview.setDataUltimaModifica(Utils.format(item.getCodice(), DATA_SQL_PATTERN, DATA_SQL_PATTERN));
                    // Propaga ora di ultima modifica
                    Time t = Utils.format(item.getExtraInfo(), TIME_SQL_PATTERN);
                    interview.setOraUltimaModifica(t);
                    // Propaga il codice rilevazione nel contesto della quale è stata fatta l'intervista
                    interview.setDescrizione(item.getLabelWeb());
                    // Aggiunge l'intervista valorizzata alla lista di interviste trovate
                    interviews.add(interview);
                }
                // Closes the statement
                pst.close();
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get out
                return (ArrayList<InterviewBean>) interviews;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Attributo obbligatorio non recuperabile; problema nel metodo di estrazione delle risposte.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (CommandException ce) {
                String msg = FOR_NAME + "Probabile problema nel metodo di utilita\' di conversione delle date.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + ce.getMessage(), ce);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query SQL o nella chiusura dello statement.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally { // ...still...
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce un ArrayList contenente tutti i rischi corruttivi
     * censiti nel contesto di una data rilevazione, oppure tutti i rischi
     * corruttivi trovati indipendentemente dalla rilevazione, in funzione del
     * valore dei parametri.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param getAll    valore convenzionale; se vale -1 comporta il recupero di tutti i rischi, indipendentemente dall'id della rilevazione
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>ArrayList&lt;RiskBean&gt;</code> - un vettore ordinato di RiskBean, che rappresentano i rischi corruttivi trovati
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<RiskBean> getRisks(PersonBean user, 
                                        int getAll, 
                                        CodeBean survey)
                                 throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            AbstractList<RiskBean> risks = new ArrayList<>();
            int nextParam = NOTHING;
            try {
                // TODO: Controllare se user è superuser
                pst = con.prepareStatement(GET_RISKS);
                pst.clearParameters();
                pst.setInt(++nextParam, survey.getId());
                pst.setInt(++nextParam, getAll);
                rs = pst.executeQuery();
                // Punta al rischio
                while (rs.next()) {
                    // Prepara il rischio
                    RiskBean risk = new RiskBean();
                    // Valorizza il rischio
                    BeanUtil.populate(risk, rs);
                    // Aggiunge il rischio alla lista dei rischi
                    risks.add(risk);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get out
                return (ArrayList<RiskBean>) risks;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Attributo obbligatorio non recuperabile; problema nel metodo di estrazione dei rischi.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "RiskBean non valorizzato; problema nella query dei rischi.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce un oggetto corrispondente ad un rischio corruttivo
     * censito nel contesto di una data rilevazione, i cui identificativi
     * accetta come argomenti.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idRisk    identificativo del rischio corruttivo 
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>RiskBean</code> - un RiskBean, che rappresenta il rischio corruttivo trovato
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    public RiskBean getRisk(PersonBean user, 
                            int idRisk, 
                            CodeBean survey)
                     throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            RiskBean risk = null;
            int nextParam = NOTHING;
            try {
                // TODO: Controllare se user è superuser
                pst = con.prepareStatement(GET_RISK);
                pst.clearParameters();
                pst.setInt(++nextParam, idRisk);
                pst.setInt(++nextParam, survey.getId());
                rs = pst.executeQuery();
                // Punta al rischio
                if (rs.next()) {
                    // Prepara il rischio
                    risk = new RiskBean();
                    // Valorizza il rischio
                    BeanUtil.populate(risk, rs);
                    // Recupera i processi del rischio
                    risk.setProcessi(getProcessByRisk(user, risk, survey));
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get out
                return risk;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Attributo obbligatorio non recuperabile; problema nel metodo di estrazione del singolo rischio.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "RiskBean non valorizzato; problema nella query del singolo rischio.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce un ArrayList contenente tutti i rischi corruttivi
     * collegati ad un dato processo o sottoprocesso e censiti nel contesto 
     * di una data rilevazione.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param process   oggetto contenente i dati del processo o del sottoprocesso
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>ArrayList&lt;RiskBean&gt;</code> - un vettore ordinato di RiskBean, che rappresentano i rischi corruttivi trovati per il sotto/processo dato
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<RiskBean> getRisksByProcess(PersonBean user, 
                                                 ProcessBean process, 
                                                 CodeBean survey)
                                          throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs, rs1 = null;
            AbstractList<RiskBean> risks = new ArrayList<>();
            AbstractList<CodeBean> factors = null;
            int nextParam = NOTHING;
            try {
                // TODO: Controllare se user è superuser
                String query = (process.getLivello() == ELEMENT_LEV_2 ? GET_RISK_BY_PROCESS : GET_RISK_BY_SUB);
                pst = con.prepareStatement(query);
                pst.clearParameters();
                pst.setInt(++nextParam, process.getId());
                pst.setInt(++nextParam, survey.getId());
                rs = pst.executeQuery();
                // Punta al rischio
                while (rs.next()) {
                    // Prepara il rischio
                    RiskBean risk = new RiskBean();
                    // Valorizza il rischio
                    BeanUtil.populate(risk, rs);
                    // Recupera i fattori abilitanti collegati al rischio corrente
                    nextParam = NOTHING;
                    pst = null;
                    // TODO: Gestire il caso dei sottoprocessi (al momento non ci sono)
                    pst = con.prepareStatement(GET_FACTORS_BY_RISK_AND_PROCESS);
                    pst.clearParameters();
                    pst.setInt(++nextParam, process.getId());
                    pst.setInt(++nextParam, risk.getId());
                    pst.setInt(++nextParam, survey.getId());
                    rs1 = pst.executeQuery();
                    // Prepara lista di fattori per il rischio corrente
                    factors = new ArrayList<>();
                    // Carica la lista
                    while (rs1.next()) {
                        // Prepara il fattore
                        CodeBean fat = new CodeBean();
                        // Valorizza il fattore
                        BeanUtil.populate(fat, rs1);
                        // Aggiunge il fattore alla lista
                        factors.add(fat);
                    }
                    // Aggiunge la lista di fattori al rischio
                    risk.setFattori(factors);
                    // Aggiunge il rischio alla lista dei rischi
                    risks.add(risk);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get out
                return (ArrayList<RiskBean>) risks;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Attributo obbligatorio non recuperabile; problema nel metodo di estrazione dei rischi per processi.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "RiskBean non valorizzato; problema nella query dei rischi.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce un ArrayList contenente tutti i processi (livello 2)
     * collegati ad un dato rischio corruttivo il cui identificativo viene
     * accettato come argomento, e censiti nel contesto di una data rilevazione.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param risk      oggetto contenente i dati del rischio
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>ArrayList&lt;RiskBean&gt;</code> - un vettore ordinato di RiskBean, che rappresentano i rischi corruttivi trovati per il sotto/processo dato
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<ProcessBean> getProcessByRisk(PersonBean user, 
                                                   RiskBean risk, 
                                                   CodeBean survey)
                                            throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs, rs1 = null;
            AbstractList<ProcessBean> pats = new ArrayList<>();
            int nextParam = NOTHING;
            try {
                // TODO: Controllare se user è superuser
                //String query = (process.getLivello() == ELEMENT_LEV_2 ? GET_PROCESS_BY_RISK : GET_SUB_BY_RISK);
                pst = con.prepareStatement(GET_PROCESS_BY_RISK);
                pst.clearParameters();
                pst.setInt(++nextParam, risk.getId());
                pst.setInt(++nextParam, survey.getId());
                rs = pst.executeQuery();
                // Punta al rischio
                while (rs.next()) {
                    // Prepara il processo
                    ProcessBean pat = new ProcessBean();
                    // Valorizza il processo
                    BeanUtil.populate(pat, rs);
                    /* == Recupera il macroprocesso_at padre == */
                    nextParam = NOTHING;
                    pst = null;
                    pst = con.prepareStatement(GET_MACRO_AT_BY_CHILD);
                    pst.clearParameters();
                    pst.setInt(++nextParam, pat.getId());
                    pst.setInt(++nextParam,survey.getId());
                    rs1 = pst.executeQuery();
                    // Punta al padre (se c'è, è solo uno!)
                    if (rs1.next()) {
                        // Prepara il padre
                        ProcessBean mat = new ProcessBean();
                        // Valorizza il padre
                        BeanUtil.populate(mat, rs1);
                        // Aggiunge il padre al figlio
                        pat.setPadre(mat);
                    }
                    // Aggiunge il processo alla lista dei processi trovati
                    pats.add(pat);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get out
                return (ArrayList<ProcessBean>) pats;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Attributo obbligatorio non recuperabile; problema nel metodo di estrazione dei processi appartenenti a rischio.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "ProcessBean non valorizzato; problema nella query dei processi di rischio.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce il numero di processi o sottoprocessi associati ad un
     * dato rischio corruttivo, oppure zero se non ce ne sono.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param params    mappa contenente i parametri di navigazione
     * @return <code>int</code> - il totale delle associazioni processo corrente/rischio corrente
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public int getRiskProcess(PersonBean user, 
                              HashMap<String, LinkedHashMap<String, String>> params)
                       throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            // Variabili per l'accesso ai dati
            PreparedStatement pst = null;
            ResultSet rs = null;
            int nextParam = NOTHING;
            int count = NOTHING;
            // Dizionario dei parametri contenente il codice della rilevazione
            LinkedHashMap<String, String> survey = params.get(PARAM_SURVEY);
            // Dizionario dei parametri dei processi scelti dall'utente per l'associazione al rischio
            LinkedHashMap<String, String> proc = params.get(PART_PROCESS);
            // Dizionario dei parametri contenente l'identificativo del rischio da associare
            LinkedHashMap<String, String> risk = params.get(PART_INSERT_RISK_PROCESS);
            try {
                // TODO: Controllare se user è superuser
                String query = (!proc.get("liv3").equals(VOID_STRING) ? GET_RISK_SUBPROCESS : GET_RISK_PROCESS);
                String procCode = (!proc.get("liv3").equals(VOID_STRING) ? proc.get("liv3") : proc.get("liv2"));
                String procId = procCode.substring(NOTHING, procCode.indexOf(DOT)); 
                pst = con.prepareStatement(query);
                pst.clearParameters();
                pst.setInt(++nextParam, Integer.parseInt(procId));
                pst.setInt(++nextParam, Integer.parseInt(risk.get("risk")));
                pst.setInt(++nextParam, Integer.parseInt(survey.get(PARAM_SURVEY)));
                rs = pst.executeQuery();
                if (rs.next()) {
                    count = rs.getInt(1);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get out
                return count;
            } catch (NumberFormatException nfe) {
                String msg = FOR_NAME + "Si e\' verificato un problema nella conversione di interi.\n" + nfe.getMessage();
                LOG.severe(msg);
                throw new WebStorageException(msg, nfe);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "ProcessBean non valorizzato; problema nella query dei processi di rischio.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce un ArrayList contenente tutti i fattori abilitanti
     * censiti indipendentemente dalla rilevazione.
     * Quest'ultimo parametro, che &egrave; "gratis", viene passato 
     * "just in case", qualora si decidesse, in secondo tempo, di storicizzare 
     * quest'entit&agrave; forte <code>(fattore_abilitante)</code>.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>ArrayList&lt;CodeBean&gt;</code> - un vettore ordinato di CodeBean, che rappresentano i fattori abilitanti trovati
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<CodeBean> getFactors(PersonBean user, 
                                          CodeBean survey)
                                   throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            AbstractList<CodeBean> factors = new ArrayList<>();
            try {
                // TODO: Controllare se user è superuser
                pst = con.prepareStatement(GET_FACTORS);
                pst.clearParameters();
                rs = pst.executeQuery();
                // Punta al fattore
                while (rs.next()) {
                    // Prepara il fattore
                    CodeBean fat = new CodeBean();
                    // Valorizza il fattore
                    BeanUtil.populate(fat, rs);
                    // Aggiunge il fattore alla lista dei fattori
                    factors.add(fat);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get out
                return (ArrayList<CodeBean>) factors;
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "RiskBean non valorizzato; problema nella query dei rischi.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    /* ********************************************************** *
     *                    Metodi di INSERIMENTO                   *
     * ********************************************************** */
    
    /**
     * <p>Metodo per fare l'inserimento di un nuovo questionario.</p>
     *
     * @param user      utente loggato
     * @param params    mappa contenente i parametri di navigazione
     * @param items     numero di quesiti a cui rispondere e quindi numero di risposte da inserire
     * @throws WebStorageException se si verifica un problema nel cast da String a Date, nell'esecuzione della query, nell'accesso al db o in qualche puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public void insertAnswers(PersonBean user, 
                              HashMap<String, LinkedHashMap<String, String>> params,
                              int items) 
                       throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        // Dizionario dei parametri contenente il codice della rilevazione
        LinkedHashMap<String, String> survey = params.get(PARAM_SURVEY);
        // Dizionario dei parametri delle strutture scelte dall'utente
        LinkedHashMap<String, String> struct = params.get(PART_SELECT_STR);
        // Dizionario dei parametri dei processi scelti dall'utente
        LinkedHashMap<String, String> proc = params.get(PART_PROCESS);
        // Dizionario dei parametri delle risposte date dall'utente
        LinkedHashMap<String, String> quest = params.get(PART_SELECT_QST);
        try {
            // Effettua la connessione
            con = prol_manager.getConnection();
            // Begin: ==>
            con.setAutoCommit(false);
            // TODO: Controllare se user è superuser
            /* === Se siamo qui vuol dire che ok   === */ 
            // Definisce un indice corrispondente al progressivo del quesito
            int index = NOTHING;
            while (index < items) {              
                pst = con.prepareStatement(INSERT_ANSWER);
                pst.clearParameters();
                 // Prepara i parametri per l'inserimento
                try {
                    // Definisce un indice per il numero di parametro da passare alla query
                    int nextParam = NOTHING;
                    /* === Valore === */
                    pst.setString(++nextParam, quest.get("risp" + index));
                    /* === Note === */
                    String descr = null;
                    if (!quest.get("note" + index).equals(VOID_STRING)) {
                        descr = new String(quest.get("note" + index));
                        pst.setString(++nextParam, descr);
                    } else {
                        // Dato facoltativo non inserito
                        pst.setNull(++nextParam, Types.NULL);
                    }
                    /* === Ordinale === */
                    pst.setInt(++nextParam, 10);
                    /* === Campi automatici: id utente, ora ultima modifica, data ultima modifica === */
                    pst.setDate(++nextParam, Utils.convert(Utils.convert(Utils.getCurrentDate()))); // non accetta un GregorianCalendar né una data java.util.Date, ma java.sql.Date
                    pst.setTime(++nextParam, Utils.getCurrentTime());   // non accetta una Stringa, ma un oggetto java.sql.Time
                    pst.setInt(++nextParam, user.getUsrId());
                    /* === Riferimento a quesito === */
                    pst.setInt(++nextParam, Integer.parseInt(quest.get("quid" + index)));
                    /* === Collegamento a rilevazione === */
                    pst.setInt(++nextParam, Integer.parseInt(survey.get(PARAM_SURVEY)));
                    /* === Collegamento a struttura_liv1 === */
                    if (!struct.get("liv1").equals(VOID_STRING)) {
                        String idAsString = struct.get("liv1").substring(struct.get("liv1").indexOf('.') + 1,struct.get("liv1").indexOf('-'));
                        int id_struttura_liv1 = Integer.parseInt(idAsString);
                        pst.setInt(++nextParam, id_struttura_liv1);
                    } else {
                        pst.setNull(++nextParam, Types.NULL);
                    }
                    /* === Collegamento a struttura_liv2 === */
                    if (!struct.get("liv2").equals(VOID_STRING)) {
                        String idAsString = struct.get("liv2").substring(struct.get("liv2").indexOf('.') + 1,struct.get("liv2").indexOf('-'));
                        int id_struttura_liv2 = Integer.parseInt(idAsString);
                        pst.setInt(++nextParam, id_struttura_liv2);
                    } else {
                        pst.setNull(++nextParam, Types.NULL);
                    }
                    /* === Collegamento a struttura_liv3 === */
                    if (!struct.get("liv3").equals(VOID_STRING)) {
                        String idAsString = struct.get("liv3").substring(struct.get("liv3").indexOf('.') + 1,struct.get("liv3").indexOf('-'));
                        int id_struttura_liv3 = Integer.parseInt(idAsString);
                        pst.setInt(++nextParam, id_struttura_liv3);
                    } else {
                        pst.setNull(++nextParam, Types.NULL);
                    }
                    /* === Collegamento a struttura_liv4 === */
                    if (!struct.get("liv4").equals(VOID_STRING)) {
                        String idAsString = struct.get("liv4").substring(struct.get("liv4").indexOf('.') + 1,struct.get("liv4").indexOf('-'));
                        int id_struttura_liv4 = Integer.parseInt(idAsString);
                        pst.setInt(++nextParam, id_struttura_liv4);
                    } else {
                        pst.setNull(++nextParam, Types.NULL);
                    }
                    /* === Collegamento a macroprocesso_at === */
                    if (!proc.get("liv1").equals(VOID_STRING)) {
                        String idAsString = proc.get("liv1").substring(NOTHING,proc.get("liv1").indexOf('.'));
                        int id_macroprocesso_at = Integer.parseInt(idAsString);
                        pst.setInt(++nextParam, id_macroprocesso_at);
                    } else {
                        pst.setNull(++nextParam, Types.NULL);
                    }
                    /* === Collegamento a processo_at === */
                    if (!proc.get("liv2").equals(VOID_STRING)) {
                        String idAsString = proc.get("liv2").substring(NOTHING,proc.get("liv2").indexOf('.'));
                        int id_processo_at = Integer.parseInt(idAsString);
                        pst.setInt(++nextParam, id_processo_at);
                    } else {
                        pst.setNull(++nextParam, Types.NULL);
                    }
                    /* === Collegamento a sottoprocesso_at === */
                    if (!proc.get("liv3").equals(VOID_STRING)) {
                        String idAsString = proc.get("liv3").substring(NOTHING,proc.get("liv3").indexOf('.'));
                        int id_subprocesso_at = Integer.parseInt(idAsString);
                        pst.setInt(++nextParam, id_subprocesso_at);
                    } else {
                        pst.setNull(++nextParam, Types.NULL);
                    }
                    pst.executeUpdate();
                } catch (NumberFormatException nfe) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nella conversione di interi.\n" + nfe.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, nfe);
                } catch (ClassCastException cce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nella conversione di tipo.\n" + cce.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, cce);
                } catch (ArrayIndexOutOfBoundsException aiobe) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nello scorrimento di liste.\n" + aiobe.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, aiobe);
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Si e\' verificato un problema in un puntamento a null.\n" + npe.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, npe);
                } catch (Exception e) {
                    String msg = FOR_NAME + "Si e\' verificato un problema.\n" + e.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, e);
                }
                // I progressivi partono da zero, quindi si ha postincremento finale
                index++;
            }
            // End: <==
            con.commit();
            pst.close();
            pst = null;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema nel codice SQL o nella chiusura dello statement.\n";
            LOG.severe(msg); 
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg); 
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }

    
    /**
     * <p>Metodo per fare l'inserimento di un nuovo rischio.</p>
     *
     * @param user      utente loggato
     * @param params    mappa contenente i parametri di navigazione
     * @throws WebStorageException se si verifica un problema nel cast da String a Date, nell'esecuzione della query, nell'accesso al db o in qualche puntamento
     */
    public void insertRisk(PersonBean user, 
                           HashMap<String, LinkedHashMap<String, String>> params) 
                    throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            // Dizionario dei parametri contenente il codice della rilevazione
            LinkedHashMap<String, String> survey = params.get(PARAM_SURVEY);
            // Dizionario dei parametri contenente gli estremi del rischio da inserire
            LinkedHashMap<String, String> risk = params.get(PART_INSERT_RISK);
            try {
                // Calcola il codice da inserire
                String code = getMaxRiskCode("USR");
                // Begin: ==>
                con.setAutoCommit(false);
                // TODO: Controllare se user è superuser
                /* === Se siamo qui vuol dire che ok   === */ 
                pst = con.prepareStatement(INSERT_RISK);
                pst.clearParameters();
                 // Prepara i parametri per l'inserimento
                try {
                    // Definisce un indice per il numero di parametro da passare alla query
                    int nextParam = NOTHING;
                    // Ottiene l'id
                    int maxRiskId = getMax("rischio_corruttivo");
                    /* === Id === */
                    pst.setInt(++nextParam, ++maxRiskId);
                    /* === Codice === */
                    pst.setString(++nextParam, code);
                    /* === Nome === */
                    pst.setString(++nextParam, risk.get("risk"));
                    /* === Descrizione === */
                    String descr = null;
                    if (!risk.get("desc").equals(VOID_STRING)) {
                        descr = new String(risk.get("desc"));
                        pst.setString(++nextParam, descr);
                    } else {
                        // Dato facoltativo non inserito
                        pst.setNull(++nextParam, Types.NULL);
                    }
                    /* === Ordinale === */
                    pst.setInt(++nextParam, 110);
                    /* === Campi automatici: id utente, ora ultima modifica, data ultima modifica === */
                    pst.setDate(++nextParam, Utils.convert(Utils.convert(Utils.getCurrentDate()))); // non accetta un GregorianCalendar né una data java.util.Date, ma java.sql.Date
                    pst.setTime(++nextParam, Utils.getCurrentTime());   // non accetta una Stringa, ma un oggetto java.sql.Time
                    pst.setInt(++nextParam, user.getUsrId());
                    /* === Collegamento a rilevazione === */
                    pst.setInt(++nextParam, Integer.parseInt(survey.get(PARAM_SURVEY)));
                    // CR (Carriage Return) o 0DH
                    pst.executeUpdate();
                } catch (NumberFormatException nfe) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nella conversione di interi.\n" + nfe.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, nfe);
                } catch (ClassCastException cce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nella conversione di tipo.\n" + cce.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, cce);
                } catch (ArrayIndexOutOfBoundsException aiobe) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nello scorrimento di liste.\n" + aiobe.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, aiobe);
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Si e\' verificato un problema in un puntamento a null.\n" + npe.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, npe);
                } catch (Exception e) {
                    String msg = FOR_NAME + "Si e\' verificato un problema.\n" + e.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, e);
                }
                // End: <==
                con.commit();
                pst.close();
                pst = null;
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Problema nel codice SQL o nella chiusura dello statement.\n";
                LOG.severe(msg); 
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg); 
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Metodo per fare l'inserimento di una nuova associazione
     * tra un rischio ed un sotto/processo, entrambi esistenti.</p>
     *
     * @param user      utente loggato
     * @param params    mappa contenente i parametri di navigazione
     * @throws WebStorageException se si verifica un problema nel cast da String a Date, nell'esecuzione della query, nell'accesso al db o in qualche puntamento
     */
    @SuppressWarnings("static-method")
    public void insertRiskProcess(PersonBean user, 
                                  HashMap<String, LinkedHashMap<String, String>> params) 
                           throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            // Dizionario dei parametri contenente il codice della rilevazione
            LinkedHashMap<String, String> survey = params.get(PARAM_SURVEY);
            // Dizionario dei parametri dei processi scelti dall'utente per l'associazione al rischio
            LinkedHashMap<String, String> proc = params.get(PART_PROCESS);
            // Dizionario dei parametri contenente l'identificativo del rischio da associare
            LinkedHashMap<String, String> risk = params.get(PART_INSERT_RISK_PROCESS);
            try {
                // Begin: ==>
                con.setAutoCommit(false);
                // TODO: Controllare se user è superuser
                // Se è presente id sottoprocesso deve associare quello, altrimenti deve associare un processo
                String query = (!proc.get("liv3").equals(VOID_STRING) ? INSERT_RISK_SUBPROCESS : INSERT_RISK_PROCESS);
                String procCode = (!proc.get("liv3").equals(VOID_STRING) ? proc.get("liv3") : proc.get("liv2"));
                String procId = procCode.substring(NOTHING, procCode.indexOf(DOT)); 
                pst = con.prepareStatement(query);
                pst.clearParameters();
                 // Prepara i parametri per l'inserimento
                try {
                    // Definisce un indice per il numero di parametro da passare alla query
                    int nextParam = NOTHING;
                    /* === Campi automatici: id utente, ora ultima modifica, data ultima modifica === */
                    pst.setDate(++nextParam, Utils.convert(Utils.convert(Utils.getCurrentDate()))); // non accetta un GregorianCalendar né una data java.util.Date, ma java.sql.Date
                    pst.setTime(++nextParam, Utils.getCurrentTime());   // non accetta una Stringa, ma un oggetto java.sql.Time
                    pst.setInt(++nextParam, user.getUsrId());
                    /* === Id Sotto/Processo === */
                    pst.setInt(++nextParam, Integer.parseInt(procId));
                    /* === Id Rischio === */
                    pst.setInt(++nextParam, Integer.parseInt(risk.get("risk")));
                    /* === Collegamento a rilevazione === */
                    pst.setInt(++nextParam, Integer.parseInt(survey.get(PARAM_SURVEY)));
                    // CR (Carriage Return) o 0DH
                    pst.executeUpdate();
                } catch (NumberFormatException nfe) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nella conversione di interi.\n" + nfe.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, nfe);
                } catch (ClassCastException cce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nella conversione di tipo.\n" + cce.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, cce);
                } catch (ArrayIndexOutOfBoundsException aiobe) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nello scorrimento di liste.\n" + aiobe.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, aiobe);
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Si e\' verificato un problema in un puntamento a null.\n" + npe.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, npe);
                } catch (Exception e) {
                    String msg = FOR_NAME + "Si e\' verificato un problema.\n" + e.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, e);
                }
                // End: <==
                con.commit();
                pst.close();
                pst = null;
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Problema nel codice SQL o nella chiusura dello statement.\n";
                LOG.severe(msg); 
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg); 
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    /* ********************************************************** *
     *                  Metodi di AGGIORNAMENTO                   *
     * ********************************************************** */
    
    /**
     * <p>Metodo per fare l'aggiornamento dei valori di una risposta data.</p>
     * <p>Essendo un'intervista identificata dall'insieme delle risposte
     * date, le quali a loro volta sono raggruppate in base a una specifica
     * data ed una specifica ora, non &egrave; possibile inserire nella risposta
     * una nuova data contestuale dal timestamp dell'aggiornamento.<br />
     * Come sviluppo evolutivo, si consiglia di aggiungere una nuova coppia
     * di campi 'data e ora' per memorizzare la data ed ora di effettivo primo
     * inserimento, e riservare i campi "data_ultima_modifica"  ed
     * "ora ultima modifica" effettivamente allo scopo per cui sono
     * nominati, ovvero per contenere data e ora dell'aggiornamento,
     * non del primo inserimento; occorrer&agrave; poi un porting
     * sui dati per travasare le informazioni attualmente contenute
     * nei due campi "data_ultima_modifica"  ed  "ora ultima modifica" 
     * nei nuovi campi "data inserimento" ed "ora inserimento" e un 
     * refactoring sul codice dell'applicazione.</p> 
     *
     * @param user      utente loggato
     * @param params    mappa contenente i parametri di navigazione
     * @throws WebStorageException se si verifica un problema nel cast da String a Date, nell'esecuzione della query, nell'accesso al db o in qualche puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public void updateAnswer(PersonBean user, 
                             HashMap<String, LinkedHashMap<String, String>> params) 
                      throws WebStorageException {
        try (Connection con = prol_manager.getConnection()) {
            PreparedStatement pst = null;
            // Dizionario dei parametri contenente il codice della rilevazione
            LinkedHashMap<String, String> survey = params.get(PARAM_SURVEY);
            // Dizionario dei parametri della risposta da aggiornare
            LinkedHashMap<String, String> quest = params.get(PART_RESUME_QST);
            try {
                // Begin: ==>
                con.setAutoCommit(false);
                // TODO: Controllare se user è superuser
                /* === Se siamo qui vuol dire che ok   === */ 
                // Recupera l'identificativo del quesito
                int idQ = new Integer(quest.get("quid")).intValue();
                // Recupera la data originale della risposta
                Date questDate = Utils.format(survey.get("d"));
                // Recupera l'ora originale della risposta
                String questTimeAsString = params.get(PARAM_SURVEY).get("t").replaceAll("_", ":");
                Time questTime = Utils.format(questTimeAsString, TIME_SQL_PATTERN);
                // Controllo sull'input
                if (idQ > NOTHING) {
                    // Prepara la query
                    pst = con.prepareStatement(UPDATE_ANSWER);
                    // Prepara i parametri per l'inserimento
                    pst.clearParameters();
                    // Definisce l'indice del parametro da passare
                    int nextParam = NOTHING;
                    /* === Valore === */
                    pst.setString(++nextParam, quest.get("risp"));
                    /* === Note === */
                    pst.setString(++nextParam, quest.get("note"));
                    /* === Campi automatici: id utente, ora ultima modifica, data ultima modifica === *
                    pst.setDate(++nextParam, Utils.convert(Utils.convert(Utils.getCurrentDate()))); // non accetta un GregorianCalendar né una data java.util.Date, ma java.sql.Date
                    pst.setTime(++nextParam, Utils.getCurrentTime());   // non accetta una Stringa, ma un oggetto java.sql.Time
                    pst.setInt(++nextParam, user.getUsrId());
                    /* === Riferimento a quesito === */
                    pst.setInt(++nextParam, idQ);
                    /* === Collegamento a rilevazione === */
                    pst.setInt(++nextParam, Integer.parseInt(survey.get(PARAM_SURVEY)));
                    /* === Data originale === */
                    pst.setDate(++nextParam, Utils.convert(questDate)); // non accetta una String né una data java.util.Date, ma java.sql.Date
                    /* === Ora originale === */
                    pst.setTime(++nextParam, questTime);   // non accetta una String, ma un oggetto java.sql.Time
                    // Esecuzione
                    pst.executeUpdate();
                }
                // End: <==
                con.commit();
                pst.close();
                pst = null;
            } catch (NumberFormatException nfe) {
                String msg = FOR_NAME + "Si e\' verificato un problema nella conversione di interi.\n" + nfe.getMessage();
                LOG.severe(msg);
                throw new WebStorageException(msg, nfe);
            } catch (ArrayIndexOutOfBoundsException aiobe) {
                String msg = FOR_NAME + "Si e\' verificato un problema nello scorrimento di liste.\n" + aiobe.getMessage();
                LOG.severe(msg);
                throw new WebStorageException(msg, aiobe);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Problema nel codice SQL o nella chiusura dello statement.\n";
                LOG.severe(msg); 
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg); 
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Si e\' verificato un problema nella conversione di tipo.\n" + ce.getMessage();
            LOG.severe(msg);
            throw new WebStorageException(msg, ce);
        } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Si e\' verificato un problema in un puntamento a null.\n" + npe.getMessage();
                LOG.severe(msg);
                throw new WebStorageException(msg, npe);
        } catch (Exception e) {
                String msg = FOR_NAME + "Si e\' verificato un problema.\n" + e.getMessage();
                LOG.severe(msg);
                throw new WebStorageException(msg, e);
        }
    }

}
