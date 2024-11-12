package com.example.turaalkalmazas.screens.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.turaalkalmazas.ADD_FRIENDS_SCREEN
import com.example.turaalkalmazas.FRIENDS_SCREEN
import com.example.turaalkalmazas.FRIEND_REQUEST_SCREEN
import com.example.turaalkalmazas.ui.theme.Theme

@Composable
fun FriendRequestScreen(
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
                TopNavigationFriendRequest(openScreen)

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

@Composable
fun TopNavigationFriendRequest(openScreen: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { openScreen(FRIENDS_SCREEN) }) {
            Text(text = "Friends")
        }
        Button(onClick = { openScreen(ADD_FRIENDS_SCREEN) }) {
            Text(text = "Add Friend")
        }
        Button(onClick = { openScreen(FRIEND_REQUEST_SCREEN) }, colors = ButtonDefaults.buttonColors(Color.Blue)) {
            Text(text = "Friend Request")
        }
    }
}