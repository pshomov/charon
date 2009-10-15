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

import java.lang.reflect.Field;

public class ObjectCombiner {

    public static <T> T combine(final T dominant, final T submisive) {

        if (dominant == null) return submisive;
        if (submisive == null) return dominant;

        final T result = instanciateObject(submisive);

        Class dominantClass = dominant.getClass();
        do {
            combineClassFields(dominant, submisive, result, dominantClass);
            dominantClass = dominantClass.getSuperclass();
        } while (dominantClass != null && dominantClass != Object.class);

        return result;
    }

    private static void combineClassFields(Object dominant, Object submisive, Object result, Class aClass) {
        Field fields[] = aClass.getDeclaredFields();
        for (Field field : fields) {
            try {
                combineField(dominant, submisive, result, field);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access field <" + (field == null ? "null" : field.getName()) + ">", e);
            }
        }
    }

    private static void combineField(Object dominant, Object submisive, Object result, Field field) throws IllegalAccessException {
        if (shouldCombineField(field) && isFieldPartOfType(field)) {
            enableFieldFullAccess(field);
            Object dominantValue = field.get(dominant);
            Object submisiveValue = field.get(submisive);

            dominantValue = dominantEquivalent(dominantValue);
            field.set(result, dominantValue == null ? submisiveValue : dominantValue);
        }
    }

    private static Object dominantEquivalent(Object dominantValue) {
        if (dominantValue instanceof String) {
            // in case of String an empty string means just like a null string - lack of data
            if (((String) dominantValue).length() == 0) dominantValue = null;
        }
        return dominantValue;
    }

    private static boolean shouldCombineField(Field field) {
        // we cannot reliably combine primitive types due to lack of null, such fields are ignored
        return !field.getType().isPrimitive();
    }

    private static void enableFieldFullAccess(Field field) {
        field.setAccessible(true);
    }

    /**
     * Determines if field is part of the original type or is it a part of some kind of proxy
     */
    private static boolean isFieldPartOfType(Field field) {
        return !field.isSynthetic();
    }

    @SuppressWarnings(value = "unchecked")
    private static <T> T instanciateObject(T submisive) {
        T result;
        try {
            result = (T) submisive.getClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Instantiating a class " + submisive.getClass().getName() + " failed. Did you forget to define a default constructor?", e);
        }
        return result;
    }
}
