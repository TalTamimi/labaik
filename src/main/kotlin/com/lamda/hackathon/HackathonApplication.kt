package com.lamda.hackathon

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.apache.commons.math3.stat.regression.SimpleRegression
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileInputStream
import java.time.LocalDateTime

val dimensions = Array(100) { Array(20) { 0 } }
val indexes = Array(100) { 0 }

fun main(args: Array<String>) {
    runApplication<HackathonApplication>(*args)
}

val minLong = 21.278002
val maxLong = 21.511996
val minLat = 39.689981
val maxLat = 39.981805

private fun gridify(long: String, lat: String): String {
    val x = ((long.toDouble() - minLong) * 10 / (maxLong - minLong)).toInt()
    val y = ((lat.toDouble() - minLat) * 10 / (maxLat - minLat)).toInt() * 10
    return (x + y).toString()
}

private fun ungridify(grid: Int): Pair<Double, Double> {
    val long = (grid % 10 * (maxLong - minLong)) + minLong
    val lat = (grid / 10 * (maxLat - minLat)) / 10
    return Pair(long, lat)
}


private fun String?.slotifiy(): String {
    val date = this!!
    val x = LocalDateTime.parse(date.substring(0, date.indexOf('.')))
    return ((x.dayOfMonth - 2) * 24 + x.hour).toString()
}


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

    @Bean
    fun GenerateReportCsv(): ValueEventListener {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("reports")
        return ref.addValueEventListener(ReportEventListener())
    }


    //    @PostMapping()
//    fun sendMessage(@RequestBody token: String) {
//        val message = Message.builder()
//                .setNotification(Notification("hi", "hi"))
//                .setToken(token)
//                .build()
//        FirebaseMessaging.getInstance().send(message)
//    }


    @GetMapping("/indexes")
    fun getIndexes(): MutableList<Pin> {
        val pins = mutableListOf<Pin>()
        for (i in 0 until indexes.size) {
            if (indexes[i] > 0) {
                val coordinates = ungridify(i)
                pins.add(Pin(coordinates.first, coordinates.second, indexes[i]))
            }
        }
        return pins
    }

    @GetMapping
    fun modelData(): String {
        dimensions.forEachIndexed { index, counts -> indexes[index] = train(counts) }
        return "hello darling"
    }

    private fun train(counts: Array<Int>): Int {
        val reg = SimpleRegression()
        counts
                .forEachIndexed { index, i -> reg.addData(index + 15.toDouble(), i.toDouble()) }

        return reg.predict(20.0).toInt()
    }
}

class ReportEventListener : ValueEventListener {
    override fun onCancelled(error: DatabaseError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val reports =
                snapshot.children.asSequence()
                        .map { it.value as Map<String, String> }
                        .groupingBy { Pair(gridify(it["latitude"].toString(), it["longitude"].toString()), it["time"].slotifiy()) }
                        .eachCount()
        File("reports.csv").printWriter().use { out ->
            reports.forEach {
                out.println("${it.key.first},${it.key.second},${it.value}")
                if (it.key.first.toInt() in 0..100 && it.key.second.toInt() - 20 < 20)
                    dimensions[it.key.first.toInt()][it.key.second.toInt() - 20] = it.value
            }
        }


    }

    private fun generateCsv(snapshot: DataSnapshot) {
        val reports = snapshot.children
        File("reports.csv").printWriter().use { out ->
            reports.forEach {
                (it.value as Map<String, String>).let {
                    out.println("${gridify(it["latitude"].toString(), it["longitude"].toString())}, ${it["type"].toString()}" +
                            ", ${it["time"].slotifiy()}")
                }
            }
        }
    }
}
