<a name="readme-top"></a>

### 2 languages found
[![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/gbetorre/rischi/blob/master/README.md)
[![it](https://img.shields.io/badge/lang-it-yellow.svg)](https://github.com/gbetorre/rischi/blob/master/README.it.md)

---

[![GPL-2.0 license][license-shield]][license-url]


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <!--
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>
  -->
  <h3 align="center">ROL [Risk OnLine]</h3>

  <p align="center">
    Web application for mapping corrupt risk to which organizational processes may be exposed
    <br><br>
    <a href="https://github.com/gbetorre/rischi"><strong>Explore files »</strong></a>
    <br>
    <a href="https://github.com/gbetorre/rischi/issues">Report Bug</a>
    ·
    <a href="https://github.com/gbetorre/rischi/pulls">Request Feature</a>
  </p>
</div>


<!-- TABLE OF CONTENTS -->

# Risk Mapping Software
The web application for <cite>corruption risk mapping</cite> is intended to help Entities, Public Administrations and investee companies to <strong>automatically quantify the corruption risk</strong> to which their organizational processes are exposed and direct them to implement appropriate countermeasures.

[![Product Landing Page][product-landing2]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing2.0.png)
<br>
<strong>*Landing page, version 2.0*</strong><br>
<br>

## About The Project

[![Goal Sample][indicator-sample01]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample01.png)
<br>
<strong>*Fig.1 - The goal of the software is to obtain, automatically, the risk value for each organizational process considered (dummy data)*</strong><br>

<p>
Through the response to a series of questions submitted to managers and operators at specific administrative offices (interview), the application makes it possible to obtain, automatically, a series of indices related to specific corruption risk to which the organizational processes may be exposed.<br>
</p>

[![Product Login Screen Shot][product-login]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/login1.40.png)
<br>
<strong>*Fig.2 - Obviously, the software is a restricted access application*</strong>
<br><br>

[![Product Landing Page][product-landing]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing1.40.png)
<br>
<strong>*Fig.3 - Landing page, version 1.40*</strong><br>

<p>
Matter of fact, each question is linked to one or more specific corruption risks; therefore, depending on the answer given by the interviewed personnel, the application expresses specific indices and attention points and, in summary, calculates the level of risk to which the examined process is exposed. 
<br><br>

[![Product Interview][product-interview]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/interview-sample.png)
<br>
<strong>*Fig.4 - An example of questions that help provide insight into the vulnerability of an organizational process*</strong>

Specifically, for each process probed through the interview, we obtain the values of 7 probability indicators (P) and 4 impact indicators (I).
<br>

[![Goal Sample alt][indicator-sample02]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample02.png)
<br>
<strong>*Fig.5 - The answers to the questions considered for some indicator may, occasionally, not allow obtaining the risk value in the dimension considered (dummy data)*</strong>
<br><br>

[![Sample alt][indicator-sample03]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample03.png)
<br>
<strong>*Fig.6 - In such cases, the software reports the reason for non-calculation; if there are multiple reasons, they are shown one at a time until the problem is corrected (dummy data)*</strong>
<br><br>

Crossing the values obtained in the indicators of probability (P) with those obtained in the indicators of impact (I) we obtain, for each organizational process surveyed, a synthetic index <code>P x I</code>, which expresses the final level of risk to which the process itself is exposed.

By linking the risk to the (counter)measures, it is also possible to obtain a number of suggestions about the organizational actions to be implemented in order to reduce the specific corruption risk identified.

## How the software works
Obviously, the Risk Mapping Software (ROL) application relies on a database, specifically a PostgreSQL-type relational database (version 12 and later), in which the questions that will be submitted to the facilities in the interviews are populated.

In the first stage, the loading of organizational structures (organizational chart) is carried out, as well as the loading of organizational processes that are produced by these structures.

These uploads to the database can be done by automatically generated entry queries or by ETL but, under study, there is a mode of bulk uploading by uploading appropriately formatted files.

Structures are organized in a tree with various levels while processes are structured in 3 main levels (macro-process, process and sub-process - the latter not used in the 2022-2025 survey in favor of process phases aka steps). 

[![Product Sample OrgChart][product-orgchart]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-str.png)
<br>
<strong>*Fig.7 - Organizational chart navigation function*</strong><br><br>

[![Product Sample Macro][product-process]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-pro.png)
<br>
<strong>*Fig.8 - Navigation function in the macroprocess tree*</strong>

Each process or subprocess (but not the macroprocess) can itself be divided into phases (or steps, or activities). Each phase can be associated with one or more structures and one or more third parties (which are entities not structured in the organizational chart but still acting on the process phase).

The software provides special features for navigating the macroprocess tree and the organizational chart tree (see <a href=“https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-str.png”>figure</a> and <a href=“https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-pro.png”>figure</a>), so you can quickly verify that the mapping matches what is actually in the organization.

Furthermore, a detail page is provided for each process that contains not only the level of risk to which the process is exposed (information that is the focus of the entire application), but also all other aggregate information related to the process itself, including: inputs, steps (aka activities), outputs, risk, and enabling factors.

[![Product Sample Process][process-29]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/pro-29.png)
<br>
<strong>*Fig.9 - Example of a detail page of a process surveyed for anti-corruption purposes*</strong>

After populating the database with the structures, macro-processes and their sub-levels, we move on to the interview phase, which consists of addressing a series of questions to a set of specific structures that oversee a specific process. The battery of questions is large (more than 150) but the decision about which questions to administer can be determined by the interviewer; in fact, all questions are optional, and <strong>there are more general questions</strong>, which probably make sense to address in every interview, <strong>and more specific questions</strong>, which only make sense to administer if you are looking at very specific processes. The questions are grouped into areas of analysis and, in the case of some structures, it might even make sense to omit questions from entire areas of analysis.

The answers are then used to obtain the value of a series of indicators, as mentioned earlier.<br>
All - but one - among the indicators depend on the responses to the questions, so that the value obtained in every indicator (but one) is calculated through an algorithm that takes into account the responses obtained.<br>
There is only one impact indicator that does not depend on the questions instead depends on the number and type of facilities involved in the measured process.

The algorithms for calculating the indicators are all different from each other.
<br><br>
[![Product Algorithm][product-algorithm]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/algorithm-P3.png)
<br>
<strong>*Fig.10 - Example (simplified) of the flowchart of the algorithm for calculating a specific probability indicator (P3: analysis/evaluation of reports received)*</strong><br><br>

As mentioned in the previous paragraph, through additional algorithms all values obtained in the probability indicators (global probability index P) and all values obtained in the impact indicators (global impact index I) are crossed.

Finally, through a classic Quantitative Risk Analysis table, the <code>P x I</code> index, or summary judgment, obtained for each process surveyed and investigated through the interviews, is eventually calculated.<br>

[![PxI][pxi]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/PxI.png)
<br>
<strong>*Fig.11 - Decision table of the algorithm for calculating PxI, with the 9 possible values derived from the arrangements with repetition D'(3,2) = 3<sup>2</sup> of the 3 possible values of P and the 3 possible values of I.*</strong>

## Future developments
At the present, the software is ready to be taylored,
with minimal adaptation, to any organization that would carry out a detailed analysis of the corruptive risk to which the processes provided by the organization itself are exposed.
<br>

It is also possible to estimate, with relative accuracy, how much time is needed to customize the software to suit a specific organization.<br>
In fact, acquired:
* the size of the organization (in particular, the number of levels of the organizational chart and the absolute number of structures to be mapped)
* the number of levels and the number of processes produced by the organization itself,
  
it becomes possible to make a relatively accurate estimate of the time required 
for the interview campaign and - consequently - to obtain the results of the various risk indicators and the P x I summary judgment.
<br><br>

---

There are, in addition, some possible developments, which could be implemented in later versions:
* Preparation of a dashboard for RATs (Anti-Corruption and Transparency Referents) to enable them to independently fill in the answers to the questions (certifying, automatically, the data entered)
* Preparation of monitoring and reporting to enable <em>governance</em> to conduct checks on progress and results achieved through the risk mapping project
* Preparation of appropriate research tools to enable the transparency office to obtain analytical queries on the interviews conducted.

<br>
Of course, event though it can be such a big help, no IT tool by itself can achieve results such as lowering corruption risk; therefore, 
any analytical insights allowed by the software will have to be examined and interpreted by anti-corruption experts.

Everyone can feel free to propose improvements and evolutions.
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>


### Built With

This section explains the main libraries and technologies used to develop and execute the project. 
More details <a href=“https://github.com/gbetorre/rischi”>here</a>

* [![Java][Java]][Java-url]
* [![JavaScript][JavaScript]][javascript-url]
* [![EL][EL]][EL-url]
* [![HTML][HTML]][HTML-url]
* [![CSS][CSS]][CSS-url]
* [![SQL][SQL]][SQL-url]
* [![Bootstrap][Bootstrap.com]][Bootstrap-url]
* [![JQuery][JQuery.com]][JQuery-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>


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

Distributed under the terms of the GNU GPL-2.0 License.<br> 
See <a href="https://github.com/gbetorre/rischi/blob/main/LICENSE">`LICENSE.txt`</a> too.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contatti

Software Engineer: Giovanroberto Torre - [@GianroTorres](https://twitter.com/GianroTorres) - gianroberto.torre@gmail.com

Project Link: [https://github.com/gbetorre/rischi](https://github.com/gbetorre/rischi)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## History

This section illustrates the evolution of the ROL software in the context of the various releases.<br>
Not all changes are described under each version number
but only the releases of the most significant features.<br>

Each version number is paired with the date of the commit, so by consulting the History of the repository it will be easy to
to go into all the changes made at the subversion: 
moreover, each version corresponds to a commit, but not each commit generates a version.<br>

<p style="font-size:small">
NOTE: By convention, in the software the version is shown in the x.xx format thus merging the sub-sub version figure with the subversion figure, while in this changelog it has the classic x.x.x format (this is to bring more descriptive accuracy).<br>
In addition, the meaning of subversions is quite different from the general one; in fact, 
it does not identify whether the version is stable or not (an aspect usually identified respectively by the final digit different from zero or equal to zero), 
nor does it have relevant jumps according to high-impact changes (e.g., moving from version 6.1.38 to version 7.0.1 of VirtualBox, which marked a fairly big change); 
in the case of the current application, in fact, version numbers have only the signficance of keeping track of releases and deploys 
(1.1.9 = XIX deploy; 1.2.0 = XX deploy; 1.9.9 = IC deploy; 2.0.0 = C deploy)</p>
<cite>(The realease date is in Italian format: sorry, should writing a script to convert all the dates, still I can do without...)</cite>

- [2.0.7] (11/11/2024) Implementation of page containing list of indicators regarding a monitored measure
- [2.0.6] (07/11/2024) Added facility to insert monitoring indicator
- [2.0.5] (05/11/2024) Added page containing form to add an indicator for monitoring; bug fix
- [2.0.4] (31/10/2024) Presentation improvements (buttons, labels, colours)
- [2.0.3] (28/10/2024) Added facility to insert monitoring details about a measure
- [2.0.2] (23/10/2024) Bug fix
- [2.0.1] (21/10/2024) Delegated “Garden Gate” attack prevention to the manager in charge of the user session
- [2.0.0] (15/10/2024) Added page containing form to assign monitoring details to a selected measure
- [1.9.9] (08/10/2024) Improvement in database connection management; bug fix
- [1.9.8] (07/10/2024) First draft implementation about monitoring page 
- [1.9.7] (30/09/2024) Bug fix
- [1.9.6] (23/09/2024) First draft implementation software component for the management of monitoring indicators
- [1.9.5] (16/09/2024) Presentation improvements (labels); highlighted filtered text via DataTables library
- [1.9.4] (12/09/2024) Presentation improvements (icons, labels); highlighted filtered text via DataTables library
- [1.9.3] (04/09/2024) Implemented PxI tabular report regarding mitigated process risks based on the measures estimated to be applied to the risks themselves
- [1.9.2] (04/09/2024) Implemented process PxI recalculation algorithm based on the measures applied to its risks
- [1.9.1] (29/07/2024) Presentation improvements (header, buttons, messages); new measure attribute to hold a popularity value of the measure itself
- [1.9.0] (22/07/2024) Implemented mitigation algorithm at the PxI level of the individual risk
- [1.8.9] (25/06/2024) Revised version, descriptions and comments
- [1.8.8] (20/06/2024) Restoring the light theme, keeping the dark theme only for the header and landing pages
- [1.8.7] (19/06/2024) Testing dark theme; mitigation algorithm first draft
- [1.8.6] (05/06/2024) Graphic revision landing page; revision of labels.
- [1.8.5] (28/05/2024) Parallel implementation impact indicator calculation; parallel implementation process risk calculation and related interviews.
- [1.8.4] (05/27/2024) First draft implementation report PxI changes in risk as a function of (estimated) application of measures.
- [1.8.3] (17/05/2024) Completed implementation measure details page. Parallel implementation output calculation of processes.
- [1.8.2] (16/05/2024) Parallel implementation process step calculation. Improvements in presentation of details of a measure. Transformation of vector icons to raster.
- [1.8.1] (14/05/2024) Parallel implementation element calculation (P-type indicators, process inputs). Improvements in presentation of suggested measures (eliminated duplicates).
- [1.8.0] (13/05/2024) First draft implementation details page of a risk prevention/mitigation measure. Improvements in presentation of suggested measures (grouping of suggested measures by measure type).
- [1.7.9] (07/05/2024) Improvements in presentation of applied measures. Bug fixes.
- [1.7.8] (06/05/2024) Implementation of function to assign mitigation measures to specific risk in the context of a process.
- [1.7.7] (04/22/2024) Added page containing form to apply mitigation measures to a risk, also listing suggested measures based on enabling factors found associated with the risk within the context of the process
- [1.7.6] (15/04/2024) Improvements visualization of prevention measure registry: showed economic sustainability of the measure and structures involved
- [1.7.5] (08/04/2024) Enhancements visualization of prevention measure register: shown measure lead structure
- [1.7.4] (27/03/2024) Completed implementation of prevention measure entry function.
- [1.7.3] (25/03/2024) Continued implementation insertion and retrieval of prevention measures
- [1.7.2] (18/03/2024) First draft of implementation of corruption risk prevention measures
- [1.7.1] (02/26/2024) Implemented block first row change log table; improvements report page risk table
- [1.7.0] (13/02/2024) Completed first version of variance log between last caching of indicator values and the same calculated at runtime; revised labels
- [1.6.9] (12/02/2024) Implemented in indicator recalculation the saving of reasons already entered; bug fixes
- [1.6.8] (06/02/2024) First draft implementation of log of changes between last caching of indicator values and the same ones calculated at runtime
- [1.6.7] (29/01/2024) Implemented edit/add note function to PxI; revised labels; fixed bugs
- [1.6.6] (22/01/2024) Implemented display of notes to PxI with comparison of PxI value in memory (at runtime) and PxI value on disk (cached)
- [1.6.5] (16/01/2024) Annual license update.
- [1.6.4] (15/01/2024) Modified structure sorting; changed presentation of highest risk value; bug fixes
- [1.6.3] (08/01/2024) Refined CSV extraction relative to single interview; added sorting by name to process tree nodes; improved PxI report presentation of processes
- [1.6.2] (15/12/2023) Revised algorithm for calculating probability size; P; added style to highlight very high risk value
- [1.6.1] (11/12/2023) Revised algorithm for calculating impact indicator I3; fixed bugs
- [1.6.0] (07/12/2023) Implemented output to Rich Text Format file of tabular report summarizing risk and facilities; bug fixes
- [1.5.9] (30/11/2023) Refined algorithm for calculating I3 indicator by taking into account the involvement of global categories of facilities and considering non-determinable cases in which there are no facilities subjects associated with process steps (a process is always a function of the activity about at least one facility or a contingent subject) 
- [1.5.8] (29/11/2023) Preparation for generating output to Rich Text Format files; corrections in end-of-line transcoding
- [1.5.7] (28/11/2023) Rewrote code for producing output other than synchronous html; revised labels
- [1.5.6] (27/11/2023) Implemented calculation of dimension I (risk impact) and combination of P and I (PxI index)
- [1.5.5] (20/11/2023) Added P (probability of risk) dimension calculation.
- [1.5.4] (16/11/2023) Revised probability indicator calculation algorithm P4; screenshot revisions
- [1.5.3] (13/11/2023) Added PxI summary table of all processes, listing the value obtained in each indicator for each process
- [1.5.2] (06/11/2023) Added in summary table of the PxI of all processes and structures, the macroprocesses, risk areas and stakeholders in each process
- [1.5.1] (30/10/2023) Added PxI summary table of all processes, listing risk and structures associated with each process
- [1.5.0] (23/10/2023) Added subpages in report section; improved presentation of default searches
- [1.4.9] (19/10/2023) Refined formal check on the validity of the answer: omitted some checks in case the answer is related to a child question (“subordinate” type questions)
- [1.4.8] (17/10/2023) Implemented on-disk caching of risk indicator values
- [1.4.7] (11/10/2023) Transformed structure containing risk indicators into ordered map
- [1.4.6] (10/10/2023) Added attribute to anticorruptive process object to contain values of risk indicators
- [1.4.5] (02/10/2023) Showed interviews on process detail page with calculated indicator values; implemented choice algorithm in case of divergent risk values between multiple interviews on the same process. Removed legacy code.
- [1.4.4] (25/09/2023) Implemented logic for calculating indicators P1, P2, P3, P4, P5 in the context of the single interview; implemented method for performing server-side checks regarding the validity of the response
- [1.4.3] (18/09/2023) First draft of implementation of indicators P1, P2, P3 in the context of single interview
- [1.4.2] (12/09/2023) Corrected charset handling in response edit form; showed stage id as title in process detail page
- [1.4.1] (11/09/2023) Added handling in interview of question type that has percentage as answer; added svg images as tree markers; revised landing page; moved some predefined searches from landing page to free search page; expanded visible application width. 
- [1.4.0] (04/09/2023) Corrected label, typography; added screenshots.
- [1.3.9] (29/08/2023) Implemented interview consultation page to hide/show unanswered questions. Graphical improvements to presentation of process details; revised login page graphics, link to download Comma Separated Values files, labels and other ornaments.
- [1.3.8] (24/08/2023) Implemented facility in interview consultation to hide/show unanswered questions.
- [1.3.7] (01/08/2023) Implemented facility to enter ternary relationship between corrupt risk and enabling factor, in the context of a process. Bug fix.
- [1.3.6] (24/07/2023) Implemented register of risk enablers; shown risk-related enablers in the context of a process.
- [1.3.5] (06/26/2023) Implemented function entering relationship between corruption risk and process surveyed by anti-corruption
- [1.3.4] (27/03/2023) Delegated interview management (viewing, entering, updating responses) to a new sofware component twinned from Risk Command
- [1.3.3] (06/03/2023) Implemented function of adding a corruption risk to the risk register
- [1.3.2] (02/28/2023) Added output details page, listing processes generated from current output where it acted as process input. Implemented output list page. Bug fixes.
- [1.3.1] (15/02/2023) Added functionality to download risk register in CSV format (extracts only risk with associated processes, as per business rules)
- [1.3.0] (13/02/2023) Added corrupt risk details page, listing processes exposed to selected risk. Implemented stand-alone process details page. Reorganized landing page.
- [1.2.9] (08/02/2023) Minor presentation improvements in process details: highlighted risk area, showed contingent subject detail.
- [1.2.8] (06/02/2023) Expanded anti-corruption process detail: showed risk to which the process is exposed
- [1.2.7] (30/01/2023) Implemented corrupt risk register; bug fixes
- [1.2.6] (12/21/2022) Added functionality to download specific process details in CSV format
- [1.2.5] (13/12/2022) Added input, phase, output details in general process extraction CSV
- [1.2.4] (01/12/2022) Showed details of a process in a separate window for pdf printing purposes
- [1.2.3] (29/11/2022) Implemented first examples of graph generation
- [1.2.2] (23/11/2022) Added functionality to download complete process tree in CSV format
- [1.2.1] (22/11/2022) Shown in node tree processes number of inputs and steps
- [1.2.0] (21/11/2022) Implemented this documentation file.
- [1.1.9] (17/11/2022) Added detail anti-corruption process: input, steps, output

- [&le; 1.1.9] Implemented interview (choosing structure and anticorruptive process, filling in answers to questions), interview list page, various extractions in CSV, organization chart and process list in navigable tree


See [open issues](https://github.com/gbetorre/rischi/issues) for a complete list of proposed features (and known problems).

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=for-the-badge
[contributors-url]: https://github.com/othneildrew/Best-README-Template/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/othneildrew/Best-README-Template.svg?style=for-the-badge
[product-screenshot]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/product-screenshot.png
[product-login]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/login1.40.png
[product-landing]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing1.40.png
[product-landing2]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing2.0.png
[product-interview]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/interview-sample.png
[product-algorithm]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/algorithm-P3.png
[product-orgchart]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-str.png
[product-process]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-pro.png
[process-29]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/pro-29.png
[process-45]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/pro-45.png
[indicator-sample01]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample01.png
[indicator-sample02]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample02.png
[indicator-sample03]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample03.png
[pxi]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/PxI.PNG
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
