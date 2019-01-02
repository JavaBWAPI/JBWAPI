package bwapi;

import org.junit.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UnitTest {
    /**
     * Test data generated using BWMirror with:
     *
     * <pre>
     * {@code
     * List<Unit> mins = mirror.getGame().getStaticMinerals().stream().filter(Unit::isVisible).collect(Collectors.toList());
     * System.out.println("{" + mins.stream().map(m -> "{" + m.getX() + ", " + m.getY() + "}").collect(Collectors.joining(", ")) + "}");
     * mirror.getGame().self().getUnits().forEach( u -> {
     *     System.out.print(u.getType() + ": " + u.getPosition());
     *     System.out.println(" {" + mins.stream().map(m -> ""+u.getDistance(m)).collect(Collectors.joining(", ")) + "}");
     * });
     * }
     * </pre>
     *
     * Output:
     * {{96, 144}, {96, 208}, {64, 240}, {96, 272}, {64, 176}, {128, 400}, {64, 304}, {96, 368}, {96, 336}}
     * Terran_SCV: [288, 296] {185, 160, 181, 149, 203, 137, 181, 154, 149}
     * Terran_SCV: [264, 296] {164, 139, 157, 125, 181, 115, 157, 133, 125}
     * Terran_SCV: [312, 296] {208, 183, 205, 173, 225, 159, 205, 177, 173}
     * Terran_SCV: [240, 296] {155, 116, 133, 101, 158, 96, 133, 110, 101}
     * Terran_Command_Center: [288, 240] {109, 102, 134, 102, 134, 121, 134, 121, 109}
     *
     */
    @Test
    public void checkDistance() {
        final int[][] minPos = new int[][]{
                {96, 144}, {96, 208}, {64, 240}, {96, 272}, {64, 176}, {128, 400}, {64, 304}, {96, 368}, {96, 336}
        };
        List<Unit> minerals = Arrays.stream(minPos)
                .map(m -> createUnit(UnitType.Resource_Mineral_Field, new Position(m[0], m[1])))
                .collect(Collectors.toList());

        HashMap<Unit, List<Integer>> units = new HashMap<>();  // Unit with respective resulting distance array
        units.put(createUnit(UnitType.Terran_SCV, new Position(288, 296)), Arrays.asList(185, 160, 181, 149, 203, 137, 181, 154, 149));
        units.put(createUnit(UnitType.Terran_SCV, new Position(264, 296)), Arrays.asList(164, 139, 157, 125, 181, 115, 157, 133, 125));
        units.put(createUnit(UnitType.Terran_SCV, new Position(312, 296)), Arrays.asList(208, 183, 205, 173, 225, 159, 205, 177, 173));
        units.put(createUnit(UnitType.Terran_SCV, new Position(240, 296)), Arrays.asList(155, 116, 133, 101, 158, 96, 133, 110, 101));
        units.put(createUnit(UnitType.Terran_Command_Center, new Position(288, 240)), Arrays.asList(109, 102, 134, 102, 134, 121, 134, 121, 109));

        for (Unit unit : units.keySet()) {
            List<Integer> results = units.get(unit);
            assertEquals(minerals.size(), results.size());
            for (int i=0; i < minerals.size(); i++) {
                Unit mineral = minerals.get(i);
                when(unit.getDistance(mineral)).thenCallRealMethod();
                int calculated = unit.getDistance(mineral);
                int expected = results.get(i);
                assertEquals(calculated, expected);
            }
        }
    }

    private Unit createUnit(UnitType type, Position position) {
        Unit u = mock(Unit.class);
        when(u.getType()).thenReturn(type);
        when(u.getPosition()).thenReturn(position);
        when(u.exists()).thenReturn(true);
        when(u.getLeft()).thenCallRealMethod();
        when(u.getRight()).thenCallRealMethod();
        when(u.getTop()).thenCallRealMethod();
        when(u.getBottom()).thenCallRealMethod();
        when(u.getX()).thenCallRealMethod();
        when(u.getY()).thenCallRealMethod();
        return u;
    }
}
