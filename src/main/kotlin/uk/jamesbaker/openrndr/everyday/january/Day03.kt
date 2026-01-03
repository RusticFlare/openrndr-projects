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
        val design = drawComposition {}

        data class SineWave(
            val freq: Double,
            val amp: Double,
        ) {
            fun sin(x: Double) = amp * kotlin.math.sin(x * freq)

            fun cos(x: Double) = amp * kotlin.math.cos(x * freq)
        }

        extend(Screenshots())

        val random = Random(9988937307788)

        val sineWaves = List(2) {
            SineWave(
                freq = random.nextDouble(3.0, 60.0) * 0.61,
                amp = random.nextDouble(50.0, 150.0),
            )
        }
        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

            val pointsAcross = (0..height).map { x ->
                Vector2(
                    x = width * 0.5 + sineWaves.sumOf { it.sin(x * 0.01) },
                    y = x.toDouble(),
                )
            }

            val pointsDown = (0..height).map { y ->
                Vector2(
                    x = width * 0.5 + sineWaves.sumOf { it.cos(y * 0.01) },
                    y = y.toDouble(),
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
                    lineStrip(pointsAcross)
                    lineStrip(pointsDown)
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
