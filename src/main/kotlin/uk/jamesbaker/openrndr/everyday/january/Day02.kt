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
import kotlin.math.sin
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        val design = drawComposition {}

        data class SineWave(
            val freq: Double,
            val amp: Double,
        ) {
            fun value(x: Double) = amp * sin(x * freq)
        }

        extend(Screenshots())

        val random = Random(15)

        val sineWaves = List(2) {
            SineWave(
                freq = random.nextDouble(3.0, 60.0) * 1.177,
                amp = random.nextDouble(50.0, 150.0),
            )
        }
        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

            val points = (0..width).map { x ->
                Vector2(
                    x = x.toDouble(),
                    y = height * 0.5 + sineWaves.sumOf { it.value(x * 0.01) },
                )
            }

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
                    lineStrip(points)
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
