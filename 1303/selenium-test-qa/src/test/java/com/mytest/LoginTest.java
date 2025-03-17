package com.mytest;

import java.nio.file.Paths;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class LoginTest {
    // Объявляем драйвер как поле класса, чтобы он был доступен во всех методах
    private WebDriver driver;
    
    // Этот метод будет выполняться перед каждым тестом
    @BeforeMethod
    public void setUp() {
        // Создаем экземпляр SafariDriver
        driver = new SafariDriver();
        
        // Максимизируем окно браузера
        driver.manage().window().maximize();
        
        // Устанавливаем неявное ожидание
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        // Путь к HTML файлу
        String htmlPath = Paths.get("src/test/resources/qa-test.html").toAbsolutePath().toString();
        driver.get("file://" + htmlPath);
    }
    
    // Тест успешной авторизации
    @Test
    public void testSuccessfulLogin() {
        // Находим поле для ввода email и вводим корректный email
        WebElement emailField = driver.findElement(By.id("loginEmail"));
        emailField.sendKeys("test@protei.ru");
        
        // Находим поле для ввода пароля и вводим корректный пароль
        WebElement passwordField = driver.findElement(By.id("loginPassword"));
        passwordField.sendKeys("test");
        
        // Находим кнопку входа и кликаем по ней
        WebElement loginButton = driver.findElement(By.id("authButton"));
        loginButton.click();
        
        // Проверяем, что после успешного входа отображается страница с данными
        WebElement dataPage = driver.findElement(By.id("inputsPage"));
        Assert.assertTrue(dataPage.isDisplayed(), "Страница с данными не отображается после авторизации");
    }
    
    // Тест некорректного формата email
    @Test
    public void testInvalidEmailFormat() {
        // Находим поле для ввода email и вводим некорректный email
        WebElement emailField = driver.findElement(By.id("loginEmail"));
        emailField.sendKeys("неправильный-email");
        
        // Находим поле для ввода пароля и вводим любой пароль
        WebElement passwordField = driver.findElement(By.id("loginPassword"));
        passwordField.sendKeys("любой-пароль");
        
        // Находим кнопку входа и кликаем по ней
        WebElement loginButton = driver.findElement(By.id("authButton"));
        loginButton.click();
        
        // Проверяем, что появилось сообщение о неверном формате email
        WebElement errorMessage = driver.findElement(By.id("emailFormatError"));
        Assert.assertTrue(errorMessage.isDisplayed(), "Сообщение о неверном формате email не отображается");
    }
    
    // Тест неверного пароля
    @Test
    public void testInvalidPassword() {
        // Находим поле для ввода email и вводим корректный email
        WebElement emailField = driver.findElement(By.id("loginEmail"));
        emailField.sendKeys("test@protei.ru");
        
        // Находим поле для ввода пароля и вводим неверный пароль
        WebElement passwordField = driver.findElement(By.id("loginPassword"));
        passwordField.sendKeys("неверный-пароль");
        
        // Находим кнопку входа и кликаем по ней
        WebElement loginButton = driver.findElement(By.id("authButton"));
        loginButton.click();
        
        // Проверяем, что появилось сообщение о неверном email или пароле
        WebElement errorMessage = driver.findElement(By.id("invalidEmailPassword"));
        Assert.assertTrue(errorMessage.isDisplayed(), "Сообщение о неверном email или пароле не отображается");
    }
    
    // Этот метод будет выполняться после каждого теста
    @AfterMethod
    public void tearDown() {
        // Закрываем браузер
        if (driver != null) {
            driver.quit();
        }
    }
}