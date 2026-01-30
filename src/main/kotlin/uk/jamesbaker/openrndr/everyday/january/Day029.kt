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
import org.openrndr.shape.Circle

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val radius = drawer.bounds.width * 8 / 19
        val steps = 6
        val stepSize = radius / steps
        val circlesToBorderWidth = generateSequence(
            Circle(
                center = drawer.bounds.center,
                radius = radius,
            ) to 1.0,
        ) { (circle, borderWidth) -> circle.copy(radius = circle.radius - stepSize) to (borderWidth * 2.25) }
            .take(steps)
            .toList()

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = ColorRGBa.BLACK
                    stroke = ColorRGBa.WHITE
                    circlesToBorderWidth.forEach { (circle, borderWidth) ->
                        strokeWeight = borderWidth
                        circle(circle)
                        strokeWeight = borderWidth / 2
                        lineLoop(circle.contour.equidistantPositions(8))
                    }
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
