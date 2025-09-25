package model;

/**
 * Simple enumerated type to distinguish between the AI player and the human player.
 * @author emmanuel adam
 */
public enum PlayerType {
    PLAYER(1),  // Human player
    MACHINE(2); // AI player

    private int type;

    /**
     * Constructor for PlayerType.
     * @param _type the type associated with the player
     */
    PlayerType(int _type) {
        type = _type;
    }

    /**
     * @return the type of the player
     */
    public int getType() {
        return type;
    }
}
