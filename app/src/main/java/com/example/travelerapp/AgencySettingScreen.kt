package com.example.travelerapp

import ReuseComponents
import android.content.Context
import android.media.Image
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.travelerapp.viewModel.AgencyViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID


@Composable
fun AgencySettingScreen(
    navController: NavController,
    context: Context,
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) ->Unit,
    viewModel: AgencyViewModel
) {
    val loggedInAgency = viewModel.loggedInAgency

    val changeName = remember {
        mutableStateOf(TextFieldValue())
    }
    val changePw = remember {
        mutableStateOf(TextFieldValue())
    }
    var changeImgUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var isImageUploadInProgress by remember { mutableStateOf(false) }

    val darkThemeState = remember {
        mutableStateOf(darkTheme)
    }

    var dropCheck = remember {
        mutableStateOf(false)
    }

    var dropChose = remember {
        mutableStateOf("English")
    }

    LaunchedEffect(loggedInAgency) {
        viewModel.loggedInAgency?.agencyPicture?.let {
            changeImgUri = Uri.parse(it)
        }
    }


    fun handleImageUpload(context: Context, imageUri: Uri?) {
        isImageUploadInProgress = true
        viewModel.uploadImage(
            context = context,
            imageUri = imageUri,
            onSuccess = { downloadUrl ->
                changeImgUri = Uri.parse(downloadUrl)
                isImageUploadInProgress = false
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



    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val title = "Settings"
        ReuseComponents.TopBar(
            title = title,
            navController,
            showBackButton = true,
            isAtSettingPage = true,
            showLogoutButton = true,
            onLogout = {
                navController.navigate(route = Screen.UserOrAdmin.route) {
                    popUpTo(Screen.UserOrAdmin.route) {
                        inclusive = true
                    }
                }
            })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(MaterialTheme.colorScheme.background),
//            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Text(text = title)
            item {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .border(
                            BorderStroke(2.dp, Color(0xffafafaf)),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .fillMaxWidth()
                        .height(90.dp),
                ) {
                    Surface (
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
//                                .background(color = Color.LightGray)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                            if (changeImgUri != null || readOldImageUri.isNotEmpty()) {
                            if (changeImgUri != null) {
//                                val displayImageUri = changeImgUri ?: Uri.parse(readOldImageUri)
                                Image(
                                    painter = rememberAsyncImagePainter(changeImgUri),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(86.dp)
                                        .clip(RoundedCornerShape(20)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                ReuseComponents.RoundImg(
                                    Modifier,
                                    painter = painterResource(R.drawable.blank_profile_picture_973460_1_1_1024x1024),
                                    contentDescription = null
                                )
                            }

                            Column(
                                modifier = Modifier.fillMaxSize()
                                    .clickable { pickImage(imagePicker) }
                            ) {
                                Text("Your Photo",
                                    modifier = Modifier.padding(start = 20.dp, bottom = 12.dp, top = 14.dp),
                                    style = TextStyle(fontSize = 18.sp)
                                )

                                Text(text = "Adding a profile picture makes\nyour profile more personalized.",
                                    modifier = Modifier
                                        .padding(start = 20.dp, bottom = 0.dp),
                                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Light, color = Color(0xff959595)))
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                navController.navigate(Screen.AgencyChangeUsername.route) {
                                    popUpTo(Screen.AgencyHome.route){
                                        inclusive = true
                                    }
                                }
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Change Username",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Arrow Icon",
                            tint = Color.Black
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                navController.navigate(Screen.AgencyChangePw.route) {
                                    popUpTo(Screen.AgencyHome.route){
                                        inclusive = true
                                    }
                                }
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Change Password",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Arrow Icon",
                            tint = Color.Black
                        )
                    }

                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){

                        Text(
                            text = "Dark Theme",
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                        )

                        Switch(
                            checked = darkThemeState.value,
                            onCheckedChange = {
                                darkThemeState.value = it
                                onDarkThemeChanged(it)
                            }
                        )
                    }

                    Button(
                        onClick = {
                            if(!isImageUploadInProgress) {
                                viewModel.editAgencyPicture(
                                    context = navController.context,
                                    db = Firebase.firestore,
                                    agencyId = loggedInAgency?.agencyId ?: "",
                                    newPicture = changeImgUri.toString()
                                )
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Image upload in progress", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Update Profile Picture")
                    }
                }
            }

        }
    }
    if( darkThemeState.value != darkThemeState.value){
        Toast.makeText(context,"Dark Theme ${darkThemeState.value}",Toast.LENGTH_SHORT).show()
    }


}

@Composable
@Preview
fun AgencySettingScreenPreview(){
    AgencySettingScreen(
        navController = rememberNavController(),
        context = LocalContext.current,
        darkTheme = false,
        onDarkThemeChanged = {},
        viewModel = AgencyViewModel()
    )
}