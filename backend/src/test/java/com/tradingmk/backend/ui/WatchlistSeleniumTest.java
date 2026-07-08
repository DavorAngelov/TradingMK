package com.tradingmk.backend.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WatchlistSeleniumTest extends BaseSeleniumTest {

    @BeforeEach
    void loginFirst() {
        driver.get(BASE_URL + "/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='Username']")
        )).sendKeys("davor");

        driver.findElement(
                By.cssSelector("input[placeholder='Password']")
        ).sendKeys("davor");

        driver.findElement(
                By.xpath("//button[contains(.,'Log In')]")
        ).click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }


    @Test
    @Order(1)
    void testAddStockToWatchlistWithPriceAlerts() {

        driver.get(BASE_URL + "/detailed/KMB");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Add to Watchlist')]")
        )).click();


        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='Price Above']")
        )).sendKeys("500");


        driver.findElement(
                By.cssSelector("input[placeholder='Price Below']")
        ).sendKeys("100");


        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Save')]")
        )).click();


        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }


    @Test
    @Order(2)
    void testAddStockToWatchlistWithoutPriceAlerts() {

        driver.get(BASE_URL + "/detailed/KMB");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Add to Watchlist')]")
        )).click();


        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Save')]")
        )).click();


        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }


    @Test
    @Order(3)
    void testDeleteEntryFromWatchlist() {

        // Create data first so this test does not depend on other tests
        driver.get(BASE_URL + "/detailed/KMB");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Add to Watchlist')]")
        )).click();


        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Save')]")
        )).click();


        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();


        // Delete from watchlist
        driver.get(BASE_URL + "/watchlist");


        List<WebElement> deleteButtons =
                wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.cssSelector("svg.text-red-500"),
                        0
                ));


        int countBefore = deleteButtons.size();


        deleteButtons.get(0).click();


        wait.until(d ->
                d.findElements(By.cssSelector("svg.text-red-500")).size()
                        < countBefore
        );


        assertTrue(
                driver.findElements(By.cssSelector("svg.text-red-500")).size()
                        < countBefore
        );
    }
}