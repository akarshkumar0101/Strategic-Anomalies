package game.unit.properties;

import game.interaction.Damage;
import game.interaction.effect.EffectType;
import game.unit.Unit;

public class HealthProperty extends Property<Integer> {

    private final ArmorProperty armorProp;

    public HealthProperty(Unit unit, int initialHealth, int initialArmor) {
	super(unit, initialHealth);
	armorProp = new ArmorProperty(unit, initialArmor);
    }

    public ArmorProperty getArmorProp() {
	return armorProp;
    }

    public double percentageHealth() {
	return (double) getCurrentPropertyValue() / getDefaultPropertyValue();
    }

    public void takeDamage(Damage damage) {
	armorProp.filterDamage(damage);
	if (!damage.wasBlocked()) {
	    takeRawDamage(damage);
	}
    }

    private void takeRawDamage(Damage damage) {
	int damageAmount = damage.getDamageAmount();

	if (damageAmount == 0) {
	    return;
	}

	addPropEffect(new PropertyEffect<Integer>(EffectType.PERMANENT, damage.getSource(), null, 0) {
	    @Override
	    public Integer affectProperty(Integer initHealth) {
		return initHealth - damageAmount;
	    }
	});
	if (damageAmount > getCurrentPropertyValue()) {
	    // TODO trigger death of unit
	}
    }

    @Override
    protected void propertyChanged(Integer oldValue, Integer newValue) {
	super.notifyPropertyChanged(oldValue, newValue);
    }
}
