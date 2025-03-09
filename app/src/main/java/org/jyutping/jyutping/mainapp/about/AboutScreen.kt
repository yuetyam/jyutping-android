package org.jyutping.jyutping.mainapp.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import org.jyutping.jyutping.BuildConfig
import org.jyutping.jyutping.R
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.ui.common.AppLinkLabel
import org.jyutping.jyutping.ui.common.WebLinkLabel
import org.jyutping.jyutping.utilities.AppMaster

@Composable
fun AboutScreen() {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                item {
                        Column(
                                modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colorScheme.background)
                                        .fillMaxWidth()
                        ) {
                                VersionLabel()
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colorScheme.background)
                                        .fillMaxWidth()
                        ) {
                                WebLinkLabel(icon = Icons.Outlined.Link, text = stringResource(id = R.string.about_label_website), uri = AppMaster.websiteAddress)
                                HorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Code, text = stringResource(id = R.string.about_label_source_code), uri = AppMaster.sourceCodeAddress)
                                HorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Lock, text = stringResource(id = R.string.about_label_privacy_policy), uri = AppMaster.privacyPolicyAddress)
                                HorizontalDivider()
                                WebLinkLabel(icon = Icons.AutoMirrored.Outlined.HelpOutline, text = stringResource(id = R.string.about_label_faq), uri = AppMaster.faqAddress)
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colorScheme.background)
                                        .fillMaxWidth()
                        ) {
                                AppLinkLabel(icon = Icons.Outlined.Group, text = stringResource(id = R.string.about_label_telegram), uri = AppMaster.TelegramWebAddress)
                                HorizontalDivider()
                                AppLinkLabel(icon = Icons.Outlined.Group, text = stringResource(id = R.string.about_label_qq), uri = AppMaster.QQWebAddress)
                                HorizontalDivider()
                                AppLinkLabel(icon = Icons.Outlined.Book, text = stringResource(id = R.string.about_label_rednote), uri = AppMaster.RedNoteAddress)
                                HorizontalDivider()
                                AppLinkLabel(icon = Icons.Outlined.CenterFocusStrong, text = stringResource(id = R.string.about_label_instagram), uri = AppMaster.InstagramWebAddress)
                                HorizontalDivider()
                                AppLinkLabel(icon = Icons.Outlined.AlternateEmail, text = stringResource(id = R.string.about_label_threads), uri = AppMaster.ThreadsAddress)
                                HorizontalDivider()
                                AppLinkLabel(icon = Icons.Outlined.AlternateEmail, text = stringResource(id = R.string.about_label_twitter), uri = AppMaster.TwitterWebAddress)
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colorScheme.background)
                                        .fillMaxWidth()
                        ) {
                                WebLinkLabel(icon = Icons.Outlined.CheckCircle, text = stringResource(id = R.string.about_label_google_forms), uri = AppMaster.GoogleFormsAddress)
                                HorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.CheckCircle, text = stringResource(id = R.string.about_label_tencent_survey), uri = AppMaster.TencentSurveyAddress)
                                HorizontalDivider()
                                EmailFeedbackButton()
                        }
                }
        }
}

@Composable
private fun VersionLabel() {
        val version: String by lazy { BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")" }
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = colorScheme.background)
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = colorScheme.onBackground
                )
                Text(
                        text = stringResource(id = R.string.about_label_version),
                        color = colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1.0f))
                SelectionContainer {
                        Text(
                                text = version,
                                color = colorScheme.onBackground
                        )
                }
        }
}

@Composable
private fun EmailFeedbackButton() {
        val context = LocalContext.current
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorScheme.background)
                        .clickable {
                                val appVersion: String = BuildConfig.VERSION_NAME // 0.1.0
                                val appBuildNumber: Int = BuildConfig.VERSION_CODE // 23
                                val androidVersion: String = Build.VERSION.RELEASE // 15
                                val sdkVersion: Int = Build.VERSION.SDK_INT // 35
                                val deviceModel: String = Build.MODEL // Pixel 9 Pro XL
                                val manufacturer: String = Build.MANUFACTURER // Google
                                val information: String = """
                                        App Version: $appVersion ($appBuildNumber)
                                        Android Version: $androidVersion (API ${sdkVersion})
                                        Device: $manufacturer $deviceModel
                                """.trimIndent()
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = "mailto:".toUri()
                                        putExtra(Intent.EXTRA_EMAIL, arrayOf(AppMaster.EmailAddress))
                                        putExtra(Intent.EXTRA_SUBJECT, "Jyutping Feedback")
                                        putExtra(Intent.EXTRA_TEXT, "\n\n${information}")
                                }
                                try {
                                        context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                        val logTag: String = PresetConstant.keyboardPackageName + ".about"
                                        e.message?.let { Log.i(logTag, it) }
                                        Toast.makeText(context, "Email Unavailable", Toast.LENGTH_LONG).show()
                                }
                        }
                        .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        tint = colorScheme.onBackground
                )
                Text(
                        text = stringResource(id = R.string.about_label_email),
                        color = colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1.0f))
                Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp).alpha(0.75f),
                        tint = colorScheme.onBackground
                )
        }
}
