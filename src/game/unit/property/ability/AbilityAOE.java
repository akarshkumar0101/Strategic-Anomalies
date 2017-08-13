package game.unit.property.ability;

import java.util.List;

import game.board.Square;

public interface AbilityAOE {

    public abstract List<Square> getAOESqaures(Square target);

}
