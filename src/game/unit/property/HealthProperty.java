package game.unit.property;

import game.interaction.Damage;
import game.interaction.Healing;
import game.interaction.effect.EffectType;
import game.unit.Unit;

public class HealthProperty extends Property<Integer> {

    private final Property<Integer> maxHealthProperty;

    private final ArmorProperty armorProp;

    public HealthProperty(Unit unit, int initialHealth, int initialArmor) {
	super(unit, initialHealth);
	maxHealthProperty = new Property<>(unit, initialHealth);

	armorProp = new ArmorProperty(unit, initialArmor);

	addPropEffect(new PropertyEffect<Integer>(EffectType.OTHER, this, 10) {
	    @Override
	    public Integer affectProperty(Integer initValue) {
		if (initValue > maxHealthProperty.getValue()) {
		    return maxHealthProperty.getValue();
		} else {
		    return initValue;
		}
	    }
	});
    }

    public Property<Integer> getMaxHealthProperty() {
	return maxHealthProperty;
    }

    public ArmorProperty getArmorProp() {
	return armorProp;
    }

    public double currentPercentageHealth() {
	return (double) getValue() / getDefaultValue();
    }

    public void takeHealing(Healing healing) {
	addPropEffect(new PropertyEffect<Integer>(EffectType.PERMANENT, healing, 0) {
	    @Override
	    public Integer affectProperty(Integer initHealth) {
		return initHealth + healing.getHealingAmount();
	    }
	});
	checkDeath();
    }

    public void takeDamage(Damage damage) {
	if (!armorProp.attemptBlock(damage)) {
	    damage = armorProp.filterDamage(damage);
	    takeRawDamage(damage);
	}
    }

    private void takeRawDamage(Damage damage) {
	addPropEffect(new PropertyEffect<Integer>(EffectType.PERMANENT, damage, 0) {
	    @Override
	    public Integer affectProperty(Integer initHealth) {
		return initHealth - damage.getDamageAmount();
	    }
	});
	checkDeath();
    }

    private void checkDeath() {
	if (getValue() <= 0) {
	    getUnitOwner().triggerDeath();
	}
    }
}
