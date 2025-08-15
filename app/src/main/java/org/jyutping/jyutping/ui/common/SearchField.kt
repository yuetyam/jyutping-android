package org.jyutping.jyutping.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.R

@Composable
fun SearchField(
        horizontalPadding: Dp = 0.dp,
        verticalPadding: Dp = 0.dp,
        textState: MutableState<String>,
        submit: () -> Unit
) {
        val focusManager = LocalFocusManager.current
        TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                placeholder = { Text(text = stringResource(id = R.string.search_field_placeholder), color = Color.Gray) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                trailingIcon = {
                        if (textState.value.isNotEmpty()) {
                                IconButton(
                                        onClick = { textState.value = "" }
                                ) {
                                        Icon(Icons.Outlined.Close, contentDescription = null)
                                }
                        }
                },
                keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus(); submit() },
                        onGo = { focusManager.clearFocus(); submit() },
                        onSearch = { focusManager.clearFocus(); submit() },
                        onSend = { focusManager.clearFocus(); submit() }
                ),
                keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                ),
                singleLine = true,
                shape = CircleShape,
                colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                )
        )
}
