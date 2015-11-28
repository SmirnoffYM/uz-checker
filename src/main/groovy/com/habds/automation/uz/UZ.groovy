package com.habds.automation.uz

import com.google.common.base.Predicate
import groovy.json.JsonOutput
import groovy.json.StringEscapeUtils
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait

/**
 * TODO: description
 *
 * @author Yurii Smyrnov
 * @version 1
 * @since 11/27/15 4:54 PM
 */
class UZ {

    static void main(String[] args) {
        String from = args[0]
        String to = args[1]
        String day = args[2]
        int hour = args.length > 3 ? Integer.parseInt(args[3]) : 0

        def result = findTrains(from, to, day, hour)
        println StringEscapeUtils.unescapeJavaScript(JsonOutput.prettyPrint(JsonOutput.toJson(result)))
    }

    static def findTrains(String from, String to, String day, int hour) {
        def driver = getDriver()
        try {
            driver.get("http://booking.uz.gov.ua/")

            driver.findElementByName("station_from").sendKeys(from)
            def optionsSelection = "//div[@id='stations_from']/div"
            wait(driver, 1) { RemoteWebDriver d -> d.findElementsByXPath(optionsSelection).size() > 0 }
            driver.findElementByXPath(optionsSelection).click()

            driver.findElementByName("station_till").sendKeys(to)
            optionsSelection = "//div[@id='stations_till']/div"
            wait(driver, 1) { RemoteWebDriver d -> d.findElementsByXPath(optionsSelection).size() > 0 }
            driver.findElementByXPath(optionsSelection).click()

            def dateInput = driver.findElementById("date_dep")
            dateInput.clear()
            dateInput.sendKeys(day)
            Thread.sleep(100)
            dateInput.sendKeys(Keys.ENTER)

            def hourLabel = String.format("%02d", hour) + ":00"
            new Select(driver.findElementByName("time_dep")).selectByVisibleText(hourLabel)

            driver.findElementByXPath("//button[@name='search']").click()

            def findTrainRows = { RemoteWebDriver d -> d.findElementsByXPath("//table[@id='ts_res_tbl']/tbody/tr") }
            wait(driver, 3) { RemoteWebDriver d -> findTrainRows.call(d).size() > 0 }
            def rows = findTrainRows.call(driver)

            return extractData(rows)
        } finally {
            driver.close()
        }
    }

    private static def extractData(List<WebElement> elements) {
        return elements.collect {
            def stations = it.findElement(By.xpath(".//td[@class='stations']")).text.split("\n")*.trim()
            def date = it.findElements(By.xpath(".//td[@class='date']/div/span"))*.text
            def time = it.findElement(By.xpath(".//td[@class='time']")).text.split("\n")*.trim()
            [
                    "number"   : it.findElement(By.xpath(".//a")).text,
                    "from"     : stations[0],
                    "to"       : stations[1],
                    "departure": date[0] + " " + time[0],
                    "arrival"  : date[1] + " " + time[1],
                    "duration" : it.findElement(By.xpath(".//td[@class='dur']")).text,
                    "places"   : it
                            .findElements(By.xpath(".//td[@class='place']/div"))
                            .groupBy { it.getAttribute("title") }
                            .collectEntries { k, v -> [(k): v[0].findElement(By.xpath(".//b")).text] }
            ]
        }
    }

    private static RemoteWebDriver getDriver() {
        def profile = new FirefoxProfile()
        profile.setPreference("intl.accept_languages", "uk")
        return new FirefoxDriver(profile)
    }

    private static void wait(RemoteWebDriver driver, long seconds, Closure<Boolean> condition) {
        new WebDriverWait(driver, seconds).until(new Predicate<WebDriver>() {
            @Override
            boolean apply(WebDriver input) {
                return condition.call(input)
            }
        })
    }
}
