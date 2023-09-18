<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![GPL-2.0 license][license-shield]][license-url]



<!-- PROJECT LOGO -->
<br />
<div align="center">
  <!--
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>
  -->
  <h3 align="center">ROL [Rischi On Line]</h3>

  <p align="center">
    Applicazione web per la mappatura dei rischi corruttivi cui sono esposti i processi organizzativi
    <br />
    <a href="https://github.com/gbetorre/rischi"><strong>Esplora i files »</strong></a>
    <br />
    <br />
    <a href="https://github.com/gbetorre/rischi/issues">Report Bug</a>
    ·
    <a href="https://github.com/pulls">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<!-- Insert TOC here -->

# Rischi
Applicazione web per la mappatura dei rischi corruttivi cui sono esposti i processi organizzativi

<!-- ABOUT THE PROJECT -->

## About The Project

[![Product Name Screen Shot][product-screenshot]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/product-screenshot.png)
<br>
<strong>*Fig.1 - prima versione del software*</strong>

<p>
L'applicazione web per la mappatura dei rischi corruttivi serve ad aiutare i responsabili della trasparenza ad individuare i punti di attenzione nel contesto dei processi organizzativi e ad indirizzarli a proporre le contromisure adeguate.
</p>

[![Product Login Screen Shot][product-login]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/login1.40.png)
<br>
<strong>*Fig.2 - il software &egrave; un'applicazione ad accesso riservato (per accedere &egrave; necessario disporre di credenziali)*</strong>
<br><br>

[![Product Landing Page][product-landing]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing1.40.png)
<br>
<strong>*Fig.3 - pagina di landing, versione 1.40*</strong>

<p>
Attraverso la risposta ad una serie di quesiti posti a responsabili e operatori presso specifiche strutture amministrative (intervista), l'applicazione permette di ottenere una serie di indici relativi a specifici rischi corruttivi cui possono essere esposti i processi organizzativi presidiati dalle strutture stesse.<br>

Ogni quesito, infatti, &egrave; collegato ad uno o pi&uacute; specifici rischi corruttivi; perci&ograve;, in funzione della risposta data dal personale intervistato, l'applicazione esprime specifici indici e punti di attenzione e, in sintesi, calcola il livello di rischio cui il processo esaminato risulta esposto. 
<br><br>

[![Product Interview][product-interview]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/interview-sample.png)
<br>
<strong>*Fig.4 - esempio di quesiti che concorrono a fornire il quadro della vulnerabilit&agrave; di un processo organizzativo*</strong>

In particolare, per ogni porocesso sondato attraverso l'intervista, si ottengono i valori di 7 indicatori di probabilit&agrave; (P) e di 4 indicatori di impatto (I).<br>
Incrociando i valori ottenuti negli indicatori di probabilit&agrave; (P) con quelli ottenuti negli indicatori di impatto (I) si ottiene, per ogni processo organizzativo censito, un indice sintetico <code>P x I</code>, che esprime il livello finale di rischio cui &egrave; esposto il processo stesso.

Collegando i rischi alle (contro)misure, &egrave; possibile ottenere anche una serie di suggerimenti circa le azioni organizzative da mettere in atto al fine di ridurre gli specifici rischi corruttivi individuati.

## Come funziona il software
Ovviamente l'applicazione Rischi On Line (ROL) si appoggia su un database, specificamente un database relazionale di tipo postgreSQL (versione 12 e successive).

In una prima fase viene effettuato il caricamento delle strutture organizzative (organigramma) e quello dei processi organizzativi che vengono prodotti dalle strutture stesse.

Questi caricamenti nel database possono essere effettuati tramite query di inserimento generate automaticamente o tramite ETL ma, allo studio, vi &egrave; una modalit&agrave; di caricamento massivo tramite l'upload di file formattati opportunamente.

Le strutture sono organizzate in un albero con vari livelli mentre i processi sono strutturati in 3 livelli principali (macroprocesso, processo e sottoprocesso). 

[![Product Sample OrgChart][product-orgchart]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-str.png)
<br>
<strong>*Fig.5 - funzione di navigazione dell'organigramma*</strong>
<br><br>
[![Product Sample Macro][product-process]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-pro.png)
<br>
<strong>*Fig.6 - funzione di navigazione dell'albero dei macroprocessi*</strong>

Ogni processo o sottoprocesso (ma non il macroprocesso) pu&ograve; essere a sua volta suddiviso in fasi (o attivit&agrave;). Ad ogni fase possono essere associate una o pi&ugrave; strutture e uno o pi&ugrave; soggetti terzi (che sono entit&agrave; non strutturate in organigramma ma comunque agenti sulla fase del processo).

Il software prevede apposite funzionalit&agrave; di navigazione nell'albero dei macroprocessi ed in quello dell'organigramma (cfr. <a href="https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-str.png">Fig. 5</a> e <a href="https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-pro.png">6</a>), in modo da verificare rapidamente che la mappatura corrisponda a quanto effettivamente presente nell'organizzazione.

Inoltre, per ogni processo viene fornita una pagina di dettaglio, contenente, oltre al livello di rischio cui il processo &egrave; esposto (informazione che &egrave; l'obiettivo principale di tutta l'applicazione), anche tutte le altre informazioni aggregate che riguardano il processo stesso, tra cui: gli input, le fasi, gli output, i rischi ed i fattori abilitanti.

[![Product Sample Process][process-29]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/pro-29.png)
<br>
<strong>*Fig.7 - esempio di pagina di dettaglio di un processo censito a fini anticorruttivi*</strong>

Dopo aver popolato il database con le strutture, i macroprocessi e i loro sottolivelli, si pu&ograve; passare alla fase dell'intervista, che consiste nel rivolgere una serie di quesiti ad una serie di specifiche strutture che presiedono uno specifico processo. La batteria di quesiti &egrave; ampia (pi&uacute; di 150) ma la decisione circa la quali quesiti somministrare pu&ograve; essere stabilita di volta in volta dall'intervistatore, nel senso che tutti i quesiti sono facoltativi e vi sono quesiti pi&uacute; generici, che probabilmente ha senso rivolgere in ogni intervista, e quesiti pi&uacute; specifici, che ha senso somministrare soltanto se si sta prendendo in esame processi molto peculiari. I quesiti sono raggruppati in ambiti di analisi e nel caso di alcune strutture potrebbe anche aver senso omettere i quesiti di interi ambiti di analisi.

Le risposte vengono poi utilizzate per ottenere il valore di una serie di indicatori, come accennato sopra.<br>
Tutti gli indicatori, tranne uno, dipendono dalle risposte ai quesiti, nel senso che il valore ottenuto nell'indicatore viene calcolato tramite un algoritmo che tiene conto delle risposte ottenute.
Vi &egrave; soltanto un indicatore di impatto che non dipende dai quesiti ma dal numero e dalla tipologia di strutture coinvolte nel processo misurato.

Gli algoritmi di calcolo degli indicatori sono tutti diversi tra loro.
<br><br>
[![Product Algorithm][product-algorithm]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/algorithm-P3.png)
<br>
<strong>*Fig.8 - esempio (semplificato) di algoritmo di calcolo di uno specifico indicatore di probabilit&agrave;*</strong>

Come accennato nel paragrafo precedente, tramite ulteriori algoritmi vengono incrociati tutti i valori ottenuti negli indicatori di probabilit&agrave; (indice globale di P) e tutti i valori ottenuti negli indicatori di impatto (indice globale I).

Infine, tramite una classica tabella della Quantitative Risk Analysis, viene calcolato l'indice P x I, o giudizio sintetico, ottenuto per ogni processo censito ed investigato tramite le interviste.

## Sviluppi futuri
Allo stato attuale il software &egrave; gi&agrave; pronto per essere adattato,
con un minimo adeguamento, a qualunque realt&agrave; organizzativa che voglia
effettuare un'analisi dettagliata dei rischi corruttivi cui i processi erogati
dall'organizzazione stessa sono esposti.
<br>

&Egrave; anche possibile stimare, con relativa precisione, quanto tempo &egrave; necessario per customizzare il software in funzione di una specifica realt&agrave; organizzativa.
Infatti, acquisite:
* le dimensioni dell'organizzazione (in particolare, il numero di livelli dell'organigramma ed il numero assoluto di strutture da mappare)
* il numero di livelli e la numerosit&agrave; dei processi prodotti dall'organizzazione stessa,
  
diventa possibile effettuare una stima relativamente accurata del tempo necessario 
affinch&eacute; sia possibile iniziare la campagna di interviste e, conseguentemente, ottenere i risultati dei vari indicatori di rischio e del giudizio sintetico P x I.
<br><br>

---

Vi sono, inoltre, alcune possibili evoluzioni, che potrebbero essere implementate in versioni successive:
* Predisposizione di un cruscotto per i RAT (Referenti Anticorruzione e Trasparenza) per consentire loro di compilare autonomamente le risposte ai quesiti (certificando, automaticamente, i dati inseriti)
* Predisposizione di monitoraggi e reportistica per consentire alla <em>governance</em> di effettuare controlli sugli stati di avanzamento e sui risultati raggiunti tramite il progetto di mappatura dei rischi
* Predisposizione di appositi strumenti di ricerca per consentire all'ufficio trasparenza di ottenere query analitiche sulle interviste effettuate.

<br>
Naturalmente, nessuno strumento informatico &egrave; in grado da solo di ottenere risultati come l'abbassamento dei rischi corruttivi; pertanto ogni approfondimento analitico permesso dal software dovr&agrave; essere esaminato ed interpretato dagli esperti dell'anticorruzione.

Ognuno pu&ograve; sentirsi libero di proporre miglioramenti ed evoluzioni.
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

Questa sezione illustra le principali librerie e tecnologie utilizzate per sviluppare ed eseguire il progetto. Maggiori dettagli sui linguaggi utilizzati si trovano <a href="https://github.com/gbetorre/rischi">qui</a>

* [![Java][Java]][Java-url]
* [![JavaScript][JavaScript]][javascript-url]
* [![EL][EL]][EL-url]
* [![HTML][HTML]][HTML-url]
* [![CSS][CSS]][CSS-url]
* [![SQL][SQL]][SQL-url]
* [![Bootstrap][Bootstrap.com]][Bootstrap-url]
* [![JQuery][JQuery.com]][JQuery-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED 
## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

### Prerequisites

This is an example of how to list things you need to use the software and how to install them.
* npm
  ```sh
  npm install npm@latest -g
  ```

### Installation

_Below is an example of how you can instruct your audience on installing and setting up your app. This template doesn't rely on any external dependencies or services._

1. Get a free API Key at [https://example.com](https://example.com)
2. Clone the repo
   ```sh
   git clone https://github.com/your_username_/Project-Name.git
   ```
3. Install NPM packages
   ```sh
   npm install
   ```
4. Enter your API in `config.js`
   ```js
   const API_KEY = 'ENTER YOUR API';
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES 
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://example.com)_

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTRIBUTING 
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distribuito nei termini della licenza GNU GPL-2.0 License. Consulta <a href="https://github.com/gbetorre/rischi/blob/main/LICENSE">`LICENSE.txt`</a> per ulteriori informazioni.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contatti

Software Engineer: Giovanroberto Torre - [@GianroTorres](https://twitter.com/GianroTorres) - gianroberto.torre@gmail.com

Project Link: [https://github.com/gbetorre/rischi](https://github.com/gbetorre/rischi)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## History

Questa sezione illustra l'evoluzione del software ROL nel contesto delle varie release.<br />
In corrispondenza di ogni numero di versione non vengono descritte tutte le modifiche 
effettuate ma solo i rilasci delle funzionalit&agrave; pi&uacute; significative.<br />
Ogni numero di versione &egrave; per&ograve; corredato della data del commit 
dei sorgenti, per cui consultando la History del repository sar&agrave; facile
entrare nel merito di tutte le modifiche effettuate in corrispondenza della
sottoversione: inoltre, ogni versione corrisponde ad un commit, ma non ogni commit
genera una versione.<br />

<p style="font-size:small">
NOTA: Per convenzione, nel software la versione viene mostrata in formato x.xx
quindi accorpando la cifra della sub-sub versione a quella della subversione,
mentre in questo changelog ha il classico formato x.x.x (ci&ograve;
per apportare maggiore precisione descrittiva).<br />
Inoltre, il significato delle subversioni &egrave; abbastanza differente da
quello generale; infatti non identifica se la versione sia stabile o meno
(aspetto solitamente identificato rispettivamente dalla cifra finale diversa da zero o uguale a zero)
n&eacute; presenta salti rilevanti in funzione di modifiche di elevato impatto
(p.es. passaggio dalla versione 6.1.38 alla versione 7.0.1 di VirtualBox, che ha
segnato una modifica abbastanza grossa); nel caso dell'applicazione corrente, infatti,
i numeri di versione hanno solo il signficato di tenere traccia dei rilasci 
e dei deploy che sono stati effettuati 
(1.1.9 = XIX deploy; 1.2.0 = XX deploy; 1.9.9 = IC deploy; 2.0.0 = C deploy)
e fornirvi il relativo significato e la relativa motivazione.
</p>

<!--
### ToDo (Roadmap)
- Aggiunta pagina che mostra i processi erogati dalla struttura evocata in maniera asincrona al clic sul nodo di una struttura
- Implementata estrazione di tutti i dati dell'organigramma (query organigramma e strutture - estrazione)
- Implementata pagina di dettaglio soggetto contingente/interessato
- Implementata pagina di dettaglio struttura
- [1.5.9] Implementate reportistiche e grafici sul rischio in rapporto alla struttura
- [1.5.8] Aggiunta pesatura dei quesiti in funzione del rischio (associazione quesito / rischio)
- [1.5.7] Implementata estrazione risultati in formato CSV
- [1.5.6] Implementata ricerca per ambito di analisi | Implementata ricerca per processo | Implementata ricerca per struttura
- [1.5.5] Aggiunti suggerimenti asincroni sulla digitazione della chiave testuale
- [1.5.4] Implementata form di ricerca sui quesiti per chiave testuale
- [1.4.5] (  /09/2023) Implementata la logica di calcolo degli indicatori P1, P2, P3, P4, P5, P6, P7, I1, I2, I3, I4 nel contesto del singolo processo
- [1.4.4] (25/09/2023) Implementata la logica di calcolo degli indicatori P1, P2, P3, P4, P5, P6, P7, I1, I2, I3, I4, P, I, PxI nel contesto della singola intervista

### Done
-->
- [1.4.3] (18/09/2023) Implementata la logica di calcolo (da perfezionare) degli indicatori P1, P2, P3 nel contesto della singola intervista
- [1.4.2] (12/09/2023) Corretta gestione charset nella form di modifica della risposta; mostrato id della fase come title nella pagina di dettaglio del processo
- [1.4.1] (11/09/2023) Aggiunta gestione in intervista del tipo di quesito che prevede come risposta una percentuale; aggiunta di immagini svg come marcatori di alberatura; revisione pagina di landing; spostamento di alcune ricerche predefinite dalla pagina di landing alla pagina della ricerca libera; ampliata larghezza visibile applicazione. 
- [1.4.0] (04/09/2023) Correzione etichetta, tipografia; aggiunti screenshot.
- [1.3.9] (29/08/2023) Implementata pagina consultazione intervista per nascondere/mostrare le domande senza risposta. Migliorie grafiche di presentazione dei dettagli di un processo; revisione grafica pagina di login, link per scaricare csv, etichette e altri ornamenti.
- [1.3.8] (24/08/2023) Implementata facility in consultazione intervista per nascondere/mostrare le domande senza risposta.
- [1.3.7] (01/08/2023) Implementata funzione inserimento relazione ternaria tra rischio corruttivo e fattore abilitante, nel contesto di un processo. Correzione di bug.
- [1.3.6] (24/07/2023) Implementato registro dei fattori abilitanti i rischi; mostrati fattori abilitanti collegati a rischi nel contesto di un processo
- [1.3.5] (26/06/2023) Implementata funzione inserimento relazione tra rischio corruttivo e processo censito dall'anticorruzione
- [1.3.4] (27/03/2023) Delegata gestione interviste (visualizzazione, inserimento, aggiornamento risposte) a un nuovo componente sofware gemmato dalla Command dei rischi
- [1.3.3] (06/03/2023) Implementata funzione di aggiunta di un rischio corruttivo al registro dei rischi
- [1.3.2] (28/02/2023) Aggiunta pagina dettagli output, elencante i processi generati a partire dall'output corrente laddove esso abbia agito da input di processo. Implementata pagina di elenco output. Correzione di bug.
- [1.3.1] (15/02/2023) Aggiunta funzionalit&agrave; di download registro dei rischi in formato CSV (estrae solo rischi con processi associati, come da regole di business)
- [1.3.0] (13/02/2023) Aggiunta pagina dettagli rischio corruttivo, elencante i processi esposti al rischio selezionato. Implementata pagina stand-alone dettagli processo. Riordinata pagina di landing.
- [1.2.9] (08/02/2023) Piccole migliorie di presentazione nei dettagli di un processo: evidenziata area di rischio, mostrato dettaglio soggetto contingente
- [1.2.8] (06/02/2023) Ampliato dettaglio processo anticorruttivo: mostrati i rischi cui il processo &egrave; esposto
- [1.2.7] (30/01/2023) Implementato registro dei rischi corruttivi; correzione di bug
- [1.2.6] (21/12/2022) Aggiunta funzionalit&agrave; di download dettagli specifico processo in formato CSV
- [1.2.5] (13/12/2022) Aggiunti estremi input, fasi, output nel CSV di estrazione generale dei processi
- [1.2.4] (01/12/2022) Mostrati dettagli di un processo in una finestra separata a fini di stampa in pdf
- [1.2.3] (29/11/2022) Implementati primi esempi di generazione grafici
- [1.2.2] (23/11/2022) Aggiunta funzionalit&agrave; di download albero completo processi in formato CSV
- [1.2.1] (22/11/2022) Mostrato in nodo albero processi numero di input e fasi
- [1.2.0] (21/11/2022) Implementato questo file di documentazione
- [1.1.9] (17/11/2022) Aggiunto dettaglio processo anticorruttivo: input, fasi, output

- [&le; 1.1.9] Implementata intervista (scelta struttura e processo anticorruttivo, compilazione risposte ai quesiti), pagina di elenco interviste, estrazioni varie in CSV, organigramma ed elenco processi in albero navigabile

<!--
- [1.1.9] Add Additional Templates w/ Examples
- [ ] Add "components" document to easily copy & paste sections of the readme
- [ ] Multi-language Support
    - [ ] Chinese
    - [ ] Spanish
-->

Vedi anche [open issues](https://github.com/gbetorre/rischi/issues) per una lista completa di funzionalit&agrave; proposte (e problemi conosciuti).

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=for-the-badge
[contributors-url]: https://github.com/othneildrew/Best-README-Template/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/othneildrew/Best-README-Template.svg?style=for-the-badge
[product-screenshot]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/product-screenshot.png
[product-login]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/login1.40.png
[product-landing]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing1.40.png
[product-interview]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/interview-sample.png
[product-algorithm]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/algorithm-P3.png
[product-orgchart]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-str.png
[product-process]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-pro.png
[process-29]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/pro-29.png
[process-45]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/pro-45.png
[issues-shield]: https://img.shields.io/github/issues/othneildrew/Best-README-Template.svg?style=for-the-badge
[issues-url]: https://github.com/othneildrew/Best-README-Template/issues
[license-shield]: https://img.shields.io/badge/license-GPL-blue
[license-url]: https://github.com/gbetorre/rischi/blob/main/LICENSE
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/othneildrew
[product-screenshot]: images/screenshot.png
[Java]: https://img.shields.io/badge/linguaggio-java-red
[Java-url]: https://www.java.com/it/
[JavaScript]: https://img.shields.io/badge/linguaggio-javascript-green
[JavaScript-url]: https://www.javascript.com/
[HTML]: https://img.shields.io/badge/linguaggio-html-blue
[HTML-url]: https://www.w3.org/html/
[EL]: https://img.shields.io/badge/Expression-Language-yellow
[EL-url]: https://docs.oracle.com/javaee/5/jstl/1.1/docs/tlddocs/index.html
[Angular.io]: https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white
[Angular-url]: https://angular.io/
[CSS]: https://img.shields.io/badge/linguaggio-CSS-orange
[CSS-url]: https://www.w3.org/Style/CSS/Overview.en.html
[SQL]: https://img.shields.io/badge/linguaggio-SQL-lime
[SQL-url]: https://www.w3schools.com/sql/
[Bootstrap.com]: https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[JQuery.com]: https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white
[JQuery-url]: https://jquery.com 
