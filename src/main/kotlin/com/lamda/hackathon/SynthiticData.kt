package com.lamda.hackathon

import com.google.firebase.database.FirebaseDatabase
import org.springframework.core.io.ClassPathResource
import java.util.*

val names = mutableListOf<String>()
val lastNames = mutableListOf<String>()
val hamlsa = listOf("Alsafah", "AlShoroq", "Taawon", "Tashelat",
        "Lamda", "Barq", "AlNosoq")
val random = Random()

fun fillNames() {
    val fnScanner = Scanner(ClassPathResource("names").inputStream, "UTF-8")
    while (fnScanner.hasNext())
        names.add(fnScanner.next())
    val lnScanner = Scanner(ClassPathResource("lastNames").inputStream, "UTF-8")
    while (lnScanner.hasNext())
        lastNames.add(lnScanner.next())
}

fun getNamePair() =
        Pair(names[random.nextInt(4500)],
                lastNames[random.nextInt(3500)])

fun getNationality() =
        random.nextInt(75)

fun getAge() =
        random.nextInt(7)

fun getGender() =
        random.nextInt(2)


fun getPhone() =
        "05" + (random.nextInt(1000000) + 9999999)

fun getHajjNumber() =
        (random.nextInt(10000) + 99999).toString()

fun getLong() =
        random.nextInt(291824).toFloat() / 1000000 + 39.689981
fun getLanguage()=
        random.nextInt(9)

fun getHamla() =
        hamlsa[random.nextInt(7)]

fun getLat() =
        random.nextInt(233994).toFloat() / 1000000 + 21.278002

fun populate(numberOfUsers: Int) {
    fillNames()
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("users")
    val name = getNamePair()
    val users = mutableMapOf<String, User>()

    for (i in 0..numberOfUsers) {
        users[getHajjNumber()] = User(
                name.first, name.second, getNationality(), getAge(),
                getGender(), getHamla(), 0, getPhone(), getLat().toString(),
                getLong().toString(), getLanguage()
        )
    }
    val usersToPopulate: Map<String, User> = users
    ref.updateChildrenAsync(usersToPopulate)

}



