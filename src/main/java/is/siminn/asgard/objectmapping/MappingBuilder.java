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

import is.siminn.asgard.objectmapping.mappingoperations.GetterSetterMappingOperation;
import static is.siminn.asgard.objectmapping.Mappings.transform;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

@SuppressWarnings("ALL") 
public class MappingBuilder<T> implements UserMappingBuilder<T> {

    private Method srcMethod;
    private Class<T> srcClass;
    private Method destMethod;
    private Class<T> destClass;
    private List<TransformationBase> transformations = new ArrayList<TransformationBase>();
    private Filter filter;

    public MappingBuilder(Class<T> srcClazz) {
        this.srcClass = srcClazz;
    }

    public MappingBuilder() {
        srcClass = null;
    }

    public <Z> Z to(Z o) {
        MapperMarker mapperInfo = (MapperMarker) o;
        if (mapperInfo.getNodeType() != MapperMarker.MappingNode.dest) throw new MappingException("you passed a 'mapping source' object in the 'to' statement");
        return o;
    }

    public UserMappingBuilder<T> applyTransformation(TransformationBase<T> transformation) {
        addTransformation(transformation);
        return this;
    }

    public UserMappingBuilder<T> applyFilter(Filter<T> filter) {
        setFilter(filter);
        return this;
    }

    public <X> UserMappingBuilder<T> applyCollectionFilter(Filter<X> filter) {
        setFilter(filter);
        return this;
    }

    public void toObject(Object destObj) {
        if(destObj instanceof MapperMarker){
            MapperMarker mm = (MapperMarker) destObj;
            setDestClass(mm.getTargetClass());
        } else {
            setDestClass((Class<T>) destObj.getClass());
        }
    }

    public <Z> void to(final Class<Z> clazz, Collection<Z> collection) {
        transformations.add(new Transformation() {
            public Object apply(Object input, Object context) {
                List result = new ArrayList();
                Collection srcCollection = (Collection) input;
                for (Object srcCollectionItem : srcCollection) {
                    if(filterAllows(context, srcCollectionItem, filter))
                        result.add(transform(srcCollectionItem, createInstance(clazz)));
                }
                return result;
            }
        });
    }

    private Object createInstance(Class<?> clazz) {
        Object resultInstance;
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    void setSrcMethod(Method method) {
        this.srcMethod = method;
    }

    void setSrcClass(Class<T> srcClass) {
        this.srcClass = srcClass;
    }

    void setDestMethod(Method destMethod) {
        this.destMethod = destMethod;
    }


    void setDestClass(Class<T> destClass) {
        this.destClass = destClass;
    }

    Method getSrcMethod() {
        return srcMethod;
    }

    public boolean isMappingComplete() {
        return getDestMethod() != null;
    }      

    private boolean isMappingStarted() {
        return getSrcMethod() != null;
    }

    private void addTransformation(TransformationBase transformation) {
        if (!isMappingComplete()) {
            transformations.add(transformation);
        } else {
            throw new TransformationException("You cannot register a transformation at this point. Did you call registerTransformation out of band?");
        }
    }

    private void setFilter(Filter filter) {
        if (isMappingStarted() && !isMappingComplete()) {
            this.filter = filter;
        } else {
            throw new TransformationException("You cannot register a filter at this point. Did you call registerFilter out of band?");
        }
    }

    Method getDestMethod() {
        return destMethod;
    }

    Class getSrcClass() {
        return srcClass;
    }

    Class getDestClass() {
        return destClass;
    }

    GetterSetterMappingOperation constructMapping() {
        GetterSetterMappingOperation result = new GetterSetterMappingOperation();
        result.setDestMethod(destMethod);
        result.setSrcMethod(srcMethod);
        result.setFilter(filter);
        result.addAllTransformations(transformations);
        return result;

    }

    public static boolean filterAllows(Object context, Object input, Filter filter) {
        //noinspection unchecked
        return filter == null || filter.allow(input, context);
    }
}
