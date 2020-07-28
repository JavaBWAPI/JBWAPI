package bwapi;

import java.util.function.Consumer;
import java.util.function.Function;

/**
* A side effect is an interaction that a bot attempts to have with the game.
* This entails sending a game or unit command, or drawing a shape.
*/
class SideEffect {

    private Consumer<ClientData> application;

    void apply(ClientData clientData) {
        application.accept(clientData);
    }

    private SideEffect() {}

    static SideEffect addUnitCommand(final int type, final int unit, final int target, final int x, final int y, final int extra) {
        SideEffect output = new SideEffect();
        output.application = (ClientData clientData) -> {
            ClientData.UnitCommand unitCommand = GameDataUtils.addUnitCommand(clientData.gameData());
            unitCommand.setTid(type);
            unitCommand.setUnitIndex(unit);
            unitCommand.setTargetIndex(target);
            unitCommand.setX(x);
            unitCommand.setY(y);
            unitCommand.setExtra(extra);
        };
        return output;
    }

    static SideEffect addCommand(final CommandType type, final int value1, final int value2) {
        SideEffect output = new SideEffect();
        output.application = (ClientData clientData) -> {
            ClientData.Command command = GameDataUtils.addCommand(clientData.gameData());
            command.setType(type);
            command.setValue1(value1);
            command.setValue2(value2);
        };
        return output;
    }

    static SideEffect addShape(final ShapeType type, final CoordinateType coordType, final int x1, final int y1, final int x2, final int y2, final int extra1, final int extra2, final int color, final boolean isSolid) {
        SideEffect output = new SideEffect();
        output.application = (ClientData clientData) -> {
            ClientData.Shape shape = GameDataUtils.addShape(clientData.gameData());
            shape.setType(type);
            shape.setCtype(coordType);
            shape.setX1(x1);
            shape.setY1(y1);
            shape.setX2(x2);
            shape.setY2(y2);
            shape.setExtra1(extra1);
            shape.setExtra2(extra2);
            shape.setColor(color);
            shape.setIsSolid(isSolid);
        };
        return output;
    }
}