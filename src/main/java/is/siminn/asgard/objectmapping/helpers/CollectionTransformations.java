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
package is.siminn.asgard.objectmapping.helpers;

import static is.siminn.asgard.objectmapping.Mappings.transform;

import java.util.*;

public class CollectionTransformations {
    public static <SrcClass, DestClass> List<DestClass> transformObjectCollections(List<SrcClass> srcCollection, Class<DestClass> destClass) {
        List<DestClass> result = new ArrayList<DestClass>();

        for (SrcClass srcObject : srcCollection) {
            DestClass destinationObject;
            try {
                destinationObject = destClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Cannot create instance of "+destClass.getName());
            }
            transform(srcObject, destinationObject);
            result.add(destinationObject);
        }
        return result;
    }
}
