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
import org.openrndr.shape.Rectangle

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val rectangle = Rectangle.fromCenter(
            center = drawer.bounds.center,
            width = width.toDouble() * 0.35,
            height = height.toDouble() * 0.85,
        )

        val corners = listOf(
            rectangle.corner,
            rectangle.corner + Vector2(x = rectangle.width, y = 0.0),
            rectangle.corner + rectangle.dimensions,
            rectangle.corner + Vector2(x = 0.0, y = rectangle.height),
        )

        val sides = generateSequence { corners }
            .flatten()
            .zipWithNext(::LineSegment)
            .take(4)
            .toList()

        val circle = Circle(
            center = drawer.bounds.center,
            radius = width.toDouble() * 0.35 / 4,
        )

        val pointCount = 40

        val circlePositions = circle.contour.equidistantPositions(pointCount = pointCount)

        val firstCirclePosition = circlePositions.first()

        val rectanglePositions = sides
            .flatMap { it.contour.equidistantPositions(pointCount = (pointCount / 4) + 1) }
            .distinct()

        val firstRectanglePosition = rectanglePositions.minBy { firstCirclePosition.distanceTo(it) }

        val shiftedRectanglePositions = generateSequence {
            rectanglePositions
        }.flatten()
            .dropWhile { it != firstRectanglePosition }
            .take(pointCount)
            .toList()

        val lineSegments = circlePositions.zip(shiftedRectanglePositions, ::LineSegment)

        extend {
            drawer.clear(ColorRGBa.BLACK)

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
                group {
                    fill = ColorRGBa.BLACK
                    stroke = null
                    rectangle(0.0, 0.0, width.toDouble(), height.toDouble() * 0.1)
                    rectangle(0.0, height.toDouble() * 0.9, width.toDouble(), height.toDouble() * 0.1)
                    rectangle(0.0, 0.0, width.toDouble() * 0.35, height.toDouble())
                    rectangle(width.toDouble() * 0.65, 0.0, width.toDouble() * 0.35, height.toDouble())
                }
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    rectangle(rectangle)
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
