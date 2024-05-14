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
public class QueryImpl implements Query {

    @Override
    public String getQueryStructures(int idR, int idl4, int idl3, int idl2, int idl1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getQueryStructure(int idS, byte level) {
        // TODO Auto-generated method stub
        return null;
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
