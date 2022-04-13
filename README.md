[![Build Status](https://travis-ci.org/JavaBWAPI/JBWAPI.svg?branch=develop)](https://travis-ci.org/JavaBWAPI/JBWAPI)[![Total alerts](https://img.shields.io/lgtm/alerts/g/JavaBWAPI/JBWAPI.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/JavaBWAPI/JBWAPI/alerts/)[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/JavaBWAPI/JBWAPI.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/JavaBWAPI/JBWAPI/context:java)
# JBWAPI

Pure Java [bwapi](https://github.com/bwapi/bwapi) 4.4.0 client implementation backed by [N00byEdge](https://github.com/N00byEdge)'s [JavaBWAPIBackend](https://github.com/N00byEdge/JavaBWAPIBackend) idea and automated by [Bytekeeper](https://github.com/Bytekeeper).

Also contains a modified version of the pure Java BWEM implementation from [BWAPI4J](https://github.com/OpenBW/BWAPI4J).

## Goals

 - Have a similar (Java) interface to BWMirror to make porting BWMirror bots easy without all the DLL and JNI hassle and overhead.
 - Stay as updated as possible with the BWAPI releases.

## Advantages

 - No dependency on external DLL's.
 - At least [5x](https://github.com/JavaBWAPI/JBWAPI/issues/17) faster compared to BWMirror for primitives as it directly reads the memory mapped client file. Even faster for BWAPI objects as it also avoids type marshalling
 - Supports both 32 and 64 bit Java (e.g. [deeplearning4j](https://deeplearning4j.org/) requires 64 bit Java which bwmirror doesn't support).
 - BWEM instead of BWTA as map analyser.
 - Supports Linux "natively" using [openbw](https://github.com/JavaBWAPI/JBWAPI/pull/73), made possible by by [ByteKeeper](https://github.com/Bytekeeper)
 - `Async` support for realtime tournament constraints, made possible by [dgant](https://github.com/dgant) 

## Warnings
 - A fake BWTA is provided for easier porting from BWMirror, but it translates BWTA calls to their respective BWEM calls, so specific Regions/Chokepoints etc. may differ.

## Usage

**Maven**

Add JitPack as a repository:
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Add JBWAPI to your dependencies in `<dependencies></dependencies>`:
```
<dependency>
    <groupId>com.github.JavaBWAPI</groupId>
    <artifactId>JBWAPI</artifactId>
    <version>1.5.1</version>
</dependency>
```

**Gradle**

Add JitPack as a repository:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add JBWAPI as a dependency:
```
dependencies {
    implementation 'com.github.JavaBWAPI:JBWAPI:1.5.1'
}
```

**Jar**

Alternatively add the latest .jar from the [releases](https://github.com/JavaBWAPI/JBWAPI/releases) page to your project.

## Compilation

`mvnw.cmd package`

or if you already have maven installed

`mvn package`

## Example

A simple `Hello World` bot is as easy as

```Java
import bwapi.*;

class HelloBot extends DefaultBWListener {
	private BWClient bwClient;

	@Override
	public void onFrame() {
		Game game = bwClient.getGame();
		game.drawTextScreen(100, 100, "Hello World!");
	}

	public static void main(String[] args) {
		HelloBot bot = new HelloBot();
		bot.bwClient = new BWClient(bot);
		bot.bwClient.startGame();
	}
}
```

## Documentation

The API documentation can be found [here](https://javabwapi.github.io/JBWAPI/).

You can also ask any further questions on the [SSCAIT Discord](https://discord.gg/DqvHsq9)

## Tutorial

If you are a just starting out with bot development, it might be helpful to follow the [tutorial](https://github.com/JavaBWAPI/Java-BWAPI-Tutorial/wiki)!


## Bots

Some bots using [JBWAPI](https://github.com/JavaBWAPI/JBWAPI) (feel free to open a pullrequest to add yours!)

 - https://github.com/dgant/PurpleWave
 - https://github.com/Ravaelles/Atlantis
 - https://github.com/impie66/Kangaroo-Bot

## Linux

If you use Linux you can choose to develop normally and run the `jar` and `starcraft` using [wine](https://www.winehq.org/) 
or try to use `openbw` by following the following [instructions](./build_with_openbw.md)
