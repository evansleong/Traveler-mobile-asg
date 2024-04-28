package com.example.travelerapp

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun AgencyAddPackageScreen(
    navController: NavController,
    context: Context
) {
    val activity = context as Activity

    val tripPackageName = remember {
        mutableStateOf(TextFieldValue())
    }

    val tripPackageFees = remember {
        mutableStateOf(TextFieldValue())
    }

    val tripPackageDeposit = remember {
        mutableStateOf(TextFieldValue())
    }

    val tripPackageDesc = remember {
        mutableStateOf(TextFieldValue())
    }

    // Initialize a mutable state for the selected radio button option
    var selectedOption by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var dbHandler: DBHandler = DBHandler(context)

        val title = "Add Package"
        ReuseComponents.TopBar(title = title, navController, showBackButton = true)

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .width(350.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text(
                        text = "Trip Name"
                    )
                    TextField(
                        value = tripPackageName.value,
                        onValueChange = { tripPackageName.value = it},
                        placeholder = { Text(text = "Enter the trip name:")},
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                item {
                    Text(
                        text = "Trip Fees"
                    )
                    TextField(
                        value = tripPackageFees.value,
                        onValueChange = { tripPackageFees.value = it},
                        placeholder = { Text(text = "Enter the trip fees:")},
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                item {
                    Text(
                        text = "Deposit"
                    )
                    TextField(
                        value = tripPackageDeposit.value,
                        onValueChange = { tripPackageDeposit.value = it},
                        placeholder = { Text(text = "Enter the trip deposit:")},
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                item {
                    Text(
                        text = "Description"
                    )
                    TextField(
                        value = tripPackageDesc.value,
                        onValueChange = { tripPackageDesc.value = it},
                        placeholder = { Text(text = "Enter the trip desc:")},
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text("Select an option:")
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Row() {
                            RadioButton(
                                selected = selectedOption == "Option 1",
                                onClick = { selectedOption = "Option 1" }
                            )
                            Text(
                                modifier = Modifier
                                    .padding(13.dp),
                                text = "Option1"
                            )
                        }
                        Row() {
                            RadioButton(
                                selected = selectedOption == "Option 2",
                                onClick = { selectedOption = "Option 2" }
                            )
                            Text(
                                modifier = Modifier
                                    .padding(13.dp),
                                text = "Option2"
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                        Row() {
                            RadioButton(
                                selected = selectedOption == "Option 3",
                                onClick = { selectedOption = "Option 3" }
                            )
                            Text(
                                modifier = Modifier
                                    .padding(13.dp),
                                text = "Option3"
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                        Row() {
                            RadioButton(
                                selected = selectedOption == "Option 4",
                                onClick = { selectedOption = "Option 4" }
                            )
                            Text(
                                modifier = Modifier
                                    .padding(13.dp),
                                text = "Option4"
                            )
                        }
                    }
                }
                item {
                    // Save button
                    Button(
                        onClick = {
                            dbHandler.addNewTrip(
                                tripPackageName.value.text,
                                tripPackageFees.value.text.toDouble(),
                                tripPackageDeposit.value.text.toDouble(),
                                tripPackageDesc.value.text
                            )
                            Toast.makeText(context, "Trip Added to Database", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Package")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewAgencyAddPackageScreen() {
    AgencyAddPackageScreen(navController = rememberNavController(), context = LocalContext.current)
}
