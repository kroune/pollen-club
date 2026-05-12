package io.github.kroune.pollen.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import dev.icerock.moko.resources.compose.stringResource
import io.github.kdroidfilter.composemediaplayer.CacheConfig
import io.github.kdroidfilter.composemediaplayer.InitialPlayerState
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState
import io.github.kroune.pollen.MR
import io.github.kroune.pollen.presentation.common.shimmerEffect

sealed interface FullScreenMedia {
    data class Image(val url: String, val contentDescription: String) : FullScreenMedia
    data class Video(val url: String) : FullScreenMedia
}

@Composable
fun FullScreenMediaViewer(
    media: FullScreenMedia,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
            contentAlignment = Alignment.Center,
        ) {
            when (media) {
                is FullScreenMedia.Image -> {
                    SubcomposeAsyncImage(
                        model = media.url,
                        contentDescription = media.contentDescription,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
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
                                    .height(200.dp),
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
                is FullScreenMedia.Video -> {
                    val playerState = rememberVideoPlayerState(
                        cacheConfig = CacheConfig(enabled = true),
                    )
                    DisposableEffect(media.url) {
                        playerState.openUri(media.url, InitialPlayerState.PLAY)
                        onDispose { playerState.stop() }
                    }
                    VideoPlayerSurface(
                        playerState = playerState,
                        modifier = Modifier.fillMaxSize(),
                        overlay = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.BottomStart,
                            ) {
                                IconButton(
                                    onClick = {
                                        if (playerState.isPlaying) playerState.pause() else playerState.play()
                                    },
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(40.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                                ) {
                                    Icon(
                                        if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                    )
                                }
                            }
                        },
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(MR.strings.close),
                    tint = Color.White,
                )
            }
        }
    }
}
