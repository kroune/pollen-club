package io.github.kroune.pollen.presentation.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.presentation.common.CollectEvents
import io.github.kroune.pollen.presentation.theme.PollenTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddFriendScreen(
    onBack: () -> Unit = {},
    viewModel: AddFriendViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    CollectEvents(viewModel.events, snackbarHostState)

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onBack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PollenTheme.colors.paper,
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            AddFriendTopBar(onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                EyebrowLabel(stringResource(MR.strings.friends_id_label))
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.friendIdInput,
                    onValueChange = viewModel::onFriendIdChanged,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PollenTheme.colors.accent,
                        unfocusedBorderColor = PollenTheme.colors.accent,
                        focusedContainerColor = PollenTheme.colors.card,
                        unfocusedContainerColor = PollenTheme.colors.card,
                    ),
                )

                Spacer(Modifier.height(18.dp))

                EyebrowLabel(stringResource(MR.strings.friends_name_label))
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.nameInput,
                    onValueChange = viewModel::onNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PollenTheme.colors.line,
                        unfocusedBorderColor = PollenTheme.colors.line,
                        focusedContainerColor = PollenTheme.colors.card,
                        unfocusedContainerColor = PollenTheme.colors.card,
                    ),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(MR.strings.friends_name_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = PollenTheme.colors.ink3,
                )

                Spacer(Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(14.dp), ambientColor = PollenTheme.colors.accent)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PollenTheme.colors.accent)
                        .clickable(enabled = !state.isSubmitting && state.friendIdInput.isNotBlank()) {
                            viewModel.submit()
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(MR.strings.friends_add_button),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }

                Spacer(Modifier.height(28.dp))

                HorizontalDivider(color = PollenTheme.colors.line2)

                Spacer(Modifier.height(18.dp))

                if (state.myServerId.isNotBlank()) {
                    EyebrowLabel(stringResource(MR.strings.friends_your_id))
                    Spacer(Modifier.height(6.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = state.myServerId,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                color = PollenTheme.colors.ink,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = stringResource(MR.strings.friends_copy),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = PollenTheme.colors.accent2,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(state.myServerId))
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(MR.strings.friends_your_id_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = PollenTheme.colors.ink3,
                        lineHeight = 16.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun AddFriendTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = PollenTheme.colors.ink,
            )
        }
        Text(
            text = stringResource(MR.strings.friends_add_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(48.dp))
    }
}

@Composable
private fun EyebrowLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = PollenTheme.colors.ink3,
    )
}
