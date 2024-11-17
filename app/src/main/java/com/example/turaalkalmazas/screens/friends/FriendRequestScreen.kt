package com.example.turaalkalmazas.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.turaalkalmazas.ui.theme.Theme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.turaalkalmazas.FRIEND_DETAILS_SCREEN
import com.example.turaalkalmazas.R
import com.example.turaalkalmazas.SIGN_IN_SCREEN
import com.example.turaalkalmazas.USER_ID
import com.example.turaalkalmazas.model.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendRequestScreen(
    openScreen: (String) -> Unit,
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FriendRequestViewModel = hiltViewModel(),
) {
    val friendRequests by viewModel.users.collectAsState()
    val user by viewModel.user.collectAsState()

    Theme {
        if (user.isAnonymous) {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.please_sign_in),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { openScreen(SIGN_IN_SCREEN) },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(stringResource(R.string.sign_in))
                    }
                }
            }

        } else {
            Surface(color = MaterialTheme.colorScheme.background) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(friendRequests) { user ->
                        FriendRequestItem(
                            user = user,
                            accept = { userId -> viewModel.onAcceptClick(userId) },
                            reject = { userId -> viewModel.onRejectClick(userId) },
                            openScreen = { route -> openScreen(route) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendRequestItem(user: User, accept: (String) -> Unit, reject: (String) -> Unit, openScreen: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = {openScreen("$FRIEND_DETAILS_SCREEN?$USER_ID=${user.id}")}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Image",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(1.dp)
            ) {
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Row {
                Button(
                    onClick = { reject(user.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Reject")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    onClick = { accept(user.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Accept")
                }
            }
        }
    }
}
