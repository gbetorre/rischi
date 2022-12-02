
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
    