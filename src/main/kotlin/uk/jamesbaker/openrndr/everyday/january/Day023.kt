package uk.jamesbaker.openrndr.everyday.january

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.svg.saveToFile
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.LineSegment
import kotlin.math.pow
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(20260123)

        val links = 3

        val points = drawer.bounds.offsetEdges(-25.0).scatter(placementRadius = 40.0, random = random)

        val lineSegments = points
            .flatMap { point ->
                points
                    .filterNot { it == point }
                    .sortedBy(point::distanceTo)
                    .take(links)
                    .map {
                        LineSegment(
                            start = minOf(point, it, compareBy(Vector2::length)),
                            end = maxOf(point, it, compareBy(Vector2::length)),
                        )
                    }
            }.distinct()

        val circles =
            points.map { Circle(center = it, radius = lineSegments.count { (a, b) -> it == a || it == b }.toDouble().pow(2.0)) }

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    lineSegments(lineSegments)
                }
                group {
                    fill = ColorRGBa.BLACK
                    stroke = ColorRGBa.WHITE
                    circles(circles)
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
