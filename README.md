[![Build Status](https://travis-ci.org/JavaBWAPI/JBWAPI.svg?branch=develop)](https://travis-ci.org/JavaBWAPI/JBWAPI)
# JBWAPI
Pure Java [bwapi](https://github.com/bwapi/bwapi) client (4.2.0) implementation backed by [N00byEdge](https://github.com/N00byEdge)'s [JavaBWAPIBackend](https://github.com/N00byEdge/JavaBWAPIBackend) idea and automated by [Bytekeeper](https://github.com/Bytekeeper).

Also contains the pure Java BWEM implementation from [BWAPI4J](https://github.com/OpenBW/BWAPI4J).

## goals
Have a similar (Java) interface to BWMirror to make porting BWMirror bots easy without all the DLL and JNI hassle and overhead.

## advantages
 - no dependency on external DLL's
 - no type marshalling
 - fast (citation needed)
 - supports both 32 and 64 bit Java (e.g. [deeplearning4j](https://deeplearning4j.org/) requires 64 bit Java which bwmirror doesn't support)
 - BWEM instead of BWTA as map analyser

## warnings
 - JBWAPI by default has Lateny Compensation disabled (and at the moment has no LatCom at all).
 - A fake BWTA is provided for easier porting, but it translates BWTA calls to their respective BWEM calls, so specific Regions/Chokepoints etc. may differ.

## usage
**maven**

Add JitPack as a repository
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Add JBWAPI as a dependecy
```
<dependency>
    <groupId>com.github.JavaBWAPI</groupId>
    <artifactId>JBWAPI</artifactId>
    <version>0.4.1</version>
</dependency>
```

**gradle**

Add JitPack as a repository
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add JBWAPI as a dependecy
```
dependencies {
    implementation 'com.github.JavaBWAPI:JBWAPI:0.4.1'
}
```

**jar**

Alternatively add the latest .jar from the [releases](https://github.com/JavaBWAPI/JBWAPI/releases) page to your project.

## compilation
`mvnw.cmd package`

or if you already have maven installed

`mvn package`
