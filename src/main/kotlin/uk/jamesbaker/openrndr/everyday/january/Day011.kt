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
        val circleCount = 12

        fun circlesFrom(radius: Double): List<Circle> {
            val circle = Circle(center = drawer.bounds.center, radius = radius)
            val centers = circle.contour.equidistantPositions(circleCount)
            val littleRadius = centers.zipWithNext { a, b -> (a - b).length / 2.0 }.first()
            return centers.map { Circle(center = it, radius = littleRadius) }
        }

        val bigRadius = width / 2.8
        val circlesList = generateSequence(bigRadius to circlesFrom(radius = bigRadius)) { (prevBigRadius, prevCircles) ->
            val bigRadius = prevBigRadius - prevCircles.first().radius
            bigRadius to circlesFrom(radius = bigRadius)
        }.map { it.second }.take(15).toList().reversed()

        extend {
            design.clear()
            design.draw {
                group {
                    fill = ColorRGBa.BLACK
                    stroke = null
                    rectangle(0.0, 0.0, width.toDouble(), height.toDouble())
                }
                group {
                    fill = ColorRGBa.WHITE
                    stroke = ColorRGBa.WHITE
                    circlesList.forEach {
                        fill = if (fill == ColorRGBa.BLACK) ColorRGBa.WHITE else ColorRGBa.BLACK
                        circles(it)
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
