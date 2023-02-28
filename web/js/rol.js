
/* ----------------- Datepicker ----------------- */
$.datepicker.regional['it'] = {
  closeText: "Chiudi",
  prevText: "&#x3C;Prec",
  nextText: "Succ&#x3E;",
  currentText: "Oggi",
  monthNames: ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
    "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"],
  monthNamesShort: ["Gen", "Feb", "Mar", "Apr", "Mag", "Giu",
    "Lug", "Ago", "Set", "Ott", "Nov", "Dic"],
  dayNames: ["Domenica", "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato"],
  dayNamesShort: ["Dom", "Lun", "Mar", "Mer", "Gio", "Ven", "Sab"],
  dayNamesMin: ["Do", "Lu", "Ma", "Me", "Gi", "Ve", "Sa"],
  weekHeader: "Sm",
  dateFormat: "dd/mm/yy",
  firstDay: 1,
  isRTL: false,
  showMonthAfterYear: false,
  yearSuffix: ""
};
$.datepicker.setDefaults($.datepicker.regional['it']);

/* ----------------- Forms ----------------- */
$('textarea').textareaAutoSize();

jQuery.validator.addMethod("greaterThan",
  function (value, element, params) {
    var finalDate = value.split('/');
    var dayFinalDate = finalDate[0];
    var monthFinalDate = finalDate[1];
    var yearFinalDate = finalDate[2];
    var firstDate = $(params).val().split('/');
    var dayFirstDate = firstDate[0];
    var monthFirstDate = firstDate[1];
    var yearFirstDate = firstDate[2];
    if (!/Invalid|NaN/.test(new Date(yearFinalDate, monthFinalDate, dayFinalDate))) {
      return new Date(yearFinalDate, monthFinalDate - 1, dayFinalDate) > new Date(yearFirstDate, monthFirstDate - 1, dayFirstDate);
    }
    return isNaN(value) && isNaN($(params).val())
      || (Number(value) > Number($(params).val()));
  }, 'Must be greater than {0}.'
);

jQuery.validator.addMethod("lessThan",
  function (value, element, params) {
    var finalDate = value.split('/');
    var dayFinalDate = finalDate[0];
    var monthFinalDate = finalDate[1];
    var yearFinalDate = finalDate[2];
    var runtimeDate = new Date(yearFinalDate, monthFinalDate - 1, dayFinalDate);
    var rightNow = new Date();
    var dayRuntimeDate = runtimeDate.getUTCDate() + 1;
    var monthRuntimeDate = runtimeDate.getUTCMonth();
    var yearRuntimeDate = runtimeDate.getUTCFullYear();
    var dayCurrentDate = rightNow.getUTCDate();
    var monthCurrentDate = rightNow.getUTCMonth();
    var yearCurrentDate = rightNow.getUTCFullYear();
    var runtimeDateAsMilliseconds = Date.UTC(yearRuntimeDate, monthRuntimeDate, dayRuntimeDate, 0, 0, 0);
    var tomorrowDateAsMilliseconds = Date.UTC(yearCurrentDate, monthCurrentDate, dayCurrentDate + 1, 0, 0, 0);
    if (!/Invalid|NaN/.test(runtimeDate)) {
      if (runtimeDateAsMilliseconds < tomorrowDateAsMilliseconds) {
        return true;
      }
      return false;
    }
    return true;
  }, 'Must be less than {0}.'
);

/* -----------------  D3 CHARTS ----------------- */
// SUPPORT TO D3 CHARTS

function uid(name) {
  return new Id("O-" + (name == null ? "" : name + "-") + ++count);
}

function Id(id) {
  this.id = id;
  this.href = new URL('#' + id, location) + "";
}

Id.prototype.toString = function () {
  return "url(" + this.href + ")";
};

process_color = function (cat) {
  switch (cat) {
    case 'AMM':
      return d3.schemePastel1[0]
    case 'INFR':
      return d3.schemePastel1[1]
    case 'SBMA':
      return d3.schemePastel1[2]
    case 'DID':
      return d3.schemePastel1[3]
    case 'RIC':
      return d3.schemePastel1[4]
    default:
      return '#BBBBBB'
  }
}

function position(group, root) {
  group.selectAll("g")
    .attr("transform", d => d === root ? 'translate(0,-30)' : 'translate(' + x(d.x0) + ',' + y(d.y0) + ')')
    .select("rect")
    .attr("width", d => d === root ? width : x(d.x1) - x(d.x0))
    .attr("height", d => d === root ? 30 : y(d.y1) - y(d.y0));
}

function tiling(node, x0, y0, x1, y1) {
  d3.treemapBinary(node, 0, 0, width, height);
  for (const child of node.children) {
    child.x0 = x0 + child.x0 / width * (x1 - x0);
    child.x1 = x0 + child.x1 / width * (x1 - x0);
    child.y0 = y0 + child.y0 / height * (y1 - y0);
    child.y1 = y0 + child.y1 / height * (y1 - y0);
  }
}

/* -----------------  ajaxcall ----------------- */
/** Funzione che fa una chiamata ajax per aggiornare un elemento con id "idDest"
 *  e funzione di callback (passata come parametro ed eseguita in caso di successo)
 */
function ajaxCall(urlAjax, tipo, data, idDest, success) { 
  //debugger;
  return $.ajax({
    url: urlAjax.replace(/&amp;/g, '&'),
    type: tipo,
    data: data,
    beforeSend: function(jqXHR, settings) {
                $('#imgload').css('visibility', 'visible');
                jqXHR.url = settings.url; // Salvo nell'oggetto jqXHR l'url della chiamata ajax per poterlo leggere dopo nella funzione success
            },
    success: success, // Funzione passata come parametro ed eseguita in caso di successo. Type: Function( Anything data, String textStatus, jqXHR jqXHR )
    timeout: 3 * 60 * 1000 // sets timeout to 3 minutes
  })
  .done(function( html ) {
  // Se è un selettore jQuery estrae la stringa di selezione
  if(idDest.selector != undefined) {
    idDest = idDest.selector;
  }
  $( idDest ).html( html );

  //ajaxForm(idDest, success);
  //activateDatepickers();
  
  if(idDest!="#tab-per") {
    // Assegna le chiamate ajax ai link della paginazione delle pubblicazioni
    $("a[class^='paginazione'],a[class*=' paginazione']").click(function(e){
      ajaxCall($(this).attr("href"), "GET", null, idDest, success);
      e.preventDefault(); //STOP default action
      $( this ).unbind( e ); //unbind. to stop multiple form submit.
    });
    $('[data-toggle="tooltip"]').tooltip();
  }
  
  if(idDest=="#tab-per") {
    // Aggiorna contatore avvisi
    //$("a[href=#tab-avvisi] .badge").text($("#tab-avvisi tbody tr a").size());
    
  }
 })
 .fail(function(jqXHR, textStatus, errorThrown) {
//   alert("Errore!");
   $( idDest ).html("<a href='#Retry'><span class='glyphicon glyphicon-refresh' aria-hidden='true'></span> Informazione non disponibile</a>");
   $( idDest ).append("<!-- Error: " + errorThrown + " -->");
   $( idDest + " a[href=#Retry]").click(function(e){
    ajaxCall(urlAjax, tipo, data, idDest, success);
    e.preventDefault(); //STOP default action
    $( this ).unbind( e ); //unbind. to stop multiple form submit.
   });
 });
}


/** Cerca delle form da rendere ajax per restare sempre nella stessa pagina
 *  e funzione passata come parametro ed eseguita in caso di successo
 */
function ajaxForm(idDest, success) {
  // Se è un selettore jQuery estrae la stringa di selezione
  if(idDest.selector != undefined) {
    idDest = idDest.selector;
  }
  $( idDest + " form").submit(function(e) {
    // Disabilita il submit
    $('[type=submit]', this).attr('disabled', 'disabled');
        
      var datiForm = $(this).serializeArray();
  
    ajaxCall($(this).attr("action"), $(this).attr("method"), datiForm, idDest, success);
    e.preventDefault(); //STOP default action
    $( this ).unbind( e ); //unbind. to stop multiple form submit.
    });
}  