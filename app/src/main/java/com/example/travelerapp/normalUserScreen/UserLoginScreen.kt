package com.example.travelerapp.normalUserScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.travelerapp.R
import com.example.travelerapp.data.User
import com.example.travelerapp.data.Wallet
import com.example.travelerapp.localDb.DBHandler
import com.example.travelerapp.screen.Screen
import com.example.travelerapp.viewModel.WalletViewModel
import com.example.travelerapp.viewModel.UserViewModel
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase

@Composable
fun LoginScreen(
    navController: NavController,
    context: Context,
    userViewModel: UserViewModel,
    walletViewModel: WalletViewModel
) {
//    val lsContext: Context = this
    val db = Firebase.firestore

    val logInEmail = remember {
        mutableStateOf(TextFieldValue())
    }

    val logInPw = remember {
        mutableStateOf(TextFieldValue())
    }

    var rememberMeChecked by rememberSaveable { mutableStateOf(userViewModel.getLoginDetails(context) != null) }

    fun clearSavedLoginDetails() {
        userViewModel.clearSavedLoginDetails(context)
    }

    val users = remember {
        mutableStateOf((emptyList<User>()))
    }

    // Check if the user is already logged in
    LaunchedEffect(Unit) {
        userViewModel.readUData(db) { userList ->
        users.value = userList
        }

        val loginDetails = userViewModel.getLoginDetails(context)
        if (loginDetails != null) {
            val (email, password) = loginDetails
            logInEmail.value = TextFieldValue(email)
            logInPw.value = TextFieldValue(password)
            val loginSuccessful = userViewModel.checkULoginCred(email, password, users.value)
            if (loginSuccessful != null) {
                userViewModel.loggedInUser = loginSuccessful
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }
                }
            }
        }
    }




    val walletList = remember { mutableStateOf(emptyList<Wallet>()) }

    walletViewModel.readWallets(db) { wallet ->
        walletList.value = wallet
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(350.dp)
                .height(600.dp)
                .background(Color.LightGray, RoundedCornerShape(16.dp))
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .padding(8.dp)
                    .size(40.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.clickable {
                        navController.navigate(route = Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                        }
                    },
                    text = "Log In",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Email",
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = logInEmail.value.text,
                    onValueChange = {
                        logInEmail.value = logInEmail.value.copy(text = it)
                    },
                    shape = RoundedCornerShape(16.dp),
                    label = { BasicText(text = "Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Password",
                    modifier = Modifier.align(Alignment.Start),
                )

                Spacer(modifier = Modifier.height(10.dp))

                var passwordVisible by rememberSaveable { mutableStateOf(false) }
                TextField(
                    value = logInPw.value.text,
                    onValueChange = {
                        logInPw.value = logInPw.value.copy(text = it)
                    },
                    shape = RoundedCornerShape(16.dp),
                    label = { BasicText(text = "Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password,),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            R.drawable.visibility
                        else R.drawable.visibility_off

                        // Please provide localized description for accessibility services
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = ImageVector.vectorResource(id = image), description)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rememberMeChecked,
                            onCheckedChange = { isChecked ->
                                rememberMeChecked = isChecked
                                if (!isChecked) {
                                    clearSavedLoginDetails()
                                } },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF5DB075))
                        )
                        Text("Remember me")
                    }
                }

                ReuseComponents.CustomButton(
                    text = "Login",
                    onClick = {
                        val email = logInEmail.value.text
                        val password = logInPw.value.text
                        val loginSuccessful = userViewModel.checkULoginCred(email, password, users.value)


                        if (loginSuccessful != null) {
                            userViewModel.loggedInUser = loginSuccessful
                            if (rememberMeChecked) {
                                userViewModel.saveLoginDetails(context, email, password)
                            }

                            val wallet = walletViewModel.checkWallet(userViewModel.loggedInUser!!.userId, walletList.value)

                            if (wallet != null){
                                walletViewModel.userWallet = wallet
                                if(wallet.walletPin != "null"){
                                    Toast.makeText(context, "Login Up Successful", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) {
                                            inclusive = true
                                        }
                                    }
                                }else{
                                    Toast.makeText(context, "Add Pin", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.AddPIN.route) {
                                        popUpTo(Screen.AddPIN.route) {
                                            inclusive = false
                                        }
                                    }
                                }
                            } else{
                                Toast.makeText(context, "wallet null", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }

//                        if(checked.value){
//                            if(logInEmail.value != "" && logInPw.value != ""){
//                        val emailTemp = logInEmail.getValueAsString()
//                        val pwTemp = logInPw.getValueAsString()
//                        val userExst = viewModel.checkULC(emailTemp,pwTemp,users.value)
////                        val userExst = dbHandler.getUserByEmailNPw(emailTemp, pwTemp)
//
//                        if(userExst!=null) {
//                            navController.navigate(route = Screen.Home.route) {
//                                popUpTo(Screen.Home.route) {
//                                    inclusive = true
//                                }
//                            }
////                        navController.navigate(route = Screen.AddPIN.route) {
////                            popUpTo(Screen.AddPIN.route) {
////                                inclusive = true
////                            }
//                        }
//                        }
//                      }

                )

//                Text(
//                    text = "Don't have an account yet? Sign up now",
//                    color = Color.Blue,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .padding(top = 16.dp)
//                        .clickable {
//                            // Navigate to the signup screen
//                            navController.navigate(Screen.Signup.route) {
//                                popUpTo(Screen.Signup.route) {
//                                    inclusive = true
//                                }
//                            }
//                        }
//                )

                val annotatedString = buildAnnotatedString {
                    append("Don't have an account yet? ")
                    pushStringAnnotation(tag = "SIGNUP", annotation = "Sign up now")
                    withStyle(style = SpanStyle(color = Color.Blue, fontSize = 14.sp, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                        append("Sign up now")
                    }
                    pop()
                }

                Text(
                    text = annotatedString,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clickable {
                            annotatedString.getStringAnnotations(tag = "SIGNUP", start = 0, end = annotatedString.length).firstOrNull()?.let { _ ->
                                navController.navigate(Screen.Signup.route) {
                                    popUpTo(Screen.Signup.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                )
                Text(
                    text = "Forgot Password?",
                    color = Color.Blue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clickable {
                            navController.navigate(Screen.UserForgotPw.route) {
                                popUpTo(Screen.Signup.route) {
                                    inclusive = true
                                }
                            }
                        }
                )
            }
        }
    }
}

@Composable
@Preview
fun LoginScreenPreview(){
    val context = LocalContext.current
    val DBH = DBHandler(context)
    LoginScreen(
        navController = rememberNavController(),
        context = LocalContext.current,
        userViewModel = UserViewModel(),
        walletViewModel = WalletViewModel()
    )
}