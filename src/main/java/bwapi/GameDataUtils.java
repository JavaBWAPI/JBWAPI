/*
MIT License

Copyright (c) 2018 Hannes Bredberg
Modified work Copyright (c) 2018 Jasper

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package bwapi;

/**
 * Static functions for modifying GameData.
 * These functions live outside GameData because GameData is auto-generated.
 */
public class GameDataUtils {

    static final int MAX_COUNT = 19999;
    static final int MAX_STRING_SIZE = 1024;

    static int addString(ClientData.GameData gameData, final String string) {
        int stringCount = gameData.getStringCount();
        if (stringCount >= MAX_COUNT) {
            throw new IllegalStateException("Too many strings!");
        }

        // Truncate string if its size equals or exceeds 1024
        final String stringTruncated = string.length() >= MAX_STRING_SIZE
                ? string.substring(0, MAX_STRING_SIZE - 1)
                : string;

        gameData.setStringCount(stringCount + 1);
        gameData.setStrings(stringCount, stringTruncated);
        return stringCount;
    }

    static ClientData.Shape addShape(ClientData.GameData gameData) {
        int shapeCount = gameData.getShapeCount();
        if (shapeCount >= MAX_COUNT) {
            throw new IllegalStateException("Too many shapes!");
        }
        gameData.setShapeCount(shapeCount + 1);
        return gameData.getShapes(shapeCount);
    }

    static ClientData.Command addCommand(ClientData.GameData gameData) {
        final int commandCount = gameData.getCommandCount();
        if (commandCount >= MAX_COUNT) {
            throw new IllegalStateException("Too many commands!");
        }
        gameData.setCommandCount(commandCount + 1);
        return gameData.getCommands(commandCount);
    }

    static ClientData.UnitCommand addUnitCommand(ClientData.GameData gameData) {
        int unitCommandCount = gameData.getUnitCommandCount();
        if (unitCommandCount >= MAX_COUNT) {
            throw new IllegalStateException("Too many unit commands!");
        }
        gameData.setUnitCommandCount(unitCommandCount + 1);
        return gameData.getUnitCommands(unitCommandCount);
    }
}
