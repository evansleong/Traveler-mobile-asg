package com.example.travelerapp

import ReuseComponents.TopBar
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.travelerapp.data.PieChartInput
import com.example.travelerapp.data.Test
import com.example.travelerapp.data.Trip
import com.example.travelerapp.ui.theme.CusFont3
import com.example.travelerapp.ui.theme.blueGray
import com.example.travelerapp.ui.theme.brightBlue
import com.example.travelerapp.ui.theme.gray
import com.example.travelerapp.ui.theme.green
import com.example.travelerapp.ui.theme.orange
import com.example.travelerapp.ui.theme.purple
import com.example.travelerapp.ui.theme.redOrange
import com.example.travelerapp.ui.theme.white
import com.example.travelerapp.viewModel.AgencyViewModel
import com.example.travelerapp.viewModel.TripViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.math.PI
import kotlin.math.atan2

@Composable
fun AgencyHomeScreen(
    navController: NavController,
    viewModel: AgencyViewModel,
    tripViewModel: TripViewModel
) {
    val db = Firebase.firestore

    val isLoggedIn = remember { mutableStateOf(true) }
    val loggedInAgency = viewModel.loggedInAgency

    val top3TripsState = remember { mutableStateOf<List<Trip>>(emptyList()) }
    val tripListState = remember { mutableStateOf<List<Trip>>(emptyList()) }
    val totalUsersState = remember { mutableStateOf(0) }

    val purchasedTripsState = remember { mutableStateOf<List<Trip>>(emptyList()) }

    LaunchedEffect(key1 = true) {
        tripViewModel.readTrip(db) { trips ->
            val filteredTrips = trips.filter { trip ->
                trip.agencyUsername == loggedInAgency?.agencyUsername
            }
            // Update the tripListState with the fetched trips
            tripListState.value = filteredTrips
        }

        tripViewModel.readPurchasedTrips(db, loggedInAgency?.agencyUsername ?: "") { totalUsers ->
            totalUsersState.value = totalUsers
        }

        tripViewModel.readTripsWithBookingCount(db, loggedInAgency?.agencyUsername ?: "") { trips ->
            tripListState.value = trips
            val sortedTrips = trips.sortedByDescending { it.noOfUserBooked }
            top3TripsState.value = sortedTrips.take(3)
        }

//        tripViewModel.readPurchasedTripsForPieChart(db, loggedInAgency?.agencyUsername ?: "") { pieChartData ->
//            pieChartDataState.value = pieChartData
//        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Welcome, ${loggedInAgency?.agencyUsername}",
                navController,
                showLogoutButton = true,
                isAgencySide = true,
                onLogout = {
                    navController.navigate(route = Screen.UserOrAdmin.route) {
                        popUpTo(Screen.UserOrAdmin.route) {
                            inclusive = true
                        }
                    }
                }
            )
        },
        bottomBar = {
            ReuseComponents.AgencyNavBar(text = "AgencyHome", navController = navController)
        },
        floatingActionButtonPosition = FabPosition.End
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(40f / 9f)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(R.drawable.traveler_banner),
                        contentDescription = "Today's Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

            val currentDate = remember {
                SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
            }
            Text(
                text = "Today",
                modifier = Modifier.padding(bottom = 4.dp, start = 15.dp),
                fontSize = 14.sp,
            )
            Text(
                text = currentDate,
                modifier = Modifier.padding(bottom = 4.dp, start = 15.dp),
                fontSize = 18.sp // Larger font size for "current date" text
            )

            Spacer(modifier = Modifier.height(30.dp))


            // Top 3 most booked trips
            Text(
                text = "Top 3 Most Booked Trips",
                modifier = Modifier.padding(bottom = 8.dp, start = 15.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CusFont3
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                if (top3TripsState.value.isEmpty()) {
                    item {
                        Text(
                            text = "No trips available...",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    itemsIndexed(top3TripsState.value) { index, trip ->
                        AgencyHomeTop3Item(
                            trip = trip,
                            navController = navController,
                            tripViewModel = tripViewModel,
                            rank = index + 1 // Pass the rank (1, 2, or 3)
                        )
                    }
                }
            }

//            Text(
//                text = " Pkgs booked: ${totalUsersState.value}",
//                modifier = Modifier
//                    .padding(bottom = 4.dp, start = 10.dp),
//                color = Color.DarkGray,
//                fontWeight = FontWeight.Bold,
//                fontSize = 25.sp
//            )

            // Divider below the "pkgs booked" text
//            Divider(
//                modifier = Modifier.padding(bottom = 5.dp)
//            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Your travel package list",
                    modifier = Modifier.padding(start = 15.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = CusFont3
                )

                // Navigate to Package List button
                if (tripListState.value.isNotEmpty()) {
                    IconButton(onClick = {
                        navController.navigate(route = Screen.AgencyPackageList.route)
                    }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Navigate to Package List",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    IconButton(onClick = {
                        navController.navigate(route = Screen.AgencyAddPackage.route)
                    }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Navigate to Add Package",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // User travel package list slider
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    )
                ) {
                    // Horizontal list of user travel packages
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        // Add Package button if no packages
                        if (tripListState.value.isEmpty()) {
                            item {
                                Text(
                                    text = "No Travel Packages Yet...",
                                    modifier = Modifier
                                        .padding(vertical = 50.dp, horizontal = 90.dp)
                                )
                            }
                        } else {
                            items(tripListState.value.take(3)) { trip ->
                                AgencyHomeTripItem(trip = trip, navController = navController, tripViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalCoilApi::class)
@Composable
fun AgencyHomeTripItem(
    trip: Trip,
    navController: NavController,
    tripViewModel: TripViewModel,
) {
//    val painter =
//        rememberAsyncImagePainter(ImageRequest.Builder
//            (LocalContext.current).data(data = trip.tripUri).apply(block = fun ImageRequest.Builder.() {
//            crossfade(true)
//            placeholder(R.drawable.loading)
//        }).build()
//        )
    val imageUrl = trip.tripUri
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(trip.tripUri.takeIf { it.isNotEmpty() })
            .crossfade(true)
            .build()
    )

    Card(
        modifier = Modifier
            .padding(15.dp)
            .width(200.dp)
            .clickable {
                tripViewModel.selectedTripId = trip.tripId
                navController.navigate(route = Screen.AgencyPackageDetail.route)
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 50.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(200.dp)
                .clickable {
                    tripViewModel.selectedTripId = trip.tripId
                    navController.navigate(route = Screen.AgencyPackageDetail.route)
                }
        ) {
            if (imageUrl.isEmpty()) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Image(
                    painter = painter,
                    contentDescription = trip.tripName,
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = trip.tripName,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = trip.tripLength,
                    fontSize = 13.sp,
                    fontFamily = CusFont3
                )
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun AgencyHomeTop3Item(
    trip: Trip,
    navController: NavController,
    tripViewModel: TripViewModel,
    rank: Int
) {
    val painter =
        rememberAsyncImagePainter(ImageRequest.Builder
            (LocalContext.current).data(data = trip.tripUri).apply(block = fun ImageRequest.Builder.() {
            crossfade(true)
            placeholder(R.drawable.loading)
        }).build()
        )

    // Determine the border color based on the rank
    val borderColor = when(rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color.Transparent // No border for other items or if rank is null
    }

    Card(
        modifier = Modifier
            .padding(15.dp)
            .width(200.dp)
            .border(
                width = 4.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                tripViewModel.selectedTripId = trip.tripId
                navController.navigate(route = Screen.AgencyPackageDetail.route)
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 23.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(200.dp)
                .clickable {
                    tripViewModel.selectedTripId = trip.tripId
                    navController.navigate(route = Screen.AgencyPackageDetail.route)
                }
        ) {
            Image(
                painter = painter,
                contentDescription = trip.tripName,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )

            // Add logos for top 3 trips
            when (rank) {
                1 -> {
                    Text(
                        text = "Top1",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFFFD700)) // Gold background color
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .align(Alignment.Start)
                    )
                }
                2 -> {
                    Text(
                        text = "Top2",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFC0C0C0)) // Silver background color
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .align(Alignment.Start)
                    )
                }
                3 -> {
                    Text(
                        text = "Top3",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFCD7F32)) // Bronze background color
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .align(Alignment.Start)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp)
            ) {
                Text(
                    text = trip.tripName,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "No of user booked: ${trip.noOfUserBooked}",
                    fontSize = 13.sp,
                    fontFamily = CusFont3
                )
            }
        }
    }
}



@Preview
@Composable
fun PreviewAgencyHomeScreen() {
    AgencyHomeScreen(
        navController = rememberNavController(),
        viewModel = AgencyViewModel(),
        tripViewModel = TripViewModel()
    )
}
