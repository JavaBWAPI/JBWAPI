package bwapi;

class BuildingPlacer {
    private static int MAX_RANGE = 64;
    private static TilePosition gDirections[] = {
            new TilePosition(1, 1),
            new TilePosition(0, 1),
            new TilePosition(-1, 1),
            new TilePosition(1, 0),
            new TilePosition(-1, 0),
            new TilePosition(1, -1),
            new TilePosition(0, -1),
            new TilePosition(-1, -1)
    };
    private static buildTemplate buildTemplates[] = // [13 + 1]
            {
                    new buildTemplate(32, 0, 0, 1),
                    new buildTemplate(0, 32, 1, 0),
                    new buildTemplate(31, 0, 0, 1),
                    new buildTemplate(0, 31, 1, 0),
                    new buildTemplate(33, 0, 0, 1),
                    new buildTemplate(0, 33, 1, 0),
                    new buildTemplate(30, 0, 0, 1),
                    new buildTemplate(29, 0, 0, 1),
                    new buildTemplate(0, 30, 1, 0),
                    new buildTemplate(28, 0, 0, 1),
                    new buildTemplate(0, 29, 1, 0),
                    new buildTemplate(27, 0, 0, 1),
                    new buildTemplate(0, 28, 1, 0),
                    new buildTemplate(-1, 0, 0, 0) // last
            };

    static TilePosition getBuildLocation(final UnitType type, final TilePosition desiredPosition1, final int maxRange, final boolean creep, final Game game) {
        // Make sure the type is compatible
        if (!type.isBuilding()) {
            return TilePosition.Invalid;
        }

        TilePosition desiredPosition = desiredPosition1;

        // Do type-specific checks
        boolean trimPlacement = true;
        Region pTargRegion = null;
        switch (type) {
            case Protoss_Pylon:
                final Unit pSpecialUnitTarget = game.getClosestUnitInRadius(
                        desiredPosition.toPosition(), 999999, (u -> u.getPlayer().equals(game.self()) && !u.isPowered()));
                if (pSpecialUnitTarget != null) {
                    desiredPosition = pSpecialUnitTarget.getPosition().toTilePosition();
                    trimPlacement = false;
                }
                break;
            case Terran_Command_Center:
            case Protoss_Nexus:
            case Zerg_Hatchery:
            case Special_Start_Location:
                trimPlacement = false;
                break;
            case Zerg_Creep_Colony:
            case Terran_Bunker:
                //if ( Get bunker placement region )
                //  trimPlacement = false;
                break;
        }

        PlacementReserve reserve = new PlacementReserve(maxRange);
        ReservePlacement(reserve, type, desiredPosition, game);

        if (trimPlacement)
            reserveTemplateSpacing(reserve);

        TilePosition centerPosition = desiredPosition.subtract((new TilePosition(MAX_RANGE, MAX_RANGE).divide(2)));
        if (pTargRegion != null)
            desiredPosition = pTargRegion.getCenter().toTilePosition();

        // Find the best position
        int bestDistance;
        int fallbackDistance;
        TilePosition bestPosition;
        TilePosition fallbackPosition;

        bestDistance = fallbackDistance = 999999;
        bestPosition = fallbackPosition = TilePosition.None;
        for (int passCount = 0; passCount < (pTargRegion != null ? 2 : 1); ++passCount) {
            for (int y = 0; y < MAX_RANGE; ++y)
                for (int x = 0; x < MAX_RANGE; ++x) {
                    // Ignore if space is reserved
                    if (reserve.getValue(x, y) == 0) {
                        continue;
                    }
                    final TilePosition currentPosition = new TilePosition(x, y).add(centerPosition);
                    //Broodwar->getGroundDistance( desiredPosition, currentPosition );
                    final int currentDistance = desiredPosition.toPosition().getApproxDistance(currentPosition.toPosition());
                    if (currentDistance < bestDistance) {
                        if (currentDistance <= maxRange) {
                            bestDistance = currentDistance;
                            bestPosition = currentPosition;
                        } else if (currentDistance < fallbackDistance) {
                            fallbackDistance = currentDistance;
                            fallbackPosition = currentPosition;
                        }
                    }
                }
            // Break pass if position is found
            if (bestPosition != TilePosition.None)
                break;

            // Break if an alternative position was found
            if (fallbackPosition != TilePosition.None) {
                bestPosition = fallbackPosition;
                break;
            }

            // If we were really targetting a region, and couldn't find a position above
            if (pTargRegion != null) // Then fallback to the default build position
                desiredPosition = centerPosition;
        }

        return bestPosition;
    }

    private static void ReservePlacement(final PlacementReserve reserve, final UnitType type, final TilePosition desiredPosition, final Game game) {
        // Reset the array
        reserve.reset();

        AssignBuildableLocations(reserve, type, desiredPosition, game);
        RemoveDisconnected(reserve, desiredPosition, game);

        // @TODO: Assign 0 to all locations that have a ground distance > maxRange

        // exclude positions off the map
        final TilePosition start = desiredPosition.subtract((new TilePosition(MAX_RANGE, MAX_RANGE).divide(2)));
        reserve.iterate((pr, x, y) -> {
            if (!(start.add(new TilePosition(x, y)).isValid(game))) {
                pr.setValue(x, y, (byte) 0);
            }
        });

        // Return if can't find a valid space
        if (!reserve.hasValidSpace()) {
            return;
        }

        ReserveGroundHeight(reserve, desiredPosition, game);
        //ReserveUnbuildable(reserve, type, desiredPosition); // NOTE: canBuildHere already includes this!

        if (!type.isResourceDepot()) {
            ReserveAllStructures(reserve, type, desiredPosition, game);
            ReserveExistingAddonPlacement(reserve, desiredPosition, game);
        }

        // Unit-specific reservations
        switch (type) {
            case Protoss_Pylon:  // @TODO
                //reservePylonPlacement();
                break;
            case Terran_Bunker:  // @TODO
                //if ( !GetBunkerPlacement() )
            {
                //reserveTurretPlacement();
            }
            break;
            case Terran_Missile_Turret:  // @TODO
            case Protoss_Photon_Cannon:
                //reserveTurretPlacement();
                break;
            case Zerg_Creep_Colony:  // @TODO
                //if ( creep || !GetBunkerPlacement() )
            {
                //reserveTurretPlacement();
            }
            break;
            default:
                if (!type.isResourceDepot())
                    ReserveDefault(reserve, type, desiredPosition, game);
                break;
        }
    }

    private static void AssignBuildableLocations(final PlacementReserve reserve, final UnitType type, final TilePosition desiredPosition, final Game game) {
        final TilePosition start = desiredPosition.subtract(new TilePosition(MAX_RANGE, MAX_RANGE).divide(2));

        // Reserve space for the addon as well
        final boolean hasAddon = type.canBuildAddon();

        // Assign 1 to all buildable locations
        reserve.iterate((pr, x, y) -> {
            if ((!hasAddon || game.canBuildHere(start.add(new TilePosition(x + 4, y + 1)), UnitType.Terran_Missile_Turret)) &&
                    game.canBuildHere(start.add(new TilePosition(x, y)), type)) {
                pr.setValue(x, y, (byte) 1);
            }
        });
    }

    private static void RemoveDisconnected(final PlacementReserve reserve, final TilePosition desiredPosition, final Game game) {
        final TilePosition start = desiredPosition.subtract(new TilePosition(MAX_RANGE, MAX_RANGE)).divide(2);

        // Assign 0 to all locations that aren't connected
        reserve.iterate((pr, x, y) -> {
            if (!game.hasPath(desiredPosition.toPosition(), start.add(new TilePosition(x, y)).toPosition())) {
                pr.setValue(x, y, (byte) 0);
            }
        });
    }

    private static void ReserveGroundHeight(final PlacementReserve reserve, final TilePosition desiredPosition, final Game game) {
        final TilePosition start = desiredPosition.subtract(new TilePosition(MAX_RANGE, MAX_RANGE)).divide(2);

        // Exclude locations with a different ground height, but restore a backup in case there are no more build locations
        reserve.backup();
        int targetHeight = game.getGroundHeight(desiredPosition);
        reserve.iterate((pr, x, y) -> {
            if (game.getGroundHeight(start.add(new TilePosition(x, y))) != targetHeight) {
                pr.setValue(x, y, (byte) 0);
            }
        });

        // Restore original if there is nothing left
        reserve.restoreIfInvalid();
    }

    private static void ReserveAllStructures(final PlacementReserve reserve, final UnitType type, final TilePosition desiredPosition, final Game game) {
        if (type.isAddon()) {
            return;
        }
        reserve.backup();

        // Reserve space around owned resource depots and resource containers
        game.self().getUnits().stream()
                .filter(u -> {
                    final UnitType ut = u.getType();
                    return u.exists() && (u.isCompleted() || (ut.producesLarva() && u.isMorphing())) && ut.isBuilding() && (ut.isResourceDepot() || ut.isRefinery());
                })
                .forEach(u -> ReserveStructure(reserve, u, 2, type, desiredPosition));

        // Reserve space around neutral resources
        if (type != UnitType.Terran_Bunker) {
            game.getNeutralUnits().stream()
                    .filter(u -> u.exists() && u.getType().isResourceContainer())
                    .forEach(u -> ReserveStructure(reserve, u, 2, type, desiredPosition));

        }
        reserve.restoreIfInvalid();
    }

    private static void ReserveExistingAddonPlacement(final PlacementReserve reserve, final TilePosition desiredPosition, final Game game) {
        final TilePosition start = desiredPosition.subtract(new TilePosition(MAX_RANGE, MAX_RANGE)).divide(2);

        //Exclude addon placement locations
        reserve.backup();
        game.self().getUnits().stream()
                .filter(u -> u.exists() && u.getType().canBuildAddon())
                .forEach(u -> {
                    final TilePosition addonPos = (u.getTilePosition().add(new TilePosition(4, 1))).subtract(start);
                    reserve.setRange(addonPos, addonPos.add(new TilePosition(2, 2)), (byte) 0);
                });

        // Restore if this gave us no build locations
        reserve.restoreIfInvalid();
    }

    private static void ReserveDefault(final PlacementReserve reserve, final UnitType type, final TilePosition desiredPosition, final Game game) {
        reserve.backup();
        PlacementReserve original = reserve;

        // Reserve some space around some specific units
        for (final Unit it : game.self().getUnits()) {
            if (!it.exists()) {
                continue;
            }

            switch (it.getType()) {
                case Terran_Factory:
                case Terran_Missile_Turret:
                case Protoss_Robotics_Facility:
                case Protoss_Gateway:
                case Protoss_Photon_Cannon:
                case Terran_Barracks:
                case Terran_Bunker:
                case Zerg_Creep_Colony:
                    ReserveStructure(reserve, it, 1, type, desiredPosition);
                    break;
                default:
                    ReserveStructure(reserve, it, 2, type, desiredPosition);
                    break;
            }
        }

        switch (type) {
            case Terran_Barracks:
            case Terran_Factory:
            case Terran_Missile_Turret:
            case Terran_Bunker:
            case Protoss_Robotics_Facility:
            case Protoss_Gateway:
            case Protoss_Photon_Cannon:
                for (int y = 0; y < 64; ++y) {
                    for (int x = 0; x < 64; ++x) {
                        for (int dir = 0; dir < 8; ++dir) {
                            TilePosition p = new TilePosition(x, y).add(gDirections[dir]);
                            if (!PlacementReserve.isValidPos(p) || original.getValue(p) == 0)
                                reserve.setValue(p, (byte) 0);
                        }

                    }
                }
                break;
        }
        reserve.restoreIfInvalid();
    }

    private static void reserveTemplateSpacing(final PlacementReserve reserve) {
        reserve.backup();

        for (int j = 0; buildTemplates[j].startX != -1; ++j) {
            final buildTemplate t = buildTemplates[j];
            int x = t.startX;
            int y = t.startY;
            for (int i = 0; i < 64; ++i) {
                reserve.setValue(x, y, (byte) 0);
                x += t.stepX;
                y += t.stepY;
            }
        }

        reserve.restoreIfInvalid();
    }

    private static void ReserveStructure(final PlacementReserve reserve, final Unit pUnit, final int padding, final UnitType type, final TilePosition desiredPosition) {
        ReserveStructureWithPadding(reserve, pUnit.getPosition().toTilePosition(), pUnit.getType().tileSize(), padding, type, desiredPosition);
    }

    private static void ReserveStructureWithPadding(final PlacementReserve reserve, final TilePosition currentPosition, final TilePosition sizeExtra, final int padding, final UnitType type, final TilePosition desiredPosition) {
        final TilePosition paddingSize = sizeExtra.add((new TilePosition(padding, padding).multiply(2)));

        final TilePosition topLeft = currentPosition.subtract(type.tileSize()).subtract((paddingSize.divide(2))).subtract(new TilePosition(1, 1));
        final TilePosition topLeftRelative = topLeft.subtract(desiredPosition).add(new TilePosition(MAX_RANGE, MAX_RANGE).divide(2));
        final TilePosition maxSize = topLeftRelative.add(type.tileSize()).add(paddingSize).add(new TilePosition(1, 1));

        reserve.setRange(topLeftRelative, maxSize, (byte) 0);
    }

    interface PlacementReserveExec {
        void operation(PlacementReserve placementReserve, int x, int y);
    }

    private static class buildTemplate {
        int startX;
        int startY;
        int stepX;
        int stepY;

        buildTemplate(int startX, int startY, int stepX, int stepY) {
            this.startX = startX;
            this.startY = startY;
            this.stepX = stepX;
            this.stepY = stepY;
        }
    }

    static class PlacementReserve {
        public final int maxSearch;
        byte data[][];
        byte save[][];

        PlacementReserve(int maxRange) {
            maxSearch = Math.min(Math.max(0, maxRange), MAX_RANGE);
            reset();
            backup();
        }

        // Checks if the given x/y value is valid for the Placement position
        static boolean isValidPos(final int x, final int y) {
            return x >= 0 && x < MAX_RANGE && y >= 0 && y < MAX_RANGE;
        }

        static boolean isValidPos(final TilePosition p) {
            return isValidPos(p.x, p.y);
        }

        void reset() {
            data = new byte[MAX_RANGE][MAX_RANGE];
            save = new byte[MAX_RANGE][MAX_RANGE];
        }

        // Sets the value in the placement reserve array
        void setValue(final int x, final int y, final byte value) {
            if (isValidPos(x, y)) {
                data[y][x] = value;
            }
        }

        void setValue(final TilePosition p, final byte value) {
            setValue(p.x, p.y, value);
        }

        void setRange(final int left, final int top, final int right, final int bottom, final byte value) {
            for (int y = top; y < bottom; ++y) {
                for (int x = left; x < right; ++x) {
                    setValue(x, y, value);
                }
            }
        }

        void setRange(TilePosition lt, TilePosition rb, byte value) {
            setRange(lt.x, lt.y, rb.x, rb.y, value);
        }

        // Gets the value from the placement reserve array, 0 if position is invalid
        byte getValue(final int x, final int y) {
            if (isValidPos(x, y))
                return data[y][x];
            return 0;
        }

        byte getValue(final TilePosition p) {
            return getValue(p.x, p.y);
        }

        void iterate(PlacementReserveExec proc) {
            // Get min/max distances
            int min = MAX_RANGE / 2 - maxSearch / 2;
            int max = min + maxSearch;
            for (int y = min; y < max; ++y)
                for (int x = min; x < max; ++x) {
                    proc.operation(this, x, y);
                }
        }

        boolean hasValidSpace() {
            // Get min/max distances
            int min = MAX_RANGE / 2 - maxSearch / 2;
            int max = min + maxSearch;
            for (int y = min; y < max; ++y) {
                for (int x = min; x < max; ++x) {
                    if (getValue(x, y) == 1)
                        return true;
                }
            }
            return false;
        }

        ;

        void backup() {
            System.arraycopy(save, 0, data, 0, save.length);
        }

        void restore() {
            System.arraycopy(data, 0, save, 0, data.length);
        }

        void restoreIfInvalid() {
            if (!hasValidSpace()) {
                restore();
            }
        }
    }
}
