/*
 *   Process Mapping Software: Modulo Applicazione web per la visualizzazione
 *   delle schede di indagine su allocazione risorse dell'ateneo,
 *   per la gestione dei processi on line (pms).
 *
 *   Process Mapping Software (pms)
 *   web applications to publish, and manage,
 *   processes, assessment and skill information.
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

import it.rol.exception.AttributoNonValorizzatoException;

import java.io.Serializable;
import java.sql.Date;
import java.util.Vector;


/**
 * <p>PersonBean &egrave; l'oggetto che rappresenta una persona fisica.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class PersonBean implements Serializable {
    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = -3415439696526030885L;
    /**
     *  Nome di questa classe.
     *  Viene utilizzato per contestualizzare i messaggi di errore.
     */
    private final String FOR_NAME = "\n" + this.getClass().getName() + ": "; //$NON-NLS-1$
    /* ************************************************************************ *
     *                    Dati identificativi della persona                     *
     * ************************************************************************ */
    /** Attributo identificativo della persona */
    protected int id;
    /** Attributo identificativo del nome della persona */
    private String nome;
    /** Attributo identificativo del cognome della persona */
    private String cognome;
    /** Attributo che memorizza la data di nascita della persona */
    private Date dataNascita;
    /** Sesso attuale della persona */
    private String sesso;
    /** Codice fiscale della persona */
    private String codiceFiscale;
    /** Specifica se la persona &egrave; obliata */
    private boolean oblio;
    /** Flag per specificare se la persona stessa debba essere visibile nel contesto della struttura/ufficio di appartenenza */
    private boolean mostraPersona;
    /** E&ndash;mail della persona */
    private String email;
    /** Et&agrave; */
    private int eta;
    /** Ritratto della persona */
    private Vector<FileDocBean> foto;
    /** Utente loggato */
    private int usrId;
    /** Url della pagina personale in caso di necessit&agrave; di mostrare la propria persona in ulteriore contesto */
    private String urlPersonalPage;
    /** link alla pagina della persona (dipende dal contesto) */
    private String url;
    /** Flag per specificare se la persona lavora in ateneo a tempo pieno o parziale */
    private boolean tempoPieno;
    /** Note */
    private String note;
    /** Lista di ruoli applicativi della persona */
    private Vector<CodeBean> ruoli;
    /** Campo note contenente una descrizione testuale del ruolo giuridico della persona */
    private String ruoloGiuridico;
    /** Codice ruolo giuridico della persona */
    private String codRuoloGiuridico;
    /** Codice area funzionale della persona  */
    private String codAreaFunzionale;
    /** Flag specificante se si tratta di una persona con incarico di responsabilit&agrave; */
    private boolean responsabile;
    /** String specificante l'eventuale responsabilit&agrave; organizzativa */
    private String livResponsabilitaOrganizzativa;
    /** String specificante l'eventuale funzione specialistica  */
    private String livFunzioneSpecialistica;
    /** String specificante l'eventuale livello di tecnico di laboratorio */
    private String livTecnicoLaboratorio;
    /** Identificativo del dipartimento cui la persona afferisce */
    protected int idDipartimento;
    /** Nome del dipartimento cui la persona afferisce */
    private String dipartimento;
    /** Indirizzo web del dipartimento cui la persona afferisce */
    protected String urlDipartimento;


    /**
     * <p>Costruttore: inizializza i campi a valori di default.</p>
     */
    public PersonBean() {
        id = idDipartimento = CodeBean.BEAN_DEFAULT_ID;
        nome = cognome = codiceFiscale = email = urlPersonalPage = null;
        dataNascita = new Date(0);
        eta = CodeBean.BEAN_DEFAULT_ID;
        responsabile = oblio = false;
        mostraPersona = tempoPieno = true;
        sesso = null;
        url = null;
        note = null;
        foto = null;
        usrId = CodeBean.BEAN_DEFAULT_ID;
        ruoli = null;
        ruoloGiuridico = null;
        codRuoloGiuridico = null;
        codAreaFunzionale = null;
        livResponsabilitaOrganizzativa = livFunzioneSpecialistica = livTecnicoLaboratorio = null;
        dipartimento = urlDipartimento = null;
    }


    //TODO COMMENTI
    /**
     * @return
     */
    public boolean isDataNascitaEmpty() {
        return (new Date(0).equals(dataNascita) || (dataNascita == null));
    }

    /**
     * @return
     */
    public boolean isDipartimentoEmpty() {
           return (dipartimento == null || dipartimento.equals(""));
    }

    /**
     * @return
     */
    public boolean isUrlDipartimentoEmpty() {
           return (urlDipartimento == null || urlDipartimento.equals(""));
    }

    /**
     * @return
     */
    public boolean isTempoPieno() {
        return tempoPieno;
    }

    /**
     * @return id
     * @throws AttributoNonValorizzatoException
     */
    public int getId() throws AttributoNonValorizzatoException {
        if (id == -2) {
            throw new AttributoNonValorizzatoException("PersonBean: attributo id Persona non valorizzato!");
        }
        return this.id;
    }

    /**
     * @return nome
     * @throws AttributoNonValorizzatoException
     */
    public String getNome() throws AttributoNonValorizzatoException {
        if (nome == null) {
            throw new AttributoNonValorizzatoException("PersonBean: attributo nome non valorizzato!");
        }
        return this.nome;
    }

    /**
     * @return cognome
     * @throws AttributoNonValorizzatoException
     */
    public String getCognome() throws AttributoNonValorizzatoException {
        if (cognome == null) {
            throw new AttributoNonValorizzatoException("PersonBean: attributo cognome non valorizzato!");
        }
        return this.cognome;
    }

	/**
     * @return dataNascita
	 * @throws AttributoNonValorizzatoException
     */
    public Date getDataNascita() throws AttributoNonValorizzatoException {
        if (new Date(0).equals(dataNascita)) {
            throw new AttributoNonValorizzatoException(FOR_NAME + ": attributo dataNascita non valorizzato!");
        }
        return this.dataNascita;
    }

    /**
     * @return codiceFiscale
     */
    public String getCodiceFiscale() {
        return this.codiceFiscale;
    }

    /**
     * @return email
     * @throws AttributoNonValorizzatoException
     */
    public String getEmail() throws AttributoNonValorizzatoException {
        if (email == null) {
            throw new AttributoNonValorizzatoException("PersonBean: attributo email non valorizzato!");
        }
        return this.email;
    }

    /**
     * @return eta
     * @throws AttributoNonValorizzatoException
     */
    public int getEta() throws AttributoNonValorizzatoException {
        if (eta == CodeBean.BEAN_DEFAULT_ID) {
            throw new AttributoNonValorizzatoException("PersonBean: attributo eta non valorizzato!");
        }
        return this.eta;
    }

    /**
     * @return ruoloGiuridico
     */
    public String getRuoloGiuridico() {
        return this.ruoloGiuridico;
    }

    /**
     * @return idDipartimento
     * @throws AttributoNonValorizzatoException
     */
    public int getIdDipartimento() throws AttributoNonValorizzatoException {
        if (idDipartimento == -2) {
            throw new AttributoNonValorizzatoException("PersonBean: attributo idDipartimento non valorizzato!");
        }
        return this.idDipartimento;
    }

    /**
     * @return dipartimento
     * @throws AttributoNonValorizzatoException
     */
    public String getDipartimento() throws AttributoNonValorizzatoException {
        if (dipartimento == null) {
            throw new AttributoNonValorizzatoException("PersonBean: attributo dipartimento non valorizzato!");
        }
        return this.dipartimento;
    }

    /**
     * @return note
     * @throws AttributoNonValorizzatoException
     */
    public String getNote() throws AttributoNonValorizzatoException {
        if (note == null) {
            throw new AttributoNonValorizzatoException("PersonBean: attributo note non valorizzato!");
        }
        return this.note;
    }

    /**
     * @return urlPersonalPage
     * @throws AttributoNonValorizzatoException
     */
    public String getUrlPersonalPage() throws AttributoNonValorizzatoException {
        if (urlPersonalPage == null) {
            throw new AttributoNonValorizzatoException("PersonBean: attributo urlPersonalPage non valorizzato!");
        }
        return this.urlPersonalPage;
    }

    /**
     * @return urlDipartimento
     *
     */
    public String getUrlDipartimento() {
        return this.urlDipartimento;
    }

    /**
     * @param cognome
     */
    public void setCognome(String string) {
        cognome = string;
    }

    /**
     * @param dataNascita
     */
    public void setDataNascita(Date date) {
        dataNascita = date;
    }

    /**
     * @param codiceFiscale
     */
    public void setCodiceFiscale(String string) {
        codiceFiscale = string;
    }

    /**
     * @param email
     */
    public void setEmail(String string) {
        email = string;
    }

    /**
     * @param eta
     */
    public void setEta(int eta) {
        this.eta = eta;
    }

    /**
     * @param id
     */
    public void setId(int i) {
        id = i;
    }

    /**
     * @param idDipartimento
     */
    public void setIdDipartimento(int i) {
        idDipartimento = i;
    }

    /**
     * @param ruoloGiuridico
     */
    public void setRuoloGiuridico(String string) {
        ruoloGiuridico = string;
    }

    /**
     * @param nome
     */
    public void setNome(String string) {
        nome = string;
    }

    /**
     * @param dipartimento
     */
    public void setDipartimento(String string) {
        dipartimento = string;
    }

    /**
     * @param note
     */
    public void setNote(String string) {
        note = string;
    }

    /**
     * @param tempoPieno Valori '1' o '0'
     */
    public void setTempoPieno(char v) {
        if (v == '1') {
            this.tempoPieno = true;
        } else {
            this.tempoPieno = false;
        }
    }


    /**
     * @param tempoPieno.
     */
    public void setTempoPieno(boolean v) {
        this.tempoPieno = v;
    }


    /**
     * @param urlPersonalPage
     */
    public void setUrlPersonalPage(String string) {
        urlPersonalPage = string;
    }


    /**
     * @param urlDipartimento
     *
     */
    public void setUrlDipartimento(java.lang.String urlDipartimento) {
        this.urlDipartimento = urlDipartimento;
    }


    /**
     * Getter for property sesso.
     * @return Value of property sesso.
     */
    public String getSesso() throws AttributoNonValorizzatoException {
        if (sesso == null) {
            throw new AttributoNonValorizzatoException("PersonBean: attributo sesso non valorizzato!");
        }
        return this.sesso;
    }

    /**
     * Setter for property sesso.
     * @param sesso New value of property sesso.
     */
    public void setSesso(String sesso) {
        if (!sesso.equals("M") && !sesso.equals("F") && !sesso.equals("m") && !sesso.equals("f"))
            this.sesso = "m";
        else
            this.sesso = sesso;
    }

    /**
     * @return mostraPersona
     */
    public boolean isMostraPersona() {
        return mostraPersona;
    }

    /**
     * @param mostraPersona
     */
    public void setMostraPersona(boolean b) {
        mostraPersona = b;
    }

    /**
     * @return responsabile
     */
    public boolean isResponsabile() {
        return responsabile;
    }

    /**
     * @param responsabile
     */
    public void setResponsabile(boolean b) {
        responsabile = b;
    }

    /**
     * @param fotoFileDoc
     */
    public void setFoto(Vector<FileDocBean> fotoFileDoc) {
        foto = fotoFileDoc;
    }

    public Vector<FileDocBean> getFoto() {
        return foto;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

	/**
	 * @return the user id
	 */
	public int getUsrId() {
		return usrId;
	}

	/**
	 * @param usrId the user id to set
	 */
	public void setUsrId(int usrId) {
		this.usrId = usrId;
	}

	/**
	 * @return the oblio
	 */
	public boolean isOblio() {
		return oblio;
	}

	/**
	 * @param oblio the oblio to set
	 */
	public void setOblio(boolean oblio) {
		this.oblio = oblio;
	}

    /**
     * @return codAreaFunzionale
     */
    public String getCodAreaFunzionale() {
        return codAreaFunzionale;
    }

    /**
     * @param codAreaFunzionale
     */
    public void setCodAreaFunzionale(String codAreaFunzionale) {
        this.codAreaFunzionale = codAreaFunzionale;
    }

    /**
     * @return codRuoloGiuridico
     */
    public String getCodRuoloGiuridico() {
        return codRuoloGiuridico;
    }

    /**
     * @param codRuoloGiuridico
     */
    public void setCodRuoloGiuridico(String codRuoloGiuridico) {
        this.codRuoloGiuridico = codRuoloGiuridico;
    }

    /**
     * @return the livResponsabilitaOrganizzativa
     */
    public String getLivResponsabilitaOrganizzativa() {
        return livResponsabilitaOrganizzativa;
    }

    /**
     * @param livResponsabilitaOrganizzativa the livResponsabilitaOrganizzativa to set
     */
    public void setLivResponsabilitaOrganizzativa(String livResponsabilitaOrganizzativa) {
        this.livResponsabilitaOrganizzativa = livResponsabilitaOrganizzativa;
    }

    /**
     * @return the livFunzioneSpecialistica
     */
    public String getLivFunzioneSpecialistica() {
        return livFunzioneSpecialistica;
    }

    /**
     * @param livFunzioneSpecialistica the livFunzioneSpecialistica to set
     */
    public void setLivFunzioneSpecialistica(String livFunzioneSpecialistica) {
        this.livFunzioneSpecialistica = livFunzioneSpecialistica;
    }

    /**
     * @return the livTecnicoLaboratorio
     */
    public String getLivTecnicoLaboratorio() {
        return livTecnicoLaboratorio;
    }

    /**
     * @param livTecnicoLaboratorio the livTecnicoLaboratorio to set
     */
    public void setLivTecnicoLaboratorio(String livTecnicoLaboratorio) {
        this.livTecnicoLaboratorio = livTecnicoLaboratorio;
    }

    /**
     * @return the ruoli
     */
    public Vector<CodeBean> getRuoli() {
        return ruoli;
    }

    /**
     * @param ruoli the ruoli to set
     */
    public void setRuoli(Vector<CodeBean> ruoli) {
        this.ruoli = ruoli;
    }



}
