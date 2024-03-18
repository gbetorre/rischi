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

package it.rol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

import it.rol.bean.InterviewBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.WebStorageException;


/**
 * <p>Query &egrave; l'interfaccia pubblic contenente tutte le query della
 * web-application &nbsp;<code>Processi on Line (prol)</code>, tranne quelle
 * composte dinamicamente da metodi implementati, di cui comunque dichiara
 * l'interfaccia.</p>
 * <p>Definisce inoltre alcune costanti di utilit&agrave;.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public interface Query extends Serializable {
    /* ********************************************************************** *
     *               Costanti convenzionali per valori interi                 *
     * ********************************************************************** */
    /**
     * <p>Costante parlante per valore da passare sul secondo argomento
     * in query strutturate in modo da considerare il primo se il secondo
     * vale NOTHING oppure il secondo se il primo vale NOTHING e il secondo
     * vale GET_ALL_BY_CLAUSE.</p>
     */
    public static final int GET_ALL_BY_CLAUSE = -1;
    /* ********************************************************************** *
     *               Costanti convenzionali per valori boolean                *
     * ********************************************************************** */
    /**
     * <p>Costante parlante per flag da passare sul secondo argomento
     * in query strutturate al fine di recuperare tutti i record, oppure
     * da utilizzare in metodi per strutturare specifiche condizioni.</p>
     */
    public static final boolean GET_ALL = true;
    /* ********************************************************************** *
     *                    Query di selezione "di servizio"                    *
     * ********************************************************************** */
    /**
     * <p>Estrae le classi Command previste per la/le applicazione/i.</p>
     */
    public static final String LOOKUP_COMMAND =
            "SELECT " +
            "       id                  AS \"id\"" +
            "   ,   nome                AS \"nomeReale\"" +
            "   ,   nome                AS \"nomeClasse\"" +
            "   ,   token               AS \"nome\"" +
            "   ,   labelweb            AS \"labelWeb\"" +
            "   ,   jsp                 AS \"paginaJsp\"" +
            "   ,   informativa         AS \"informativa\"" +
            "  FROM command";

    /**
     * <p>Estrae l'id massimo da una tabella definita nel chiamante</p>
     */
    public static final String SELECT_MAX_ID =
            "SELECT " +
            "       MAX(id)             AS \"max\"" +
            "   FROM ";

    /**
     * <p>Estrae l'id minimo da una tabella definita nel chiamante</p>
     */
    public static final String SELECT_MIN_ID =
            "SELECT " +
            "       MIN(id)             AS \"min\"" +
            "   FROM ";

    /**
     * <p>Estrae il massimo codice di un rischio avente data tipologia di codice 
     * dalla tabella del rischio corruttivo</p>
     */
    public static final String SELECT_MAX_RISK_CODE =
            "SELECT " +
            "       MAX(codice)         AS \"nome\"" +
            "   FROM rischio_corruttivo" +
            "   WHERE codice ILIKE ?";
    
    /**
     * <p>Estrae l'utente con username e password passati come parametri.</p>
     */
    public static final String GET_USR =
            "SELECT " +
            "       U.id                AS \"usrId\"" +
            "   ,   P.id                AS \"id\"" +
            "   ,   P.nome              AS \"nome\"" +
            "   ,   P.cognome           AS \"cognome\"" +
            "   ,   P.sesso             AS \"sesso\"" +
            "   ,   P.data_nascita      AS \"dataNascita\"" +
            "   ,   P.codice_fiscale    AS \"codiceFiscale\"" +
            "   ,   P.email             AS \"email\"" +
            "   ,   P.cittadinanza      AS \"cittadinanza\"" +
            "   ,   P.note              AS \"note\"" +
            "   FROM usr U" +
            "       INNER JOIN persona P ON P.id = U.id_persona" +
            "   WHERE   login = ?" +
            "       AND (( passwd IS NULL OR passwd = ? ) " +
            "           AND ( passwdform IS NULL OR passwdform = ? ))";

    /**
     * <p>Estrae il ruolo di una persona
     * avente login passato come parametro,
     * assumendo che sulla login ci sia un vincolo di UNIQUE.</p>
     */
    public static final String GET_RUOLOUTENTE =
            "SELECT " +
            "       RA.id               AS \"id\"" +
            "   ,   RA.nome             AS \"nome\"" +
            "   FROM ruolo_applicativo RA " +
            "       INNER JOIN usr U on RA.id = U.id_ruolo" +
            "   WHERE U.login = ?";

    /**
     * <p>Estrae identificativo tupla ultimo accesso, se esiste
     * per l'utente il cui username viene passato come parametro.</p>
     */
    public static final String GET_ACCESSLOG_BY_LOGIN =
            "SELECT " +
            "       A.id                AS  \"id\"" +
            "   FROM access_log A " +
            "   WHERE A.login = ? ";

    /**
     * <p>Estrae la password criptata e il seme dell'utente,
     * identificato tramite username, passato come parametro.</p>
     */
    public static final String GET_ENCRYPTEDPASSWORD =
            "SELECT " +
            "       U.passwdform        AS \"nome\"" +
            "   ,   U.salt              AS \"informativa\"" +
            "   FROM usr U" +
            "   WHERE U.login = ?";

    /* ********************************************************************** *
     *                            Query di Selezione                          *
     * ********************************************************************** */
    /**
     * <p>Estrae:
     * <dl><dt>se viene passato l'id rilevazione sul primo e secondo parametro
     * <dd>i dati di una specifica rilevazione, avente id passato come parametro,</dd><br>
     * <p><em>oppure</em></p>
     * <dt>se viene passato -1 sul primo e secondo parametro</dt>
     * <dd>i dati dell'ultima rilevazione che ha trovato,
     * in base all'ordine di data_rilevazione</dd></dl></p>
     */
    public static final String GET_SURVEY =
            "SELECT " +
            "       R.id                AS \"id\"" +
            "   ,   R.codice            AS \"nome\"" +
            "   ,   R.nome              AS \"informativa\"" +
            "   ,   R.ordinale          AS \"ordinale\"" +
            "   FROM rilevazione R" +
            "   WHERE (R.id = ? OR -1 = ?)" +
            "       AND R.chiusa = true" +
            "   ORDER BY data_rilevazione DESC";    
    
    /**
     * <p>Estrae tutti i macroprocessi censiti dall'anticorruzione filtrati 
     * in base all'identificativo della rilevazione, passato come parametro.</p>
     */
    public static final String GET_MACRO_AT_BY_SURVEY =
            "SELECT DISTINCT" +
            "       MAT.id                  AS \"id\"" +
            "   ,   MAT.codice              AS \"codice\"" +
            "   ,   MAT.nome                AS \"nome\"" +
            "   ,   MAT.ordinale            AS \"ordinale\"" +
            "   ,   MAT.id_rilevazione      AS \"idRilevazione\"" +
            "   FROM macroprocesso_at MAT" +
            "       INNER JOIN rilevazione R ON MAT.id_rilevazione = R.id" +
            "   WHERE R.codice ILIKE ?" +
            "   ORDER BY MAT.nome, MAT.codice";
    
    /**
     * <p>Estrae tutti i processi anticorruzione appartenenti a un macroprocesso
     * anticorruzione, il cui identificativo viene passato come parametro
     * e che sono collegati al codice identificativo della rilevazione corrente,
     * che viene passato come parametro.</p>
     */
    public static final String GET_PROCESSI_AT_BY_MACRO =
            "SELECT DISTINCT" +
            "       PRAT.id                 AS \"id\"" +
            "   ,   PRAT.codice             AS \"codice\"" +
            "   ,   PRAT.nome               AS \"nome\"" +
            "   ,   PRAT.ordinale           AS \"ordinale\"" +
            "   ,   PRAT.smartworking       AS \"smartWorking\"" +
            "   ,   (SELECT count(*) FROM input_processo_at INPAT WHERE INPAT.id_processo_at = PRAT.id AND INPAT.id_rilevazione = PRAT.id_rilevazione) AS \"vincoli\"" +
            "   ,   (SELECT count(*) FROM attivita A WHERE A.id_processo_at = PRAT.id AND A.id_rilevazione = PRAT.id_rilevazione) AS \"descrizioneStatoCorrente\"" +
            "   ,   (SELECT count(*) FROM output_processo_at OUTPAT WHERE OUTPAT.id_processo_at = PRAT.id AND OUTPAT.id_rilevazione = PRAT.id_rilevazione) AS \"obiettiviMisurabili\"" +
            "   ,   AR.nome                 AS \"areaRischio\"" +
            "   FROM processo_at PRAT" +
            "       INNER JOIN macroprocesso_at MAT ON PRAT.id_macroprocesso_at = MAT.id" +
            "       INNER JOIN area_rischio AR ON MAT.id_area_rischio = AR.id" +
            "       INNER JOIN rilevazione R ON PRAT.id_rilevazione = R.id" +
            "   WHERE PRAT.id_macroprocesso_at = ?" +
            "       AND R.codice ILIKE ?" +
            "   ORDER BY PRAT.nome, PRAT.codice";

    /**
     * <p>Estrae tutti i sottoprocessi anticorruzione appartenenti a un processo
     * anticorruzione, il cui identificativo viene passato come parametro.
     * e che sono collegati al codice identificativo della rilevazione corrente,
     * che viene passato come parametro.</p>
     */
    public static final String GET_SOTTOPROCESSI_AT_BY_PROCESS =
            "SELECT DISTINCT" +
            "       SPRAT.id                AS \"id\"" +
            "   ,   SPRAT.codice            AS \"codice\"" +
            "   ,   SPRAT.nome              AS \"nome\"" +
            "   ,   SPRAT.ordinale          AS \"ordinale\"" +
            "   ,   SPRAT.smartworking      AS \"smartWorking\"" +
            // Eventualmente contare qui quanti input, fasi, output, rischi... ha un sottoprocesso
            "   FROM sottoprocesso_at SPRAT" +
            "       INNER JOIN processo_at PRAT ON SPRAT.id_processo_at = PRAT.id" +
            "       INNER JOIN rilevazione R ON SPRAT.id_rilevazione = R.id" +
            "   WHERE SPRAT.id_processo_at = ?" +
            "       AND R.codice ILIKE ?" +
            "   ORDER BY SPRAT.codice";

    /**
     * <p>Estrae il macroprocesso censito dall'anticorruzione sulla base 
     * dell'identificativo di un suo processo anticorruttivo figlio, 
     * e della rilevazione, i cui identificativi sono entrambi passati 
     * come parametri.</p>
     */
    public static final String GET_MACRO_AT_BY_CHILD =
            "SELECT" +
            "       MAT.id                  AS \"id\"" +
            "   ,   MAT.codice              AS \"codice\"" +
            "   ,   MAT.nome                AS \"nome\"" +
            "   ,   MAT.ordinale            AS \"ordinale\"" +
            "   ,   MAT.id_rilevazione      AS \"idRilevazione\"" +
            "   FROM macroprocesso_at MAT" +
            "       INNER JOIN processo_at PAT ON PAT.id_macroprocesso_at = MAT.id" +
            "       INNER JOIN rilevazione R ON MAT.id_rilevazione = R.id" +
            "   WHERE PAT.id = ?" +
            "       AND R.id = ?" ;
    
    /**
     * <p>Se sul terzo parametro viene passato il valore convenzionale -1 
     * estrae tutti i quesiti filtrati in base all'identificativo 
     * della rilevazione, passato come parametro; 
     * oppure se sul secondo e sul terzo parametro viene passato l'id di un
     * quesito, estrae tale quesito specifico.</p>
     */
    public static final String GET_QUESTIONS =
            "SELECT DISTINCT " +
            "       Q.id                    AS \"id\"" +
            "   ,   Q.codice                AS \"codice\"" +
            "   ,   Q.nome                  AS \"nome\"" +
            "   ,   Q.formulazione          AS \"formulazione\"" +
            "   ,   Q.ordinale              AS \"ordinale\"" +
            "   ,   Q.id_ambito_analisi     AS \"cod1\"" +
            "   ,   Q.id_tipo_quesito       AS \"cod2\"" +
            "   ,   Q.id_tipo_formulazione  AS \"cod3\"" +
            "   ,   Q.id_quesito            AS \"cod4\"" +
            "   FROM quesito Q" +
            "       INNER JOIN ambito_analisi AA ON Q.id_ambito_analisi = AA.id" +
            "       INNER JOIN tipo_quesito TQ ON Q.id_tipo_quesito = TQ.id" +
            "       INNER JOIN tipo_formulazione TF ON Q.id_tipo_formulazione = TF.id" +
            "   WHERE id_rilevazione = ?" + 
            "       AND (Q.id = ? OR -1 = ?)" +
            "   ORDER BY Q.id_ambito_analisi";
    
    /**
     * <p>Estrae i quesiti figli di un dato quesito padre in una data rilevazione
     * i cui identificativi vengono passati come parametro.</p>
     */
    public static final String GET_QUESTIONS_BY_QUESTION =
            "SELECT DISTINCT " +
            "       Q.id                    AS \"id\"" +
            "   ,   Q.codice                AS \"codice\"" +
            "   ,   Q.nome                  AS \"nome\"" +
            "   ,   Q.formulazione          AS \"formulazione\"" +
            "   ,   Q.ordinale              AS \"ordinale\"" +
            "   ,   Q.id_ambito_analisi     AS \"cod1\"" +
            "   ,   Q.id_tipo_quesito       AS \"cod2\"" +
            "   ,   Q.id_tipo_formulazione  AS \"cod3\"" +
            "   ,   Q.id_quesito            AS \"cod4\"" +
            "   FROM quesito Q" +
            "       INNER JOIN ambito_analisi AA ON Q.id_ambito_analisi = AA.id" +
            "       INNER JOIN tipo_quesito TQ ON Q.id_tipo_quesito = TQ.id" +
            "       INNER JOIN tipo_formulazione TF ON Q.id_tipo_formulazione = TF.id" +
            "   WHERE id_rilevazione = ?" + 
            "       AND Q.id_quesito = ?" +
            "   ORDER BY Q.id";
    
    /**
     * <p>Estrae i codici indicatore e i relativi quesiti associati.</p>
     */
    public static final String GET_QUESTIONS_BY_INDICATOR = 
            "SELECT DISTINCT" +
            "       IQ.cod_indicatore       AS \"nome\"" +
            "   ,   IQ.id_quesito           AS \"id\"" +
            "   FROM indicatore_quesito IQ" +
            "       INNER JOIN rilevazione R ON IQ.id_rilevazione = R.id" +
            "   WHERE (IQ.id_rilevazione = ? OR -1 = ?)" +
            "   ORDER BY IQ.cod_indicatore";
    
    /**
     * <p>Estrae l'ambito di analisi in base all'identificativo 
     * del quesito, oppure tutti gli ambiti di analisi se viene passato 
     * il valore fittizio -1.</p>
     */
    public static final String GET_AMBIT =
            "SELECT DISTINCT" +
            "       AA.id                   AS \"id\"" +
            "   ,   AA.nome                 AS \"nome\"" +
            "   ,   AA.valore               AS \"informativa\"" +
            "   ,   AA.ordinale             AS \"ordinale\"" +
            "   FROM ambito_analisi AA" +
            "       INNER JOIN quesito Q ON AA.id = Q.id_ambito_analisi" +
            "   WHERE (AA.id = ? OR -1 = ?)" +
            "   ORDER BY AA.id";
    
    /**
     * <p>Estrae il tipo di quesito in base all'identificativo 
     * del quesito, oppure tutti i tipi di quesito se viene passato 
     * il valore fittizio -1.</p>
     */
    public static final String GET_QUESTION_TYPE =
            "SELECT DISTINCT" +
            "       TQ.id                   AS \"id\"" +
            "   ,   TQ.nome                 AS \"nome\"" +
            "   ,   TQ.valore               AS \"informativa\"" +
            "   ,   TQ.ordinale             AS \"ordinale\"" +
            "   FROM tipo_quesito TQ" +
            "       INNER JOIN quesito Q ON TQ.id = Q.id_tipo_quesito" +
            "   WHERE (TQ.id = ? OR -1 = ?)" +
            "   ORDER BY TQ.id";
    
    /**
     * <p>Estrae il tipo di formulazione in base all'identificativo 
     * del quesito, oppure tutti i tipi di formulazione se viene passato 
     * il valore fittizio -1.</p>
     */
    public static final String GET_QUESTION_WORDING =
            "SELECT DISTINCT" +
            "       TF.id                   AS \"id\"" +
            "   ,   TF.nome                 AS \"nome\"" +
            "   ,   TF.valore               AS \"informativa\"" +
            "   ,   TF.criterio             AS \"extraInfo\"" +
            "   ,   TF.ordinale             AS \"ordinale\"" +
            "   FROM tipo_formulazione TF" +
            "       INNER JOIN quesito Q ON TF.id = Q.id_tipo_quesito" +
            "   WHERE (TF.id = ? OR -1 = ?)" +
            "   ORDER BY TF.id";
    
    /**
     * <p>Calcola il numero di quesiti dato l'identificativo della rilevazione 
     * cui sono collegati, passato come parametro.</p>
     */
    public static final String GET_QUESTION_AMOUNT_BY_SURVEY =
            "SELECT" +
            "       count(*)                AS \"informativa\"" +
            "   FROM quesito Q" +
            "   WHERE Q.id_rilevazione = ?";
    
    /**
     * <p>Conta il numero di quesiti che hanno ricevuto una risposta,
     * data l'intervista cui sono collegati, 
     * identificata tramite data e ora.</p>
     */
    public static final String GET_QUESTION_AMOUNT_WITH_ANSWER_BY_INTERVIEW =
            "SELECT" +
            "       count(*)                AS \"informativa\"" +
            "   FROM risposta R" +
            "       INNER JOIN quesito Q ON R.id_quesito = Q.id" +
            "   WHERE (NOT R.valore ILIKE '')" +
            "       AND R.valore IS NOT NULL" +
            "       AND R.data_ultima_modifica = ?" +
            "       AND R.ora_ultima_modifica = ?" +
            "       AND R.id_rilevazione = ?";

    /**
     * <p>Estrae le interviste effettuate intervistando le strutture
     * di livello 4. Per ogni struttura di 4° livello trovata, estrae
     * le strutture di livello superiore che la aggregano, nonch&eacute; 
     * i processi anticorruttivi sondati nel contesto dell'intervista
     * (sar&agrave; presente almeno uno: tra macroprocesso, processo o
     * sottoprocesso anticorruttivo e, al pi&uacute;, tutti e tre i livelli).</p>
     * <p>Ogni riga, quindi, corrisponde ad una distinta intervista.</p>
     */
    public static final String GET_INTERVIEWED_STRUCT_L4 = 
            "SELECT DISTINCT" +
            "   ,   R.id_struttura_liv4     AS \"cod4\"" +
            "   ,   R.id_struttura_liv3     AS \"cod3\"" +
            "   ,   R.id_struttura_liv2     AS \"cod2\"" +
            "   ,   R.id_struttura_liv1     AS \"cod1\"" +
            "   ,   R.id_macroprocesso_at   AS \"value1\"" +
            "   ,   R.id_processo_at        AS \"value2\"" +
            "   ,   R.id_sottoprocesso_at   AS \"value3\"" +       
            "   ,   R.data_ultima_modifica, AS \"codice\"" + 
            "   ,   R.ora_ultima_modifica,  AS \"extraInfo\"" + 
            "   FROM risposta R" +
            "   WHERE R.id_struttura_liv4 IS NOT NULL" +
            "   ORDER BY R.data_ultima_modifica, R.ora_ultima_modifica DESC";    

    /**
     * <p>Estrae le interviste effettuate nel contesto di una data rilevazione
     * se viene passato l'identificativo della rilevazione sul primo e secondo
     * parametro, oppure tutte le interviste se viene passato un valore 
     * convezionale (-1) sul secondo parametro. 
     * Per ogni riga sar&agrave; presente almeno
     * un riferimento ai processi anticorruttivi sondati 
     * nel contesto dell'intervista (sar&agrave; presente almeno uno: 
     * tra macroprocesso, processo o sottoprocesso anticorruttivo e, 
     * al pi&uacute;, tutti e tre i livelli).</p>
     * <p>Ogni riga, quindi, corrisponder&agrave; ad una distinta intervista.</p>
     */
    public static final String GET_INTERVIEWS = 
            "SELECT DISTINCT" +
            "       R.id_struttura_liv1     AS \"cod1\"" +
            "   ,   R.id_struttura_liv2     AS \"cod2\"" +
            "   ,   R.id_struttura_liv3     AS \"cod3\"" +
            "   ,   R.id_struttura_liv4     AS \"cod4\"" +
            "   ,   R.id_macroprocesso_at   AS \"value1\"" +
            "   ,   R.id_processo_at        AS \"value2\"" +
            "   ,   R.id_sottoprocesso_at   AS \"value3\"" +       
            "   ,   R.data_ultima_modifica  AS \"codice\"" + 
            "   ,   R.ora_ultima_modifica   AS \"extraInfo\"" + 
            "   ,   S.codice                AS \"labelWeb\"" +
            "   FROM risposta R" +
            "       INNER JOIN rilevazione S ON R.id_rilevazione = S.id" +
            "   WHERE (R.id_rilevazione = ? OR -1 = ?)" +
            "   ORDER BY R.data_ultima_modifica DESC, R.ora_ultima_modifica DESC";

    /**
     * <p>Estrae gli indicatori trovati nel contesto di una rilevazione
     * specifica, oppure indipendentemente dalla rilevazione.</p>
     */
    public static final String GET_INDICATORS = 
            "SELECT DISTINCT" +
            "       IND.codice              AS \"nome\"" +
            "   ,   IND.nome                AS \"informativa\"" +
            "   ,   IND.ordinale            AS \"ordinale\"" +
            "   FROM indicatore IND" +
            "       INNER JOIN rilevazione R ON IND.id_rilevazione = R.id" +
            "   WHERE (IND.id_rilevazione = ? OR -1 = ?)" +
            "   ORDER BY ordinale";
    
    /**
     * <p>Estrae tutti gli input di un processo anticorruttivo
     * il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_INPUT_BY_PROCESS_AT = 
            "SELECT DISTINCT" +
            "       INP.id                      AS \"id\"" +
            "   ,   INP.nome                    AS \"nome\"" +
            "   ,   INP.descrizione             AS \"informativa\"" +
            "   ,   INP.ordinale                AS \"ordinale\"" +
            "   ,   INP.interno                 AS \"urlInterno\"" +
            "   ,   INP.data_ultima_modifica    AS \"codice\"" +
            "   ,   INP.ora_ultima_modifica     AS \"extraInfo\"" +
            "   ,   INP.id_output               AS \"value1\"" +
            "   ,   INPAT.id_processo_at        AS \"value2\"" +
            "   ,   INP.id_rilevazione          AS \"value3\"" +
            "   ,   PAT.nome                    AS \"labelWeb\"" +
            "   ,   AR.nome                     AS \"nomeReale\"" +
            "   FROM input INP" +
            "       INNER JOIN input_processo_at INPAT ON INPAT.id_input = INP.id" +
            "       INNER JOIN processo_at PAT ON INPAT.id_processo_at = PAT.id" +
            "       INNER JOIN macroprocesso_at MAT ON PAT.id_macroprocesso_at = MAT.id" +
            "       INNER JOIN area_rischio AR ON MAT.id_area_rischio = AR.id" +
            "   WHERE INPAT.id_processo_at = ?" +
            "       AND INPAT.id_rilevazione = ?" +
            "   ORDER BY INP.nome";
    
    /**
     * <p>Estrae tutti gli input di un sottoprocesso anticorruttivo
     * il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_INPUT_BY_SUBPROCESS_AT = 
            "SELECT DISTINCT" +
            "       INP.id                      AS \"id\"" +
            "   ,   INP.nome                    AS \"nome\"" +
            "   ,   INP.descrizione             AS \"informativa\"" +
            "   ,   INP.ordinale                AS \"ordinale\"" +
            "   ,   INP.interno                 AS \"urlInterno\"" +
            "   ,   INP.data_ultima_modifica    AS \"codice\"" +
            "   ,   INP.ora_ultima_modifica     AS \"extraInfo\"" +
            "   ,   INP.id_output               AS \"value1\"" +
            "   ,   INSPAT.id_sottoprocesso_at  AS \"value2\"" +
            "   ,   INP.id_rilevazione          AS \"value3\"" +
            "   ,   SPAT.nome                   AS \"labelWeb\"" +
            "   ,   AR.nome                     AS \"nomeReale\"" +
            "   FROM input INP" +
            "       INNER JOIN input_sottoprocesso_at INSPAT ON INSPAT.id_input = INP.id" +
            "       INNER JOIN sottoprocesso_at SPAT ON INSPAT.id_sottoprocesso_at = SPAT.id" +
            "       INNER JOIN processo_at PAT ON SPAT.id_processo_at = PAT.id" +
            "       INNER JOIN macroprocesso_at MAT ON PAT.id_macroprocesso_at = MAT.id" +
            "       INNER JOIN area_rischio AR ON MAT.id_area_rischio = AR.id" +
            "   WHERE INSPAT.id_sottoprocesso_at = ?" +
            "       AND INSPAT.id_rilevazione = ?" +
            "   ORDER BY INP.nome";

    /**
     * <p>Estrae gli estremi delle attivit&agrave; collegate ad un processo 
     * anticorruttivo (fasi del processo at),
     * il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_ACTIVITIES_BY_PROCESS_AT = 
            "SELECT DISTINCT" +
            "       A.id                        AS \"id\"" +
            "   ,   A.codice                    AS \"codice\"" +                    
            "   ,   A.nome                      AS \"nome\"" +
            "   ,   A.descrizione               AS \"descrizione\"" +
            "   ,   A.ordinale                  AS \"ordinale\"" +
            "   ,   A.mandatory                 AS \"mandatory\"" +
            "   FROM attivita A" +
            "   WHERE A.id_processo_at = ?" +
            "       AND A.id_rilevazione = ?" +
            "   ORDER BY A.ordinale, A.codice";

    /**
     * <p>Estrae gli estremi delle attivit&agrave; collegate ad un sottoprocesso 
     * anticorruttivo (fasi del sottoprocesso at),
     * il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_ACTIVITIES_BY_SUBPROCESS_AT = 
            "SELECT DISTINCT" +
            "       A.id                        AS \"id\"" +
            "   ,   A.codice                    AS \"codice\"" +                    
            "   ,   A.nome                      AS \"nome\"" +
            "   ,   A.descrizione               AS \"informativa\"" +
            "   ,   A.ordinale                  AS \"ordinale\"" +
            "   ,   A.mandatory                 AS \"mandatory\"" +
            "   FROM attivita A" +
            "   WHERE A.id_sottoprocesso_at = ?" +
            "       AND A.id_rilevazione = ?" +
            "   ORDER BY A.ordinale, A.codice";
    
    /**
     * <p>Estrae un elenco di identificativi di sole strutture 
     * (no soggetti contingenti) collegate, tramite le sue fasi, 
     * ad uno specifico processo, di cui viene passato 
     * l'identificativo come parametro.</p>
     * <p>La query effettua una LEFT OUTER JOIN con le strutture, per&ograve;,
     * tramite una clausola correlata, applica una restrizione, scartando 
     * le possibili tuple in cui tutti gli identificativi delle strutture 
     * sono pari a null (data la particolare articolazione delle strutture
     * organizzative in entit&agrave; multiple legate da una relazione
     * di composizione, non era pratico utilizzare direttamente una
     * EQUI-JOIN).</p>  
     */
    public static final String GET_STRUCTS_BY_PROCESS_AT = 
            "SELECT DISTINCT" +
            "       SA.id_struttura_liv1        AS \"cod1\"" +
            "   ,   SA.id_struttura_liv2        AS \"cod2\"" +
            "   ,   SA.id_struttura_liv3        AS \"cod3\"" +
            "   ,   SA.id_struttura_liv4        AS \"cod4\"" +
            "   ,   SA.id_rilevazione           AS \"value3\"" +
            "   ,   L1.nome                     AS \"extraInfo1\"" +
            "   ,   L2.nome                     AS \"extraInfo2\"" +
            "   ,   L3.nome                     AS \"extraInfo3\"" +
            "   ,   L4.nome                     AS \"extraInfo4\"" +
            "   FROM struttura_attivita SA" +
            "       INNER JOIN attivita A ON SA.id_attivita = A.id" +
            "       INNER JOIN processo_at PAT ON A.id_processo_at = PAT.id" +
            "       LEFT JOIN struttura_liv1 L1 ON SA.id_struttura_liv1 = L1.id" +
            "       LEFT JOIN struttura_liv2 L2 ON SA.id_struttura_liv2 = L2.id" +
            "       LEFT JOIN struttura_liv3 L3 ON SA.id_struttura_liv3 = L3.id" +
            "       LEFT JOIN struttura_liv4 L4 ON SA.id_struttura_liv4 = L4.id" +
            "   WHERE PAT.id = ?" +
            "       AND (SA.id_struttura_liv1 IS NOT NULL" +
            "         OR SA.id_struttura_liv2 IS NOT NULL" +
            "         OR SA.id_struttura_liv3 IS NOT NULL" +
            "         OR SA.id_struttura_liv4 IS NOT NULL" +
            "       )" + // La JOIN (SA,L*) è lasca ma un id struttura è richiesto
            "       AND SA.id_rilevazione = ?";
    
    /**
     * <p>Estrae un elenco di identificativi di soggetti contingenti
     * collegati, tramite le sue fasi, 
     * ad uno specifico processo, di cui viene passato 
     * l'identificativo come parametro.</p>
     * <p>Prestare attenzione al fatto che la query effettua una LEFT OUTER JOIN 
     * con le strutture, per&ograve; quella che recupera non &egrave; 
     * la struttura collegata alla fase ma quella eventualmente collegata 
     * al soggetto contingente corrente (ovvero quella che coadiuva il soggetto
     * stesso).</p>  
     */
    public static final String GET_SUBJECTS_BY_PROCESS_AT = 
            "SELECT DISTINCT" +
            "       SA.id_soggetto_contingente  AS \"id\"" +
            "   ,   SC.nome                     AS \"nome\"" +
            "   ,   SC.descrizione              AS \"informativa\"" +
            "   ,   SC.ordinale                 AS \"ordinale\"" +
            "   ,   SC.id_struttura_liv1        AS \"cod1\"" +
            "   ,   SC.id_struttura_liv2        AS \"cod2\"" +
            "   ,   SC.id_struttura_liv3        AS \"cod3\"" +
            "   ,   SC.id_struttura_liv4        AS \"cod4\"" +
            "   ,   SA.id_rilevazione           AS \"value1\"" +
            /*
            "   ,   L1.nome                     AS \"extraInfo1\"" +
            "   ,   L2.nome                     AS \"extraInfo2\"" +
            "   ,   L3.nome                     AS \"extraInfo3\"" +
            "   ,   L4.nome                     AS \"extraInfo4\"" +
            */
            "   FROM struttura_attivita SA" +
            "       INNER JOIN attivita A ON SA.id_attivita = A.id" +
            "       INNER JOIN processo_at PAT ON A.id_processo_at = PAT.id" +
            "       INNER JOIN soggetto_contingente SC ON SA.id_soggetto_contingente = SC.id" +
            "       LEFT JOIN struttura_liv1 L1 ON SA.id_struttura_liv1 = L1.id" +
            "       LEFT JOIN struttura_liv2 L2 ON SA.id_struttura_liv2 = L2.id" +
            "       LEFT JOIN struttura_liv3 L3 ON SA.id_struttura_liv3 = L3.id" +
            "       LEFT JOIN struttura_liv4 L4 ON SA.id_struttura_liv4 = L4.id" +
            "   WHERE PAT.id = ?" +
            "       AND SA.id_rilevazione = ?";

    /**
     * <p>Estrae un elenco di identificativi di strutture e soggetti contingenti
     * collegati ad una specifica attivit&agrave;, di cui viene passato 
     * l'identificativo come parametro.</p>
     */
    public static final String GET_STRUCTS_BY_ACTIVITY = 
            "SELECT DISTINCT" +
            "       SA.id_struttura_liv1        AS \"cod1\"" +
            "   ,   SA.id_struttura_liv2        AS \"cod2\"" +
            "   ,   SA.id_struttura_liv3        AS \"cod3\"" +
            "   ,   SA.id_struttura_liv4        AS \"cod4\"" +
            "   ,   SA.id_soggetto_contingente  AS \"value1\"" +      
            "   ,   SA.id_attivita              AS \"value2\"" +
            "   ,   SA.id_rilevazione           AS \"value3\"" +
            "   ,   SA.data_ultima_modifica     AS \"codice\"" +
            "   ,   SA.ora_ultima_modifica      AS \"extraInfo\"" +
            "   FROM struttura_attivita SA" +
            "   WHERE SA.id_attivita = ?" +
            "       AND SA.id_rilevazione = ?";
    
    /**
     * <p>Estrae gli estremi di un processo interessato a un'attivit&agrave;
     * il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_SUBJECT_BY_ACTIVITY = 
            "SELECT DISTINCT" +
            "       SC.id                       AS \"id\"" +               
            "   ,   SC.nome                     AS \"nome\"" +
            "   ,   SC.descrizione              AS \"informativa\"" +
            "   ,   SC.ordinale                 AS \"ordinale\"" +
            "   ,   SC.data_ultima_modifica     AS \"codice\"" +
            "   ,   SC.ora_ultima_modifica      AS \"extraInfo\"" +
            "   ,   SC.id_tipo                  AS \"value1\"" +
            "   ,   SC.id_stato                 AS \"value2\"" +            
            "   ,   SC.id_rilevazione           AS \"value3\"" +
            "   ,   SC.id_struttura_liv1        AS \"cod1\"" +
            "   ,   SC.id_struttura_liv2        AS \"cod2\"" +
            "   ,   SC.id_struttura_liv3        AS \"cod3\"" +
            "   ,   SC.id_struttura_liv4        AS \"cod4\"" +
            "   FROM soggetto_contingente SC" +
            "       INNER JOIN struttura_attivita SA ON SA.id_soggetto_contingente = SC.id" +
            "   WHERE SA.id_attivita = ?" +
            "       AND SA.id_rilevazione = ?" +
            "       AND SC.id_stato = 1" +
            "   ORDER BY SC.nome";

    /**
     * <p>Estrae gli estremi di un soggetto interessato a un'attivit&agrave;
     * in base all'identificativo dello stesso, purch&eacute; il soggetto
     * stesso risulti in stato 'attivo'.</p>
     */
    public static final String GET_SUBJECT = 
            "SELECT DISTINCT" +
            "       SC.id                       AS \"id\"" +               
            "   ,   SC.nome                     AS \"nome\"" +
            "   ,   SC.descrizione              AS \"informativa\"" +
            "   ,   SC.ordinale                 AS \"ordinale\"" +
            "   ,   SC.data_ultima_modifica     AS \"codice\"" +
            "   ,   SC.ora_ultima_modifica      AS \"extraInfo\"" +
            "   ,   SC.id_tipo                  AS \"value1\"" +
            "   ,   SC.id_stato                 AS \"value2\"" +            
            "   ,   SC.id_rilevazione           AS \"value3\"" +
            "   ,   SC.id_struttura_liv1        AS \"cod1\"" +
            "   ,   SC.id_struttura_liv2        AS \"cod2\"" +
            "   ,   SC.id_struttura_liv3        AS \"cod3\"" +
            "   ,   SC.id_struttura_liv4        AS \"cod4\"" +
            "   FROM soggetto_contingente SC" +
            "   WHERE SC.id = ?" +
            "       AND SC.id_rilevazione = ?" +
            "       AND SC.id_stato = 1" +
            "   ORDER BY SC.nome";
    
    /**
     * <p>Estrae tutti gli output di un processo anticorruttivo
     * il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_OUTPUT_BY_PROCESS_AT = 
            "SELECT DISTINCT" +
            "       OUT.id                      AS \"id\"" +
            "   ,   OUT.nome                    AS \"nome\"" +
            "   ,   OUT.descrizione             AS \"informativa\"" +
            "   ,   OUT.ordinale                AS \"ordinale\"" +
            "   ,   OUT.data_ultima_modifica    AS \"codice\"" +
            "   ,   OUT.ora_ultima_modifica     AS \"extraInfo\"" +
            "   ,   OUTPAT.id_processo_at       AS \"value2\"" +
            "   ,   OUT.id_rilevazione          AS \"value3\"" +
            "   FROM output OUT" +
            "       INNER JOIN output_processo_at OUTPAT ON OUTPAT.id_output = OUT.id" +
            "   WHERE OUTPAT.id_processo_at = ?" +
            "       AND OUTPAT.id_rilevazione = ?" +
            "   ORDER BY OUT.nome";

    /**
     * <p>Estrae tutti gli output di un sottoprocesso anticorruttivo
     * il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_OUTPUT_BY_SUBPROCESS_AT = 
            "SELECT DISTINCT" +
            "       OUT.id                      AS \"id\"" +
            "   ,   OUT.nome                    AS \"nome\"" +
            "   ,   OUT.descrizione             AS \"informativa\"" +
            "   ,   OUT.ordinale                AS \"ordinale\"" +
            "   ,   OUT.data_ultima_modifica    AS \"codice\"" +
            "   ,   OUT.ora_ultima_modifica     AS \"extraInfo\"" +
            "   ,   OUTSPAT.id_sottoprocesso_at AS \"value2\"" +
            "   ,   OUT.id_rilevazione          AS \"value3\"" +
            "   FROM output OUT" +
            "       INNER JOIN output_sottoprocesso_at OUTSPAT ON OUTSPAT.id_output = OUT.id" +
            "   WHERE OUTSPAT.id_sottoprocesso_at = ?" +
            "       AND OUTSPAT.id_rilevazione = ?" +
            "   ORDER BY OUT.nome";
    
    /**
     * <p>Estrae l'elenco di tutti gli output trovati per una data
     * rilevazione, selezionando le tuple atte a comporre l'elenco
     * degli output dei processi - livello 2, quindi no Macro (= 1) no Sub (= 3) -
     * anticorruttivi.</p>
     * <p>Ogni riga, quindi, corrisponder&agrave; ad un distinto output di processo,
     * contenente anche il numero di processi_at che sono generati da tale output.
     * Per generazione di un processo_at a partire da un output si intende
     * la generazione di quel processo_at avente in input l'output stesso;
     * i processi, infatti, vengono considerati come generati dagli input e, 
     * laddove un output di un processo funge anche da input di un altro processo,
     * questa informazione viene calcolata.</p>
     */
    public static final String GET_OUTPUTS = 
            "SELECT DISTINCT" +
            "       OUT.id                      AS \"id\"" +
            "   ,   OUT.nome                    AS \"nome\"" +
            "   ,   OUT.descrizione             AS \"descrizione\"" +
            "   ,   OUT.ordinale                AS \"ordinale\"" +
            "   ,   OUT.data_ultima_modifica    AS \"dataUltimaModifica\"" +
            "   ,   OUT.ora_ultima_modifica     AS \"oraUltimaModifica\"" +
            "   ,   OUT.id_rilevazione          AS \"idRilevazione\"" +
            "   ,   count(INPAT.id_processo_at) AS \"livello\"" +
            "   FROM output OUT" +
            "       INNER JOIN rilevazione S ON OUT.id_rilevazione = S.id" +
            "       LEFT JOIN input INP ON INP.id_output = OUT.id" + 
            "       LEFT JOIN input_processo_at INPAT ON INPAT.id_input = INP.id" +
            "   WHERE (OUT.id_rilevazione = ?)" +
            "   GROUP BY (OUT.id, OUT.nome, OUT.descrizione, OUT.ordinale, OUT.data_ultima_modifica, OUT.ora_ultima_modifica, OUT.id_rilevazione)" +
            "   ORDER BY OUT.nome";
    
    /**
     * <p>Estrae un dato output di un processo anticorruttivo
     * in funzione del suo identificativo, passato come parametro.</p>
     */
    public static final String GET_OUTPUT = 
            "SELECT" +
            "       OUT.id                      AS \"id\"" +
            "   ,   OUT.nome                    AS \"nome\"" +
            "   ,   OUT.descrizione             AS \"descrizione\"" +
            "   ,   OUT.ordinale                AS \"ordinale\"" +
            "   ,   OUT.data_ultima_modifica    AS \"dataUltimaModifica\"" +
            "   ,   OUT.ora_ultima_modifica     AS \"oraUltimaModifica\"" +
            "   ,   OUT.id_rilevazione          AS \"idRilevazione\"" +
            "   FROM output OUT" +
            "   ,   rilevazione R" +            // Old-style inner join, sometimes...
            "   WHERE OUT.id_rilevazione = R.id" +
            "       AND OUT.id = ?" +
            "       AND R.id = ?";
    
    /**
     * <p>Estrae i processi - livello 2, quindi no Macro (= 1) no Sub (= 3) -
     * collegati ad un input che deriva da un output, dove l'identificativo 
     * di quest'ultimo viene passato come parametro, e che sono stati rilevati 
     * nel contesto di una data rilevazione, il cui identificativo 
     * viene passato come parametro.</p>
     */
    public static final String GET_PROCESS_AT_BY_OUTPUT = 
            "SELECT DISTINCT" +
            "           PAT.id                          AS \"id\"" +
            "   ,       PAT.codice                      AS \"codice\"" +
            "   ,       PAT.nome                        AS \"nome\"" +
            "   , " +   Constants.ELEMENT_LEV_2 + "     AS \"livello\"" +
            "   ,       PAT.ordinale                    AS \"ordinale\"" +
            "   ,       PAT.id_macroprocesso_at" +
            "   FROM processo_at PAT" +
            "       INNER JOIN input_processo_at INPAT ON INPAT.id_processo_at = PAT.id" +
            "       INNER JOIN input INP ON INPAT.id_input = INP.id" +
            "   WHERE INP.id_output = ?" +
            "       AND INP.id_rilevazione = ?" +
            "   ORDER BY PAT.id_macroprocesso_at, PAT.nome";
    
    /**
     * <p>Estrae l'elenco di tutti i rischi corruttivi trovati per una data
     * rilevazione, oppure indipendentemente dalla rilevazione (in funzione
     * dei parametri), selezionando le tuple atte a comporre il registro 
     * dei rischi corruttivi.</p>
     * <p>Ogni riga, quindi, corrisponder&agrave; ad un distinto rischio,
     * contenente anche il numero di processi_at che corrono tale rischio.</p>
     */
    public static final String GET_RISKS = 
            "SELECT DISTINCT" +
            "       RC.id                               AS \"id\"" +
            "   ,   RC.codice                           AS \"informativa\"" +
            "   ,   RC.nome                             AS \"nome\"" +
            "   ,   RC.descrizione                      AS \"stato\"" +
            "   ,   RC.ordinale                         AS \"ordinale\"" +
            "   ,   count(RPAT.id_processo_at)::VARCHAR AS \"impatto\"" +
            "   FROM rischio_corruttivo RC" +
            "       INNER JOIN rilevazione S ON RC.id_rilevazione = S.id" +
            "       LEFT JOIN rischio_processo_at RPAT ON RPAT.id_rischio_corruttivo = RC.id" +
            "   WHERE (RC.id_rilevazione = ? OR -1 = ?)" +
            "   GROUP BY (RC.id, RC.codice, RC.nome, RC.descrizione, RC.ordinale)" +
            "   ORDER BY RC.nome";
    
    /**
     * <p>Estrae un rischio corruttivo dato il suo identificativo e quello
     * della rilevazione entro cui &egrave; collocato, passati entrambi
     * come parametri.</p> 
     */
    public static final String GET_RISK = 
            "SELECT" +
            "       RC.id                               AS \"id\"" +
            "   ,   RC.codice                           AS \"informativa\"" +
            "   ,   RC.nome                             AS \"nome\"" +
            "   ,   RC.descrizione                      AS \"stato\"" +
            "   ,   RC.ordinale                         AS \"ordinale\"" +
            "   FROM rischio_corruttivo RC" +
            "       INNER JOIN rilevazione S ON RC.id_rilevazione = S.id" +
            "   WHERE RC.id = ?" +
            "       AND RC.id_rilevazione = ?";

    /**
     * <p>Estrae i rischi collegati ad uno specifico processo anticorruttivo,
     * il cui identificativo viene passato come parametro, nel contesto di una
     * specifica rilevazione, il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_RISK_BY_PROCESS = 
            "SELECT DISTINCT" +
            "       RC.id                               AS \"id\"" +
            "   ,   RC.codice                           AS \"informativa\"" +
            "   ,   RC.nome                             AS \"nome\"" +
            "   ,   RC.descrizione                      AS \"stato\"" +
            "   ,   RC.ordinale                         AS \"ordinale\"" +
            "   FROM rischio_corruttivo RC" +
            "       INNER JOIN rischio_processo_at RPAT ON RPAT.id_rischio_corruttivo = RC.id" +
            "   WHERE RPAT.id_processo_at = ?" +
            "       AND RPAT.id_rilevazione = ?" +
            "   ORDER BY RC.nome";
    
    /**
     * <p>Estrae i rischi collegati ad uno specifico sottoprocesso anticorruttivo,
     * il cui identificativo viene passato come parametro, nel contesto di una
     * specifica rilevazione, il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_RISK_BY_SUB = 
            "SELECT DISTINCT" +
            "       RC.id                               AS \"id\"" +
            "   ,   RC.codice                           AS \"informativa\"" +
            "   ,   RC.nome                             AS \"nome\"" +
            "   ,   RC.descrizione                      AS \"stato\"" +
            "   ,   RC.ordinale                         AS \"ordinale\"" +
            "   FROM rischio_corruttivo RC" +
            "       INNER JOIN rischio_sottoprocesso_at RSPAT ON RSPAT.id_rischio_corruttivo = RC.id" +
            "   WHERE RSPAT.id_sottoprocesso_at = ?" +
            "       AND RSPAT.id_rilevazione = ?" +
            "   ORDER BY RC.nome";   

    /**
     * <p>Estrae i fattori abilitanti collegati ad uno specifico rischio e ad
     * uno specifico processo anticorruttivo,
     * il cui identificativo viene passato come parametro, nel contesto di una
     * specifica rilevazione, il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_FACTORS_BY_RISK_AND_PROCESS = 
            "SELECT DISTINCT" +
            "       FA.id                               AS \"id\"" +
            "   ,   FA.nome                             AS \"nome\"" +
            "   ,   FA.descrizione                      AS \"informativa\"" +
            "   ,   FA.ordinale                         AS \"ordinale\"" +
            "   FROM fattore_rischio_processo_at FRP" +
            "       INNER JOIN fattore_abilitante FA ON FRP.id_fattore_abilitante = FA.id" +
            "   WHERE FRP.id_processo_at = ?" +
            "       AND FRP.id_rischio_corruttivo = ?" +
            "       AND FRP.id_rilevazione = ?" +
            "   ORDER BY FA.nome";
    
    /**
     * <p>Estrae il registro dei fattori abilitanti.
     * I fattori abilitanti sono un'entit&agrave; forte, astorica e indipendente 
     * persino dalla rilevazione (sono trasversali alle varie rilevazioni).
     * La contestualizzazione temporale dei fattori abilitanti, infatti,
     * interviene nella relazione ternaria che è identificata dalla tupla
     * (id_fattore, id_rischio, id_processo); in questo caso &egrave; stata
     * anche introdotta la dipendenza dalla rilevazione, che contribuisce alla
     * chiave primaria della relazione.</p>
     */
    public static final String GET_FACTORS = 
            "SELECT DISTINCT" +
            "       FA.id                               AS \"id\"" +
            "   ,   FA.nome                             AS \"nome\"" +
            "   ,   FA.descrizione                      AS \"informativa\"" +
            "   ,   FA.ordinale                         AS \"ordinale\"" +
            "   FROM fattore_abilitante FA" +
            "   ORDER BY FA.ordinale, FA.nome";
 
    /**
     * <p>Estrae i processi - livello 2, quindi no Macro (= 1) no Sub (= 3) -
     * collegati a un rischio corruttivo il cui identificativo 
     * viene passato come parametro, e che sono stati rilevati nel contesto 
     * di una data rilevazione, il cui identificativo 
     * viene passato come parametro.</p>
     */
    public static final String GET_PROCESS_AT_BY_RISK = 
            "SELECT DISTINCT" +
            "           PAT.id                          AS \"id\"" +
            "   ,       PAT.codice                      AS \"codice\"" +
            "   ,       PAT.nome                        AS \"nome\"" +
            "   , " +   Constants.ELEMENT_LEV_2 + "     AS \"livello\"" +
            "   ,       PAT.ordinale                    AS \"ordinale\"" +
            "   ,       PAT.id_macroprocesso_at" +
            "   FROM processo_at PAT" +
            "       INNER JOIN rischio_processo_at RPAT ON RPAT.id_processo_at = PAT.id" +
            "   WHERE RPAT.id_rischio_corruttivo = ?" +
            "       AND RPAT.id_rilevazione = ?" +
            "   ORDER BY PAT.id_macroprocesso_at, PAT.nome";
    
    /**
     * <p>Conta quanti sono i processi - livello 2, quindi no Macro (= 1) no Sub (= 3) -
     * collegati a un dato rischio corruttivo.</p>
     */
    public static final String GET_RISK_PROCESS = 
            "SELECT count(*)" +
            "   FROM rischio_processo_at RAT" +
            "   WHERE RAT.id_processo_at = ?" +
            "       AND RAT.id_rischio_corruttivo = ?" +
            "       AND RAT.id_rilevazione = ?";
    
    /**
     * <p>Conta quanti sono i processi - livello 2, quindi no Macro (= 1) no Sub (= 3) -
     * collegati a un dato rischio corruttivo.</p>
     */
    public static final String GET_RISK_SUBPROCESS = 
            "SELECT count(*)" +
            "   FROM rischio_sottoprocesso_at RSPAT" +
            "   WHERE RSPAT.id_sottoprocesso_at = ?" +
            "       AND RSPAT.id_rischio_corruttivo = ?" +
            "       AND RSPAT.id_rilevazione = ?";
    
    /**
     * <p>Estrae un processo - livello 2, quindi no Macro (= 1) no Sub (= 3) -
     * dato l'id che viene passato come parametro.</p>
     */
    public static final String GET_PROCESS_AT_BY_ID = 
            "SELECT " +
            "           PAT.id                          AS \"id\"" +
            "   ,       PAT.codice                      AS \"codice\"" +
            "   ,       PAT.nome                        AS \"nome\"" +
            "   , " +   Constants.ELEMENT_LEV_2 + "     AS \"livello\"" +
            "   ,       PAT.ordinale                    AS \"ordinale\"" +
            "   ,       PAT.id_rilevazione              AS \"idRilevazione\"" +
            "   FROM processo_at PAT" +
            "   WHERE PAT.id = ?" +
            "       AND PAT.id_rilevazione = ?";
    
    /**
     * <p>Conta quante sono le associazioni esistenti tra:
     * dato processo, dato rischio e dato fattore abilitante
     * collegate a una data rilevazione.<br>
     * Dati i vincoli della relazione, ovvero
     * <pre>PRIMARY KEY (id_fattore_abilitante, 
     *             id_rischio_corruttivo, 
     *             id_processo_at, 
     *             id_rilevazione)</pre>
     * pu&ograve; restituire soltanto o 0 o 1.</p>
     */
    public static final String GET_FACTOR_RISK_PROCESS = 
            "SELECT count(*)" +
            "   FROM fattore_rischio_processo_at FRAT" +
            "   WHERE FRAT.id_processo_at = ?" +
            "       AND FRAT.id_rischio_corruttivo = ?" +
            "       AND FRAT.id_fattore_abilitante = ?" +
            "       AND FRAT.id_rilevazione = ?";
    
    /**
     * <p>Estrae i valori di tutti gli indicatori di rischio relativi
     *  ai processi censiti a fini anticorruttivi ottenuti nel contesto
     *  di una data rilevazione.<br />
     *  Questi valori sono stati memorizzati in un'apposita relazione
     *  in base a un pre-calcolo (caching).<br />
     *  Inoltre, d&agrave; la possibilit&agrave; di sottrarre un ammontare
     *  passato come parametro dai valori effettivi degli indicatori
     *  (se non si desidera alterare questi valori, &egrave; sufficiente 
     *  impostare il valore dell'ammontare a zero)
     *  Se, effettuando un'operazione utilizzando questo ammontare, si ottiene
     *  un risultato inferiore a zero (0 = rischio MINIMO) la query mantiene
     *  il valore 0.</p>
     *  <p>Utilizza la clausola WITH per dare un nome a una subquery che
     *  calcola quanti indicatori esistono per ogni identificativo di processo.</p>
     */
    public static final String GET_INDICATOR_PAT = 
            "WITH num_indicatori AS (" +
            "   SELECT " +
            "           PAT.id" +
            "        ,  count(INPAT.cod_indicatore)" +
            "       FROM processo_at PAT" +
            "           INNER JOIN indicatore_processo_at INPAT ON INPAT.id_processo_at = PAT.id" +
            "        GROUP BY (PAT.id)" +
            "        ORDER BY PAT.id " +
            ")" +
            "SELECT DISTINCT" +
            "       INPAT.id_processo_at                AS \"cod1\"" +
            "   ,   INPAT.cod_indicatore                AS \"codice\"" +
            "   ,   INPAT.id_rilevazione                AS \"cod2\"" +
            "   ,   CASE" +
            "           WHEN (INPAT.valore - ?) < 0 THEN 0" +
            "           ELSE (INPAT.valore - ?)" +
            "       END AS \"value1\"" +
            //"   ,   INPAT.valore - ?                    AS \"value1\"" +
            "   ,   INPAT.descrizione                   AS \"extraInfo\"" +
            "   ,   INPAT.note                          AS \"informativa\"" +
            "   ,   INPAT.ordinale                      AS \"ordinale\"" +
            "   ,   INPAT.data_ultima_modifica          AS \"extraInfo1\"" +
            "   ,   INPAT.ora_ultima_modifica           AS \"extraInfo2\"" +
            "   ,   INPAT.id_usr_ultima_modifica        AS \"extraInfo3\"" +
            "   ,   N.count                             AS \"cod3\"" +
            "   FROM indicatore_processo_at INPAT" +
            "       JOIN num_indicatori N ON INPAT.id_processo_at = N.id" +
            "   WHERE INPAT.id_rilevazione = ?" +
            "   ORDER BY INPAT.id_processo_at, INPAT.cod_indicatore";
    
    /**
     * <p>Seleziona  i valori del PxI di un processo anticorruttivo di dato id
     *  nel contesto di una data rilevazione.
     *  Li unisce con i valori del P perch&eacute; ci interessa la data
     *  di un indicatore non soggetto ad aggiornamento singolo.
     *  Quando infatti l'utente aggiorna la nota del giudizio sintetico,
     *  la data di ultima modifica del PxI viene aggiornata; se ci basassimo
     *  su di essa per stabilire quando &egrave; stato calcolato il valore
     *  del PxI (p.es. "ALTO") saremmo fuori strada; recuperiamo quindi
     *  anche la data di ultima modifica di un altro indicatore (ho preso P
     *  ma avrei potuto prendere qualunque altro) e poi la aggiungiamo
     *  all'oggetto che incapsuler&agrave; il PxI, sotto forma di 
     *  informazione aggiuntiva.</p>
     *  <p>Nota che, come sempre, c'erano anche altri modi di scrivere 
     *  la query, senza ricorrere alla UNION, p.es. modificando la
     *  clausola come segue:
     *  <pre>WHERE INPAT.cod_indicatore ILIKE 'P%'</pre>
     *  Tuttavia ho scelto di fare la UNION 
     *  per maggiore chiarezza (auto)esplicativa.</p>
     */
    public static final String GET_INDICATOR_PXI_BY_PROCESS = 
            "(SELECT" +
            "       INPAT.cod_indicatore                AS \"nome\"" +
            "   ,   INPAT.valore                        AS \"livello\"" +
            "   ,   INPAT.descrizione                   AS \"extraInfo\"" +
            "   ,   INPAT.note                          AS \"informativa\"" +
            "   ,   INPAT.data_ultima_modifica          AS \"extraInfo1\"" +
            "   ,   INPAT.ora_ultima_modifica           AS \"extraInfo2\"" +
            "   ,   INPAT.id_usr_ultima_modifica        AS \"extraInfo3\"" +
            "   FROM indicatore_processo_at INPAT" +
            "   WHERE INPAT.cod_indicatore = 'PxI'" +
            "       AND INPAT.id_processo_at = ?" +
            "       AND INPAT.id_rilevazione = ?)" +
            " UNION " +
            "(SELECT" +
            "       INPAT.cod_indicatore                AS \"nome\"" +
            "   ,   INPAT.valore                        " +
            "   ,   INPAT.descrizione                   " +
            "   ,   INPAT.note                          " +
            "   ,   INPAT.data_ultima_modifica          AS \"extraInfo1\"" +
            "   ,   INPAT.ora_ultima_modifica           " +
            "   ,   INPAT.id_usr_ultima_modifica        " +
            "   FROM indicatore_processo_at INPAT" +
            "   WHERE INPAT.cod_indicatore = 'P'" +
            "       AND INPAT.id_processo_at = ?" +
            "       AND INPAT.id_rilevazione = ?)";
    
    /**
     * <p>Estrae i valori di tutte le note al giudizio sintetico di tutti
     *  i processi censiti a fini anticorruttivi ottenuti nel contesto
     *  di una data rilevazione.</p>
     */
    public static final String GET_NOTES_PXI = 
            "SELECT DISTINCT" +
            "       INPAT.id_processo_at                AS \"cod1\"" +
            "   ,   INPAT.cod_indicatore                AS \"codice\"" +
            "   ,   INPAT.id_rilevazione                AS \"cod2\"" +
            "   ,   INPAT.valore                        AS \"value1\"" +
            "   ,   INPAT.descrizione                   AS \"extraInfo\"" +
            "   ,   INPAT.note                          AS \"informativa\"" +
            "   ,   INPAT.ordinale                      AS \"ordinale\"" +
            "   ,   INPAT.data_ultima_modifica          AS \"extraInfo1\"" +
            "   ,   INPAT.ora_ultima_modifica           AS \"extraInfo2\"" +
            "   ,   INPAT.id_usr_ultima_modifica        AS \"extraInfo3\"" +
            "   FROM indicatore_processo_at INPAT" +
            "   WHERE INPAT.cod_indicatore = 'PxI'" +
            "       AND INPAT.id_rilevazione = ?" +
            "   ORDER BY INPAT.id_processo_at";
    
    /**
     * <p>Estrae i valori di tutte le tipologie di misure.</p>
     */
    public static final String GET_MEASURE_TYPES = 
            "SELECT" +
            "       TM.id                               AS \"id\"" +
            "   ,   TM.prefisso                         AS \"informativa\"" +
            "   ,   TM.nome                             AS \"nome\"" +
            "   ,   TM.ordinale                         AS \"ordinale\"" +
            "   FROM tipo_misura TM" +
            "   ORDER BY TM.id";
    
    /**
     * <p>Estrae i valori dei caratteri delle misure.</p>
     */
    public static final String GET_MEASURE_CHARACTERS = 
            "SELECT" +
            "       TC.codice                           AS \"informativa\"" +
            "   ,   TC.nome                             AS \"nome\"" +
            "   ,   TC.ordinale                         AS \"ordinale\"" +
            "   FROM tipo_carattere TC" +
            "   ORDER BY TC.codice";

    /* ************************************************************************ *
     *  Interfacce di metodi che costruiscono dinamicamente Query di Selezione  *
     *    (in taluni casi non si riesce a prestabilire la query ma questa va    *
     *      assemblata in funzione dei parametri ricevuti)                      *
     * ************************************************************************ */
    /**
     * <p>Costruisce dinamicamente la query che seleziona le strutture di un dato livello
     * oppure le strutture di un livello gerarchico inferiore appartenenti a una struttura
     * di livello gerarchico superiore, a seconda del valore dei parametri di input.</p>
     *
     * @param idR  identificativo della rilevazione
     * @param idl4 identificativo della struttura di livello 4, -1 se si vogliono tutte le strutture di questo livello, oppure 0 se non interessa questo livello
     * @param idl3 identificativo della struttura di livello 3, -1 se si vogliono tutte le strutture di questo livello, oppure 0 se non interessa questo livello
     * @param idl2 identificativo della struttura di livello 2, -1 se si vogliono tutte le strutture di questo livello, oppure 0 se non interessa questo livello
     * @param idl1 identificativo della struttura di livello 1, -1 se si vogliono tutte le strutture di questo livello, oppure 0 se non interessa questo livello
     * @return <code>String</code> - la query che seleziona le strutture desiderate
     */
    public String getQueryStructures(int idR, int idl4, int idl3, int idl2, int idl1);
    
    /**
     * <p>Costruisce dinamicamente la query che seleziona una struttura in base al suo
     * identificativo, dato il livello identificante l'insieme entro cui cercarla.</p>
     *
     * @param idS   identificativo della struttura cercata
     * @param level marcatore dell'insieme entro cui effettuare la ricerca
     * @return <code>String</code> - la query che seleziona la struttura desiderata
     */
    public String getQueryStructure(int idS, byte level);
    
    /**
     * <p>Costruisce dinamicamente la query che seleziona una struttura 
     * di qualsivoglia livello (tupla di struttura_liv1 oppure di struttura_liv2...
     * etc.) in base ai parametri ricevuti.</p>
     * <p>In particolare,
     * verifica prima la significativit&agrave; 
     * dell'identificativo dell'eventuale struttura di livello 4;<ul>
     * <li>se questo &egrave; significativo, restituisce la query per recuperare
     * la struttura di livello 4 ed esce;</li>
     * <li>se invece questo non &egrave; significativo, verifica la 
     * significativit&agrave; dell'identificativo della struttura 
     * di livello 3, etc.</li></ul>
     * Alla fine dei test in cascata, restituisce la query per l'estrazione della 
     * struttura di primo livello, o un valore convenzionale (&mdash;) se 
     * nessun id &egrave; risultato significativo.
     * 
     * @param idR   identificativo della rilevazione
     * @param idl4  identificativo della struttura di livello 4, oppure -2 se non interessa questo livello
     * @param idl3  identificativo della struttura di livello 3, oppure -2 se non interessa questo livello
     * @param idl2  identificativo della struttura di livello 2, oppure -2 se non interessa questo livello
     * @param idl1  identificativo della struttura di livello 1, oppure -2 se non interessa questo livello
     * @return <code>String</code> - la query che seleziona la struttura cercata oppure &lsquo;&ndash;&rsquo; se nessun parametro id e' stato trovato significativo
     */
    public String getQueryStructureBySurvey(int idR, int idl4, int idl3, int idl2, int idl1);
    
    /**
     * <p>In funzione del parametro specificante il livello
     * (1 = macroprocesso_at | 2 = processo_at | 3 = sottoprocesso_at),
     * costruisce dinamicamente la query che estrae tutti i macroprocessi 
     * censiti dall'anticorruzione (oppure uno specifico macroprocesso) 
     * filtrati in base all'identificativo della rilevazione, passato 
     * come parametro, rappresentando in ogni tupla anche i valori 
     * delle entit&agrave; collegate.</p>
     * 
     * @param idP       identificativo di macroprocesso_at, processo_at o sottoprocesso_at
     * @param level     identificativo del livello a cui e' relativo l'id (1 = macroprocesso_at | 2 = processo_at | 3 = sottoprocesso_at)
     * @param idSur     codice identificativo della rilevazione
     * @return <code>String</code> - la query che seleziona l'insieme desiderato
     */
    public String getQueryMacroSubProcessAtBySurvey(int idP, byte level, int idSur);
    
    /**
     * <p>Costruisce dinamicamente la query che seleziona un insieme di risposte
     * ad una serie di quesiti associati a una data rilevazione.</p>
     * <p>Siccome potrebbero esservi pi&uacute; insiemi di risposte a 
     * parit&agrave; di parametri (se la stessa struttura e lo stesso processo
     * sono stati scelti pi&uacute; volte come "paletti" per l'intervista),
     * il metodo prende in input anche un numero che limita il totale dei record
     * da recuperare.</p>
     * 
     * @param params    mappa contenente tutti i parametri di navigazione trovati
     * @param idSurvey  identificativo della rilevazione
     * @param limit     numero di record da recuperare 
     * @param idQuest   identificativo di un quesito, se si vuol recuperare solo le risposte date a quel quesito
     * @param getAll    flag booleano; se true bisogna recuperare le risposte indipendentemente dall'id del quesito
     * @return <code>String</code> - la query che seleziona l'insieme desiderato
     */
    public String getQueryAnswers(HashMap<String, LinkedHashMap<String, String>> params, int idSurvey, int limit, int idQuest, boolean getAll);
    
    /**
     * <p>Costruisce dinamicamente la query che seleziona un insieme di risposte
     * ad una serie di quesiti associati ad una data intervista nel contesto
     * di una data rilevazione.</p>
     * 
     * @param params    oggetto contenente tutti i parametri utili per il recupero delle risposte
     * @param idSurvey  identificativo della rilevazione
     * @return <code>String</code> - la query che seleziona l'insieme desiderato
     * @throws AttributoNonValorizzatoException se risulta impossibile recuperare un attributo obbligatorio di un bean
     * @throws WebStorageException se manca uno o piu' elementi indispensabili per il recupero delle risposte
     */
    public String getQueryAnswers(InterviewBean params, int idSurvey) throws AttributoNonValorizzatoException, WebStorageException;
    
    /**
     * <p>Costruisce dinamicamente la query che seleziona un processo 
     * di qualsivoglia livello (tupla di macroprocesso_at oppure di processo_at...
     * etc.) in base ai parametri ricevuti.</p>
     * <p>In particolare,
     * verifica prima la significativit&agrave; 
     * dell'identificativo dell'eventuale sottoprocesso_at;<ul>
     * <li>se questo &egrave; significativo, restituisce la query per recuperare
     * il sottoprocesso_at ed esce;</li>
     * <li>se invece questo non &egrave; significativo, verifica la 
     * significativit&agrave; dell'identificativo del processo_at, etc.</li></ul>
     * Alla fine dei test in cascata, restituisce la query per l'estrazione del 
     * macroprocesso_at, o un valore convenzionale (&mdash;) se 
     * nessun id &egrave; risultato significativo.
     * 
     * @param idR   identificativo della rilevazione
     * @param idS   identificativo sottoprocesso anticorruzione
     * @param idP   identificativo processo anticorruzione
     * @param idM   identificativo macroprocesso anticorruzione
     * @return <code>String</code> - la query che seleziona il processo anticorruttivo cercato oppure &lsquo;&ndash;&rsquo; se nessun parametro id e' stato trovato significativo
     */
    public String getQueryProcessBySurvey(int idR, int idS, int idP, int idM);
    
    /* ********************************************************************** *
     *                         Query di inserimento                           *
     * ********************************************************************** */
    /**
     * <p>Query per inserimento dell'ultimo accesso al sistema.</p>
     */
    public static final String INSERT_ACCESSLOG_BY_USER =
            "INSERT INTO access_log" +
            "   (   id" +
            "   ,   login" +
            "   ,   data_ultimo_accesso" +
            "   ,   ora_ultimo_accesso )" +
            "   VALUES (? " +          // id
            "   ,       ? " +          // login
            "   ,       ? " +          // dataultimoaccesso
            "   ,       ?)" ;          // oraultimoaccesso

    /**
     * <p>Query per inserimento di una risposta ad uno specifico quesito
     * rivolto ad una specifica struttura e ad uno specifico processo
     * censito ai fini della mappatura dei rischi corruttivi.</p>
     */
    public static final String INSERT_ANSWER =
            "INSERT INTO risposta" +
            "   (   valore" +
            "   ,   note" +
            "   ,   ordinale" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   ,   id_quesito" +
            "   ,   id_rilevazione" +
            "   ,   id_struttura_liv1" +
            "   ,   id_struttura_liv2" +
            "   ,   id_struttura_liv3" +
            "   ,   id_struttura_liv4" +
            "   ,   id_macroprocesso_at" +
            "   ,   id_processo_at" +
            "   ,   id_sottoprocesso_at" +
            "   )" +
            "   VALUES (? " +       // valore
            "   ,       ? " +       // note
            "   ,       ? " +       // ordinale
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "   ,       ? " +       // id_quesito
            "   ,       ? " +       // id_rilevazione
            "   ,       ? " +       // id_struttura_liv1
            "   ,       ? " +       // id_struttura_liv2
            "   ,       ? " +       // id_struttura_liv3
            "   ,       ? " +       // id_struttura_liv4
            "   ,       ? " +       // id_macroprocesso_at
            "   ,       ? " +       // id_processo_at
            "   ,       ? " +       // id_sottoprocesso_at
            "          )" ;
    
    /**
     * <p>Query per inserimento di un rischio corruttivo.
     * Deve gestire anche l'inserimento dell'identificativo perch&eacute; 
     * il sequence <code>rischio_corruttivo_id_seq</code> 
     * potrebbe essere sfasato.</p>
     */
    public static final String INSERT_RISK =
            "INSERT INTO rischio_corruttivo" +
            "   (   id" +
            "   ,   codice" +
            "   ,   nome" +
            "   ,   descrizione" +
            "   ,   ordinale" +            
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   ,   id_rilevazione" +
            "   )" +
            "   VALUES (? " +       // id
            "   ,       ? " +       // codice
            "   ,       ? " +       // nome
            "   ,       ? " +       // descrizione
            "   ,       ? " +       // ordinale           
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "   ,       ? " +       // id_rilevazione
            "          )" ;
    
    /**
     * <p>Query per inserimento di un'associazione tra un rischio corruttivo
     * ed un processo censito dall'anticorruzione.</p>
     */
    public static final String INSERT_RISK_PROCESS =
            "INSERT INTO rischio_processo_at" +
            "   (   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   ,   id_processo_at" +
            "   ,   id_rischio_corruttivo" +
            "   ,   id_rilevazione" +
            "   )" +
            "   VALUES (? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "   ,       ? " +       // id_processo_at
            "   ,       ? " +       // id_rischio_corruttivo
            "   ,       ? " +       // id_rilevazione
            "          )" ;
    
    /**
     * <p>Query per inserimento di un'associazione tra un rischio corruttivo
     * ed un sottoprocesso censito dall'anticorruzione.</p>
     */
    public static final String INSERT_RISK_SUBPROCESS =
            "INSERT INTO rischio_sottoprocesso_at" +
            "   (   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   ,   id_sottoprocesso_at" +
            "   ,   id_rischio_corruttivo" +
            "   ,   id_rilevazione" +
            "   )" +
            "   VALUES (? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "   ,       ? " +       // id_sottoprocesso_at
            "   ,       ? " +       // id_rischio_corruttivo
            "   ,       ? " +       // id_rilevazione
            "          )" ;
    
    /**
     * <p>Query per inserimento di un'associazione tra:<ul>
     * <li>un rischio corruttivo</li>
     * <li>un fattore abilitante</li>
     * <li>un processo censito dall'anticorruzione</li>
     * <li>nel contesto di una data rilevazione</li>
     * </ul>
     * (entità debole associativa 4-aria PxRxFxS).</p>
     */
    public static final String INSERT_FACTOR_RISK_PROCESS =
            "INSERT INTO fattore_rischio_processo_at" +
            "   (   id_fattore_abilitante" +
            "   ,   id_rischio_corruttivo" +
            "   ,   id_processo_at" +
            "   ,   id_rilevazione" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   )" +
            "   VALUES (? " +       // id_fattore_abilitante
            "   ,       ? " +       // id_rischio_corruttivo
            "   ,       ? " +       // id_processo_at
            "   ,       ? " +       // id_rilevazione
            "   ,       ? " +       // data_ultima_modifica
            "   ,       ? " +       // ora_ultima_modifica
            "   ,       ? " +       // id_usr_ultima_modifica
            "          )" ;
    
    /**
     * <p>Query per inserimento dei valori calcolati
     * relativamente agli indicatori di rischio dei processi censiti
     * a fini anticorruttivi.</p>
     */
    public static final String INSERT_INDICATOR_PROCESS =
            "INSERT INTO indicatore_processo_at" +
            "   (   id_processo_at" +
            "   ,   cod_indicatore" +
            "   ,   id_rilevazione" +
            "   ,   valore" +
            "   ,   descrizione" +
            "   ,   note" +
            "   ,   ordinale" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   )" +
            "   VALUES (? " +       // id_processo_at
            "   ,       ? " +       // cod_indicatore
            "   ,       ? " +       // id_rilevazione
            "   ,       ? " +       // valore
            "   ,       ? " +       // descrizione
            "   ,       ? " +       // note
            "   ,       ? " +       // ordinale
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "          )" ;
    
    /* ********************************************************************** *
     *                         Query di aggiornamento                         *
     * ********************************************************************** */
    /**
     * <p>Query per aggiornamento di ultimo accesso al sistema.</p>
     */
    public static final String UPDATE_ACCESSLOG_BY_USER =
            "UPDATE access_log" +
            "   SET login  = ?" +
            "   ,   data_ultimo_accesso = ?" +
            "   ,   ora_ultimo_accesso = ?" +
            "   WHERE id = ? ";

    /**
     * <p>Query per aggiornamento dei valori di una data risposta.</p>
     */
    public static final String UPDATE_ANSWER =
            "UPDATE risposta" +
            "   SET valore                  = ?" +
            "   ,   note                    = ?" +
//            "   ,   data_ultima_modifica    = ?" +
//            "   ,   ora_ultima_modifica     = ?" +
//            "   ,   id_usr_ultima_modifica  = ?" +
            "   WHERE id_quesito            = ?" +
            "       AND id_rilevazione      = ?" +
            "       AND data_ultima_modifica = ?" +
            "       AND ora_ultima_modifica = ?";
    
    /**
     * <p>Query per aggiornamento di una nota di un giudizio sintetico.</p>
     */
    public static final String UPDATE_NOTE_BY_PROCESS =
            "UPDATE indicatore_processo_at" +
            "   SET note  = ?" +
            "   ,  data_ultima_modifica =   ?" +
            "   ,  ora_ultima_modifica =    ?" +
            "   ,  id_usr_ultima_modifica = ?" +
            "   WHERE cod_indicatore = 'PxI'" +
            "       AND id_processo_at = ?" +
            "       AND id_rilevazione = ?";

    /* ********************************************************************** *
     *                         Query di eliminazione                          *
     * ********************************************************************** */
    /**
     * <p>Query per eliminazione di tutte le tuple della tabella contenente
     * i risultati dell'elaborazione sugli indicatori (la DELETE senza
     * parametri &egrave; sulla tabella!).</p>
     */
    public static final String DELETE_INDICATOR_PROCESS_RESULTS =
            "DELETE FROM indicatore_processo_at";
    
}
