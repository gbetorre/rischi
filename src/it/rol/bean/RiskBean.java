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
    /* *************************************************************************** *  
     *                      Dati identificativi del rischio                        *
     * *************************************************************************** */
    /** Impatto del rischio */
    private String impatto;
    /** Livello del rischio */
    private String livello;
    /** Stato del rischio */
    private String stato;
    /** Urgenza del rischio */
    private boolean urgenza;
	
    
    /**
     * <p>Costruttore: inizializza i campi a valori di default.</p>
     */
    public RiskBean() {
    	super();
    	impatto = livello = null;
    	stato = null;
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
	 * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e livello non &egrave; stato valorizzato (&egrave; un dato obbligatorio) 
	 * oppure livello non &egrave; stato valorizzato correttamente
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
	 * oppure stato non &egrave; stato valorizzato correttamente
	 */
	public String getStato() throws AttributoNonValorizzatoException {
		if (stato == null) {
			throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo stato non valorizzato!");
		}/*
		else if (!STATO_RISCHIO_AS_LIST.contains(stato)) {
			throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo stato non valorizzato correttamente!");
		}*/
		else {
			return stato;
		}
	}

	/**
	 * Imposta il stato di rischio
	 * @param stato - stato del rischio
	 */
	public void setStato(String stato) {
		this.stato = stato;
	}
	
	
	/* ********************************************************* *
     *           Metodi getter e setter per urgenza              *
     * ********************************************************* */
    /**
     * Restituisce true se il rischio &egrave; urgenze - false (default) se non lo &egrave;
     * 
     * @return <code>Boolean</code> - true/false rischio urgente
     */
    public boolean isUrgenza() {
        return urgenza;
    }

    /**
     * Imposta l'urgenza del rischio
     *  
     * @param urgenza - urgenza true/false da impostare
     */
    public void setUrgenza(boolean urgenza) {
        this.urgenza = urgenza;
    }
	
}
