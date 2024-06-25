package org.efford.quakes

import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class MapPane(private val data: QuakeDataset): Pane() {
    init { paintMap() }

    private fun paintMap() {
        with(children) {
            clear()
            add(ImageView(Image("map-base.png")))
        }

        for (quake in data) {
            val x = 3.0 * (quake.longitude + 180.0)
            val y = 3.0 * (90.0 - quake.latitude)
            val point = Rectangle(x-2.0, y-2.0, 5.0, 5.0).apply {
                strokeWidth = 0.0
                fill = Color.RED
            }

            val text = with(quake) {
                String.format("M%.1f, %.1f km (%.4f\u00b0, %.4f\u00b0)",
                    magnitude, depth, longitude, latitude)
            }

            val tip = Tooltip(text)
            Tooltip.install(point, tip)
            children.add(point)
        }
    }
}