Feature: Gestion des stocks et des marges

  Background:
    Given un système d'information
    And un magasin
    And une recette "Cookie au Chocolat blanc"
    And un fournisseur "Pasbob" qui fournit l'ingredient "Plain dough" au magasin


  Scenario: un manager veut modifié la marge du magasin sur les cookies personalisés
    When un manager modifie la marge sur les cookies personalisés à la valeur 2 dans le magasin
    Then la marge sur les cookies personalisés dans ce magasin est égale à 2

  Scenario: un manager veut ajouter une recette du mois "Cookie au Chocolat blanc"
    When un manager ajoute la recette du mois "Cookie au Chocolat blanc" dans le magasin
    Then le magasin possède bien "Cookie au Chocolat blanc" en temps que cookie du mois

  Scenario: un manager veut changer le fournisseur pour de "Plain dough" pour  "Bob"
    When un manager change le fournisseur de l'ingredient "Plain dough" pour le fournisseur de nom "Bob"
    Then le fournisseur de "Plain dough" est bien "Bob"

  Scenario: un manager veut régler la table de conversion euros - points du système d'infidélité pour sa boutique
    When un manager change la valeur du ratio de conversion euros-points par "0.2" de la carte d'infidélité
    Then le ratio est effectivement remplacé par le nouveau ratio de "0.2"

  Scenario: un manager veut modifier les horaires d'un jour particulier pour son magasin
    When le manager modifie les horaires du magasin pour que l'ouverture soit "10:00:00" le "2019-03-15"
    Then les horaires du magasin sont bien modifié pour le "2019-03-15", il ouvre bien à "10:00:00"
