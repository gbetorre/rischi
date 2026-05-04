# Policy di Sicurezza

## Destinazione d'uso

Questo software è sviluppato ad uso interno per la Pubblica Amministrazione.

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

Sono in adozione strumenti di scansione automatica (come GitHub Dependabot e CodeQL) per monitorare costantemente le vulnerabilità nelle librerie di terze parti (Supply Chain Security) in conformità con i requisiti della direttiva NIS2.
