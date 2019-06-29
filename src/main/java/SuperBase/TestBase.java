package SuperBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import ExcelManager.ExcelReader;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;


public class TestBase {

    public static final Logger log = Logger.getLogger(TestBase.class.getSimpleName());
    public static WebDriver driver ;
    public static WebDriverWait wait ;

    public static ExtentReports extent;
    public static ExtentTest Test ;
    public static ITestResult Result ;
    public Properties OR = new Properties();
    public Properties APP = new Properties();


    static{
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_YYYY_HH_mm_ss");
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir")+"/src/main/java/com/pharmeasy/MercuryUI/Reports/"+formatter.format(calendar.getTime())+".html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        log.info("Extent reports initialized");

    }

    /**
     *  Reads Excel Sheet Data
     *  @param excelName - Excel Name
     *  @param sheetName - Sheet Name
     *
     * @return - String[][]
     */
    public String[][] readExcel(String excelName,String sheetName) throws Exception {
        String path = System.getProperty("user.dir")+"//src//main//resources//Data//" + excelName + ".xlsx" ;
        ExcelReader reader = new ExcelReader(path);
        return reader.getDataFromSheet(excelName, sheetName);
    }

    /**
     *  Loads property file
     */
    private void loadFromORproperties() {
        String path = System.getProperty("user.dir")+"//src//main//resources//Config//" ;
        File file = new File(path+"OR.properties");
        try {
            FileInputStream fis = new FileInputStream(file);
            OR.load(fis);
            log.info("Property file successfully loaded from the path : "+path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.info("Property file not found");
            System.out.println("File Not found in the specified location");
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Property file not found");
        }
    }

    /**
     *  Loads property file
     */
    private void loadFromAPPproperties() {
        String path = System.getProperty("user.dir")+"//src//main//resources//Config//" ;
        File file = new File(path+"APP.properties");
        try {
            FileInputStream fis = new FileInputStream(file);
            APP.load(fis);
            log.info("Property file successfully loaded from the path : "+path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.info("Property file not found");
            System.out.println("File Not found in the specified location");
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Property file not found");
        }
    }

    /**
     *  Intializes Logs, Property Files, Browser and Driver
     */
    public void init() {
        PropertyConfigurator.configure(System.getProperty("user.dir")+"//log4j.properties");
        loadFromORproperties();
        loadFromAPPproperties();
        selectBrowser(OR.getProperty("BrowserName"));
        log.info("Browser driver instantiated");
        waitForElementToLoad();
        getBaseUrl(OR.getProperty("baseURL"));
        log.info(" Navigated to the url ");
    }

    /**
     *  Selects Browser based on User specified in Property file
     *  @param browserName - Browser Namb*/
    private void selectBrowser(final String browserName){
        if(browserName.equalsIgnoreCase("firefox")){
            System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"//Drivers//geckodriver.exe");
            driver = new FirefoxDriver();
            wait = new WebDriverWait(driver, 120);
            wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath(""))));
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
            waitForElementToLoad();

        }
        else if(browserName.equalsIgnoreCase("chrome")){
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"//Drivers//chromedriver.exe");
            driver = new ChromeDriver();
            wait = new WebDriverWait(driver, 120);
            waitForElementToLoad();

        }
        else if(browserName.equalsIgnoreCase("htmlunit")){
            //driver = new HtmlUnitDriver() ;
            waitForElementToLoad();
        }
    }

    /**
     * Gets URL from property file
     */
    private void getBaseUrl(String BaseUrl){
        driver.get(BaseUrl);
        try {
            driver.manage().window().maximize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void waitForElementToLoad(){
        driver.manage().timeouts().implicitlyWait(60,TimeUnit.SECONDS);
    }

    public void waitforPageToLoad(){
        driver.manage().timeouts().pageLoadTimeout(60,TimeUnit.SECONDS);
    }

    /**
     *  Takes Screenshot
     *  @param methodName - Method Name
     *  @param packageFolder - Package Folder
     */
    public String getScreenshot(String methodName, String packageFolder){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_YYYY_HH_mm_ss");

        try {
            File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            String ReportDirectory = System.getProperty("user.dir")+"//src//main//java//com//Screenshots/" + packageFolder + "/";
            String destination = ReportDirectory + methodName+"_"+formatter.format(calendar.getTime())+".png";
            File destFile = new File(destination);
            FileHandler.copy(srcFile, destFile);

            return destination ;
        } catch (WebDriverException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null ;
    }

    /**
     * Selects value from the Auto suggested Dropdown
     * This method will get the List Of web elements By the tag Name and searches each element with the text
     *
     * @param text - text
     */
    public void SelectFromAutoSuggestionSearch(String text){
        List<WebElement> AutoSuggestionItemList = driver.findElements(By.tagName("li"));
        for(WebElement we : AutoSuggestionItemList){
            if(we.getText().contains(text)){
                we.click();
                log.info("Clicked on "+text+" from the Auto suggestion list");
                break ;
            }
        }
    }



    public void getResult(ITestResult Result) throws IOException{
        if(Result.getStatus()==ITestResult.SUCCESS){
            Test.log(Status.PASS,  Result.getName()+" Test is Passed");
            String imagePath = getScreenshot(Result.getName(),"SuccessScreenshots");
            Test.addScreenCaptureFromPath(imagePath, "Passed Test case Screen shot");

        }
        else if(Result.getStatus()==ITestResult.FAILURE){
            Test.log(Status.FAIL, Result.getName()+" Test is Failed");
            String imagePath = getScreenshot(Result.getName(),"FailureScreenshots");
            Test.addScreenCaptureFromPath(imagePath, "Failed Test case Screen shot");

        }
        else if(Result.getStatus()==ITestResult.SKIP){
            Test.log(Status.SKIP,Result.getName()+" Test is Skipped");
        }
        else if(Result.getStatus()==ITestResult.STARTED){
            Test.log(Status.INFO,Result.getName()+" Test is Started");
        }
    }


    @BeforeMethod()
    public void beforeMethod(Method Result) throws IOException, InterruptedException{
        Test = extent.createTest(Result.getName());
        Test.log(Status.INFO, Result.getName()+" Test is Started");
        init();
    }

    @AfterMethod()
    public void afterMethod(ITestResult Result) throws IOException{
        getResult(Result);
        driver.close();

    }

    @AfterClass(alwaysRun=true)
    public void endTest(){
        try {
            extent.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}