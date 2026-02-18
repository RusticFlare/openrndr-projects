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
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Rectangle
import org.openrndr.shape.difference
import org.openrndr.shape.intersection

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val circleCount = 18
        val radiusStep = drawer.bounds.width / 2 / circleCount

        val center = drawer.bounds.center
        val circles = List(circleCount - 6) {
            Circle(
                center = center,
                radius = radiusStep * (it + 1),
            )
        }

        val shapes = circles.zipWithNext { inner, outer ->
            Rectangle(
                corner = center - Vector2(x = inner.radius, y = 0.0),
                width = inner.radius * 2,
                height = outer.radius,
            ).shape.difference(inner.shape).intersection(outer.shape)
        } + Rectangle(
            corner = center - Vector2(x = circles.last().radius, y = 0.0),
            width = circles.last().radius * 2,
            height = drawer.bounds.height,
        ).shape.difference(circles.last().shape)

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    circles(circles)
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
