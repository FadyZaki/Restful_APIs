package org.crowdlib.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import org.junit.Before;
import org.junit.Test;

/**
 * This simple test class demonstrates how the classes that implement a RESTful API can be tested
 * using plain JUnit. Since they are simple POJOs it is not necessary to fire up the web application
 * container in order to test them. Of course, this does not test that the annotations are correct,
 * so to test the RESTful API as a whole, a higher level test is still needed.
 * 
 * @author alex.voss@st-andrews.ac.uk
 *
 */
public class MyResourceTests {

  MyResource myResource = new MyResource();

  @Before
  public void setup() {

  }

  @Test
  public void whenGetItIsCalledItShouldGetTheNameOfTheUserFromTheSecurityContext() {
    // given:
    MockPrincipal principal = new MockPrincipal() {        
          @Override
          public String getName() {
            this.called = true;
            return "Lady Gaga";
          }          
    };
    MockSecurityContext securityContext = new MockSecurityContext() {
      public Principal getUserPrincipal() {
        this.called = true;
        return principal;
      }
    };
    this.myResource.setSecurityContext(securityContext);
    // when:
    String result = this.myResource.getIt();
    // then:
    assertTrue(securityContext.called);
    assertTrue(principal.called);
    assertEquals("Got it! Thanks, Lady Gaga",result);
  }

  // Mock Principal implementation class. This has no method implementations
  // since by leaving the only method declared in Principal abstract we can
  // force this being implemented in the test itself.
  private abstract class MockPrincipal implements Principal {
    protected boolean called = false;
  }

  private abstract class MockSecurityContext implements SecurityContext {

    protected boolean called = false;

    @Override
    public boolean isUserInRole(String role) {
      return false;
    }

    @Override
    public boolean isSecure() {
      return false;
    }

    @Override
    public String getAuthenticationScheme() {
      return null;
    }

  }
}
