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
package is.siminn.asgard.objectmapping.reporting;

import is.siminn.asgard.objectmapping.Filter;
import is.siminn.asgard.objectmapping.TransformationBase;
import is.siminn.asgard.objectmapping.mappingoperations.BaseMappingOperation;
import is.siminn.asgard.objectmapping.mappingoperations.GetterSetterMappingOperation;
import org.kohsuke.graphviz.Graph;
import org.kohsuke.graphviz.Node;
import org.kohsuke.graphviz.Style;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GraphVizReporter implements BindingsProcessor{
    private Node srcClazz;
    private Node destClazz;
    private Method srcMethod;
    private Graph graph;

    public GraphVizReporter() {
        Style style = new Style();
        graph = new Graph();
        graph.style(style);
    }

    public void processSrcClass(Class clazz) {
        srcClazz = new Node();
        srcClazz.attr("label", clazz.getName());
        graph.node(srcClazz);
    }

    public void processDestClass(Class clazz) {
        destClazz = new Node();
        destClazz.attr("label", clazz.getName());
        graph.node(destClazz);
    }

    public void processDestMethod(Method clazz) {
        Style style = new Style();
        style.attr("label", srcMethod.getName()+"->"+ clazz.getName());
//        style.attr("headlabel", srcMethod.getName());
//        style.attr("taillabel", destMethod.getName());
//        style.attr("labelangle", "-22.0");
//        style.attr("label", "0");
//        style.attr("labeldistance", "0.5");
//        style.attr("decorate", "true");
        graph.edge(srcClazz, destClazz, style);
    }

    public void processSrcMethod(Method clazz) {
        srcMethod = clazz;
    }

    public void processTransformation(TransformationBase transformation) {
    }

    public void processFilter(Filter filter) {
    }

    public void process(BaseMappingOperation operation) {
        throw new UnsupportedOperationException("Please don't call me");
    }

    public void process(GetterSetterMappingOperation operation) {
        Method srcMethod = operation.getSrcMethod();
        processSrcMethod(srcMethod);
        if (operation.getFilter() != null){
            processFilter(operation.getFilter());
        }
        final List<TransformationBase> transformationList = operation.getTransformations();
        for (TransformationBase transformation : transformationList) {
            processTransformation(transformation);
        }
        Method destMethod = operation.getDestMethod();
        processDestMethod(destMethod);

    }

    public String render(){
        try {
            final ArrayList<String> options = new ArrayList<String>();
            options.add("dot");
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            graph.generateTo(options, byteArrayOutputStream);
            return byteArrayOutputStream.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
