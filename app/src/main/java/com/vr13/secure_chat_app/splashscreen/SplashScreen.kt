package com.vr13.secure_chat_app.splashscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vr13.secure_chat_app.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // navigate after delay
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("second_screen") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.black1)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.shield),
            contentDescription = null,
            tint = colorResource(R.color.blue),
            modifier = Modifier.size(110.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Cipher",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Your conversations, secured and private",
            color = colorResource(R.color.grey),
            fontSize = 17.sp
        )
    }
}
