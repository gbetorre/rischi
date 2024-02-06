
           
    <hr class="separatore" />
    <h3 class="mt-1 m-0 font-weight-bold float-left">Grafici di esempio</h3>    
    <hr class="riga"/>

    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
      google.charts.load("current", {packages:["corechart"]});
      google.charts.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
          ['Area di rischio', 'Numero di rischi'],
          ['ACQUISIZIONE E GESTIONE DEL PERSONALE',  63],
          ['CONTRATTI PUBBLICI',  43],
          ['GESTIONE DELLE ATTIVITÀ DI RICERCA',  35],
          ['PROVVEDIMENTI AMPLIATIVI DELLA SFERA GIURIDICA DEI DESTINATARI CON EFFETTI ECONOMICI DIRETTI',  31],
          ['INCARICHI, NOMINE E COLLABORAZIONI',  11]
        ]);

        var options = {
          title: 'Aree di rischio e relativo numero di rischi associati',
          pieHole: 0.4,
          chartArea:{left:50,top:30,width:800,height:'75%'}
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
                ['DIREZIONE GENERALE',  46],
                ['DIREZIONE RISORSE UMANE', 39],   
                ['DIREZIONE TECNICA, GARE-ACQUISTI E LOGISTICA',  24],
                ['DIREZIONE OFFERTA FORMATIVA, SERVIZI E SEGRETERIE STUDENTI',  12],     
                ['DIREZIONE RISORSE FINANZIARIE', 12],
                ['AREA RICERCA',  5],                
                ['CENTRO LINGUISTICO D\'ATENEO', 1],
                ['SCUOLA DI MEDICINA E CHIRURGIA',  1]
            ]);
      
            var options = {
              title: 'Numero di fasi gestite da strutture di II livello \n (direttamente o attraverso loro suddivisioni)',
              hAxis: {
                title: 'Strutture di II livello',
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
<%--
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
 