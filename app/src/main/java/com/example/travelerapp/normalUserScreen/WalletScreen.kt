package com.example.travelerapp.normalUserScreen

import ReuseComponents
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.travelerapp.data.Transaction
import com.example.travelerapp.localDb.DBHandler
import com.example.travelerapp.screen.Screen
import com.example.travelerapp.viewModel.TransactionViewModel
import com.example.travelerapp.viewModel.WalletViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WalletScreen(
    navController: NavController,
    context: Context,
    walletViewModel: WalletViewModel,
    transactionViewModel: TransactionViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF5DB075)),
    ) {
        val dbHandler: DBHandler = DBHandler(context)
        val wallet = walletViewModel.userWallet
        val db = Firebase.firestore

        val transactionList = remember { mutableStateOf(emptyList<Transaction>()) }

        transactionViewModel.readTxs(db){
            val filteredTx = it.filter { tx ->
                tx.user_id == wallet?.user_id
            }
            transactionList.value = filteredTx
        }

        val title = "Wallet"
        ReuseComponents.TopBar(title = title, navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.2f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(bottom = 16.dp)
            ){
                Spacer(modifier = Modifier.height(240.dp))
                Column{
                    Text(
                        text = "Your Balance",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFCFCFC),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )
                    if (wallet != null) {
                        Text(
                            text = "MYR${wallet.available}",
                            color = Color.White,
                            fontSize = 29.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = 2.dp)
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier =Modifier.padding(horizontal = 16.dp),
                ){
                    Button(
                        onClick = {
                            navController.navigate(route = Screen.Reload.route) {
                                popUpTo(Screen.Reload.route) {
                                    inclusive = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color(0xFF5DB075),
                            containerColor = Color.White,
                        ),
                        contentPadding = PaddingValues(0.dp),
                    ){
                        Text(
                            text = "+ RELOAD",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 2.dp)
                                .sizeIn(maxHeight = 20.dp)
                        )
                    }
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
                .weight(0.8f)
        ){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                stickyHeader {
                    Text(
                        text = "History",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp)
                            .background(MaterialTheme.colorScheme.background),
                    )
                }
                if (transactionList.value.isEmpty()) {
                    item {
                        Text(
                            text = "No transactions",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                } else {
                    items(transactionList.value) { transaction ->
                        val currentDate = Date(transaction.created_at)
                        val dateFormat = SimpleDateFormat("HH:mm dd MMMM yyyy", Locale.getDefault())
                        val formattedDate = dateFormat.format(currentDate)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    transactionViewModel.transaction = transaction
                                    navController.navigate(Screen.Transaction.route){
                                        popUpTo(Screen.Transaction.route){
                                            inclusive = true
                                        }
                                    }
                                },
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 8.dp)
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .background(MaterialTheme.colorScheme.background)
                                ){
                                    Column {
                                        Text(
                                            text = formattedDate,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = transaction.remarks,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = transaction.description,
                                        )
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        modifier = Modifier
                                            .padding(top = 16.dp)

                                    ){
                                        Text(
                                            text = if (transaction.operation == "Reload") "+ ${transaction.amount}" else "- ${transaction.amount}",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        ReuseComponents.NavBar(text = title, navController = navController)
    }
}

@Composable
@Preview
fun WalletScreenPreview(){
//    WalletScreen(
//        navController = rememberNavController(),
//        context = LocalContext.current,
//
//    )
}