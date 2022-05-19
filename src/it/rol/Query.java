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

package it.rol;

import java.io.Serializable;
import java.util.HashMap;


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
     *                          1. Query comuni                               *
     * ********************************************************************** */
    /**
     * <p>Estrae le classi Command previste per la/le applicazione/i.</p>
     */
    public static final String LOOKUP_COMMAND =
            "SELECT " +
            "       id          AS \"id\"" +
            "   ,   nome        AS \"nomeReale\"" +
            "   ,   nome        AS \"nomeClasse\"" +
            "   ,   token       AS \"nome\"" +
            "   ,   labelweb    AS \"labelWeb\"" +
            "   ,   jsp         AS \"paginaJsp\"" +
            "   ,   informativa AS \"informativa\"" +
            "  FROM command";

    /**
     * <p>Estrae l'id massimo da una tabella definita nel chiamante</p>
     */
    public static final String SELECT_MAX_ID =
            "SELECT " +
            "       MAX(id)                     AS \"max\"" +
            "   FROM ";

    /**
     * <p>Estrae l'id minimo da una tabella definita nel chiamante</p>
     */
    public static final String SELECT_MIN_ID =
            "SELECT " +
            "       MIN(id)                     AS \"min\"" +
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
            "       RA.id           AS \"id\"" +
            "   ,   RA.nome         AS \"nome\"" +
            "   FROM ruolo_applicativo RA " +
            "       INNER JOIN usr U on RA.id = U.id_ruolo" +
            "   WHERE U.login = ?";

    /**
     * <p>Estrae identificativo tupla ultimo accesso, se esiste
     * per l'utente il cui username viene passato come parametro.</p>
     */
    public static final String GET_ACCESSLOG_BY_LOGIN =
            "SELECT " +
            "       A.id                    AS  \"id\"" +
            "   FROM access_log A " +
            "   WHERE A.login = ? ";

    /**
     * <p>Estrae la password criptata e il seme dell'utente,
     * identificato tramite username, passato come parametro.</p>
     */
    public static final String GET_ENCRYPTEDPASSWORD =
            "SELECT " +
            "       U.passwdform    AS \"nome\"" +
            "   ,   U.salt          AS \"informativa\"" +
            "   FROM usr U" +
            "   WHERE U.login = ?";

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
            "       R.id        AS \"id\"" +
            "   ,   R.codice    AS \"nome\"" +
            "   ,   R.nome      AS \"informativa\"" +
            "   ,   R.ordinale  AS \"ordinale\"" +
            "   FROM rilevazione R" +
            "   WHERE (R.id = ? OR -1 = ?)" +
            "       AND R.chiusa = true" +
            "   ORDER BY data_rilevazione DESC";
    
    /* ********************************************************************** *
     *           2. Query di selezione PMS (Process Mapping Software)         *
     * ********************************************************************** */    
    /**
     * <p>Estrae tutti i macroprocessi filtrati in base all'identificativo della rilevazione,
     * passato come parametro.</p>
     */
    public static final String GET_MACRO_BY_SURVEY =
            "SELECT DISTINCT" +
            "       M.id                AS \"id\"" +
            "   ,   M.codice            AS \"codice\"" +
            "   ,   M.nome              AS \"nome\"" +
            "   ,   M.descrizione       AS \"descrizione\"" +
            "   ,   M.ordinale          AS \"ordinale\"" +
            "   ,   M.id_rilevazione    AS \"idAppo\"" +
            "   ,   ROUND(100 * SUM(AM.quotaparte) OVER (PARTITION BY M.id) / (SUM(AM.quotaparte) OVER ())::numeric, 2) AS \"quotaParte\"" +
            "   ,   ROUND((SUM(AM.quotaparte * (A.perc_parttime / 100.0)) OVER (PARTITION BY M.id) / 100.0), 2)         AS \"fte\"" +
            "   FROM macroprocesso M" +
            "       INNER JOIN rilevazione R ON M.id_rilevazione = R.id" +
            "       INNER JOIN allocazione_macroprocesso AM ON AM.id_macroprocesso = M.id" +
            "       INNER JOIN afferenza A ON R.id = A.id_rilevazione AND A.id_persona = AM.id_persona" +
            "   WHERE R.codice ILIKE ?" +
            "   ORDER BY M.codice";

    /**
     * <p>Estrae tutti i macroprocessi collegati a una struttura di I livello e dato id,
     * filtrati in base all'identificativo della rilevazione, passato come parametro.</p>
     */
    public static final String GET_MACROPROCESSI_BY_STRUCT_L1 =
            "SELECT DISTINCT" +
            "       M.id                AS \"id\"" +
            "   ,   M.codice            AS \"codice\"" +
            "   ,   M.nome              AS \"nome\"" +
            "   ,   M.descrizione       AS \"descrizione\"" +
            "   ,   M.ordinale          AS \"ordinale\"" +
            "   ,   M.id_rilevazione    AS \"idAppo\"" +
            "   ,   ROUND(100 * SUM(AM.quotaparte) OVER (PARTITION BY M.id) / (SUM(AM.quotaparte) OVER ())::numeric, 2) AS \"quotaParte\"" +
            "   ,   ROUND((SUM(AM.quotaparte * (A.perc_parttime / 100.0)) OVER (PARTITION BY M.id) / 100.0), 2)         AS \"fte\"" +
            "   FROM macroprocesso M" +
            "       INNER JOIN rilevazione R ON M.id_rilevazione = R.id" +
            "       INNER JOIN allocazione_macroprocesso AM ON AM.id_macroprocesso = M.id" +
            "       INNER JOIN struttura_liv1 L1 ON L1.id_rilevazione = R.id" +
            "       INNER JOIN afferenza A ON R.id = A.id_rilevazione AND A.id_persona = AM.id_persona" +
            "   WHERE R.codice ILIKE ?" +
            "       AND L1.id = ?" +
            "       AND AM.id_persona IN" +
            "           (SELECT P.id FROM persona P" +
            "               INNER JOIN afferenza AF ON AF.id_persona = P.id" +
            "               WHERE " +
            "               (AF.id_struttura_liv1 = ? " +
            "                   OR AF.id_struttura_liv2 IN " +
            "                       (SELECT L2.id FROM struttura_liv2 L2 WHERE L2.id_struttura_liv1 = ?)" +
            "                   OR AF.id_struttura_liv3 IN " +
            "                       (SELECT L3.id FROM struttura_liv3 L3 WHERE L3.id_struttura_liv2 IN" +
            "                           (SELECT L2.id FROM struttura_liv2 L2 WHERE L2.id_struttura_liv1 = ?)" +
            "                       )" +
            "                   OR AF.id_struttura_liv4 IN " +
            "                       (SELECT L4.id FROM struttura_liv4 L4 WHERE L4.id_struttura_liv3 IN" +
            "                           (SELECT L3.id FROM struttura_liv3 L3 WHERE L3.id_struttura_liv2 IN" +
            "                               (SELECT L2.id FROM struttura_liv2 L2 WHERE L2.id_struttura_liv1 = ?)" +
            "                           )" +
            "                       )" +
            "               )" +
            "           )" +
            "   ORDER BY M.codice";

    /**
     * <p>Estrae tutti i macroprocessi collegati a una struttura di II livello e dato id,
     * filtrati in base all'identificativo della rilevazione, passato come parametro.</p>
     */
    public static final String GET_MACROPROCESSI_BY_STRUCT_L2 =
            "SELECT DISTINCT" +
            "       M.id                AS \"id\"" +
            "   ,   M.codice            AS \"codice\"" +
            "   ,   M.nome              AS \"nome\"" +
            "   ,   M.descrizione       AS \"descrizione\"" +
            "   ,   M.ordinale          AS \"ordinale\"" +
            "   ,   M.id_rilevazione    AS \"idAppo\"" +
            "   ,   ROUND(100 * SUM(AM.quotaparte) OVER (PARTITION BY M.id) / (SUM(AM.quotaparte) OVER ())::numeric, 2) AS \"quotaParte\"" +
            "   ,   ROUND((SUM(AM.quotaparte * (A.perc_parttime / 100.0)) OVER (PARTITION BY M.id) / 100.0), 2)         AS \"fte\"" +
            "   FROM macroprocesso M" +
            "       INNER JOIN rilevazione R ON M.id_rilevazione = R.id" +
            "       INNER JOIN allocazione_macroprocesso AM ON AM.id_macroprocesso = M.id" +
            "       INNER JOIN struttura_liv2 L2 ON L2.id_rilevazione = R.id" +
            "       INNER JOIN afferenza A ON R.id = A.id_rilevazione AND A.id_persona = AM.id_persona" +
            "   WHERE R.codice ILIKE ?" +
            "       AND L2.id = ?" +
            "       AND AM.id_persona IN" +
            "           (SELECT P.id FROM persona P" +
            "               INNER JOIN afferenza AF ON AF.id_persona = P.id" +
            "               WHERE " +
            "               (AF.id_struttura_liv2 = ? " +
            "                   OR AF.id_struttura_liv3 IN " +
            "                       (SELECT L3.id FROM struttura_liv3 L3 WHERE L3.id_struttura_liv2 = ?)" +
            "                   OR AF.id_struttura_liv4 IN " +
            "                       (SELECT L4.id FROM struttura_liv4 L4 WHERE L4.id_struttura_liv3 IN" +
            "                           (SELECT L3.id FROM struttura_liv3 L3 WHERE L3.id_struttura_liv2 = ?)" +
            "                       )" +
            "               )" +
            "           )" +
            "   ORDER BY M.codice";

    /**
     * <p>Estrae tutti i macroprocessi collegati a una struttura di III livello e dato id,
     * filtrati in base all'identificativo della rilevazione, passato come parametro.</p>
     */
    public static final String GET_MACROPROCESSI_BY_STRUCT_L3 =
            "SELECT DISTINCT" +
            "       M.id                AS \"id\"" +
            "   ,   M.codice            AS \"codice\"" +
            "   ,   M.nome              AS \"nome\"" +
            "   ,   M.descrizione       AS \"descrizione\"" +
            "   ,   M.ordinale          AS \"ordinale\"" +
            "   ,   M.id_rilevazione    AS \"idAppo\"" +
            "   ,   ROUND(100 * SUM(AM.quotaparte) OVER (PARTITION BY M.id) / (SUM(AM.quotaparte) OVER ())::numeric, 2) AS \"quotaParte\"" +
            "   ,   ROUND((SUM(AM.quotaparte * (A.perc_parttime / 100.0)) OVER (PARTITION BY M.id) / 100.0), 2)         AS \"fte\"" +
            "   FROM macroprocesso M" +
            "       INNER JOIN rilevazione R ON M.id_rilevazione = R.id" +
            "       INNER JOIN allocazione_macroprocesso AM ON AM.id_macroprocesso = M.id" +
            "       INNER JOIN struttura_liv3 L3 ON L3.id_rilevazione = R.id" +
            "       INNER JOIN afferenza A ON R.id = A.id_rilevazione AND A.id_persona = AM.id_persona" +
            "   WHERE R.codice ILIKE ?" +
            "       AND L3.id = ?" +
            "       AND AM.id_persona IN" +
            "           (SELECT P.id FROM persona P" +
            "               INNER JOIN afferenza AF ON AF.id_persona = P.id" +
            "               WHERE" +
            "               (AF.id_struttura_liv3 = ? " +
            "                   OR AF.id_struttura_liv4 IN " +
            "                       (SELECT L4.id FROM struttura_liv4 L4 WHERE L4.id_struttura_liv3 = ?)" +
            "               )" +
            "           )" +
            "   ORDER BY M.codice";

    /**
     * <p>Estrae tutti i macroprocessi collegati a una struttura di IV livello e dato id,
     * filtrati in base all'identificativo della rilevazione, passato come parametro.</p>
     */
    public static final String GET_MACROPROCESSI_BY_STRUCT_L4 =
            "SELECT DISTINCT" +
            "       M.id                AS \"id\"" +
            "   ,   M.codice            AS \"codice\"" +
            "   ,   M.nome              AS \"nome\"" +
            "   ,   M.descrizione       AS \"descrizione\"" +
            "   ,   M.ordinale          AS \"ordinale\"" +
            "   ,   M.id_rilevazione    AS \"idAppo\"" +
            "   ,   ROUND(100 * SUM(AM.quotaparte) OVER (PARTITION BY M.id) / (SUM(AM.quotaparte) OVER ())::numeric, 2) AS \"quotaParte\"" +
            "   ,   ROUND((SUM(AM.quotaparte * (A.perc_parttime / 100.0)) OVER (PARTITION BY M.id) / 100.0), 2)         AS \"fte\"" +
            "   FROM macroprocesso M" +
            "       INNER JOIN rilevazione R ON M.id_rilevazione = R.id" +
            "       INNER JOIN allocazione_macroprocesso AM ON AM.id_macroprocesso = M.id" +
            "       INNER JOIN struttura_liv4 L4 ON L4.id_rilevazione = R.id" +
            "       INNER JOIN afferenza A ON R.id = A.id_rilevazione AND A.id_persona = AM.id_persona" +
            "   WHERE R.codice ILIKE ?" +
            "       AND L4.id = ?" +
            "       AND AM.id_persona IN" +
            "           (SELECT P.id FROM persona P" +
            "               INNER JOIN afferenza AF ON AF.id_persona = P.id" +
            "               WHERE AF.id_struttura_liv4 = ?)" +
            "   ORDER BY M.codice";

    /**
     * <p>Estrae tutti i macroprocessi allocati su una struttura persona dato id,
     * filtrati in base all'identificativo della rilevazione, passato come parametro.</p>
     */
    public static final String GET_MACRO_BY_PERSON =
            "SELECT DISTINCT" +
            "       M.id                AS \"id\"" +
            "   ,   M.codice            AS \"codice\"" +
            "   ,   M.nome              AS \"nome\"" +
            "   ,   M.descrizione       AS \"descrizione\"" +
            "   ,   M.ordinale          AS \"ordinale\"" +
            "   ,   M.id_rilevazione    AS \"idAppo\"" +
            "   ,   ROUND(100 * SUM(AM.quotaparte) OVER (PARTITION BY M.id) / (SUM(AM.quotaparte) OVER ())::numeric, 2) AS \"quotaParte\"" +
            "   ,   ROUND((SUM(AM.quotaparte * (A.perc_parttime / 100.0)) OVER (PARTITION BY M.id) / 100.0), 2)         AS \"fte\"" +
            "   FROM macroprocesso M" +
            "       INNER JOIN rilevazione R ON M.id_rilevazione = R.id" +
            "       INNER JOIN allocazione_macroprocesso AM ON AM.id_macroprocesso = M.id" +
            "       INNER JOIN afferenza A ON R.id = A.id_rilevazione AND A.id_persona = AM.id_persona" +
            "   WHERE R.codice ILIKE ?" +
            "       AND AM.id_persona = ?" +
            "   ORDER BY M.codice";

    /**
     * <p>Estrae tutti i processi appartenenti a un macroprocesso, il cui identificativo viene
     * passato come parametro, e allocati su una persona, il cui identificativo viene passato
     * come parametro, nel contesto di una specifica rilevazione.</p>
     */
    public static final String GET_PROCESSI_BY_MACRO_AND_PERSON =
            "SELECT DISTINCT" +
            "       PR.id                AS \"id\"" +
            "   ,   PR.codice            AS \"codice\"" +
            "   ,   PR.nome              AS \"nome\"" +
            "   ,   PR.ordinale          AS \"ordinale\"" +
            "   ,   PR.smartworking      AS \"smartWorking\"" +
            "   ,   COALESCE(ROUND(100 * SUM(AP.quotaparte) OVER (PARTITION BY PR.id) / (SUM(AP.quotaparte) OVER ())::numeric, 2), 0) AS \"quotaParte\"" +
            "   ,   COALESCE(ROUND((SUM(AP.quotaparte * (A.perc_parttime / 100.0)) OVER (PARTITION BY PR.id) / 100.0), 2), 0)         AS \"fte\"" +
            "   FROM processo PR" +
            "       INNER JOIN allocazione_processo AP ON AP.id_processo = PR.id" +
            "       INNER JOIN rilevazione R ON PR.id_rilevazione = R.id" +
            "       INNER JOIN macroprocesso M ON PR.id_macroprocesso = M.id" +
            "       INNER JOIN afferenza A ON AP.id_persona = A.id_persona AND AP.id_rilevazione = A.id_rilevazione" +
            "   WHERE R.codice ILIKE ?" +
            "       AND PR.id_macroprocesso = ?" +
            "       AND AP.id_persona = ?" +
            "   ORDER BY PR.codice";
    
    /**
     * <p>Estrae tutti i processi appartenenti a un macroprocesso, il cui identificativo viene
     * passato come parametro.</p>
     * <p><small>Notare che questa query e la correlata GET_MACRO_BY_SURVEY (rispettivamente,
     * estrazione processi per id macroprocesso ed estrazione macroprocessi per
     * rilevazione) sarebbero potute essere state raggruppate in un'unica query
     * e poi smistate a valle, in modo da ripristinare la relazione padre/figlio,
     * nel codice Java. Ricordiamo infatti che il modello gerarchico degli oggetti
     * non &egrave; automatico da mappare nel modello relazionale dei dati e richiede
     * infatti una operazione di traduzione (cfr. Sarti).<br />
     * Se avessimo proceduto con un'unica query,
     * si sarebbe fatto un unico accesso disco, e ottimizzati i tempi di estrazione,
     * perch&eacute; l'accesso ai dati &egrave; circa 1000 volte pi&uacute; lento della
     * elaborazione dei dati in memoria. Tuttavia, siccome non sto implementando Amazon,
     * questa criticit&agrave; sull'ottimizzazione dei tempi non ce l'ho, e quindi faccio
     * 2 query, che producono un codice molto pi&uacute; leggibile.</small></p>
     */
    public static final String GET_PROCESSI_BY_MACRO =
            "SELECT DISTINCT" +
            "       PR.id                AS \"id\"" +
            "   ,   PR.codice            AS \"codice\"" +
            "   ,   PR.nome              AS \"nome\"" +
            "   ,   PR.ordinale          AS \"ordinale\"" +
            "   ,   PR.smartworking      AS \"smartWorking\"" +
            "   ,   COALESCE(ROUND(100 * SUM(AP.quotaparte) OVER (PARTITION BY PR.id) / (SUM(AP.quotaparte) OVER ())::numeric, 2), 0) AS \"quotaParte\"" +
            "   ,   COALESCE(ROUND((SUM(AP.quotaparte * (A.perc_parttime / 100.0)) OVER (PARTITION BY PR.id) / 100.0), 2), 0)         AS \"fte\"" +
            "   FROM processo PR" +
            "       INNER JOIN macroprocesso M ON PR.id_macroprocesso = M.id" +
            "       INNER JOIN allocazione_processo AP ON AP.id_processo = PR.id" +
            "       INNER JOIN afferenza A ON AP.id_persona = A.id_persona AND AP.id_rilevazione = A.id_rilevazione" +
            "   WHERE PR.id_macroprocesso = ?" +
            "   ORDER BY PR.codice";

    /**
     * <p>Estrae le persone allocate su un macroprocesso</p>
     */
    public static final String GET_PEOPLE_BY_MACRO =
            "SELECT " +
            "       P.id        AS \"id\"" +
            "   ,   P.nome      AS \"nome\"" +
            "   ,   P.cognome   AS \"cognome\"" +
            "   ,   P.sesso     AS \"sesso\"" +
            "   FROM persona P" +
            "       INNER JOIN allocazione_macroprocesso AM ON AM.id_persona = P.id" +
            "   WHERE AM.id_macroprocesso = ?" +
            "       AND AM.id_rilevazione = ?" +
            "   ORDER BY P.cognome";

    /**
     * <p>Estrae le persone allocate su un processo</p>
     */
    public static final String GET_PEOPLE_BY_PROCESS =
            "SELECT " +
                    "       P.id        AS \"id\"" +
                    "   ,   P.nome      AS \"nome\"" +
                    "   ,   P.cognome   AS \"cognome\"" +
                    "   ,   P.sesso     AS \"sesso\"" +
                    "   FROM persona P" +
                    "       INNER JOIN allocazione_processo AM ON AM.id_persona = P.id" +
                    "   WHERE AM.id_processo = ?" +
                    "       AND AM.id_rilevazione = ?" +
                    "   ORDER BY P.cognome";

    /**
     * <p>Estrae la singola persona in base al suo identificativo, passato come parametro</p>
     */
    public static final String GET_PERSON =
            "SELECT " +
                    "       P.id                        AS \"id\"" +
                    "   ,   P.nome                      AS \"nome\"" +
                    "   ,   P.cognome                   AS \"cognome\"" +
                    "   ,   P.sesso                     AS \"sesso\"" +
                    "   ,   P.data_nascita              AS \"dataNascita\"" +
                    "   ,   P.sesso                     AS \"sesso\"" +
                    "   ,   AF.codice_area_funz         AS \"codAreaFunzionale\"" +
                    "   ,   AF.codice_ruolo_giuridico   AS \"codRuoloGiuridico\"" +
                    "   ,   AF.responsabile             AS \"responsabile\"" +
                    "   ,   AF.respons_organizzativa    AS \"livResponsabilitaOrganizzativa\"" +
                    "   ,   AF.funzione_specialistica   AS \"livFunzioneSpecialistica\"" +
                    "   ,   AF.tecnico_lab              AS \"livTecnicoLaboratorio\"" +
                    "   ,   AF.tempo_pieno              AS \"tempoPieno\"" +
                    "   ,   AF.perc_parttime            AS \"note\"" +
                    "   ,   COALESCE(AF.id_struttura_liv4, AF.id_struttura_liv3, AF.id_struttura_liv2, AF.id_struttura_liv1) AS \"idDipartimento\"" +
                    "   ,   CASE" +
                    "           WHEN AF.id_struttura_liv4 IS NOT NULL THEN 4" +
                    "           WHEN AF.id_struttura_liv3 IS NOT NULL THEN 3" +
                    "           WHEN AF.id_struttura_liv2 IS NOT NULL THEN 2" +
                    "           WHEN AF.id_struttura_liv1 IS NOT NULL THEN 1" +
                    "           ELSE 0" +
                    "       END AS \"urlDipartimento\"" +
                    "   FROM persona P" +
                    "       INNER JOIN afferenza AF ON AF.id_persona = P.id" +
                    "   WHERE AF.id_rilevazione = ?" +
                    "       AND P.id = ?";

    /**
     * <p>Estrae le strutture allocate su un macroprocesso</p>
     */
    public static final String GET_STRUCTS_BY_MACRO =
            "WITH gerarchia AS (" +
            "    SELECT sl1.id                                                                                AS id_l1," +
            "           sl1.codice                                                                            AS codice_uo_l1," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l1," +
            "           sl1.id                                                                                AS id_l2," +
            "           sl1.codice                                                                            AS codice_uo_l2," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l2," +
            "           sl1.id                                                                                AS id_l3," +
            "           sl1.codice                                                                            AS codice_uo_l3," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l3," +
            "           sl1.id                                                                                AS id_l4," +
            "           sl1.codice                                                                            AS codice_uo_l4," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l4," +
            "           1                                                                                     AS livello" +
            "    FROM struttura_liv1 sl1 " +
            "    WHERE sl1.id_rilevazione = ?" +
            "  UNION" +
            "    SELECT sl1.id                                                                                AS id_l1," +
            "           sl1.codice                                                                            AS codice_uo_l1," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l1," +
            "           sl2.id                                                                                AS id_l2," +
            "           sl2.codice                                                                            AS codice_uo_l2," +
            "           CASE WHEN sl2.prefisso IS NULL THEN sl2.nome ELSE sl2.prefisso || ' ' || sl2.nome END AS descrizione_uo_l2," +
            "           sl2.id                                                                                AS id_l3," +
            "           sl2.codice                                                                            AS codice_uo_l3," +
            "           CASE WHEN sl2.prefisso IS NULL THEN sl2.nome ELSE sl2.prefisso || ' ' || sl2.nome END AS descrizione_uo_l3," +
            "           sl2.id                                                                                AS id_l4," +
            "           sl2.codice                                                                            AS codice_uo_l4," +
            "           CASE WHEN sl2.prefisso IS NULL THEN sl2.nome ELSE sl2.prefisso || ' ' || sl2.nome END AS descrizione_uo_l4," +
            "           2                                                                                     AS livello" +
            "    FROM struttura_liv1 sl1" +
            "       JOIN struttura_liv2 sl2 ON sl1.id = sl2.id_struttura_liv1" +
            "    WHERE sl1.id_rilevazione = ?" +
            "  UNION" +
            "    SELECT sl1.id                                                                                AS id_l1," +
            "           sl1.codice                                                                            AS codice_uo_l1," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l1," +
            "           sl2.id                                                                                AS id_l2," +
            "           sl2.codice                                                                            AS codice_uo_l2," +
            "           CASE WHEN sl2.prefisso IS NULL THEN sl2.nome ELSE sl2.prefisso || ' ' || sl2.nome END AS descrizione_uo_l2," +
            "           sl3.id                                                                                AS id_l3," +
            "           sl3.codice                                                                            AS codice_uo_l3," +
            "           CASE WHEN sl3.prefisso IS NULL THEN sl3.nome ELSE sl3.prefisso || ' ' || sl3.nome END AS descrizione_uo_l3," +
            "           sl3.id                                                                                AS id_l4," +
            "           sl3.codice                                                                            AS codice_uo_l4," +
            "           CASE WHEN sl3.prefisso IS NULL THEN sl3.nome ELSE sl3.prefisso || ' ' || sl3.nome END AS descrizione_uo_l4," +
            "           3                                                                                     AS livello" +
            "    FROM struttura_liv1 sl1" +
            "       JOIN struttura_liv2 sl2 ON sl1.id = sl2.id_struttura_liv1" +
            "       JOIN struttura_liv3 sl3 ON sl2.id = sl3.id_struttura_liv2" +
            "    WHERE sl1.id_rilevazione = ?" +
            "  UNION" +
            "    SELECT sl1.id                                                                                AS id_l1," +
            "           sl1.codice                                                                            AS codice_uo_l1," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l1," +
            "           sl2.id                                                                                AS id_l2," +
            "           sl2.codice                                                                            AS codice_uo_l2," +
            "           CASE WHEN sl2.prefisso IS NULL THEN sl2.nome ELSE sl2.prefisso || ' ' || sl2.nome END AS descrizione_uo_l2," +
            "           sl3.id                                                                                AS id_l3," +
            "           sl3.codice                                                                            AS codice_uo_l3," +
            "           CASE WHEN sl3.prefisso IS NULL THEN sl3.nome ELSE sl3.prefisso || ' ' || sl3.nome END AS descrizione_uo_l3," +
            "           sl4.id                                                                                AS id_l4," +
            "           sl4.codice                                                                            AS codice_uo_l4," +
            "           CASE WHEN sl4.prefisso IS NULL THEN sl4.nome ELSE sl4.prefisso || ' ' || sl4.nome END AS descrizione_uo_l4," +
            "           4                                                                                     AS livello" +
            "    FROM struttura_liv1 sl1" +
            "       JOIN struttura_liv2 sl2 ON sl1.id = sl2.id_struttura_liv1" +
            "       JOIN struttura_liv3 sl3 ON sl2.id = sl3.id_struttura_liv2" +
            "       JOIN struttura_liv4 sl4 ON sl3.id = sl4.id_struttura_liv3" +
            "    WHERE sl1.id_rilevazione = ?" +
            ")" +
            "SELECT DISTINCT " +
            "       id_l1                   AS \"cod1\"," +
            "       codice_uo_l1            AS \"codice\"," +
            "       descrizione_uo_l1       AS \"informativa\"," +
            "       id_l2                   AS \"cod2\"," +
            "       codice_uo_l2            AS \"nomeReale\"," +
            "       descrizione_uo_l2       AS \"extraInfo\"," +
            "       id_l3                   AS \"cod3\"," +
            "       codice_uo_l3            AS \"nomeClasse\"," +
            "       descrizione_uo_l3       AS \"labelWeb\"," +
            "       id_l4                   AS \"cod4\"," +
            "       codice_uo_l4            AS \"nome\"," +
            "       descrizione_uo_l4       AS \"paginaJsp\"," +
            "       COALESCE(ROUND((SUM((AM.quotaparte)*(a.perc_parttime/100)) OVER (PARTITION BY codice_uo_l1) / 100.0), 2), 0)    AS \"value1\"," +   // fte_L1
            "       COALESCE(ROUND((SUM((AM.quotaparte)*(a.perc_parttime/100)) OVER (PARTITION BY codice_uo_l2) / 100.0), 2), 0)    AS \"value2\"," +   // fte_L2
            "       COALESCE(ROUND((SUM((AM.quotaparte)*(a.perc_parttime/100)) OVER (PARTITION BY codice_uo_l3) / 100.0), 2), 0)    AS \"value3\"," +   // fte_L3
            "       COALESCE(ROUND((SUM((AM.quotaparte)*(a.perc_parttime/100)) OVER (PARTITION BY codice_uo_l4) / 100.0), 2), 0)    AS \"value4\"," +   // fte_L4
            "       mp.codice               AS \"url\"," +
            "       mp.nome                 AS \"icona\"" +
            "    FROM afferenza a" +
            "       JOIN rilevazione r ON a.id_rilevazione = r.id" +
            "       JOIN persona p ON a.id_persona = p.id" +
            "       JOIN gerarchia g ON CASE" +
            "                               WHEN a.id_struttura_liv1 IS NOT NULL THEN a.id_struttura_liv1 = g.id_l1 AND g.livello = 1" +
            "                               WHEN a.id_struttura_liv2 IS NOT NULL THEN a.id_struttura_liv2 = g.id_l2 AND g.livello = 2" +
            "                               WHEN a.id_struttura_liv3 IS NOT NULL THEN a.id_struttura_liv3 = g.id_l3 AND g.livello = 3" +
            "                               WHEN a.id_struttura_liv4 IS NOT NULL THEN a.id_struttura_liv4 = g.id_l4 AND g.livello = 4" +
            "                           END" +
            "       JOIN allocazione_macroprocesso AM ON p.id = AM.id_persona AND AM.id_rilevazione = r.id" +
            "       JOIN macroprocesso mp ON AM.id_macroprocesso = mp.id" +
            "    WHERE r.id = ?" +
            "       AND (AM.id_macroprocesso = ? OR -1 = ?)" +
            "    ORDER BY descrizione_uo_l1, descrizione_uo_l2, descrizione_uo_l3, descrizione_uo_l4";

    /**
     * <p>Estrae le strutture allocate su un processo</p>
     */
    public static final String GET_STRUCTS_BY_PROCESS =
            "WITH gerarchia AS (" +
            "    SELECT sl1.id                                                                                AS id_l1," +
            "           sl1.codice                                                                            AS codice_uo_l1," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l1," +
            "           sl1.id                                                                                AS id_l2," +
            "           sl1.codice                                                                            AS codice_uo_l2," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l2," +
            "           sl1.id                                                                                AS id_l3," +
            "           sl1.codice                                                                            AS codice_uo_l3," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l3," +
            "           sl1.id                                                                                AS id_l4," +
            "           sl1.codice                                                                            AS codice_uo_l4," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l4," +
            "           1                                                                                     AS livello" +
            "    FROM struttura_liv1 sl1 " +
            "    WHERE sl1.id_rilevazione = ?" +
            "  UNION" +
            "    SELECT sl1.id                                                                                AS id_l1," +
            "           sl1.codice                                                                            AS codice_uo_l1," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l1," +
            "           sl2.id                                                                                AS id_l2," +
            "           sl2.codice                                                                            AS codice_uo_l2," +
            "           CASE WHEN sl2.prefisso IS NULL THEN sl2.nome ELSE sl2.prefisso || ' ' || sl2.nome END AS descrizione_uo_l2," +
            "           sl2.id                                                                                AS id_l3," +
            "           sl2.codice                                                                            AS codice_uo_l3," +
            "           CASE WHEN sl2.prefisso IS NULL THEN sl2.nome ELSE sl2.prefisso || ' ' || sl2.nome END AS descrizione_uo_l3," +
            "           sl2.id                                                                                AS id_l4," +
            "           sl2.codice                                                                            AS codice_uo_l4," +
            "           CASE WHEN sl2.prefisso IS NULL THEN sl2.nome ELSE sl2.prefisso || ' ' || sl2.nome END AS descrizione_uo_l4," +
            "           2                                                                                     AS livello" +
            "    FROM struttura_liv1 sl1" +
            "       JOIN struttura_liv2 sl2 ON sl1.id = sl2.id_struttura_liv1" +
            "    WHERE sl1.id_rilevazione = ?" +
            "  UNION" +
            "    SELECT sl1.id                                                                                AS id_l1," +
            "           sl1.codice                                                                            AS codice_uo_l1," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l1," +
            "           sl2.id                                                                                AS id_l2," +
            "           sl2.codice                                                                            AS codice_uo_l2," +
            "           CASE WHEN sl2.prefisso IS NULL THEN sl2.nome ELSE sl2.prefisso || ' ' || sl2.nome END AS descrizione_uo_l2," +
            "           sl3.id                                                                                AS id_l3," +
            "           sl3.codice                                                                            AS codice_uo_l3," +
            "           CASE WHEN sl3.prefisso IS NULL THEN sl3.nome ELSE sl3.prefisso || ' ' || sl3.nome END AS descrizione_uo_l3," +
            "           sl3.id                                                                                AS id_l4," +
            "           sl3.codice                                                                            AS codice_uo_l4," +
            "           CASE WHEN sl3.prefisso IS NULL THEN sl3.nome ELSE sl3.prefisso || ' ' || sl3.nome END AS descrizione_uo_l4," +
            "           3                                                                                     AS livello" +
            "    FROM struttura_liv1 sl1" +
            "       JOIN struttura_liv2 sl2 ON sl1.id = sl2.id_struttura_liv1" +
            "       JOIN struttura_liv3 sl3 ON sl2.id = sl3.id_struttura_liv2" +
            "    WHERE sl1.id_rilevazione = ?" +
            "  UNION" +
            "    SELECT sl1.id                                                                                AS id_l1," +
            "           sl1.codice                                                                            AS codice_uo_l1," +
            "           CASE WHEN sl1.prefisso IS NULL THEN sl1.nome ELSE sl1.prefisso || ' ' || sl1.nome END AS descrizione_uo_l1," +
            "           sl2.id                                                                                AS id_l2," +
            "           sl2.codice                                                                            AS codice_uo_l2," +
            "           CASE WHEN sl2.prefisso IS NULL THEN sl2.nome ELSE sl2.prefisso || ' ' || sl2.nome END AS descrizione_uo_l2," +
            "           sl3.id                                                                                AS id_l3," +
            "           sl3.codice                                                                            AS codice_uo_l3," +
            "           CASE WHEN sl3.prefisso IS NULL THEN sl3.nome ELSE sl3.prefisso || ' ' || sl3.nome END AS descrizione_uo_l3," +
            "           sl4.id                                                                                AS id_l4," +
            "           sl4.codice                                                                            AS codice_uo_l4," +
            "           CASE WHEN sl4.prefisso IS NULL THEN sl4.nome ELSE sl4.prefisso || ' ' || sl4.nome END AS descrizione_uo_l4," +
            "           4                                                                                     AS livello" +
            "    FROM struttura_liv1 sl1" +
            "       JOIN struttura_liv2 sl2 ON sl1.id = sl2.id_struttura_liv1" +
            "       JOIN struttura_liv3 sl3 ON sl2.id = sl3.id_struttura_liv2" +
            "       JOIN struttura_liv4 sl4 ON sl3.id = sl4.id_struttura_liv3" +
            "    WHERE sl1.id_rilevazione = ?" +
            ")" +
            "SELECT DISTINCT " +
            "       id_l1                   AS \"cod1\"," +
            "       codice_uo_l1            AS \"codice\"," +
            "       descrizione_uo_l1       AS \"informativa\"," +
            "       id_l2                   AS \"cod2\"," +
            "       codice_uo_l2            AS \"nomeReale\"," +
            "       descrizione_uo_l2       AS \"extraInfo\"," +
            "       id_l3                   AS \"cod3\"," +
            "       codice_uo_l3            AS \"nomeClasse\"," +
            "       descrizione_uo_l3       AS \"labelWeb\"," +
            "       id_l4                   AS \"cod4\"," +
            "       codice_uo_l4            AS \"nome\"," +
            "       descrizione_uo_l4       AS \"paginaJsp\"," +
            "       COALESCE(ROUND((SUM((AM.quotaparte)*(a.perc_parttime/100)) OVER (PARTITION BY codice_uo_l1) / 100.0), 2), 0)    AS \"value1\"," +   // fte_L1
            "       COALESCE(ROUND((SUM((AM.quotaparte)*(a.perc_parttime/100)) OVER (PARTITION BY codice_uo_l2) / 100.0), 2), 0)    AS \"value2\"," +   // fte_L2
            "       COALESCE(ROUND((SUM((AM.quotaparte)*(a.perc_parttime/100)) OVER (PARTITION BY codice_uo_l3) / 100.0), 2), 0)    AS \"value3\"," +   // fte_L3
            "       COALESCE(ROUND((SUM((AM.quotaparte)*(a.perc_parttime/100)) OVER (PARTITION BY codice_uo_l4) / 100.0), 2), 0)    AS \"value4\"," +   // fte_L4
            "       mp.codice               AS \"url\"," +
            "       mp.nome                 AS \"icona\"" +
            "    FROM afferenza a" +
            "       JOIN rilevazione r ON a.id_rilevazione = r.id" +
            "       JOIN persona p ON a.id_persona = p.id" +
            "       JOIN gerarchia g ON CASE" +
            "                               WHEN a.id_struttura_liv1 IS NOT NULL THEN a.id_struttura_liv1 = g.id_l1 AND g.livello = 1" +
            "                               WHEN a.id_struttura_liv2 IS NOT NULL THEN a.id_struttura_liv2 = g.id_l2 AND g.livello = 2" +
            "                               WHEN a.id_struttura_liv3 IS NOT NULL THEN a.id_struttura_liv3 = g.id_l3 AND g.livello = 3" +
            "                               WHEN a.id_struttura_liv4 IS NOT NULL THEN a.id_struttura_liv4 = g.id_l4 AND g.livello = 4" +
            "                           END" +
            "       JOIN allocazione_processo AM ON p.id = AM.id_persona AND AM.id_rilevazione = r.id" +
            "       JOIN processo mp ON AM.id_processo = mp.id" +
            "    WHERE r.id = ?" +
            "       AND (AM.id_processo = ? OR -1 = ?)" +
            "    ORDER BY descrizione_uo_l1, descrizione_uo_l2, descrizione_uo_l3, descrizione_uo_l4";

    /**
     * <p>Estrae le aree funzionali scartando i valori riservati per la gestione del dato</p>
     */
    public static final String GET_AREE_FUNZ =
            "SELECT " +
            "       A.codice        AS \"codice\"" +
            "   ,   A.nome          AS \"nome\"" +
            "   ,   A.ordinale      AS \"ordinale\"" +
            "   FROM area_funzionale A" +
            "   WHERE A.nome ILIKE 'Area%'" +
            "   ORDER BY A.codice";

    /**
     * <p>Estrae tutti i ruoli giuridici</p>
     */
    public static final String GET_ROLES =
            "SELECT " +
            "       RG.codice        AS \"codice\"" +
            "   ,   RG.nome          AS \"nome\"" +
            "   ,   RG.tipo_ruolo    AS \"informativa\"" +
            "   ,   RG.ordinale      AS \"ordinale\"" +
            "   FROM ruolo_giuridico RG" +
            "   ORDER BY RG.codice";

    /**
     * <p>Estrae le persone afferenti a una struttura allocata su un macroprocesso</p>
     *
     * @param idM identificativo del macroprocesso
     * @param idR identificativo della rilevazione
     * @param idl identificativi delle strutture di livello 4, 3, 2 e 1 (-1 se null)
     * @return <code>String</code> - la query che estrae le persone afferenti alla struttura data, costruita dinamicamente
     */
    public String getQueryPeopleByStructureAndMacro(int idM, int idR, int[] idl);

    /**
     * <p>Estrae le persone afferenti a una struttura allocata su un processo</p>
     *
     * @param idP identificativo del   processo
     * @param idR identificativo della rilevazione
     * @param idl identificativi delle strutture di livello 4, 3, 2 e 1 (-1 se null)
     * @return <code>String</code> - la query che estrae le persone afferenti alla struttura data, costruita dinamicamente
     */
    public String getQueryPeopleByStructureAndProcess(int idP, int idR, int[] idl);

    /**
     * <p>Costruisce dinamicamente la query che, a partire dall'identificativo di
     * una struttura di un dato livello, recupera tutti i macroprocessi ad essa collegati.</p>
     *
     * @param id    identificativo della struttura di cui si vogliono recuperare i macroprocessi
     * @param level livello della struttura di cui si vogliono recuperare i macroprocessi
     * @param code  codice della rilevazione nel contesto della quale si vogliono recuperare i macroprocessi
     * @return <code>String</code> - la query che seleziona i macroprocessi desiderati
     */
    public String getQueryMacroByStruct(int id, byte level, String code);

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
     * <p>Costruisce dinamicamente la query che seleziona un insieme di persone in base
     * ad una serie di parametri di ricerca immessi dall'utente tramite funzionalit&agrave;
     * di navigazione.</p>
     *
     * @param fields    mappa contenente i parametri utente, indicizzati per nome
     * @param idSurvey  identificativo della rilevazione
     * @return <code>String</code> - la query che seleziona l'insieme desiderato
     */
    public String getQueryPeople(HashMap<String, String> fields, int idSurvey);
    
    /* ********************************************************************** *
     *              3. Query di Selezione di ROL (Rischi On Line)             *
     * ********************************************************************** */
    /**
     * <p>Estrae tutti i macroprocessi censiti dall'anticorruzione filtrati 
     * in base all'identificativo della rilevazione, passato come parametro.</p>
     */
    public static final String GET_MACRO_AT_BY_SURVEY =
            "SELECT DISTINCT" +
            "       MAT.id                AS \"id\"" +
            "   ,   MAT.codice            AS \"codice\"" +
            "   ,   MAT.nome              AS \"nome\"" +
            "   ,   MAT.ordinale          AS \"ordinale\"" +
            "   ,   MAT.id_rilevazione    AS \"idAppo\"" +
            "   FROM macroprocesso_at MAT" +
            "       INNER JOIN rilevazione R ON MAT.id_rilevazione = R.id" +
            //"       INNER JOIN allocazione_macroprocesso_at AM ON AM.id_macroprocesso = M.id" +
            //"       INNER JOIN afferenza A ON R.id = A.id_rilevazione AND A.id_persona = AM.id_persona" +
            "   WHERE R.codice ILIKE ?" +
            "   ORDER BY MAT.codice";
    
    /**
     * <p>Estrae tutti i processi anticorruzione appartenenti a un macroprocesso
     * anticorruzione, il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_PROCESSI_AT_BY_MACRO =
            "SELECT DISTINCT" +
            "       PRAT.id                AS \"id\"" +
            "   ,   PRAT.codice            AS \"codice\"" +
            "   ,   PRAT.nome              AS \"nome\"" +
            "   ,   PRAT.ordinale          AS \"ordinale\"" +
            "   ,   PRAT.smartworking      AS \"smartWorking\"" +
            "   FROM processo_at PRAT" +
            "       INNER JOIN macroprocesso_at MAT ON PRAT.id_macroprocesso_at = MAT.id" +
            //"       INNER JOIN allocazione_processo_at AP ON AP.id_processo = PR.id" +
            //"       INNER JOIN afferenza A ON AP.id_persona = A.id_persona AND AP.id_rilevazione = A.id_rilevazione" +
            "   WHERE PRAT.id_macroprocesso_at = ?" +
            "   ORDER BY PRAT.codice";

    /**
     * <p>Estrae tutti i sottoprocessi anticorruzione appartenenti a un processo
     * anticorruzione, il cui identificativo viene passato come parametro.</p>
     */
    public static final String GET_SOTTOPROCESSI_AT_BY_PROCESS =
            "SELECT DISTINCT" +
            "       SPRAT.id                AS \"id\"" +
            "   ,   SPRAT.codice            AS \"codice\"" +
            "   ,   SPRAT.nome              AS \"nome\"" +
            "   ,   SPRAT.ordinale          AS \"ordinale\"" +
            "   ,   SPRAT.smartworking      AS \"smartWorking\"" +
            "   FROM sottoprocesso_at SPRAT" +
            "       INNER JOIN processo_at PRAT ON SPRAT.id_processo_at = PRAT.id" +
            //"       INNER JOIN allocazione_processo_at AP ON AP.id_processo = PR.id" +
            //"       INNER JOIN afferenza A ON AP.id_persona = A.id_persona AND AP.id_rilevazione = A.id_rilevazione" +
            "   WHERE SPRAT.id_processo_at = ?" +
            "   ORDER BY SPRAT.codice";
    
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
     * <p>Costruisce dinamicamente la query che seleziona un insieme di persone in base
     * ad una serie di parametri di ricerca immessi dall'utente tramite funzionalit&agrave;
     * di navigazione.</p>
     *  TODO COMMENTO
     * @param fields    mappa contenente i parametri utente, indicizzati per nome
     * @param idSurvey  identificativo della rilevazione
     * @return <code>String</code> - la query che seleziona l'insieme desiderato
     */
    public String getQueryAnswers(HashMap<String, String> fields, int idSurvey);
    
    /* ********************************************************************** *
     *                        4. Query di inserimento                         *
     * ********************************************************************** */
    /**
     * <p>Query per inserimento di ultimo accesso al sistema.</p>
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
    
    
    /* ********************************************************************** *
     *                       5. Query di aggiornamento                        *
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


}
