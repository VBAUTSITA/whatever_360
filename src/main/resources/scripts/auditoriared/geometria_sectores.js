// Geometria de cada sector tal como la usa el motor en el paso 6 (azimuth efectivo de los
// sub-beams MM, ancho de haz por TXRX y radio por TA o, sin TA, promedio de 30 vecinas).
// Solo usa funciones que el motor exporta: si el motor cambia esas reglas, el mapa las sigue.
var AuditoriaGeometria = {
    calcular: function (porGrupo, nombres) {
        var buscados = Object.create(null);
        for (var i = 0; i < nombres.length; i++) buscados[String(nombres[i]).toUpperCase()] = true;
        var salida = {};
        for (var grupo in porGrupo) {
            if (!porGrupo.hasOwnProperty(grupo)) continue;
            var pool = new PCIPlanner.PoolCarrier(porGrupo[grupo], PCIPlanner.DEFAULTS);
            for (var k = 0; k < pool.n; k++) {
                if (!buscados[String(pool.nombre[k]).toUpperCase()]) continue;
                salida[pool.nombre[k]] = {
                    az: pool.azEf[k], hbeam: pool.hbeam[k], radio: pool.radioEfectivo(k), sinTa: pool.sinTa[k]
                };
            }
        }
        return salida;
    },

    // Uses the engine's own geometry. No RF measurements, propagation model or risk probabilities.
    vecinos: function (porGrupo, nombreObjetivo, limite) {
        function clave(s) { return String(s).trim().toUpperCase(); }
        function valido(n) { return typeof n === 'number' && isFinite(n); }
        var api = PCIPlanner, p = api.DEFAULTS;
        for (var grupo in porGrupo) {
            if (!Object.prototype.hasOwnProperty.call(porGrupo, grupo)) continue;
            var pool = new api.PoolCarrier(porGrupo[grupo], p), objetivo = -1;
            for (var i = 0; i < pool.n; i++) {
                if (clave(pool.nombre[i]) === clave(nombreObjetivo)) { objetivo = i; break; }
            }
            if (objetivo < 0) continue;
            var candidatos = [];
            for (var j = 0; j < pool.n; j++) {
                if (j === objetivo) continue;
                candidatos.push({ indice: j, distancia: api.haversine_m(pool.lat[objetivo], pool.lon[objetivo], pool.lat[j], pool.lon[j]) });
            }
            candidatos.sort(function (a, b) { return a.distancia - b.distancia || String(pool.nombre[a.indice]).localeCompare(String(pool.nombre[b.indice])); });
            candidatos = candidatos.slice(0, limite);
            var radioA = pool.radioEfectivo(objetivo), epsg = api.epsgUTM(pool.lat[objetivo], pool.lon[objetivo]);
            var wedgeA = valido(radioA) && radioA > 0 ? api.wedgeLatLon(pool.lat[objetivo], pool.lon[objetivo], pool.azEf[objetivo], pool.hbeam[objetivo], radioA, p.n_arco) : null;
            return candidatos.map(function (v) {
                var k = v.indice, radioB = pool.radioEfectivo(k), area = null, normalizada = null;
                if (wedgeA && valido(radioB) && radioB > 0) {
                    var wedgeB = api.wedgeLatLon(pool.lat[k], pool.lon[k], pool.azEf[k], pool.hbeam[k], radioB, p.n_arco);
                    var valor = api.areaInterseccionM2(wedgeA, wedgeB, epsg);
                    if (valido(valor) && valor >= 0) area = valor;
                    if (area !== null && !(v.distancia <= p.radio_cosito_m && p.politica_cosito === 'excluir')) {
                        normalizada = area / Math.max(v.distancia, p.dist_min_normalizacion_m);
                    }
                }
                var rsiA = api.TABLA_RSI[pool.pci[objetivo]], rsiB = api.TABLA_RSI[pool.pci[k]];
                return { nombre: pool.nombre[k], distanciaMetros: v.distancia,
                    azimuthEfectivo: pool.azEf[k], hbeam: pool.hbeam[k], radioM: valido(radioB) && radioB > 0 ? radioB : null,
                    areaInterseccionM2: area, interseccionNormalizadaM: normalizada,
                    mismoPci: pool.pci[k] === pool.pci[objetivo], mismoMod3: pool.pss[k] === pool.pss[objetivo],
                    mismoRsi: rsiA !== undefined && rsiA === rsiB,
                    sinTaObjetivo: !!pool.sinTa[objetivo], sinTaVecina: !!pool.sinTa[k] };
            });
        }
        throw new Error('La celda objetivo no existe en el contexto cargado');
    }
};
