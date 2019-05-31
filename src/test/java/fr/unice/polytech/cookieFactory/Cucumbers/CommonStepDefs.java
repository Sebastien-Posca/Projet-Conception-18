package fr.unice.polytech.cookieFactory.Cucumbers;

import cucumber.api.java.en.Given;
import fr.unice.polytech.cookieFactory.SI;

public class CommonStepDefs {

    public static SI si;

    @Given("^un système d'information$")
    public void unSystèmeDInformation() throws Throwable {
        si = new SI();
    }
}
