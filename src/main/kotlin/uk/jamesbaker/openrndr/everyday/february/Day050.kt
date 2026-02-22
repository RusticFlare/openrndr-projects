package uk.jamesbaker.openrndr.everyday.february

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.primitives.tangents
import org.openrndr.extra.svg.saveToFile
import org.openrndr.shape.Circle
import org.openrndr.shape.Shape
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.difference
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(0b110000)

        val circleCount = 0b110000

        val radiusStep = drawer.bounds.dimensions.length / 2 / circleCount

        val circles = List(circleCount) {
            Circle(
                center = drawer.bounds.center,
                radius = (it + 1) * radiusStep,
            )
        }

        val shapes = circles
            .zipWithNext { inner, outer ->
                val position = random.nextDouble()
                listOf(
                    outer.contour.position(position),
                    outer.contour.position((position + 0.25) % 1.0),
                    outer.contour.position((position + 0.5) % 1.0),
                    outer.contour.position((position + 0.75) % 1.0),
                ).map { outerPoint ->
                    Shape(
                        listOf(
                            ShapeContour.fromPoints(
                                points = inner.tangents(outerPoint).toList() + outerPoint,
                                closed = true,
                            ),
                        ),
                    ).difference(inner.shape)
                }
            }.flatten()

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    circles(circles)
                    fill = ColorRGBa.WHITE
                    stroke = null
                    shapes(shapes)
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
