# MCTS : MonteCarlo Tree Search

Cet espace présente 2 applications permettant à 1 joueur d'affronter une IA qui s'est entrainée à l'aide de l'algorithme MonteCarlo Tree Search.
cf. [enoncé TP jeu](http://emmanuel.adam.free.fr/site/spip.php?article201)

- TicTacToe est donc le jeu du morpion du du OXO.. L'IA par MonteCarlo n'a pas été optimisée, vous pouvez gagner.

- P4 est le jeu de 'Puissance4' ou 'Aligne4'. L'IA a été ici optimisée.. C'est à dire qu'elle va supposer que si c'est au tour de l'humain de joureur et qu'il peut immédiatement gagner, alors l'humain jouera le coup gagnant pour lui, et donc le coup perdant pour l'IA.
L'IA joue également contre elle-même à chaque coup, et non plus une seule fois au départ du jeu. Ce qui lui permet de générer des parties plus précises par rapport au contexte.
Dans ce jeu, les diagonales ne sont pas prises en compte..
