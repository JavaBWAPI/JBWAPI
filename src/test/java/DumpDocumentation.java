import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Not made to be maintained...
 */
public class DumpDocumentation {

    public static void main(String[] args) throws IOException {
        // Use default installation location or give the path of the BWAPI dir as the first argument
        String bwapi_dir = "/Users/" + System.getProperty("user.name") + "/Documents/BWAPI/";

        if (args.length > 1) {
            bwapi_dir = args[1];
        }
        File[] headerFiles = new File(bwapi_dir + "/include/BWAPI").listFiles((f, n) -> n.endsWith(".h"));
        File[] classFiles = new File("src/main/java/bwapi").listFiles((f, n) -> n.endsWith(".java"));

        Objects.requireNonNull(headerFiles);
        Objects.requireNonNull(classFiles);

        Map<String, File> headerMap = Arrays.stream(headerFiles)
                .collect(Collectors.toMap(f -> f.getName().substring(0, f.getName().lastIndexOf('.')) , f -> f));
        Map<String, File> classMap = Arrays.stream(classFiles)
                .collect(Collectors.toMap(f -> f.getName().substring(0, f.getName().lastIndexOf('.')) , f -> f));

        //System.out.println(headerMap);
        //System.out.println(classMap);

        Set<String> nameIntersection = headerMap.keySet().stream()
                .filter(classMap.keySet()::contains)
                .collect(Collectors.toSet());

        //Exceptions
        nameIntersection.remove("Position"); //JBWAPI uses Point class
        nameIntersection.remove("Client"); //this is different for BWAPI and JBWAPI
        //System.out.println(nameIntersection);

        for (String name : nameIntersection) {
            // Exceptions Position & Point
            addDocumentation(name, headerMap.get(name), classMap.get(name));
        }
    }

    private static void addDocumentation(String name, File headerFile, File classFile) throws IOException {
        System.out.println("Extracting documentation for " + name);

        List<String> classLines = new ArrayList<>(Files.readAllLines(classFile.toPath()));

        Set<String> excluded = new HashSet<>(Arrays.asList("equals(", "hashCode(", "compareTo("));
        Set<String> publicMethods = classLines.stream()
                .filter(l -> l.trim().startsWith("public") && l.trim().endsWith("{"))
                .map(l -> {
                    if (l.contains("class")) { //also want class documentation if available
                        return "class " + l.substring(l.indexOf("class")).split(" ")[1];
                    } else if (l.contains("enum")) { //also want enum documentation if available
                        return "namespace " + l.substring(l.indexOf("enum")).split(" ")[1];
                    } else {
                        String[] words = l.substring(0, l.indexOf('(') + 1).split(" ");
                        return words[words.length - 1];
                    }
                })
                .filter( n -> !excluded.contains(n))
                .collect(Collectors.toSet());
        System.out.println("Public Methods: " + publicMethods);

        Map<String, List<String>> documentation = new HashMap<>();

        String[] headerLines = Files.readAllLines(headerFile.toPath()).stream()
                .map(String::trim)
                .toArray(String[]::new);

        for (int i=0; i < headerLines.length; i++) {
            String line = headerLines[i];
            final int index = i;
            if (!line.startsWith("///")) { //exclude comments
                Optional<String> method = publicMethods.stream()
                        .filter(word -> line.contains(word)
                                && (headerLines[index-1].startsWith("///") || headerLines[index-1].startsWith("#endif")))
                        .findAny();
                if (method.isPresent()) {
                    List<String> docs = extractCPPDocumentation(i, headerLines);
                    documentation.put(method.get(), toJavaDoc(docs));
                    publicMethods.remove( method.get());
                }
            }

        }

        List<String> finalCode = classLines;
        for (String key : documentation.keySet()) {
            final String repkey = key.replace("namespace", "enum");
            String line = finalCode.stream()
                    .filter(l -> l.trim().startsWith("public") && l.trim().endsWith("{") && l.contains(repkey))
                    .max(Comparator.comparingInt(q -> q.split(",").length - (q.contains("()") ? 1 : 0))).get();
            int index = finalCode.indexOf(line);
            String whitespace = line.substring(0, line.indexOf("public"));
            List<String> newList = new ArrayList<>(finalCode.subList(0, index));
            newList.addAll(documentation.get(key).stream().map(t -> whitespace + t).collect(Collectors.toList()));
            newList.addAll(new ArrayList<>(finalCode.subList(index, finalCode.size())));
            finalCode = newList;
        }
        Path filePath = classFile.toPath();
        try {
            classFile.delete();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Files.write(filePath, finalCode);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Not found: " + publicMethods + "\n");
    }

    private static List<String> extractCPPDocumentation(int index, String[] headerLines) {
        List<String> docs = new ArrayList<>();
        int deprecated = 0;
        while (index >= 0) {
            index -= 1;
            String line = headerLines[index];
            if (line.startsWith("///")) {
                docs.add(line);
            } else if (line.startsWith("#endif")){ // for Deprecated methods
                deprecated += 1;
            }
            else if (deprecated > 0) {
                if (line.startsWith("#ifndef")) {
                    deprecated -= 1;
                }
            }
            else {
                break;
            }
        }
        Collections.reverse(docs);
        return docs;
    }

    private static List<String> toJavaDoc(List<String> docs) {
        List<String> javadoc = new ArrayList<>();
        javadoc.add("/**");
        boolean isCode = false;
        while (!docs.isEmpty()) {
            String line = docs.get(0);
            if (line.contains("@code")) {  // skip code
                isCode = true;
            }
            else if (line.contains("@endcode")) {
                isCode = false;
                docs.remove(0);
                continue;
            }
            if (!isCode && toSkip.stream().noneMatch(line::contains)) {
                if (line.contains("@returns")) {
                    if (line.trim().equals("/// @returns")) {
                        docs.remove(0);
                        line = docs.get(0);
                        line = line.replace("/// ", " * @return");
                    }
                    else {
                        line = line.replace("@returns", "@return");
                    }

                }
                if (line.contains("<param name=\"")) {
                    String name = line.split("<param name=\"")[1].split("\">")[0];
                    docs.remove(0);
                    String localLine = docs.get(0);
                    line = " * @param " + name;
                    while(!localLine.contains("</param")) {
                        line += " " + localLine.substring(3).trim();
                        docs.remove(0);
                        localLine = docs.get(0);
                    }

                }
                line = removeAll(line, toRemove);
                line = replaceAll(line, toReplace);
                line = replaceAll(line, atEnd);
                javadoc.add(line);
            }
            docs.remove(0);
        }
        javadoc.add(" */");
        return javadoc;
    }

    private static String removeAll(String string, List<String> substring) {
        String s = string;
        for (String ss : substring) {
            s = s.replace(ss, "");
        }
        return s;
    }

    private static String replaceAll(String string, Map<String, String> substring) {
        String s = string;
        for (String key : substring.keySet()) {
            s = s.replace(key, substring.get(key));
        }
        return s;
    }

    private static List<String> toSkip = Arrays.asList(
            "@ingroup",
            "Example usage"
    );

    private static List<String> toRemove = Arrays.asList(
            "<summary>",
            "</summary>",
            "BWAPI::",
            "Interface",
            "::Enum",
            ".c_str()",
            "::c_str()",
            "ExampleAIModule::");

    private static Map<String, String> toReplace = new HashMap<String, String>(){{
        put("std::string", "String");
        put("Broodwar->", "game.");
        put("Broodwar << ", "game.sendText(");
        put(" << std::endl", " + \"\\n\")");
        put("nullptr", "null");
        put("Unitset", "List<Unit>");
        put("UnitSet", "List<Unit>");
        put("Playerset", "List<Player>");
        put("Forceset", "List<Force>");
        put("Bulletset", "List<Bullet>");
        put("Regionset", "List<Region>");
        put("bool ", "boolean ");
        put("s::", ".");
        put("Pointer to ", "");
        put("interface object", "object");
    }};

    private static Map<String, String> atEnd = new HashMap<String, String>(){{
        put("std.printf", "String");
        put("///", " *");
        put("::", "#");
        put("Text.Size", "TextSize");
        put(".Enum", "");
    }};

}
