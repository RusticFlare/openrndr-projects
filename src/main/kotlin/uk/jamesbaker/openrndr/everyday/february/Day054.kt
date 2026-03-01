package uk.jamesbaker.openrndr.everyday.february

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

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val steps = 0b110010
        val innerRadius = (drawer.bounds.width / steps) / 2
        val stepSize = ((drawer.bounds.width / 2) - innerRadius) / steps

        val circles = generateSequence(innerRadius + stepSize) { it + stepSize }
            .take(steps)
            .map { Circle(center = drawer.bounds.center, radius = it).contour }
            .toList()

        val reversedCircles = circles.reversed()

        val positionStep = 1.0 / steps / 2

        val lineSegments = reversedCircles.mapIndexed { index, circle ->
            val p0 = (1.25 + (positionStep * index)) % 1.0
            val p1 = (p0 + 0.5) % 1.0
            LineSegment(circle.position(p0), circle.position(p1))
        }

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

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
