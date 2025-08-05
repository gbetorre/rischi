<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->

### Lingue
[![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/gbetorre/rischi/blob/master/README.md)
[![it](https://img.shields.io/badge/lang-it-yellow.svg)](https://github.com/gbetorre/rischi/blob/master/README.it.md)

---

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
  <h3 align="center">ROL-RMS</h3>

  <p align="center">
    Applicazione web per la mappatura e il monitoraggio dei rischi corruttivi cui sono esposti i processi organizzativi
    <br><br>
    <a href="https://github.com/gbetorre/rischi"><strong>Esplora i files »</strong></a>
    <br><br>
    <a href="https://github.com/gbetorre/rischi/issues">Report Bug</a>
    ·
    <a href="https://github.com/gbetorre/rischi/pulls">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<!-- Insert TOC here -->

# Software di Mappatura dei Rischi corruttivi [ROL-RMS]

L'applicazione web per la mappatura dei rischi corruttivi <code>ROL-RMS</code> serve ad aiutare Enti, Pubbliche Amministrazioni, aziende partecipate &ndash; e chiunque sia interessato a monitorare e gestire il rischio corruttivo &ndash; <strong>a mappare in modo semplice i processi organizzativi, a quantificare automaticamente i rischi corruttivi</strong> cui gli stessi risultano esposti e ad indirizzare i soggetti a mettere in atto <strong>le contromisure adeguate</strong>.

[![Product Landing Page][product-landing4]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing2.27.png)
<br>
<strong>*Fig.1 - Pagina di landing, versione 2.2.7*</strong><br>
<br>
<!-- ABOUT THE PROJECT -->

## About The Project

[![Goal Sample][indicator-sample01]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample01.png)
<br>
<strong>*Fig.2 - Uno degli obiettivi del software &egrave; quantificare, in automatico, il valore di rischio per ogni processo organizzativo considerato (dati fittizi&#770;)*</strong><br>

### In sintesi
In breve, il software <code>ROL-RMS</code> permette di:
* quantificare il livello di rischio corruttivo cui &egrave; esposto ogni processo organizzativo (<strong>livello di rischio iniziale</strong>);
* quantificare quanto si riduce tale livello di rischio se una serie di misure di mitigazione viene applicata al processo (<strong>livello di rischio stimato</strong>);
* quantificare quanto si &egrave; effettivamente ridotto il livello di rischio, date le misure di mitigazione che sono state effettivamente applicate (<strong>livello di rischio effettivo</strong>).

Tutte queste quantit&agrave; (livello di rischio iniziale, stimato ed effettivo) sono numeriche e determinate tramite algoritmi deterministici, quindi non soggette a variazioni stocastiche.

In altri termini, dato un livello di rischio iniziale, applicando determinate misure si otterra <i>sempre</i> una determinata riduzione del livello di rischio e sar&agrave; anche ricostruibile il processo tramite cui &egrave; stata determinata tale riduzione. In tal senso, l'explainability di tutto questo software &egrave; completa (e anche accessibile tramite la lettura degli stessi sorgenti pubblicati in questo repository).

Gli algoritmi di mitigazione - come peraltro tutti gli algoritmi di calcolo del rischio, di calcolo del <code>PxI</code> (quest'ultimo verr&agrave; definito [nel prossimo capitolo](#overview)), etc. - sono stati disegnati in base al know-how di personale esperto del rischio corruttivo e sono stati formalizzati completamente in fase di analisi prima di passare alla fase di implementazione. 

[![Dashboard Graphics][dashboard-graph]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-graphics.png)
<br>
<strong>*Fig.3 - Effettuando la quantificazione dei fattori coinvolti, diventa possibile produrre in modo semplice reportistiche aggregate, anche sotto forma di infografiche  (dati fittizi&#770;)*</strong><br>

### L'approccio valutativo
L'enfasi posta finora sulla quantificazione dei valori in gioco potrebbe indurre a credere che l'approccio utilizzato per stimare l'esposizione dell'organizzazione ai rischi sia esclusivamente di tipo quantitativo: ma cos&iacute; non &egrave;.<br>
La determinazione quantitativa dei livelli di rischio non esaurisce, infatti, le funzionalit&agrave; del software, che permette di accompagnare, ed arricchire, gli indici e i valori quantificati anche con valutazioni qualitative.<br>
L'approccio valutativo scelto, pur non essendo esclusivamente di tipo qualitativo, non &egrave; neppure esclusivamente di tipo quantitativo: si tratta, infatti, di <strong>un approccio valutativo di tipo misto.</strong>

[![Samples][judgements-sample]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/giudizi-sintetici.png)
<br>
<strong>*Fig.4 - Nel software ROL-RMS le valutazioni qualitative (giudizi sintetici) accompagnano sempre i valori quantitativi, in tutti i tipi di report (dati fittizi&#770;)*</strong><br>

Nell'allegato 1 al Piano Nazionale Anticorruzione del 2019, l'Autorit&agrave; Nazionale Anticorruzione ha specificato che la valutazione qualitativa non va messa in secondo piano rispetto alla valutazione quantitativa, per una serie di motivi e, in primis:
* mancanza di serie storiche;
* mancanza di competenze specialistiche;
* carenza di risultati positivi in sede di monitoraggio.

In estrema sintesi, quindi, dopo il 2019 l'ottica di ANAC si &egrave; spostata da una valutazione puramente quantitativa ad una principalmente qualitativa.
Nell'analisi del software ROL-RMS si &egrave; considerato, tuttavia, che un approccio di tipo misto permettesse di fruire dei pregi dei due mondi: quello qualitativo e quello quantitativo. 
Infatti:
* gli elementi qualitativi (i giudizi sintetici) arricchiscono i dati numerici con una chiave di lettura interpretativa, che illumina meglio la scena e il contesto;
* i valori quantitativi (indicatori di rischio, indici, monitoraggi...), dal canto loro, permettono di ottenere dati immediatamente comprensibili e comparabili, potenzialmente anche da un'amministrazione all'altra. 
Inoltre, l'elevata automazione resa possibile dal linguaggio imperativo con cui il software &egrave; realizzato (Java), non obbliga a lunghe ricognizioni e pesature per ottenere i valori numerici, ma facilita enormemente il compito di calcolo.

Per questi motivi, l'approccio valutativo misto &egrave; stato ritenuto il migliore per lo sviluppo del software <code>ROL-RMS</code>.

### Il software in pratica
Il workflow generale &egrave; suddiviso in 4 distinti step, o filoni di lavoro:
* Step 1: caricamento di strutture e processi <strong>(mappatura dell'organizzazione)</strong>
* Step 2: <strong>calcolo del rischio</strong> corruttivo di ogni processo (accompagnato dalla formulazione di un giudizio sintetico adeguatamente motivato per ogni processo)
* Step 3: <strong>individuazione delle misure</strong> di mitigazione da applicare ad ogni processo
* Step 4: <strong>monitoraggio</strong> al fine di verificare se le misure previste sono state applicate.

Questi 4 step sono pensati entro un flusso sincrono, ovvero per essere portati a compimento in sequenza, non in parallelo.<br> 
Ad esempio, non si pu&ograve; passare allo Step 2 (calcolo del rischio) se non &egrave; stato completato lo Step 1 (mappatura dei processi); analogamente, non si pu&ograve; passare allo Step 3 (individuazione delle misure) se non &egrave; stato completato lo Step 2; e cos&iacute; via.<br>
Questa modalit&agrave; "lineare" guida gli attori nel processo di mappatura e gestione e permette di gestire, in modo semplificato, la complessit&agrave; del dominio informativo e dell'obiettivo globale che si vuol realizzare: ovvero la riduzione, quantificabile, comprovabile e scientificamente fondata, dei livelli di rischi corruttivi cui i processi organizzativi sono esposti.<br>
<br>
Alla fine del 4° Step, sar&agrave; stata realizzata una rilevazione completa del monitoraggio e del trattamento del rischio corruttivo in organizzazione.
<br>
A questo punto, si pu&ograve; iterare il processo, procedendo con una nuova rilevazione: <strong>il sistema &egrave; predisposto</strong>, infatti, <strong>per la storicizzazione.</strong>
<br>
Ogni rilevazione successiva potr&agrave; essere messa a confronto con la precedente attraverso specifici cruscotti multirilevazione, che permetteranno di analizzare i delta e i trend relativi ai processi e ai relativi rischi corruttivi, da una rilevazione all'altra.

## Overview

[Nel prossimo capitolo](#come-funziona-il-software) verranno esaminati pi&uacute; in dettaglio i vari step.<br>
Nel presente paragrafo viene data, invece, una descrizione a grandi linee del workflow generale dal punto di vista delle azioni messe in atto per realizzare l'obiettivo generale.

Anzitutto, &egrave; opportuno definire i <strong>soggetti</strong> coinvolti:
1. L'esperto o l'ufficio anticorruzione
2. I responsabili e gli operatori degli uffici
3. Il software engineer

[![Product Login Screen Shot][product-login2]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/login2.25.png)
<br>
<strong>*Fig.5 - Il software &egrave; un'applicazione ad accesso riservato (per accedere &egrave; necessario disporre di credenziali). Pertanto &egrave; possibile determinare il profilo utente all'accesso al sistema.*</strong>
<br><br>

Rispetto ai <strong>ruoli</strong> svolti: 
1. <strong>L'esperto</strong> di anticorruzione, con l'aiuto del software: 
  * effettua il calcolo del rischio, 
  * stabilisce quali misure di mitigazione applicare ai processi pi&uacute; a rischio e 
  * ne cura il monitoraggio.
2. <strong>Il personale</strong> degli uffici che sovrintendono i processi risponde ai quesiti dell'intervista e fornisce i valori raccolti nel monitoraggio.
3. <strong>Il software engineer</strong> cura la fase di mappatura dei processi e coadiuva gli altri soggetti attraverso tutto il workflow.

Consultando la mappatura dei processi, effettuata nello Step 1 ([v. paragrafo precedente](#about-the-project)), si diviene in grado di stabilire l'elenco delle strutture organizzative coinvolte nell'erogazione dei relativi processi.
A quel punto, &egrave; possibile quindi rivolgere una serie di quesiti a responsabili ed operatori ubicati presso tali strutture, in merito ai processi prodotti dalle strutture stesse.
Attraverso l'analisi delle risposte a tali quesiti, l'applicazione permette di ottenere, automaticamente, una serie di indici relativi a specifici rischi corruttivi cui risultano esposti i processi organizzativi presidiati dalle strutture stesse.<br>


<p>
Ogni quesito, infatti, &egrave; collegato ad uno o pi&uacute; specifici rischi corruttivi; perci&ograve;, in funzione della risposta data dal personale intervistato, l'applicazione esprime specifici indici e punti di attenzione e, in sintesi, calcola il livello di rischio cui il processo esaminato risulta esposto. 
<br><br>

[![Product Interview][product-interview]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/interview-sample.png)
<br>
<strong>*Fig.6 - Esempio di quesiti che concorrono a fornire il quadro della vulnerabilit&agrave; di un processo organizzativo*</strong>

In particolare, per ogni processo sondato attraverso l'intervista, si ottengono i valori di 7 indicatori di probabilit&agrave; (P) e di 4 indicatori di impatto (I).
<br>

[![Goal Sample alt][indicator-sample02]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample02.png)
<br>
<strong>*Fig.7 - Le risposte ai quesiti considerati per il calcolo dell'indicatore potrebbero, occasionalmente, non permettere di ottenere il valore di rischio nella dimensione considerata (dati fittizi&#770;)*</strong>
<br><br>

[![Sample alt][indicator-sample03]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample03.png)
<br>
<strong>*Fig.8 - In tali casi il software riporta il motivo del mancato calcolo; se i motivi sono molteplici, vengono mostrati uno per volta finch&eacute; il problema non viene corretto (dati fittizi&#770;)*</strong>
<br><br>

Incrociando i valori ottenuti negli indicatori di probabilit&agrave; (P) con quelli ottenuti negli indicatori di impatto (I) si ottiene, per ogni processo organizzativo censito, un indice sintetico <code>P x I</code>, che esprime il livello iniziale di rischio cui &egrave; esposto il processo stesso.

Collegando i rischi alle (contro)misure, &egrave; possibile ottenere anche una serie di suggerimenti circa le azioni organizzative da mettere in atto al fine di ridurre gli specifici rischi corruttivi individuati.

# Come funziona il software

Ovviamente l'applicazione <strong>Rischi On Line: Risk Mapping Software <code>(ROL-RMS)</code></strong> si appoggia su un database, specificamente un database relazionale di tipo PostgreSQL (versione 12 e successive), in cui sono popolati i quesiti che verranno sottoposti alle strutture nelle interviste (e tutte le altre informazioni persistenti).

[![DB representation, layout circular][schema-physical]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/DB-circular.png)
<br>
<strong>*Fig.9 - Rappresentazione grafica delle entit&agrave; e relazioni dello schema, Layout: Circular (powered by yFiles)*</strong>
<br><br>

## Step 1: Individuazione del contesto (mappatura organizzativa)
In una prima fase viene effettuato il caricamento delle strutture organizzative (organigramma) e quello dei processi organizzativi che vengono prodotti dalle strutture stesse.

Questi caricamenti nel database possono essere effettuati tramite query di inserimento generate automaticamente o tramite ETL ma, allo studio, vi &egrave; una modalit&agrave; di caricamento massivo tramite l'upload di file formattati opportunamente.

Le strutture sono organizzate in un albero con vari livelli mentre i processi sono strutturati in 3 livelli principali (macroprocesso, processo e sottoprocesso). 

[![Product Sample OrgChart][product-orgchart]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-str.png)
<br>
<strong>*Fig.10 - Funzione di navigazione dell'organigramma*</strong><br><br>

[![Product Sample Macro][product-process]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-pro.png)
<br>
<strong>*Fig.11 - Funzione di navigazione dell'albero dei macroprocessi*</strong>

Come &egrave; noto in letteratura sulla mappatura dei processi, vi sono svariate tassonomie che &egrave; possibile adottare per classificare e gerarchizzare i processi organizzativi.<br> 
Nel presente software si è optato per la seguente strutturazione gerarchica:
<pre>
* Area di Rischio
    * |_ Macroprocesso
       *   |_  Processo
           *     |_ Sottoprocesso
</pre>

Tutte queste entit&agrave; sono legate tra loro da relazioni di composizione (&ldquo;aggregazione forte&rdquo;, o aggregazione compositiva).<br>
L'area di rischio &egrave; il livello pi&uacute; generale: essa ha poche propriet&agrave; e aggrega i macroprocessi, i quali, a loro volta, aggregano i processi, e cos&iacute; via.

[![Class Diagram part Process][class-diagram]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/class-diagram.png)
<br>
<strong>*Fig.12 - Diagramma delle classi relativo alle entit&agrave; coinvolte nella rappresentazione dei processi.*</strong>

Ogni processo o sottoprocesso (ma non il macroprocesso) pu&ograve; essere a sua volta suddiviso in fasi (o attivit&agrave;). Ad ogni fase possono essere associate una o pi&ugrave; strutture e uno o pi&ugrave; soggetti terzi (questi ultimi sono entit&agrave; non strutturate in organigramma ma comunque agenti sulla fase del processo).

Il software prevede apposite funzionalit&agrave; di navigazione nell'albero dei macroprocessi ed in quello dell'organigramma (cfr. <a href="https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-str.png">Fig. 10</a> e <a href="https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-pro.png">11</a>), in modo da verificare rapidamente che la mappatura corrisponda a quanto effettivamente presente nell'organizzazione.

Inoltre, per ogni processo viene fornita una pagina di dettaglio, contenente, oltre ai rischi ed ai livelli di rischio cui il processo &egrave; esposto (informazione di grande interesse dato lo scopo del software), anche tutte le altre informazioni aggregate che riguardano il processo stesso, tra cui: gli input, le fasi, gli output ed i fattori abilitanti.

[![Product Sample Process][process-29]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/pro-29.png)
<br>
<strong>*Fig.13 - Esempio di pagina di dettaglio di un processo censito a fini anticorruttivi ma non ancora investigato tramite intervista &ndash; per il quale, quindi, non &egrave; stato ancora possibile determinare i livelli di rischio*</strong>

## Step 2: Calcolo del rischio (interviste e indicatori di rischio)
Dopo aver popolato il database con le strutture, i macroprocessi e i loro sottolivelli, si pu&ograve; passare alla fase <i>delle interviste</i>, che consiste nel rivolgere una serie di quesiti ad una serie di specifiche strutture che presiedono uno specifico processo. 

La batteria di quesiti &egrave; ampia (pi&uacute; di 150) ma la decisione circa quali quesiti somministrare pu&ograve; essere stabilita di volta in volta dall'intervistatore, nel senso che tutti i quesiti sono facoltativi e vi sono quesiti pi&uacute; generici, che probabilmente ha senso rivolgere in ogni intervista, e quesiti pi&uacute; specifici, che ha senso somministrare soltanto se si sta prendendo in esame processi molto peculiari. 
I quesiti sono raggruppati in <strong>ambiti di analisi</strong> e, nel caso di alcune strutture, potrebbe anche aver senso omettere i quesiti di interi ambiti di analisi.<br>

[![Question domains sample][question-domains]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/questions-domains.png)
<br>
<strong>*Fig.14 - Esempio di raggruppamenti di quesiti in ambiti di analisi*</strong>

Le risposte vengono poi utilizzate per ottenere il valore di una serie di indicatori di rischio, come accennato in precedenza.<br>

[![PxI analytical dashboard][dashboard-risk]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-pxi.png)
<br>
<strong>*Fig.15 - Il cruscotto degli indicatori permette di consultare non solo il valore del PxI di ogni processo ma anche i valori di tutte le dimensioni e gli indicatori in base ai quali questo indice sintetico &egrave; stato calcolato*</strong>

<strong>Il calcolo dei valori di tutti gli indicatori e dello stesso indice PxI di ogni processo &egrave; automatizzato</strong> nel senso che, nel momento in cui l'intervista viene salvata, in automatico viene processato il calcolo del valore di tutti gli indicatori e del PxI.
Tutti gli indicatori &ndash; tranne uno &ndash; dipendono infatti dalle risposte ai quesiti, nel senso che il valore ottenuto nell'indicatore viene calcolato tramite un algoritmo che tiene conto delle risposte ottenute.<br>
Vi &egrave; soltanto un indicatore di impatto che non dipende dai quesiti ma dal numero e dalla tipologia di strutture coinvolte nel processo misurato. Anche il valore di questo viene calcolato automaticamente.

Gli algoritmi di calcolo degli indicatori sono tutti diversi tra loro.
<br><br>
[![Product Algorithm][product-algorithm]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/algorithm-P3.png)
<br>
<strong>*Fig.16 - Esempio (semplificato) del flowchart dell'algoritmo di calcolo di uno specifico indicatore di probabilit&agrave; (P3: analisi/valutazione delle segnalazioni ricevute)*</strong><br>

Come accennato nel paragrafo precedente, tramite ulteriori algoritmi vengono incrociati tutti i valori ottenuti negli indicatori di probabilit&agrave; (indice globale di probabilit&agrave; <code>P</code>) e tutti i valori ottenuti negli indicatori di impatto (indice globale di impatto <code>I</code>).

Infine, tramite una classica tabella della Quantitative Risk Analysis, viene calcolato l'indice  <code>P x I</code>, o giudizio sintetico, ottenuto per ogni processo censito ed investigato tramite le interviste.<br>

[![PxI][pxi]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/PxI.png)
<br>
<strong>*Fig.17 - Tabella di decisione dell’algoritmo per il calcolo del PxI, con i 9 valori possibili derivanti dalle disposizioni con ripetizione D'(3,2) = 3<sup>2</sup> dei 3 valori possibili del P e dei 3 valori possibili di I.*</strong>

><strong>&Egrave; importante sottolineare che una feature del software consiste dunque nell'automazione del calcolo degli indicatori e del PxI: dopo aver censito processi e strutture, &egrave; sufficiente effettuare le interviste per far s&iacute; che il software faccia il resto.</strong>

## Step 3: Trattamento del rischio (misure di mitigazione, stima)
Tramite le fasi 1 e 2 (ovvero: mappatura dei processi e calcolo del rischio corruttivo degli stessi) si ottiene dunque una visione complessiva sul livello di rischio cui ogni processo organizzativo censito risulta esposto.

[![PxI concise dashboard][dashboard-risk2]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-synthesis-pxi.png)
<br>
<strong>*Fig.18 - La tabella del PxI totalizzato da ogni processo fornisce una visione d'insieme sui livelli di rischio cui sono esposti i processi organizzativi*</strong>

Aver realizzato questa mappatura costituisce un buon punto di partenza per poter stabilire quali misure di mitigazione/prevenzione del rischio corruttivo &egrave; opportuno applicare ai rischi stessi.
Quest'ultima &egrave; la fase 3, ovvero <strong>la fase di individuazione delle misure di mitigazione atte a ridurre il valore del rischio.</strong>

Come si definisce una misura di mitigazione?
Le misure di mitigazione del rischio corruttivo corrispondono ad azioni atte a:
contenere / calmierare / mitigare / prevenire / trattare / ridurre
il rischio corruttivo, a seconda della tipologia e dello scopo della misura stessa.<br>
(Notare che, generalmente, si pu&ograve; considerare la definizione data in implicazione doppia, <code>sse</code> 
vale a dire: <ul>
<li> una misura di mitigazione M &egrave; un'azione che, applicata a un processo, ne riduce il rischio: M &rarr; rr </li>
<li> &and;</li>
<li> se il rischio di un processo &egrave; ridotto da una certa azione, allora quell'azione &egrave; una misura di mitigazione : rr &rarr; M</li></ul> 
da cui <code>M &harr; rr</code>).<br><br>

Senza entrare nei dettagli implementativi dell'entit&agrave; misura, i cui attributi e riferimenti vengono approfonditi nell'<a href="#readme-contact">analisi dei requisiti</a>, basti tener conto che la misura di mitigazione &egrave; un oggetto complesso, avente uno o pi&uacute; tipologie, parecchie relazioni con le strutture organizzative e una serie di propriet&agrave; specifiche (la sostenibilit&agrave; economica, il carattere, il numero di fasi di attuazione, etc.)</p>

[![Form to insert new measure][add-measure]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/form-measure.png)
<br>
<strong>*Fig.19 - La maschera per inserire una nuova misura di mitigazione del rischio corruttivo*</strong>

In un primo step, quindi, l'ufficio &ndash; o l'esperto &ndash; anticorruzione si occupa di censire tutte le varie misure che ritiene opportuno suggerire, andando a costituire un registro delle misure.
[![List of inserted measure][list-measures]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/list-measures.png)
<br>
<strong>*Fig.20 - Il registro delle misure di mitigazione del rischio corruttivo*</strong>

Dopo aver costituito questa lista di misure applicabili, il problema di chi si occupa di anticorruzione &egrave; andare a individuare quale o quali misure applicare a quale specifico rischio in quale specifico processo.
La granularit&agrave; delle associazioni tra processo e misura &egrave; infatti relativamente fine e per essere rappresentata ha bisogno di una relazione ternaria.<br>

[![Schema ER measure (part)][schema-measure]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/SchemaER-measure.png)
<br>
<strong>*Fig.21 - Parte del diagramma ER per la rappresentazione delle misure*</strong>

Quello che accade in pratica, quindi, &egrave; che, partendo dall'analisi dei livelli di rischio cui sono esposti i processi (fotografata dalla dashboard dei PxI: v. p.es. <a href="https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-synthesis-pxi.png">Fig. 18</a>) l'esperto decide che &egrave; opportuno che l'organizzazione metta in atto opportune misure.

Quali misure, per&ograve;, scegliere, tra le varie misure possibili? Ovvero: come individuare le misure migliori per ogni rischio di ogni dato processo?<br>
Anche qui viene in aiuto il sistema <code>ROL-RMS</code>: 

><strong>uno dei vantaggi offerti dal software, su questo versante, &egrave; il fatto che il sistema stesso suggerisce quali misure applicare a ciascun rischio nel contesto di ciascun processo!</strong> 

[![Assignment measure to risk][assign-measure]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/form-measure2.png)
<br>
<strong>*Fig.22 - La maschera per l'assegnazione di una misura a una coppia rischio-processo. Le misure pi&uacute; appropriate vengono suggerite dal software ma l'operatore &egrave; libero di assegnarne altre, in aggiunta o in sostituzione di quelle suggerite.*</strong>

Le misure di mitigazione, infatti, tramite la loro tipologia, hanno un'associazione con il fattore abilitante e questa relazione rende possibile individuare il contesto di applicazione delle misure stesse in funzione del rischio e del processo.

Riepilogando: il software propone un insieme di misure che, sulla base dell'informazione di cui dispone internamente, sono appropriate per il rischio considerato entro il processo considerato. 
Nulla vieta per&ograve; di assegnarne altre in aggiunta o in sostituzione di quelle proposte.

Una volta applicate le misure, &egrave; possibile verificare come variano i livelli di rischio attraverso la consultazione di appositi cruscotti, che mettono a confronto il PxI prima e dopo l'applicazione delle misure stesse.<br>

[![How to risk decrease applying measures][dashboard-risk4]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-synthesis-measures.png)
<br>
<strong>*Fig.23 - Tabella che mostra quali sono le misure da applicare ad ogni processo e come varierebbero i livelli del PxI prima e dopo l'applicazione. Nella schermata considerata, si sono avute riduzioni di rischio e un livello che, invece, &egrave; rimasto inviariato.*</strong>

## Step 4: Certificazione del rischio (misure di mitigazione, monitoraggio)
La fase di applicazione delle misure, appena vista, &egrave; per&ograve; soltanto una <i>stima</i> della misura in cui il rischio pu&ograve; essere ridotto <i>se</i> le misure proposte vengono applicate.
La fase di monitoraggio, che conclude il ciclo di gestione del rischio corruttivo, consiste nel verificare se le misure proposte sono poi state effettivamente applicate.

[![Monitor entrypoint][list-monitor]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/list-monitored-measures.png)
<br>
<strong>*Fig.24 - Pagina iniziale monitoraggio.*</strong>

Dal momento che dispone di una serie di cruscotti e report on-demand:
> <strong>il software offre anche specifici strumenti analitici per verificare in che misura &egrave; cambiato il livello di rischio non solo in funzione dell'applicazione ipotetica, ma anche di quella effettiva delle misure di mitigazione.</strong>

Semplificando, alla fine della fase di monitoraggio verranno ottenuti alcuni report con 3 colonne:
* <strong>il livello del PxI iniziale:</strong> determinato in base alle risposte ai quesiti date dalle strutture intervistate (quello che, [in uno dei paragrafi qui sopra](#in-sintesi), non essendo allora stato definito il significato di PxI, &egrave; stato chiamato <i>livello di rischio iniziale</i>);
* <strong>il livello del PxI intermedio:</strong> calcolato in base all'ipotetica applicazione delle misure di mitigazione (stima - ovvero quello che [pi&ugrave; sopra](#in-sintesi) &egrave; stato chiamato <i>livello di rischio stimato</i>)
* <strong>il livello del PxI finale:</strong> ricalibrato dopo aver verificato quali delle misure richieste siano state effettivamente applicate (monitoraggio - [pi&ugrave; sopra](#in-sintesi) chiamato <i>livello di rischio effettivo</i>).

Questo tipo di report conclude il ciclo di gestione del rischio e costituisce la certificazione dei livelli di rischio prodotta dall'esperto/ufficio anticorruzione.

### Fasi di attuazione e Indicatori di monitoraggio
Vale la pena anche di approfondire brevemente alcuni aspetti coinvolti nel monitoraggio.

Quest'ultimo step del ciclo di gestione del rischio corruttivo consiste, come gi&agrave; detto, nel consultare le strutture che avevano in carico i processi per verificare se effettivamente le misure proposte sono state applicate.

Per poter procedere anche su questo versante in modo <i>scientifico,</i> evitando che la verifica si riduca a una banale telefonata alla struttura incaricata chiedendo se le misure sono state messe in atto (semplice flag SI/NO), &egrave; stata realizzata un'impalcatura di entit&agrave; pi&uacute; articolata, non per complicare inutilmente il modello, ma piuttosto per ottenere un riscontro puntuale in merito alle singole azioni effettuate.

Distinguiamo, quindi, tra misura assoluta (vale a dire la misura <i>tout-court</i>) e misura monitorata; quest'ultima &egrave; una <code>is a</code> della prima, e contiene, al suo interno, i dettagli necessari al monitoraggio della misura stessa, ovvero necessari a stabilire se la misura sia o meno stata applicata.<br>

[![Schema ER monitoring (part)][schema-monitor]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/SchemaER-monitoring.png)
<br>
<strong>*Fig.25 - Parte del diagramma ER per la rappresentazione della misura monitorata ed entit&agrave; relative*</strong>

Questi dettagli includono: 
* la descrizione e il numero delle fasi di attuazione della misura;
* l'obiettivo del piano integrato di attivit&agrave; e organizzazione (PIAO), o di altro documento analogo, che giustifica l'applicazione della misura.

Su ogni fase di attuazione (che non va confusa con la "fase" del processo organizzativo, che &egrave; un'attivit&agrave;, ovvero un sottolivello, un task attraverso cui passa la realizzazione del processo stesso, e che da alcuni autori viene etichettata come "sottoprocesso") pu&ograve; essere applicato un <strong>indicatore di monitoraggio</strong>.

[![List of phases with indicators][list-indicators]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/list-phases_indicators.png)
<br>
<strong>*Fig.26 - Esempio di una misura monitorata con 2 fasi di attuazione: su una &egrave; stato assegnato un indicatore di monitoraggio, sull'altra non ancora*</strong>

Un indicatore di monitoraggio &egrave; un oggetto completamente distinto dall'<strong>indicatore di rischio</strong>, ovvero quello ottenuto in base alle risposte date all'intervista; mentre un indicatore di rischio (oggetto approfondito nello Step 2: Calcolo del rischio)  pu&ograve; quantificare il livello di probabilit&agrave; o di impatto che un certo rischio abbia rispetto ad un certo processo, l'indicatore di monitoraggio definisce una baseline ed un target e rappresenta il criterio cui si applica la misurazione (un oggetto a sua volta distinto).

[![Form to insert new monitoring indicator][add-indicator]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/form-indicator.png)
<br>
<strong>*Fig.27 - Maschera per l'inserimento di un nuovo indicatore di monitoraggio*</strong>

Riepilogando:
<mark>
* fase di attuazione  ≠ fase di processo
* indicatore di monitoraggio ≠ indicatore di rischio
</mark>

### Misura e Misurazione
Su un indicatore di monitoraggio vengono applicate una o pi&ugrave; misurazioni; quest'ultimo oggetto, a sua volta, non va confuso con la misura!

* La misura &egrave; una rappresentazione delle azioni correttive da realizzare per abbassare il livello di rischio.
* La misurazione &egrave; invece una verifica applicata a un indicatore di monitoraggio che consiste nel registrare se effettivamente le azioni correttive sono state messe in atto.

La misurazione &egrave; quindi, in certo qual modo, un epifenomeno della misura.<br>

# Roadmap

Tre delle funzioni attualmente implementate nel software <code>ROL-RMS</code>, ovvero:
<mark>
* il calcolo del rischio esistente, 
* il suggerimento circa le misure di mitigazione da applicare al rischio esistente,
* la produzione di tabelle comparative per consultare come varia il rischio in funzione delle misure ipotetiche e di quelle applicate,
</mark>

costituiscono strumenti utili (tools), che possono costituire un valido aiuto per l'ufficio o l'esperto del rischio corruttivo che devono effettuare un assessment relativamente a questi aspetti nel contesto di un'organizzazione.<br>

Allo stato attuale (versione di riferimento: <code>2.2</code>) il software &egrave; gi&agrave; pronto per essere adattato,
con un minimo adeguamento, a qualunque realt&agrave; organizzativa che voglia
effettuare un'analisi dettagliata dei rischi corruttivi cui i processi erogati
dall'organizzazione stessa sono esposti.
<br>

A questo proposito, &egrave; ora possibile effettuare l'inserimento dei processi - e relativi elementi (fasi, input, output etc.) - direttamente tramite form.
[![Form to insert new inputs][add-inputs]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/form-inputs.png)
<br>
<strong>*Fig.28 - Maschera per l'inserimento di nuovi input o di nuovi collegamenti tra processi ed input esistenti*</strong>

&Egrave; anche possibile stimare, con relativa precisione, quanto tempo &egrave; necessario per customizzare il software in funzione di una specifica realt&agrave; organizzativa.
Infatti, acquisite:
* le dimensioni dell'organizzazione (in particolare, il numero di livelli dell'organigramma ed il numero assoluto di strutture da mappare),
* il numero di livelli e la numerosit&agrave; dei processi prodotti dall'organizzazione stessa,
  
diventa possibile effettuare una stima relativamente accurata del tempo necessario 
affinch&eacute; sia possibile iniziare la campagna di interviste e, conseguentemente, ottenere i risultati dei vari indicatori di rischio e del giudizio sintetico <code>P x I</code>.
<br><br>

## Sviluppi futuri
Vi sono, inoltre, alcune possibili evoluzioni, che potrebbero essere implementate in versioni successive:
* Predisposizione di un cruscotto per i RAT (Referenti Anticorruzione e Trasparenza) per consentire loro di compilare autonomamente le risposte ai quesiti (certificando, automaticamente, i dati inseriti).
* Predisposizione di monitoraggi e reportistica, anche in forma grafica (istogrammi, grafici a torta, etc.), per consentire alla <em>governance</em> di effettuare controlli sugli stati di avanzamento e sui risultati raggiunti tramite il progetto di mappatura dei rischi.
* Predisposizione di appositi strumenti di ricerca per consentire all'ufficio trasparenza di ottenere query analitiche sulle interviste effettuate.
* Implementazione del multilingue (internazionalizzazione).
* Implementazione di layer di sicurezza aggiuntivi.

### Internazionalizzazione
Implementare una resa degli output in molte lingue diverse &egrave; un'operazione relativamente semplice da fare agendo su un software che si appoggia su un database relazionale ben strutturato e definito, come &egrave; nel caso di <code>ROL-RMS</code>.
Un modello consolidato, adatto alla resa di testi e titoli in un numero non prefissato di lingue diverse, &egrave; facilmente implementabile estendendo il database tramite: 
1. l'aggiunta di una tabella di traduzione per ogni tabella che contiene elementi testuali da tradurre e 
2. riscrivendo le query con l'aggiunta di LEFT OUTER JOIN che permettano di recuperare il valore tradotto, se presente.<br>
<sub>Per approfondire &egrave; possibile far riferimento al paper <i>A Framework for the Internationalization of Data-Intensive Web Applications</i></sub>

### Sicurezza
[![Error 505][product-error2]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/deniedAccess.png)
<br>
<strong>*Fig.29 - Schermata di errore mostrata in caso di tentativo di accesso senza corretta autenticazione*</strong>

Il sistema &egrave; gi&agrave; predisposto per gestire una serie di attacchi, quali la SQL Injection o alcuni attacchi di tipo Cross-site request forgery (CSRF).
Inoltre, implementa la sessione utente, il cui stato controlla sistematicamente, e alcuni meccanismi per prevenire attacchi di tipo DDOS, come ad esempio il caching.

Tuttavia, se dovesse essere aperto al pubblico, sarebbe necessario effettuare una revisione in merito alla sicurezza e sarebbe necessario implementare una serie di ulteriori controlli per garantire la validit&agrave; delle assunzioni effettuate in ciascun punto della navigazione, ed in particolare nei punti in cui si opera in scrittura sui dati.

Si confida nella comprensione del contribuitore relativamente al fatto che, essendo il sistema al momento sviluppato ad uso interno, alcuni aspetti di sicurezza non siano stati approfonditi estesamente: essendo le risorse limitate, si &egrave; preferito, in fasi di sviluppo, concentrarsi sulle funzionalit&agrave; piuttosto che su queste tematiche, chiaramente importanti ma cruciali soprattutto in fase di pubblicazione.

Gli aspetti di sicurezza possono certamente essere irrobustiti, ma l'investimento su questo versante &egrave; legato alla popolarit&agrave; del progetto: se questo &egrave; destinato a restare confinato entro i limiti di qualche ufficio anticorruzione e trasparenza, chiaramente non ha molto senso preoccuparsi di fornire strati aggiuntivi, essendo gi&agrave; implementata la sicurezza di base; in caso contrario, l'investimento anche su questo versante si arricchisce di senso.

<br>
Naturalmente, nessuno strumento informatico &egrave; in grado da solo di ottenere risultati come l'abbassamento dei rischi corruttivi; pertanto ogni approfondimento analitico permesso dal software dovr&agrave; essere esaminato ed interpretato dagli esperti dell'anticorruzione.

Ognuno pu&ograve; sentirsi libero di proporre miglioramenti ed evoluzioni.
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### ToDo
- Implementare un motore di ricerca interno
- Implementare ricerca sui quesiti per chiave testuale
- Aggiungere suggerimenti asincroni sulla digitazione della chiave testuale
- Implementare ricerca per struttura
- Implementare ricerca per processo 
- Implementare ricerca di quesiti e risposte per ambito di analisi 
- Implementare estrazione risultati forniti dal motore di ricerca interno in open data
- Aggiungere pesatura dei quesiti in funzione del rischio (associazione quesito / rischio)
- Implementare reportistiche e grafici sul rischio in rapporto alla struttura
- Implementare pagina di dettaglio struttura
- Implementare pagina di dettaglio soggetto contingente/interessato
- Implementare estrazione di tutti i dati dell'organigramma (query organigramma e strutture - estrazione)
- Aggiungere pagina che mostra i processi erogati dalla struttura evocata in maniera asincrona al clic sul nodo di una struttura

# History

Questa sezione illustra l'evoluzione del software ROL nel contesto delle varie release.<br />
In corrispondenza di ogni numero di versione non vengono descritte tutte le modifiche 
effettuate ma solo i rilasci delle funzionalit&agrave; pi&uacute; significative.<br />
Ogni numero di versione &egrave; per&ograve; corredato della data del commit 
dei sorgenti, per cui consultando la History del repository sar&agrave; facile
entrare nel merito di tutte le modifiche effettuate in corrispondenza della
sottoversione: inoltre, ogni versione corrisponde ad un commit (a meno di errori), ma non ogni commit
genera una versione.<br />

<sub>
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
</sub>

<!--
- Aggiunta pagina che mostra i processi erogati dalla struttura evocata in maniera asincrona al clic sul nodo di una struttura
- Implementata estrazione di tutti i dati dell'organigramma (query organigramma e strutture - estrazione)
- Implementata pagina di dettaglio soggetto contingente/interessato
- Implementata pagina di dettaglio struttura
- [2.9.9] Implementate reportistiche e grafici sul rischio in rapporto alla struttura
- [2.9.8] Aggiunta pesatura dei quesiti in funzione del rischio (associazione quesito / rischio)
- [2.9.7] Implementata estrazione risultati in formato CSV
- [2.9.6] Implementata ricerca per ambito di analisi | Implementata ricerca per processo | Implementata ricerca per struttura
- [2.9.5] Aggiunti suggerimenti asincroni sulla digitazione della chiave testuale
- [2.9.4] Implementata form di ricerca sui quesiti per chiave testuale
- ...
-->

### 2025
- [2.3.5] (04/08/2025) Implementata gestione delle risposte alle 3 domande relative alla misurazione (monitoraggio)
- [2.3.4] (11/06/2025) Implementata visualizzazione delle note al processo
- [2.3.3] (10/06/2025) Miglioramenti nella presentazione (aggiunto bottone, rilassato vincolo)
- [2.3.2] (20/05/2025) Revisionato algoritmo di calcolo dell'indicatore di probabilità P1
- [2.3.1] (19/05/2025) Correzione di bug; miglioramenti nella presentazione (bottoni, messaggi)
- [2.3.0] (06/05/2025) Implementata pagina di visualizzazione misurazione
- [2.2.9] (02/04/2025) Implementato registro degli input di processo
- [2.2.8] (26/03/2025) Implementata funzionalit&agrave; di inserimento output di processo
- [2.2.7] (25/03/2025) Revisione pagina di landing; revisione di etichette
- [2.2.6] (24/03/2025) Ordinamento ambiti di analisi che raggruppano i quesiti dell'intervista in base al loro numero d'ordine; ottimizzazioni
- [2.2.5] (11/03/2025) Implementata funzionalit&agrave; di inserimento associazioni tra strutture/soggetti terzi e fasi di processo
- [2.2.4] (05/03/2025) Prima bozza di implementazione form per assegnazione strutture/soggetti alle fasi; miglioramenti nella presentazione delle fasi (nascoste descrizioni soggetti interessati)
- [2.2.3] (04/03/2025) Implementata funzionalit&agrave; di inserimento fasi di processo
- [2.2.2] (03/03/2025) Implementata funzionalit&agrave; di modifica dell'ordinamento delle fasi collegate ad un processo
- [2.2.1] (25/02/2025) Implementata possibilit&agrave; di collegare molteplici input a un processo in una sola operazione
- [2.2.0] (19/02/2025) Implementata funzionalit&agrave; di inserimento input di processo
- [2.1.9] (14/02/2025) Miglioramenti nella presentazione (home)
- [2.1.8] (10/02/2025) Prima bozza di implementazione form per inserimento input
- [2.1.7] (05/02/2025) Correzione di bug
- [2.1.6] (03/02/2025) Implementata funzionalit&agrave; di inserimento processo
- [2.1.5] (29/01/2025) Implementata funzionalit&agrave; di inserimento macroprocesso
- [2.1.4] (27/01/2025) Prima bozza di implementazione form per inserimento macroprocesso
- [2.1.3] (20/01/2025) Implementata pagina iniziale inserimento processo
- [2.1.2] (13/01/2025) Revisione grafica pagine; miglioramenti nella presentazione pagina iniziale monitoraggio (conteggio fasi, indicatori, misurazioni e completezza di ogni misura)

### 2024
- [2.1.1] (02/12/2024) Implementati controlli lato client in form di aggiunta misurazione di indicatore
- [2.1.0] (28/11/2024) Aggiunta funzionalit&agrave; di download processi con PxI mitigati (stima) in formato CSV (estrae processi, PxI iniziale e valore del PxI calmierato che si otterrebbe se tutte le misure previste venissero effettivamente applicate)
- [2.0.9] (25/11/2024) Aggiunta pagina contenente form per inserire una nuova misurazione di indicatore; correzione di bug
- [2.0.8] (19/11/2024) Implementati controlli lato client in form di aggiunta indicatore di monitoraggio; aggiunta pagina dettagli indicatore di monitoraggio; correzione di bug
- [2.0.7] (11/11/2024) Implementata pagina elenco indicatori di una misura monitorata
- [2.0.6] (07/11/2024) Implementata funzionalit&agrave; di inserimento indicatore di monitoraggio 
- [2.0.5] (05/11/2024) Aggiunta pagina contenente form per inserire un nuovo indicatore di monitoraggio; correzione di bug
- [2.0.4] (31/10/2024) Revisione grafica pagine; miglioramenti nella presentazione (bottoni, etichette)
- [2.0.3] (28/10/2024) Implementata funzionalit&agrave; di inserimento dettagli del monitoraggio di una misura
- [2.0.2] (23/10/2024) Correzione di bug
- [2.0.1] (21/10/2024) Delegato metodo di prevenzione attacchi tipo "Garden Gate" al manager che si occupa della sessione utente
- [2.0.0] (15/10/2024) Aggiunta pagina contenente form per inserire i dettagli relativi al monitoraggio ad una misura di mitigazione selezionata
- [1.9.9] (08/10/2024) Implementata gestione separata della connessione al database; correzione di bug
- [1.9.8] (07/10/2024) Implementata pagina iniziale monitoraggio
- [1.9.7] (30/09/2024) Prima bozza di implementazione codice per il monitoraggio; correzione di bug
- [1.9.6] (23/09/2024) Prima bozza di implementazione componente software per la gestione degli indicatori di monitoraggio
- [1.9.5] (16/09/2024) Miglioramenti nella presentazione (etichette); aggiunta evidenziazione testo filtrato tramite libreria DataTables
- [1.9.4] (12/09/2024) Miglioramenti nella presentazione (icone, etichette); aggiunta evidenziazione testo filtrato tramite libreria DataTables
- [1.9.3] (04/09/2024) Implementato report tabella dei PxI calmierati in base alle misure stimate
- [1.9.2] (rilascio 02/08/2024 - commit 04/09/2024) Implementato algoritmo di ricalcolo PxI del processo in base alle misure applicate ai suoi rischi
- [1.9.1] (29/07/2024) Miglioramenti nella presentazione (header, bottoni, messaggi); aggiunto attributo a oggetto misura per contenere un valore di popolarit&agrave; della misura stessa
- [1.9.0] (22/07/2024) Implementato algoritmo di mitigazione a livello del PxI del singolo rischio in funzione delle misure applicate
- [1.8.9] (25/06/2024) Uniformazione versione, descrizioni e commenti
- [1.8.8] (20/06/2024) Ripristino del tema chiaro, mantenendo il tema scuro solo per l'header e le pagine di landing
- [1.8.7] (19/06/2024) Prova tema scuro; prima bozza di implementazione algoritmo di mitigazione
- [1.8.6] (05/06/2024) Revisione grafica pagina di landing; revisione di etichette
- [1.8.5] (28/05/2024) Implementazione parallela calcolo indicatori di impatto; implementazione parallela calcolo rischi di processi e interviste collegate
- [1.8.4] (27/05/2024) Prima bozza di implementazione report variazioni PxI dei rischi in funzione dell'applicazione (stimata) delle misure
- [1.8.3] (17/05/2024) Completamento implementazione pagina dei dettagli di una misura. Implementazione parallela calcolo output di processi
- [1.8.2] (16/05/2024) Implementazione parallela calcolo fasi di processo. Miglioramenti nella presentazione dei dettagli di una misura. Trasformazione di icone vettoriali in raster
- [1.8.1] (14/05/2024) Implementazione parallela calcolo elementi (indicatori di tipo P, input di processi). Miglioramenti nella presentazione delle misure suggerite (eliminati doppioni)
- [1.8.0] (13/05/2024) Prima bozza implementazione pagina dei dettagli di una misura di prevenzione/mitigazione del rischio. Miglioramenti nella presentazione delle misure suggerite (raggruppamento delle misure suggerite per tipologia di misura)
- [1.7.9] (07/05/2024) Miglioramenti nella presentazione delle misure applicate; correzione di bug
- [1.7.8] (06/05/2024) Implementazione funzione di assegnazione delle misure di mitigazione a specifico rischio nel contesto di un processo
- [1.7.7] (22/04/2024) Aggiunta pagina contenente form per applicare misure di mitigazione a un rischio, elencante anche le misure suggerite sulla base dei fattori abilitanti trovati associati al rischio entro il contesto del processo
- [1.7.6] (15/04/2024) Miglioramenti visualizzazione del registro delle misure di prevenzione: mostrata sostenibilit&agrave; economica della misura e strutture coinvolte
- [1.7.5] (08/04/2024) Miglioramenti visualizzazione del registro delle misure di prevenzione: mostrata struttura capofila della misura
- [1.7.4] (27/03/2024) Completamento implementazione funzione di inserimento delle misure di prevenzione
- [1.7.3] (25/03/2024) Proseguimento implementazione inserimento e recupero delle misure di prevenzione
- [1.7.2] (18/03/2024) Prima bozza di implementazione misure di prevenzione del rischio corruttivo
- [1.7.1] (26/02/2024) Implementato blocco prima riga tabella del log delle variazioni; migliorie pagina report tabella dei rischi
- [1.7.0] (13/02/2024) Completata prima versione del log delle variazioni tra l'ultimo caching dei valori degli indicatori e gli stessi calcolati a runtime; revisione di etichette
- [1.6.9] (12/02/2024) Implementato nel ricalcolo indicatori il salvataggio delle motivazioni gi&agrave; inserite; correzione di bug
- [1.6.8] (06/02/2024) Prima bozza di implementazione del log delle variazioni tra l'ultimo caching dei valori degli indicatori e gli stessi calcolati a runtime
- [1.6.7] (29/01/2024) Implementata funzione di modifica/aggiunta nota al PxI; revisione di etichette; correzione di bug
- [1.6.6] (22/01/2024) Implementata visualizzazione delle note al PxI con confronto valore PxI in memoria (a runtime) e valore PxI su disco (in cache)
- [1.6.5] (16/01/2024) Aggiornamento annuale licenza
- [1.6.4] (15/01/2024) Modificato ordinamento strutture; cambiata presentazione valore di rischio altissimo; correzione di bug
- [1.6.3] (08/01/2024) Raffinata estrazione CSV relativa a intervista singola; aggiunto ordinamento per nome a nodi albero processi; migliorata presentazione report PxI dei processi

### 2023
- [1.6.2] (15/12/2023) Revisionato algoritmo di calcolo della dimensione di probabilit&agrave; P; aggiunto stile per evidenziare valore di rischio altissimo
- [1.6.1] (11/12/2023) Revisionato algoritmo di calcolo dell'indicatore di impatto I3; correzione di bug
- [1.6.0] (07/12/2023) Implementato output su file RTF del report tabellare riepilogante rischi e strutture; correzione di bug
- [1.5.9] (30/11/2023) Raffinato algoritmo di calcolo indicatore I3 tenendo conto dell'interessamento di categorie globali di strutture e considerando non determinabili i casi in cui non risultano n&eacute; strutture n&eacute; soggetti associati alle fasi del processo (un processo &egrave; sempre funzione dell'attivit&agrave; di almeno una struttura o un soggetto contingente) 
- [1.5.8] (29/11/2023) Predisposizione alla generazione di output su file RTF; correzioni nelle transcodifiche dei fine riga
- [1.5.7] (28/11/2023) Riscritto codice per la produzione di output diversi da html sincrono; revisione di etichette
- [1.5.6] (27/11/2023) Implementato calcolo dimensione I (impatto del rischio) e della combinazione di P ed I (indice PxI)
- [1.5.5] (20/11/2023) Aggiunto calcolo dimensione P (probabilit&agrave; del rischio)
- [1.5.4] (16/11/2023) Revisionato algoritmo di calcolo dell'indicatore di probabilit&agrave; P4; revisioni screenshot
- [1.5.3] (13/11/2023) Aggiunta tabella riepilogativa del PxI di tutti i processi, elencante il valore ottenuto in ogni indicatore per ogni processo
- [1.5.2] (06/11/2023) Aggiunti in tabella riepilogativa del PxI di tutti i processi e strutture, i macroprocessi, le aree di rischio ed i soggetti interessati a ogni processo
- [1.5.1] (30/10/2023) Aggiunta tabella riepilogativa del PxI di tutti i processi, elencante rischi e strutture associate a ogni processo
- [1.5.0] (23/10/2023) Aggiunte sottopagine in sezione report; migliorata presentazione ricerche predefinite
- [1.4.9] (19/10/2023) Raffinato controllo formale sulla validita' della risposta: omessi alcuni controlli in caso la risposta sia relativa a un quesito figlio (quesiti di tipo "di cui")
- [1.4.8] (17/10/2023) Implementato caching su disco dei valori degli indicatori di rischio
- [1.4.7] (11/10/2023) Trasformata struttura contenente gli indicatori di rischio in mappa ordinata
- [1.4.6] (10/10/2023) Aggiunto attributo a oggetto processo anticorruttivo per contenere valori degli indicatori di rischio
- [1.4.5] (02/10/2023) Mostrate interviste nella pagina di dettaglio del processo, con valori degli indicatori calcolati; implementato algoritmo di scelta in caso di valori di rischio divergenti tra interviste multiple sullo stesso processo. Rimosso codice legacy.
- [1.4.4] (25/09/2023) Implementata la logica di calcolo degli indicatori P1, P2, P3, P4, P5 nel contesto della singola intervista; implementato metodo per effettuare i controlli lato server riguardo la validit&agrave; della risposta
- [1.4.3] (18/09/2023) Prima bozza di implementazione degli indicatori P1, P2, P3 nel contesto della singola intervista
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

### 2022
- [1.2.6] (21/12/2022) Aggiunta funzionalit&agrave; di download dettagli specifico processo in formato CSV
- [1.2.5] (13/12/2022) Aggiunti estremi input, fasi, output nel CSV di estrazione generale dei processi
- [1.2.4] (01/12/2022) Mostrati dettagli di un processo in una finestra separata a fini di stampa in pdf
- [1.2.3] (29/11/2022) Implementati primi esempi di generazione grafici
- [1.2.2] (23/11/2022) Aggiunta funzionalit&agrave; di download albero completo processi in formato CSV
- [1.2.1] (22/11/2022) Mostrato in nodo albero processi numero di input e fasi
- [1.2.0] (21/11/2022) Implementato questo file di documentazione
- [1.1.9] (17/11/2022) Aggiunto dettaglio processo anticorruttivo: input, fasi, output

- [&le; 1.1.9] Implementata intervista (scelta struttura e processo anticorruttivo, compilazione risposte ai quesiti), pagina di elenco interviste, estrazioni varie in CSV, organigramma ed elenco processi in albero navigabile




### Built With

Questo progetto &egrave; rilasciato in Open Source ed utilizza esclusivamente tecnologie STANDARD e framework consolidati. 
Ad esempio: 
* POJO per Java (tutto lo strato CONTROLLER); 
* Ajax per le richieste asincrone (XHR); 
* Bootstrap, e i suoi plugin, per i fogli di stile e l'interfaccia responsive (VIEW); 
* jQuery, e standard JavaScript, per la manipolazione lato client del DOM; 
* SQL per l'accesso al MODEL; 
* JSTL (Expression Language) per la costruzione della VIEW; 
* JSON per la costruzione di alberi di navigazione; 
e così via.

Tecnicamente, <strong>l'applicazione è un'architettura monolitica.</strong>
Non varrebbe neppure la pena di giustificare questa scelta, data la natura del progetto (uno sviluppo incrementale portato avanti negli anni da un singolo software engineer), ma val la pena di evidenziare i principali vantaggi di un'architettura monolitica rispetto ad una a microservizi, nel contesto dei task realizzati tramite il presente software:
* il codice è depositato tutto in un unico repository, cioè quello che viene documentato dal presente file README;
* l'applicazione è facile da deployare: eseguendo un singolo script, una nuova versione viene rilasciata ed il server viene aggiornato in pochi istanti;
* l'applicazione è più facile da debuggare; nonostante - laddove non vi fossero rischi di race condition - il calcolo sia stato parallelizzato, è sufficiente attivare un singolo punto di interruzione per entrare in debug, controllare tutti i valori assunti dalle variabili e sfruttare tutti i meccanismi di controllo;
* le performance di un'applicazione monolitica sono migliori rispetto a quella di una a microservizi perché i singoli componenti dialogano efficientemente (considerare che, nonostante questo, è stato necessario implementare meccanismi di caching a causa delle latenze indotte dal grande numero di calcoli che è necessario realizzare per ottenere i valori degli indicatori di rischio).

Di seguito sono elencate le principali librerie e tecnologie utilizzate per sviluppare ed eseguire il progetto. 

* [![Java][Java]][Java-url]
* [![JavaScript][JavaScript]][javascript-url]
* [![EL][EL]][EL-url]
* [![HTML][HTML]][HTML-url]
* [![CSS][CSS]][CSS-url]
* [![SQL][SQL]][SQL-url]
* [![Bootstrap][Bootstrap.com]][Bootstrap-url]
* [![JQuery][JQuery.com]][JQuery-url]

Maggiori dettagli sui linguaggi utilizzati si trovano <a href="https://github.com/gbetorre/rischi">qui</a>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->

## Getting Started

In questa sezione vengono brevemente fornite le istruzioni per effettuare il setup locale del progetto.
Per ottenere una copia locale funzionante del software <code>ROL-RMS</code> &egrave; possibile procedere seguendo alcuni semplici step.

### Prerequisiti

Il software usa molti framework standard ma anche alcune librerie specifiche.
In particolare, sono utilizzate:

* jQuery 3.3.1 e jQuery-UI 1.12.1
* jQuery Validate 1.17
* jQuery additional-methods 1.17
* jQuery highlight 3
* jQuery dataTables 1.12.1
* jQuery modal 
* jQuery textarea_autosize
* Bootstrap 4.5.2
* Bootstrap grayscale 5.0.2
* Bootstrap SB Admin 6.0.2
* OrgChart 1.0.5

Queste librerie sono sempre referenziabili tramite link al sito del produttore, ma disponibili anche offline tramite clone del repository dei sorgenti.

La sintassi utilizzata nei sorgenti Java li rende compatibili con versioni di Java pari o superiori alla 1.8; inoltre, per compilare richiedono l'inclusione, nel build path, di tutta la JRE System Library e inoltre di:

* servlet-api
* jsp-api
* el-api
* jstl 1.2
* cos.jar
* commons-text.jar
* commons-logging
* commons-lang
* commons-beanutils-core

Inoltre, per stabilire la connessione al database, il sorgenti del Model usano:

* postgresql-jdbc.

Il database su cui l'applicazione si appoggia &egrave; un database relazionale PostgreSQL v. 12 e superiori.
L'SQL con cui sono scritte le query &egrave; comunuque sostanzialmente standard e facilmente adattabile a qualunque DBMS.

Per far girare l'applicazione &egrave; quindi necessario come prerequisito generare prima un'istanza dello schema e valorizzarlo con una serie di elementi di base.
A questo scopo, &egrave; possibile partire da uno script di creazione e successive query di inserimento, ma anche effettuare il restore di un dump gi&agrave; pronto.

Come noto, in quest'ultimo caso, su piattaforma Linux, si pu&ograve; procedere con:
* pg_restore
  ```cd /home/<user>/Backup/dump/rol
  createdb rol -e utf-8 -U <user>
  pg_restore -Fc -d rol -U <user> dumpRol.sql -v
  ```
dove: 
* <code>&lt;user&gt;</code> &egrave; l'utente concordato, che diverr&agrave; proprietario del database, che &egrave; stato inserito in precedenza come utente di db, con i relativi diritti di amministratore;
* il dump si chiama "dumpRol.sql".


### Installazione

_Di seguito un esempio dei passi necessari per installare ed eseguire il sistema software <code>ROL-RMS</code>._

1. Ottenere un dump del database di produzione, con preimpostato un utente concordato pienamente in grado di consultarne e amministrarne i dati

2. Clonare il repo
   ```sh
   git clone https://github.com/gbetorre/rischi.git
   ```
3. Installare i packages necessari, compreso un web server dotato di container JSP

4. Configurare il server, compilare i sorgenti ed effettuare un deploy

5. Puntare il browser all'indirizzo configurato, ad esempio
   ```
   https://localhost:8080/rol/
   ```


[![Index Sample screen][product-landing3]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing2.2.png)
<br>
<strong>*Fig.30 - Enjoy*</strong>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES 
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://example.com)_

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTRIBUTING -->

## Contributing

I contributi sono ci&ograve; che rende le comunit&agrave; Open Source uno spazio fantastico per apprendere, venire ispirati e dare spazio alla creativit&agrave;.
Ogni contributo sar&agrave; pertanto **grandemente apprezzato**.

Chiunque abbia suggerimenti che potrebbero migliorare il progetto pu&ograve; scaricare il repository, testarlo in locale, apportarvi modifiche e creare una pull request:

1. Effettuare un Fork del progetto (dall'URL https://github.com/gbetorre/rischi/tree/main cliccare sul pulsante "Fork" in alto a destra)
2. Clonare il Fork  (`git clone https://github.com/username/rischi.git` dove username &egrave; l'utente GitHub)
3. Creare la propria Feature Branch (`git checkout -b feature/AmazingFeature`)
4. Committare le modifiche (`git commit -m 'Added some AmazingFeature | Aggiunta AmazingFeature'`)
5. Fare il Push al Branch (`git push -u origin feature/AmazingFeature`)
6. Aprire una Pull Request (cliccare sul tab "Pull requests" poi su "New pull request"; dare un titolo chiaro e aggiungere una descrizione dettagliata dei cambiamenti effettuati; quindi cliccare su "Create pull request").

[![Repository features list][list-features]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/features.png)
<br>
<strong>*Fig.31 - Elenco delle features di un repository listate tramite il software Sourcetree*</strong>


Per poter far girare il software &egrave; necessario effettuare un deploy del database su cui lo stesso &egrave; appoggiato.
Per ottenere un dump del database di produzione, corrispondente a un'implementazione completa dello schema ma contenente solo dati esemplificativi, <a href="mailto:gianroberto.torre@gmail.com">contattare l'autore</a>.

Un modo pi&uacute; semplificato per suggerire modifiche consiste nell'aprire semplicemente una issue contrassegnata con il tag "enhancement".

Se sta risultando interessante, non dimenticare di dare una stella al progetto!

Per una lista dei contributori, v.: [AUTHORS](AUTHORS) file.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- LICENSE -->

## Licenze

Il Software <code>ROL-RMS</code> &egrave; distribuito nei termini della licenza GNU GPL-2.0 License. 

Per "Software", in questo contesto, si intendono tutti i sorgenti prodotti dall'<a href="mailto:gianroberto.torre@gmail.com">autore</a>.

Laddove i sorgenti facciano riferimento a librerie di terze parti, occorre consultare la relativa documentazione, in particolare le condizioni di licenza e d'uso.

Il Software in effetti, come &egrave; normale per qualunque software, fa ricorso a codici rilasciati con una licenza che pu&ograve; coincidere o meno con quella del software stesso.

In particolare:
* jQuery &egrave; rilasciato con _[licenza MIT](https://jquery.com/license/)_ analogamente a molte altre librerie (p.es. Boostrap, OrgChart etc.)
* Servlet, JSP ed Expression Language sono rilasciati con Apache License 
* Le pagine di landing usano modeli di templatemo, che sono rilasciati sostanzialmente nel pubblico dominio (Public Domain)
* Alcune pagine utilizzano immagini rilasciate con licenza libera da _[Pixabay](https://pixabay.com/it/service/terms/)_ che fa uso di Creative Commons in combinazione con altri termini
* Svariate immagini sono prodotte con il modello generativo di intelligenza artificiale FLUX1-schnell, che utilizza _[Apache License 2.0](https://github.com/black-forest-labs/flux/blob/main/LICENSE)_

A ogni modo, il Software fa uso esclusivamente di componenti con licenza libera e viene rilasciato "AS IS", come previsto dalla propria licenza.
Consulta <a href="https://github.com/gbetorre/rischi/blob/main/LICENSE">`LICENSE.txt`</a> per ulteriori informazioni.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<a name="readme-contact"></a>
<!-- CONTACT -->

## Contatti
Per approfondire ed ottenere accesso al documento di analisi dei requisiti, <a href="mailto:gianroberto.torre@gmail.com">contattare l'autore</a>.

Software Engineer: Giovanroberto Torre - [@GianroTorres](https://twitter.com/GianroTorres) - gianroberto.torre@gmail.com

Corruptive risk Analyst: Alberto Maria Arena Agostino - [albertomaria.arenaagostino](https://www.univr.it/it/contatti/albertomaria.arenaagostino) - albertomaria.arenaagostino@univr.it

Project Link: [https://github.com/gbetorre/rischi](https://github.com/gbetorre/rischi)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

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
[product-login]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/login1.95.png
[product-login2]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/login2.25.png
[product-landing]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing1.40.png
[product-landing2]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing2.0.png
[product-landing3]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing2.2.png
[product-landing4]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing2.27.png
[product-interview]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/interview-sample.png
[product-algorithm]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/algorithm-P3.png
[product-orgchart]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-str.png
[product-process]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-pro.png
[product-error1]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/internalServerError.png
[product-error2]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/deniedAccess.png
[process-29]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/pro-29.png
[process-45]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/pro-45.png
[indicator-sample01]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample01.png
[indicator-sample02]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample02.png
[indicator-sample03]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample03.png
[judgements-sample]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/giudizi-sintetici.png
[pxi]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/PxI.PNG
[question-domains]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/questions-domains.png
[dashboard-risk]:   https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-pxi.png
[dashboard-risk2]:  https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-synthesis-pxi.png
[dashboard-risk3]:  https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-overwiev-pxi.png
[dashboard-risk4]:  https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-synthesis-measures.png
[dashboard-graph]:  https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-graphics.png
[add-measure]:      https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/form-measure.png
[assign-measure]:   https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/form-measure2.png
[add-indicator]:    https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/form-indicator.png
[add-inputs]:       https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/form-inputs.png
[list-measures]:    https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/list-measures.png
[list-monitor]:     https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/list-monitored-measures.png
[list-indicators]:  https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/list-phases_indicators.png
[schema-measure]:   https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/SchemaER-measure.png
[schema-monitor]:   https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/SchemaER-monitoring.png
[schema-physical]:  https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/DB-circular.png
[class-diagram]:  https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/class-diagram.png
[list-features]:  https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/features.png
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
