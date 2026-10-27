/* Auditoría Red v2: capa propia; conserva planners originales de Recursos.
 * Haversine R=6371000m. Modelo geométrico, no certifica RF ni confusión.
 */
var AuditoriaModelo = (function (api) {
    'use strict';
    var columnas = null, limites = null;
    function clave(s) { return String(s == null ? '' : s).trim().toUpperCase(); }
    function numero(n) { return typeof n === 'number' && isFinite(n); }
    function ordenarNombre(a, b) { a = clave(a); b = clave(b); return a < b ? -1 : a > b ? 1 : 0; }
    function compatible(a, b) {
        if (a._canal_radio && b._canal_radio) return a._canal_radio === b._canal_radio;
        // Sin ambos canales sólo se evalúa el grupo candidato carrier/banda; no prueba co-canal.
        return clave(a.Carrier) === clave(b.Carrier) && clave(a.Freqband) === clave(b.Freqband);
    }
    function distancia(p, i, j) { return api.haversine_m(p.lat[i], p.lon[i], p.lat[j], p.lon[j]); }
    function cercanas(p, i, k) {
        var lista = [];
        for (var j = 0; j < p.n; j++) if (j !== i && compatible(p._filasAudit[i], p._filasAudit[j])) lista.push({ i: j, d: distancia(p, i, j) });
        lista.sort(function (a, b) { return a.d - b.d || ordenarNombre(p.nombre[a.i], p.nombre[b.i]); });
        return lista.slice(0, k);
    }
    function radioTa(fila) {
        if (String(fila._estado_ta).indexOf('VALIDO') !== 0) return null;
        var acumulado = 0;
        for (var b = 0; b < columnas.length; b++) {
            acumulado += Number(fila[columnas[b]]) || 0;
            if (acumulado >= 0.80 - 1e-12) return b < limites.length ? limites[b] : null;
        }
        return null;
    }
    function pool(filas) {
        var p = new api.PoolCarrier(filas, api.DEFAULTS);
        p._filasAudit = filas; p._fuenteRadioAudit = []; p._vecinasAudit = []; p._radioAudit = [];
        p.radioEfectivo = function (i) { return this._radioAudit[i]; };
        p.nCercanasIdx = function (i, k) { return cercanas(this, i, k).map(function (v) { return v.i; }); };
        for (var i = 0; i < p.n; i++) {
            var estado = String(filas[i]._estado_ta), radio = radioTa(filas[i]);
            p.sinTa[i] = estado.indexOf('VALIDO') !== 0; p._vecinasAudit[i] = 0;
            if (!p.sinTa[i]) {
                p._radioAudit[i] = radio;
                p._fuenteRadioAudit[i] = radio === null ? 'TA_DESBORDAMIENTO_DESCONOCIDO' : 'TA_P80';
            } else {
                // Cosites <=1m no aportan escala espacial. Sin vecinas, radio desconocido.
                var vecinas = cercanas(p, i, p.n).filter(function (v) { return v.d > 1; }).slice(0, 30);
                var suma = 0; for (var n = 0; n < vecinas.length; n++) suma += vecinas[n].d;
                p._vecinasAudit[i] = vecinas.length;
                p._radioAudit[i] = vecinas.length ? suma / vecinas.length : null;
                p._fuenteRadioAudit[i] = vecinas.length ? 'RESPALDO_VECINAS' : 'SIN_VECINAS';
            }
        }
        return p;
    }
    function configurar(tecnologia) {
        columnas = tecnologia === '5G' ? ['ta_0_117_mts_porc','ta_117_507_mts_porc','ta_507_975_mts_porc','ta_975_1775_mts_porc','ta_1775_3315_mts_porc','ta_3315_7215_mts_porc','ta_7215_15015_mts_porc','ta_mayor_a_15015_mts_porc'] : ['ta_0_234_mts_porc','ta_234_546_mts_porc','ta_546_1014_mts_porc','ta_1014_1950_mts_porc','ta_1950_3510_mts_porc','ta_3510_6630_mts_porc','ta_6630_14430_mts_porc','ta_mayor_a_14430_mts_porc'];
        limites = tecnologia === '5G' ? [117,507,975,1775,3315,7215,15015] : [234,546,1014,1950,3510,6630,14430];
    }
    function diferencia(a, b) { return Math.abs(((a - b + 540) % 360) - 180); }
    function visible(p, i, j, d) {
        var a = p.radioEfectivo(i), b = p.radioEfectivo(j);
        if (!numero(a) || !numero(b) || d <= 1 || d > a || d > b) return false;
        return diferencia(api.rumbo(p.lat[i],p.lon[i],p.lat[j],p.lon[j]), p.azEf[i]) <= p.hbeam[i]/2 &&
            diferencia(api.rumbo(p.lat[j],p.lon[j],p.lat[i],p.lon[i]), p.azEf[j]) <= p.hbeam[j]/2;
    }
    function geometria(p, i) {
        return { az: p.azEf[i], hbeam: p.hbeam[i], radio: p.radioEfectivo(i), sinTa: p.sinTa[i],
            fuenteRadio: p._fuenteRadioAudit[i], vecinasRespaldo: p._vecinasAudit[i] };
    }
    return {
        configurar: configurar, pool: pool, cercanas: cercanas, compatible: compatible, geometria: geometria,
        auditar: function (grupos, opciones) {
            var tipos = ['PCI_COLLISION','PCI_MOD3','RSI_COLLISION'], conteo = {}, capped = {}, salida = [];
            tipos.forEach(function (t) { conteo[t] = 0; });
            function agregar(tipo, p, i, j, d, valor, motivo) {
                if (conteo[tipo] >= opciones.cap) { capped[tipo] = true; return; }
                conteo[tipo]++;
                salida.push({type:tipo,aName:p.nombre[i],bName:p.nombre[j],dist:d,val:valor,motivo:motivo});
            }
            Object.keys(grupos).sort().forEach(function (grupo) {
                var filas = grupos[grupo].slice().sort(function (a,b) { return ordenarNombre(a.Cellname,b.Cellname); });
                var p = pool(filas), proximas = [];
                for (var i = 0; i < p.n; i++) { var set = {}; cercanas(p,i,30).forEach(function(v) { set[v.i]=true; }); proximas[i]=set; }
                // Pares completos: el límite nearest300/600 no puede ocultar una colisión en radio.
                for (var a = 0; a < p.n; a++) for (var b = a+1; b < p.n; b++) {
                    if (!compatible(filas[a],filas[b])) continue;
                    var d = distancia(p,a,b), evidencia = filas[a]._canal_radio && filas[b]._canal_radio ? 'canal coincidente' : 'frecuencia RF no verificada (carrier/banda)';
                    if (d <= opciones.radioReuso && p.pci[a] === p.pci[b]) agregar('PCI_COLLISION',p,a,b,d,p.pci[a],'PCI idéntico dentro del radio de reuso; '+evidencia+'. Requiere revisar cobertura y configuración.');
                    if (p.pci[a] !== p.pci[b] && p.pss[a] === p.pss[b] && (proximas[a][b] || proximas[b][a]) && visible(p,a,b,d)) agregar('PCI_MOD3',p,a,b,d,p.pss[a],'PSS igual, PCI distinto y visibilidad direccional mutua del modelo; par entre las 30 cercanas de al menos una celda; '+evidencia+'. No acredita confusión de vecindades.');
                    var rsiA=api.TABLA_RSI[p.pci[a]], rsiB=api.TABLA_RSI[p.pci[b]];
                    if (d <= opciones.radioReuso && rsiA !== undefined && rsiA === rsiB) agregar('RSI_COLLISION',p,a,b,d,rsiA,'Coincidencia del lookup TABLA_RSI derivado del PCI; RSI/PRACH configurado no consultado; '+evidencia+'.');
                }
            });
            var alcanzados = tipos.filter(function(t) { return !!capped[t]; });
            return {conflicts:salida,capped:alcanzados.length>0,cappedTipos:alcanzados};
        }
    };
})(PCIPlanner);
