package tauriaSignUp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import util.TestBase;
import util.TestUtils;

import java.io.FileReader;

public class SignUp extends TestBase {
    private String businessName = TestUtils.generateName();
    private String emailAddress = TestUtils.generateEmail();
    private String validPassword = TestUtils.generatePassword();
    @Parameters("testEnv")
    @Test
    public void navigateToSignUpPage(String testEnv) throws Exception{
        TestUtils.testTitle("Navigate to the SignUp Page");
        TestUtils.assertSearchText("XPATH","//div[@id='root']/div/div/div[2]/div/div/header/h1","Sign In");
        TestUtils.assertSearchText("XPATH","//div[@id='root']/div/div/div[2]/div/div/header/h2","Welcome to Tauria");
        TestUtils.assertSearchText("XPATH","//div[@id='root']/div/div/div[2]/div[2]","Don't have an account? CREATE ONE");
        TestUtils.elementIsPresent("XPATH","//div[2]/button","CREATE ONE BUTTON");
        getDriver().findElement(By.xpath("//div[2]/button")).click();
        Thread.sleep(500);
        TestUtils.assertSearchText("XPATH","//div[@id='root']/div/div/div[3]/div/div/header/h1","Set Up Workspace");
        TestUtils.assertSearchText("XPATH","//div[@id='root']/div/div/div/div/div/h1","Secure collaboration platform for businesses");
    }
    @Parameters("testEnv")
    @Test
    public void setUpWorkSpace(String testEnv) throws Exception{
        TestUtils.testTitle("Attempt to create Work space with only numbers");
        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("resources/" + testEnv + "/data.conf.json"));
        JSONObject envs = (JSONObject) config.get("Invalid_business_format");
        String invalid_format_digits_only = (String) envs.get("invalid_format_digits_only");
        String invalid_format_digits_before_letters = (String) envs.get("invalid_format_digits_before_letters");
        String invalid_format_letters_with_space = (String) envs.get("invalid_format_letters_with_space");
        String invalid_format_letters_with_specialCharacter = (String) envs.get("invalid_format_letters_with_specialCharacter");
        getDriver().findElement(By.id("name")).sendKeys(invalid_format_digits_only);
        getDriver().findElement(By.xpath("//button")).click();
        Thread.sleep(300);
        TestUtils.assertSearchText("XPATH","//form/div/div/p","The business name is invalid");
        TestUtils.testTitle("Attempt to create Work space with Digits before letters");
        getDriver().findElement(By.id("name")).clear();
        getDriver().findElement(By.id("name")).sendKeys(invalid_format_digits_before_letters);
        getDriver().findElement(By.xpath("//button")).click();
        Thread.sleep(300);
        TestUtils.assertSearchText("XPATH","//form/div/div/p","The business name is invalid");
        TestUtils.testTitle("Attempt to create Work space with space between letters");
        getDriver().findElement(By.id("name")).clear();
        getDriver().findElement(By.id("name")).sendKeys(invalid_format_letters_with_space);
        getDriver().findElement(By.xpath("//button")).click();
        Thread.sleep(300);
        TestUtils.assertSearchText("XPATH","//form/div/div/p","The business name is invalid");
        TestUtils.testTitle("Attempt to create Work space with space between letters");
        getDriver().findElement(By.id("name")).clear();
        getDriver().findElement(By.id("name")).sendKeys(invalid_format_letters_with_specialCharacter);
        getDriver().findElement(By.xpath("//button")).click();
        Thread.sleep(300);
        TestUtils.assertSearchText("XPATH","//form/div/div/p","The business name is invalid");
        TestUtils.testTitle("Attempt to create Work space with Valid business name");
        getDriver().findElement(By.id("name")).clear();
        getDriver().findElement(By.id("name")).sendKeys(businessName);
        getDriver().findElement(By.xpath("//button")).click();
        Thread.sleep(300);
        WebDriverWait wait = new WebDriverWait(getDriver(),15);
        wait.until(ExpectedConditions.invisibilityOfElementWithText(By.xpath("//div[@id='root']/div/div/div[3]/div/div/header/h1"),"Set Up Workspace"));
        TestUtils.assertSearchText("XPATH","(.//*[normalize-space(text()) and normalize-space(.)='FIRST NAME'])[1]/preceding::h1[1]","Personal Details");
    }
    @Parameters("testEnv")
    @Test
    public void personalDetails(String testEnv) throws Exception{
        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("resources/" + testEnv + "/data.conf.json"));
        JSONObject envs = (JSONObject) config.get("Invalid_personal_details");
        String firstName = (String) envs.get("firstName");
        String lastName = (String) envs.get("lastName");
        String email = (String) envs.get("email");
        TestUtils.testTitle("Attempt to Proceed with an empty Personal Details");
        getDriver().findElement(By.xpath("//div[@id='root']/div/div/div[3]/div/div/form/div/button")).click();
        Thread.sleep(300);
        TestUtils.assertSearchText("XPATH","//div[@id='root']/div/div/div[3]/div/div/p","Please complete the entire form.");
        TestUtils.testTitle("Attempt to Proceed with an Only first name Details provided");
        getDriver().findElement(By.id("first")).sendKeys(firstName);
        getDriver().findElement(By.xpath("//div[@id='root']/div/div/div[3]/div/div/form/div/button")).click();
        Thread.sleep(300);
        TestUtils.assertSearchText("XPATH","//div[2]/p","Please complete the field.");
        TestUtils.assertSearchText("XPATH","//div[3]/p","Please complete the field.");
        TestUtils.testTitle("Attempt to Proceed with invalid Personal Details provided (FirstName,LastName,Email");
        getDriver().findElement(By.id("first")).clear();
        getDriver().findElement(By.id("first")).sendKeys(firstName);
        getDriver().findElement(By.id("last")).sendKeys(lastName);
        getDriver().findElement(By.id("email")).sendKeys(email);
        getDriver().findElement(By.xpath("//div[@id='root']/div/div/div[3]/div/div/form/div/button")).click();
        Thread.sleep(300);
        TestUtils.assertSearchText("XPATH","//div[3]/p","Email is invalid.");
        TestUtils.testTitle("Attempt to Proceed with an valid Personal Details provided");
        getDriver().findElement(By.id("email")).clear();
        getDriver().findElement(By.id("email")).sendKeys(emailAddress);
        getDriver().findElement(By.xpath("//div[@id='root']/div/div/div[3]/div/div/form/div/button")).click();
        Thread.sleep(300);
    }
    @Parameters("testEnv")
    @Test
    public void password(String testEnv) throws Exception{
        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("resources/" + testEnv + "/data.conf.json"));
        JSONObject envs = (JSONObject) config.get("Invalid_password_details");
        String password1 = (String) envs.get("password1");
        String password2 = (String) envs.get("password2");
        String password3 = (String) envs.get("password3");
        String password4 = (String) envs.get("password4");
        String confirm_password = (String) envs.get("confirm_password");
        TestUtils.testTitle("Attempt to Proceed with an empty Password ");
        getDriver().findElement(By.xpath("//form/div/button")).click();
        Thread.sleep(500);
        TestUtils.assertSearchText("XPATH","//div[3]/div/div/p","Please complete the entire form.");
        TestUtils.testTitle("Attempt to Proceed with an Invalid Password format");
        getDriver().findElement(By.id("password")).sendKeys(password1);
        getDriver().findElement(By.xpath("//form/div/button")).click();
        TestUtils.assertSearchText("XPATH","//div[@id='root']/div/div/div[3]/div/div/form/div/div/div/div[2]/div[2]","Weak Password");
        TestUtils.assertSearchText("XPATH","//form/div/div/p","Password must contain an uppercase letter.");
        TestUtils.testTitle("Attempt to Proceed with an Invalid Password format without a number("+password2+")");
        getDriver().findElement(By.id("password")).clear();
        getDriver().findElement(By.id("password")).sendKeys(password2);
        TestUtils.assertSearchText("XPATH","//div[2]/div[2]","Moderate Password");
        getDriver().findElement(By.xpath("//form/div/button")).click();
        TestUtils.assertSearchText("XPATH","//form/div/div/p","Password must contain a number.");
        TestUtils.testTitle("Attempt to Proceed with a Valid Password format ("+password3+")");
        getDriver().findElement(By.id("password")).clear();
        getDriver().findElement(By.id("password")).sendKeys(password3);
        TestUtils.assertSearchText("XPATH","//div[2]/div[2]","Moderate Password");
        TestUtils.testTitle("Attempt to Proceed with a Valid Strong Password format ("+password4+")");
        getDriver().findElement(By.id("password")).clear();
        getDriver().findElement(By.id("password")).sendKeys(password4);
        TestUtils.assertSearchText("XPATH","//div[2]/div[2]","Strong Password");
        TestUtils.testTitle("Attempt to Proceed with a Valid Password AND an invalid Confirm password");
        getDriver().findElement(By.id("confirm")).sendKeys(confirm_password);
        getDriver().findElement(By.xpath("//form/div/button")).click();
        Thread.sleep(500);
        TestUtils.assertSearchText("XPATH","//div[3]/div/div/p","Passwords must match.");
        TestUtils.testTitle("Attempt to Proceed with a Valid Password AND a valid Confirm password");
        getDriver().findElement(By.id("password")).clear();
        getDriver().findElement(By.id("password")).sendKeys(validPassword);
        getDriver().findElement(By.id("confirm")).clear();
        getDriver().findElement(By.id("confirm")).sendKeys(validPassword);
        getDriver().findElement(By.xpath("//form/div/button")).click();
        Thread.sleep(1000);
        WebDriverWait wait = new WebDriverWait(getDriver(),60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='root']/div/div/div[2]/p")));
        Thread.sleep(6000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[4]/div/div[2]/div/div")));
        TestUtils.assertSearchText("XPATH","//div[4]/div/div[2]/div/div","What would you like to start?");
        TestUtils.assertSearchText("XPATH","//h3","Host video calls and conferences");
        TestUtils.assertSearchText("XPATH","//div[2]/div/div/h3","Organize your team schedule");
        TestUtils.assertSearchText("XPATH","//div[2]/div/h3","Message team members");
        TestUtils.assertSearchText("XPATH","//div[2]/div[2]/div/h3","Store and manage sensitive files");
    }
}
