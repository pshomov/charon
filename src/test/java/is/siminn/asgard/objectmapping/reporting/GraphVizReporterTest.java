/*

Created by Petar Shomov <petar@sprettur.is> and contributors

Copyright (c) 2009 Síminn hf (http://www.siminn.is). All rights
reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Inital version of this file contributed by Síminn hf. (http://www.siminn.is)

*/
package is.siminn.asgard.objectmapping.reporting;

import is.siminn.asgard.objectmapping.*;
import is.siminn.asgard.objectmapping.mappingoperations.GetterSetterMappingOperation;
import org.testng.annotations.*;

import java.lang.reflect.*;

@Test
public class GraphVizReporterTest {
    private TransformationSet transSet;
    private ClassBindings classBindings;

    @Test(enabled = false)
    public void a_simple_test_to_generate_me_some_report() throws NoSuchMethodException {
        GraphVizReporter reporter = new GraphVizReporter();
        final Method srcMethod = ClassBindingsTest.AA.class.getMethod("hashCode");
        final Method destMethod = ClassBindingsTest.AC.class.getMethod("toString");
        final Method src1Method = ClassBindingsTest.AA.class.getMethod("hashCode");
        final Method dest1Method = ClassBindingsTest.AC.class.getMethod("hashCode");
        transSet.add(ClassBindingsTest.AA.class, ClassBindingsTest.AC.class, new GetterSetterMappingOperation(srcMethod, destMethod));
        transSet.add(ClassBindingsTest.AA.class, ClassBindingsTest.AC.class, new GetterSetterMappingOperation(src1Method, dest1Method));
        transSet.add(ClassBindingsTest.AA.class, ClassBindingsTest.AC.class, new GetterSetterMappingOperation(src1Method, dest1Method));
        transSet.add(ClassBindingsTest.AA.class, ClassBindingsTest.AC.class, new GetterSetterMappingOperation(src1Method, dest1Method));
        transSet.add(ClassBindingsTest.AA.class, ClassBindingsTest.AC.class, new GetterSetterMappingOperation(src1Method, dest1Method));

        classBindings.processBindings(ClassBindingsTest.AC.class, reporter);
        final String s = reporter.render();
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        transSet = new TransformationSet();
        classBindings = new ClassBindings(transSet);
    }

}
