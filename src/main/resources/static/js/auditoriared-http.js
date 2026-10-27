(function (root) {
    'use strict';
    function headers(json) {
        var result = { Accept: 'application/json' };
        if (json) result['Content-Type'] = 'application/json';
        var token = document.querySelector('meta[name="_csrf"]');
        var name = document.querySelector('meta[name="_csrf_header"]');
        if (token && name && token.content && name.content) result[name.content] = token.content;
        return result;
    }
    function read(response) {
        if (response.redirected || !/application\/json/i.test(response.headers.get('Content-Type') || '')) {
            throw new Error('La sesión pudo haber expirado. Inicie sesión y vuelva a consultar.');
        }
        return response.json().then(function (data) {
            if (!response.ok) throw new Error(data.error || 'No se pudo completar la consulta (' + response.status + ').');
            return data;
        });
    }
    function csv(value) {
        var text = value === null || value === undefined ? '' : String(value);
        if (/^[\s]*[=+\-@]/.test(text)) text = "'" + text;
        return '"' + text.replace(/"/g, '""') + '"';
    }
    root.AuditoriaHttp = { headers: headers, read: read, csv: csv };
}(typeof window !== 'undefined' ? window : this));
