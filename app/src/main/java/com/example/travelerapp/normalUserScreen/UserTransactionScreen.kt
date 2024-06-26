package com.example.travelerapp.normalUserScreen

import ReuseComponents
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.travelerapp.viewModel.TransactionViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@Composable
fun TransactionScreen(
    navController: NavController,
    context: Context,
    transactionViewModel: TransactionViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val title = "Transaction Details"
        val db = Firebase.firestore
        val transaction = transactionViewModel.transaction

        ReuseComponents.TopBar(title = title, navController, showBackButton = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(color = Color.White)
        ) {
            if (transaction != null) {
                val utcDateTime = remember {
                    val instant = Instant.ofEpochMilli(transaction.created_at)
                    LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
                }
                val formattedDate = remember {
                    val formatter = DateTimeFormatter.ofPattern("HH:mm dd MMMM yyyy")
                    utcDateTime.format(formatter)
                }

                LazyColumn(
                    verticalArrangement = Arrangement.Top,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp, horizontal = 12.dp)
                        ){
                            Text(
                                text = if (transaction.operation == "Reload") "+RM${transaction.amount}" else "-RM${transaction.amount}",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Underline()
                    }
                    item {
                        newRow("Transaction Type", transaction.operation)
                    }
                    item {
                        newRow("Remarks", transaction.remarks)
                    }
                    item {
                        newRow("Description", transaction.description)
                    }
                    item {
                        newRow("Date/Time", formattedDate)
                    }
                    item {
                        newRow("Status", transaction.status)
                    }
                    item {
                        newRow("Transaction ID", transaction.id)
                    }
                }
            }
        }
        ReuseComponents.NavBar(text = title, navController = navController)
    }
}

@Composable
fun Underline() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .border(
                border = BorderStroke(1.dp, color = Color(0xFFADADAD)),
                shape = MaterialTheme.shapes.small
            )
    )
}

@Composable
fun newRow(subtitle: String, content: String) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ){
        Column (
            horizontalAlignment = Alignment.Start,
//            modifier = Modifier.weight(0.5f)
        ){
            Text(
                text = subtitle,
                fontSize = 16.sp,
                color = Color(0xFFADADAD)
            )
        }
        Column (
            horizontalAlignment = Alignment.End,
//            modifier = Modifier.weight(0.4f)
        ){
            Text(
                text = content,
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .border(
                border = BorderStroke(1.dp, color = Color(0xFFADADAD)),
                shape = MaterialTheme.shapes.small
            )
    )
}
@Composable
@Preview
fun TransactionScreenPreview(){
//    TransactionScreen(
//        navController = rememberNavController(),
//        context = LocalContext.current
//    )
}