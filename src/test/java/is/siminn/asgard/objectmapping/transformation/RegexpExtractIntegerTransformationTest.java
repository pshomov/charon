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
package is.siminn.asgard.objectmapping.transformation;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import is.siminn.asgard.objectmapping.Transformation;

@SuppressWarnings({"RedundantCast"})
@Test
public class RegexpExtractIntegerTransformationTest {

    public void should_extract_integer_from_first_group(){
        Transformation<String> streetnoTransform = new RegexpExtractIntegerTransformation(".*\\s+(\\d+).*");
        assertEquals(streetnoTransform.apply("BLAH 234",null),(Integer)234);
    }

    public void should_throw_on_non_number_group_match_and_contain_offending_context(){
        Transformation<String> transformation = new RegexpExtractIntegerTransformation("(.*)\\s+\\d+.*");
        try {
            transformation.apply("BLAH 234",null);
        } catch (NumberFormatException e) {
            assertTrue(e.getMessage().indexOf("BLAH")>=0);
        }
    }

    public void should_use_default_value_when_no_match(){
        Transformation<String> transformation = new RegexpExtractIntegerTransformation("(.*)\\s+\\d+.*", 0);
        final Integer o = (Integer) transformation.apply("FANST EKKI/ERLENDIS", null);
        assertEquals(o.intValue(), 0);
    }

    public void should_use_default_value_when_input_parameter_is_null(){
        Transformation<String> transformation = new RegexpExtractIntegerTransformation("(.*)\\s+\\d+.*", 0);
        final Integer o = (Integer) transformation.apply(null, null);
        assertEquals(o.intValue(), 0);
    }
}
