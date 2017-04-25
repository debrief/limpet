/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.ui.range_slider;

import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.Range;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.arithmetic.SimpleMovingAverageOperation.SimpleMovingAverageCommand;
import info.limpet.stackedcharts.ui.editor.Activator;
import info.limpet.ui.core_view.CoreAnalysisView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.measure.unit.Unit;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;

/**
 * display analysis overview of selection
 * 
 * @author ian
 * 
 */
public class RangeSliderView extends CoreAnalysisView implements
    IChangeListener
{

  /**
   * helper class, to embody three types of data we use in range control
   * 
   * @author ian
   * 
   */
  private static interface RangeHelper
  {
    void updatedTo(int val);

    public String getMinText();

    public String getMaxText();

    public int getMinVal();

    public int getMaxVal();

    public String getValueText();

    String getLabel();

    int getValue();

    /**
     * stop listening to the subject item
     * 
     */
    void dropListener();

  }

  private static class CommandHelper implements RangeHelper, IChangeListener
  {

    private final SimpleMovingAverageCommand _myCommand;
    private final int _sliderThumb;

    public CommandHelper(SimpleMovingAverageCommand command, int sliderThumb)
    {
      _myCommand = command;
      _sliderThumb = sliderThumb;
      _myCommand.addChangeListener(this);
    }

    @Override
    public void updatedTo(int val)
    {
      _myCommand.setWindowSize(val);
      _myCommand.recalculate(null);

    }

    @Override
    public String getMinText()
    {
      return "0";
    }

    @Override
    public String getMaxText()
    {
      return "50";
    }

    @Override
    public int getMinVal()
    {
      return 0;
    }

    @Override
    public int getMaxVal()
    {
      return 50 + _sliderThumb;
    }

    @Override
    public String getValueText()
    {
      return "" + getValue();
    }

    @Override
    public String getLabel()
    {
      return _myCommand.getName();
    }

    @Override
    public int getValue()
    {
      return _myCommand.getWindowSize();
    }

    @Override
    public void dataChanged(IStoreItem subject)
    {
    }

    @Override
    public void metadataChanged(IStoreItem subject)
    {
    }

    @Override
    public void collectionDeleted(IStoreItem subject)
    {
    }

    @Override
    public void dropListener()
    {
      _myCommand.removeChangeListener(this);
    }

  }

  private static class DateHelper implements RangeHelper
  {
    private final Long _start;
    private final Long _end;
    private long _current;
    private final SimpleDateFormat sdf;
    private final int _sliderThumb;
    private final String _name;
    private final IStoreGroup _group;
    private final NumberDocument _collection;

    // introduce scale factor, to let us handle more than the number
    // of millis in Integer.MAX_VALUE
    private final float scaleFactor;

    @SuppressWarnings("unused")
    public DateHelper(Long start, Long end, int sliderThumb, String name,
        IStoreGroup group, NumberDocument temp)
    {
      _start = start;
      _end = end;

      // do we have a start time
      Date gTime = group.getTime();
      if (gTime != null)
      {
        _current = new Long(gTime.getTime());
      }
      else
      {
        _current = new Long(start);
      }
      _sliderThumb = sliderThumb;
      _name = name;
      _group = group;

      final long range = end - start;

      // do we span a day?
      final long MILLIS_IN_ONE_DAY = 24 * 60 * 60 * 1000;
      final String DATE_FORMAT;
      if (range > MILLIS_IN_ONE_DAY)
      {
        DATE_FORMAT = "yy/MM/dd HH:mm";
      }
      else
      {
        DATE_FORMAT = "HH:mm:ss";
      }

      sdf = new SimpleDateFormat(DATE_FORMAT);
      sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
      _collection = temp;

      // do we need to scale the value?
      if (range < Integer.MAX_VALUE)
      {
        scaleFactor = 1;
      }
      else
      {
        scaleFactor = (float) range / (Integer.MAX_VALUE / 2f);
      }
    }

    @Override
    public void updatedTo(int val)
    {
      _current = _start + (long) (val * scaleFactor);

      // and store the value in the group
      _group.setTime(new Date(_current));
    }

    @Override
    public String getMinText()
    {
      return sdf.format(_start);
    }

    @Override
    public String getMaxText()
    {
      return sdf.format(_end);
    }

    @Override
    public int getMinVal()
    {
      return 0;
    }

    @Override
    public int getMaxVal()
    {
      long range = _end - _start;
      float scaledRange = range / scaleFactor;
      int maxVal = (int) scaledRange + _sliderThumb;
      return maxVal;
    }

    @Override
    public String getValueText()
    {
      return sdf.format(_current);
    }

    @Override
    public String getLabel()
    {
      return _name;
    }

    @Override
    public int getValue()
    {
      // how far along are we?
      long diff = _current - _start;

      // and scale it
      int scaled = (int) (diff / scaleFactor);

      return scaled;
    }

    @Override
    public void dropListener()
    {
      // don't worry, we don't need to do anything
    }

  }

  private static class NumberHelper implements RangeHelper, IChangeListener
  {

    private final Range _rng;
    private final Unit<?> _units;
    private int _curVal;
    private final int _sliderThumb;
    private final String _name;
    private final NumberDocument _collection;

    public NumberHelper(Range rng, Unit<?> theUnits, double startVal,
        int sliderThumb, String name, NumberDocument collection)
    {
      _rng = rng;
      _units = theUnits;
      _curVal = (int) startVal;
      _sliderThumb = sliderThumb;
      _name = name;
      _collection = collection;
      collection.addChangeListener(this);
    }

    @Override
    public void updatedTo(int val)
    {
      _curVal = val;

      // ok, and store it
      _collection.replaceSingleton(val);
      _collection.fireDataChanged();
    }

    @Override
    public String getMinText()
    {
      Number min = _rng.getMinimum();
      return "" + min;
    }

    @Override
    public String getMaxText()
    {
      Number max = _rng.getMaximum();
      return "" + max;
    }

    @Override
    public int getMinVal()
    {
      return 0;
    }

    @Override
    public int getMaxVal()
    {
      Number max = _rng.getMaximum();
      int maxVal = max.intValue() + _sliderThumb;
      return maxVal;
    }

    @Override
    public String getValueText()
    {
      final String unitStr;
      if (_units != null && _units.toString().length() > 0)
      {
        unitStr = _units.toString();
      }
      else
      {
        unitStr = "n/a";
      }

      return "" + _curVal + unitStr;
    }

    @Override
    public String getLabel()
    {
      return _name;
    }

    @Override
    public int getValue()
    {
      return _curVal;
    }

    @Override
    public void dataChanged(IStoreItem subject)
    {
    }

    @Override
    public void metadataChanged(IStoreItem subject)
    {
    }

    @Override
    public void collectionDeleted(IStoreItem subject)
    {
    }

    @Override
    public void dropListener()
    {
      _collection.removeChangeListener(this);
    }

  }

  private RangeHelper _myHelper = null;

  private static final String PENDING_TEXT = "     ====== pending  ====== ";

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "info.limpet.ui.RangeSliderView";

  private Slider slider;

  private Label minL;

  private Label val;

  private Label maxL;

  private Label label;

  /**
   * The constructor.
   */
  public RangeSliderView()
  {
    super(ID, "Range Slider");
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  public void createPartControl(Composite parent)
  {
    makeActions();
    contributeToActionBars();

    // ok, do the layout
    Composite holder = new Composite(parent, SWT.NONE);
    GridLayout gl = new GridLayout(3, false);
    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    holder.setLayoutData(gd);
    holder.setLayout(gl);

    label = new Label(holder, SWT.NONE);
    gd = new GridData(SWT.CENTER, SWT.FILL, true, false);
    gd.horizontalSpan = 3;
    label.setLayoutData(gd);
    label.setText(PENDING_TEXT);

    slider = new Slider(holder, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    gd.horizontalSpan = 3;
    slider.setLayoutData(gd);
    slider.addListener(SWT.Selection, new Listener()
    {

      @Override
      public void handleEvent(Event event)
      {
        int curVal = slider.getSelection();

        if (_myHelper != null)
        {
          _myHelper.updatedTo(curVal);
          val.setText(_myHelper.getValueText());
        }
      }
    });
    minL = new Label(holder, SWT.NONE);
    gd = new GridData(SWT.BEGINNING, SWT.FILL, true, false);
    minL.setLayoutData(gd);
    minL.setText("   ==   ");

    val = new Label(holder, SWT.NONE);
    gd = new GridData(SWT.CENTER, SWT.FILL, true, false);
    val.setLayoutData(gd);
    val.setText("   ==   ");

    maxL = new Label(holder, SWT.NONE);
    gd = new GridData(SWT.END, SWT.FILL, true, false);
    maxL.setLayoutData(gd);
    maxL.setText("   ==   ");

    // register as selection listener
    setupListener();
  }

  @Override
  public void display(List<IStoreItem> res)
  {
    if (res.size() != 1)
    {
      return;
    }

    IStoreItem first = res.get(0);
    if (first instanceof IDocument)
    {
      IDocument newColl = (IDocument) res.get(0);

      if (newColl instanceof NumberDocument)
      {
        NumberDocument currentColl = (NumberDocument) newColl;
        showData(currentColl);
      }
    }
    else if (first instanceof ICommand
        && first instanceof SimpleMovingAverageCommand)
    {
      SimpleMovingAverageCommand command = (SimpleMovingAverageCommand) first;
      showData(command);

    }

  }

  protected void dropListener()
  {
    if (_myHelper != null)
    {
      _myHelper.dropListener();
      _myHelper = null;
    }
  }

  private void showData(final Object object)
  {

    if (object instanceof SimpleMovingAverageCommand)
    {
      if (_myHelper != null)
        if (_myHelper instanceof CommandHelper)
        {
          CommandHelper cHelp = (CommandHelper) _myHelper;
          if (cHelp._myCommand != object)
          {
            dropListener();
          }
          else
          {
            return;
          }
        }
      SimpleMovingAverageCommand sam = (SimpleMovingAverageCommand) object;
      _myHelper = new CommandHelper(sam, slider.getThumb());
    }
    else if (object instanceof NumberDocument)
    {
      NumberDocument qc = (NumberDocument) object;

      // does it have a range?
      Range rng = qc.getRange();

      if (rng != null)
      {
        Unit<?> theUnits = qc.getUnits();
        if (qc.size() > 0)
        {
          // ok, drop the current object
          if (_myHelper != null)
          {
            if (_myHelper instanceof NumberHelper)
            {
              NumberHelper cHelp = (NumberHelper) _myHelper;
              if (cHelp._collection != object)
              {
                dropListener();
              }
              else
              {
                return;
              }
            }
          }

          int curVal = (int) qc.getValue(0);

          _myHelper =
              new NumberHelper(rng, theUnits, curVal, slider.getThumb(), qc
                  .getName(), qc);
        }
      }
      else if (qc.isIndexed())
      {
        // ok, time data, show the time range
        NumberDocument temp = (NumberDocument) qc;

        if (_myHelper != null)
          if (_myHelper instanceof DateHelper)
          {
            DateHelper cHelp = (DateHelper) _myHelper;
            if (cHelp._collection != object)
            {
              dropListener();
            }
            else
            {
              return;
            }
          }

        // now we need the time range
        // TODO: we need to support indexing the data
        // Long start = temp.getTimes().get(0);
        // Long end = temp.getTimes().get(temp.getTimes().size() - 1);
        IStoreGroup parent = findTopParent(temp);

        // just double-check we can fit in the period
        if (parent != null)
        {
          // TODO: reinstate the date helper
          // _myHelper =
          // new DateHelper(start, end, slider.getThumb(), temp.getName(),
          // parent, temp);
        }
        else
        {
          Activator.getDefault().getLog().log(
              new Status(Status.WARNING, Activator.PLUGIN_ID,
                  "Couldn't find top level group"));
        }
      }
    }

    if (_myHelper != null)
    {
      // and format them
      minL.setText(_myHelper.getMinText());
      maxL.setText(_myHelper.getMaxText());
      val.setText("   " + _myHelper.getValueText());
      label.setText(_myHelper.getLabel());
      slider.setMinimum(_myHelper.getMinVal());
      slider.setMaximum(_myHelper.getMaxVal());
      slider.setSelection(_myHelper.getValue());
      slider.setEnabled(true);
      label.getParent().getParent().layout(true, true);
      label.getParent().getParent().redraw();
    }
    else
    {
      slider.setEnabled(false);
    }
  }

  @Override
  public void setFocus()
  {
    slider.setFocus();
  }

  @Override
  protected boolean appliesToMe(final List<IStoreItem> selection,
      final CollectionComplianceTests tests)
  {
    boolean res = false;

    if (selection.size() == 1)
    {
      IStoreItem item = selection.iterator().next();
      if (item instanceof IDocument)
      {
        IDocument coll = (IDocument) item;

        if (coll.isQuantity() && coll.size() == 1)
        {
          NumberDocument qc = (NumberDocument) coll;
          Range range = qc.getRange();
          res = range != null;
        }
        else if (coll.isIndexed() && coll.size() > 0)
        {
          res = true;
        }
      }
      else if (item instanceof ICommand)
      {
        ICommand coll = (ICommand) item;

        if (coll instanceof SimpleMovingAverageCommand)
        {
          res = true;
        }
      }
    }
    return res;
  }

  @Override
  protected String getTextForClipboard()
  {
    return "Pending";
  }

  @Override
  public void dataChanged(IStoreItem subject)
  {
    // ok, re=do the update
    showData(subject);
  }

  @Override
  public void collectionDeleted(IStoreItem subject)
  {
  }

  @Override
  public void metadataChanged(IStoreItem subject)
  {
    dataChanged(subject);
  }

  public static IStoreGroup findTopParent(final IStoreItem subject)
  {
    IStoreGroup res = null;
    IStoreGroup thisItem = subject.getParent();
    while (thisItem != null)
    {
      res = thisItem;
      thisItem = thisItem.getParent();
    }
    return res;
  }

}
