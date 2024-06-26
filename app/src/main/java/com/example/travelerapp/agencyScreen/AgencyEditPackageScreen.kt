package com.example.travelerapp.agencyScreen

import ReuseComponents
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.travelerapp.DateUtils
import com.example.travelerapp.data.Trip
import com.example.travelerapp.screen.Screen
import com.example.travelerapp.ui.theme.CusFont3
import com.example.travelerapp.viewModel.TripViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgencyEditPackageScreen(
    navController: NavController,
    trip: Trip,
    context: Context,
    tripViewModel: TripViewModel
) {
    val db = Firebase.firestore
    val tripState = remember { mutableStateOf<Trip?>(null) }

    val selectedPackage = tripViewModel.selectedTripId

    var editedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var showConfirmDialog by remember { mutableStateOf(false) }

    val tripPackageDeptDate = rememberDatePickerState()
    val tripPackageRetDate = rememberDatePickerState()

    val deptDateMillisToLocalDate = tripPackageDeptDate.selectedDateMillis?.let {
        DateUtils().convertMillisToLocalDate(it)
    }

    val retDateMillisToLocalDate = tripPackageRetDate.selectedDateMillis?.let {
        DateUtils().convertMillisToLocalDate(it)
    }

    val deptDateToString = deptDateMillisToLocalDate?.let {
        DateUtils().dateToString(deptDateMillisToLocalDate)
    } ?: ""

    val retDateToString = retDateMillisToLocalDate?.let {
        DateUtils().dateToString(retDateMillisToLocalDate)
    } ?: ""

    var selectedOptions by remember { mutableStateOf(trip.options.toMutableSet()) }

    var isInputValid by remember { mutableStateOf(false) }


    LaunchedEffect(selectedPackage) {
        tripViewModel.readSingleTrip(db, selectedPackage.toString()) { trip ->
            tripState.value = trip
        }
    }

    LaunchedEffect(trip) {
        selectedOptions = trip.options.toMutableSet()

    }

    fun showConfirmationDialog() {
        showConfirmDialog = true
    }

    fun handleSaveButtonClick() {
        showConfirmationDialog()
    }

    fun calculateTripLength(
        departureMillis: Long?,
        returnMillis: Long?,
        existingTripLength: String
    ): String {
        if (departureMillis != null && returnMillis != null) {
            val departureDate = DateUtils().convertMillisToLocalDate(departureMillis)
            val returnDate = DateUtils().convertMillisToLocalDate(returnMillis)
            val days = ChronoUnit.DAYS.between(departureDate, returnDate) + 1
            val nights = if (days > 1) days - 1 else 0
            return if (days < 2) "1 DAY TRIP" else "$days DAYS $nights NIGHTS"
        }
        return existingTripLength // Default to "invalid" if dates are not selected
    }


    tripState.value?.let { trip ->
        var isEditing by remember { mutableStateOf(false) }
        var editedTripName by remember { mutableStateOf(trip.tripName) }
        var editedTripFees by remember { mutableStateOf(trip.tripFees) }
        var editedTripDeposit by remember { mutableStateOf(trip.tripDeposit) }
        var editedTripDesc by remember { mutableStateOf(trip.tripDesc) }
        var editedDepartureDate by remember { mutableStateOf(trip.depDate) }
        var editedReturnDate by remember { mutableStateOf(trip.retDate) }
        var readTripLength by remember { mutableStateOf(trip.tripLength) }
        var editedTripLength by remember {
            mutableStateOf(
                calculateTripLength(
                    tripPackageDeptDate.selectedDateMillis,
                    tripPackageRetDate.selectedDateMillis,
                    readTripLength
                )
            )
        }
//        val editedTripLength = remember { mutableStateOf(calculateTripLength(tripPackageDeptDate.selectedDateMillis, tripPackageRetDate.selectedDateMillis)) }
        var editedOptions by remember { mutableStateOf(trip.options.joinToString(separator = "\n")) }
        var readOldImageUri by remember { mutableStateOf(trip.tripUri) }

        LaunchedEffect(
            tripPackageDeptDate.selectedDateMillis,
            tripPackageRetDate.selectedDateMillis
        ) {
            editedTripLength = calculateTripLength(
                tripPackageDeptDate.selectedDateMillis,
                tripPackageRetDate.selectedDateMillis,
                readTripLength
            )
        }

        fun validateInputFields() {
            isInputValid = (
                    editedTripName.isNotBlank() &&
                            editedTripDesc.isNotBlank() &&
                            editedTripFees > 0 &&
                            editedTripDeposit > 0 &&
                            editedDepartureDate.isNotBlank() &&
                            editedReturnDate.isNotBlank() &&
                            selectedOptions.isNotEmpty()
                    )
        }

        LaunchedEffect(
            editedTripName, editedTripDesc, editedTripFees, editedTripDeposit,
            editedDepartureDate, editedReturnDate, selectedOptions
        ) {
            validateInputFields()
        }

        fun handleImageUpload(context: Context, imageUri: Uri?) {
            tripViewModel.uploadImage(
                context = context,
                imageUri = imageUri,
                onSuccess = { downloadUrl ->
                    editedImageUri = Uri.parse(downloadUrl)
                },
                onFailure = { exception ->
                    Log.e("ImageUpload", "Error uploading image: ${exception.message}")
                }
            )
        }

        val imagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            handleImageUpload(context, uri)
        }

        fun pickImage(imagePicker: ActivityResultLauncher<String>) {
            imagePicker.launch("image/*")
        }

        LaunchedEffect(
            tripPackageDeptDate.selectedDateMillis,
            tripPackageRetDate.selectedDateMillis
        ) {
            editedTripLength = calculateTripLength(
                tripPackageDeptDate.selectedDateMillis,
                tripPackageRetDate.selectedDateMillis,
                readTripLength
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val title = "Edit ${trip.tripName}"
            ReuseComponents.TopBar(
                title = title,
                navController,
                showBackButton = true,
                showLogoutButton = true,
                isAtSettingPage = true,
                onLogout = {
                    navController.navigate(route = Screen.UserOrAdmin.route) {
                        popUpTo(Screen.UserOrAdmin.route) {
                            inclusive = true
                        }
                    }
                })

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { pickImage(imagePicker) }
                    .background(Color.Gray)
            ) {
                val displayImageUri = editedImageUri ?: readOldImageUri
                Image(
                    painter = rememberAsyncImagePainter(displayImageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
                IconButton(
                    onClick = {
                        pickImage(imagePicker)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {}
            }
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .width(350.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(text = "Trip Name", fontWeight = FontWeight.Bold)
                        EditableStringFieldWithButton(
                            text = editedTripName,
                            onTextChanged = { editedTripName = it },
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        Text(text = "Trip Description", fontWeight = FontWeight.Bold)
                        EditableStringFieldWithButton(
                            text = editedTripDesc,
                            onTextChanged = { editedTripDesc = it }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        Text(text = "Trip Fees", fontWeight = FontWeight.Bold)
                        EditableDoubleFieldWithButton(
                            value = editedTripFees,
                            onValueChanged = { editedTripFees = it }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        Text(text = "Trip Deposit", fontWeight = FontWeight.Bold)
                        EditableDoubleFieldWithButton(
                            value = editedTripDeposit,
                            onValueChanged = { editedTripDeposit = it }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Departure Date", fontWeight = FontWeight.Bold)
                        EditableDateFieldWithButton(
                            date = editedDepartureDate,
                            onDateChanged = { newDate ->
                                editedDepartureDate = newDate
                                editedTripLength = calculateTripLength(
                                    tripPackageDeptDate.selectedDateMillis,
                                    tripPackageRetDate.selectedDateMillis,
                                    readTripLength
                                )
                            },
                            datePickerState = tripPackageDeptDate,
                            dateToString = deptDateToString
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Return Date", fontWeight = FontWeight.Bold)
                        EditableDateFieldWithButton(
                            date = editedReturnDate,
                            onDateChanged = { editedReturnDate = it },
                            datePickerState = tripPackageRetDate,
                            dateToString = retDateToString
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Trip Length", fontWeight = FontWeight.Bold)
                        Box {
                            TextField(
                                value = editedTripLength,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Options (Select at least 1):", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        val allOptions = listOf(
                            "Included breakfast",
                            "Free Parking",
                            "Travel Insurance",
                            "Tipping and Taxes",
                            "Full Board Meals",
                            "Airport Transport"
                        )
                        allOptions.forEach { option ->
                            OptionsCheckbox(
                                option,
                                selectedOptions,
                                trip,
                                onOptionCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        selectedOptions.add(option)
                                    } else {
                                        selectedOptions.remove(option)
                                    }
                                })
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        ReuseComponents.CustomButton(
                            text = "Save Changes",
                            onClick = {
                                if (selectedOptions.isEmpty()) {
                                    selectedOptions =
                                        trip.options.toMutableSet() // Keep previous options
                                }

                                validateInputFields()

                                if (isInputValid) {
                                    handleSaveButtonClick()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please fill in all required fields",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }

                    item {
                        // Confirmation Dialog
                        if (showConfirmDialog) {
                            AlertDialog(
                                onDismissRequest = { showConfirmDialog = false },
                                title = {
                                    Text("Confirm Save")
                                },
                                text = {
                                    Text("Are you sure you want to edit this trip package?")
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            tripViewModel.editTrip(
                                                context = navController.context,
                                                db = Firebase.firestore,
                                                tripId = trip.tripId,
                                                newTripUri = editedImageUri?.toString()
                                                    ?: trip.tripUri,
                                                newTripName = editedTripName.uppercase(),
                                                newTripLength = editedTripLength,
                                                newTripFees = editedTripFees,
                                                newTripDeposit = editedTripDeposit,
                                                newTripDesc = editedTripDesc.uppercase(),
                                                newDeptDate = editedDepartureDate,
                                                newRetDate = editedReturnDate,
                                                newOptions = selectedOptions.toList(),
                                            )
                                            navController.popBackStack()
                                            Toast.makeText(
                                                context,
                                                "Trip Edited",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            showConfirmDialog = false
                                            navController.popBackStack()
                                        },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text("Edit")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showConfirmDialog = false },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditableStringFieldWithButton(
    text: String,
    onTextChanged: (String) -> Unit
) {
    var showError by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = text,
            onValueChange = {
                onTextChanged(it)
                showError = it.isBlank()
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
    }

    // Show error message if input is empty
    if (showError) {
        Text(
            text = "Cannot be blank",
            color = Color.Red,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun EditableDoubleFieldWithButton(
    value: Double,
    onValueChanged: (Double) -> Unit
) {
    var showError by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(String.format("%.2f", value)) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = text,
            onValueChange = {
                text = it
                val newValue = it.toDoubleOrNull()
                if (newValue != null) {
                    onValueChanged(newValue)
                    showError = false
                } else {
                    showError = true // Show error if parsing fails or input is empty
                }
            },
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(fontFamily = CusFont3),
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
    // Show error message if parsing fails or input is empty
    if (showError || text.isBlank()) {
        Text(
            text = "Invalid number",
            color = Color.Red,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableDateFieldWithButton(
    date: String,
    onDateChanged: (String) -> Unit,
    datePickerState: DatePickerState,
    dateToString: String,
//    editedTripLength: (String) -> Unit,
) {
    var selectedDate by remember { mutableStateOf(date) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Update selectedDate when dateToString changes
    LaunchedEffect(dateToString) {
        if (dateToString.isNotEmpty()) {
            selectedDate = dateToString
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = selectedDate,
            onValueChange = { /* Do not handle value change here */ },
            shape = RoundedCornerShape(20.dp),
            readOnly = true,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                showDatePicker = true
            }
        ) {
            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
        }
        Spacer(modifier = Modifier.width(8.dp))
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        showDatePicker = false
                        onDateChanged(selectedDate)
                        // Update the selected date
//                        editedTripLength.val = calculateTripLength(
//                            tripPackageDeptDate.selectedDateMillis,
//                            tripPackageRetDate.selectedDateMillis
//                        )
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDatePicker = false }
                ) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}


@Composable
fun OptionsCheckbox(
    option: String,
    selectedOptions: MutableSet<String>,
    trip: Trip,
    onOptionCheckedChange: (Boolean) -> Unit
) {
    var isChecked by remember { mutableStateOf(option in selectedOptions) }

    LaunchedEffect(selectedOptions) {
        selectedOptions += trip.options
        isChecked = option in selectedOptions || option in trip.options
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = option,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        androidx.compose.material3.Checkbox(
            checked = isChecked,
//            onCheckedChange = { newCheckedState ->
//                isChecked = newCheckedState
//                if (newCheckedState) {
//                    selectedOptions += option // Add the option to the selected set
//                    selectedOptions.addAll(trip.options)
//                } else {
//                    selectedOptions.remove(option) // Remove the option from the selected set
//                }
//                onOptionCheckedChange()
//            }
            onCheckedChange = { newCheckedState ->
                isChecked = newCheckedState
                if (newCheckedState) {
                    selectedOptions += option // Add the option to the selected set
                } else {
                    selectedOptions.remove(option) // Remove the option from the selected set
                }
                onOptionCheckedChange(newCheckedState)
            }
        )
    }
    Divider()
}

fun parseDateFromString(dateString: String): LocalDate? {
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy")
    return try {
        LocalDate.parse(dateString, formatter)
    } catch (e: Exception) {
        // Handle parsing error if needed
        null
    }
}


