<%@ include file="URL.jspf" %>
    <div class="row">
      <h3 class="mt-1 m-0 font-weight-bold">Funzioni</h3>
      <hr class="riga" />
      <div class="col-xl-4 col-md-6 mx-auto">
        <div class="card bg-warning text-dark mb-4 shadow-nohover">
          <div class="card-body landing-card-body d-flex align-items-center">
            <i class="fa-solid fa-microphone-lines fa-5x"></i>&nbsp;&nbsp;
            <span class="h4 landing-card-title">&nbsp;&nbsp;&nbsp;&nbsp;Nuova Intervista</span>
          </div>
          <a class="small stretched-link" href="${str}"></a>
        </div>
      </div>
      <div class="col-xl-4 col-md-6 mx-auto">
        <div class="card bgAct4 mb-4">
          <div class="card-body landing-card-body d-flex align-items-center">
            <i class="fas fa-sitemap fa-5x"></i>&nbsp;&nbsp;
            <span class="h4 landing-card-title">Organigramma e strutture</span>
          </div>
          <a class="small text-white stretched-link" href="${st}"></a>
        </div>
      </div>
      <div class="col-xl-4 col-md-6 mx-auto">
        <div class="card bg-success text-white mb-4 shadow-nohover">
          <div class="card-body landing-card-body d-flex align-items-center">
            <i class="fas fa-cogs fa-5x"></i>&nbsp;&nbsp;
            <span class="h4 landing-card-title">&nbsp;&nbsp;&nbsp;&nbsp;Processi</span>
          </div>
          <a class="small stretched-link" href="${pr}"></a>
        </div>
      </div>
    </div>
    <hr class="separatore" /><br />
    <div class="row">
      <h3 class="mt-1 m-0 font-weight-bold">Strumenti di Analisi</h3>
      <hr class="riga" />
      <div class="col-xl-4 col-md-6 mx-auto">
        <div class="card bgcolor2 mb-4">
          <div class="card-body landing-card-body d-flex align-items-center">
            <i class="fa-solid fa-headphones fa-5x"></i>&nbsp;&nbsp;
            <span class="h4 landing-card-title">Archivio Interviste</span>
          </div>
          <a class="small text-white stretched-link" href="${sqs}"></a>
        </div>
      </div>
<%--  <div class="col-xl-4 col-md-6 mx-auto">
        <div class="card bg-primary text-white mb-4">
          <div class="card-body landing-card-body d-flex align-items-center">
            <i class="fa-solid fa-cat fa-5x"></i>&nbsp;&nbsp;
            <span class="h4 landing-card-title">Cruscotto RAT</span>
          </div>
           <a class="small text-white stretched-link" href="${st}"></a>
        </div>
      </div> --%>
      <div class="col-xl-4 col-md-6 mx-auto">
        <div class="card bgAct12 mb-4">
          <div class="card-body landing-card-body d-flex align-items-center">
            <i class="fa-solid fa-magnifying-glass-chart fa-5x"></i>&nbsp;&nbsp;
            <span class="h4 landing-card-title">Ricerca Libera</span>
          </div>
           <a class="small text-white stretched-link" href="${ris}"></a>
        </div>
      </div>
      <div class="col-xl-4 col-md-6 mx-auto">
        <div class="card bg-danger text-white mb-4">
          <div class="card-body landing-card-body d-flex align-items-center">
            <i class="fas fa-chart-line fa-5x"></i>&nbsp;&nbsp;
            <span class="h4 landing-card-title">Monitoraggio e Report</span>
          </div>
          <a class="small text-white stretched-link" href="${mu}"></a>
        </div>
      </div>
    </div>
