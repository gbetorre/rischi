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

package it.rol.bean;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;


/**
 * <p>BeanUtil.java &egrave; una classe di servizio.</p> 
 * <p>Permette di popolare un Java Data Bean a partire da una riga di un ResultSet 
 * scegliendo, per gli attributi che hanno anche le traduzioni, 
 * i valori secondo le preferenze espresse da un oggetto di
 * tipo Lingue. Inoltre inserisce nell'attributo 'lingua'<nomeAttributo> il
 * codice della lingua del valore inserito in <nomeAttributo>. Gli attributi del
 * Java Data Bean devono avere il medesimo nome delle colonne del ResultSet.</p>
 * 
 * <p>Created: 25/06/2003<br />
 * BeanUtil Copyright (C) 2003 Roberto Posenato 
 * Roberto Posenato  &lt;posenato@sci.univr.it&gt; 
 * Dipartimento di Informatica Università degli Studi di Verona 
 * Strada le Grazie 15 37134 Verona (Italy)
 * </p>
 * 
 * @author Roberto Posenato
 */
public class BeanUtil {

	/**
	 * Rappresenta una coppia di valori stringa
	 */
	static class ValoreELingua {
		String valore, lingua;

		ValoreELingua(String v, String l) {
			valore = v;
			lingua = l;
		}
	}

	/*
	 * L'oggetto log può essere definito a livello di classe All logging goes
	 * through this logger
	 */
	private static Logger log = Logger.getLogger(BeanUtil.class.getName());

	/*
	 * static initializer block Inizializza tutti i parametri per le chiamate ai
	 * convertitori.
	 *
	static {
		// Attivo valori di default per le conversioni di valori
		// nulli secondo la specifica 08
		ConvertUtils.register(new IntegerConverter(new Integer(-1)), Integer.TYPE);
		ConvertUtils.register(new LongConverter(new Long(-1)), Long.TYPE);
		ConvertUtils.register(new DoubleConverter(new Double(-1.0)), Double.TYPE);
		ConvertUtils.register(new FloatConverter(new Float(-1.0)), Float.TYPE);

		// assicuro che gli attributi di tipo Date e Time siano convertiti senza
		// lanciare eccezioni
		ConvertUtils.register(new SqlDateConverter(null), java.sql.Date.class);
		ConvertUtils.register(new SqlTimeConverter(null), java.sql.Time.class);
		//Registro un converter specifico per java.util.Date in modo
		//da poter mappare attributi di tipo timestamp.
		ConvertUtils.register(new DateConverter(null), java.sql.Timestamp.class);
		ConvertUtils.register(new DateConverter(null), java.util.Date.class);
	}*/

	/**
	 * Popola gli attributi di bean che hanno nome uguale a nomi delle attributi
	 * del resultSet con i valori presenti nel resultSet. Per quei attributi che
	 * hanno anche valori in più lingue, la determinazione di quale valore
	 * utilizzare è data dall'ordine delle lingue fornito dall'oggetto lingue.
	 * <br>
	 * Il bean deve rispettare la specifica dei Java Data Bean circa la
	 * composizione dei nomi e dei metodi getter e setter degli attributi. <br>
	 * I nomi degli attributi soggetti a traduzione devono avere il formato
	 * L0-nome per il valore in l'italiano, L1-nome per l valore nella prima
	 * lingua, L2-nome per il valore nella seconda lingua. L'ordine degli
	 * attributi nel resultSet è obbligatoriamente: L0-nome, L1-nome, L2-nome.
	 * 
	 * @param bean
	 *        a JavaDataBean to fill.
	 * @param resultSet
	 *        the result set that contains data.
	 */
	public static void populate(Object bean, ResultSet resultSet) throws SQLException {
		int cols = 0;
		ResultSetMetaData metaData = null;

		try {
			metaData = resultSet.getMetaData();
			cols = metaData.getColumnCount();
		} catch (SQLException emd) {
			throw new SQLException("Errore nell'acquisire MetaData per un ResultSet: " + emd.getMessage());
		}

		log.finest("Numero colonne 'cols': " + cols);

		if (resultSet != null) {
			String columnName = null;
			int columnType = java.sql.Types.NULL;
			Object columnValue = null;
			ValoreELingua valoreELingua = null;

			for (int i = 1; i <= cols; i++) {
				try {
					try {
						columnName = metaData.getColumnName(i);
						columnType = metaData.getColumnType(i);
					} catch (SQLException emd1) {
						throw new SQLException(
								"BeanUtil.populate: errore nell'acquisire nome colonna e suo tipo dal metadata: "
										+ emd1.getMessage());
					}

					// gestione di un attributo normale (cioè senza lingue)
					columnValue = resultSet.getObject(i);

					if (columnValue == null) {
						// Secondo la direttiva 08 il valore null di una
						// colonna di tipo VARCHAR deve essere inserito
						// nell'attributo del bean come stringa ""
						// Per i tipi primitivi, la gestione dei valori di
						// default è demandata al ConvertUtils
						if (columnType == java.sql.Types.VARCHAR)
							columnValue = "";
					}

					BeanUtils.copyProperty(bean, columnName, columnValue);

					if (log.getLevel() == Level.FINEST) {
						log.logp(Level.FINEST, BeanUtil.class.getName(), "populate()", "columnName: '" + columnName
								+ "'\nValore nel result set: '" + resultSet.getObject(i) + "'\nValore nel bean: '"
								+ BeanUtils.getProperty(bean, columnName) + "'");
					}
				} catch (java.lang.IllegalAccessException ie) {
					throw new SQLException("Non è possibile accedere al metodo associato a '" + columnName
							+ "' nel bean di tipo '" + bean.getClass() + "': " + ie.getMessage());
				} catch (java.lang.reflect.InvocationTargetException te) {
					if (te.getMessage() == null) {
						log.logp(Level.SEVERE, "it.univr.di.dol.bean.BeanUtils", "populate()",
								"Il messaggio associato all'eccezione è vuoto, in stderr viene stampato lo StackTrace");
						te.printStackTrace();
					}
					throw new SQLException("Problemi nel settare l'attributo '" + columnName + "' nel bean di tipo '"
							+ bean.getClass() + "': " + te.getMessage());
				} catch (SQLException emd1) {
					throw new SQLException("BeanUtil.populate: errore generico all'interno del ciclo for: "
							+ emd1.getMessage());
				} catch (NoSuchMethodException emd2) {
					// il result set è più ricco del bean... ignoriamo.
					log.logp(Level.INFO, BeanUtil.class.getName(), "populate()", "Il result set contiene la colonna '"
							+ columnName + "' che non è presente nel bean '" + bean.getClass() + "'");
				}
			}
		}
	}

}