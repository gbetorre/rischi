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
 *   Copyright (C) renewed 2023 Giovanroberto Torre
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

import java.util.AbstractList;

import it.rol.exception.AttributoNonValorizzatoException;

/**
 * <p>Classe usata per rappresentare un rischio.</p>
 * 
 * @author <a href="mailto:giovanroberto.torre@univr.it">Giovanroberto Torre</a>
 */
public class RiskBean extends CodeBean {

	/**
	 * La serializzazione necessita di dichiarare una costante di tipo long
	 * identificativa della versione seriale. 
	 * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
	 * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
	 */
	private static final long serialVersionUID = 5746067585544185299L;
	/**
     *  Nome di questa classe. 
     *  Viene utilizzato per contestualizzare i messaggi di errore.
     */
    private final String FOR_NAME = "\n" + this.getClass().getName() + ": "; //$NON-NLS-1$
    /** Impatto del rischio */
    private String impatto;
    /** Livello del rischio */
    private String livello;
    /** Stato del rischio */
    private String stato;
    /** Urgenza del rischio */
    private boolean urgente;
    /** Processi esposti al rischio */
    private AbstractList<ProcessBean> processi;
    
	
    /* ************************************************************ *  
     *                         Costruttori                          *
     * ************************************************************ */
    /**
     * <p>Costruttore senza parametri: inizializza i campi a valori di default.</p>
     */
    public RiskBean() {
    	super();
    	impatto = livello = null;
    	stato = null;
    	urgente = false;
    	processi = null;
    }
	
	
	/* ********************************************************* *
     *           Metodi getter e setter per impatto              *
     * ********************************************************* */
	/**
	 * Restituisce l'impatto del rischio
	 * @return <code>impatto</code> - impatto del rischio
	 * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e impatto non &egrave; stato valorizzato (&egrave; un dato obbligatorio) 
	 * oppure impatto non &egrave; stato valorizzato correttamente
	 */
	public String getImpatto() throws AttributoNonValorizzatoException {
		if (impatto == null) {
			throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo impatto non valorizzato!");
		}/*
		else if (!LIVELLI_RISCHIO_AS_LIST.contains(impatto)) {
			throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo impatto non valorizzato correttamente!");
		}*/
		else {
			return impatto;
		}
	}

	/**
	 * Imposta l'impatto di rischio
	 * @param impatto - impatto del rischio
	 */
	public void setImpatto(String impatto) {
		this.impatto = impatto;
	}
	
	
	/* ********************************************************* *
     *           Metodi getter e setter per livello              *
     * ********************************************************* */
	/**
	 * Restituisce il livello del rischio
	 * @return <code>livello</code> - livello del rischio
	 * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e livello non &egrave; stato valorizzato (&egrave; un dato obbligatorio) oppure se l'attributo livello non risulta valorizzato correttamente
	 */
	public String getLivello() throws AttributoNonValorizzatoException {
		if (livello == null) {
			throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo livello non valorizzato!");
		}/*
		else if (!LIVELLI_RISCHIO_AS_LIST.contains(livello)) {
			throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo livello non valorizzato correttamente!");
		}*/
		else {
			return livello;
		}
	}

	/**
	 * Imposta il livello di rischio
	 * @param livello - livello del rischio
	 */
	public void setLivello(String livello) {
		this.livello = livello;
	}


	/* ********************************************************* *
     *            Metodi getter e setter per stato               *
     * ********************************************************* */
	/**
	 * Restituisce lo stato del rischio
	 * @return <code>stato</code> - stato del rischio
	 * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e stato non &egrave; stato valorizzato (&egrave; un dato obbligatorio) 
	 */
	public String getStato() throws AttributoNonValorizzatoException {
		if (stato == null) {
			throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo stato non valorizzato!");
		}
        return stato;
	}

	/**
	 * Imposta lo stato di rischio
	 * @param stato - stato del rischio
	 */
	public void setStato(String stato) {
		this.stato = stato;
	}
	
	
	/* ********************************************************* *
     *           Metodi getter e setter per urgenza              *
     * ********************************************************* */
    /**
     * Restituisce true se il rischio &egrave; urgente - false (default) se non lo &egrave;
     * 
     * @return <code>Boolean</code> - true/false rischio urgente
     */
    public boolean isUrgente() {
        return urgente;
    }

    /**
     * Imposta l'urgenza del rischio
     *  
     * @param urgente - urgenza true/false da impostare
     */
    public void setUrgente(boolean urgente) {
        this.urgente = urgente;
    }

    
    /* ********************************************************* *
     *          Metodi getter e setter per processi              *
     * ********************************************************* */
    /**
     * Restituisce una lista di processi che risultano esposti
     * al rischio corrente;
     * non solleva un'eccezione se questo attributo &egrave;
     * non significativo (perch&eacute; i rischi potrebbero anche essere
     * previsti nel registro dei rischio ma non attualmente applicati 
     * all'insieme dei processi mappati ai fini anticorruttivi).
     *
     * @return <code>processi</code> - lista di processi esposti al rischio corrente
     */
    public AbstractList<ProcessBean> getProcessi() {
        return processi;
    }

    /**
     * Imposta i processi esposti al rischio corrente.
     *
     * @param processi - processi esposti al rischio corrente, da impostare
     */
    public void setProcessi(AbstractList<ProcessBean> processi) {
        this.processi = processi;
    }
	
}
