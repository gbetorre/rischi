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

package it.rol.command;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.DBWrapper;
import it.rol.Main;
import it.rol.bean.CodeBean;
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.CommandException;
import it.rol.exception.WebStorageException;


/**
 * <p><code>PersonCommand.java</code><br>
 * Implementa la logica per la gestione delle persone collegate ai processi on line (PROL).</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class PersonCommand extends ItemBean implements Command, Constants {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = 4885941293740116980L;
    /**
     *  Nome di questa classe
     *  (utilizzato per contestualizzare i messaggi di errore)
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     * Log per debug in produzione
     */
    protected static Logger LOG = Logger.getLogger(Main.class.getName());
    /**
     * Pagina a cui la command reindirizza per mostrare la pagina iniziale relativa alla funzione persone
     */
    private static final String nomeFileElenco = "/jsp/peElenco.jsp";
    /**
     * Pagina a cui la command reindirizza per mostrare i dettagli basati una ricerca per id
     */
    private static final String nomeFilePersona = "/jsp/peDettaglio.jsp";
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutti gli attributi del progetto
     */
    private static final HashMap<String, String> nomeFile = new HashMap<String, String>();
    /**
     *  Processo di dato id
     */
    ProcessBean runtimeProcess = null;


    /**
     * Crea una nuova istanza di questa Command
     */
    public PersonCommand() {
        /*;*/   // It Doesn't Anything
    }


    /**
     * <p>Raccoglie i valori dell'oggetto ItemBean
     * e li passa a questa classe command.</p>
	 *
	 * @param voceMenu la VoceMenuBean pari alla Command presente.
	 * @throws it.rol.exception.CommandException se l'attributo paginaJsp di questa command non e' stato valorizzato.
     */
    @Override
    public void init(ItemBean voceMenu) throws CommandException {
        this.setId(voceMenu.getId());
        this.setNome(voceMenu.getNome());
        this.setLabelWeb(voceMenu.getLabelWeb());
        this.setNomeClasse(voceMenu.getNomeClasse());
        this.setPaginaJsp(voceMenu.getPaginaJsp());
        this.setInformativa(voceMenu.getInformativa());
        if (this.getPaginaJsp() == null) {
          String msg = FOR_NAME + "La voce menu' " + this.getNome() + " non ha il campo paginaJsp. Impossibile visualizzare i risultati.\n";
          throw new CommandException(msg);
        }
        // Carica la hashmap contenente le pagine da includere in funzione dei parametri sulla querystring
        //nomeFile.put(PART_SEARCH_PERSON, nomeFileElenco);
        //nomeFile.put(Query.PART_PROJECT, this.getPaginaJsp());
    }


    /**
     * <p>Gestisce il flusso principale.</p>
     * <p>Prepara i bean.</p>
     * <p>Passa nella Request i valori che verranno utilizzati dall'applicazione.</p>
     *
     * @param req la HttpServletRequest contenente la richiesta del client
     * @throws CommandException se si verifica un problema, tipicamente nell'accesso a campi non accessibili o in qualche altro tipo di puntamento
     */
    @Override
    public void execute(HttpServletRequest req)
                 throws CommandException {
        /* ******************************************************************** *
         *           Crea e inizializza le variabili locali comuni              *
         * ******************************************************************** */
        // Databound
        DBWrapper db = null;
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        // Utente loggato
        PersonBean user = null;
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter(PARAM_SURVEY, DASH);
        // Recupera o inizializza 'id persona'
        int idPe = parser.getIntParameter("idp", DEFAULT_ID);
        // Recupera o inizializza 'tipo pagina'
        String part = parser.getStringParameter("p", DASH);
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Dichiara elenco di aree funzionali
        AbstractList<ItemBean> aree = new ArrayList<ItemBean>();
        // Dichiara elenco di ruoli giuridici
        AbstractList<ItemBean> qualifiche = new ArrayList<ItemBean>();
        // Dichiara elenco di persone in base a specifica ricerca
        AbstractList<PersonBean> people = null;
        // Dichiara persona
        PersonBean person = null;
        // Dichiara elenco di macroprocessi in base a specifica persona
        AbstractList<ProcessBean> mp = null;
        // Dichiara mappa di parametri di ricerca
        HashMap<String, LinkedHashMap<String, String>> params = null;
        // Preprara BreadCrumbs
        LinkedList<ItemBean> bC = null;
        /* ******************************************************************** *
         *      Instanzia nuova classe WebStorage per il recupero dei dati      *
         * ******************************************************************** */
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new CommandException(FOR_NAME + "Non e\' disponibile un collegamento al database\n." + wse.getMessage(), wse);
        }
        /* ******************************************************************** *
         *                         Recupera la Sessione                         *
         * ******************************************************************** */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            user = (PersonBean) ses.getAttribute("usr");
            if (user == null) {
                throw new CommandException(FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n");
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
        /* ******************************************************************** *
         *                   Decide il valore della pagina                      *
         * ******************************************************************** */
        try {
            // Il parametro di navigazione 'rilevazione' Ã¨ obbligatorio
            if (!codeSur.equals(DASH)) {
                // Recupero delle liste comuni
                aree = db.getAreeFunzionali(user);
                qualifiche = db.getRuoliGiuridici(user);
                // Il parametro di navigazione 'p' permette di addentrarsi nelle funzioni
                if (nomeFile.containsKey(part)) {
                    // Viene richiesta la visualizzazione di una persona specifica
                    if (idPe > 0) {
                        // Recupera il singolo individuo
                        person = db.getPerson(user, idPe, ConfigManager.getSurvey(codeSur));
                        // Ne recupera i macroprocessi, contenenti i processi al loro interno
                        mp = db.getMacroByPerson(user, person, ConfigManager.getSurvey(codeSur));
                        // Personalizza le breadcrumbs
                        bC = HomePageCommand.makeBreadCrumbs(ConfigManager.getAppName(), req.getQueryString(), "ExtraInfo");
                        // Imposta il valore della pagina personale
                        fileJspT = nomeFilePersona;
                    } else {    // Viene richiesta la visualizzazione di un elenco
                        // Valorizza la tabella che conterrÃ  i valori dei parametri passati dalla form
                        params = new HashMap<String, LinkedHashMap<String, String>>();
                        // Carica la tabella dei parametri
                        //loadParams(part, parser, params);
                        // Fa la query
                        //people = retrievePeople(params.get(part), codeSur, user, db);
                        // Personalizza le breadcrumbs
                        bC = HomePageCommand.makeBreadCrumbs(ConfigManager.getAppName(), req.getQueryString(), null);
                        // Imposta il valore della pagina di ricerca
                        fileJspT = nomeFile.get(part);
                    }
                } else {
                    // Viene richiesta la visualizzazione della pagina di ricerca
                    fileJspT = nomeFileElenco;
                }
            } else {    // manca il codice rilevazione
                String msg = FOR_NAME + "Impossibile recuperare il codice della rilevazione.\n";
                LOG.severe(msg + "Qualcuno ha probabilmente alterato il codice rilevazione nell\'URI della pagina.\n");
                throw new CommandException(msg);
            }
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di valori dal db.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        /* ******************************************************************** *
         *              Settaggi in request dei valori calcolati                *
         * ******************************************************************** */
        // Imposta nella request elenco aree funzionali
        req.setAttribute("areeFunz", aree);
        // Imposta nella request elenco ruoli giuridici
        req.setAttribute("ruoliGiur", qualifiche);
        // Imposta nella request elenco persone trovate, nel caso in cui siano valorizzate
        if (people != null) {
            req.setAttribute("persone", people);
        }
        // Imposta nella request le chiavi di ricerca, se presenti
        if (params != null) {
            req.setAttribute("tokens", params);
        }
        // Imposta nella request singola persona, nel caso in cui sia stata richiesta
        if (person != null) {
            req.setAttribute("persona", person);
        }
        // Imposta nella request lista macro/processi su cui Ã¨ allocata la persona, in caso esistano
        if (mp != null) {
            req.setAttribute("macroprocessi", mp);
        }
        // Imposta nella request breadcrumbs in caso siano state personalizzate
        if (bC != null) {
            req.removeAttribute("breadCrumbs");
            req.setAttribute("breadCrumbs", bC);
        }
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }


    /**
     * <p>Restituisce un Vector di tutte le persone allocate su macro/processo
     * organizzativo il cui identificativo viene passato come parametro; 
     * ciascuna delle persone contiene al proprio interno specifici attributi
     * caratterizzanti l'associazione della persona alla propria struttura.</p>
     *
     * @param type          identifica se la richiesta riguarda un macroprocesso o un processo
     * @param id            l'identificativo del macroprocesso o processo
     * @param codeSurvey    il codice della rilevazione
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;PersonBean&gt;</code> - lista di persone recuperate
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<PersonBean> retrievePeopleByMacroOrProcess(String type,
                                                                       int id,
                                                                       String codeSurvey,
                                                                       PersonBean user,
                                                                       DBWrapper db)
                                                                throws CommandException {
        ArrayList<PersonBean> people = null;
        CodeBean survey = ConfigManager.getSurvey(codeSurvey);
        try {
            // Estrae le persone allocate su un dato macroprocesso in una data rilevazione
            people = db.getPeopleByMacroOrProcess(user, type, id, survey.getId());
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Identificativo della rilevazione non recuperabile; problema nel metodo di estrazione delle persone per macro/processi.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero delle persone su macro/processo.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return people;
    }


    /**
     * <p>Restituisce un Vector di tutte le persone allocate su un macroprocesso
     * organizzativo il cui identificativo viene passato come parametro; 
     * ciascuna delle persone contiene al proprio interno specifici attributi
     * caratterizzanti l'associazione della persona alla propria struttura.</p>
     *
     * @param idMacro       l'identificativo del macroprocesso
     * @param codeSurvey    il codice della rilevazione
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;PersonBean&gt;</code> - lista di persone recuperate
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<PersonBean> retrievePeopleByMacro(int idMacro,
                                                              String codeSurvey,
                                                              PersonBean user,
                                                              DBWrapper db)
                                                       throws CommandException {
        return retrievePeopleByMacroOrProcess(PART_MACROPROCESS, idMacro, codeSurvey, user, db);
    }


    /**
     * <p>Restituisce un Vector di tutte le persone allocate su un processo
     * organizzativo il cui identificativo viene passato come parametro; 
     * ciascuna delle persone contiene al proprio interno specifici attributi
     * caratterizzanti l'associazione della persona alla propria struttura.</p>
     *
     * @param idProcess     l'identificativo del processo
     * @param codeSurvey    il codice della rilevazione
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;PersonBean&gt;</code> - lista di persone recuperate
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<PersonBean> retrievePeopleByProcess(int idProcess,
                                                                String codeSurvey,
                                                                PersonBean user,
                                                                DBWrapper db)
                                                         throws CommandException {
        return retrievePeopleByMacroOrProcess(PART_PROCESS, idProcess, codeSurvey, user, db);
    }

}
