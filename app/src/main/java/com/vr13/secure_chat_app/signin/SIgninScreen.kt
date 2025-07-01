package com.vr13.secure_chat_app.signin

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vr13.secure_chat_app.R
import com.vr13.secure_chat_app.viewmodel.SignInViewModel
import com.vr13.secure_chat_app.viewmodel.SignInState

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel = viewModel()
) {
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var otp by remember { mutableStateOf("") }

    val signInState by viewModel.signInState.collectAsState()
    val context = LocalContext.current as Activity

    LaunchedEffect(signInState) {
        if (signInState is SignInState.Success) {
            navController.navigate("chat_list") {
                popUpTo("sign_in") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.black1))
            .padding(horizontal = 24.dp, vertical = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Welcome Back",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = {
                Text(
                    text = "Phone number",
                    color = Color(0xFFB0BEC5)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF263442),
                unfocusedContainerColor = Color(0xFF263442),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = otp,
            onValueChange = { otp = it },
            placeholder = {
                Text(
                    text = "OTP (One Time Password)",
                    color = Color(0xFFB0BEC5)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF263442),
                unfocusedContainerColor = Color(0xFF263442),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.sendOtp(phoneNumber.text.trim(), context)
            },
            modifier = Modifier.size(150.dp, 50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.blue),
            )
        ) {
            Text("Get-OTP", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(325.dp))

        Button(
            onClick = {
                viewModel.verifyOtp(otp)
            },
            modifier = Modifier.size(350.dp, 50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.blue),
            )
        ) {
            Text("Sign in", fontWeight = FontWeight.Bold)
        }

        if (signInState is SignInState.Loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = Color.White)
        }

        if (signInState is SignInState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (signInState as SignInState.Error).message,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
