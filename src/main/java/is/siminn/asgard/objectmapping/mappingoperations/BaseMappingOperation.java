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
package is.siminn.asgard.objectmapping.mappingoperations;

import is.siminn.asgard.objectmapping.ContextTransformation;
import is.siminn.asgard.objectmapping.Transformation;
import is.siminn.asgard.objectmapping.TransformationBase;
import is.siminn.asgard.objectmapping.TransformationContext;
import is.siminn.asgard.objectmapping.TransformationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseMappingOperation implements SimpleMappingOperation {
    protected List<TransformationBase> transformations = new ArrayList<TransformationBase>();

    public void addTransformation(Transformation transformation) {
        transformations.add(transformation);
    }

    public List<TransformationBase> getTransformations() {
        return Collections.unmodifiableList(transformations);
    }

    Object applyAllTransformations(TransformationContext context) {
        Object input = context.getInput();
        for (TransformationBase transformation : getTransformations()) {
            //noinspection unchecked
            try {
                if (transformation instanceof Transformation) {
                    Transformation originalTransformation = (Transformation) transformation;
                    //noinspection unchecked
                    context.setInput(originalTransformation.apply(context.getInput(), context.getSourceContext()));
                } else if (transformation instanceof ContextTransformation){
                    ContextTransformation ctxTransform = (ContextTransformation) transformation;
                    context.setInput(ctxTransform.apply(context));
                }
            } catch (Exception e) {
                throw new TransformationException("Transformation for projection " + toString() + " failed, on source '" + input + "'",e);
            }
        }
        return context.getInput();
    }
}
