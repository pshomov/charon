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
package is.siminn.asgard.objectmapping;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import is.siminn.asgard.objectmapping.mappingoperations.BaseMappingOperation;
import is.siminn.asgard.objectmapping.mappingoperations.GetterSetterMappingOperation;

@Test
public class TransformationSetTest {

    public void should_allow_access_to_all_transformations_for_specified_source_and_destinantion_classes() {
        TransformationSet set = new TransformationSet();
        GetterSetterMappingOperation op1 = new GetterSetterMappingOperation();
        GetterSetterMappingOperation op2 = new GetterSetterMappingOperation();
        GetterSetterMappingOperation op3 = new GetterSetterMappingOperation();
        set.add(Customer.class, Customer.class, op1);
        set.add(Customer.class, Customer.class, op2);
        set.add(String.class, Customer.class, op3);
        List<BaseMappingOperation> ops = set.query(Customer.class, Customer.class);
        assertEquals(ops.size(), 2);
        assertEquals(ops.get(0), op1);
        assertEquals(ops.get(1), op2);
    }

    public void should_return_empty_results_after_clear() {
        TransformationSet set = new TransformationSet();
        GetterSetterMappingOperation op1 = new GetterSetterMappingOperation();
        GetterSetterMappingOperation op2 = new GetterSetterMappingOperation();
        GetterSetterMappingOperation op3 = new GetterSetterMappingOperation();
        set.add(Customer.class, Customer.class, op1);
        set.add(Customer.class, Customer.class, op2);
        set.add(String.class, Customer.class, op3);

        set.clear();

        List<BaseMappingOperation> ops = set.query(Customer.class, Customer.class);
        assertEquals(ops.size(), 0);
    }

    public void should_allow_access_to_all_transformations_of_the_base_class_of_the_destination_for_specified_source_and_destinantion_classes() {
        TransformationSet set = new TransformationSet();
        GetterSetterMappingOperation op1 = new GetterSetterMappingOperation();
        GetterSetterMappingOperation op2 = new GetterSetterMappingOperation();
        GetterSetterMappingOperation op3 = new GetterSetterMappingOperation();
        set.add(Customer.class, Customer.class, op1);
        set.add(Customer.class, Customer.class, op2);
        set.add(String.class, Customer.class, op3);
        List<BaseMappingOperation> ops = set.query(Customer.class, CustomerSubClass.class);
        assertEquals(ops.size(), 2);
        assertEquals(ops.get(0), op1);
        assertEquals(ops.get(1), op2);
    }

    public void should_allow_access_to_all_transformations_of_the_base_classes_of_both_the_specified_source_and_destinantion_classes() {
        TransformationSet set = new TransformationSet();
        GetterSetterMappingOperation op1 = new GetterSetterMappingOperation();
        GetterSetterMappingOperation op2 = new GetterSetterMappingOperation();
        GetterSetterMappingOperation op3 = new GetterSetterMappingOperation();
        set.add(Customer.class, Customer.class, op1);
        set.add(Customer.class, Customer.class, op2);
        set.add(String.class, Customer.class, op3);
        List<BaseMappingOperation> ops = set.query(CustomerSubClass.class, CustomerSubClass.class);
        assertEquals(ops.size(), 2);
        assertEquals(ops.get(0), op1);
        assertEquals(ops.get(1), op2);
    }

    public void should_allow_to_get_list_of_all_source_classes_for_a_specific_dest_class() {
        TransformationSet set = new TransformationSet();
        GetterSetterMappingOperation op2 = new GetterSetterMappingOperation();
        GetterSetterMappingOperation op3 = new GetterSetterMappingOperation();
        set.add(Customer.class, Customer.class, op2);
        set.add(String.class, Customer.class, op3);
        Set<Class> ops = set.getSourceClasses(Customer.class);
        assertEquals(ops.size(), 2);
        assertTrue(ops.contains(Customer.class));
        assertTrue(ops.contains(String.class));
    }

    public void should_allow_to_get_list_of_all_source_classes_for_all_base_classes_of_dest_class() {
        TransformationSet set = new TransformationSet();
        GetterSetterMappingOperation op2 = new GetterSetterMappingOperation();
        GetterSetterMappingOperation op3 = new GetterSetterMappingOperation();
        set.add(Integer.class, Customer.class, op2);
        set.add(String.class, CustomerSubClass.class, op3);
        Set<Class> ops = set.getSourceClasses(CustomerSubClass.class);
        assertEquals(ops.size(), 2);
        assertTrue(ops.contains(Integer.class));
        assertTrue(ops.contains(String.class));
    }

    private class CustomerSubClass extends Customer {
    }
}
