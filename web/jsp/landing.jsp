<%@ include file="URL.jspf" %>
    <div class="row">
      <h3 class="mt-1 m-0 font-weight-bold">Analisi singola rilevazione</h3>
      <hr class="riga" />
        <div class="col-xl-3 col-md-6 mx-auto">
        <div class="card bg-primary text-white mb-4">
          <div class="card-body landing-card-body d-flex align-items-center">
            <i class="fas fa-sitemap fa-5x"></i>&nbsp;&nbsp;
            <span class="h3 landing-card-title">Organigramma e strutture</span>
          </div>
          <a class="small text-white stretched-link" href="${st}"></a>
        </div>
      </div>
      <div class="col-xl-3 col-md-6 mx-auto">
        <div class="card bg-warning text-dark mb-4 shadow-nohover">
          <div class="card-body landing-card-body d-flex align-items-center">
            <i class="fas fa-cogs fa-5x"></i>&nbsp;&nbsp;
            <span class="h3 landing-card-title">&nbsp;&nbsp;&nbsp;&nbsp;Processi</span>
          </div>
          <a class="small stretched-link" href="${pr}"></a>
        </div>
      </div>
      <div class="col-xl-3 col-md-6 mx-auto">
        <div class="card bg-success text-white mb-4">
          <div class="card-body landing-card-body d-flex align-items-center">
            <i class="fas fa-users fa-5x"></i>&nbsp;&nbsp;
            <span class="h3 landing-card-title">Risorse umane</span>
          </div>
          <a class="small text-white stretched-link" href="${pe}"></a>
        </div>
      </div>
    </div>
    <hr class="separatore" /><br />
    <div class="row">
      <h3 class="mt-1 m-0 font-weight-bold">Analisi aggregate</h3>
      <hr class="riga" />
      <div class="col-xl-3 col-md-6 mx-auto">
        <div class="card bg-danger text-white mb-4">
          <div class="card-body landing-card-body d-flex align-items-center">
            <i class="fas fa-chart-line fa-5x"></i>&nbsp;&nbsp;
            <span class="h3 landing-card-title">Multi-rilevazione</span>
          </div>
<%--          <a class="small text-white stretched-link" href="${mul}"></a>--%>
        </div>
      </div>
    </div>
