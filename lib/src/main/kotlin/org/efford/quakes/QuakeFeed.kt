package org.efford.quakes

import java.net.URI
import java.net.URL

/**
 * A USGS Earthquake Hazards Program data feed.
 *
 * The appropriate feed URL is constructed by specifying a severity
 * level ("all", "1.0", "2.5", "4.5", "significant") and a time
 * period ("hour", "day", "week", "month").
 *
 * @property level Severity level for the feed
 * @property period Time period for the feed
 *
 * @author Nick Efford
 */
class QuakeFeed(val level: String, val period: String) {

    companion object {
        /** Severity levels for which there is a USGS data feed */
        val validLevels = setOf("all", "1.0", "2.5", "4.5", "significant")

        /** Time periods for which there is a USGS data feed */
        val validPeriods = setOf("hour", "day", "week", "month")
    }

    init {
        require(level in validLevels) {
            "Invalid level: must be one of $validLevels"
        }
        require(period in validPeriods) {
            "Invalid period: must be one of $validPeriods"
        }
    }

    /**
     * Feed URL
     *
     * This points to the USGS data feed (CSV version) that corresponds
     * to the chosen `level` and `period` properties.
     */
    val source: URL = URI.create(
        "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/${level}_${period}.csv").toURL()

    /**
     * Generates a string representation of this feed.
     *
     * @return Feed details as a string
     */
    override fun toString() = """QuakeFeed(level="$level", period="$period")"""

    /**
     * Reads the entire contents of this feed.
     *
     * @return Feed data as a string
     */
    fun read() = source.readText()
}
