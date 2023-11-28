<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="URL.jspf" %>
    <div>
      <h3 class="mt-1 m-0 font-weight-bold float-left">Monitoraggio e Report</h3>
      <hr class="riga"/>
      <ol class="breadcrumb mb-4">
        <li class="breadcrumb-item active">Report tabellari</li>
      </ol>
      <div class="row">
        <div class="col-xl-3 col-md-6">
          <div class="card bgAct20 text-dark mb-4">
            <div class="card-body">
              <i class="fa-solid fa-table fa-4x"></i>&nbsp;&nbsp;
              <i class="fas fa-cogs fa-4x"></i>&nbsp;&nbsp;
              <!-- <i class="fa-solid fa-triangle-exclamation fa-4x"></i>&nbsp;&nbsp;  -->
              Report P x I
            </div>
            <div class="card-footer d-flex align-items-center justify-content-between">
              <a class="small stretched-link" href="${mro}">Vai</a>
              <div class="small"><i class="fas fa-angle-right"></i></div>
            </div>
          </div>
        </div>
        <div class="col-xl-3 col-md-6">
          <div class="card bgAct20 text-dark mb-4">
            <div class="card-body">
              <i class="fa-solid fa-table fa-4x"></i>&nbsp;&nbsp;
              <i class="fas fa-sitemap fa-4x"></i>&nbsp;&nbsp;
              Report strutture e rischi
            </div>
            <div class="card-footer d-flex align-items-center justify-content-between">
              <a class="small stretched-link" href="${mtr}">Vai</a>
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
            <div class="card-body">Grafici</div>
            <div class="card-footer d-flex align-items-center justify-content-between">
              <a class="small stretched-link" href="#">Vedi dettagli</a>
              <div class="small text-white"><i class="fas fa-angle-right"></i></div>
            </div>
          </div>
        </div>
      </div>
    </div>
