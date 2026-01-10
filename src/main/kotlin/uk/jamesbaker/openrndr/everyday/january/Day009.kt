package uk.jamesbaker.openrndr.everyday.january

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.svg.saveToFile
import org.openrndr.shape.Circle
import org.openrndr.shape.LineSegment
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(20260109)

        val steps = setOf(4, 20, 40, 60, 80, 100, 120, 140, 180)

        val lineSegments = generateSequence { random.nextDouble(from = 0.0, until = (width / 2.0)) }
            .take(12)
            .sorted()
            .map { Circle(center = drawer.bounds.center, radius = it) }
            .windowed(size = 2, step = 2) { (a, b) ->
                val pointCount = steps.random(random)
                a.contour.equidistantPositions(pointCount).zip(b.contour.equidistantPositions(pointCount), ::LineSegment)
            }.flatten()
            .toList()

        extend {
            design.clear()
            design.draw {
                group {
                    fill = ColorRGBa.BLACK
                    stroke = null
                    rectangle(0.0, 0.0, width.toDouble(), height.toDouble())
                }
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    lineSegments(lineSegments)
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
