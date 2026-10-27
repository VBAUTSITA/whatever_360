(function () {
    'use strict';
    var root = document.getElementById('ad-panel');
    if (!root) return;
    var http = window.AuditoriaHttp;
    function $(id) { return document.getElementById(id); }
    function empty(el) { while (el.firstChild) el.removeChild(el.firstChild); }
    function node(tag, text, parent) {
        var el = document.createElement(tag);
        if (text !== undefined && text !== null) el.textContent = String(text);
        if (parent) parent.appendChild(el);
        return el;
    }
    function number(value) {
        if (value === null || value === undefined || String(value).trim() === '') return null;
        var n = Number(value); return Number.isFinite(n) ? n : null;
    }
    function metric(value, digits) { var n = number(value); return n === null ? '—' : n.toLocaleString('es-PE', { maximumFractionDigits: digits === undefined ? 1 : digits }); }
    function distance(value) { return value >= 1000 ? metric(value / 1000, 2) + ' km' : metric(value, 0) + ' m'; }
    function key(cell) { return cell.tecnologia + '|' + cell.nombre; }
    var state = { base: null, filters: Object.create(null), controls: Object.create(null), result: null,
        page: 1, target: '', busy: false, pending: false, refreshPending: false, version: 0, search: 0, pickedSite: false };
    var maxRadius = Number(root.dataset.radioMax), maxReuse = Number(root.dataset.reusoMax);
    $('ad-radius').max = maxRadius; $('ad-reuse').max = maxReuse; $('ad-reuse').value = maxReuse;
    var map = L.map('ad-map', { preferCanvas: true }).setView([-9.2, -75], 5);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '&copy; OpenStreetMap contributors' }).addTo(map);
    var areaLayer = L.layerGroup().addTo(map), contextLayer = L.layerGroup().addTo(map), pairLayer = L.layerGroup().addTo(map);
    var selectedPair = null;
    function status(text, error) { $('ad-status').textContent = text; $('ad-status').className = 'ad-status' + (error ? ' error' : ''); }
    function messageRow(body, text, count) { empty(body); var tr = node('tr', null, body), td = node('td', text, tr); td.colSpan = count; td.className = 'ad-empty'; }
    function selectable(tr, action) {
        tr.dataset.selectable = 'true'; tr.tabIndex = 0;
        tr.addEventListener('click', action);
        tr.addEventListener('keydown', function (event) { if (event.key === 'Enter' || event.key === ' ') { event.preventDefault(); action(); } });
    }
    function filters(definitions) {
        empty($('ad-filters')); state.controls = Object.create(null);
        definitions.forEach(function (definition) {
            var group = node('div', null, $('ad-filters')); group.className = 'ad-filter';
            var label = node('label', definition.etiqueta, group), input;
            label.htmlFor = 'ad-filter-' + definition.id;
            if (definition.tipo === 'SELECCION') {
                input = node('select', null, group); var option = node('option', 'Todos', input); option.value = '';
                definition.opciones.forEach(function (value) { var op = node('option', value, input); op.value = value; });
            } else {
                input = node('input', null, group); input.type = definition.tipo === 'MAXIMO' ? 'number' : 'text';
                if (input.type === 'number') { input.min = 0; input.step = 'any'; } else input.maxLength = 200;
            }
            input.id = 'ad-filter-' + definition.id;
            input.value = (state.filters[definition.id] || [''])[0];
            input.addEventListener(definition.tipo === 'TEXTO' ? 'input' : 'change', function () {
                var value = input.value.trim();
                if (value) state.filters[definition.id] = [value]; else delete state.filters[definition.id];
                state.page = 1;
                clearTimeout(state.filterTimer); state.filterTimer = setTimeout(function () { query(false); }, definition.tipo === 'TEXTO' ? 350 : 0);
            });
            state.controls[definition.id] = input;
        });
    }
    function destination(lat, lon, bearing, meters) {
        var radians = Math.PI / 180, d = meters / 6371008.8, a = lat * radians, b = bearing * radians;
        var targetLat = Math.asin(Math.sin(a) * Math.cos(d) + Math.cos(a) * Math.sin(d) * Math.cos(b));
        var targetLon = lon * radians + Math.atan2(Math.sin(b) * Math.sin(d) * Math.cos(a), Math.cos(d) - Math.sin(a) * Math.sin(targetLat));
        return [targetLat / radians, targetLon / radians];
    }
    function drawCell(cell, color) {
        var text = node('div', cell.nombre + '\n' + cell.tecnologia + ' · ' + cell.carrier + ' / ' + cell.banda + '\nPCI ' + cell.pci);
        text.style.whiteSpace = 'pre-line';
        if (number(cell.radioM) > 0 && number(cell.hbeam) > 0 && number(cell.azimuthEfectivo) !== null) {
            var points = [[cell.latitud, cell.longitud]];
            for (var i = 0; i <= 40; i++) points.push(destination(cell.latitud, cell.longitud, cell.azimuthEfectivo - cell.hbeam / 2 + cell.hbeam * i / 40, cell.radioM));
            points.push([cell.latitud, cell.longitud]);
            L.polygon(points, { color: color, weight: 2, fillOpacity: 0.13 }).bindPopup(text).addTo(pairLayer);
        }
        L.circleMarker([cell.latitud, cell.longitud], { radius: 5, color: color, fillOpacity: 1 }).bindPopup(text).addTo(pairLayer);
    }
    function showContext() {
        contextLayer.clearLayers();
        if (!$('ad-map-context').checked || !state.result) return;
        state.result.celdas.slice(0, 200).forEach(function (cell) {
            var label = node('span', cell.nombre);
            L.circleMarker([cell.latitud, cell.longitud], { radius: 3, color: '#7b9691', weight: 1, fillOpacity: 0.35 }).bindTooltip(label).addTo(contextLayer);
        });
    }
    function showPair(a, b, explanation) {
        pairLayer.clearLayers(); selectedPair = [key(a), key(b)];
        drawCell(a, '#126c64'); drawCell(b, '#bf5a24');
        L.polyline([[a.latitud, a.longitud], [b.latitud, b.longitud]], { color: '#173439', weight: 2, dashArray: '5 6' }).addTo(pairLayer);
        map.fitBounds(L.latLngBounds([[a.latitud, a.longitud], [b.latitud, b.longitud]]), { padding: [40, 40], maxZoom: 16 });
        $('ad-map-note').textContent = a.nombre + ' ↔ ' + b.nombre;
        $('ad-pair-detail').textContent = explanation;
    }
    function render(result) {
        state.result = result; state.page = result.pagina;
        $('ad-incomplete').hidden = !result.incompleto;
        $('ad-count-cells').textContent = metric(result.celdas.length, 0);
        $('ad-count-findings').textContent = metric(result.totalHallazgos, 0);
        $('ad-count-filtered').textContent = metric(result.totalFiltrados, 0);
        var unknown = result.celdas.some(function (c) { return c.sinTa === null || c.sinTa === undefined; });
        $('ad-count-no-ta').textContent = unknown ? 'Sin datos' : metric(result.celdas.filter(function (c) { return c.sinTa; }).length, 0);
        filters(result.filtros);
        empty($('ad-findings'));
        result.hallazgos.forEach(function (finding) {
            var tr = node('tr', null, $('ad-findings')), tag = node('span', finding.etiqueta, node('td', null, tr));
            tag.className = 'ad-tag' + (finding.tipo === 'PCI_COLLISION' ? ' pci' : '');
            node('td', finding.celdaA.nombre, tr); node('td', finding.celdaB.nombre, tr);
            node('td', finding.celdaA.carrier + ' / ' + finding.celdaA.banda, tr);
            node('td', distance(finding.distanciaMetros), tr); node('td', finding.motivo || 'Regla de reuso; revisar el contexto.', tr);
            selectable(tr, function () {
                showPair(finding.celdaA, finding.celdaB, finding.etiqueta + ' · ' + distance(finding.distanciaMetros) + '\n' + (finding.motivo || 'Coincidencia de regla de planificación; requiere revisión.'));
            });
        });
        if (!result.hallazgos.length) messageRow($('ad-findings'), result.incompleto ? 'Sin hallazgos visibles en un resultado incompleto.' : 'Sin hallazgos con estos filtros, reglas y contexto.', 6);
        $('ad-page-label').textContent = 'Página ' + result.pagina + ' de ' + result.totalPaginas + ' · ' + result.totalFiltrados + ' hallazgos';
        $('ad-prev').disabled = result.pagina <= 1; $('ad-next').disabled = result.pagina >= result.totalPaginas;
        var select = $('ad-target'); empty(select); node('option', 'Seleccione una celda', select).value = '';
        result.celdas.forEach(function (cell) { node('option', cell.tecnologia + ' · ' + cell.nombre, select).value = key(cell); });
        select.value = state.target;
        if (select.value !== state.target) state.target = '';
        empty($('ad-neighbors'));
        result.vecinos.forEach(function (neighbor) {
            var cell = neighbor.celda, tr = node('tr', null, $('ad-neighbors'));
            node('td', cell.nombre, tr); node('td', cell.pci + ' / ' + cell.mod + (neighbor.mismoPci ? ' · PCI igual' : ''), tr);
            node('td', distance(neighbor.distanciaMetros), tr); node('td', metric(neighbor.interseccionNormalizadaM, 2) + (neighbor.interseccionNormalizadaM === null ? '' : ' m'), tr);
            node('td', neighbor.sinTaVecina ? 'Sin TA' : 'Con TA', tr);
            selectable(tr, function () {
                showPair(result.objetivo, cell, 'Distancia: ' + distance(neighbor.distanciaMetros)
                    + '\nÁrea de solapamiento estimada: ' + metric(neighbor.areaInterseccionM2, 1) + ' m²'
                    + '\nSolapamiento / distancia: ' + metric(neighbor.interseccionNormalizadaM, 2) + ' m'
                    + '\nPCI igual: ' + (neighbor.mismoPci ? 'sí' : 'no') + ' · PSS igual: ' + (neighbor.mismoMod3 ? 'sí' : 'no')
                    + '\nGeometría de planificación; no es una medición RF.');
            });
        });
        if (!result.vecinos.length) messageRow($('ad-neighbors'), result.objetivo ? 'No hay otras celdas del mismo carrier/banda en el contexto cargado.' : 'Seleccione una celda objetivo.', 5);
        empty($('ad-notices')); result.avisos.forEach(function (notice) { node('li', notice, $('ad-notices')); });
        $('ad-engines').textContent = result.resumen.map(function (r) { return r.tecnologia + ': ' + r.motor + ' · ' + r.filasLeidas + ' filas leídas'; }).join(' | ');
        areaLayer.clearLayers();
        L.circle([result.alcance.latitud, result.alcance.longitud], { radius: result.alcance.radioKm * 1000, color: '#126c64', fill: false, weight: 1 }).addTo(areaLayer);
        if (!selectedPair) map.fitBounds(L.latLng(result.alcance.latitud, result.alcance.longitud).toBounds(result.alcance.radioKm * 2000), { maxZoom: 14 });
        showContext();
        status('Consulta ' + result.generadoEn + (result.desdeCache ? ' · Resultado en caché' : ' · Datos recalculados') + ' · Contexto: ' + metric(result.alcance.radioKm + result.alcance.margenKm, 1) + ' km');
    }
    function query(refresh) {
        if (!state.base) return;
        if (state.busy) { state.pending = true; state.refreshPending = state.refreshPending || refresh; return; }
        var payload = Object.assign({}, state.base, { filtros: state.filters, pagina: state.page, tamanoPagina: 25,
            limiteVecinas: number($('ad-neighbor-limit').value), refrescar: !!refresh });
        if (state.target) { var target = state.result.celdas.find(function (c) { return key(c) === state.target; }); if (target) { payload.celdaObjetivo = target.nombre; payload.tecnologiaObjetivo = target.tecnologia; } }
        state.busy = true; var version = state.version;
        $('ad-run').disabled = $('ad-refresh').disabled = true;
        root.setAttribute('aria-busy', 'true'); status('Consultando el área y su contexto…');
        fetch(root.dataset.queryUrl, { method: 'POST', credentials: 'same-origin', headers: http.headers(true), body: JSON.stringify(payload) })
            .then(http.read).then(function (result) { if (version === state.version && !state.pending) render(result); })
            .catch(function (error) { if (version === state.version) status(error.message, true); })
            .then(function () {
                state.busy = false; $('ad-run').disabled = $('ad-refresh').disabled = false; root.setAttribute('aria-busy', 'false');
                if (state.pending) { var again = state.refreshPending; state.pending = state.refreshPending = false; query(again); }
            });
    }
    function invalidate() {
        state.version++; state.base = null; state.result = null; state.target = ''; selectedPair = null;
        pairLayer.clearLayers(); contextLayer.clearLayers(); areaLayer.clearLayers();
        ['ad-count-cells', 'ad-count-findings', 'ad-count-filtered', 'ad-count-no-ta'].forEach(function (id) { $(id).textContent = '—'; });
        messageRow($('ad-findings'), 'Consulte el área modificada para actualizar los hallazgos.', 6);
        messageRow($('ad-neighbors'), 'Seleccione una celda después de consultar.', 5);
        empty($('ad-target')); node('option', 'Consulte un área primero', $('ad-target')).value = '';
        $('ad-prev').disabled = $('ad-next').disabled = true; $('ad-incomplete').hidden = true;
        status('Área modificada. Pulse Consultar área.');
    }
    function submit(refresh) {
        if (!$('ad-form').reportValidity()) return;
        var lat = number($('ad-lat').value), lon = number($('ad-lon').value), radius = number($('ad-radius').value), reuse = number($('ad-reuse').value);
        var technologies = []; if ($('ad-4g').checked) technologies.push('4G'); if ($('ad-5g').checked) technologies.push('5G');
        if (lat === null || lon === null || Math.abs(lat) > 90 || Math.abs(lon) > 180 || !(radius > 0) || radius > maxRadius || !(reuse >= 0.5) || reuse > maxReuse || !technologies.length) { status('Revise el centro, los radios y las tecnologías seleccionadas.', true); return; }
        state.version++; state.page = 1; selectedPair = null; state.target = ''; pairLayer.clearLayers();
        state.base = { sitio: $('ad-site').value || null, latitud: lat, longitud: lon, radioKm: radius, radioReusoKm: reuse, tecnologias: technologies };
        query(refresh);
    }
    $('ad-form').addEventListener('submit', function (event) { event.preventDefault(); submit(false); });
    $('ad-refresh').addEventListener('click', function () { submit(true); });
    ['ad-lat', 'ad-lon', 'ad-radius', 'ad-reuse', 'ad-4g', 'ad-5g'].forEach(function (id) { $(id).addEventListener('change', invalidate); });
    $('ad-target').addEventListener('change', function () { state.target = this.value; selectedPair = null; pairLayer.clearLayers(); query(false); });
    $('ad-neighbor-limit').addEventListener('change', function () { query(false); });
    $('ad-map-context').addEventListener('change', showContext);
    $('ad-prev').addEventListener('click', function () { state.page--; query(false); });
    $('ad-next').addEventListener('click', function () { state.page++; query(false); });
    $('ad-reset').addEventListener('click', function () { state.filters = Object.create(null); state.page = 1; Object.keys(state.controls).forEach(function (id) { state.controls[id].value = ''; }); query(false); });
    $('ad-site').addEventListener('input', function () {
        var sequence = ++state.search, text = this.value.trim(); clearTimeout(state.searchTimer);
        if (state.pickedSite) { $('ad-lat').value = $('ad-lon').value = ''; state.pickedSite = false; invalidate(); }
        empty($('ad-suggestions')); $('ad-suggestions').hidden = true;
        if (text.length < 2) return;
        state.searchTimer = setTimeout(function () {
            fetch(root.dataset.searchUrl + '?q=' + encodeURIComponent(text), { credentials: 'same-origin', headers: http.headers(false) }).then(http.read).then(function (sites) {
                if (sequence !== state.search) return;
                sites.forEach(function (site) {
                    var button = node('button', site.nombre + ' · ' + site.tecnologia, $('ad-suggestions')); button.type = 'button'; button.setAttribute('role', 'option');
                    button.addEventListener('click', function () { $('ad-site').value = site.nombre; $('ad-lat').value = site.latitud; $('ad-lon').value = site.longitud; state.pickedSite = true; $('ad-suggestions').hidden = true; invalidate(); });
                });
                $('ad-suggestions').hidden = sites.length === 0;
            }).catch(function (error) { if (sequence === state.search) status(error.message, true); });
        }, 300);
    });
    document.addEventListener('click', function (event) { if (!event.target.closest('.ad-search')) $('ad-suggestions').hidden = true; });
    document.addEventListener('keydown', function (event) { if (event.key === 'Escape') $('ad-suggestions').hidden = true; });
    fetch(root.dataset.filtersUrl, { credentials: 'same-origin', headers: http.headers(false) }).then(http.read).then(filters).catch(function (error) { status(error.message, true); });
}());
