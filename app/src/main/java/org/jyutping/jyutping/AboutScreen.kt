package org.jyutping.jyutping

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.ui.common.OpenAppLabel
import org.jyutping.jyutping.ui.common.WebLinkLabel

@Composable
fun AboutScreen() {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
                item {
                        VersionLabel()
                }
                item {
                        Column(
                                modifier = Modifier.padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                                WebLinkLabel(icon = Icons.Outlined.Public, text = stringResource(id = R.string.about_label_website), uri = AppMaster.websiteAddress)
                                WebLinkLabel(icon = Icons.Outlined.Code, text = stringResource(id = R.string.about_label_source_code), uri = AppMaster.sourceCodeAddress)
                                WebLinkLabel(icon = Icons.Outlined.Lock, text = stringResource(id = R.string.about_label_privacy_policy), uri = AppMaster.privacyPolicyAddress)
                                WebLinkLabel(icon = Icons.Outlined.HelpOutline, text = stringResource(id = R.string.about_label_faq), uri = AppMaster.faqAddress)
                        }
                }
                item {
                        Column(
                                modifier = Modifier.padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                                OpenAppLabel(icon = Icons.Outlined.Group, text = stringResource(id = R.string.about_label_telegram), uri = AppMaster.telegramWebAddress)
                                OpenAppLabel(icon = Icons.Outlined.Group, text = stringResource(id = R.string.about_label_qq), uri = AppMaster.qqWebAddress)
                                OpenAppLabel(icon = Icons.Outlined.AlternateEmail, text = stringResource(id = R.string.about_label_twitter), uri = AppMaster.twitterWebAddress)
                                OpenAppLabel(icon = Icons.Outlined.CenterFocusStrong, text = stringResource(id = R.string.about_label_instagram), uri = AppMaster.instagramWebAddress)
                        }
                }
        }
}

@Composable
private fun VersionLabel() {
        val version: String = BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"
        Row(
                modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                Text(text = stringResource(id = R.string.about_label_version))
                Spacer(modifier = Modifier.weight(1.0f))
                SelectionContainer() {
                        Text(text = version)
                }
        }
}
