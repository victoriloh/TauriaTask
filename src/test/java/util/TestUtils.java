package util;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import enums.TargetTypeEnum;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.plexus.util.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author victoriloh24@gmail.com
 */
public class TestUtils extends TestBase {


    /**
     * @param driver
     * @param screenshotName
     * @return
     * @throws IOException
     * @description to take a screenshot
     */
    public static String getScreenshot(WebDriver driver, String screenshotName) throws IOException {

        String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);

        String destination = System.getProperty("user.dir") + "/TestsScreenshots/" + screenshotName + dateName + ".png";

        File finalDestination = new File(destination);
        FileUtils.copyFile(source, finalDestination);
        return destination;
    }


    /**
     * @param type
     * @param element
     * @param value
     * @throws InterruptedException
     * @description to check if the expected text is present in the page.
     */
    public static void assertSearchText(String type, String element, String value) {

        StringBuffer verificationErrors = new StringBuffer();
        TargetTypeEnum targetTypeEnum = TargetTypeEnum.valueOf(type);
        String ttype = null;

        switch (targetTypeEnum) {
            case ID:
                ttype = getDriver().findElement(By.id(element)).getText();
                break;
            case NAME:
                ttype = getDriver().findElement(By.name(element)).getText();
                break;
            case CSSSELECTOR:
                ttype = getDriver().findElement(By.cssSelector(element)).getText();
                break;

            case XPATH:
                ttype = getDriver().findElement(By.xpath(element)).getText();
                break;

            default:
                ttype = getDriver().findElement(By.id(element)).getText();
                break;
        }

        try {
            Assert.assertEquals(ttype, value);
            testInfo.get().log(Status.INFO, value + " found");
        } catch (Error e) {
            verificationErrors.append(e.toString());
            String verificationErrorString = verificationErrors.toString();
            testInfo.get().error(value + " not found");
            testInfo.get().error(verificationErrorString);
        }
    }


    public static void assertText(By locator, String expectedText) {
        String actualText = getDriver().findElement(locator).getText();

        if (actualText.contains(expectedText)) {
            testInfo.get().info(expectedText + " was found");
        } else {
            testInfo.get().error(expectedText + " not found");
            testInfo.get().error("Expected " + expectedText + " but found " + actualText);
        }
    }


    public static String generatePassword() {

        long y = (long) (Math.random() * 1000 + 03330000L);
        String Surfix = "Ilo!!";
        String num = Long.toString(y);
        String number = Surfix + num;
        return number;

    }

    /**
     * @return number
     * @description to generate a 7 digit number
     */
    public static String generatePhoneNumber() {

        long y = (long) (Math.random() * 100000 + 1000000L);
        String num = Long.toString(y);
        return num;

    }

    public static String generateEmail(){
        long y = (long) (Math.random() * 10000 + 100000L);
        String num = Long.toString(y);
        String suffix= "fasterpay@yopmail.com";
        String prefix= num;
        String email = prefix + suffix;
        return email;
    }

    public static String addScreenshot() {

        TakesScreenshot ts = (TakesScreenshot) getDriver();
        File scrFile = ts.getScreenshotAs(OutputType.FILE);

        String encodedBase64 = null;
        FileInputStream fileInputStreamReader = null;
        try {
            fileInputStreamReader = new FileInputStream(scrFile);
            byte[] bytes = new byte[(int) scrFile.length()];
            fileInputStreamReader.read(bytes);
            encodedBase64 = new String(Base64.encodeBase64(bytes));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "data:image/png;base64," + encodedBase64;
    }

    public static boolean isAlertPresents() {
        try {
            getDriver().switchTo().alert();
            return true;
        } // try
        catch (Exception e) {
            return false;
        } // catch
    }



    public static String generateName() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }

    public static String CheckBrowser() {
        //Get Browser name and version.
        Capabilities caps = ((RemoteWebDriver) getDriver()).getCapabilities();
        String browserName = caps.getBrowserName();
        String browserVersion = caps.getVersion();

        //return browser name and version.
        String os = browserName + " " + browserVersion;

        return os;
    }


    public static ExtentTest elementIsPresent(String elementType, String locator, String message){

        WebElement elementPresent = null;

        TargetTypeEnum targetTypeEnum = TargetTypeEnum.valueOf(elementType);
        switch (targetTypeEnum) {
            case ID:
                try{
                    elementPresent = getDriver().findElement(By.id(locator));
                }catch (Exception e){}
                break;
            case NAME:
                try{
                    elementPresent = getDriver().findElement(By.name(locator));
                }catch (Exception e){}
                break;
            case CSSSELECTOR:
                try{
                    elementPresent = getDriver().findElement(By.cssSelector(locator));
                }catch (Exception e){}
                break;
            case XPATH:
                try{
                    elementPresent = getDriver().findElement(By.xpath(locator));
                }catch (Exception e){}
                break;
            default:
                try{
                    elementPresent = getDriver().findElement(By.id(locator));
                }catch (Exception e){}
        }
        if(elementPresent != null){
            return testInfo.get().log(Status.INFO, message+" is present");

        }
        else {
            return testInfo.get().fail(message+" is not present");
        }
    }



    public static void testTitle(String phrase) {
        String word = "<b>"+phrase+"</b>";
        Markup w = MarkupHelper.createLabel(word, ExtentColor.BLUE);
        testInfo.get().info(w);
    }




}
