<%@ include file="URL.jspf" %>
    <div id="layoutSidenav_content">
      <div class="container-fluid px-4">
        <h1 class="mt-4">Dashboard</h1>
        <hr class="separatore" />
        <ol class="breadcrumb mb-4">
          <li class="breadcrumb-item active"><strong>Funzioni</strong></li>
        </ol>
        <div class="row">
          <div class="col-xl-3 col-md-6">
            <div class="card bg-warning text-dark mb-4 shadow-nohover">
              <div class="card-body landing-card-body d-flex align-items-center">
                <i class="fa-solid fa-microphone-lines fa-5x"></i>&nbsp;&nbsp;
                <span class="h4 landing-card-title">&nbsp;&nbsp;&nbsp;&nbsp;Nuova Intervista</span>
              </div>
              <a class="small stretched-link" href="${str}"></a>
            </div>
          </div>
          <div class="col-xl-3 col-md-6">
            <div class="card bg-primary mb-4">
              <div class="card-body landing-card-body d-flex align-items-center">
                <i class="fas fa-sitemap fa-5x"></i>&nbsp;&nbsp;
                <span class="h4 landing-card-title text-white">Organigramma e strutture</span>
              </div>
              <a class="small text-white stretched-link" href="${st}"></a>
            </div>
          </div>
          <div class="col-xl-3 col-md-6">
            <div class="card bg-success text-white mb-4 shadow-nohover">
              <div class="card-body landing-card-body d-flex align-items-center">
                <i class="fas fa-cogs fa-5x"></i>&nbsp;&nbsp;
                <span class="h4 landing-card-title">&nbsp;&nbsp;&nbsp;&nbsp;Processi</span>
              </div>
              <a class="small stretched-link" href="${pr}"></a>
            </div>
          </div>
          <div class="col-xl-3 col-md-6">
            <div class="card bgAct10 mb-4">
              <div class="card-body landing-card-body d-flex align-items-center">
                <i class="fa-solid fa-triangle-exclamation fa-5x"></i>&nbsp;&nbsp;
                <span class="h4 landing-card-title">Registro dei Rischi</span>
              </div>
              <a class="small text-white stretched-link" href="${ri}"></a>
            </div>
          </div>
          <div class="col-xl-3 col-md-6">
            <div class="card bgAct26 mb-4">
              <div class="card-body landing-card-body d-flex align-items-center">
                <i class="fa-solid fa-umbrella fa-5x"></i>&nbsp;&nbsp;
                <span class="h4 text-white landing-card-title">&nbsp; Misure di mitigazione</span>
              </div>
              <a class="small text-white stretched-link" href="${ms}"></a>
            </div>
          </div>
        </div>
        <br><hr class="separatore" />
        <ol class="breadcrumb mb-4">
          <li class="breadcrumb-item active"><strong>Strumenti di Analisi</strong></li>
        </ol>
        <div class="row">
          <div class="col-xl-3 col-md-6">
            <div class="card bgcolor2 mb-4">
              <div class="card-body landing-card-body d-flex align-items-center">
                <i class="fa-solid fa-headphones fa-5x"></i>&nbsp;&nbsp;
                <span class="h4 landing-card-title">Archivio Interviste</span>
              </div>
              <a class="small text-white stretched-link" href="${in}"></a>
            </div>
          </div>
          <div class="col-xl-3 col-md-6">
            <div class="card bgAct12 mb-4">
              <div class="card-body landing-card-body d-flex align-items-center">
                <i class="fa-solid fa-magnifying-glass-chart fa-5x"></i>&nbsp;&nbsp;
                <span class="h4 landing-card-title">Ricerche</span>
              </div>
               <a class="small text-white stretched-link" href="${ris}"></a>
            </div>
          </div>
          <div class="col-xl-3 col-md-6">
            <div class="card bg-danger text-white mb-4">
              <div class="card-body landing-card-body d-flex align-items-center">
                <i class="fas fa-chart-line fa-5x"></i>&nbsp;&nbsp;
                <span class="h4 landing-card-title">Monitoraggio e Report</span>
              </div>
              <a class="small text-white stretched-link" href="${mu}"></a>
            </div>
          </div>
        </div>
      </div>
    </div>
