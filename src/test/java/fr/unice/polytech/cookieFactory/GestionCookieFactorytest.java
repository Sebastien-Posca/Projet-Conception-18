package fr.unice.polytech.cookieFactory;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;



@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/ressources/features/GererCookieFactory.feature")
public class GestionCookieFactorytest {
}
