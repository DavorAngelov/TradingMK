package com.tradingmk.backend.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SignupLoginSeleniumTest extends BaseSeleniumTest {

    @Test
    void testSuccessfulSignup() {
        driver.get(BASE_URL + "/signup");
        String uniqueUser = "qauser_" + UUID.randomUUID().toString().substring(0, 8);

        driver.findElement(By.cssSelector("input[placeholder='Username']")).sendKeys(uniqueUser);
        driver.findElement(By.cssSelector("input[placeholder='Email']")).sendKeys(uniqueUser + "@test.com");
        driver.findElement(By.cssSelector("input[placeholder='Password']")).sendKeys("Password123!");
        driver.findElement(By.xpath("//button[text()='Sign Up']")).click();

        WebElement success = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//p[contains(text(), 'signup successfull')]")));
        assertTrue(success.isDisplayed());
    }

    @Test
    void testSignupWithMissingFields_isBlockedByHtmlValidation() {
        driver.get(BASE_URL + "/signup");
        driver.findElement(By.xpath("//button[text()='Sign Up']")).click();
        // required attribute prevents submission; still on signup page
        assertTrue(driver.getCurrentUrl().contains("/signup"));
    }

    @Test
    void testLoginWithInvalidCredentials_showsError() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.cssSelector("input[placeholder='Username']")).sendKeys("nonexistent_user_xyz");
        driver.findElement(By.cssSelector("input[placeholder='Password']")).sendKeys("wrongpass");
        driver.findElement(By.xpath("//button[text()='Log In']")).click();

        WebElement error = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("p.text-red-600")));
        assertTrue(error.isDisplayed());
    }

}