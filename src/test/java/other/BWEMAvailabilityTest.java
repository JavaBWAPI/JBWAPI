package other;


import bwem.Altitude;
import bwem.BWEM;
import bwem.BWMap;

/**
 * Simple class to check all required public methods are available
 */
public class BWEMAvailabilityTest {


    void ignore() {
        // BWEM
        BWEM bwem = new BWEM(null);
        bwem.initialize();

        //BWMap
        BWMap map = bwem.getMap();
        map.automaticPathUpdate();
        map.enableAutomaticPathAnalysis();

        // called FindBasesForStartingLocations in BWEM
        map.assignStartingLocationsToSuitableBases();


        // Available in map.getData().getMapData()
        // // Returns the size of the Map in Tiles.
	    // const BWAPI::TilePosition & Size() const { return m_Size; }
        //
        // // Returns the size of the Map in MiniTiles.
	    // const BWAPI::WalkPosition & WalkSize() const							{ return m_WalkSize; }

        // // Returns the center of the Map in pixels.
        //const BWAPI::Position &Center() const								{ return m_center; }

        // // Returns a random position in the Map in pixels.
        // BWAPI::Position	RandomPosition() const;

        // called MaxAltitude in BWEM
        Altitude at = map.getHighestAltitude();

    }
}
