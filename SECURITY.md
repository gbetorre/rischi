![en](https://img.shields.io/badge/lang-en-red.svg)

# Security Policy 

## Intended Use

This software is currently being developed for internal use by the Public Administration.

Although the source code is publicly available for transparency and specifically in accordance with the “open source first” principle, the operational instance resides within a protected network perimeter and is not accessible from the outside.

## Supported Versions

Currently, security is actively monitored on the following versions:


| Version | Supported | Notes |
| :--- | :--- | :--- |
| JakartaEE (Latest) | ✅ Under review | Recommended for deployment on Tomcat 10+ |
| JavaEE (Legacy) | ⚠️ Critical patches only | In migration phase |

## Reporting a Vulnerability

If you encounter a vulnerability in the code or configuration, please **do not open a public issue**.

Instead, use one of the following channels for secure reporting (Coordinated Vulnerability Disclosure):

*   **Email:** [giovanroberto.torre@univr.it]
*   **Subject:** Vulnerability Report - [ROL-RMS]

Please include a detailed description of the issue and, if possible, the steps to reproduce the problem. 
We will respond as soon as possible to confirm receipt and discuss next steps.

## Dependency Management

We are adopting automated scanning tools (such as GitHub Dependabot and CodeQL) to continuously monitor any vulnerability in third-party libraries (supply chain security) in accordance with the requirements of the NIS2 Directive.

## NIS 2 Regulation

The project addresses three critical issues identified by the ACN’s audits:

* **Article 21 (_Supply Chain Security_):** automatic dependency inventory.
* **Vulnerability management:** Dependabot sends notifications (along with ready-to-apply patches) as soon as a vulnerability is discovered in a library.
* **Traceability:** Every security update will be documented via a [Pull Request](https://github.com/gbetorre/rischi/pulls?q=is%3Apr+is%3Aclosed), facilitating compliance audits.


---


![it](https://img.shields.io/badge/lang-it-yellow.svg)

# Policy di Sicurezza

## Destinazione d'uso

Questo software &egrave; attualmente sviluppato ad uso interno per la Pubblica Amministrazione.

Sebbene il codice sorgente sia disponibile pubblicamente per trasparenza e in accordo con quanto previsto dal  Codice dell'Amministrazione Digitale (CAD), in particolare dagli artt. 68 e 69 (principio "open source first"), l'istanza operativa risiede in un perimetro di rete protetto e non accessibile dall'esterno.

## Versioni Supportate

Attualmente, la sicurezza viene monitorata attivamente sulle seguenti versioni:


| Versione | Supportata | Note |
| :--- | :--- | :--- |
| JakartaEE (Latest) | ✅ In fase di studio | Consigliata per il deploy su Tomcat 10+ |
| JavaEE (Legacy) | ⚠️ Solo patch critiche | In fase di migrazione |

## Segnalazione di una Vulnerabilità

Se dovessi riscontrare una vulnerabilità nel codice o nella configurazione, ti preghiamo di **non aprire una Issue pubblica**.

Utilizza invece uno dei seguenti canali per una segnalazione protetta (Coordinated Vulnerability Disclosure):

*   **Email:** [giovanroberto.torre@univr.it]
*   **Oggetto:** Segnalazione Vulnerabilità - [ROL-RMS]

Ti preghiamo di includere una descrizione dettagliata del problema e, se possibile, i passaggi per riprodurre l'anomalia. 
Ti risponderemo il prima possibile per confermare la ricezione e discutere i passi successivi.

## Gestione delle Dipendenze

Vengono adottati strumenti di scansione automatica (come GitHub Dependabot e CodeQL) per monitorare costantemente le vulnerabilità nelle librerie di terze parti (Supply Chain Security) in conformità con i requisiti della direttiva NIS2.

## Regolamentazione NIS 2

Il progetto risolve tre punti critici richiesti dai controlli dell'ACN:

* **Articolo 21 (_Sicurezza della catena di approvvigionamento_):** inventario automatico delle dipendenze.
* **Gestione delle vulnerabilità:** Dependabot invia notifiche (e relative patch pronte) appena scoperta una falla in una libreria.
* **Tracciabilità:** agni aggiornamento di sicurezza sarà documentato tramite una [Pull Request](https://github.com/gbetorre/rischi/pulls?q=is%3Apr+is%3Aclosed), agevolando gli audit di conformità.

