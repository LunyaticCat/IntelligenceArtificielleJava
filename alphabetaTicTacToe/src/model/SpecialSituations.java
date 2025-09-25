package model;

/**
 * Special game situations with their associated values
 */
public enum SpecialSituations {
    XXX(1000),  // Three X's in a row
    XX_(100),   // Two X's and an empty space
    X_X(100),   // Two X's separated by an empty space
    _XX(100),   // Two X's with an empty space before them
    X__(10),     // One X and two empty spaces
    _X_(100),     // One X surrounded by empty spaces
    __X(10),     // One X with two empty spaces before it

    OOO(-100), // Three O's in a row
    OO_(-10),  // Two O's and an empty space
    O_O(-10),  // Two O's separated by an empty space
    _OO(-10),  // Two O's with an empty space before them
    O__(-10),    // One O and two empty spaces
    _O_(-10),    // One O surrounded by empty spaces
    __O(10);    // One O with two empty spaces before it

    final double value;

    /**
     * Constructor for SpecialSituations
     * @param value the value associated with the situation
     */
    SpecialSituations(double value) {
        this.value = value;
    }

    /**
     * @return the value of the situation
     */
    public double getValue() {
        return value;
    }
}
