package org.efford.quakes

import kotlinx.datetime.Instant

/**
 * Simple representation of an earthquake (or similar seismic event).
 *
 * @property time The time of the event
 * @property longitude Longitude of event epicentre (degrees)
 * @property latitude Latitude of event epicentre (degrees)
 * @property depth Depth below surface (km)
 * @property magnitude Magnitude of the event
 *
 * @author Nick Efford
 */
data class Quake(
    val time: Instant,
    val longitude: Double,
    val latitude: Double,
    val depth: Double,
    val magnitude: Double
) {
    init {
        require(longitude in -180.0..180.0) { "Invalid longitude" }
        require(latitude in -90.0..90.0) { "Invalid latitude" }
        require(depth >= 0.0) { "Invalid depth" }
        require(magnitude > 0.0) { "Invalid magnitude" }
    }
}
