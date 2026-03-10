package uk.jamesbaker.openrndr.everyday.march

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.draw.loadImage
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.primitives.grid
import org.openrndr.extra.shapes.utilities.splitAt
import org.openrndr.extra.svg.saveToFile
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import kotlin.math.min
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(0b111011)

        val image0 = loadImage("data/images/ameenfahmy-u35b7yQ28cY-unsplash.jpg")
        val image0SquareWidth = min(image0.bounds.width, image0.bounds.height)
        val image0Offset = Vector2(x = (image0.bounds.width - image0SquareWidth) / 2, y = 0.0)
        val image0Square = Rectangle(corner = image0Offset, width = image0SquareWidth, height = image0SquareWidth)

        val colorA = ColorRGBa.BLACK
        val colorB = ColorRGBa.fromHex("ffd1dc")

        val rectangleCount = 0b111011

        val shapes = drawer.bounds
            .grid(columns = rectangleCount, rows = rectangleCount)
            .flatten()
            .mapNotNull { rectangle ->
                if (random.nextBoolean()) {
                    null
                } else if (random.nextBoolean()) {
                    rectangle.shape
                } else {
                    rectangle.contour
                        .splitAt(listOf(0.5))
                        .map { it.close().shape }
                        .random(random)
                }
            }

        extend {
            drawer.clear(colorA)
            drawer.image(image0, image0Square, drawer.bounds)
            drawer.stroke = colorB

            design.clear()
            design.draw {
                group {
                    fill = colorB
                    stroke = null
                    shapes(shapes)
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
