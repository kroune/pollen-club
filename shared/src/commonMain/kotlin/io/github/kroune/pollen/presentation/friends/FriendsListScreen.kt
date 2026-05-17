package io.github.kroune.pollen.presentation.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.LoadState
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import io.github.kroune.pollen.presentation.common.CollectEvents
import io.github.kroune.pollen.presentation.common.FriendsListSkeleton
import io.github.kroune.pollen.presentation.common.FullScreenError
import androidx.compose.ui.tooling.preview.Preview
import io.github.kroune.pollen.presentation.theme.PollenTheme
import io.github.kroune.pollen.util.formatDateLocalized
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.viewmodel.koinViewModel

/** ViewModel convenience overload — used by navigation. */
@Composable
fun FriendsListScreen(
    onBack: () -> Unit = {},
    onNavigateToAddFriend: () -> Unit = {},
    viewModel: FriendsListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val locale by viewModel.locale.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEvents(viewModel.events, snackbarHostState, onRetry = viewModel::loadData)

    FriendsListScreen(
        state = state,
        locale = locale,
        onBack = onBack,
        onNavigateToAddFriend = onNavigateToAddFriend,
        onDeleteFriend = viewModel::deleteFriend,
        onRetry = viewModel::loadData,
        snackbarHostState = snackbarHostState,
    )
}

/** State-based overload — previewable and testable. */
@Composable
fun FriendsListScreen(
    state: FriendsListUiState,
    locale: AppLocale,
    onBack: () -> Unit,
    onNavigateToAddFriend: () -> Unit,
    onDeleteFriend: (Int) -> Unit,
    onRetry: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PollenTheme.colors.paper,
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            FriendsTopBar(onBack = onBack)
            FriendsListBody(
                state = state,
                locale = locale,
                onNavigateToAddFriend = onNavigateToAddFriend,
                onDeleteFriend = onDeleteFriend,
                onRetry = onRetry,
            )
        }
    }
}

/** ViewModel convenience overload — used inside FeedScreen pager. */
@Composable
fun FriendsListContent(
    onNavigateToAddFriend: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: FriendsListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val locale by viewModel.locale.collectAsState()
    CollectEvents(viewModel.events, snackbarHostState, onRetry = viewModel::loadData)

    FriendsListContent(
        state = state,
        locale = locale,
        onNavigateToAddFriend = onNavigateToAddFriend,
        onDeleteFriend = viewModel::deleteFriend,
        onRetry = viewModel::loadData,
    )
}

/** State-based overload — previewable and testable. */
@Composable
fun FriendsListContent(
    state: FriendsListUiState,
    locale: AppLocale,
    onNavigateToAddFriend: () -> Unit,
    onDeleteFriend: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    FriendsListBody(
        state = state,
        locale = locale,
        onNavigateToAddFriend = onNavigateToAddFriend,
        onDeleteFriend = onDeleteFriend,
        onRetry = onRetry,
    )
}

@Composable
private fun FriendsListBody(
    state: FriendsListUiState,
    locale: AppLocale,
    onNavigateToAddFriend: () -> Unit,
    onDeleteFriend: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    when (val friends = state.friends) {
        is LoadState.Loading -> {
            FriendsListSkeleton(modifier = Modifier.padding(16.dp))
        }
        is LoadState.Failed -> {
            FullScreenError(onRetry = onRetry)
        }
        is LoadState.Loaded -> {
            if (friends.data.isEmpty()) {
                FriendsEmptyState(
                    myServerId = state.myServerId,
                    onAddFriend = onNavigateToAddFriend,
                )
            } else {
                FriendsPopulatedList(
                    friends = friends.data,
                    locale = locale,
                    onAddFriend = onNavigateToAddFriend,
                    onDeleteFriend = onDeleteFriend,
                )
            }
        }
    }
}

@Composable
private fun FriendsTopBar(onBack: () -> Unit) {
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
            text = stringResource(MR.strings.settings_friends),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(48.dp))
    }
}

@Composable
private fun FriendsPopulatedList(
    friends: ImmutableList<FriendUi>,
    locale: AppLocale,
    onAddFriend: () -> Unit,
    onDeleteFriend: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${stringResource(MR.strings.friends_your_friends)} · ${friends.size}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = PollenTheme.colors.ink3,
                letterSpacing = 1.2.sp,
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onAddFriend)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = PollenTheme.colors.accent2,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(MR.strings.friends_add),
                    style = MaterialTheme.typography.labelSmall,
                    color = PollenTheme.colors.accent2,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.card),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column {
                friends.forEachIndexed { index, friend ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = PollenTheme.colors.line2,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                    FriendRow(
                        friend = friend,
                        locale = locale,
                        onDelete = { onDeleteFriend(friend.friendId) },
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = PollenTheme.colors.paper2),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Text(
                text = stringResource(MR.strings.friends_map_hint),
                style = MaterialTheme.typography.bodySmall,
                color = PollenTheme.colors.ink3,
                lineHeight = 16.sp,
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}

@Composable
private fun FriendRow(
    friend: FriendUi,
    locale: AppLocale,
    onDelete: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text(
                    text = stringResource(MR.strings.friends_delete),
                    color = MaterialTheme.colorScheme.onError,
                    fontWeight = FontWeight.Medium,
                )
            }
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PollenTheme.colors.card)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val clipboardManager = LocalClipboardManager.current
            val displayName = friend.name ?: "ID ${friend.friendId}"

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = PollenTheme.colors.ink,
                )
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${friend.friendId}",
                        fontSize = 12.sp,
                        color = PollenTheme.colors.ink3,
                        letterSpacing = 0.3.sp,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(MR.strings.friends_copy),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = PollenTheme.colors.accent2,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PollenTheme.colors.accentLight)
                            .clickable {
                                clipboardManager.setText(AnnotatedString("${friend.friendId}"))
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }

            if (friend.lastPinFeeling != null) {
                LastPinInfo(friend, locale)
            } else {
                Text(
                    text = stringResource(MR.strings.friends_no_pins),
                    style = MaterialTheme.typography.labelSmall,
                    color = PollenTheme.colors.ink3,
                )
            }
        }
    }
}

@Composable
private fun LastPinInfo(friend: FriendUi, locale: AppLocale) {
    val feelingColor = when (friend.lastPinFeeling) {
        Feeling.GOOD -> PollenTheme.colors.severity1
        Feeling.MIDDLE -> PollenTheme.colors.severity2
        Feeling.BAD -> PollenTheme.colors.severity4
        null -> PollenTheme.colors.ink3
    }
    val feelingText = when (friend.lastPinFeeling) {
        Feeling.GOOD -> stringResource(MR.strings.feeling_good)
        Feeling.MIDDLE -> stringResource(MR.strings.feeling_moderate)
        Feeling.BAD -> stringResource(MR.strings.feeling_bad)
        null -> ""
    }

    Column(horizontalAlignment = Alignment.End) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(feelingColor),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = feelingText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = feelingColor,
            )
        }
        val meta = buildString {
            friend.lastPinPollenName?.let { append(it) }
            friend.lastPinDate?.let {
                if (isNotEmpty()) append(" · ")
                append(formatDateLocalized(it, locale))
            }
        }
        if (meta.isNotEmpty()) {
            Text(
                text = meta,
                style = MaterialTheme.typography.labelSmall,
                color = PollenTheme.colors.ink3,
            )
        }
    }
}

@Composable
private fun FriendsEmptyState(
    myServerId: String,
    onAddFriend: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.weight(1f))

        val dashedColor = PollenTheme.colors.line
        Box(
            modifier = Modifier
                .size(72.dp)
                .drawBehind {
                    drawRoundRect(
                        color = dashedColor,
                        cornerRadius = CornerRadius(size.width / 2),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)),
                        ),
                    )
                }
                .clip(CircleShape)
                .background(PollenTheme.colors.paper2),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.PushPin,
                contentDescription = null,
                tint = PollenTheme.colors.ink3,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(MR.strings.friends_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = PollenTheme.colors.ink,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(MR.strings.friends_empty_body_qr),
            style = MaterialTheme.typography.bodySmall,
            color = PollenTheme.colors.ink3,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(PollenTheme.colors.accent)
                .clickable(onClick = onAddFriend)
                .padding(horizontal = 28.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(MR.strings.friends_empty_cta),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        if (myServerId.isNotBlank()) {
            HorizontalDivider(color = PollenTheme.colors.line2)
            Spacer(Modifier.height(16.dp))
            YourCodeForFriends(
                myServerId = myServerId,
                qrSize = 60.dp,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

// region Previews

private val previewFriends = persistentListOf(
    FriendUi(
        friendId = 1001,
        name = "Алексей",
        lastPinFeeling = Feeling.BAD,
        lastPinPollenName = "Берёза",
        lastPinDate = "2026-05-13",
    ),
    FriendUi(
        friendId = 1002,
        name = "Мария",
        lastPinFeeling = Feeling.GOOD,
        lastPinPollenName = "Ольха",
        lastPinDate = "2026-05-12",
    ),
    FriendUi(
        friendId = 1003,
        name = null,
        lastPinFeeling = null,
        lastPinPollenName = null,
        lastPinDate = null,
    ),
)

@Preview
@Composable
private fun PreviewFriendsScreenPopulated() {
    PollenTheme {
        FriendsListScreen(
            state = FriendsListUiState(
                friends = LoadState.Loaded(previewFriends),
                myServerId = "12345",
            ),
            locale = AppLocale.RU,
            onBack = {},
            onNavigateToAddFriend = {},
            onDeleteFriend = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun PreviewFriendsScreenEmpty() {
    PollenTheme {
        FriendsListScreen(
            state = FriendsListUiState(
                friends = LoadState.Loaded(persistentListOf()),
                myServerId = "12345",
            ),
            locale = AppLocale.RU,
            onBack = {},
            onNavigateToAddFriend = {},
            onDeleteFriend = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun PreviewFriendsScreenLoading() {
    PollenTheme {
        FriendsListScreen(
            state = FriendsListUiState(friends = LoadState.Loading),
            locale = AppLocale.RU,
            onBack = {},
            onNavigateToAddFriend = {},
            onDeleteFriend = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun PreviewFriendsScreenFailed() {
    PollenTheme {
        FriendsListScreen(
            state = FriendsListUiState(friends = LoadState.Failed),
            locale = AppLocale.RU,
            onBack = {},
            onNavigateToAddFriend = {},
            onDeleteFriend = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun PreviewFriendsPopulated() {
    PollenTheme {
        FriendsPopulatedList(
            friends = previewFriends,
            locale = AppLocale.RU,
            onAddFriend = {},
            onDeleteFriend = {},
        )
    }
}

@Preview
@Composable
private fun PreviewFriendsEmpty() {
    PollenTheme {
        FriendsEmptyState(
            myServerId = "12345",
            onAddFriend = {},
        )
    }
}

@Preview
@Composable
private fun PreviewFriendsLoading() {
    PollenTheme {
        FriendsListBody(
            state = FriendsListUiState(friends = LoadState.Loading),
            locale = AppLocale.RU,
            onNavigateToAddFriend = {},
            onDeleteFriend = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun PreviewFriendsFailed() {
    PollenTheme {
        FriendsListBody(
            state = FriendsListUiState(friends = LoadState.Failed),
            locale = AppLocale.RU,
            onNavigateToAddFriend = {},
            onDeleteFriend = {},
            onRetry = {},
        )
    }
}

// endregion
