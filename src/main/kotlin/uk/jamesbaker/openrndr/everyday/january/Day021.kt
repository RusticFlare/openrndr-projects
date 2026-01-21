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

        val stepSize = 5.0
        val circles = generateSequence(stepSize) { it + stepSize }
            .takeWhile { it <= drawer.bounds.dimensions.length / 2 }
            .map { Circle(center = drawer.bounds.center, radius = it) }
            .toList()

        val steps = 16

        val pointsA = circles
            .mapIndexed { index, circle -> circle.contour.position((index % steps).toDouble() / steps) }
        val pointsB = circles
            .mapIndexed { index, circle -> circle.contour.position(((index + (steps * 1 / 8)) % steps).toDouble() / steps) }
        val pointsC = circles
            .mapIndexed { index, circle -> circle.contour.position(((index + (steps * 2 / 8)) % steps).toDouble() / steps) }
        val pointsD = circles
            .mapIndexed { index, circle -> circle.contour.position(((index + (steps * 3 / 8)) % steps).toDouble() / steps) }
        val pointsE = circles
            .mapIndexed { index, circle -> circle.contour.position(((index + (steps * 4 / 8)) % steps).toDouble() / steps) }
        val pointsF = circles
            .mapIndexed { index, circle -> circle.contour.position(((index + (steps * 5 / 8)) % steps).toDouble() / steps) }
        val pointsG = circles
            .mapIndexed { index, circle -> circle.contour.position(((index + (steps * 6 / 8)) % steps).toDouble() / steps) }
        val pointsH = circles
            .mapIndexed { index, circle -> circle.contour.position(((index + (steps * 7 / 8)) % steps).toDouble() / steps) }

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    lineStrip(pointsA)
                    lineStrip(pointsB)
                    lineStrip(pointsC)
                    lineStrip(pointsD)
                    lineStrip(pointsE)
                    lineStrip(pointsF)
                    lineStrip(pointsG)
                    lineStrip(pointsH)
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
