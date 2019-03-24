package bwem;


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
    }
}
