package uk.jamesbaker.openrndr.everyday.january

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.primitives.grid
import org.openrndr.extra.svg.saveToFile
import org.openrndr.shape.Circle
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(104121)

        val shrink = 0.5
        val triples = drawer.bounds
            .grid(columns = 2, rows = 2)
            .asSequence()
            .flatten()
            .flatMap {
                if (random.nextBoolean()) {
                    it
                        .grid(columns = 2, rows = 2)
                        .flatten()
                        .asSequence()
                        .flatMap {
                            if (random.nextBoolean()) {
                                it
                                    .grid(columns = 2, rows = 2)
                                    .flatten()
                                    .asSequence()
                                    .flatMap {
                                        if (random.nextBoolean()) {
                                            it
                                                .grid(columns = 2, rows = 2)
                                                .flatten()
                                                .asSequence()
                                                .flatMap {
                                                    if (random.nextBoolean()) {
                                                        it.grid(columns = 2, rows = 2).flatten().asSequence()
                                                    } else {
                                                        sequenceOf(it)
                                                    }
                                                }
                                        } else {
                                            sequenceOf(it)
                                        }
                                    }
                            } else {
                                sequenceOf(it)
                            }
                        }
                } else {
                    sequenceOf(it)
                }
            }.map { it.copy(width = it.width - shrink, height = it.height - shrink) }
            .map { Triple(it, Circle(center = it.center, radius = it.width / 2.0), random.nextBoolean()) }
            .toList()

        extend {
            design.clear()
            design.draw {
                group {
                    stroke = null
                    triples.forEach { (r, _, b) ->
                        fill = if (b) {
                            ColorRGBa.WHITE
                        } else {
                            ColorRGBa.BLACK
                        }
                        rectangle(r)
                    }
                    triples.forEach { (_, c, b) ->
                        fill = if (b) {
                            ColorRGBa.BLACK
                        } else {
                            ColorRGBa.WHITE
                        }
                        circle(c)
                    }
                }
            }
            drawer.composition(design)
        }
        // Show a save dialog when pressing the `s` key, then save the design
        // with the chosen file name into the selected folder.
        keyboard.keyDown.listen {
            if (it.name == "s") {
                saveFileDialog(supportedExtensions = listOf("SVG" to listOf("svg"))) { file ->
                    design.saveToFile(file)
                }
            }
        }
    }
}
