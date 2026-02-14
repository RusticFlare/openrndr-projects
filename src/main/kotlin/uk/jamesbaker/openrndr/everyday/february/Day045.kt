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
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(0b101100)

        data class SineWave(
            val frequency: Double,
            val amplitude: Double,
            val offset: Double,
        ) {
            fun value(x: Double) = amplitude * sin((x + offset) * frequency)
        }

        val waves = 7
        val lines = List(waves + 2) {
            SineWave(
                frequency = random.nextDouble(2.0, 8.0),
                amplitude = random.nextDouble(10.0, width.toDouble() / (waves + 1) / 2),
                offset = random.nextDouble(0.0, 2 * PI),
            )
        }.mapIndexed { index, sineWave ->
            val offset = width.toDouble() * (index - 1) / (waves - 1)
            (0..height).map { y ->
                Vector2(
                    x = offset + sineWave.value(y * 0.01) * (1.0011.pow(y) - 1.0),
                    y = y.toDouble(),
                )
            }
        }.zipWithNext { xs, ys ->
            val steps = 6
            val zss = List(steps) { index ->
                val scale = (index.toDouble() + 1) / (steps + 1)
                xs.zip(ys) { x, y ->
                    val xToY = y - x
                    x + (xToY * scale)
                }
            }
            zss.plus(element = xs).plus(element = ys)
        }.flatten()
            .distinct()

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    lines.forEach { lineStrip(it) }
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
