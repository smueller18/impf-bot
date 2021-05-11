package de.tfr.impf

import de.tfr.impf.config.Config
import de.tfr.impf.page.*
import de.tfr.impf.selenium.createDriver
import de.tfr.impf.slack.SlackClient
import mu.KotlinLogging
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import java.lang.System.currentTimeMillis

val log = KotlinLogging.logger("ReportJob")

class ReportJob {
    private var driver: WebDriver = createDriver()

    private val locations = Config.locationList()

    fun reportFreeSlots() {
        log.info { "Started checking these ${locations.size} locations:\n$locations" }
        while (true) {
            checkLocations()
        }
    }

    private fun checkLocations() {
        for (location in locations) {
            try {
                checkLocation(location)
            } catch (e: Exception) {
                log.error(e) { "Failed to check location: $location\n" + e.message }
            }
            log.debug { "Waiting for ${Config.waitingTime()/1000}s" }
            Thread.sleep(Config.waitingTime())
        }
    }


    private fun checkLocation(location: Config.Location) {
        val mainPage = openMainPage(driver)
        val cookieNag = CookieNagComponent(driver)
        mainPage.isDisplayed()
        mainPage.chooseLocation(location.name)
        log.debug { "Waiting for ${Config.waitingTimeForBrowser()/1000}s" }
        Thread.sleep(Config.waitingTimeForBrowser())
        cookieNag.acceptCookies()
        mainPage.submitLocation()

        takeASeatInWaitingRoom()

        log.debug { "delete window.sessionStorage[\"ets-session-count-down-timer-init\"]" }
        val js: JavascriptExecutor = driver as JavascriptExecutor
        js.executeScript("console.log('delete window.sessionStorage[\"ets-session-count-down-timer-init\"]')")
        js.executeScript("delete window.sessionStorage[\"ets-session-count-down-timer-init\"]")

        val locationPage = LocationPage(driver)
        if (locationPage.isDisplayed()) {
            log.debug { "Changed to location page: $location" }
            if (location.hasCode()) {
                searchFreeDateByCode(locationPage, location)
            }
        }
    }

    private fun takeASeatInWaitingRoom() {
        val waitingRoom = WaitingRoomPage(driver)
        val waitingTimeEnd = currentTimeMillis() + Config.waitingTimeInWaitingRoomTotal()
        while (waitingRoom.isDisplayed() && currentTimeMillis() < waitingTimeEnd) {
            log.debug { "Waiting in WaitingRoomPage for ${Config.waitingTimeInWaitingRoom()/1000}s (time remaining: ${(waitingTimeEnd - currentTimeMillis())/1000/60}m) ..." }
            Thread.sleep(Config.waitingTimeInWaitingRoom())
        }
    }

    private fun searchFreeDateByCode(
        locationPage: LocationPage,
        location: Config.Location
    ) {
        locationPage.confirmClaim()
        val code = location.placementCode
        if (code != null) {
            locationPage.enterCodeSegment0(code)
            locationPage.searchForFreeDate()
            locationPage.searchForVaccinateDate()
            val bookingPage = BookingPage(driver)

            if (locationPage.hasNoVaccinateDateAvailable() || locationPage.hasVacError() || locationPage.hasSyntaxError()) {
                log.debug { "Correct code, but not free vaccination slots: $location" }
            } else if (bookingPage.isDisplayed() && bookingPage.isDisplayingVaccinationDates()) {
                sendMessageFoundDates(location)
                waitLongForUserInput()
            } else {
                log.debug { "Correct code, we can't see the bookable vaccination dates: $location" }
            }
        }
    }

    private fun waitLongForUserInput() {
        Thread.sleep(Config.waitingTimeForUserAction())
    }

    private fun sendMessageFoundDates(location: Config.Location) {
        val message = "Found free vaccination dates in location ${location.name}:${driver.currentUrl}\n" +
                "Ten minutes left to choose a date."
        sendMessage(message)
    }

    private fun sendMessage(message: String) {
        log.info { message }
        if (Config.isSlackEnabled()) {
            SlackClient().sendMessage(message)
        }
    }

    private fun openMainPage(driver: WebDriver): MainPage {
        val mainPage = MainPage(driver)
        mainPage.open()
        log.debug { "Choose State: " + mainPage.chooseState()?.text }
        mainPage.chooseState()?.click()
        mainPage.chooseStateItemBW()
        return mainPage
    }
}

