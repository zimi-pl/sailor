package pl.zimi.repository.proccessor;


import pl.zimi.repository.annotation.Queryable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("pl.zimi.repository.annotation.Queryable")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class QueryableProcessor extends AbstractProcessor {

    /** public for ServiceLoader */
    public QueryableProcessor() {
    }

    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        List<TypeElement> classesToHandle = (List<TypeElement>) roundEnv.getElementsAnnotatedWith(Queryable.class)
                .stream()
                .filter(e -> e.getKind() == ElementKind.CLASS)
                .collect(Collectors.toList());
        List<String> classesToHandleStrings = classesToHandle.stream().map(e -> e.getQualifiedName().toString()).collect(Collectors.toList());


        for (Element e : classesToHandle) {
            final String name = capitalize(e.getSimpleName().toString());
            final TypeElement clazz = (TypeElement)e;
            final PackageElement pack = (PackageElement) e.getEnclosingElement();
            try {
                final JavaFileObject f = processingEnv.getFiler().
                        createSourceFile(pack.getQualifiedName() + ".S" + clazz.getSimpleName());
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                        "Creating " + f.toUri());
                try (Writer w = f.openWriter()) {
                    PrintWriter pw = new PrintWriter(w);
                    pw.println("package " + pack.getQualifiedName() + ";");

                    pw.println("import pl.zimi.repository.annotation.Descriptor;");
                    pw.println("public class S"
                            + clazz.getSimpleName() + " extends Descriptor {");

                    pw.println("");
                    pw.println("    public S" + clazz.getSimpleName() + "(Descriptor parent, String path) {");
                    pw.println("        super(parent, path);");
                    pw.println("    }");
                    pw.println("    public static S" + clazz.getSimpleName() + " " + decapitalize(clazz.getSimpleName() + " = new S" + clazz.getSimpleName() + "(null, \"\");"));

                    for (final Element element : clazz.getEnclosedElements()) {
                        if (element.getKind() == ElementKind.FIELD) {
                            VariableElement field = (VariableElement) element;
                            String s = field.asType().toString();
                            if (classesToHandleStrings.contains(s)) {
                                int i = s.lastIndexOf(".");
                                String sa = s.substring(0, i) + ".S" + s.substring(i + 1);
                                pw.println("    public " + sa + " " + field.getSimpleName() + " = new " + sa + "(this, \"" + field.getSimpleName() + "\");");
                            } else {
                                pw.println("    public Descriptor " + field.getSimpleName() + " = new Descriptor(this, \"" + field.getSimpleName() + "\");");
                            }
                        }
                    }
                    pw.println("}");
                    pw.flush();
                }
            } catch (IOException x) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        x.toString());
            }
        }
        return true;
    }

    private static String capitalize(String name) {
        char[] c = name.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }

    private static String decapitalize(String name) {
        char[] c = name.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }
}