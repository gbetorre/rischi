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
 *   Copyright (C) 2022 renewed 2023 Giovanroberto Torre
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

import it.rol.exception.AttributoNonValorizzatoException;


/** 
 * <p>Classe per rappresentare quesiti.</p>   
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class QuestionBean extends ItemBean {
    
    /**
     * Generated Serial Version ID (let's compliant with Hybernate, man!)
     */
    private static final long serialVersionUID = 6507852092768226919L;
    /**
     *  Nome di questa classe. 
     *  Viene utilizzato per contestualizzare i messaggi di errore.
     */
    private final String FOR_NAME = "\n" + this.getClass().getName() + ": "; //$NON-NLS-1$
    
    /* **************************************************** *
     *                  Variabili d'istanza                 *
     * **************************************************** */
    /**
     * Formulazione del quesito
     */
    private String formulazione;
    /**
     * Ambito di analisi
     */
    private CodeBean ambito;
    /**
     * Tipo quesito
     */
    private CodeBean tipo;
    /**
     * Tipo formulazione
     */
    private ItemBean tipoFormulazione;
    /**
     * Quesito padre
     */
    private QuestionBean parentQuestion;
    /**
     * Quesiti figli
     */
    private ArrayList<QuestionBean> childQuestions;
    /**
     * Risposta
     */
    private QuestionBean answer;
    
    
    /* **************************************************** *
     *                      Costruttori                     *
     * **************************************************** */
    /**
     * Inizializza i campi a valori convenzionali di default.
     */
    public QuestionBean() {
        super();
        this.formulazione = null;
        this.ambito = this.tipo = null; 
        this.tipoFormulazione = null;
        this.parentQuestion = this.answer = null;
        this.childQuestions = null;
    }
    
 
    /* **************************************************** *
     *  Metodi getter e setter per formulazione del quesito *
     * **************************************************** */
    /**
     * Restituisce la formulazione di un quesito.
     * @return <code>nome</code> - la denominazione del gruppo 
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'attributo obbligatorio non &egrave; stato valorizzato
     */
    public String getFormulazione() throws AttributoNonValorizzatoException {
        if (this.formulazione == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo formulazione non valorizzato!");
        }
        return this.formulazione;
    }
    
    /**
     * Imposta la formulazione di un quesitp.
     * @param formulazione la domanda
     */
    public void setFormulazione(String formulazione) {
        this.formulazione = formulazione;
    }    
        
    
    /* **************************************************** *
     *     Metodi getter e setter per ambito di analisi     *
     * **************************************************** */
    /**
     * Restituisce l'ambito di analisi di un quesito.
     * @return <code>ambito</code> - l'ambito del quesito
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'attributo obbligatorio non &egrave; stato valorizzato
     */
    public CodeBean getAmbito() throws AttributoNonValorizzatoException {
        if (this.ambito == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo ambito non valorizzato!");
        }
        return this.ambito;
    }
    
    /**
     * Imposta l'ambito di analisi di un quesito.
     * @param ambito l'ambito di analisi del quesito
     */
    public void setAmbito(CodeBean ambito) {
        this.ambito = ambito;
    }
    
    
    /* **************************************************** *
     *      Metodi getter e setter per tipo di quesito      *
     * **************************************************** */
    /**
     * <p>Restituisce il tipo di un quesito.</p>
     * <p>Possono esservi quesiti di tipo “On/Off”, ovvero “Si/No”, vale a dire 
     * quesiti misurabili su scala nominale: la tipologia delle risposte &egrave;
     * quindi di tipo qualitativo perch&eacute; le risposte stesse non possono 
     * essere espresse in termini di “quantità” ma solo di “qualità”.
     * L’altro tipo di quesiti è rappresentato da quesiti di tipo quantitativo; 
     * ad essi la risposta &egrave; costituita da un numero appartenente ad N
     * e quindi è misurabile su una scala a rapporti.</p>
     * @return <code>tipo</code> - il tipo del quesito
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'attributo obbligatorio non &egrave; stato valorizzato
     */
    public CodeBean getTipo() throws AttributoNonValorizzatoException {
        if (this.tipo == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo tipo non valorizzato!");
        }
        return this.tipo;
    }
    
    /**
     * Imposta il tipo di un quesito.
     * @param tipo il tipo del quesito
     */
    public void setTipo(CodeBean tipo) {
        this.tipo = tipo;
    }
    
    
    /* **************************************************** *
     *    Metodi getter e setter per tipo di formulazione   *
     * **************************************************** */
    /**
     * <p>Restituisce il tipo di formulazione di un quesito.</p>
     * <p>Se un quesito qualitativo (ovvero un quesito di tipo "Si/No") &egrave; 
     * formulato in modo che la risposta "S&iacute;"
     * &egrave; identificativa di alto rischio, la formulazione del quesito 
     * &egrave; "Affermativa"; viceversa &egrave; "Negativa".<br />
     * Se un quesito quantitativo &egrave; formulato in modo che valori
     * alti (cio&egrave; distanti da zero) siano indicativi di alto rischio,
     * &egrave; un quesito con formulazione "Quantitativa Positiva"; viceversa
     * ha formulazione "Quantitativa Negativa".</p>
     * @return <code>tipoFormulazione</code> - il tipo di formulazione del quesito
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'attributo obbligatorio non &egrave; stato valorizzato
     */
    public ItemBean getTipoFormulazione() throws AttributoNonValorizzatoException {
        if (this.tipoFormulazione == null) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo tipoFormulazione non valorizzato!");
        }
        return this.tipoFormulazione;
    }
    
    /**
     * Imposta il tipo di formulazione di un quesito.
     * @param tipoFormulazione il tipoFormulazione da impostare per il quesito
     */
    public void setTipoFormulazione(ItemBean tipoFormulazione) {
        this.tipoFormulazione = tipoFormulazione;
    }

    
    /* **************************************************** *
     *       Metodi getter e setter per quesito padre       *
     * **************************************************** */
    /**
     * Restituisce il quesito padre del quesito corrente.
     * @return <code>parent</code> - l'eventuale padre del quesito
     */
    public QuestionBean getParentQuestion()  {
        return this.parentQuestion;
    }
    
    /**
     * Imposta il quesito padre del quesito corrente.
     * @param parent il quesito padre del quesito corrente
     */
    public void setParentQuestion(QuestionBean parent) {
        this.parentQuestion = parent;
    }
    
    
    /* **************************************************** *
     *      Metodi getter e setter per quesiti "di cui"     *
     * **************************************************** */
    /**
     * Restituisce i quesiti figli del quesito corrente.
     * @return <code>parent</code> - gli eventuali quesiti "di cui" del quesito
     */
    public ArrayList<QuestionBean> getChildQuestions() {
        return this.childQuestions;
    }

    /**
     * Imposta i quesiti "di cui" del quesito corrente.
     * @param childs i quesiti "di cui" del quesito corrente
     */
    public void setChildQuestions(ArrayList<QuestionBean> childs) {
        this.childQuestions = childs;
    }

    
    /* **************************************************** *
     *        Metodi getter e setter per la risposta        *
     * **************************************************** */
    /**
     * Restituisce la risposta al quesito corrente.
     * @return <code>answer</code> - l'eventuale risposta data al quesito
     */
    public QuestionBean getAnswer()  {
        return this.answer;
    }
    
    /**
     * Imposta la risposta al quesito corrente.
     * @param answer la risposta al quesito corrente
     */
    public void setAnswer(QuestionBean answer) {
        this.answer = answer;
    }
    
}
