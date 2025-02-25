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
    ·
    <a href="https://github.com/gbetorre/rischi/blob/master/README.it.md">Need Italian?&nbsp;<img src="https://github.com/gbetorre/rischi/blob/main/web/img/italy.png" alt="IT" width="40" height="40"></a>
  </p>
</div>


<!-- TABLE OF CONTENTS -->

# Risk Mapping Software
The web application for <cite>corruption risk mapping</cite> is intended to help Entities, Public Administrations and investee companies to <strong>automatically quantify the corruption risk</strong> to which their organizational processes are exposed and direct them to implement appropriate countermeasures.

[![Product Landing Page][product-landing2]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing2.0.png)
<br>
<strong>*Fig. 1 - Landing page, version 2.0*</strong><br>
<br>

## About The Project

[![Goal Sample][indicator-sample01]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample01.png)
<br>
<strong>*Fig.2 - The goal of the software is to obtain, automatically, the risk value for each organizational process considered (dummy data)*</strong><br>

### In a nutshell
In brief, the <code>ROL-RMS</code> software makes it possible to:
* quantify the level of corruption risk to which each organizational process is exposed (<strong>initial risk level</strong>);
* quantify how much the risk level is reduced if a series of mitigation measures are applied to the process (<strong>estimated risk level</strong>);
* quantify how much you actually reduced that level of risk given the mitigation measures that were actually applied (<strong>actual risk level</strong>).

All these quantities (initial, estimated and actual risk level) are numerical and determined by deterministic algorithms, thus not subject to stochastic variation.

Given an initial risk level, applying certain measures will <i>always</i> result in a certain reduction in the risk level, and the process by which this reduction was determined will also be reconstructible. 
Therefore, the explainability of all this software is complete (and, of course, accessible by reading the same sources published in this repository).

The mitigation algorithms-as, moreover, are all algorithms for calculating risk, calculating PxI, etc. - were designed based on the know-how of experienced corrupt risk experts and were fully formalized in the analysis phase before moving to the implementation phase.

[![Dashboard Graphics][dashboard-graph]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-graphics.png)
<br>
<strong>*Fig.3 - By performing quantification of the actors involved, it becomes possible to easily produce aggregate reports, even in the form of infographics (dummy data)*</strong><br>

### In practice
The general workflow is divided into 4 steps:
* Step 1: Loading of structures and processes <strong>(organization mapping)</strong>
* Step 2: <strong>Quantification of the corruption risk</strong> of each process
* Step 3: <strong>Identification of mitigation measures</strong> to be applied to each process
* Step 4: <strong>Monitoring</strong> in order to check whether the planned measures have been actually implemented.

These 4 steps are designed within a synchronous flow, that is, to be completed sequentially, not in parallel.<br>

For example, one cannot move to Step 2 (risk calculation) if Step 1 (process mapping) has not been completed; 
similarly, one cannot move to Step 3 (identification of measures) if Step 2 has not been completed; and so on.<br>

This “linear” mode guides the actors in the process of mapping and management and allows them to manage, in a simplified way, the complexity of the information domain and the overall objective that is to be achieved: that is, the quantifiable, demonstrable and scientifically based reduction of the levels of corruptive risks to which organizational processes are exposed.<br>

At the end of Step 4, a comprehensive survey of the monitoring and treatment of corruption risk in the organization will have been conducted.
<br>
At this point, the process can be iterated, proceeding with a new detection: in fact, <strong>the system is set up for historicization.</strong>
<br>
Each subsequent detection can be compared with the previous one through specific multidetection dashboards, which will allow to analyze deltas and trends related to processes and related corrupt risks, from one detection to the next.

## Overview

[The next chapter](#how-the-software-works) will examine the various steps in more detail.<br>
Instead, in this section, a broad description of the overall workflow is given from the perspective of the actions put in place to achieve the overall goal.

First of all, it is appropriate to define the <strong>parties</strong> involved:
1. The anti-corruption expert or office.
2. The office managers and operators
3. The software engineer

[![Product Login Screen Shot][product-login]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/login1.95.png)
<br>
<strong>*Fig.4 - Obviously, the software is a restricted access application*</strong>
<br><br>

Regarding to the <strong>roles</strong> played: 
1. <strong>The anti-corruption expert or office</strong> aided by the software: 
  * performs risk calculations, 
  * determines which mitigation measures to apply to the processes most at risk
  * and monitors them.
2. <strong>Personnel from offices</strong> overseeing processes answer interview questions and provide values collected in monitoring.
3. <strong>The software engineer</strong> oversees the process mapping phase and assists other parties through the entire workflow.

By consulting the process mapping, carried out in Step 1 ([see previous paragraph](#about-the-project)), one becomes able to establish the list of organizational structures involved in the delivery of the relevant processes.

At that point, it is then possible to ask a series of questions to managers and operators located at those facilities about the processes produced by those facilities.

Through the analysis of the answers to these questions, the application makes it possible to obtain, automatically, a series of indices related to specific corruption risks to which the organizational processes overseen by the structures themselves are exposed.<br>


<p>
Each question, in fact, is linked to one or more specific corruption risks; therefore, depending on the answer given by the interviewed personnel, 
the application expresses specific indices and attention points and, in summary, calculates the level of risk to which the examined process is exposed.
<br><br>

[![Product Interview][product-interview]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/interview-sample.png)
<br>
<strong>*Fig.5 - Example of questions used to computate the vulnerability of an organizational process*</strong>

Specifically, for each process probed through the interview, we obtain the values of 7 probability indicators (P) and 4 impact indicators (I).
<br>

[![Goal Sample alt][indicator-sample02]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample02.png)
<br>
<strong>*Fig.6 - The answers to the questions considered for some indicator may, occasionally, not allow obtaining the risk value in the dimension considered (dummy data)*</strong>
<br><br>

[![Sample alt][indicator-sample03]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/indicator-sample03.png)
<br>
<strong>*Fig.7 - In such cases, the software reports the reason for non-calculation; if there are multiple reasons, they are shown one at a time until the problem is corrected (dummy data)*</strong>
<br><br>

Crossing the values obtained in the indicators of probability (P) with those obtained in the indicators of impact (I) we obtain, for each organizational process surveyed, a synthetic index P x I, which expresses the final level of risk to which the process itself is exposed.

By linking the risk to the (counter)measures, it is also possible to obtain a number of suggestions about the organizational actions to be implemented in order to reduce the specific corruption risk identified.

# How the software works

Obviously, the <strong>Rischi On Line: Risk Mapping Software <code>(ROL-RMS)</code></strong> application relies on a database, specifically a PostgreSQL-type 
relational database (version 12 and later), in which the questions that will be submitted to the facilities in the interviews are populated.

[![DB representation, layout circular][schema-physical]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/DB-circular.png)
<br>
<strong>*Fig.8 - Graphic representation of the entities and relations of the database, Layout: Circular (powered by yFiles)*</strong>
<br><br>

## Step 1: Context identification (organizational mapping)
In the first stage, the loading of organizational structures (organizational chart) is carried out, as well as the loading of organizational processes that are produced by these structures.

These uploads to the database can be done via entry queries or via ETL but, under study, there is a mode of bulk uploading via appropriately formatted file uploads.

Structures are organized in a tree with various levels while processes are structured in 3 main levels (macro-process, process and sub-process). 

[![Product Sample OrgChart][product-orgchart]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-str.png)
<br>
<strong>*Fig.9 - Organizational chart navigation function*</strong><br><br>

[![Product Sample Macro][product-process]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-pro.png)
<br>
<strong>*Fig.10 - Navigation function in the macroprocess - and process - tree*</strong>

In the literature on process mapping, there are a variety of taxonomies that can be adopted to classify and hierarchize organizational processes.<br> 
In this software,  the following hierarchical structure was chosen:

<pre>
* Risk Area
    * |_ Macroprocess
       *   |_  Process
           *     |_ Subprocess
</pre>

These entities are related to each other by composition relationships.<br>
The risk area is the most general level: it has few properties and aggregates macro processes, which, in turn, aggregate processes, and so on.

[![Class Diagram part Process][class-diagram]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/class-diagram.png)
<br>
<strong>*Fig.11 - Class diagram regarding the entities involved in process representation.*</strong>

Each process or subprocess (but not the macroprocess) can itself be divided into phases (or activities). 
One or more structures and one or more third parties (which are entities not structured in the organizational chart but still acting on the process step) can be associated with each phase..

The software provides special features for navigating the macroprocess tree and the organizational chart tree (see <a href=“https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-str.png”>Fig. 9</a> and <a href=“https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/nav-pro.png”>10</a>), so that you can quickly verify that the mapping corresponds to what is actually present in the organization.

Furthermore, a detail page is provided for each process that contains not only the risk levels to which the process is exposed (information of great interest given the purpose of the software), but also all other aggregate information related to the process itself, including: the inputs, steps, outputs, risks, and enabling factors.

[![Product Sample Process][process-29]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/pro-29.png)
<br>
<strong>*Fig.12 - Example of a detail page of a process surveyed for anti-corruption purposes*</strong>

## Step 2: Risk calculation (interviews and indicators)
After populating the database with structures, macroprocesses and their sublayers, one can move on to the <cite>interviews</cite> phase, which consists of asking a series of questions to a number of specific structures that preside over a specific process. 

The battery of questions is very large (more than 150) but the decision about which questions to administer can be determined by the interviewer; in fact, all questions are optional, and there are more general questions, which probably make sense to address in every interview, and more specific questions, which only make sense to administer if you are looking at very specific processes. The questions are grouped into areas of analysis and, in the case of some structures, it might even make sense to omit questions from entire areas of analysis.<br>


[![Question domains sample][question-domains]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/questions-domains.png)
<br>
<strong>*Fig.13 - Example of groupings of questions into areas of analysis*</strong>

The answers are then used to obtain the value of a series of indicators, as mentioned earlier.<br>

[![PxI analytical dashboard][dashboard-risk]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-pxi.png)
<br>
<strong>*Fig.14 - The indicator dashboard allows to consult not only the PxI value of each process but also the values of all dimensions and indicators based on which this summary index was calculated*</strong>

<strong>The calculation of the values of all indicators and the same PxI index of each process is automated!</strong> As a matter of fact, at the moment the interview is saved, the calculation of the value of all indicators and PxI is automatically processed.<br>

All - but one - among the indicators depend on the responses to the questions, so that the value obtained in every indicator (but one) is calculated through an algorithm that takes into account the responses obtained.<br>

There is only one impact indicator that does not depend on the questions instead depends on the number and type of facilities involved in the measured process.<br>

The algorithms for calculating the indicators are all different from each other.
<br><br>
[![Product Algorithm][product-algorithm]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/algorithm-P3.png)
<br>
<strong>*Fig.15 - Example (simplified) of the flowchart of the algorithm for calculating a specific probability indicator (P3: analysis/evaluation of reports received)*</strong><br>

As mentioned in the previous paragraph, through additional algorithms all values obtained in the probability indicators (global probability index <code>P</code>) 
and all values obtained in the impact indicators (global impact index <code>I</code>) are crossed.

Finally, through a classic Quantitative Risk Analysis table, the <code>P x I</code> index, or summary judgment, obtained for each process surveyed and investigated through the interviews, is eventually calculated

[![PxI][pxi]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/PxI.png)
<br>
<strong>*Fig.16 - Decision table of the algorithm for calculating PxI, with the 9 possible values derived from the arrangements with repetition D'(3,2) = 3<sup>2</sup> of the 3 possible values of P and the 3 possible values of I.*</strong>

><strong>It is important to remark that a feature of the software is thus the automation of the calculation of indicators and PxI: after surveying processes and structures, 
it is enough to conduct the interviews for the software to do the rest.</strong>

## Step 3: Risk treatment (estimated risk reduction)
Through steps 1 and 2 (i.e., process mapping and calculation of their corruptive risk), an overall view is thus obtained of the level of risk to which each organizational process surveyed is exposed.

[![PxI concise dashboard][dashboard-risk2]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-synthesis-pxi.png)
<br>
<strong>*Fig.17 - The table of PxI totaled by each process provides an overview of the levels of risk to which organizational processes are exposed*</strong>

Having carried out this mapping is a good starting point for being able to determine which mitigation/prevention measures of corruption risk should be applied to the risks themselves.

The latter is phase 3, or <strong>the phase of identifying mitigation measures that will reduce the value of the risk.</strong>

How is a mitigation measure defined?

Corruption risk mitigation measures correspond to actions aimed at: containing | calming | mitigating | preventing | treating | reducing the risk of corruption, 
depending on the type and purpose of the measure itself.<br>

(Note that, generally, one can consider the definition given in double implication, making it a good definition).<br>

Without detailing too much, let's say that a mitigation measure is a complex object, having one or more types, several relationships with organizational structures, 
and a number of specific properties (economic viability, character, number of implementation steps, and so on)</p>

[![Form to insert new measure][add-measure]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/form-measure.png)
<br>
<strong>*Fig.18 - The form to enter a new mitigation measure*</strong>

In a first step, then, the anti-corruption bureau - or expert - takes a census of all the various measures it deems appropriate to suggest, going on to form a list of measures.
[![List of inserted measure][list-measures]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/list-measures.png)
<br>
<strong>*Fig.19 - List of corrupt risk mitigation measures*</strong>

Having constituted this list of applicable measures, the problem for anti-corruption practitioners is to go on to identify which measure or measures apply to which specific risk in which specific process.
The granularity of the associations between process and measure is in fact relatively fine and needs a ternary relationship to be represented.<br>

[![Schema ER measure (part)][schema-measure]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/SchemaER-measure.png)
<br>
<strong>*Fig.20 - Detail of the ER diagram for the representation of measurements*</strong>

What happens in practice, then, is that, starting from the analysis of the levels of risk to which processes are exposed (photographed by the PxI dashboard: 
see, e.g., <a href=“https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-synthesis-pxi.png”>Fig. 17</a>) 
the expert decides that it is appropriate for the organization to implement appropriate measures.

What measures, however, to choose from among the various possible measures? That is, how to identify the best measures for each risk of any given process?<br>
The <code>ROL-RMS</code> system also comes to the rescue here: 

><strong>one of the advantages offered by the software, on this side, is the fact that the system itself suggests which measures to apply to each risk in the context of each process!</strong> 

[![Assignment measure to risk][assign-measure]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/form-measure2.png)
<br>
<strong>*Fig.21 - The form to assign a measure to a risk-process pair. The most appropriate measures are suggested by the software, but the operator is free to assign others, either in addition to or instead of those suggested.*</strong>

In fact, mitigation measures, through their typology, have an association with the enabling factor, and this relationship makes it possible to identify the context of application of these measures according to risk and process.

To summarize: the software proposes a set of measures that, based on the information it has internally, are appropriate for the considered risk within the considered process. 
However, there is nothing to prevent it from assigning others in addition to or instead of those proposed.

Once the measures have been applied, it is possible to check how risk levels vary by consulting special dashboards, which compare PxI before and after the measures have been applied.<br>

[![How to risk decrease applying measures][dashboard-risk4]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/dashboard-synthesis-measures.png)
<br>
<strong>*Fig.22 - Table showing what measures should be applied to each process and how PxI levels would vary before and after application. In the screen considered, there were reductions in risk and a level that, instead, remained unchanged.*</strong>

## Step 4: Risk certification (monitored risk reduction)
However, the measure implementation stage, just seen, is only an <i>estimate</i> of the extent to which risk can be reduced <i>if</i> the proposed measures are implemented (could be a BIG IF).
The monitoring phase, which ends the corruption risk management cycle, is used to verify whether the proposed measures have actually been implemented.

[![Monitor entrypoint][list-monitor]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/list-monitored-measures.png)
<br>
<strong>*Fig.23 - Monitoring start page.*</strong>

Since it has a number of on-demand dashboards and reports:
> <strong>the software also offers specific analytical tools to check to what extent the level of risk has changed not only as a function of the hypothetical but also the actual application of mitigation measures.</strong>

Simplifying, some reports with 3 columns will be obtained at the end of the monitoring phase:
* <strong>the initial PxI level:</strong>determined based on the responses to the questions given by the surveyed facilities;
* <strong>the level of the intermediate PxI:</strong> calculated based on the hypothetical application of mitigation measures (estimation)
* <strong>the final PxI level:</strong> recalibrated after verifying which of the required measures have actually been applied (monitoring).

This kind of report concludes the risk management cycle and is the certification of risk levels produced by the anti-corruption expert/office.

[![Schema ER monitoring (part)][schema-monitor]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/SchemaER-monitoring.png)
<br>
<strong>*Fig.24 - Part of the ER diagram for the representation of the monitored measurement and related entities*</strong>

[![List of phases with indicators][list-indicators]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/list-phases_indicators.png)
<br>
<strong>*Fig.25 - Example of a monitored measure with 2 implementation phases: on one a monitoring indicator has been assigned, on the other not yet*</strong>

[![Form to insert new monitoring indicator][add-indicator]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/form-indicator.png)
<br>
<strong>*Fig.26 - Form for entering a new monitoring indicator*</strong>


# Roadmap

Three of the functions currently implemented in the <code>ROL-RMS</code> software, namely:
<mark>
* the calculation of existing risk, 
* the suggestion about mitigation measures to be applied to the existing risk,
* the production of comparative tables to consult how risk varies as a function of hypothetical and applied measures,
</mark>

are useful tools which can be a valuable aid to the office or corrupt risk expert who needs to conduct an assessment regarding these issues in the context of an organization.<br>

At the present (version <code>2.2</code>), the software is ready to be taylored, with minimal adaptation, 
to any organization that would carry out a detailed analysis of the corruptive risk to which the processes provided by the organization itself are exposed.<br>

It is also possible to estimate, with relative accuracy, how much time is needed to customize the software to suit a specific organization.<br>
In fact, acquired:
* the size of the organization (in particular, the number of levels of the organizational chart and the absolute number of structures to be mapped)
* the number of levels and the number of processes produced by the organization itself,
  
it becomes possible to make a relatively accurate estimate of the time required 
for the interview campaign and - consequently - to obtain the results of the various risk indicators and the <code>P x I</code> summary judgment.
<br><br>

## Future developments

There are, in addition, some possible developments, which could be implemented in later versions:
* Preparation of a dashboard for RATs (Anti-Corruption and Transparency Referents) to enable them to independently fill in the answers to the questions (certifying, automatically, the data entered).
* Preparation of monitoring and reporting to enable <em>governance</em> to conduct checks on progress and results achieved through the risk mapping project.
* Preparation of appropriate research tools to enable the transparency office to obtain analytical queries on the interviews conducted.
* Implementation of multilingualism (internationalization)

### Internationalization of the textual elements
Implementing output rendering in many different languages is a relatively simple task to do by acting on software that relies on a well-structured and defined relational database - as in the present case.
A well-established model, suitable for rendering text and titles in an unfixed number of different languages, is easily implemented by extending the database by: 
* adding a translation table for each table that contains text elements to be translated, and 
* rewriting the queries with the addition of LEFT OUTER JOINs to retrieve the translated value, if any.<br>
<sub>See also the paper <cite>A Framework for the Internationalization of Data-Intensive Web Applications</cite></sub>

### Security Profiles.
[![Error 505][product-error2]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/deniedAccess.png)
<br>
<strong>*Fig.27 - Error screen in case of login attempt without authentication*</strong>

The system is already prepared to handle a number of attacks, such as SQL Injection or some Cross-site request forgery (CSRF) attacks.
It also implements the user session, the state of which it systematically checks; still, it implements some mechanisms to prevent DDOS-type attacks, such as caching.

However, if this software were to be opened to the public, a review would need to be made regarding security, and a number of additional checks would need to be implemented to ensure the validity of the assumptions made at each point of navigation, and in particular at the points where writing to the data is performed.

One trusts in the contributor's understanding regarding the fact that, since the system is currently developed for internal use, some security aspects have not been explored extensively: since resources are limited, it was preferred, in development stages, to focus on functionality rather than on these issues, which are clearly important but crucial especially in the wide-release and production stage.

The security aspects can certainly be beefed up, but the investment on this side is related to the popularity of the project: if this software, <code>ROL-RMS</code>, is doomed to remain confined within the boundaries of some Anti-Corruption and Transparency Bureau, there is clearly not much point in bothering to provide additional layers, since basic security is already provided.


<br>
Of course, even though it can be such a big help, no IT tool by itself can achieve results such as lowering corruption risk; therefore, 
any analytical insights allowed by the software will have to be examined and interpreted by anti-corruption experts.

Everyone can feel free to propose improvements and evolutions.
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### ToDo
- To Implement an internal search engine
- To Implement search on queries by textual key
- To Add asynchronous suggestions on text key typing
- To Implement search by structure
- To Implement search by process 
- To Implement search on queries and answers by scope of analysis 
- To Implement extraction of results provided by internal search engine in open data
- To Add weighting of queries according to risk (query/risk association)
- To Implement reporting and graphs on risk in relation to structure
- To Implement structure detail page
- To Implement contingent/affected subject detail page
- To Implement extraction of all organization chart data (organization chart and facilities query - extraction)
- To Add page showing processes delivered by the structure evoked asynchronously upon clicking on a structure node

# History

This section illustrates the evolution of the ROL software in the context of the various releases.<br>
Not all changes are described under each version number
but only the releases of the most significant features.<br>

Each version number is paired with the date of the commit, so by consulting the History of the repository it will be easy to
to go into all the changes made at the subversion: 
moreover, each version corresponds to a commit, but not each commit generates a version.<br>

<sub>
NOTE: By convention, in the software the version is shown in the x.xx format thus merging the sub-sub version figure with the subversion figure, while in this changelog it has the classic x.x.x format (this is to bring more descriptive accuracy).<br>
In addition, the meaning of subversions is quite different from the general one; in fact, 
it does not identify whether the version is stable or not (an aspect usually identified respectively by the final digit different from zero or equal to zero), 
nor does it have relevant jumps according to high-impact changes (e.g., moving from version 6.1.38 to version 7.0.1 of VirtualBox, which marked a fairly big change); 
in the case of the current application, in fact, version numbers have only the signficance of keeping track of releases and deploys 
(1.1.9 = XIX deploy; 1.2.0 = XX deploy; 1.9.9 = IC deploy; 2.0.0 = C deploy)</p>
<cite>(The realease date is in Italian format: sorry, should writing a script to convert all the dates, still I can do without...)</cite>
</sub>

### 2025
- [2.2.1] (25/02/2025) Added facility to insert multiple inputs through a single submission
- [2.2.0] (19/02/2025) Added facility to insert new inputs and/or link an existing input to a process
- [2.1.9] (14/02/2025) Presentation improvements (home)
- [2.1.8] (10/02/2025) First draft implementation about the page to insert a new input 
- [2.1.7] (05/02/2025) Bug fix
- [2.1.6] (03/02/2025) Added facility to insert a new process
- [2.1.5] (29/01/2025) Added facility to insert a new macroprocess
- [2.1.4] (27/01/2025) First draft implementation about the page to insert a new macroprocess
- [2.1.3] (20/01/2025) First draft implementation about the start page of the inserting a new process
- [2.1.2] (13/01/2025) Presentation improvements (badges, labels, colours)

### 2024
- [2.1.1] (02/12/2024) Implemented client-side controls in measurement form
- [2.1.0] (28/11/2024) Added facility to download process register in CSV format (extracts processes with PxI original and PxI mitigated after measures)
- [2.0.9] (25/11/2024) Added page containing form to add a measurement for monitoring; bug fix
- [2.0.8] (19/11/2024) Implemented client-side controls in form to add an indicator; added indicator details page; bug fix
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

### 2023
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

### 2022
- [1.2.6] (12/21/2022) Added functionality to download specific process details in CSV format
- [1.2.5] (13/12/2022) Added input, phase, output details in general process extraction CSV
- [1.2.4] (01/12/2022) Showed details of a process in a separate window for pdf printing purposes
- [1.2.3] (29/11/2022) Implemented first examples of graph generation
- [1.2.2] (23/11/2022) Added functionality to download complete process tree in CSV format
- [1.2.1] (22/11/2022) Shown in node tree processes number of inputs and steps
- [1.2.0] (21/11/2022) Implemented this documentation file.
- [1.1.9] (17/11/2022) Added detail anti-corruption process: input, steps, output

- [&le; 1.1.9] Implemented interview (choosing structure and anticorruptive process, filling in answers to questions), interview list page, various extractions in CSV, organization chart and process list in navigable tree


### Built With

This project uses only STANDARD technologies and established frameworks. 
For instance: 
* POJO for Java (all CONTROLLER layer); 
* Ajax for asynchronous requests (XHR); 
* Bootstrap - and its plugins - for style sheets and responsive interface (VIEW); 
* jQuery - and vanilla Javascript - for client-side DOM manipulation; 
* SQL for MODEL access; 
* JSTL (Expression Language) for VIEW construction; 
* JSON for navigation tree construction; 
and so on.

Technically, this software application is a monolithic architecture. 
It would not even be worth justifying this choice, given the nature of the project (an incremental development carried out over the years by a single software engineer), but let us recall the main advantages of a monolithic architecture over a microservices one, especially for the kind of tasks managed from this application:
* the code is all deposited in a single repository - which one documented by this README file;
* the application is easy to deploy: by running a single script, a new version is released and the server is updated in a few moments;
* you can debug the application with ease: although the computation has been parallelized (where there was no risk of race condition), a single breakpoint is sufficient to enter debug, check all the values assumed by the variables, and access to all the control mechanisms;
* the performance of a monolithic application is better than that of a microservices one because the individual components talk to each other efficiently (nevertheless, it was necessary to implement internal caching mechanisms because of the large number of calculations that need to be performed to obtain risk indicator values).

The main libraries and technologies used to develop and execute the project are down below:

* [![Java][Java]][Java-url]
* [![JavaScript][JavaScript]][javascript-url]
* [![EL][EL]][EL-url]
* [![HTML][HTML]][HTML-url]
* [![CSS][CSS]][CSS-url]
* [![SQL][SQL]][SQL-url]
* [![Bootstrap][Bootstrap.com]][Bootstrap-url]
* [![JQuery][JQuery.com]][JQuery-url]

<a href="https://github.com/gbetorre/rischi">Here</a> further details on the languages used can be found.

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


<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. 
Any contributions you make are **greatly appreciated**.

Anyone with suggestions that could improve the project can download the repository, test it locally, make changes to it, and create a pull request:

1. Fork the Project (from URL https://github.com/gbetorre/rischi/tree/main click on "Fork" button)
2. Clone the fork (`git clone https://github.com/username/rischi.git`where username is your GitHub user)
3. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
4. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
5. Push to the Branch (`git push origin feature/AmazingFeature`)
6. Open a Pull Request (click on the “Pull requests” tab then on “New pull request”; give a clear title and add a detailed description of the changes made; then click on “Create pull request”).

[![Repository features list][list-features]](https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/features.png)
<br>
<strong>*Fig.28 - List of features of a repository listed using Sourcetree software*</strong>


In order to run the software, it is necessary to deploy the database on which it rests.
A schema dump is not yet available in the repository, but to obtain a production one (containing only sample data) <a href="mailto:gianroberto.torre@gmail.com">contact the author</a>.

A more simplified way to suggest changes is to simply open an issue tagged “enhancement.”

Don't forget to starred the project!

See also: [AUTHORS](AUTHORS) file.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- LICENSE -->
## License

This project is licensed under the terms of the GNU GPL-2.0 License. See <a href="https://github.com/gbetorre/rischi/blob/main/LICENSE">`LICENSE.txt`</a> for further information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<a name="readme-contact"></a>
<!-- CONTACT -->
## Contact
To learn more and gain access to the requirements analysis document, <a href="mailto:gianroberto.torre@gmail.com">contact the author</a>.

Software Engineer: Giovanroberto Torre - [@GianroTorres](https://twitter.com/GianroTorres) - gianroberto.torre@gmail.com

Project Link: [https://github.com/gbetorre/rischi](https://github.com/gbetorre/rischi)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!--
- [1.1.9] Add Additional Templates w/ Examples
- [ ] Add "components" document to easily copy & paste sections of the readme
- [ ] Multi-language Support
    - [ ] Chinese
    - [ ] Spanish
-->

See also [open issues](https://github.com/gbetorre/rischi/issues) for a complete list of proposed functionalities (and known problems).

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=for-the-badge
[contributors-url]: https://github.com/othneildrew/Best-README-Template/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/othneildrew/Best-README-Template.svg?style=for-the-badge
[product-screenshot]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/product-screenshot.png
[product-login]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/login1.95.png
[product-landing]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing1.40.png
[product-landing2]: https://github.com/gbetorre/rischi/blob/main/web/img/screenshot/landing2.0.png
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
