package bwapi;

import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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

    @Test
    public void getUnitsInRadiusShouldExcludeSelf() {
        Game game = spy(new Game());
        doReturn(0).when(game).getFrameCount();

        Unit selfUnit = spy(new Unit(mock(ClientData.UnitData.class), 0, game));
        Position pos = new Position(100, 100);
        doReturn(pos).when(selfUnit).getPosition();
        doReturn(90).when(selfUnit).getLeft();
        doReturn(110).when(selfUnit).getRight();
        doReturn(90).when(selfUnit).getTop();
        doReturn(110).when(selfUnit).getBottom();
        doReturn(true).when(selfUnit).exists();
        doReturn(0).when(selfUnit).getDistance(any(Unit.class));
        doReturn(0).when(selfUnit).getDistance(any(Position.class));

        Unit neighbor = mock(Unit.class);
        when(neighbor.getPosition()).thenReturn(new Position(105, 100));
        when(neighbor.getLeft()).thenReturn(95);
        when(neighbor.getRight()).thenReturn(115);
        when(neighbor.getTop()).thenReturn(90);
        when(neighbor.getBottom()).thenReturn(110);
        when(neighbor.exists()).thenReturn(true);
        when(neighbor.getDistance(any(Position.class))).thenReturn(0);

        game.setAllUnits(Arrays.asList(selfUnit, neighbor));

        List<Unit> result = selfUnit.getUnitsInRadius(50, u -> true);
        assertEquals(Collections.singletonList(neighbor), result);
    }

    @Test
    public void getUnitsInWeaponRangeShouldOnlyEvaluatePredicateForValidTargets() {
        Game game = spy(new Game());
        doReturn(0).when(game).getFrameCount();
        Player selfPlayer = mock(Player.class);

        Unit selfUnit = spy(new Unit(mock(ClientData.UnitData.class), 0, game));
        doReturn(true).when(selfUnit).exists();
        doReturn(selfPlayer).when(selfUnit).getPlayer();
        doReturn(100).when(selfUnit).getLeft();
        doReturn(100).when(selfUnit).getTop();
        doReturn(120).when(selfUnit).getRight();
        doReturn(120).when(selfUnit).getBottom();

        Unit invincibleUnit = mock(Unit.class);
        when(invincibleUnit.getLeft()).thenReturn(110);
        when(invincibleUnit.getTop()).thenReturn(110);
        when(invincibleUnit.getRight()).thenReturn(130);
        when(invincibleUnit.getBottom()).thenReturn(130);
        when(invincibleUnit.isInvincible()).thenReturn(true);

        Unit outOfRangeUnit = mock(Unit.class);
        when(outOfRangeUnit.getLeft()).thenReturn(110);
        when(outOfRangeUnit.getTop()).thenReturn(110);
        when(outOfRangeUnit.getRight()).thenReturn(130);
        when(outOfRangeUnit.getBottom()).thenReturn(130);
        when(outOfRangeUnit.isInvincible()).thenReturn(false);
        doReturn(9999).when(selfUnit).getDistance(outOfRangeUnit);

        game.setAllUnits(Arrays.asList(selfUnit, invincibleUnit, outOfRangeUnit));

        AtomicInteger predicateCalls = new AtomicInteger(0);
        selfUnit.getUnitsInWeaponRange(WeaponType.Gauss_Rifle, u -> {
            predicateCalls.incrementAndGet();
            return true;
        });

        assertEquals(0, predicateCalls.get());
    }

    @Test
    public void canAttackUnitGroupedShouldCheckTargetUnitInvincibility() {
        Unit attacker = mock(Unit.class);
        Unit target = mock(Unit.class);
        Player selfPlayer = mock(Player.class);

        when(attacker.getPlayer()).thenReturn(selfPlayer);
        when(attacker.getType()).thenReturn(UnitType.Terran_Marine);
        when(attacker.isInvincible()).thenReturn(false);

        when(target.isInvincible()).thenReturn(true);
        when(target.isFlying()).thenReturn(false);
        when(target.getType()).thenReturn(UnitType.Terran_SCV);

        when(attacker.canAttackUnitGrouped(target, false, false, false, false)).thenCallRealMethod();

        assertFalse(attacker.canAttackUnitGrouped(target, false, false, false, false));
    }

    @Test
    public void canGatherShouldCheckPathToTargetPosition() {
        Game game = mock(Game.class);
        Player selfPlayer = mock(Player.class);
        when(game.self()).thenReturn(selfPlayer);

        Position workerPos = new Position(100, 100);
        Position unreachableMineralPos = new Position(500, 500);

        Unit worker = spy(new Unit(mock(ClientData.UnitData.class), 0, game));
        doReturn(workerPos).when(worker).getPosition();
        doReturn(selfPlayer).when(worker).getPlayer();
        doReturn(true).when(worker).isCompleted();
        doReturn(true).when(worker).hasPath(workerPos);
        doReturn(false).when(worker).hasPath(unreachableMineralPos);

        Unit unreachableMineral = mock(Unit.class);
        when(unreachableMineral.getType()).thenReturn(UnitType.Resource_Mineral_Field);
        when(unreachableMineral.getPosition()).thenReturn(unreachableMineralPos);
        when(unreachableMineral.isCompleted()).thenReturn(true);

        assertFalse(worker.canGather(unreachableMineral, false, false, false));
    }

    @Test
    public void canGatherShouldCheckTargetRefineryOwner() {
        Game game = mock(Game.class);
        Player selfPlayer = mock(Player.class);
        Player enemyPlayer = mock(Player.class);
        when(game.self()).thenReturn(selfPlayer);

        Position workerPos = new Position(100, 100);
        Position reachableRefineryPos = new Position(150, 150);

        Unit worker = spy(new Unit(mock(ClientData.UnitData.class), 0, game));
        doReturn(workerPos).when(worker).getPosition();
        doReturn(selfPlayer).when(worker).getPlayer();
        doReturn(true).when(worker).isCompleted();
        doReturn(true).when(worker).hasPath(workerPos);
        doReturn(true).when(worker).hasPath(reachableRefineryPos);

        Unit enemyRefinery = mock(Unit.class);
        when(enemyRefinery.getType()).thenReturn(UnitType.Terran_Refinery);
        when(enemyRefinery.getPosition()).thenReturn(reachableRefineryPos);
        when(enemyRefinery.getPlayer()).thenReturn(enemyPlayer);
        when(enemyRefinery.isCompleted()).thenReturn(true);

        assertFalse(worker.canGather(enemyRefinery, false, false, false));
    }

    @Test
    public void canGatherShouldCheckTargetUnitCompleted() {
        Game game = mock(Game.class);
        Player selfPlayer = mock(Player.class);
        when(game.self()).thenReturn(selfPlayer);

        Position workerPos = new Position(100, 100);
        Position refineryPos = new Position(150, 150);

        Unit worker = spy(new Unit(mock(ClientData.UnitData.class), 0, game));
        doReturn(workerPos).when(worker).getPosition();
        doReturn(selfPlayer).when(worker).getPlayer();
        doReturn(true).when(worker).isCompleted();
        doReturn(true).when(worker).hasPath(refineryPos);

        Unit incompleteRefinery = mock(Unit.class);
        when(incompleteRefinery.getType()).thenReturn(UnitType.Terran_Refinery);
        when(incompleteRefinery.getPosition()).thenReturn(refineryPos);
        when(incompleteRefinery.getPlayer()).thenReturn(selfPlayer);
        when(incompleteRefinery.isCompleted()).thenReturn(false);

        assertFalse(worker.canGather(incompleteRefinery, false, false, false));
    }

    @Test
    public void canUseTechUnitHealingShouldRejectEnemyTarget() {
        Player selfPlayer = mock(Player.class);
        Player enemyPlayer = mock(Player.class);
        when(enemyPlayer.isNeutral()).thenReturn(false);
        when(selfPlayer.isEnemy(selfPlayer)).thenReturn(false);
        when(selfPlayer.isEnemy(enemyPlayer)).thenReturn(true);

        Unit medic = mock(Unit.class);
        when(medic.getPlayer()).thenReturn(selfPlayer);

        Unit enemyMarine = mock(Unit.class);
        when(enemyMarine.getPlayer()).thenReturn(enemyPlayer);
        when(enemyMarine.getType()).thenReturn(UnitType.Terran_Marine);
        when(enemyMarine.isFlying()).thenReturn(false);
        when(enemyMarine.getHitPoints()).thenReturn(20);

        when(medic.canUseTechUnit(TechType.Healing, enemyMarine, false, false, false, false)).thenCallRealMethod();

        assertFalse(medic.canUseTechUnit(TechType.Healing, enemyMarine, false, false, false, false));
    }
}
