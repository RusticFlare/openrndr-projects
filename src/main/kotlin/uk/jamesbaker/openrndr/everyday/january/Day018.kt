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
import org.openrndr.shape.Circle
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(20260118)

        val circles = List(250) {
            Circle(
                center = Vector2(x = random.nextDouble(until = drawer.bounds.width), y = random.nextDouble(until = drawer.bounds.height)),
                radius = random.nextDouble(from = 5.0, until = 250.0),
            )
        }.sortedByDescending { it.radius }

        extend {
            design.clear()
            design.draw {
                group {
                    fill = ColorRGBa.BLACK
                    stroke = ColorRGBa.WHITE
                    circles(circles)
                }
//                group {
//                    fill = ColorRGBa.BLACK
//                    stroke = null
//                    rectangle(0.0, 0.0, width.toDouble(), height.toDouble() * 0.1)
//                    rectangle(0.0, height.toDouble() * 0.9, width.toDouble(), height.toDouble() * 0.1)
//                    rectangle(0.0, 0.0, width.toDouble() * 0.35, height.toDouble())
//                    rectangle(width.toDouble() * 0.65, 0.0, width.toDouble() * 0.35, height.toDouble())
//                }
//                group {
//                    fill = null
//                    stroke = ColorRGBa.WHITE
//                    rectangle(width.toDouble() * 0.325, height.toDouble() * 0.075, width.toDouble() * 0.35, height.toDouble() * 0.85)
//                }
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
