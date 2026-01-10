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
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())

        val design = drawComposition {}

        val random = Random(111123511)

        val step = width / 15.0
        val radiusMax = step / 2.0
        val radiusMin = radiusMax / 20.0

        val positions = generateSequence(radiusMax) { (it + step).takeIf { it < width } }
            .flatMap { x ->
                generateSequence(radiusMax) { (it + step).takeIf { it < height } }.map { y ->
                    Vector2(x = x, y = y)
                }
            }.toList()
        val radii1 = positions.map { random.nextDouble(radiusMin, radiusMax - 1) }
        val radii2 = positions.map { random.nextDouble(radiusMin, radiusMax - 1) }
        val radii3 = positions.map { random.nextDouble(radiusMin, radiusMax - 1) }

        extend {
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
                    circles(positions = positions, radii = radii1)
                    circles(positions = positions, radii = radii2)
                    circles(positions = positions, radii = radii3)
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
