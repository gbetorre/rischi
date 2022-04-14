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

package it.rol.bean;

import java.io.Serializable;

import it.rol.Query;
import it.rol.exception.AttributoNonValorizzatoException;

/**
 * <p>Classe usata per rappresentare un rischio.</p>
 * 
 * @author <a href="mailto:andrea.tonel@studenti.univr.it">Andrea Tonel</a>
 */
public class RiskBean extends CodeBean implements Serializable {

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
	 * @throws it.alma.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e impatto non &egrave; stato valorizzato (&egrave; un dato obbligatorio) 
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
	 * @throws it.alma.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e livello non &egrave; stato valorizzato (&egrave; un dato obbligatorio) 
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
	 * @throws it.alma.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e stato non &egrave; stato valorizzato (&egrave; un dato obbligatorio) 
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
