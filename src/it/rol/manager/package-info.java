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
 * <p>Package contenente le classi che fanno da manager, ovvero
 * si occupano di compiti specifici, che nell'applicazione 
 * svolgono in maniera esclusiva e non sovrapposta all'azione 
 * di alcuna altra classe.
 * In particolare, abbiamo:
 * <dl>
 * <dt>DBManager</dt>
 * <dd>si occupa di costruire l'opportuna stringa di connessione al database
 * in funzione dell'ambiente di esecuzione; &egrave; indipendente da 
 * qualunque altra classe dell'applicazione.</dd> 
 * </dd>
 * <dt>ConfigManager</dt>
 * <dd>si occupa di recuperare tutti i parametri di configurazione
 * che possono essere utili in vari punti dell'applicazione, e di esporne
 * i valori in variabili di classe che possono essere lette 
 * tramite appositi metodi accessori.</dd> 
 * <dt>SessionManager</dt>
 * <dd>si occupa dell'autenticazione e della gestione delle sessioni utente.</dd>
 * <dt>PasswordGenerator</dt>
 * <dd>generatore di password formate secondo criteri specificati.</dd>
 * </dl>
 * </p> 
 * 
 * <code>Elementi del package:<ul>
 * <li>DBManager</li>
 * <li>ConfigManager</li>
 * <li>SessionManager</li>
 * <li>PasswordGenerator</li>
 * </ul></code>
 * 
 * <p>Created on 30 ‎settembre ‎2024, ‏‎13:32:17</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 * @author (outer-root)
 */
package it.rol.manager;