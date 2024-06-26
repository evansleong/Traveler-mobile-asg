package com.example.travelerapp.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travelerapp.normalUserScreen.AddPINScreen
import com.example.travelerapp.agencyScreen.AgencyAddPackageScreen
import com.example.travelerapp.agencyScreen.AgencyChangePwScreen
import com.example.travelerapp.agencyScreen.AgencyEditPackageScreen
import com.example.travelerapp.agencyScreen.AgencyForgotPwScreen
import com.example.travelerapp.agencyScreen.AgencyHomeScreen
import com.example.travelerapp.agencyScreen.AgencyLoginScreen
import com.example.travelerapp.agencyScreen.AgencyPackageDetail
import com.example.travelerapp.agencyScreen.AgencyPackageList
import com.example.travelerapp.agencyScreen.AgencySettingScreen
import com.example.travelerapp.agencyScreen.AgencySignUpScreen
import com.example.travelerapp.localDb.DBHandler
import com.example.travelerapp.normalUserScreen.EditReviewScreen
import com.example.travelerapp.normalUserScreen.HomeScreen
import com.example.travelerapp.normalUserScreen.LoginScreen
import com.example.travelerapp.normalUserScreen.PaymentScreen
import com.example.travelerapp.PickUserTypeScreen
import com.example.travelerapp.normalUserScreen.ReloadScreen
import com.example.travelerapp.normalUserScreen.ReviewScreen
import com.example.travelerapp.normalUserScreen.SettingsScreen
import com.example.travelerapp.normalUserScreen.SignUpScreen
import com.example.travelerapp.normalUserScreen.TransactionScreen
import com.example.travelerapp.normalUserScreen.UserBookingTripScreen
import com.example.travelerapp.normalUserScreen.UserChangePwScreen
import com.example.travelerapp.normalUserScreen.UserForgotPwScreen
import com.example.travelerapp.normalUserScreen.UserPackageListScreen
import com.example.travelerapp.normalUserScreen.UserPackagePurchased
import com.example.travelerapp.normalUserScreen.UserPurchasedPackageDetail
import com.example.travelerapp.normalUserScreen.UserViewTripScreen
import com.example.travelerapp.normalUserScreen.WalletScreen
import com.example.travelerapp.data.Trip
import com.example.travelerapp.viewModel.AgencyViewModel
import com.example.travelerapp.viewModel.PurchasedTripViewModel
import com.example.travelerapp.viewModel.ReviewViewModel
import com.example.travelerapp.viewModel.TransactionViewModel
import com.example.travelerapp.viewModel.TripViewModel
import com.example.travelerapp.viewModel.WalletViewModel
import com.example.travelerapp.viewModel.UserViewModel

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    dbHandler: DBHandler,
    darkTheme: Boolean,
    onDarkThemeChanged:(Boolean)->Unit
) {
    val agencyViewModel: AgencyViewModel = viewModel()
    val tripViewModel: TripViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val walletViewModel: WalletViewModel = viewModel()
    val reviewViewModel: ReviewViewModel = viewModel()
    val transactionViewModel: TransactionViewModel = viewModel()
    val purchasedTripViewModel: PurchasedTripViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.UserOrAdmin.route
//        startDestination = Screen.Login.route
    ) {
        composable(
            route = Screen.UserOrAdmin.route
        ){
            PickUserTypeScreen(navController)
        }
        composable(
            route = Screen.Home.route
        ){
            HomeScreen(navController, tripViewModel = tripViewModel)
        }
        composable(
            route = Screen.Login.route
        ){
            LoginScreen(navController, context = LocalContext.current, userViewModel, walletViewModel)
        }
        composable(
            route = Screen.Signup.route
        ){
            SignUpScreen(navController, context = LocalContext.current, dbHandler, userViewModel, walletViewModel)
        }
        composable(
            route = Screen.UserForgotPw.route
        ){
            UserForgotPwScreen(navController, context = LocalContext.current, userViewModel)
        }
        composable(
            route = Screen.UserChangePw.route
        ){
            UserChangePwScreen(navController, context = LocalContext.current, userViewModel)
        }
        composable(
            route = Screen.AddPIN.route
        ){
            AddPINScreen(navController, context = LocalContext.current, walletViewModel)
        }
        composable(
            route = Screen.Review.route
        ){
            ReviewScreen(navController, context = LocalContext.current, userViewModel, reviewViewModel, transactionViewModel)
        }
        composable(
             route = Screen.EditReview.route
        ){ backStackEntry ->
            EditReviewScreen(navController, context = LocalContext.current, reviewViewModel, tripViewModel, userViewModel)
        }
        composable(
            route = Screen.UserDisplayPackageList.route
        ){
            UserPackageListScreen(navController, context = LocalContext.current , tripViewModel)
        }
        composable(
            route = Screen.UserViewTrip.route
        ){
            UserViewTripScreen(navController, context = LocalContext.current , tripViewModel)
        }
        composable(
            route = Screen.UserBookingTripScreen.route
        ){
            UserBookingTripScreen(navController, context = LocalContext.current , tripViewModel)
        }
        composable(
            route = Screen.UserPackagePurchased.route
        ){
            UserPackagePurchased(navController, context = LocalContext.current,userViewModel,tripViewModel,purchasedTripViewModel)
        }
        composable(
            route = Screen.UserPackageDetails.route
        ){
            UserPurchasedPackageDetail(navController, context = LocalContext.current, tripViewModel, purchasedTripViewModel)
        }
        composable(
            route = Screen.Payment.route
        ){
            PaymentScreen(navController, context = LocalContext.current, tripViewModel, walletViewModel, transactionViewModel,purchasedTripViewModel)
        }
        composable(
            route = Screen.Wallet.route
        ){
            WalletScreen(navController, context = LocalContext.current, walletViewModel, transactionViewModel)
        }
        composable(
            route = Screen.Transaction.route
        ) {
            TransactionScreen(navController, context = LocalContext.current, transactionViewModel)
        }
        composable(
            route = Screen.Reload.route
        ) {
            ReloadScreen(navController, context = LocalContext.current, walletViewModel, transactionViewModel)
        }
        composable(
            route = Screen.Settings.route
        ) {
            SettingsScreen(navController,context = LocalContext.current,darkTheme = darkTheme,onDarkThemeChanged = onDarkThemeChanged, userViewModel)
        }
        composable(
            route = Screen.AgencyLogin.route
        ){
            AgencyLoginScreen(navController, context = LocalContext.current, viewModel = agencyViewModel)
        }
        composable(
            route = Screen.AgencySignup.route
        ){
            AgencySignUpScreen(navController, context = LocalContext.current, viewModel = agencyViewModel)
        }
        composable(
            route = Screen.AgencyChangePw.route
        ){
            AgencyChangePwScreen(navController, context = LocalContext.current, agencyViewModel)
        }
        composable(
            route = Screen.AgencyForgotPw.route
        ) {
            AgencyForgotPwScreen(navController, context = LocalContext.current, viewModel = agencyViewModel
            )
        }
        composable(
            route = Screen.AgencyHome.route
        ){
            AgencyHomeScreen(navController, viewModel = agencyViewModel, tripViewModel = tripViewModel)
        }
        composable(
            route = Screen.AgencyAddPackage.route
        ) {
            AgencyAddPackageScreen(navController = navController, context = LocalContext.current, viewModel = agencyViewModel, tripViewModel = tripViewModel)
        }
        composable(
            route = Screen.AgencyPackageList.route
        ) {
            AgencyPackageList(navController, context = LocalContext.current, viewModel = agencyViewModel, tripViewModel = tripViewModel)
        }
        composable(
            route = Screen.AgencyPackageDetail.route
        ) {
            AgencyPackageDetail(navController, context = LocalContext.current, tripViewModel = tripViewModel)
        }
        composable(
            route = Screen.AgencyEditPackage.route
        ) {
            AgencyEditPackageScreen(navController, trip = Trip(), context = LocalContext.current, tripViewModel = tripViewModel)
        }
        composable(
            route = Screen.AgencySetting.route
        ) {
            AgencySettingScreen(navController, context = LocalContext.current, darkTheme = darkTheme, onDarkThemeChanged = onDarkThemeChanged, viewModel = agencyViewModel,
            )
        }
    }
}
