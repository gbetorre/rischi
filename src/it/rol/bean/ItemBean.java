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

import java.io.Serializable;

/**
 * <p>Classe che serve a rappresentare oggetti generici (p.es. voci in un menu,
 * righe di un log, etc.)</p>
 * <p>Effettua l'override dei metodi necessari a permettere sia l'ordinamento 
 * in strutture vettoriali, sia l'utilizzo di questo tipo di oggetto 
 * come chiave di tabelle hash (dictionaries).</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class ItemBean implements Serializable, Comparable<ItemBean> {
    
    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = -4800164838294374204L;
    /**     Nome di questa classe                                           */
    private final String FOR_NAME = "\n" + this.getClass().getName() + ": "; //$NON-NLS-1$
    /* $NON-NLS-1$ silence a warning that Eclipse emits when it encounters string literals
        The idea is that UI messages should not be embedded as string literals, 
        but rather sourced from a resource file (so that they can be translated, proofed, etc).*/
    /**     Suffisso per le Command                                         */
    static final String COMMAND_SUFFIX = "Command";
    /**     Identificativo della voce                                       */
    private int id;
    /**     Nome della voce                                                 */
    private String nome;
    /**     Nome reale della voce                                           */
    private String nomeReale;
    /**     Etichetta per la voce                                           */
    private String labelWeb;
    /**     Nome di entita' associata                                       */ 
    private String nomeClasse;
    /**     Attributo per memorizzare il valore di una pagina associata     */
    private String paginaJsp;
    /**    Attributo per memorizzare il link sotteso all'etichetta          */
    private String url;
    /**     Attributo che pu&ograve; contenere informazioni descrittive     */
    private String informativa;
    /**     Numero d'ordine dell'elemento rispetto agli altri               */
    private int ordinale;
    /**     Attributo per memorizzare il nome di un'immagine                */
    private String icona;
    /**     Attributo per la definizione del livello della voce o altro     */
    private int livello;
    /**     Attributo per definire il puntamento dell'url della voce        */
    private boolean urlInterno;
    /**     Attributo per definire altre informazioni contestuali           */
    private boolean privato;
    /**     Attributo che serve a memorizzare ulteriori informazioni        */
    private String extraInfo;
    /**     Attributo utile per rappresentare un codice descrittivo         */
    private String codice;
    /**     Attributo per rappresentare un codice numerico della voce       */
    private int cod1;
    /**     Attributo per rappresentare un codice numerico della voce       */
    private int cod2;
    /**     Attributo per rappresentare un codice numerico della voce       */
    private int cod3;
    /**     Attributo per rappresentare un codice numerico della voce       */
    private int cod4;
    /**     Attributo per rappresentare un indice quantitativo della voce   */
    private float value1;
    /**     Attributo per rappresentare un indice quantitativo della voce   */
    private float value2;
    /**     Attributo per rappresentare un indice quantitativo della voce   */
    private float value3;
    /**     Attributo per rappresentare un indice quantitativo della voce   */
    private float value4;
    
    
    /**
     * <p>Override Costruttore di Default</p>
     * <p>Inizializza le variabili di classe a valori convenzionali</p>
     */
    public ItemBean() {
        id = cod1 = cod2 = cod3 = cod4 = ordinale = CodeBean.BEAN_DEFAULT_ID;
        value1 = value2 = value3 = value4 = CodeBean.BEAN_DEFAULT_FLOAT;
        nome = nomeReale = labelWeb = nomeClasse = paginaJsp = url = informativa = icona = null;
        livello = 0;
        urlInterno = true;
        privato = false;
        extraInfo = codice = null;
    }
    
    
    /**
     * <p>Costruttore da ItemBean</p>
     * <p>Inizializza le variabili di classe a valori presi da
     * un ItemBean passato come argomento</p>
     *  
     * @param old ItemBean di cui si vuol recuperare parte dei valori
     */
    public ItemBean(final ItemBean old) {
        this.id = old.getId();
        this.nome = old.getNome();
        this.nomeReale = old.getNomeReale();
        this.labelWeb = old.getLabelWeb();
        this.nomeClasse = old.getNomeClasse();
        this.paginaJsp = old.getPaginaJsp();
        this.url = old.getUrl();
        this.informativa = old.getInformativa();
        this.ordinale = old.getOrdinale();
        this.icona = old.getIcona();
        this.livello = old.getLivello();
        this.urlInterno = true;
        this.cod1 = this.cod2 = this.cod3 = this.cod4 = -2;
        this.value1 = this.value2 = this.value3 = this.value4 = CodeBean.BEAN_DEFAULT_FLOAT;
        this.codice = null;
    }
    
    
    /**
     * <p>Costruttore parametrizzato</p>
     * <p>
     * ItemBean(String nome, String labelWeb, String url, int livello)</p>
     * 
     * @param nome      nome della voce da creare; puo' corrispondere al token
     * @param labelWeb  etichetta da mostrare per l'url della voce da creare
     * @param url       URL della voce da creare
     * @param livello   livello di indentazione della voce da creare
     */
    public ItemBean(String nome, String labelWeb, String url, int livello) {
        this.nome = nome;
        this.labelWeb = labelWeb;
        this.url = url;
        this.livello = livello;
        this.urlInterno = true;
    }
    
    
    /**
     * <p>Costruttore parametrizzato</p>
     * <p>
     * ItemBean(String nome, String labelWeb, String url, String informativa, int livello)
     * </p>
     * 
     * @param nome          nome della voce da creare; puo' corrispondere al token
     * @param labelWeb      etichetta da mostrare per l'url della voce da creare
     * @param url           URL della voce da creare
     * @param informativa   una descrizione della voce da creare
     * @param livello       livello di indentazione della voce da creare
     */
    public ItemBean(String nome, String labelWeb, String url, String informativa, int livello) {
        this.nome = nome;
        this.labelWeb = labelWeb;
        this.url = url;
        this.informativa = informativa;
        this.livello = livello;
        this.urlInterno = true;
    }
    
    
    /**
     * <p>Costruttore parametrizzato</p>
     * <p>
     * ItemBean(String nome, String labelWeb, String url, String informativa, String icona, int livello)
     * </p>
     * 
     * @param nome          nome della voce da creare; puo' corrispondere al token
     * @param labelWeb      etichetta da mostrare per l'url della voce da creare
     * @param url           URL della voce da creare
     * @param informativa   una descrizione della voce da creare
     * @param icona         immagine da mostrare a corredo della voce da creare
     * @param livello       livello di indentazione della voce da creare
     */
    public ItemBean(String nome, String labelWeb, String url, String informativa, String icona, int livello) {
        this.nome = nome;
        this.labelWeb = labelWeb;
        this.url = url;
        this.informativa = informativa;
        this.icona = icona;
        this.livello = livello;
        this.urlInterno = true;
    }
    
    
    /**
     * <p>Costruttore parametrizzato</p>
     * <p>
     * ItemBean(String nome, String labelWeb, String url, String informativa, String icona, int livello)
     * </p>
     * 
     * @param nome          nome della voce da creare; puo' corrispondere al token
     * @param nomeReale     nome che dovrebbe essere 'reale' nel senso che non e' il nome di un altro elemento ma proprio dell'attributo 'nome' nella tabella delle voci di menu 
     * @param labelWeb      etichetta da mostrare per l'url della voce da creare
     * @param url           URL della voce da creare
     * @param informativa   una descrizione della voce da creare
     * @param icona         immagine da mostrare a corredo della voce da creare
     * @param livello       livello di indentazione della voce da creare
     */
    public ItemBean(String nome, String nomeReale, String labelWeb, String url, String informativa, String icona, int livello) {
        this.nome = nome;
        this.nomeReale = nomeReale;
        this.labelWeb = labelWeb;
        this.url = url;
        this.informativa = informativa;
        this.icona = icona;
        this.livello = livello;
        this.urlInterno = true;
    }
    
    /* **************************************************************** *
     * Metodi Ovverride per ordinare oggetti di questo tipo nelle liste *
     * **************************************************************** */
    
    /*
     * <p>Compara due oggetti ItemBean basandosi sull'id</p>
     * <p>
     * Questo permette di effettuare comparazioni direttamente 
     * tra oggetti ItemBean, funzionali anche ad ordinamenti, 
     * come nell'esempio:
     * <pre>
     * // Dichiara le voci del tempo
     * List&lt;ItemBean&gt; theVoicesOfTime = null;
     * // Recupera le voci del tempo
     * theVoicesOfTime = db.getTheVoicesOfTime();
     * // Fa l'ordinamento per id voce, come specificato nel metodo compareTo()
     * Collections.sort(theVoicesOfTime);
     * </pre></p>
     */
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @SuppressWarnings("javadoc")
    @Override
    public int compareTo(ItemBean o) {
        if (this.id == o.getId()) 
            return 0;
        else if (this.id < o.getId()) 
            return -1;
        else 
            return 1;
    }
    
    /* **************************************************************** *
     *  Metodi Ovverride per usare l'oggetto come key di un dictionary  *
     * **************************************************************** */
    
    /* Compare only item ids */
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("javadoc")
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        ItemBean other = (ItemBean) o;
        if (this.id != other.getId())
            return false;
        return true;
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
        result = prime * result + this.id; 
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("javadoc")
    @Override
    public String toString() {
        return FOR_NAME + "@" + this.nome + String.valueOf(this.id);
    }
    
    /* ************************************************************************ *  
     *                          Accessori e Mutatori                            *
     * ************************************************************************ */
    
    /**
     * @return <code>id</code> - l'identificativo della voce di menu
     */
    public int getId() {
        return this.id;
    }
    
    
    /**
     * @param i un intero rappresentante l'identificativo della voce di menu da impostare
     */
    public void setId(int i) {
        id = i;
    }
    
    
    /**
     * @return <code>nome</code> - il token usato per individuare questa entita' nella query string
     */
    public String getNome() {
        return this.nome;
    }
    
    
    /**
     * @param v un oggetto String usato per impostare nella voce di menu il valore di 'ent'
     */
    public void setNome(String v) {
        this.nome = v;
    }
    
    
    /**
     * <p>Nome reale della voce, in caso di corrispondenze logiche</p>
     * 
     * @return <code>nomeReale</code> - un oggetto String usato per impostare il nome della voce di menu 
     */
    public String getNomeReale() {
        return this.nomeReale;
    }
    
    
    /**
     * @param v un oggetto String usato per impostare il nome della voce di menu 
     */
    public void setNomeReale(String v) {
        this.nomeReale = v;
    }
    
    
    /**
     * <p>Etichetta da visualizzare per la voce</p>
     * 
     * @return <code>labelWeb</code> - etichetta da mostrare nel menu, per rappresentare la voce
     */
    public String getLabelWeb() {
        return this.labelWeb;
    }
    
    
    /**
     * @param v un oggetto String contenente l'etichetta per la voce di menu
     */
    public void setLabelWeb(String v) {
        this.labelWeb = v;
    }
    
    
    /**
     * <p>Attributo che pu&ograve; essere usato per memorizzare
     * il nome di una entit&agrave; associata alla voce
     * oppure altre informazioni attinenti </p>
     * 
     * @return <code>nomeClasse</code> - un oggetto String contenente tradizionalmente il nome della Command associata alla voce di menu
     */
    public String getNomeClasse() {
        return this.nomeClasse + COMMAND_SUFFIX;
    }
    
    
    /**
     * @param v un oggetto String usato tradizionalmente per impostare il nome della Command associata alla voce di menu
     */
    public void setNomeClasse(String v) {
        this.nomeClasse = v;
    }
    
    
    /**
     * <p>Attributo che pu&ograve; essere usato per memorizzare
     * il valore della pagina associata ad una classe referenziata
     * dalla voce di menu, oppure altre informazioni attinenti.</p>
     * 
     * @return <code>paginaJsp</code> - oggetto String usato tradizionalmente per memorizzare il nome della pagina jsp associata alla it.univr.di.uol.command.Command {@link #getNomeClasse()}
     */
    public String getPaginaJsp() {
        return this.paginaJsp;
    }
    
    
    /**
     * @param v oggetto String usato tradizionalmente per impostare il nome della pagina jsp associata alla it.univr.di.uol.command.Command {@link #getNomeClasse()}
     */
    public void setPaginaJsp(String v) {
        this.paginaJsp = v;
    }
    
    
    /**
     * @return <code>url</code> - oggetto String contenente il link da sottendere alla voce di menu
     */
    public String getUrl() {
        return this.url;
    }
    
    
    /**
     * @param v oggetto String per memorizzare l'url della voce di menu
     */
    public void setUrl(String v) {
        this.url = v;
    }
    
    
    /**
     * @return <code>informativa</code> - un campo note descrittivo
     */
    public String getInformativa() {
        return this.informativa;
    }
    
    
    /**
     * @param string un oggetto String contenente informazioni da impostare
     */
    public void setInformativa(String string) {
        informativa = string;
    }
    
    
    /**
     * @return <code>ordinale</code> - il numero d'ordine della voce
     */
    public int getOrdinale() {
        return this.ordinale;
    }
    
    
    /**
     * @param i un intero rappresentante il numero d'ordine della voce da impostare
     */
    public void setOrdinale(int i) {
        ordinale = i;
    }
    
        
    /**
     * <p>Attributo per memorizzare il nome di un'immagine,
     * o un attributo di stile utile a far apparire un'immagine,
     * a corredo della voce di menu.</p>
     * 
     * @return <code>icona</code> - il nome di un'immagine, o un attributo di stile utile a far apparire un'immagine, a corredo della voce di menu
     */
    public String getIcona() {
        return this.icona;
    }
    
    
    /**
     * @param string il nome di un'immagine, o un attributo di stile utile a far apparire un'immagine, a corredo della voce di menu da impostare
     */
    public void setIcona(String string) {
        icona = string;
    }
    
    
    /**
     * <p>Attributo per la definizione del livello di 
     * indentazione della voce nel menu o per contenere altre informazioni
     * di contesto.</p>
     * <p>Restituisce un intero rappresentante ad esempio 
     * il livello di indentazione della voce del menu:
     * <dl>
     * <dt>0</dt>
     * <dd>primo livello, ovvero voci presentate di default,
     * navigazione di default o alberatura piatta</dd>
     * <dt>1</dt>
     * <dd>voci ad un livello di indentazione sotto al 
     * livello zero</dd>
     * <dt>2</dt>
     * <dd>voci figlie delle voci di livello 1</dd>
     * <dt>...</dt>
     * <dd>la serie potrebbe continuare</dd>
     * </dl>
     * La serie di indentazioni potrebbe proseguire indefinitamente
     * (in teoria, fino alla massima capienza di un intero in Java,
     * che &egrave; pari a <pre>[2<sup>31</sup>-1]</pre>). 
     * Tuttavia, per ragioni di ordine pratico (di praticit&agrave;
     * di navigazione) nella maggioranza dei casi esistono solo 
     * voci di livello zero e 1, in taluni casi di livello 2.
     * </p> 
     * 
     * @return </code>livello</code> - un intero rappresentante il livello della voce di menu 
     */
    public int getLivello() {
        return this.livello;
    }
    
    
    /**
     * @param livello un intero per impostare il livello della voce
     */
    public void setLivello(int livello) {
        this.livello = livello;
    }
    
    
    /**
     * <p>Metodo per sapere se l'url specificato &egrave; interno o meno.</p>
     * <p>Pi&uacute; in dettaglio, questo attributo pu&ograve; essere usato per definire 
     * se l'url della voce di menu punta a un'applicazione web istituzionale 
     * oppure a un sito esterno.</p>
     *
     * @return true se l'url &egrave; interno, false altrimenti.
     */
    public boolean isUrlInterno() {
        return urlInterno;
    }    
    
    
    /**
     * Metodo per impostare se l'url &egrave; interno o meno.
     *
     * @param v valore da assegnare alla variabile
     */
    public void setUrlInterno(boolean v) {
        urlInterno = v;
    }

    
    /**
     * <p>Restituisce il valore dell'attributo specificante se l'item 
     * deve essere considerato 'privato'.</p>
     * <p>In generale, l'attributo pu&ograve; essere usato per definire 
     * altre informazioni contestuali, p.es. il fatto che il link richiede 
     * un'autenticazione a monte del <cite>landing</cite>
     * o il fatto che l'elemento non &egrave; pubblico.</p> 
     * 
     * @return boolean - true se l'elemento e' privato, false altrimenti
     */
    public boolean isPrivato() {
        return privato;
    }

    
    /**
     * Imposta il valore dell'attributo specificante se l'item
     * deve essere considerato 'privato'
     * 
     * @param privato flag boolean true se l'elemento e' privato, false altrimenti
     */
    public void setPrivato(boolean privato) {
        this.privato = privato;
    }
    

    /**
     * <p>Attributo che serve a memorizzare ulteriori informazioni, 
     * utile e.g. quando questo oggetto viene usato per incapsulare valori 
     * che necessitano di molte informazioni aggiuntive.</p>
     * 
     * @return <code>String</code> extraInfo - un oggetto String contenente eventuali informazioni aggiuntive
     */
    public String getExtraInfo() {
        return extraInfo;
    }


    /**
     * @param extraInfo le eventuali informazioni extra da impostare
     */
    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }


    /**
     * @return Restituisce un codice impostato per la voce
     */
    public String getCodice() {
        return codice;
    }


    /**
     * @param codice un codice da impostare per la voce
     */
    public void setCodice(String codice) {
        this.codice = codice;
    }


    /**
     * @return restituisce un intero che rappresenta un codice numerico della voce
     */
    public int getCod1() {
        return cod1;
    }


    /**
     * @param cod1 intero che rappresenta un codice numerico della voce, da impostare
     */
    public void setCod1(int cod1) {
        this.cod1 = cod1;
    }


    /**
     * @return restituisce un intero che rappresenta un codice numerico della voce
     */
    public int getCod2() {
        return cod2;
    }


    /**
     * @param cod2 intero che rappresenta un codice numerico della voce, da impostare
     */
    public void setCod2(int cod2) {
        this.cod2 = cod2;
    }


    /**
     * @return restituisce un intero che rappresenta un codice numerico della voce
     */
    public int getCod3() {
        return cod3;
    }


    /**
     * @param cod3 intero che rappresenta un codice numerico della voce, da impostare
     */
    public void setCod3(int cod3) {
        this.cod3 = cod3;
    }


    /**
     * @return restituisce un intero che rappresenta un codice numerico della voce
     */
    public int getCod4() {
        return cod4;
    }


    /**
     * @param cod4 intero che rappresenta un codice numerico della voce, da impostare
     */
    public void setCod4(int cod4) {
        this.cod4 = cod4;
    }


    /**
     * @return restituisce un numero a virgola mobile per rappresentare un indice quantitativo della voce
     */
    public float getValue1() {
        return value1;
    }


    /**
     * @param value1 numero a virgola mobile per rappresentare un indice quantitativo, da impostare
     */
    public void setValue1(float value1) {
        this.value1 = value1;
    }


    /**
     * @return restituisce un numero a virgola mobile per rappresentare un indice quantitativo della voce
     */
    public float getValue2() {
        return value2;
    }


    /**
     * @param value2 numero a virgola mobile per rappresentare un indice quantitativo, da impostare
     */
    public void setValue2(float value2) {
        this.value2 = value2;
    }


    /**
     * @return restituisce un numero a virgola mobile per rappresentare un indice quantitativo della voce
     */
    public float getValue3() {
        return value3;
    }


    /**
     * @param value3 numero a virgola mobile per rappresentare un indice quantitativo, da impostare
     */
    public void setValue3(float value3) {
        this.value3 = value3;
    }


    /**
     * @return restituisce un numero a virgola mobile per rappresentare un indice quantitativo della voce
     */
    public float getValue4() {
        return value4;
    }


    /**
     * @param value4 numero a virgola mobile per rappresentare un indice quantitativo, da impostare
     */
    public void setValue4(float value4) {
        this.value4 = value4;
    }
    
}