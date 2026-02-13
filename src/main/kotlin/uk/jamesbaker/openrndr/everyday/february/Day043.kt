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
import kotlin.math.pow

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val circleCount = 10
        val points = 2.0.pow(circleCount).toInt()
        val stepsSize = 1.0 / points

        val positionLists = generateSequence(List(points) { it * stepsSize }) { previous ->
            previous.windowed(size = 2, step = 2) { (a, b) -> (a + b) / 2 }
        }.take(circleCount).toList().reversed()

        val maxRadius = drawer.bounds.dimensions.length / 2
        val radiusStep = maxRadius / circleCount

        val circles = List(circleCount) {
            Circle(
                center = drawer.bounds.center,
                radius = radiusStep * (it + 1),
            )
        }

        val pointsOnCircles = circles.mapIndexed { index, circle ->
            val contour = circle.contour
            positionLists[index].map { position -> contour.position(position) }
        }
        val innerLineSegments = pointsOnCircles.first().map { LineSegment(it, drawer.bounds.center) }
        val lineSegments = pointsOnCircles
            .zipWithNext { inner, outer -> outer.map { out -> LineSegment(out, inner.minBy { it.distanceTo(out) }) } }
            .flatten()

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
//                    circles(circles)
                    lineSegments(innerLineSegments)
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
