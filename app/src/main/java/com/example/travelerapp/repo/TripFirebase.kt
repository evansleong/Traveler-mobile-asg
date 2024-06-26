package com.example.travelerapp.repo

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.travelerapp.data.Trip
import com.google.firebase.firestore.FirebaseFirestore

class TripFirebase {
    fun addDataToFirestore(
        context: Context,
        db: FirebaseFirestore,
        tripId: String,
        tripPackageName: String,
        tripLength: String,
        tripPackageFees: Double,
        tripPackageDeposit: Double,
        tripPackageDesc: String,
        tripPackageDeptDate: String,
        tripPackageRetDate: String,
        uploadedImageUri: String?,
        selectedOption: List<String>,
        isAvailable: Int,
        noOfUserBooked: Int,
        agencyUsername: String,
        onSuccess: () -> Unit,
        agencyId: String
//    isChecked: Boolean
    ) {

        val tripData = hashMapOf(
            "tripId" to tripId,
            "tripName" to tripPackageName,
            "tripLength" to tripLength,
            "tripFees" to tripPackageFees,
            "tripDeposit" to tripPackageDeposit,
            "tripDesc" to tripPackageDesc,
            "depDate" to tripPackageDeptDate,
            "retDate" to tripPackageRetDate,
            "tripUri" to uploadedImageUri,
            "options" to selectedOption,
            "isAvailable" to isAvailable,
            "noOfUserBooked" to noOfUserBooked,
            "agencyUsername" to agencyUsername,
            "agencyId" to agencyId
        )

        db.collection("trips")
            .document(tripId)
            .set(tripData)
            .addOnSuccessListener {
                Log.d("Firestore", "Document added with ID: $tripId")
                Toast.makeText(
                    context,
                    "Trip added to Firestore with ID: $tripId",
                    Toast.LENGTH_SHORT
                ).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding document", e)
                Toast.makeText(context, "Error adding trip to Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    fun editTripInFirestore(
        context: Context,
        db: FirebaseFirestore,
        tripId: String,
        newImageUri: String,
        newTripName: String,
        newTripLength: String,
        newTripFees: Double,
        newTripDeposit: Double,
        newTripDesc: String,
        newDeptDate: String,
        newRetDate: String,
        newOptions: List<String>
    ) {
        val tripRef = db.collection("trips").document(tripId)

        val newData = hashMapOf(
            "tripUri" to newImageUri,
            "tripName" to newTripName,
            "tripLength" to newTripLength,
            "tripFees" to newTripFees,
            "tripDeposit" to newTripDeposit,
            "tripDesc" to newTripDesc,
            "depDate" to newDeptDate,
            "retDate" to newRetDate,
            "options" to newOptions
        )

        tripRef
            .update(newData)
            .addOnSuccessListener {
                Log.d("Firestore", "Trip edited successfully")
//                Toast.makeText(context, "Trip edited successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error editing trip: ${e.message}", e)
//                Toast.makeText(context, "Error editing trip", Toast.LENGTH_SHORT).show()
            }
    }

    fun deleteTripFromFirestore(
        db: FirebaseFirestore,
        tripId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("trips")
            .document(tripId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    // Function to read data from Firestore
    fun readDataFromFirestore(db: FirebaseFirestore, callback: (List<Trip>) -> Unit) {
        db.collection("trips")
            .get()
            .addOnSuccessListener { documents ->
                val trips = mutableListOf<Trip>()
                for (document in documents) {
                    try {
                        val trip: Trip = document.toObject(Trip::class.java)
                        trips.add(trip)
                    } catch (e: Exception) {
                        Log.e("Firestore", "Error converting document to Trip: ${e.message}")
                    }
                }
                callback(trips)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting documents: ${e.message}", e)
            }
    }

    fun updateAvailableAmount(
        db: FirebaseFirestore,
        available: Int,
        numPax: Int,
        tripId: String,
        callback: (Boolean) -> Unit
    ) {
        val balance: Int = available - numPax

        // First, get the current noOfUserBooked value
        db.collection("trips")
            .document(tripId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Retrieve the current noOfUserBooked value
                    val currentNumBooked = documentSnapshot.getLong("noOfUserBooked")?.toInt() ?: 0
                    val newNumBooked = currentNumBooked + numPax

                    // Update the document with the new values
                    db.collection("trips")
                        .document(tripId)
                        .update(
                            mapOf(
                                "isAvailable" to balance,
                                "noOfUserBooked" to newNumBooked
                            )
                        )
                        .addOnSuccessListener {
                            callback(true) // Success, document updated
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error updating document: ${e.message}", e)
                            callback(false)
                        }
                } else {
                    Log.e("Firestore", "Document not found")
                    callback(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting document: ${e.message}", e)
                callback(false)
            }
    }


    fun readSingleTripFromFirestore(
        db: FirebaseFirestore,
        tripId: String,
        callback: (Trip?) -> Unit
    ) {
        db.collection("trips")
            .document(tripId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    try {
                        val trip: Trip? = documentSnapshot.toObject(Trip::class.java)
                        callback(trip)
                    } catch (e: Exception) {
                        Log.e("Firestore", "Error converting document to Trip: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e("Firestore", "Document does not exist for tripId: $tripId")
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting document: ${e.message}", e)
                callback(null)
            }
    }

    fun readMultipleTripFromFirestore(
        db: FirebaseFirestore,
        tripIds: List<String>,
        callback: (List<Trip?>) -> Unit
    ) {
        val trips = mutableListOf<Trip?>()
        val totalTrips = tripIds.size
        var tripsRetrieved = 0

        for (tripId in tripIds) {
            db.collection("trips")
                .document(tripId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        try {
                            val trip: Trip? = documentSnapshot.toObject(Trip::class.java)
                            trips.add(trip)
                        } catch (e: Exception) {
                            Log.e("Firestore", "Error converting document to Trip: ${e.message}")
                            trips.add(null)
                        }
                    } else {
                        Log.e("Firestore", "Document does not exist for tripId: $tripId")
                        trips.add(null)
                    }
                    tripsRetrieved++
                    if (tripsRetrieved == totalTrips) {
                        callback(trips)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error getting document: ${e.message}", e)
                    tripsRetrieved++
                    if (tripsRetrieved == totalTrips) {
                        callback(trips)
                    }
                }
        }
    }

    fun addPurchasedTrip(
        db: FirebaseFirestore,
        context: Context,
        tripId: String,
        agencyUsername: String,
        agencyId: String,
        noPax: Int,
    ) {
        val tripData = hashMapOf(
            "tripId" to tripId,
            "agencyUsername" to agencyUsername,
            "agencyId" to agencyId,
            "noPax" to noPax
        )

        db.collection("purchasedTrips")
            .add(tripData)
            .addOnSuccessListener {
                Log.d("Firestore", "Document added to purchasedTrips with ID: $it.id")
                Toast.makeText(context, "PurchasedTrips added to Firestore with ID: $it.id", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding document", e)
                Toast.makeText(context, "Error adding purchasedTrips to Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to read the total number of purchased trips
    fun readPurchasedTrips(
        db: FirebaseFirestore,
        agencyUsername: String,
        agencyId: String,
        callback: (Int) -> Unit
    ) {
        db.collection("purchasedTrips")
            .whereEqualTo("agencyId", agencyId)
            .get()
            .addOnSuccessListener { documents ->
                var totalNoPax = 0 // Initialize the total number of passengers
                for (document in documents) {
                    val noPax = document.getLong("noPax")?.toInt() ?: 0
                    totalNoPax += noPax // Add the number of passengers to the total
                }
                callback(totalNoPax) // Pass the total number of passengers to the callback function
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting purchasedTrips: ${e.message}", e)
                callback(0) // If there's an error, pass 0 as the total number of passengers
            }
    }

//    fun readPurchasedTripsUserAndNoPax(
//        db: FirebaseFirestore,
//        tripId: String,
//        callback: (List<Pair<Int, String>>) -> Unit
//    ) {
//        db.collection("purchasedTrips")
//            .whereEqualTo("tripId", tripId)
//            .get()
//            .addOnSuccessListener { documents ->
//                val tripUserPairs = mutableListOf<Pair<Int, String>>()
//                val userIdMap = mutableMapOf<String, Int>()
//
//                for (document in documents) {
//                    val userId = document.getString("userName") ?: ""
//                    val noPax = document.getLong("noPax")?.toInt() ?: 0
//
//                    // Check if the user ID already exists in the map
//                    if (userIdMap.containsKey(userId)) {
//                        // If the user ID exists, add the number of pax to the existing count
//                        userIdMap[userId] = userIdMap.getValue(userId) + noPax
//                    } else {
//                        // If the user ID doesn't exist, add it to the map with the number of pax
//                        userIdMap[userId] = noPax
//                    }
//                }
//
//                // Convert the userIdMap entries to a list of Pair<Int, String>
//                tripUserPairs.addAll(userIdMap.entries.map { Pair(it.value, it.key) })
//
//                callback(tripUserPairs)
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Error getting purchased trip details: ${e.message}", e)
//                callback(emptyList())
//            }
//    }

    fun readPurchasedTripsUserAndNoPax(
        db: FirebaseFirestore,
        tripId: String,
        callback: (List<Pair<Int, String>>) -> Unit
    ) {
        db.collection("purchasedTrips")
            .whereEqualTo("tripId", tripId)
            .get()
            .addOnSuccessListener { documents ->
                val tripUserPairs = mutableListOf<Pair<Int, String>>()
                val userIds = mutableSetOf<String>()

                // First pass: collect userIds and create pairs with noPax and userId
                for (document in documents) {
                    val userId = document.getString("userId") ?: ""
                    val noPax = document.getLong("noPax")?.toInt() ?: 0

                    tripUserPairs.add(Pair(noPax, userId))
                    userIds.add(userId)
                }

                // Fetch userNames for the collected userIds
                if (userIds.isNotEmpty()) {
                    db.collection("User")
                        .whereIn("userId", userIds.toList())
                        .get()
                        .addOnSuccessListener { userDocuments ->
                            val userIdToUserNameMap = mutableMapOf<String, String>()

                            for (userDocument in userDocuments) {
                                val userId = userDocument.getString("userId") ?: ""
                                val userName = userDocument.getString("userName") ?: ""
                                userIdToUserNameMap[userId] = userName
                            }

                            // Replace userId with userName in tripUserPairs
                            val tripUserPairsWithNames = tripUserPairs.map { pair ->
                                val userName = userIdToUserNameMap[pair.second] ?: pair.second
                                Pair(pair.first, userName)
                            }

                            callback(tripUserPairsWithNames)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error getting user details: ${e.message}", e)
                            callback(emptyList())
                        }
                } else {
                    callback(tripUserPairs) // No userIds to look up
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting purchased trip details: ${e.message}", e)
                callback(emptyList())
            }
    }


    fun readTripsWithBookingCounts(db: FirebaseFirestore, agencyUsername: String, agencyId: String, onTripsRead: (List<Trip>) -> Unit) {
        db.collection("trips")
            .whereEqualTo("agencyId", agencyId)
            .get()
            .addOnSuccessListener { documents ->
                val trips = documents.map { document ->
                    document.toObject(Trip::class.java)
                }
                onTripsRead(trips)
            }
            .addOnFailureListener { exception ->
                Log.w("TripViewModel", "Error getting documents: ", exception)
            }
    }

}

