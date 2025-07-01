package com.vr13.secure_chat_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vr13.secure_chat_app.R

@Composable
fun Second_Screen(navController: NavController) {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.black1))
    ) {
        Row(modifier = Modifier.padding(top = 40.dp, start = 165.dp)) {
            Text(
                "Cipher",
                color = Color.White,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.black1)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                "Your privacy,our",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Text("priority", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Experience secure messaging with end-to-end ",
                color = Color.White,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("encryption and disappearing messages ", color = Color.White, fontSize = 15.sp)

            Spacer(modifier = Modifier.height(400.dp))

            Button(
                onClick = {
                    navController.navigate("signup") {
                        popUpTo("second_screen") { inclusive = true }
                    }
                },
                modifier = Modifier.size(350.dp, 50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.blue)
                )
            ) {
                Text("Sign Up", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    navController.navigate("signin") {
                        popUpTo("second_screen") { inclusive = true }
                    }
                },
                modifier = Modifier.size(350.dp, 50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Sign In", color = colorResource(R.color.black1), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SecondScreenPreview() {
    // Previews cannot use NavController, so just preview a placeholder:
    Second_Screen(navController = androidx.navigation.compose.rememberNavController())
}
