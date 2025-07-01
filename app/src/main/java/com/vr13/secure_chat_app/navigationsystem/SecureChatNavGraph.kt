package com.vr13.secure_chat_app.navigationsystem

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vr13.secure_chat_app.addcontact.AddContactScreen
import com.vr13.secure_chat_app.chatdetail.ChatDetailScreen
import com.vr13.secure_chat_app.splashscreen.SplashScreen
import com.vr13.secure_chat_app.chatlist.ChatListScreen
import com.vr13.secure_chat_app.search.SearchScreen
import com.vr13.secure_chat_app.signin.SignInScreen
import com.vr13.secure_chat_app.signup.EditProfileScreen
import com.vr13.secure_chat_app.signup.RegisterScreen
import com.vr13.secure_chat_app.ui.screens.Second_Screen

@Composable
fun SecureChatNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("second_screen") {
            Second_Screen(navController)
        }
        composable("signin") {
            SignInScreen(navController)
        }
        composable("signup") {
            RegisterScreen(navController)
        }
        composable("edit_profile") {
            EditProfileScreen(navController)
        }
        composable("chat_list") {
            ChatListScreen(navController)
        }
        composable(
            route = "chat_screen/{chatId}/{currentUserId}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("currentUserId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            val currentUserId = backStackEntry.arguments?.getString("currentUserId") ?: ""

            ChatDetailScreen(
                chatId = chatId,
                currentUserId = currentUserId,
                navController = navController
            )
        }
        composable("search") {
            SearchScreen(navController)
        }
        composable("add_contact") {
            AddContactScreen(navController)
        }
    }
}
