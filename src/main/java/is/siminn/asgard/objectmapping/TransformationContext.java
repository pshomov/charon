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

public class TransformationContext<T> {
    private Map<String,Object> params;
    private T input;
    private Object sourceContext;

    public TransformationContext(T input, Map<String,Object> params, Object transformationContext) {
        this.input = input;
        this.params = params;
        this.sourceContext = transformationContext;
    }

    public Map getParams() {
        return params;
    }

    public T getInput() {
        return input;
    }

    public void setInput(T input) {
        this.input = input;
    }

    public Object getSourceContext() {
        return sourceContext;
    }

    public Object getParam(String key) {
        return params.get(key);
    }
}
