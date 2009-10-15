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

import is.siminn.asgard.objectmapping.Transformation;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import org.testng.annotations.Test;

@Test
public class CapitalizeTransformationTest {

    private static final String TEST_STRING =
            "   the qUICK FOX jumped over THE.LAZy-dog@*123@*e      123 \u00E6\u00F0\u00F6 \u00D6\u00D0\u00DE ";
    private static final String RESULT_STRING =
            "The Quick Fox Jumped Over The.Lazy-Dog@*123@*E 123 \u00C6\u00F0\u00F6 \u00D6\u00F0\u00FE";

    private Transformation transformation;

    public CapitalizeTransformationTest() {
        this.transformation = new CapitalizeTransformation();
    }

    public void should_capitilize_only_first_letter_when_input_string_is_not_empty() {
        assertEquals(transformation.apply(TEST_STRING, null), RESULT_STRING);
    }

    public void should_return_empty_string_when_input_string_is_empty() {
        assertEquals(transformation.apply("", null), "");
    }

    public void should_return_null_when_input_string_is_null() {
        assertNull(transformation.apply(null, null));

    }
}

