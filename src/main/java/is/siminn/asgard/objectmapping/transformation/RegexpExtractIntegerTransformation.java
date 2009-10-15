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
package is.siminn.asgard.objectmapping.transformation;

import is.siminn.asgard.objectmapping.Transformation;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegexpExtractIntegerTransformation implements Transformation<String> {


    private Pattern pattern;
    private Integer defaultValue;

    public RegexpExtractIntegerTransformation(String regexpattern) {
        pattern = Pattern.compile(regexpattern);
    }

    public RegexpExtractIntegerTransformation(String regexpattern, int defaultValue) {
        this(regexpattern);
        this.defaultValue = defaultValue;
    }

    public Object apply(String input, Object context) {
        if(input==null) return defaultValue;
        Integer result = defaultValue;
        Matcher matcher = pattern.matcher(input);
        if(matcher.matches()){
            String theint = matcher.group(1);
            result = Integer.parseInt(theint);
        }
        return result;
    }
}
