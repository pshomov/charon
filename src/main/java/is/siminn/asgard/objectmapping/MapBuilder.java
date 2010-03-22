package is.siminn.asgard.objectmapping;

import is.siminn.asgard.objectmapping.mappingoperations.GetterSetterMappingOperation;

import java.lang.reflect.Method;
import java.util.List;

public class MapBuilder<T> {
    public Method srcMethod;
    public Class<T> srcClass;
    public Method destMethod;
    public Class<T> destClass;
    public List<TransformationBase> transformations;
    public Filter filter;

    public MapBuilder(List<TransformationBase> transformations) {
        this.transformations = transformations;
    }

    public Object createInstance(Class<?> clazz) {
        Object resultInstance;
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSrcMethod(Method method) {
        this.srcMethod = method;
    }

    void setSrcClass(Class<T> srcClass) {
        this.srcClass = srcClass;
    }

    public void setDestMethod(Method destMethod) {
        this.destMethod = destMethod;
    }

    void setDestClass(Class<T> destClass) {
        this.destClass = destClass;
    }

    public Method getSrcMethod() {
        return srcMethod;
    }

    public boolean isMappingComplete() {
        return getDestMethod() != null;
    }

    public boolean isMappingStarted() {
        return getSrcMethod() != null;
    }

    public void addTransformation(TransformationBase transformation) {
        if (!isMappingComplete()) {
            transformations.add(transformation);
        } else {
            throw new TransformationException("You cannot register a transformation at this point. Did you call registerTransformation out of band?");
        }
    }

    public void setFilter(Filter filter) {
        if (isMappingStarted() && !isMappingComplete()) {
            this.filter = filter;
        } else {
            throw new TransformationException("You cannot register a filter at this point. Did you call registerFilter out of band?");
        }
    }

    public Method getDestMethod() {
        return destMethod;
    }

    public Class getSrcClass() {
        return srcClass;
    }

    public Class getDestClass() {
        return destClass;
    }

    public GetterSetterMappingOperation constructMapping() {
        GetterSetterMappingOperation result = new GetterSetterMappingOperation();
        result.setDestMethod(destMethod);
        result.setSrcMethod(srcMethod);
        result.setFilter(filter);
        result.addAllTransformations(transformations);
        return result;

    }

    public Filter getFilter() {
        return filter;
    }

    public List<TransformationBase> getTransformations() {
        return transformations;
    }

    public static boolean filterAllows(Object context, Object input, Filter filter) {
        //noinspection unchecked
        return filter == null || filter.allow(input, context);
    }
}