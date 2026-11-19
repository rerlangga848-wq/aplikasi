package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ai.configureAppCheck
import com.example.data.NpaData
import com.example.ui.components.FloatingMusicPlayer
import com.example.ui.components.FloatingOtterPet
import com.example.ui.components.NopiChatSheet
import com.example.ui.screens.ChallengeScreen
import com.example.ui.screens.DetoxScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LabScreen
import com.example.ui.screens.TeamScreen
import com.example.ui.theme.*
import com.example.viewmodel.LabPhase
import com.example.viewmodel.NopiChatViewModel
import com.example.viewmodel.NpaViewModel

enum class NpaDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "Beranda", Icons.Filled.Home, Icons.Outlined.Home),
    LAB("lab", "Doom Test", Icons.Filled.Psychology, Icons.Outlined.Psychology),
    CHALLENGE("challenge", "Challenge", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents),
    DETOX("detox", "Detox", Icons.Filled.Timer, Icons.Outlined.Timer),
    TEAM("team", "Tim", Icons.Filled.Groups, Icons.Outlined.Groups)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            NpaTheme {
                NpaApp()
            }
        }
    }
}

@Composable
fun NpaApp(
    viewModel: NpaViewModel = viewModel(),
    chatViewModel: NopiChatViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NpaDestination.HOME.route

    val navigateTo = remember(navController) {
        { route: String ->
            if (navController.currentDestination?.route != route) {
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(NpaCream)) {
        val useRail = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (useRail) {
                NavigationRail(
                    containerColor = NpaCream,
                    modifier = Modifier
                        .fillMaxHeight()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .border(width = 2.dp, color = NpaNavy.copy(alpha = 0.15f))
                ) {
                    Spacer(Modifier.height(12.dp))
                    NpaBrandMark(onClick = { navigateTo(NpaDestination.HOME.route) })
                    Spacer(Modifier.height(16.dp))
                    NpaDestination.entries.forEach { dest ->
                        val selected = currentRoute == dest.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigateTo(dest.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                    contentDescription = dest.label
                                )
                            },
                            label = { Text(dest.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = NpaNavy,
                                selectedTextColor = NpaNavy,
                                indicatorColor = NpaYellow,
                                unselectedIconColor = NpaNavy.copy(alpha = 0.6f),
                                unselectedTextColor = NpaNavy.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }

            Scaffold(
                containerColor = NpaCream,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    NpaTopBar(
                        onLogoClick = { navigateTo(NpaDestination.HOME.route) },
                        onMulaiClick = { navigateTo(NpaDestination.CHALLENGE.route) }
                    )
                },
                bottomBar = {
                    if (!useRail) {
                        Column(modifier = Modifier.background(NpaCream)) {
                            Box(Modifier.fillMaxWidth().height(2.5.dp).background(NpaNavy))
                            NavigationBar(
                                containerColor = NpaCream,
                                windowInsets = WindowInsets.navigationBars
                            ) {
                                NpaDestination.entries.forEach { dest ->
                                    val selected = currentRoute == dest.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { navigateTo(dest.route) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                                contentDescription = dest.label
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = dest.label,
                                                style = MaterialTheme.typography.labelSmall,
                                                maxLines = 1
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = NpaNavy,
                                            selectedTextColor = NpaNavy,
                                            indicatorColor = NpaYellow,
                                            unselectedIconColor = NpaNavy.copy(alpha = 0.6f),
                                            unselectedTextColor = NpaNavy.copy(alpha = 0.6f)
                                        ),
                                        modifier = Modifier.testTag("nav_${dest.route}")
                                    )
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NpaDestination.HOME.route,
                        enterTransition = { fadeIn(animationSpec = tween(110)) },
                        exitTransition = { fadeOut(animationSpec = tween(80)) },
                        popEnterTransition = { fadeIn(animationSpec = tween(110)) },
                        popExitTransition = { fadeOut(animationSpec = tween(80)) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(NpaDestination.HOME.route) {
                            HomeScreen(
                                homeStateFlow = viewModel.homeState,
                                liveCounterFlow = viewModel.liveNpaCounter,
                                onNextQuote = viewModel::nextQuote,
                                onDropPhone = viewModel::dropPhoneHero,
                                onResetHero = viewModel::resetHero,
                                onNavigateToChallenge = { navigateTo(NpaDestination.CHALLENGE.route) },
                                onNavigateToLab = { navigateTo(NpaDestination.LAB.route) }
                            )
                        }
                        composable(NpaDestination.LAB.route) {
                            LabScreen(
                                storyStateFlow = viewModel.storyState,
                                doomStateFlow = viewModel.doomState,
                                focusStateFlow = viewModel.focusState,
                                presenceStateFlow = viewModel.presenceState,
                                onStartDoomTest = viewModel::startDoomTest,
                                onStopDoomTest = viewModel::stopDoomTest,
                                onResetDoomTest = viewModel::resetDoomTest,
                                onResetDoomScrollScore = viewModel::resetDoomScrollScore,
                                onReportDoomScrollDelta = viewModel::reportDoomScrollDelta,
                                onEngageDoomPost = viewModel::engageDoomPost,
                                onDismissDoomToast = viewModel::dismissDoomToast,
                                onStartFocusShield = viewModel::startFocusShield,
                                onTapTemptation = viewModel::tapTemptation,
                                onStartPresenceMeter = viewModel::startPresenceMeter,
                                onPresenceAction = viewModel::triggerPresenceAction,
                                onStartConnectionStory = viewModel::startConnectionStory,
                                onChooseConnectionOption = viewModel::chooseConnectionOption,
                                onNextConnectionStep = viewModel::nextConnectionStep,
                                onChooseStoryNode = viewModel::chooseStoryNode,
                                onNavigateToDetox = { navigateTo(NpaDestination.DETOX.route) },
                                onNavigateToChallenge = { navigateTo(NpaDestination.CHALLENGE.route) }
                            )
                        }
                        composable(NpaDestination.CHALLENGE.route) {
                            ChallengeScreen(
                                challengeStateFlow = viewModel.challengeState,
                                persistedStateFlow = viewModel.persistedState,
                                onSelectChallenge = viewModel::selectChallenge,
                                onNameChange = viewModel::updatePledgerName,
                                onSubmitPledge = viewModel::submitPledge,
                                onResetPledge = viewModel::resetChallengePledge
                            )
                        }
                        composable(NpaDestination.DETOX.route) {
                            DetoxScreen(
                                detoxRunningFlow = viewModel.detoxRunning,
                                detoxSecondsFlow = viewModel.detoxSeconds,
                                persistedStateFlow = viewModel.persistedState,
                                onStart = viewModel::startDetoxTimer,
                                onPause = viewModel::pauseDetoxTimer,
                                onReset = viewModel::resetDetoxTimer
                            )
                        }
                        composable(NpaDestination.TEAM.route) {
                            TeamScreen()
                        }
                    }

                    // Floating Otter Pet (bottom-start) — opens Gemini chatbot
                    FloatingOtterPet(
                        onOpenChat = chatViewModel::openChat,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 14.dp, bottom = 14.dp)
                    )

                    // Isolated Music Player host — state changes never recompose NavHost
                    FloatingMusicPlayerHost(
                        viewModel = viewModel,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 14.dp, bottom = 14.dp)
                    )
                }
            }
        }

        // Isolated Chat Sheet host — typing / streaming never recomposes root NpaApp
        NopiChatHost(
            viewModel = viewModel,
            chatViewModel = chatViewModel
        )
    }
}

@Composable
private fun FloatingMusicPlayerHost(
    viewModel: NpaViewModel,
    modifier: Modifier = Modifier
) {
    val musicState by viewModel.musicState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    FloatingMusicPlayer(
        tracks = NpaData.tracks,
        activeIndex = musicState.activeTrackIndex,
        isPlaying = musicState.isMusicPlaying,
        drawerOpen = musicState.musicDrawerOpen,
        onToggleDrawer = viewModel::toggleMusicDrawer,
        onSelectTrack = viewModel::selectTrack,
        onTogglePlayPause = viewModel::togglePlayPause,
        onStop = viewModel::stopMusic,
        onOpenYouTube = { videoId ->
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=$videoId")
            )
            context.startActivity(intent)
        },
        modifier = modifier
    )
}

@Composable
private fun NopiChatHost(
    viewModel: NpaViewModel,
    chatViewModel: NopiChatViewModel
) {
    val chatState by chatViewModel.uiState.collectAsStateWithLifecycle()
    if (chatState.isOpen) {
        val persistedState by viewModel.persistedState.collectAsStateWithLifecycle()
        val doomSnapshot by viewModel.doomState.collectAsStateWithLifecycle()
        val latestScore = if (doomSnapshot.phase == LabPhase.RESULT) {
            doomSnapshot.digitalHabitScore
        } else {
            persistedState.lastDoomHabitScore
        }
        val latestSpeed = if (doomSnapshot.phase == LabPhase.RESULT) {
            doomSnapshot.averageSpeedDpPerSec
        } else {
            persistedState.lastDoomAvgSpeedDpPerSec?.toFloat()
        }
        NopiChatSheet(
            chatState = chatState,
            latestHabitScore = latestScore,
            latestScrollSpeedDpPerSec = latestSpeed,
            onDismiss = chatViewModel::closeChat,
            onSelectRole = chatViewModel::switchRole,
            onResetChat = chatViewModel::resetConversation,
            onInputChange = chatViewModel::updateInput,
            onSend = chatViewModel::sendMessage
        )
    }
}

@Composable
private fun NpaTopBar(
    onLogoClick: () -> Unit,
    onMulaiClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NpaCream)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NpaBrandMark(onClick = onLogoClick)

            NeoButton(
                onClick = onMulaiClick,
                containerColor = NpaCoral,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.testTag("topbar_mulai_button")
            ) {
                Text(
                    text = "Mulai NPA",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(NpaNavy)
        )
    }
}

@Composable
private fun NpaBrandMark(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = NpaCream
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(NpaNavy)
                    .border(2.dp, NpaNavy, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("N", style = MaterialTheme.typography.titleLarge, color = NpaCream)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("NPA", style = MaterialTheme.typography.headlineMedium, color = NpaNavy)
                Spacer(Modifier.width(3.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NpaMint)
                        .border(1.5.dp, NpaNavy, CircleShape)
                )
            }
        }
    }
}
