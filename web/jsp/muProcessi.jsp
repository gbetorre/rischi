<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="mats" value="${requestScope.macroProcessi}" scope="page" />

<style>
.grid {
  display: grid;
  grid-template-columns: repeat(15,130px);
  gap: 10px;
  color: #fff;
  height: 600px;
}
.entry {
  background: #999;
  padding: 1em 2em;
}
.entry.root {
  background: #000; z-index: 10!important;
}
.entry.root, .entry.head {
  position: sticky;
  top: 0; left: 0;
  background: #444;
  z-index: 1;
}
</style>


    <h3 class="mt-1 m-0 font-weight-bold float-left">Report processi e rischi</h3>    
    <hr class="riga"/>
    <div class="grid scrollX">
      <!-- columns header -->
      <div class="entry root text-center"><span>PROCESSO</span></div>
      <div class="entry head text-center"><span>PxI</span></div>
      <div class="entry head text-center"><span>P</span></div>
      <div class="entry head text-center"><span>I</span></div>
      <div class="entry head text-center">P1</div>
      <div class="entry head text-center">P2</div>
      <div class="entry head text-center">P3</div>
      <div class="entry head text-center">P4</div>
      <div class="entry head text-center">P5</div>
      <div class="entry head text-center">P6</div>
      <div class="entry head text-center">P7</div>
      <div class="entry head text-center">I1</div>
      <div class="entry head text-center">I2</div>
      <div class="entry head text-center">I3</div>
      <div class="entry head text-center">I4</div>
      <!-- normal cells -->
    <c:forEach var="mat" items="${mats}">
      <div class="entry head bgAct4">
        <span class="textcolormaroon"><c:out value="${mat.nome}" /></span>
      </div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <c:forEach var="pat" items="${mat.processi}" varStatus="status">
      <div class="entry head bgAct4">           
        <a href="${initParam.appName}/?q=pr&p=pro&pliv=${pat.id}&liv=${pat.livello}&r=${param['r']}">
          <c:out value="${status.count}) ${pat.nome}" />
        </a>
      </div>
      <div class="entry">
        <c:out value="${pat.indicatori.get('PI').informativa}" />
      </div>
      <div class="entry"><c:out value="${pat.indicatori.get('P').informativa}" /></div>
      <div class="entry"><c:out value="${pat.indicatori.get('I').informativa}" /></div>
      <div class="text-center noHeader bgcolor-${fn:toLowerCase(pat.indicatori.get('P1').informativa)}">
        <c:out value="${pat.indicatori.get('P1').informativa}" />
      </div>
      <div class="text-center noHeader bgcolor-${fn:toLowerCase(pat.indicatori.get('P2').informativa)}">
        <c:out value="${pat.indicatori.get('P2').informativa}" />
      </div>
      <div class="text-center noHeader bgcolor-${fn:toLowerCase(pat.indicatori.get('P3').informativa)}">
        <c:out value="${pat.indicatori.get('P3').informativa}" />
      </div>
      <div class="text-center noHeader bgcolor-${fn:toLowerCase(pat.indicatori.get('P4').informativa)}">
        <c:out value="${pat.indicatori.get('P4').informativa}" />
      </div>
      <div class="text-center noHeader bgcolor-${fn:toLowerCase(pat.indicatori.get('P5').informativa)}">
        <c:out value="${pat.indicatori.get('P5').informativa}" />
      </div>
      <div class="entry"></div>
      <div class="entry"></div>
      <div class="entry"></div>
      <div class="entry"></div>
      <div class="entry"></div>
      <div class="entry"></div>
      </c:forEach> 
    </c:forEach>
    </div>
          &nbsp;
          <a href="${mro}&msg=refresh_ce" type="button" class="btn btn-primary float-right" value="Update">
            <i class="fa-solid fa-arrow-rotate-right"></i>
            Ricalcola
          </a>

<%--
    <h3 class="mt-1 m-0 font-weight-bold float-left">Rischi e Processi</h3>    
    <hr class="riga"/>
    <div class="row reportStateAct" id="headState">
      <div class="col-sm-5"></div>
      <div class="col-sm-1 bgSts21 text-center">P1</div>
      <div class="col-sm-1 bgSts22 text-center">P2</div>
      <div class="col-sm-1 bgSts23 text-center">P3</div>
      <div class="col-sm-1 bgSts24 text-center">P4</div>
      <div class="col-sm-1 bgSts25 text-center">P5</div>
      <div class="col-sm-1 bgSts26 text-center">P6</div>
      <div class="col-sm-1 bgSts26 text-center">P7</div>
    </div>
  <c:forEach var="mat" items="${mats}">
    <div class="row reportWpRow">
      <div class="col-5 reportWpHead">
        <a href="#">
          <strong><c:out value="${mat.nome}" /></strong>
        </a>
      </div>
      <div class="col-1 bgSts21"></div>
      <div class="col-1 bgSts23"></div>
      <div class="col-1 bgSts25"></div>
      <div class="col-1 bgSts21"></div>
      <div class="col-1 bgSts23"></div>
      <div class="col-1 bgSts25"></div>
      <div class="col-1 bgSts21"></div>
    </div>
    <c:forEach var="pat" items="${mat.processi}" varStatus="status">
          <div class="row">
            <div class="col-1 reportWpRow"></div>
            <div class="col-4 reportAct">
            
              <a href="${initParam.appName}/?q=pr&p=pro&pliv=${pat.id}&liv=${pat.livello}&r=${param['r']}">
                <c:out value="${status.count}) ${pat.nome}" />
              </a>
            </div>
            
              
              
              <div class="col-1 text-center bgcolor-${fn:toLowerCase(pat.indicatori.get('P1').informativa)}"><c:out value="${pat.indicatori.get('P1').informativa}" /></div>
            
              
              
              <div class="col-1 bgSts22 " title="Dal 01/01/2021 al 31/12/2021"></div>
            
              
              
                
              
              <div class="col-1 bgSts23 bgAct23" title="Dal 01/01/2021 al 31/12/2021"></div>
            
              
              
              <div class="col-1 bgSts24 " title="Dal 01/01/2021 al 31/12/2021"></div>
            
              
              
              <div class="col-1 bgSts25 " title="Dal 01/01/2021 al 31/12/2021"></div>
            
              
              
              <div class="col-1 bgSts26 " title="Dal 01/01/2021 al 31/12/2021"></div>
              <div class="col-1 bgSts26 " title="Dal 01/01/2021 al 31/12/2021"></div>
          </div>
      </c:forEach> 
    </c:forEach>


          

            

            
    <hr class="separatore" />
    <h3 class="mt-1 m-0 font-weight-bold float-left">Grafici di esempio</h3>    
    <hr class="riga"/>

    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
      google.charts.load("current", {packages:["corechart"]});
      google.charts.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
          ['Task', 'Hours per Day'],
          ['Direzione RISORSE FINANZIARIE',     2],
          ['Direzione GENERALE',      1],
          ['Centro LINGUISTICO D\'ATENEO',      3]
        ]);

        var options = {
          title: 'Numero di fasi gestite da direzioni, dipartimenti, centri',
          pieHole: 0.4,
        };

        var chart = new google.visualization.PieChart(document.getElementById('donutchart'));
        chart.draw(data, options);
      }
    </script>
    <div id="donutchart" style="width: 900px; height: 500px;"></div>

    <script type="text/javascript">
      google.charts.load('current', {packages: ['corechart', 'bar']});
      google.charts.setOnLoadCallback(drawBar);
      
      function drawBar() {
      
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Time of Day');
            data.addColumn('number', 'fasi');
      
            data.addRows([
                ['Tutti i dipartimenti',     14],
                ['Commissione (con il supporto dell\'UO Concorsi)',  9],
                ['Rettore (con il supporto dell\'UO Concorsi)',  6],
                ['Direttore Generale (con il supporto dell\'UO Concorsi)', 4],
                ['Direttore Generale (con il supporto dell\'UO Personale Tecnico-Amministrativo)', 3],
                ['Consiglio di Amministrazione e Rettore (con il supporto dell\'UO Concorsi)', 3],
                ['Senato Accademico e Consiglio di Amministrazione (con il supporto dell\'UO Organi)', 2],
                ['Commissione (con il supporto dell\'UO Personale Tecnico-Amministrativo)',  2],
                ['Tutti i Centri/Scuole/Poli',  1]
            ]);
      
            var options = {
              title: 'Numero di fasi gestite da soggetti diversi da strutture',
              hAxis: {
                title: 'Soggetti contingenti',
                textStyle: {
                    color: "#000",
                    fontName: "sans-serif",
                    fontSize: 8,
                    bold: true,
                    italic: false
                }
              },
              vAxis: {
                title: 'Numero di fasi gestite'
              }
              
            };
      
            var chart = new google.visualization.ColumnChart(
              document.getElementById('chart_div'));
      
            chart.draw(data, options);
      }
    </script>
    <div id="chart_div" style="width: 900px; height: 500px;"></div>

    <script type="text/javascript">
      google.charts.load('current', {'packages':['bar']});
      google.charts.setOnLoadCallback(drawMaterial);

      function drawMaterial() {
        var data = google.visualization.arrayToDataTable([
          ['Anno', 'Istanze totali', 'Istanze in materia di TFA'],
          ['2020', 215, 70],
          ['2021', 177, 75],
          ['2022', 196, 75]
        ]);

        var options = {
          chart: {
            title: 'Istanze di accesso agli atti',
            subtitle: 'Istanze totali e in materia di TFA: 2020-2022',
          }
        };

        var chart = new google.charts.Bar(document.getElementById('columnchart_material'));

        chart.draw(data, google.charts.Bar.convertOptions(options));
      }
    </script>
    <div id="columnchart_material" style="width: 800px; height: 500px;"></div>
     --%>
    