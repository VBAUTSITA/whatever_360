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

/**
 * FAREND candidates. findAll() is used for the nearest-site search, which
 * is fine for a catalog of hundreds/thousands of sites. If the real table
 * is huge, replace with a native query that filters by a bounding box (or
 * SDO_NN / earthdistance depending on the database) before the exact
 * Haversine comparison.
 */
@Repository
interface FarendSiteRepository extends JpaRepository<FarendSite, String> {
}

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
