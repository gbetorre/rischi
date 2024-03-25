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

import it.rol.exception.AttributoNonValorizzatoException;

/**
 * <p>Classe che serve a rappresentare una misura di prevenzione (o 
 * mitigazione) del rischio corruttivo.
 * 
 * Created on Tue 19 Mar 2024 02:11:05 PM CET
 * </p>
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class MeasureBean extends CodeBean {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = -814558787188144742L;
    /** Nome di questa classe.  */
    private final String FOR_NAME = "\n" + this.getClass().getName() + ": "; //$NON-NLS-1$
    /** Codice; ha la struttura <pre>MP.&lt;carattere&gt;.&lt;progressivo&gt;</pre> */
    private String codice;
    /** Risponde alla domanda: &quot;La misura &egrave, onerosa?&quot; */
    private String comportaSpese;
    /** Data ultima modifica */
    private Date dataUltimaModifica;
    /** Ora ultima modifica */
    private Time oraUltimaModifica;
    /** Autore ultima modifica */
    private int autoreUltimaModifica;
    /** Carattere della misura (Generale | Specifico ...) */
    private CodeBean carattere;
    /** Rilevazione che ha rilevato la misura */
    private CodeBean rilevazione;
    /** Tipologie della misura */
    private ArrayList<CodeBean> tipologie;
    /** Lista di strutture capofila che sovrintendono alla misura */
    private ArrayList<DepartmentBean> capofila;
    /** Lista di strutture capofila aggiuntive che sovrintendono alla misura */
    private ArrayList<DepartmentBean> capofila2;
    /** Lista di strutture capofila aggiuntive che sovrintendono alla misura */
    private ArrayList<DepartmentBean> capofila3;
    /** Lista di strutture gregarie che sovrintendono alla misura */
    private ArrayList<DepartmentBean> gregarie;

    
    /* ************************************************************************ *
     *                               Costruttori                                *
     * ************************************************************************ */
    
    /**
     * <p>Override del Costruttore di Default</p>
     * <p>Inizializza le variabili di classe a valori convenzionali</p>
     */
    public MeasureBean() {
        super();
        codice = null;
        comportaSpese = null;
        dataUltimaModifica = new Date(0);
        oraUltimaModifica = null;
        autoreUltimaModifica = BEAN_DEFAULT_ID;
        carattere = null;
        rilevazione = null;
        tipologie = null;
        capofila = capofila2 = capofila3 = gregarie = null;
    }


    /**
     * <p>Costruttore parametrizzato.</p>
     * @param o oggetto di cui propagare gli attributi
     * @throws AttributoNonValorizzatoException se un dato obbligatorio non e' stato valorizzato nel parametro
     */
    public MeasureBean(CodeBean o) throws AttributoNonValorizzatoException {
        super(o);
    }

    
    /* ********************************************************* *
     *              Metodi getter e setter per codice            *
     * ********************************************************* */
    
    /**
     * Restituisce il codice della misura
     * 
     * @return <code>codice</code> - codice della misura
     */
    public String getCodice() {
        return codice;
    }
    
    /**
     * Imposta il codice di una misura
     * 
     * @param codice - codice misura da impostare
     */
    public void setCodice(String codice) {
        this.codice = codice;
    }

    
    /* ********************************************************* *
     *     Metodi getter e setter per sostenibilità economica    *
     * ********************************************************* */
    
    /**
     * Risponde alla domanda: Comporta spese?
     * 
     * @return <code>comportaSpese</code> - risposta alla sostenibilit&agrave; economica
     */
    public String getComportaSpese() {
        return comportaSpese;
    }

    /**
     * Imposta la specificit&agrave; economica della misura
     *
     * @param comportaSpese risposta in merito all'onerosit&agrave; della misura
     */
    public void setComportaSpese(String comportaSpese) {
        this.comportaSpese = comportaSpese;
    }
    

    /* *********************************************************** *
     *       Metodi getter e setter per data ultima modifica       *
     * *********************************************************** */
    
    /**
     * Restituisce la data dell'ultima modifica della misura
     *
     * @return <code>java.util.Date</code> - data dell'ultima modifica
     */
    public Date getDataUltimaModifica() {
        return dataUltimaModifica;
    }

    /**
     * Imposta la data dell'ultima della misura
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
     * Restituisce l'ora dell'ultima modifica di una misura
     *
     * @return <code>java.sql.Time</code> - ora dell'ultima modifica
     */
    public Time getOraUltimaModifica() {
        return oraUltimaModifica;
    }

    /**
     * Imposta l'ora dell'ultima modifica di una misura
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
     * Restituisce l'autore dell'ultima modifica di una misura
     *
     * @return <code>int</code> - autore ultima modifica
     */
    public int getAutoreUltimaModifica() {
        return autoreUltimaModifica;
    }

    /**
     * Imposta l'autore dell'ultima modifica di una misura
     *
     * @param autoreUltimaModifica autore ultima modifica da impostare
     */
    public void setAutoreUltimaModifica(int autoreUltimaModifica) {
        this.autoreUltimaModifica = autoreUltimaModifica;
    }

    
    /* *************************************************** *
     *  Metodi getter e setter per carattere della misura  *
     * *************************************************** */
    
    /**
     * Restituisce un oggetto CodeBean che rappresenta il carattere della misura
     * @return <code>carattere</code> - carattere della misura
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    public CodeBean getCarattere() throws AttributoNonValorizzatoException {
        if (carattere == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo carattere non valorizzato!");
        }
        return carattere;
    }

    /**
     * Imposta il carattere con l'oggetto CodeBean passato
     * @param carattere - il carattere della misura
     */
    public void setCarattere(CodeBean carattere) {
        this.carattere = carattere;
    }
    
    
    /* *************************************************** *
     * Metodi getter e setter per rilevazione della misura *
     * *************************************************** */
    
    /**
     * Restituisce un oggetto CodeBean che rappresenta la rilevazione della misura
     * @return <code>rilevazione</code> - rilevazione che ha rilevato la misura
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
    
    
    /* ********************************************************* *
     *          Metodi getter e setter per tipologie             *
     * ********************************************************* */
    
    /**
     * Restituisce il vettore contenente i tipi della misura
     * 
     * @return <code>tipologie</code> - ArrayList contenente le tipologie della misura
     */
    public ArrayList<CodeBean> getTipologie() {
        return tipologie;
    }

    /**
     * Imposta le tipologie della misura
     * 
     * @param tipologie - ArrayList da impostare
     */
    public void setTipologie(ArrayList<CodeBean> tipologie) {
        this.tipologie = tipologie;
    }
    
    
    /* ********************************************************* *
     *        Metodi getter e setter per strutture capofila      *
     * ********************************************************* */
    
    /**
     * Restituisce il vettore contenente le strutture capofila
     * 
     * @return <code>strutture</code> - ArrayList contenente le strutture capofila
     */
    public ArrayList<DepartmentBean> getCapofila() {
        return capofila;
    }

    /**
     * Imposta le strutture che capofila
     * 
     * @param capofila - ArrayList da impostare
     */
    public void setCapofila(ArrayList<DepartmentBean> capofila) {
        this.capofila = capofila;
    }
    
    
    /* ********************************************************* *
     *      Metodi getter e setter per strutture capofila 2      *
     * ********************************************************* */
    
    /**
     * Restituisce il vettore contenente le strutture capofila
     * 
     * @return <code>strutture</code> - ArrayList contenente le strutture capofila
     */
    public ArrayList<DepartmentBean> getCapofila2() {
        return capofila2;
    }

    /**
     * Imposta le strutture che capofila
     * 
     * @param capofila2 - ArrayList da impostare
     */
    public void setCapofila2(ArrayList<DepartmentBean> capofila2) {
        this.capofila2 = capofila2;
    }
    
    
    /* ********************************************************* *
     *      Metodi getter e setter per strutture capofila 3      *
     * ********************************************************* */
    
    /**
     * Restituisce il vettore contenente le strutture capofila
     * 
     * @return <code>strutture</code> - ArrayList contenente le strutture capofila
     */
    public ArrayList<DepartmentBean> getCapofila3() {
        return capofila3;
    }

    /**
     * Imposta le strutture che capofila
     * 
     * @param capofila3 - ArrayList da impostare
     */
    public void setCapofila3(ArrayList<DepartmentBean> capofila3) {
        this.capofila3 = capofila3;
    }
    
    
    /* ********************************************************* *
     *       Metodi getter e setter per strutture gregarie       *
     * ********************************************************* */
    
    /**
     * Restituisce il vettore contenente le strutture gregarie
     * 
     * @return <code>strutture</code> - ArrayList contenente le strutture gregarie
     */
    public ArrayList<DepartmentBean> getGregarie() {
        return gregarie;
    }

    /**
     * Imposta le strutture che partecipano alla misura in qualit&agrave; di gregarie
     * 
     * @param gregarie - ArrayList da impostare
     */
    public void setStrutture(ArrayList<DepartmentBean> gregarie) {
        this.gregarie = gregarie;
    }
    
}
