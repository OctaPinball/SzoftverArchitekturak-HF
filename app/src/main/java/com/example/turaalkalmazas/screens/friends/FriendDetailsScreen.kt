package com.example.turaalkalmazas.screens.friends

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.turaalkalmazas.ui.theme.Theme

@Composable
fun FriendDetailsScreen(
    userId: String,
    popUpScreen: (String) -> Unit,
    restartApp: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FriendRequestViewModel = hiltViewModel(),
) {
    Theme{
        Text(text = userId)
    }
}