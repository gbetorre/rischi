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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import it.rol.exception.AttributoNonValorizzatoException;

/**
 * <p>Classe usata per rappresentare un'intervista.</p>
 * 
 * @author <a href="mailto:giovanroberto.torre@univr.it">Giovanroberto Torre</a>
 */
public class InterviewBean extends CodeBean {

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
    /* ************************************************************************ *  
     *                   Dati identificativi dell'indicatore                    *
     * ************************************************************************ */
    // Inherited from its parent
    /* ************************************************************************ *  
     *                    Dati descrittivi dell'indicatore                      *
     * ************************************************************************ */
	/** Descrizione della misurazione */
    private String descrizione;
    /** Data di effettuazione dell'intervista  */
    private Date data;
    /** Flag di ultima intervista: permette assunzioni conseguenti */
    private boolean ultimo;
    /** Attributo identificativo della struttura intervistata */
    private DepartmentBean struttura;
    /** Attributo identificativo del processo anticorruttivo esaminato */
    private ProcessBean processo;
    /** Data ultima modifica */
    private Date dataUltimaModifica;
    /** Ora ultima modifica */
    private Time oraUltimaModifica;
    /** Autore ultima modifica */
    private String autoreUltimaModifica;
    /** Note alla misurazione */
    private String note;
    /* *******************************************************  *
     *                         Allegati                         *
     * ******************************************************** */
    /**
     * Struttura vettoriale di risposte fornite nel contesto dell'intervista
     */
    private ArrayList<QuestionBean> risposte;
    /**
     * HashMap con insertion order di indicatori ricavati in base 
     * alle risposte all'intervista indicizzati per codice
     */
    private LinkedHashMap<String, InterviewBean>  indicatori;
    
	
    /**
     * <p>Costruttore: inizializza i campi a valori di default.</p>
     */
	public InterviewBean() {
	    super();
		descrizione = note = null;
		data = new Date(0);
		ultimo = false;
		struttura = null;
		processo = null;
        dataUltimaModifica = new Date(0);
        oraUltimaModifica = null;
        autoreUltimaModifica = null;
        risposte = null;
        indicatori = null;
	}
	
	
    /**
     * <p>Costruttore parametrico: ammette come argomenti i valori obbligatori,
     * pi&uacute; alcuni riferimenti temporali necessari per identificare
     * l'intervista. Imposta gli altri campi a valori di default.</p>
     * 
     * @param id             identificativo dell'intervista
     * @param nome           etichetta identificativa dell'intervista
     * @param informativa    descrizione ampia dell'intervista
     * @param data           data del sodaggio
     * @param dataIntervista data del sondaggio
     * @param oraIntervista  ora del sondaggio
     */
    public InterviewBean(int id, String nome, String informativa, Date data, Date dataIntervista, Time oraIntervista) {
        super.setId(id);
        super.setNome(nome);
        super.setInformativa(informativa);
        super.setOrdinale(BEAN_DEFAULT_ID);
        descrizione = null;
        note = null;
        this.data = data;
        ultimo = false;
        struttura = null;
        processo = null;
        dataUltimaModifica = dataIntervista;
        oraUltimaModifica = oraIntervista;
        autoreUltimaModifica = null;
        risposte = null;
        indicatori = null;
    }


	/* ********************************************************* *
     *         Metodi getter e setter per descrizione            *
     * ********************************************************* */
	/**
	 * Restituisce la descrizione di una intervista  
	 * @return <code>descrizione</code> - descrizione della intervista
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * Imposta la descrizione di una intervista
	 * @param descrizione - descrizione da settare
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


    /* ********************************************************* *
     *              Metodi getter e setter per note              *
     * ********************************************************* */
    /**
     * Restituisce le note aggiuntive  
     * @return <code>note</code> - note descrittive dell'oggetto
     */
    public String getNote() {
        return note;
    }

    /**
     * Imposta le note aggiuntive
     * @param note - note da settare
     */
    public void setNote(String note) {
        this.note = note;
    }
    
    
    /* ***************************************************************** *
     *  Metodi getter e setter per data misurazione (risultato attuale)  *
     * ***************************************************************** */
    /**
     * Restituisce la data di effettuazione della intervista
     * 
     * @return <code>Date</code> - data misurazione
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e questo attributo non &egrave; stato valorizzato (dovrebbe essere un dato obbligatorio)
     */
    public Date getData() throws AttributoNonValorizzatoException {
        if (new Date(0).equals(data)) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo data non valorizzato! A quale data fa riferimento la intervista? Specificare una data.");
        }
        return data;
    }

    /**
     * @param data data intervista da impostare
     */
    public void setData(Date data) {
        this.data = data;
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
    
    
    /* ************************************************************************ *
     *  Metodi getter e setter per struttura misurata dall'intervista corrente  *
     * ************************************************************************ */
    /**
     * Restituisce il bean rappresentante la struttura di livello massimo collegata all'intervista corrente.
     * 
     * @return <code>DepartmentBean</code> - Struttura di livello massimo associata all'intervista corrente
     */
    public DepartmentBean getStruttura() {
        return struttura;
    }

    /**
     * Imposta il bean rappresentante la struttura sondata.
     * 
     * @param struttura - DepartmentBean collegato, da impostare
     */
    public void setStruttura(DepartmentBean struttura) {
        this.struttura = struttura;
    }
    
    
    /* ************************************************************************ *
     *   Metodi getter e setter per processo misurato dall'intervista corrente  *
     * ************************************************************************ */
    /**
     * Restituisce il bean rappresentante il processo di livello massimo collegato all'intervista corrente.
     * 
     * @return <code>ProcessBean</code> - Processo di livello massimo associato all'intervista corrente
     */
    public ProcessBean getProcesso() {
        return processo;
    }

    /**
     * Imposta il bean rappresentante il processo sondato.
     * 
     * @param processo - ProcessBean collegato, da impostare
     */
    public void setProcesso(ProcessBean processo) {
        this.processo = processo;
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
     *              Metodi getter e setter per risposte               *
     * ************************************************************** */
    /**
     * Restituisce l'elenco delle risposte fornite nel contesto dell'intervista 
     * 
     * @return <code>ArrayList&lt;QuestionBean&gt;</code> - elenco di risposte
     */
    public ArrayList<QuestionBean> getRisposte() {
        return risposte;
    }

    /**
     * Imposta l'elenco delle risposte fornite nel contesto dell'intervista
     * corrente
     * 
     * @param risposte ArrayList di QuestionBean da impostare
     */
    public void setRisposte(ArrayList<QuestionBean> risposte) {
        this.risposte = risposte;
    }
    

    /* ********************************************************* *
     *          Metodi getter e setter per indicatori            *
     * ********************************************************* */
    /**
     * Restituisce la lista ordinata di indicatori calcolati per l'intervista corrente, indicizzati per codice
     * @return <code>LjnkedHashMap&lt;String&comma;&nbsp;InterviewBean&gt;</code> - indicatori calcolati sulle risposte dell'intervista corrente
     */
    public LinkedHashMap<String, InterviewBean> getIndicatori() {
        return indicatori;
    }

    /**
     * Imposta la lista ordinata di indicatori calcolati per l'intervista corrente, indicizzati per codice
     * @param indicatori - tabella degli indicatori, indicizzati per codice, da settare
     */
    public void setIndicatori(LinkedHashMap<String, InterviewBean> indicatori) {
        this.indicatori = indicatori;
    }

}