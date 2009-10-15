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

import org.testng.annotations.Test;
import static org.testng.Assert.fail;
import static org.mockito.Mockito.mock;

@Test
public class MappingBuilderTest {
    @Test(enabled = false)
    public void should_throw_if_trying_to_use_registerTransformation() {
        MappingBuilder builder = new MappingBuilder();
        try {
            builder.applyTransformation(mock(Transformation.class));
            fail("should not get here");
        } catch (TransformationException e) {
        }
    }

    public void should_throw_if_trying_to_use_registerFilter_drectly() {
        MappingBuilder builder = new MappingBuilder();
        try {
            builder.applyFilter(mock(Filter.class));
            fail("should not get here");
        } catch (TransformationException e) {
        }
    }

}
