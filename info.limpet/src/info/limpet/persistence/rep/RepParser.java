package info.limpet.persistence.rep;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;
import info.limpet.IStoreItem;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.LocationDocumentBuilder;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.persistence.FileParser;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.measure.quantity.Velocity;

public class RepParser extends FileParser
{
  private static final DateFormat FOUR_DIGIT_YEAR_FORMAT =
      new SimpleDateFormat("yyyyMMdd HHmmss");

  private static final DateFormat TWO_DIGIT_YEAR_FORMAT = new SimpleDateFormat(
      "yyMMdd HHmmss");

  /**
   * the normal token delimiter (for comma & white-space separated fields)
   */
  static final String normalDelimiters = " \t\n\r\f";
  /**
   * the quoted delimiter, for quoted track names
   */
  static private final String quoteDelimiter = "\"";

  private static interface Item
  {
    public String getName();
  }
  
  public static class CutItem implements Item
  {
    final String _host;
    final String _sensor;
    private Point2D _origin;
    private Object _dtg;
    private Double _brg;
    private Double _brg2;
    private Double _freq;
    private Double _rng;
    public CutItem(final String host, final String sensor, Point2D origin, Date theDtg, Double brg, Double brg2, Double freq, Double rng)
    {
      _host = host;
      _sensor = sensor;
      _origin = origin;
      _dtg = theDtg;
      _brg = brg;
      _brg2 = brg2;
      _freq = freq;
      _rng = rng;
    }
    @Override
    public String getName()
    {
      return _host + "-" + _sensor;
    }
    
  }
  
  public static class FixItem implements Item
  {
    final private Date _date;
    final private Point2D _loc;
    final private double _speedMS;
    final private double _course;
    final private String _name;
    @SuppressWarnings("unused")
    final private String _sym;
    final private double _depth;

    public FixItem(Date theDate, Point2D theLoc, double theCourse,
        double speedMS, double depth, String theTrackName, String symbology)
    {
      _date = theDate;
      _loc = theLoc;
      _course = theCourse;
      _speedMS = speedMS;
      _name = theTrackName;
      _sym = symbology;
      _depth = depth;
    }

    @Override
    public String getName()
    {
      return _name;
    }

  }

  private static class CutGenerator implements Generator
  {
    private LocationDocumentBuilder _origin;
    private NumberDocumentBuilder _bearing;
    private NumberDocumentBuilder _freq;

    public boolean isSingleton()
    {
      final boolean res;
      if(!_origin.getValues().isEmpty())
      {
        res = _origin.getValues().size() == 1;            
      }
      else if(!_bearing.getValues().isEmpty())
      {
        res = _bearing.getValues().size() == 1;            
      }
      else if(!_freq.getValues().isEmpty())
      {
        res = _freq.getValues().size() == 1;            
      }
      else
      {
        res = false;
      }
      return res;
    }

    public CutGenerator(final String name)
    {
//      final String _host;
//      final String _sensor;
//      private Point2D _origin;
//      private Object _dtg;
//      private Double _brg;
//      private Double _brg2;
//      private Double _freq;
//      private Double _rng;
      
      
      // generate the builders
      _origin =
          new LocationDocumentBuilder(name + "-location", null,
              SampleData.MILLIS);
//      _course =
//          new NumberDocumentBuilder(name + "-course", SampleData.DEGREE_ANGLE,
//              null, SampleData.MILLIS);
//      _speed =
//          new NumberDocumentBuilder(name + "-speed", METRE.divide(SECOND)
//              .asType(Velocity.class), null, SampleData.MILLIS);
//      _depth =
//          new NumberDocumentBuilder(name + "-depth", METRE, null,
//              SampleData.MILLIS);
    }

    public void add(Item thisEntry)
    {
      CutItem cut = (CutItem) thisEntry;
//      final long theDate;
//      if (thisEntry._date != null)
//      {
//        theDate = thisEntry._date.getTime();
//      }
//      else
//      {
//        theDate = -1;
//      }
//      _locB.add(theDate, thisEntry._loc);
//      _speed.add(theDate, thisEntry._speedMS);
//      _course.add(theDate, thisEntry._course);
//      _depth.add(theDate, thisEntry._depth);
    }

    @Override
    public void storeTo(StoreGroup group)
    {
      // TODO Auto-generated method stub
      
    }
  }

  // parse the line
  // 951212 050000.000 CARPET @C 12 11 10.63 N 11 41 52.37 W 269.7 2.0 0
  abstract private static interface Generator
  {
    public void add(Item item);

    public void storeTo(StoreGroup group);
  }
  
  private static class TrackGenerator implements Generator
  {
    LocationDocumentBuilder _locB;
    NumberDocumentBuilder _speed;
    NumberDocumentBuilder _course;
    NumberDocumentBuilder _depth;
    final private String _name;
  
    public boolean isSingleton()
    {
      return _speed.getValues().size() == 1;
    }
  
    public TrackGenerator(final String name)
    {
      _name = name;
      // generate the builders
      _locB =
          new LocationDocumentBuilder(name + "-location", null,
              SampleData.MILLIS);
      _course =
          new NumberDocumentBuilder(name + "-course", SampleData.DEGREE_ANGLE,
              null, SampleData.MILLIS);
      _speed =
          new NumberDocumentBuilder(name + "-speed", METRE.divide(SECOND)
              .asType(Velocity.class), null, SampleData.MILLIS);
      _depth =
          new NumberDocumentBuilder(name + "-depth", METRE, null,
              SampleData.MILLIS);
    }
  
    public void add(Item thisE)
    {
      FixItem thisEntry = (FixItem) thisE;
      final long theDate;
      if (thisEntry._date != null)
      {
        theDate = thisEntry._date.getTime();
      }
      else
      {
        theDate = -1;
      }
      _locB.add(theDate, thisEntry._loc);
      _speed.add(theDate, thisEntry._speedMS);
      _course.add(theDate, thisEntry._course);
      _depth.add(theDate, thisEntry._depth);
    }

    @Override
    public void storeTo(StoreGroup group)
    {

      // ok, separate processing if it's a singleton
      // ok, only add the others if they're present
      if (isSingleton())
      {
        // just do the single location
        LocationDocument locDoc = _locB.toDocument();
        group.add(locDoc);
      }
      else
      {
        // ok, create group for this track
        StoreGroup thisTrack = new StoreGroup(_name);

        // now add the constituents
        thisTrack.add(_locB.toDocument());
        thisTrack.add(_speed.toDocument());
        thisTrack.add(_course.toDocument());
        thisTrack.add(_depth.toDocument());
        group.add(thisTrack);
      }

    }
  }

  @Override
  public List<IStoreItem> parse(String filePath) throws IOException
  {
    final List<IStoreItem> res = new ArrayList<IStoreItem>();
    final File inFile = new File(filePath);
    final Reader in =
        new InputStreamReader(new FileInputStream(inFile), Charset
            .forName("UTF-8"));
    final String fullFileName = inFile.getName();
    final String fileName = filePrefix(fullFileName);
    final StoreGroup group = new StoreGroup(fileName);
    res.add(group);

    final Map<String, Generator> builders =
        new HashMap<String, Generator>();

    // ok, loop through the data
    BufferedReader br = null;
    try
    {
      br = new BufferedReader(in);
      for (String line; (line = br.readLine()) != null;)
      {
        Item thisE = null;

        // have a look at the line
        if (line.startsWith(";;"))
        {
          // comment: ignore
        }
        else if (line.startsWith(";TEXT:"))
        {
          thisE = parseThisLabel(line);
        }
        else if (line.startsWith(";SENSOR2:"))
        {
          thisE = parseThisSensor2(line);
        }
        else if (line.startsWith(";"))
        {
          // it must be some other kind of entry, ignore
        }
        else
        {
          thisE = parseThisRepLine(line);
        }

        if (thisE != null)
        {
          final String name = thisE.getName();

          // do we know this track already?
          Generator thisG = builders.get(name);

          if (thisG == null)
          {
            thisG = builderFor(thisE, name);
            builders.put(name, thisG);
          }

          // ok, submit the new line
          thisG.add(thisE);
        }
      }
    }
    finally
    {
      if (br != null)
      {
        br.close();
      }
    }

    // ok, store handle the data
    for (String name : builders.keySet())
    {
      Generator thisGen = builders.get(name);
      
      thisGen.storeTo(group);
    }

    return res;

  }

  private Generator builderFor(Item thisE, String name)
  {
    final Generator thisG;
    if (thisE instanceof FixItem)
    {
      thisG = new TrackGenerator(name);
    }
    else if(thisE instanceof CutItem)
    {
      thisG = new CutGenerator(name);
    }
    else
    {
      thisG = null;
    }

    return thisG;
  }

  /**
   * parse a date string using our format
   */
  private static Date parseThis(final String rawText)
  {
    Date date = null;
    Date res = null;

    // right, start off by trimming spaces off the date
    final String theRawText = rawText.trim();

    String secondPart = theRawText;
    String subSecondPart = null;

    // start off by seeing if we have sub-second date
    final int subSecondIndex = theRawText.indexOf('.');
    if (subSecondIndex > 0)
    {
      // so, there is a separator - extract the text before the separator
      secondPart = theRawText.substring(0, subSecondIndex);

      // just check that the '.' isn't the last character
      if (subSecondIndex < theRawText.length() - 1)
      {
        // yes, we do have digits after the separator
        subSecondPart = theRawText.substring(subSecondIndex + 1);
      }
    }

    // next determine if we have a 4-figure year value (in which case the
    // space will be in column 9
    final int spaceIndex = secondPart.indexOf(" ");

    try
    {
      if (spaceIndex > 6)
      {
        date = FOUR_DIGIT_YEAR_FORMAT.parse(secondPart);
      }
      else
      {
        date = TWO_DIGIT_YEAR_FORMAT.parse(secondPart);
      }
    }
    catch (final ParseException e1)
    {
      e1.printStackTrace();
    }

    int millis = 0;

    // do we have a sub-second part?
    if (subSecondPart != null)
    {
      // get the value
      millis = Integer.parseInt(subSecondPart);
    }

    if (millis != -1 && date != null)
    {
      res = new Date(date.getTime() + millis);
    }
    else
    {
      res = date;
    }

    return res;
  }

  private static String checkForQuotedName(final StringTokenizer st,
      String theName)
  {
    // so, does the track name contain a quote character?
    final int quoteIndex = theName.indexOf("\"");
    if (quoteIndex >= 0)
    {
      // aah, but, we may have just read in all of the item. just check if
      // the
      // token contains
      // both speech marks...
      final int secondQuoteIndex = theName.indexOf("\"", quoteIndex + 1);

      if (secondQuoteIndex >= 0)
      {
        // yes, we have caught both quotes
        // just trim off the quote marks
        theName = theName.substring(1, theName.length() - 1);
      }
      else
      {
        // no, we just caught the first quote.
        // fish around for the second one.

        String lastPartOfName = st.nextToken(quoteDelimiter);

        // yup. the ne
        theName += lastPartOfName;

        // and trim away the quote
        theName = theName.substring(theName.indexOf("\"") + 1);

        // consume the trailing quote delimiter (note - we allow spaces
        // & tabs)
        lastPartOfName = st.nextToken(" \t");
      }
    }
    return theName;
  }

  private static String padToken(final String token)
  {
    final String res;
    if (token.length() == 6)
    {
      res = token;
    }
    else
    {
      final int numMissing = 6 - token.length();
      final StringBuffer buffer = new StringBuffer(6);
      for (int i = 0; i < numMissing; i++)
      {
        buffer.append("0");
      }
      buffer.append(token);
      res = buffer.toString();
    }
    return res;
  }

  /**
   * parse a date string using our format
   */
  private static Date parseThis(final String dateToken, final String timeToken)
  {
    // do we have millis?
    final int decPoint = timeToken.indexOf(".");
    String milliStr, timeStr;
    if (decPoint > 0)
    {
      milliStr = timeToken.substring(decPoint, timeToken.length());
      timeStr = timeToken.substring(0, decPoint);
    }
    else
    {
      milliStr = "";
      timeStr = timeToken;
    }

    // sort out if we have to padd
    // check the date for missing leading zeros
    final String theDateToken = padToken(dateToken);
    timeStr = padToken(timeStr);

    final String composite = theDateToken + " " + timeStr + milliStr;

    return parseThis(composite);
  }

  private static FixItem parseThisRepLine(final String line)
  {
    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(line);

    // declare local variables
    Point2D theLoc;
    double latDeg, longDeg, latMin, longMin;
    char latHem, longHem;
    double latSec, longSec;
    Date theDate = null;
    double theCourse;
    double speedMS;
    double theDepth;

    String theTrackName;

    // check it's not an empty line
    if (!st.hasMoreTokens())
      return null;

    // parse the line
    // 951212 050000.000 CARPET @C 12 11 10.63 N 11 41 52.37 W 269.7 2.0 0

    // combine the date, a space, and the time
    final String dateToken = st.nextToken();
    final String timeToken = st.nextToken();

    // and extract the date
    theDate = parseThis(dateToken, timeToken);

    // trouble - the track name may have been quoted, in which case we will
    // pull
    // in the remaining fields aswell
    theTrackName = checkForQuotedName(st, st.nextToken()).trim();

    final String symbology = st.nextToken(normalDelimiters);

    latDeg = Double.parseDouble(st.nextToken());
    latMin = Double.parseDouble(st.nextToken());
    latSec = Double.parseDouble(st.nextToken());

    /**
     * now, we may have trouble here, since there may not be a space between the hemisphere
     * character and a 3-digit latitude value - so BE CAREFUL
     */
    final String vDiff = st.nextToken();
    if (vDiff.length() > 3)
    {
      // hmm, they are combined
      latHem = vDiff.charAt(0);
      final String secondPart = vDiff.substring(1, vDiff.length());
      longDeg = Double.parseDouble(secondPart);
    }
    else
    {
      // they are separate, so only the hem is in this one
      latHem = vDiff.charAt(0);
      longDeg = Double.parseDouble(st.nextToken());
    }
    longMin = Double.parseDouble(st.nextToken());
    longSec = Double.parseDouble(st.nextToken());
    longHem = st.nextToken().charAt(0);

    // parse (and convert) the vessel status parameters
    theCourse = Double.parseDouble(st.nextToken());
    speedMS = Double.valueOf(st.nextToken()) * 0.514444;

    // get the depth value
    final String depthStr = st.nextToken();

    // we know that the Depth str may be NaN, but Java can interpret this
    // directly
    if (depthStr.equals("NaN"))
      theDepth = Double.NaN;
    else
      theDepth = Double.parseDouble(depthStr);

    // NEW FEATURE: we take any remaining text, and use it as a label
    String txtLabel = null;
    if (st.hasMoreTokens())
      txtLabel = st.nextToken("\r");
    if (txtLabel != null)
      txtLabel = txtLabel.trim();

    // create the tactical data
    theLoc =
        new Point2D.Double(toDegs(longDeg, longMin, longSec, longHem), toDegs(
            latDeg, latMin, latSec, latHem));

    return new FixItem(theDate, theLoc, theCourse, speedMS, theDepth,
        theTrackName, symbology);
  }

  private static double toDegs(double degs, double mins, double secs, char hem)
  {
    double res = degs + mins / 60 + secs / (60 * 60);
    if (hem == 'S' || hem == 'W')
    {
      res = -res;
    }
    return res;
  }

  private FixItem parseThisLabel(final String line)
  {
    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(line);

    // declare local variables
    Point2D theLoc;
    double latDeg, longDeg, latMin, longMin;
    char latHem, longHem;
    double latSec, longSec;
    Date theDate = null;

    String theTrackName = "UNKNOWN";

    // check it's not an empty line
    if (!st.hasMoreTokens())
      return null;

    // parse the line
    // ;TEXT: @E 21.7 0 0 N 21.5 0 0 W test text

    // skip the label
    st.nextToken();

    // skip the symbology
    String symbology = st.nextToken();

    // now the location
    latDeg = Double.parseDouble(st.nextToken());
    latMin = Double.parseDouble(st.nextToken());
    latSec = Double.parseDouble(st.nextToken());

    /**
     * now, we may have trouble here, since there may not be a space between the hemisphere
     * character and a 3-digit latitude value - so BE CAREFUL
     */
    final String vDiff = st.nextToken();
    if (vDiff.length() > 3)
    {
      // hmm, they are combined
      latHem = vDiff.charAt(0);
      final String secondPart = vDiff.substring(1, vDiff.length());
      longDeg = Double.parseDouble(secondPart);
    }
    else
    {
      // they are separate, so only the hem is in this one
      latHem = vDiff.charAt(0);
      longDeg = Double.parseDouble(st.nextToken());
    }
    longMin = Double.parseDouble(st.nextToken());
    longSec = Double.parseDouble(st.nextToken());
    longHem = st.nextToken().charAt(0);

    // use the label as the track name
    if (st.hasMoreTokens())
      theTrackName = st.nextToken("\r");
    if (theTrackName != null)
      theTrackName = theTrackName.trim();

    // create the tactical data
    theLoc =
        new Point2D.Double(toDegs(longDeg, longMin, longSec, longHem), toDegs(
            latDeg, latMin, latSec, latHem));

    return new FixItem(theDate, theLoc, 0, 0, 0, theTrackName, symbology);
  }

  private CutItem parseThisSensor2(final String line)
  {

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(line);

    // declare local variables
    String theText;
    String theTrack;
    String sensorName;
    double latDeg, longDeg, latMin, longMin;
    char latHem, longHem;
    double latSec, longSec;
    Point2D origin = null;
    Date theDtg = null;
    Double brg = null;
    Double rng = null;
    Double brg2 = null, freq = null;
    java.awt.Color theColor;

    // skip the comment identifier
    st.nextToken();

    // combine the date, a space, and the time
    final String dateToken = st.nextToken();
    final String timeToken = st.nextToken();

    // and extract the date
    theDtg = parseThis(dateToken, timeToken);

    // get the (possibly multi-word) track name
    theTrack = checkForQuotedName(st, st.nextToken());

    // start with the symbology
    st.nextToken(normalDelimiters);

    // now the sensor offsets
    final String next = st.nextToken().trim();
    // find out if it's our null value
    if (next.startsWith("N"))
    {
      // ditch it,
    }
    else
    {

      // get the deg out of this value
      latDeg = Double.parseDouble(next);

      // ok, this is valid data, persevere with it
      latMin = Double.parseDouble(st.nextToken());
      latSec = Double.parseDouble(st.nextToken());

      /**
       * now, we may have trouble here, since there may not be a space between the hemisphere
       * character and a 3-digit latitude value - so BE CAREFUL
       */
      final String vDiff = st.nextToken();
      if (vDiff.length() > 3)
      {
        // hmm, they are combined
        latHem = vDiff.charAt(0);
        final String secondPart = vDiff.substring(1, vDiff.length());
        longDeg = Double.parseDouble(secondPart);
      }
      else
      {
        // they are separate, so only the hem is in this one
        latHem = vDiff.charAt(0);
        longDeg = Double.parseDouble(st.nextToken());
      }

      longMin = Double.parseDouble(st.nextToken());
      longSec = Double.parseDouble(st.nextToken());
      longHem = st.nextToken().charAt(0);

      // create the origin
      origin =
          new Point2D.Double(toDegs(longDeg, longMin, longSec, longHem),
              toDegs(latDeg, latMin, latSec, latHem));
    } // whether the duff origin data was entered

    // get the bearing
    final String brgStr = st.nextToken();
    if (!brgStr.startsWith("N"))
    {
      // cool, we have data
      brg = new Double(Double.parseDouble(brgStr));
    }

    // and now get the ambiguous bearing
    String tmp = st.nextToken();
    if (!tmp.startsWith("N"))
    {
      // cool, we have data
      brg2 = new Double(Double.parseDouble(tmp));
    }

    // and the frequency
    tmp = st.nextToken();
    if (!tmp.startsWith("N"))
    {
      // cool, we have data
      freq = new Double(Double.parseDouble(tmp));
    }

    // and the range
    tmp = st.nextToken();
    if (!tmp.startsWith("N"))
    {
      // cool, we have data
      double rngYds = Double.parseDouble(tmp);
      rng = rngYds * 0.9144;
    }

    // get the (possibly multi-word) track name
    sensorName = checkForQuotedName(st, st.nextToken());

    // trim the sensor name
    sensorName = sensorName.trim();

    // and lastly read in the message (to the end of the line)
    String labelTxt = st.nextToken("\r");

    // did we find anything
    if (labelTxt != null)
    {
      theText = labelTxt.trim();
    }
    else
    {
      // nothing found, use empty string
      theText = "";
    }

    return new CutItem(theTrack, sensorName, origin, theDtg, brg, brg2, freq,
        rng);
    
  }

}
