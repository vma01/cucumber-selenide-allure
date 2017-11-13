package microservice.testlibraries;

import com.codeborne.selenide.Selenide;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import microservice.common.MsCommon;
import microservice.common.MsConstants;
import microservice.pages.ProductsPage;
import microservice.msrest.MsCatalogRest;
import microservice.msrest.MsCustomerRest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.codeborne.selenide.Selenide.$;
import static microservice.helper.SeleniumHelper.printMethodName;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class AcceptanceTestsStepDefinitions {

    private ProductsPage msMainPage;

    @Given("^order by (.*) should not exist$")
    public void orderShouldNotExistByCustomer(String customer) throws IOException {
        printMethodName();

        //System.out.println("Sleeping before opening browser...");
        //Selenide.sleep(120000);

        //Enable this if "Whitelabel Error Page" caused by order service down, will stop and start Microservice
        try {
            HashMap<String, ArrayList<String>> out = MsCommon.executeSystemCommandAndReturnStdOutAndStdErr("docker-compose -f /Users/heikmjar/git/microservice-demo-acceptance-tests-copy/src/test/resources/docker-compose-dev.yml down -v --remove-orphans", true);
            MsCommon.executeSystemCommandAndReturnStdOutAndStdErr("docker-compose -f /Users/heikmjar/git/microservice-demo-acceptance-tests-copy/src/test/resources/docker-compose-dev.yml up -d",true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        msMainPage = Selenide.open(MsConstants.microserviceHost, ProductsPage.class);
        msMainPage.navigateToOrdersPage()
                .deleteOrderByCustomer(customer)
                .navigateBackToProductsPage();
    }

    @And("product (.*) should not be in the catalog through REST API")
    public void productShouldNotBeInTheCatalogThroughRESTAPI( String catalogItemName) {
        printMethodName();

        MsCatalogRest.deleteCatalogItemByName(MsConstants.catalogServiceUrl,MsConstants.catalogURI,catalogItemName);

    }

    @And("customer (.*) should not exist through REST API")
    public void customerShouldNotExistThroughRESTAPI(String customer) {
        printMethodName();

        MsCustomerRest.deleteCustomerByName(MsConstants.customerServiceUrl,MsConstants.customerURI,customer);

    }

    @And("product (.*) is added to the catalog with price (.*)")
    public void productIsAddedToTheCatalog(String catalogItem, String catalogItemPrice) {
        printMethodName();

        msMainPage.navigateToAddProductPage()
                .addCatalogItem(catalogItem, catalogItemPrice)
                .navigateBackToProductsPage();
    }

    @And("customer (.*) is added")
    public void customerIsAdded(String customer) {
        printMethodName();

        msMainPage.navigateToCustomersPage()
                .addCustomer(customer)
                .navigateBackToProductsPage();
    }

    @When("I order product (.*) as customer (.*)")
    public void iOrderProduct(String catalogItem, String customer) {
        printMethodName();

        //Remove this, just to get UI test to fail for a allure report and selenide screenshot
        //$(By.xpath("//h1[contains(text(),'Success')]")).shouldBe(Condition.visible);

        msMainPage.navigateToOrdersPage()
                .addOrderByCustomer(catalogItem, customer)
                .navigateBackToProductsPage();

    }

    @Then("I can verify my order of (.*) with price (.*) by customer (.*)")
    public void iCanVerifyMyOrder(String catalogItem, String price,String customer) {
        printMethodName();

        msMainPage.navigateToOrdersPage()
                .verifyOrderByCustomer(customer,catalogItem, price)
                .navigateBackToProductsPage();


    }
}

