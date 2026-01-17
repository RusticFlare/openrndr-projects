package uk.jamesbaker.openrndr.website

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.draw.ColorFormat
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.svg.saveToFile
import org.openrndr.shape.Circle
import java.io.File

private fun main() = application {
    configure {
        width = 1000
        height = 1000
        windowTransparent = true
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val outer = Circle(
            center = drawer.bounds.center,
            radius = (122.0 / 256.0) * 1000.0,
        )
        val inner = Circle(
            center = drawer.bounds.center,
            radius = (97.0 / 256.0) * 1000.0,
        )
        val rt = renderTarget(width, height) {
            colorBuffer(format = ColorFormat.RGBa)
        }

        extend {
            backgroundColor = ColorRGBa.TRANSPARENT
            design.clear()

            drawer.isolatedWithTarget(rt) {
                drawer.clear(ColorRGBa.TRANSPARENT)

                fill = null
                stroke = ColorRGBa.fromHex("ef5d90")
                strokeWeight = (10.0 / 256.0) * 1000.0
                circle(outer)

                fill = ColorRGBa.fromHex("ef5d90")
                stroke = null
                circle(inner)
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
            if (it.name == "p") {
                rt.colorBuffer(0).saveToFile(File("screenshots/output.png"))
            }
        }
    }
}
