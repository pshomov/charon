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

import is.siminn.asgard.objectmapping.Filter;
import is.siminn.asgard.objectmapping.MappingBuilder;
import is.siminn.asgard.objectmapping.Mappings;
import is.siminn.asgard.objectmapping.TransformationBase;
import is.siminn.asgard.objectmapping.TransformationException;
import is.siminn.asgard.objectmapping.TransformationContext;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GetterSetterMappingOperation extends BaseMappingOperation {
    private Method srcMethod;
    private Method destMethod;
    private Filter filter;

    public GetterSetterMappingOperation(Method srcMethod, Method destMethod) {
        this.srcMethod = srcMethod;
        this.destMethod = destMethod;
    }

    public GetterSetterMappingOperation() {
    }

    public void setSrcMethod(Method srcMethod) {
        this.srcMethod = srcMethod;
    }

    public void setDestMethod(Method destMethod) {
        this.destMethod = destMethod;
    }

    public Method getSrcMethod() {
        return srcMethod;
    }

    public Method getDestMethod() {
        return destMethod;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Filter getFilter() {
        return filter;
    }

    public void apply(Object src, Object dest, Map projectionContext) {
        if (isCollectionTypeGetter(this.destMethod)) {
            applyCollectionsMapping(src, dest, projectionContext);
        } else {
            if (this.srcIsMethodCall()) {
                applyMethodToMethodMapping(src, dest, projectionContext);
            } else {
                applyObjectToMethodMapping(src, dest, projectionContext);
            }
        }
    }

    private void applyObjectToMethodMapping(Object src, Object dest, Map projectionContext) {
        final Class<?> destClass = getDestinationClass();
        Object newObject = createInstance(destClass);
        src = applyAllTransformations(new TransformationContext(src, projectionContext, src));
        callDestSetter(dest, Mappings.transform(src, newObject));
    }

    private void applyMethodToMethodMapping(Object src, Object dest, Map projectionContext) {
        Object srcObjectForMapping = getValueFromSourceGetter(src);

        Filter filter = getFilter();
        if (MappingBuilder.filterAllows(src, srcObjectForMapping, filter)) {
            srcObjectForMapping = applyAllTransformations(new TransformationContext(srcObjectForMapping, projectionContext, src));
            callDestSetter(dest, srcObjectForMapping);
        }
    }

    private void applyCollectionsMapping(Object src, Object dest, Map projectionContext) {
        Object srcObjectForMapping = getValueFromSourceGetter(src);
        srcObjectForMapping = applyAllTransformations(new TransformationContext(srcObjectForMapping, projectionContext, src));
        Collection destCollection = callDestCollection(dest);
        //noinspection unchecked
        destCollection.addAll((Collection) srcObjectForMapping);
    }

    public void addAllTransformations(List<TransformationBase> transformations) {
        this.transformations.addAll(transformations);
    }

    Object getValueFromSourceGetter(Object src) {
        Object instance;
        try {
            instance = getSrcMethod().invoke(src);
        } catch (Exception e) {
            throw new TransformationException("Getting source property from " + src.getClass().getName() + " method name " + getSrcMethod().getName() + " failed.", e);
        }
        if (instance instanceof String) {
            String str = (String) instance;
            instance = str.trim();
        }
        return instance;
    }

    private Collection callDestCollection(Object dest){
        try {
            return (Collection) getDestMethod().invoke(dest);
        } catch (Exception e) {
            throw new TransformationException("Getting destination collection for mapping from " + dest.getClass().getName() + " method name " + getDestMethod().getName() + " failed.", e);
        }
    }

    void callDestSetter(Object dest, Object src) {
        try {
            if(isSetter(getDestMethod())){
                getDestMethod().invoke(dest, src);
            } else {
                throw new TransformationException("Do not know what to do with " + dest.getClass().getName() + " method name " + getDestMethod().getName() + " with value class \"" + src + "\", no handling available.");
            }
        } catch (Exception e) {
            throw new TransformationException("Setting destination property on " + dest.getClass().getName() + " method name " + getDestMethod().getName() + " with value \"" + src + "\" failed.", e);
        }
    }


    private boolean isCollectionTypeGetter(Method destMethod) {
        Class[] interfaces = destMethod.getReturnType().getInterfaces();
        for (Class anInterface : interfaces) {
            if (anInterface.equals(Collection.class))
                return true;
        }
        return false;
    }

    private boolean isSetter(Method destMethod) {
        return destMethod.getParameterTypes().length==1;
    }

    Class<?> getDestinationClass() {
        return getDestMethod().getParameterTypes()[0];
    }

    boolean srcIsMethodCall() {
        return getSrcMethod() != null;
    }

    static Object createInstance(Class<?> destClass) {
        Object newObject;
        try {
            newObject = destClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error instantiationg an instance for " + destClass.getName());
        }
        return newObject;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if(srcMethod!=null)
            result.append(srcMethod.getDeclaringClass().getName()).append(".").append(srcMethod.getName());
        if(destMethod!=null)
            result.append("->").append(destMethod.getDeclaringClass().getName()).append(".").append(destMethod.getName());
        return result.toString();
    }
}
