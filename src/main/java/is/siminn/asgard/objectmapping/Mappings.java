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
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class Mappings {

    static TransformationSet transformations = new TransformationSet();
    static MappingBuilder ongoingMapping = new MappingBuilder();

    public static <T> T src(final Class<T> aClass) {
        Enhancer e = new Enhancer();
        e.setSuperclass(aClass);
        e.setInterfaces(new Class[]{MapperMarker.class});
        e.setCallback(new MethodInterceptor() {

            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                if (method.getDeclaringClass() == MapperMarker.class && method.getName().equals("getTargetClass")) {
                    return aClass;
                }
                if (method.getDeclaringClass() == MapperMarker.class && method.getName().equals("getNodeType")) {
                    return MapperMarker.MappingNode.src;
                }
                if (!shouldRecordMethod(method)) {
                    return proxy.invokeSuper(obj, args);
                }
                checkMappingDone();
                ongoingMapping.setSrcMethod(method);
                ongoingMapping.setSrcClass(aClass);
                return null;
            }
        });

        return (T) e.create();
    }

    public static <T> T dest(final Class<T> aClass) {
        Enhancer e = new Enhancer();
        e.setSuperclass(aClass);
        e.setInterfaces(new Class[]{MapperMarker.class});
        e.setCallback(new MethodInterceptor() {

            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                if (method.getDeclaringClass() == MapperMarker.class && method.getName().equals("getTargetClass")) {
                    return aClass;
                }
                if (method.getDeclaringClass() == MapperMarker.class && method.getName().equals("getNodeType")) {
                    return MapperMarker.MappingNode.dest;
                }
                if (!shouldRecordMethod(method)) {
                    return proxy.invokeSuper(obj, args);
                }
                ongoingMapping.setDestMethod(method);
                ongoingMapping.setDestClass(aClass);
                return null;
            }
        });

        return (T) e.create();
    }

    public static void automap(Object src, Object dest) {
        MapperMarker srcNodeInfo = (MapperMarker) src;
        MapperMarker destNodeInfo = (MapperMarker) dest;
        for (Method method : srcNodeInfo.getTargetClass().getMethods()) {
                if (method.getName().startsWith("get")){
                    try {
                        final Method setter = destNodeInfo.getTargetClass().getMethod(method.getName().replaceFirst("get", "set"), method.getReturnType());
                        if ((setter.getParameterTypes()[0] == method.getReturnType())) {
                            ongoingMapping.setSrcClass(srcNodeInfo.getTargetClass());
                            ongoingMapping.setSrcMethod(method);
                            ongoingMapping.setDestClass(destNodeInfo.getTargetClass());
                            ongoingMapping.setDestMethod(setter);
                            checkMappingDone();
                        }
                    } catch (NoSuchMethodException e) {
                        continue;
                    }
                }
        }
    }

    public static <Src> UserMappingBuilder<Src> from(Src src) {
        if (src instanceof MapperMarker) {
            checkMappingDone();
            MapperMarker mapperInfo = (MapperMarker) src;
            if (mapperInfo.getNodeType() != MapperMarker.MappingNode.src) throw new MappingException("you passed a 'mapping destination' object in the from statement");
            Class srcClazz = mapperInfo.getTargetClass();
            ongoingMapping.setSrcClass(srcClazz);
        }
        return ongoingMapping;
    }

    public static void flush() {
        transformations.clear();
        ongoingMapping = new MappingBuilder();
    }

    public static <Dest> Dest transform(final Object src, Dest dest) {
        return transform(src, dest, Collections.EMPTY_MAP);
    }

    public static <Dest> Dest transform(final Object src, Dest dest, Map projectionContext) {
        checkMappingDone();
        if (projectionContext == null) projectionContext = Collections.EMPTY_MAP;

        if (src == null || dest == null) return dest;
        List<BaseMappingOperation> operations = transformations.query(src.getClass(), dest.getClass());

        if (src.getClass() == dest.getClass() && operations.size() == 0) return (Dest) src;

        for (BaseMappingOperation operation : operations) {
            operation.apply(src, dest, projectionContext);
        }
        return dest;
    }

    private static boolean shouldRecordMethod(Method method) {
        String name = method.getName();
        return name.startsWith("get") || name.startsWith("set") || name.startsWith("is");
    }

    private static void checkMappingDone() {
        if (ongoingMapping.isMappingComplete()) {
            transformations.add(ongoingMapping.getSrcClass(), ongoingMapping.getDestClass(), ongoingMapping.constructMapping());
            ongoingMapping = new MappingBuilder();
        }
    }

}
