package org.jyutping.jyutping

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit

@Composable
fun AboutScreen() {
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(id = R.color.white))
                        .wrapContentSize(Alignment.Center)
        ) {
                Text(
                        text = stringResource(id = R.string.screen_title_about),
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = TextUnit.Unspecified
                )
        }
}
