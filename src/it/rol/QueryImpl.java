/**
 * 
 */
package it.rol;

import java.util.HashMap;
import java.util.LinkedHashMap;

import it.rol.bean.InterviewBean;
import it.rol.exception.AttributoNonValorizzatoException;
import it.rol.exception.WebStorageException;

/**
 * @author outer-root
 *
 */
public class QueryImpl implements Query, Constants {

    
    /**
     * 
     */
    private static final long serialVersionUID = 7339151352217708748L;


    /* (non-Javadoc)
     * @see it.rol.Query#getQueryStructures(int, int, int, int, int)
     */
    @SuppressWarnings("javadoc")
    @Override
    public String getQueryStructures(int idR, int idl4, int idl3, int idl2, int idl1) {
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


    /* (non-Javadoc)
     * @see it.rol.Query#getQueryStructure(int, byte)
     */
    @SuppressWarnings("javadoc")
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

    @Override
    public String getMeasuresByFactors(int idF, int idS, String codeM) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    /* (non-Javadoc)
     * @see it.rol.Query#getMeasureByRiskAndProcess(String, String, String, String, int)
     */
    @SuppressWarnings("javadoc")
    @Override
    public String getMeasureByRiskAndProcess(String idR, String idP, String idS, String codeM, int getAll) {
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

}
