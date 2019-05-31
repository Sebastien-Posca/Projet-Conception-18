package fr.unice.polytech.cookieFactory.Cucumbers;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import fr.unice.polytech.cookieFactory.Shop.Customer;
import fr.unice.polytech.cookieFactory.Shop.ItemOrder;
import fr.unice.polytech.cookieFactory.Shop.RegisteredCustomer;
import fr.unice.polytech.cookieFactory.Shop.Shop;
import fr.unice.polytech.cookieFactory.Supplier;
import fr.unice.polytech.cookieFactory.UnfaithPass;
import fr.unice.polytech.cookieFactory.order.Order;
import fr.unice.polytech.cookieFactory.order.PaymentState;
import fr.unice.polytech.cookieFactory.order.ProcessingState;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipe;
import fr.unice.polytech.cookieFactory.recipe.CookieRecipeBuilder;
import fr.unice.polytech.cookieFactory.recipe.Ingredient;
import fr.unice.polytech.cookieFactory.recipe.IngredientCatalog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class CommandeDunCookieStepDef {

    int orderId = -1;
    UnfaithPass  unfaithPass = new UnfaithPass();


    @Given("^un client d'email \"([^\"]*)\" et de mot de passe \"([^\"]*)\" possèdant le passe d'infidélité$")
    public void unClientDEmailEtDeMotDePassePossèdantLePasseDInfidélité(String arg0, String arg1) throws Throwable {
        Shop shop = new Shop("nice", 0.2, 1);
        CommonStepDefs.si.addShop(shop);
        CommonStepDefs.si.getShopById(0).addNewScheduleDay(LocalTime.of(8,0), LocalTime.of(20,0),LocalDate.parse("2019-01-10") );
        shop.setUnfaithPass(unfaithPass);

        CommonStepDefs.si.signIn(arg0,arg1);
        CommonStepDefs.si.getCustomerByEmail(arg0).setUnfaithPassId(unfaithPass.createPassAccount());
    }

    @And("^une recette de cookie \"([^\"]*)\" disponible dans le magasin$")
    public void uneRecetteDeCookieDisponibleDansLeMagasin(String arg0) throws Throwable {
        CommonStepDefs.si.addRecipe(arg0,"");
        CommonStepDefs.si.addRecipeToShop(0, arg0);

        Map<Ingredient, Integer> ingredientIntegerMap = new HashMap<>();
        Supplier supplier = new Supplier(arg0,ingredientIntegerMap);
        for(Ingredient ingredient : CommonStepDefs.si.getRecipeByName(arg0).getIngredients() ){
            ingredientIntegerMap.put(CommonStepDefs.si.getIngredient(ingredient.getName()), 1);
            supplier.setIngredientPrice(ingredient, 1);
            CommonStepDefs.si.addIngredientToShop(0, ingredient.getName(),1, supplier);
            CommonStepDefs.si.getShopById(0).addStock(ingredient, 100);
        }
    }

    @When("^le client \"([^\"]*)\" commande (\\d+) \"([^\"]*)\"$")
    public void leClientCommande(String arg0, int arg1, String arg2) throws Throwable {
        LocalDateTime currentDateTime = LocalDateTime.parse("2019-01-10T10:03:10");
        List<ItemOrder> itemOrders = new ArrayList<>();
        itemOrders.add(CommonStepDefs.si.createItemOrder(0, arg2, arg1));
        try{
        String idToParse =CommonStepDefs.si.placeOrderWithAccount(itemOrders,CommonStepDefs.si.getCustomerByEmail(arg0).getCustomerId(),0, currentDateTime.plusHours(2), true);
        this.orderId = Integer.parseInt(idToParse.split(" ")[1]);}
        catch (Exception e){
            this.orderId = -1;
        }
    }

    @And("^le client \"([^\"]*)\" paye sa commande$")
    public void leClientPayeSaCommande(String arg0) throws Throwable {
        CommonStepDefs.si.payOrderOnline(0, this.orderId, 123456);
    }

    @Then("^le magasin a dans ses commandes en cours une commande pour le client \"([^\"]*)\" et contenant \"([^\"]*)\" \"([^\"]*)\"$")
    public void leMagasinADansSesCommandesEnCoursUneCommandePourLeClientEtContenant(String arg0, String arg1, String arg2) throws Throwable {
        assert CommonStepDefs.si.getShopById(0).getPendingOrders().stream().anyMatch(o -> o.getCustomer().getEmail().equals(arg0));
    }

    @And("^la commande est bien marqué comme payé$")
    public void laCommandeEstBienMarquéCommePayé() throws Throwable {
        assert CommonStepDefs.si.getShopById(0).getOrderById(this.orderId).getPaymentState() == PaymentState.PAID;
    }

    @And("^des points ont été crédités sur le passe du client \"([^\"]*)\"$")
    public void desPointsOntÉtéCréditésSurLePasseDuClient(String arg0) throws Throwable {
        assert unfaithPass.getPointsOfPass(CommonStepDefs.si.getCustomerByEmail(arg0).getUnfaithPassId()) != 0;
    }

    @When("^la commande est prete$")
    public void laCommandeEstPrete() throws Throwable {
        CommonStepDefs.si.getShopById(0).getOrderById(orderId).startProcessingOrder();
        CommonStepDefs.si.getShopById(0).getOrderById(orderId).setReady();
    }

    @And("^le client va chercher sa commande$")
    public void leClientVaChercherSaCommande() throws Throwable {
        CommonStepDefs.si.getShopById(0).getOrderById(orderId).deliver();
    }

    @Then("^la commande est bien marqué comme récupéré par le client$")
    public void laCommandeEstBienMarquéCommeRécupéréParLeClient() throws Throwable {
        assert CommonStepDefs.si.getShopById(0).getOrderById(orderId).getProcessingState() == ProcessingState.DELIVERED;
    }

    @When("^Le client \"([^\"]*)\" a le droit à une réduction$")
    public void leClientALeDroitÀUneRéduction(String arg0) throws Throwable {
        CommonStepDefs.si.addRecipe("Classic", "");
        RegisteredCustomer customer = (RegisteredCustomer) CommonStepDefs.si.getCustomerByEmail(arg0);
        customer.updateCookieCount(new ArrayList<>(Arrays.asList(new ItemOrder(
                CommonStepDefs.si.getRecipeByName("Classic"),30))));
    }

    @When("^Un client non enregistré d'email \"([^\"]*)\" commande (\\d+) \"([^\"]*)\"$")
    public void unClientNonEnregistréDEmailCommande(String arg0, int arg1, String arg2) throws Throwable {
        LocalDateTime currentDateTime = LocalDateTime.parse("2019-01-10T10:03:10");
        List<ItemOrder> itemOrders = new ArrayList<>();
        itemOrders.add(CommonStepDefs.si.createItemOrder(0, arg2, arg1));
        String idToParse = CommonStepDefs.si.placeOrderWithoutAccount(itemOrders,arg0,0, currentDateTime.plusHours(2), true);
        this.orderId = Integer.parseInt(idToParse.split(" ")[1]);
    }

    @Then("^Un customer est ajoute au catalogue de client avec l'email \"([^\"]*)\"$")
    public void unCustomerEstAjouteAuCatalogueDeClientAvecLEmail(String arg0) throws Throwable {
        assert CommonStepDefs.si.getCustomerByEmail(arg0) != null;
    }


    @And("^la réduction a bien été utilisé pour le client \"([^\"]*)\"$")
    public void laRéductionABienÉtéUtiliséPourLeClient(String arg0) throws Throwable {
        RegisteredCustomer customer = (RegisteredCustomer) CommonStepDefs.si.getCustomerByEmail(arg0);
        assert !customer.hasDiscount();
    }

    @Then("^Le client \"([^\"]*)\" a le droit à une réduction pour sa prochaine commande$")
    public void leClientALeDroitÀUneRéductionPourSaProchaineCommande(String arg0) throws Throwable {
        RegisteredCustomer customer2 = (RegisteredCustomer) CommonStepDefs.si.getCustomerByEmail(arg0);
        assert customer2.hasDiscount();
    }

    @And("^un client d'email \"([^\"]*)\" et de mot de passe \"([^\"]*)\"$")
    public void unClientDEmailEtDeMotDePasse(String arg0, String arg1) throws Throwable {
        CommonStepDefs.si.signIn(arg0,arg1);
    }

    @And("^le client \"([^\"]*)\" paye sa commande au comptoir avec sa carte d'infidélité et choisit le bonus$")
    public void leClientPayeSaCommandeAuComptoirAvecSaCarteDInfidélitéEtChoisitLeBonus(String arg0) throws Throwable {
        Customer customer = CommonStepDefs.si.getCustomerByEmail(arg0);
        unfaithPass.givePointsToPass(customer.getUnfaithPassId(),10);
        CommonStepDefs.si.payOrderAtCounterUnfaithPass(0,this.orderId,false,true,customer.getUnfaithPassId());
    }

    @Then("^la derniere commande est (\\d+) \"([^\"]*)\"$")
    public void laDerniereCommandeEst(int arg0, String arg1) throws Throwable {
        CookieRecipe cookieRecipe =CommonStepDefs.si.getRecipeByName(arg1);
        List<Order> orders = CommonStepDefs.si.getShopById(0).getOrders();
        Order lastOrder = orders.get(orders.size()-1);
        assert lastOrder.getItemOrders().size() == 1;
        assert lastOrder.getItemOrders().get(0).getCount()==1;
        assert lastOrder.getItemOrders().get(0).getCookieRecipe().getName().equals(cookieRecipe.getName());

        }

    @When("^il n'y a plus de stock de \"([^\"]*)\"$")
    public void ilNYAPlusDeStockDe(String arg0) throws Throwable {
        CommonStepDefs.si.removeStockToShop(CommonStepDefs.si.getIngredient("vanilla"),400, 0);
    }

    @Then("^la commande ne se crée pas$")
    public void laCommandeNeSeCréePas() throws Throwable {
        assert this.orderId == -1;
    }
}
