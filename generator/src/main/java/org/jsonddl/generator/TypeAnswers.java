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
package org.jsonddl.generator;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.jsonddl.impl.ContextImpl;
import org.jsonddl.model.Kind;
import org.jsonddl.model.Type;

public class TypeAnswers {
  private static final Map<Kind, Class<?>> contextTypes = new EnumMap<Kind, Class<?>>(
      Kind.class);
  static {
    contextTypes.put(Kind.BOOLEAN, ContextImpl.ValueContext.Builder.class);
    contextTypes.put(Kind.DDL, ContextImpl.ObjectContext.Builder.class);
    contextTypes.put(Kind.DOUBLE, ContextImpl.ValueContext.Builder.class);
    contextTypes.put(Kind.ENUM, ContextImpl.ValueContext.Builder.class);
    contextTypes.put(Kind.EXTERNAL, ContextImpl.ValueContext.Builder.class);
    contextTypes.put(Kind.INTEGER, ContextImpl.ValueContext.Builder.class);
    contextTypes.put(Kind.LIST, ContextImpl.ListContext.Builder.class);
    contextTypes.put(Kind.MAP, ContextImpl.MapContext.Builder.class);
    contextTypes.put(Kind.STRING, ContextImpl.ValueContext.Builder.class);
  }

  public static String getContextBuilderDeclaration(Type type) {
    StringBuilder sb = new StringBuilder();
    Class<?> contextBuilderType = getContextBuilderType(type);
    sb.append(contextBuilderType.getCanonicalName());
    sb.append("<");
    if (contextTypes.get(Kind.EXTERNAL).equals(contextBuilderType)) {
      sb.append(getParameterizedQualifiedSourceName(type));
    } else {
      sb.append(getContextParameterization(type));
    }
    sb.append(">");
    return sb.toString();
  }

  public static Class<?> getContextBuilderType(Type type) {
    switch (type.getKind()) {
      case LIST:
        if (Kind.DDL.equals(type.getListElement().getKind())) {
          return contextTypes.get(Kind.LIST);
        }
        return contextTypes.get(Kind.EXTERNAL);
      case MAP:
        if (Kind.DDL.equals(type.getMapValue().getKind())) {
          return contextTypes.get(Kind.MAP);
        }
        return contextTypes.get(Kind.EXTERNAL);
    }
    return contextTypes.get(type.getKind());
  }

  public static String getContextParameterization(Type type) {
    switch (type.getKind()) {
      case LIST:
        return type.getListElement().getName();
      case MAP:
        return type.getMapValue().getName();
    }
    return getParameterizedQualifiedSourceName(type);
  }

  public static String getParameterizedQualifiedSourceName(Type type) {
    switch (type.getKind()) {
      case LIST:
        return String.format("%s<%s>", getQualifiedSourceName(type),
            getParameterizedQualifiedSourceName(type.getListElement()));
      case MAP:
        return String.format("%s<%s,%s>", getQualifiedSourceName(type),
            getParameterizedQualifiedSourceName(type.getMapKey()),
            getParameterizedQualifiedSourceName(type.getMapValue()));
    }
    return getQualifiedSourceName(type);
  }

  public static String getQualifiedSourceName(Type type) {
    switch (type.getKind()) {
      case BOOLEAN:
        return Boolean.class.getCanonicalName();
      case DOUBLE:
        return Double.class.getCanonicalName();
      case INTEGER:
        return Integer.class.getCanonicalName();
      case STRING:
        return String.class.getCanonicalName();
      case DDL:
      case ENUM:
      case EXTERNAL:
        return type.getName();
      case LIST:
        return List.class.getCanonicalName();
      case MAP:
        return Map.class.getCanonicalName();
    }
    throw new UnsupportedOperationException(type.toString());
  }

  /**
   * Returns {@code true} if the two types are equivalent.
   */
  public static boolean isSameType(Type a, Type b) {
    if (!a.getKind().equals(b.getKind())) {
      return false;
    }

    switch (a.getKind()) {
      case DDL:
      case ENUM:
      case EXTERNAL:
        return a.getName().equals(b.getName());
      case LIST:
        return isSameType(a.getListElement(), b.getListElement());
      case MAP:
        return isSameType(a.getMapKey(), b.getMapKey()) &&
          isSameType(a.getMapValue(), b.getMapValue());
    }

    return true;
  }

  public static boolean shouldProtect(Type type) {
    switch (type.getKind()) {
      case DDL:
      case LIST:
      case MAP:
        return true;
    }
    return false;
  }
}
