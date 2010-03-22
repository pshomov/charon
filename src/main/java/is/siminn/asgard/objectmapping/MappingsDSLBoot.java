package is.siminn.asgard.objectmapping;

import is.siminn.asgard.objectmapping.mappingoperations.BaseMappingOperation;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MappingsDSLBoot {
    TransformationSet transformations = new TransformationSet();
    MappingBuilder ongoingMapping = new MappingBuilder();

    public <T> T src(final Class<T> aClass) {
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

    public <T> T dest(final Class<T> aClass) {
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

    public void automap(Object src, Object dest) {
        MapperMarker srcNodeInfo = (MapperMarker) src;
        MapperMarker destNodeInfo = (MapperMarker) dest;
        final Class destClass = destNodeInfo.getTargetClass();
        final Class srcClass = srcNodeInfo.getTargetClass();
        automap(srcClass, destClass);
    }

    public void automap(Class srcClass, Class destClass) {
        for (Method method : srcClass.getMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    final Method setter = destClass.getMethod(method.getName().replaceFirst("get", "set"), method.getReturnType());
                    if ((setter.getParameterTypes()[0] == method.getReturnType()) && isAutomappableType(method.getReturnType())) {
                        ongoingMapping.setSrcClass(srcClass);
                        ongoingMapping.setSrcMethod(method);
                        ongoingMapping.setDestClass(destClass);
                        ongoingMapping.setDestMethod(setter);
                        checkMappingDone();
                    }
                } catch (NoSuchMethodException e) {
                    continue;
                }
            }
        }
    }

    private static boolean isAutomappableType(Class<?> cls) {
        return !Collection.class.isAssignableFrom(cls);
    }

    public <Src> UserMappingBuilder<Src> from(Src src) {
        if (src instanceof MapperMarker) {
            checkMappingDone();
            MapperMarker mapperInfo = (MapperMarker) src;
            if (mapperInfo.getNodeType() != MapperMarker.MappingNode.src)
                throw new MappingException("you passed a 'mapping destination' object in the from statement");
            Class srcClazz = mapperInfo.getTargetClass();
            ongoingMapping.setSrcClass(srcClazz);
        }
        return ongoingMapping;
    }

    public void flush() {
        transformations.clear();
        ongoingMapping = new MappingBuilder();
    }

    public <Dest> Dest transform(final Object src, Dest dest) {
        return transform(src, dest, Collections.EMPTY_MAP);
    }

    public <Dest> Dest transform(final Object src, Dest dest, Map projectionContext) {
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

    private boolean shouldRecordMethod(Method method) {
        String name = method.getName();
        return name.startsWith("get") || name.startsWith("set") || name.startsWith("is");
    }

    private void checkMappingDone() {
        if (ongoingMapping.isMappingComplete()) {
            transformations.add(ongoingMapping.getSrcClass(), ongoingMapping.getDestClass(), ongoingMapping.constructMapping());
            ongoingMapping = new MappingBuilder();
        }
    }
}
