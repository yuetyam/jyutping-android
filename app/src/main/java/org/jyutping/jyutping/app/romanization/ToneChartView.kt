package org.jyutping.jyutping.app.romanization

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import org.jyutping.jyutping.presets.AppleColor

@Composable
fun ToneChartView(modifier: Modifier = Modifier, textColor: Color) {
        // TextMeasurer allows us to measure and draw text efficiently inside a Canvas
        val textMeasurer = rememberTextMeasurer()

        Canvas(modifier = modifier) {
                val widthPx = size.width
                val heightPx = size.height

                val widthUnit = widthPx / 13f
                val heightUnit = heightPx / 5f

                // Calculate y position for level k
                val yOf = { k: Int -> (6f - k.toFloat() - 0.5f) * heightUnit }

                // Define generic text styles
                val defaultTextStyle = TextStyle(fontSize = 15.sp, color = textColor)
                val smallTextStyle = TextStyle(fontSize = 12.sp, color = textColor)

                fun drawCenteredText(text: String, center: Offset, style: TextStyle = defaultTextStyle) {
                        val textLayoutResult = textMeasurer.measure(text, style)
                        drawText(
                                textLayoutResult = textLayoutResult,
                                topLeft = Offset(
                                        x = center.x - (textLayoutResult.size.width / 2f),
                                        y = center.y - (textLayoutResult.size.height / 2f)
                                )
                        )
                }

                fun drawToneLine(start: Offset, end: Offset, color: Color) {
                        val angle = atan2(end.y - start.y, end.x - start.x)
                        val arrowLength = 12.dp.toPx()
                        val arrowHalfWidth = 6.dp.toPx()
                        val lineEnd = Offset(end.x - arrowHalfWidth * cos(angle), end.y - arrowHalfWidth * sin(angle))
                        val tip = Offset(lineEnd.x + arrowLength * cos(angle), lineEnd.y + arrowLength * sin(angle))
                        val firstCorner = Offset(lineEnd.x + arrowHalfWidth * sin(angle), lineEnd.y - arrowHalfWidth * cos(angle))
                        val secondCorner = Offset(lineEnd.x - arrowHalfWidth * sin(angle), lineEnd.y + arrowHalfWidth * cos(angle))
                        drawLine(
                                color = color,
                                start = start,
                                end = lineEnd,
                                strokeWidth = 4.dp.toPx()
                        )
                        drawPath(
                                path = Path().apply {
                                        moveTo(tip.x, tip.y)
                                        lineTo(firstCorner.x, firstCorner.y)
                                        lineTo(secondCorner.x, secondCorner.y)
                                        close()
                                },
                                color = color
                        )
                }

                // Draw Y-axis labels
                drawCenteredText("高", Offset(0f, -2.dp.toPx()), smallTextStyle)
                for (levelValue in 1..5) {
                        drawCenteredText(levelValue.toString(), Offset(0f, yOf(levelValue)))
                }
                drawCenteredText("低", Offset(0f, heightPx + 2.dp.toPx()), smallTextStyle)

                // Draw dashed horizontal background lines
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(0f, 6.dp.toPx()), 0f)
                for (k in 1..5) {
                        drawLine(
                                color = Color.Gray,
                                start = Offset(14.dp.toPx(), yOf(k)),
                                end = Offset(widthPx, yOf(k)),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                                pathEffect = pathEffect
                        )
                }

                // Tone 1: 陰平
                drawToneLine(
                        start = Offset(widthUnit, yOf(5)),
                        end = Offset(widthUnit * 3f, yOf(5)),
                        color = AppleColor.red
                )

                // Tone 2: 陰上
                drawToneLine(
                        start = Offset(widthUnit * 3f, yOf(3)),
                        end = Offset(widthUnit * 4.5f, yOf(5)),
                        color = AppleColor.teal
                )

                // Tone 3: 陰去
                drawToneLine(
                        start = Offset(widthUnit * 4.5f, yOf(3)),
                        end = Offset(widthUnit * 6.5f, yOf(3)),
                        color = AppleColor.purple
                )

                // Tone 4: 陽平
                drawToneLine(
                        start = Offset(widthUnit * 6.5f, yOf(2)),
                        end = Offset(widthUnit * 8.5f, yOf(1)),
                        color = AppleColor.orange
                )

                // Tone 5: 陽上
                drawToneLine(
                        start = Offset(widthUnit * 9f, yOf(1)),
                        end = Offset(widthUnit * 10.5f, yOf(3)),
                        color = AppleColor.green
                )

                // Tone 6: 陽去
                drawToneLine(
                        start = Offset(widthUnit * 10.5f, yOf(2)),
                        end = Offset(widthUnit * 12.5f, yOf(2)),
                        color = AppleColor.blue
                )

                // Draw Tone Labels
                drawCenteredText("1 陰平", Offset(widthUnit * 2f, yOf(5) - 14.dp.toPx()))
                drawCenteredText("2 陰上", Offset(widthUnit * 3.5f - 12.dp.toPx(), yOf(4) - 14.dp.toPx()))
                drawCenteredText("3 陰去", Offset(widthUnit * 5.5f, yOf(3) - 16.dp.toPx()))
                drawCenteredText("4 陽平", Offset(widthUnit * 7f - 12.dp.toPx(), yOf(1) - 12.dp.toPx()))
                drawCenteredText("5 陽上", Offset(widthUnit * 9.5f - 12.dp.toPx(), yOf(2) - 16.dp.toPx()))
                drawCenteredText("6 陽去", Offset(widthUnit * 11.5f, yOf(2) - 16.dp.toPx()))
        }
}
