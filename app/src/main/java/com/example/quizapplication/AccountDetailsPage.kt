package com.example.quizapplication

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.quizapplication.ui.theme.QuizApplicationTheme
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class AccountDetailsPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizApplicationTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AccountDetails(navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetails(navController: NavController) {
    var password by remember { mutableStateOf( "") }
//    var emailAuth by remember { mutableStateOf( "") }
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    var email by remember { mutableStateOf(firebaseUser?.email ?: "") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val firestore = FirebaseFirestore.getInstance()
    val lightPurple = Color(0xFFB19CD9)
    var profilePicture by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            profilePicture = uri
        }
    var showDialogForLogout by remember { mutableStateOf(false) }
    var showDialogForSave by remember { mutableStateOf(false) }
    Box (
        modifier = Modifier
            .fillMaxSize()
    ){TopAppBar(
            title = { Text(text = "Account Details") },
            navigationIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "BackArrow",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {
                            navController.navigate("HomePage")
                        }
                )
            },
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            showDialogForLogout = true
                        }
                    ) {
                    }
                }

                if (showDialogForLogout) {
                    AlertDialog(
                        containerColor = lightPurple,
                        onDismissRequest = {
                            showDialogForLogout = false
                        },
                        title = { Text("Logout", style = MaterialTheme.typography.headlineMedium) },
                        text = { Text("Are you sure you want to log out?", style = MaterialTheme.typography.bodyLarge) },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDialogForLogout = false
                                    navController.navigate("LoginPage")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                            ) {
                                Text("Confirm", color = Color.White)
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    showDialogForLogout = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Cancel", color = Color.White)
                            }
                        }
                    )
                }

                if (showDialogForSave) {
                    AlertDialog(
                        containerColor = lightPurple,
                        onDismissRequest = {
                            if (errorMessage.isEmpty()) {
                                showDialogForSave = false
                            }
                        },
                        title = { Text("Save Updates", style = MaterialTheme.typography.headlineMedium) },
                        text = { Text("Are you sure you want to save the updates?", style = MaterialTheme.typography.bodyLarge) },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDialogForSave = false
                                    updateUsername(username, navController)
                                    updatePhoneNumber(phone, navController)
                                    updatePassword(password, navController)
//                                    updateEmail(email, navController)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                            ) {
                                Text("Confirm", color = Color.White)
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    if (errorMessage.isEmpty()) {
                                        showDialogForSave = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Cancel", color = Color.White)
                            }
                        }
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = lightPurple)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, top = 100.dp, end = 20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                val profilePictureUrl = firebaseUser.photoUrl?.toString()
                if (profilePictureUrl != null) {
                    Image(
                        painter = rememberImagePainter(data = profilePictureUrl),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .aspectRatio(1f)
                            .clickable {
                                launcher.launch("image/*")
                            }
                    )
                    profilePicture?.let { uri ->
                        updateProfilePicture(firebaseUser.uid, uri) { url ->
                            if (url != null) {
                                println("New profile picture URL: $url")
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(url))
                                    .build()
                                firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d("UpdateProfile", "User profile updated.")
                                        } else {
                                            Log.e(
                                                "UpdateProfile",
                                                "Error updating profile: ${task.exception}"
                                            )
                                        }
                                    }
                            } else {
                                println("Failed to update profile picture")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Update profile picture?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = lightPurple,
                        modifier = Modifier
                            .clickable { launcher.launch("image/*") }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "AccountCircle",
                        modifier = Modifier.size(50.dp)
                    )
                    Text("Do you want to set a profile picture?")
                }

                Spacer(modifier = Modifier.height(10.dp))

//                var name by remember { mutableStateOf("") }
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = { name = it },
//                    label = { Text("Name") },
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Default.Person,
//                            contentDescription = null,
//                            tint = Color.Gray
//                        )
//                    },
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
////                        textColor = Color.Black,
//                        focusedBorderColor = Color.Blue,
//                        unfocusedBorderColor = Color.Gray,
//                        focusedLabelColor = Color.Blue,
//                        unfocusedLabelColor = if (name.isEmpty()) Color.Red else Color.Gray
//                    ),
//                    modifier = Modifier.fillMaxWidth()
//                )



                LaunchedEffect(key1 = true) {
                    firebaseUser?.let {
                        firestore.collection("users").document(it.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    phone = document.getString("phone") ?: ""
                                    username = document.getString("username") ?: ""
                                    password = document.getString("password") ?: ""
//                                    email = document.getString("email") ?: ""
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error fetching document", e)
                            }
                    }
                }


                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Blue,
                        unfocusedLabelColor = if (username.isEmpty()) Color.Red else Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = "" },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        textColor = Color.Black,
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Blue,
                        unfocusedLabelColor = if (email.isEmpty()) Color.Red else Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        errorMessage = "" },
                    label = { Text("Phone Number") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Blue,
                        unfocusedLabelColor = if (phone.isEmpty()) Color.Red else Color.Gray
                    ),
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = ""},
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Blue,
                        unfocusedLabelColor = if (password.isEmpty()) Color.Red else Color.Gray
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showDialogForSave = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showDialogForLogout = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }
            }
        }
    }
}

fun updateProfilePicture(userId: String, profilePicture: Uri?, onComplete: (String?) -> Unit) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val imageName = "profile_pictures/${userId}/profile_picture"
    val imageRef = storageRef.child(imageName)

    val uploadTask = imageRef.putFile(profilePicture!!)

    uploadTask.continueWithTask { task ->
        if (!task.isSuccessful) {
            task.exception?.let {
                throw it
            }
        }

        imageRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result
            Log.d("UpdateProfilePicture", "Download URL: $downloadUri")
            onComplete(downloadUri.toString())
        } else {
            Log.e("UpdateProfilePicture", "Error uploading profile picture: ${task.exception}")
            onComplete(null)
        }
    }
}

fun updateUsername(username: String, navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    if (firebaseUser != null) {
        data class UserInfo(
            val username: String,
            val timestamp: Timestamp
        )

        val userData = UserInfo(
            username = username,
            timestamp = Timestamp.now()
        )

        firestore.collection("users").document(firebaseUser.uid)
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                navController.navigate("HomePage")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    } else {
    }
}

fun updatePhoneNumber(phone: String, navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    if (firebaseUser != null) {
        data class UserInfo(
            val phone: String,
            val timestamp: Timestamp
        )

        val userData = UserInfo(
            phone = phone,
            timestamp = Timestamp.now()
        )

        firestore.collection("users").document(firebaseUser.uid)
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                navController.navigate("HomePage")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    } else {
        Log.e(TAG, "User is not signed in.")
    }
}

fun updatePassword(password: String, navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    if (firebaseUser != null) {
        data class UserInfo(
            val password: String,
        )

        val userData = UserInfo(
            password = password,
        )

        firestore.collection("users").document(firebaseUser.uid)
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                firebaseUser.updatePassword(password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User password updated.")
                            navController.navigate("HomePage")
                        } else {
                            Log.w(TAG, "Error updating password in Firebase Authentication", task.exception)
                        }
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    } else {
        Log.e(TAG, "User is not signed in.")
    }
}

//fun updateEmail(email: String, navController: NavController) {
//    val firestore = FirebaseFirestore.getInstance()
//    val firebaseUser = FirebaseAuth.getInstance().currentUser
//
//    if (firebaseUser != null) {
//        data class UserInfo(
//            val email: String,
//        )
//
//        val userData = UserInfo(
//            email = email,
//        )
//
//        firestore.collection("users").document(firebaseUser.uid)
//            .set(userData, SetOptions.merge())
//            .addOnSuccessListener {
//                Log.d(TAG, "DocumentSnapshot successfully updated!")
//                firebaseUser.updateEmail(email)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            Log.d(TAG, "User email updated.")
//                            navController.navigate("HomePage")
//                        } else {
//                            Log.w(TAG, "Error updating email in Firebase Authentication", task.exception)
//                        }
//                    }
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error updating document", e)
//            }
//    } else {
//        Log.e(TAG, "User is not signed in.")
//    }
//}




//fun accountValidPassword(password: String): Boolean{
//    return password.isNotEmpty() && password.length > 5
//}
//
//fun accountValidEmail(email: String): Boolean {
//    val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
//    return email.isNotEmpty() && email.matches(emailRegex)
//}
//
//fun accountValidSave(email: String, password: String): Boolean{
//    return accountValidPassword(password) && accountValidEmail(email)
//}
