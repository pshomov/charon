package is.siminn.asgard.objectmapping;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static is.siminn.asgard.objectmapping.Mappings.*;
import is.siminn.asgard.objectmapping.helper_objects.O1;
import is.siminn.asgard.objectmapping.helper_objects.O2;


@Test
public class AutoMappingTest {
    public void should_automatically_map_fields_with_the_same_name_and_same_type(){
        O1 src = src(O1.class);
        O2 dest = dest(O2.class);

        automap(src,dest);
        final O2 o2 = transform(new O1("fle"), new O2());
        assertEquals(o2.getName(),  "fle");
    }
}
