import com.sun.org.apache.xpath.internal.operations.Variable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DumpToClient {

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

    StringWriter sw = new StringWriter();
    try (PrintWriter out = new PrintWriter(sw)) {
      out.println("package bwapi;");
      out.println("import java.nio.ByteBuffer;");
      out.println("final class ClientData {");
      out.println("  private final ByteBuffer sharedMemory;");
      out.println("  ClientData(ByteBuffer sharedMemory) {");
      out.println("    this.sharedMemory = sharedMemory;");
      out.println("  }");
      structs.values().forEach(s -> {
        out.printf("  class %s {\n", s.name);
        out.printf("    static final int SIZE = %d;\n", s.size);
        out.println("    private int myOffset;");
        out.printf("    public %s(int myOffset) {\n", s.name);
        out.println("      this.myOffset = myOffset;");
        out.println("    }");
        s.variables.forEach(v -> {
          out.print("    ");
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
          String camelCaseName = v.name.substring(0, 1).toUpperCase() + v.name.substring(1);
          if (v.name.startsWith("is") || v.name.startsWith("get")) {
            out.printf(" %s(", v.name);
          } else {
            out.printf(" get%s(", camelCaseName);
          }
          List<String> params = new ArrayList<>();
          String offsetString;
          int arrayIndices = v.arraySizes.size();
          if (v.type == Type.CHAR) arrayIndices--;
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
          out.printf("      int offset = %s;\n", offsetString);
          out.print("      return ");
          switch (v.type) {
            case UNSIGNED_INT:
            case INT:
              out.print("sharedMemory.getInt(offset)");
              break;
            case CHAR:
              out.printf("Buffers.toString(sharedMemory, offset, %d)", v.arraySizes.size());
              break;
            case ENUM:
              out.print(v.enumName + ".idToEnum[sharedMemory.getInt(offset)]");
              break;
            case DOUBLE:
              out.print("sharedMemory.getDouble(offset)");
              break;
            case STRUCT:
              out.printf("new %s(offset)", v.structRef.name);
              break;
            case BOOLEAN:
              out.print("sharedMemory.get(offset) != 0");
              break;
            case UNSIGNED_SHORT:
              out.print("sharedMemory.getShort(offset)");
              break;
          }
          out.println(";");
          out.println("    }");

          if (v.type != Type.STRUCT && (v.type != Type.CHAR || !v.arraySizes.isEmpty())) {
            if (paramString.isEmpty()) {
              out.printf("    void set%s(", camelCaseName);
            } else {
              out.printf("    void set%s(%s, ", camelCaseName, paramString);
            }
            switch (v.type) {
              case INT:
              case UNSIGNED_INT:
                out.println("int value) {");
                out.printf("      sharedMemory.putInt(%s, value);\n", offsetString);
                break;
              case ENUM:
                out.printf("%s value) {\n", v.enumName);
                out.printf("      sharedMemory.putInt(%s, value.id);\n", offsetString);
                break;
              case UNSIGNED_SHORT:
                out.print("short value) {");
                out.printf("      sharedMemory.putShort(%s, value);\n", offsetString);
                break;
              case DOUBLE:
                out.print("double value) {");
                out.printf("      sharedMemory.putDouble(%s, value);\n", offsetString);
                break;
              case BOOLEAN:
                out.print("boolean value) {");
                out.printf("      sharedMemory.put(%s, (byte) (value ? 1 : 0));\n", offsetString);
                break;
              case CHAR:
                out.println("String value) {");
                int maxLength = v.arraySizes.get(v.arraySizes.size() - 1);
                out.printf("      Buffers.fromString(sharedMemory, %s, %d, value);\n", offsetString, maxLength);
                break;
            }
            out.println("    }");
          }
        });
        out.println("  }");
      });
      out.println("}");
    }
    Files.write(Paths.get("src/main/java/bwapi/ClientData.java"),
        Collections.singleton(sw.toString()),
        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
  }

  public enum Type {
    STRUCT,
    BOOLEAN,
    INT,
    CHAR,
    UNSIGNED_SHORT,
    UNSIGNED_INT,
    DOUBLE,
    ENUM
  }

  public static class Variable {

    private final String name;
    private final Type type;
    int offset;
    private Struct structRef;
    private String enumName;
    private List<Integer> arraySizes = Collections.emptyList();

    public Variable(String name, Type type) {
      this.name = name;
      this.type = type;
    }
  }

  public static class Struct {

    final String name;
    int size;
    List<Variable> variables = new ArrayList<>();

    public Struct(String name) {
      this.name = name;
    }
  }
}
