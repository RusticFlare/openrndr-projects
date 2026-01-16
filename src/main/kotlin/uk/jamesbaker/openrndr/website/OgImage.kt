package uk.jamesbaker.openrndr.website

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFontImageMap
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.textwriter.writer
import org.openrndr.math.Vector2

fun main() = application {
    configure {
        width = 1200
        height = 630
    }
    oliveProgram {
        extend(Screenshots())

        val scale = 0.3

        val headingFont = loadFontImageMap("fonts/BebasNeue-Regular.ttf", 88.0 / scale)
        val subheadingFont = loadFontImageMap("fonts/BebasNeue-Regular.ttf", 38.0 / scale)
        val bodyFont = loadFontImageMap("fonts/BebasNeue-Regular.ttf", 26.0 / scale)

        extend {
            drawer.clear(ColorRGBa.fromHex("222222"))

            val pink = ColorRGBa.fromHex("ef5d90")
            val offWhite = ColorRGBa.fromHex("FAFAFA")

            val circleCenter = Vector2(x = width / 2.0, y = height / 4.0)

            drawer.fill = pink
            drawer.stroke = null
            drawer.circle(position = circleCenter, radius = 97.0)

            drawer.fill = null
            drawer.stroke = pink

            drawer.strokeWeight = 10.0

            drawer.circle(position = circleCenter, radius = 122.0)

            drawer.strokeWeight = 1.0

            drawer.lineSegment(
                start = Vector2(x = width * 2 / 6.0, y = height / 2.0),
                end = Vector2(x = width * 4 / 6.0, y = height / 2.0),
            )

            drawer.lineSegment(
                start = Vector2(x = width * 3 / 8.0, y = height * 3 / 4.0),
                end = Vector2(x = width * 5 / 8.0, y = height * 3 / 4.0),
            )

            val heading = "James Baker"
            val subheading = "Images & Writing"
            val url = "jamesbaker.uk"
            val date = " "

            drawer.scale(s = scale)

            drawer.fontMap = headingFont
            drawer.fill = offWhite
            drawer.writer {
                box = drawer.bounds.div(scale)
                horizontalAlign = 0.5

                verticalAlign = 380.0 / 630.0
                text(heading)
            }

            drawer.fontMap = subheadingFont
            drawer.fill = pink
            drawer.writer {
                box = drawer.bounds.div(scale)
                horizontalAlign = 0.5

                verticalAlign = 435.0 / 630.0
                text(subheading)
            }

            drawer.fontMap = bodyFont
            drawer.fill = offWhite.copy(alpha = 0.65)
            drawer.writer {
                box = drawer.bounds.div(scale)
                horizontalAlign = 0.5

                verticalAlign = 510.0 / 630.0
                text(url)
            }

            drawer.fontMap = bodyFont
            drawer.fill = offWhite.copy(alpha = 0.65)
            drawer.writer {
                box = drawer.bounds.div(scale)
                horizontalAlign = 0.5

                verticalAlign = 545.0 / 630.0
                text(date)
            }
        }
    }
}
