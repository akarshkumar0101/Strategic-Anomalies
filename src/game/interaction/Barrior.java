package game.interaction;

public class Barrior {

    private final int shieldAmount;

    private final BarriorType barriorType;

    private final Object source;

    public Barrior(int shieldAmount, BarriorType barriorType, Object source) {
	this.shieldAmount = shieldAmount;
	this.barriorType = barriorType;
	this.source = source;

    }

    public int getShieldAmount() {
	return shieldAmount;
    }

    public BarriorType getBarriorType() {
	return barriorType;
    }

    public Object getSource() {
	return source;
    }

    @Override
    public boolean equals(Object another) {
	if (!(another instanceof Damage)) {
	    return false;
	}
	Barrior barrior = (Barrior) another;
	if (barrior.shieldAmount == shieldAmount && barrior.barriorType.equals(barriorType)
		&& barrior.source.equals(source)) {
	    return true;
	}
	return false;
    }
}
