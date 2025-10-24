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

//    F-----------------------------X--------------+-----------a---+
//    +   |                         +              +           |   +
//    E...+                         +              +           +...+
//    +                             +              +               +
//    +                             +              +               +
//    +                             +              +           d...c
//    D                             +              +           |   +
//     .                            J--------------+---------------+
//       .                        .
//         .                    .
//           C               K
//           +               +
//           +               +
//           +...B       M...L
//           +   |       |   +
//           +---A---o-------+

class ExpansiveBubbleShape(
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
        private fun leftBasedPath(size: Size, density: Density): Path {
                val maxWidth = size.width
                val height = size.height

                // C to D
                val curveHeight: Float = height / 5F

                // A to C
                val keyHeight: Float = (height - curveHeight) / 2F

                // F to X
                val primaryWidth = maxWidth / (expansionCount * 3F + 5F) * 5F

                // C to K
                val keyWidth = primaryWidth / 5F * 3F

                // C to D
                val curveWidth: Float = primaryWidth / 5F

                val keyCornerRadius: Float = PresetConstant.keyCornerRadius * 2F * density.density
                val previewCornerRadius: Float = PresetConstant.keyCornerRadius * 1.62F * 2F * density.density

                val pointO = Offset(x = primaryWidth / 2F, y = height)
                val pointA = Offset(x = curveWidth + keyCornerRadius, y = pointO.y)
                val pointB = Offset(x = curveWidth, y = height - keyCornerRadius)
                val pointC = Offset(x = curveWidth, y = pointO.y - keyHeight)
                val pointD = Offset(x = 0F, y = pointC.y - curveHeight)
                val curve1adjust = (pointC.y - pointD.y) / 3F
                val curve1Control1 = Offset(x = pointC.x, y = pointC.y - curve1adjust)
                val curve1Control2 = Offset(x = pointD.x, y = pointD.y + curve1adjust)
                val pointE = Offset(x = 0F, y = previewCornerRadius)
                val pointF = Offset(x = 0F, y = 0F)
                val pointX = Offset(x = primaryWidth, y = 0F)
                val extendedWith = keyWidth * expansionCount
                // val pointExtA = Offset(x = pointX.x + extendedWith - previewCornerRadius, y = 0F)
                val pointExtA = Offset(x = maxWidth - previewCornerRadius, y = 0F)
                val extrasHeight = curveHeight / 2.0F
                val pointJ = Offset(x = primaryWidth, y = pointD.y + extrasHeight)
                val pointExtC = Offset(x = maxWidth, y = pointJ.y - previewCornerRadius)
                val pointExtD = Offset(x = pointExtA.x, y = pointExtC.y)
                val pointK = Offset(x = primaryWidth - curveWidth, y = pointC.y)
                val xControl = (pointJ.x - pointK.x) / 3F
                val yControl = (pointK.y - pointJ.y) / 3F
                val curve2Control1 = Offset(x = pointJ.x - xControl, y = pointJ.y)
                val curve2Control2 = Offset(x = pointK.x, y = pointK.y - yControl)
                val pointL = Offset(x = pointK.x, y = pointO.y - keyCornerRadius)
                val pointM = Offset(x = pointL.x - keyCornerRadius, y = pointL.y)
                return Path().apply {
                        moveTo(pointO.x, pointO.y)
                        lineTo(pointA.x, pointA.y)
                        arcTo(
                                rect = Rect(
                                        offset = pointB,
                                        size = Size(keyCornerRadius, keyCornerRadius)
                                ),
                                startAngleDegrees = 90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointC.x, pointC.y)
                        cubicTo(
                                x1 = curve1Control1.x, y1 = curve1Control1.y,
                                x2 = curve1Control2.x, y2 = curve1Control2.y,
                                x3 = pointD.x, y3 = pointD.y,
                        )
                        lineTo(pointE.x, pointE.y)
                        arcTo(
                                rect = Rect(
                                        offset = pointF,
                                        size = Size(previewCornerRadius, previewCornerRadius)
                                ),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointX.x, pointX.y)

                        lineTo(pointExtA.x, pointExtA.y)
                        arcTo(
                                rect = Rect(
                                        offset = pointExtA,
                                        size = Size(previewCornerRadius, previewCornerRadius)
                                ),
                                startAngleDegrees = -90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointExtC.x, pointExtC.y)
                        arcTo(
                                rect = Rect(
                                        offset = pointExtD,
                                        size = Size(previewCornerRadius, previewCornerRadius)
                                ),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointJ.x, pointJ.y)
                        cubicTo(
                                x1 = curve2Control1.x, y1 = curve2Control1.y,
                                x2 = curve2Control2.x, y2 = curve2Control2.y,
                                x3 = pointK.x, y3 = pointK.y,
                        )
                        lineTo(pointL.x, pointL.y)
                        arcTo(
                                rect = Rect(
                                        offset = pointM,
                                        size = Size(keyCornerRadius, keyCornerRadius)
                                ),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        close()
                }
        }
        private fun rightBasedPath(size: Size, density: Density): Path {
                val maxWidth = size.width
                val height = size.height

                // C to D
                val curveHeight: Float = height / 5F

                // A to C
                val keyHeight: Float = (height - curveHeight) / 2F

                // D to J
                val primaryWidth = maxWidth / (expansionCount * 3F + 5F) * 5F

                // C to K
                val keyWidth = primaryWidth / 5F * 3F

                // C to D
                val curveWidth: Float = primaryWidth / 5F

                val keyCornerRadius: Float = PresetConstant.keyCornerRadius * 2F * density.density
                val previewCornerRadius: Float = PresetConstant.keyCornerRadius * 1.62F * 2F * density.density

                // D to J
                val extrasHeight = curveHeight / 2.0F

                val pointO = Offset(x = (maxWidth - primaryWidth / 2F), y = height)
                val pointD = Offset(x = maxWidth - primaryWidth, y = keyHeight + extrasHeight)
                val pointC = Offset(x = pointD.x + curveWidth, y = height - keyHeight)
                val pointA = Offset(x = pointC.x + keyCornerRadius, y = pointO.y)
                val pointB = Offset(x = pointC.x, y = pointA.y - keyCornerRadius)
                val xControl = (pointC.x - pointD.x) / 3F
                val yControl = (pointC.y - pointD.y) / 3F
                val curve1Control1 = Offset(x = pointC.x, y = pointC.y - yControl)
                val curve1Control2 = Offset(x = pointD.x + xControl, y = pointD.y)
                val extA = Offset(x = previewCornerRadius, y = pointD.y)
                val extB = Offset(x = 0F, y = extA.y - previewCornerRadius)
                val extC = Offset(x = 0F, y = previewCornerRadius)
                val extD = Offset(x = 0F, y = 0F)
                val pointG = Offset(x = maxWidth - previewCornerRadius, y = 0F)
                val pointJ = Offset(x = maxWidth, y = keyHeight)
                val pointK = Offset(x = (pointO.x + keyWidth / 2F), y = pointC.y)
                val curve2adjust = (pointK.y - pointJ.y) / 3F
                val curve2Control1 = Offset(x = pointJ.x, y = pointJ.y + curve2adjust)
                val curve2Control2 = Offset(x = pointK.x, y = pointK.y - curve2adjust)
                val pointL = Offset(x = pointK.x, y = pointB.y)
                val pointM = Offset(x = pointL.x - keyCornerRadius, y = pointL.y)
                return Path().apply {
                        moveTo(pointO.x, pointO.y)
                        lineTo(pointA.x, pointA.y)
                        arcTo(
                                rect = Rect(
                                        offset = pointB,
                                        size = Size(keyCornerRadius, keyCornerRadius)
                                ),
                                startAngleDegrees = 90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointC.x, pointC.y)
                        cubicTo(
                                x1 = curve1Control1.x, y1 = curve1Control1.y,
                                x2 = curve1Control2.x, y2 = curve1Control2.y,
                                x3 = pointD.x, y3 = pointD.y,
                        )
                        lineTo(extA.x, extA.y)
                        arcTo(
                                rect = Rect(
                                        offset = extB,
                                        size = Size(previewCornerRadius, previewCornerRadius)
                                ),
                                startAngleDegrees = 90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(extC.x, extC.y)
                        arcTo(
                                rect = Rect(
                                        offset = extD,
                                        size = Size(previewCornerRadius, previewCornerRadius)
                                ),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointG.x, pointG.y)
                        arcTo(
                                rect = Rect(
                                        offset = pointG,
                                        size = Size(previewCornerRadius, previewCornerRadius)
                                ),
                                startAngleDegrees = -90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        lineTo(pointJ.x, pointJ.y)
                        cubicTo(
                                x1 = curve2Control1.x, y1 = curve2Control1.y,
                                x2 = curve2Control2.x, y2 = curve2Control2.y,
                                x3 = pointK.x, y3 = pointK.y,
                        )
                        lineTo(pointL.x, pointL.y)
                        arcTo(
                                rect = Rect(
                                        offset = pointM,
                                        size = Size(keyCornerRadius, keyCornerRadius)
                                ),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                        )
                        close()
                }
        }
}

//    d-----------+-----------+------------------------G---+
//    +   |       +           +                        |   +
//    c...+       +           +                        +...+
//    +           +           +                            +
//    +           +           +                            +
//    b...+       +           +                            +
//    +   |       +           +                            J
//    +---a-------+-----------D                           .
//                              .                       .
//                                .                   .
//                                  C               K
//                                  +               +
//                                  +               +
//                                  B...+       M...L
//                                  +   |       |   +
//                                  +---A---o-------+
