/*
 * Copyright (c) 2022 Anh Tester
 * Automation Framework Selenium
 */

package anhtester.com.keywords;

import anhtester.com.constants.FrameworkConstants;
import anhtester.com.driver.DriverManager;
import anhtester.com.enums.FailureHandling;
import anhtester.com.helpers.Helpers;
import anhtester.com.report.AllureManager;
import anhtester.com.report.ExtentReportManager;
import anhtester.com.report.ExtentTestManager;
import anhtester.com.utils.BrowserInfoUtils;
import anhtester.com.utils.DateUtils;
import anhtester.com.utils.Log;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v109.network.Network;
import org.openqa.selenium.devtools.v109.network.model.Headers;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.*;

import static anhtester.com.constants.FrameworkConstants.*;

/**
 * Keyword WebUI l?? class chung l??m th?? vi???n x??? l?? s???n v???i nhi???u h??m custom t??? Selenium v?? Java.
 * Tr??? v??? l?? m???t Class ch???a c??c h??m Static. G???i l???i d??ng b???ng c??ch l???y t??n class ch???m t??n h??m (WebUI.method)
 */
public class WebUI {

    private static SoftAssert softAssert = new SoftAssert();

    public static void stopSoftAssertAll() {
        softAssert.assertAll();
    }

    public static void smartWait() {
        if (ACTIVE_PAGE_LOADED.trim().toLowerCase().equals("true")) {
            waitForPageLoaded();
        }
        sleep(WAIT_SLEEP_STEP);
    }

    public static void addScreenshotToReport(String screenshotName) {
        if (SCREENSHOT_ALL_STEPS.equals(YES)) {
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.addScreenShot(Helpers.makeSlug(screenshotName));
            }
            //CaptureHelpers.captureScreenshot(DriverManager.getDriver(), Helpers.makeSlug(screenshotName));
            AllureManager.takeScreenshotStep();
        }
    }
    //public static void dropDowns(){
        //WebElement staticDropDown=getWebElement(By.xpath("//a[@id=\"kc-current-locale-link\"]"));
        //Select dropdown=new Select(staticDropDown);
        //dropdown.selectByVisibleText("English");
        //System.out.println(dropdown.getFirstSelectedOption().getText());

    //}
    //public static void actions (){

      //  Actions ac= new Actions();
        //WebElement move=getWebElement(By.xpath("//a[@id=\\\"kc-current-locale-link\\\"]"));
        //ac.moveToElement(move).build().perform();


    //}
    public static String getPathDownloadDirectory() {
        String path = "";
        String machine_name = System.getProperty("user.home");
        path = machine_name + File.separator + "Downloads";
        return path;
    }

    public static int countFilesInDownloadDirectory() {
        String pathFolderDownload = getPathDownloadDirectory();
        File file = new File(pathFolderDownload);
        int i = 0;
        for (File listOfFiles : file.listFiles()) {
            if (listOfFiles.isFile()) {
                i++;
            }
        }
        return i;
    }

    public static boolean verifyFileContainsInDownloadDirectory(String fileName) {
        boolean flag = false;
        try {
            String pathFolderDownload = getPathDownloadDirectory();
            File dir = new File(pathFolderDownload);
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                flag = false;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains(fileName)) {
                    flag = true;
                }
            }
            return flag;
        } catch (Exception e) {
            e.getMessage();
            return flag;
        }
    }

    public static boolean verifyFileEqualsInDownloadDirectory(String fileName) {
        boolean flag = false;
        try {
            String pathFolderDownload = getPathDownloadDirectory();
            File dir = new File(pathFolderDownload);
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                flag = false;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals(fileName)) {
                    flag = true;
                }
            }
            return flag;
        } catch (Exception e) {
            e.getMessage();
            return flag;
        }
    }

    public static Boolean verifyDownloadFileContainsNameCompletedWaitTimeout(String fileName, int timeoutSeconds) {
        boolean check = false;
        int i = 0;
        while (i < timeoutSeconds) {
            boolean exist = verifyFileContainsInDownloadDirectory(fileName);
            if (exist == true) {
                i = timeoutSeconds;
                return check = true;
            }
            sleep(1);
            i++;
        }
        return check;
    }


    public static Boolean verifyDownloadFileEqualsNameCompletedWaitTimeout(String fileName, int timeoutSeconds) {
        boolean check = false;
        int i = 0;
        while (i < timeoutSeconds) {
            boolean exist = verifyFileEqualsInDownloadDirectory(fileName);
            if (exist == true) {
                i = timeoutSeconds;
                return check = true;
            }
            sleep(1);
            i++;
        }
        return check;
    }

    public static void deleteAllFileInDownloadDirectory() {
        try {
            String pathFolderDownload = getPathDownloadDirectory();
            File file = new File(pathFolderDownload);
            File[] listOfFiles = file.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    new File(listOfFiles[i].toString()).delete();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static void deleteAllFileInDirectory(String pathDirectory) {
        try {
            File file = new File(pathDirectory);
            File[] listOfFiles = file.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    new File(listOfFiles[i].toString()).delete();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Step("Verify File Downloaded With JS [Equals]: {0}")
    public static Boolean verifyFileDownloadedWithJS_Equals(String fileName) {
        getURL("chrome://downloads");
        sleep(3);
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        String element = (String) js.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('#show').getAttribute('title')");
        File file = new File(element);
        Log.info(element);
        Log.info(file.getName());
        if (file.exists() && file.getName().trim().equals(fileName)) {
            return true;
        } else {
            return false;
        }
    }

    @Step("Verify File Downloaded With JS [Contains]: {0}")
    public static Boolean verifyFileDownloadedWithJS_Contains(String fileName) {
        getURL("chrome://downloads");
        sleep(3);
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        String element = (String) js.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('#show').getAttribute('title')");
        File file = new File(element);
        Log.info(element);
        Log.info(file.getName());
        if (file.exists() && file.getName().trim().contains(fileName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Login as Authentication on URL
     *
     * @param url
     * @param username
     * @param password
     */
    @Step("Get to URL {0} with authentication")
    public static void getToUrlAuthentication(String url, String username, String password) {
        // Get the devtools from the running driver and create a session
        DevTools devTools = ((HasDevTools) DriverManager.getDriver()).getDevTools();
        devTools.createSession();

        // Enable the Network domain of devtools
        devTools.send(Network.enable(Optional.of(100000), Optional.of(100000), Optional.of(100000)));
        String auth = username + ":" + password;

        // Encoding the username and password using Base64 (java.util)
        String encodeToString = Base64.getEncoder().encodeToString(auth.getBytes());

        // Pass the network header -> Authorization : Basic <encoded String>
        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + encodeToString);
        devTools.send(Network.setExtraHTTPHeaders(new Headers(headers)));

        Log.info("getToUrlAuthentication with URL: " + url);
        Log.info("getToUrlAuthentication with Username: " + username);
        Log.info("getToUrlAuthentication with Password: " + password);
        // Load the application url
        getURL(url);
        Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(3));

    }

    /**
     * Get code text of QR Code image
     *
     * @param by l?? element d???ng ?????i t?????ng By
     * @return text of QR Code
     */
    @Step("Get QR code from image {0}")
    public static String getQRCodeFromImage(By by) {
        String qrCodeURL = WebUI.getAttributeElement(by, "src");
        //Create an object of URL Class
        URL url = null;
        try {
            url = new URL(qrCodeURL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        //Pass the URL class object to store the file as image
        BufferedImage bufferedimage = null;
        try {
            bufferedimage = ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Process the image
        LuminanceSource luminanceSource = new BufferedImageLuminanceSource(bufferedimage);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
        //To Capture details of QR code
        Result result = null;
        try {
            result = new MultiFormatReader().decode(binaryBitmap);
        } catch (com.google.zxing.NotFoundException e) {
            throw new RuntimeException(e);
        }
        return result.getText();
    }

    //Handle HTML5 validation message and valid value

    /**
     * Ki???m tra field c?? b???t bu???c nh???p hay kh??ng
     *
     * @param by l?? element thu???c ki???u By
     * @return true/false t????ng ???ng v???i required
     */
    @Step("Verify HTML5 message of element {0} required field")
    public static Boolean verifyHTML5RequiredField(By by) {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        Boolean verifyRequired = (Boolean) js.executeScript("return arguments[0].required;", getWebElement(by));
        return verifyRequired;
    }

    /**
     * Ki???m tra gi?? tr??? trong field nh???p c?? h???p l??? hay ch??a
     *
     * @param by l?? element thu???c ki???u By
     * @return true/false t????ng ???ng v???i Valid
     */
    @Step("Verify HTML5 message of element {0} valid")
    public static Boolean verifyHTML5ValidValueField(By by) {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        Boolean verifyValid = (Boolean) js.executeScript("return arguments[0].validity.valid;", getWebElement(by));
        return verifyValid;
    }

    /**
     * L???y ra c??u th??ng b??o t??? HTML5 c???a field
     *
     * @param by l?? element thu???c ki???u By
     * @return gi?? tr??? chu???i Text c???a c??u th??ng b??o (String)
     */
    @Step("Get HTML5 message of element {0}")
    public static String getHTML5MessageField(By by) {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        String message = (String) js.executeScript("return arguments[0].validationMessage;", getWebElement(by));
        return message;
    }


    /**
     * Kh??i ph???c c???a s??? v?? ?????t k??ch th?????c c???a s???.
     *
     * @param widthPixel  is Width with Pixel
     * @param heightPixel is Height with Pixel
     */
    public static void setWindowSize(int widthPixel, int heightPixel) {
        DriverManager.getDriver().manage().window().setSize(new Dimension(widthPixel, heightPixel));
    }

    /**
     * Di chuy???n c???a s??? ?????n v??? tr?? ???? ch???n X, Y t??nh t??? 0 g???c tr??i tr??n c??ng
     *
     * @param X (int) - ngang
     * @param Y (int) - d???c
     */
    public static void setWindowPosition(int X, int Y) {
        DriverManager.getDriver().manage().window().setPosition(new Point(X, Y));
    }

    public static void maximizeWindow() {
        DriverManager.getDriver().manage().window().maximize();
    }

    public static void minimizeWindow() {
        DriverManager.getDriver().manage().window().minimize();
    }

    /**
     * Ch???p ???nh m??n h??nh t???i v??? tr?? element. Kh??ng ch???p h???t c??? m??n h??nh.
     *
     * @param by          l?? element thu???c ki???u By
     * @param elementName ????? ?????t t??n file ???nh .png
     */
    public static void screenshotElement(By by, String elementName) {
        File scrFile = getWebElement(by).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File("./" + elementName + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * In trang hi???n t???i trong tr??nh duy???t.
     * Note: Ch??? ho???t ?????ng ??? ch??? ????? headless
     *
     * @param endPage l?? t???ng s??? trang c???n in ra. T??nh t??? 1.
     * @return is content of page form PDF file
     */
    public static String printPage(int endPage) {
        PrintOptions printOptions = new PrintOptions();
        //From page 1 to end page
        printOptions.setPageRanges("1-" + endPage);

        Pdf pdf = ((PrintsPage) DriverManager.getDriver()).print(printOptions);
        return pdf.getContent();
    }

    public static JavascriptExecutor getJsExecutor() {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        return js;
    }

    /**
     * Chuy???n ?????i ?????i t?????ng d???ng By sang WebElement
     * ????? t??m ki???m m???t element
     *
     * @param by l?? element thu???c ki???u By
     * @return Tr??? v??? l?? m???t ?????i t?????ng WebElement
     */
    public static WebElement getWebElement(By by) {
        return DriverManager.getDriver().findElement(by);
    }

    /**
     * Chuy???n ?????i ?????i t?????ng d???ng By sang WebElement
     * ????? t??m ki???m nhi???u element
     *
     * @param by l?? element thu???c ki???u By
     * @return Tr??? v??? l?? Danh s??ch ?????i t?????ng WebElement
     */
    public static List<WebElement> getWebElements(By by) {
        return DriverManager.getDriver().findElements(by);
    }

    /**
     * In ra c??u message trong Console log
     *
     * @param object truy???n v??o object b???t k???
     */
    public static void logConsole(@Nullable Object object) {
        System.out.println(object);
    }

    /**
     * Ch??? ?????i ??p bu???c v???i ????n v??? l?? Gi??y
     *
     * @param second l?? s??? nguy??n d????ng t????ng ???ng s??? Gi??y
     */
    public static void sleep(double second) {
        try {
            Thread.sleep((long) (second * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allow popup notifications c???a browser tr??n website
     *
     * @return gi?? tr??? ???? setup Allow - thu???c ?????i t?????ng ChromeOptions
     */
    public static ChromeOptions notificationsAllow() {
        // T???o Map ????? l??u tr??? c??c t??y ch???n
        Map<String, Object> prefs = new HashMap<String, Object>();

        // Th??m kh??a v?? gi?? tr??? v??o Map nh?? sau ????? t???t th??ng b??o c???a tr??nh duy???t
        // Truy???n ?????i s??? 1 ????? CHO PH??P v?? 2 ????? CH???N
        prefs.put("profile.default_content_setting_values.notifications", 1);

        // T???o m???t phi??n ChromeOptions
        ChromeOptions options = new ChromeOptions();

        // D??ng h??m setExperimentalOption th???c thi gi?? tr??? th??ng qua ?????i t?????ng prefs tr??n
        options.setExperimentalOption("prefs", prefs);

        //Tr??? v??? gi?? tr??? ???? setup thu???c ?????i t?????ng ChromeOptions
        return options;
    }

    /**
     * Block popup notifications c???a browser tr??n website
     *
     * @return gi?? tr??? ???? setup Block - thu???c ?????i t?????ng ChromeOptions
     */
    public static ChromeOptions notificationsBlock() {
        // T???o Map ????? l??u tr??? c??c t??y ch???n
        Map<String, Object> prefs = new HashMap<String, Object>();

        // Th??m kh??a v?? gi?? tr??? v??o Map nh?? sau ????? t???t th??ng b??o c???a tr??nh duy???t
        // Truy???n ?????i s??? 1 ????? CHO PH??P v?? 2 ????? CH???N
        prefs.put("profile.default_content_setting_values.notifications", 2);

        // T???o m???t phi??n ChromeOptions
        ChromeOptions options = new ChromeOptions();

        // D??ng h??m setExperimentalOption th???c thi gi?? tr??? th??ng qua ?????i t?????ng prefs tr??n
        options.setExperimentalOption("prefs", prefs);

        //Tr??? v??? gi?? tr??? ???? setup thu???c ?????i t?????ng ChromeOptions
        return options;
    }

    /**
     * Upload file ki???u click hi???n form ch???n file local trong m??y t??nh c???a b???n
     *
     * @param by       l?? element thu???c ki???u By
     * @param filePath ???????ng d???n tuy???t ?????i ?????n file tr??n m??y t??nh c???a b???n
     */
    @Step("Upload File With Local Form")
    public static void uploadFileWithLocalForm(By by, String filePath) {
        smartWait();

        Actions action = new Actions(DriverManager.getDriver());
        //Click ????? m??? form upload
        action.moveToElement(getWebElement(by)).click().perform();
        sleep(3);

        // Kh???i t???o Robot class
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        // Copy File path v??o Clipboard
        StringSelection str = new StringSelection(filePath);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(str, null);

        //Check OS for Windows
        if (BrowserInfoUtils.isWindows()) {
            // Nh???n Control+V ????? d??n
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);

            // X??c nh???n Control V tr??n
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_V);
            robot.delay(2000);
            // Nh???n Enter
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }
        //Check OS for MAC
        if (BrowserInfoUtils.isMac()) {
            robot.keyPress(KeyEvent.VK_META);
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_META);
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.delay(1000);

            //Open goto MAC
            robot.keyPress(KeyEvent.VK_META);
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_G);
            robot.keyRelease(KeyEvent.VK_META);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_G);

            //Paste the clipboard value
            robot.keyPress(KeyEvent.VK_META);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_META);
            robot.keyRelease(KeyEvent.VK_V);
            robot.delay(1000);

            //Press Enter key to close the Goto MAC and Upload on MAC
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }

        Log.info("Upload File With Local Form: " + filePath);
        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.info("Upload File With Local Form: " + filePath);
        }
        AllureManager.saveTextLog("Upload File With Local Form: " + filePath);

    }

    /**
     * Upload file ki???u k??o link tr???c ti???p v??o ?? input
     *
     * @param by       truy???n v??o element d???ng ?????i t?????ng By
     * @param filePath ???????ng d???n tuy???t ?????i ?????n file
     */
    @Step("Upload File with SendKeys")
    public static void uploadFileWithSendKeys(By by, String filePath) {
        smartWait();

        waitForElementVisible(by).sendKeys(filePath);

        Log.info("Upload File with SendKeys");
        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.info("Upload File with SendKeys");
        }
        AllureManager.saveTextLog("Upload File with SendKeys");

    }

    @Step("Get Current URL")
    public static String getCurrentUrl() {
        smartWait();
        Log.info("Get Current URL: " + DriverManager.getDriver().getCurrentUrl());
        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.info("Get Current URL: " + DriverManager.getDriver().getCurrentUrl());
        }
        AllureManager.saveTextLog("Get Current URL: " + DriverManager.getDriver().getCurrentUrl());
        return DriverManager.getDriver().getCurrentUrl();
    }

    @Step("Get Page Title")
    public static String getPageTitle() {
        smartWait();
        String title = DriverManager.getDriver().getTitle();
        Log.info("Get Page Title: " + DriverManager.getDriver().getTitle());
        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.info("Get Page Title: " + DriverManager.getDriver().getTitle());
        }
        AllureManager.saveTextLog("Get Page Title: " + DriverManager.getDriver().getTitle());
        return title;
    }

    public static boolean getPageTitle(String pageTitle) {
        smartWait();
        return getPageTitle().equals(pageTitle);
    }

    public static boolean verifyPageContainsText(String text) {
        smartWait();
        return DriverManager.getDriver().getPageSource().contains(text);
    }

    //Handle checkbox and radio button

    public static boolean verifyElementChecked(By by) {
        smartWait();

        boolean checked = getWebElement(by).isSelected();

        if (checked) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean verifyElementChecked(By by, String message) {
        smartWait();

        waitForElementVisible(by);

        boolean checked = getWebElement(by).isSelected();

        if (checked) {
            return true;
        } else {
            Assert.fail(message);
            return false;
        }
    }

    //Handle dropdown

    /**
     * Ch???n gi?? tr??? trong dropdown v???i d???ng ?????ng (kh??ng ph???i Select Option thu???n)
     *
     * @param objectListItem l?? locator c???a list item d???ng ?????i t?????ng By
     * @param text           gi?? tr??? c???n ch???n d???ng Text c???a item
     * @return click ch???n m???t item ch??? ?????nh v???i gi?? tr??? Text
     */
    public static boolean selectOptionDynamic(By objectListItem, String text) {
        smartWait();
        //?????i v???i dropdown ?????ng (div, li, span,...kh??ng ph???i d???ng select option)
        try {
            List<WebElement> elements = getWebElements(objectListItem);

            for (WebElement element : elements) {
                Log.info(element.getText());
                if (element.getText().toLowerCase().trim().contains(text.toLowerCase().trim())) {
                    element.click();
                    return true;
                }
            }
        } catch (Exception e) {
            Log.info(e.getMessage());
            e.getMessage();
        }
        return false;
    }

    public static boolean verifyOptionDynamicExist(By objectListItem, String text) {
        smartWait();

        try {
            List<WebElement> elements = getWebElements(objectListItem);

            for (WebElement element : elements) {
                Log.info(element.getText());
                if (element.getText().toLowerCase().trim().contains(text.toLowerCase().trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.info(e.getMessage());
            e.getMessage();
        }
        return false;
    }

    public static int getOptionDynamicTotal(By objectListItem) {
        smartWait();

        try {
            List<WebElement> elements = getWebElements(objectListItem);
            return elements.size();
        } catch (Exception e) {
            Log.info(e.getMessage());
            e.getMessage();
        }
        return 0;
    }

    //Dropdown (Select Option)
    public static void selectOptionByText(By by, String text) {
        smartWait();
        Select select = new Select(getWebElement(by));
        select.selectByVisibleText(text);
    }

    public static void selectOptionByValue(By by, String value) {
        smartWait();

        Select select = new Select(getWebElement(by));
        select.selectByValue(value);
    }

    public static void selectOptionByIndex(By by, int index) {
        smartWait();

        Select select = new Select(getWebElement(by));
        select.selectByIndex(index);
    }

    public static void verifyOptionTotal(By element, int total) {
        smartWait();

        Select select = new Select(getWebElement(element));
        Assert.assertEquals(total, select.getOptions().size());
    }

    public static boolean verifySelectedByText(By by, String text) {
        sleep(WAIT_SLEEP_STEP);

        Select select = new Select(getWebElement(by));
        Log.info("Option Selected by text: " + select.getFirstSelectedOption().getText());
        return select.getFirstSelectedOption().getText().equals(text);
    }

    public static boolean verifySelectedByValue(By by, String optionValue) {
        sleep(WAIT_SLEEP_STEP);

        Select select = new Select(getWebElement(by));
        Log.info("Option Selected by value: " + select.getFirstSelectedOption().getAttribute("value"));
        return select.getFirstSelectedOption().getAttribute("value").equals(optionValue);
    }

    public static boolean verifySelectedByIndex(By by, int index) {
        sleep(WAIT_SLEEP_STEP);

        boolean res = false;
        Select select = new Select(getWebElement(by));
        int indexFirstOption = select.getOptions().indexOf(select.getFirstSelectedOption());
        Log.info("The First Option selected by index: " + indexFirstOption);
        Log.info("Expected index: " + index);
        if (indexFirstOption == index) {
            res = true;
        } else {
            res = false;
        }
        return res;
    }

    //Handle frame iframe

    public static void switchToFrameByIndex(int index) {
        sleep(WAIT_SLEEP_STEP);

        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(FrameworkConstants.WAIT_EXPLICIT), Duration.ofMillis(500));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(index));
        //DriverManager.getDriver().switchTo().frame(Index);
    }

    public static void switchToFrameByIdOrName(String IdOrName) {
        sleep(WAIT_SLEEP_STEP);

        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(FrameworkConstants.WAIT_EXPLICIT), Duration.ofMillis(500));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(IdOrName));
    }

    public static void switchToFrameByElement(By by) {
        sleep(WAIT_SLEEP_STEP);

        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(FrameworkConstants.WAIT_EXPLICIT), Duration.ofMillis(500));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by));
    }

    public static void switchToDefaultContent() {
        sleep(WAIT_SLEEP_STEP);

        DriverManager.getDriver().switchTo().defaultContent();
    }


    public static void switchToWindowOrTab(int position) {
        sleep(WAIT_SLEEP_STEP);

        DriverManager.getDriver().switchTo().window(DriverManager.getDriver().getWindowHandles().toArray()[position].toString());
    }

    public static boolean verifyNumberOfWindowsOrTab(int number) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT)).until(ExpectedConditions.numberOfWindowsToBe(number));
    }

    public static void openNewTab() {
        sleep(WAIT_SLEEP_STEP);

        // Opens a new tab and switches to new tab
        DriverManager.getDriver().switchTo().newWindow(WindowType.TAB);
    }

    public static void openNewWindow() {
        sleep(WAIT_SLEEP_STEP);
        // Opens a new window and switches to new window
        DriverManager.getDriver().switchTo().newWindow(WindowType.WINDOW);
    }

    public static void switchToMainWindow() {
        sleep(WAIT_SLEEP_STEP);
        DriverManager.getDriver().switchTo().window(DriverManager.getDriver().getWindowHandles().toArray()[0].toString());
    }

    public static void switchToMainWindow(String originalWindow) {
        sleep(WAIT_SLEEP_STEP);
        DriverManager.getDriver().switchTo().window(originalWindow);
    }

    public static void switchToLastWindow() {
        smartWait();

        Set<String> windowHandles = DriverManager.getDriver().getWindowHandles();
        DriverManager.getDriver().switchTo().window(DriverManager.getDriver().getWindowHandles().toArray()[windowHandles.size() - 1].toString());
    }

    /*
        ========== Handle Alert ==================
     */

    public static void alertAccept() {
        smartWait();

        DriverManager.getDriver().switchTo().alert().accept();
    }

    public static void alertDismiss() {
        smartWait();

        DriverManager.getDriver().switchTo().alert().dismiss();
    }

    public static void alertGetText() {
        smartWait();

        DriverManager.getDriver().switchTo().alert().getText();
    }

    public static void alertSetText(String text) {
        smartWait();

        DriverManager.getDriver().switchTo().alert().sendKeys(text);
    }

    public static boolean verifyAlertPresent(int timeOut) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOut), Duration.ofMillis(500));
            wait.until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (Throwable error) {
            Assert.fail("Not found Alert.");
            return false;
        }
    }

    //Handle Elements

    public static List<String> getListElementsText(By by) {
        smartWait();

        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(FrameworkConstants.WAIT_EXPLICIT), Duration.ofMillis(500));
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));

        List<WebElement> listElement = getWebElements(by);
        List<String> listText = new ArrayList<>();

        for (WebElement e : listElement) {
            listText.add(e.getText());
        }

        return listText;
    }

    public static boolean verifyElementExists(By by) {
        smartWait();

        boolean res;
        List<WebElement> elementList = getWebElements(by);
        if (elementList.size() > 0) {
            res = true;
            Log.info("Element existing");
        } else {
            res = false;
            Log.error("Element not exists");

        }
        return res;
    }

    @Step("Verify Equals: {0} ---AND--- {1}")
    public static boolean verifyEquals(Object value1, Object value2) {
        boolean result = value1.equals(value2);
        if (result == true) {
            Log.info("Verify Equals: " + value1 + " = " + value2);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.pass("Verify Equals: " + value1 + " = " + value2);
            }
            AllureManager.saveTextLog("Verify Equals: " + value1 + " = " + value2);
        } else {
            Log.info("Verify Equals: " + value1 + " != " + value2);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.fail("Verify Equals: " + value1 + " != " + value2);
            }
            AllureManager.saveTextLog("Verify Equals: " + value1 + " != " + value2);
            Assert.assertEquals(value1, value2, value1 + " != " + value2);
        }
        return result;
    }

    @Step("Verify Equals: {0} ---AND--- {1}")
    public static boolean verifyEquals(Object value1, Object value2, String message) {
        boolean result = value1.equals(value2);
        if (result == true) {
            Log.info("Verify Equals: " + value1 + " = " + value2);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.pass("Verify Equals: " + value1 + " = " + value2);
            }
            AllureManager.saveTextLog("Verify Equals: " + value1 + " = " + value2);
        } else {
            Log.info("Verify Equals: " + value1 + " != " + value2);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.fail("Verify Equals: " + value1 + " != " + value2);
            }
            AllureManager.saveTextLog("Verify Equals: " + value1 + " != " + value2);
            Assert.assertEquals(value1, value2, message);
        }
        return result;
    }

    @Step("Verify Contains: {0} ---AND--- {1}")
    public static boolean verifyContains(String value1, String value2) {
        boolean result = value1.contains(value2);
        if (result == true) {
            Log.info("Verify Equals: " + value1 + " CONTAINS " + value2);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.pass("Verify Contains: " + value1 + " CONTAINS " + value2);
            }
            AllureManager.saveTextLog("Verify Contains: " + value1 + "CONTAINS" + value2);
        } else {
            Log.info("Verify Contains: " + value1 + " NOT CONTAINS " + value2);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.fail("Verify Contains: " + value1 + " NOT CONTAINS " + value2);
            }
            AllureManager.saveTextLog("Verify Contains: " + value1 + " NOT CONTAINS " + value2);

            Assert.assertEquals(value1, value2, value1 + " NOT CONTAINS " + value2);
        }
        return result;
    }

    @Step("Verify Contains: {0} ---AND--- {1}")
    public static boolean verifyContains(String value1, String value2, String message) {
        boolean result = value1.contains(value2);
        if (result == true) {
            Log.info("Verify Equals: " + value1 + " CONTAINS " + value2);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.pass("Verify Contains: " + value1 + " CONTAINS " + value2);
            }
            AllureManager.saveTextLog("Verify Contains: " + value1 + "CONTAINS" + value2);
        } else {
            Log.info("Verify Contains: " + value1 + " NOT CONTAINS " + value2);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.fail("Verify Contains: " + value1 + " NOT CONTAINS " + value2);
            }
            AllureManager.saveTextLog("Verify Contains: " + value1 + " NOT CONTAINS " + value2);

            Assert.assertEquals(value1, value2, message);
        }
        return result;
    }

    @Step("Verify TRUE with condition: {0}")
    public static boolean verifyTrue(Boolean condition) {
        if (condition == true) {
            Log.info("Verify TRUE: " + condition);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.pass("Verify TRUE: " + condition);
            }
            AllureManager.saveTextLog("Verify TRUE: " + condition);
        } else {
            Log.info("Verify TRUE: " + condition);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.fail("Verify TRUE: " + condition);
            }
            AllureManager.saveTextLog("Verify TRUE: " + condition);

            Assert.assertTrue(condition, "The condition is FALSE.");
        }
        return condition;
    }

    @Step("Verify TRUE with condition: {0}")
    public static boolean verifyTrue(Boolean condition, String message) {
        if (condition == true) {
            Log.info("Verify TRUE: " + condition);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.pass("Verify TRUE: " + condition);
            }
            AllureManager.saveTextLog("Verify TRUE: " + condition);
        } else {
            Log.info("Verify TRUE: " + condition);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.fail("Verify TRUE: " + condition);
            }
            AllureManager.saveTextLog("Verify TRUE: " + condition);

            Assert.assertTrue(condition, message);
        }
        return condition;
    }

    public static boolean verifyElementText(By by, String text) {
        smartWait();
        waitForElementVisible(by);

        return getTextElement(by).trim().equals(text.trim());
    }

    @Step("Verify text of an element [Equals]")
    public static boolean verifyElementTextEquals(By by, String text, FailureHandling flowControl) {
        smartWait();

        waitForElementVisible(by);

        boolean result = getTextElement(by).trim().equals(text.trim());

        if (result == true) {
            Log.info("Verify text of an element [Equals]: " + result);
        } else {
            Log.warn("Verify text of an element [Equals]: " + result);
        }

        if (flowControl.equals(FailureHandling.STOP_ON_FAILURE)) {
            Assert.assertEquals(getTextElement(by).trim(), text.trim(), "The actual text is '" + getTextElement(by).trim() + "' not equals '" + text.trim() + "'");
        }
        if (flowControl.equals(FailureHandling.CONTINUE_ON_FAILURE)) {
            softAssert.assertEquals(getTextElement(by).trim(), text.trim(), "The actual text is '" + getTextElement(by).trim() + "' not equals '" + text.trim() + "'");
            if (result == false) {
                ExtentReportManager.fail("The actual text is '" + getTextElement(by).trim() + "' not equals '" + text.trim() + "'");
            }
        }
        if (flowControl.equals(FailureHandling.OPTIONAL)) {
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.warning("Verify text of an element [Equals] - " + result);
                ExtentReportManager.warning("The actual text is '" + getTextElement(by).trim() + "' not equals expected text '" + text.trim() + "'");
            }
            AllureManager.saveTextLog("Verify text of an element [Equals] - " + result + ". The actual text is '" + getTextElement(by).trim() + "' not equals '" + text.trim() + "'");
        }

        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());

        return getTextElement(by).trim().equals(text.trim());
    }

    @Step("Verify text of an element [Equals]")
    public static boolean verifyElementTextEquals(By by, String text) {
        smartWait();
        waitForElementVisible(by);

        boolean result = getTextElement(by).trim().equals(text.trim());

        if (result == true) {
            Log.info("Verify text of an element [Equals]: " + result);
        } else {
            Log.warn("Verify text of an element [Equals]: " + result);
        }

        Assert.assertEquals(getTextElement(by).trim(), text.trim(), "The actual text is '" + getTextElement(by).trim() + "' not equals '" + text.trim() + "'");

        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.warning("Verify text of an element [Equals] : " + result);
            ExtentReportManager.warning("The actual text is '" + getTextElement(by).trim() + "' not equals '" + text.trim() + "'");
        }
        AllureManager.saveTextLog("Verify text of an element [Equals] : " + result + ". The actual text is '" + getTextElement(by).trim() + "' not equals '" + text.trim() + "'");

        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());

        return result;
    }

    @Step("Verify text of an element [Contains]")
    public static boolean verifyElementTextContains(By by, String text, FailureHandling flowControl) {
        smartWait();
        waitForElementVisible(by);

        boolean result = getTextElement(by).trim().contains(text.trim());

        if (result == true) {
            Log.info("Verify text of an element [Contains]: " + result);
        } else {
            Log.warn("Verify text of an element [Contains]: " + result);
        }

        if (flowControl.equals(FailureHandling.STOP_ON_FAILURE)) {
            Assert.assertTrue(result, "The actual text is " + getTextElement(by).trim() + " not contains " + text.trim());
        }
        if (flowControl.equals(FailureHandling.CONTINUE_ON_FAILURE)) {
            softAssert.assertTrue(result, "The actual text is " + getTextElement(by).trim() + " not contains " + text.trim());
        }
        if (flowControl.equals(FailureHandling.OPTIONAL)) {
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.warning("Verify text of an element [Contains] - " + result);
            }
            AllureManager.saveTextLog("Verify text of an element [Contains] - " + result);
        }

        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());

        return getTextElement(by).trim().contains(text.trim());
    }

    @Step("Verify text {1} of element [Contains] {0}")
    public static boolean verifyElementTextContains(By by, String text) {
        smartWait();
        waitForElementVisible(by);

        boolean result = getTextElement(by).trim().contains(text.trim());

        if (result == true) {
            Log.info("Verify text of an element [Contains]: " + result);
        } else {
            Log.warn("Verify text of an element [Contains]: " + result);
        }

        Assert.assertTrue(result, "The actual text is " + getTextElement(by).trim() + " not contains " + text.trim());

        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.info("Verify text of an element [Contains] : " + result);
        }
        AllureManager.saveTextLog("Verify text of an element [Contains] : " + result);

        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());

        return result;
    }

    @Step("Verify element Clickable {0}")
    public static boolean verifyElementClickable(By by) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT), Duration.ofMillis(500));
            wait.until(ExpectedConditions.elementToBeClickable(by));
            Log.info("Verify element clickable " + by);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.info("Verify element clickable " + by);
            }
            AllureManager.saveTextLog("Verify element clickable " + by);
            return true;
        } catch (Exception e) {
            Log.error(e.getMessage());
            Assert.fail("FAILED. Element not clickable " + by);
            return false;
        }
    }

    @Step("Verify element Clickable {0} with timeout {1} second")
    public static boolean verifyElementClickable(By by, int timeout) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeout), Duration.ofMillis(500));
            wait.until(ExpectedConditions.elementToBeClickable(by));
            Log.info("Verify element clickable " + by);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.info("Verify element clickable " + by);
            }
            AllureManager.saveTextLog("Verify element clickable " + by);
            return true;
        } catch (Exception e) {
            Log.error("FAILED. Element not clickable " + by);
            Log.error(e.getMessage());
            Assert.fail("FAILED. Element not clickable " + by);
            return false;
        }
    }

    @Step("Verify element Clickable {0}")
    public static boolean verifyElementClickable(By by, int timeout, String message) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeout), Duration.ofMillis(500));
            wait.until(ExpectedConditions.elementToBeClickable(by));
            Log.info("Verify element clickable " + by);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.info("Verify element clickable " + by);
            }
            AllureManager.saveTextLog("Verify element clickable " + by);
            return true;
        } catch (Exception e) {
            Log.error(message);
            Log.error(e.getMessage());
            Assert.fail(message + "" + e.getMessage());
            return false;
        }
    }

    @Step("Verify element present {0}")
    public static boolean verifyElementPresent(By by) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT), Duration.ofMillis(500));
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
            Log.info("Verify element present " + by);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.info("Verify element present " + by);
            }
            AllureManager.saveTextLog("Verify element present " + by);
            addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());
            return true;
        } catch (Exception e) {
            Log.info("The element does NOT present. " + e.getMessage());
            Assert.fail("The element does NOT present. " + e.getMessage());
            return false;
        }
    }

    @Step("Verify element present {0} with timeout {1} second")
    public static boolean verifyElementPresent(By by, int timeout) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
            Log.info("Verify element present " + by);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.info("Verify element present " + by);
            }
            AllureManager.saveTextLog("Verify element present " + by);
            addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());
            return true;
        } catch (Exception e) {
            Log.info("The element does NOT present. " + e.getMessage());
            Assert.fail("The element does NOT present. " + e.getMessage());
            return false;
        }
    }

    @Step("Verify element present {0}")
    public static boolean verifyElementPresent(By by, String message) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT), Duration.ofMillis(500));
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
            Log.info("Verify element present " + by);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.info("Verify element present " + by);
            }
            AllureManager.saveTextLog("Verify element present " + by);
            addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());
            return true;
        } catch (Exception e) {
            if (message.isEmpty() || message == null) {
                Log.error("The element does NOT present. " + e.getMessage());
                Assert.fail("The element does NOT present. " + e.getMessage());
            } else {
                Log.error(message);
                Assert.fail(message);
            }

            return false;
        }
    }

    @Step("Verify element present {0} with timeout {1} second")
    public static boolean verifyElementPresent(By by, int timeout, String message) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
            Log.info("Verify element present " + by);
            if (ExtentTestManager.getExtentTest() != null) {
                ExtentReportManager.info("Verify element present " + by);
            }
            AllureManager.saveTextLog("Verify element present " + by);
            addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());
            return true;
        } catch (Exception e) {
            if (message.isEmpty() || message == null) {
                Log.error("The element does NOT present. " + e.getMessage());
                Assert.fail("The element does NOT present. " + e.getMessage());
            } else {
                Log.error(message);
                Assert.fail(message);
            }

            return false;
        }
    }

    @Step("Verify element NOT present {0}")
    public static boolean verifyElementNotPresent(By by) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT), Duration.ofMillis(500));
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
            Log.error("The element presents. " + by);
            Assert.fail("The element presents. " + by);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Step("Verify element NOT present {0} with timeout {1} second")
    public static boolean verifyElementNotPresent(By by, int timeout) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
            Log.error("Element is present " + by);
            Assert.fail("The element presents. " + by);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Step("Verify element NOT present {0}")
    public static boolean verifyElementNotPresent(By by, String message) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT), Duration.ofMillis(500));
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
            if (message.isEmpty() || message == null) {
                Log.error("The element presents. " + by);
                Assert.fail("The element presents. " + by);
            } else {
                Log.error(message);
                Assert.fail(message + " " + by);
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Step("Verify element NOT present {0} with timeout {1} second")
    public static boolean verifyElementNotPresent(By by, int timeout, String message) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
            if (message.isEmpty() || message == null) {
                Log.error("The element presents. " + by);
                Assert.fail("The element presents. " + by);
            } else {
                Log.error(message + by);
                Assert.fail(message + by);
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Step("Verify element visible {0}")
    public static boolean isElementVisible(By by, long timeout) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            Log.info("Verify element visible " + by);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Verify element visible {0}")
    public static boolean verifyElementVisible(By by) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(FrameworkConstants.WAIT_EXPLICIT), Duration.ofMillis(500));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            Log.info("Verify element visible " + by);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Verify element visible {0} with timeout {1} second")
    public static boolean verifyElementVisible(By by, long timeout) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            Log.info("Verify element visible " + by);
            return true;
        } catch (Exception e) {
            Log.error("The element is not visible. " + e.getMessage());
            Assert.fail("The element is NOT visible. " + by);
            return false;
        }
    }

    @Step("Verify element visible {0}")
    public static boolean verifyElementVisible(By by, String message) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT), Duration.ofMillis(500));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            Log.info("Verify element visible " + by);
            return true;
        } catch (Exception e) {
            if (message.isEmpty() || message == null) {
                Log.error("The element is not visible. " + by);
                Assert.fail("The element is NOT visible. " + by);
            } else {
                Log.error(message + by);
                Assert.fail(message + by);
            }
            return false;
        }
    }

    @Step("Verify element visible {0} with timeout {1} second")
    public static boolean verifyElementVisible(By by, int timeout, String message) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            Log.info("Verify element visible " + by);
            return true;
        } catch (Exception e) {
            if (message.isEmpty() || message == null) {
                Log.error("The element is not visible. " + by);
                Assert.fail("The element is NOT visible. " + by);
            } else {
                Log.error(message + by);
                Assert.fail(message + by);
            }
            return false;
        }
    }

    @Step("Verify element NOT visible {0}")
    public static boolean verifyElementNotVisible(By by) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(FrameworkConstants.WAIT_EXPLICIT), Duration.ofMillis(500));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            Log.error("FAILED. The element is visible " + by);
            Assert.fail("FAILED. The element is visible " + by);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Step("Verify element NOT visible {0} with timeout {1} second")
    public static boolean verifyElementNotVisible(By by, int timeout) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            Log.error("FAILED. The element is visible " + by);
            Assert.fail("FAILED. The element is visible " + by);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Step("Verify element NOT visible {0}")
    public static boolean verifyElementNotVisible(By by, String message) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT), Duration.ofMillis(500));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            if (message.isEmpty() || message == null) {
                Log.error("FAILED. The element is visible " + by);
                Assert.fail("FAILED. The element is visible " + by);
            } else {
                Log.error(message + " " + by);
                Assert.fail(message + " " + by);
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Step("Verify element NOT visible {0} with timeout {1} second")
    public static boolean verifyElementNotVisible(By by, int timeout, String message) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            if (message.isEmpty() || message == null) {
                Log.error("FAILED. The element is visible " + by);
                Assert.fail("FAILED. The element is visible " + by);
            } else {
                Log.error(message + " " + by);
                Assert.fail(message + " " + by);
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Step("Scroll to element {0}")
    public static void scrollToElement(By by) {
        smartWait();

        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript("arguments[0].scrollIntoView(true);", getWebElement(by));
        Log.info("Scroll to element " + by);
    }

    @Step("Scroll to element {0}")
    public static void scrollToElement(WebElement element) {
        smartWait();

        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        Log.info("Scroll to element " + element);
    }

    @Step("Scroll to position X={0}, Y={1}")
    public static void scrollToPosition(int X, int Y) {
        smartWait();

        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript("window.scrollTo(" + X + "," + Y + ");");
        Log.info("Scroll to position X = " + X + " ; Y = " + Y);
    }

    @Step("Hover on element {0}")
    public static boolean hoverOnElement(By by) {
        smartWait();

        try {
            Actions action = new Actions(DriverManager.getDriver());
            action.moveToElement(getWebElement(by)).perform();
            Log.info("Hover on element " + by);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Mouse hover on element {0}")
    public static boolean mouseHover(By by) {
        smartWait();

        try {
            Actions action = new Actions(DriverManager.getDriver());
            action.moveToElement(getWebElement(by)).perform();
            Log.info("Mouse hover on element " + by);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Drag from element {0} to element {1}")
    public static boolean dragAndDrop(By fromElement, By toElement) {
        smartWait();

        try {
            Actions action = new Actions(DriverManager.getDriver());
            action.dragAndDrop(getWebElement(fromElement), getWebElement(toElement)).perform();
            //action.clickAndHold(getWebElement(fromElement)).moveToElement(getWebElement(toElement)).release(getWebElement(toElement)).build().perform();
            return true;
        } catch (Exception e) {
            Log.info(e.getMessage());
            return false;
        }
    }

    @Step("Drag HTML5 from element {0} to element {1}")
    public static boolean dragAndDropHTML5(By fromElement, By toElement) {
        smartWait();

        try {
            Robot robot = new Robot();
            robot.mouseMove(0, 0);

            int X1 = getWebElement(fromElement).getLocation().getX() + (getWebElement(fromElement).getSize().getWidth() / 2);
            int Y1 = getWebElement(fromElement).getLocation().getY() + (getWebElement(fromElement).getSize().getHeight() / 2);
            System.out.println(X1 + " , " + Y1);

            int X2 = getWebElement(toElement).getLocation().getX() + (getWebElement(toElement).getSize().getWidth() / 2);
            int Y2 = getWebElement(toElement).getLocation().getY() + (getWebElement(toElement).getSize().getHeight() / 2);
            System.out.println(X2 + " , " + Y2);

            //Ch??? n??y l???y to??? ????? hi???n t???i c???ng th??m 120px l?? ph???n header c???a browser (1920x1080 current window)
            //Header: chrome is being controlled by automated test software
            sleep(1);
            robot.mouseMove(X1, Y1 + 120);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

            sleep(1);
            robot.mouseMove(X2, Y2 + 120);
            sleep(1);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            return true;
        } catch (Exception e) {
            Log.info(e.getMessage());
            return false;
        }
    }

    @Step("Drag from element {0} to X={1}, Y={2}")
    public static boolean dragAndDropToOffset(By fromElement, int X, int Y) {
        smartWait();

        try {
            Robot robot = new Robot();
            robot.mouseMove(0, 0);
            int X1 = getWebElement(fromElement).getLocation().getX() + (getWebElement(fromElement).getSize().getWidth() / 2);
            int Y1 = getWebElement(fromElement).getLocation().getY() + (getWebElement(fromElement).getSize().getHeight() / 2);
            System.out.println(X1 + " , " + Y1);
            sleep(1);

            //Ch??? n??y l???y to??? ????? hi???n t???i c???ng th??m 120px l?? ph???n header c???a browser (1920x1080 current window)
            //Header: chrome is being controlled by automated test software
            robot.mouseMove(X1, Y1 + 120);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

            sleep(1);
            robot.mouseMove(X, Y + 120);
            sleep(1);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            return true;
        } catch (Exception e) {
            Log.info(e.getMessage());
            return false;
        }
    }

    @Step("Move to element {0}")
    public static boolean moveToElement(By toElement) {
        smartWait();

        try {
            Actions action = new Actions(DriverManager.getDriver());
            action.moveToElement(getWebElement(toElement)).release(getWebElement(toElement)).build().perform();
            return true;
        } catch (Exception e) {
            Log.info(e.getMessage());
            return false;
        }
    }

    @Step("Move to offset X={0}, Y={1}")
    public static boolean moveToOffset(int X, int Y) {
        smartWait();

        try {
            Actions action = new Actions(DriverManager.getDriver());
            action.moveByOffset(X, Y).build().perform();
            return true;
        } catch (Exception e) {
            Log.info(e.getMessage());
            return false;
        }
    }

    @Step("Press ENTER keyboard")
    public static boolean pressENTER() {
        smartWait();

        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Press ESC keyboard")
    public static boolean pressESC() {
        smartWait();

        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ESCAPE);
            robot.keyRelease(KeyEvent.VK_ESCAPE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Press F5 keyboard")
    public static boolean pressF5() {
        smartWait();

        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_F5);
            robot.keyRelease(KeyEvent.VK_F5);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Press F11 keyboard")
    public static boolean pressF11() {
        smartWait();

        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_F11);
            robot.keyRelease(KeyEvent.VK_F11);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Reload page")
    public static void reloadPage() {
        smartWait();

        DriverManager.getDriver().navigate().refresh();
        waitForPageLoaded();
        Log.info("Reloaded page " + DriverManager.getDriver().getCurrentUrl());
    }


    /**
     * @param by truy???n v??o ?????i t?????ng element d???ng By
     * @return T?? m??u vi???n ????? cho Element tr??n website
     */
    @Step("Highlight on element")
    public static WebElement highLightElement(By by) {
        smartWait();

        // draw a border around the found element
        if (DriverManager.getDriver() instanceof JavascriptExecutor) {
            ((JavascriptExecutor) DriverManager.getDriver()).executeScript("arguments[0].style.border='3px solid red'", waitForElementVisible(by));
            sleep(1);
            Log.info("Highlight on element " + by);
        }
        return getWebElement(by);
    }

    /**
     * Open website with URL
     *
     * @param URL
     */
    @Step("Open website with URL {0}")
    public static void getURL(String URL) {
        sleep(WAIT_SLEEP_STEP);

        DriverManager.getDriver().get(URL);
        waitForPageLoaded();

        Log.info("Open URL: " + URL);

        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.pass("Open URL: " + URL);
        }
        AllureManager.saveTextLog("Open URL: " + URL);

        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());

    }

    /**
     * Open website with navigate to URL
     *
     * @param URL
     */
    @Step("Navigate to URL {0}")
    public static void navigateToUrl(String URL) {
        DriverManager.getDriver().navigate().to(URL);
        waitForPageLoaded();

        Log.info("Navigate to URL: " + URL);

        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.pass("Navigate to URL: " + URL);
        }
        AllureManager.saveTextLog("Navigate to URL: " + URL);

        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());

    }

    /**
     * ??i???n gi?? tr??? v??o ?? Text
     *
     * @param by    element d???ng ?????i t?????ng By
     * @param value gi?? tr??? c???n ??i???n v??o ?? text
     */
    @Step("Set text on textbox")
    public static void setText(By by, String value) {
        waitForElementVisible(by).sendKeys(value);
        Log.info("Set text " + value + " on " + by.toString());

        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.pass("Set text " + value + " on " + by.toString());
        }
        AllureManager.saveTextLog("Set text " + value + " on " + by.toString());

        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());

    }

    /**
     * X??a gi?? tr??? trong ?? Text
     *
     * @param by element d???ng ?????i t?????ng By
     */
    @Step("Clear value in textbox")
    public static void clearText(By by) {
        waitForElementVisible(by).clear();
        Log.info("Clear value in textbox " + by.toString());

        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.pass("Clear value in textbox " + by.toString());
        }
        AllureManager.saveTextLog("Clear value in textbox");
        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());

    }

    /**
     * ??i???n gi?? tr??? v??o ?? Text
     *
     * @param by    element d???ng ?????i t?????ng By
     * @param value gi?? tr??? c???n ??i???n v??o ?? text
     */
    @Step("Clear and Fill text on text box")
    public static void clearAndFillText(By by, String value) {
        waitForElementVisible(by).clear();
        waitForElementVisible(by).sendKeys(value);
        Log.info("Clear and Fill " + value + " on " + by.toString());

        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.pass("Clear and Fill " + value + " on " + by.toString());
        }
        AllureManager.saveTextLog("Clear and Fill " + value + " on " + by.toString());

        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());

    }

    /**
     * Click chu???t v??o ?????i t?????ng Element tr??n web
     *
     * @param by element d???ng ?????i t?????ng By
     */
    @Step("Click on the element {0}")
    public static void clickElement(By by) {
        waitForElementVisible(by).click();
        Log.info("Click on element " + by.toString());

        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.pass("Clicked on the object " + by.toString());
        }
        AllureManager.saveTextLog("Clicked on the object " + by.toString());

        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());

    }

    /**
     * Click chu???t v??o Element tr??n web v???i Javascript (click ng???m kh??ng s??? b??? che)
     *
     * @param by element d???ng ?????i t?????ng By
     */
    @Step("Click on the element by Javascript {0}")
    public static void clickElementWithJs(By by) {
        //?????i ?????n khi element ???? t???n t???i
        waitForElementPresent(by);
        //Scroll to element v???i Js
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript("arguments[0].scrollIntoView(true);", getWebElement(by));
        //click v???i js
        js.executeScript("arguments[0].click();", getWebElement(by));

        Log.info("Click on element with JS: " + by);
        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.pass("Click on element with JS: " + by);
        }
        AllureManager.saveTextLog("Clicked on the object " + by);
        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());

    }

    /**
     * Click on the link on website with text
     *
     * @param linkText is the visible text of a link
     */
    @Step("Click on the link text {0}")
    public static void clickLinkText(String linkText) {
        smartWait();

        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(FrameworkConstants.WAIT_EXPLICIT), Duration.ofMillis(500));
        WebElement elementWaited = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(linkText)));
        elementWaited.click();

        Log.info("Click on link text " + linkText);
        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.pass("Click on link text " + linkText);
        }
        AllureManager.saveTextLog("Click on link text " + linkText);
        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());
    }

    /**
     * Click chu???t ph???i v??o ?????i t?????ng Element tr??n web
     *
     * @param by element d???ng ?????i t?????ng By
     */
    @Step("Right click on element {0}")
    public static void rightClickElement(By by) {
        Actions action = new Actions(DriverManager.getDriver());
        action.contextClick(waitForElementVisible(by)).build().perform();
        Log.info("Right click on element " + by);
        if (ExtentTestManager.getExtentTest() != null) {
            ExtentReportManager.pass("Right click on element " + by);
        }
        AllureManager.saveTextLog("Right click on element " + by);
        addScreenshotToReport(Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + DateUtils.getCurrentDateTime());

    }

    /**
     * Get text of a element
     *
     * @param by element d???ng ?????i t?????ng By
     * @return text of a element
     */
    @Step("Get text of element {0}")
    public static String getTextElement(By by) {
        smartWait();
        AllureManager.saveTextLog("Get text of element " + by.toString());
        AllureManager.saveTextLog("==> The Text is: " + waitForElementVisible(by).getText());
        return waitForElementVisible(by).getText().trim();
    }

    /**
     * L???y gi?? tr??? t??? thu???c t??nh c???a element
     *
     * @param by            element d???ng ?????i t?????ng By
     * @param attributeName t??n thu???c t??nh
     * @return gi?? tr??? thu???c t??nh c???a element
     */
    @Step("Get attribute {1} of element {0}")
    public static String getAttributeElement(By by, String attributeName) {
        smartWait();
        return waitForElementVisible(by).getAttribute(attributeName);
    }

    /**
     * L???y gi?? tr??? CSS c???a m???t element
     *
     * @param by      element d???ng ?????i t?????ng By
     * @param cssName t??n thu???c t??nh CSS
     * @return gi?? tr??? c???a CSS
     */
    @Step("Get CSS value {1} of element {0}")
    public static String getCssValueElement(By by, String cssName) {
        smartWait();
        return waitForElementVisible(by).getCssValue(cssName);
    }

    @Step("Get Size of element {0}")
    public static Dimension getSizeElement(By by) {
        smartWait();
        return waitForElementVisible(by).getSize();
    }

    @Step("Get Location of element {0}")
    public static Point getLocationElement(By by) {
        smartWait();
        return waitForElementVisible(by).getLocation();
    }

    @Step("Get Tag Name of element {0}")
    public static String getTagNameElement(By by) {
        smartWait();
        return waitForElementVisible(by).getTagName();
    }

    //Handle Table

    /**
     * Ki???m tra gi?? tr??? t???ng c???t c???a table khi t??m ki???m theo ??i???u ki???n B???NG (equals)
     *
     * @param column v??? tr?? c???t
     * @param value  gi?? tr??? c???n so s??nh
     */
    @Step("Check data by EQUALS type after searching on the Table by Column.")
    public static void checkEqualsValueOnTableByColumn(int column, String value) {
        smartWait();
        sleep(1);
        List<WebElement> totalRows = getWebElements(By.xpath("//tbody/tr"));
        Log.info("Number of results for keywords (" + value + "): " + totalRows.size());

        if (totalRows.size() < 1) {
            Log.info("Not found value: " + value);
        } else {
            for (int i = 1; i <= totalRows.size(); i++) {
                boolean res = false;
                WebElement title = waitForElementVisible(By.xpath("//tbody/tr[" + i + "]/td[" + column + "]"));
                res = title.getText().toUpperCase().equals(value.toUpperCase());
                Log.info("Row " + i + ": " + res + " - " + title.getText());
                Assert.assertTrue(res, "Row " + i + " (" + title.getText() + ")" + " equals no value: " + value);
            }
        }
    }

    /**
     * Ki???m tra gi?? tr??? t???ng c???t c???a table khi t??m ki???m theo ??i???u ki???n CH???A (contains)
     *
     * @param column v??? tr?? c???t
     * @param value  gi?? tr??? c???n so s??nh
     */
    @Step("Check data by CONTAINS type after searching on the Table by Column.")
    public static void checkContainsValueOnTableByColumn(int column, String value) {
        smartWait();
        sleep(1);
        List<WebElement> totalRows = getWebElements(By.xpath("//tbody/tr"));
        Log.info("Number of results for keywords (" + value + "): " + totalRows.size());

        if (totalRows.size() < 1) {
            Log.info("Not found value: " + value);
        } else {
            for (int i = 1; i <= totalRows.size(); i++) {
                boolean res = false;
                WebElement title = waitForElementVisible(By.xpath("//tbody/tr[" + i + "]/td[" + column + "]"));
                res = title.getText().toUpperCase().contains(value.toUpperCase());
                Log.info("Row " + i + ": " + res + " - " + title.getText());
                Assert.assertTrue(res, "Row " + i + " (" + title.getText() + ")" + " contains no value: " + value);
            }
        }
    }

    /**
     * Ki???m tra gi?? tr??? t???ng c???t c???a table khi t??m ki???m theo ??i???u ki???n CH???A v???i xpath tu??? ch???nh
     *
     * @param column           v??? tr?? c???t
     * @param value            gi?? tr??? c???n so s??nh
     * @param xpathToTRtagname gi?? tr??? xpath t??nh ?????n th??? TR
     */
    @Step("Check data by CONTAINS type after searching on the Table by Column.")
    public static void checkContainsValueOnTableByColumn(int column, String value, String xpathToTRtagname) {
        smartWait();

        //xpathToTRtagname is locator from table to "tr" tagname of data section: //tbody/tr, //div[@id='example_wrapper']//tbody/tr, ...
        List<WebElement> totalRows = DriverManager.getDriver().findElements(By.xpath(xpathToTRtagname));
        sleep(1);
        Log.info("Number of results for keywords (" + value + "): " + totalRows.size());

        if (totalRows.size() < 1) {
            Log.info("Not found value: " + value);
        } else {
            for (int i = 1; i <= totalRows.size(); i++) {
                boolean res = false;
                WebElement title = waitForElementVisible(By.xpath(xpathToTRtagname + "[" + i + "]/td[" + column + "]"));
                res = title.getText().toUpperCase().contains(value.toUpperCase());
                Log.info("Row " + i + ": " + res + " - " + title.getText());
                Assert.assertTrue(res, "Row " + i + " (" + title.getText() + ")" + " contains no value " + value);
            }
        }
    }

    /**
     * L???y gi?? tr??? c???a m???t c???t t??? table
     *
     * @param column v??? tr?? c???t
     * @return m???ng danh s??ch gi?? tr??? c???a m???t c???t
     */
    public static ArrayList getValueTableByColumn(int column) {
        smartWait();

        List<WebElement> totalRows = DriverManager.getDriver().findElements(By.xpath("//tbody/tr"));
        sleep(1);
        Log.info("Number of results for column (" + column + "): " + totalRows.size()); //Kh??ng th??ch ghi log th?? x??a nhen

        ArrayList arrayList = new ArrayList<String>();

        if (totalRows.size() < 1) {
            Log.info("Not found value !!");
        } else {
            for (int i = 1; i <= totalRows.size(); i++) {
                boolean res = false;
                WebElement title = DriverManager.getDriver().findElement(By.xpath("//tbody/tr[" + i + "]/td[" + column + "]"));
                arrayList.add(title.getText());
                Log.info("Row " + i + ":" + title.getText()); //Kh??ng th??ch ghi log th?? x??a nhen
            }
        }

        return arrayList;
    }

    //Wait Element

    /**
     * Ch??? ?????i element s???n s??ng hi???n th??? ????? thao t??c theo th???i gian tu??? ??
     *
     * @param by      element d???ng ?????i t?????ng By
     * @param timeOut th???i gian ch??? t???i ??a
     * @return m???t ?????i t?????ng WebElement ???? s???n s??ng ????? thao t??c
     */
    public static WebElement waitForElementVisible(By by, long timeOut) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOut), Duration.ofMillis(500));

            boolean check = verifyElementVisible(by, timeOut);
            if (check == true) {
                return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            } else {
                scrollToElement(by);
                return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            }
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for the element Visible. " + by.toString());
            Log.error("Timeout waiting for the element Visible. " + by.toString());
        }
        return null;
    }

    /**
     * Ch??? ?????i element s???n s??ng hi???n th??? ????? thao t??c
     *
     * @param by element d???ng ?????i t?????ng By
     * @return m???t ?????i t?????ng WebElement ???? s???n s??ng ????? thao t??c
     */
    public static WebElement waitForElementVisible(By by) {
        smartWait();
        waitForElementPresent(by);

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT), Duration.ofMillis(500));
            boolean check = isElementVisible(by, 1);
            if (check == true) {
                return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            } else {
                scrollToElement(by);
                return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            }
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for the element Visible. " + by.toString());
            Log.error("Timeout waiting for the element Visible. " + by.toString());
        }
        return null;
    }

    /**
     * Ch??? ?????i element s???n s??ng hi???n th??? ????? CLICK theo th???i gian tu??? ??
     *
     * @param by      element d???ng ?????i t?????ng By
     * @param timeOut th???i gian ch??? t???i ??a
     * @return m???t ?????i t?????ng WebElement ???? s???n s??ng ????? CLICK
     */
    public static WebElement waitForElementClickable(By by, long timeOut) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOut), Duration.ofMillis(500));
            return wait.until(ExpectedConditions.elementToBeClickable(getWebElement(by)));
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for the element ready to click. " + by.toString());
            Log.error("Timeout waiting for the element ready to click. " + by.toString());
        }
        return null;
    }

    /**
     * Ch??? ?????i element s???n s??ng hi???n th??? ????? CLICK
     *
     * @param by element d???ng ?????i t?????ng By
     * @return m???t ?????i t?????ng WebElement ???? s???n s??ng ????? CLICK
     */
    public static WebElement waitForElementClickable(By by) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT), Duration.ofMillis(500));
            return wait.until(ExpectedConditions.elementToBeClickable(getWebElement(by)));
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for the element ready to click. " + by.toString());
            Log.error("Timeout waiting for the element ready to click. " + by.toString());
        }
        return null;
    }

    /**
     * Ch??? ?????i element s???n s??ng t???n t???i trong DOM theo th???i gian tu??? ??
     *
     * @param by      element d???ng ?????i t?????ng By
     * @param timeOut th???i gian ch??? t???i ??a
     * @return m???t ?????i t?????ng WebElement ???? t???n t???i
     */
    public static WebElement waitForElementPresent(By by, long timeOut) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOut), Duration.ofMillis(500));
            return wait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (Throwable error) {
            Log.error("Timeout waiting for the element to exist. " + by.toString());
            Assert.fail("Timeout waiting for the element to exist. " + by.toString());
        }

        return null;
    }

    /**
     * Ch??? ?????i element s???n s??ng t???n t???i trong DOM theo th???i gian tu??? ??
     *
     * @param by element d???ng ?????i t?????ng By
     * @return m???t ?????i t?????ng WebElement ???? t???n t???i
     */
    public static WebElement waitForElementPresent(By by) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT), Duration.ofMillis(500));
            return wait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (Throwable error) {
            Log.error("Element not exist. " + by.toString());
            Assert.fail("Element not exist. " + by.toString());
        }
        return null;
    }

    /**
     * Ch??? ?????i thu???c t??nh c???a m???t element t???n t???i
     *
     * @param by        element d???ng ?????i t?????ng By
     * @param attribute t??n thu???c t??nh
     * @return true/false
     */
    public static boolean waitForElementHasAttribute(By by, String attribute) {
        smartWait();

        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT), Duration.ofMillis(500));
            return wait.until(ExpectedConditions.attributeToBeNotEmpty(waitForElementPresent(by), attribute));
        } catch (Throwable error) {
            Log.error("Timeout for element " + by.toString() + " to exist attribute: " + attribute);
            Assert.fail("Timeout for element " + by.toString() + " to exist attribute: " + attribute);
        }
        return false;
    }

    /**
     * Ki???m tra gi?? tr??? t??? thu???c t??nh c???a m???t element c?? ????ng hay kh??ng
     *
     * @param by        element d???ng ?????i t?????ng By
     * @param attribute t??n thu???c t??nh
     * @param value     gi?? tr???
     * @return true/false
     */
    @Step("Verify element {0} with attribute {1} has value is {2}")
    public static boolean verifyElementAttributeValue(By by, String attribute, String value) {
        smartWait();

        waitForElementVisible(by);
        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_EXPLICIT), Duration.ofMillis(500));
            wait.until(ExpectedConditions.attributeToBe(by, attribute, value));
            return true;
        } catch (Throwable error) {
            Log.error("Object: " + by.toString() + ". Not found value: " + value + " with attribute type: " + attribute + ". Actual get Attribute value is: " + getAttributeElement(by, attribute));
            Assert.fail("Object: " + by.toString() + ". Not found value: " + value + " with attribute type: " + attribute + ". Actual get Attribute value is: " + getAttributeElement(by, attribute));
            return false;
        }
    }

    /**
     * Ch??? ?????i thu???c t??nh c???a m???t element t???n t???i v???i th???i gian tu??? ch???nh
     *
     * @param by        element d???ng ?????i t?????ng By
     * @param attribute t??n thu???c t??nh
     * @return true/false
     * @timeOut th???i gian ch??? t???i ??a
     */
    @Step("Verify element {0} has attribute {1} with timeout {2} second")
    public static boolean verifyElementHasAttribute(By by, String attribute, int timeOut) {
        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOut));
            wait.until(ExpectedConditions.attributeToBeNotEmpty(waitForElementPresent(by), attribute));
            return true;
        } catch (Throwable error) {
            Log.error("Not found Attribute " + attribute + " of element " + by.toString());
            Assert.fail("Not found Attribute " + attribute + " of element " + by.toString());
            return false;
        }
    }

    // Wait Page loaded

    /**
     * Ch??? ?????i trang t???i xong (Javascript) v???i th???i gian m???c ?????nh t??? config
     */
    public static void waitForPageLoaded() {
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_PAGE_LOADED), Duration.ofMillis(500));
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();

        // wait for Javascript to loaded
        ExpectedCondition<Boolean> jsLoad = driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");

        //Get JS is Ready
        boolean jsReady = js.executeScript("return document.readyState").toString().equals("complete");

        //Wait Javascript until it is Ready!
        if (!jsReady) {
            Log.info("Javascript in NOT Ready!");
            //Wait for Javascript to load
            try {
                wait.until(jsLoad);
            } catch (Throwable error) {
                error.printStackTrace();
                Assert.fail("Timeout waiting for page load (Javascript). (" + WAIT_PAGE_LOADED + "s)");
            }
        }
    }

    /**
     * Ch??? ?????i JQuery t???i xong v???i th???i gian m???c ?????nh t??? config
     */
    public static void waitForJQueryLoad() {
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_PAGE_LOADED), Duration.ofMillis(500));
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();

        //Wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = driver -> {
            assert driver != null;
            return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
        };

        //Get JQuery is Ready
        boolean jqueryReady = (Boolean) js.executeScript("return jQuery.active==0");

        //Wait JQuery until it is Ready!
        if (!jqueryReady) {
            Log.info("JQuery is NOT Ready!");
            try {
                //Wait for jQuery to load
                wait.until(jQueryLoad);
            } catch (Throwable error) {
                Assert.fail("Timeout waiting for JQuery load. (" + WAIT_PAGE_LOADED + "s)");
            }
        }
    }

    //Wait for Angular Load

    /**
     * Ch??? ?????i Angular t???i xong v???i th???i gian m???c ?????nh t??? config
     */
    public static void waitForAngularLoad() {
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_PAGE_LOADED), Duration.ofMillis(500));
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        final String angularReadyScript = "return angular.element(document).injector().get('$http').pendingRequests.length === 0";

        //Wait for ANGULAR to load
        ExpectedCondition<Boolean> angularLoad = driver -> {
            assert driver != null;
            return Boolean.valueOf(((JavascriptExecutor) driver).executeScript(angularReadyScript).toString());
        };

        //Get Angular is Ready
        boolean angularReady = Boolean.parseBoolean(js.executeScript(angularReadyScript).toString());

        //Wait ANGULAR until it is Ready!
        if (!angularReady) {
            Log.warn("Angular is NOT Ready!");
            //Wait for Angular to load
            try {
                //Wait for jQuery to load
                wait.until(angularLoad);
            } catch (Throwable error) {
                Assert.fail("Timeout waiting for Angular load. (" + WAIT_PAGE_LOADED + "s)");
            }
        }


    }


}