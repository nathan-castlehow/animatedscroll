package com.vitruvian.animatescroll

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vitruvian.animatescroll.ui.theme.AnimatescrollTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimatescrollTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    AnimateScrollCancellationIssue()
                }
            }
        }
    }
}



val colors = listOf(
    Color.Red,
    Color.Yellow,
    Color.Green,
    Color.Blue
)
private suspend fun scrollEffect(state: LazyListState, rowIndex: Int) {
    try {
        val index = if (rowIndex > 0) rowIndex - 1 else rowIndex
        val offset = if (rowIndex > 0) 0 else 0

        state.animateScrollToItem(
            index, offset
        )
    } catch (ex: CancellationException) {
        Log.e("scrollEffect", "cancelled scrollEffect $rowIndex", ex)
    }
}

val START_PADDING = 0.dp

@Composable
fun AnimateScrollCancellationIssue() {
    val range = remember { (0..20).map { it } }
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val focusRequesters = remember {
        val frs = (range.indices).map {
            FocusRequester()
        }
        frs
    }


    LazyColumn(
        state = state
    ) {
        item {
            Button(onClick = {
                scope.launch {
                    focusRequesters[3].requestFocus() // comment me out
                    scrollEffect(state, 3)
                }
            }) {
                Text("Request focus and scroll")
            }
        }
        itemsIndexed(range) { index, item ->
            ScrollableRow(
                focusRequesters[index]
            )
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScrollableRow(
    focusRequester: FocusRequester,
) {
    val state = rememberLazyListState()
    val frs = remember {
        (0..20).map { FocusRequester() }
    }
    Box(
        modifier = Modifier
            .focusOrder(focusRequester)
            .focusable(true)
    ) {
        Column {
            LazyRow(
                state = state,
                contentPadding = PaddingValues(horizontal = START_PADDING),
            ) {
                items(frs.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(color = colors[index % colors.size])
                            .size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {}
                }
            }
        }

    }

}


