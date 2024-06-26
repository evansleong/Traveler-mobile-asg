package com.example.travelerapp.localDb

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import com.example.travelerapp.data.Review
import com.example.travelerapp.data.Trip
import com.example.travelerapp.data.User

class DBHandler(context: Context) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTripTable = """
            CREATE TABLE trips (
                trip_id TEXT PRIMARY KEY,
                trip_name TEXT NOT NULL,
                trip_length TEXT NOT NULL,               
                trip_fees REAL NOT NULL,
                trip_deposit REAL NOT NULL,
                trip_desc TEXT NOT NULL,
                trip_dep_date TEXT NOT NULL,
                trip_ret_date TEXT NOT NULL,
                trip_image_uri TEXT NOT NULL,
                trip_options TEXT NOT NULL,
                is_available INTEGER,
                no_user_booked INTEGER,
                agency_username TEXT NOT NULL
            )
            """

        val createUserTable = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY,
                username TEXT NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL,
                is_user INTEGER DEFAULT 1 CHECK (is_user IN (0,1)) NOT NULL
            )
        """
        val createWalletTable = """
                CREATE TABLE wallets (
                id TEXT PRIMARY KEY,
                available REAL DEFAULT 0 NOT NULL,
                frozen REAL DEFAULT 0 NOT NULL,
                walletPin TEXT,
                user_id TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """
        val createTransactionTable = """
            CREATE TABLE transactions (
                id TEXT PRIMARY KEY,
                operation TEXT CHECK (operation IN ('Reload', 'Payment')) NOT NULL,
                amount TEXT NOT NULL,
                status TEXT CHECK (status IN ('success', 'pending', 'failed')) NOT NULL,
                remarks TEXT,
                description TEXT,
                created_at TIMESTAMP,
                user_id TEXT,
                trip_id TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (trip_id) REFERENCES trips(id)
            )
        """
        val createReviewTable = """
            CREATE TABLE reviews (
                id TEXT PRIMARY KEY,
                trip_name TEXT,
                title TEXT,
                rating REAL NOT NULL,
                comment TEXT,
                is_public INTEGER CHECK (is_public IN (0,1)),
                imageUrls TEXT,
                created_at TIMESTAMP,
                trip_id TEXT,
                user_id TEXT,
                FOREIGN KEY (trip_id) REFERENCES trips(id),
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """
        db.execSQL(createTripTable)
        db.execSQL(createUserTable)
        db.execSQL(createWalletTable)
        db.execSQL(createTransactionTable)
        db.execSQL(createReviewTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS trips")
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS wallets")
        db.execSQL("DROP TABLE IF EXISTS transactions")
        db.execSQL("DROP TABLE IF EXISTS reviews")
        onCreate(db)
    }

    fun addNewUser(
        username: String,
        email: String,
        password: String,
    ) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("email", email)
            put("password", password)
        }
        db.insert("users", null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getUserByEmailNPw(username: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE email=? AND password=?",
            arrayOf(username, password)
        )
        var user: User? = null

        if (cursor != null) {
            val idColIdx = cursor.getColumnIndex("id")
            if (idColIdx != -1 && cursor.moveToFirst()) {
                user = User(
                    userId = cursor.getString(cursor.getColumnIndex("id")),
                    userName = cursor.getString(cursor.getColumnIndex("username")),
                    userEmail = cursor.getString(cursor.getColumnIndex("email")),
                    userPw = cursor.getString(cursor.getColumnIndex("password")),
                )
            }
        }
        cursor.close()
        db.close()
        return user
    }

    fun getAllTrips(agencyUsername: String): List<Trip> {
        val tripList: ArrayList<Trip> = ArrayList()
        val db = this.readableDatabase
        val selectionArgs = arrayOf(agencyUsername)
        val cursorTrips: Cursor = db.rawQuery("SELECT * FROM trips WHERE agency_username=?", selectionArgs)

        if (cursorTrips.moveToFirst()) {
            do {
                val tripIdIndex = cursorTrips.getColumnIndex("trip_id")
                val tripNameIndex = cursorTrips.getColumnIndex("trip_name")
                val tripLengthIndex = cursorTrips.getColumnIndex("trip_length")
                val tripFeesIndex = cursorTrips.getColumnIndex("trip_fees")
                val tripDepositIndex = cursorTrips.getColumnIndex("trip_deposit")
                val tripDescIndex = cursorTrips.getColumnIndex("trip_desc")
                val tripDepDateIndex = cursorTrips.getColumnIndex("trip_dep_date")
                val tripRetDateIndex = cursorTrips.getColumnIndex("trip_ret_date")
                val tripImageUriIndex = cursorTrips.getColumnIndex("trip_image_uri")
//                val tripOptionsIndex = cursorTrips.getColumnIndex("trip_options")
                val isAvailableIndex = cursorTrips.getColumnIndex("is_available")
                val noUserBookedIndex = cursorTrips.getColumnIndex("no_user_booked")
                val agencyUsernameIndex = cursorTrips.getColumnIndex("agency_username")

                if (tripIdIndex >= 0 && tripNameIndex >= 0 && tripLengthIndex >= 0 && tripFeesIndex >= 0 &&
                    tripDepositIndex >= 0 && tripDescIndex >= 0 && tripDepDateIndex >= 0 &&
                    tripRetDateIndex >= 0 && tripImageUriIndex >= 0 &&
                    isAvailableIndex >= 0 && noUserBookedIndex >= 0 && agencyUsernameIndex >= 0) {

                    val trip = Trip(
                        tripId = cursorTrips.getString(tripIdIndex),
                        tripName = cursorTrips.getString(tripNameIndex),
                        tripLength = cursorTrips.getString(tripLengthIndex),
                        tripFees = cursorTrips.getDouble(tripFeesIndex),
                        tripDeposit = cursorTrips.getDouble(tripDepositIndex),
                        tripDesc = cursorTrips.getString(tripDescIndex),
                        depDate = cursorTrips.getString(tripDepDateIndex),
                        retDate = cursorTrips.getString(tripRetDateIndex),
                        tripUri = cursorTrips.getString(tripImageUriIndex),
//                        options = cursorTrips.getString(tripOptionsIndex),
                        isAvailable = cursorTrips.getInt(isAvailableIndex),
                        noOfUserBooked = cursorTrips.getInt(noUserBookedIndex),
                        agencyUsername = cursorTrips.getString(agencyUsernameIndex)
                    )
                    tripList.add(trip)
                }
            } while (cursorTrips.moveToNext())
        }

        cursorTrips.close()
        db.close()

        return tripList
    }

    fun addNewTrip(
        tripId: String,
        tripName: String,
        tripLength: String,
        tripFees: Double,
        tripDeposit: Double,
        tripDesc: String,
        depDate: String,
        retDate: String,
        imageUri: String?,
        tripOptions: List<String>,
        isAvailable: Int,
        noOfUserBooked: Int,
        agencyUsername: String,
    ) {
        val optionsString = tripOptions.joinToString(", ")
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("trip_id", tripId)
            put("trip_name", tripName)
            put("trip_length", tripLength)
            put("trip_fees", tripFees)
            put("trip_deposit", tripDeposit)
            put("trip_desc", tripDesc)
            put("trip_dep_date", depDate)
            put("trip_ret_date", retDate)
            put("trip_image_uri", imageUri)
            put("trip_options", optionsString)
            put("is_available", isAvailable)
            put("no_user_booked", noOfUserBooked)
            put("agency_username", agencyUsername)
        }
        db.insert("trips", null, values)
        db.close()
    }

    fun deleteAllTrip() {
        val db = this.writableDatabase
        db.delete("trips", null, null)
        db.close()
    }


    //wallets parts
    fun createWallet(id: String, user_id: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("id", id)
            put("user_id", user_id)
            put("available", 0.0)
            put("frozen", 0.0)
            put("walletPIN", "null")
        }
        db.insert("wallets", null, values)
        db.close()
    }

    fun updateWalletPin(pin: String, user_id: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("walletPin", pin)
        }
        db.update("wallets", values, "user_id = ?", arrayOf(user_id))
//        val cursor = db.rawQuery("UPDATE wallets SET walletPin = :pin WHERE user_id = :AUTH_USER", null)
//        cursor.close()
        db.close()
    }

    fun updateBalance(user_id: String, available: String, amount: String, action: String) {
        val db = this.writableDatabase
        val num1 = available.toDouble()
        val num2 = amount.toDouble()
        var total: Double? = null
        if (action == "Reload") {
            total = num1?.plus(num2)
        } else {
            total = num1?.minus(num2)
        }
        val values = ContentValues().apply {
            put("available", total)
        }
        db.update("wallets", values, "user_id = ?", arrayOf(user_id))
//        val cursor = db.rawQuery("UPDATE wallets SET available = :amount WHERE user_id = :AUTH_USER", null)
//        cursor.close()
        db.close()
    }

    //transaction parts
    fun createTransaction(
        id: String,
        operation: String,
        amount: String,
        description: String,
        tripName:String? = "null",
//        agencyUsername: String? = "null",
        user_id: String,
        trip_id: String? = "null",
    ) {
        val db = this.writableDatabase
        val time = System.currentTimeMillis()
        val remarks = if (trip_id != "null") tripName else operation
        val values = ContentValues().apply {
            put("id", id)
            put("trip_id", trip_id)
            put("user_id", user_id)
            put("operation", operation)
            put("amount", amount)
            put("status", "success")
            put("remarks", remarks)
            put("description", description)
            put("created_at", time)
        }
        db.insert("transactions", null, values)
        db.close()
    }

    // review parts
    fun saveReview(
        id: String = "null",
        trip_name: String,
        title: String,
        rating: Int,
        comment: String,
        imageUris: List<Uri>,
        is_public: Int,
        trip_id: String? = "null",
        user_id: String? = "null",
        created_at: Long = 0L,
        action: String,
    ) {
        val db = this.writableDatabase

        val imageUrls = mutableListOf<String>()
        for (uri in imageUris) {
            imageUrls.add(uri.toString())
        }
        val imageUrlsString: String = imageUrls.joinToString(",")
        val time = System.currentTimeMillis()

        val values = ContentValues().apply {
            put("id", id)
            put("trip_id", trip_id)
            put("user_id", user_id)
            put("trip_name", trip_name)
            put("title", title)
            put("rating", rating)
            put("comment", comment)
            put("is_public", is_public)
            put("imageUrls", imageUrlsString)
            put("created_at", if (created_at != 0L) created_at else time)
        }
        if (action == "Edit") {
            val review = getReviewById(id)
            values.remove("id")
            db.update("reviews", values, "id = ?", arrayOf(id))
        } else {
            db.insert("reviews", null, values)
        }
        db.close()
    }
    fun getReviewById(id: String): Review? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM reviews WHERE id = $id", null)

        val review: Review? = if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex("id")
            val tripIdIndex = cursor.getColumnIndex("trip_id")
            val userIdIndex = cursor.getColumnIndex("user_id")
            val tripNameIndex = cursor.getColumnIndex("trip_name")
            val titleIndex = cursor.getColumnIndex("title")
            val ratingIndex = cursor.getColumnIndex("rating")
            val commentIndex = cursor.getColumnIndex("comment")
            val isPublicIndex = cursor.getColumnIndex("is_public")
            val imageUrlIndex = cursor.getColumnIndex("imageUrl")
            val createdAtIndex = cursor.getColumnIndex("created_at")

            Review(
                id = cursor.getString(idIndex),
                trip_id = cursor.getString(tripIdIndex),
                user_id = cursor.getString(userIdIndex),
                trip_name = cursor.getString(tripNameIndex),
                title = cursor.getString(titleIndex),
                rating = cursor.getInt(ratingIndex),
                comment = cursor.getString(commentIndex),
                is_public = cursor.getInt(isPublicIndex),
                imageUrls = cursor.getString(imageUrlIndex),
                created_at = cursor.getLong(createdAtIndex)
            )
        } else {
            null // If no review found with the given ID, return null
        }
        cursor.close()
        db.close()
        return review
    }

    //    fun getAllReview(): List<Review> {
//        val reviewList: ArrayList<Review> = ArrayList()
//        val db = this.readableDatabase
//        val cursorReviews: Cursor = db.rawQuery(
//            "SELECT * FROM reviews WHERE user_id = ?",
//            arrayOf(AUTH_USER)
//        )
//        if (cursorReviews.moveToFirst()) {
//            val idIndex = cursorReviews.getColumnIndex("id")
//            val tripIdIndex = cursorReviews.getColumnIndex("trip_id")
//            val userIdIndex = cursorReviews.getColumnIndex("user_id")
//            val tripNameIndex = cursorReviews.getColumnIndex("trip_name")
//            val titleIndex = cursorReviews.getColumnIndex("title")
//            val ratingIndex = cursorReviews.getColumnIndex("rating")
//            val commentIndex = cursorReviews.getColumnIndex("comment")
//            val isPublicIndex = cursorReviews.getColumnIndex("is_public")
//            val imageUrlsIndex = cursorReviews.getColumnIndex("imageUrls")
//            val createdAtIndex = cursorReviews.getColumnIndex("created_at")
//            do {
//                reviewList.add(
//                    Review(
//                        id = cursorReviews.getString(idIndex),
//                        trip_id = cursorReviews.getString(tripIdIndex),
//                        user_id = cursorReviews.getString(userIdIndex),
//                        trip_name = cursorReviews.getString(tripNameIndex),
//                        title = cursorReviews.getString(titleIndex),
//                        rating = cursorReviews.getInt(ratingIndex),
//                        comment = cursorReviews.getString(commentIndex),
//                        is_public = cursorReviews.getInt(isPublicIndex),
//                        imageUrls = cursorReviews.getString(imageUrlsIndex),
//                        created_at = cursorReviews.getLong(createdAtIndex),
//                    )
//                )
//            } while (cursorReviews.moveToNext())
//        }
//        cursorReviews.close()
//        db.close()
//        return reviewList
//    }

//    fun getAllTransactions(): List<Transaction> {
//        val transactions: ArrayList<Transaction> = ArrayList()
//        val db = this.readableDatabase
//
//        val query = """
//        SELECT t.id, t.trip_id, t.wallet_id, t.operation, t.amount, t.status, t.remarks, t.created_at
//        FROM transactions t
//        INNER JOIN wallets w ON t.wallet_id = w.id
//        WHERE w.user_id = ?
//    """.trimIndent()
//

//        val cursorTransactions: Cursor = db.rawQuery(query, arrayOf(AUTH_USER))
//        if (cursorTransactions.moveToFirst()) {
//            val idIndex = cursorTransactions.getColumnIndex("id")
//            val tripIdIndex = cursorTransactions.getColumnIndex("trip_id")
//            val walletIdIndex = cursorTransactions.getColumnIndex("wallet_id")
//            val operationIndex = cursorTransactions.getColumnIndex("operation")
//            val amountIndex = cursorTransactions.getColumnIndex("amount")
//            val statusIndex = cursorTransactions.getColumnIndex("status")
//            val remarksIndex = cursorTransactions.getColumnIndex("remarks")
//            val descriptionIndex = cursorTransactions.getColumnIndex("description")
//            val createdAtIndex = cursorTransactions.getColumnIndex("created_at")
//
//            do {
//                transactions.add(
//                    Transaction(
//                        id = cursorTransactions.getString(idIndex),
//                        trip_id = cursorTransactions.getString(tripIdIndex),
//                        user_id = cursorTransactions.getString(walletIdIndex),
//                        operation = cursorTransactions.getString(operationIndex),
//                        amount = cursorTransactions.getString(amountIndex),
//                        status = cursorTransactions.getString(statusIndex),
//                        remarks = cursorTransactions.getString(remarksIndex),
//                        description = cursorTransactions.getString(descriptionIndex),
//                        created_at = cursorTransactions.getLong(createdAtIndex)
//                    )
//                )
//            } while (cursorTransactions.moveToNext())
//        }
//
//        cursorTransactions.close()
//        db.close()
//        return transactions
//    }



//    fun getUserWallet(): Wallet? {
//        val db = this.readableDatabase
//        val query = "SELECT * FROM wallets WHERE user_id = ?"
//        val cursor = db.rawQuery(query, arrayOf(AUTH_USER))
//
//
//        val wallet: Wallet? = if (cursor.moveToFirst()) {
//            val availableIndex = cursor.getColumnIndex("available")
//            val frozenIndex = cursor.getColumnIndex("frozen")
//            val walletPINIndex = cursor.getColumnIndex("walletPIN")
//
//            Wallet(
//                user_id = AUTH_USER,
//                available = cursor.getString(availableIndex),
//                frozen = cursor.getString(frozenIndex),
//                walletPin = decrypt(cursor.getBlob(walletPINIndex)),
//            )
//        } else {
//            null // If no review found with the given ID, return null
//        }
//        cursor.close()
//        db.close()
//        return wallet
//    }


    companion object {
        private const val DB_NAME = "travelerDB"
        private const val DB_VERSION = 19
    }
}