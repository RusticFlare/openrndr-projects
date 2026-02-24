package uk.jamesbaker.openrndr.everyday.february

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.primitives.grid
import org.openrndr.extra.svg.saveToFile
import org.openrndr.math.Polar
import org.openrndr.shape.LineSegment
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(0b110011)

        val grid = 0b110011

        val points = drawer.bounds
            .offsetEdges(50.0)
            .grid(columns = grid, rows = grid)
            .map { column ->
                column.map {
                    it.center + Polar(
                        theta = random.nextInt(until = 360).toDouble(),
                        radius = random.nextDouble(until = it.width / 3),
                    ).cartesian
                }
            }

        val lineSegments = points.withIndex().drop(1).dropLast(1).flatMap { (x, column) ->
            column.withIndex().drop(1).dropLast(1).flatMap { (y, v) ->
                listOf(
                    LineSegment(points[x - 1][y], v),
                    LineSegment(points[x + 1][y], v),
                    LineSegment(points[x][y - 1], v),
                    LineSegment(points[x][y + 1], v),
                )
            }
        }

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
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
