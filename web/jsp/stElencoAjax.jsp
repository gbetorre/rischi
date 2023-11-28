<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
<c:set var="listaProcessi" value="${requestScope.lista}" scope="page" />
<c:catch var="exception">
      <hr class="separatore" />
  <c:choose>
    <c:when test="${pageScope.listaProcessi.size() gt zero}">
      <c:set var="first" value="${pageScope.listaProcessi.get(zero)}" />
      <c:set var="uo" value="${first.dipart}" />
      <h4 id="tab-per" class="col-12">
        Processi collegati a ${uo.prefisso} ${uo.nome}
      </h4>
      <hr class="riga"/>
      <div class="lightTable sezioneElenco overflow-auto" style="height: 620px;">
        <ul class="list-group list-group-flush">
          <c:forEach var="m" items="${pageScope.listaProcessi}">
            <li class="list-group-item macroprocesso ${m.codice}">
              <b><c:out value="${m.codice}"/>: </b>
              <c:out value="${m.nome}"/><br/>
              <i>Personale FTE: <c:out value="${m.fte}"/>; Quotaparte: <c:out value="${m.quotaParte}"/>%</i>
              <ul>
              <c:forEach var="pe" items="${m.persone}">
                <li>
                  <a href="${pes}&idp=${pe.id}">
                    <c:out value="${pe.nome}" escapeXml="false" />
                    <c:out value="${pe.cognome}" escapeXml="false" />
                  </a>
                  <c:out value="(${pe.note})" />
                </li>
              </c:forEach>
              </ul>
              <c:if test="${not empty m.processi}">
                <ul>
                  <c:forEach var="p" items="${m.processi}">
                    <li class="list-group-item">
                      <b><c:out value="${p.codice}"/>: </b>
                      <c:out value="${p.nome}"/><br/>
                      <i>Personale FTE: <c:out value="${p.fte}"/>; Quotaparte: <c:out value="${p.quotaParte}"/>%</i>
                      <ul>
                      <c:forEach var="per" items="${p.persone}">
                        <li>
                          <a href="${pes}&idp=${per.id}">
                            <c:out value="${per.nome}" escapeXml="false" />
                            <c:out value="${per.cognome}" escapeXml="false" />
                          </a>
                          <c:out value="(${per.note})" />
                        </li>
                      </c:forEach>
                      </ul>
                    </li>
                  </c:forEach>
                </ul>
              </c:if>
            </li>
          </c:forEach>
        </ul>
      </div>
    </c:when>
    <c:otherwise>
    <h4 id="tab-per" class="col-12">
        Nessun processo trovato
    </h4>
    </c:otherwise>
  </c:choose>
</c:catch>
<c:out value="${exception}" />