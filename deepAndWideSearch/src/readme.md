# Recherche en Largeur / Profondeur

La classe DeepAndBreadthSearch présente un algortihme de recherche en largeur / profondeur basé sur la notion de noeuds libres et noeuds clos.
Aucun backtracking sous forme de reccurrence, mais une création d'états à partir de l'état courant, qui un fois traité passe en noeuds clos.
Les nouveaux états sont ajoutés en tête des noeuds libres si recherche en profondeur, ou en fin de liste si recherche en largeur.
Avant ajout, on véfirie qu'ils ne sont pas déjà présents en noeuds libres ou clos..

La classe State est une classe abstraite à instancier pour résoudre votre problème.

La classe StateQueen présente un exemple d'utilisation pour la résolution du pb des N-reines.
