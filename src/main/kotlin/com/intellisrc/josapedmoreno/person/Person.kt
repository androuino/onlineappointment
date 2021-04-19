package com.intellisrc.josapedmoreno.person

import com.intellisrc.core.Config
import com.intellisrc.core.Log
import com.intellisrc.core.SysInfo
import com.intellisrc.db.Data
import com.intellisrc.db.Database
import com.intellisrc.josapedmoreno.types.CustomColor
import java.io.IOException
import java.lang.Exception
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.nio.file.Files
import java.util.*
import kotlin.Throws

/**
 * Representation of a Person
 * @since 2020/01/15.
 * @author A.Lepe
 */
class Person {
    /**
     * Get the ID
     * @return Person's id
     */
    var id = 0
    var firstName: String? = null
    var lastName: String? = null
    var age = 0
    var favouriteColor: CustomColor? = null
    var hobby: Array<String> = emptyArray()

    /**
     * Exception thrown when we can not create a Person object
     */
    class IllegalPersonException : Exception() {}

    /**
     * Create a new Person
     * @param firstName : First Name (e.g. John)
     * @param lastName  : Last Name  (e.g. Doe)
     * @param age       : Age        (e.g. 50)
     * @param favouriteColor : Color object
     * @param hobby     : String array with a list of hobbies
     * @throws IllegalPersonException : Unable to create Person in database
     */
    constructor(firstName: String?, lastName: String?, age: Int, favouriteColor: CustomColor?, hobby: Array<String>) {
        if (firstName!!.isEmpty() || lastName!!.isEmpty() || age <= 0) {
            Log.w("First name: %s, Last name: %s or Age: %d is invalid", firstName, lastName, age)
            throw IllegalPersonException()
        }
        this.firstName = firstName
        this.lastName = lastName
        this.age = age
        this.favouriteColor = favouriteColor
        this.hobby = hobby
        val db = Database.connect()
        val row: MutableMap<String?, Any?> = HashMap()
        row["first_name"] = firstName
        row["last_name"] = lastName
        row["age"] = age
        row["favourite_colour"] = favouriteColor
        row["hobby"] = StringBuilder().append(",").append(hobby).toString()
        val inserted = db.table(personTable).insert(row)
        id = db.lastID
        db.close()
        if (inserted) {
            Log.i("NEW Person: [%s %s] with [%d]", firstName, lastName, id)
        } else {
            throw IllegalPersonException()
        }
    }

    /**
     * Retrieves a Person knowing its ID
     * @param id : Person's ID
     * @throws IllegalPersonException : Unable to find Person in database
     */
    @Suppress("UNCHECKED_CAST")
    constructor(id: Int) {
        this.id = id
        if (id <= 0) {
            Log.w("Trying to get a Person with invalid ID: %d", id)
            throw IllegalPersonException()
        }
        val db = Database.connect()
        val row = db.table(personTable).key("id")[id]
        db.close()
        if (row == null || row.isEmpty) {
            Log.w("Person with id: %d doesn't exists", id)
            throw IllegalPersonException()
        }
        val personMap = cleanInputMap(row.toMap())
        firstName = personMap["first_name"].toString()
        lastName = personMap["last_name"].toString()
        age = (personMap["age"] as Int?)!!
        favouriteColor = personMap["favourite_colour"] as CustomColor?
        hobby = personMap["hobby"] as? Array<String> ?: emptyArray()
    }

    /**
     * Empty constructor which is used internally to create Person objects
     * without connecting to the database.
     */
    private constructor() {}

    /**
     * Updates a Person's name
     * @param newFirstName : first name
     * @param newLastName : last name
     * @return true if succeeds
     */
    fun updateName(newFirstName: String, newLastName: String): Boolean {
        var ok = false
        if (isValid && !newFirstName.isEmpty() && !newLastName.isEmpty()) {
            val db = Database.connect()
            val map = HashMap<String, Any>()
            map["first_name"] = newFirstName
            map["last_name"] = newLastName
            ok = db.table(personTable).key("id").update(map, id)
            if (ok) {
                firstName = newFirstName
                lastName = newLastName
            }
            db.close()
            Log.i("Person with id: %d updated name to: %s %s", id, newFirstName, newLastName)
        }
        return ok
    }

    /**
     * Update a Person's age
     * @param newAge : age
     * @return true if succeeds
     */
    fun updateAge(newAge: Int): Boolean {
        var ok = false
        if (isValid && newAge > 0) {
            val db = Database.connect()
            val map = HashMap<String, Any>()
            map["age"] = newAge
            ok = db.table(personTable).key("id").update(map, id)
            if (ok) {
                age = newAge
            }
            db.close()
            Log.i("Person with id: %d updated his/her age to: %d", id, newAge)
        }
        return ok
    }

    /**
     * Update a Person's favourite color.
     * @param color : Color object
     * @return true if succeeds
     */
    fun updateColor(color: CustomColor): Boolean {
        var ok = false
        if (isValid) {
            val db = Database.connect()
            val map = HashMap<String, Any>()
            map["favourite_colour"] = color.toString()
            ok = db.table(personTable).key("id").update(map, id)
            db.close()
            if (ok) {
                favouriteColor = color
            }
            Log.i("Person with id: %d changed favourite color to: %s", id, color.toString())
        }
        return ok
    }

    /**
     * Update hobby to a Person
     * @param hobbies : String array of new hobbies
     * @return true if succeeds
     */
    fun updHobbies(hobbies: Array<String>): Boolean {
        var ok = false
        if (isValid) {
            val db = Database.connect()
            val newHobbiesStr = StringBuilder().append(",").append(hobbies).toString()
            val map = HashMap<String, Any>()
            map["hobby"] = newHobbiesStr
            ok = db.table(personTable).key("id").update(map, id)
            db.close()
            if (ok) {
                hobby = hobbies
            }
            Log.i("Person with id: %d added: %s as hobby", id, hobby)
        }
        return ok
    }

    /**
     * Delete a person
     * @return true if succeeds
     */
    fun delete(): Boolean {
        var ok = false
        if (isValid) {
            val db = Database.connect()
            ok = db.table(personTable).key("id").delete(id)
            db.close()
            Log.i("Person with id: %d was deleted.", id)
            id = 0 //clear this Person
        }
        return ok
    }

    /**
     * Validate a Person
     * @return true if a Person is valid
     */
    private val isValid: Boolean
        get() {
            val valid = id > 0 && !firstName!!.isEmpty()
            if (!valid) {
                Log.w("Person was not valid")
            }
            return valid
        }

    /**
     * Export Person as Map object
     * @return a Map with Person data
     */
    fun toMap(): Map<String, Any?> {
        val personMap: MutableMap<String, Any?> = HashMap()
        personMap["id"] = id
        personMap["first_name"] = firstName
        personMap["last_name"] = lastName
        personMap["age"] = age
        personMap["favourite_colour"] = favouriteColor.toString()
        personMap["hobby"] = hobby
        return personMap
    }

    companion object {
        private const val personTable = "person"
        ////////////////////////// STATIC METHODS ////////////////////////////////
        /**
         * Clone a Person : I hope this is not illegal :)
         * This method will copy all fields from an existing Person and
         * create a new one based on it.
         *
         * @param person : Person object to copy from
         * @return Person (with new ID)
         * @throws IllegalPersonException : Unable to clone Person : if it was illegal after all.
         */
        @Throws(IllegalPersonException::class)
        fun clone(person: Person): Person {
            val clone = Person(person.firstName, person.lastName, person.age, person.favouriteColor, person.hobby)
            Log.i("Clone process succeed. New id is: %d", clone.id)
            return clone
        }

        /**
         * Creates a Person from a Map Object
         * @param map : Data to import
         * @return a Person object
         * @throws IllegalPersonException : Unable to import person from Map
         */
        @Suppress("UNCHECKED_CAST")
        @Throws(IllegalPersonException::class)
        fun fromMap(map: Map<*, *>?): Person {
            val person = Person()
            val personMap = cleanInputMap(map)
            person.id = (personMap["id"] as Int?)!!
            person.firstName = personMap["first_name"].toString()
            person.lastName = personMap["last_name"].toString()
            person.age = (personMap["age"] as Int?)!!
            person.favouriteColor = personMap["favourite_colour"] as CustomColor?
            person.hobby = personMap["hobby"] as? Array<String> ?: emptyArray()
            return person
        }

        /**
         * It will receive a Map and will return a Map (after cleaning the input).
         * If there is an invalid input, it will return a empty Map
         * @param input : Map (from JSON or Data)
         * @return a clean Map
         */
        @Suppress("UNCHECKED_CAST")
        @Throws(IllegalPersonException::class)
        fun cleanInputMap(input: Map<*, *>?): HashMap<String, Any> {
            val cleanMap = HashMap<String, Any>()
            var ok = true
            for (inKey in input?.keys!!) {
                when (val key = inKey.toString()) {
                    "id" -> cleanMap[key] = input[key].toString().toInt()
                    "last_name", "first_name" -> {
                        val name = input[key].toString().trim { it <= ' ' }.replace("[^a-z A-Z]".toRegex(), "")
                        if (name.isNotEmpty()) {
                            cleanMap[key] = name
                        } else {
                            Log.w("%s value was invalid: %s", key, input[key].toString())
                            ok = false
                        }
                    }
                    "age" -> try {
                        val age = input[key].toString().replace("\\..*".toRegex(), "").toInt()
                        if (age > 0) {
                            cleanMap[key] = age
                        } else {
                            ok = false
                        }
                    } catch (ignored: NumberFormatException) {
                        Log.w("Age value was invalid: %s", input[key].toString())
                        ok = false
                    }
                    "favourite_colour" -> {
                        val color = CustomColor.fromString(input[key].toString())
                        cleanMap[key] = color
                    }
                    "hobby" -> {
                        var hobbies: Array<String> = emptyArray()
                        if (input[key] is Array<*>) {
                            hobbies = input[key] as? Array<String> ?: emptyArray()
                        } else if (input[key] is ArrayList<*>) {
                            val list = input[key] as ArrayList<*>?
                            hobbies = emptyArray()
                            var i = 0
                            while (i < list?.size!!) {
                                hobbies[i] = list[i].toString()
                                i++
                            }
                        } else if (input[key] is String) {
                            val hobby = (input[key] as String?)!!.trim { it <= ' ' }
                            if (!hobby.isEmpty()) {
                                if (hobby.matches(Regex("[0-9a-zA-Z ,'/&-]+"))) {
                                    hobbies = hobby.split(",").toTypedArray()
                                } else {
                                    Log.w("Hobby [%s] contained unknown characters.", hobby)
                                    ok = false
                                }
                            }
                        } else {
                            ok = false
                            Log.w("Hobby [%s] was of wrong type")
                        }
                        if (ok) {
                            cleanMap[key] = hobbies
                        }
                    }
                    else -> {
                        Log.w("Unidentified key: %s", key)
                        ok = false
                    }
                }
            }
            if (!ok) {
                Log.w("Input map contained incorrect data")
                throw IllegalPersonException()
            }
            return cleanMap
        }

        /**
         * Generates a list of Person objects from a Data object (database)
         * @param rows : Database object containing the records
         * @return a list of Person objects
         */
        private fun fromData(rows: Data?): List<Person?> {
            val list: MutableList<Person> = ArrayList()
            if (rows != null) {
                val data = rows.toListMap()
                for (datum in data) {
                    try {
                        list.add(fromMap(datum))
                    } catch (ignored: IllegalPersonException) {
                    }
                }
            }
            return Collections.unmodifiableList(list)
        }

        /**
         * Search all people by name
         * @param name : Partial or full string to match
         * @return a list of Person objects found
         */
        fun searchByName(name: String): List<Person?> {
            val db = Database.connect()
            var result: List<Person?> = ArrayList()
            var search = java.lang.String.join("", name.toLowerCase().replace("[^a-z]".toRegex(), ""))
            if (!search.isEmpty()) {
                Log.i("Searching for: %s", search)
                search = "%$search%"
                val rows = db.table(personTable)
                    .where("lower(`first_name`) LIKE ? or lower(`last_name`) LIKE ?", search, search).get()
                result = fromData(rows)
                db.close()
            }
            return result
        }

        /**
         * Get all records as Person
         * @param offset : retrieve starting at this count
         * @param qty  : how many records to get
         * @return list of Person objects in database
         */
        fun getAll(offset: Int, qty: Int): List<Person?> {
            val db = Database.connect()
            val rows = db.table(personTable).limit(qty, offset).get()
            db.close()
            return fromData(rows)
        }

        /**
         * It will create the database if the database doesn't exists.
         * NOTE: You may remove the database file to create it again.
         */
        fun initDB() {
            val dbFile = SysInfo.getFile(Config.get("db.name", "rest") + ".db")
            if (!dbFile.exists()) {
                val db = Database.connect()
                val createSQL = SysInfo.getFile("create.sql")
                if (createSQL.exists()) {
                    try {
                        val query = Files.readString(createSQL.toPath())
                        if (query.isNotEmpty()) {
                            for (command in query.replace("\n".toRegex(), "").split(";").toTypedArray()) {
                                if (command.trim { it <= ' ' }.isNotEmpty()) {
                                    db.set(command)
                                }
                            }
                        }
                    } catch (e: IOException) {
                        Log.w("Unable to open file: %s", createSQL.absoluteFile)
                    }
                }
                db.close()
            }
        }
    }
}