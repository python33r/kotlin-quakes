package org.efford.quakes

import javafx.application.Application
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Error: level and period must be specified on command line")
        exitProcess(1)
    }

    Application.launch(QuakeMap::class.java, *args)
}
