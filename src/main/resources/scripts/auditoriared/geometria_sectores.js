// Usa el modelo propio de auditoría. Área m²; área/distancia m; no mide interferencia RF.
var AuditoriaGeometria = {
    calcular: function (porGrupo, nombres) {
        var buscados = Object.create(null), salida = Object.create(null);
        nombres.forEach(function (n) { buscados[String(n).trim().toUpperCase()] = true; });
        Object.keys(porGrupo).sort().forEach(function (grupo) {
            var pool = AuditoriaModelo.pool(porGrupo[grupo]);
            for (var k = 0; k < pool.n; k++) if (buscados[String(pool.nombre[k]).trim().toUpperCase()]) salida[pool.nombre[k]] = AuditoriaModelo.geometria(pool,k);
        });
        return salida;
    },
    vecinos: function (porGrupo, nombreObjetivo, limite) {
        function clave(s) { return String(s).trim().toUpperCase(); }
        function valido(n) { return typeof n === 'number' && isFinite(n); }
        var api = PCIPlanner, grupos=Object.keys(porGrupo).sort();
        for (var g=0;g<grupos.length;g++) {
            var pool=AuditoriaModelo.pool(porGrupo[grupos[g]]), objetivo=-1;
            for (var i=0;i<pool.n;i++) if(clave(pool.nombre[i])===clave(nombreObjetivo)) { objetivo=i; break; }
            if(objetivo<0) continue;
            var radioA=pool.radioEfectivo(objetivo), epsg=api.epsgUTM(pool.lat[objetivo],pool.lon[objetivo]);
            var wedgeA=valido(radioA)&&radioA>0 ? api.wedgeLatLon(pool.lat[objetivo],pool.lon[objetivo],pool.azEf[objetivo],pool.hbeam[objetivo],radioA,64):null;
            return AuditoriaModelo.cercanas(pool,objetivo,limite).map(function(v) {
                var k=v.i, radioB=pool.radioEfectivo(k), area=null, normalizada=null;
                // Co-site <=1m excluido: no se divide por una distancia artificial.
                // UTM local no se utiliza fuera de dominio o atravesando el antimeridiano.
                var utmValida=Math.abs(pool.lat[objetivo])<=80 && Math.abs(pool.lat[k])<=80 && Math.abs(pool.lon[k]-pool.lon[objetivo])<=6;
                if(wedgeA && valido(radioB)&&radioB>0 && v.d>1 && utmValida) {
                    var wedgeB=api.wedgeLatLon(pool.lat[k],pool.lon[k],pool.azEf[k],pool.hbeam[k],radioB,64);
                    var valor=api.areaInterseccionM2(wedgeA,wedgeB,epsg);
                    if(valido(valor)&&valor>=0) { area=valor;normalizada=area/v.d; }
                }
                var geo=AuditoriaModelo.geometria(pool,k), rsiA=api.TABLA_RSI[pool.pci[objetivo]],rsiB=api.TABLA_RSI[pool.pci[k]];
                return {nombre:pool.nombre[k],distanciaMetros:v.d,azimuthEfectivo:geo.az,hbeam:geo.hbeam,radioM:geo.radio,
                    fuenteRadio:geo.fuenteRadio,vecinasRespaldo:geo.vecinasRespaldo,areaInterseccionM2:area,interseccionNormalizadaM:normalizada,
                    mismoPci:pool.pci[k]===pool.pci[objetivo],mismoMod3:pool.pss[k]===pool.pss[objetivo],mismoRsi:rsiA!==undefined&&rsiA===rsiB,
                    sinTaObjetivo:!!pool.sinTa[objetivo],sinTaVecina:!!pool.sinTa[k]};
            });
        }
        throw new Error('La celda objetivo no existe en el contexto cargado');
    }
};
