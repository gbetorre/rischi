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

package it.rol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

import it.rol.bean.InterviewBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.WebStorageException;


/**
 * <p>Query &egrave; l'interfaccia contenente tutte le query, 
 * &quot;secche&quot; e parametriche, della web-application &nbsp;
 * <code>ROL-RMS (Rischi On Line-Risk Mapping Software)</code>, 
 * tranne quelle composte a runtime da metodi implementati, 
 * di cui comunque dichiara l'interfaccia pubblica.<br>
 * Utilizza degli speciali marcatori (question marks) nei punti
 * in cui verranno passati i parametri, sfruttando il classico meccanismo 
 * del {@link java.sql.PreparedStatement}.</p>
 * <p>Definisce inoltre alcune costanti di utilit&agrave;.</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 * @see java.sql.PreparedStatement
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
     * <p>Estrae il massimo codice di un macroprocesso appartenente a una 
     * data area di rischio</p>
     */
    public static final String SELECT_MAX_MAT_CODE =
            "SELECT " +
            "       MAX(codice)         AS \"nome\"" +
            "   FROM macroprocesso_at" +
            "   WHERE id_area_rischio = ?";
    
    /**
     * <p>Estrae il massimo codice di un processo dato il codice
     * del suo macroprocesso e fornisce anche il codice macroprocesso
     * (il codice macroprocesso in effetti &egrave; gi&agrave; contenuto 
     * nel codice processo per&ograve; siccome in questo punto abbiamo solo 
     * l'id macro,ottenere il codice macro dalla stessa query &egrave; 
     * gratis in termini di accesso al disco o computazione (legata 
     * al successivo parsing nel caso in cui si debba ricavare a valle).</p>
     */
    public static final String SELECT_MAX_PAT_CODE =
            "SELECT " +
            "       MAX(PAT.codice)     AS \"nome\"" +
            "   ,   MAT.codice          AS \"informativa\"" +
            "   FROM processo_at PAT" +
            "       INNER JOIN macroprocesso_at MAT ON PAT.id_macroprocesso_at = MAT.id" +
            "   WHERE id_macroprocesso_at = ?" +
            "   GROUP BY(MAT.codice)";
    
    /**
     * <p>Estrae il numero di record di una tabella definita nel chiamante</p>
     */
    public static final String SELECT_COUNT =
            "SELECT " +
            "       count(*)            AS \"n\"" +
            "   FROM ";
    
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
     * <p>Estrae tutte le aree di rischio censite e storicizzate in base
     * alla rilevazione, il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_AREE_BY_SURVEY =
            "SELECT " +
            "       AR.id                   AS \"id\"" +
            "   ,   AR.codice               AS \"codice\"" +
            "   ,   AR.nome                 AS \"nome\"" +
            "   ,   AR.descrizione          AS \"informativa\"" +
            "   ,   AR.ordinale             AS \"ordinale\"" +
            "   FROM area_rischio AR" +
            "       INNER JOIN rilevazione R ON AR.id_rilevazione = R.id" +
            "   WHERE R.id = ?" +
            "   ORDER BY AR.codice, AR.nome";
    
    /**
     * <p>Estrae tutti i macroprocessi censiti dall'anticorruzione filtrati 
     * in base all'identificativo dell'area di rischio, il cui identificativo 
     * viene passato come parametro, nel contesto della rilevazione, 
     * il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_MACRO_AT_BY_AREA =
            "SELECT DISTINCT" +
            "       MAT.id                  AS \"id\"" +
            "   ,   MAT.codice              AS \"codice\"" +
            "   ,   MAT.nome                AS \"nome\"" +
            "   ,   MAT.ordinale            AS \"ordinale\"" +
            "   ,   MAT.id_rilevazione      AS \"idRilevazione\"" +
            "   FROM macroprocesso_at MAT" +
            "       INNER JOIN rilevazione R ON MAT.id_rilevazione = R.id" +
            "       INNER JOIN area_rischio AR ON MAT.id_area_rischio = AR.id" +
            "   WHERE AR.id = ?" +
            "       AND R.id = ?" +
            "   ORDER BY MAT.codice";
    
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
     *  impostare il valore dell'ammontare a zero).
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
            "   ORDER BY TM.ordinale";
    
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
    
    /**
     * <p>Estrae i valori di tutte le tipologie di indicatori.</p>
     */
    public static final String GET_INDICATOR_TYPES = 
            "SELECT" +
            "       TI.id                               AS \"id\"" +
            "   ,   TI.nome                             AS \"nome\"" +
            "   ,   TI.valore                           AS \"informativa\"" +
            "   ,   TI.ordinale                         AS \"ordinale\"" +
            "   FROM tipo_indicatore TI" +
            "   ORDER BY TI.ordinale";
    
    /**
     * <p>Estrae le tipologie di una misura.</p>
     */
    public static final String GET_MEASURE_TYPE = 
            "SELECT" +
            "       TM.id                               AS \"id\"" +
            "   ,   TM.prefisso                         AS \"informativa\"" +
            "   ,   TM.nome                             AS \"nome\"" +
            "   ,   TM.ordinale                         AS \"ordinale\"" +
            "   FROM tipo_misura TM" +
            "       INNER JOIN misura_tipologia MST ON MST.id_tipo_misura = TM.id" +
            "       INNER JOIN misura MS ON MST.cod_misura = MS.codice" +
            "   WHERE (MS.codice = ? AND MS.id_rilevazione = ?)" +
            "   ORDER BY TM.id";

    /**
     * <p>Seleziona l'elenco di tutte le misure di mitigazione trovate per una 
     * data rilevazione, oppure seleziona una specifica misura (in funzione
     * dei parametri), selezionando le tuple atte a comporre il registro 
     * delle misure di prevenzione e mitigazione dei rischi corruttivi.</p>
     * <p>Ogni riga, quindi, corrisponder&agrave; ad una distinta misura.</p>
     */
    public static final String GET_MEASURES = 
            "SELECT DISTINCT" +
            "       MS.codice                           AS \"codice\"" +
            "   ,   MS.nome                             AS \"nome\"" +
            "   ,   MS.descrizione                      AS \"informativa\"" +
            "   ,   MS.onerosa                          AS \"onerosa\"" +
            "   ,   MS.ordinale                         AS \"ordinale\"" +
            "   ,   MS.data_ultima_modifica             AS \"dataUltimaModifica\"" +
            "   ,   MS.ora_ultima_modifica              AS \"oraUltimaModifica\"" +
            "   ,   MS.id_rilevazione                   AS \"idRilevazione\"" +
            "   ,   count(MRPAT.id_rischio_corruttivo)::SMALLINT  AS \"uso\"" +
            "   ,   CASE" +
            "           WHEN (SELECT MM.codice FROM misuramonitoraggio MM WHERE MM.codice = MS.codice AND MM.id_rilevazione = MS.id_rilevazione) IS NOT NULL THEN true" +
            "           ELSE false" +
            "       END                                 AS \"dettagli\"" +
            "   ,   MM.obiettivopiao                    AS \"obiettivo\"" +
            "   ,   MM.data_ultima_modifica             AS \"dataMonitoraggio\"" +
            "   FROM misura MS" +
            "       INNER JOIN rilevazione S ON MS.id_rilevazione = S.id" +
            "       LEFT JOIN misura_rischio_processo_at MRPAT ON (MRPAT.cod_misura = MS.codice AND MRPAT.id_rilevazione = MS.id_rilevazione)" +
            "       LEFT JOIN misuramonitoraggio MM ON MM.codice = MS.codice AND MM.id_rilevazione = MS.id_rilevazione" +
            "   WHERE MS.id_rilevazione = ?" +
            "       AND (MS.codice = ? OR -1 = ?)" +
            "   GROUP BY (MS.codice, MS.nome, MS.descrizione, MS.onerosa, MS.ordinale, MS.data_ultima_modifica, MS.ora_ultima_modifica, MS.id_rilevazione, MM.obiettivopiao, MM.data_ultima_modifica)" +
            "   ORDER BY MS.nome";
    
    /**
     * <p>Estrae gli estremi delle strutture di una misura.</p>
     */
    public static final String GET_STRUCTS_BY_MEASURE = 
            "SELECT DISTINCT" +
            "       MST.id_struttura_liv1               AS \"cod1\"" +
            "   ,   MST.id_struttura_liv2               AS \"cod2\"" +
            "   ,   MST.id_struttura_liv3               AS \"cod3\"" +
            "   ,   MST.id_struttura_liv4               AS \"cod4\"" +
            "   ,   MST.id_rilevazione                  AS \"value3\"" +
            "   ,   MST.ruolo                           AS \"extraInfo\"" +
            "   ,   L1.prefisso                         AS \"nome\"" +
            "   ,   L2.prefisso                         AS \"nomeReale\"" +
            "   ,   L3.prefisso                         AS \"codice\"" +
            "   ,   L4.prefisso                         AS \"labelWeb\"" +
            "   ,   L1.nome                             AS \"extraInfo1\"" +
            "   ,   L2.nome                             AS \"extraInfo2\"" +
            "   ,   L3.nome                             AS \"extraInfo3\"" +
            "   ,   L4.nome                             AS \"extraInfo4\"" +
            "   FROM misura_struttura MST" +
            "       LEFT JOIN struttura_liv1 L1 ON MST.id_struttura_liv1 = L1.id" +
            "       LEFT JOIN struttura_liv2 L2 ON MST.id_struttura_liv2 = L2.id" +
            "       LEFT JOIN struttura_liv3 L3 ON MST.id_struttura_liv3 = L3.id" +
            "       LEFT JOIN struttura_liv4 L4 ON MST.id_struttura_liv4 = L4.id" +
            "   WHERE   MST.cod_misura = ?" +
            "       AND MST.ruolo ILIKE ?" + 
            "       AND MST.id_rilevazione = ?" +
            "   ORDER BY MST.ruolo " +
            "   ,       MST.id_struttura_liv1" +
            "   ,       MST.id_struttura_liv2" +
            "   ,       MST.id_struttura_liv3" +
            "   ,       MST.id_struttura_liv4";
        
    /**
     * <p>Estrae i rischi corruttivi a cui &egrave; stata applicata 
     * una specifica misura di prevenzione, il cui codice viene 
     * passato come parametro, nel contesto di una specifica rilevazione, 
     * il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_RISKS_AND_PROCESS_BY_MEASURE = 
            "SELECT DISTINCT" +
            "       RC.id                               AS \"id\"" +
            "   ,   RC.nome                             AS \"nome\"" +
            "   ,   PAT.id                              AS \"cod1\"" +
            "   ,   PAT.nome                            AS \"extraInfo1\"" +
            "   FROM rischio_corruttivo RC" +
            "       INNER JOIN misura_rischio_processo_at MRPAT ON MRPAT.id_rischio_corruttivo = RC.id" +
            "       INNER JOIN processo_at PAT ON MRPAT.id_processo_at = PAT.id" +
            "   WHERE MRPAT.cod_misura = ?" +
            "       AND MRPAT.id_rilevazione = ?" +
            "   ORDER BY RC.nome";
    
    /**
     * <p>Seleziona l'elenco di tutte le misure di mitigazione trovate per una 
     * data rilevazione cui corrispondano anche i dettagli inseriti 
     * ai fini del monitoraggio; in base ai valori passati come parametri,
     * pu&ograve; anche selezionare una specifica misura.</p>
     * <p>Ogni riga, quindi, corrisponder&agrave; ad una distinta 
     * misura monitorata.</p>
     */
    public static final String GET_MEASURES_MONITORED = 
            "SELECT DISTINCT" +
            "       MS.codice                           AS \"codice\"" +
            "   ,   MS.nome                             AS \"nome\"" +
            "   ,   MS.descrizione                      AS \"informativa\"" +
            "   ,   MS.onerosa                          AS \"onerosa\"" +
            "   ,   MS.ordinale                         AS \"ordinale\"" +
            "   ,   MM.obiettivopiao                    AS \"obiettivo\"" +
            "   ,   MM.data_ultima_modifica             AS \"dataUltimaModifica\"" +
            "   ,   MM.ora_ultima_modifica              AS \"oraUltimaModifica\"" +
            "   ,   MM.id_rilevazione                   AS \"idRilevazione\"" +
            "   FROM misura MS" +
            "   ,    misuramonitoraggio MM" +
            "       INNER JOIN rilevazione S ON MS.id_rilevazione = S.id" +
            "       LEFT JOIN misura_rischio_processo_at MRPAT ON (MRPAT.cod_misura = MS.codice AND MRPAT.id_rilevazione = MS.id_rilevazione)" +
            "   WHERE (MS.codice = MM.codice AND MS.id_rilevazione = MM.id_rilevazione) " +
            "       AND MS.id_rilevazione = ?" +
            "       AND (MS.codice = ? OR -1 = ?)" +
            "   ORDER BY MS.nome";
    
    /**
     * <p>Estrae gli estremi delle strutture:<ol>
     * <li>aventi nei confronti di una misura uno specifico ruolo 
     * ricevuto come parametro;</li> 
     * <li>che hanno almeno una misura collegata;</li>
     * <li>calcolando anche quante misure sono effettivamente collegate 
     * a ciascuna struttura selezionata.</li></ol></p>
     */
    public static final String GET_STRUCTS_BY_ROLE = 
            "   SELECT" +
            "       S4.id                               AS \"id\"" +
            "   ,   4                                   AS \"livello\"" +
            "   ,   S4.prefisso                         AS \"prefisso\"" +  
            "   ,   S4.nome                             AS \"nome\"" +
            "   ,   count(MST.cod_misura)::SMALLINT     AS \"fte\"" +
            "   FROM struttura_liv4 S4" +
            "       INNER JOIN misura_struttura MST ON S4.id = MST.id_struttura_liv4" +
            "   WHERE   MST.ruolo ILIKE ?" +
            "       AND MST.id_rilevazione = ?" +
            "   GROUP BY (S4.id, S4.prefisso, S4.nome)" +
            "UNION" +
            "   SELECT" +
            "       S3.id                               AS \"id\"" +
            "   ,   3                                   AS \"livello\"" +
            "   ,   S3.prefisso                         AS \"prefisso\"" +  
            "   ,   S3.nome                             AS \"nome\"" +
            "   ,   count(MST.cod_misura)::SMALLINT     AS \"fte\"" +
            "   FROM struttura_liv3 S3" +
            "       INNER JOIN misura_struttura MST ON S3.id = MST.id_struttura_liv3" +
            "   WHERE   MST.ruolo ILIKE ?" +
            "       AND MST.id_struttura_liv4 IS NULL" +
            "       AND MST.id_rilevazione = ?" +
            "   GROUP BY (S3.id, S3.prefisso, S3.nome)" +
            "UNION" +
            "   SELECT" +
            "       S2.id                               AS \"id\"" +
            "   ,   2                                   AS \"livello\"" +
            "   ,   S2.prefisso                         AS \"prefisso\"" +  
            "   ,   S2.nome                             AS \"nome\"" +
            "   ,   count(MST.cod_misura)::SMALLINT     AS \"fte\"" +
            "   FROM struttura_liv2 S2" +
            "       INNER JOIN misura_struttura MST ON S2.id = MST.id_struttura_liv2" +
            "   WHERE   MST.ruolo ILIKE ?" +
            "       AND MST.id_struttura_liv3 IS NULL" +
            "       AND MST.id_struttura_liv4 IS NULL" +
            "       AND MST.id_rilevazione = ?" +
            "   GROUP BY (S2.id, S2.prefisso, S2.nome)" +
            "   ORDER BY livello, prefisso, id";
    
    /**
     * <p>Relativamente a una misura di dato codice seleziona:<ul>
     * <li>l'elenco di tutte le sue fasi di attuazione</li> 
     * <li>oppure, entro questo insieme, una specifica misura di dato id.</li>
     * </ul>Quindi:<dl>
     * <dt>se si vuol ottenere l'elenco completo delle fasi di una misura:</dt>
     * <dd>passare un valore qualunque sul 2° parametro (p.es. -1) e -1 sul 3° parametro</dd>
     * <dt>se si vuol ottenere una specifica fase della misura:</dt>
     * <dd>passare l'id della misura sul 2° e sul 3° parametro.</dd></dl></p>
     */
    public static final String GET_MEASURE_ACTIVITIES = 
            "SELECT DISTINCT" +
            "       FA.id                               AS \"id\"" +
            "   ,   FA.nome                             AS \"nome\"" +
            "   ,   FA.cod_misura                       AS \"codice\"" +
            "   ,   FA.ordinale                         AS \"ordinale\"" +
            "   ,   FA.data_ultima_modifica             AS \"dataUltimaModifica\"" +
            "   ,   FA.ora_ultima_modifica              AS \"oraUltimaModifica\"" +
            "   ,   FA.id_rilevazione                   AS \"idRilevazione\"" +
            "   FROM fase FA" +
            "       INNER JOIN misuramonitoraggio MM ON (FA.cod_misura = MM.codice AND FA.id_rilevazione = MM.id_rilevazione)" +
            "       INNER JOIN misura MS ON (MM.codice = MS.codice AND MM.id_rilevazione = MS.id_rilevazione)" +
            "       INNER JOIN rilevazione S ON MS.id_rilevazione = S.id" +
            "   WHERE (FA.cod_misura = ?) " +
            "       AND (FA.id = ? OR -1 = ?)" +
            "       AND FA.id_rilevazione = ?" +
            "   ORDER BY FA.id";
    
    /**
     * <p>Estrae gli estremi dei collegamenti di un indicatore 
     * associato a una fase di attuazione di dato id.</p>
     */
    public static final String GET_INDICATOR_BY_ACTIVITY_RAW = 
            "SELECT " +
            "       IND.id                              AS \"cod1\"" +
            "   ,   IND.id_fase                         AS \"cod2\"" +
            "   ,   IND.id_tipo                         AS \"cod3\"" +
            "   ,   IND.id_stato                        AS \"cod4\"" +
            "   FROM indicatoremonitoraggio IND" +
            "   WHERE IND.id_fase = ?";
    
    /**
     * <p>Estrae i valori di un indicatore associato a una fase di attuazione
     * di dato id.</p>
     */
    public static final String GET_INDICATOR_BY_ACTIVITY = 
            "SELECT DISTINCT" +
            "       IND.id                              AS \"id\"" +
            "   ,   IND.nome                            AS \"nome\"" +
            "   ,   IND.descrizione                     AS \"descrizione\"" +
            "   ,   IND.baseline                        AS \"baseline\"" +
            "   ,   IND.databaseline                    AS \"dataBaseline\"" +
            "   ,   IND.target                          AS \"target\"" +
            "   ,   IND.datatarget                      AS \"dataTarget\"" +
            "   ,   IND.data_ultima_modifica            AS \"dataUltimaModifica\"" +
            "   ,   IND.ora_ultima_modifica             AS \"oraUltimaModifica\"" +
            "   ,   IND.id_usr_ultima_modifica          AS \"autoreUltimaModifica\"" +
            "   FROM indicatoremonitoraggio IND" +
            "   WHERE IND.id_fase = ?" +
            "       AND IND.id_rilevazione = ?";        // pleonastica
    
    /**
     * <p><ul><li>Seleziona l'elenco di tutte le misurazioni collegate a un 
     * indicatore di dato id, nel contesto di una rilevazione di dato id<br>
     * OPPURE</li>
     * <li>seleziona una specifica misurazione collegata agli specifici
     * parametri di cui sopra.</li></ul></p>
     */
    public static final String GET_MEASUREMENTS_BY_INDICATOR = 
            "SELECT DISTINCT" +
            "       MZ.id                               AS \"id\"" +
            "   ,   MZ.valore                           AS \"valore\"" +
            "   ,   MZ.azioni                           AS \"informativa\"" +
            "   ,   MZ.motivazioni                      AS \"descrizione\"" +
            "   ,   MZ.ultimo                           AS \"ultimo\"" +
            "   ,   MZ.data_ultima_modifica             AS \"dataUltimaModifica\"" +
            "   ,   MZ.ora_ultima_modifica              AS \"oraUltimaModifica\"" +
            "   FROM misurazione MZ" +
            "       INNER JOIN rilevazione S ON MZ.id_rilevazione = S.id" +
            "   WHERE (MZ.id_indicatoremonitoraggio = ?) " +
            "       AND MZ.id_rilevazione = ?" +
            "       AND (MZ.id = ? OR -1 = ?)" +
            "   ORDER BY MZ.data_ultima_modifica";
    
    /**
     * <p>Seleziona l'elenco di tutte le misurazioni collegate a una 
     * misurazione di dato codice, nel contesto di una rilevazione di dato id</p>
     */
    public static final String GET_MEASUREMENTS_BY_MEASURE = 
            "SELECT DISTINCT" +
            "       MZ.id                               AS \"id\"" +
            "   ,   MZ.valore                           AS \"valore\"" +
            "   ,   MZ.azioni                           AS \"informativa\"" +
            "   ,   MZ.motivazioni                      AS \"descrizione\"" +
            "   ,   MZ.ultimo                           AS \"ultimo\"" +
            "   ,   MZ.data_ultima_modifica             AS \"dataUltimaModifica\"" +
            "   ,   MZ.ora_ultima_modifica              AS \"oraUltimaModifica\"" +
            "   ,   MZ.id_rilevazione                   AS \"idRilevazione\"" +
            "   FROM misurazione MZ" +
            "       INNER JOIN rilevazione S ON MZ.id_rilevazione = S.id" +
            "   WHERE (MZ.id_indicatoremonitoraggio = ?) " +
            "       AND MZ.id_rilevazione = ?" +
            "       AND (MZ.id = ? OR -1 = ?)" +
            "   ORDER BY MZ.data_ultima_modifica";
    
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
     * <p>In funzione del parametro specificante il livello
     * (1 = macroprocesso_at | 2 = processo_at | 3 = sottoprocesso_at),
     * costruisce dinamicamente la query che estrae uno specifico, 
     * rispettivamente, macroprocesso, processo o sottoprocesso.</p>
     * 
     * @param idP       identificativo di macroprocesso_at, processo_at o sottoprocesso_at
     * @param level     identificativo del livello a cui e' relativo l'id (1 = macroprocesso_at | 2 = processo_at | 3 = sottoprocesso_at)
     * @param idSur     codice identificativo della rilevazione
     * @return <code>String</code> - la query che seleziona l'elemento desiderato
     */
    public String getQueryMacroSubProcessAtById(int idP, byte level, int idSur);
    
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
    
    /**
     * <p>Seleziona le misure che, tramite la loro tipologia, sono collegate 
     * a un fattore abilitante, il cui identificativo viene passato
     * come parametro, escludendo alcune misure i cui codici, facoltativamente,
     * vengono passati come parametro.</p>
     * 
     * @param idF   identificativo del fattore abilitante
     * @param idS   identificativo della rilevazione
     * @param codeM codici delle misure cercate
     * @return <code>String</code> - la query che seleziona le misure cercate
     */
    public String getMeasuresByFactors(int idF, int idS, String codeM);
    
    /**
     * <p>Seleziona l'elenco di tutte le misure di mitigazione trovate per un dato
     * rischio, nel contesto di un dato processo, e in riferimento a una data
     * rilevazione, i cui identificativi vengono passati come parametri
     * (relazione 4-aria).</p>
     * <p>Ogni riga, quindi, corrisponder&agrave; ad una distinta misura.</p>
     * 
     * @param idR   identificativo del rischio corruttivo
     * @param idP   identificativo processo anticorruzione
     * @param idS   identificativo della rilevazione ("survey")
     * @param codeM codici delle misure
     * @param getAll valore convenzionale che permette di ottenere tutte le misure collegate al rischio e al processo considerati
     * @return <code>String</code> - la query che seleziona le misure cercate
     */
    public String getMeasureByRiskAndProcess(String idR, String idP, String idS, String codeM, int getAll);

    /**
     * <p>Seleziona le misure di mitigazione <strong>direttamente</strong>
     * collegate a una struttura di dato livello, recuperando anche attributi
     * esclusivi dei dettagli inseriti ai fini del monitoraggio e riferimenti
     * alla struttura collegata alla misura stessa.
     * Basa la selezione principalmente sull'identificativo della struttura
     * e sul suo ruolo rispetto alla misura.</p> 
     * 
     * @param idR   identificativo della rilevazione
     * @param idS   identificativo della struttura
     * @param level livello della struttura
     * @param role  ruolo della struttura rispetto alla misura
     * @return <code>String</code> - la query che seleziona le misure cercate
     */
    public String getMeasuresByStruct(int idR, int idS, byte level, String role);
    
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
    
    /**
     * <p>Query per inserimento di una misura di mitigazione del rischio.</p>
     */
    public static final String INSERT_MEASURE =
            "INSERT INTO misura" +
            "   (   codice" +
            "   ,   nome" +
            "   ,   descrizione" +
            "   ,   onerosa" +
            "   ,   ordinale" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   ,   id_rilevazione" +
            "   )" +
            "   VALUES (? " +       // codice
            "   ,       ? " +       // nome
            "   ,       ? " +       // descrizione
            "   ,       ? " +       // onerosa
            "   ,       ? " +       // ordinale
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "   ,       ? " +       // id rilevazione
            "          )" ;
    
    /**
     * <p>Query per inserimento di una tupla nella relazione tra misura e carattere.</p>
     */
    public static final String INSERT_MEASURE_CHARACTER =
            "INSERT INTO misura_carattere" +
            "   (   cod_misura" +
            "   ,   cod_carattere" +
            "   ,   id_rilevazione" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   )" +
            "   VALUES (? " +       // codice misura
            "   ,       ? " +       // codice carattere
            "   ,       ? " +       // id rilevazione
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "          )" ;
    
    /**
     * <p>Query per inserimento di una tupla nella relazione 
     * tra misura e tipo di misura.</p>
     */
    public static final String INSERT_MEASURE_TYPE =
            "INSERT INTO misura_tipologia" +
            "   (   cod_misura" +
            "   ,   id_tipo_misura" +
            "   ,   id_rilevazione" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   )" +
            "   VALUES (? " +       // codice misura
            "   ,       ? " +       // id tipo misura
            "   ,       ? " +       // id rilevazione
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "          )" ;
    
    /**
     * <p>Query per inserimento di una struttura collegata a una misura.</p>
     */
    public static final String INSERT_MEASURE_STRUCT =
            "INSERT INTO misura_struttura" +
            //"   (   id" +
            "   (   ruolo" +
            "   ,   id_struttura_liv1" +
            "   ,   id_struttura_liv2" +
            "   ,   id_struttura_liv3" +
            "   ,   id_struttura_liv4" +
            "   ,   cod_misura" +
            "   ,   id_rilevazione" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   )" +
            //"   VALUES (? " +       // id
            "   VALUES (? " +       // ruolo
            "   ,       ? " +       // id_struttura_liv1
            "   ,       ? " +       // id_struttura_liv2
            "   ,       ? " +       // id_struttura_liv3
            "   ,       ? " +       // id_struttura_liv4
            "   ,       ? " +       // codice misura
            "   ,       ? " +       // id rilevazione
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "          )" ;
    
    /**
     * <p>Query per inserimento di un'associazione tra:<ul>
     * <li>un rischio corruttivo</li>
     * <li>una misura di prevenzione</li>
     * <li>un processo censito dall'anticorruzione</li>
     * <li>nel contesto di una data rilevazione</li>
     * </ul>
     * (entità debole associativa 4-aria PxRxMxS).</p>
     */
    public static final String INSERT_MEASURE_RISK_PROCESS =
            "INSERT INTO misura_rischio_processo_at" +
            "   (   cod_misura" +
            "   ,   id_rischio_corruttivo" +
            "   ,   id_processo_at" +
            "   ,   id_rilevazione" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   )" +
            "   VALUES (? " +       // cod_misura
            "   ,       ? " +       // id_rischio_corruttivo
            "   ,       ? " +       // id_processo_at
            "   ,       ? " +       // id_rilevazione
            "   ,       ? " +       // data_ultima_modifica
            "   ,       ? " +       // ora_ultima_modifica
            "   ,       ? " +       // id_usr_ultima_modifica
            "          )" ;
    
    /**
     * <p>Query per inserimento dei dettagli di una misura di mitigazione.</p>
     */
    public static final String INSERT_MEASURE_DETAILS =
            "INSERT INTO misuramonitoraggio" +
            "   (   codice" +
            "   ,   id_rilevazione" +                   
            "   ,   obiettivopiao" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   )" +
            "   VALUES (? " +       // codice
            "   ,       ? " +       // id rilevazione
            "   ,       ? " +       // obiettivopiao
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "          )" ;
    
    /**
     * <p>Query per inserimento di una fase di attuazione di una misura di mitigazione.</p>
     */
    public static final String INSERT_MEASURE_ACTIVITY =
            "INSERT INTO fase" +
            "   (   nome" +
            "   ,   ordinale" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +            
            "   ,   cod_misura" +
            "   ,   id_rilevazione" +
            "   )" +
            "   VALUES (? " +       // nome
            "   ,       ? " +       // ordinale
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "   ,       ? " +       // cod_misura
            "   ,       ? " +       // id_rilevazione
            "          )" ;
    
    /**
     * <p>Query per inserimento di un indicatore di monitoraggio.</p>
     */
    public static final String INSERT_MEASURE_INDICATOR =
            "INSERT INTO indicatoremonitoraggio" +
            "   (   nome" +
            "   ,   descrizione" +
            "   ,   baseline" +
            "   ,   databaseline" +
            "   ,   target" +
            "   ,   datatarget" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   ,   id_fase" +
            "   ,   id_tipo" +
            "   ,   id_stato" +
            "   ,   id_rilevazione" +
            "   )" +
            "   VALUES (? " +       // nome
            "   ,       ? " +       // descrizione
            "   ,       ? " +       // baseline
            "   ,       ? " +       // databaseline
            "   ,       ? " +       // target
            "   ,       ? " +       // datatarget         
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "   ,       ? " +       // id fase
            "   ,       ? " +       // id tipo
            "   ,       ? " +       // id stato
            "   ,       ? " +       // id rilevazione
            "          )" ;
    
    /**
     * <p>Query per inserimento di una misurazione di un indicatore di monitoraggio.</p>
     */
    public static final String INSERT_MEASUREMENT =
            "INSERT INTO misurazione" +
            "   (   valore" +
            "   ,   azioni" +
            "   ,   motivazioni" +
            "   ,   ultimo" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   ,   id_indicatoremonitoraggio" +
            "   ,   id_rilevazione" +
            "   )" +
            "   VALUES (? " +       // valore
            "   ,       ? " +       // azioni
            "   ,       ? " +       // motivazioni
            "   ,       ? " +       // ultimo
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "   ,       ? " +       // id indicatore
            "   ,       ? " +       // id rilevazione
            "          )" ;
    
    /**
     * <p>Query per inserimento di un macroprocesso censito a fini
     * di mappatura, valutazione, gestione e monitoraggio del rischio corruttivo.
     * Deve gestire manualmente l'identificativo a causa del flusso di 
     * navigazione (serve subito a valle dell'INSERT) per cui ha pi&uacute;
     * senso gestirlo a mano piuttosto che fare una INSERT e subito dopo
     * una SELECT (sono due accessi al disco anzich&eacute; uno).</p>
     */
    public static final String INSERT_MACRO_AT =
            "INSERT INTO macroprocesso_at" +
            "   (   id" +
            "   ,   codice" +
            "   ,   nome" +           
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   ,   id_area_rischio" +
            "   ,   id_rilevazione" +
            "   )" +
            "   VALUES (? " +       // id
            "   ,       ? " +       // codice
            "   ,       ? " +       // nome     
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "   ,       ? " +       // id_area_rischio
            "   ,       ? " +       // id_rilevazione
            "          )" ;
    
    /**
     * <p>Query per inserimento di un processo censito a fini
     * di mappatura, valutazione, gestione e monitoraggio del rischio corruttivo.
     * Deve gestire manualmente l'identificativo a causa del flusso di 
     * navigazione (serve subito a valle dell'INSERT) per cui ha pi&uacute;
     * senso gestirlo a mano piuttosto che fare una INSERT e subito dopo
     * una SELECT (sono due accessi al disco anzich&eacute; uno).</p>
     */
    public static final String INSERT_PROCESS_AT =
            "INSERT INTO processo_at" +
            "   (   id" +
            "   ,   codice" +
            "   ,   nome" +           
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica " +
            "   ,   id_usr_ultima_modifica" +
            "   ,   id_macroprocesso_at" +
            "   ,   id_rilevazione" +
            "   )" +
            "   VALUES (? " +       // id
            "   ,       ? " +       // codice
            "   ,       ? " +       // nome     
            "   ,       ? " +       // data ultima modifica
            "   ,       ? " +       // ora ultima modifica
            "   ,       ? " +       // autore ultima modifica
            "   ,       ? " +       // id_macroprocesso
            "   ,       ? " +       // id_rilevazione
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
