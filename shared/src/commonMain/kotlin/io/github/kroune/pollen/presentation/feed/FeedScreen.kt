package io.github.kroune.pollen.presentation.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import dev.icerock.moko.resources.compose.stringResource
import io.github.kdroidfilter.composemediaplayer.CacheConfig
import io.github.kdroidfilter.composemediaplayer.InitialPlayerState
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.domain.model.CommentDomain
import io.github.kroune.pollen.domain.model.ExpertInfoDomain
import io.github.kroune.pollen.domain.model.FeedDataDomain
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.MediaType
import io.github.kroune.pollen.domain.model.VkPostDomain
import io.github.kroune.pollen.presentation.common.CollectEffects
import io.github.kroune.pollen.presentation.common.ExpertRegistry
import io.github.kroune.pollen.presentation.common.FeedListSkeleton
import io.github.kroune.pollen.presentation.common.FullScreenError
import io.github.kroune.pollen.presentation.common.UiEvent
import io.github.kroune.pollen.presentation.common.formatDateLocalized
import io.github.kroune.pollen.presentation.common.shimmerEffect
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource

@Composable
fun FeedScreen(
    state: FeedUiState,
    effects: Flow<UiEvent> = emptyFlow(),
    onRefresh: () -> Unit = {},
    friendsTabContent: (@Composable (SnackbarHostState) -> Unit)? = null,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffects(effects, snackbarHostState, onRetry = onRefresh)

    val scope = rememberCoroutineScope()
    val tabTitles = listOf(
        stringResource(MR.strings.feed_tab_news),
        stringResource(MR.strings.feed_tab_feed),
        stringResource(MR.strings.feed_tab_friends),
        stringResource(MR.strings.feed_tab_media),
    )
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(title) },
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    2 -> friendsTabContent?.invoke(snackbarHostState) ?: Box(Modifier.fillMaxSize())
                    else -> FeedTabPage(
                        feed = state.feed,
                        page = page,
                        isRefreshing = state.isRefreshing,
                        onRetry = onRefresh,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedTabPage(
    feed: LoadState<FeedDataDomain>,
    page: Int,
    isRefreshing: Boolean,
    onRetry: () -> Unit,
) {
    when (feed) {
        is LoadState.Loading -> FeedListSkeleton(modifier = Modifier.fillMaxSize().padding(16.dp))
        is LoadState.Failed -> FullScreenError(onRetry = onRetry)
        is LoadState.Loaded -> PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRetry,
            modifier = Modifier.fillMaxSize(),
        ) {
            when (page) {
                0 -> NewsPage(feed.data)
                1 -> FeedPage(feed.data)
                3 -> MediaPage(feed.data)
                else -> {}
            }
        }
    }
}

@Composable
private fun NewsPage(feed: FeedDataDomain) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (feed.comments.isEmpty()) {
            item { Text(stringResource(MR.strings.feed_no_expert_comments)) }
        }
        items(feed.comments, key = { "c${it.id}" }) { comment ->
            val expertPhoto = ExpertRegistry.photoFor(comment.expertId)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(expertPhoto),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "${comment.expert.name} ${comment.expert.title}",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                formatDateLocalized(comment.date),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        comment.text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (comment.pinned) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedPage(feed: FeedDataDomain) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (feed.vkPosts.isEmpty()) {
            item { Text(stringResource(MR.strings.feed_no_posts)) }
        }
        items(feed.vkPosts, key = { "v${it.id}" }) { post ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (post.userName.isNotBlank()) {
                        Text(
                            post.userName,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Spacer(Modifier.height(2.dp))
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            formatDateLocalized(post.date),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        if (post.location.isNotBlank()) {
                            Text(
                                post.location,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(post.content, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun MediaPage(feed: FeedDataDomain) {
    var fullScreenMedia by remember { mutableStateOf<FullScreenMedia?>(null) }

    fullScreenMedia?.let { media ->
        FullScreenMediaViewer(media = media, onDismiss = { fullScreenMedia = null })
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (feed.media.isEmpty()) {
            item { Text(stringResource(MR.strings.feed_no_media)) }
        }
        items(feed.media, key = { "m${it.id}" }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            formatDateLocalized(item.date),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        if (item.description.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                item.description,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    if (item.url.isNotBlank()) {
                        val bottomClip = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp))
                        when (item.type) {
                            MediaType.IMAGE -> {
                                MediaImage(
                                    url = item.url,
                                    contentDescription = item.description,
                                    onClick = {
                                        fullScreenMedia = FullScreenMedia.Image(
                                            url = item.url,
                                            contentDescription = item.description,
                                        )
                                    },
                                    modifier = bottomClip,
                                )
                            }
                            MediaType.VIDEO -> {
                                MediaVideo(
                                    url = item.url,
                                    onFullscreen = {
                                        fullScreenMedia = FullScreenMedia.Video(url = item.url)
                                    },
                                    modifier = bottomClip.aspectRatio(16f / 9f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaImage(
    url: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
        contentScale = ContentScale.FillWidth,
        loading = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .shimmerEffect(),
            )
        },
        error = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp),
                )
            }
        },
        success = {
            SubcomposeAsyncImageContent()
        },
    )
}

@Composable
private fun MediaVideo(
    url: String,
    onFullscreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isActivated by remember { mutableStateOf(false) }

    if (!isActivated) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .clickable { isActivated = true },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = stringResource(MR.strings.media_play_video),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                    .padding(12.dp),
            )
        }
    } else {
        val playerState = rememberVideoPlayerState(
            cacheConfig = CacheConfig(enabled = true),
        )
        DisposableEffect(url) {
            playerState.openUri(url, InitialPlayerState.PLAY)
            onDispose { playerState.stop() }
        }
        VideoPlayerSurface(
            playerState = playerState,
            modifier = modifier.clickable {
                isActivated = false
                onFullscreen()
            },
        )
    }
}

// region Previews

@Preview
@Composable
private fun PreviewFeedScreenLoading() {
    PollenTheme {
        FeedScreen(
            state = FeedUiState(feed = LoadState.Loading),
            onRefresh = {},
        )
    }
}

@Preview
@Composable
private fun PreviewFeedScreenLoaded() {
    val feed = FeedDataDomain(
        comments = persistentListOf(
            CommentDomain(
                id = 1,
                date = LocalDate(2026, 5, 14),
                expertId = 1,
                expert = ExpertInfoDomain(name = "Ирина", title = "к.м.н."),
                text = "Концентрация пыльцы берёзы достигла средних значений.",
                locationId = 1,
                pinned = true,
            ),
        ),
        vkPosts = persistentListOf(
            VkPostDomain(
                id = 1,
                date = LocalDate(2026, 5, 14),
                location = "Москва",
                userName = "Marina Moskalenko",
                content = "Сегодня зафиксировано повышенное содержание пыльцы.",
            ),
        ),
    )
    PollenTheme {
        FeedScreen(
            state = FeedUiState(feed = LoadState.Loaded(feed)),
            onRefresh = {},
        )
    }
}

@Preview
@Composable
private fun PreviewFeedScreenFailed() {
    PollenTheme {
        FeedScreen(
            state = FeedUiState(feed = LoadState.Failed),
            onRefresh = {},
        )
    }
}

@Preview
@Composable
private fun PreviewFeedTabLoading() {
    PollenTheme {
        FeedTabPage(
            feed = LoadState.Loading,
            page = 0,
            isRefreshing = false,
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun PreviewFeedTabFailed() {
    PollenTheme {
        FeedTabPage(
            feed = LoadState.Failed,
            page = 0,
            isRefreshing = false,
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun PreviewNewsPageLoaded() {
    val feed = FeedDataDomain(
        comments = persistentListOf(
            CommentDomain(
                id = 1,
                date = LocalDate(2026, 5, 14),
                expertId = 1,
                expert = ExpertInfoDomain(name = "Ирина", title = "к.м.н."),
                text = "Концентрация пыльцы берёзы достигла средних значений. Аллергикам рекомендуется носить маску на улице.",
                locationId = 1,
                pinned = true,
            ),
            CommentDomain(
                id = 2,
                date = LocalDate(2026, 5, 13),
                expertId = 2,
                expert = ExpertInfoDomain(name = "Алексей", title = "аллерголог"),
                text = "Орешник закончил пыление. Ольха на спаде.",
                locationId = 1,
                pinned = false,
            ),
        ),
    )
    PollenTheme {
        NewsPage(feed = feed)
    }
}

@Preview
@Composable
private fun PreviewFeedPageLoaded() {
    val feed = FeedDataDomain(
        vkPosts = persistentListOf(
            VkPostDomain(
                id = 1,
                date = LocalDate(2026, 5, 14),
                location = "Москва",
                userName = "Ksana Radchenko",
                content = "Сегодня на станции мониторинга зафиксировано повышенное содержание пыльцы.",
            ),
        ),
    )
    PollenTheme {
        FeedPage(feed = feed)
    }
}

// endregion
