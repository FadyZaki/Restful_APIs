package org.crowdlib.main
import java.security.Principal;
import org.crowdlib.main.MyResource
import spock.lang.Specification
import javax.ws.rs.core.SecurityContext;

/**
 * This simple test class demonstrates how the classes that implement a RESTful 
 * API can be tested using Spock. Since they are simple POJOs it is not necessary
 * to fire up the web application container in order to test them. Of course, this 
 * does not test that the annotations are correct, so to test the RESTful API as
 * a whole, a higher level test is still needed.
 * 
 * @author alex.voss@st-andrews.ac.uk
 *
 */
class MyResourceSpec extends Specification {

  SecurityContext mockSecurityContext = Mock(SecurityContext)
  Principal mockPrincipal = Mock(Principal)
  MyResource myResource = new MyResource()
  
  void setup() {
    myResource.setSecurityContext(mockSecurityContext)
  }
  
  void "when getIt() is called it should get the name of the user from the SecurityContext"() {    
    when:
      String result = myResource.getIt();  
    then:
      1 * mockSecurityContext.getUserPrincipal() >> mockPrincipal
      1 * mockPrincipal.getName() >> "Lady Gaga"
      result == "Got it! Thanks, Lady Gaga"
  }
}
