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
import org.openrndr.math.Polar
import kotlin.math.sin
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 500
        height = 500
    }
    oliveProgram {
        val design = drawComposition {}

        data class SineWave(
            val freq: Double,
            val amp: Double,
        ) {
            fun value(x: Double) = amp * sin(freq * x)
        }

        extend(Screenshots())

        val random = Random(15)

        val radius = width / 4.0

        val sineWaves = List(2) {
            SineWave(
                freq = random.nextDouble(1.0, 300.0).toInt().toDouble(),
                amp = random.nextDouble(radius / 25, radius / 2),
            )
        }
        extend {
            val circlePoints = generateSequence(0.0) { it + 0.05 }
                .takeWhile { it < 360 }
                .map { theta ->
                    Polar(
                        theta = theta,
                        radius = radius + sineWaves.sumOf { it.value(Math.toRadians(theta)) },
                    ).cartesian + drawer.bounds.center
                }.toList() +
                (Polar(theta = 0.0, radius = radius).cartesian + drawer.bounds.center)

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
                    lineStrip(circlePoints)
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
