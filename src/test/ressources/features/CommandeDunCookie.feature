Feature: Commander un cookie


  Background:
    Given un système d'information
    And un client d'email "registered@gmail.com" et de mot de passe "Password" possèdant le passe d'infidélité
    And un client d'email "registered2@gmail.com" et de mot de passe "Password"
    And un magasin
    And une recette de cookie "Cookie à la vanille" disponible dans le magasin


  Scenario: Le client enregistré possèdant le passe d'infidélité passe une commande en ligne, paye en ligne et va chercher sa commande en magasin
    When le client "registered@gmail.com" commande 2 "Cookie à la vanille"
    And  le client "registered@gmail.com" paye sa commande
    Then le magasin a dans ses commandes en cours une commande pour le client "registered@gmail.com" et contenant "2" "Cookie à la vanille"
    And  la commande est bien marqué comme payé
    And  des points ont été crédités sur le passe du client "registered@gmail.com"

    When la commande est prete
    And  le client va chercher sa commande
    Then la commande est bien marqué comme récupéré par le client


  Scenario: Le client n'ayant pas de compte passe une commande en ligne
    When Un client non enregistré d'email "unregistered@gmail.com" commande 2 "Cookie à la vanille"
    Then Un customer est ajoute au catalogue de client avec l'email "unregistered@gmail.com"
    And  le magasin a dans ses commandes en cours une commande pour le client "unregistered@gmail.com" et contenant "2" "Cookie à la vanille"

  Scenario: le client enregistré, commande 30 cookies
    When le client "registered2@gmail.com" commande 30 "Cookie à la vanille"
    And le client "registered2@gmail.com" paye sa commande
    Then le magasin a dans ses commandes en cours une commande pour le client "registered2@gmail.com" et contenant "30" "Cookie à la vanille"
    And Le client "registered2@gmail.com" a le droit à une réduction pour sa prochaine commande

  Scenario: Le client enregistré, qui a maintenant le droit à une réduction, passe une commande en ligne
    When Le client "registered2@gmail.com" a le droit à une réduction
    And  le client "registered2@gmail.com" commande 2 "Cookie à la vanille"
    And le client "registered2@gmail.com" paye sa commande
    Then le magasin a dans ses commandes en cours une commande pour le client "registered2@gmail.com" et contenant "2" "Cookie à la vanille"
    And  la réduction a bien été utilisé pour le client "registered2@gmail.com"

  Scenario: Le client enregistré, qui paye au comptoir avec sa carte d'infidélité, choisit d'avoir le bonus
    When le client "registered@gmail.com" commande 2 "Cookie à la vanille"
    And le client "registered@gmail.com" paye sa commande au comptoir avec sa carte d'infidélité et choisit le bonus
    Then la derniere commande est 1 "Cookie à la vanille"

  Scenario: Un client enregistré essaie de passer une commande mais il n'y a plus de stock
    When il n'y a plus de stock de "vanilla"
    And  le client "registered3@gmail.com" commande 2 "Cookie à la vanille"
    Then la commande ne se crée pas
