/*
 *   Rischi On Line (ROL-RMS), Applicazione web: 
 *   - per la gestione di sondaggi inerenti al rischio corruttivo 
 *     cui i processi organizzativi di una PA possono essere esposti, 
 *   - per la produzione di mappature e reportistica finalizzate 
 *     alla valutazione del rischio corruttivo nella pubblica amministrazione, 
 *   - per ottenere suggerimenti riguardo le misure di mitigazione 
 *     che possono calmierare specifici rischi 
 *   - e per effettuare il monitoraggio al fine di verificare quali misure
 *     proposte sono state effettivamente attuate dai soggetti interessati
 *     alla gestione dei processi a rischio e stabilire quantitativamente 
 *     in che grado questa attuazione di misure abbia effettivamente ridotto 
 *     i livelli di rischio.
 *
 *   Risk Mapping and Management Software (ROL-RMS),
 *   web application: 
 *   - to assess the amount and type of corruption risk to which each organizational process is exposed, 
 *   - to publish and manage, reports and information on risk
 *   - and to propose mitigation measures specifically aimed at reducing risk, 
 *   - also allowing monitoring to be carried out to see 
 *     which proposed mitigation measures were then actually implemented 
 *     and quantify how much that implementation of measures actually 
 *     reduced risk levels.
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

package it.rol.bean;

import java.sql.Time;
import java.util.Date;
import java.util.Vector;

import it.rol.exception.AttributoNonValorizzatoException;

/**
 * <p>Classe usata per rappresentare un indicatore di monitoraggio.</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class IndicatorBean extends CodeBean {

	/**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
	 */
    private static final long serialVersionUID = -8193133105503064353L;
    /**
     *  Nome di questa classe. 
     *  Viene utilizzato per contestualizzare i messaggi di errore.
     */
    private final String FOR_NAME = "\n" + this.getClass().getName() + ": "; //$NON-NLS-1$
    /* ******************************************************** *
     *                   Dati dell'indicatore                   *
     * ******************************************************** */
	/** Descrizione dell'indicatore */
    private String descrizione;
    /** Descrizione della baseline dell'indicatore (risultati attuali in questo indicatore) */
    private String baseline;
    /** Data di contestualizzazione del valore baseline (data in cui i risultati baseline sono attuali)  */
    private Date dataBaseline;    
    /** Descrizione del target dell'indicatore (risultati attesi in questo indicatore) */
    private String target;
    /** Data di contestualizzazione del target (data in cui i risultati target sono attesi) */
    private Date dataTarget;
    /** Target revisionato */
    private String targetRev;
    /** Motivi dell'aggiornamento */
    private String noteRevisione;
    /** Data ultima revisione */
    private Date dataRevisione;
    /** Autore ultima revisione */
    private String autoreUltimaRevisione;
    /* ******************************************************** *
     *          Dati descrittivi dell'ultima modifica           *
     * ******************************************************** */
    /** Data ultima modifica */
    private Date dataUltimaModifica;
    /** Ora ultima modifica */
    private Time oraUltimaModifica;
    /** Autore ultima modifica */
    private String autoreUltimaModifica;
    /* ******************************************************** *
     *               Attributi di tipo e di stato               *
     * ******************************************************** */
    /** Tipo dell'indicatore */
    private CodeBean tipo;
    /** Stato indicatore */
    private CodeBean stato;
    /* ******************************************************** *
     *                      Altri Attributi                     *
     * ******************************************************** */ 
    /** Attributo identificativo della fase di attuazione associata all'indicatore corrente */
    private ActivityBean fase;
    /** Numero di misurazioni presenti per l'indicatore */
    private int totMisurazioni;
    /** Elenco di misurazioni presenti per l'indicatore */
    private Vector<?> misurazioni;
    
	
    /**
     * <p>Costruttore: inizializza i campi a valori di default.</p>
     */
	public IndicatorBean() {
	    super();
		descrizione = null;
		baseline = null;
		dataBaseline = dataTarget = new Date(0);
		target = targetRev = null;
		fase = null;
		dataUltimaModifica = dataRevisione = new Date(0);
        oraUltimaModifica = null;
        autoreUltimaModifica = autoreUltimaRevisione = null;
        noteRevisione = null;
        tipo = null;
        stato = null;
        totMisurazioni = BEAN_DEFAULT_ID;
        misurazioni = null;
	}


	/* ********************************************************* *
     *         Metodi getter e setter per descrizione            *
     * ********************************************************* */
	/**
	 * Restituisce la descrizione di un indicatore 
	 * @return <code>descrizione</code> - descrizione dell'indicatore
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * Imposta la descrizione di un indicatore
	 * @param descrizione - descrizione da settare
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

    
    /* ********************************************************* *
     *  Metodi getter e setter per baseline (risultati attuali)  *
     * ********************************************************* */
    /**
     * Restituisce la descrizione della baseline relativa all'anno
     * in cui viene definito l'indicatore (v. annoBaseline)
     * 
     * @return <code>baseline</code> - baseline dell'indicatore
     * @throws AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e questo attributo non &egrave; stato valorizzato (potrebbe essere un dato obbligatorio)
     */
    public String getBaseline() throws AttributoNonValorizzatoException {
        if (baseline == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo baseline non valorizzato!");
        }
        return baseline;
    }

    /**
     * Imposta la baseline di un indicatore
     * 
     * @param baseline - valore da impostare
     */
    public void setBaseline(String baseline) {
        this.baseline = baseline;
    }

    
    /* ********************************************************* *
     *    Metodi getter e setter per target (risultati attesi)   *
     * ********************************************************* */
    /**
     * Restituisce i risultati attesi per l'indicatore
     * 
     * @return <code>String</code> - risultati attesi per l'indicatore
     * @throws AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e questo attributo non &egrave; stato valorizzato (potrebbe essere un dato obbligatorio)
     */
    public String getTarget() throws AttributoNonValorizzatoException {
        if (target == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo target non valorizzato!");
        }
        return target;
    }

    /**
     * Imposta i risultati attesi per l'indicatore
     * 
     * @param target i risultati attesi nell'indicatore, da impostare
     */
    public void setTarget(String target) {
        this.target = target;
    }
    
    
    /* ********************************************************* *
     *      Metodi getter e setter per target revisionato        *
     * ********************************************************* */
    /**
     * Restituisce il valore revisionato dei risultati attesi per l'indicatore
     * 
     * @return <code>String</code> - target revisionato per l'indicatore
     */
    public String getTargetRivisto() {
        return targetRev;
    }

    /**
     * <p>Imposta un target revisionato per l'indicatore.</p>
     * <p>Non sempre le cose vanno come previsto (v. ad esempio Covid&ndash;19)
     * e quindi &egrave; necessario rivedere, generalmente al ribasso,
     * le proprie aspettative.</p>
     * 
     * @param targetRivisto il valore revisionato del target dell'indicatore, da impostare
     */
    public void setTargetRivisto(String targetRivisto) {
        this.targetRev = targetRivisto;
    }
    
    
    /* ***************************************************************** *
     *    Metodi getter e setter per data baseline (risultati attuali)   *
     * ***************************************************************** */
    /**
     * Restituisce la data a cui i risultati attuali fanno riferimento
     * 
     * @return <code>Date</code> - data Baseline
     * @throws AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e questo attributo non &egrave; stato valorizzato (potrebbe essere un dato obbligatorio)
     */
    public Date getDataBaseline() throws AttributoNonValorizzatoException {
        if (new Date(0).equals(dataBaseline)) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo data baseline non valorizzato! A quale data fa riferimento la baseline? Specificare una data.");
        }
        return dataBaseline;
    }

    /**
     * @param dataBaseline data baseline da impostare
     */
    public void setDataBaseline(Date dataBaseline) {
        this.dataBaseline = dataBaseline;
    }

   
    /* ***************************************************************** *
     *     Metodi getter e setter per data target (risultati attesi)     *
     * ***************************************************************** */
    /**
     * Restituisce l'anno a cui i risultati attuali fanno riferimento
     * 
     * @return <code>Date</code> - data Target
     * @throws AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e questo attributo non &egrave; stato valorizzato (potrebbe essere un dato obbligatorio)
     */
    public Date getDataTarget() throws AttributoNonValorizzatoException {
        if (new Date(0).equals(dataTarget)) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo data target non valorizzato! A quale data fa riferimento il target? Specificarne una.");
        }
        return dataTarget;
    }

    /**
     * @param dataTarget data target da impostare
     */
    public void setDataTarget(Date dataTarget) {
        this.dataTarget = dataTarget;
    }
    
    
    /* ********************************************************************* *
     *    Metodi getter e setter per fase misurata dall'indicatore corrente  *
     * ********************************************************************* */
    /**
     * Restituisce il bean rappresentante la fase di attuazione collegata all'indicatore corrente.
     * 
     * @return <code>ActivityBean</code> - Fase misurata con l'indicatore corrente
     */
    public ActivityBean getFase() {
        return fase;
    }

    /**
     * Imposta il bean rappresentante la fase misurata dall'indicatore corrente.
     * 
     * @param fase - Fase collegata, cioe' sulla quale l'indicatore corrente e' stato usato, da impostare
     */
    public void setFase(ActivityBean fase) {
        this.fase = fase;
    }
    

    /* *********************************************************** *
     *       Metodi getter e setter per data ultima modifica       *
     * *********************************************************** */
    /**
     * Restituisce la data dell'ultima modifica 
     * 
     * @return <code>java.util.Date</code> - data dell'ultima modifica
     */
    public Date getDataUltimaModifica() {
        return dataUltimaModifica;
    }

    /**
     * Imposta la data dell'ultima modifica
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
     * Restituisce l'ora dell'ultima modifica 
     * 
     * @return <code>java.sql.Time</code> - ora dell'ultima modifica
     */
    public Time getOraUltimaModifica() {
        return oraUltimaModifica;
    }
    
    /**
     * Imposta l'ora dell'ultima modifica 
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
     * Restituisce l'autore dell'ultima modifica 
     * 
     * @return <code>String</code> - autore ultima modifica
     */
    public String getAutoreUltimaModifica() {
        return autoreUltimaModifica;
    }

    /**
     * Imposta l'autore dell'ultima modifica
     * 
     * @param autoreUltimaModifica autore ultima modifica da impostare
     */
    public void setAutoreUltimaModifica(String autoreUltimaModifica) {
        this.autoreUltimaModifica = autoreUltimaModifica;
    }
    
    
    /* *********************************************************** *
     *       Metodi getter e setter per data ultima revisione      *
     * *********************************************************** */
    /**
     * Restituisce la data dell'ultima revisione 
     * 
     * @return <code>java.util.Date</code> - data dell'ultima revisione
     */
    public Date getDataRevisione() {
        return dataRevisione;
    }

    /**
     * Imposta la data dell'ultima revisione
     * 
     * @param dataRevisione data ultima revisione da impostare
     */
    public void setDataRevisione(Date dataRevisione) {
        this.dataRevisione = dataRevisione;
    }

    
    /* ************************************************************** *
     *      Metodi getter e setter per autore ultima revisione        *
     * ************************************************************** */
    /**
     * Restituisce l'autore dell'ultima revisione 
     * 
     * @return <code>String</code> - autore ultima revisione
     */
    public String getAutoreUltimaRevisione() {
        return autoreUltimaRevisione;
    }

    /**
     * Imposta l'autore dell'ultima revisione
     * 
     * @param autoreUltimaRevisione autore ultima revisione da impostare
     */
    public void setAutoreUltimaRevisione(String autoreUltimaRevisione) {
        this.autoreUltimaRevisione = autoreUltimaRevisione;
    }
    
    
    /* ********************************************************* *
     *        Metodi getter e setter per note revisione          *
     * ********************************************************* */
    /**
     * Restituisce i motivi che hanno portato all'aggiornamento di un indicatore 
     * @return <code>noteRevisione</code> - descrizione dei motivi dell'aggiornamento
     */
    public String getNoteRevisione() {
        return noteRevisione;
    }

    /**
     * Imposta la descrizione dei motivi che hanno portato all'aggiornamento di un indicatore
     * @param noteRevisione - note revisione da settare
     */
    public void setNoteRevisione(String noteRevisione) {
        this.noteRevisione = noteRevisione;
    }
    
    
    /* ************************************************************** *
     *           Metodi getter e setter per tipo indicatore           *
     * ************************************************************** */
    /**
     * Restituisce il tipo dell'indicatore
     * 
     * @return <code>CodeBean</code> - oggetto per il tipo indicatore
     * @throws AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e questo attributo non &egrave; stato valorizzato (potrebbe essere un dato obbligatorio)
     */
    public CodeBean getTipo() throws AttributoNonValorizzatoException {
        if (tipo == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo tipo indicatore non valorizzato! Qual e\' il tipo dell\'indicatore? Specificarne uno.");
        }
        return tipo;
    }

    /**
     * Imposta il tipo dell'indicatore
     * 
     * @param tipo il tipo indicatore da impostare
     */
    public void setTipo(CodeBean tipo) {
        this.tipo = tipo;
    }
    
    
    /* ************************************************************** *
     *           Metodi getter e setter per stato indicatore          *
     * ************************************************************** */
    /**
     * Restituisce lo stato dell'indicatore
     * 
     * @return <code>CodeBean</code> - oggetto per lo stato dell'indicatore
     * @throws AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e questo attributo non &egrave; stato valorizzato (potrebbe essere un dato obbligatorio)
     */
    public CodeBean getStato() throws AttributoNonValorizzatoException {
        if (stato == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo stato indicatore non valorizzato! Qual e\' lo stato dell\'indicatore? Specificarne uno!");
        }
        return stato;
    }

    /**
     * Imposta lo stato dell'indicatore
     * 
     * @param stato lo stato indicatore da impostare
     */
    public void setStato(CodeBean stato) {
        this.stato = stato;
    }
    
    
    /* ************************************************************************* *
     * Metodi getter e setter per numero di misurazioni presenti sull'indicatore *
     * ************************************************************************* */
    /**
     * Restituisce il numero di misurazioni eventualmente presenti 
     * relative all'indicatore, o il valore di default altrimenti.
     * L'attributo &egrave; obbligatorio non perch&eacute; sia obbligatorio
     * che per un dato indicatore siano presenti misurazioni, ma perch&eacute;
     * deve essere sempre possibile stabilire, quanto meno, se per quell'
     * indicatore sono o meno presenti misurazioni "at all".
     * 
     * @return <code>int</code> - numero di misurazioni trovate sull'indicatore
     * @throws AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e questo attributo non &egrave; stato valorizzato (potrebbe essere un dato obbligatorio)
     */
    public int getTotMisurazioni() throws AttributoNonValorizzatoException {
        if (totMisurazioni == BEAN_DEFAULT_ID) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo numero misurazioni indicatore non valorizzato! Ci sono o non ci sono misurazioni su questo indicatore? Questo deve essere un attributo sempre decidibile.");
        }
        return totMisurazioni;
    }

    /**
     * Imposta il numero di misurazioni trovate per l'indicatore
     * 
     * @param totMisurazioni il numero di misurazioni da impostare
     */
    public void setTotMisurazioni(int totMisurazioni) {
        this.totMisurazioni = totMisurazioni;
    }

    
    /* ********************************************************** *
     *          Metodi getter e setter per misurazioni            *
     * ********************************************************** */
    /**
     * Restituisce lista delle misurazioni che hanno rilevato i valori assunti da questo indicatore.
     * @return <code>Vector&lt;MeasurementBean&gt;</code> - elenco delle misurazioni che si riferiscono all'indicatore 
     */
    public Vector<?> getMisurazioni() {
        return misurazioni;
    }

    /**
     * Imposta la lista delle misurazioni che fanno riferimento all'indicatore.
     * @param misurazioni - elenco misurazioni da impostare
     */
    public void setMisurazioni(Vector<?> misurazioni) {
        this.misurazioni = misurazioni;
    }

}