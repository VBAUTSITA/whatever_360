(function () {
    'use strict';

    var raiz = document.getElementById('auditoria-red');
    if (!raiz || typeof L === 'undefined') return;

    var URL_SITIOS = raiz.getAttribute('data-sitios-url');
    var URL_EJECUTAR = raiz.getAttribute('data-ejecutar-url');
    var URL_SITIO = raiz.getAttribute('data-sitio-url');
    var RADIO_MAX = Number(raiz.getAttribute('data-radio-max')) || 20;
    var REUSO_MAX = Number(raiz.getAttribute('data-reuso-max')) || 10;
    // Mismos colores que la lamina del procedimiento (paso 6): MOD 0 rojo, 1 azul, 2 verde.
    var COLOR_MOD = ['#ff3b3b', '#3b5bff', '#2ecc40'];
    // Numero de MOD dentro del sector: siempre que sean pocas celdas o al acercar; con muchas
    // celdas y poco zoom no se lee y solo estorba.
    var ZOOM_ETIQUETAS = 13;
    var MAX_ETIQUETAS_SIN_ZOOM = 250;

    function $(id) { return document.getElementById(id); }

    var form = $('ar-form');
    var inSitio = $('ar-sitio');
    var sugerencias = $('ar-sugerencias');
    var inLat = $('ar-lat');
    var inLon = $('ar-lon');
    var inRadio = $('ar-radio');
    var inReuso = $('ar-reuso');
    var btnAuditar = $('ar-auditar');
    var btnRecalcular = $('ar-recalcular');
    var btnCsv = $('ar-csv');
    var divEstado = $('ar-estado');
    var divTope = $('ar-tope');
    var divConteo = $('ar-conteo');
    var cuerpo = $('ar-hallazgos');
    var filtros = {
        tipo: $('ar-f-tipo'), tec: $('ar-f-tec'), carrier: $('ar-f-carrier'),
        banda: $('ar-f-banda'), texto: $('ar-f-texto'), solo: $('ar-f-solo')
    };

    // sitio: llegó desde /recursos/asignar (modo sitio); celda: la celda planificada elegida.
    var estado = { resultado: null, seleccion: null, corriendo: false, busqueda: 0, temporizador: null,
        sitio: false, celda: null };

    inReuso.value = REUSO_MAX;
    inReuso.max = REUSO_MAX;
    inRadio.max = RADIO_MAX;
    $('ar-radio-nota').textContent = 'máximo ' + RADIO_MAX + ' km';
    $('ar-reuso-nota').textContent = 'máximo ' + REUSO_MAX + ' km. Dos celdas con el mismo PCI más lejos ' +
        'que esto no se marcan. También es el margen alrededor del área: en zonas densas, bajarlo acelera la auditoría.';

    // Igual que AuditoriaRedService.margenPara: el radio de reuso mas 0.1 km de holgura.
    function margenKm() {
        var r = numero(inReuso.value);
        return (r > 0 ? r : REUSO_MAX) + 0.1;
    }

    var mapa = L.map('ar-mapa', { preferCanvas: true }).setView([-9.2, -75.0], 5);
    var satelite = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
        attribution: 'Imagen &copy; Esri', maxZoom: 19
    });
    var calles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap', maxZoom: 19
    });
    satelite.addTo(mapa);
    L.control.layers({ 'Satélite': satelite, 'Calles (OSM)': calles }, null, { position: 'topright' }).addTo(mapa);
    var capaAlcance = L.layerGroup().addTo(mapa);
    var capaCeldas = L.layerGroup().addTo(mapa);
    var capaEtiquetas = L.layerGroup().addTo(mapa);
    var capaSeleccion = L.layerGroup().addTo(mapa);

    // Leyenda MOD con casillas, como en la lamina: oculta o muestra los sectores de cada MOD.
    var modVisible = [true, true, true];
    var leyenda = L.control({ position: 'bottomleft' });
    leyenda.onAdd = function () {
        var div = L.DomUtil.create('div', 'ar-leyenda-mod');
        var titulo = document.createElement('b');
        titulo.textContent = 'MOD';
        div.appendChild(titulo);
        [0, 1, 2].forEach(function (m) {
            var etiqueta = document.createElement('label');
            var casilla = document.createElement('input');
            casilla.type = 'checkbox';
            casilla.checked = true;
            casilla.id = 'ar-mod-' + m;
            casilla.addEventListener('change', function () { modVisible[m] = casilla.checked; redibujar(); });
            var muestra = document.createElement('span');
            muestra.className = 'ar-muestra';
            muestra.style.background = COLOR_MOD[m];
            etiqueta.appendChild(casilla);
            etiqueta.appendChild(muestra);
            etiqueta.appendChild(document.createTextNode(' ' + m));
            div.appendChild(etiqueta);
        });
        L.DomEvent.disableClickPropagation(div);
        return div;
    };
    leyenda.addTo(mapa);

    // Regla de distancia y rumbo, como «Calcular distancias» del mapa de coordenadas, con dos mejoras:
    // usa el haversine y el radio terrestre del motor (6 371 000 m), así da los mismos metros que la
    // columna «Distancia» de los hallazgos; y un clic en un sector ancla el punto en el sitio de esa celda.
    var RADIO_MOTOR_M = 6371000;
    var capaMedicion = L.layerGroup().addTo(mapa);
    var medicion = { activa: false, puntos: [] };
    var regla = L.control({ position: 'topleft' });
    regla.onAdd = function () {
        var div = L.DomUtil.create('div', 'ar-regla leaflet-bar');
        var boton = L.DomUtil.create('button', 'ar-regla-boton', div);
        boton.type = 'button';
        boton.textContent = 'Medir distancia';
        boton.addEventListener('click', alternarMedicion);
        L.DomUtil.create('div', 'ar-regla-panel', div).hidden = true;
        L.DomEvent.disableClickPropagation(div);
        // Además, cortar el click aquí: «Deshacer» y «Borrar» redibujan el panel y se quitan a sí mismos del
        // DOM, y entonces Leaflet ya no encuentra la marca de arriba y toma el clic como un punto en el mapa.
        L.DomEvent.on(div, 'click', L.DomEvent.stopPropagation);
        return div;
    };
    regla.addTo(mapa);

    // ---------- utilidades ----------

    function numero(v) {
        if (v === null || v === undefined || v === '') return null;
        var n = Number(v);
        return isFinite(n) ? n : null;
    }

    function mensaje(texto, esError) {
        divEstado.textContent = texto || '';
        divEstado.className = esError ? 'ar-aviso' : 'ar-nota';
    }

    function vaciar(el) { while (el.firstChild) el.removeChild(el.firstChild); }

    function centroActual() {
        var lat = numero(inLat.value), lon = numero(inLon.value);
        if (lat === null || lon === null || Math.abs(lat) > 90 || Math.abs(lon) > 180) return null;
        return [lat, lon];
    }

    // Punto a una distancia y rumbo dados (esfera), para armar el contorno del sector.
    function destino(lat, lon, rumbo, metros) {
        var R = 6371008.8, d = metros / R, b = rumbo * Math.PI / 180;
        var f1 = lat * Math.PI / 180, l1 = lon * Math.PI / 180;
        var f2 = Math.asin(Math.sin(f1) * Math.cos(d) + Math.cos(f1) * Math.sin(d) * Math.cos(b));
        var l2 = l1 + Math.atan2(Math.sin(b) * Math.sin(d) * Math.cos(f1), Math.cos(d) - Math.sin(f1) * Math.sin(f2));
        return [f2 * 180 / Math.PI, l2 * 180 / Math.PI];
    }

    // ---------- regla ----------

    function distanciaM(a, b) {
        var r = Math.PI / 180, dLat = (b.lat - a.lat) * r, dLon = (b.lon - a.lon) * r;
        var s = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(a.lat * r) * Math.cos(b.lat * r) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * RADIO_MOTOR_M * Math.asin(Math.sqrt(Math.min(1, s)));
    }

    // Rumbo de a hacia b: 0-360° desde el norte, en sentido horario, como el azimuth de las celdas.
    function rumbo(a, b) {
        var r = Math.PI / 180, dLon = (b.lon - a.lon) * r;
        var y = Math.sin(dLon) * Math.cos(b.lat * r);
        var x = Math.cos(a.lat * r) * Math.sin(b.lat * r) - Math.sin(a.lat * r) * Math.cos(b.lat * r) * Math.cos(dLon);
        return (Math.atan2(y, x) / r + 360) % 360;
    }

    function textoDistancia(m) { return m >= 1000 ? (m / 1000).toFixed(2) + ' km' : Math.round(m) + ' m'; }

    function alternarMedicion() {
        medicion.activa = !medicion.activa;
        var div = regla.getContainer();
        div.querySelector('.ar-regla-boton').textContent = medicion.activa ? 'Terminar medición' : 'Medir distancia';
        L.DomUtil[medicion.activa ? 'addClass' : 'removeClass'](div, 'activa');
        mapa.getContainer().style.cursor = medicion.activa ? 'crosshair' : '';
        pintarMedicion();
    }

    function agregarPunto(p) {
        medicion.puntos.push(p);
        pintarMedicion();
    }

    function lineaPanel(panel, texto, clase) {
        var div = L.DomUtil.create('div', clase || '', panel);
        div.textContent = texto;
    }

    function botonPanel(panel, texto, accion) {
        var b = L.DomUtil.create('button', 'btn btn-mini', panel);
        b.type = 'button';
        b.textContent = texto;
        b.addEventListener('click', accion);
    }

    // Texto sobre el mapa como marcador divIcon, igual que los números de MOD. El texto va como nodo, no
    // como HTML: puede traer el nombre de una celda.
    function rotulo(latlng, texto) {
        var span = document.createElement('span');
        span.textContent = texto;
        L.marker(latlng, { interactive: false, keyboard: false,
            icon: L.divIcon({ className: 'ar-regla-rotulo', html: span, iconSize: null, iconAnchor: [-7, 20] })
        }).addTo(capaMedicion);
    }

    function pintarMedicion() {
        var pts = medicion.puntos, panel = regla.getContainer().querySelector('.ar-regla-panel'), total = 0;
        capaMedicion.clearLayers();
        vaciar(panel);
        panel.hidden = !medicion.activa && !pts.length;
        pts.forEach(function (p, i) {
            L.circleMarker([p.lat, p.lon], { radius: 5, color: '#c00', weight: 2, fillColor: '#fff', fillOpacity: 1,
                interactive: false }).addTo(capaMedicion);
            rotulo([p.lat, p.lon], 'P' + (i + 1) + (p.nombre ? ' · ' + p.nombre : ''));
            if (i === 0) return;
            var a = pts[i - 1], m = distanciaM(a, p), az = rumbo(a, p);
            total += m;
            L.polyline([[a.lat, a.lon], [p.lat, p.lon]], { color: '#c00', weight: 3, interactive: false })
                .addTo(capaMedicion);
            rotulo([(a.lat + p.lat) / 2, (a.lon + p.lon) / 2], textoDistancia(m) + ' · ' + Math.round(az) + '°');
            lineaPanel(panel, 'P' + i + ' → P' + (i + 1) + ': ' + Math.round(m) + ' m · rumbo ' + az.toFixed(1) + '°');
        });
        if (pts.length > 2) {
            lineaPanel(panel, 'Total: ' + Math.round(total) + ' m (' + (total / 1000).toFixed(2) + ' km)', 'ar-regla-total');
        }
        if (medicion.activa && pts.length < 2) {
            lineaPanel(panel, 'Clic en el mapa para poner puntos; en un sector, el punto se ancla en el sitio de ' +
                'esa celda. Esc termina.', 'ar-nota');
        }
        if (pts.length) {
            botonPanel(panel, 'Deshacer', function () { pts.pop(); pintarMedicion(); });
            botonPanel(panel, 'Borrar', function () { medicion.puntos = []; pintarMedicion(); });
        }
    }

    function tieneGeometria(c) { return c.radioM !== null && c.radioM !== undefined && c.radioM > 0
        && Number.isFinite(c.azimuthEfectivo) && Number.isFinite(c.hbeam) && c.hbeam > 0; }

    // Geometria estimada del motor; nunca se inventa alcance cuando falta radio utilizable.
    function sector(c) {
        if (tieneGeometria(c)) return { az: c.azimuthEfectivo, haz: c.hbeam, radio: c.radioM };
        return { az: c.azimuth, haz: 0, radio: 0 };
    }

    // Mismo trazo que wedgeLatLon del motor: el centro y un arco de az - haz/2 a az + haz/2.
    function contornoSector(c) {
        var s = sector(c), pasos = 32, puntos = [[c.latitud, c.longitud]];
        for (var i = 0; i <= pasos; i++) {
            puntos.push(destino(c.latitud, c.longitud, s.az - s.haz / 2 + s.haz * i / pasos, s.radio));
        }
        return puntos;
    }

    function puntoEtiqueta(c) {
        var s = sector(c);
        return destino(c.latitud, c.longitud, s.az, s.radio * 0.6);
    }

    // ---------- filtros ----------

    function celdaPasa(c) {
        if (!c) return false;
        var texto = filtros.texto.value.trim().toUpperCase();
        return (!estado.celda || mismoGrupo(c, estado.celda))
            && (!filtros.tec.value || c.tecnologia === filtros.tec.value)
            && (!filtros.carrier.value || String(c.carrier) === filtros.carrier.value)
            && (!filtros.banda.value || String(c.banda) === filtros.banda.value)
            && (!texto || String(c.nombre).toUpperCase().indexOf(texto) >= 0);
    }

    function hallazgoPasa(h) {
        return (!filtros.tipo.value || h.etiqueta === filtros.tipo.value)
            && (!estado.celda || esDeLaCelda(h, estado.celda))
            && (celdaPasa(h.celdaA) || celdaPasa(h.celdaB));
    }

    function hallazgosVisibles() {
        return estado.resultado ? estado.resultado.hallazgos.filter(hallazgoPasa) : [];
    }

    function llenarSelect(select, valores) {
        var primero = select.options[0].textContent;
        vaciar(select);
        var todos = document.createElement('option');
        todos.value = '';
        todos.textContent = primero;
        select.appendChild(todos);
        valores.forEach(function (v) {
            var op = document.createElement('option');
            op.value = v;
            op.textContent = v;
            select.appendChild(op);
        });
    }

    function valoresDe(lista, campo) {
        var vistos = {};
        lista.forEach(function (x) {
            var v = x && x[campo];
            if (v !== null && v !== undefined && String(v) !== '') vistos[String(v)] = true;
        });
        return Object.keys(vistos).sort(function (a, b) { return a.localeCompare(b, 'es', { numeric: true }); });
    }

    function prepararFiltros() {
        var r = estado.resultado;
        llenarSelect(filtros.tipo, valoresDe(r.hallazgos, 'etiqueta'));
        llenarSelect(filtros.tec, valoresDe(r.celdas, 'tecnologia'));
        llenarSelect(filtros.carrier, valoresDe(r.celdas, 'carrier'));
        llenarSelect(filtros.banda, valoresDe(r.celdas, 'banda'));
    }

    // ---------- mapa ----------

    function dibujarAlcance(centro, radioKm, margenKm) {
        capaAlcance.clearLayers();
        if (!centro || !(radioKm > 0)) return;
        // Amarillo y naranja se distinguen tanto sobre satelite como sobre calles.
        L.circle(centro, { radius: (radioKm + margenKm) * 1000, color: '#ff9800', weight: 2,
            dashArray: '8 6', fill: false, interactive: false }).addTo(capaAlcance);
        L.circle(centro, { radius: radioKm * 1000, color: '#ffd400', weight: 3,
            fill: false, interactive: false }).addTo(capaAlcance);
    }

    function encuadrar(centro, radioKm, margenKm) {
        // L.circle().getBounds() exige que el circulo ya este en el mapa; toBounds no.
        if (centro && radioKm > 0) {
            mapa.fitBounds(L.latLng(centro[0], centro[1]).toBounds(2 * (radioKm + margenKm) * 1000));
        }
    }

    function textoCelda(c) {
        var geo = tieneGeometria(c)
            ? '\nAzimuth efectivo ' + Math.round(c.azimuthEfectivo) + '° · haz ' + Math.round(c.hbeam) + '° · radio ' +
              Math.round(c.radioM) + ' m estimados' + (c.sinTa ? ' (respaldo por vecinas disponibles: ' + (c.vecinasRespaldo || 'sin conteo') + ')' : ' (por TA P80)')
            : '\nSin alcance utilizable: solo ubicación, sin sector estimado';
        return c.nombre + '\n' + c.tecnologia + ' · carrier ' + (c.carrier || '-') + ' · banda ' +
            (c.banda || '-') + '\nPCI ' + c.pci + ' · MOD ' + c.mod + ' · azimuth ' + c.azimuth + '°' + geo +
            '\nCanal radio: ' + (c.canalRadio || 'no informado; frecuencia no verificada') +
            '\nFuente TA: ' + (c.fuenteTa || 'no informada') + ' · Estado: ' + (c.estadoTa || 'no informado') +
            '\nRSI derivado de PCI; no es PRACH configurado' +
            (c.conHallazgo ? '\nTiene hallazgos' : '') +
            (c.planificada ? '\nPlanificada: PCI propuesto, todavía sin guardar' : c.dentroArea || estado.sitio ? '' : '\n(celda del margen)');
    }

    function popup(c) {
        var div = document.createElement('div');
        div.style.whiteSpace = 'pre-line';
        div.textContent = textoCelda(c);
        return div;
    }

    // El relleno es el MOD; el borde negro marca las celdas con hallazgo y el blanco, las planificadas.
    function dibujarCelda(c, capa, resaltada) {
        var color = COLOR_MOD[c.mod];
        var opciones = {
            color: resaltada ? '#000' : c.planificada ? '#fff' : c.conHallazgo ? '#000' : color,
            weight: resaltada ? 4 : c.planificada ? 3 : (c.conHallazgo ? 2 : 1),
            fillColor: color, fillOpacity: resaltada ? 0.75 : 0.45,
            dashArray: c.dentroArea || estado.sitio ? null : '4 4',
            bubblingMouseEvents: false
        };
        var poligono = (tieneGeometria(c) ? L.polygon(contornoSector(c), opciones)
            : L.circleMarker([c.latitud, c.longitud], Object.assign({ radius: 5 }, opciones))).addTo(capa);
        // Midiendo, el clic ancla el punto en el sitio de la celda. Si no, abre sus datos (y una planificada,
        // además, pasa a ser la elegida). El popup no va atado al sector: así sobrevive al redibujo del zoom.
        poligono.on('click', function (e) {
            if (medicion.activa) return agregarPunto({ lat: c.latitud, lon: c.longitud, nombre: c.nombre });
            if (c.planificada) seleccionarCelda(c);
            L.popup({ autoPan: !c.planificada }).setLatLng(e.latlng).setContent(popup(c)).openOn(mapa);
        });
    }

    function etiquetar(c) {
        L.marker(puntoEtiqueta(c), {
            interactive: false, keyboard: false,
            icon: L.divIcon({ className: 'ar-etiqueta-mod', html: String(c.mod), iconSize: [14, 14] })
        }).addTo(capaEtiquetas);
    }

    function redibujar() {
        capaCeldas.clearLayers();
        capaEtiquetas.clearLayers();
        capaSeleccion.clearLayers();
        var r = estado.resultado;
        if (!r) return;
        var a = r.alcance;
        dibujarAlcance([a.latitud, a.longitud], a.radioKm, a.margenKm);
        var visibles = r.celdas.filter(function (c) {
            return modVisible[c.mod] && celdaPasa(c) && (!filtros.solo.checked || c.conHallazgo);
        });
        // Los sectores grandes primero: si no, taparian a los chicos.
        visibles.sort(function (x, y) { return sector(y).radio - sector(x).radio; });
        visibles.forEach(function (c) { dibujarCelda(c, capaCeldas, false); });
        if (mapa.getZoom() >= ZOOM_ETIQUETAS || visibles.length <= MAX_ETIQUETAS_SIN_ZOOM) visibles.forEach(etiquetar);
        if (estado.seleccion) dibujarSeleccion(estado.seleccion);
        else if (estado.celda) dibujarCeldaElegida(estado.celda);
    }

    function dibujarSeleccion(h) {
        capaSeleccion.clearLayers();
        var a = [h.celdaA.latitud, h.celdaA.longitud], b = [h.celdaB.latitud, h.celdaB.longitud];
        L.polyline([a, b], { color: '#000', weight: 3, dashArray: '8 4', interactive: false }).addTo(capaSeleccion);
        dibujarCelda(h.celdaA, capaSeleccion, true);
        dibujarCelda(h.celdaB, capaSeleccion, true);
    }

    function seleccionar(h, fila) {
        estado.seleccion = h;
        Array.prototype.forEach.call(cuerpo.querySelectorAll('tr.ar-sel'), function (tr) { tr.className = ''; });
        if (fila) fila.className = 'ar-sel';
        var a = L.latLng(h.celdaA.latitud, h.celdaA.longitud), b = L.latLng(h.celdaB.latitud, h.celdaB.longitud);
        if (a.equals(b)) mapa.setView(a, 16);
        else mapa.fitBounds(L.latLngBounds(a, b), { padding: [60, 60], maxZoom: 16 });
        dibujarSeleccion(h);
    }

    // ---------- tabla ----------

    function td(tr, texto) {
        var c = document.createElement('td');
        c.textContent = texto === null || texto === undefined ? '' : String(texto);
        tr.appendChild(c);
    }

    function carrierBanda(c) { return (c.carrier || '-') + ' / ' + (c.banda || '-'); }

    function nombreEnTabla(c) {
        return c.nombre + (c.planificada ? ' (PLAN)' : c.dentroArea || estado.sitio ? '' : ' (margen)');
    }

    function pintarTabla() {
        vaciar(cuerpo);
        var r = estado.resultado;
        if (!r) return;
        var visibles = hallazgosVisibles();
        var frag = document.createDocumentFragment();
        visibles.forEach(function (h) {
            var tr = document.createElement('tr');
            td(tr, h.etiqueta);
            td(tr, h.valor);
            td(tr, nombreEnTabla(h.celdaA));
            td(tr, nombreEnTabla(h.celdaB));
            td(tr, h.celdaA.tecnologia);
            td(tr, carrierBanda(h.celdaA));
            td(tr, Math.round(h.distanciaMetros));
            td(tr, h.motivo);
            if (estado.seleccion === h) tr.className = 'ar-sel';
            tr.addEventListener('click', function () { seleccionar(h, tr); });
            frag.appendChild(tr);
        });
        cuerpo.appendChild(frag);
        divConteo.textContent = visibles.length + ' de ' + r.hallazgos.length + ' hallazgos con los filtros actuales.';
        btnCsv.disabled = visibles.length === 0;
    }

    function resumen(r) {
        var partes = r.resumen.map(function (t) {
            return t.tecnologia + ': ' + t.celdasEnArea + (estado.sitio ? ' celdas del sitio contra ' + t.celdasEnMargen +
                ' existentes, ' : ' celdas en el área, ' + t.celdasEnMargen + ' en el margen, ') +
                t.hallazgos + ' hallazgos (motor ' + t.motor + ')' +
                (t.geometriaDisponible ? '' : ' [sin geometría del motor: solo ubicaciones]');
        });
        return partes.join('. ') + '. Calculado en ' + (r.duracionMs / 1000).toFixed(1) + ' s, ' +
            r.generadoEn + (r.desdeCache ? ', tomado de la caché' : '') + '.' +
            (r.avisos && r.avisos.length ? ' ' + r.avisos.join(' ') : '');
    }

    // ---------- CSV ----------

    function csvCampo(v) {
        return window.AuditoriaHttp.csv(v);
    }

    function exportarCsv() {
        var filas = [['Tipo', 'Valor', 'Celda A', 'Lat A', 'Lon A', 'PCI A', 'Celda B', 'Lat B', 'Lon B', 'PCI B',
            'Tecnologia', 'Carrier', 'Banda', 'Distancia m', 'Motivo']];
        hallazgosVisibles().forEach(function (h) {
            var a = h.celdaA, b = h.celdaB;
            filas.push([h.etiqueta, h.valor, a.nombre, a.latitud, a.longitud, a.pci, b.nombre, b.latitud,
                b.longitud, b.pci, a.tecnologia, a.carrier, a.banda, Math.round(h.distanciaMetros), h.motivo]);
        });
        // Punto y coma y BOM: asi lo abre bien Excel con configuracion regional en espanol.
        var texto = '﻿' + filas.map(function (f) { return f.map(csvCampo).join(';'); }).join('\r\n');
        var enlace = document.createElement('a');
        enlace.href = URL.createObjectURL(new Blob([texto], { type: 'text/csv;charset=utf-8' }));
        enlace.download = 'auditoria_red_' + estado.resultado.generadoEn.replace(/[: ]/g, '-') + '.csv';
        document.body.appendChild(enlace);
        enlace.click();
        document.body.removeChild(enlace);
        setTimeout(function () { URL.revokeObjectURL(enlace.href); }, 1000);
    }

    // ---------- busqueda de sitio ----------

    function ocultarSugerencias() { vaciar(sugerencias); sugerencias.hidden = true; }

    function mostrarSugerencias(sitios) {
        vaciar(sugerencias);
        if (!sitios.length) {
            var nada = document.createElement('div');
            nada.textContent = 'Sin resultados';
            sugerencias.appendChild(nada);
        }
        sitios.forEach(function (s) {
            var op = document.createElement('div');
            op.textContent = s.nombre + ' (' + s.tecnologia + ')';
            op.addEventListener('mousedown', function (e) {
                e.preventDefault();
                inSitio.value = s.nombre;
                inLat.value = s.latitud;
                inLon.value = s.longitud;
                ocultarSugerencias();
                borrador();
            });
            sugerencias.appendChild(op);
        });
        sugerencias.hidden = false;
    }

    function buscarSitio() {
        var q = inSitio.value.trim();
        estado.busqueda++;
        var numeroBusqueda = estado.busqueda;
        if (estado.temporizador) clearTimeout(estado.temporizador);
        if (q.length < 2) { ocultarSugerencias(); return; }
        estado.temporizador = setTimeout(function () {
            fetch(URL_SITIOS + '?q=' + encodeURIComponent(q), { credentials: 'same-origin' })
                .then(leerRespuesta)
                .then(function (sitios) {
                    if (numeroBusqueda === estado.busqueda) mostrarSugerencias(sitios || []);
                })
                .catch(function (e) { mensaje(e.message, true); });
        }, 300);
    }

    // ---------- auditoria ----------

    function leerRespuesta(resp) {
        return window.AuditoriaHttp.read(resp);
    }

    function solicitud(refrescar) {
        var centro = centroActual();
        var radio = numero(inRadio.value);
        var reuso = numero(inReuso.value);
        var tecnologias = [];
        if ($('ar-4g').checked) tecnologias.push('4G');
        if ($('ar-5g').checked) tecnologias.push('5G');
        if (!centro) throw new Error('Elija un sitio de la lista o haga clic en el mapa.');
        if (!(radio > 0) || radio > RADIO_MAX) throw new Error('El radio debe estar entre 0 y ' + RADIO_MAX + ' km.');
        if (!(reuso >= 0.5) || reuso > REUSO_MAX) throw new Error('El radio de reuso debe estar entre 0.5 y ' + REUSO_MAX + ' km.');
        if (!tecnologias.length) throw new Error('Marque 4G, 5G o ambas.');
        return { sitio: inSitio.value.trim() || null, latitud: centro[0], longitud: centro[1],
            radioKm: radio, radioReusoKm: reuso, tecnologias: tecnologias, refrescar: !!refrescar };
    }

    function mostrar(r) {
        estado.resultado = r;
        estado.seleccion = null;
        prepararFiltros();
        pintarTabla();
        redibujar();
        encuadrar([r.alcance.latitud, r.alcance.longitud], r.alcance.radioKm, r.alcance.margenKm);
        divTope.hidden = !r.capped;
        divTope.textContent = r.capped ? 'El motor llegó a su tope de hallazgos (' + r.cappedTipos.join(', ') +
            '). El resultado está incompleto: reduzca el radio.' : '';
        mensaje(resumen(r), false);
    }

    function auditar(refrescar) {
        if (estado.corriendo) return;
        var datos;
        try { datos = solicitud(refrescar); } catch (e) { mensaje(e.message, true); return; }
        estado.corriendo = true;
        btnAuditar.disabled = btnRecalcular.disabled = true;
        mensaje('Auditando... puede tardar en zonas con muchas celdas.', false);
        fetch(URL_EJECUTAR, {
            method: 'POST', credentials: 'same-origin',
            headers: window.AuditoriaHttp.headers(true), body: JSON.stringify(datos)
        }).then(leerRespuesta).then(mostrar).catch(function (e) {
            mensaje(e.message, true);
        }).then(function () {
            estado.corriendo = false;
            btnAuditar.disabled = btnRecalcular.disabled = false;
        });
    }

    // Antes de auditar se muestra el area que se va a pedir.
    function borrador() {
        if (estado.resultado) return;
        var centro = centroActual(), radio = numero(inRadio.value);
        dibujarAlcance(centro, radio, margenKm());
        encuadrar(centro, radio, margenKm());
    }

    function nuevoCentro() {
        // Cambiar el centro o el radio invalida lo dibujado: se vuelve al borrador.
        if (estado.resultado) {
            estado.resultado = null;
            estado.seleccion = null;
            capaCeldas.clearLayers();
            capaEtiquetas.clearLayers();
            capaSeleccion.clearLayers();
            vaciar(cuerpo);
            divConteo.textContent = 'Pulse Auditar para analizar la nueva área.';
            divTope.hidden = true;
            btnCsv.disabled = true;
        }
        borrador();
    }

    // ---------- eventos ----------

    form.addEventListener('submit', function (e) { e.preventDefault(); auditar(false); });
    btnRecalcular.addEventListener('click', function () { auditar(true); });
    btnCsv.addEventListener('click', exportarCsv);
    inSitio.addEventListener('input', buscarSitio);
    inSitio.addEventListener('blur', function () { setTimeout(ocultarSugerencias, 150); });
    [inLat, inLon, inRadio, inReuso].forEach(function (el) { el.addEventListener('change', nuevoCentro); });
    ['tipo', 'tec', 'carrier', 'banda', 'solo'].forEach(function (k) {
        filtros[k].addEventListener('change', function () {
            estado.seleccion = null;
            pintarTabla();
            redibujar();
        });
    });
    filtros.texto.addEventListener('input', function () { pintarTabla(); redibujar(); });
    mapa.on('click', function (e) {
        if (medicion.activa) {
            agregarPunto({ lat: e.latlng.lat, lon: e.latlng.lng, nombre: null });
            return;
        }
        if (estado.sitio) return;
        inLat.value = e.latlng.lat.toFixed(6);
        inLon.value = e.latlng.lng.toFixed(6);
        inSitio.value = '';
        nuevoCentro();
    });
    // Al cambiar el zoom se ajusta la visibilidad de las etiquetas.
    mapa.on('zoomend', redibujar);
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && medicion.activa) alternarMedicion();
    });

    // ---------- modo sitio: los PCI propuestos de /recursos/asignar ----------
    // El enlace «Ver mapa» de la pantalla de asignación abre /auditoria-red/sitio?sitio=..&ids=..&pci=..
    // El servidor manda las celdas del sitio, sus 30 vecinas existentes por carrier y banda, y las que
    // chocan con ellas. Las celdas de un sitio se tapan entre sí: se elige una y el mapa deja solo su
    // carrier y banda, que es donde el motor busca colisiones y riesgo PSS (mod-3).

    var consulta = new URLSearchParams(location.search);
    var divCeldasSitio = $('ar-sitio-celdas');

    function mismaCelda(a, b) { return a.nombre === b.nombre && a.tecnologia === b.tecnologia; }
    // Mismo pool que el motor (claves_carrier): carrier + banda, sin espacios ni mayúsculas.
    function clave(v) { return String(v || '').trim().toUpperCase(); }
    function mismoGrupo(a, b) {
        return a.tecnologia === b.tecnologia && clave(a.carrier) === clave(b.carrier) && clave(a.banda) === clave(b.banda);
    }
    function esDeLaCelda(h, c) { return mismaCelda(h.celdaA, c) || mismaCelda(h.celdaB, c); }
    function hallazgosDe(c) { return estado.resultado.hallazgos.filter(function (h) { return esDeLaCelda(h, c); }); }

    function planificadas() {
        return estado.resultado.celdas.filter(function (c) { return c.planificada; })
            .sort(function (a, b) { return a.nombre.localeCompare(b.nombre, 'es', { numeric: true }); });
    }

    // Una línea de la celda elegida a cada celda con la que tiene un hallazgo.
    function dibujarCeldaElegida(c) {
        hallazgosVisibles().forEach(function (h) {
            var otra = mismaCelda(h.celdaA, c) ? h.celdaB : h.celdaA;
            L.polyline([[c.latitud, c.longitud], [otra.latitud, otra.longitud]],
                { color: '#000', weight: 2, dashArray: '8 4', interactive: false }).addTo(capaSeleccion);
        });
        dibujarCelda(c, capaSeleccion, true);
    }

    // c = null es «Todas»: todas las celdas del sitio y todos sus hallazgos.
    function seleccionarCelda(c) {
        estado.celda = c;
        estado.seleccion = null;
        pintarCeldasSitio();
        pintarTabla();
        redibujar();
        var limites = L.latLngBounds([]);
        // Encuadra lo que queda dibujado: el sitio, sus vecinas y las celdas con las que choca.
        (c ? [c] : planificadas()).forEach(function (p) { limites.extend(L.latLngBounds(contornoSector(p))); });
        estado.resultado.celdas.filter(celdaPasa).forEach(function (o) { limites.extend([o.latitud, o.longitud]); });
        mapa.fitBounds(limites, { padding: [40, 40], maxZoom: 16 });
    }

    function pintarCeldasSitio() {
        vaciar(divCeldasSitio);
        [null].concat(planificadas()).forEach(function (c) {
            var n = c ? hallazgosDe(c).length : estado.resultado.hallazgos.length;
            var boton = document.createElement('button');
            boton.type = 'button';
            boton.className = 'btn btn-small' + ((c ? estado.celda && mismaCelda(c, estado.celda) : !estado.celda) ? ' btn-info' : '');
            boton.textContent = (c ? c.nombre + ' · ' + carrierBanda(c) + ' · PCI ' + c.pci : 'Todas') + ' · ' +
                (n ? n + (n === 1 ? ' hallazgo' : ' hallazgos') : 'sin hallazgos');
            boton.addEventListener('click', function () { seleccionarCelda(c); });
            divCeldasSitio.appendChild(boton);
        });
    }

    if (consulta.has('ids')) {
        estado.sitio = true;
        form.hidden = true;
        $('ar-titulo').textContent = ['Sitio', consulta.get('sitio'), 'con los PCI propuestos'].filter(Boolean).join(' ');
        $('ar-intro').textContent = 'Vista previa: no se guarda nada. Cada celda del sitio, con el PCI que tiene ahora la ' +
            'pantalla de asignación, se compara con el contexto de red cargado (colisión PCI potencial, ' +
            'coincidencia RSI derivada y riesgo PSS mod-3). Sin canal radio verificado, la coincidencia de frecuencia es provisional. Solo aparecen los hallazgos en que participa una celda del sitio.';
        $('ar-sitio-panel').hidden = false;
        mensaje('Revisando reglas de planificación de las celdas del sitio...', false);
        fetch(URL_SITIO + location.search, { credentials: 'same-origin' }).then(leerRespuesta).then(function (r) {
            mostrar(r);
            // Arranca en la primera celda con hallazgos, que es lo que el usuario vino a ver. Sin hallazgos,
            // en «Todas»: el sitio completo con sus vecinas.
            seleccionarCelda(planificadas().filter(function (c) { return hallazgosDe(c).length > 0; })[0] || null);
        }).catch(function (e) { mensaje(e.message, true); });
    }
}());
