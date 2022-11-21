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

package it.rol.bean;

import java.sql.Time;
import java.util.AbstractList;
import java.util.Date;

import it.rol.Constants;
import it.rol.exception.AttributoNonValorizzatoException;

/**
 * <p>Classe usata per rappresentare un processo/macroprocesso.</p>
 * <p>Il modello ingegneristico che &egrave; alla base del disegno di questo
 * oggetto &egrave; di tipo white&ndash;box, cio&egrave; include attributi di stato.<br />
 * Com'&egrave; noto, per gli ingegneri, la descrizione dello stato comporta
 * una notevole complicazione delle equazioni del modello, che richiede un sistema di
 * un'equazione algebrica <strong>e</strong> un'equazione differenziale, anche in un
 * modello SISO semplificato.<br />
 * Predispongo gli attributi per la gestione dello stato in previsione di un'evoluzione
 * del modello degli oggetti e della rappresentazione dei processi, di l&agrave; da venire
 * ma che vedo con favore.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class ProcessBean extends CodeBean {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = 1L;

    /**
     *  Nome di questa classe.
     *  Viene utilizzato per contestualizzare i messaggi di errore.
     */
    private final String FOR_NAME = "\n" + this.getClass().getName() + ": "; //$NON-NLS-1$
    /* ************************************************************************ *
     *                          Variabili d'istanza                             *
     * ------------------------------------------------------------------------ *
     *                    Dati identificativi del processo                      *
     * ************************************************************************ */
    /** Codice del processo */
    private String codice;
    /** Descrizione del processo */
    private String descrizione;
    /** Data di inizio del processo */
    private Date dataInizio;
    /** Data di fine del processo */
    private Date dataFine;
    /* ------------------------------------------------------------------------ *
     *                      Dati descrittivi del processo                       *
     * ------------------------------------------------------------------------ */
    /** Quota parte */
    private float quotaParte;
    /** Full Time Equivalent */
    private float fte;
    /** Obiettivi misurabili del processo */
    private String obiettiviMisurabili;
    /** Erogabilit&agrave; in modalit&agrave; telelavoro */
    private String smartWorking;
    /* ------------------------------------------------------------------------ *
     *               Dati descrittivi dello stato del processo                  *
     * ------------------------------------------------------------------------ */
    /** Stato processo */
    private CodeBean statoprocesso;
    /** Descrizione dello stato corrente del processo */
    private String descrizioneStatoCorrente;
    /* ------------------------------------------------------------------------ *
     *        Altri attributi del processo, o relazioni verso altre entita'     *
     * ------------------------------------------------------------------------ */
    /** Identificativo della rilevazione */
    private int idRilevazione;
    /** Rilevazione che ha rilevato il processo */
    private CodeBean rilevazione;
    /** Vincoli del processo */
    private String vincoli;
    /** Etichetta di classificazione processo */
    private String tag;
    /** Etichetta di classificazione tipo processo */
    private String tipo;
    /** Id del dipartimento del processo */
    private int idDipart;
    /** Dipartimento del processo */
    private DepartmentBean dipart;
    /** Processi aggregati dal macroprocesso */
    private AbstractList<ProcessBean> processi;
    /** Persone allocate sul processo/macroprocesso */
    private AbstractList<PersonBean> persone;
    /** Livello gerarchico del processo in una gerarchia di tipi di processi */
    private int livello;
    /* ------------------------------------------------------------------------ *
     *                   Dati descrittivi dell'ultima modifica                  *
     * ------------------------------------------------------------------------ */
    /** Data ultima modifica */
    private Date dataUltimaModifica;
    /** Ora ultima modifica */
    private Time oraUltimaModifica;
    /** Autore ultima modifica */
    private int autoreUltimaModifica;


    /* ************************************************************************ *
     *                               Costruttori                                *
     * ************************************************************************ */
    /**
     * <p>Costruttore, e da superclasse: inizializza i campi a valori di default.</p>
     */
    public ProcessBean() {
        super();
        codice = descrizione = null;
        dataInizio = new Date(0);
        dataFine = new Date(0);
        dataUltimaModifica = new Date(0);
        oraUltimaModifica = null;
        autoreUltimaModifica = -2;
        descrizioneStatoCorrente = null;
        obiettiviMisurabili = null;
        quotaParte = fte = BEAN_DEFAULT_FLOAT;
        smartWorking = null;
        vincoli = null;
        tag = tipo = null;
        idDipart = -2;
        dipart = null;
        idRilevazione = -2;
        statoprocesso = null;
        processi = null;
        persone = null;
        rilevazione = null;
    }


    /* **************************************************** *
     *           Metodi getter e setter per codice          *
     * **************************************************** */
    /**
     * Restituisce il codice identificativo del processo.
     * @return <code>codice</code> - codice identificativo del processo
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e codice non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    public String getCodice() throws AttributoNonValorizzatoException {
        if (codice == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo codice non valorizzato!");
        }
        return codice;
    }

    /**
     * Imposta il codice univoco del processo.
     * @param codice - il codice da impostare
     */
    public void setCodice(String codice) {
        this.codice = codice;
    }


     /* **************************************************** *
     *           Metodi getter e setter per descrizione     *
     * **************************************************** */
    /**
     * <p>Restituisce la descrizione di un processo.</p>
     * <p>Se la descrizione non &egrave; stata valorizzata ed &egrave;
     * rimasta impostata al valore di default (<code>null</code>)
     * dato dal costruttore, si fa riferimento a <code>VOID_STRING</code>
     * per inizializzarla, e quindi poterla cos&iacute; usare
     * nei test dei confronti tra valori.<br />
     * Tuttavia, se si usa il metodo populate di BeanUtils, ci&ograve;
     * &egrave; pleonastico, in quanto &egrave; la stessa <code>populate()</code>,
     * in accordo con la direttiva 08, a restituire il valore "stringa vuota"
     * piuttosto che <code>null</code> in caso di colonne null, quindi al test
     * del confronto, dopo l'applicazione della <code>populate()</code>,
     * i valori arrivano gi&agrave; pronti per essere comparati! (Ovvero:
     * valorizzati se &egrave; presente un valore, stringa vuota se &egrave;
     * presente <code> null</code>).</p>
     *
     * @return <code>descrizione</code> - la descrizione del processo
     */
    public String getDescrizione() {
        if (descrizione == null) {
            return Constants.VOID_STRING;
        }
        return descrizione;
    }

    /**
     * Imposta la descrizione di un processo.
     * @param descrizione - la descrizione da impostare
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }


     /* **************************************************** *
     *           Metodi getter e setter per data inizio     *
     * **************************************************** */
    /**
     * Restituisce la data di inizio di un processo
     * @return <code>dataInizio</code> - la data di inizio del processo
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e dataInizio non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    public Date getDataInizio() throws AttributoNonValorizzatoException  {
        if(new Date(0).equals(dataInizio)) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo data inizio non valorizzato!");
        }
        return dataInizio;
    }

    /**
     * Imposta la data di inizio di un processo.
     * @param dataInizio  - la data di inizio da impostare
     */
    public void setDataInizio(Date dataInizio) {
        this.dataInizio = dataInizio;
    }


    /* **************************************************** *
     *           Metodi getter e setter per data fine       *
     * **************************************************** */
    /**
     * Restituisce la data di fine di un processo
     * @return <code>dataFine</code> - la data di fine del processo
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e dataFine non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    public Date getDataFine() throws AttributoNonValorizzatoException {
        if(new Date(0).equals(dataFine)) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo data fine non valorizzato!");
        }
        return dataFine;
    }

    /**
     * Imposta la data di fine di un processo.
     * @param dataFine - la data di fine da impostare
     */
    public void setDataFine(Date dataFine) {
        this.dataFine = dataFine;
    }


    /* **************************************************** *
     *        Metodi getter e setter per smartworking       *
     * **************************************************** */
    /**
     * Restituisce la lavorabilità in smart working del processo.
     * @return <code>smartWorking</code> - stringa specificante se il processo e' erogabile, anche parzialmente, in modalita' smart working
     */
    public String getSmartWorking() {
        return smartWorking;
    }

    /**
     * Imposta la lavorabilità in smart working del processo.
     * @param smartWorking - stringa specificante se il processo è erogabile in smart working
     */
    public void setSmartWorking(String smartWorking) {
        this.smartWorking = smartWorking;
    }


    /* ******************************************************* *
     *   Metodi getter e setter per descrizioneStatoCorrente   *
     * ******************************************************* */
    /**
     * Restituisce la descrizione dello stato corrente del processo
     * @return <code>descrizioneStatoCorrente</code> - descrizione stato corrente
     */
    public String getDescrizioneStatoCorrente() {
        return descrizioneStatoCorrente;
    }

    /**
     * Imposta la descrizione dello stato corrente
     * @param descrizioneStatoCorrente - descrizione dello stato corrente del processo da impostare
     */
    public void setDescrizioneStatoCorrente(String descrizioneStatoCorrente) {
        this.descrizioneStatoCorrente = descrizioneStatoCorrente;
    }


    /* **************************************************** *
     *    Metodi getter e setter per full time equivalent   *
     * **************************************************** */
    /**
     * <p>Restituisce indice full time equivalent del processo.</p>
     *
     * @return <code>float</code> - full time equivalent
     */
    public float getFte() {
        return fte;
    }

    /**
     * Imposta fte del processo
     * @param fte - indice full time equivalent del processo da impostare
     */
    public void setFte(float fte) {
        this.fte = fte;
    }


    /* *************************************************** *
     *       Metodi getter e setter per quota parte        *
     * *************************************************** */
    /**
     * Restituisce la quotaparte totalizzata dal processo
     * @return <code>quotaParte</code> - quota parte
     */
    public float getQuotaParte() {
        return quotaParte;
    }

    /**
     * Imposta la quotaparte del processo
     * @param quotaParte - quota parte da impostare
     */
    public void setQuotaParte(float quotaParte) {
        this.quotaParte = quotaParte;
    }


    /* ****************************************************** *
     *     Metodi getter e setter per obiettiviMisurabili     *
     * ****************************************************** */
    /**
     * <p>Restituisce gli obiettivi misurabili del processo,
     * inizializzandoli a stringa vuota in caso non siano stati
     * mai valorizzati nel contesto del processo caricato a
     * runtime.</p>
     * <p>Questo tipo di comportamento &egrave; un po' l'opposto
     * del trattamento riservato agli attributi obbligatori.</p>
     *
     * @return <code>obiettiviMisurabili</code> - obiettivi misurabili
     */
    public String getObiettiviMisurabili() {
        if (obiettiviMisurabili == null) {
            return Constants.VOID_STRING;
        }
        return obiettiviMisurabili;
    }

    /**
     * Imposta gli obiettivi misurabili
     * @param obiettiviMisurabili - obiettivi misurabili da impostare
     */
    public void setObiettiviMisurabili(String obiettiviMisurabili) {
        this.obiettiviMisurabili = obiettiviMisurabili;
    }


    /* ************************************************* *
     *         Metodi getter e setter per vincoli        *
     * ************************************************* */
    /**
     * Restituisce i vincoli del processo
     * @return <code>vincoli</code> - vincoli del processo
     */
    public String getVincoli() {
        return vincoli;
    }

    /**
     * Imposta i vincoli del processo
     * @param vincoli - vincoli del processo da impostare
     */
    public void setVincoli(String vincoli) {
        this.vincoli = vincoli;
    }


    /* ************************************************* *
     *        Metodi getter e setter per etichetta       *
     * ************************************************* */
    /**
     * Restituisce l'etichetta del processo
     * @return <code>tag</code> - etichetta del processo
     */
    public String getTag() {
        return tag;
    }

    /**
     * Imposta etichetta classificatoria del processo
     * @param label - etichetta del processo da impostare
     */
    public void setTag(String label) {
        tag = label;
    }


    /* ************************************************* *
     *     Metodi getter e setter per etichetta tipo     *
     *   (e.g.: 'E' = Eccellenza | 'P' = Performance)    *
     * ************************************************* */
    /**
     * Restituisce il tipo del processo
     * @return <code>tag</code> - tipo del processo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Imposta etichetta classificatoria del tipo del processo
     * @param tipo - etichetta del processo da impostare
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    /* ************************************************** *
     *         Metodi getter e setter per idDipart        *
     * ************************************************** */
    /**
     * Restituisce l'id del dipartimento del processo
     * @return <code>idDipart</code> - id del dipartimento del processo
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e idDipart non         &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    public int getIdDipart() throws AttributoNonValorizzatoException {
        if (idDipart == -2) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo idDipart non valorizzato!");
        }
        return idDipart;
    }

    /**
     * Imposta l'id del dipartimento del processo
     * @param idDipart - id del dipartimento del processo
     */
    public void setIdDipart(int idDipart) {
        this.idDipart = idDipart;
    }


    /* *************************************************** *
     *          Metodi getter e setter per dipart          *
     * *************************************************** */
    /**
     * Restituisce un oggetto DepartmentBean che rappresenta il dipartimento del processo
     * @return <code>dipart</code> - dipartimento del processo
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e dipart non         &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    public DepartmentBean getDipart() throws AttributoNonValorizzatoException {
        if (dipart == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo dipart non valorizzato!");
        }
        return dipart;
    }

    /**
     * Imposta dipart con l'oggetto DepartmentBean passato
     * @param dipart - dipartimento del processo da impostare
     */
    public void setDipart(DepartmentBean dipart) {
        this.dipart = dipart;
    }


    /* *************************************************** *
     *       Metodi getter e setter per rilevazione        *
     * *************************************************** */
    /**
     * Restituisce un oggetto CodeBean che rappresenta la rilevazione del processo
     * @return <code>rilevazione</code> - rilevazione che ha rilevato il processo
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e rilevazione non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    public CodeBean getRilevazione() throws AttributoNonValorizzatoException {
        if (rilevazione == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo rilevazione non valorizzato!");
        }
        return rilevazione;
    }

    /**
     * Imposta rilevazione con l'oggetto CodeBean passato
     * @param rilevazione - rilevazione del processo da impostare
     */
    public void setRilevazione(CodeBean rilevazione) {
        this.rilevazione = rilevazione;
    }


    /* ***************************************************** *
     * Metodi getter e setter per identificativo rilevazione *
     * ***************************************************** */
    /**
     * Restituisce l'identificativo della rilevazione cui il processo &egrave;
     * collegato
     * @return <code>idRilevazione</code> - identificativo della rilevazione
     */
    public int getIdRilevazione() {
        return idRilevazione;
    }

    /**
     * Imposta l'identificativo della rilevazione cui il processo &egrave;
     * collegato
     * @param id - identificativo generico
     */
    public void setIdRilevazione(int id) {
        this.idRilevazione = id;
    }


    /* *************************************************** *
     *       Metodi getter e setter per statoprocesso      *
     * *************************************************** */
    /**
     * Restituisce un oggetto StatoprocessoBean che rappresenta lo stato del processo
     *
     * @return <code>statoprocesso</code> - stato del processo
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e statoprocesso non         &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    public CodeBean getStatoprocesso() throws AttributoNonValorizzatoException {
        if(statoprocesso == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo statoprocesso non valorizzato!");
        }
        return statoprocesso;
    }

    /**
     * Imposta lo stato di un processo.
     *
     * @param statoprocesso - stato processo da impostare
     */
    public void setStatoprocesso(CodeBean statoprocesso) {
        this.statoprocesso = statoprocesso;
    }


    /* *************************************************** *
     *       Metodi getter e setter per sottoprocessi      *
     * *************************************************** */
    /**
     * Restituisce una lista di sottoprocessi che sono stati aggregati
     * per il processo (che in tal caso &egrave; in realt&agrave; un macroprocesso);
     * non solleva un'eccezione se questo attributo &egrave;
     * non significativo (perch&eacute; il presente bean serve a rappresentare sia
     * il sottprocesso sia il macroprocesso).
     *
     * @return <code>processi</code> - lista di sottoprocessi del macroprocesso
     */
    public AbstractList<ProcessBean> getProcessi() {
        return processi;
    }

    /**
     * Imposta i sottoprocessi di un macroprocesso.
     *
     * @param processi - sottoprocessi di macroprocesso da impostare
     */
    public void setProcessi(AbstractList<ProcessBean> processi) {
        this.processi = processi;
    }


    /* *************************************************** *
     *     Metodi getter e setter per persone allocate     *
     * *************************************************** */
    /**
     * Restituisce una lista di persone che sono stati allocate
     * per il processo (o il macroprocesso);
     * non solleva un'eccezione se questo attributo &egrave;
     * non valorizzato (perch&eacute; questo dato, anche se significativo,
     * non deve essere necessariamente caricato).
     *
     * @return <code>persone</code> - lista di persone del macro/processo
     */
    public AbstractList<PersonBean> getPersone() {
        return persone;
    }

    /**
     * Imposta le persone allocate su di un macro/processo.
     *
     * @param persone - persone di macro/processo da impostare
     */
    public void setPersone(AbstractList<PersonBean> persone) {
        this.persone = persone;
    }
    
    /* **************************************************** *
     *          Metodi getter e setter per livello          *
     * **************************************************** */
    /**
     * Restituisce il livello di un processo:<ul>
     * <li>1 = macroprocesso</li>
     * <li>2 = processo</li>
     * <li>3 = sottoprocesso (o subprocesso)</li></ul>
     * 
     * @return <code>livello</code> - il livello del processo in una gerarchia di tipi di processo
     */
    public int getLivello() {
        return livello;
    }
    
    /**
     * Imposta il livello di una struttura.
     * @param livello - il livello di processo da impostare
     */
    public void setLivello(int livello) {
        this.livello = livello;
    }



    /* *********************************************************** *
     *       Metodi getter e setter per data ultima modifica       *
     * *********************************************************** */
    /**
     * Restituisce la data dell'ultima modifica dello status di un processo
     *
     * @return <code>java.util.Date</code> - data dell'ultima modifica
     */
    public Date getDataUltimaModifica() {
        return dataUltimaModifica;
    }

    /**
     * Imposta la data dell'ultima dello status di un processo
     *
     * @param dataUltimaModifica data ultima modifica da impostare
     */
    public void setDataUltimaModifica(Date dataUltimaModifica) {
        this.dataUltimaModifica = dataUltimaModifica;
    }


    /* *********************************************************** *
     *       Metodi getter e setter per ora ultima modifica        *
     * *********************************************************** */
    /**
     * Restituisce l'ora dell'ultima modifica di un processo
     *
     * @return <code>java.sql.Time</code> - ora dell'ultima modifica
     */
    public Time getOraUltimaModifica() {
        return oraUltimaModifica;
    }

    /**
     * Imposta l'ora dell'ultima modifica di un processo
     *
     * @param oraUltimaModifica ora ultima modifica da impostare
     */
    public void setOraUltimaModifica(Time oraUltimaModifica) {
        this.oraUltimaModifica = oraUltimaModifica;
    }


    /* ************************************************************** *
     *       Metodi getter e setter per autore ultima modifica        *
     * ************************************************************** */
    /**
     * Restituisce l'autore dell'ultima modifica di un processo
     *
     * @return <code>int</code> - autore ultima modifica
     */
    public int getAutoreUltimaModifica() {
        return autoreUltimaModifica;
    }

    /**
     * Imposta l'autore dell'ultima modifica di un processo
     *
     * @param autoreUltimaModifica autore ultima modifica da impostare
     */
    public void setAutoreUltimaModifica(int autoreUltimaModifica) {
        this.autoreUltimaModifica = autoreUltimaModifica;
    }


}
