package com.cyril.replug.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cyril.replug.ui.screens.about.AboutScreen
import com.cyril.replug.ui.screens.add.AddScreen
import com.cyril.replug.ui.screens.add.RecycleScreen
import com.cyril.replug.ui.screens.add.SellScreen
import com.cyril.replug.ui.screens.auth.LoginScreen
import com.cyril.replug.ui.screens.auth.RegisterScreen
import com.cyril.replug.ui.screens.chat.ChatInboxScreen
import com.cyril.replug.ui.screens.chat.ChatScreen
import com.cyril.replug.ui.screens.home.HomeScreen
import com.cyril.replug.ui.screens.notifications.NotificationScreen
import com.cyril.replug.ui.screens.onboarding.Onboarding1Screen
import com.cyril.replug.ui.screens.onboarding.Onboarding2Screen
import com.cyril.replug.ui.screens.onboarding.Onboarding3Screen
import com.cyril.replug.ui.screens.payment.PaymentScreen
import com.cyril.replug.ui.screens.products.OrdersScreen
import com.cyril.replug.ui.screens.products.ProductDetailScreen
import com.cyril.replug.ui.screens.profile.ContactScreen
import com.cyril.replug.ui.screens.profile.EditProductScreen
import com.cyril.replug.ui.screens.profile.EditProfileScreen
import com.cyril.replug.ui.screens.profile.MyListingsScreen
import com.cyril.replug.ui.screens.profile.ProfileScreen
import com.cyril.replug.ui.screens.profile.RecycledProductsScreen
import com.cyril.replug.ui.screens.scaffold.ScaffoldScreen
import com.cyril.replug.ui.screens.search.SearchScreen
import com.cyril.replug.ui.screens.splash.SplashScreen
import com.cyril.replug.ui.screens.wishlist.WishlistScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH,
    enterTransition: () -> EnterTransition = {
        slideInHorizontally(animationSpec = tween(150)) // faster (default is slower)
    },
    exitTransition: () -> ExitTransition = {
        slideOutHorizontally(animationSpec = tween(150))
    }

) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ROUTE_SPLASH) {
            SplashScreen(navController)
        }
        composable(ROUTE_ONBOARDING1) {
            Onboarding1Screen(navController)
        }
        composable(ROUTE_HOME) {
            HomeScreen(navController)
        }
        composable(ROUTE_ABOUT) {
            AboutScreen(navController)
        }
        composable(ROUTE_LOGIN) {
            LoginScreen(navController)
        }
        composable(ROUTE_REGISTER) {
            RegisterScreen(navController)
        }
        composable(ROUTE_SCAFFOLD) {
            ScaffoldScreen(navController)
        }
        composable(ROUTE_ONBOARDING2) {
            Onboarding2Screen(navController)
        }
        composable(ROUTE_ONBOARDING3) {
            Onboarding3Screen(navController)
        }
        composable(ROUTE_SEARCH) {
            SearchScreen(navController)
        }
        composable(ROUTE_WISHLIST) {
            WishlistScreen(navController)
        }
        composable(ROUTE_PROFILE) {
            ProfileScreen(navController)
        }
        composable(ROUTE_EPROFILE) {
            EditProfileScreen(navController)
        }
        composable(ROUTE_ADD) {
            AddScreen(navController)
        }
        composable(ROUTE_RECYCLE) {
            RecycleScreen(navController)
        }
        composable(ROUTE_SELL) {
            SellScreen(navController)
        }
        composable(ROUTE_CONTACT) {
            ContactScreen(navController)
        }
        composable(ROUTE_ORDERS) {
            OrdersScreen(navController)
        }
        composable(ROUTE_NOTIFICATION) {
            NotificationScreen(navController)
        }
        composable(ROUTE_RECYCLED_PRODUCTS) {
            RecycledProductsScreen(navController)
        }
        composable(ROUTE_CHAT_INBOX) {
            ChatInboxScreen(navController)
        }
        composable(ROUTE_MY_LISTINGS) {
            MyListingsScreen(navController)
        }
        composable("editProduct/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            EditProductScreen(navController = navController, productId = productId)
        }
        composable("chat/{chatId}/{productName}") { backStackEntry ->
            ChatScreen(
                chatId       = backStackEntry.arguments?.getString("chatId") ?: "",
                productName  = backStackEntry.arguments?.getString("productName") ?: "",
                navController = navController
            )
        }
        composable("productDetail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""

            ProductDetailScreen(
                productId = productId,
                navController = navController,
                onBuyClick = {
                    navController.navigate("payment/$productId")
                }
            )
        }
        composable("payment/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""

            PaymentScreen(
                productId = productId,
                navController = navController,
                onPaymentDone = {
                    navController.popBackStack()
                }
            )
        }


















    }



}




