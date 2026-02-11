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
        fun <T> List<List<T>>.transpose(): List<List<T>> {
            if (isEmpty()) return emptyList()
            val innerSize = first().size
            check(all { it.size == innerSize }) { "All elements must have the same size." }
            val result = MutableList(innerSize) { mutableListOf<T>() }
            forEach { innerList ->
                innerList.forEachIndexed { innerIndex, t ->
                    result[innerIndex].add(t)
                }
            }
            return result.map { it.toList() }
        }

        extend(Screenshots())
        val design = drawComposition {}

        val maxRadius = drawer.bounds.dimensions.length / 2
        val circleCount = 12
        val radiusStep = maxRadius / circleCount
        val scaleFactor = 12

        val pointsOnCircles = List(circleCount) {
            Circle(
                center = drawer.bounds.center,
                radius = radiusStep * (it + 1),
            ).contour.equidistantPositions(pointCount = scaleFactor * (it + 1))
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
