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

package it.rol.exception;


/**
 * Questa eccezione viene ritornata quando si tenta di accedere
 * ad un attributo di un Bean che non è stato inizializzato
 * (attributo del Bean non letto dal Database o in generale non valorizzato)
 */
public class AttributoNonValorizzatoException extends Exception {
    /**
     * Necessario in quanto si espande Exception
     */
    private static final long serialVersionUID = 1L;

    
    /**
     * Costruttore da superclasse
     */
    public AttributoNonValorizzatoException() {
        super();
    }

    
    /**
     * Costruttore parametrizzato da superclasse
     * @param message un messaggio da mostare nello stdout (p.es. log o console)
     */
    public AttributoNonValorizzatoException(String message) {
        super(message);
    }

    
    /**
     * @param message   un messaggio da mostare nello stdout (e.g. log o console)
     * @param cause     l\'eccezione catturata, che viene passata alla superclasse tramite l\'incapsulamento in this 
     */
    public AttributoNonValorizzatoException(String message, Throwable cause) {
        super(message, cause);
    }

    
    /**
     * @param cause la sola eccezione catturata, che viene passata alla superclasse tramite incapsulamento in this
     */
    public AttributoNonValorizzatoException(Throwable cause) {
        super(cause);
    }
    
}
