package com.tracker.web.valorizacion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * All repositories of the valorizacion module, grouped in one file — the
 * same convention the datafillbook module uses (DF4GRepositories).
 * Package-private interfaces are fine: Spring Data proxies them anyway and
 * nothing outside this package needs them.
 */

/** Read-only lookup on the PAP black-box view. */
@Repository
interface PapValorizacionRepository extends JpaRepository<PapValorizacion, Long> {
}

// FAREND lookup is NOT a JpaRepository: it reads tm_conf_actual_celda_4g
// through native SQL with a bounding-box pre-filter — see FarendTmConfDao,
// which mirrors the recursos module's RedExistenteServiceImpl pattern.

@Repository
interface FormValorizacionIndoorRepository
        extends JpaRepository<FormValorizacionIndoor, Long> {
    List<FormValorizacionIndoor> findByIdValorizacionOrderByFechaCreacionDesc(Long idValorizacion);
}

@Repository
interface FormValorizacionOutdoorRepository
        extends JpaRepository<FormValorizacionOutdoor, Long> {
    List<FormValorizacionOutdoor> findByIdValorizacionOrderByFechaCreacionDesc(Long idValorizacion);
}

@Repository
interface FormValorizacionOutdoorPlusIndoorRepository
        extends JpaRepository<FormValorizacionOutdoorPlusIndoor, Long> {
    List<FormValorizacionOutdoorPlusIndoor> findByIdValorizacionOrderByFechaCreacionDesc(Long idValorizacion);
}
