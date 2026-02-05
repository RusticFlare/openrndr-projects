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
import org.openrndr.shape.Circle

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val pointsPerSide = 11

        val contour = Circle(center = drawer.bounds.center, radius = drawer.bounds.width / 6).contour
        val vanishingPoint1 = contour.position(0.25)
        val vanishingPoint2 = contour.position(0.75)

        val edgePairs = drawer.bounds.contour
            .equidistantPositions(pointCount = pointsPerSide * 4)
            .windowed(size = 2, step = 2)
        val lineLoops1 = edgePairs.map { it + vanishingPoint1 }
        val lineLoops2 = edgePairs.map { it + vanishingPoint2 }

        extend {
            drawer.clear(ColorRGBa.WHITE)

            design.clear()
            design.draw {
                group {
                    stroke = null
                    fill = ColorRGBa.BLACK
                    lineLoops1.forEach { lineLoop(it) }
                    lineLoops2.forEach { lineLoop(it) }
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
