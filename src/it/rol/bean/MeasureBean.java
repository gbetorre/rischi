/*
 *   Rischi On Line (ROL-RMS), Applicazione web: 
 *   - per la gestione di sondaggi inerenti al rischio corruttivo 
 *   cui i processi organizzativi di una PA possono essere esposti, 
 *   - per la produzione di mappature e reportistica finalizzate 
 *   alla valutazione del rischio corruttivo nella pubblica amministrazione, 
 *   - per ottenere suggerimenti riguardo le misure di mitigazione 
 *   che possono calmierare specifici rischi 
 *   - e per effettuare il monitoraggio al fine di verificare quali misure
 *   proposte sono state effettivamente attuate dai soggetti interessati
 *   alla gestione dei processi a rischio e stabilire quantitativamente 
 *   in che grado questa attuazione di misure abbia effettivamente ridotto 
 *   i livelli di rischio.
 *
 *   Risk Mapping and Management Software (ROL-RMS),
 *   web application: 
 *   - to assess the amount and type of corruption risk to which each organizational process is exposed, 
 *   - to publish and manage, reports and information on risk
 *   - and to propose mitigation measures specifically aimed at reducing risk, 
 *   - also allowing monitoring to be carried out to see 
 *   which proposed mitigation measures were then actually implemented 
 *   and quantify how much that implementation of measures actually 
 *   reduced risk levels.
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import it.rol.Constants;
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
    /** Risponde alla domanda: &quot;La misura comporta spese?&quot; */
    private String onerosa;
    /** Numero di volte che la misura e' stata usata */
    private int uso;
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
    /** Obiettivo PIAO  */
    private String obiettivo;
    /** Ruolo  */
    private String ruolo;
    /** Dettagli Monitoraggio */
    private boolean dettagli;
    /** Data Monitoraggio */
    private Date dataMonitoraggio;
    /** Fasi di attuazione */
    private ArrayList<ActivityBean> fasi;
    
    
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
        onerosa = null;
        uso = BEAN_DEFAULT_ID;
        dataUltimaModifica = dataMonitoraggio = new Date(0);
        oraUltimaModifica = null;
        autoreUltimaModifica = BEAN_DEFAULT_ID;
        carattere = null;
        rilevazione = null;
        tipologie = null;
        capofila = capofila2 = capofila3 = gregarie = null;
        obiettivo = ruolo = null;
        dettagli = false;
        fasi = null;
    }


    /**
     * <p>Costruttore parametrizzato.</p>
     * 
     * @param o oggetto di cui propagare gli attributi
     * @throws AttributoNonValorizzatoException se un dato obbligatorio non e' stato valorizzato nel parametro
     */
    public MeasureBean(CodeBean o) throws AttributoNonValorizzatoException {
        super(o);
    }

    /* ************************************************************************ *
     *          Metodi per ordinare oggetti di questo tipo nelle liste          *
    /* ************************************************************************ */
    
    /* Careful: depends only on Ordinale!
     * <p>Compara due oggetti basandosi sulla loro chiave</p>
     * <p>
     * Questo permette di effettuare comparazioni direttamente 
     * tra oggetti, funzionali anche ad ordinamenti in collezioni, 
     * come nell'esempio:
     * <pre>
     * // Dichiara le voci del tempo
     * List&lt;MeasureBean&gt; theVoicesOfTime = db.getTheVoicesOfTime();
     * // Fa l'ordinamento per id voce, come specificato nel metodo compareTo()
     * Collections.sort(theVoicesOfTime);
     * </pre></p>
     */
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @SuppressWarnings("javadoc")
    public int compareTo(MeasureBean o) {
        int ordinale = super.getOrdinale();
        if ((ordinale == o.getOrdinale())) 
            return 0;
        else if (ordinale < o.getOrdinale()) 
            return -1;
        else 
            return 1;
    }
    
    /* ************************************************************************ *
     *      Metodi Ovverride per usare l'oggetto come key di un dictionary      *
     *      e/o per poter capire se è quello da rimuovere da una Collection     *
    /* ************************************************************************ */
    
    /* The primary key of these types are both code AND id_rilevazione */
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings({ "javadoc", "unused" })
    @Override
    public boolean equals(Object o) {
        // If they are the same object, they are equals for sure!
        if (this == o)
            return true;
        // If the other isn't real, they are different for sure!
        if (o == null)
            return false;
        // It their types aren't equals, they are different for sure!
        if (getClass() != o.getClass())
            return false;
        MeasureBean other = (MeasureBean) o;
        try {
            if (
                (this.codice.equals(other.getCodice())) && 
                (this.rilevazione.getId() == other.getRilevazione().getId())
               ) 
            return true;
        } catch (AttributoNonValorizzatoException anve) {
            // If they aren't comparable, we assume they are different! 
            return false;
        }
        return false;
    }
    
    
    /* Depends only on item id */
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @SuppressWarnings("javadoc")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        String code = this.codice;
        String progressive = code.substring(code.lastIndexOf(Constants.DOT) + Constants.ELEMENT_LEV_1, code.length());
        result = prime * result + Integer.parseInt(progressive); 
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("javadoc")
    @Override
    public String toString() {
        int idRilevazione = BEAN_DEFAULT_ID;
        try {
            idRilevazione = this.getRilevazione().getId();
        } catch (AttributoNonValorizzatoException anve) {
            return anve.getLocalizedMessage();
        }
        return FOR_NAME + "@" + this.codice + Constants.UNDERSCORE + idRilevazione;
    }
        
    
    /* ************************************************************************ *  
     *                          Accessori e Mutatori                            *
     * ************************************************************************ */
    
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
     * Risponde alla domanda: "Comporta spese?"
     * 
     * @return <code>comportaSpese</code> - risposta alla sostenibilit&agrave; economica
     */
    public String getOnerosa() {
        return onerosa;
    }

    
    /**
     * Imposta la specificit&agrave; economica della misura
     *
     * @param comportaSpese risposta in merito all'onerosit&agrave; della misura
     */
    public void setOnerosa(String comportaSpese) {
        onerosa = comportaSpese;
    }
    
    
    /**
     * Trasforma il valore convenzionale "ND" ricevuto in un'etichetta
     * pi&uacute; esplicita, lasciando inalterati gli altri valori.
     * 
     * @param comportaSpese la risposta alla domanda "Comporta spese?" memorizzata nel db 
     * @return <code>String</code> - stringa ben formattata in caso il valore sia una sigla convenzionale
     */
    @SuppressWarnings("static-method")
    public String getOnerosa(String comportaSpese) {
        if (!comportaSpese.equals("ND")) {
            return comportaSpese;
        }
        return Constants.ND;
    }
    

    /* *********************************************************** *
     *    Metodi getter e setter per numero di usi della misura    *
     * *********************************************************** */
    
    /**
     * Restituisce il numero di volte in cui la misura &egrave; stata
     * applicata ai rischi nel contesto dei rispettivi processi (nella rilevazione
     * corrente).
     * 
     * @return <code>int</code> - byte castato a int rappresentante la "popolarit&agrave;" della misura
     */
    public int getUso() {
        return uso;
    }


    /**
     * Imposta il numero di volte che la misura corrente &egrave; stata applicata a rischi nella rilevazione corrente
     * 
     * @param uso la "popolarit&agrave;" della misura
     */
    public void setUso(int uso) {
        this.uso = uso;
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
     * 
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
     * 
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
     * Restituisce il vettore contenente le strutture capofila.
     * La vera capofila &egrave; l'ultima dell'elenco; quelle che la precedono
     * sono i suoi ascendenti.
     * 
     * @return <code>strutture</code> - ArrayList contenente le strutture capofila
     */
    public ArrayList<DepartmentBean> getCapofila() {
        return capofila;
    }

    
    /**
     * Imposta le strutture capofila
     * 
     * @param capofila - ArrayList da impostare
     */
    public void setCapofila(ArrayList<DepartmentBean> capofila) {
        this.capofila = capofila;
    }
    
    
    /**
     * Trasforma una struttura, che pu&ograve; essere o di tipo capofila 
     * o di tipo gregaria (cio&egrave; pu&ograve; essere stata associata
     * alla misura come sua struttura capofila o sua struttura gregaria), 
     * da ItemBean a DepartmentBean.<ol>
     * <li>Se la struttura ricevuta in argomento sotto forma di ItemBean 
     * &egrave; una capofila, allora la struttura di tipo DepartmentBean
     * restituita conterr&agrave; internamente tutta la gerarchia sottostante.
     * La gerarchia viene ricostruita a partire dagli attributi valorizzati
     * opportunamente dell'ItemBean ricevuto.</li>
     * <li>Se invece la struttura ricevuta &egrave; gregaria (anche detta 
     * "struttura coinvolta" o "struttura associata"), allora la struttura 
     * restituita corrisponder&agrave; ad una semplice conversione di tipo, 
     * con relativo travaso semplice dei valori degli attributi.</li> 
     * 
     * @param struttura - struttura destrutturata da travasare
     * @return <code>DepartmentBean</code> - struttura travasata, contenente anche la gerarchia delle strutture figlie se capofila
     */
    @SuppressWarnings("static-method")
    public DepartmentBean getStruttura(ItemBean struttura) {
        DepartmentBean liv = null;
        if (!struttura.getExtraInfo().equals(Constants.GR)) {   // Non è gregaria
            DepartmentBean liv1 = new DepartmentBean();
            DepartmentBean liv2 = new DepartmentBean();
            DepartmentBean liv3 = new DepartmentBean();
            DepartmentBean liv4 = new DepartmentBean();
            Vector<DepartmentBean> figlie2 = null;
            Vector<DepartmentBean> figlie3 = null;
            Vector<DepartmentBean> figlie4 = null;
            liv1.setId(struttura.getCod1());
            liv1.setPrefisso(struttura.getNome());
            liv1.setNome(struttura.getExtraInfo1());
            liv1.setLivello(Constants.ELEMENT_LEV_1);
            liv1.setInformativa(struttura.getExtraInfo());
            if (struttura.getCod2() > Constants.NOTHING) {
                figlie2 = new Vector<>();
                liv2.setId(struttura.getCod2());
                liv2.setPrefisso(struttura.getNomeReale());
                liv2.setNome(struttura.getExtraInfo2());
                liv2.setLivello(Constants.ELEMENT_LEV_2);
                liv2.setInformativa(struttura.getExtraInfo());
                if (struttura.getCod3() > Constants.NOTHING) {
                    figlie3 = new Vector<>();
                    liv3.setId(struttura.getCod3());
                    liv3.setPrefisso(struttura.getCodice());
                    liv3.setNome(struttura.getExtraInfo3());
                    liv3.setLivello(Constants.ELEMENT_LEV_3);
                    liv3.setInformativa(struttura.getExtraInfo());
                    if (struttura.getCod4() > Constants.NOTHING) {
                        figlie4 = new Vector<>();
                        liv4.setId(struttura.getCod4());
                        liv4.setPrefisso(struttura.getLabelWeb());
                        liv4.setNome(struttura.getExtraInfo4());
                        liv4.setLivello(Constants.ELEMENT_LEV_4);
                        liv4.setInformativa(struttura.getExtraInfo());
                        figlie4.add(liv4);
                    }
                    liv3.setFiglie(figlie4);
                    figlie3.add(liv3);
                }
                liv2.setFiglie(figlie3);
                figlie2.add(liv2);
            }
            liv1.setFiglie(figlie2);
            liv = liv1;
        } else {                                    // Non è capofila ma gregaria
            liv = new DepartmentBean();
            if (struttura.getCod1() > Constants.NOTHING) {
                liv.setId(struttura.getCod1());
                liv.setPrefisso(struttura.getNome());
                liv.setNome(struttura.getExtraInfo1());
                liv.setLivello(Constants.ELEMENT_LEV_1);
                liv.setInformativa(struttura.getExtraInfo());
            } else if (struttura.getCod2() > Constants.NOTHING) {
                liv.setId(struttura.getCod2());
                liv.setPrefisso(struttura.getNomeReale());
                liv.setNome(struttura.getExtraInfo2());
                liv.setLivello(Constants.ELEMENT_LEV_2);
                liv.setInformativa(struttura.getExtraInfo());
            } else if (struttura.getCod3() > Constants.NOTHING) {
                liv.setId(struttura.getCod3());
                liv.setPrefisso(struttura.getCodice());
                liv.setNome(struttura.getExtraInfo3());
                liv.setLivello(Constants.ELEMENT_LEV_3);
                liv.setInformativa(struttura.getExtraInfo());
            } else if (struttura.getCod4() > Constants.NOTHING) {
                liv.setId(struttura.getCod4());
                liv.setPrefisso(struttura.getLabelWeb());
                liv.setNome(struttura.getExtraInfo4());
                liv.setLivello(Constants.ELEMENT_LEV_4);
                liv.setInformativa(struttura.getExtraInfo());
            }
        }
        return liv;
    }
    
    
    /**
     * Trasforma una struttura capofila contenente al proprio interno
     * la gerarchia delle strutture figlie, in una lista ordinata
     * di tipo vettoriale, in cui ogni nodo viene collocato 
     * allo stesso livello. 
     * Dal punto di vista del registro delle misure, l'ultima foglia, quindi
     * l'ultimo elemento della lista, &egrave; l'elemento pi&uacute; 
     * significativo,la "reale" capofila, rappresentando i nodi precedenti 
     * soltanto la memorizzazione della sua gerarchia.
     * 
     * @param capofilaTree - la struttura gerarchica di struttura capofila  
     * @return <code>ArrayList&lt;DepartmentBean&gt;</code> - la struttura capofila in cui i nodi sono stati trasformati in elementi di una lista
     */
    @SuppressWarnings("static-method")
    public ArrayList<DepartmentBean> getCapofila(DepartmentBean capofilaTree) {
        ArrayList<DepartmentBean> capofilaAsList = new ArrayList<>();
        // La aggiunge alla lista delle capofila
        capofilaAsList.add(capofilaTree);
        // Controlla se ha figlie
        if (capofilaTree.getFiglie() != null) {
            // Se ha una figlia aggiunge la figlia
            capofilaAsList.add(capofilaTree.getFiglie().firstElement());
            // Controlla se ha nipoti
            if (capofilaTree.getFiglie().firstElement().getFiglie() != null) {
                // Se ha una nipote aggiunge la nipote
                capofilaAsList.add(capofilaTree.getFiglie().firstElement().getFiglie().firstElement());
                // Controlla se ha pronipoti
                if (capofilaTree.getFiglie().firstElement().getFiglie().firstElement().getFiglie() != null) {
                    // Se ha una pronipote aggiunge la pronipote
                    capofilaAsList.add(capofilaTree.getFiglie().firstElement().getFiglie().firstElement().getFiglie().firstElement());
                }
            }
        }
        return capofilaAsList;
    }
    
    
    /* ********************************************************* *
     *      Metodi getter e setter per strutture capofila 2      *
     * ********************************************************* */
    
    /**
     * Restituisce il vettore contenente le strutture capofila aggiuntive
     * 
     * @return <code>strutture</code> - ArrayList contenente le strutture capofila aggiuntive
     */
    public ArrayList<DepartmentBean> getCapofila2() {
        return capofila2;
    }

    
    /**
     * Imposta le strutture che sono capofila aggiuntive
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
     * Restituisce il vettore contenente le strutture capofila opzionali
     * 
     * @return <code>strutture</code> - ArrayList contenente le strutture capofila opzionali
     */
    public ArrayList<DepartmentBean> getCapofila3() {
        return capofila3;
    }

    
    /**
     * Imposta le strutture che sono capofila opzionali
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
    public void setGregarie(ArrayList<DepartmentBean> gregarie) {
        this.gregarie = gregarie;
    }
    
    
    /* ********************************************************* *
     *        Metodi getter e setter per obiettivo [PIAO]        *
     * ********************************************************* */

    /**
     * Restituisce l'obiettivo della misura
     * 
     * @return <code>obiettivo</code> - Stringa contenente i dettagli dell'obiettivo che la misura intende realizzare
     */
    public String getObiettivo() {
        return obiettivo;
    }


    /**
     * Imposta l'obiettivo della misura
     * 
     * @param obiettivo - obiettivo della misura, da impostare
     */
    public void setObiettivo(String obiettivo) {
        this.obiettivo = obiettivo;
    }
    
    
    /* ********************************************************* *
     *              Metodi getter e setter per ruolo             *
     * ********************************************************* */

    /**
     * Restituisce un'informazione relativa a un ruolo
     * (p.es. un ruolo rivestito dalla misura oppure il ruolo
     * rivestito da una struttura collegata alla misura nei confronti 
     * della misura stessa).
     * 
     * @return <code>ruolo</code> - Stringa contenente i dettagli di un ruolo
     */
    public String getRuolo() {
        return ruolo;
    }


    /**
     * Imposta un ruolo correlato alla misura corrente
     * 
     * @param ruolo - ruolo attinente alla misura, da impostare
     */
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }
    
    
    /* ********************************************************* *
     *      Metodi getter e setter per dettagli monitoraggio     *
     * ********************************************************* */

    /**
     * Restituisce <code>true</code> se la misura corrente &egrave; sottoposta
     * a monitoraggio, ovvero se sono presenti i dettagli relativi 
     * al monitoraggio per la misura considerata.
     * 
     * @return <code>dettagli</code> - true se la misura e' sottoposta a monitoraggio, false altrimenti
     */
    public boolean isDettagli() {
        return dettagli;
    }


    /**
     * Imposta il flag specificante se la misura ha dettagli di monitoraggio
     * associati.
     * 
     * @param dettagli - flag specificante la presenza di dettagli relativi al monitoraggio
     */
    public void setDettagli(boolean dettagli) {
        this.dettagli = dettagli;
    }
    
    
    /* ********************************************************* *
     *      Metodi getter e setter per dettagli monitoraggio     *
     * ********************************************************* */

    /**
     * Restituisce la data di ultima modifica dei dettagli del monitoraggio
     * collegati alla misura corrente. Se i dettagli non sono stati mai 
     * aggiornati, la data &egrave; quella di inserimento dei dettagli;
     * altrimenti, la data &egrave; quella dell'ultimo aggiornamento 
     * dei dettagli stessi.
     * 
     * @return <code>dataMonitoraggio</code> - la data di inserimento o ultima modifica dei dettagli del monitoraggio 
     */
    public Date getDataMonitoraggio() {
        return dataMonitoraggio;
    }


    /**
     * Imposta la data di ultima modifica dei dettagli del monitoraggio trovata.
     * 
     * @param dataMonitoraggio - data in cui sono stati inseriti i dettagli del monitoraggio, da impostare
     */
    public void setDataMonitoraggio(Date dataMonitoraggio) {
        this.dataMonitoraggio = dataMonitoraggio;
    }
    
    
    /* ********************************************************* *
     *        Metodi getter e setter per fasi di attuazione      *
     * ********************************************************* */

    /**
     * Restituisce l'elenco delle fasi di attuazione
     * 
     * @return <code>fasi</code> - le fasi di attuazione della misura
     */
    public ArrayList<ActivityBean> getFasi() {
        return fasi;
    }


    /**
     * Imposta lista delle fasi di attuazione della misura
     * 
     * @param fasi - le fasi da impostare
     */
    public void setFasi(ArrayList<ActivityBean> fasi) {
        this.fasi = fasi;
    }
    
    
    /* ************************************************************************ *
     *      Metodi di calcolo (tot. indicatori e misurazioni, stato misura)     *
     * ************************************************************************ */
    
    /**
     * Conta quanti indicatori in totale insistono sulle varie fasi di attuazione
     * della misura corrente
     * 
     * @return <code>totIndicatori</code> - un intero piccolo rappresentante il numero di indicatori complessivo della misura
     */
    public byte getTotIndicatori() {
        byte totInd = Constants.NOTHING;
        if (this.fasi != null) {
            for (ActivityBean fase : this.fasi) {
                // Ogni fase ha al più un indicatore: FASE (0,1)-<>-(1,1) INDICATORE
                if (fase.hasIndicatore()) {
                    // Se trova indicatore incrementa il contatore
                    totInd++;
                }
            }
        }
        return totInd;
    }
    
    
    /**
     * Conta quante misurazioni in totale sono effettuate sugli indicatori 
     * collegati alle varie fasi di attuazione della misura corrente
     * 
     * @return <code>totMisurazioni</code> - un intero piccolo rappresentante il numero di misurazioni applicate agli indicatori delle fasi della misura
     */
    public byte getTotMisurazioni() {
        byte totMis = Constants.NOTHING;
        if (this.fasi != null) {
            for (ActivityBean fase : this.fasi) {
                // Ogni fase ha al più un indicatore: FASE (0,1)-<>-(1,1) INDICATORE
                if (fase.hasIndicatore()) {
                    IndicatorBean indicatore = fase.getIndicatore();
                    if (indicatore.getMisurazioni() != null) {
                        totMis += indicatore.getMisurazioni().size();
                    }

                }
            }
        }
        return totMis;
    }
    
    
    /**
     * Verifica se ogni indicatore collegato ad ogni fase ha ricevuto almeno
     * una misurazione.<ol> 
     * <li>Se questo &egrave; vero, allora la misura corrente pu&ograve; 
     * essere considerata "monitorata" e il metodo restituisce 'true';</li>
     * <li>altrimenti, se almeno un indicatore non ha ricevuto alcuna misurazione,
     * la misura non pu&ograve; essere considerata "monitorata" e il presente
     * metodo restituisce 'false'.</li></ol> 
     * La definizione di &quot;misura monitorata&quot; &egrave; ben
     * formalizzata:<br> 
     * <cite>una /misura monitorata/ &egrave; una misura di mitigazione 
     * suddivisa in fasi di attuazione, su cui &egrave; stato applicato 
     * almeno un indicatore che &egrave; stato misurato almeno una volta.
     * </cite><br> 
     * In altri termini:<br>
     * Per definire una misura di mitigazione come misura monitorata 
     * &egrave; <strong>necessario e sufficiente</strong> che 
     * sia stato inserito almeno un indicatore su una delle fasi della misura 
     * e che lo stesso sia stato misurato almeno una volta.<br> 
     * Nel contesto di una misura, potrebbero quindi esservi anche alcune fasi
     * prive di indicatori e ci&ograve; nonostante la misura, se gli altri
     * indicatori hanno ricevuto almeno una misurazione, sar&agrave; considerata
     * "monitorata"; ci&ograve; per via del fatto che non sempre tutte le fasi
     * di una misura hanno un indicatore associato, proprio da regole di 
     * business.<br> 
     * Come si vede, non &egrave; possibile trarre conclusioni sulla
     * completezza del monitoraggio di una misura confrontando banalmente 
     * se il totale degli indicatori ({@link #getTotIndicatori()}) &egrave;
     * uguale al totale delle misurazioni ({@link #getTotMisurazioni()})
     * perch&eacute; un numero di misurazioni elevato non garantisce che tutti
     * gli indicatori siano stati misurati (p.es., una misura potrebbe avere: 
     * 3 fasi, 2 indicatori e 2 misurazioni fatte solo sul primo indicatore; 
     * il fatto che il numero di misurazioni in questo caso sia uguale
     * al numero di indicatori non implica che tutti gli indicatori 
     * abbiano ricevuto una misurazione).
     * Alcuni casi d'uso:<ul>
     * <li>Misura A ha 2 fasi, a1 e a2; solo a1 ha un indicatore e questo 
     * ha ricevuto una misurazione: la misura A risulta "monitorata".</li>
     * <li>Misura B ha 3 fasi, b1, b2 e b3; tutte e 3 le fasi hanno un indicatore
     * ciascuno, quindi i1 per b1, i2 per b2, i3 per b3; i1 e i2 hanno ricevuto 
     * rispettivamente 1 e 2 misurazioni, mentre i3 non ha ricevuto misurazioni:
     * la misura risulta "non monitorata".</li>
     * <li>Misura C ha 2 fasi ma nessun indicatore: la misura risulta 
     * "non misurabile", perch&eacute; potrebbe anche essere corretto 
     * non prevedere indicatori su una misura in presenza 
     * di situazioni particolari (p.es. "verifica sul pantouflage" 
     * che potrebbe restare a lungo dotata di dettagli ma priva di indicatori 
     * in quanto non facilmente misurabile. Siccome il metodo restituisce
     * soltanto un valore boolean, non &egrave; possibile discriminare
     * tra "misura non monitorata" e "misura non misurabile" 
     * (o "non monitorabile"), ma possono essere sviluppati ulteriori 
     * strati di codice per intercettare a monte questo caso. 
     * (Per ulteriori dettagli, v. analisi dei requisiti). </li></ul><br>
     * 
     * @return <code>boolean</code> - se vero la misura è monitorata, se falso è non monitorata
     */
    public boolean getMonitorata() {
        // Inizializza tutti i contatori a zero
        int totFasi = Constants.NOTHING;
        int totIndicatori = Constants.NOTHING;
        int indicatoriMisurati = Constants.NOTHING;
        // Una misura potrebbe non avere i dettagli monitoraggio
        if (this.fasi != null) {
            // Conta le fasi della misura
            totFasi = fasi.size();
            // Scorre le fasi
            for (int i = 0; i < totFasi; i++) {
                ActivityBean fase = fasi.get(i);
                // Le fasi potrebbero legittimamente non avere indicatori
                if (fase.hasIndicatore()) {
                    // Tiene il conto del totale degli indicatori della misura
                    totIndicatori += 1;
                    // Ottiene l'indicatore
                    IndicatorBean indicatore = fase.getIndicatore();
                    // Controlla se l'indicatore ha misurazioni
                    if (indicatore.getMisurazioni() != null && !indicatore.getMisurazioni().isEmpty()) {
                        // Tiene il conto degli indicatori che hanno misurazioni
                        indicatoriMisurati += 1;
                    } else {
                        // Se c'è almeno un indicatore non misurato, la misura non può essere totalmente monitorata
                        return false;
                    }
                }
            }
            // Se nessuna fase ha indicatori, la misura non è misurabile
            if (totIndicatori == Constants.NOTHING) {
                return false;
            }
            // Se il numero di tutti gli indicatori definiti è uguale a quello di tutti gli indicatori misurati, la misura è totalmente monitorata 
            if (totIndicatori == indicatoriMisurati) {
                return true;
            }
        }
        // Se non ci sono le fasi la misura non è monitorabile
        return false;
    }

}
