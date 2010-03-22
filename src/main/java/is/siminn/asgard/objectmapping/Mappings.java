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

import java.util.Map;

@SuppressWarnings("ALL")
/**
 * This is just a syntactic sugar for the clients to avoid using instance varaibles for the mapping domain and its operations
 */
public class Mappings {

    static MappingsDSLBoot mappingDomain = new MappingsDSLBoot();

    public static <T> T src(final Class<T> aClass) {
        return mappingDomain.src(aClass);
    }

    public static <T> T dest(final Class<T> aClass) {
        return mappingDomain.dest(aClass);
    }

    public static void automap(Object src, Object dest) {
        mappingDomain.automap(src, dest);
    }

    public static void automap(Class srcClass, Class destClass) {
        mappingDomain.automap(srcClass, destClass);
    }

    public static <Src> UserMappingBuilder<Src> from(Src src) {
        return mappingDomain.from(src);
    }

    public static void flush() {
        mappingDomain.flush();
    }

    public static <Dest> Dest transform(final Object src, Dest dest) {
        return mappingDomain.transform(src, dest);
    }

    public static <Dest> Dest transform(final Object src, Dest dest, Map projectionContext) {
        return mappingDomain.transform(src, dest, projectionContext);
    }
}
