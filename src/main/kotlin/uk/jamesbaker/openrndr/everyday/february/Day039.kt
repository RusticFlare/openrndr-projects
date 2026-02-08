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
import org.openrndr.math.Polar
import org.openrndr.shape.Shape
import org.openrndr.shape.ShapeContour
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(0b100110)

        val mainPathCount = 10

        val maxAverageRadius = drawer.bounds.dimensions.length / 2
        val averageRadiusStep = maxAverageRadius / mainPathCount

        val radiusVariance = 10.0

        val shapes = generateSequence(maxAverageRadius) { it - averageRadiusStep }
            .take(mainPathCount + 1)
            .map { averageRadius ->
                List(360) {
                    Polar(
                        theta = it.toDouble(),
                        radius = averageRadius + random.nextDouble(from = -radiusVariance, until = radiusVariance),
                    )
                }
            }.map { lineLoop -> lineLoop.map { it.cartesian + drawer.bounds.center } }
            .zipWithNext { outer, inner -> listOf(outer, outer.zip(inner) { o, i -> (o + i) / 2.0 }, inner) }
            .flatten()
            .distinct()
            .zipWithNext { outer, inner -> listOf(outer, outer.zip(inner) { o, i -> (o + i) / 2.0 }, inner) }
            .flatten()
            .distinct()
            .map { Shape(listOf(ShapeContour.fromPoints(it, closed = true))) }
            .toList()

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    stroke = null
                    shapes.forEachIndexed { index, shape ->
                        fill = if (index % 2 == 0) ColorRGBa.WHITE else ColorRGBa.BLACK
                        shape(shape)
                    }
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
