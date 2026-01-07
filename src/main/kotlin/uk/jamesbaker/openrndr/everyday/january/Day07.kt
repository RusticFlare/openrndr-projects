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
        val random = Random(5432198)

        fun getPointsMid(startY: Double): List<Vector2> = generateSequence(Vector2(x = width * 0.3, y = startY)) {
            it + Polar(theta = random.nextDouble(-75.0, 75.0), radius = random.nextDouble(height / 100.0, height / 50.0)).cartesian
        }.takeWhile { it.x - (height / 20.0) <= width }
            .toList()

        val points1 = getPointsMid(height * 1 / 6.0)
        val points2 = getPointsMid(height * 2 / 6.0)
        val points3 = getPointsMid(height * 3 / 6.0)
        val points4 = getPointsMid(height * 4 / 6.0)
        val points5 = getPointsMid(height * 5 / 6.0)

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

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
                    lineStrip(points1)
                    lineStrip(points2)
                    lineStrip(points3)
                    lineStrip(points4)
                    lineStrip(points5)
                }
                group {
                    fill = ColorRGBa.BLACK
                    stroke = null
                    rectangle(0.0, 0.0, width.toDouble(), height.toDouble() * 0.1)
                    rectangle(0.0, height.toDouble() * 0.9, width.toDouble(), height.toDouble() * 0.1)
                    rectangle(0.0, 0.0, width.toDouble() * 0.35, height.toDouble())
                    rectangle(width.toDouble() * 0.65, 0.0, width.toDouble() * 0.35, height.toDouble())
                }
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    rectangle(width.toDouble() * 0.325, height.toDouble() * 0.075, width.toDouble() * 0.35, height.toDouble() * 0.85)
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
