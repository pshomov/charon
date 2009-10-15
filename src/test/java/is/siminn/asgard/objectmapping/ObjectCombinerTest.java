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

import static is.siminn.asgard.objectmapping.ObjectCombiner.combine;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

@Test
public class ObjectCombinerTest {
    public void should_combine_strings() {
        SampleClass submisive = new SampleClass("fle");
        SampleClass dominant = new SampleClass("flo");
        SampleClass result = combine(dominant, submisive);
        assertEquals(result.getFle(), "flo");
    }

    public void should_combine_references_but_null_pointers_do_not_dominate() {
        SampleClass submisive = new SampleClass("fle");
        SampleClass dominant = new SampleClass(null);
        SampleClass result = combine(dominant, submisive);
        assertEquals(result.getFle(), "fle");
    }

    public void should_combine_strings_but_empty_strings_do_not_dominate() {
        SampleClass submisive = new SampleClass("fle");
        SampleClass dominant = new SampleClass("");
        SampleClass result = combine(dominant, submisive);
        assertEquals(result.getFle(), "fle");
    }

    public void should_combine_all_fields() {
        NotSoSampleClass submisive = new NotSoSampleClass(0, "fle", new NotSoSampleClass(0, "child", null));
        NotSoSampleClass dominant = new NotSoSampleClass(1, "floo", null);
        NotSoSampleClass result = combine(dominant, submisive);
        assertEquals(result.getName(), "floo");
        assertEquals(result.getA(), new Integer(1));
        assertEquals(result.getOtherInst().getName(), "child");
    }

    public void should_combine_all_fields_in_ancestors() {
        B submisive = new B(1,2);
        B dominant = new B(3,4);
        B result = combine(dominant, submisive);
        assertEquals(result.a, new Integer(3));
        assertEquals(result.b, new Integer(4));
    }

    public void should_combine_even_when_dominant_object_is_null(){
          NotSoSampleClass submisive = new NotSoSampleClass(1, "floo", null);
          NotSoSampleClass result = combine(null, submisive);
          assertEquals(result.getName(), "floo");
          assertEquals(result.getA(), new Integer(1));
          assertNull(result.getOtherInst());
      }

    public void should_return_dominant_if_submissive_is_null(){
        B submisive = null;
        B dominant = new B(3,4);
        B result = combine(dominant, submisive);
        assertEquals(result.a, new Integer(3));
        assertEquals(result.b, new Integer(4));
    }

    public void should_return_submissive_if_dominant_is_null(){
        B submisive = new B(3,4);
        B dominant = null;
        B result = combine(dominant, submisive);
        assertEquals(result.a, new Integer(3));
        assertEquals(result.b, new Integer(4));
    }

    public void should_ignore_primitive_boolean_fields(){
        C submissive = new C(true);
        C dominant = new C(true);

        C res = combine(dominant, submissive);
        assertFalse(res.isValue());
    }

    public void should_ignore_primitive_integer_fields(){
        E submissive = new E(3);
        E dominant = new E(4);
        E res = combine(dominant, submissive);
        assertEquals(res.getI(),0);
    }

    public void should_combine_first_class_boolean_fields(){
        D submissive = new D(Boolean.FALSE);
        D dominant = new D(Boolean.TRUE);

        D res = combine(dominant, submissive);
        assertTrue(res.getValue());
    }

    public static class A{
        public Integer a;

        public A(){}

        public A(Integer a) {
            this.a = a;
        }
    }

    public static class B extends A{
        public Integer b;

        public B(){}
        public B(Integer a, Integer b) {
            super(a);
            this.b = b;
        }

    }

    public static class NotSoSampleClass {
        Integer a;
        String name;
        NotSoSampleClass otherInst;

        public NotSoSampleClass(Integer a, String name, NotSoSampleClass otherInst) {
            this.a = a;
            this.name = name;
            this.otherInst = otherInst;
        }

        public Integer getA() {
            return a;
        }

        public String getName() {
            return name;
        }

        public NotSoSampleClass getOtherInst() {
            return otherInst;
        }

        public NotSoSampleClass() {
        }
    }

    public static class SampleClass {
        private String fle;

        public SampleClass(String fle) {
            this.fle = fle;
        }

        public String getFle() {
            return fle;
        }

        public SampleClass() {
        }
    }

    public static class C {
        private boolean value;

        public C(){
        }

        public C(boolean value) {
            this.value = value;
        }


        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }

    public static class D {
        private Boolean value;

        public D(){}

        public D(Boolean value) {
            this.value = value;
        }

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }
    }

    public static class E {
        private int i;

        public E(){}

        public E(int i) {
            this.i = i;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }
}
