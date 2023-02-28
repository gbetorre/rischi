// Menu contestuale su eventuale grafico di orgchart
$(function () {
  $(document).tooltip({
    position: {my: "center top", at: "center bottom"}
  });
});

/* -----------------  POP-UP (help) ----------------- */
// Trap Mouse Position 
//Copyright 2006,2007 Bontrager Connection, LLC
var cX = 0;
var cY = 0;
var rX = 0;
var rY = 0;

function UpdateCursorPosition(e) {
  cX = e.pageX;
  cY = e.pageY;
}

function UpdateCursorPositionDocAll(e) {
  cX = event.clientX;
  cY = event.clientY;
}

if (document.all) {
  document.onmousemove = UpdateCursorPositionDocAll;
} else {
  document.onmousemove = UpdateCursorPosition;
}

function AssignPosition(d, tipo) {
  if (self.pageYOffset) {
    rX = self.pageXOffset;
    rY = self.pageYOffset;
  } else if (document.documentElement && document.documentElement.scrollTop) {
    rX = document.documentElement.scrollLeft;
    rY = document.documentElement.scrollTop;
  } else if (document.body) {
    rX = document.body.scrollLeft;
    rY = document.body.scrollTop;
  }
  if (document.all) {
    cX += rX;
    cY += rY;
  }
  if (tipo == "Note") {
    // Sposto + a sinistra, in quanto verrebbe in parte nascosto a destra
    d.style.left = (cX - 100) + "px";
    d.style.top = (cY + 10) + "px";
  } else {
    d.style.left = (cX + 10) + "px";
    d.style.top = (cY + 10) + "px";
  }
}

function HideContent(d) {
  if (d.length < 1) {
    return;
  }
  document.getElementById(d).style.display = "none";
}

function ShowContent(d, tipo) {
  if (d.length < 1) {
    return;
  }
  var dd = document.getElementById(d);
  AssignPosition(dd, tipo);
  dd.style.display = "block";
  dd.style.height = "200px";
}

function ReverseContentDisplay(d) {
  if (d.length < 1) {
    return;
  }
  var dd = document.getElementById(d);
  AssignPosition(dd);
  if (dd.style.display == "none") {
    dd.style.display = "block";
  } else {
    dd.style.display = "none";
  }
}

//\Trap Mouse Position ------------------------------------------------

function testClickPopup(nome) {
  // Chiusura
  var myname = nome.parentElement.name;
  if (!(myname == undefined)) {
    if (!(myname.indexOf('popup1') >= 0)) {
      HideContent('popup1');
    }
  } else {
    var myname = nome.name;
    if (!(myname == undefined)) {
      if (!(myname.indexOf('popup1') >= 0)) {
        HideContent('popup1');
      }
    } else {
      // Nasconde a prescindere
      HideContent('popup1');
    }
  }
}

function popupWindow(tit, o, d, t) {
  // o - Object to display.
  // d - Display, true =  display, false = hide
  // t - Text to display in the popup
  var obj = document.getElementById(o);
  var iltitolo = document.getElementById("titolopopup1");
  var contenuto = document.getElementById("popup1Text");

  if (d) {
    /*
    obj.style.display = 'block';
    obj.style.visibility = 'visible';
    iltitolo.innerHTML = tit;
    contenuto.innerHTML = t;
    obj.style.width= "350px";
    obj.style.height = "200px";
    obj.style.left = ((screen.availWidth - 700)/2);
    obj.style.top = ((screen.availHeight - 400)/2);
    */
    ShowContent(obj.id, tit);
    iltitolo.innerHTML = tit;
    contenuto.innerHTML = unescape(t);
  } else {
    /*
    contenuto.innerHTML = '';
    obj.style.display = 'none';
    obj.style.visibility = 'hidden';
    */
    contenuto.innerHTML = "";
    HideContent(obj.id);
  }
}
