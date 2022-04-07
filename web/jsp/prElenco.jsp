<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:choose>
  <c:when test="${not empty requestScope.macroprocessi}">
    <div class="row">
      <h3 class="mt-1 m-0 font-weight-bold">Processi</h3>
      <hr class="riga"/>
      <div id="chart_div" class="col-6">
      </div>
      <div class="col-6">
        <div class="lightTable sezioneElenco overflow-auto" style="height: 620px;">
          <ul class="list-group list-group-flush">
            <c:forEach var="m" items="${requestScope.macroprocessi}">
              <li class="list-group-item macroprocesso ${m.codice}">
                <b><c:out value="${m.codice}"/>:</b> <c:out value="${m.nome}"/><br/>
                <i>Personale FTE: <fmt:formatNumber type="number" value="${m.fte}" maxFractionDigits="2"/>; Quotaparte:
                  <fmt:formatNumber type="number" value="${m.quotaParte}" maxFractionDigits="2"/>%</i>
                <c:if test="${not empty m.processi}">
                  <ul class="list-group list-group-flush processo ${p.codice}">
                    <c:forEach var="p" items="${m.processi}">
                      <li class="list-group-item">
                        <b><c:out value="${p.codice}"/></b> <c:out value="${p.nome}"/><br/>
                        <i>Personale FTE: <fmt:formatNumber type="number" value="${p.fte}" maxFractionDigits="2"/>;
                          Quotaparte: <fmt:formatNumber type="number" value="${p.quotaParte}"
                                                        maxFractionDigits="2"/>%</i>
                      </li>
                    </c:forEach>
                  </ul>
                </c:if>
              </li>
            </c:forEach>
          </ul>
        </div>
        <a href="${macCSV}">
          <i class="fas fa-download"></i> <span class="lead">Scarica tutti i dati</span>
        </a>
      </div>
    </div>
    <div class="row" id="tab-lst"></div>
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript" src="https://d3js.org/d3.v6.min.js"></script>
    <script type="text/javascript">
      let count = 0;

      d3Locale = {
        thousands: ".",
        grouping: [3],
        currency: ["€", ""],
        decimal: ","
      };
      d3.formatDefaultLocale(d3Locale);


      width = height = $("#chart_div").width();

      const x = d3.scaleLinear().rangeRound([0, width]);
      const y = d3.scaleLinear().rangeRound([0, height]);

      // When zooming in, draw the new nodes on top, and fade them in.
      function zoomin(d) {
        const group0 = group.attr("pointer-events", "none");
        const group1 = group = svg.append("g").call(render, d);

        x.domain([d.x0, d.x1]);
        y.domain([d.y0, d.y1]);

        svg.transition()
          .duration(750)
          .call(t => group0.transition(t).remove()
            .call(position, d.parent))
          .call(t => group1.transition(t)
            .attrTween("opacity", () => d3.interpolate(0, 1))
            .call(position, d));

        $('li.macroprocesso').css("display", "none"); // nascondo tutti i macroprocessi
        $('li[class*="' + d.id + '"]').css("display", "inline"); // mostro solo il (macro)processo selezionato
        $('li[class*="' + d.id + '"] > ul').css("display", "block"); // in caso mostro i processi del macroprocesso selezionato
        if (d.data[5] === 'processo')
          $('li[class*="' + d.id.substring(0, 10) + '"]').css("display", "inline"); // mostro il macroprocesso padre del processo selezionato

        // Ajax call per lista persone
        var url = "data?q=pr";
        var params = "&r=${param['r']}&" + (d.data[5] === 'macroprocesso' ? "idM=" : "idR=") + d.data[4];
        updateCount = function (data, textStatus, jqXHR) {
          $("div#tab-lst").html(data);
        }
        ajaxCall(url, "GET", params, '#tab-lst', updateCount).then(response => {
          $("span#macro_or_processo").text(d.data[5]);
          $("span#cod_macro_or_processo").text(d.data[0]);
        });
      }

      // When zooming out, draw the old nodes on top, and fade them out.
      function zoomout(d) {
        const group0 = group.attr("pointer-events", "none");
        const group1 = group = svg.insert("g", "*").call(render, d.parent);

        x.domain([d.parent.x0, d.parent.x1]);
        y.domain([d.parent.y0, d.parent.y1]);

        svg.transition()
          .duration(750)
          .call(t => group0.transition(t).remove()
            .attrTween("opacity", () => d3.interpolate(1, 0))
            .call(position, d))
          .call(t => group1.transition(t)
            .call(position, d.parent));

        if (d.data[5] === 'macroprocesso') {
          $('ul.processo').css("display", "none"); // nascondo tutti i processi
          $('li.macroprocesso').css("display", "inline"); // mostro tutti i macroprocessi
          $('#org-div').css("display", "none");
        }
        else {
          $('li.macroprocesso').css("display", "none"); // nascondo tutti i macroprocessi
          $('li[class*="' + d.data[1] + '"]').css("display", "inline"); // mostro solo il (macro)processo selezionato
          $('li[class*="' + d.data[1] + '"] > ul').css("display", "block"); // in caso mostro i processi del macroprocesso selezionato
        }
        // Ajax call per lista persone
        if (d.data[5] === 'processo') {
          var url = "data?q=pr";
          var params = "p=mac" + "&r=${param['r']}&" + "idM=" + d.data[6];
          updateCount = function (data, textStatus, jqXHR) {
            $("div#tab-lst").html(data);
          }
          ajaxCall(url, "GET", params, '#tab-lst', updateCount).then(response => {
            $("span#macro_or_processo").text('macroprocesso');
            $("span#cod_macro_or_processo").text(d.data[1]);
          });
        }
      }

      format = d3.format("~r")

      // Preparo i dati
      data = []
      data.push(['Macroprocessi', null, null, null, null])
      data.push(
        <c:forEach var="m" items="${requestScope.macroprocessi}">
          <c:out value="['${m.codice}', 'Macroprocessi', ${m.fte}, ${m.quotaParte}, '${m.id}', 'macroprocesso']," escapeXml="false" />
          <c:if test="${empty m.processi}">
            <c:out value="['${m.codice} ', '${m.codice}', ${m.fte}, ${m.quotaParte}, '${m.id}', 'macroprocesso']," escapeXml="false" />
          </c:if>
        </c:forEach>
      )
      data.push(
        <c:forEach var="m" items="${requestScope.macroprocessi}" varStatus="statusM">
          <c:forEach var="p" items="${m.processi}" varStatus="statusP">
            <c:out value="['${p.codice}', '${m.codice}', ${p.fte}, ${p.quotaParte}, '${p.id}', 'processo', '${m.id}']," escapeXml="false" />
            <c:out value="['${p.codice} ', '${p.codice}', ${p.fte}, ${p.quotaParte}, '${p.id}', 'processo', '${m.id}']," escapeXml="false" />
          </c:forEach>
        </c:forEach>
      )

      // stratify the data: reformatting for d3.js
      const root = d3.stratify()
        .id(d => d[0])
        .parentId(d => d[1])
        (data);

      root.sum(d => +d[2]);   // Compute the numeric value for each entity
      root.sort((a, b) => (/*b.height - a.height ||*/ b.value - a.value));

      // Then d3.treemap computes the position of each element of the hierarchy
      // The coordinates are added to the root object above
      const treemap = d3.treemap()
        .tile(tiling)
        (root);

      // append the svg object to the page
      const svg = d3.select("#chart_div")
        .append("svg")
        .attr("viewBox", [0.5, -30.5, width, height + 30])
        .style("font", "10px sans-serif");

      let group = svg.append("g")
        .call(render, treemap);

      function render(group, root) {
        const node = group
          .selectAll("g")
          .data(root.children.concat(root))
          .join("g")

        node.attr("mp", d => d.data[0])

        node.filter(d => d === root ? d.parent : d.children)
          .attr("cursor", "pointer")
          .on("click", (event, d) => d === root ? zoomout(root) : zoomin(d));

        node.append("title")
          .text(d => d.data[0] + '\nPersonale FTE: ' + format(d.data[2]) + '\nQuotaparte: ' + (d === root ? 100 : format(d.data[3])) + '%');

        node.append("rect")
          .attr("id", d => (d.leafUid = uid("leaf")).id)
          .attr("fill", d => process_color(d.data[0].split('-')[0]))
          .attr("stroke", "#ffffff")
          .attr("fill-opacity", 1)

        node.append("clipPath")
          .attr("id", d => (d.clipUid = uid("clip")).id)
          .append("use")
          .attr("xlink:href", d => d.leafUid.href);

        node.append("text")
          .attr("clip-path", d => d.clipUid)
          .attr("font-weight", d => d === root ? "bold" : null)
          .attr("font-size", d => d === root ? "120%" : "100%")
          .selectAll("tspan")
          .data(d => d.data[0].split(/(?=[A-Z][a-z])|\s+/g))
          .join("tspan")
          .attr("x", 3)
          .attr("y", (d, i, nodes) => ((i === nodes.length - 1) * 0.3 + 1.1 + i * 0.9) + 'em')
          //.attr("fill-opacity", (d, i, nodes) => i === nodes.length - 1 ? 0.7 : null)
          .text(d => d);

        group.call(position, root);
      }

      // Se è stato richiesto un particolare (macro)processo simulo i click sulla Treemap
      // in modo da navigare fino al (macro)processo d'interesse
      <c:if test="${requestScope.pr ne '-'}">
        pr = '<c:out value="${requestScope.pr}"/>';
        // Se pr rappresenta il codice di un processo prima simulo il click sul suo macroprocesso
        if (/^[A-Z]+-\d\d\.\d\d\.\d\d$/.test(pr)) {
          d3.select('g[mp="' + pr.substr(0, pr.length - 3) + '"]').node().dispatchEvent(new Event('click'));
        }
        // imposto una minima attesa perché l'eventuale precedente click sul macroprocesso abbia effetto
        setTimeout(function () {
          d3.select('g[mp="' + pr + '"]').node().dispatchEvent(new Event('click'));
        }, 100);
      </c:if>

    </script>
  </c:when>
  <c:otherwise>
    <div class="alert alert-danger">
      <strong>Spiacente
        <c:out value="${sessionScope.usr.nome}"/>
        <c:out value="${sessionScope.usr.cognome}"/><br/>
      </strong>
      <p>
        Non sono stati trovati processi collegati alla rilevazione ${param['r']}.<br/>
        <a href="${pr}"><i class="fas fa-home"></i> Torna alla home</a>
      </p>
    </div>
  </c:otherwise>
</c:choose>
