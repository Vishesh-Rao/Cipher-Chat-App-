package com.vr13.secure_chat_app.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.vr13.secure_chat_app.R
import com.vr13.secure_chat_app.viewmodel.ChatListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    viewModel: ChatListViewModel = viewModel()
) {
    var search by remember { mutableStateOf("") }
    val chatRooms by viewModel.chatRooms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // store a local snapshot of errorMessage to allow smart-cast
    val localError = errorMessage

    LaunchedEffect(currentUserId) {
        currentUserId?.let {
            viewModel.fetchChatsForUser(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0C1515))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0C1515)) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* current screen */ },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.chat),
                            contentDescription = "Chats",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("add_contact") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Add Contact",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("edit_profile") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
            }
        },
        containerColor = Color(0xFF0C1515)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFF263442), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                if (search.isEmpty()) {
                    Text(
                        text = "Search",
                        color = Color(0xFFB0BEC5),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                BasicTextField(
                    value = search,
                    onValueChange = { search = it },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator(color = Color.White)
                }
                !localError.isNullOrEmpty() -> {
                    Text(
                        text = localError,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
                else -> {
                    chatRooms
                        .filter {
                            it.participants.any { p -> p.contains(search, ignoreCase = true) } ||
                                    it.lastMessage.contains(search, ignoreCase = true)
                        }
                        .forEach { chat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        currentUserId?.let { uid ->
                                            navController.navigate("chat_screen/${chat.chatId}/$uid")
                                        }
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = chat.participants.joinToString(", "),
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        chat.lastMessage,
                                        color = Color(0xFFB0BEC5),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}
