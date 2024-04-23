package com.example.travelerapp

import ReuseComponents
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog


@Composable
fun ReloadScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val title = "Wallet"
        ReuseComponents.TopBar(title = title, navController)
        val balance = "100.00"
        val focusRequester = remember { FocusRequester() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.2f)
                .background(color = Color(0xFF5DB075))
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(bottom = 16.dp)
            ){
                Spacer(modifier = Modifier.padding(top = 240.dp))
                Column{
                    Text(
                        text = "Your Balance",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFCFCFC),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )
                    Text(
                        text = "MYR$balance",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 2.dp)
                    )
                }

            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .weight(0.8f)
                .padding(top = 32.dp)
                .padding(horizontal = 16.dp)
        ) {

            var amountInput by remember { mutableStateOf("") }
            val focusTextInput = remember { mutableStateOf(false) }
            val showDialog = remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = Color(0xFFA0D3AF))
            ) {
                EditNumberField(
                    value = amountInput, onValueChange = { amountInput = it }, modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .focusRequester(focusRequester)
                        .background(color = Color(0xFFE1E1E1))
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {

                    AmountButton(text = "RM100") {
                        amountInput = "100"
                    }
                    AmountButton(text = "RM200") {
                        amountInput = "200"
                    }
                    AmountButton(text = "RM300") {
                        amountInput = "300"
                    }

                }
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    AmountButton(text = "RM500") {
                        amountInput = "500"
                    }
                    AmountButton(text = "RM1000") {
                        amountInput = "1000"
                    }
                    Button(
                        onClick = {
                            focusTextInput.value = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color(0xFF5DB075),
                            containerColor = Color.White,
                        )
                    ) {
                        Text(text = "Other")
                    }
                    if (focusTextInput.value) {
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                            focusTextInput.value = false
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
            ) {
                Button(
                    onClick = { showDialog.value = true },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = Color(0xFFEDE9E9)
                    ),
                    shape = RoundedCornerShape(32.dp),
                    contentPadding = PaddingValues(vertical = 20.dp, horizontal = 88.dp),
                    modifier = Modifier
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Reload Wallet",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
                if (showDialog.value) {
                    PinInputDialog(
                        onContinue = {
                            //check the it value with the PIN here
                            showDialog.value = false
                        }) {
                        showDialog.value = false
                    }
                }
            }
        }
        ReuseComponents.NavBar(text = title, navController = navController)
    }
}

@Composable
fun PinInputDialog(onContinue: (String) -> Unit, onCancel: () -> Unit) {
    Dialog(onDismissRequest = { onCancel() }) {
        var pinValue by remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(12.dp)
                .clip(RoundedCornerShape(36.dp))
        ){
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row (
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(horizontal = 8.dp),
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.back_button),
                        contentDescription = "back_button",
                        modifier = Modifier.clickable {
                            onCancel()
                        }
                    )
                }
                Text(
                    text = "Enter Your PIN",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
                Column {
                    // Display the PIN value
                    Text(
                        text = pinValue,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE1E1E1))
                            .padding(horizontal = 8.dp)
                    )
                    // Button row for 0-9 and backspace
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        ) {
                        NumberButtonRow(start = 1, end = 3) {
                            if (pinValue.length < 6) pinValue += it
                        }
                        NumberButtonRow(start = 4, end = 6) {
                            if (pinValue.length < 6) pinValue += it
                        }
                        NumberButtonRow(start = 7, end = 9) {
                            if (pinValue.length < 6) pinValue += it
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Button(
                                onClick = {
                                    if (pinValue.isNotEmpty()) {
                                        pinValue = pinValue.dropLast(1)
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFFB3B56C)),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.backspace),
                                    contentDescription = "backspace_button",
                                    tint = Color.Black,
                                    modifier = Modifier.padding(vertical = 16.dp),
                                )
                            }
                            Button(
                                onClick = { if (pinValue.length < 6) pinValue += "0" },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFFE1E1E1)),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "0",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Button(
                                onClick = { onContinue(pinValue) },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF36D100)),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Enter",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun NumberButtonRow(start: Int, end: Int, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in start..end) {
            Button(
                onClick = { onClick("$i") },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFE1E1E1)),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = "$i",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun AmountButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF5DB075))
    ) {
        Text(text)
    }
}

@Composable
fun EditNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier) {

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(text = "Enter your Reload Amount", color = Color.Black.copy(alpha = 0.32f), fontWeight = FontWeight.Light) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}
@Composable
@Preview
fun ReloadScreenPreview(){
    ReloadScreen(
        navController = rememberNavController()
    )
}