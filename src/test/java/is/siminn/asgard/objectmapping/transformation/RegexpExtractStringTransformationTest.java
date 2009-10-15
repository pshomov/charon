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
import is.siminn.asgard.objectmapping.Transformation;

@Test
public class RegexpExtractStringTransformationTest {
    private static final Transformation<String> STREETNAME_TRANSFORM = new RegexpExtractStringTransformation("(.*)\\s+\\d+.*",true);
    private static final Transformation<String> STREETNO_LETTER_TRANSFORM = new RegexpExtractStringTransformation(".*\\s+\\d+\\s*([a-zA-Z,\u00E1,\u00E9,\u00ED,\u00FA,\u00FD,\u00F3,\u00F6,\u00FE,\u00C1,\u00C9,\u00CD,\u00DA,\u00DD,\u00D3,\u00D6,\u00DE,\u00F0,\u00D0]).*",false);

    public void should_extract_string_from_first_group(){
        assertEquals(STREETNAME_TRANSFORM.apply("BLAH 234",null),"BLAH");
    }

    public void should_return_whole_string_if_no_match(){
        assertEquals(STREETNAME_TRANSFORM.apply("BLAH",null),"BLAH");
    }

    public void should_return_empty_string_if_no_match(){
        assertEquals(STREETNO_LETTER_TRANSFORM.apply("BLAH",null),"");
    }
}
