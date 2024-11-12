package com.example.turaalkalmazas.screens.friends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.turaalkalmazas.ADD_FRIENDS_SCREEN
import com.example.turaalkalmazas.ui.theme.Theme

@Composable
fun AddFriendsScreen(
    openScreen: (String) -> Unit,
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddFriendViewModel = hiltViewModel()
) {
    Theme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopNavigationFriends(openScreen, ADD_FRIENDS_SCREEN)

                Text(
                    text = "Friends Screen",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
