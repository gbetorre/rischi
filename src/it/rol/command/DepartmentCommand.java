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

package it.rol.command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.ParameterParser;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.DBWrapper;
import it.rol.Data;
import it.rol.SessionManager;
import it.rol.util.Utils;
import it.rol.bean.CodeBean;
import it.rol.bean.DepartmentBean;
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
import it.rol.exception.CommandException;
import it.rol.exception.WebStorageException;


/**
 * <p><code>DepartmentCommand.java</code><br>
 * Implementa la logica per la gestione delle strutture organizzative.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class DepartmentCommand extends ItemBean implements Command, Constants {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = 7816862687031662585L;
    /**
     *  Nome di questa classe
     *  (utilizzato per contestualizzare i messaggi di errore)
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     * Log per debug in produzione
     */
    protected static Logger LOG = Logger.getLogger(ConfigManager.class.getName());
    /**
     * Pagina di default della Command
     */
    private static final String nomeFileElenco = "/jsp/stElenco.jsp";
    /**
     * Nome del file json della Command (dipende dalla pagina di default)
     */
    private String nomeFileJson = nomeFileElenco.substring(nomeFileElenco.lastIndexOf('/'), nomeFileElenco.indexOf(DOT));
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutti gli attributi del progetto
     */
    private static final HashMap<String, String> nomeFile = new HashMap<>();
    /**
     *  Processo di dato id
     */
    ProcessBean runtimeProcess = null;


    /**
     * Crea una nuova istanza di questa Command
     */
    public DepartmentCommand() {
        ;   // It doesn't anything
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
        nomeFile.put(PART_MACROPROCESS, nomeFileElenco);
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
         *            Crea e inizializza le variabili locali comuni             *
         * ******************************************************************** */
        // Databound
        DBWrapper db = null;
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        // Utente loggato
        PersonBean user = null;
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter("r", DASH);
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Prepara un oggetto contenente i parametri opzionali per i nodi
        ItemBean options = new ItemBean(VOID_STRING,  VOID_STRING,  VOID_STRING, VOID_STRING, STR_PFX, NOTHING);
        /* ******************************************************************** *
         *      Instanzia nuova classe WebStorage per il recupero dei dati      *
         * ******************************************************************** */
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new CommandException(FOR_NAME + "Non e\' disponibile un collegamento al database\n." + wse.getMessage(), wse);
        }
        /* ******************************************************************** *
         *         Previene il rischio di attacchi di tipo Garden Gate          *
         * ******************************************************************** */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            user = SessionManager.checkSession(req.getSession(IF_EXISTS_DONOT_CREATE_NEW));
        } catch (RuntimeException re) {
            throw new CommandException(FOR_NAME + "Problema a livello dell\'autenticazione utente!\n" + re.getMessage(), re);
        }
        /* ******************************************************************** *
         *                   Decide il valore della pagina                      *
         * ******************************************************************** */
        try {
            // Il parametro di navigazione 'rilevazione' è obbligatorio
            if (!codeSur.equals(DASH)) {
                /* Il parametro di navigazione 'p' permette di addentrarsi nelle funzioni
                if (nomeFile.containsKey(part)) {
                    // Viene richiesta la visualizzazione di una struttura specifica
                    if (idSt > NOTHING) {
                        // TODO eventuale codice per preparazione pagina di dettaglio struttura
                    } else {
                        m = db.getMacroBySurvey(user, codeSur);
                    }
                    fileJspT = nomeFile.get(part);
                } else {     */
                // Viene richiesta la visualizzazione dell'organigramma
                ArrayList<DepartmentBean> structs = retrieveStructures(codeSur, user, db);
                // Genera il file json contenente le informazioni strutturate
                printJson(req, structs, nomeFileJson, options);
                // Imposta la jsp
                fileJspT = nomeFileElenco;
              /*}*/
            } else {    // Manca il codice rilevazione!
                String msg = FOR_NAME + "Impossibile recuperare il codice della rilevazione.\n";
                LOG.severe(msg + "E\' possibile che qualcuno abbia alterato il codice rilevazione nell\'URL della pagina.\n");
                throw new CommandException(msg);
            }
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Probabile problema nel recupero di valori dal db.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ce.getMessage(), ce);
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
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }


    /**
     * <p>Estrae l'albero completo (vista gerarchica) delle strutture
     * collegate a una data rilevazione, il cui identificativo testuale
     * viene passato come parametro.</p>
     *
     * @param codeSurvey    codice testuale della rilevazione
     * @param user          utente loggato
     * @param db            databound gia' istanziato
     * @return <code>ArrayList&lt;DepartmentBean&gt;</code> - ArrayList di strutture collegate alla rilevazione
     * @throws CommandException se si verifica un problema nella query o nell'estrazione, nel recupero di valori o in qualche altro tipo di puntamento
     */
    public static ArrayList<DepartmentBean> retrieveStructures(String codeSurvey,
                                                               PersonBean user,
                                                               DBWrapper db)
                                                        throws CommandException {
        // Recupera l'oggetto rilevazione a partire dal codice
        CodeBean survey = ConfigManager.getSurvey(codeSurvey);
        try {
            // Chiama il metodo del databound che estrae l'albero delle strutture nella rilevazione
            ArrayList<DepartmentBean> structs = db.getStructures(user, survey);
            return structs;
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di valori dal db.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }


    /**
     * <p>Gestisce la creazione di un file JSON formattato per la libreria
     * orgchart.js (versione 1.0.5) contenente le informazioni relative alla
     * gerarchia delle strutture (organigramma).</p>
     *
     * @param req HttpServletRequest    per recuperare la path assoluta della web application
     * @param structs                   lista gerarchica delle strutture dell'organigramma
     * @param filename                  nome del file json da generare, parametrizzato
     * @param options                   opzioni di formattazione aggiuntive
     * @throws CommandException         se si verifica un puntamento a null o in genere nei casi in cui nella gestione del flusso informativo di questo metodo si verifica un problema
     */
    public static void printJson(HttpServletRequest req,
                                 ArrayList<DepartmentBean> structs,
                                 String filename,
                                 ItemBean options)
                          throws CommandException {
        FileWriter out = null;
        try {
            /* **************************************************************** *
             *     Controlla che la directory esista; se non esiste, la crea   *
             * **************************************************************** */
            // Ottiene la path assoluta della root della web application (/)
            String appPath = req.getServletContext().getRealPath(VOID_STRING);
            LOG.info(FOR_NAME + "appPath vale: " + appPath);
            // Crea la documents root directory (/documenti) se essa non esiste
            String documentsRootName = appPath + ConfigManager.getDirDocuments();
            File documentsRoot = new File(documentsRootName);
            if (!documentsRoot.exists()) {
                if (documentsRoot.mkdir()) {
                	LOG.info(FOR_NAME + documentsRootName + " creata.\n");
                } else {
                	String msg = FOR_NAME + "Attenzione: impossibile creare la directory \'documenti\' sotto " + documentsRootName;
                	LOG.severe(msg);
                	// Inutile continuare
                	throw new CommandException(msg + " Verificare eventualmente di avere i diritti di scrittura nella root dell\'applicazione.\n");
                }
            }
            // Crea la sottodirectory dei file json (/documenti/json) se non esiste
            File jsonDir = new File(appPath + ConfigManager.getDirJson());
            if (!jsonDir.exists()) {
                jsonDir.mkdir();
                LOG.info(FOR_NAME + jsonDir + " creata.\n");
            }
            /* ************************************************************ *
             *                    Genera e scrive il json                   *
             * ************************************************************ */
            String givenName = filename;
            LOG.info(FOR_NAME + "Nome stabilito per il file: " + givenName);
            String extension = DOT + JSON;
            // Crea il FileWriter (il nome della variabile è quello standard del PrintWriter, finesse...)
            out = new FileWriter(jsonDir + File.separator + givenName + extension);
            // Json begins
            StringBuilder json = new StringBuilder("[");
            // First Node: the principal
            json.append(Data.getStructureJsonNode(COMMAND_STRUCTURES, "1000", null, "ATENEO", VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING, null, STR_PFX, NOTHING)).append(",\n");
            int count = NOTHING;
            do {
                DepartmentBean s1 = structs.get(count);
                // I LIVELLO
                json.append(Data.getStructureJsonNode(COMMAND_STRUCTURES, 
                                                      s1.getExtraInfo().getCodice(), 
                                                      "1000", 
                                                      Utils.checkQuote(s1.getNome()), 
                                                      VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING,
                                                      options.getNome(), options.getIcona(), 
                                                      s1.getLivello())).append(",\n");
                // II LIVELLO
                for (DepartmentBean s2 : s1.getFiglie()) {
                    json.append(Data.getStructureJsonNode(COMMAND_STRUCTURES, 
                                                          s2.getExtraInfo().getCodice(), 
                                                          s1.getExtraInfo().getCodice(), 
                                                          Utils.checkQuote(s2.getPrefisso() + BLANK_SPACE + s2.getNome()), 
                                                          (s2.getPersone() != null ? Data.makeDescrJsonNode(s2.getPersone(), s2.getLivello()) : VOID_STRING), VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING,
                                                          options.getNome(), options.getIcona(), 
                                                          s2.getLivello())).append(",\n");
                    // III LIVELLO
                    for (DepartmentBean s3 : s2.getFiglie()) {
                        json.append(Data.getStructureJsonNode(COMMAND_STRUCTURES, 
                                                              s3.getExtraInfo().getCodice(), 
                                                              s2.getExtraInfo().getCodice(), 
                                                              Utils.checkQuote(s3.getPrefisso() + BLANK_SPACE + s3.getNome()), 
                                                              (s3.getPersone() != null ? Data.makeDescrJsonNode(s3.getPersone(), s3.getLivello()) : VOID_STRING), VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING,
                                                              options.getNome(), options.getIcona(), 
                                                              s3.getLivello())).append(",\n");
                        // IV LIVELLO
                        for (DepartmentBean s4 : s3.getFiglie()) {
                            json.append(Data.getStructureJsonNode(COMMAND_STRUCTURES, 
                                                                  s4.getExtraInfo().getCodice(), 
                                                                  s3.getExtraInfo().getCodice(), 
                                                                  Utils.checkQuote(s4.getPrefisso() + BLANK_SPACE + s4.getNome()), 
                                                                  (s4.getPersone() != null ? Data.makeDescrJsonNode(s4.getPersone(), s4.getLivello()) : VOID_STRING), VOID_STRING, VOID_STRING, VOID_STRING, VOID_STRING, 
                                                                  options.getNome(), options.getIcona(), 
                                                                  s4.getLivello())).append(",\n");
                        }
                    }
                }
                count++;
            } while (count < structs.size());
            json = new StringBuilder(json.substring(NOTHING, json.length() - 2));
            json.append("]");
            out.write(json.toString());
            LOG.info(FOR_NAME + "Salvataggio in file system effettuato.\n");
        } catch (IOException ioe) {
            throw new CommandException("Impossibile scrivere il file. Verificare nome e percorso.\n" + ioe.getMessage(), ioe);
        } catch (IllegalStateException ise) {
            throw new CommandException("Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n" + ise.getMessage(), ise);
        } catch (NullPointerException npe) {
            throw new CommandException("Errore nell'estrazione dei dipartimenti che gestiscono il corso.\n" + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        // Tenta in ogni caso di chiudere il file
        } finally {
            if (out != null) {
                try {
                    out.close();
                    LOG.info("File chiuso con successo.");
                } catch (IOException ioe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura del file!\n";
                    LOG.severe(msg);
                    throw new CommandException(msg + ioe.getMessage());
                } catch (Exception e) {
                    throw new CommandException(FOR_NAME + e.getMessage());
                }
            }
        }
    }

}
