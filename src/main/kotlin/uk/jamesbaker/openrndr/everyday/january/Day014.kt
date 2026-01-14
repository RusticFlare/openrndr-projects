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
import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import org.openrndr.shape.Shape
import org.openrndr.shape.shape
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(865674)

        fun Rectangle.randomSegment(): Shape {
            val upX = random.nextBoolean()
            val upY = random.nextBoolean()
            val center = Vector2(
                x = corner.x + if (upX) width else 0.0,
                y = corner.y + if (upY) width else 0.0,
            )
            val start = center.copy(x = center.x + if (upX) -width else width)
            val end = center.copy(y = center.y + if (upY) -width else width)
            val through = center + Polar(
                theta = if (upX && upY) {
                    225.0
                } else if (upX) {
                    135.0
                } else if (upY) {
                    315.0
                } else {
                    45.0
                },
                radius = width,
            ).cartesian
            return shape {
                boundary {
                    moveTo(center)
                    lineTo(start)
                    circularArcTo(through, end)
                    close()
                }
            }
        }

        val grid = 10

        val shrink = 0.5
        val triples = drawer.bounds
            .grid(columns = grid, rows = grid)
            .flatten()
            .map { it.copy(width = it.width - shrink, height = it.height - shrink) }
            .map { Triple(it, it.randomSegment(), random.nextBoolean()) }
            .toList()

        extend {
            design.clear()
            backgroundColor = ColorRGBa.RED
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
                    triples.forEach { (_, s, b) ->
                        fill = if (b) {
                            ColorRGBa.BLACK
                        } else {
                            ColorRGBa.WHITE
                        }
                        shape(s)
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
