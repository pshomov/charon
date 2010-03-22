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

    private final MapBuilder<T> mapBuilder = new MapBuilder<T>(new ArrayList<TransformationBase>());

    public MappingBuilder(Class<T> srcClazz) {
        this.mapBuilder.setSrcClass(srcClazz);
    }

    public MappingBuilder() {
        mapBuilder.setSrcClass(null);
    }

    public <Z> Z to(Z o) {
        MapperMarker mapperInfo = (MapperMarker) o;
        if (mapperInfo.getNodeType() != MapperMarker.MappingNode.dest) throw new MappingException("you passed a 'mapping source' object in the 'to' statement");
        return o;
    }

    public UserMappingBuilder<T> applyTransformation(TransformationBase<T> transformation) {
        mapBuilder.addTransformation(transformation);
        return this;
    }

    public UserMappingBuilder<T> applyFilter(Filter<T> filter) {
        mapBuilder.setFilter(filter);
        return this;
    }

    public <X> UserMappingBuilder<T> applyCollectionFilter(Filter<X> filter) {
        mapBuilder.setFilter(filter);
        return this;
    }

    public void toObject(Object destObj) {
        if(destObj instanceof MapperMarker){
            MapperMarker mm = (MapperMarker) destObj;
            mapBuilder.setDestClass(mm.getTargetClass());
        } else {
            mapBuilder.setDestClass((Class<T>) destObj.getClass());
        }
    }

    public <Z> void to(final Class<Z> clazz, Collection<Z> collection) {
        mapBuilder.getTransformations().add(new Transformation() {
            public Object apply(Object input, Object context) {
                List result = new ArrayList();
                Collection srcCollection = (Collection) input;
                for (Object srcCollectionItem : srcCollection) {
                    if(MapBuilder.filterAllows(context, srcCollectionItem, mapBuilder.getFilter()))
                        result.add(transform(srcCollectionItem, mapBuilder.createInstance(clazz)));
                }
                return result;
            }
        });
    }

    private Object createInstance(Class<?> clazz) {
        return mapBuilder.createInstance(clazz);
    }

    void setSrcMethod(Method method) {
        mapBuilder.setSrcMethod(method);
    }

    void setSrcClass(Class<T> srcClass) {
        mapBuilder.setSrcClass(srcClass);
    }

    void setDestMethod(Method destMethod) {
        mapBuilder.setDestMethod(destMethod);
    }


    void setDestClass(Class<T> destClass) {
        mapBuilder.setDestClass(destClass);
    }

    Method getSrcMethod() {
        return mapBuilder.getSrcMethod();
    }

    public boolean isMappingComplete() {
        return mapBuilder.isMappingComplete();
    }

    private boolean isMappingStarted() {
        return mapBuilder.isMappingStarted();
    }

    private void addTransformation(TransformationBase transformation) {
        mapBuilder.addTransformation(transformation);
    }

    private void setFilter(Filter filter) {
        mapBuilder.setFilter(filter);
    }

    Method getDestMethod() {
        return mapBuilder.getDestMethod();
    }

    Class getSrcClass() {
        return mapBuilder.getSrcClass();
    }

    Class getDestClass() {
        return mapBuilder.getDestClass();
    }

    GetterSetterMappingOperation constructMapping() {

        return mapBuilder.constructMapping();
    }

}
