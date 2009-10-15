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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CapitalizeTransformation implements Transformation<String> {

    private Pattern pattern;
    private Pattern isWhiteSpacePattern;

    public CapitalizeTransformation() {
        pattern = Pattern.compile(("((\\s)+)|((\\p{Punct})+)|([\\S&&[^\\p{Punct}]]+)"));
        isWhiteSpacePattern = Pattern.compile("(\\s)+");
    }

    public Object apply(String input, Object context) {
        if (input == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(input);
        StringBuilder builder = new StringBuilder(input.length());
        boolean whiteSpaceDiscovered = false;
        boolean firstWordAdded = false;
        while (matcher.find()) {
            String group = matcher.group();
            Matcher whiteSpaceMatcher = isWhiteSpacePattern.matcher(group);
            if (whiteSpaceMatcher.matches()) {
                whiteSpaceDiscovered = firstWordAdded;
            } else {
                if (whiteSpaceDiscovered) {
                    builder.append(" ");
                }
                // capitalize word
                builder.append(group.substring(0, 1).toUpperCase());
                if (group.length() > 1) {
                    builder.append(group.substring(1).toLowerCase());
                }
                whiteSpaceDiscovered = false;
                firstWordAdded = true;
            }
        }
        return builder.toString();
    }

}
