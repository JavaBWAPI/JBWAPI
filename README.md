# JBWAPI
Pure java bwapi client (4.2.0) implementation backed by N00byEdge's JavaBWAPIBackend.

WORK IN PROGRESS

### goals
Have a similar (java) interface to BWMirror to make porting bwmirror bots easy without all the DLL and JNI hassle and overhead.

### advantages
TODO

### compilation
TODO

### todo
 - make it actually run the event loop, and add events
 - search bwapi documentation for "since bwapi 4.2.0"
 - fix Color type (https://github.com/bwapi/bwapi/blob/456ad612abc84da4103162ba0bf8ec4f053a4b1d/bwapi/include/BWAPI/Color.h)
 - think about how to use/implements Event/EventType (ask n00byEdge?)
 - implement all the canX methods
 
### optionally later
 - cleanup code (checkstyle, formatting etc.)
 - port over the pure java BWEM implementation from BWAPI4J
 - add example implementation
 - add tests and CI
