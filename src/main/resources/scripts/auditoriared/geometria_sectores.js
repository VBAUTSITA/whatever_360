// Geometria de cada sector tal como la usa el motor en el paso 6 (azimuth efectivo de los
// sub-beams MM, ancho de haz por TXRX y radio por TA o, sin TA, promedio de 30 vecinas).
// Solo usa funciones que el motor exporta: si el motor cambia esas reglas, el mapa las sigue.
var AuditoriaGeometria = {
    calcular: function (porGrupo, nombres) {
        var buscados = {};
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
    }
};
