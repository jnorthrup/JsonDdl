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
package org.jsonddl;

public interface JsonDdlObject<J extends JsonDdlObject<J>> {
  public interface Builder<J extends JsonDdlObject<J>> {
    J build();

    Builder<J> from(J copyFrom);
    //
    // Builder<J> from(String json);
  }

  void accept(JsonDdlVisitor visitor);

  //
  J acceptMutable(JsonDdlVisitor visitor);

  //
  // <O extends JsonDdlObject<O>> O as(Class<O> clazz);
  //
  Builder<J> builder();

  Builder<J> newInstance();

  String toJson();

  void traverse(JsonDdlVisitor visitor);

  J traverseMutable(JsonDdlVisitor visitor);
}
