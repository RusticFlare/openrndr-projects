package uk.jamesbaker.openrndr.everyday.february

import org.openrndr.application
import org.openrndr.draw.loadImage
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.primitives.grid
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
        val random = Random(0b100100)

        val checkerboardSize = 0b100100

        val image0 = loadImage("data/images/doncoombez-hmATM6SXDz0-unsplash.jpg")
        val image0SquareWidth = min(image0.bounds.width, image0.bounds.height)
        val image0Offset = Vector2(x = (image0.bounds.width - image0SquareWidth) / 2, y = 0.0)
        val image0Square = Rectangle(corner = image0Offset, width = image0SquareWidth, height = image0SquareWidth)
        val grid0 = image0Square.grid(columns = checkerboardSize, rows = checkerboardSize).flatten()

        val image1 = loadImage("data/images/steve-busch-PjiRvYo-uFw-unsplash.jpg")
        val image1SquareWidth = min(image1.bounds.width, image1.bounds.height)
        val image1Offset = Vector2(x = (image1.bounds.width - image1SquareWidth) / 2, y = 0.0)
        val image1Square = Rectangle(corner = image1Offset, width = image1SquareWidth, height = image1SquareWidth)
        val grid1 = image1Square.grid(columns = checkerboardSize, rows = checkerboardSize).flatten()

        val parts = grid0.zip(grid1) { a, b ->
            if (random.nextBoolean()) {
                image0 to a
            } else {
                image1 to b
            }
        }

        val boundsGrid = drawer.bounds.grid(columns = checkerboardSize, rows = checkerboardSize).flatten()

        extend {
            parts.zip(boundsGrid) { (image, source), target ->
                drawer.image(image, source, target)
            }
        }
    }
}
