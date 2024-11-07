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

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import it.rol.exception.AttributoNonValorizzatoException;

/**
 * <p>Classe usata per rappresentare un'attivit&agrave; (o fase) di processo.</p>
 * 
 * @author <a href="mailto:giovanroberto.torre@univr.it">Giovanroberto Torre</a>
 */
public class ActivityBean extends CodeBean {

	/**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
	 */
	private static final long serialVersionUID = -90515316806359446L;
	/**
     *  Nome di questa classe. 
     *  Viene utilizzato per contestualizzare i messaggi di errore.
     */
    private final String FOR_NAME = "\n" + this.getClass().getName() + ": "; //$NON-NLS-1$
    /* $NON-NLS-1$ silence a warning that Eclipse emits when it encounters string literals
        The idea is that UI messages should not be embedded as string literals, 
        but rather sourced from a resource file (so that they can be translated, proofed, etc).*/
    /* ************************************************************************ *  
     *                     Dati descrittivi dell'attività                       *
     * ************************************************************************ */
    /** Descrizione dell'attivit&agrave; */
    private String descrizione;
    /** Codice dell'attivit&agrave; */
    private String codice;
    /** Data inizio dell'attivit&agrave; */
    private Date dataInizio;
    /** Data fine dell'attivit&agrave; */
    private Date dataFine;
    /** Data inizio di attesa dell'attivit&agrave; */
    private Date dataInizioAttesa;
    /** Data fine di attesa dell'attivit&agrave; */
    private Date dataFineAttesa;
    /** Data di inizio effettiva dell'attivit&agrave; */
    private Date dataInizioEffettiva;
    /** Data fine dell'attivit&agrave; effettiva */
    private Date dataFineEffettiva;
    /** Giorni uomo previsti per l'attivit&agrave; */
    private int guPrevisti;
    /** Giorni uomo effettivi per l'attivit&agrave; */
    private int guEffettivi;
    /** Giorni uomo rimanenti per l'attivit&agrave; */
    private int guRimanenti;
    /** Obbligatoriet&agrave; della fase */
    private boolean mandatory;
    /** Milestone attivit&agrave; */
    private boolean milestone;
    /* *********************************************** *
     *            Strutture dell'attività              *
     * *********************************************** */
    /** Vector di strutture che partecipano all'attivit&agrave; */
    private ArrayList<DepartmentBean> strutture;
    /** Vector di soggetti/enti esterni che partecipano all'attivit&agrave; */
    private Vector<DepartmentBean> soggetti;
    /* *********************************************** *
     *           Riferimenti ad altre entità           *
     * *********************************************** */
    /** Riferimento al livello di complessit&agrave; dell'attivit&agrave; */
    private int idComplessita;
    /** Riferimento allo stato in cui si trova l'attivit&agrave; */
    private int idStato;
    /** Stato esatto in cui si trova l'attivit&agrave; */
    private CodeBean stato;
    /** Indicatore di monitoraggio collegato alla fase di attuazione della misura */
    private IndicatorBean indicatore;
    
    
    /**
     * <p>Costruttore: inizializza i campi a valori di default.</p>
     */
    public ActivityBean() {
        super();
    	descrizione = codice = null;
    	dataInizio = dataFine = dataInizioAttesa = dataFineAttesa = dataInizioEffettiva = dataFineEffettiva = new Date(0);
    	guPrevisti = guEffettivi = guRimanenti = -2;
    	mandatory = milestone = false;
    	strutture = null; 
    	soggetti = null;
    	idComplessita = idStato = -2;
    	stato = null;
    	indicatore = null;
    }
    
    
    /**
     * <p>Costruttore da ActivityBean</p>
     * <p>Inizializza le variabili di classe a valori presi da
     * un ActivityBean passato come argomento</p>
     *  
     * @param old il ActivityBean di cui si vogliono recuperare i valori
     * @throws AttributoNonValorizzatoException  se manca qualche valore di attributo considerato obbligatorio
     */
    public ActivityBean(final ActivityBean old) 
                 throws AttributoNonValorizzatoException {
        super.setId(old.getId());
        super.setNome(old.getNome());
        this.descrizione = old.getDescrizione();
        this.codice = old.getCodice();
        this.dataInizio = old.getDataInizio();
        this.dataFine = old.getDataFine();
        this.dataInizioAttesa = old.getDataInizioAttesa();
        this.dataFineAttesa = old.getDataFineAttesa();
        this.dataInizioEffettiva = old.getDataInizioEffettiva();
        this.dataFineEffettiva = old.getDataFineEffettiva();
        this.guPrevisti = old.getGuPrevisti();
        this.guEffettivi = old.getGuEffettivi();
        this.guRimanenti = old.getGuRimanenti();
        this.mandatory = old.isMandatory();
        this.milestone = old.isMilestone();
        this.strutture = old.getStrutture();
        this.soggetti = old.getSoggetti();
        this.idComplessita = old.getIdComplessita();
        this.idStato = old.getIdStato();
        this.stato = old.getStato();
    }
    
	
	/* ************************************************************* *
     *           Metodi getter e setter per descrizione              *
     * ************************************************************* */
	/**
	 * Restituisce la descrizione di un'attivit&agrave;
	 * 
	 * @return <code>descrizione</code> - descrizione dell'attivit&agrave;
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * Imposta la descrizione di un'attivit&agrave;
	 * 
	 * @param descrizione - descrizione da settare
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


    /* ********************************************************* *
     *              Metodi getter e setter per codice            *
     * ********************************************************* */
    /**
     * Restituisce il codice dell'attivit&agrave;
     * 
     * @return <code>codice</code> - codice dell'attvit&agrave;
     */
    public String getCodice() {
        return codice;
    }
    
    /**
     * Imposta il codice di un'attivit&agrave;
     * 
     * @param codice - codice attivit&agrave; da impostare
     */
    public void setCodice(String codice) {
        this.codice = codice;
    }
    
	
	/* ************************************************************* *
     *           Metodi getter e setter per dataInizio               *
     * ************************************************************* */
	/**
	 * Restituisce la data inizio di un'attivit&agrave;
	 * 
	 * @return <code>dataInizio</code> - data inizio di un'attivit&agrave;
	 */
	public Date getDataInizio() {
		return dataInizio;
	}

	/**
	 * Imposta la data di inizio di un'attivit&agrave;
	 * 
	 * @param dataInizio - data di inizio da impostare
	 */
	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	
	/* ************************************************************* *
     *            Metodi getter e setter per dataFine                *
     * ************************************************************* */
	/**
	 * Restituisce la data fine di un'attivit&agrave;
	 * 
	 * @return <code>dataFine</code> - data di fine di un'attivit&agrave;
	 * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e dataFine non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
	 */
	public Date getDataFine() throws AttributoNonValorizzatoException {
		if (new Date(0).equals(dataFine)) {
			throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo dataFine non valorizzato!");
		}
		return dataFine;
	}

	/**
	 * Imposta la data di fine di un'attivit&agrave;
	 * 
	 * @param dataFine - data di fine da impostare
	 */
	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}

	
	/* ************************************************************* *
     *        Metodi getter e setter per dataInizioAttesa            *
     * ************************************************************* */
	/**
	 * Restituisce la data di inizio attesa di un'attivit&agrave;
	 * 
	 * @return <code>dataInizioAttesa</code> - data inizio di attesa di un'attivit&agrave;
	 */
	public Date getDataInizioAttesa() {
		return dataInizioAttesa;
	}

	/**
	 * Imposta la data di inizio attesa di un'attivit&agrave;
	 * 
	 * @param dataInizioAttesa - data di inizio di un'attivit&agrave;
	 */
	public void setDataInizioAttesa(Date dataInizioAttesa) {
		this.dataInizioAttesa = dataInizioAttesa;
	}

	
	/* ************************************************************* *
     *         Metodi getter e setter per dataFineAttesa             *
     * ************************************************************* */
	/**
	 * Restituisce la data di fine attesa di un'attivit&agrave;
	 * 
	 * @return <code>dataFineAttesa</code> - data fine di attesa di un'attivit&agrave;
	 */
	public Date getDataFineAttesa() {
		return dataFineAttesa;
	}

	/**
	 * Imposta la data di fine attesa di un'attivit&agrave;
	 * 
	 * @param dataFineAttesa - data di fine attesa di un'attivit&agrave;
	 */
	public void setDataFineAttesa(Date dataFineAttesa) {
		this.dataFineAttesa = dataFineAttesa;
	}


	/* ************************************************************** *
     *       Metodi getter e setter per dataInizioEffettiva           *
     * ************************************************************** */
	/**
	 * Restituisce la data inizio effettiva di un'attivit&agrave;
	 * 
	 * @return <code>dataInizioEffettiva</code> - data di inizio effettiva di un'attivit&agrave;
	 */
	public Date getDataInizioEffettiva() {
		return dataInizioEffettiva;
	}

	/**
	 * Imposta la data inizio effettiva di un'attivit&agrave;
	 * 
	 * @param dataInizioEffettiva - data di inizio effettiva da impostare
	 */
	public void setDataInizioEffettiva(Date dataInizioEffettiva) {
		this.dataInizioEffettiva = dataInizioEffettiva;
	}


	/* ************************************************************** *
     *        Metodi getter e setter per dataFineEffettiva            *
     * ************************************************************** */
	/**
	 * Restituisce la data fine effettiva di un'attivit&agrave;
	 * 
	 * @return <code>dataFineEffettiva</code> - data di fine effettiva di un'attivit&agrave;
	 */
	public Date getDataFineEffettiva() {
		return dataFineEffettiva;
	}

	/**
	 * Imposta la data fine effettiva di un'attivit&agrave;
	 * 
	 * @param dataFineEffettiva - data di fine effettiva da impostare
	 */
	public void setDataFineEffettiva(Date dataFineEffettiva) {
		this.dataFineEffettiva = dataFineEffettiva;
	}

	
	/* ********************************************************* *
     *         Metodi getter e setter per guPrevisti             *
     * ********************************************************* */
	/**
	 * Restituisce i giorni uomo previsti per l'attivit&agrave;
	 * 
	 * @return <code>guPrevisti</code> - giorni uomo previsti per l'attivit&agrave;
	 */
	public int getGuPrevisti() {
		return guPrevisti;
	}

	/**
	 * Imposta i giorni uomo previsti per l'attivit&agrave;
	 * 
	 * @param guPrevisti - giorni uomo previsti da impostare
	 */
	public void setGuPrevisti(int guPrevisti) {
		this.guPrevisti = guPrevisti;
	}

	
	/* ********************************************************* *
     *         Metodi getter e setter per guEffettivi            *
     * ********************************************************* */
	/**
	 * Restituisce i giorni uomo effettivi per l'attivit&agrave;
	 * 
	 * @return <code>guEffettivi</code> - giorni uomo effettivi per l'attivit&agrave;
	 */
	public int getGuEffettivi() {
		return guEffettivi;
	}

	/**
	 * Imposta i giorni uomo effettivi per l'attivit&agrave;
	 * 
	 * @param guEffettivi - giorni uomo effettivi da impostare
	 */
	public void setGuEffettivi(int guEffettivi) {
		this.guEffettivi = guEffettivi;
	}


	/* ********************************************************* *
     *         Metodi getter e setter per guRimanenti            *
     * ********************************************************* */
	/**
	 * Restituisce i giorni uomo rimanenti per l'attivit&agrave;
	 * 
	 * @return <code>guRimanenti</code> - giorni uomo rimanenti per l'attivit&agrave;
	 */
	public int getGuRimanenti() {
		return guRimanenti;
	}

	/**
	 * Imposta i giorni uomo rimanenti per l'attivit&agrave;
	 * 
	 * @param guRimanenti - giorni uomo rimanenti da impostare
	 */
	public void setGuRimanenti(int guRimanenti) {
		this.guRimanenti = guRimanenti;
	}

	
    /* ********************************************************* *
     *         Metodi getter e setter per obbligatorieta'        *
     * ********************************************************* */
    /**
     * Restituisce true se l'attivit&agrave; &egrave; obbligatoria - false (default) se non lo &egrave;
     * 
     * @return <code>mandatory</code> - true/false attivit&agrave; obbligatoria
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Imposta obbligatoriet&agrave; della fase
     *  
     * @param mandatory - obbligatoriet&agrave; true/false da impostare
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    
    /* ********************************************************* *
     *          Metodi getter e setter per milestone             *
     * ********************************************************* */
	/**
	 * Restituisce true se l'attivit&agrave; &egrave; milestone - false (default) se non lo &egrave;
	 * 
	 * @return <code>milestone</code> - true/false attivit&agrave; milestone
	 */
	public boolean isMilestone() {
		return milestone;
	}

	/**
	 * Imposta milestone l'attivit&agrave;
	 *  
	 * @param milestone - milestone true/false da impostare
	 */
	public void setMilestone(boolean milestone) {
		this.milestone = milestone;
	}

	
	/* ********************************************************* *
     *          Metodi getter e setter per strutture             *
     * ********************************************************* */
    /**
     * Restituisce il vettore contenente le strutture partecipanti all'attivit&agrave;
     * 
     * @return <code>strutture</code> - ArrayList contenente le strutture che partecipano all'attivit&agrave;
     */
    public ArrayList<DepartmentBean> getStrutture() {
        return strutture;
    }

    /**
     * Imposta le strutture che partecipano all'attivit&agrave;
     * 
     * @param strutture - ArrayList da impostare
     */
    public void setStrutture(ArrayList<DepartmentBean> strutture) {
        this.strutture = strutture;
    }

    
    /* ************************************************************ *
     *       Metodi getter e setter per soggetti interessati        *
     *          / soggetti contingenti / enti esterni               *
     * ************************************************************ */
    /**
     * Restituisce il vector contenente i soggetti non strutturali partecipanti all'attivit&agrave;
     * 
     * @return <code>soggetti</code> - Vector contenente i soggetti che partecipano all'attivit&agrave;
     */
    public Vector<DepartmentBean> getSoggetti() {
        return soggetti;
    }

    /**
     * Imposta i soggetti contingenti che partecipano all'attivit&agrave;
     * 
     * @param soggetti - Vector da impostare
     */
    public void setSoggetti(Vector<DepartmentBean> soggetti) {
        this.soggetti = soggetti;
    }

    
    /* ******************************************************************** *
     *  Metodi getter e setter per il grado di complessita' dell'attivita'  *
     * ******************************************************************** */
    /**
     * Restituisce l'id del grado di complessit&agrave; di una attivit&agrave; 
     * 
     * @return <code>idComplessita</code> - l'id della complessit&agrave; che descrive l'attivit&agrave;
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e questo attributo non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    public int getIdComplessita() throws AttributoNonValorizzatoException {
        if (idComplessita == -2) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo id complessita\' non valorizzato!");
        }
        return idComplessita;
    }

    /**
     * <p>Imposta l'identificativo della complessit&agrave; che descrive, appunto,
     * il grado di complessit&agrave; dell'attivit&agrave;</p>
     * <p>Restituisce l'id del grado di complessit&agrave; dell'attivit&agrave;.
     * Quantunque generalmente i metodi setter non restituiscano valori,
     * nulla vieta di permettergli di restituire il valore oggetto del settaggio
     * (valore del parametro o dell'argomento).
     * Tuttavia ci&ograve; comporterebbe un problema nella reflection:
     * <pre>
     * java.util.logging.Logger@4893ccd0: Oggetto ActivityBean non valorizzato; problema nella query dell'attivita'.
     * Problemi nel settare l'attributo 'idComplessita' nel bean di tipo 'class it.alma.bean.ActivityBean': Cannot set idComplessita
     * Problemi nel settare l'attributo 'idComplessita' nel bean di tipo 'class it.alma.bean.ActivityBean': Cannot set idComplessita
     * it.alma.bean.BeanUtil.populate(BeanUtil.java:185)
     * </pre>
     * Per cui nel costruttore NON facciamo:
     * <pre>setIdWbs(setIdStato(setIdComplessita(-2)));</pre>
     * ma usiamo il solito sistema di inizializzare tutto a un default negativo.</p>
     * 
     * @param idComplessita idComplessita da impostare
     */
    public void setIdComplessita(int idComplessita) {
        this.idComplessita = idComplessita;
    }
    
    
    /* ******************************************************************** *
     *    Metodi getter e setter per l'identificativo dello stato           * 
     *    in cui si trova l'attivita'                                       *
     * ******************************************************************** */
    /**
     * Restituisce l'id dello stato di avanzamento di una attivit&agrave; 
     * 
     * @return <code>idStato</code> - l'id dello stato che descrive l'avanzamento in cui l'attivit&agrave; si trova
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e questo attributo non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    public int getIdStato() throws AttributoNonValorizzatoException {
        if (idStato == -2) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo id stato non valorizzato!");
        }
        return idStato;
    }

    /**
     * <p>Imposta l'identificativo dello stato che descrive, appunto,
     * il grado di avanzamento dell'attivit&agrave;</p>
     * <p>Restituisce l'id del grado di avanzamento dell'attivit&agrave; (stato).
     * Quantunque generalmente i metodi setter non restituiscano valori,
     * Eclipse IDE propone questo pattern nel momento in cui i getter e 
     * setter vengono generati automaticamente. Tuttavia questo modello
     * collide con la reflection implementata dal BeanUtil, per cui
     * lasciamo <code>void</code>.</p>
     * 
     * @param idStato identificativo dello stato di avanzamento da impostare
     */
    public void setIdStato(int idStato) {
        this.idStato = idStato;
    }
	
    
    /* ********************************************************* *
     *          Metodi getter e setter per lo stato              *
     * ********************************************************* */
    /**
     * <p>Restituisce lo stato esatto (stato calcolato) per l'attivit&agrave;
     * contestualmente al momento presente (right now, right here).</p>
     * 
     * @return <code>CodeBean</code> - oggetto rappresentante lo stato dell'attivit&agrave;
     */
    public CodeBean getStato() {
        return stato;
    }

    /**
     * <p>Imposta lo stato esatto (stato calcolato) relativo all'attivit&agrave;</p>
     * 
     * @param stato - stato da impostare
     */
    public void setStato(CodeBean stato) {
        this.stato = stato;
    }
    
    
    /* ********************************************************* *
     *          Metodi getter e setter per l'indicatore          *
     * ********************************************************* */

    /**
     * <p>Restituisce l'eventuale indicatore di monitoraggio collegato alla
     * fase di attuazione di una misura.</p>
     * 
     * @return <code>IndicatorBean</code> - l'indicatore
     */
    public IndicatorBean getIndicatore() {
        return indicatore;
    }

    /**
     * <p>Imposta l'eventuale indicatore di monitoraggio collegato alla
     * fase corrente nel contesto di una certa misura di mitigazione 
     * monitorata.</p>
     * 
     * @param indicatore - l'indicatore da impostare
     */
    public void setIndicatore(IndicatorBean indicatore) {
        this.indicatore = indicatore;
    }   

}
