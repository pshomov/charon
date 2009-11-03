package is.siminn.asgard.objectmapping;

import static is.siminn.asgard.objectmapping.Mappings.*;
import is.siminn.asgard.objectmapping.helper_objects.O1;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

@Test
public class MappingErrorDetectionTest {
    public void should_not_accept_dest_object_as_parameter_to_from() {
        MappingsTest.CustomerLocal srcObj = src(MappingsTest.CustomerLocal.class);
        Customer intermediate = dest(Customer.class);
        Account destination = dest(Account.class);

        from(srcObj.getCity()).to(intermediate).setCity(null);
        try {
            from(destination).to(destination).setCustomer(null);
            fail("should not get here");
        } catch (MappingException e) {
            // good
        }
    }

    public void should_not_accept_src_object_as_parameter_to_to(){
        O1 srcObj =  src(O1.class);
        try {
            from(srcObj.getName()).to(srcObj).setName(null);
            fail("should not get here");
        } catch (MappingException e) {
            // good
        }
    }
}
