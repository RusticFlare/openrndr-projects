package uk.jamesbaker.openrndr.everyday.march

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.svg.saveToFile
import org.openrndr.math.Polar
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
        val random = Random(0b111110)

        val lineStrip = generateSequence(Vector2(x = 0.0, y = drawer.bounds.height / 2)) { previous ->
            if (previous.x > drawer.bounds.width) {
                return@generateSequence null
            }
            previous + Polar(
                theta = random.nextInt(from = -70, until = 71).toDouble(),
                radius = random.nextDouble(from = drawer.bounds.width / 200, until = drawer.bounds.width / 40),
            ).cartesian
        }.toList()

        val stepStep = Vector2(0.0, drawer.bounds.width / 300)

        val otherLineStrips = List(18) { index ->
            val step = stepStep * ((index + 1) * (index + 2) * 0.5)
            listOf(
                lineStrip.map { point -> point + step },
                lineStrip.map { point -> point - step },
            )
        }.flatten()

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    lineStrip(lineStrip)
                    otherLineStrips.forEach { lineStrip(it) }
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
