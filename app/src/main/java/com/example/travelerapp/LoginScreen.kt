package com.example.travelerapp

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginScreen(
    navController: NavController
) {
    val checked = remember { mutableStateOf(false) }

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

                Button.RoundedOutlinedTextField(
                    value = "",
                    onValueChange = {},
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Password",
                    modifier = Modifier.align(Alignment.Start),
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button.RoundedOutlinedTextField(
                    value = "",
                    onValueChange = {},
                    shape = RoundedCornerShape(16.dp),
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
                            checked = checked.value,
                            onCheckedChange = { isChecked -> checked.value = isChecked },
                            colors = CheckboxDefaults.colors(checkedColor = Color.Green)
                        )
                        Text("I would like to receive your newsletter and other promotional information")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = checked.value,
                            onCheckedChange = { isChecked -> checked.value = isChecked },
                            colors = CheckboxDefaults.colors(checkedColor = Color.Green)
                        )
                        Text("Remember me")
                    }
                }

                Button.CustomButton(
                    text = "Login",
                    onClick = {
                        navController.navigate(route = Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
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
    LoginScreen(
        navController = rememberNavController()
    )
}