package com.lamda.hackathon

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.*
import java.io.FileInputStream
import java.util.*


@SpringBootApplication
@RestController
@Configuration
class HackathonApplication {


    @Bean
    fun firebase(): FirebaseApp {
        val serviceAccount = FileInputStream("C:\\Users\\talal\\IdeaProjects\\hackathon\\src\\main\\resources\\hajj-hackathon1-firebase-adminsdk-vafwb-40b685d2d1.json")

        val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://hajj-hackathon1.firebaseio.com")
                .build()

        return FirebaseApp.initializeApp(options)
    }

    //    @GetMapping
    fun populateData() {
        populate(1000)
    }


//    fun listenToDb(): ValueEventListener {
//        val database = FirebaseDatabase.getInstance()
//        val ref = database.getReference("users")
//
//        val users: Map<String, User> =
//                mapOf("123467" to
//                        User("talal", "Altamimi", 0,
//                                3, 0, "Lamda", 0,
//                                "0558899775", "21.485811", "39.192504799999995")
//                )
//
//        ref.updateChildrenAsync(users)
//// Attach a listener to read the data at our posts reference
//        return ref.addValueEventListener(UserEventListener())
//    }


    @PostMapping()
    fun sendMessage(@PathVariable token:String) {

        val message = Message.builder()
                .putData("score", "850")
                .putData("time", "2:45")
                .setToken(token)
                .build()

// Send a message to the device corresponding to the provided
// registration token.
        val response = FirebaseMessaging.getInstance().send(message)
// Response is a message ID string.
        println("Successfully sent message: $response")
    }


}

fun main(args: Array<String>) {
    runApplication<HackathonApplication>(*args)
}

class UserEventListener() : ValueEventListener {
    override fun onCancelled(error: DatabaseError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val name = snapshot.getValue(String::class.java)
    }

}

