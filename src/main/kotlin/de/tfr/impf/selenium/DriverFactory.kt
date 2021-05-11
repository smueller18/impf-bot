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

fun createDriverGrid(): WebDriver {
    try {
        val nodeURL = "http://" + Config.pathDriver + "/wd/hub";
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

fun createDriver(): WebDriver {
    if (Config.pathDriver.contains(":")) {
        return createDriverGrid()
    }
    System.setProperty(Config.nameDriver, Config.pathDriver + Config.exeDriver)
    val chromeOptions = ChromeOptions()
    val chromeDriver = ChromeDriver(chromeOptions)
    chromeDriver.setTimeOut(Config.searchElementTimeout())
    return chromeDriver
}

fun WebDriver.setTimeOut(milliseconds: Long) {
    this.manage()?.timeouts()?.implicitlyWait(milliseconds, TimeUnit.MILLISECONDS)
}
