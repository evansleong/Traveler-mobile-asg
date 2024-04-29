package com.example.travelerapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun AgencyHomeScreen(
    navController: NavController,
    loggedInUserName: String,
    soldPackagesCount: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val title = "Welcome, $loggedInUserName"
        ReuseComponents.TopBar(title = title, navController)

        Spacer(modifier = Modifier.height(20.dp))

        // Current date and sold packages statistic
        val currentDate = remember {
            SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
        }
        Text(
            text = "Today",
            modifier = Modifier.padding(bottom = 4.dp, start = 15.dp),
            fontSize = 14.sp // Smaller font size for "Today" text
        )
        Text(
            text = currentDate,
            modifier = Modifier.padding(bottom = 4.dp, start = 15.dp),
            fontSize = 18.sp // Larger font size for "current date" text
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "$soldPackagesCount Pkgs booked",
            modifier = Modifier
                .padding(bottom = 16.dp, start = 200.dp),
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )

        // Divider below the "pkgs booked" text
        Divider(
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Your travel package list",
            modifier = Modifier.padding(bottom = 10.dp, start = 15.dp),
            fontSize = 18.sp // Smaller font size for "Today" text
        )

        // User travel package list slider
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            // Horizontal list of user travel packages
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
//                items(userTravelPackages.take(2)) { packageItem ->
//                    PackageItemCard(packageItem = packageItem, onClick = { /* Handle package item click */ })
//                }
            }
            Button(
                onClick = {
                    navController.navigate(route = Screen.AgencyPackageList.route)
                },
                modifier = Modifier.align(Alignment.BottomEnd)
                    .height(90.dp)
                    .width(120.dp)
                    .padding(20.dp)
                    .align(Alignment.BottomEnd)
                    .offset(y = (-200).dp)
            ) {
                Text(
                    text = ">",
                    fontSize = 25.sp
                )
            }
        }
        ReuseComponents.NavBar(text = title, navController = navController)
    }
}

//@Composable
//fun PackageItemCard(packageItem: TravelPackage, onClick: () -> Unit) {
//    Box(
//        modifier = Modifier
//            .padding(end = 8.dp)
//            .background(Color.LightGray, RoundedCornerShape(8.dp))
//            .padding(8.dp)
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Image(
//                painter = painterResource(id = packageItem.imageResId),
//                contentDescription = null, // Provide content description if needed
//                modifier = Modifier
//                    .clickable(onClick = onClick) // Make the image clickable
//                    .size(100.dp)
//            )
//            Text(
//                text = packageItem.name,
//                modifier = Modifier.padding(top = 4.dp),
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//}


@Preview
@Composable
fun PreviewAgencyHomeScreen() {
    val loggedInUserName = "John Doe"
    val soldPackagesCount = 10
//    val userTravelPackages = listOf(
//        TravelPackage(name = "Package 1", imageResId = R.drawable.invoker),
//        TravelPackage(name = "Package 2", imageResId = R.drawable.invoker),
//        TravelPackage(name = "Package 3", imageResId = R.drawable.invoker)
//    )
    AgencyHomeScreen(
        navController = rememberNavController(),
        loggedInUserName = loggedInUserName,
        soldPackagesCount = soldPackagesCount,
//        userTravelPackages = userTravelPackages,
    )
}
