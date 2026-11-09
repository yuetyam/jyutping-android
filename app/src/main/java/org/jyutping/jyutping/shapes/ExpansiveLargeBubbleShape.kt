package org.jyutping.jyutping.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import org.jyutping.jyutping.models.KeySide
import org.jyutping.jyutping.presets.PresetConstant

/**
 * An expansive bubble shape with straight sides and large corners, for the wider alternatives of the nine-key layout.
 *
 * The shape fills the whole preview area: (expansionCount + 1) key widths wide, and two key heights plus a small gap high.
 * The key itself occupies the bottom-left (Left side) or bottom-right (Right side) block.
 *
 * @param side Key location. Left expands rightward; Right expands leftward.
 * @param expansionCount Count of the extra blocks.
 */
class ExpansiveLargeBubbleShape(
        private val side: KeySide,
        private val expansionCount: Int
) : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
                val path: Path = when (side) {
                        KeySide.Left -> leftBasedPath(size, density)
                        KeySide.Right -> rightBasedPath(size, density)
                }
                return Outline.Generic(path)
        }

        //    E---------------+---------------+------------a---+
        //    +   |           +               +            |   +
        //    D...+           +               +            +...+
        //    +               +               +                +
        //    +               +               +                +
        //    +               +               +            d...c
        //    +               +               +            |   +
        //    +               K---------------+----------------+
        //    +               +
        //    +               +
        //    +               +
        //    B...+       M...L
        //    +   |       |   +
        //    +---A---o-------+

        private fun leftBasedPath(size: Size, density: Density): Path {
                val width = size.width
                val height = size.height
                val extraHeight: Float = 4F * density.density
                val baseWidth: Float = width / (expansionCount + 1F)
                val baseHeight: Float = (height - extraHeight) / 2F
                val cornerRadius: Float = PresetConstant.largeKeyCornerRadius * density.density
                val cornerSize = Size(cornerRadius * 2F, cornerRadius * 2F)

                val pointO = Offset(x = baseWidth / 2F, y = height)
                val pointA = Offset(x = cornerRadius, y = height)
                val pointB = Offset(x = 0F, y = height - cornerSize.height)
                val pointD = Offset(x = 0F, y = cornerRadius)
                val pointE = Offset(x = 0F, y = 0F)
                val pointExtA = Offset(x = width - cornerSize.width, y = 0F)
                val pointExtC = Offset(x = width, y = baseHeight - cornerRadius)
                val pointExtD = Offset(x = width - cornerSize.width, y = baseHeight - cornerSize.height)
                val pointK = Offset(x = baseWidth, y = baseHeight)
                val pointL = Offset(x = baseWidth, y = height - cornerRadius)
                val pointM = Offset(x = baseWidth - cornerSize.width, y = height - cornerSize.height)
                return Path().apply {
                        moveTo(pointO.x, pointO.y)
                        lineTo(pointA.x, pointA.y)
                        arcTo(
                                rect = Rect(offset = pointB, size = cornerSize),
                                startAngleDegrees = 90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointD.x, pointD.y)
                        arcTo(
                                rect = Rect(offset = pointE, size = cornerSize),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointExtA.x + cornerRadius, pointExtA.y)
                        arcTo(
                                rect = Rect(offset = pointExtA, size = cornerSize),
                                startAngleDegrees = -90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointExtC.x, pointExtC.y)
                        arcTo(
                                rect = Rect(offset = pointExtD, size = cornerSize),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointK.x, pointK.y)
                        lineTo(pointL.x, pointL.y)
                        arcTo(
                                rect = Rect(offset = pointM, size = cornerSize),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        close()
                }
        }

        //    d---------------+---------------+-----------G---+
        //    +   |           +               +           |   +
        //    c...+           +               +           +...+
        //    +               +               +               +
        //    +               +               +               +
        //    b...+           +               +               +
        //    +   |           +               +               +
        //    +---a-----------+---------------C               +
        //                                    +               +
        //                                    +               +
        //                                    +               +
        //                                    B...+       M...L
        //                                    +   |       |   +
        //                                    +---A---o-------+

        private fun rightBasedPath(size: Size, density: Density): Path {
                val width = size.width
                val height = size.height
                val extraHeight: Float = 4F * density.density
                val baseWidth: Float = width / (expansionCount + 1F)
                val baseHeight: Float = (height - extraHeight) / 2F
                val cornerRadius: Float = PresetConstant.largeKeyCornerRadius * density.density
                val cornerSize = Size(cornerRadius * 2F, cornerRadius * 2F)

                val pointO = Offset(x = width - baseWidth / 2F, y = height)
                val pointA = Offset(x = width - baseWidth + cornerRadius, y = height)
                val pointB = Offset(x = width - baseWidth, y = height - cornerSize.height)
                val pointC = Offset(x = width - baseWidth, y = baseHeight)
                val pointExtA = Offset(x = cornerRadius, y = baseHeight)
                val pointExtB = Offset(x = 0F, y = baseHeight - cornerSize.height)
                val pointExtC = Offset(x = 0F, y = cornerRadius)
                val pointExtD = Offset(x = 0F, y = 0F)
                val pointG = Offset(x = width - cornerSize.width, y = 0F)
                val pointL = Offset(x = width, y = height - cornerRadius)
                val pointM = Offset(x = width - cornerSize.width, y = height - cornerSize.height)
                return Path().apply {
                        moveTo(pointO.x, pointO.y)
                        lineTo(pointA.x, pointA.y)
                        arcTo(
                                rect = Rect(offset = pointB, size = cornerSize),
                                startAngleDegrees = 90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointC.x, pointC.y)
                        lineTo(pointExtA.x, pointExtA.y)
                        arcTo(
                                rect = Rect(offset = pointExtB, size = cornerSize),
                                startAngleDegrees = 90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointExtC.x, pointExtC.y)
                        arcTo(
                                rect = Rect(offset = pointExtD, size = cornerSize),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointG.x + cornerRadius, pointG.y)
                        arcTo(
                                rect = Rect(offset = pointG, size = cornerSize),
                                startAngleDegrees = -90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointL.x, pointL.y)
                        arcTo(
                                rect = Rect(offset = pointM, size = cornerSize),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        close()
                }
        }
}
