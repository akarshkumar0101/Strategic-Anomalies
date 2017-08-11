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
	maxHealthProperty = new Property<Integer>(unit, initialHealth) {

	    @Override
	    protected Object[] getSpecificationsOfPropertyChange(Integer oldValue, Integer newValue) {
		return null;
	    }
	};
	armorProp = new ArmorProperty(unit, initialArmor);
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
	int healingAmount = healing.getHealingAmount();
	if (healingAmount == 0) {
	    return;
	}

	int maxHealth = maxHealthProperty.getValue();
	int currentHealth = getValue();
	if (currentHealth + healingAmount > maxHealth) {
	    healingAmount = maxHealth - currentHealth;
	}
	int healingAmountFinal = healingAmount;

	addPropEffect(new PropertyEffect<Integer>(EffectType.PERMANENT, healing.getSource(), 0) {
	    @Override
	    public Integer affectProperty(Integer initHealth) {
		return initHealth + healingAmountFinal;
	    }
	});
	if (getValue() <= 0) {
	    getUnitOwner().triggerDeath();
	}
    }

    public void takeDamage(Damage damage) {
	if (!armorProp.attemptBlock(damage)) {
	    damage = armorProp.filterDamage(damage);
	    takeRawDamage(damage);
	}
    }

    private void takeRawDamage(Damage damage) {
	int damageAmount = damage.getDamageAmount();

	if (damageAmount == 0) {
	    return;
	}

	addPropEffect(new PropertyEffect<Integer>(EffectType.PERMANENT, damage.getSource(), 0) {
	    @Override
	    public Integer affectProperty(Integer initHealth) {
		return initHealth - damageAmount;
	    }
	});
	if (getValue() <= 0) {
	    getUnitOwner().triggerDeath();
	}
    }

    @Override
    protected Object[] getSpecificationsOfPropertyChange(Integer oldValue, Integer newValue) {
	return null;
    }
}
