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
import static org.mockito.Mockito.*;
import org.mockito.*;
import org.testng.annotations.*;

import java.lang.reflect.*;

@Test
public class ClassBindingsTest {
    private TransformationSet transSet;
    private BindingsProcessor processor;
    private ClassBindings classBindings;

    public void should_announce_src_and_destination_classes() {
        transSet.add(AA.class, AC.class, new GetterSetterMappingOperation());

        classBindings.processBindings(AC.class, processor);

        verify(processor).processSrcClass(AA.class);
        verify(processor).processDestClass(AC.class);
    }

    @Test(enabled = false)
    public void should_announce_mapping_operation() throws NoSuchMethodException {
        final Method methodM1 = AA.class.getMethod("getM1");
        final Method methodMM = AC.class.getMethod("setMM", int.class);
        GetterSetterMappingOperation mappingOp = new GetterSetterMappingOperation(methodM1, methodMM);
        transSet.add(AA.class, AC.class, mappingOp);

        classBindings.processBindings(AC.class, processor);

        verify(processor).process(mappingOp);
    }
    
    @Test(enabled = false)
    public void should_announce_src_transformation_and_destination_methods() throws NoSuchMethodException {
        final Method methodM1 = AA.class.getMethod("getM1");
        final Method methodMM = AC.class.getMethod("setMM", int.class);
        final GetterSetterMappingOperation newOperation = new GetterSetterMappingOperation(methodM1, methodMM);
        newOperation.addTransformation(null);
        transSet.add(AA.class, AC.class, newOperation);

        InOrder ordered = inOrder(processor);

        classBindings.processBindings(AC.class, processor);

        ordered.verify(processor).processSrcMethod(methodM1);
        ordered.verify(processor).processTransformation(null);
        ordered.verify(processor).processDestMethod(methodMM);
    }

    @Test(enabled = false)
    public void should_announce_src_filter_transformation_and_destination_methods() throws NoSuchMethodException {
        final Method methodM1 = AA.class.getMethod("getM1");
        final Method methodMM = AC.class.getMethod("setMM", int.class);
        final GetterSetterMappingOperation newOperation = new GetterSetterMappingOperation(methodM1, methodMM);
        newOperation.addTransformation(null);
        newOperation.setFilter(mock(Filter.class));
        transSet.add(AA.class, AC.class, newOperation);

        InOrder ordered = inOrder(processor);

        classBindings.processBindings(AC.class, processor);

        ordered.verify(processor).processSrcMethod(methodM1);
        ordered.verify(processor).processFilter((Filter) anyObject());
        ordered.verify(processor).processTransformation(null);
        ordered.verify(processor).processDestMethod(methodMM);
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        transSet = new TransformationSet();
        processor = mock(BindingsProcessor.class);
        classBindings = new ClassBindings(transSet);
    }

    public class AA {
        public int getM1() {
            return 1;
        }
    }

    public class AC {
        public void setMM(int a) {
        }
    }
}
