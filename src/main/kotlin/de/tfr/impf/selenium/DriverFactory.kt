package de.tfr.impf.selenium

import de.tfr.impf.config.Config
import de.tfr.impf.log
import mu.KotlinLogging
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

fun createDriver(host: String): WebDriver {
    try {
        val nodeURL = "http://" + host + "/wd/hub";
        val capability = DesiredCapabilities.chrome();
        capability.setBrowserName("chrome");
        val chromeDriver = RemoteWebDriver(URL(nodeURL), capability);

        chromeDriver.setTimeOut(Config.searchElementTimeout())
        return chromeDriver
    } catch (e: MalformedURLException) {
        log.error("Failed to setup RemoteWebDriver ($e)")
        exitProcess(1)
    }
}

fun WebDriver.setTimeOut(milliseconds: Long) {
    this.manage()?.timeouts()?.implicitlyWait(milliseconds, TimeUnit.MILLISECONDS)
}
