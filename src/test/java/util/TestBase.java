package util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.browserstack.local.Local;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class TestBase {
    public static ExtentReports reports;
    public static ExtentHtmlReporter htmlReporter;
    private static ThreadLocal<ExtentTest> parentTest = new ThreadLocal<ExtentTest>();
    public static ThreadLocal<ExtentTest> testInfo = new ThreadLocal<ExtentTest>();
    private static OptionsManager optionsManager = new OptionsManager();
    public static ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();
    public static String toAddress;
    private Local l;
    public String local = "local";
    public String remoteJenkins = "remote-jenkins";
    public String remoteBrowserStack = "remote-browserStack";

    @Parameters("testEnv")
    public static String myUrl(String testEnv) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("resources/" + testEnv + "/data.conf.json"));
        JSONObject envs = (JSONObject) config.get("Url");
        String url = (String) envs.get("url");

        String myUrl = null;
        if (testEnv.equalsIgnoreCase("stagingData")) {
            myUrl = System.getProperty("instance-url", url);
        } else {
            myUrl = System.getProperty("instance-url", url);
        }
        return myUrl;
    }

    public static String gridUrl = System.getProperty("grid-url", "");

    @BeforeSuite
    @Parameters({"groupReport", "testEnv"})
    public void setUp(String groupReport, String testEnv) throws Exception {

        htmlReporter = new ExtentHtmlReporter(new File(System.getProperty("user.dir") + groupReport));
        //	htmlReporter.loadXMLConfig(String.valueOf(new File(System.getProperty("user.dir") + "/resources/extent-config.xml")));
        htmlReporter.config().setDocumentTitle("FasterPay Report");
        htmlReporter.config().setReportName("Automation Test Reports for FasterPay Dashboard");
        reports = new ExtentReports();
        reports.setSystemInfo("TEST ENVIRONMENT", myUrl(testEnv));
        reports.attachReporter(htmlReporter);
    }

    @Parameters({"myBrowser", "config", "environment", "server", "testEnv"})
    @BeforeClass
    public void beforeClass(String myBrowser, String config_file, String environment, String server, String testEnv) throws Exception {
        ExtentTest parent = reports.createTest(getClass().getName());
        parentTest.set(parent);
        if (server.equals(remoteBrowserStack)) {
            JSONParser parser = new JSONParser();
            JSONObject config = (JSONObject) parser.parse(new FileReader("resources/conf/" + config_file));
            JSONObject envs = (JSONObject) config.get("environments");
            DesiredCapabilities capabilities = new DesiredCapabilities();

            Map<String, String> envCapabilities = (Map<String, String>) envs.get(environment);
            Iterator it = envCapabilities.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
            }
            Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
            it = commonCapabilities.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (capabilities.getCapability(pair.getKey().toString()) == null) {
                    capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
                }
            }
            String username = System.getenv("BROWSERSTACK_USERNAME");
            if (username == null) {
                username = (String) config.get("user");
            }

            String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
            if (accessKey == null) {
                accessKey = (String) config.get("key");
            }

            if (capabilities.getCapability("browserstack.local") != null && capabilities.getCapability("browserstack.local") == "true") {
                l = new Local();
                Map<String, String> options = new HashMap<String, String>();
                options.put("key", accessKey);
                l.start(options);
            }
            driver.set(new RemoteWebDriver(new URL("http://" + username + ":" + accessKey + "@" + config.get("server") + "/wd/hub"), capabilities));
            getDriver().manage().window().maximize();
            getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            getDriver().get(myUrl(testEnv));
        } else if (server.equals(remoteJenkins)) {
            String browser = "chrome";
            if (browser.equalsIgnoreCase(myBrowser)) {
                DesiredCapabilities capability = DesiredCapabilities.chrome();
                capability.setCapability(ChromeOptions.CAPABILITY, optionsManager.getChromeOptions());
                capability.setBrowserName(myBrowser);
                capability.setCapability("name", myUrl(testEnv));
                capability.setPlatform(Platform.ANY);
                driver.set(new RemoteWebDriver(new URL(gridUrl), capability));
            } else {
                DesiredCapabilities capability = DesiredCapabilities.firefox();
                capability.setCapability(FirefoxOptions.FIREFOX_OPTIONS, optionsManager.getFirefoxOptions());
                capability.setCapability(FirefoxProfile.ALLOWED_HOSTS_PREFERENCE, optionsManager.getFirefoxOptions());
                capability.setBrowserName(myBrowser);
                capability.setCapability("name", myUrl(testEnv));
                capability.setPlatform(Platform.ANY);
                driver.set(new RemoteWebDriver(new URL(gridUrl), capability));
            }
            getDriver().manage().window().maximize();
            getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            getDriver().get(myUrl(testEnv));

        } else if (server.equals(local)) {
            // Local Directory
            File classpathRoot = new File(System.getProperty("user.dir"));
            File chromeDriver = new File(classpathRoot, "chromedriver.exe");
            System.setProperty("webdriver.chrome.driver", chromeDriver.getAbsolutePath());
            driver.set(new ChromeDriver(optionsManager.getChromeOptions()));
        }
        getDriver().manage().window().maximize();
        getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        getDriver().get(myUrl(testEnv));

    }


    @AfterClass
    public void afterClass() {
        getDriver().quit();
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    @BeforeMethod(description = "fetch test cases name")
    public void register(Method method) throws InterruptedException {

        ExtentTest child = parentTest.get().createNode(method.getName());
        testInfo.set(child);
        testInfo.get().assignCategory("Test");
        testInfo.get().getModel().setDescription(TestUtils.CheckBrowser());
        if (TestUtils.isAlertPresents()) {
            getDriver().switchTo().alert().accept();
            Thread.sleep(1000);
        }

    }

    @AfterMethod(description = "to display the result after each test method")
    public void captureStatus(ITestResult result) throws IOException {
        for (String group : result.getMethod().getGroups())
            testInfo.get().assignCategory(group);
        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshotPath = TestUtils.addScreenshot();
            testInfo.get().addScreenCaptureFromBase64String(screenshotPath);
            testInfo.get().fail(result.getThrowable());
            getDriver().navigate().refresh();
        }
        else if (result.getStatus() == ITestResult.SKIP)
            testInfo.get().skip(result.getThrowable());
        else
            testInfo.get().pass(result.getName() +" Test passed");
        reports.flush();
    }



}
