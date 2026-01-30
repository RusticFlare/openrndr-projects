package uk.jamesbaker.openrndr.everyday.january

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
import org.openrndr.shape.Circle
import org.openrndr.shape.intersection

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val grid = 69
        val smallShapes = drawer.bounds
            .grid(columns = grid, rows = grid)
            .flatten()
            .map { Circle(center = it.center, radius = it.width / 3) }
            .map { it.shape }

        val bigShape = Circle(center = drawer.bounds.center, radius = drawer.bounds.width / 3).shape

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = ColorRGBa.WHITE
                    stroke = null
                    smallShapes.forEach { miniShape ->
                        shape(miniShape.intersection(bigShape))
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
