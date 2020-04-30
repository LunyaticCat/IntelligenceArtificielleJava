# Jeu du Surakarta

Le jeu du Surakarta serait originaire de l’île de Java, en Indonésie.
Un descriptif wikipedia est accessible [sur ce lien](https://fr.wikipedia.org/wiki/Surakarta_(jeu)) .


- Ce jeu se compose d’une grille 6x6 particulière contenant des boucles.
- Deux joueurs se font face ; 
  - ils possèdent chacun 12 pions posées sur les points de croisement des 2 lignes les plus proches d’eux.


- A chaque tour de jeu, un joueur peut :
  - soit déplacer un pion sans prendre de pion à l’adversaire.
    - un joueur peut bouger son pion de son point de croisement à un point de croisement adjacent en ligne, colonne ou diagonale.
  - soit prendre un pion de l’adversaire en prenant sa place
    - le mouvement est sans limite de longueur de chemin,
      - sur une même ligne, non droite car il **doit passer au moins une fois par une courbe**.
      - Il est possible d’enchaîner plusieurs boucles pour atteindre un pion de l’adversaire.
   -Il n’est pas possible de sauter au-dessus d’un pion (à soi ou à son adversaire).
   
Un déplacement pour prise doit obligatoirement se terminer par une prise ; 
il n’est pas possible d’emprunter une ou plusieurs boucle si ce n’est pas pour prendre un pion de l’adversaire.

Vous trouverez ici un code utilisant JavaSwing permettant à 2 humains de jouer l'un contre l'autre..

La classe principale est : JeuSuraKarta
