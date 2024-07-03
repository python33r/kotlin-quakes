package org.efford.quakes

import java.nio.file.Files

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.path

import com.github.ajalt.mordant.rendering.TextAlign.CENTER
import com.github.ajalt.mordant.rendering.TextAlign.RIGHT
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.table.Borders.ALL
import com.github.ajalt.mordant.table.table

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
    val details by option("-d", "--details", help="Display quake details as a table").flag()
    val ordering by option("-o", "--order", help="Sort order for quake details")
        .choice("+depth", "-depth", "+mag", "-mag")
    val plain by option("-p", "--plain", help="Use plainer format for quake details").flag()
    val file by option("-f", "--file", metavar="path", help="Output file for feed data").path()

    val level by argument("level", help="Severity level ${QuakeFeed.validLevels}")
        .choice(*QuakeFeed.validLevels.toTypedArray())
    val period by argument("period", help="Time period ${QuakeFeed.validPeriods}")
        .choice(*QuakeFeed.validPeriods.toTypedArray())

    override fun run() {
        val feed = QuakeFeed(level, period)
        val data = QuakeDataset().apply { update(feed) }

        if (summary) summarize(data)

        when {
            details && plain -> displayPlainDetails(data, ordering)
            details -> displayDetails(data, ordering)
        }

        file?.let { Files.write(it, csvLines(data)) }
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

    private fun echof(format: String, vararg args: Any) {
        echo(String.format(format, *args))
    }

    private fun displayDetails(data: QuakeDataset, ordering: String?) {
        val comparison = when(ordering) {
            "+depth" -> compareBy<Quake> { it.depth }
            "-depth" -> compareByDescending<Quake> { it.depth }
            "+mag" -> compareBy<Quake> { it.magnitude }
            "-mag" -> compareByDescending<Quake> { it.magnitude }
            else -> compareBy<Quake> { it.time }
        }

        val details = table {
            tableBorders = ALL
            align = RIGHT
            header {
                align = CENTER
                style = TextStyle(bold = true)
                row("Lon", "Lat", "Depth", "Mag")
            }
            body {
                cellBorders = ALL
                data.toList().sortedWith(comparison).forEach {
                    row(
                        String.format("%.4f", it.longitude),
                        String.format("%.4f", it.latitude),
                        String.format("%.2f", it.depth),
                        String.format("%.1f", it.magnitude)
                    )
                }
            }
        }
    
        echo()
        echo(details)
    }

    private fun displayPlainDetails(data: QuakeDataset, ordering: String?) {
        echo()
        echo(when(ordering) {
            "+depth" -> data.asTable(compareBy { it.depth })
            "-depth" -> data.asTable(compareByDescending { it.depth })
            "+mag" -> data.asTable(compareBy { it.magnitude })
            "-mag" -> data.asTable(compareByDescending { it.magnitude })
            else -> data.asTable()
        })
    }

    private fun csvLines(data: QuakeDataset) = buildList<String> {
        add("time,longitude,latitude,depth,magnitude")
        for (quake in data) {
            with (quake) {
                add(String.format("%s,%.4f,%.4f,%.2f,%.1f",
                    time, longitude, latitude, depth, magnitude))
            }
        }
    }
}
