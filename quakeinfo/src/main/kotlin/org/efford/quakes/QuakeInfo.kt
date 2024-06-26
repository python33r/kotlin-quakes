package org.efford.quakes

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice

/**
 * Program to extract information from a USGS earthquake data feed.
 *
 * @author Nick Efford
 */
class QuakeInfo: CliktCommand(
    name="quakeinfo",
    help="Extracts information from a USGS earthquake data feed."
) {
    val summary by option("-s", "--summary", help="Display summary statistics").flag()
    val table by option("-t", "--table", help="Display table of quake details").flag()
    val ordering by option("-o", "--order", help="Sort order for quake details")
        .choice("+depth", "-depth", "+mag", "-mag")

    val level by argument("level", help="Severity level ${QuakeFeed.validLevels}")
        .choice(*QuakeFeed.validLevels.toTypedArray())
    val period by argument("period", help="Time period ${QuakeFeed.validPeriods}")
        .choice(*QuakeFeed.validPeriods.toTypedArray())

    override fun run() {
        val feed = QuakeFeed(level, period)
        val data = QuakeDataset().apply { updateFrom(feed) }
        if (summary) summarize(data)
        if (table) displayTable(data, ordering)
    }

    private fun summarize(data: QuakeDataset) {
        with(data) {
            echo()
            echo("$size quakes acquired")
            shallowestQuake?.let { echof("Shallowest was at %.2f km", it.depth) }
            deepestQuake?.let { echof("Deepest was at %.2f km", it.depth) }
            weakestQuake?.let { echof("Weakest had a magnitude of %.1f", it.magnitude) }
            strongestQuake?.let { echof("Strongest had a magnitude of %.1f", it.magnitude) }
            meanDepth?.let { echof("Mean depth = %.2f km", it) }
            meanMagnitude?.let { echof("Mean magnitude = %.1f", it) }
        }
    }

    private fun displayTable(data: QuakeDataset, ordering: String?) {
        echo()
        echo(when(ordering) {
            "+depth" -> data.asTable(compareBy { it.depth })
            "-depth" -> data.asTable(compareByDescending { it.depth })
            "+mag" -> data.asTable(compareBy { it.magnitude })
            "-mag" -> data.asTable(compareByDescending { it.magnitude })
            else -> data.asTable()
        })
    }

    private fun echof(format: String, vararg args: Any) {
        echo(String.format(format, *args))
    }
}
