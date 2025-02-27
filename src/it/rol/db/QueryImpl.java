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
 *   alla gestione dei processi a rischio.
 *
 *   Risk Mapping and Management Software (ROL-RMS),
 *   web application: 
 *   - to assess the amount and type of corruption risk to which each organizational process is exposed, 
 *   - to publish and manage, reports and information on risk
 *   - and to propose mitigation measures specifically aimed at reducing risk, 
 *   - also allowing monitoring to be carried out to see 
 *   which proposed mitigation measures were then actually implemented.
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

package it.rol.db;

import java.util.HashMap;
import java.util.LinkedHashMap;

import it.rol.Constants;
import it.rol.Query;
import it.rol.bean.InterviewBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.WebStorageException;


/**
 * <p>QueryImpl &egrave; l'implementazione dell'interfaccia {@link Query} 
 * la quale contiene, oltre alle query parametriche della della web-application 
 * &nbsp; <code>ROL-RMS (Rischi On Line &mdash; Risk Mapping Software)</code>,
 * anche l'interfaccia pubblica di alcuni metodi per la costruzione di
 * query a runtime &ndash; implementazione di cui si prende carico proprio
 * il presente oggetto.<br> 
 * Implementa pertanto alcuni metodi, dichiarati in quella, che costruiscono,
 * a runtime, query la cui esatta struttura non era possibile stabilire 
 * in anticipo (a meno di introdurre notevoli ridondanze e violare
 * il paradigma DRY).<br>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 * @see Query
 */
public class QueryImpl implements Query, Constants {

    
    /**
     * Auto-generated serial version ID: parent implements 
     * the Serializable interface.
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 7339151352217708748L;


    /** 
     * {@link Query#getQueryStructures(int, int, int, int, int)} 
     * @see it.rol.Query#getQueryStructures(int, int, int, int, int)
     */
    @Override
    public String getQueryStructures(int idR, 
                                     int idl4, 
                                     int idl3, 
                                     int idl2, 
                                     int idl1) {
        String tableFrom = null;
        String tableWhere = null;
        byte level = (byte) DEFAULT_ID;
        // Se passa -1 su questo parametro vuol recuperare tutte le strutture di livello 1
        if (idl1 == DEFAULT_ID) {
            level = (byte) 1;
            tableFrom = "struttura_liv1";
            tableWhere = DEFAULT_ID + " = " + idl1 ;
        // Altrimenti, se passa un id, vuol recuperare tutte le strutture di livello 2 che afferiscono alla struttura di livello 1 di tale id
        } else if (idl1 > NOTHING) {
            level = (byte) 2;
            tableFrom = "struttura_liv2";
            tableWhere = "id_struttura_liv1 = " + idl1;
        }
        // Liv 2
        if (idl2 == DEFAULT_ID) {       // Tutte le Liv 2
            level = (byte) 2;
            tableFrom = "struttura_liv2";
            tableWhere = DEFAULT_ID + " = " + idl2 ;
        } else if (idl2 > NOTHING) {    // Solo le Liv 3 afferenti a una specifica Liv 2
            level = (byte) 3;
            tableFrom = "struttura_liv3";
            tableWhere = "id_struttura_liv2 = " + idl2;
        }
        // Liv 3
        if (idl3 == DEFAULT_ID) {       // Tutte le Liv 3
            level = (byte) 3;
            tableFrom = "struttura_liv3";
            tableWhere = DEFAULT_ID + " = " + idl3 ;
        } else if (idl3 > NOTHING) {    // Solo le Liv 4 afferenti a una specifica Liv 3
            level = (byte) 4;
            tableFrom = "struttura_liv4";
            tableWhere = "id_struttura_liv3 = " + idl3;
        }
        // Liv 4
        if (idl4 == DEFAULT_ID) {       // Tutte le Liv 4
            level = (byte) 4;
            tableFrom = "struttura_liv4";
            tableWhere = DEFAULT_ID + " = " + idl4 ;
        }
        final String GET_STRUCTURE_BY_STRUCTURE =
                "SELECT " +
                "       D.id                 AS \"id\"" +
                "   ,   D.nome               AS \"nome\"" +
                "   ,   D.codice             AS \"informativa\"" +
                "   ,   D.ordinale           AS \"ordinale\"" +
                "   ,   D.prefisso           AS \"prefisso\"" +
                "   ,   D.acronimo           AS \"acronimo\"" +
                "   ,   D.indirizzo_sede     AS \"indirizzo\"" +
                "   ," + level + "::SMALLINT AS \"livello\"" +
                "   FROM " + tableFrom + " D" +
                "   WHERE D.id_rilevazione = " + idR +
                "       AND " + tableWhere +
                "       AND id_stato = 1" + // Prende solo le strutture attive
                "   ORDER BY D.nome, D.ordinale";
        return GET_STRUCTURE_BY_STRUCTURE;
    }


    /**
     * {@link Query#getQueryStructure(int, byte)}
     * @see it.rol.Query#getQueryStructure(int, byte)
     */
    @Override
    public String getQueryStructure(int id,
                                    byte level) {
        String table = null;
        switch(level) {
            case 1:
                table = "struttura_liv1";
                break;
            case 2:
                table = "struttura_liv2";
                break;
            case 3:
                table = "struttura_liv3";
                break;
            case 4:
                table = "struttura_liv4";
                break;
        }
        final String GET_STRUCTURE_BY_ID =
                "SELECT " +
                "       D.id        AS \"id\"" +
                "   ,   D.nome      AS \"nome\"" +
                "   ,   D.codice    AS \"informativa\"" +
                "   ,   D.prefisso  AS \"prefisso\"" +
                "   ,   D.acronimo  AS \"acronimo\"" +
                "   ,   D.ordinale  AS \"ordinale\"" +
                "   ," + level +  " AS \"livello\"" + 
                "   FROM " + table + " D" +
                "   WHERE D.id = " + id +
                "   ORDER BY D.ordinale";
        return GET_STRUCTURE_BY_ID;
    }


    @Override
    public String getQueryStructureBySurvey(int idR, int idl4, int idl3, int idl2, int idl1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getQueryMacroSubProcessAtBySurvey(int idP, byte level, int idSur) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    /**
     * {@link Query#getQueryMacroSubProcessAtByIdOrCode(int, byte, int)}
     * @see it.rol.Query#getQueryMacroSubProcessAtByIdOrCode(int, String, byte, int)
     */
    @Override
    public String getQueryMacroSubProcessAtByIdOrCode(int idO, 
                                                      String codeO, 
                                                      byte level, 
                                                      int idSur) {
        String tableName = null;
        String tableClause = null;
        switch(level) {
            case ELEMENT_LEV_1:
                tableName = "macroprocesso_at";
                break;
            case ELEMENT_LEV_2:
                tableName = "processo_at";
                break;
            case ELEMENT_LEV_3:
                tableName = "sottoprocesso_at";
                break;
            default:
                tableClause = VOID_STRING;
                break;
        }
        tableClause = " AND (" +
                            tableName + ".id = " + idO +
                      "     OR " +
                            tableName + ".codice = '" + codeO + "'" + 
                      "     )"; 
        final String GET_MPSAT_BY_ID_OR_CODE =
                "SELECT DISTINCT " +
                                 tableName + ".id       AS \"id\"" +
                        "   ," + tableName + ".codice   AS \"codice\"" +
                        "   ," + tableName + ".nome     AS \"nome\"" +
                        "   ," + tableName + ".ordinale AS \"ordinale\"" +
                        "   FROM " + tableName + 
                        "   WHERE " + tableName + ".id_rilevazione = " + idSur + 
                                tableClause;
        return GET_MPSAT_BY_ID_OR_CODE;
    }

    @Override
    public String getQueryAnswers(HashMap<String, LinkedHashMap<String, String>> params, int idSurvey, int limit,
            int idQuest, boolean getAll) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getQueryAnswers(InterviewBean params, int idSurvey)
            throws AttributoNonValorizzatoException, WebStorageException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getQueryProcessBySurvey(int idR, int idS, int idP, int idM) {
        // TODO Auto-generated method stub
        return null;
    }

    
    /**
     * {@link Query#getMeasuresByFactors(int, int, String)}
     * @see it.rol.Query#getMeasuresByFactors(int, int, String)
     */
    @Override
    public String getMeasuresByFactors(int idF, 
                                       int idS, 
                                       String codeM) {
        final String GET_MEASURES_BY_FACTOR = 
                "SELECT DISTINCT" +
                "       MS.codice                           AS \"codice\"" +
                "   ,   MS.nome                             AS \"nome\"" +
                "   ,   MS.descrizione                      AS \"stato\"" +
                "   ,   MS.onerosa                          AS \"onerosa\"" +
                "   ,   MS.ordinale                         AS \"ordinale\"" +
                "   ,   MS.data_ultima_modifica             AS \"dataUltimaModifica\"" +
                "   ,   MS.ora_ultima_modifica              AS \"oraUltimaModifica\"" +
                "   FROM fattore_tipologia FATTY" +
                "       INNER JOIN misura_tipologia MISTY ON MISTY.id_tipo_misura = FATTY.id_tipo_misura" +
                "       INNER JOIN misura MS ON (MISTY.cod_misura = MS.codice AND MISTY.id_rilevazione = MS.id_rilevazione)" +
                "   WHERE FATTY.id_fattore_abilitante = " + idF +
                "       AND FATTY.id_rilevazione = " + idS +
                "       AND MS.codice NOT IN (" + codeM + ")" +
                "   ORDER BY MS.nome";
        return GET_MEASURES_BY_FACTOR;
    }
    
    
    /**
     * {@link Query#getMeasureByRiskAndProcess(String, String, String, String, int)}
     * @see it.rol.Query#getMeasureByRiskAndProcess(String, String, String, String, int)
     */
    @Override
    public String getMeasureByRiskAndProcess(String idR, 
                                             String idP, 
                                             String idS, 
                                             String codeM, 
                                             int getAll) {
        final String GET_MEASURES_BY_RISK_AND_PROCESS = 
                "SELECT DISTINCT" +
                        "       MS.codice                           AS \"codice\"" +
                        "   ,   MS.nome                             AS \"nome\"" +
                        "   ,   MS.descrizione                      AS \"stato\"" +
                        "   ,   MS.onerosa                          AS \"onerosa\"" +
                        "   ,   MS.ordinale                         AS \"ordinale\"" +
                        "   ,   MS.data_ultima_modifica             AS \"dataUltimaModifica\"" +
                        "   ,   MS.ora_ultima_modifica              AS \"oraUltimaModifica\"" +
                        "   ,   MS.id_rilevazione                   AS \"idRilevazione\"" +
                        "   FROM misura_rischio_processo_at MRP" +
                        "       INNER JOIN misura MS ON (MRP.cod_misura = MS.codice AND MRP.id_rilevazione = MS.id_rilevazione)" +
                        "   WHERE MRP.id_processo_at = " + idP +
                        "       AND MRP.id_rischio_corruttivo = " + idR +
                        "       AND MRP.id_rilevazione = " + idS +
                        "       AND (" +
                        "           MS.codice IN (" + codeM + ")" +
                        "           OR -1 = " + getAll +
                        "           )" +
                        "   ORDER BY MS.codice";
        return GET_MEASURES_BY_RISK_AND_PROCESS;
    }
    
    
    /**
     * {@link Query#getMeasuresByStruct(int, int, byte, String)}
     * @see it.rol.Query#getMeasuresByStruct(int, int, byte, String)
     */
    @Override
    public String getMeasuresByStruct(int idR, 
                                      int idS,
                                      byte level,
                                      String role) {
        String clause = null;
        switch(level) {
        case (ELEMENT_LEV_1) :
            clause = "  AND MST.id_struttura_liv2 IS NULL" +
                     "  AND MST.id_struttura_liv3 IS NULL" +
                     "  AND MST.id_struttura_liv4 IS NULL";
            break;
        case (ELEMENT_LEV_2) :
            clause = "  AND MST.id_struttura_liv3 IS NULL" +
                     "  AND MST.id_struttura_liv4 IS NULL";
            break;
        case (ELEMENT_LEV_3) :
            clause = "  AND MST.id_struttura_liv4 IS NULL";
            break;
        default:
            clause = VOID_STRING;
            break;
        }
        final String GET_MEASURE_BY_STRUCT =
                "SELECT DISTINCT" +
                "       MST.cod_misura                                              AS \"codice\"" +
                "   ,   MST.ruolo                                                   AS \"ruolo\"" +
                "   ,   MS.nome                                                     AS \"nome\"" +
                "   ,   MS.descrizione                                              AS \"informativa\"" +
                "   ,   MS.onerosa                                                  AS \"onerosa\"" +
                "   ,   MS.ordinale                                                 AS \"ordinale\"" +
                "   ,   MM.obiettivopiao                                            AS \"obiettivo\"" +
                "   ,   COALESCE(MM.data_ultima_modifica, MS.data_ultima_modifica)  AS \"dataUltimaModifica\"" +
                "   ,   COALESCE(MM.ora_ultima_modifica, MS.ora_ultima_modifica)    AS \"oraUltimaModifica\"" +
                "   ,   COALESCE(MM.id_rilevazione, MS.id_rilevazione)              AS \"idRilevazione\"" +
                "   ,   CASE " +
                "           WHEN" +
                "               (SELECT MM.codice" +
                "                   FROM misuramonitoraggio MM" +
                "                   WHERE MM.codice = MST.cod_misura AND MM.id_rilevazione = MS.id_rilevazione) " +
                "               IS NOT NULL THEN true " +
                "           ELSE false " +
                "       END                                                         AS \"dettagli\"" +
                "   FROM misura MS" +
                "       LEFT JOIN misuramonitoraggio MM ON (MS.codice = MM.codice AND MS.id_rilevazione = MM.id_rilevazione)" +
                "       INNER JOIN misura_struttura MST ON (MS.codice = MST.cod_misura AND MS.id_rilevazione = MST.id_rilevazione)" +
                "       INNER JOIN rilevazione S ON MS.id_rilevazione = S.id" +
                "   WHERE MST.id_struttura_liv" + level + " = " + idS +
                        clause +
                "       AND MST.ruolo ILIKE '" + role + "'" +
                "       AND MST.id_rilevazione = " + idR +
                "   ORDER BY MST.ruolo, MS.nome";
        return GET_MEASURE_BY_STRUCT;
    }

}
