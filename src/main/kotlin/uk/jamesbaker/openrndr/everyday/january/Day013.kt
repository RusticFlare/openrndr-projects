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
import org.openrndr.shape.Shape
import org.openrndr.shape.intersections
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
//        val random = Random(2328668)

        fun randomPoint() = Vector2(
            x = random.nextInt(from = 0, until = width).toDouble(),
            y = random.nextInt(from = 0, until = height).toDouble(),
        )

        fun Shape.overlaps(other: Shape): Boolean = contours.any { ca ->
            other.contours.any { cb ->
                ca.intersections(cb).isNotEmpty()
            }
        }

        val circles = mutableListOf<Circle>()

        repeat(26) {
            val center = generateSequence { randomPoint() }.first { point -> circles.none { it.contains(point) } }
            val cs = generateSequence(10.0) { it + 10 }
                .map { Circle(center = center, radius = it) }
                .takeWhile { circle -> circles.none { it.shape.overlaps(circle.shape) } && !circle.shape.overlaps(drawer.bounds.shape) }
                .toList()
            circles.addAll(cs)
        }

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
