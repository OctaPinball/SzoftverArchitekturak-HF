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
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.turaalkalmazas.FRIEND_DETAILS_SCREEN
import com.example.turaalkalmazas.FRIEND_REQUEST_SCREEN
import com.example.turaalkalmazas.R
import com.example.turaalkalmazas.SIGN_IN_SCREEN
import com.example.turaalkalmazas.USER_ID
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.model.UserRelation
import com.example.turaalkalmazas.model.UserRelationType
import com.example.turaalkalmazas.screens.common.LogInToAccessFeature



@Composable
fun AddFriendsScreen(
    openScreen: (String) -> Unit,
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddFriendViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val friends by viewModel.users.collectAsState()
    val user by viewModel.user.collectAsState()


    Theme {
        if (user.isAnonymous) {
            LogInToAccessFeature(openScreen)
        } else {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.searchQuery = it.text
                            viewModel.onSearchValueChange()
                        },
                        label = { Text(stringResource(R.string.search)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(30.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(friends) { user ->
                            AddFriendItem(
                                user = user,
                                onAddButtonClick = { userid -> viewModel.onAddButtonClick(userid) },
                                openScreen = { route -> openScreen(route) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendItem(user: UserRelation, onAddButtonClick: (String) -> Unit, openScreen: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = {openScreen("$FRIEND_DETAILS_SCREEN?$USER_ID=${user.user.id}")}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Image",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = user.user.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = user.user.email,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            when (user.relationType) {
                UserRelationType.NONE -> {
                    Button(onClick = {
                        onAddButtonClick(user.user.id)
                    }) {
                        Text(stringResource(R.string.add))
                    }
                }

                UserRelationType.IN_REQUEST -> {
                    Button(onClick = {
                        openScreen(FRIEND_REQUEST_SCREEN)
                    }) {
                        Text(stringResource(R.string.accept))
                    }
                }

                UserRelationType.OUT_REQUEST -> {
                    Button(
                        enabled = false,
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text(stringResource(R.string.added))
                    }
                }

                UserRelationType.FRIEND -> {}//UNREACHABLE
            }
        }
    }
}
