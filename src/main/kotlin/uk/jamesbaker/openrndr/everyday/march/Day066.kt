package uk.jamesbaker.openrndr.everyday.march

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
import org.openrndr.shape.LineSegment
import org.openrndr.shape.difference
import org.openrndr.shape.intersection
import org.openrndr.shape.intersections

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val circle = Circle(center = drawer.bounds.center, radius = drawer.bounds.width / 3)
        val circleShape = circle.shape
        val circleContour = circle.contour

        val lineCount = 99

        val stepSize = drawer.bounds.width / lineCount
        val halfStepSize = stepSize / 2

        val outerLineSegments = drawer.bounds
            .grid(columns = lineCount, rows = 1)
            .flatten()
            .drop(1)
            .map { LineSegment(it.corner, it.corner.copy(y = it.height)) }

        val outerLineSegmentShapes = outerLineSegments
            .map { it.shape }
        val outerShapes = outerLineSegmentShapes
            .map { it.difference(circleShape) }

        val innerLineSegments = outerLineSegments
            .map { it.copy(start = it.start.copy(x = it.start.x + halfStepSize), end = it.end.copy(x = it.end.x + halfStepSize)) }
        val innerLineSegmentShapes = innerLineSegments
            .map { it.shape }
        val innerShapes = innerLineSegmentShapes
            .map { it.intersection(circleShape) }

        val outerIntersectionTs = outerLineSegments
            .flatMap { it.contour.intersections(circleContour) }
            .map { it.b.contourT }
        val innerIntersectionTs = innerLineSegments
            .flatMap { it.contour.intersections(circleContour) }
            .map { it.b.contourT }

        val contours = (outerIntersectionTs + innerIntersectionTs)
            .sorted()
            .windowed(2, 2) { (a, b) -> circleContour.sub(a, b) }
        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    shapes(innerShapes.filterIndexed { index, _ -> index != (lineCount / 2) - 1 })
                    shapes(outerShapes)
                    contours(contours)
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
