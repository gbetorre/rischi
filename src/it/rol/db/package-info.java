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
 *   Copyright (C) 2022-2026 Giovanroberto Torre
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

/**
 * <p>Package contenente le classi che si occupano di:<ul> 
 * <li>effettuare le connessioni al database;</li> 
 * <li>le interfacce che dichiarano le query di vario tipo 
 * (selezione, inserimento, aggiornamento, cancellazione);</li> 
 * <li>le classi che le implementano e si occupano 
 * di generare query dinamiche</li> 
 * <li>e, in generale, tutti gli oggetti che hanno attinenza 
 * con lo strato <code>model</code>.</li></ul></p> 
 * 
 * <code>Elementi logicamente inclusi nel package:<ul>
 * <li><em>Query</em></li>
 * <li>QueryImpl</li>
 * <li>DBWrapper</li>
 * </ul></code>
 * 
 * <p>Created on Mon 21 Oct 2024 11:12:57 AM CEST</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 * @author (outer-root)
 */
package it.rol.db;
