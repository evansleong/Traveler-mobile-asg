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
        agencyUsername: String
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
            "agencyUsername" to agencyUsername
//        "isActive" to isChecked
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
        newTripName: String,
        newTripLength: String,
        newTripFees: Double,
        newTripDesc: String,
        newOptions: List<String>
    ) {
        val tripRef = db.collection("trips").document(tripId)

        val newData = hashMapOf(
            "tripName" to newTripName,
            "tripLength" to newTripLength,
            "tripFees" to newTripFees,
            "tripDesc" to newTripDesc,
            "options" to newOptions
        )

        tripRef
            .update(newData)
            .addOnSuccessListener {
                Log.d("Firestore", "Trip edited successfully")
                Toast.makeText(context, "Trip edited successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error editing trip: ${e.message}", e)
                Toast.makeText(context, "Error editing trip", Toast.LENGTH_SHORT).show()
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
}
