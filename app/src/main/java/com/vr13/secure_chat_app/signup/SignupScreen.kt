package com.vr13.secure_chat_app.signup

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.vr13.secure_chat_app.R
import com.vr13.secure_chat_app.viewmodel.SignUpViewModel
import com.vr13.secure_chat_app.viewmodel.SignUpState

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: SignUpViewModel = viewModel()
) {
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var otp by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf(TextFieldValue("")) }

    val context = LocalContext.current

    val signUpState by viewModel.signUpState.collectAsState()

    // navigate on success
    LaunchedEffect(signUpState) {
        if (signUpState is SignUpState.Success) {
            navController.navigate("edit_profile") {
                popUpTo("signup") { inclusive = true }
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
            "Create your account",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))

        // user name
        TextField(
            value = userName,
            onValueChange = { userName = it },
            placeholder = {
                Text("Name", color = Color(0xFFB0BEC5))
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

        // phone number
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = {
                Text("Phone number", color = Color(0xFFB0BEC5))
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

        // otp
        TextField(
            value = otp,
            onValueChange = { otp = it },
            placeholder = {
                Text("OTP (One Time Password)", color = Color(0xFFB0BEC5))
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
                viewModel.sendOtp(phoneNumber.text.trim(), context as Activity)
            },
            modifier = Modifier.size(150.dp, 50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.blue))
        ) {
            Text("Get-OTP", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(325.dp))

        Button(
            onClick = {
                viewModel.verifyOtp(otp, userName.text.trim())
            },
            modifier = Modifier.size(350.dp, 50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.blue))
        ) {
            Text("Next", fontWeight = FontWeight.Bold)
        }

        if (signUpState is SignUpState.Loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = Color.White)
        }

        if (signUpState is SignUpState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (signUpState as SignUpState.Error).message,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}
