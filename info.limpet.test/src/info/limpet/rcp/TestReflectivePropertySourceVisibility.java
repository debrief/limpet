package info.limpet.rcp;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import info.limpet.UIProperty;
import info.limpet.rcp.data_provider.data.ReflectivePropertySource;
import junit.framework.TestCase;

/**
 * Test visibility evaluation of properties. Full syntax of JEXL available here:
 * http://commons.apache.org/proper/commons-jexl/reference/syntax.html
 */
public class TestReflectivePropertySourceVisibility extends TestCase
{

  public void testStateVisibility()
  {
    assertFalse(new TestSubject1(null).containsProperty("state"));
    assertFalse(new TestSubject1("Belgium").containsProperty("state"));
    assertTrue(new TestSubject1("USA").containsProperty("state"));
  }

  public void testMonthlyPensionVisibility()
  {
    assertFalse(new TestSubject2(Gender.FEMALE, 55).containsProperty(
        "monthlyPension"));
    assertTrue(new TestSubject2(Gender.FEMALE, 60).containsProperty(
        "monthlyPension"));
    assertFalse(new TestSubject2(Gender.MALE, 60).containsProperty(
        "monthlyPension"));
    assertTrue(new TestSubject2(Gender.MALE, 65).containsProperty(
        "monthlyPension"));
  }

  public void testSecondLineVisibility()
  {
    String[] line1 = new String[]
    {"First", " ", "line."};
    assertFalse(new TestSubject3(line1).containsProperty("secondLine"));

    line1 = new String[]
    {"First", " ", "line,"};
    assertTrue(new TestSubject3(line1).containsProperty("secondLine"));
  }

  enum Gender
  {
    MALE, FEMALE
  };

  static class BaseTestSubject
  {
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

  public static class TestSubject1 extends BaseTestSubject
  {

    private String country;

    private String state;

    public TestSubject1(String country)
    {
      this.country = country;
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

  }

  public static class TestSubject2 extends BaseTestSubject
  {
    private Gender gender;

    private int age;

    private double monthlyPension;

    public TestSubject2(Gender gender, int age)
    {
      this.gender = gender;
      this.age = age;
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

  }

  /**
   * A test subject to demonstrate array access, empty and size functions.
   */
  public static class TestSubject3 extends BaseTestSubject
  {
    private String[] firstLine;

    /**
     * Second line shall only be visible if the last word in the first line does not end with period
     */
    private String[] secondLine;

    private TestSubject3(String[] firstLine)
    {
      this.firstLine = firstLine;
    }

    @UIProperty(name = "First line")
    public String[] getFirstLine()
    {
      return firstLine;
    }

    @UIProperty(name = "Second line",
        visibleWhen = "!empty(firstLine) && !(firstLine[size(firstLine)-1] =$ '.')")
    public String[] getSecondLine()
    {
      return secondLine;
    }

  }
}
