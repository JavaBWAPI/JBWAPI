# JBWAPI
Pure Java [bwapi](https://github.com/bwapi/bwapi) client (4.2.0) implementation backed by [N00byEdge](https://github.com/N00byEdge)'s [JavaBWAPIBackend](https://github.com/N00byEdge/JavaBWAPIBackend).

Also contains the pure Java BWEM implementation from [BWAPI4J](https://github.com/OpenBW/BWAPI4J).

**WORK IN PROGRESS**

If you currently need a Java API for bot development, please use the much more modern and stable [BWAPI4J](https://github.com/OpenBW/BWAPI4J).

If you find any bugs please create an issue.

### goals
Have a similar (java) interface to BWMirror to make porting bwmirror bots easy without all the DLL and JNI hassle and overhead.

### advantages
 - no dependency on external DLL's
 - no type marshalling
 - fast (citation needed)

### compilation
`mvnw.cmd package`

or if you already have maven installed

`mvn package`
