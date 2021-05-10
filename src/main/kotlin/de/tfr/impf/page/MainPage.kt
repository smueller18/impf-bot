package de.tfr.impf.page

import de.tfr.impf.config.Config
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.html5.WebStorage

/**
 * Main page with location selection
 */
class MainPage(driver: WebDriver) : AbstractPage(driver) {

    fun open() {
        driver.get(Config.mainPageUrl)
        if (driver is WebStorage) {
            log.debug { "Delete session and local storage" }
            val webStorage = driver
            webStorage.sessionStorage.clear()
            webStorage.localStorage.clear()
        } else {
            log.debug { "Driver is not of type WebStorage" }
        }
    }

    fun chooseState(): WebElement? = findAll("//span[@role='combobox']").firstOrNull()

    fun chooseLocation(): WebElement? = findAll("//span[@role='combobox']").getOrNull(1)

    fun chooseStateItemBW() =
        findAll("//li[@role='option' and contains(text() , 'Baden-Württemberg')]").firstOrNull()?.click()

    fun title(): WebElement = findBy("//h1")

    override fun isDisplayed() = title().text == "Buchen Sie die Termine für Ihre Corona-Schutzimpfung"

    fun chooseLocation(locationName: String) {
        chooseLocation()?.click()
        findAnyBy("//li[@role='option' and contains(text() , '$locationName')]")?.click()
    }

    fun submitLocation() {
        findAnyBy("//button[@type='submit']")?.click()
    }


}


