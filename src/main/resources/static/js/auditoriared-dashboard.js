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
    function quantity(value, unit, digits) { return number(value) === null ? 'No disponible' : metric(value, digits) + ' ' + unit; }
    function distance(value) { return number(value) === null ? 'No disponible' : value >= 1000 ? metric(value / 1000, 2) + ' km' : metric(value, 0) + ' m'; }
    function taLabel(cell) {
        var labels = { AUSENTE: 'Sin TA', INVALIDO: 'TA inválido', VALIDO: 'TA válido', VALIDO_FRACCION: 'TA válido (fracción)', VALIDO_PORCENTAJE: 'TA válido (%)', SIN_TRAFICO: 'Sin tráfico TA' };
        if (cell.estadoTa) return labels[cell.estadoTa] || cell.estadoTa.replace(/_/g, ' ');
        return cell.sinTa === true ? 'Sin TA' : cell.sinTa === false ? 'Con TA' : 'TA no disponible';
    }
    function frequency(a, b) { return a.canalRadio && b.canalRadio ? (a.canalRadio === b.canalRadio ? 'Canal radio coincidente: ' + a.canalRadio + '.' : 'Canales radio distintos: ' + a.canalRadio + ' / ' + b.canalRadio + '.') : 'Frecuencia no verificada: falta canal radio (EARFCN / NR-ARFCN).'; }
    function radioSource(cell) {
        var labels = { TA_P80: 'TA P80', RESPALDO_VECINAS: 'Respaldo por vecinas', SIN_VECINAS: 'Sin vecinas para respaldo', TA_DESBORDAMIENTO_DESCONOCIDO: 'TA en intervalo abierto; alcance desconocido' };
        return (labels[cell.fuenteRadio] || cell.fuenteRadio || 'No informado') + (cell.vecinasRespaldo > 0 ? ' (' + cell.vecinasRespaldo + ' vecinas)' : '');
    }
    function generatedAt(value) {
        var date = new Date(value);
        return Number.isNaN(date.getTime()) ? value : date.toLocaleString('es-PE', { timeZone: 'America/Lima', dateStyle: 'short', timeStyle: 'medium' }) + ' (Lima)';
    }
    function key(cell) { return cell.tecnologia + '|' + cell.nombre; }
    var state = { base: null, filters: Object.create(null), controls: Object.create(null), result: null,
        page: 1, target: '', busy: false, pending: false, refreshPending: false, version: 0, search: 0, pickedSite: false, suggestion: -1 };
    var maxRadius = Number(root.dataset.radioMax), maxReuse = Number(root.dataset.reusoMax);
    $('ad-radius').max = maxRadius; $('ad-reuse').max = maxReuse; $('ad-reuse').value = maxReuse;
    var map = L.map('ad-map', { preferCanvas: true }).setView([-9.2, -75], 5);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '&copy; OpenStreetMap contributors' }).addTo(map);
    var areaLayer = L.layerGroup().addTo(map), contextLayer = L.layerGroup().addTo(map), pairLayer = L.layerGroup().addTo(map);
    var selectedPair = null;
    Array.prototype.forEach.call(document.querySelectorAll('.ad-header nav a'), function (link) {
        if (link.pathname.split(';')[0] === window.location.pathname.split(';')[0]) link.setAttribute('aria-current', 'page');
    });
    function status(text, error) { $('ad-status').textContent = text; $('ad-status').className = 'ad-status' + (error ? ' error' : ''); }
    function messageRow(body, text, count) { empty(body); body.closest('table').classList.add('ad-empty-table'); var tr = node('tr', null, body), td = node('td', text, tr); td.colSpan = count; td.className = 'ad-empty'; }
    function selectable(tr, action) {
        tr.dataset.selectable = 'true'; tr.tabIndex = 0;
        tr.title = 'Seleccionar relación y ver evidencia en el mapa';
        function select() {
            Array.prototype.forEach.call(root.querySelectorAll('tr.selected'), function (row) { row.classList.remove('selected'); row.removeAttribute('aria-current'); });
            tr.classList.add('selected'); tr.setAttribute('aria-current', 'true'); action();
        }
        tr.addEventListener('click', select);
        tr.addEventListener('keydown', function (event) { if (event.key === 'Enter' || event.key === ' ') { event.preventDefault(); select(); } });
    }
    function filters(definitions) {
        var active = document.activeElement, focused = active && active.id, start = active && active.selectionStart, end = active && active.selectionEnd;
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
        if (focused && focused.indexOf('ad-filter-') === 0 && $(focused)) {
            $(focused).focus({ preventScroll: true });
            if ($(focused).type === 'text' && start !== null) $(focused).setSelectionRange(start, end);
        }
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
        $('ad-context-note').textContent = 'Contexto opcional: hasta 200 marcadores del área.';
        if (!$('ad-map-context').checked || !state.result) return;
        state.result.celdas.slice(0, 200).forEach(function (cell) {
            var label = node('span', cell.nombre);
            L.circleMarker([cell.latitud, cell.longitud], { radius: 3, color: '#777', weight: 1, fillOpacity: 0.35 }).bindTooltip(label).addTo(contextLayer);
        });
        $('ad-context-note').textContent = 'Contexto visible: ' + Math.min(200, state.result.celdas.length) + ' de ' + state.result.celdas.length + ' celdas del área. El límite visual no modifica el cálculo.';
    }
    function clearPair() {
        pairLayer.clearLayers(); selectedPair = null;
        $('ad-legend-a').textContent = 'Celda A'; $('ad-legend-b').hidden = false;
        Array.prototype.forEach.call(root.querySelectorAll('tr.selected'), function (row) { row.classList.remove('selected'); row.removeAttribute('aria-current'); });
        $('ad-map-note').textContent = 'Seleccione un hallazgo o una vecina.';
        $('ad-pair-detail').textContent = 'Se resaltarán solamente las celdas seleccionadas.';
        empty($('ad-pair-evidence'));
    }
    function pairEvidence(a, b) {
        var container = $('ad-pair-evidence'); empty(container);
        var cells = b ? [a, b] : [a];
        var table = node('table', null, container), head = node('tr', null, node('thead', null, table));
        (b ? ['Dato', 'Celda A', 'Celda B'] : ['Dato', 'Celda objetivo']).forEach(function (text) { var th = node('th', text, head); th.scope = 'col'; });
        var body = node('tbody', null, table);
        function row(label, values) { var tr = node('tr', null, body); var th = node('th', label, tr); th.scope = 'row'; values.forEach(function (text) { node('td', text, tr); }); }
        row('Celda', cells.map(function (c) { return c.nombre; }));
        row('PCI / PSS', cells.map(function (c) { return c.pci + ' / ' + c.mod; }));
        row('Carrier / banda', cells.map(function (c) { return c.carrier + ' / ' + c.banda; }));
        row('Canal radio', cells.map(function (c) { return c.canalRadio || 'No informado'; }));
        row('TA', cells.map(taLabel));
        row('Fuente TA', cells.map(function (c) { return c.fuenteTa || 'No informada'; }));
        row('Radio estimado', cells.map(function (c) { return distance(c.radioM); }));
        row('Origen del radio', cells.map(radioSource));
        row('Azimut / haz', cells.map(function (c) { return quantity(c.azimuthEfectivo, '°', 1) + ' / ' + quantity(c.hbeam, '°', 1); }));
        row('Ubicación', cells.map(function (c) { return c.dentroArea === false ? 'Margen del área' : 'Dentro del área'; }));
        node('p', (b ? frequency(a, b) + ' ' : '') + 'La geometría no acredita interferencia RF ni confusión PCI.', container).className = 'ad-hint';
        if (cells.some(function (c) { return number(c.radioM) === null; })) node('p', 'Sin alcance utilizable: el mapa muestra la ubicación, sin inventar sectores.', container).className = 'ad-hint';
    }
    function showTarget(cell) {
        pairLayer.clearLayers(); selectedPair = { keys: [key(cell)], generation: state.result.generadoEn }; drawCell(cell, '#0073b7');
        $('ad-legend-a').textContent = 'Celda objetivo'; $('ad-legend-b').hidden = true;
        map.fitBounds(L.featureGroup(pairLayer.getLayers()).getBounds(), { padding: [22, 22], maxZoom: 16 });
        $('ad-map-note').textContent = cell.nombre + ' · objetivo';
        $('ad-pair-detail').textContent = 'Celda objetivo. Seleccione una vecina para comparar su evidencia.';
        pairEvidence(cell);
    }
    function showPair(a, b, explanation) {
        pairLayer.clearLayers(); selectedPair = { keys: [key(a), key(b)], generation: state.result.generadoEn };
        $('ad-legend-a').textContent = 'Celda A'; $('ad-legend-b').hidden = false;
        drawCell(a, '#0073b7'); drawCell(b, '#da272c');
        L.polyline([[a.latitud, a.longitud], [b.latitud, b.longitud]], { color: '#444', weight: 2, dashArray: '5 6' }).addTo(pairLayer);
        map.fitBounds(L.featureGroup(pairLayer.getLayers()).getBounds(), { padding: [22, 22], maxZoom: 16 });
        $('ad-map-note').textContent = a.nombre + ' ↔ ' + b.nombre;
        $('ad-pair-detail').textContent = explanation;
        pairEvidence(a, b);
    }
    function reconcileSelection(result) {
        if (!selectedPair || selectedPair.generation === result.generadoEn) return;
        var loaded = Object.create(null);
        function add(cell) { if (cell) loaded[key(cell)] = cell; }
        result.celdas.forEach(add); add(result.objetivo);
        result.vecinos.forEach(function (neighbor) { add(neighbor.celda); });
        result.hallazgos.forEach(function (finding) { add(finding.celdaA); add(finding.celdaB); });
        var cells = selectedPair.keys.map(function (identity) { return loaded[identity]; });
        if (cells.some(function (cell) { return !cell; })) { clearPair(); return; }
        if (cells.length === 1) showTarget(cells[0]);
        else showPair(cells[0], cells[1], 'Relación conservada con los datos del cálculo actual. Revise su evidencia y su presencia en los hallazgos actuales.');
    }
    function render(result) {
        state.result = result; state.page = result.pagina;
        $('ad-incomplete').hidden = !result.incompleto;
        $('ad-count-cells').textContent = metric(result.celdas.length, 0);
        $('ad-count-findings').textContent = metric(result.totalHallazgos, 0);
        $('ad-count-filtered').textContent = metric(result.totalFiltrados, 0);
        var unknown = result.celdas.some(function (c) { return c.sinTa === null || c.sinTa === undefined; });
        $('ad-count-no-ta').textContent = unknown ? 'Sin datos' : metric(result.celdas.filter(function (c) { return c.sinTa; }).length, 0);
        var noFrequency = result.celdas.filter(function (c) { return !c.canalRadio; }).length;
        $('ad-evidence-warning').hidden = !result.celdas.length;
        $('ad-evidence-warning').textContent = (noFrequency ? metric(noFrequency, 0) + ' celdas del área sin canal radio: las coincidencias carrier/banda son provisionales. ' : '')
            + 'PSS (mod-3) no es confusión PCI. RSI corresponde a la tabla derivada de PCI, sin configuración PRACH verificada.';
        filters(result.filtros);
        empty($('ad-findings'));
        $('ad-findings').closest('table').classList.remove('ad-empty-table');
        result.hallazgos.forEach(function (finding) {
            var tr = node('tr', null, $('ad-findings')), tag = node('span', finding.etiqueta, node('td', null, tr));
            tag.className = 'ad-tag' + (finding.tipo === 'PCI_COLLISION' ? ' pci' : '');
            [finding.celdaA, finding.celdaB].forEach(function (cell) { var td = node('td', cell.nombre, tr); node('span', cell.tecnologia + ' · PCI ' + cell.pci + ' · PSS ' + cell.mod, td).className = 'ad-cell-meta'; });
            node('td', finding.celdaA.carrier + ' / ' + finding.celdaA.banda, tr);
            node('td', distance(finding.distanciaMetros), tr);
            var evidence = node('td', finding.motivo || 'Regla de reuso; revisar el contexto.', tr);
            node('span', finding.evidenciaFrecuencia === 'CANAL_COINCIDENTE' ? 'Canal coincidente' : 'Frecuencia no verificada', evidence).className = 'ad-frequency';
            selectable(tr, function () {
                showPair(finding.celdaA, finding.celdaB, finding.etiqueta + ' · ' + distance(finding.distanciaMetros) + '\n' + (finding.motivo || 'Coincidencia de regla de planificación; requiere revisión.'));
            });
        });
        if (!result.hallazgos.length) messageRow($('ad-findings'), result.incompleto ? 'Sin hallazgos visibles en un resultado incompleto. No certifica ausencia de conflictos.' : 'Sin hallazgos con estos filtros, reglas y contexto. No certifica que un PCI sea seguro.', 6);
        $('ad-page-label').textContent = 'Página ' + result.pagina + ' de ' + result.totalPaginas + ' · ' + result.totalFiltrados + ' hallazgos';
        $('ad-prev').disabled = result.pagina <= 1; $('ad-next').disabled = result.pagina >= result.totalPaginas;
        var select = $('ad-target'); empty(select); node('option', 'Seleccione una celda', select).value = '';
        result.celdas.forEach(function (cell) { node('option', cell.tecnologia + ' · ' + cell.nombre, select).value = key(cell); });
        select.value = state.target;
        if (select.value !== state.target) state.target = '';
        empty($('ad-neighbors'));
        $('ad-neighbors').closest('table').classList.remove('ad-empty-table');
        result.vecinos.forEach(function (neighbor) {
            var cell = neighbor.celda, tr = node('tr', null, $('ad-neighbors'));
            node('td', cell.nombre, tr); node('td', cell.pci + ' / ' + cell.mod + (neighbor.mismoPci ? ' · PCI igual' : ''), tr);
            node('td', distance(neighbor.distanciaMetros), tr); node('td', quantity(neighbor.interseccionNormalizadaM, 'm', 2), tr);
            node('td', taLabel(cell), tr);
            selectable(tr, function () {
                showPair(result.objetivo, cell, 'Distancia: ' + distance(neighbor.distanciaMetros)
                    + '\nÁrea de solapamiento estimada: ' + quantity(neighbor.areaInterseccionM2, 'm²', 1)
                    + '\nSolapamiento / distancia: ' + quantity(neighbor.interseccionNormalizadaM, 'm', 2)
                    + '\nPCI igual: ' + (neighbor.mismoPci ? 'sí' : 'no') + ' · PSS igual: ' + (neighbor.mismoMod3 ? 'sí' : 'no')
                    + '\nGeometría de planificación; no es una medición RF.');
            });
        });
        if (!result.vecinos.length) messageRow($('ad-neighbors'), result.objetivo ? 'No hay otras celdas del mismo carrier/banda en el contexto cargado.' : 'Seleccione una celda objetivo.', 5);
        reconcileSelection(result);
        if (result.objetivo && !selectedPair) showTarget(result.objetivo);
        empty($('ad-notices')); result.avisos.forEach(function (notice) { node('li', notice, $('ad-notices')); });
        empty($('ad-engines'));
        result.resumen.forEach(function (r) {
            var section = node('div', null, $('ad-engines')); section.className = 'ad-engine';
            node('strong', r.tecnologia + ' · ' + r.filasLeidas + ' filas leídas', section);
            node('div', 'Área: ' + r.celdasEnArea + ' · Margen: ' + r.celdasEnMargen + ' · Excluidas por datos: ' + r.descartadasSinDatos + ' · Duplicadas: ' + r.duplicadas, section);
            node('div', 'Motor: ' + r.motor, section);
            if (number(r.duplicadasConflictivas) !== null) node('div', 'Duplicadas con conflicto: ' + r.duplicadasConflictivas + ' · Sin frecuencia: ' + r.celdasSinFrecuencia + ' · TA ausente: ' + r.taAusente + ' · TA inválido: ' + r.taInvalido, section);
            if (number(r.taOverflow) !== null) node('div', 'TA en intervalo abierto: ' + r.taOverflow + ' · Sin radio utilizable: ' + r.geometriaSinRadio, section);
        });
        areaLayer.clearLayers();
        L.circle([result.alcance.latitud, result.alcance.longitud], { radius: result.alcance.radioKm * 1000, color: '#0073b7', fill: false, weight: 1 }).addTo(areaLayer);
        if (!selectedPair) map.fitBounds(L.latLng(result.alcance.latitud, result.alcance.longitud).toBounds(result.alcance.radioKm * 2000), { maxZoom: 14 });
        showContext();
        status('Cálculo ' + generatedAt(result.generadoEn) + (result.desdeCache ? ' · Resultado en caché' : ' · Datos recalculados')
            + (number(result.edadSegundos) !== null ? ' · Antigüedad del cálculo: ' + metric(result.edadSegundos, 0) + ' s' : '')
            + ' · Área ' + metric(result.alcance.radioKm, 1) + ' km + margen ' + metric(result.alcance.margenKm, 1) + ' km');
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
        clearPair(); contextLayer.clearLayers(); areaLayer.clearLayers();
        ['ad-count-cells', 'ad-count-findings', 'ad-count-filtered', 'ad-count-no-ta'].forEach(function (id) { $(id).textContent = '—'; });
        messageRow($('ad-findings'), 'Consulte el área modificada para actualizar los hallazgos.', 6);
        messageRow($('ad-neighbors'), 'Seleccione una celda después de consultar.', 5);
        empty($('ad-target')); node('option', 'Consulte un área primero', $('ad-target')).value = '';
        $('ad-prev').disabled = $('ad-next').disabled = true; $('ad-incomplete').hidden = true;
        $('ad-evidence-warning').hidden = true; $('ad-page-label').textContent = 'Sin consulta';
        $('ad-context-note').textContent = 'Contexto opcional: hasta 200 marcadores del área.';
        empty($('ad-notices')); empty($('ad-engines'));
        status('Área modificada. Pulse Consultar área.');
    }
    function submit(refresh) {
        if (!$('ad-form').reportValidity()) return;
        var lat = number($('ad-lat').value), lon = number($('ad-lon').value), radius = number($('ad-radius').value), reuse = number($('ad-reuse').value);
        var technologies = []; if ($('ad-4g').checked) technologies.push('4G'); if ($('ad-5g').checked) technologies.push('5G');
        if (lat === null || lon === null || Math.abs(lat) > 90 || Math.abs(lon) > 180 || !(radius > 0) || radius > maxRadius || !(reuse >= 0.5) || reuse > maxReuse || !technologies.length) { status('Revise el centro, los radios y las tecnologías seleccionadas.', true); return; }
        state.version++; state.page = 1; state.target = ''; clearPair();
        state.base = { sitio: $('ad-site').value || null, latitud: lat, longitud: lon, radioKm: radius, radioReusoKm: reuse, tecnologias: technologies };
        query(refresh);
    }
    $('ad-form').addEventListener('submit', function (event) { event.preventDefault(); submit(false); });
    $('ad-refresh').addEventListener('click', function () { submit(true); });
    ['ad-lat', 'ad-lon', 'ad-radius', 'ad-reuse', 'ad-4g', 'ad-5g'].forEach(function (id) { $(id).addEventListener('change', invalidate); });
    $('ad-target').addEventListener('change', function () { state.target = this.value; clearPair(); query(false); });
    $('ad-neighbor-limit').addEventListener('change', function () { if (this.reportValidity()) query(false); });
    $('ad-map-context').addEventListener('change', showContext);
    $('ad-prev').addEventListener('click', function () { state.page--; query(false); });
    $('ad-next').addEventListener('click', function () { state.page++; query(false); });
    $('ad-reset').addEventListener('click', function () { state.filters = Object.create(null); state.page = 1; Object.keys(state.controls).forEach(function (id) { state.controls[id].value = ''; }); query(false); });
    function suggestionsVisible(visible) {
        $('ad-suggestions').hidden = !visible; $('ad-site').setAttribute('aria-expanded', String(visible));
        state.suggestion = -1; $('ad-site').removeAttribute('aria-activedescendant');
        Array.prototype.forEach.call($('ad-suggestions').children, function (option) { option.setAttribute('aria-selected', 'false'); });
    }
    $('ad-site').addEventListener('keydown', function (event) {
        var options = $('ad-suggestions').children;
        if (event.key === 'Escape') { suggestionsVisible(false); return; }
        if (!options.length || $('ad-suggestions').hidden) return;
        if (event.key === 'Enter' && state.suggestion >= 0) { event.preventDefault(); options[state.suggestion].click(); return; }
        if (event.key !== 'ArrowDown' && event.key !== 'ArrowUp') return;
        event.preventDefault();
        state.suggestion = state.suggestion < 0 ? (event.key === 'ArrowDown' ? 0 : options.length - 1)
            : (state.suggestion + (event.key === 'ArrowDown' ? 1 : -1) + options.length) % options.length;
        Array.prototype.forEach.call(options, function (option, index) { option.setAttribute('aria-selected', String(index === state.suggestion)); });
        $('ad-site').setAttribute('aria-activedescendant', options[state.suggestion].id);
        options[state.suggestion].scrollIntoView({ block: 'nearest' });
    });
    $('ad-site').addEventListener('input', function () {
        var sequence = ++state.search, text = this.value.trim(); clearTimeout(state.searchTimer);
        if (state.pickedSite) { $('ad-lat').value = $('ad-lon').value = ''; state.pickedSite = false; invalidate(); }
        empty($('ad-suggestions')); suggestionsVisible(false);
        if (text.length < 2) return;
        state.searchTimer = setTimeout(function () {
            fetch(root.dataset.searchUrl + '?q=' + encodeURIComponent(text), { credentials: 'same-origin', headers: http.headers(false) }).then(http.read).then(function (sites) {
                if (sequence !== state.search) return;
                sites.forEach(function (site, index) {
                    var button = node('button', site.nombre + ' · ' + site.tecnologia, $('ad-suggestions')); button.type = 'button'; button.setAttribute('role', 'option');
                    button.id = 'ad-site-option-' + index; button.tabIndex = -1; button.setAttribute('aria-selected', 'false');
                    button.addEventListener('click', function () { $('ad-site').value = site.nombre; $('ad-lat').value = site.latitud; $('ad-lon').value = site.longitud; state.pickedSite = true; suggestionsVisible(false); invalidate(); $('ad-site').focus({ preventScroll: true }); });
                });
                suggestionsVisible(sites.length > 0);
                if (!sites.length) status('No se encontraron sitios. Puede introducir las coordenadas del área.');
            }).catch(function (error) { if (sequence === state.search) status(error.message, true); });
        }, 300);
    });
    document.addEventListener('click', function (event) { if (!event.target.closest('.ad-search')) { state.search++; clearTimeout(state.searchTimer); suggestionsVisible(false); } });
    document.addEventListener('keydown', function (event) { if (event.key === 'Escape') { state.search++; clearTimeout(state.searchTimer); suggestionsVisible(false); } });
    fetch(root.dataset.filtersUrl, { credentials: 'same-origin', headers: http.headers(false) }).then(http.read).then(filters).catch(function (error) { status(error.message, true); });
}());
