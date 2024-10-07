/*
 *   Rischi On Line (ROL): Applicazione web per la gestione di 
 *   sondaggi inerenti al rischio corruttivo cui i processi organizzativi
 *   di una PA possono essere esposti e per la produzione di mappature
 *   e reportistica finalizzate alla valutazione del rischio corruttivo
 *   nella pubblica amministrazione.
 *
 *   Risk Mapping Software (ROL)
 *   web applications to assess the amount, and kind, of risk
 *   which each process is exposed, and to publish, and manage,
 *   report and risk information.
 *   Copyright (C) 2022-2024 Giovanroberto Torre
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

import java.util.ArrayList;
import java.util.Vector;

/**
 * <p>Classe usata per rappresentare un dipartimento o, pi&uacute; 
 * in generale, una struttura organizzativa (direzione, polo, centro, etc.).</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class DepartmentBean extends CodeBean {

	/**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
	 */
    private static final long serialVersionUID = 4579077159512261378L;
    /** Prefisso della struttura */
    private String prefisso;
    /** Acronimo della struttura */
    private String acronimo;
    /** Indirizzo della sede della struttura */
    private String indirizzo;
    /** Livello gerarchico della struttura in una gerarchia di organigramma */
    private int livello;
    /** Struttura padre della struttura corrente */
    private DepartmentBean padre;
    /** Elenco figlie della struttura corrente */
    private Vector<DepartmentBean> figlie;
    /** Full Time Equivalent */
    private float fte;
    /** Elenco persone allocate sulla struttura corrente */
    private ArrayList<PersonBean> persone;
    /** Oggetto per contenere informazioni aggiuntive relative alla struttura */
    private ItemBean extraInfo;
    /** Lista di misure di mitigazione associate alla struttura */
    private ArrayList<MeasureBean> misure;
    
    
    /* ************************************************************************ *
     *                               Costruttori                                *
     * ************************************************************************ */
    
    /**
     * <p>Costruttore che inizializza i campi a valori di default.</p>
     */
    public DepartmentBean() {
        super();
    	prefisso = null;
    	acronimo = null;
    	indirizzo = null;
    	livello = (byte) BEAN_DEFAULT_ID;
    	padre = null;
    	figlie = null;
    	persone = null;
    	fte = BEAN_DEFAULT_FLOAT;
    	setExtraInfo(null);
    	misure = null;
    }

    
	/* **************************************************** *
     *           Metodi getter e setter per prefisso        *
     * **************************************************** */
    
	/**
	 * Restituisce il prefisso di un dipartimento
	 * 
	 * @return <code>prefisso</code> - prefisso del dipartimento
	 */
	public String getPrefisso() {
		return prefisso;
	}

	/**
	 * Imposta il prefisso di un dipartimento
	 * 
	 * @param prefisso - il prefisso del dipartimento da impostare
	 */
	public void setPrefisso(String prefisso) {
		this.prefisso = prefisso;
	}

	
    /* **************************************************** *
     *           Metodi getter e setter per acronimo        *
     * **************************************************** */
	
    /**
     * Restituisce l'acronimo di un dipartimento
     * 
     * @return <code>acronimo</code> - acronimo del dipartimento
     */
    public String getAcronimo() {
        return acronimo;
    }

    /**
     * Imposta il prefisso di un dipartimento
     * 
     * @param acronimo - il prefisso del dipartimento da impostare
     */
    public void setAcronimo(String acronimo) {
        this.acronimo = acronimo;
    }
    
    
	/* **************************************************** *
     *        Metodi getter e setter per indirizzo          *
     * **************************************************** */
    
	/**
	 * Restituisce l'indirizzo della sede di un dipartimento
	 * 
	 * @return <code>indirizzo</code> - indirizzo della sede del dipartimento
	 */
	public String getIndirizzo() {
		return indirizzo;
	}

	/**
	 * Imposta l'indirizzo della sede di un dipartimento
	 * 
	 * @param indirizzoSede - indirizzo della sede da impostare
	 */
	public void setIndirizzo(String indirizzoSede) {
		indirizzo = indirizzoSede;
	}

	
    /* **************************************************** *
     *          Metodi getter e setter per livello          *
     * **************************************************** */
	
    /**
     * Restituisce il livello di una struttura
     * 
     * @return <code>livello</code> - il livello della struttura
     */
    public int getLivello() {
        return livello;
    }
    
    /**
     * Imposta il livello di una struttura
     * 
     * @param livello - il livello della struttura da impostare
     */
    public void setLivello(int livello) {
        this.livello = livello;
    }

    
    /* **************************************************** *
     *      Metodi getter e setter per struttura padre      *
     * **************************************************** */
    
    /**
     * Restituisce il diretto ascendente della struttura corrente
     * 
     * @return <code>DepartmentBean</code> - il padre della struttura corrente
     */
    public DepartmentBean getPadre() {
        return padre;
    }

    /**
     * Imposta il padre della struttura corrente
     * 
     * @param padre - il padre da impostare
     */
    public void setPadre(DepartmentBean padre) {
        this.padre = padre;
    }


    /* **************************************************** *
     *      Metodi getter e setter per strutture figlie     *
     * **************************************************** */
    
    /**
     * Restituisce una lista di diretti discendenti della struttura corrente
     * 
     * @return <code>Vector&lt;DepartmentBean&gt; - strutture figlie della struttura corrente
     */
    public Vector<DepartmentBean> getFiglie() {
        return figlie;
    }

    /**
     * Imposta le figlie della struttura corrente
     * 
     * @param figlie - le strutture figlie da impostare
     */
    public void setFiglie(Vector<DepartmentBean> figlie) {
        this.figlie = figlie;
    }
    
    
    /* **************************************************** *
     *    Metodi getter e setter per full time equivalent   *
     * **************************************************** */
    
    /**
     * Restituisce indice full time equivalent della struttura
     * 
     * @return <code>float</code> - full time equivalent
     */
    public float getFte() {
        return fte;
    }

    /**
     * Imposta fte della struttura
     * 
     * @param fte - indice full time equivalent della struttura da impostare
     */
    public void setFte(float fte) {
        this.fte = fte;
    }


    /* **************************************************** *
     *      Metodi getter e setter per persone allocate     *
     * **************************************************** */
    
    /**
     * Restituisce una lista di persone allocate sulla struttura corrente
     * 
     * @return <code>Vector&lt;PersonBean&gt; - persone allocate sulla struttura corrente
     */
    public ArrayList<PersonBean> getPersone() {
        return persone;
    }

    /**
     * Imposta le persone allocate sulla struttura corrente
     * 
     * @param persone - le persone allocate da impostare
     */
    public void setPersone(ArrayList<PersonBean> persone) {
        this.persone = persone;
    }


    /* **************************************************** *
     *  Metodi getter e setter per informazioni aggiuntive  *
     * **************************************************** */
    
    /**
     * Restituisce informazioni aggiuntive relative alla struttura corrente
     * 
     * @return <code>ItemBean - informazioni aggiunte relative alla struttura
     */
    public ItemBean getExtraInfo() {
        return extraInfo;
    }

    /**
     * Imposta informazioni aggiuntive relative alla struttura corrente
     * 
     * @param extraInfo - le informazioni aggiuntive da impostare
     */
    public void setExtraInfo(ItemBean extraInfo) {
        this.extraInfo = extraInfo;
    }

    
    /* **************************************************** *
     *   Metodi getter e setter per misure di mitigazione   *
     * **************************************************** */
    
    /**
     * Restituisce lista di misure di mitigazione eventualmente associate
     * alla struttura corrente
     * 
     * @return <code>ArrayList&lt;MeasureBean&gt;</code> - misure associate con eventuali dettagli di monitoraggio
     */
    public ArrayList<MeasureBean> getMisure() {
        return misure;
    }


    /**
     * Imposta lista di misure cui la struttura corrente &egrave; associata
     * 
     * @param misure - le misure associate da impostare
     */
    public void setMisure(ArrayList<MeasureBean> misure) {
        this.misure = misure;
    }

}
