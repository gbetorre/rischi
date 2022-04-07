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

package it.rol.command;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.rol.ConfigManager;
import it.rol.Constants;
import it.rol.DBWrapper;
import it.rol.Utils;
import it.rol.bean.CodeBean;
import it.rol.bean.DepartmentBean;
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.CommandException;
import it.rol.exception.WebStorageException;


/**
 * <p><code>DepartmentCommand.java</code><br>
 * Implementa la logica per la gestione delle strutture collegate ai processi on line (PROL).</p>
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
     * Pagina a cui la command reindirizza per mostrare i processi che l'utente ha i diritti di visualizzare
     */
    private static final String nomeFileElenco = "/jsp/stElenco.jsp";
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
    public DepartmentCommand() {
        ;   // It Doesn't Anything
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
        // Recupera o inizializza 'id processo'
        int idSt = parser.getIntParameter("id", -1);
        // Recupera o inizializza 'tipo parte'
        String part = parser.getStringParameter("p", DASH);
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Dichiara elenco di processi
        AbstractList<ProcessBean> m = new ArrayList<ProcessBean>();
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
            // Il parametro di navigazione 'rilevazione' è obbligatorio
            if (!codeSur.equals(DASH)) {
                // Il parametro di navigazione 'p' permette di addentrarsi nelle funzioni
                if (nomeFile.containsKey(part)) {
                    // Viene richiesta la visualizzazione di una struttura specifica
                    if (idSt > NOTHING) {
                        // TODO
                    } else {
                        // TODO
                        m = db.getMacroBySurvey(user, codeSur);
                    }
                    fileJspT = nomeFile.get(part);
                } else {    // Viene richiesta la visualizzazione dell'organigramma
                    // Recupera le strutture associate
                    ArrayList<DepartmentBean> structs = retrieveStructures(codeSur, user, db);
                    // Stampa il json
                    printJson(req, structs, "prElencoAjax", new ItemBean());
                    // Imposta la jsp
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
        // Imposta nella request elenco macroprocessi
        req.setAttribute("macroprocessi", m);
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }


    /**
     * <p>Estrae l'albero completo (vista gerarchica) delle strutture
     * collegate a una rilevazione, il cui identificativo testuale
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
            // Chiama il metodo del databound che estrae l'albero completo delle strutture nella rilevazione
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
     * <p>Estrae l'elenco (vista piatta, non gerarchica) delle strutture
     * collegate a una rilevazione, il cui identificativo testuale
     * viene passato come parametro.</p>
     *
     * @param type          valore identificante se si vuol fare la query sulle strutture collegate a processi o a macroprocessi
     * @param id            identificativo del processo o macroprocesso
     * @param getAll        flag specificante, se vale -1, che si vogliono recuperare tutte le strutture collegate a tutti i macro/processi
     * @param codeSurvey    codice testuale della rilevazione
     * @param user          utente loggato
     * @param db            databound gia' istanziato
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - ArrayList di strutture collegate alla rilevazione e al macro/processo specifico, oppure a tutti i macro/processi
     * @throws CommandException se si verifica un problema nella query o nell'estrazione, nel recupero di valori o in qualche altro tipo di puntamento
     */
    public static ArrayList<ItemBean> retrieveStructsByMacroOrProcess(String type,
                                                                      int id,
                                                                      int getAll,
                                                                      String codeSurvey,
                                                                      PersonBean user,
                                                                      DBWrapper db)
                                                               throws CommandException {
        // Recupera l'oggetto rilevazione a partire dal codice
        CodeBean survey = ConfigManager.getSurvey(codeSurvey);
        try {
            // Chiama il metodo del databound che estrae la vista piatta delle strutture nella rilevazione
            ArrayList<ItemBean> structs = db.getStructsByMacroOrProcess(user, type, id, getAll, survey.getId());
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
     * <p>Restituisce un ArrayList di tutte le strutture allocate su macroprocesso
     * il cui identificativo viene passato come argomento.</p>
     *
     * @param idType     indica se l'ID identifica un macroprocesso o un processo
     * @param id         l'identificativo del macroprocesso o processo
     * @param codeSurvey il codice della rilevazione
     * @param user       utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param db         WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;DepartmentBean&gt;</code> - lista di strutture recuperate
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    private static ArrayList<DepartmentBean> retrieveStructuresByMacroOrProcess(String idType,
                                                                                int id,
                                                                                String codeSurvey,
                                                                                PersonBean user,
                                                                                DBWrapper db)
                                                                         throws CommandException {
        ArrayList<DepartmentBean> structs = null;
        CodeBean survey = ConfigManager.getSurvey(codeSurvey);
        int idThru = NOTHING;   // id globale
        try {
            // Estrae le strutture allocate su un dato macroprocesso in una data rilevazione
            ArrayList<ItemBean> items = db.getStructsByMacroOrProcess(user, idType, id, id, survey.getId());
            HashMap<Integer, DepartmentBean> l1s = new HashMap<>();
            HashMap<Integer, DepartmentBean> l2s = new HashMap<>();
            HashMap<Integer, DepartmentBean> l3s = new HashMap<>();
            HashMap<Integer, DepartmentBean> l4s = new HashMap<>();
            for (ItemBean i: items) {
                // Incrementa l'id globale
                ++idThru;
                Integer id_uo_l1 = i.getCod1();
                Integer id_uo_l2 = i.getCod2();
                Integer id_uo_l3 = i.getCod3();
                Integer id_uo_l4 = i.getCod4();
                DepartmentBean uo_l1;
                DepartmentBean uo_l2;
                DepartmentBean uo_l3;
                DepartmentBean uo_l4;
                // Controlla se nella mappa delle strutture di I livello esiste la struttura
                if (!l1s.containsKey(id_uo_l1)) {
                    // Se non esiste la crea
                    uo_l1 = new DepartmentBean();
                    // Ne imposta il livello
                    uo_l1.setLivello((byte) 1);
                    // Vi memorizza l'id specifico
                    uo_l1.setId(i.getCod1());
                    // Crea un contenitore di informazioni aggiuntive
                    ItemBean infoS1 = new ItemBean();
                    // Memorizza l'id globale
                    infoS1.setCodice(String.valueOf(idThru + DOT + uo_l1.getId() + DASH + uo_l1.getLivello()));
                    // Vi memorizza il nome
                    uo_l1.setNome(i.getInformativa());
                    // Fte
                    uo_l1.setFte(i.getValue1());
                    // Strutture figlie
                    uo_l1.setFiglie(new Vector<DepartmentBean>());
                    // Persone
                    uo_l1.setPersone(db.getPeopleByStructureAndMacroOrProcess(user, idType, id, survey.getId(), new int[]{uo_l1.getId(), DEFAULT_ID, DEFAULT_ID, DEFAULT_ID}));
                    // Salva le informazioni aggiuntive
                    uo_l1.setExtraInfo(infoS1);
                    // Aggiunge la struttura valorizzata alla mappa
                    l1s.put(id_uo_l1, uo_l1);
                }
                uo_l2 = l2s.get(id_uo_l2);
                if (uo_l2 == null) { // l2s does not contain the key id_uo_l2, i.e., does not contain uo_l2
                    uo_l2 = new DepartmentBean();
                    uo_l2.setLivello((byte) 2);
                    uo_l2.setId(i.getCod2());
                    ItemBean infoS2 = new ItemBean();
                    infoS2.setCodice(String.valueOf(idThru + DOT + uo_l2.getId() + DASH + uo_l2.getLivello()));
                    uo_l2.setPrefisso(VOID_STRING);
                    uo_l2.setNome(i.getExtraInfo());
                    uo_l2.setFte(i.getValue2());
                    uo_l2.setPadre(l1s.get(id_uo_l1));
                    uo_l2.setFiglie(new Vector<DepartmentBean>());
                    uo_l2.setPersone(db.getPeopleByStructureAndMacroOrProcess(user, idType, id, survey.getId(), new int[]{DEFAULT_ID, uo_l2.getId(), DEFAULT_ID, DEFAULT_ID}));
                    uo_l2.setExtraInfo(infoS2);
                    l2s.put(id_uo_l2, uo_l2);
                    l1s.get(id_uo_l1).getFiglie().add(uo_l2);
                }
                uo_l3 = l3s.get(id_uo_l3);
                if (uo_l3 == null && !l2s.containsKey(id_uo_l3)) {  // l3s does not contain the key id_uo_l3, i.e., does not contain uo_l3
                    uo_l3 = new DepartmentBean();
                    uo_l3.setLivello((byte) 3);
                    uo_l3.setId(i.getCod3());
                    ItemBean infoS3 = new ItemBean();
                    infoS3.setCodice(String.valueOf(idThru + DOT + uo_l3.getId() + DASH + uo_l3.getLivello()));
                    uo_l3.setPrefisso(VOID_STRING);
                    uo_l3.setNome(i.getLabelWeb());
                    uo_l3.setFte(i.getValue3());
                    uo_l3.setPadre(l2s.get(id_uo_l2));
                    uo_l3.setFiglie(new Vector<DepartmentBean>());
                    uo_l3.setPersone(db.getPeopleByStructureAndMacroOrProcess(user, idType, id, survey.getId(), new int[]{DEFAULT_ID, DEFAULT_ID, uo_l3.getId(), DEFAULT_ID}));
                    uo_l3.setExtraInfo(infoS3);
                    l3s.put(id_uo_l3, uo_l3);
                    l2s.get(id_uo_l2).getFiglie().add(uo_l3);
                }
                uo_l4 = l4s.get(id_uo_l4);
                if (uo_l4 == null && !l3s.containsKey(id_uo_l4)) {  // l4s does not contain the key id_uo_l4, i.e., does not contain uo_l4
                    uo_l4 = new DepartmentBean();
                    uo_l4.setLivello((byte) 4);
                    uo_l4.setId(i.getCod4());
                    ItemBean infoS4 = new ItemBean();
                    infoS4.setCodice(String.valueOf(idThru + DOT + uo_l4.getId() + DASH + uo_l4.getLivello()));
                    uo_l4.setPrefisso(VOID_STRING);
                    uo_l4.setNome(i.getPaginaJsp());
                    uo_l4.setFte(i.getValue4());
                    uo_l4.setPadre(l3s.get(id_uo_l3));
                    uo_l4.setPersone(db.getPeopleByStructureAndMacroOrProcess(user, idType, id, survey.getId(), new int[]{DEFAULT_ID, DEFAULT_ID, DEFAULT_ID , uo_l4.getId()}));
                    uo_l4.setExtraInfo(infoS4);
                    l4s.put(id_uo_l4, uo_l4);
                    if (l3s.get(id_uo_l3) != null) {
                        l3s.get(id_uo_l3).getFiglie().add(uo_l4);
                    }
                }
            }
            structs = new ArrayList<>(l1s.values());
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Identificativo della rilevazione non recuperabile; problema nel metodo di estrazione delle strutture per macro/processi.\n";
            LOG.severe(msg);
            throw new CommandException(msg + anve.getMessage(), anve);
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero delle strutture su macro/processo.\n";
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
        return structs;
    }


    /**
     * <p>Restituisce un ArrayList di tutte le strutture allocate su macroprocesso
     * il cui identificativo viene passato come argomento.</p>
     *
     * @param idMacro       l'identificativo del macroprocesso
     * @param codeSurvey    il codice della rilevazione
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;DepartmentBean&gt;</code> - lista di strutture recuperate
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<DepartmentBean> retrieveStructuresByMacro(int idMacro,
                                                                      String codeSurvey,
                                                                      PersonBean user,
                                                                      DBWrapper db)
                                                               throws CommandException {
        return retrieveStructuresByMacroOrProcess(PART_MACROPROCESS, idMacro, codeSurvey, user, db);
    }


    /**
     * <p>Restituisce un ArrayList di tutte le strutture allocate su processo
     * il cui identificativo viene passato come argomento.</p>
     *
     * @param idProcess     l'identificativo del processo
     * @param codeSurvey    il codice della rilevazione
     * @param user          utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param db            WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;DepartmentBean&gt;</code> - lista di strutture recuperate
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<DepartmentBean> retrieveStructuresByProcess(int idProcess,
                                                                        String codeSurvey,
                                                                        PersonBean user,
                                                                        DBWrapper db)
                                                                 throws CommandException {
        return retrieveStructuresByMacroOrProcess(PART_PROCESS, idProcess, codeSurvey, user, db);
    }


    /**
     * <p>Genera il nodo JSON</p>
     *
     * @param tipo          valore che serve a differenziare tra tipi diversi di nodi per poter applicare formattazioni o attributi diversi
     * @param codice        codice del nodo corrente
     * @param codicePadre   codice del nodo padre del nodo corrente
     * @param nome          etichetta del nodo
     * @param descr         descrizione del nodo
     * @param bgColor       parametro opzionale specificante il colore dei box/nodi in formato esadecimale
     * @param livello       livello gerarchico del nodo
     * @return <code>String</code> - il nodo in formato String
     */
    private static String getStructureJsonNode(String tipo,
                                               String codice,
                                               String codicePadre,
                                               String nome,
                                               String descr,
                                               String bgColor,
                                               int livello) {
        /* ======================== *
         *   Controlli sull'input   *
         * ======================== */
        String codiceGest = (codicePadre == null ? "null" : "\"" + codicePadre + "\"");
        String nodeImage = null;
        if (tipo.equals(COMMAND_PERSON)) {
            nodeImage = "per.png";
        } else if (tipo.equals(COMMAND_STRUCTURES)) {
            nodeImage = "dept_l" + livello + ".png";
        }
        String height =  (descr.length() > 100) ? String.valueOf(descr.length()) : String.valueOf(146);
        Color backgroundColor = null;
        if (bgColor != null && !bgColor.equals(VOID_STRING)) {
            backgroundColor = Color.decode(bgColor);
        } else {
            backgroundColor = new Color(51,182,208);
        }
        /* ======================== */
        // Generazione nodo
        return "{\"nodeId\":\"" + codice + "\"," +
                "  \"parentNodeId\":" + codiceGest + "," +
                "  \"width\":342," +
                "  \"height\":" + height +"," +
                "  \"borderWidth\":1," +
                "  \"borderRadius\":5," +
                "  \"borderColor\":{\"red\":15,\"green\":140,\"blue\":121,\"alpha\":1}," +
                "  \"backgroundColor\":{\"red\":" + backgroundColor.getRed() + ",\"green\":" + backgroundColor.getGreen() + ",\"blue\":" + backgroundColor.getBlue() + ",\"alpha\":1}," +
                "  \"nodeImage\":{\"url\":\"web/img/" + nodeImage + "\",\"width\":50,\"height\":50,\"centerTopDistance\":0,\"centerLeftDistance\":0,\"cornerShape\":\"CIRCLE\",\"shadow\":false,\"borderWidth\":0,\"borderColor\":{\"red\":19,\"green\":123,\"blue\":128,\"alpha\":1}}," +
                "  \"nodeIcon\":{\"icon\":\"\",\"size\":30}," +
                "  \"template\":\"<div>\\n <div style=\\\"margin-left:15px;\\n margin-right:15px;\\n text-align: center;\\n margin-top:10px;\\n font-size:20px;\\n font-weight:bold;\\n \\\">" + nome + "</div>\\n <div style=\\\"margin-left:80px;\\n margin-right:15px;\\n margin-top:3px;\\n font-size:16px;\\n \\\">" + descr + "</div>\\n\\n </div>\"," +
                "  \"connectorLineColor\":{\"red\":220,\"green\":189,\"blue\":207,\"alpha\":1}," +
                "  \"connectorLineWidth\":5," +
              //"  \"dashArray\":\"\"," +
                "  \"expanded\":false }";
    }

    
    /**
     * <p>Genera la descrizione del nodo JSON</p>
     *
     * @param list          struttura vettoriale contenente informazioni
     * @param livello       livello gerarchico del nodo
     * @return <code>String</code> - il nodo in formato String
     * @throws AttributoNonValorizzatoException 
     */
    private static String makeDescrJsonNode(ArrayList<?> list,
                                            int livello) 
                                     throws AttributoNonValorizzatoException {
        StringBuffer descr = new StringBuffer();
        descr.append("<ul>");
        for (int i = 0; i < list.size(); i++) {
            PersonBean p = (PersonBean) list.get(i);
            descr.append("<li>");
            descr.append(p.getNome());
            descr.append(BLANK_SPACE);
            descr.append(p.getCognome());
            descr.append(BLANK_SPACE + Utils.DASH + BLANK_SPACE);
            descr.append(p.getNote());
            descr.append("</li>");
        }
        descr.append("</ul>");
        /* ======================== */
        // Generazione descr
        return descr.toString();
    }
    

    /**
     * <p>Gestisce la creazione di un file JSON formattato per la libreria
     * orgchart.js.</p>
     *
     * @param req HttpServletRequest    per recuperare la path assoluta della web application
     * @param structs                   lista gerarchica delle strutture dell'organigramma
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
            json.append(getStructureJsonNode(COMMAND_STRUCTURES, "1000", null, "ATENEO", VOID_STRING, null, NOTHING)).append(",\n");
            int count = NOTHING;
            do {
                DepartmentBean s1 = structs.get(count);
                // I LIVELLO
                json.append(getStructureJsonNode(COMMAND_STRUCTURES, s1.getExtraInfo().getCodice(), "1000", Utils.checkQuote(s1.getNome()), VOID_STRING, options.getCodice(), s1.getLivello())).append(",\n");
                // II LIVELLO
                for (DepartmentBean s2 : s1.getFiglie()) {
                    json.append(getStructureJsonNode(COMMAND_STRUCTURES, s2.getExtraInfo().getCodice(), s1.getExtraInfo().getCodice(), Utils.checkQuote(s2.getPrefisso() + BLANK_SPACE + s2.getNome()), (s2.getPersone() != null ? makeDescrJsonNode(s2.getPersone(), s2.getLivello()) : VOID_STRING), options.getCodice(), s2.getLivello())).append(",\n");
                    // III LIVELLO
                    for (DepartmentBean s3 : s2.getFiglie()) {
                        json.append(getStructureJsonNode(COMMAND_STRUCTURES, s3.getExtraInfo().getCodice(), s2.getExtraInfo().getCodice(), Utils.checkQuote(s3.getPrefisso() + BLANK_SPACE + s3.getNome()), (s3.getPersone() != null ? makeDescrJsonNode(s3.getPersone(), s3.getLivello()) : VOID_STRING), options.getCodice(), s3.getLivello())).append(",\n");
                        // IV LIVELLO
                        for (DepartmentBean s4 : s3.getFiglie()) {
                            json.append(getStructureJsonNode(COMMAND_STRUCTURES, s4.getExtraInfo().getCodice(), s3.getExtraInfo().getCodice(), Utils.checkQuote(s4.getPrefisso() + BLANK_SPACE + s4.getNome()), (s4.getPersone() != null ? makeDescrJsonNode(s4.getPersone(), s4.getLivello()) : VOID_STRING), options.getCodice(), s4.getLivello())).append(",\n");
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
