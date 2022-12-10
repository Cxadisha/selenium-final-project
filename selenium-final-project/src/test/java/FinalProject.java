import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class FinalProject {
    WebDriver driver;

//    @BeforeTest
//    public void beforeTest() {
//
//         single browser
//        WebDriverManager.chromedriver().setup();
//        driver = new ChromeDriver();
//        driver.manage().window().maximize();
//    }

    // cross browser
    @BeforeTest
    @Parameters("browser")
    public void setup(String browser) throws Exception {

        if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
        }

        else if(browser.equalsIgnoreCase("Edge")){
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
            driver.manage().window().maximize();
        }

        else{
            throw new Exception("Browser is not correct");
        }
    }

    @Test
    public void test() {

        chooseMovie();
        bookTicket();
        chooseSeat();
        fillTheForm();

    }

    private void chooseMovie() {
        // - Navigate to the swoop.ge
        driver.get("https://www.swoop.ge/");

        // - Go to 'კინო'
        WebElement movies = driver.findElement(By.xpath("//label[text()='კინო']"));
        movies.click();

        //  - Select the first movie in the returned list and click on ‘ყიდვა’ button
        List<WebElement> moviesList = driver.findElements(By.xpath("//div[@class='movies-deal']"));

        // hover on the first movie
        Actions perform = new Actions(driver);
        WebElement firstMovie = moviesList.get(0);
        perform.moveToElement(firstMovie).perform();

        // click on buy button
        WebElement button = driver.findElement(By.xpath("//div[@class='info-cinema-ticket']"));
        button.click();

    }

    private void bookTicket() {

        WebElement eastPoint = driver.findElement(By.xpath("//a[text()='კავეა ისთ ფოინთი']"));

        WebElement scrollTo = driver.findElement(By.xpath("//div[@class='movie-image new-trailer movie_poster']"));

        // - Scroll and choose ‘კავეა ისთ ფოინთი’
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView();", scrollTo);
        // choose
        eastPoint.click();

        // - Check that only ‘კავეა ისთ ფოინთი’ options are returned
        List<WebElement> options = driver.findElements(By.xpath("//p[@class='cinema-title']"));
        List<WebElement> caveas = new ArrayList<>();

        int count = 0;
        String str = "კავეა ისთ ფოინთი";
        for (int option = 0; option < options.size(); option++) {
            String cinema = options.get(option).getText();
            if (cinema.equals(str)) {
                caveas.add(options.get(option));
            }
        }

        for (int cinema = 0; cinema < caveas.size(); cinema++) {
            String str1 = caveas.get(cinema).getText();
            String str2 = "კავეა ისთ ფოინთი";
            Assert.assertEquals(str1, str2);
        }

        WebElement lastDate = driver.findElement(By.xpath("//div[@id='384933']/child::div/child::ul/child::li[last()]"));
        WebElement lastOption = driver.findElement(By.xpath("//div[@id='384933']/child::div/child::div[last()]"));

        // - save movie name, cinema name
        String movieName = driver.findElement(By.xpath("//p[@class='name']")).getText();
        String cinemaName = eastPoint.getText();

        // - Click on last date
        lastDate.click();

        // save full date and datetime
        String fullDate = lastDate.getAttribute("aria-controls");
        String time = driver.findElement(By.xpath("//div[@id='384933']/child::div/child::div[last()]/child::a/child::p[@style='width:35px;']")).getText();
        // dateFormat output + time
        String datetime = dateFormat(fullDate) + " " + time;

        // scroll to last option (if window is zoomed 150%)
        js.executeScript("arguments[0].scrollIntoView();", lastOption);

        // - Click on last option
        lastOption.click();

        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@class='movie-title']")));

        String actualMovieName = driver.findElement(By.xpath("//p[@class='movie-title']")).getText();
        String actualCinemaName = driver.findElement(By.xpath("//p[@class='movie-cinema'][1]")).getText();
        String actualDateTime = driver.findElement(By.xpath("//p[@class='movie-cinema'][2]")).getText();

        // - Check in opened popup that movie name, cinema and datetime is valid
        Assert.assertEquals(actualMovieName, movieName);
        Assert.assertEquals(actualCinemaName, cinemaName);
        Assert.assertEquals(actualDateTime, datetime);

    }

    // converts "day-choose-13.12.2022" format to "13 დეკემბერი" format
    private String dateFormat(String fullFormat) {
        // "day-choose-13.12.2022"
        String month = fullFormat.substring(14, 16);
        String day = fullFormat.substring(11, 13);
        String str = "";

        switch (month) {
            case "01": str = day + " იანვარი";
                break;
            case "02": str = day + " თებერვალი";
                break;
            case "03": str = day + " მარტი";
                break;
            case "04": str = day + " აპრილი";
                break;
            case "05": str = day + " მაისი";
                break;
            case "06": str = day + " ივნისი";
                break;
            case "07": str = day + " ივლისი";
                break;
            case "08": str = day + " აგვისტო";
                break;
            case "09": str = day + " სექტემბერი";
                break;
            case "10": str = day + " ოქტომბერი";
                break;
            case "11": str = day + " ნოემბერი";
                break;
            case "12": str = day + " დეკემბერი";
                break;
            default:
                System.out.println("switch error");
        }
        return str;
    }

    private void chooseSeat() {

        // - Choose any vacant place
        List<WebElement> numberOfSeats = driver.findElements(By.xpath("//div[@class='container mobile-class-for-height']/child::div"));

        for (int seat = 1; seat <= numberOfSeats.size(); seat++) {

            WebElement currentSeat = driver.findElement(By.xpath("//div[@class='container mobile-class-for-height']/child::div[" + seat + "]"));
            if (currentSeat.isEnabled()) {
                currentSeat.click();
                break;
            }
        }
    }

    private void fillTheForm() {

        // - Register for a new account
        WebDriverWait wait = new WebDriverWait(driver, 20);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//p[@class='register']")));
        element.click();

        // - Choose ‘იურიდიული პირი’
        driver.findElement(By.xpath("//a[@href='#register-content-2']")).click();

        // - Fill all mandatory with not valid data and optional fields, in case of dropdowns choose any
        // non-default option
        new Select(driver.findElement(By.id("lLegalForm"))).selectByValue("ENT");
        driver.findElement(By.id("lName")).sendKeys("The buddy and co.");
        driver.findElement(By.id("lTaxCode")).sendKeys("123");
        driver.findElement(By.id("lCity")).sendKeys("Tbilisi");
        driver.findElement(By.id("lPostalCode")).sendKeys("1");
        driver.findElement(By.id("lContactPersonEmail")).sendKeys("123");
        driver.findElement(By.id("lContactPersonPassword")).sendKeys("ent123");
        driver.findElement(By.id("lContactPersonConfirmPassword")).sendKeys("ent123");
        driver.findElement(By.id("lContactPersonName")).sendKeys("The buddy");
        driver.findElement(By.id("lContactPersonPersonalID")).sendKeys("1");
        driver.findElement(By.id("lContactPersonPhone")).sendKeys("1");

        List<WebElement> registers = driver.findElements(By.xpath("//input[@value='რეგისტრაცია']"));
        for (WebElement register: registers) {
            if (register.isDisplayed()) {register.click();}
        }

        // - Check that error message ‘რეგისტრაციის დროს დაფიქსირდა შეცდომა!’ is appear\
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("legalInfoMassage")));
        String actualMessage = driver.findElement(By.id("legalInfoMassage")).getText();
        String expectedMessage = "რეგისტრაციის დროს დაფიქსირდა შეცდომა!";
        Assert.assertEquals(actualMessage, expectedMessage);
    }

    @AfterTest
    private void afterMethod() {
        driver.quit();
    }
}
