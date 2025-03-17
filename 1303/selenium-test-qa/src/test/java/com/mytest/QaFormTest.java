package com.mytest;

import java.io.File;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class QaFormTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String PAGE_URL = new File("src/test/resources/qa-test.html").getAbsolutePath();

    @BeforeMethod
    public void setUp() {
        driver = new SafariDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        driver.get("file://" + PAGE_URL);
        login("test@protei.ru", "test");
    }
    
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    // Вспомогательный метод для авторизации
    private void login(String email, String password) {
        driver.findElement(By.id("loginEmail")).sendKeys(email);
        driver.findElement(By.id("loginPassword")).sendKeys(password);
        driver.findElement(By.id("authButton")).click();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputsPage")));
    }
    
    /* Тест 1: Проверка добавления записи с валидными данными */
    @Test
    public void testAddValidFormData() {
        // Заполняем форму валидными данными
        driver.findElement(By.id("dataEmail")).sendKeys("user@example.com");
        driver.findElement(By.id("dataName")).sendKeys("Иван Иванов");
        
        // Выбираем пол "Мужской" (уже выбран по умолчанию)
        
        // Отмечаем чекбоксы
        driver.findElement(By.id("dataCheck11")).click();
        driver.findElement(By.id("dataCheck12")).click();
        
        // Выбираем радиокнопку
        driver.findElement(By.id("dataSelect22")).click();
        
        // Отправляем форму
        driver.findElement(By.id("dataSend")).click();
        
        // Ждем появления модального окна и проверяем его текст
        WebElement modalAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".uk-modal-content")));
        Assert.assertTrue(modalAlert.getText().contains("Данные добавлены."));
        
        // Закрываем модальное окно
        driver.findElement(By.cssSelector(".uk-modal-close")).click();
        
        // Проверяем, что запись добавлена в таблицу
        WebElement lastRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#dataTable tbody tr:last-child")));
        String rowText = lastRow.getText();
        
        Assert.assertTrue(rowText.contains("user@example.com"));
        Assert.assertTrue(rowText.contains("Иван Иванов"));
        Assert.assertTrue(rowText.contains("Мужской"));
        Assert.assertTrue(rowText.contains("1.1, 1.2"));
        Assert.assertTrue(rowText.contains("2.2"));
    }
    
    /* Тест 2: Проверка валидации поля Email в форме анкеты */
    @Test
    public void testInvalidEmailValidation() {
        // Пытаемся отправить форму с некорректным email
        driver.findElement(By.id("dataEmail")).sendKeys("invalid-email");
        driver.findElement(By.id("dataName")).sendKeys("Тест Тестов");
        driver.findElement(By.id("dataSend")).click();
        
        // Проверяем, что появилось сообщение об ошибке
        WebElement errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("emailFormatError")));
        Assert.assertTrue(errorAlert.getText().contains("Неверный формат E-Mail"));
        
        // Проверяем, что запись не была добавлена в таблицу (таблица пустая)
        Assert.assertEquals(driver.findElements(By.cssSelector("#dataTable tbody tr")).size(), 0);
    }
    
    /* Тест 3: Проверка валидации пустого имени */
    @Test
    public void testEmptyNameValidation() {
        // Пытаемся отправить форму с пустым именем
        driver.findElement(By.id("dataEmail")).sendKeys("valid@example.com");
        driver.findElement(By.id("dataName")).sendKeys("");
        driver.findElement(By.id("dataSend")).click();
        
        // Проверяем, что появилось сообщение об ошибке
        WebElement errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("blankNameError")));
        Assert.assertTrue(errorAlert.getText().contains("Поле имя не может быть пустым"));
        
        // Проверяем, что запись не была добавлена в таблицу
        Assert.assertEquals(driver.findElements(By.cssSelector("#dataTable tbody tr")).size(), 0);
    }

        /* Тест 4: Проверка выбора обоих чекбоксов */
    @Test
    public void testBothCheckboxesSelected() {
        // Заполняем форму с выбором обоих чекбоксов
        driver.findElement(By.id("dataEmail")).sendKeys("checkboxes@example.com");
        driver.findElement(By.id("dataName")).sendKeys("Чекбокс Тест");
        
        // Выбираем оба чекбокса
        driver.findElement(By.id("dataCheck11")).click();
        driver.findElement(By.id("dataCheck12")).click();
        
        // Выбираем радиокнопку
        driver.findElement(By.id("dataSelect22")).click();
        
        // Отправляем форму
        driver.findElement(By.id("dataSend")).click();
        
        // Ждем появления модального окна и закрываем его
        WebElement modalAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".uk-modal-content")));
        driver.findElement(By.cssSelector(".uk-modal-close")).click();
        
        // Проверяем, что запись добавлена в таблицу с обоими чекбоксами
        WebElement lastRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#dataTable tbody tr:last-child")));
        String rowText = lastRow.getText();
        
        Assert.assertTrue(rowText.contains("checkboxes@example.com"));
        Assert.assertTrue(rowText.contains("Чекбокс Тест"));
        Assert.assertTrue(rowText.contains("1.1, 1.2")); // Оба чекбокса выбраны
        Assert.assertTrue(rowText.contains("2.2")); // Выбрана радиокнопка 2.2
    }

    /* Тест 5: Проверка выбора радиокнопки 2.3 */
    @Test
    public void testRadioButton23Selected() {
        // Заполняем форму с выбором радиокнопки 2.3
        driver.findElement(By.id("dataEmail")).sendKeys("radio23@example.com");
        driver.findElement(By.id("dataName")).sendKeys("Радио Тест");
        
        // Выбираем радиокнопку 2.3
        driver.findElement(By.id("dataSelect23")).click();
        
        // Отправляем форму
        driver.findElement(By.id("dataSend")).click();
        
        // Ждем появления модального окна и закрываем его
        WebElement modalAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".uk-modal-content")));
        driver.findElement(By.cssSelector(".uk-modal-close")).click();
        
        // Проверяем, что запись добавлена в таблицу с радиокнопкой 2.3
        WebElement lastRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#dataTable tbody tr:last-child")));
        String rowText = lastRow.getText();
        
        Assert.assertTrue(rowText.contains("radio23@example.com"));
        Assert.assertTrue(rowText.contains("Радио Тест"));
        Assert.assertTrue(rowText.contains("Нет")); // Чекбоксы не выбраны
        Assert.assertTrue(rowText.contains("2.3")); // Выбрана радиокнопка 2.3
    }

    /* Тест 6: Проверка граничных значений для имени (1 символ) */
    @Test
    public void testNameBoundaryValue() {
        // Заполняем форму с именем из одного символа
        driver.findElement(By.id("dataEmail")).sendKeys("boundary@example.com");
        driver.findElement(By.id("dataName")).sendKeys("А");
        
        // Отправляем форму
        driver.findElement(By.id("dataSend")).click();
        
        // Ждем появления модального окна и закрываем его
        WebElement modalAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".uk-modal-content")));
        driver.findElement(By.cssSelector(".uk-modal-close")).click();
        
        // Проверяем, что запись добавлена в таблицу с именем из одного символа
        WebElement lastRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#dataTable tbody tr:last-child")));
        String rowText = lastRow.getText();
        
        Assert.assertTrue(rowText.contains("boundary@example.com"));
        Assert.assertTrue(rowText.contains("А"));
    }

    /* Тест 7: Проверка специальных символов в email */
    @Test
    public void testSpecialCharactersInEmail() {
        // Заполняем форму с email, содержащим специальные символы
        driver.findElement(By.id("dataEmail")).sendKeys("special+chars.test@example.com");
        driver.findElement(By.id("dataName")).sendKeys("Специальные Символы");
        
        // Отправляем форму
        driver.findElement(By.id("dataSend")).click();
        
        // Ждем появления модального окна и закрываем его
        WebElement modalAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".uk-modal-content")));
        driver.findElement(By.cssSelector(".uk-modal-close")).click();
        
        // Проверяем, что запись добавлена в таблицу с email, содержащим специальные символы
        WebElement lastRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#dataTable tbody tr:last-child")));
        String rowText = lastRow.getText();
        
        Assert.assertTrue(rowText.contains("special+chars.test@example.com"));
        Assert.assertTrue(rowText.contains("Специальные Символы"));
    }

    /* Тест 8: Проверка длинных значений в полях */
    @Test
    public void testLongValues() {
        // Создаем длинные строки для email и имени
        String longEmail = "very.long.email.address.with.many.characters" + 
                           "@extremely.long.domain.name.with.many.characters.com";
        String longName = "Очень длинное имя пользователя, которое содержит много символов и проверяет " +
                          "обработку длинных строк в форме";
        
        // Заполняем форму с длинными значениями
        driver.findElement(By.id("dataEmail")).sendKeys(longEmail);
        driver.findElement(By.id("dataName")).sendKeys(longName);
        
        // Выбираем чекбокс и радиокнопку
        driver.findElement(By.id("dataCheck11")).click();
        driver.findElement(By.id("dataSelect22")).click();
        
        // Отправляем форму
        driver.findElement(By.id("dataSend")).click();
        
        // Ждем появления модального окна и закрываем его
        WebElement modalAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".uk-modal-content")));
        driver.findElement(By.cssSelector(".uk-modal-close")).click();
        
        // Проверяем, что запись добавлена в таблицу с длинными значениями
        WebElement lastRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#dataTable tbody tr:last-child")));
        String rowText = lastRow.getText();
        
        Assert.assertTrue(rowText.contains(longEmail));
        Assert.assertTrue(rowText.contains(longName));
        Assert.assertTrue(rowText.contains("1.1")); // Выбран чекбокс 1.1
        Assert.assertTrue(rowText.contains("2.2")); // Выбрана радиокнопка 2.2
    }
}