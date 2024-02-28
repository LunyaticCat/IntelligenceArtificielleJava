# IA pour les jeux avec Ludii

On utilisera ici la bibliothèque / application Ludii pour tester des algorithmes IA spécialisés dans les jeux.

Principalement, les IA développées seront celles issues de la branche MCTS des IA pour jeux.

Pour rappel, ces IA fonctionnent selon la méthode exploration + exploitation : 
  - exploration de situations par jeux aléatoires
  - exploitation des situations les plus probables de mener à une victoire.

Pour rappel, le principe général de MCTS est :
 - Utiliser un arbre pour représenter le plateau de jeu, la situation de base étant la racine de l'arbre. 
 - Explorer l'arbre en sélectionnant les mouvements qui ont été le moins visités. 
 - Utiliser une simulation pour estimer la valeur de chaque mouvement. 
 - Éventuellement, _biaiser_ la sélection en faveur de mouvements qui ont été joués plus fréquemment.

De là, plusieurs implémentations sont possibles : 
 - **UCB (Upper Confidence Bound)** :
   - Explorer l'arbre en sélectionnant les mouvements qui ont la confiance la plus élevée.
   - La confiance pour un coup $a$ à partir d'un état $s$ est calculée à l'aide de la formule suivante :
       - $UCB(s, a) = Q(s, a) + E(s, a)$ où :
         - $Q(s, a)$ est la qualité, la récompense moyenne pour le coup $a$ dans l'état $s$ (nb gains/ nb visites).
         - $E(s, a)$ est un coefficient d'exploration, en général basé sur le nombre de fois que le coup $a$ a été joué à partir de $s$
 - **UCT (Upper Confidence bounds applied to Trees)**:
 - Explorer l'arbre en sélectionnant les mouvements qui ont la confiance (valeur bornée par une limite supérieure) la plus élevée. 
 - La limite supérieure de confiance pour un coup $a$ à partir d'un état $s$ est calculée à l'aide de la formule suivante : 
   - $UCT(s,a) = Q(s, a) + C \times \sqrt{\frac{2.log(max(1, N(s)))}{N(s,a)}}$ où : 
     - $Q(s, a)$ est la qualité, la récompense moyenne pour le coup $a$ dans l'état $s$ (nb gains/ nb visites). 
     - $N(s)$ est le nombre de passages par l'état $s$.
     - $N(s, a)$ est le nombre de fois que le coup $a$ a été joué dans l'état $s$.
     - $C$ est une constante qui contrôle l'équilibre entre l'exploration et l'exploitation.
 
UCT est en quelque sorte une implémentation particulière de UCB.

Il existe de nombreuses variantes, entre autres :
  - **Progressive Bias** : pour l'algorithme UCB, au coefficient d'exploration est ajouté un biais basé sur une heuristique de jeu.
  - **Progressive Unpruning (PUP)** : Les enfants d'un nœud générés par *expand()* sont tous effacés sauf celui choisi tant que le nœud n'a pas été visité un certain nombre de fois.
  - **Rapid Action Value Estimation (RAVE)** : simplement utilise une sauvegarde des valuations des nœuds générées lors d'une précédente utilisation.
  - **Transpositions and Move Groups (TMG)** : mémoïsation de séquences d'actions ayant des résultats similaires pour éviter le recalcul.
  - **Nested Monte Carlo Tree Search (NMCTS)** : MCTS est lancé récursivement par *expand()* (un NMCTS de niveau 1 correspond à un MCTS classique).
  - **Decoupled Upper Confidence bounds (DUCT)** : MCTS est lancé pour chaque noeud du second niveau  *expand()*
  - **Double Progressive Widening (DPW)** : Le nombre de fils généré par *expand()* est limité et s'accroît progressivement avec la profondeur.

Vous pouvez les adapter pour créer le vôtre ; le choix de l'algorithme dépend du jeu.

--
Travail à réaliser.

- Développez une IA de type Progressive BIAS, adaptée jeu Reversi.
  - Donc pour la valeur des cases occupées a un impact dans le choix d'un noeud (dans coef d'exploration)
    (appelez là ProgBiasVotreNom)

- Développez une IA de type PUP, et appliquée là à un jeu à large choix comme HexAmazons

