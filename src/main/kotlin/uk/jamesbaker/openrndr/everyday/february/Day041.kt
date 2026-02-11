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
import org.openrndr.shape.LineSegment

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        fun <T> List<List<T>>.transpose(): List<List<T>> {
            if (isEmpty()) return emptyList()
            val innerSize = first().size
            check(all { it.size == innerSize }) { "All elements must have the same size." }
            val result = MutableList(innerSize) { mutableListOf<T>() }
            forEach { innerList ->
                innerList.forEachIndexed { innerIndex, t ->
                    result[innerIndex].add(t)
                }
            }
            return result.map { it.toList() }
        }
        extend(Screenshots())
        val design = drawComposition {}

        val changeCount = 6
        val yStep = drawer.bounds.height / (changeCount - 1)
        val lineCount = 75
        val scaleFactor = 2

        val top = LineSegment(start = Vector2.ZERO, end = Vector2(x = drawer.bounds.width, y = 0.0))

        var first = true

        val lineStrips = generateSequence(top) {
            val copy = it
                .copy(
                    start = it.start.copy(y = it.start.y + yStep),
                    end = it.end.copy(y = it.end.y + yStep),
                )
            if (first) {
                first = false
                copy
            } else {
                copy.extendTo(it.length * scaleFactor)
            }
        }.take(changeCount)
            .map { it.contour.equidistantPositions(pointCount = lineCount) }
            .toList()
            .transpose()

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    lineStrips.forEach { lineStrip(it) }
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
