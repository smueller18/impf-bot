package de.tfr.impf.page

import mu.KotlinLogging
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

val log = KotlinLogging.logger("LocationPage")
class LocationPage(driver: WebDriver) : AbstractPage(driver) {

    fun title(): WebElement? = findAnyBy("//h1")

    override fun isDisplayed() = title()?.text == "Wurde Ihr Anspruch auf eine Corona-Schutzimpfung bereits geprüft?"

    /**
     * Fo you have a verification code -> Yes
     */
    fun confirmClaim() {
        findAnyBy(claimSelection("Ja"))?.click()
    }

    private fun claimSelection(text: String) =
        "//input[@type='radio' and @name='vaccination-approval-checked']//following-sibling::span[contains(text(),'$text')]/.."

    fun submitInput() {
        findAnyBy("//button[@type='submit']")?.click()
    }

    fun codeField(index: Int) = findAnyBy("//input[@type='text' and @data-index='$index']")

    private fun fillCodeField(index: Int, code: String) = codeField(index)?.sendKeys(code)

    fun enterCodeSegment0(code: String) = fillCodeField(0, code)

    /**
     * Termin suchen
     */
    fun searchForFreeDate() = submitInput()

    /**
     * Impftermine > Termin suchen
     */
    fun searchForVaccinateDate() = findAnyBy("//button[contains(text(),'Termine suchen')]")?.click()

    fun hasNoVaccinateDateAvailable(): Boolean = (findAnyBy("//span[@class='its-slot-pair-search-no-results']")?.isDisplayed) ?: false

    fun hasVacError(): Boolean = findAll("//span[contains(@class, 'text-pre-wrap') and contains(text(), 'Fehler')]").isNotEmpty()
    // log.debug { "app-count-down: ${findAnyBy("//app-count-down")?.text}" }

}

