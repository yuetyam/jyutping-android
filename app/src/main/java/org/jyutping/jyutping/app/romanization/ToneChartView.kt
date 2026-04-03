package org.jyutping.jyutping.app.romanization

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                drawLine(
                        color = AppleColor.red,
                        start = Offset(widthUnit, yOf(5)),
                        end = Offset(widthUnit * 2.5f, yOf(5)),
                        strokeWidth = 4.dp.toPx()
                )

                // Tone 2: 陰上
                drawLine(
                        color = AppleColor.teal,
                        start = Offset(widthUnit * 3f, yOf(3)),
                        end = Offset(widthUnit * 4.5f, yOf(5)),
                        strokeWidth = 4.dp.toPx()
                )

                // Tone 3: 陰去
                drawLine(
                        color = AppleColor.purple,
                        start = Offset(widthUnit * 5f, yOf(3)),
                        end = Offset(widthUnit * 6.5f, yOf(3)),
                        strokeWidth = 4.dp.toPx()
                )

                // Tone 4: 陽平
                drawLine(
                        color = AppleColor.orange,
                        start = Offset(widthUnit * 7f, yOf(2)),
                        end = Offset(widthUnit * 8.5f, yOf(1)),
                        strokeWidth = 4.dp.toPx()
                )

                // Tone 5: 陽上
                drawLine(
                        color = AppleColor.green,
                        start = Offset(widthUnit * 9f, yOf(1)),
                        end = Offset(widthUnit * 10.5f, yOf(3)),
                        strokeWidth = 4.dp.toPx()
                )

                // Tone 6: 陽去
                drawLine(
                        color = AppleColor.blue,
                        start = Offset(widthUnit * 11f, yOf(2)),
                        end = Offset(widthUnit * 12.5f, yOf(2)),
                        strokeWidth = 4.dp.toPx()
                )

                // Draw Tone Labels
                drawCenteredText("1 陰平", Offset(widthUnit * 1.5f + 8.dp.toPx(), yOf(5) - 16.dp.toPx()))
                drawCenteredText("2 陰上", Offset(widthUnit * 3.5f - 12.dp.toPx(), yOf(4) - 16.dp.toPx()))
                drawCenteredText("3 陰去", Offset(widthUnit * 5.5f + 10.dp.toPx(), yOf(3) - 16.dp.toPx()))
                drawCenteredText("4 陽平", Offset(widthUnit * 7.5f - 12.dp.toPx(), yOf(1) - 16.dp.toPx()))
                drawCenteredText("5 陽上", Offset(widthUnit * 9.5f - 12.dp.toPx(), yOf(2) - 16.dp.toPx()))
                drawCenteredText("6 陽去", Offset(widthUnit * 11.5f + 8.dp.toPx(), yOf(2) - 16.dp.toPx()))
        }
}
