import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class DumpToClient {
    private static final Pattern VAR_DECL = Pattern.compile(
        "^\\s+(\\d+) \\| +((?:struct )?BWAPI[^:]*::)?(\\S[^\\[]+)\\s([\\[0-9\\]]+)?\\s?(\\S+)$");
    private static final Pattern SIZE_DECL = Pattern.compile(
        "^\\s+\\| \\[sizeof=(\\d+), align=\\d+.+");

    public static void main(String[] args) throws IOException {
        Map<String, Struct> structs = new HashMap<>();

        List<String> lines = Files.readAllLines(Paths.get("bwapistructs/dump"));
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("| struct BWAPI")) {
                String structName = line.substring(line.indexOf(':') + 2);
                Struct struct = structs.computeIfAbsent(structName, Struct::new);
                while (++i < lines.size()) {
                    line = lines.get(i);
                    if (line.contains("   class ")) {
                        continue;
                    }
                    Matcher matcher = VAR_DECL.matcher(line);
                    if (!matcher.matches()) {
                        Matcher sizeMatcher = SIZE_DECL.matcher(line);
                        if (sizeMatcher.matches()) {
                            struct.size = Integer.parseInt(sizeMatcher.group(1));
                        }
                        break;
                    }
                    String varName = matcher.group(5);
                    String typeName = matcher.group(3);
                    Variable var;
                    switch (typeName) {
                        case "int":
                            var = new Variable(varName, Type.INT);
                            break;
                        case "_Bool":
                            var = new Variable(varName, Type.BOOLEAN);
                            break;
                        case "char":
                            var = new Variable(varName, Type.CHAR);
                            break;
                        case "unsigned short":
                            var = new Variable(varName, Type.UNSIGNED_SHORT);
                            break;
                        case "unsigned int":
                            var = new Variable(varName, Type.UNSIGNED_INT);
                            break;
                        case "double":
                            var = new Variable(varName, Type.DOUBLE);
                            break;
                        default:
                            if (typeName.endsWith("::Enum")) {
                                var = new Variable(varName, Type.ENUM);
                                var.enumName = typeName.substring(0, typeName.length() - 6);
                            } else {
                                Struct structRef = structs.computeIfAbsent(typeName, Struct::new);
                                var = new Variable(varName, Type.STRUCT);
                                var.structRef = structRef;
                            }
                    }
                    var.offset = Integer.parseInt(matcher.group(1));
                    if (matcher.group(4) != null) {
                        var.arraySizes = Arrays.stream(matcher.group(4).split("]\\[|]|\\["))
                            .filter(s -> !s.isEmpty())
                            .map(Integer::parseInt).collect(Collectors.toList());
                    }
                    struct.variables.add(var);
                }
            }
        }
         // Dynamic regions in the memory mapped file. Hardcoded for BWAPI 4.4
        DynamicRegion[] dynamicReadRegions = {
                new DynamicRegion("client_version", "getGroundHeight"),
                new DynamicRegion("isVisible", "mapTileRegionId"),
                new DynamicRegion("startLocationCount", "stringCount"),
                new DynamicRegion("commandCount", "unitSearchSize"),
        };
        int idx = 0;
        boolean inside = false;
        for (Variable variable : structs.get("GameData").variables) {
            if (idx == dynamicReadRegions.length) continue;
            if (!inside) {
                if (variable.name.equals(dynamicReadRegions[idx].start)) {
                    dynamicReadRegions[idx].startOffset = variable.offset;
                    inside = true;
                    variable.extraOffset = idx;
                }
            } else {
                if (variable.name.equals(dynamicReadRegions[idx].end)) {
                    dynamicReadRegions[idx].endOffset = variable.offset;
                    inside = false;
                    idx++;
                } else {
                    variable.extraOffset = idx;
                }
            }
        }
        int dynamicOffsetSize = Arrays.stream(dynamicReadRegions)
                .mapToInt(r -> r.endOffset - r.startOffset)
                .sum();
        assert(idx == dynamicReadRegions.length); // only bwapi 4.4 supported

        StringWriter sw = new StringWriter();
        try (PrintWriter out = new PrintWriter(sw)) {
            out.print("package bwapi;\n");
            out.print("\n");
            out.print("/** Autogenerated, do not modify manually. */\n");
            out.print("class ClientData {\n");
            out.print("    // Do not modify.\n");
            out.print("    static final int[][] ALL_DYNAMIC_OFFSETS = new int[][]{\n");
            Arrays.stream(dynamicReadRegions).forEach(r ->
                    out.printf("        {%d, %d},\n", r.startOffset, r.endOffset)
            );
            out.print("    };\n");
            out.printf("    static final int DYNAMIC_OFFSET_SIZE = %d;\n", dynamicOffsetSize);
            out.print("    static class Wrapper {\n");
            out.print("         WrappedBuffer buff = null;\n");
            out.print("    }\n");
            out.print("    private final Wrapper mainBuffer = new Wrapper();\n");
            out.print("    // dynamicBuffer can point to the same buffer as main or its own.\n");
            out.print("    private final Wrapper dynamicBuffer = new Wrapper();\n");
            out.print("    private final GameData gameData;\n");
            out.print("    ClientData() {\n");
            out.print("        gameData = new ClientData.GameData(0);\n");
            out.print("    }\n");
            out.print("    GameData gameData() {\n");
            out.print("        return gameData;\n");
            out.print("    }\n");
            out.print("    void setBuffer(WrappedBuffer buffer) {\n");
            out.print("        this.mainBuffer.buff = buffer;\n");
            out.print("        this.dynamicBuffer.buff = buffer;\n");
            out.print("    }\n");
            out.print("    void setDynamicBuffer(WrappedBuffer dynamicBuffer) {\n");
            out.print("        this.dynamicBuffer.buff = dynamicBuffer;\n");
            out.print("    }\n");
            // Skip getExtraOffset0()
            for (int offsetIdx = 1; offsetIdx < dynamicReadRegions.length; offsetIdx++) {
                out.printf("    int getExtraOffset%d() {\n", offsetIdx);
                out.printf("        return dynamicBuffer.buff.getSize() == DYNAMIC_OFFSET_SIZE ? %d : 0;\n", dynamicReadRegions[offsetIdx].startOffset);
                out.print("    }\n");
            }

            structs.values().forEach(s -> {
                out.printf("    class %s {\n", s.name);
                out.printf("        static final int SIZE = %d;\n", s.size);
                out.print("        private final int myOffset;\n");
                if (!s.name.equals("GameData")) {
                    out.print("        private final Wrapper wrapper;\n");
                    out.printf("        %s(int myOffset, Wrapper wrapper) {\n", s.name);
                    out.print("            this.wrapper = wrapper;\n");
                } else {
                    out.printf("        %s(int myOffset) {\n", s.name);
                }
                out.print("            this.myOffset = myOffset;\n");
                out.print("        }\n");
                s.variables.forEach(v -> {
                    out.print("        ");
                    switch (v.type) {
                        case INT:
                            out.print("int");
                            break;
                        case CHAR:
                            if (v.arraySizes.isEmpty()) {
                                out.print("char");
                            } else {
                                out.print("String");
                            }
                            break;
                        case ENUM:
                            out.print(v.enumName);
                            break;
                        case DOUBLE:
                            out.print("double");
                            break;
                        case STRUCT:
                            out.print(v.structRef.name);
                            break;
                        case BOOLEAN:
                            out.print("boolean");
                            break;
                        case UNSIGNED_INT:
                            out.print("int");
                            break;
                        case UNSIGNED_SHORT:
                            out.print("short");
                            break;
                    }
                    String camelCaseName =
                        v.name.substring(0, 1).toUpperCase() + v.name.substring(1);
                    if (v.name.startsWith("is") || v.name.startsWith("get")) {
                        out.printf(" %s(", v.name);
                    } else {
                        out.printf(" get%s(", camelCaseName);
                    }
                    List<String> params = new ArrayList<>();
                    String offsetString;
                    int arrayIndices = v.arraySizes.size();
                    if (v.type == Type.CHAR) {
                        arrayIndices--;
                    }
                    if (arrayIndices > 0) {
                        List<String> index = new ArrayList<>();
                        int offset = 1;
                        for (int i = v.arraySizes.size() - 1; i >= 0; i--) {
                            int size = 1;
                            switch (v.type) {
                                case UNSIGNED_SHORT:
                                    size *= 2;
                                    break;
                                case UNSIGNED_INT:
                                case INT:
                                case ENUM:
                                    size *= 4;
                                    break;
                                case STRUCT:
                                    size *= v.structRef.size;
                                    break;
                            }
                            if (i < arrayIndices) {
                                params.add("int " + (char) ('i' + arrayIndices - 1 - i));
                                index.add(
                                    size + " * " + offset + " * " + (char) ('i' + i));
                            }
                            offset *= v.arraySizes.get(i);
                        }
                        offsetString = "myOffset + " + v.offset + " + " + String.join(" + ", index);
                    } else {
                        offsetString = "myOffset + " + v.offset;
                    }
                    if (v.extraOffset > 0) {
                        offsetString += " - getExtraOffset" + v.extraOffset + "()";
                    }
                    String paramString = String.join(", ", params);
                    out.printf("%s) {\n", paramString);
                    out.printf("            int offset = %s;\n", offsetString);
                    out.print("            return ");
                    String bufferName = s.name.equals("GameData")
                            ? (v.extraOffset >= 0 ? "dynamicBuffer" : "mainBuffer")
                            : "wrapper";
                    switch (v.type) {
                        case UNSIGNED_INT:
                        case INT:
                            out.printf("%s.buff.getInt(offset)", bufferName);
                            break;
                        case CHAR:
                            out.printf("%s.buff.getString(offset, %d)",
                                bufferName, v.arraySizes.get(v.arraySizes.size() - 1));
                            break;
                        case ENUM:
                            out.printf(v.enumName + ".idToEnum[%s.buff.getInt(offset)]", bufferName);
                            break;
                        case DOUBLE:
                            out.printf("%s.buff.getDouble(offset)", bufferName);
                            break;
                        case STRUCT: // TODO(me): what to do here?
                            if (v.extraOffset >= 0) System.out.println(v);
                            out.printf("new %s(offset, %s)", v.structRef.name, bufferName);
                            break;
                        case BOOLEAN:
                            out.printf("%s.buff.getByte(offset) != 0", bufferName);
                            break;
                        case UNSIGNED_SHORT:
                            out.printf("%s.buff.getShort(offset)", bufferName);
                            break;
                    }
                    out.print(";\n");
                    out.print("        }\n");
                    // buffer.put should never be to a dynamicBuffer
                    if (v.type != Type.STRUCT && (v.type != Type.CHAR || !v.arraySizes.isEmpty())) {
                        if (paramString.isEmpty()) {
                            out.printf("        void set%s(", camelCaseName);
                        } else {
                            out.printf("        void set%s(%s, ", camelCaseName, paramString);
                        }
                        switch (v.type) {
                            case INT:
                            case UNSIGNED_INT:
                                out.print("int value) {\n");
                                out.printf("            mainBuffer.buff.putInt(%s, value);\n",
                                    offsetString);
                                break;
                            case ENUM:
                                out.printf("%s value) {\n", v.enumName);
                                out.printf("            mainBuffer.buff.putInt(%s, value.id);\n",
                                    offsetString);
                                break;
                            case UNSIGNED_SHORT:
                                out.print("short value) {\n");
                                out.printf("            mainBuffer.buff.putShort(%s, value);\n",
                                    offsetString);
                                break;
                            case DOUBLE:
                                out.print("double value) {\n");
                                out.printf("            mainBuffer.buff.putDouble(%s, value);\n",
                                    offsetString);
                                break;
                            case BOOLEAN:
                                out.print("boolean value) {\n");
                                out.printf(
                                    "            mainBuffer.buff.putByte(%s, (byte) (value ? 1 : 0));\n",
                                    offsetString);
                                break;
                            case CHAR:
                                out.print("String value) {\n");
                                int maxLength = v.arraySizes.get(v.arraySizes.size() - 1);
                                out.printf(
                                    "            mainBuffer.buff.putString(%s, %d, value);\n",
                                    offsetString,
                                    maxLength);
                                break;
                        }
                        out.print("        }\n");
                    }
                });
                out.print("    }\n");
            });
            out.print("}\n");
        }
        Files.write(Paths.get("src/main/java/bwapi/ClientData.java"),
            Collections.singleton(sw.toString()),
            StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    enum Type {
        STRUCT,
        BOOLEAN,
        INT,
        CHAR,
        UNSIGNED_SHORT,
        UNSIGNED_INT,
        DOUBLE,
        ENUM
    }

    static class Variable {
        private final String name;
        private final Type type;
        int offset;
        int extraOffset = -1;
        private Struct structRef;
        private String enumName;
        private List<Integer> arraySizes = Collections.emptyList();

        Variable(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String toString() {
            return "Variable{" +
                    "name='" + name + '\'' +
                    ", type=" + type +
                    ", offset=" + offset +
                    ", extraOffset=" + extraOffset +
                    ", structRef=" + structRef +
                    ", enumName='" + enumName + '\'' +
                    ", arraySizes=" + arraySizes +
                    '}';
        }
    }

    static class Struct {
        final String name;
        int size;
        List<Variable> variables = new ArrayList<>();

        Struct(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Struct{" +
                    "name='" + name + '\'' +
                    ", size=" + size +
                    ", variables=" + variables +
                    '}';
        }
    }

    static class DynamicRegion {
        String start;
        int startOffset = -1;
        String end;
        int endOffset = -1;

        DynamicRegion(String start, String end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "DynamicRegion{" +
                    "start='" + start + '\'' +
                    ", startOffset=" + startOffset +
                    ", end='" + end + '\'' +
                    ", endOffset=" + endOffset +
                    '}';
        }
    }
}
