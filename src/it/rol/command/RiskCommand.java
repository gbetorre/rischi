/*
 *   Alma on Line: Applicazione WEB per la visualizzazione 
 *   delle schede di indagine su popolazione dell'ateneo,
 *   della gestione dei progetti on line (POL) 
 *   e della preparazione e del monitoraggio delle informazioni riguardanti 
 *   l'offerta formativa che hanno ricadute sulla valutazione della didattica 
 *   (questionari on line - QOL).
 *   
 *   Copyright (C) 2018 Giovanroberto Torre<br />
 *   Alma on Line (aol), Projects on Line (pol), Questionnaire on Line (qol);
 *   web applications to publish, and manage, students evaluation,
 *   projects, students and degrees information.
 *   Copyright (C) renewed 2018 Universita' degli Studi di Verona, 
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
 *   Giovanroberto Torre <giovanroberto.torre@univr.it>
 *   Sistemi Informatici per il Reporting di Ateneo
 *   Universita' degli Studi di Verona
 *   Via Dell'Artigliere, 8
 *   37129 Verona (Italy)
 */

package it.rol.command;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.rol.Constants;
import it.rol.DBWrapper;
import it.rol.Main;
import it.rol.Query;
import it.rol.Utils;
import it.rol.bean.DepartmentBean;
import it.rol.bean.ItemBean;
import it.rol.bean.PersonBean;
import it.rol.bean.ProcessBean;
import it.rol.bean.RiskBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.CommandException;
import it.rol.exception.WebStorageException;


/** 
 * <p><code>RiskCommand.java</code><br />
 * Implementa la logica per la gestione dei rischi corruttivi (ROL).</p>
 * 
 * <p>Created on Tue 12 Apr 2022 09:46:04 AM CEST</p>
 * 
 * @author <a href="mailto:giovanroberto.torre@univr.it">Giovanroberto Torre</a>
 */
public class RiskCommand extends ItemBean implements Command, Constants {
    
    /**
     * 
     */
    private static final long serialVersionUID = 6619103818860851921L;
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
     * Pagina a cui la command reindirizza per mostrare la lista dei rischi del progetto
     */
    private static final String nomeFileElenco = "/jsp/riElenco.jsp";
    /**
     * Pagina a cui la command fa riferimento per permettere la selezione di una struttura
     */
    private static final String nomeFileSelectStruct = "/jsp/riStruttura.jsp";
    /**
     * Pagina a cui la command fa riferimento per permettere la selezione di un processo
     */
    private static final String nomeFileSelectProcess = "/jsp/riProcesso.jsp";
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutti gli attributi del progetto
     */    
    private static final HashMap<String, String> nomeFile = new HashMap<>();
    /**
     *  Rischi del progetto di dato id
     */
    Vector<RiskBean> riskOfRuntimeProject = null; 
    /**
     *  Progetto di dato id
     */
    //ProjectBean runtimeProject = null; 
    
    /** 
     * Crea una nuova istanza di WbsCommand 
     */
    public RiskCommand() {
        /*;*/   // It doesn't anything
    }
  
    
    /** 
     * <p>Raccoglie i valori dell'oggetto ItemBean
     * e li passa a questa classe command.</p>
     *
     * @param voceMenu la VoceMenuBean pari alla Command presente.
     * @throws CommandException se l'attributo paginaJsp di questa command non e' stato valorizzato.
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
        nomeFile.put(COMMAND_RISK, nomeFileElenco);
        nomeFile.put(PART_SELECT_STR, nomeFileSelectStruct);
        nomeFile.put(PART_PROCESS, nomeFileSelectProcess);
        //nomeFile.put(Query.MODIFY_PART, nomeFileRisk);
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
         *              Dichiara e inizializza variabili locali                 *
         * ******************************************************************** */
        // Databound
        DBWrapper db = null;
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter("r", DASH);
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Utente loggato
        PersonBean user = null;
        // Data di oggi sotto forma di oggetto String
        GregorianCalendar today = Utils.getCurrentDate();
        // Elenco strutture collegate alla rilevazione
        ArrayList<DepartmentBean> structs = null;
        // Elenco strutture collegate alla rilevazione
        HashMap<String, Vector<DepartmentBean>> flatStructs = null;
        // Variabile contenente l'indirizzo alla pagina di reindirizzamento
        String redirect = null;
        /* ******************************************************************** *
         *                    Recupera parametri e attributi                    *
         * ******************************************************************** */
        // Recupera o inizializza 'id progetto'
        int idPrj = parser.getIntParameter("id", DEFAULT_ID);
        // Recupera o inizializza 'tipo pagina'   
        String part = parser.getStringParameter("p", "-");
        // Flag di scrittura
        boolean write = (boolean) req.getAttribute("w");
        /* ******************************************************************** *
         *      Instanzia nuova classe DBWrapper per il recupero dei dati       *
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
            if (ses == null) {
                throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n");
            }
            user = (PersonBean) ses.getAttribute("usr");
            if (user == null) {
                throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n");
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
         *                          Corpo del programma                         *
         * ******************************************************************** */
        // Decide il valore della pagina
        try {
            // Controllo sull'input
            if (!codeSur.equals(DASH)) {
                // Recupera in ogni caso il progetto richiesto dalla navigazione utente
                //runtimeProject = db.getProject(idPrj, user.getId());
                // Recupera in ogni caso i rischi associati al progetto richiesto dalla navigazione utente
                //riskOfRuntimeProject = db.getRisks(idPrj, user);
                // Verifica se deve eseguire un'operazione di scrittura
                if (write) {
                    // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
                    //HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
                    // Recupera i progetti su cui l'utente ha diritti di scrittura
                    //Vector<ProjectBean> writablePrj = (Vector<ProjectBean>) ses.getAttribute("writableProjects");
                    // Se non ci sono progetti scrivibili e il flag "write" è true c'è qualcosa che non va...
                    /*if (writablePrj == null) {
                        String msg = FOR_NAME + "Il flag di scrittura e\' true pero\' non sono stati trovati progetti scrivibili: problema!.\n";
                        LOG.severe(msg);
                        throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n");
                    }*/
                    // Trasforma un Vector di progetti scrivibili dall'utente loggato in un dictionary degli stessi
                    //HashMap<Integer, ProjectBean> writableProjects = ProjectCommand.decant(writablePrj);
                    // Controllo quale azione vuole fare l'utente
                    if (nomeFile.containsKey(part)) {
                        // Creazione della tabella che conterrà i valori dei parametri passati dalle form
                        HashMap<String, LinkedHashMap<String, String>> params = new HashMap<>();
                        //loadParams(part, parser, params);
                        // Controlla se deve effettuare un inserimento o un aggiornamento
                        if (part.equalsIgnoreCase(PART_SELECT_STR)) {
                            /* ************************************************ *
                             *                  SELECT Process Part                *
                             * ************************************************ */
                            // Devono essere possibile identificare la struttura
                            loadParams(part, parser, params);
                            LinkedHashMap<String, String> struct = params.get(PART_SELECT_STR);
                            //String par = HomePageCommand.getParameters(req, MIME_TYPE_TEXT);
                            StringBuffer paramsStruct = new StringBuffer();
                            for (Map.Entry<String, String> set : struct.entrySet()) {
                                // Printing all elements of a Map
                                paramsStruct.append("&");
                                paramsStruct.append("s" + set.getKey() + "=" + set.getValue());
                            }
                            redirect = "q=" + COMMAND_RISK + "&p=" + PART_PROCESS + paramsStruct.toString() + "&r=AT2022";
                        }/* else if (part.equalsIgnoreCase(Query.ADD_TO_PROJECT)) {
                            /* ************************************************ *
                             *                  INSERT Risk Part                *
                             * ************************************************ *
                            loadParams(part, parser, params);
                            db.insertWbs(idPrj, user, writablePrj, params.get(Query.ADD_TO_PROJECT));
                            redirect = "q=" + Query.PART_RISK + "&id=" + idPrj;
                        }*/
                    } else {
                        // Deve eseguire una eliminazione
                    }
                } else {
                    /* ************************************************ *
                     *                  SELECT Risk Part                *
                     * ************************************************ */
                    if (nomeFile.containsKey(part)) {   // Aggiungere condizioni più specifiche se ci sarà più di una "part"
                        // Recupera le strutture della rilevazione corrente
                        structs = DepartmentCommand.retrieveStructures(codeSur, user, db);
                        flatStructs = decant(structs);
                        fileJspT = nomeFile.get(part);
                    } else {
                        //riskOfRuntimeProject = db.getRisks(idPrj, user);
                        fileJspT = nomeFileElenco;
                    }
                }
            } else {
                // Se siamo qui vuol dire che l'id del progetto non è > zero, il che è un guaio
                HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
                ses.invalidate();
                String msg = FOR_NAME + "Qualcuno ha tentato di inserire un indirizzo nel browser avente un id progetto non valido!.\n";
                LOG.severe(msg);
                throw new CommandException("Attenzione: indirizzo richiesto non valido!\n");
            }
        /*}  catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di valori dal db.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);*/
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Si e\' verificato un problema in una conversione di tipo.\n";
            LOG.severe(msg);
            throw new CommandException(msg + cce.getMessage(), cce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
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
        // Imposta nella request elenco strutture
        if (structs != null) {
            req.setAttribute("strutture", structs);
        }
        // Imposta nella request elenco strutture sotto forma di dictionary
        if (flatStructs != null) {
            req.setAttribute("elencoStrutture", flatStructs);
        }
        // Imposta nella request data di oggi 
        req.setAttribute("now", Utils.format(today));
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
        /* ******************************************************************** *
         * Settaggi in request di valori facoltativi: attenzione, il passaggio  *
         * di questi attributi e' condizionato al fatto che siano significativi *
         * ******************************************************************** */
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }
    }
    
    
    /**
     * <p>Travasa una struttura vettoriale matriciale (Vector con Vector con Vector etc.) 
     * di DepartmentBean, ciascuno dei quali pu&ograve; contenere un Vector di 
     * figlie, in una corrispondente struttura di tipo Dictionary, HashMap, 
     * in cui le chiavi sono rappresentate da oggetti String e i valori
     * sono rappresentati dalle figlie del nodo avente codice corrispondente
     * alla chiave.</p>
     * <p>&Egrave; utile per un accesso pi&uacute; diretto alle figlie
     * di ogni struttura, evitando di dover ogni volta ciclare la struttura
     * matriciale fino al nodo di cui si vogliono ottenere le strutture figlie.</p>
     *
     * @param structs Vector di DepartmentBean da travasare in HashMap
     * @return <code>HashMap&lt;String&comma; Vector&lt;DepartmentBean&gt;&gt;</code> - Struttura di tipo Dictionary, o Mappa ordinata, avente per chiave il codice del nodo, e per valore il Vector delle sue figlie
     * @throws CommandException se si verifica un problema nell'accesso all'id di un oggetto, nello scorrimento di liste o in qualche altro tipo di puntamento
     */
    private static HashMap<String, Vector<DepartmentBean>> decant(ArrayList<DepartmentBean> structs)
                                                           throws CommandException {
        HashMap<String, Vector<DepartmentBean>> flatStructs = new HashMap<>();
        try {
            for (DepartmentBean l1 : structs) {
                String keyL1 = l1.getExtraInfo().getCodice(); 
                Vector<DepartmentBean> vL2 = l1.getFiglie();
                flatStructs.put(keyL1, vL2);
                for (DepartmentBean l2 : vL2) {
                    String keyL2 = l2.getExtraInfo().getCodice(); 
                    Vector<DepartmentBean> vL3 = l2.getFiglie();
                    flatStructs.put(keyL2, vL3);
                    for (DepartmentBean l3 : vL3) {
                        String keyL3 = l3.getExtraInfo().getCodice(); 
                        Vector<DepartmentBean> vL4 = l3.getFiglie();
                        flatStructs.put(keyL3, vL4);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento fuori tabella.\n" + aiobe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, aiobe);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Si e\' verificato un problema di conversione di tipo.\n" + cce.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, cce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento.\n" + npe.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel travaso di un Vector in un Dictionary.\n" + e.getMessage();
            LOG.severe(msg);
            throw new CommandException(msg, e);
        }
        return flatStructs;
    }
    
    
    /**
     * <p>Valorizza per riferimento una mappa contenente i valori relativi  
     * ad una attivit&agrave; eventualmente da aggiornare.</p> 
     * 
     * @param part la sezione del sito che si vuole aggiornare
     * @param parser oggetto per la gestione assistita dei parametri di input, gia' pronto all'uso
     * @param formParams mappa da valorizzare per riferimento (ByRef)
     * @throws CommandException se si verifica un problema nella gestione degli oggetti data o in qualche tipo di puntamento
     */
    private static void loadParams(String part, 
                                   ParameterParser parser,
                                   HashMap<String, LinkedHashMap<String, String>> formParams)
                            throws CommandException {
        /* **************************************************** *
         *           Ramo di SELECT di una Struttura            *
         * **************************************************** */
        if (part.equalsIgnoreCase(PART_SELECT_STR)) {
            // Recupero e caricamento parametri di struttura
            LinkedHashMap<String, String> struct = new LinkedHashMap<>();
            struct.put("liv1",  parser.getStringParameter("str-liv1", VOID_STRING));
            struct.put("liv2",  parser.getStringParameter("str-liv2", VOID_STRING));
            struct.put("liv3",  parser.getStringParameter("str-liv3", VOID_STRING));
            struct.put("liv4",  parser.getStringParameter("str-liv4", VOID_STRING));
            formParams.put(PART_SELECT_STR, struct);
        } 
        /* **************************************************** *
         *              Ramo di INSERT di una Wbs               *
         * **************************************************** *
        else if (part.equalsIgnoreCase(Query.ADD_TO_PROJECT)) {
            HashMap<String, String> wbs = new HashMap<String, String>();
            wbs.put("wbs-idpadre",      parser.getStringParameter("wbs-idpadre", Utils.VOID_STRING));
            wbs.put("wbs-name",         parser.getStringParameter("wbs-name", Utils.VOID_STRING));
            wbs.put("wbs-descr",        parser.getStringParameter("wbs-descr", Utils.VOID_STRING));
            wbs.put("wbs-workpackage",  parser.getStringParameter("wbs-workpackage", Utils.VOID_STRING));
            wbs.put("wbs-note",         parser.getStringParameter("wbs-note", Utils.VOID_STRING));
            wbs.put("wbs-result",       parser.getStringParameter("wbs-result", Utils.VOID_STRING));
            formParams.put(Query.ADD_TO_PROJECT, wbs);
        }*/
    }
    
}