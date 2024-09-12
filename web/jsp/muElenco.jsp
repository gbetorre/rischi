<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="URL.jspf" %>
    <div>
      <h3 class="mt-1 m-0 font-weight-bold float-left">Report</h3>
      <hr class="riga"/>
      <ol class="breadcrumb mb-4">
        <li class="breadcrumb-item active">Report tabellari</li>
      </ol>
      <div class="row">
        <div class="col-xl-3 col-md-6">
          <div class="card bgAct20 text-dark mb-4">
            <div class="card-body">
              <i class="fa-solid fa-table fa-4x"></i>&nbsp;&nbsp;
              <i class="fas fa-sitemap fa-4x"></i>&nbsp;&nbsp;
              P x I &nbsp; Strutture
            </div>
            <div class="card-footer d-flex align-items-center justify-content-between">
              <a class="small stretched-link" href="${mtr}">Report tabellare PxI</a>
              <div class="small"><i class="fas fa-angle-right"></i></div>
            </div>
          </div>
        </div>
        <div class="col-xl-3 col-md-6">
          <div class="card bgAct20 text-dark mb-4">
            <div class="card-body">
              <i class="fa-solid fa-table fa-4x"></i>&nbsp;&nbsp;
              <i class="fa-solid fa-umbrella fa-4x"></i>&nbsp;&nbsp;
              P x I &nbsp; Misure
            </div>
            <div class="card-footer d-flex align-items-center justify-content-between">
              <a class="small stretched-link" href="${mes}">Report tabellare PxI mitigati</a>
              <div class="small"><i class="fas fa-angle-right"></i></div>
            </div>
          </div>
        </div>
      </div>
      <br><hr class="separatore" />
      <ol class="breadcrumb mb-4">
        <li class="breadcrumb-item active">Report analitici</li>
      </ol>
      <div class="row">
        <div class="col-xl-3 col-md-6">
          <div class="card bgAct20 text-dark mb-4">
            <div class="card-body">
              <i class="fa-solid fa-table-cells fa-4x"></i>&nbsp;&nbsp;
              <i class="fas fa-cogs fa-4x"></i>&nbsp;&nbsp;
              P x I &nbsp; Indicatori
            </div>
            <div class="card-footer d-flex align-items-center justify-content-between">
              <a class="small stretched-link" href="${mro}">Report analitico indicatori</a>
              <div class="small"><i class="fas fa-angle-right"></i></div>
            </div>
          </div>
        </div>
        <div class="col-xl-3 col-md-6">
          <div class="card bgAct20 text-dark mb-4">
            <div class="card-body">
              <i class="fa-solid fa-table-cells fa-4x"></i>&nbsp;&nbsp;
              <i class="fa-solid fa-triangle-exclamation fa-4x"></i>&nbsp;&nbsp;
              P x I &nbsp; Rischi
            </div>
            <div class="card-footer d-flex align-items-center justify-content-between">
              <a class="small stretched-link" href="${mis}">Report analitico PxI mitigati</a>
              <div class="small"><i class="fas fa-angle-right"></i></div>
            </div>
          </div>
        </div>
      </div>
      <br><hr class="separatore" />
      <ol class="breadcrumb mb-4">
        <li class="breadcrumb-item active">Report grafici</li>
      </ol>
      <div class="row">
        <div class="col-xl-3 col-md-6">
          <div class="card bg-primary text-white mb-4">
            <div class="card-body">
              <i class="fa-solid fa-chart-pie fa-4x"></i>&nbsp;&nbsp;
              <!-- <i class="fa-solid fa-triangle-exclamation fa-4x"></i>&nbsp;&nbsp; -->
              Grafici
            </div>
            <div class="card-footer d-flex align-items-center justify-content-between">
              <a class="small stretched-link  text-white" href="${gra}">Report aggregati e sintetici</a>
              <div class="small"><i class="fas fa-angle-right"></i></div>
            </div>
          </div>
        </div>
      </div>
    </div>
