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

import is.siminn.asgard.objectmapping.mappingoperations.BaseMappingOperation;
import is.siminn.asgard.objectmapping.mappingoperations.GetterSetterMappingOperation;

import java.util.*;


public class TransformationSet {
    Map<Class, Map<Class, List<BaseMappingOperation>>> transfomarmations = new HashMap<Class, Map<Class, List<BaseMappingOperation>>>();

    public void add(Class srcClass, Class destClass, GetterSetterMappingOperation newOperation) {
        Map<Class, List<BaseMappingOperation>> srcClassMap = transfomarmations.get(srcClass);
        if (srcClassMap == null){
            srcClassMap = new HashMap<Class, List<BaseMappingOperation>>();
            transfomarmations.put(srcClass, srcClassMap);
        }

        List<BaseMappingOperation> operations = srcClassMap.get(destClass);
        if (operations == null){
            operations = new ArrayList<BaseMappingOperation>();
            srcClassMap.put(destClass, operations);
        }

        operations.add(newOperation);
    }

    @SuppressWarnings(value = "unchecked")
    public List<BaseMappingOperation> query(Class srcClass, Class destClass) {
        List<BaseMappingOperation> result = new ArrayList<BaseMappingOperation>();
        Iterable<Class> allDestClasses = getAllClasses(destClass);
        Iterable<Class> allSrcClasses = getAllClasses(srcClass);
        for (Class sClass : allSrcClasses) {
            Map<Class, List<BaseMappingOperation>> srcClassMap = transfomarmations.get(sClass);
            if (srcClassMap != null){
                for (Class dClass : allDestClasses) {
                    List<BaseMappingOperation> destClassMap = srcClassMap.get(dClass);
                    if (destClassMap != null){
                        result.addAll(destClassMap);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Retrieves all base classes of the specified class
     * @param aClass the class whose base classes we want to get
     * @return a set of all base classes and the specified class itself
     */
    private Set<Class> getAllClasses(Class aClass) {
        Set<Class> result = new HashSet<Class>();
        Class lastClass = aClass;
        do{
            result.add(lastClass);
            lastClass = lastClass.getSuperclass();
        } while(lastClass != null && lastClass != Object.class);
        return result;
    }

    public void clear() {
        transfomarmations.clear();
    }

    public Set<Class> getSourceClasses(Class destClass) {
        Set<Class> result = new HashSet<Class>();
        Set<Class> allClasses = getAllClasses(destClass);
        Set<Map.Entry<Class, Map<Class, List<BaseMappingOperation>>>> entries = transfomarmations.entrySet();
        for (Map.Entry<Class,Map<Class,List<BaseMappingOperation>>> entry : entries) {
            Set<Class> destClasses = entry.getValue().keySet();
            for (Class aClass : destClasses) {
                if (allClasses.contains(aClass)) {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }

}
