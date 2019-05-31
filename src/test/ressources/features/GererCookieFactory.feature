Feature: Gérer la marque Cookie Factory

  Background:
    Given un système d'information

  Scenario: un manager de Cookie Factory veut ajouter un nouveau magasin
    When le manager ajoute un magasin localisé à "Nice"
    Then le magasin se trouve dans le catalogue de magasin

  Scenario: un manager de Cookie Factory veut ajouter un nouvel ingrédient
    When le manager ajoute un nouvel ingredient de type "DOUGH" appelé "Super Dough"
    Then le nouvel ingrédient "Super Dough" se trouve bien dans la liste des ingredients

  Scenario: un manager de Cookie Factory veut ajouter une nouvelle recette globale
    When le manager ajoute une nouvelle recette "Super Recipe"
    Then la nouvelle recette "Super Recipe" se trouve dans le catalogue de recette
