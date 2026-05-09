package io.github.kroune.pollen.presentation.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.ExpertRegistry
import io.github.kroune.pollen.domain.model.FeedDataDomain
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.domain.model.MediaType
import io.github.kroune.pollen.presentation.common.CollectEvents
import io.github.kroune.pollen.presentation.common.FeedListSkeleton
import io.github.kroune.pollen.presentation.common.FullScreenError
import io.github.kroune.pollen.util.formatDateLocalized
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(viewModel: FeedViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val locale by viewModel.locale.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    CollectEvents(viewModel.events, snackbarHostState, onRetry = viewModel::refresh)

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = when (locale) {
        AppLocale.RU -> listOf("Новости", "Лента", "Друзья", "Медиа")
        AppLocale.EN -> listOf("News", "Feed", "Friends", "Media")
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        when (val feed = uiState.feed) {
            is LoadState.Failed -> {
                FullScreenError(onRetry = viewModel::refresh)
            }
            else -> {
                PullToRefreshBox(
                    isRefreshing = feed is LoadState.Loading,
                    onRefresh = viewModel::refresh,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TabRow(selectedTabIndex = selectedTab) {
                            tabTitles.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = { Text(title) },
                                )
                            }
                        }

                        when (feed) {
                            is LoadState.Loading -> {
                                FeedListSkeleton(modifier = Modifier.padding(16.dp))
                            }
                            is LoadState.Loaded -> {
                                FeedContent(feed.data, selectedTab, locale)
                            }
                            is LoadState.Failed -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedContent(feed: FeedDataDomain, selectedTab: Int, locale: AppLocale) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        when (selectedTab) {
            0 -> {
                if (feed.comments.isEmpty()) {
                    item {
                        Text(
                            if (locale == AppLocale.RU) "Нет комментариев экспертов." else "No expert comments yet.",
                        )
                    }
                }
                items(feed.comments, key = { "c${it.id}" }) { comment ->
                    val expertProfile = ExpertRegistry.get(comment.expertId, locale)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(expertProfile.photo),
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
                                        formatDateLocalized(comment.date, locale),
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
            1 -> {
                if (feed.vkPosts.isEmpty()) {
                    item {
                        Text(
                            if (locale == AppLocale.RU) "Нет записей в ленте." else "No feed posts yet.",
                        )
                    }
                }
                items(feed.vkPosts, key = { "v${it.id}" }) { post ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    formatDateLocalized(post.date, locale),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    post.location,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(post.content, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            2 -> {
                if (feed.friendFeels.isEmpty()) {
                    item {
                        Text(
                            if (locale == AppLocale.RU) "Нет данных от друзей." else "No friend data yet.",
                        )
                    }
                }
                items(feed.friendFeels, key = { "f${it.id}" }) { feel ->
                    val feelingColor = when (feel.feeling) {
                        Feeling.GOOD -> MaterialTheme.colorScheme.primary
                        Feeling.MIDDLE -> MaterialTheme.colorScheme.tertiary
                        Feeling.BAD -> MaterialTheme.colorScheme.error
                    }
                    val feelingText = when (feel.feeling) {
                        Feeling.GOOD -> if (locale == AppLocale.RU) "Хорошо" else "Good"
                        Feeling.MIDDLE -> if (locale == AppLocale.RU) "Средне" else "Average"
                        Feeling.BAD -> if (locale == AppLocale.RU) "Плохо" else "Bad"
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    formatDateLocalized(feel.date, locale),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    feel.location,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                feelingText,
                                style = MaterialTheme.typography.titleMedium,
                                color = feelingColor,
                            )
                        }
                    }
                }
            }
            3 -> {
                if (feed.media.isEmpty()) {
                    item {
                        Text(
                            if (locale == AppLocale.RU) "Нет медиа." else "No media items yet.",
                        )
                    }
                }
                items(feed.media, key = { "m${it.id}" }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Column {
                            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                                Text(
                                    formatDateLocalized(item.date, locale),
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
                            if (item.type == MediaType.IMAGE && item.url.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                AsyncImage(
                                    model = item.url,
                                    contentDescription = item.description,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
