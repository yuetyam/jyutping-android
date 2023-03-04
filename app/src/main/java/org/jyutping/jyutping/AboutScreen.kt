package org.jyutping.jyutping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun AboutScreen() {
        Column(
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
        ) {
                Text(
                        text = stringResource(id = R.string.screen_title_about),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                )
        }
}
