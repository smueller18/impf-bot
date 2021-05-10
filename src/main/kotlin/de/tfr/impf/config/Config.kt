package de.tfr.impf.config

import de.tfr.impf.log
import java.util.concurrent.TimeUnit

object Config : KProperties() {

    init {
        loadProperties("config.properties")
        log.info { "Loaded properties" }
    }

    val mainPageUrl: String by lazyProperty()
    private val locations: String by lazyProperty()

    /**
     * Comma separated list of locations (e.g. "69123 Heidelberg,76137 Karlsruhe")
     */
    fun locationList() = locations.split(",").map(this::parseLocation)

    val sendRequest: Boolean by lazyBoolProperty()

    private val waitingTime: Long by lazyLongProperty()

    /**
     * Waiting time before checking the next location in milliseconds [ms]
     */
    fun waitingTime(): Long = TimeUnit.SECONDS.toMillis(waitingTime)

    private val searchElementTimeout: Long by lazyLongProperty()

    /**
     * Timeout when searching an element on the page in milliseconds [ms]
     */
    fun searchElementTimeout(): Long = TimeUnit.SECONDS.toMillis(searchElementTimeout)

    private val waitingTimeForUserAction: Long by lazyLongProperty()

    private val waitingTimeForBrowser: Long by lazyLongProperty()

    private val waitingTimeInWaitingRoom: Long by lazyLongProperty()
    private val waitingTimeInWaitingRoomTotal: Long by lazyLongProperty()


    /**
     * Explicit waiting time for browser updates in milliseconds [ms]
     */
    fun waitingTimeForBrowser(): Long = TimeUnit.SECONDS.toMillis(waitingTimeForBrowser)

    /**
     * Waiting time for a manual user interaction in milliseconds [ms]
     */
    fun waitingTimeForUserAction(): Long = TimeUnit.MINUTES.toMillis(waitingTimeForUserAction)

    /**
     * Total waiting time in Waiting Room in milliseconds [ms]
     */
    fun waitingTimeInWaitingRoomTotal(): Long = TimeUnit.MINUTES.toMillis(waitingTimeInWaitingRoomTotal)

    /**
     * Single waiting time in Waiting Room in milliseconds [ms]
     */
    fun waitingTimeInWaitingRoom(): Long = TimeUnit.SECONDS.toMillis(waitingTimeInWaitingRoom)

    val nameDriver: String by lazyProperty()
    val exeDriver: String by lazyProperty()
    val pathDriver: String by lazyProperty()

    fun isSlackEnabled() = slackEnabled
    val slackBotApiToken: String by lazyProperty()
    private val slackEnabled: Boolean by lazyBoolProperty()
    val slackBotChannel: String by lazyProperty()

    /**
     * @locationStatement location name with optional verification code in square brackets. e.g. "69123 Heidelberg[XXXX-XXXX-XXXX]]"
     */
    private fun parseLocation(locationStatement: String): Location {
        val placementCode = locationStatement.substringAfter("[", "").substringBefore("]", "").ifEmpty { null }
        val name = locationStatement.substringBefore("[")
        return Location(name, placementCode)
    }

    class Location(val name: String, val placementCode: String?) {
        fun hasCode() = placementCode != null

        private fun getCodeSegment(index: Int): String = (placementCode?.split("-")?.get(index)).orEmpty()
        fun getCodeSegment0(): String = getCodeSegment(0)
        fun getCodeSegment1(): String = getCodeSegment(1)
        fun getCodeSegment2(): String = getCodeSegment(2)

        override fun toString(): String {
            return name
        }

    }
}