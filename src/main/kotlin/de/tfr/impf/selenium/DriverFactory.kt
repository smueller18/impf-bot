package de.tfr.impf.selenium

import de.tfr.impf.config.Config
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.util.concurrent.TimeUnit

fun createDriver(): ChromeDriver {
    System.setProperty(Config.nameDriver, Config.pathDriver + Config.exeDriver)
    val chromeOptions = ChromeOptions()
    val chromeDriver = ChromeDriver(chromeOptions)
    chromeDriver.setTimeOut(Config.searchElementTimeout())
    return chromeDriver
}

fun WebDriver.setTimeOut(milliseconds: Long) {
    this.manage()?.timeouts()?.implicitlyWait(milliseconds, TimeUnit.MILLISECONDS)
}
