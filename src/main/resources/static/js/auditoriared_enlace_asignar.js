// Modulo auditoriared: enlace «Ver mapa» en /recursos/asignar, uno por sitio, junto al primer nombre
// del sitio. Abre /auditoria-red/sitio en otra pestaña con los PCI que están AHORA en los inputs
// (incluidos los cambios a mano). Solo lee la tabla: no toca el formulario ni el POST de /asignar.
(function () {
    'use strict';

    var tbody = document.getElementById('tbody-asignar');
    var form = tbody && tbody.closest ? tbody.closest('form') : null;
    if (!form) return;
    // th:action ya trae el context path: /<ctx>/recursos/asignar -> /<ctx>/auditoria-red/sitio
    var base = form.getAttribute('action').replace(/\/recursos\/asignar$/, '/auditoria-red/sitio');
    var sitios = {};

    function url(sitio) {
        var ids = [], pcis = [];
        sitios[sitio].forEach(function (f) {
            var pci = f.pci.value.trim();
            if (f.id.value && /^\d+$/.test(pci)) { ids.push(f.id.value); pcis.push(pci); }
        });
        return ids.length ? base + '?sitio=' + encodeURIComponent(sitio) + '&ids=' + ids.join(',') +
            '&pci=' + pcis.join(',') : null;
    }

    function enlace(sitio) {
        var a = document.createElement('a');
        a.href = base;
        a.target = '_blank';
        a.textContent = 'Ver mapa';
        a.title = 'Interferencia de las celdas de ' + sitio + ' con los PCI de esta tabla (no guarda nada)';
        a.style.cssText = 'margin-left:6px;font-size:11px;white-space:nowrap;';
        // mousedown cubre también el clic central y el «abrir en otra pestaña» del menú contextual.
        function actualizar(e) {
            var destino = url(sitio);
            if (destino) { a.href = destino; return; }
            if (e.type === 'click') {
                e.preventDefault();
                alert('Ninguna celda de ' + sitio + ' tiene PCI en la tabla todavía.');
            }
        }
        a.addEventListener('mousedown', actualizar);
        a.addEventListener('click', actualizar);
        return a;
    }

    Array.prototype.forEach.call(tbody.querySelectorAll('input[name$=".pciPsc"]'), function (inPci) {
        var fila = inPci.closest('tr');
        var inId = fila.querySelector('input[name$=".id"]');
        var nombre = fila.cells[0].querySelector('span');
        var sitio = nombre ? nombre.textContent.trim() : '';
        if (!inId || !sitio) return;
        if (!sitios[sitio]) {
            sitios[sitio] = [];
            nombre.parentNode.insertBefore(enlace(sitio), nombre.nextSibling);
        }
        sitios[sitio].push({ id: inId, pci: inPci });
    });
}());
