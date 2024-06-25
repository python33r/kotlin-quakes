package org.efford.quakes

import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

class QuakeMap: Application() {
    override fun start(stage: Stage) {
        val level = parameters.unnamed[0]
        val period = parameters.unnamed[1]

        val feed = QuakeFeed(level, period)
        val data = QuakeDataset().apply { updateFrom(feed) }

        val pane = BorderPane().apply {
            padding = Insets(5.0, 5.0, 5.0, 5.0)
            center = MapPane(data)
            bottom = Label("level=$level, period=$period")
        }

        with(stage) {
            title = "Quake Map"
            scene = Scene(pane)
            show()
        }
    }
}
