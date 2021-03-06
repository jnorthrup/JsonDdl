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
package org.jsonddl.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * This just ensures that the maven processor runs at the right time.
 */
public class JsonDdlTest {
  @Test
  public void test() {
    new org.jsonddl.test.industrial.Example.Builder().build();
  }

  @Test
  public void testPojo() {
    new org.jsonddl.test.pojo.Example();
  }

  /**
   * Make sure the expected resources have been made available on the classpath.
   */
  @Test
  public void testResource() {
    assertNotNull(Thread.currentThread().getContextClassLoader()
        .getResource("org/jsonddl/test/industrial/idiomatic.js"));
    assertNotNull(Thread.currentThread().getContextClassLoader()
        .getResource("org/jsonddl/test/industrial/schema.js"));
  }
}
