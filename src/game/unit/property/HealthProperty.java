package game.unit.property;

import game.interaction.Damage;
import game.interaction.Healing;
import game.interaction.effect.EffectType;
import game.interaction.incident.Condition;
import game.unit.Unit;

public class HealthProperty extends Property<Integer> {

    private final Property<Integer> maxHealthProperty;

    private final ArmorProperty armorProp;

    public HealthProperty(Unit unit, int initialHealth, int initialArmor) {
	super(unit, initialHealth);
	maxHealthProperty = new Property<Integer>(unit, initialHealth) {
	    @Override
	    protected void propertyChanged(Integer oldValue, Integer newValue) {
		super.notifyPropertyChanged(oldValue, newValue);
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

    public double percentageHealth() {
	return (double) getCurrentPropertyValue() / getDefaultPropertyValue();
    }

    public void takeHealing(Healing healing) {
	int healingAmount = healing.getHealingAmount();
	if (healingAmount == 0) {
	    return;
	}

	int maxHealth = maxHealthProperty.getCurrentPropertyValue();
	int currentHealth = getCurrentPropertyValue();
	if (currentHealth + healingAmount > maxHealth) {
	    healingAmount = maxHealth - currentHealth;
	}
	int healingAmountFinal = healingAmount;

	addPropEffect(
		new PropertyEffect<Integer>(EffectType.PERMANENT, healing.getSource(), Condition.trueCondition, 0) {
		    @Override
		    public Integer affectProperty(Integer initHealth) {
			return initHealth + healingAmountFinal;
		    }
		});
	if (getCurrentPropertyValue() <= 0) {
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

	addPropEffect(
		new PropertyEffect<Integer>(EffectType.PERMANENT, damage.getSource(), Condition.trueCondition, 0) {
		    @Override
		    public Integer affectProperty(Integer initHealth) {
			return initHealth - damageAmount;
		    }
		});
	if (getCurrentPropertyValue() <= 0) {
	    getUnitOwner().triggerDeath();
	}
    }

    @Override
    protected void propertyChanged(Integer oldValue, Integer newValue) {
	super.notifyPropertyChanged(oldValue, newValue);
    }
}
