package uk.jamesbaker.openrndr.everyday.february

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.primitives.tangents
import org.openrndr.extra.svg.saveToFile
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.LineSegment
import kotlin.random.Random
import kotlin.random.nextInt

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(0b101101)

        val start = drawer.bounds.center
        val middleRadius = random.nextDouble(from = drawer.bounds.width / 12, until = drawer.bounds.width / 6)

        val middle = Circle(center = start, radius = middleRadius)

        val lineSegments = generateSequence {
            Vector2(
                x = random.nextInt(0..1000).toDouble(),
                y = random.nextInt(0..1000).toDouble(),
            )
        }.filterNot { it in middle }
            .take(0b101101)
            .flatMap {
                val (a, b) = middle.tangents(it)
                listOf(LineSegment(it, a), LineSegment(b, it))
            }.toList()

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    circle(middle)
                    lineSegments(lineSegments)
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
