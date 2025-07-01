package com.vr13.secure_chat_app.addcontact

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.vr13.secure_chat_app.R
import com.vr13.secure_chat_app.viewmodel.AddContactViewModel
import com.vr13.secure_chat_app.viewmodel.AddContactState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(navController: NavController) {
    val viewModel: AddContactViewModel = viewModel()
    var searchText by remember { mutableStateOf("") }

    val searchResults by viewModel.searchResults.collectAsState()
    val addContactState by viewModel.addContactState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Contact", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0C1515))
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

            // Search bar
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
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (searchText.isEmpty()) {
                        Text("Search by username", color = Color.Gray)
                    }
                    BasicTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            viewModel.searchUsers(searchText)
                        },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact list
            Column {
                searchResults.forEach { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(contact.photoUrl ?: R.drawable.a),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = contact.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "@${contact.username}",
                                color = Color(0xFFB0BEC5),
                                fontSize = 14.sp
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.addContact(contact.uid) { chatId ->
                                    navController.navigate("chat_screen/$chatId")
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF263442),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.person),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Add")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (addContactState) {
                is AddContactState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }
                is AddContactState.Error -> {
                    Text(
                        text = (addContactState as AddContactState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                is AddContactState.Success -> {
                    Text(
                        text = (addContactState as AddContactState.Success).message,
                        color = Color.Green,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                else -> Unit
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AddContactScreenPreview() {
    AddContactScreen(navController = rememberNavController())
}
