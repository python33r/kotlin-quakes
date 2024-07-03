package org.efford.quakes

import kotlinx.datetime.Instant

const val MIN_FIELD_COUNT = 5
const val TIME_FIELD = 0
const val LAT_FIELD = 1
const val LON_FIELD = 2
const val DEPTH_FIELD = 3
const val MAG_FIELD = 4

/**
 * Dataset acquired from a USGS Earthquake Hazards Program data feed.
 *
 * @author Nick Efford
 */
class QuakeDataset {
    private val quakes = mutableListOf<Quake>()

    /**
     * Updates this dataset, replacing its contents with new quake data.
     *
     * @param[feed] Source of the quake data
     */
    fun update(feed: QuakeFeed) {
        with(feed.read()) {
            quakes.clear()
            // Iterate over all non-blank lines, skipping header
            lineSequence().drop(1).filter { it.isNotBlank() }.forEach {
                addRecordFor(it)
            }
        }
    }

    private fun addRecordFor(line: String) {
        with(line.split(",")) {
            // Crude split on "," is good enough here
            // (none of the first five fields contain commas)
            if (size >= MIN_FIELD_COUNT) {
                val time = Instant.parse(get(TIME_FIELD))
                val lat = get(LAT_FIELD).toDouble()
                val lon = get(LON_FIELD).toDouble()
                val depth = get(DEPTH_FIELD).toDouble()
                val mag = get(MAG_FIELD).toDouble()
                quakes.add(Quake(time, lon, lat, depth, mag))
            }
        }
    }

    /**
     * Size of (i.e., number of quake events in) this dataset
     */
    val size get() = quakes.size

    /**
     * Indicates whether this dataset is currently empty.
     *
     * @return `true` if dataset is empty, otherwise `false`
     */
    fun isEmpty() = quakes.isEmpty()

    /**
     * Retrieves the quake at the given position in this dataset.
     *
     * @param[index] Position of the desired quake
     * @return Quake object at the given index
     */
    operator fun get(index: Int) = quakes[index]

    /**
     * Provides an iterator over this dataset's quakes.
     */
    operator fun iterator(): Iterator<Quake> {
        // Use asSequence() so that we avoid returning a MutableIterator
        return quakes.asSequence().iterator()
    }

    /**
     * Renders quake data as plain text in tabular form.
     *
     * A `Comparator` object can optionally be provided to specify the
     * order in which quakes are sorted in the table. The default sort is
     * by ascending event time.
     *
     * @param[ordering] Comparator specifying sort order for quakes
     * @return String containing tabulated quake data
     */
    fun asTable(ordering: Comparator<Quake> = compareBy { it.time }) = buildString {
        appendLine("+-----------+----------+--------+-----+")
        appendLine("|    Lon    |   Lat    |  Depth | Mag |")
        appendLine("+-----------+----------+--------+-----+")
        quakes.sortedWith(ordering).forEach {
            appendLine(String.format("| %9.4f | %8.4f | %6.2f | %3.1f |",
                it.longitude, it.latitude, it.depth, it.magnitude))
        }
        appendLine("+-----------+----------+--------+-----+")
    }

    /**
     * Renders quake data as an HTML table.
     *
     * An `id` attribute for the table can optionally be provided,
     * allowing for targeted styling via CSS.
     *
     * A `Comparator` object can optionally be provided to specify the
     * order in which quakes are sorted in the table. The default sort is
     * by ascending event time.
     *
     * @param[id] `id` attribute for the generated table
     * @param[ordering] Comparator specifying sort order for quakes
     * @return String containing tabulated quake data
     */
    fun asHtmlTable(
        id: String = "",
        ordering: Comparator<Quake> = compareBy { it.time }
    ) = buildString {
        appendLine(if (id.isNotBlank()) "<table id=\"$id\">" else "<table>")
        appendLine("<thead>")
        appendLine("<tr><th>Lon</th><th>Lat</th><th>Depth</th><th>Mag</th></tr>")
        appendLine("</thead>")
        appendLine("<tbody>")
        quakes.sortedWith(ordering).forEach {
            appendLine(String.format(
                "<tr><td>%.4f</td><td>%.4f</td><td>%.2f</td><td>%.1f</td></tr>",
                it.longitude, it.latitude, it.depth, it.magnitude))
        }
        appendLine("</tbody>")
        appendLine("</table>")
    }

    /**
     * Shallowest quake in this dataset
     *
     * This will be `null` if the dataset is empty.
     */
    val shallowestQuake get() = quakes.minByOrNull { it.depth }

    /**
     * Deepest quake in this dataset
     *
     * This will be `null` if the dataset is empty.
     */
    val deepestQuake get() = quakes.maxByOrNull { it.depth }

    /**
     * Weakest quake in this dataset
     *
     * This will be `null` if the dataset is empty.
     */
    val weakestQuake get() = quakes.minByOrNull { it.magnitude }

    /**
     * Strongest quake in this dataset
     *
     * This will be `null` if the dataset is empty.
     */
    val strongestQuake get() = quakes.maxByOrNull { it.magnitude }

    /**
     * Mean depth (km) of quakes in this dataset
     *
     * This will be `null` if the dataset is empty.
     */
    val meanDepth get() = if (size > 0) quakes.map { it.depth }.average() else null

    /**
     * Mean magnitude of quakes in this dataset
     *
     * This will be `null` if the dataset is empty.
     */
    val meanMagnitude get() = if (size > 0) quakes.map { it.magnitude }.average() else null
}
