package com.example.turaalkalmazas.screens.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.turaalkalmazas.ADD_FRIENDS_SCREEN
import com.example.turaalkalmazas.FRIENDS_SCREEN
import com.example.turaalkalmazas.FRIEND_REQUEST_SCREEN
import com.example.turaalkalmazas.R

@Composable
fun TopNavigationFriends(openScreen: (String) -> Unit, selectedButton: String) {

    val buttonColors = @Composable { screen: String ->
        if (selectedButton == screen) {
            ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background)
        } else {
            ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
        }
    }

    val tintColors = @Composable { screen: String ->
        if (selectedButton == screen) {
            MaterialTheme.colorScheme.primary
        } else {
            LocalContentColor.current
        }
    }

    @Composable
    fun TopNavButton(screen: String, icon: ImageVector, label: String) {
        Button(
            onClick = { openScreen(screen) },
            colors = buttonColors(screen)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = tintColors(screen)
            )
            Text(
                text = label,
                color = tintColors(screen)
            )
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TopNavButton(FRIENDS_SCREEN, Icons.Default.Person, stringResource(R.string.friends))
            TopNavButton(ADD_FRIENDS_SCREEN, Icons.Default.Add, stringResource(R.string.add))
            TopNavButton(FRIEND_REQUEST_SCREEN, Icons.Default.ArrowForward, stringResource(R.string.friend_request))
        }
    }
}
