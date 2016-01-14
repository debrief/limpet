package info.limpet.rcp;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import info.limpet.UIProperty;
import info.limpet.rcp.data_provider.data.ReflectivePropertySource;
import junit.framework.TestCase;

/**
 * Test visibility evaluation of properties .
 */
public class TestReflectivePropertySourceVisibility extends TestCase
{

  public void testStateVisibility()
  {
    assertFalse(new TestSubject(null).containsProperty("state"));
    assertFalse(new TestSubject("Belgium").containsProperty("state"));
    assertTrue(new TestSubject("USA").containsProperty("state"));
  }

  public void testMonthlyPensionVisibility()
  {
    assertFalse(new TestSubject(Gender.FEMALE, 55).containsProperty(
        "monthlyPension"));
    assertTrue(new TestSubject(Gender.FEMALE, 60).containsProperty(
        "monthlyPension"));
    assertFalse(new TestSubject(Gender.MALE, 60).containsProperty(
        "monthlyPension"));
    assertTrue(new TestSubject(Gender.MALE, 65).containsProperty(
        "monthlyPension"));
  }

  enum Gender
  {
    MALE, FEMALE
  };

  public static class TestSubject
  {

    private String country;

    private String state;

    private Gender gender;

    private int age;

    private double monthlyPension;

    public TestSubject(String country)
    {
      this.country = country;
    }

    public TestSubject(Gender gender, int age)
    {
      this.gender = gender;
      this.age = age;
    }

    public String getCountry()
    {
      return country;
    }

    public void setCountry(String country)
    {
      this.country = country;
    }

    /**
     * String literals can also be enclosed with double quotes, which needs to be escaped with
     * backslash in that case, for example:
     * 
     * <pre>
     * visibleWhen = "country != null && country.equalsIgnoreCase(\"USA\")"
     * </pre>
     * 
     * is also a valid declaration
     * 
     * @return
     */
    @UIProperty(name = "State",
        visibleWhen = "country != null && country.equalsIgnoreCase('USA')")
    public String getState()
    {
      return state;
    }

    public void setState(String state)
    {
      this.state = state;
    }

    public int getAge()
    {
      return age;
    }

    public void setAge(int age)
    {
      this.age = age;
    }

    public Gender getGender()
    {
      return gender;
    }

    public void setGender(Gender gender)
    {
      this.gender = gender;
    }

    /**
     * Enumeration literals are defined the same way String literals are defined. Equality check is
     * done using "=="
     * 
     * @return
     */
    @UIProperty(name = "Monthly Pension",
        visibleWhen = "age >= 65 && gender == 'MALE' || age >= 60 && gender == 'FEMALE'")
    public double getMonthlyPension()
    {
      return monthlyPension;
    }

    public void setMonthlyPension(double monthlyPension)
    {
      this.monthlyPension = monthlyPension;
    }

    boolean containsProperty(String id)
    {
      IPropertyDescriptor[] descriptors = new ReflectivePropertySource(this)
          .getPropertyDescriptors();
      for (IPropertyDescriptor descriptor : descriptors)
      {
        if (descriptor.getId().equals(id))
        {
          return true;
        }
      }
      return false;
    }
  }
}
