package org.jyutping.jyutping.ui.about

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
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.utilities.AppMaster
import org.jyutping.jyutping.BuildConfig
import org.jyutping.jyutping.R
import org.jyutping.jyutping.ui.common.*

@Composable
fun AboutScreen() {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
                item {
                        VersionLabel()
                }
                item {
                        Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                WebLinkLabel(icon = Icons.Outlined.Link, text = stringResource(id = R.string.about_label_website), uri = AppMaster.websiteAddress)
                                WebLinkLabel(icon = Icons.Outlined.Code, text = stringResource(id = R.string.about_label_source_code), uri = AppMaster.sourceCodeAddress)
                                WebLinkLabel(icon = Icons.Outlined.Lock, text = stringResource(id = R.string.about_label_privacy_policy), uri = AppMaster.privacyPolicyAddress)
                                WebLinkLabel(icon = Icons.AutoMirrored.Outlined.HelpOutline, text = stringResource(id = R.string.about_label_faq), uri = AppMaster.faqAddress)
                        }
                }
                item {
                        Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                OpenAppLabel(icon = Icons.Outlined.Group, text = stringResource(id = R.string.about_label_telegram), uri = AppMaster.telegramWebAddress)
                                OpenAppLabel(icon = Icons.Outlined.Group, text = stringResource(id = R.string.about_label_qq), uri = AppMaster.qqWebAddress)
                                OpenAppLabel(icon = Icons.Outlined.AlternateEmail, text = stringResource(id = R.string.about_label_twitter), uri = AppMaster.twitterWebAddress)
                                OpenAppLabel(icon = Icons.Outlined.CenterFocusStrong, text = stringResource(id = R.string.about_label_instagram), uri = AppMaster.instagramWebAddress)
                        }
                }
                item {
                        Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                WebLinkLabel(icon = Icons.Outlined.CheckCircle, text = stringResource(id = R.string.about_label_google_forms), uri = AppMaster.GoogleFormsAddress)
                                WebLinkLabel(icon = Icons.Outlined.CheckCircle, text = stringResource(id = R.string.about_label_tencent_survey), uri = AppMaster.TencentSurveyAddress)

                                // TODO: Implement mailto
                                // OpenAppLabel(icon = Icons.Outlined.Mail, text = stringResource(id = R.string.about_label_email), uri = AppMaster.EmailAddress)
                        }
                }
        }
}

@Composable
private fun VersionLabel() {
        val version: String = BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                Text(text = stringResource(id = R.string.about_label_version))
                Spacer(modifier = Modifier.weight(1.0f))
                SelectionContainer {
                        Text(text = version)
                }
        }
}
