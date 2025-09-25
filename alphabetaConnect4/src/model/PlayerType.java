package model;
public enum PlayerType {
	PLAYER(1), MACHINE(2);
	private int type;

	PlayerType(int _type) {
		type = _type;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

}
