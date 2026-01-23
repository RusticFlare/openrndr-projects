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
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.LineSegment
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(20260122)

        val steps = 20

        fun points(): List<Vector2> {
            val circle = Circle(
                center = drawer.bounds.center,
                radius = random.nextDouble(from = drawer.bounds.width / 6, until = drawer.bounds.width * 2 / 3),
            ).contour

            val a = random.nextDouble()
            val b = random.nextDouble()
            val p0 = min(a, b)
            val p1 = max(a, b)
            val stepSize = (p1 - p0) / steps
            return generateSequence(p0) { it + stepSize }
                .takeWhile { it <= p1 }
                .map { circle.position(it) }
                .toList()
        }

        val lineSegments = points().zip(points(), ::LineSegment)

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
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
