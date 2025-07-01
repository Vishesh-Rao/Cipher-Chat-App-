package com.vr13.secure_chat_app.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.navigation.NavHostController
import com.vr13.secure_chat_app.R

data class ContactItem(
    val name: String,
    val lastMessage: String,
    val avatar: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }

    val contacts = listOf(
        ContactItem("Sophia Carter", "See you tomorrow!", R.drawable.a),
        ContactItem("Ethan Walker", "Sounds good to me.", R.drawable.a),
        ContactItem("Olivia Bennett", "I'll be there in 10.", R.drawable.a),
        ContactItem("Noah Thompson", "Let's catch up soon.", R.drawable.a),
        ContactItem("Ava Harris", "Thanks for the update.", R.drawable.a),
        ContactItem("Liam Cooper", "I'm on my way.", R.drawable.a),
        ContactItem("Isabella Reed", "Can we reschedule?", R.drawable.a),
        ContactItem("Jackson Hayes", "I'll call you later.", R.drawable.a)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0C1515)
                )
            )
        },
        containerColor = Color(0xFF0C1515)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF263442), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search",
                    tint = Color.Gray,
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (searchText.isEmpty()) {
                        Text("Search", color = Color.Gray)
                    }
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { searchText = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact List
            Column {
                contacts.filter {
                    it.name.contains(searchText, ignoreCase = true)
                }.forEach { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Navigate to chat detail
                                navController.navigate("chat_detail")
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = contact.avatar),
                            contentDescription = null,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = contact.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Last message: \"${contact.lastMessage}\"",
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
