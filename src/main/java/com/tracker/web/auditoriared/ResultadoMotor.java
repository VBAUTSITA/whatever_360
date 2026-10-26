package com.tracker.web.auditoriared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ResultadoMotor {
    private final List<ConflictoMotor> conflictos;
    private final boolean capped;
    private final List<String> cappedTipos;

    public ResultadoMotor(List<ConflictoMotor> conflictos, boolean capped, List<String> cappedTipos) {
        this.conflictos = Collections.unmodifiableList(new ArrayList<>(conflictos));
        this.capped = capped;
        this.cappedTipos = Collections.unmodifiableList(new ArrayList<>(cappedTipos));
    }

    public List<ConflictoMotor> getConflictos() { return conflictos; }
    public boolean isCapped() { return capped; }
    public List<String> getCappedTipos() { return cappedTipos; }
}
