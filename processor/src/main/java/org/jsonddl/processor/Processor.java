/*
 * Copyright 2011 Robert W. Vawter III <bob@vawter.org>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jsonddl.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import org.jsonddl.generator.Dialect;
import org.jsonddl.generator.Generator;
import org.jsonddl.generator.Options;

@SupportedAnnotationTypes("org.jsonddl.processor.GenerateFrom")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Processor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element elt : roundEnv.getElementsAnnotatedWith(GenerateFrom.class)) {
      final PackageElement pelt = (PackageElement) elt;
      String packageName = pelt.getQualifiedName().toString();
      GenerateFrom gen = pelt.getAnnotation(GenerateFrom.class);
      try {
        FileObject obj = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH,
              packageName, gen.value());

        Map<String, String> extraOptions = new TreeMap<String, String>();
        for (ExtraOption extra : gen.extraOptions()) {
          extraOptions.put(extra.key(), extra.value());
        }
        Options options = new Options.Builder()
            .withDialects(Arrays.asList(gen.dialects()))
            .withExtraOptions(extraOptions)
            .withPackageName(pelt.getQualifiedName().toString())
            .build();
        new Generator().generate(obj.openInputStream(), options, new Dialect.Collector() {

          @Override
          public void println(String message) {
            processingEnv.getMessager().printMessage(Kind.NOTE, message);
          }

          @Override
          public void println(String format, Object... args) {
            println(String.format(format, args));
          }

          @Override
          public Writer writeJavaSource(String packageName, String simpleName)
                throws IOException {
            JavaFileObject obj = processingEnv.getFiler().createSourceFile(
                  packageName + "." + simpleName);
            return new OutputStreamWriter(obj.openOutputStream(), SOURCE_CHARSET);
          }

          @Override
          public OutputStream writeResource(String path) throws IOException {
            FileObject obj = processingEnv.getFiler().createResource(
                  StandardLocation.CLASS_OUTPUT, "", path);
            return obj.openOutputStream();
          }
        });

      } catch (IOException e) {
        processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage(), pelt);
        e.printStackTrace();
      }
    }
    return false;
  }
}
