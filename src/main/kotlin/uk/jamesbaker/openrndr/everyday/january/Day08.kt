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
        val random = Random(73783468)

        val stepSize = 20.0
        val up = Vector2(x = 0.0, y = -height / stepSize)
        val down = Vector2(x = 0.0, y = height / stepSize)
        val left = Vector2(x = -width / stepSize, y = 0.0)
        val right = Vector2(x = width / stepSize, y = 0.0)

        val vecs = setOf(up, down, left, right)
        val vecsMap = mapOf(
            (up to setOf(up, left, right)),
            (down to setOf(down, left, right)),
            (left to setOf(up, down, left)),
            (right to setOf(up, down, right)),
        )

        val allPoints = generateSequence {
            generateSequence(vecs.random(random)) { vecsMap.getValue(it).random(random) }
                .runningFold(drawer.bounds.center) { acc, vector2 -> acc + vector2 }
                .zipWithNext()
                .takeWhile { (a, b) -> a in drawer.bounds || b in drawer.bounds }
                .flatMap { (a, b) -> sequenceOf(a, b) }
                .toList()
        }.take(7).toList()

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
                    allPoints.forEach { lineStrip(it) }
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
