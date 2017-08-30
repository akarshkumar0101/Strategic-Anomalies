package game.unit.property;

import game.interaction.Damage;
import game.interaction.Healing;
import game.interaction.effect.EffectType;
import game.unit.Unit;

public class HealthProperty extends Property<Integer> {

    private final Property<Integer> maxHealthProp;

    private final ArmorProperty armorProp;

    public HealthProperty(Unit unit, int initialHealth, int initialArmor) {
	super(unit, initialHealth);
	maxHealthProp = new Property<>(unit, initialHealth);

	armorProp = new ArmorProperty(unit, initialArmor);

	setupNaturalPropEffects();
    }

    private void setupNaturalPropEffects() {
	addPropEffect(new PropertyEffect<Integer>(EffectType.OTHER, this, 10) {
	    @Override
	    public Integer affectProperty(Integer initValue) {
		if (initValue > maxHealthProp.getValue()) {
		    return maxHealthProp.getValue();
		} else {
		    return initValue;
		}
	    }
	});
	updateValueOnReporter(maxHealthProp.getChangeReporter());
    }

    public Property<Integer> getMaxHealthProperty() {
	return maxHealthProp;
    }

    public ArmorProperty getArmorProp() {
	return armorProp;
    }

    public double currentPercentageHealth() {
	return (double) getValue() / getDefaultValue();
    }

    public void takeHealing(Healing healing) {
	addPropEffect(new PropertyEffect<Integer>(EffectType.PERMANENT_BASE, healing, 0) {
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
	addPropEffect(new PropertyEffect<Integer>(EffectType.PERMANENT_BASE, damage, 0) {
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
