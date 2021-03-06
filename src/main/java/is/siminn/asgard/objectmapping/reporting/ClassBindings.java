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

import is.siminn.asgard.objectmapping.mappingoperations.BaseMappingOperation;
import is.siminn.asgard.objectmapping.TransformationSet;

import java.util.List;
import java.util.Set;

public class ClassBindings {
    private final TransformationSet transSet;

    public ClassBindings(TransformationSet transSet) {

        this.transSet = transSet;
    }

    public void processBindings(Class aClass, BindingsProcessor processor) {
        Set<Class> sourceClasses = transSet.getSourceClasses(aClass);
        processor.processDestClass(aClass);
        for (Class sourceClass : sourceClasses) {
            processor.processSrcClass(sourceClass);
            List<BaseMappingOperation> operationList = transSet.query(sourceClass, aClass);
            for (BaseMappingOperation operation : operationList) {
                processor.process(operation);
            }
        }
    }
}
