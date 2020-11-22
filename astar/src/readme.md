# A Star : recherche informée

L'algortihme A-Star est un algortihme de recherche de solution "informée", c'est-à-dire qu'il se base sur une heuristique pour guider sa recherche, au contraire des algorithmes de recherche aveugles(profondeur, largeur) qui eux n'exploitent pas les connaissances sur le problème à résoudre.

La classe AlgoAStar contient l'algorithme A-Star.
La classe State est un état générique à étendre. Un état possède les variables f,g,h, ainsi que des fonctions de comparaison, etc.

Un exemple d'utilisation est donné par la classe StateTaquin pour la résolution du jeu du Taquin qui consiste à faire coulisser une pièce vide afin de réordonner des pièces.
Ainsi par exemple, l'objectif est d'atteindre le plus rapidement
<pre>
A B C D
E F G H
I J K L
M N O _
</pre>

à partir de 
<pre>
_ A C D
E B K G
N F J H
I M O L
</pre>

