import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AmazonTest {
	public static WebDriver driver = null;
	public static JavascriptExecutor executor = null;
	
	private static String driverExeLocation = "C:\\Users\\berks\\Desktop\\geckodriver.exe"; // to be changed
	
	private static String driverParameter = "webdriver.gecko.driver";
	private static String URL = "https://www.amazon.com/";
	
	private static String email = "johndoe@xyz.com"; // to be changed
	private static String password = "password"; // to be changed
	
	private static String searchKey = "samsung";
	private static String selectedItem;

	@BeforeClass
	public static void setUp() throws Exception {
		System.setProperty(driverParameter, driverExeLocation);
		driver = new FirefoxDriver();
		driver.manage().deleteAllCookies();
		// for synchronous tasks do these
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		driver.get(URL); // open Amazon 
	}

	@AfterClass
	public static void tearDown() throws Exception {
		Thread.sleep(2000);
		driver.quit();
	}

	@Test
	public void test_1_Website() {
		String actualURL = driver.getTitle();
		String expectedURL = "Amazon.com: Online Shopping for Electronics, Apparel, Computers, Books, DVDs & more";
		Assert.assertEquals(actualURL, expectedURL);
		System.out.println("URL TEST for Amazon is passed");
	}

	@Test
	public void test_2_SignIn() {
		// click on sign in button on home page
		driver.findElement(By.cssSelector("#nav-link-accountList > span.nav-line-2")).click();
		String signIn = driver.getTitle();
		Assert.assertEquals(signIn, "Amazon Sign In");
		// enter email
		driver.findElement(By.id("ap_email")).sendKeys(email);
		// enter password
		driver.findElement(By.id("ap_password")).sendKeys(password);
		// click on submit
		driver.findElement(By.id("signInSubmit")).click();
		System.out.println("Sign in is successful");
	}

	@Test
	public void test_3and4_Search() {
		// search something on search bar on home page
		driver.findElement(By.id("twotabsearchtextbox")).sendKeys(searchKey);
		// click on search button
		driver.findElement(By.id("nav-search-submit-text")).click();
		// use JavaScriptExecutor to scroll on page
		// cast driver to JavaScriptExecutor
		executor = (JavascriptExecutor) driver;
		executor.executeScript("window.scrollTo(0, 600)");

		// try to get "results for" text on web page 
		// if it is found then search is successful 
		WebElement element = driver.findElement(By.xpath("//span[@id='s-result-count']"));
		String resultFound = element.getText();
		Assert.assertTrue(resultFound.contains("results for"));
		System.out.println("Search for Samsung is passed");
	}

	@Test
	public void test_5_NavigatePage() throws InterruptedException {
		// click on the given result page
		driver.findElement(By.linkText("2")).click();
		Thread.sleep(3000);
		// get page title
		String pageTitle = driver.getTitle();
		// get current page number
		WebElement element = driver.findElement(By.xpath("//div[@id='pagn']/span[3]"));
		String actualPageNumber = element.getText();
		String expectedPageNumber = "2";
		Assert.assertEquals(actualPageNumber, expectedPageNumber);
		// if the page title contains "samsung" then navigation is successful  
		Assert.assertTrue(pageTitle.contains("Amazon.com: samsung"));
		System.out.println("Page " + actualPageNumber + " is displayed");
	}

	@Test
	public void test_6_SelectItem() throws Exception {
		Thread.sleep(2000);
		// scroll to the given item 
		executor = (JavascriptExecutor) driver;
		executor.executeScript("window.scrollTo(0, 600)");
		// click on the item to see details
		driver.findElement(By.xpath("//li[3]//a")).click();
		Thread.sleep(3000);
	}

	@Test
	public void test_7and8_AddToList() throws Exception {
		// test 7
		// scroll to add to list button
		executor = (JavascriptExecutor) driver;
		executor.executeScript("window.scrollTo(0, 600)");
		// click on add to list button
		driver.findElement(By.id("add-to-wishlist-button-submit")).click();
		Thread.sleep(3000);

		// get selected item 
		WebElement element = driver.findElement(By.xpath("//span[@id='productTitle']"));
		selectedItem = element.getText();
		// click on view the wish list
		driver.findElement(By.cssSelector("span.w-button-text")).click();
		Thread.sleep(3000);

		// test 8
		// create an array of product titles to get items in wish list
		// get the product title in wish list then compare with selected item
		List<WebElement> productTitles = driver.findElements(By.xpath("//div[@id='g-items']//h5")); 
		for(WebElement productTitle : productTitles) {
			// compare selected item and any item in wish list
			String listedItem = productTitle.getText();            
			if (listedItem.equals(selectedItem)) {
				System.out.println(listedItem + " is added to wish list");
				Assert.assertTrue(listedItem.equals(selectedItem));
			}		
		}
	}

	@Test
	public void test_9and10_DeleteItemFromWishList() throws Exception {
		// click on delete item button
		driver.findElement(By.name("submit.deleteItem")).click();
		Thread.sleep(3000);
		
		// check whether the item exists on g-items removed class
		boolean itemExists = false;
		
		// create an array of product titles under g-item-sortable removed
		// get the product title in this list and compare it with selected item
		List<WebElement> productTitles = driver.findElements(By.xpath("//div[@id='g-items']//div[@class = 'a-section a-spacing-none g-item-sortable g-item-sortable-removed']/div/div[1]"));
		for(WebElement productTitle : productTitles) {
			// compare selected item and any item in wish list
			// try to find selected item in the list of removed items
			String listedItem = productTitle.getText();
			if (listedItem.equals(selectedItem)) {
				itemExists = true;
				System.out.println(selectedItem + " is deleted from wish list");
				Assert.assertTrue(itemExists);	
			}
		}
	}

}
