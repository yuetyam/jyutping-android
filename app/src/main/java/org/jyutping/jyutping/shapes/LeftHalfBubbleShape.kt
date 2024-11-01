package org.jyutping.jyutping.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

//    F---------------G---+
//    +   |           |   +
//    E-.-+           +-.-+
//    +                   +
//    +                   +
//    D                   +
//     .                  +
//       .                +
//        C               +
//        +               +
//        +               +
//        B-+-+       M...L
//        +   |       |   +
//        +---A---o-------+

class LeftHalfBubbleShape : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
                val width = size.width
                val height = size.height
                val isPhoneLandscape: Boolean = (width > height)
                val keyCornerRadius: Float = 12F * density.density
                val previewCornerRadius: Float = 20F * density.density
                val curveWidth: Float = width / 3F
                val curveHeight: Float = if (isPhoneLandscape) (height / 3F) else (height / 5F)
                val keyWidth: Float = width - curveWidth
                val keyHeight: Float = (height - curveHeight) / 2F
                val pointO = Offset(x = width - (keyWidth / 2F), y = height)
                val pointA = Offset(x = curveWidth + keyCornerRadius, y = height)
                val pointB = Offset(x = curveWidth, y = height - keyCornerRadius)
                val pointC = Offset(x = curveWidth, y = height - keyHeight)
                val pointD = Offset(x = 0F, y = pointC.y - curveHeight)
                val curve1adjust = (pointC.y - pointD.y) / 3F
                val curve1Control1 = Offset(x = pointC.x, y = pointC.y - curve1adjust)
                val curve1Control2 = Offset(x = pointD.x, y = pointD.y + curve1adjust)
                val pointE = Offset(x = 0F, y = previewCornerRadius)
                val pointF = Offset(x = 0F, y = 0F)
                val pointG = Offset(x = width - keyCornerRadius, y = 0F)
                val pointL = Offset(x = width, y = height - keyCornerRadius)
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
                        lineTo(pointG.x, pointG.y)
                        arcTo(
                                rect = Rect(
                                        offset = pointG,
                                        size = Size(keyCornerRadius, keyCornerRadius)
                                ),
                                startAngleDegrees = -90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
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
