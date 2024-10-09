package com.yosemiteyss.flexirotate.ui.screen

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.yosemiteyss.flexirotate.ui.composable.PagerIndicator

@Composable
fun SetupScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()
    val pageList = remember { mutableStateOf(SetupPageManager.getPageList()) }

    val pagerState = rememberPagerState(
        initialPage = SetupPageManager.getCurrentPage(context).ordinal,
        pageCount = { pageList.value.size }
    )

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            pagerState.animateScrollToPage(SetupPageManager.getCurrentPage(context).ordinal)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false
                ) { index ->
                    when (pageList.value[index]) {
                        SetupPageManager.SetupPage.ENABLE_NOTIFICATION -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                EnableNotificationPage()
                            } else {
                                throw IllegalStateException("SetupPage.ENABLE_NOTIFICATION should only be used when sdk >= 33")
                            }
                        }

                        SetupPageManager.SetupPage.ENABLE_WRITE_SETTINGS -> EnableWriteSettingsPage()
                        SetupPageManager.SetupPage.ENABLE_ACCESSIBILITY -> EnableAccessibilityPage()
                        SetupPageManager.SetupPage.COMPLETED -> CompleteSetupPage()
                    }
                }
            }

            PagerIndicator(
                count = pagerState.pageCount,
                currentIndex = pagerState.currentPage
            )
        }
    }
}




