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

import static is.siminn.asgard.objectmapping.Mappings.*;
import is.siminn.asgard.objectmapping.transformation.StringToIntegerTransformation;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Test
public class MappingsTest {

    @BeforeMethod
    public void setup() {
        flush();
    }


    public void should_map_one_property_from_src_object_into_another_property_in_destination() {

        Customer srcObj = src(Customer.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getCity()).to(destObj).setCity(null);

        Customer customerSrc = new Customer();
        customerSrc.setCity("city 17");
        Customer customerDest = new Customer();
        transform(customerSrc, customerDest);
        assertEquals(customerDest.getCity(), "city 17");
    }

    public void should_map_two_properties_from_source_to_destination() {

        Customer srcObj = src(Customer.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getCity()).to(destObj).setCity(null);
        from(srcObj.getPostalcode()).to(destObj).setPostalcode(null);

        Customer customerSrc = new Customer();
        customerSrc.setCity("city 17");
        customerSrc.setPostalcode("101");
        Customer customerDest = new Customer();
        transform(customerSrc, customerDest);
        assertEquals(customerDest.getCity(), "city 17");
        assertEquals(customerDest.getPostalcode(), "101");
    }

    public void should_clear_mappings_on_flush() {

        Customer srcObj = src(Customer.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getCity()).to(destObj).setCity(null);
        from(srcObj.getPostalcode()).to(destObj).setPostalcode(null);

        Customer customerSrc = new Customer();
        customerSrc.setCity("city 17");
        customerSrc.setPostalcode("101");
        Customer customerDest = new Customer();

        flush();

        transform(customerSrc, customerDest);

        assertNull(customerDest.getCity());
        assertNull(customerDest.getPostalcode());
    }

    public void finalize_method_does_not_get_recorded_as_part_of_the_transformation() {

        initMapping();

        System.gc();

        Customer customerSrc = new Customer();
        customerSrc.setCity("city 17");
        customerSrc.setPostalcode("101");
        Customer customerDest = new Customer();
        transform(customerSrc, customerDest);
        assertEquals(customerDest.getCity(), "city 17");
        assertEquals(customerDest.getPostalcode(), "101");
    }


    private void initMapping() {
        Customer srcObj = src(Customer.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getCity()).to(destObj).setCity(null);
        from(srcObj.getPostalcode()).to(destObj).setPostalcode(null);
    }

    public void should_apply_mapping_appropriate_for_the_class() {

        CustomerLocal otherSrcObj = src(CustomerLocal.class);
        Customer srcObj = src(Customer.class);
        Customer destObj = dest(Customer.class);

        from(otherSrcObj.getCity()).to(destObj).setCustomertype(null);
        from(srcObj.getCity()).to(destObj).setCity(null);
        from(srcObj.getPostalcode()).to(destObj).setPostalcode(null);

        Customer customerSrc = new Customer();
        customerSrc.setCity("city 17");
        customerSrc.setPostalcode("101");

        Customer customerDest = new Customer();

        transform(customerSrc, customerDest);

        assertEquals(customerDest.getCity(), "city 17");
        assertEquals(customerDest.getPostalcode(), "101");
        assertNull(customerDest.getCustomertype());
    }

    public void should_apply_mapping_appropriate_for_a_subclass_when_that_class_is_registered_in_mappings() {

        CustomerLocal otherSrcObj = src(CustomerLocal.class);
        Customer srcObj = src(Customer.class);
        Customer destObj = dest(Customer.class);

        from(otherSrcObj.getCity()).to(destObj).setCustomertype(null);
        from(srcObj.getCity()).to(destObj).setCity(null);
        from(srcObj.getPostalcode()).to(destObj).setPostalcode(null);

        Customer customerSrc = new Customer();
        customerSrc.setCity("city 17");
        customerSrc.setPostalcode("101");

        CustomerSubClass customerDest = new CustomerSubClass();

        transform(customerSrc, customerDest);

        assertEquals(customerDest.getCity(), "city 17");
        assertEquals(customerDest.getPostalcode(), "101");
        assertNull(customerDest.getCustomertype());
    }


    public void should_apply_transformation() {
        Customer srcObj = src(Customer.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getCity()).applyTransformation(new SimpleTransformation()).to(destObj).setCity(null);

        Customer customerSrc = new Customer();
        customerSrc.setCity(" city 17 ");
        Customer customerDest = new Customer();


        transform(customerSrc, customerDest);

        assertEquals(customerDest.getCity(), "city 17");
    }

    public void should_skip_transformation_if_the_filter_does_not_approve() {
        Customer srcObj = src(Customer.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getCity()).applyFilter(new Filter<String>() {
            public boolean allow(String input, Object context) {
                return false;
            }
        }).to(destObj).setCity(null);

        Customer customerSrc = new Customer();
        customerSrc.setCity("city 17");
        Customer customerDest = new Customer();


        transform(customerSrc, customerDest);

        assertNull(customerDest.getCity());

    }

    public void should_omit_object_from_collection_if_the_filter_does_not_approve() {
        Customer srcObj = src(Customer.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getAccounts()).applyCollectionFilter(new Filter<Account>() {
            public boolean allow(Account input, Object context) {
                return input.getAccountNo().equals("1");
            }
        }).to(Account.class, destObj.getAccounts());

        Account account1 = new Account();
        account1.setAccountNo("1");

        Account account2 = new Account();
        account2.setAccountNo("2");

        Customer customerSrc = new Customer();
        customerSrc.getAccounts().add(account1);
        customerSrc.getAccounts().add(account2);
        Customer customerDest = new Customer();


        transform(customerSrc, customerDest);

        assertEquals(customerDest.getAccounts().size(),1);
        assertEquals(customerDest.getAccounts().get(0).getAccountNo(),"1");

    }

    public void should_not_modify_destination_object_if_source_is_null() {
        Customer srcObj = src(Customer.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getCity()).to(destObj).setCity(null);

        Customer customerDest = new Customer();
        customerDest.setCity("city 17");

        transform(null, customerDest);

        assertEquals(customerDest.getCity(), "city 17");

    }

    public void should_not_throw_if_destination_object_is_null() {
        Customer srcObj = src(Customer.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getCity()).to(destObj).setCity(null);

        Customer customerSrc = new Customer();
        customerSrc.setCity("city 17");

        transform(customerSrc, null);

        assertEquals(customerSrc.getCity(), "city 17");

    }

    public void should_map_src_object_to_many_fields_on_dest_object() {
        CustomerLocal srcObj = src(CustomerLocal.class);
        Customer intermediate = dest(Customer.class);
        Account destination = dest(Account.class);

        from(srcObj.getCity()).to(intermediate).setCity(null);
        from(srcObj).to(destination).setCustomer(null);

        CustomerLocal customerSrc = new CustomerLocal();
        customerSrc.setCity("city 17");

        final Account dest = new Account();
        transform(customerSrc, dest);

        assertEquals(dest.getCustomer().getCity(), "city 17");

    }

    public void should_map_src_object_then_applyTransformations_to_dest_method() {
        CustomerLocal srcObj = src(CustomerLocal.class);
        Customer intermediate = dest(Customer.class);

        from(srcObj).applyTransformation(new Transformation<CustomerLocal>() {
            public Object apply(CustomerLocal input, Object context) {
                return "the city";
            }
        }).to(intermediate).setCity(null);

        CustomerLocal customerSrc = new CustomerLocal();

        final Customer dest = new Customer();
        transform(customerSrc, dest);

        assertEquals(dest.getCity(), "the city");

    }

    public void should_auto_trim_all_String_values_in_destinations() {
        CustomerLocal srcObj = src(CustomerLocal.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getCity()).to(destObj).setCity(null);

        CustomerLocal customerSrc = new CustomerLocal();
        customerSrc.setCity(" city 17 ");

        final Customer dest = new Customer();
        transform(customerSrc, dest);

        assertEquals(dest.getCity(), "city 17");

    }

    public void transform_should_return_the_destination_object() {
        CustomerLocal srcObj = src(CustomerLocal.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getCity()).to(destObj).setCity(null);

        CustomerLocal customerSrc = new CustomerLocal();
        customerSrc.setCity(" city 17 ");

        final Customer dest = new Customer();
        Customer dest2 = transform(customerSrc, dest);

        assertSame(dest2, dest);
    }

    public void should_map_typed_collection_using_mapping_of_collected_object_class(){
        Customer dest = dest(Customer.class);
        CustomerType2 src = src(CustomerType2.class);

        from(src.getAccounts()).to(Account.class, dest.getAccounts());

        Account destAccount = dest(Account.class);
        AccountType2 srcAccountType = src(AccountType2.class);

        from(srcAccountType.getAccountNo()).to(destAccount).setAccountNo(null);

        Customer destObj = new Customer();
        CustomerType2 srcObj = new CustomerType2();
        srcObj.getAccounts().add(new AccountType2("ble"));
        srcObj.getAccounts().add(new AccountType2("bla"));

        transform(srcObj, destObj);
        
        assertEquals(destObj.getAccounts().size(),2, "Destination object should have same number of items in the collection");
        List<Account> list = destObj.getAccounts();
        assertEquals(list.get(0).getAccountNo(),"ble", "Should be ble");
        assertEquals(destObj.getAccounts().get(1).getAccountNo(),"bla", "Should be bla");
    }


    @Test(enabled = false)
    public void should_map_properties_on_name_on_similar_objects() {
        Customer destObj = dest(Customer.class);
        CustomerType2 srcObj = src(CustomerType2.class);

        from(srcObj).toObject(destObj);

        final Customer dest = new Customer();
        CustomerType2 src = new CustomerType2();
        src.setCity("Reykjavik");

        transform(src, dest);
        assertEquals(dest.getCity(), src.getCity());
    }

    public void should_return_src_object_when_calling_transform_and_there_are_no_mappings_for_src_and_destinatioan_and_they_are_the_same_type() {
        final String src = "src";
        final String dest = "dest";
        assertSame(transform(src, dest), src);
    }

    enum TestEnum {a, b}

    public void should_process_enum_value_normally_as_part_of_transformation(){
        EnumClassParams srcObj = src(EnumClassParams.class);
        EnumClassParams destObj = dest(EnumClassParams.class);

        from(srcObj.getA()).to(destObj).setA(null);
        final EnumClassParams enumClassParamsSrc = new EnumClassParams();
        enumClassParamsSrc.setA(TestEnum.b);
        EnumClassParams destt = transform(enumClassParamsSrc, new EnumClassParams());
        assertEquals(destt.getA(), TestEnum.b);
    }


    @Test
    public void should_report_exact_field_in_exception_on_a_transformation_failure() {


        CustomerLocal srcObj = src(CustomerLocal.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getCity()).applyTransformation(new StringToIntegerTransformation()).to(destObj).setCity(null);

        CustomerLocal customerSrc = new CustomerLocal();
        customerSrc.setCity(" city 17 ");

        final Customer dest = new Customer();
        try {
            transform(customerSrc, dest);
            fail("Expecting an exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().indexOf("getCity")>=0, "Actual message: " + e.getMessage());
            assertTrue(e.getMessage().indexOf("setCity")>=0, "Actual message: " + e.getMessage());
        }
    }

    @Test
    public void should_pass_context_information_along_into_ContextTransformation() {
        CustomerLocal srcObj = src(CustomerLocal.class);
        Customer destObj = dest(Customer.class);

        final Map projectionContext = new HashMap();

        from(srcObj.getCity()).applyTransformation(new ContextTransformation<String>(){
            public Object apply(TransformationContext<String> stringTransformationContext) {
                assertSame(stringTransformationContext.getParams(), projectionContext);
                return null;
            }
        }).to(destObj).setCity(null);

        CustomerLocal customerSrc = new CustomerLocal();
        customerSrc.setCity(" city 17 ");

        final Customer dest = new Customer();
        transform(customerSrc, dest, projectionContext);
    }

    @Test
    public void should_pass_transformed_input_on_to_next_transformation_in_a_transformation_chain() {
        CustomerLocal srcObj = src(CustomerLocal.class);
        Customer destObj = dest(Customer.class);

        final Map projectionContext = new HashMap();

        from(srcObj.getCity()).applyTransformation(new ContextTransformation<String>(){
            public Object apply(TransformationContext<String> stringTransformationContext) {
                return "onetransform";
            }
        }).applyTransformation(new ContextTransformation<String>() {
            public Object apply(TransformationContext<String> stringTransformationContext) {
                assertEquals("onetransform", stringTransformationContext.getInput());
                return null;
            }
        }).to(destObj).setCity(null);

        CustomerLocal customerSrc = new CustomerLocal();
        customerSrc.setCity(" city 17 ");

        final Customer dest = new Customer();
        transform(customerSrc, dest, projectionContext);
    }


    @Test
    public void should_transform_null_ProjectionContext_to_an_empty_map() {
        CustomerLocal srcObj = src(CustomerLocal.class);
        Customer destObj = dest(Customer.class);

        from(srcObj.getCity()).applyTransformation(new ContextTransformation<String>(){
            public Object apply(TransformationContext<String> stringTransformationContext) {
                assertEquals(stringTransformationContext.getParams().size(), 0);
                return null;
            }
        }).to(destObj).setCity(null);

        CustomerLocal customerSrc = new CustomerLocal();
        customerSrc.setCity(" city 17 ");

        final Customer dest = new Customer();
        transform(customerSrc, dest, null);
    }

    private class SimpleTransformation implements Transformation<String> {

        public String apply(String input, Object context) {
            return input.trim();
        }
    }

    private class CustomerSubClass extends Customer {
    }

    public static class CustomerType2 {
        private String city;
        private String postalcode;
        private String customertype;
        private List<AccountType2> accounts = new ArrayList<AccountType2>();

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPostalcode() {
            return postalcode;
        }

        public void setPostalcode(String postalcode) {
            this.postalcode = postalcode;
        }

        public void setCustomertype(String customertype) {
            this.customertype = customertype;
        }

        public String getCustomertype() {
            return customertype;
        }

        public List<AccountType2> getAccounts(){
            return accounts;
        }
    }

    public static class AccountType2{
        private String accountNo;

        public AccountType2() {
        }

        public AccountType2(String accountNo) {
            this.accountNo = accountNo;
        }

        public String getAccountNo() {
            return accountNo;
        }

        public void setAccountNo(String accountNo) {
            this.accountNo = accountNo;
        }
    }

    public static class CustomerLocal {
        private String city;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

    }
}
