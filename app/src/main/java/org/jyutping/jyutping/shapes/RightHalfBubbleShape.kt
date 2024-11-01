package org.jyutping.jyutping.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

//    F--------------------G---+
//    +   |                |   +
//    E...+                +...+
//    +                        +
//    +                        +
//    +                        J
//    +                     .
//    +                   .
//    +                 K
//    +                 +
//    +                 +
//    B-+-+         M...L
//    +   +         |   +
//    +-+-A----o--------+

class RightHalfBubbleShape : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
                val width = size.width
                val height = size.height
                val keyCornerRadius: Float = 12F * density.density
                val previewCornerRadius: Float = 20F * density.density
                val curveWidth: Float = width / 3F
                val curveHeight: Float = height / 5F
                val keyWidth: Float = width - curveWidth
                val keyHeight: Float = (height - curveHeight) / 2F
                val pointO = Offset(x = keyWidth / 2F, y = height)
                val pointA = Offset(x = keyCornerRadius, y = height)
                val pointB = Offset(x = 0F, y = height - keyCornerRadius)
                val pointE = Offset(x = 0F, y = keyCornerRadius)
                val pointF = Offset(x = 0F, y = 0F)
                val pointG = Offset(x = width - previewCornerRadius, y = 0F)
                val pointJ = Offset(x = width, y = keyHeight)
                val pointK = Offset(x = keyWidth, y = height - keyHeight)
                val curve2adjust = (pointK.y - pointJ.y) / 3F
                val curve2Control1 = Offset(x = pointJ.x, y = pointJ.y + curve2adjust)
                val curve2Control2 = Offset(x = pointK.x, y = pointK.y - curve2adjust)
                val pointL = Offset(x = keyWidth, y = height - keyCornerRadius)
                val pointM = Offset(x = pointL.x - keyCornerRadius, y = pointL.y)
                val path = Path().apply {
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
                        lineTo(pointE.x, pointE.y)
                        arcTo(
                                rect = Rect(
                                        offset = pointF,
                                        size = Size(keyCornerRadius, keyCornerRadius)
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
                return Outline.Generic(path)
        }
}
