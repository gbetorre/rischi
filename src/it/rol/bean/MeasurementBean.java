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
 * <p>Classe usata per rappresentare la misurazione di un indicatore (di monitoraggio).</p>
 * 
 * <p>Created on Wed 20 Nov 2024 10:28:23 AM CET</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class MeasurementBean extends CodeBean {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
	 */
    private static final long serialVersionUID = 7375214565768143951L;
    /**
     *  Nome di questa classe. 
     *  Viene utilizzato per contestualizzare i messaggi di errore.
     */
    private final String FOR_NAME = "\n" + this.getClass().getName() + ": "; //$NON-NLS-1$
    
    // Inherited from its parent: informativa -> "azioni svolte per raggiungere l'obiettivo"
    /** Dato  identificativo */
    private String valore;
	/** Motivazioni ritardo / mancato raggiungimento */
    private String descrizione;
    /** Data di contestualizzazione della misurazione dell'indicatore  */
    private Date dataMisurazione;
    /** Flag di ultima misurazione: permette assunzioni conseguenti */
    private boolean ultimo;
    /** Indicatore misurato dalla misurazione corrente */
    private IndicatorBean indicatore;
    /** Data ultima modifica */
    private Date dataUltimaModifica;
    /** Ora ultima modifica */
    private Time oraUltimaModifica;
    /** Autore ultima modifica */
    private String autoreUltimaModifica;
    /** Domanda 1 */
    private String domanda1;
    /** Domanda 2 */
    private String domanda2;
    /** Domanda 3 */
    private String domanda3;
    /** Ulteriori informazioni (id indicatore...) */
    private ItemBean extraInfo;
    /** Vector di fileset, ciascuno rappresentante un riferimento logico ad un allegato fisico */
    private Vector<FileDocBean> allegati;
    
	
    /**
     * <p>Costruttore: inizializza i campi a valori di default.</p>
     */
	public MeasurementBean() {
	    super();
		valore = descrizione = null;
		dataMisurazione = new Date(0);
		ultimo = false;
		indicatore = null;
        dataUltimaModifica = new Date(0);
        oraUltimaModifica = null;
        autoreUltimaModifica = null;
        domanda1 = domanda2 = domanda3 = null;
        extraInfo = null;
        allegati = null;
	}


	/* ********************************************************* *
     *  Metodi getter e setter per valore (risultato registrato) *
     * ********************************************************* */
    /**
     * Restituisce il valore di una misurazione
     * 
     * @return <code>valore</code> - risultato certificato dalla misurazione
     */
    public String getValore() {
        return valore;
    }

    /**
     * Imposta il valore di una misurazione
     * 
     * @param valore - risultato da settare
     */
    public void setValore(String valore) {
        this.valore = valore;
    }
    
	
	/* ********************************************************* *
     *         Metodi getter e setter per descrizione            *
     * ********************************************************* */
	/**
	 * Restituisce la descrizione di una misurazione
	 * 
	 * @return <code>descrizione</code> - descrizione della misurazione
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * Imposta la descrizione di una misurazione
	 * 
	 * @param descrizione - descrizione da settare
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
    
    
    /* ***************************************************************** *
     *  Metodi getter e setter per data misurazione (risultato attuale)  *
     * ***************************************************************** *
    /**
     * Restituisce la data di effettuazione della misurazione
     * 
     * @return <code>Date</code> - data misurazione
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e questo attributo non &egrave; stato valorizzato (dovrebbe essere un dato obbligatorio)
     *
    public Date getDataMisurazione() throws AttributoNonValorizzatoException {
        if (new Date(0).equals(dataMisurazione)) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo data non valorizzato! A quale data fa riferimento la misurazione? Specificare una data.");
        }
        return dataMisurazione;
    }

    /**
     * @param dataMisurazione data misurazione da impostare
     *
    public void setDataMisurazione(Date dataMisurazione) {
        this.dataMisurazione = dataMisurazione;
    }*/
	
	
    /**
     * Restituisce la data di effettuazione della misurazione
     * (che coincide con la data di ultima modifica della misurazione)
     * 
     * @return <code>Date</code> - data misurazione
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e questo attributo non &egrave; stato valorizzato (dato obbligatorio)
     */
    public Date getDataMisurazione() throws AttributoNonValorizzatoException {
        if (new Date(0).equals(dataUltimaModifica)) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo data non valorizzato! A quale data fa riferimento la misurazione? Specificare una data.");
        }
        return dataUltimaModifica;
    }

    /**
     * &Egrave; un wrapper di setDataUltimaModifica(date)
     */
    public void setDataMisurazione() {
        //this.dataMisurazione = dataMisurazione;
        dataMisurazione = dataUltimaModifica;
    }
    
    
    /* ********************************************************* *
     *      Metodi getter e setter per ultima misurazione        *
     * ********************************************************* */
    /**
     * Restituisce true se la misurazione &egrave; l'ultima - false (default) se non lo &egrave;
     * 
     * @return <code>ultimo</code> - true/false misurazione ultima
     */
    public boolean isUltimo() {
        return ultimo;
    }

    /**
     * Imposta ultima misurazione
     *  
     * @param ultimo - ultima misurazione true/false da impostare
     */
    public void setUltimo(boolean ultimo) {
        this.ultimo = ultimo;
    }
    
    
    /* ********************************************************************* *
     * Metodi getter e setter per indicatore misurato dalla misura corrente  *
     * ********************************************************************* */
    /**
     * Restituisce il bean rappresentante l'indicatore collegato alla misurazione corrente.
     * 
     * @return <code>IndicatorBean</code> - Indicatore misurato con la misurazione corrente
     */
    public IndicatorBean getIndicatore() {
        return indicatore;
    }

    /**
     * Imposta il bean rappresentante l'indicatore misurato.
     * 
     * @param indicatore - IndicatorBean collegato, da impostare
     */
    public void setIndicatore(IndicatorBean indicatore) {
        this.indicatore = indicatore;
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
    
    
    /* ************************************************************** *
     *              Metodi getter e setter per allegati               *
     * ************************************************************** */
    /**
     * Restituisce l'elenco dei riferimenti logici agli allegati fisici
     * in precedenza caricati per la misurazione corrente 
     * 
     * @return <code>Vector&lt;FileDocBean&gt;</code> - elenco di riferimenti logici ad allegati fisici
     */
    public Vector<FileDocBean> getAllegati() {
        return allegati;
    }

    /**
     * Imposta l'elenco dei riferimenti logici agli allegati fisici
     * caricati per la misurazione corrente
     * 
     * @param allegati Vector di FileDocBean da impostare
     */
    public void setAllegati(Vector<FileDocBean> allegati) {
        this.allegati = allegati;
    }
    
    
    /* ************************************************************** *
     *              Metodi getter e setter per domanda 1              *
     * ************************************************************** */
    /**
     * Restituisce il contenuto della domanda 1
     * 
     * @return <code>domanda 1</code> - descrizione della domanda 1
     */
    public String getDomanda1() {
        return domanda1;
    }

    /**
     * Imposta il contenuto della domanda 1
     * 
     * @param domanda1 - descrizione da settare
     */
    public void setDomanda1(String domanda1) {
        this.domanda1 = domanda1;
    }
    
    
    /* ************************************************************** *
     *              Metodi getter e setter per domanda 2              *
     * ************************************************************** */
    /**
     * Restituisce il contenuto della domanda 2
     * 
     * @return <code>domanda 2</code> - descrizione della domanda 2
     */
    public String getDomanda2() {
        return domanda2;
    }

    /**
     * Imposta il contenuto della domanda 2
     * 
     * @param domanda2 - descrizione da settare
     */
    public void setDomanda2(String domanda2) {
        this.domanda2 = domanda2;
    }
    
    
    /* ************************************************************** *
     *              Metodi getter e setter per domanda 3              *
     * ************************************************************** */
    /**
     * Restituisce il contenuto della domanda 3
     * 
     * @return <code>domanda 3</code> - descrizione della domanda 3
     */
    public String getDomanda3() {
        return domanda3;
    }

    /**
     * Imposta il contenuto della domanda 3
     * 
     * @param domanda3 - descrizione da settare
     */
    public void setDomanda3(String domanda3) {
        this.domanda3 = domanda3;
    }
    
    
    /* ************************************************************** *
     *       Metodi getter e setter per ulteriori informazioni        *
     * ************************************************************** */
    /**
     * Restituisce un oggetto contenente ulteriori informazioni
     * 
     * @return <code>ItemBean</code> - un oggetto generico contenente informazioni
     */
    public ItemBean getExtraInfo() {
        return extraInfo;
    }

    /**
     * Imposta un oggetto contente ulteriori informazioni 
     * relative alla misurazione corrente
     * 
     * @param extraInfo oggetto contenente ulteriori informazioni, da impostare
     */
    public void setExtraInfo(ItemBean extraInfo) {
        this.extraInfo = extraInfo;
    }

}
