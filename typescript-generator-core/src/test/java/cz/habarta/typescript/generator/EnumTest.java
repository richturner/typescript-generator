package cz.habarta.typescript.generator;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import cz.habarta.typescript.generator.ext.ClassEnumExtension;
import java.util.*;
import static org.junit.Assert.*;

import cz.habarta.typescript.generator.ext.EnumWithInterfacesExtension;
import org.junit.Test;

public class EnumTest {

    @Test
    public void testEnumAsUnion() {
        final Settings settings = TestUtils.settings();
//        settings.mapEnum = EnumMapping.asUnion;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(AClass.class));
        final String expected = (
                "\n" +
                "interface AClass {\n" +
                "    direction: Direction;\n" +
                "}\n" +
                "\n" +
                "type Direction = 'North' | 'East' | 'South' | 'West';\n"
                ).replace("'", "\"");
        assertEquals(expected, output);
    }

    @Test
    public void testSingleEnum() {
        final Settings settings = TestUtils.settings();
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(Direction.class));
        final String expected = (
                "\n" +
                "type Direction = 'North' | 'East' | 'South' | 'West';\n"
                ).replace("'", "\"");
        assertEquals(expected, output);
    }

    @Test
    public void testEnumAsInlineUnion() {
        final Settings settings = TestUtils.settings();
        settings.quotes = "'";
        settings.mapEnum = EnumMapping.asInlineUnion;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(AClass.class));
        final String expected =
                "\n" +
                "interface AClass {\n" +
                "    direction: 'North' | 'East' | 'South' | 'West';\n" +
                "}\n";
        assertEquals(expected, output);
    }

    @Test
    public void testEnumAsNumberBasedEnum() {
        final Settings settings = TestUtils.settings();
        settings.mapEnum = EnumMapping.asNumberBasedEnum;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(AClass.class));
        final String expected = (
                "\n" +
                "interface AClass {\n" +
                "    direction: Direction;\n" +
                "}\n" +
                "\n" +
                "declare const enum Direction {\n" +
                "    North,\n" +
                "    East,\n" +
                "    South,\n" +
                "    West,\n" +
                "}\n"
                ).replace("'", "\"");
        assertEquals(expected, output);
    }

    @Test
    public void testEnumAsEnum() {
        final Settings settings = TestUtils.settings();
        settings.mapEnum = EnumMapping.asEnum;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(AClass.class));
        final String expected = (
                "interface AClass {\n" +
                "    direction: Direction;\n" +
                "}\n" +
                "\n" +
                "declare const enum Direction {\n" +
                "    North = 'North',\n" +
                "    East = 'East',\n" +
                "    South = 'South',\n" +
                "    West = 'West',\n" +
                "}"
                ).replace("'", "\"");
        assertEquals(expected.trim(), output.trim());
    }

    @Test
    public void testEnumsWithClassEnumPattern() {
        final Settings settings = TestUtils.settings();
        settings.mapEnum = EnumMapping.asEnum;
        settings.jsonLibrary = JsonLibrary.jackson2;
        final ClassEnumExtension classEnumExtension = new ClassEnumExtension();
        classEnumExtension.setConfiguration(Collections.singletonMap("classEnumPattern", "Enum"));
        settings.extensions.add(classEnumExtension);
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(DummyEnum.class, DummyClassEnum.class));
        final String expected = (
                "\ndeclare const enum DummyClassEnum {\n" +
                        "    ATYPE = 'ATYPE',\n" +
                        "    BTYPE = 'BTYPE',\n" +
                        "    CTYPE = 'CTYPE',\n" +
                        "}\n" +
                        "\ndeclare const enum DummyEnum {\n" +
                        "    Red = 'Red',\n" +
                        "    Green = 'Green',\n" +
                        "    Blue = 'Blue',\n" +
                        "}\n"
                ).replace("'", "\"");
        assertEquals(expected.trim(), output.trim());
    }

    @Test
    public void testEnumWithJsonPropertyAnnotations() {
        final Settings settings = TestUtils.settings();
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(SideWithJsonPropertyAnnotations.class));
        final String expected = (
                "\n" +
                "type SideWithJsonPropertyAnnotations = 'left-side' | 'right-side';\n"
                ).replace("'", "\"");
        assertEquals(expected, output);
    }

    @Test
    public void testEnumWithJsonValueMethodAnnotation() {
        final Settings settings = TestUtils.settings();
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(SideWithJsonValueMethodAnnotation.class));
        final String expected = (
                "\n" +
                "type SideWithJsonValueMethodAnnotation = 'left-side' | 'right-side';\n"
                ).replace("'", "\"");
        assertEquals(expected, output);
    }

    @Test
    public void testEnumWithJsonValueFieldAnnotation() {
        final Settings settings = TestUtils.settings();
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(SideWithJsonValueFieldAnnotation.class));
        final String expected = (
                "\n" +
                "type SideWithJsonValueFieldAnnotation = 'left-side' | 'right-side';\n"
                ).replace("'", "\"");
        assertEquals(expected, output);
    }

    @Test
    public void testEnumUsingToString() {
        final Settings settings = TestUtils.settings();
        settings.jackson2Configuration = new Jackson2ConfigurationResolved();
        settings.jackson2Configuration.enumsUsingToString = true;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(SideUsingToString.class));
        final String expected = (
                "\n" +
                "type SideUsingToString = 'toString:left-side' | 'toString:right-side';\n"
                ).replace("'", "\"");
        assertEquals(expected, output);
    }

    @Test
    public void testEmptyEnum() {
        final Settings settings = TestUtils.settings();
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(EmptyEnum.class));
        final String expected = (
                "\n" +
                "type EmptyEnum = never;\n"
                ).replace("'", "\"");
        assertEquals(expected, output);
    }

    @Test
    public void testExcludeObjectEnum() {
        final Settings settings = TestUtils.settings();
        settings.setExcludeFilter(Arrays.asList(StatusType.class.getName()), Arrays.<String>asList());
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(ClassWithObjectEnum.class, StatusType.class));
        assertTrue(!output.contains("StatusType"));
    }

    @Test
    public void testObjectEnum() {
        final Settings settings = TestUtils.settings();
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(StatusType.class));
        final String expected = "" +
                "interface StatusType {\n" +
                "    code: number;\n" +
                "    label: string;\n" +
                "}";
        assertEquals(expected.trim(), output.trim());
    }

    @Test
    public void testEnumWithInterface() {
        Input input = Input.from(ABInterfaceUsage.class, AInterfaceImpl.class, BInterfaceEnum.class, AInterfaceEnum.class);
        Settings settings = TestUtils.settings();
        settings.sortDeclarations = true;
        settings.sortTypeDeclarations = true;
        settings.optionalProperties = OptionalProperties.all;
        settings.outputKind = TypeScriptOutputKind.module;
        settings.outputFileType = TypeScriptFileType.implementationFile;
        EnumWithInterfacesExtension enumExtension = new EnumWithInterfacesExtension();
        settings.extensions.add(enumExtension);
        String output = new TypeScriptGenerator(settings).generateTypeScript(input);
        final String expectedWithExtension = "" +
                "export interface ABInterfaceUsage {\n" +
                "    a?: AInterface;\n" +
                "    b?: BInterface;\n" +
                "}\n" +
                "\n" +
                "export interface AInterface {\n" +
                "    description?: string;\n" +
                "}\n" +
                "\n" +
                "export interface AInterfaceImpl extends AInterface {\n" +
                "}\n" +
                "\n" +
                "export interface BInterface {\n" +
                "    aarRay?: AInterface[];\n" +
                "    info?: string;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "// Added by 'EnumWithInterfacesExtension' extension\n" +
                "\n" +
                "export class AInterfaceEnum implements AInterface {\n" +
                "    public static readonly A_1 = new AInterfaceEnum(\"A_1\", \"I am AInterfaceEnum 1\");\n" +
                "    public static readonly A_2 = new AInterfaceEnum(\"A_2\", \"I am AInterfaceEnum 2\");\n" +
                "\n" +
                "    private constructor(public readonly name: string, public readonly description?: string) {}\n" +
                "\n" +
                "    toString() {\n" +
                "        return this.name;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "export class BInterfaceEnum implements BInterface {\n" +
                "    public static readonly B_1 = new BInterfaceEnum(\"B_1\", undefined, [AInterfaceEnum.A_1,AInterfaceEnum.A_2]);\n" +
                "    public static readonly B_2 = new BInterfaceEnum(\"B_2\", \"I am BInterfaceEnum 2\", [AInterfaceEnum.A_2,{\"description\":\"I am not AInterfaceEnum\"}]);\n" +
                "\n" +
                "    private constructor(public readonly name: string, public readonly info?: string, public readonly aArray?: AInterface[]) {}\n" +
                "\n" +
                "    toString() {\n" +
                "        return this.name;\n" +
                "    }\n" +
                "}";

        assertEquals(expectedWithExtension.trim(), output.trim());

        settings = TestUtils.settings();
        settings.optionalProperties = OptionalProperties.all;
        settings.sortDeclarations = true;
        settings.sortTypeDeclarations = true;
        settings.outputKind = TypeScriptOutputKind.module;
        settings.outputFileType = TypeScriptFileType.implementationFile;
        enumExtension = new EnumWithInterfacesExtension();
        enumExtension.setConfiguration(Collections.singletonMap(EnumWithInterfacesExtension.CFG_ENUM_PATTERN, "**AInterfaceEnum**"));
        settings.extensions.add(enumExtension);
        output = new TypeScriptGenerator(settings).generateTypeScript(input);
        final String expectedWithoutExtension = "" +
                "\n" +
                "export interface ABInterfaceUsage {\n" +
                "    a?: AInterface;\n" +
                "    b?: BInterface;\n" +
                "}\n" +
                "\n" +
                "export interface AInterface {\n" +
                "    description?: string;\n" +
                "}\n" +
                "\n" +
                "export interface AInterfaceImpl extends AInterface {\n" +
                "}\n" +
                "\n" +
                "export interface BInterface {\n" +
                "    aarRay?: AInterface[];\n" +
                "    info?: string;\n" +
                "}\n" +
                "\n" +
                "export type BInterfaceEnum = \"B_1\" | \"B_2\";\n" +
                "\n" +
                "\n" +
                "// Added by 'EnumWithInterfacesExtension' extension\n" +
                "\n" +
                "export class AInterfaceEnum implements AInterface {\n" +
                "    public static readonly A_1 = new AInterfaceEnum(\"A_1\", \"I am AInterfaceEnum 1\");\n" +
                "    public static readonly A_2 = new AInterfaceEnum(\"A_2\", \"I am AInterfaceEnum 2\");\n" +
                "\n" +
                "    private constructor(public readonly name: string, public readonly description?: string) {}\n" +
                "\n" +
                "    toString() {\n" +
                "        return this.name;\n" +
                "    }\n" +
                "}\n";

        assertEquals(expectedWithoutExtension.trim(), output.trim());
    }

    private static class AClass {
        public Direction direction;
    }

    enum Direction {
        North,
        East, 
        South,
        West
    }

    enum SideWithJsonPropertyAnnotations {
        @JsonProperty("left-side")
        Left,
        @JsonProperty("right-side")
        Right
    }

    enum SideWithJsonValueMethodAnnotation {
        @JsonProperty("@JsonProperty ignored since @JsonValue has higher precedence")
        Left("left-side"),
        @JsonProperty("@JsonProperty ignored since @JsonValue has higher precedence")
        Right("right-side");

        private final String jsonValue;

        private SideWithJsonValueMethodAnnotation(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        @JsonValue
        public Object getJsonValue() {
            return jsonValue;
        }
    }

    enum SideWithJsonValueFieldAnnotation {
        @JsonProperty("@JsonProperty ignored since @JsonValue has higher precedence")
        Left("left-side"),
        @JsonProperty("@JsonProperty ignored since @JsonValue has higher precedence")
        Right("right-side");

        @JsonValue
        private final String jsonValue;

        private SideWithJsonValueFieldAnnotation(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        @Override
        public String toString() {
            return "AAA " + name();
        }
    }

    enum SideUsingToString {
        @JsonProperty("@JsonProperty ignored since toString() has higher precedence")
        Left("left-side"),
        @JsonProperty("@JsonProperty ignored since toString() has higher precedence")
        Right("right-side");

        private final String jsonValue;

        private SideUsingToString(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        @Override
        public String toString() {
            return "toString:" + jsonValue;
        }
    }

    enum EmptyEnum {
    }

    static class ClassWithObjectEnum {
        public StatusType status;
        public List<Map<String, StatusType>> statuses;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum StatusType {
        GOOD(0, "Good"),
        FULL(1, "Full");

        private final int code;
        private final String label;

        private StatusType(int code, String label) {
            this.label = label;
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum BInterfaceEnum implements BInterface {
        B_1(null, AInterfaceEnum.A_1, AInterfaceEnum.A_2),
        B_2("I am BInterfaceEnum 2", AInterfaceEnum.A_2, new AInterfaceImpl("I am not AInterfaceEnum"));

        protected String info;
        protected AInterface[] aArray;

        BInterfaceEnum(String info, AInterface...aInterfaces) {
            this.info = info;
            this.aArray = aInterfaces;
        }

        @Override
        public String getInfo() {
            return info;
        }

        public AInterface[] getAArRay() {
            return aArray;
        }
    }

    public enum AInterfaceEnum implements AInterface {
        A_1("I am AInterfaceEnum 1"),
        A_2("I am AInterfaceEnum 2");

        protected String description;

        AInterfaceEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public interface AInterface {
        @JsonProperty
        String getDescription();
    }

    public interface BInterface {
        String getInfo();
        AInterface[] getAArRay();
    }

    public static class AInterfaceImpl implements AInterface {

        protected String description;

        public AInterfaceImpl(String description) {
            this.description = description;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }

    public interface ABInterfaceUsage {
        AInterface getA();
        BInterface getB();
    }

}

