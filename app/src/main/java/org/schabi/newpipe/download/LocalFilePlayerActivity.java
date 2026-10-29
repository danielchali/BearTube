package org.schabi.newpipe.download;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import org.schabi.newpipe.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Compact docked mini-bar offline player that expands to fullscreen album art on click.
 */
public class LocalFilePlayerActivity extends AppCompatActivity {
    private static final long SEEK_DELTA_MS = 10000;
    private static final long REWIND_THRESHOLD_MS = 3000;
    private static final int SEEK_BAR_MAX = 1000;
    private static final int SWIPE_THRESHOLD_PX = 60;

    private ExoPlayer player;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private boolean isFullscreen;
    private boolean isUserSeeking;

    private CardView miniPlayerCard;
    private View fullscreenPlayerLayout;

    private TextView miniTitle;
    private ImageButton miniPrev;
    private ImageButton miniPlayPause;
    private ImageButton miniNext;
    private ImageButton miniClose;

    private PlayerView fullPlayerView;
    private ImageView fullAlbumArt;
    private TextView fullTitle;
    private SeekBar fullSeekBar;
    private TextView fullTimeCurrent;
    private TextView fullTimeDuration;
    private ImageButton fullCollapse;
    private ImageButton fullClose;
    private ImageButton fullPrev;
    private ImageButton fullRewind;
    private ImageButton fullPlayPause;
    private ImageButton fullForward;
    private ImageButton fullNext;

    private List<String> playlistUris = new ArrayList<>();
    private List<String> playlistTitles = new ArrayList<>();

    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (player != null && !isUserSeeking) {
                final long position = player.getCurrentPosition();
                final long duration = player.getDuration();
                if (duration > 0) {
                    final int progress = (int) ((position * SEEK_BAR_MAX) / duration);
                    fullSeekBar.setProgress(progress);
                    fullTimeCurrent.setText(formatTime(position));
                    fullTimeDuration.setText(formatTime(duration));
                }
            }
            if (player != null && player.isPlaying()) {
                progressHandler.postDelayed(this, 500);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_file_player);

        initViews();
        setupInsets();
        setupBackNavigation();
        setupPlaylist();
    }

    private void setupInsets() {
        final View rootLayout = findViewById(R.id.player_root_layout);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                final int topInset = insets.getInsets(
                        WindowInsetsCompat.Type.statusBars()).top;
                final int bottomInset = insets.getInsets(
                        WindowInsetsCompat.Type.navigationBars()).bottom;
                fullscreenPlayerLayout.setPadding(0, topInset, 0, bottomInset);
                return insets;
            });
        }
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFullscreen) {
                    showFullscreen(false);
                } else {
                    finish();
                }
            }
        });
    }

    private void initViews() {
        miniPlayerCard = findViewById(R.id.mini_player_card);
        fullscreenPlayerLayout = findViewById(R.id.fullscreen_player_layout);

        miniTitle = findViewById(R.id.mini_title);
        miniPrev = findViewById(R.id.mini_prev);
        miniPlayPause = findViewById(R.id.mini_play_pause);
        miniNext = findViewById(R.id.mini_next);
        miniClose = findViewById(R.id.mini_close);

        fullPlayerView = findViewById(R.id.full_player_view);
        fullAlbumArt = findViewById(R.id.full_album_art);
        fullTitle = findViewById(R.id.full_title);
        fullSeekBar = findViewById(R.id.full_seek_bar);
        fullTimeCurrent = findViewById(R.id.full_time_current);
        fullTimeDuration = findViewById(R.id.full_time_duration);
        fullCollapse = findViewById(R.id.full_collapse);
        fullClose = findViewById(R.id.full_close);
        fullPrev = findViewById(R.id.full_prev);
        fullRewind = findViewById(R.id.full_rewind);
        fullPlayPause = findViewById(R.id.full_play_pause);
        fullForward = findViewById(R.id.full_forward);
        fullNext = findViewById(R.id.full_next);

        miniPlayerCard.setOnClickListener(v -> showFullscreen(true));
        fullCollapse.setOnClickListener(v -> showFullscreen(false));

        final View dragHandle = findViewById(R.id.drag_handle);
        if (dragHandle != null) {
            dragHandle.setOnClickListener(v -> showFullscreen(false));
        }

        final View fullTopBar = findViewById(R.id.full_top_bar);
        if (fullTopBar != null) {
            fullTopBar.setOnClickListener(v -> showFullscreen(false));
        }

        final GestureDetector gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(final MotionEvent e1, final MotionEvent e2,
                                           final float velocityX, final float velocityY) {
                        if (e1 != null && e2 != null
                                && (e2.getY() - e1.getY() > SWIPE_THRESHOLD_PX)
                                && Math.abs(velocityY) > SWIPE_THRESHOLD_PX) {
                            showFullscreen(false);
                            return true;
                        }
                        return false;
                    }
                });
        final View.OnTouchListener swipeListener = (v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        };
        fullscreenPlayerLayout.setOnTouchListener(swipeListener);

        miniClose.setOnClickListener(v -> finish());
        fullClose.setOnClickListener(v -> finish());

        miniPlayPause.setOnClickListener(v -> togglePlayPause());
        fullPlayPause.setOnClickListener(v -> togglePlayPause());

        miniPrev.setOnClickListener(v -> seekPrevious());
        fullPrev.setOnClickListener(v -> seekPrevious());

        miniNext.setOnClickListener(v -> seekNext());
        fullNext.setOnClickListener(v -> seekNext());

        fullRewind.setOnClickListener(v -> seekBy(-SEEK_DELTA_MS));
        fullForward.setOnClickListener(v -> seekBy(SEEK_DELTA_MS));

        fullSeekBar.setMax(SEEK_BAR_MAX);
        fullSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress,
                                          final boolean fromUser) {
                if (fromUser && player != null && player.getDuration() > 0) {
                    final long pos = (progress * player.getDuration()) / SEEK_BAR_MAX;
                    fullTimeCurrent.setText(formatTime(pos));
                }
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                isUserSeeking = false;
                if (player != null && player.getDuration() > 0) {
                    final long pos = (seekBar.getProgress() * player.getDuration())
                            / SEEK_BAR_MAX;
                    player.seekTo(pos);
                }
            }
        });
    }

    private void setupPlaylist() {
        final Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        final ArrayList<String> uris = intent.getStringArrayListExtra(IntentExtras.PLAYLIST_URIS);
        final ArrayList<String> titles =
                intent.getStringArrayListExtra(IntentExtras.PLAYLIST_TITLES);
        final int startIndex = intent.getIntExtra(IntentExtras.PLAYLIST_INDEX, 0);

        if (uris != null && !uris.isEmpty()) {
            playlistUris = uris;
            playlistTitles = titles != null ? titles : new ArrayList<>();
        } else if (intent.getData() != null) {
            playlistUris.add(intent.getData().toString());
            final String singleTitle = intent.getStringExtra(IntentExtras.TITLE);
            playlistTitles.add(singleTitle != null ? singleTitle : getString(R.string.downloads));
        } else {
            finish();
            return;
        }

        player = new ExoPlayer.Builder(this).build();
        final com.google.android.exoplayer2.audio.AudioAttributes audioAttrs =
                new com.google.android.exoplayer2.audio.AudioAttributes.Builder()
                        .setUsage(com.google.android.exoplayer2.C.USAGE_MEDIA)
                        .setContentType(com.google.android.exoplayer2.C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build();
        player.setAudioAttributes(audioAttrs, true);
        player.setHandleAudioBecomingNoisy(true);
        fullPlayerView.setPlayer(player);
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(final boolean isPlaying) {
                updatePlayPauseButton(isPlaying);
            }

            @Override
            public void onMediaItemTransition(@Nullable final MediaItem mediaItem,
                                              final int reason) {
                updateCurrentTitle();
            }

            @Override
            public void onPlayerError(@NonNull final PlaybackException error) {
                finish();
            }
        });

        for (final String uriString : playlistUris) {
            player.addMediaItem(MediaItem.fromUri(Uri.parse(uriString)));
        }

        final int targetIndex = Math.max(0, Math.min(startIndex, playlistUris.size() - 1));
        player.seekTo(targetIndex, 0);
        player.prepare();
        player.play();

        updateCurrentTitle();
        updatePlayPauseButton(true);
    }

    private void updateCurrentTitle() {
        if (player == null) {
            return;
        }
        final int index = player.getCurrentMediaItemIndex();
        String title = null;
        if (index >= 0 && index < playlistTitles.size()) {
            title = playlistTitles.get(index);
        }
        if (title == null || title.isEmpty()) {
            title = getIntent().getStringExtra(IntentExtras.TITLE);
        }
        if (title == null || title.isEmpty()) {
            title = getString(R.string.downloads);
        }
        miniTitle.setText(title);
        fullTitle.setText(title);
    }

    private void updatePlayPauseButton(final boolean isPlaying) {
        final int icon = isPlaying ? R.drawable.ic_pause : R.drawable.ic_play_arrow;
        miniPlayPause.setImageResource(icon);
        fullPlayPause.setImageResource(icon);
        if (isPlaying) {
            progressHandler.post(progressRunnable);
        } else {
            progressHandler.removeCallbacks(progressRunnable);
        }
    }

    private void showFullscreen(final boolean show) {
        isFullscreen = show;
        if (show) {
            miniPlayerCard.setVisibility(View.GONE);
            fullscreenPlayerLayout.setVisibility(View.VISIBLE);
            progressHandler.post(progressRunnable);
        } else {
            fullscreenPlayerLayout.setVisibility(View.GONE);
            miniPlayerCard.setVisibility(View.VISIBLE);
        }
    }

    private void togglePlayPause() {
        if (player == null) {
            return;
        }
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.play();
        }
    }

    private void seekPrevious() {
        if (player == null) {
            return;
        }
        if (player.getCurrentPosition() > REWIND_THRESHOLD_MS) {
            player.seekTo(0);
        } else if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem();
        } else {
            player.seekTo(0);
        }
    }

    private void seekNext() {
        if (player == null) {
            return;
        }
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem();
        }
    }

    private void seekBy(final long deltaMs) {
        if (player == null) {
            return;
        }
        final long target = Math.max(0,
                Math.min(player.getCurrentPosition() + deltaMs, player.getDuration()));
        player.seekTo(target);
    }

    private static String formatTime(final long ms) {
        final long totalSeconds = Math.max(0, ms / 1000);
        final long seconds = totalSeconds % 60;
        final long minutes = (totalSeconds / 60) % 60;
        final long hours = totalSeconds / 3600;
        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            showFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null && player.isPlaying()) {
            progressHandler.post(progressRunnable);
            updatePlayPauseButton(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressHandler.removeCallbacks(progressRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressHandler.removeCallbacks(progressRunnable);
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public static final class IntentExtras {
        public static final String TITLE = "download_title";
        public static final String PLAYLIST_URIS = "playlist_uris";
        public static final String PLAYLIST_TITLES = "playlist_titles";
        public static final String PLAYLIST_INDEX = "playlist_index";

        private IntentExtras() {
        }
    }
}
