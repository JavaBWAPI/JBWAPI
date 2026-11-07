import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                System.out.println("Parsing: " + structName);
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

        Struct gameData = structs.get("GameData");
        Map<Struct, String> asyncSliceStructs = new LinkedHashMap<>();
        for (Variable v : gameData.variables) {
            if (v.type == Type.STRUCT) {
                if (v.offset >= 4823260 && v.offset < 5085404) {
                    asyncSliceStructs.put(v.structRef, "packedBuffer.slice2");
                } else if (v.offset >= 10586480 && v.offset < 10962632) {
                    asyncSliceStructs.put(v.structRef, "packedBuffer.slice3");
                }
            }
        }
        Set<String> protectedOffsetStructs = asyncSliceStructs.keySet().stream()
            .map(s -> s.name)
            .collect(Collectors.toSet());
        protectedOffsetStructs.add("GameData");

        StringWriter sw = new StringWriter();
        try (PrintWriter out = new PrintWriter(sw)) {
            out.println("package bwapi;");
            out.println("");
            out.println("import com.sun.jna.Pointer;");
            out.println("");
            out.println("class ClientData {");
            out.println("    WrappedBuffer buffer;");
            out.println("    GameData gameData;");
            out.println("    ClientData() {");
            out.println("        gameData = createGameData(0);");
            out.println("    }");
            out.println("    GameData createGameData(int offset) {");
            out.println("        return new ClientData.GameData(offset);");
            out.println("    }");
            out.println("    GameData gameData() {");
            out.println("        return gameData;");
            out.println("    }");
            out.println("    void setBuffer(WrappedBuffer buffer) {");
            out.println("        this.buffer = buffer;");
            out.println("    }");
            out.println("    void setPointer(Pointer pointer) {");
            out.println("        setBuffer(new WrappedBuffer(pointer, GameData.SIZE));");
            out.println("    }");
            out.println("    WrappedBuffer getStaticBuffer() {");
            out.println("        return buffer;");
            out.println("    }");

            structs.values().forEach(s -> {
                String bufName = s.name.equals("RegionData") ? "getStaticBuffer()" : "buffer";
                out.printf("    class %s {\n", s.name);
                out.printf("        static final int SIZE = %d;\n", s.size);
                String access = protectedOffsetStructs.contains(s.name) ? "protected" : "private";
                out.printf("        %s int myOffset;\n", access);
                out.printf("        %s(int myOffset) {\n", s.name);
                out.println("          this.myOffset = myOffset;");
                out.println("        }");
                s.variables.forEach(v -> generateVariable(out, v, bufName, false, false));
                out.println("    }");
            });
            out.println("}");
        }
        String clientDataText = sw.toString().replace("\r\n", "\n").replace("\n", "\r\n");
        Files.write(Paths.get("src/main/java/bwapi/ClientData.java"),
            clientDataText.getBytes(StandardCharsets.UTF_8),
            StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

        StringWriter swAsync = new StringWriter();
        try (PrintWriter out = new PrintWriter(swAsync)) {
            out.println("package bwapi;");
            out.println("");
            out.println("/**");
            out.println(" * Asynchronous client data that routes static information to the main/static shared buffer");
            out.println(" * and dynamic information through pre-shifted slice views.");
            out.println(" * Automatically generated by DumpToClient.");
            out.println(" */");
            out.println("class AsyncClientData extends ClientData {");
            out.println("    private WrappedBuffer staticBuffer;");
            out.println("    private PackedWrappedBuffer staticPackedView;");
            out.println("    PackedWrappedBuffer packedBuffer;");
            out.println("");
            out.println("    AsyncClientData() {");
            out.println("        super();");
            out.println("    }");
            out.println("");
            out.println("    AsyncClientData(WrappedBuffer staticBuffer) {");
            out.println("        super();");
            out.println("        setStaticBuffer(staticBuffer);");
            out.println("    }");
            out.println("");
            out.println("    void setStaticBuffer(WrappedBuffer staticBuffer) {");
            out.println("        this.staticBuffer = staticBuffer;");
            out.println("        this.staticPackedView = staticBuffer != null ? new PackedWrappedBuffer(staticBuffer) : null;");
            out.println("    }");
            out.println("");
            out.println("    @Override");
            out.println("    void setBuffer(WrappedBuffer buffer) {");
            out.println("        super.setBuffer(buffer);");
            out.println("        if (buffer instanceof PackedWrappedBuffer) {");
            out.println("            this.packedBuffer = (PackedWrappedBuffer) buffer;");
            out.println("        } else if (buffer != null && buffer == this.staticBuffer) {");
            out.println("            this.packedBuffer = this.staticPackedView;");
            out.println("        } else {");
            out.println("            this.packedBuffer = buffer != null ? new PackedWrappedBuffer(buffer) : null;");
            out.println("        }");
            out.println("    }");
            out.println("");
            out.println("    @Override");
            out.println("    WrappedBuffer getStaticBuffer() {");
            out.println("        return staticBuffer != null ? staticBuffer : buffer;");
            out.println("    }");
            out.println("");
            out.println("    @Override");
            out.println("    GameData createGameData(int offset) {");
            out.println("        return new AsyncGameData(offset);");
            out.println("    }");
            out.println("");

            for (Map.Entry<Struct, String> entry : asyncSliceStructs.entrySet()) {
                Struct s = entry.getKey();
                String sliceBuf = entry.getValue();
                out.printf("    class Async%s extends %s {\n", s.name, s.name);
                out.printf("        Async%s(int myOffset) {\n", s.name);
                out.println("            super(myOffset);");
                out.println("        }");
                s.variables.forEach(v -> generateVariable(out, v, sliceBuf, true, false));
                out.println("    }");
                out.println("");
            }

            out.println("    class AsyncGameData extends GameData {");
            out.println("        AsyncGameData(int myOffset) {");
            out.println("            super(myOffset);");
            out.println("        }");
            out.println("");

            for (Variable v : gameData.variables) {
                boolean isStaticTile = v.offset >= 3447004 && v.offset < 4823260;
                boolean isDynamicTile = v.offset >= 4823260 && v.offset < 5085404;
                boolean isStaticRegion = v.offset >= 5085404 && v.offset < 10586480;
                boolean isDynamicGame = v.offset >= 10586480 && v.offset < 10962632;

                if (isStaticTile || isStaticRegion) {
                    generateVariable(out, v, "getStaticBuffer()", true, false);
                } else if (isDynamicTile) {
                    generateVariable(out, v, "packedBuffer.slice2", true, true);
                } else if (isDynamicGame) {
                    generateVariable(out, v, "packedBuffer.slice3", true, true);
                }
            }
            out.println("    }");
            out.println("}");
        }
        String asyncClientDataText = swAsync.toString().replace("\r\n", "\n");
        Files.write(Paths.get("src/main/java/bwapi/AsyncClientData.java"),
            asyncClientDataText.getBytes(StandardCharsets.UTF_8),
            StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    private static void generateVariable(PrintWriter out, Variable v, String bufName, boolean isOverride, boolean useAsyncStruct) {
        if (isOverride) {
            out.println("        @Override");
        }
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
        String paramString = String.join(", ", params);
        out.printf("%s) {\n", paramString);
        out.printf("            int offset = %s;\n", offsetString);
        out.print("            return ");
        switch (v.type) {
            case UNSIGNED_INT:
            case INT:
                out.printf("%s.getInt(offset)", bufName);
                break;
            case CHAR:
                out.printf("%s.getString(offset, %d)",
                    bufName, v.arraySizes.get(v.arraySizes.size() - 1));
                break;
            case ENUM:
                out.printf("%s.idToEnum[%s.getInt(offset)]", v.enumName, bufName);
                break;
            case DOUBLE:
                out.printf("%s.getDouble(offset)", bufName);
                break;
            case STRUCT:
                String structCtor = useAsyncStruct ? "Async" + v.structRef.name : v.structRef.name;
                out.printf("new %s(offset)", structCtor);
                break;
            case BOOLEAN:
                out.printf("%s.getByte(offset) != 0", bufName);
                break;
            case UNSIGNED_SHORT:
                out.printf("%s.getShort(offset)", bufName);
                break;
        }
        out.println(";");
        out.println("        }");

        if (v.type != Type.STRUCT && (v.type != Type.CHAR || !v.arraySizes.isEmpty())) {
            if (isOverride) {
                out.println("        @Override");
            }
            if (paramString.isEmpty()) {
                out.printf("        void set%s(", camelCaseName);
            } else {
                out.printf("        void set%s(%s, ", camelCaseName, paramString);
            }
            switch (v.type) {
                case INT:
                case UNSIGNED_INT:
                    out.println("int value) {");
                    out.printf("            %s.putInt(%s, value);\n",
                        bufName, offsetString);
                    break;
                case ENUM:
                    out.printf("%s value) {\n", v.enumName);
                    out.printf("            %s.putInt(%s, value.id);\n",
                        bufName, offsetString);
                    break;
                case UNSIGNED_SHORT:
                    out.println("short value) {");
                    out.printf("            %s.putShort(%s, value);\n",
                        bufName, offsetString);
                    break;
                case DOUBLE:
                    out.println("double value) {");
                    out.printf("            %s.putDouble(%s, value);\n",
                        bufName, offsetString);
                    break;
                case BOOLEAN:
                    out.println("boolean value) {");
                    out.printf(
                        "            %s.putByte(%s, (byte) (value ? 1 : 0));\n",
                        bufName, offsetString);
                    break;
                case CHAR:
                    out.println("String value) {");
                    int maxLength = v.arraySizes.get(v.arraySizes.size() - 1);
                    out.printf(
                        "            %s.putString(%s, %d, value);\n",
                        bufName, offsetString,
                        maxLength);
                    break;
            }
            out.println("        }");
        }
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
        private Struct structRef;
        private String enumName;
        private List<Integer> arraySizes = Collections.emptyList();

        Variable(String name, Type type) {
            this.name = name;
            this.type = type;
        }
    }

    static class Struct {

        final String name;
        int size;
        List<Variable> variables = new ArrayList<>();

        Struct(String name) {
            this.name = name;
        }
    }
}
