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
import info.limpet.impl.SampleData;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.RangedEntity;
import info.limpet.operations.arithmetic.SimpleMovingAverageOperation.SimpleMovingAverageCommand;
import info.limpet.ui.core_view.CoreAnalysisView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

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
public class RangeSliderView extends CoreAnalysisView
{

  private class DateIndexHelper implements RangeHelper
  {
    private final Long _start;
    private final Long _end;
    private long _current;
    private final SimpleDateFormat sdf;
    private int _sliderThumb;
    private final String _name;
    private final IStoreGroup _group;
    private final NumberDocument _collection;

    // introduce scale factor, to let us handle more than the number
    // of millis in Integer.MAX_VALUE
    private final float scaleFactor;

    public DateIndexHelper(final Long start, final Long end, final String name,
        final IStoreGroup group, final NumberDocument doc)
    {
      _start = start;
      _end = end;

      // do we have a start time
      final Date gTime = group.getTime();
      if (gTime != null)
      {
        _current = new Long(gTime.getTime());
      }
      else
      {
        _current = new Long(start);
      }
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
      _collection = doc;

      // do we need to scale the value?
      if (range < Integer.MAX_VALUE)
      {
        scaleFactor = 1;
      }
      else
      {
        scaleFactor = range / (Integer.MAX_VALUE / 2f);
      }
    }

    @Override
    public void dropListener()
    {
      // don't worry, we don't need to do anything
    }

    @Override
    public String getLabel()
    {
      return _name;
    }

    @Override
    public String getMaxText()
    {
      return sdf.format(_end);
    }

    @Override
    public int getMaxVal()
    {
      final long range = _end - _start;
      final float scaledRange = range / scaleFactor;
      final int maxVal = (int) scaledRange + _sliderThumb;
      return maxVal;
    }

    @Override
    public String getMinText()
    {
      return sdf.format(_start);
    }

    @Override
    public int getMinVal()
    {
      return 0;
    }

    public String getUnits()
    {
      final Unit<?> units = _collection.getIndexUnits();
      return units != null ? units.toString() : null;
    }

    @Override
    public int getValue()
    {
      // how far along are we?
      final long diff = _current - _start;

      // and scale it
      final int scaled = (int) (diff / scaleFactor);

      return scaled;
    }

    @Override
    public String getValueText()
    {
      final String value = sdf.format(_current);
      final String res;
      final String units = getUnits();
      if (units != null)
      {
        res = value + "(" + units + ")";
      }
      else
      {
        res = value;
      }
      return res;
    }

    @Override
    public void setSliderThumb(final int val)
    {
      _sliderThumb = val;
    }

    @Override
    public void updatedTo(final int val)
    {
      _current = _start + (long) (val * scaleFactor);

      // and store the value in the group
      _group.setTime(new Date(_current));
    }

  }

  private static class Figure implements Listener
  {
    public Label label;
    public Slider slider;
    public Label minL;
    public Label val;
    public Label maxL;
    public RangeHelper helper;
    public Composite holder;

    public void connect()
    {
      slider.addListener(SWT.Selection, this);
    }

    public void detach()
    {
      holder.dispose();
    }

    public void disconnect()
    {
      if (slider != null && !slider.isDisposed())
      {
        slider.removeListener(SWT.Selection, this);
      }
    }

    @Override
    public void handleEvent(final Event event)
    {
      final int curVal = slider.getSelection();

      if (helper != null)
      {
        helper.updatedTo(curVal);
        if (!val.isDisposed())
        {
          val.setText(helper.getValueText());
        }
      }
    }

    public void refresh()
    {
      val.setText(helper.getValueText());
      label.setText(helper.getLabel());
    }

    public void initialise()
    {
      // and format them
      minL.setText(helper.getMinText());
      maxL.setText(helper.getMaxText());
      val.setText("   " + helper.getValueText());
      label.setText(helper.getLabel());

      // use some dummy values, just to get us in the ball park
      slider.setMinimum(0);
      slider.setMaximum(10000);

      // now the real limits
      slider.setMinimum(helper.getMinVal());
      slider.setMaximum(helper.getMaxVal());
      slider.setSelection(helper.getValue());
      slider.setEnabled(true);
      label.getParent().getParent().layout(true, true);
      label.getParent().getParent().redraw();

      // store the thumb witdth
      helper.setSliderThumb(slider.getThumb());
    }
  }

  private class RangedEntityHelper implements RangeHelper, IChangeListener
  {

    private final RangedEntity _rangedEntity;
    private int _sliderThumb;
    private final Range _range;

    public RangedEntityHelper(final RangedEntity rCommand)
    {
      _rangedEntity = rCommand;
      _rangedEntity.addTransientChangeListener(this);
      _range = rCommand.getRange();
    }

    @Override
    public void collectionDeleted(final IStoreItem subject)
    {
      // ok, we need to die.
      itemDeleted(subject);
    }

    @Override
    public void dataChanged(final IStoreItem subject)
    {
      itemChanged(subject);
    }

    @Override
    public void dropListener()
    {
      _rangedEntity.removeTransientChangeListener(this);
    }

    @Override
    public String getLabel()
    {
      return _rangedEntity.getName();
    }

    @Override
    public String getMaxText()
    {
      return "" + _range.getMaximum();
    }

    @Override
    public int getMaxVal()
    {
      return _sliderThumb + ((Double) _range.getMaximum()).intValue();
    }

    @Override
    public String getMinText()
    {
      return "" + _range.getMinimum();
    }

    @Override
    public int getMinVal()
    {
      return ((Double) _range.getMinimum()).intValue();
    }

    @Override
    public int getValue()
    {
      return (int) _rangedEntity.getValue();
    }

    @Override
    public String getValueText()
    {
      final String res;
      if (_rangedEntity instanceof NumberDocument)
      {
        final NumberDocument doc = (NumberDocument) _rangedEntity;
        final Unit<?> units = doc.getUnits();
        if (units != null && units.toString().length() > 0)
        {
          res = getValue() + " (" + units + ")";
        }
        else
        {
          res = "" + getValue();
        }
      }
      else
      {
        res = "" + getValue();
      }
      return res;
    }

    @Override
    public void metadataChanged(final IStoreItem subject)
    {
      itemMetadataChanged(subject);
    }

    @Override
    public void setSliderThumb(final int val)
    {
      _sliderThumb = val;
    }

    @Override
    public void updatedTo(final int val)
    {
      _rangedEntity.setValue(val);
    }

  }

  /**
   * helper class, to embody three types of data we use in range control
   * 
   * @author ian
   * 
   */
  private static interface RangeHelper
  {
    /**
     * stop listening to the subject item
     * 
     */
    void dropListener();

    String getLabel();

    public String getMaxText();

    public int getMaxVal();

    public String getMinText();

    public int getMinVal();

    int getValue();

    public String getValueText();

    public void setSliderThumb(int val);

    void updatedTo(int val);

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

  private Composite _sliderColumn;

  private final Map<RangedEntity, Figure> _entities =
      new HashMap<RangedEntity, Figure>();

  private static final String PENDING_TEXT = "     ====== pending  ====== ";

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "info.limpet.ui.RangeSliderView";

  /**
   * The constructor.
   */
  public RangeSliderView()
  {
    super(ID, "Range Slider");
  }

  private Figure addRow(final Composite column, final RangeHelper helper)
  {
    final Figure figure = new Figure();

    // store the helper
    figure.helper = helper;

    // ok, do the layout
    figure.holder = new Composite(column, SWT.NONE);
    final GridLayout gl = new GridLayout(3, false);
    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    figure.holder.setLayoutData(gd);
    figure.holder.setLayout(gl);

    figure.label = new Label(figure.holder, SWT.NONE);
    gd = new GridData(SWT.CENTER, SWT.FILL, true, false);
    gd.horizontalSpan = 3;
    figure.label.setLayoutData(gd);
    figure.label.setText(PENDING_TEXT);

    figure.slider = new Slider(figure.holder, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    gd.horizontalSpan = 3;
    figure.slider.setLayoutData(gd);

    // and connect to the control
    figure.connect();

    figure.minL = new Label(figure.holder, SWT.NONE);
    gd = new GridData(SWT.BEGINNING, SWT.FILL, true, false);
    figure.minL.setLayoutData(gd);
    figure.minL.setText("   ==   ");

    figure.val = new Label(figure.holder, SWT.NONE);
    gd = new GridData(SWT.CENTER, SWT.FILL, true, false);
    figure.val.setLayoutData(gd);
    figure.val.setText("   ==   ");

    figure.maxL = new Label(figure.holder, SWT.NONE);
    gd = new GridData(SWT.END, SWT.FILL, true, false);
    figure.maxL.setLayoutData(gd);
    figure.maxL.setText("   ==   ");

    return figure;
  }

  @Override
  protected boolean appliesToMe(final List<IStoreItem> selection,
      final CollectionComplianceTests tests)
  {
    boolean res = true;

    for (final IStoreItem item : selection)
    {
      if (item instanceof IDocument)
      {
        final IDocument<?> coll = (IDocument<?>) item;

        if (coll.isQuantity() && coll.size() == 1)
        {
          final NumberDocument qc = (NumberDocument) coll;
          final Range range = qc.getRange();
          res = range != null;
        }
        else if (coll.isIndexed() && coll.size() > 1)
        {
          // ok, it's indexed, and we have more than one item in the datsaet
          res = true;
        }
        else
        {
          res = false;
        }
      }
      else if (item instanceof ICommand)
      {
        final ICommand coll = (ICommand) item;

        if (coll instanceof SimpleMovingAverageCommand)
        {
          res = true;
        }
        else
        {
          res = false;
        }
      }
      else if (item instanceof IStoreGroup)
      {
        // see if all the items are singletons
        final IStoreGroup group = (IStoreGroup) item;
        res = true;
        for (final IStoreItem t : group)
        {
          if (t instanceof NumberDocument)
          {
            final NumberDocument doc = (NumberDocument) t;
            if (doc.size() != 1)
            {
              res = false;
              break;
            }
            else
            {
              res = true;
            }
          }
        }
      }
    }

    return res;
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(final Composite parent)
  {
    makeActions();
    contributeToActionBars();

    // register as selection listener
    setupListener();

    _sliderColumn = new Composite(parent, SWT.NONE);
    _sliderColumn.setLayout(new GridLayout(1, true));

    // create a row
    // firstFigure = addRow(_sliderColumn, _myHelper);

  }

  protected void createChangeListeners(List<IStoreItem> res)
  {
    // ok, we don't directly listen to the selection,
    // if it's a group then we listen to the children.
    // So, leave it to the rest of the range-slider
    // to configure listeners
  }

  @Override
  protected void doDisplay(final List<IStoreItem> selection)
  {
    if (selection.size() == 0)
    {
      return;
    }

    final List<RangedEntity> toShow = new ArrayList<RangedEntity>();

    for (final IStoreItem item : selection)
    {
      if (item instanceof RangedEntity)
      {
        final boolean useIt;
        // check it looks right
        if (item instanceof NumberDocument)
        {
          NumberDocument doc = (NumberDocument) item;
          if (doc.size() == 1 && doc.getRange() != null)
          {
            // ok, go for it
            useIt = true;
          }
          else if (doc.size() >= 2 && doc.getIndexUnits() != null)
          {
            // we can use it, if it's an indexed dataset
            // TODO: allow this to be true,
            // if we want to allow indexed datasets
            useIt = false;
          }
          else
          {
            useIt = false;
          }
        }
        else
        {
          useIt = true;
        }
        if (useIt)
        {
          toShow.add((RangedEntity) item);
        }
      }
      else if (item instanceof IStoreGroup)
      {
        final IStoreGroup group = (IStoreGroup) item;

        // process kids
        for (final IStoreItem doc : group)
        {
          if (doc instanceof RangedEntity)
          {
            toShow.add((RangedEntity) doc);
          }
        }
      }
    }

    if (toShow.size() > 0)
    {
      // ok, clear the sliders
      clearSliders();

      // and show the new data
      showData(toShow);
    }
  }

  private void clearSliders()
  {
    // clear current listeners
    for (final Figure thisFigure : _entities.values())
    {
      // drop this one
      thisFigure.disconnect();

      // and detach it from its parent
      thisFigure.detach();
    }

    // ok, list empty
    _entities.clear();
  }

  @Override
  protected String getTextForClipboard()
  {
    return "Pending";
  }

  private void itemChanged(final IStoreItem subject)
  {
    // ok, find the figure
    final Figure figure = _entities.get(subject);

    if (figure != null)
    {
      // ok, get it to repaint itself
      figure.refresh();
    }
  }

  private void itemDeleted(final IStoreItem subject)
  {
    // ok, find the figure
    final Figure figure = _entities.get(subject);

    if (figure != null)
    {
      figure.disconnect();
      figure.detach();
    }
  }

  private void itemMetadataChanged(final IStoreItem subject)
  {
    itemChanged(subject);
  }

  @Override
  public void setFocus()
  {
    _sliderColumn.setFocus();
  }

  private void showData(final List<RangedEntity> items)
  {

    // ok, loop through them
    for (final RangedEntity entity : items)
    {
      final RangeHelper helper;

      // sort out a helper
      if (entity instanceof RangedEntity)
      {
        final RangedEntity ranged = entity;

        if (ranged instanceof NumberDocument)
        {
          final NumberDocument doc = (NumberDocument) ranged;
          helper = createNumberHelper(ranged, doc);
        }
        else
        {
          helper = null;
        }

      }
      else
      {
        helper = null;
      }

      // did we create one?
      if (helper != null)
      {
        // ok, add this item
        final Figure figure = addRow(_sliderColumn, helper);

        // draw the figure
        figure.initialise();

        // store the figure
        _entities.put(entity, figure);
      }
    }
  }

  private RangeHelper createNumberHelper(final RangedEntity ranged,
      final NumberDocument doc)
  {
    final RangeHelper helper;
    if (doc.size() == 1 && doc.getRange() != null)
    {
      helper = new RangedEntityHelper(ranged);
    }
    else
    {
      // TODO: sort out if we introduce index filters,
      // then remove the next line
      boolean allowIndexFilter = false;
      if (allowIndexFilter
          && doc.isIndexed()
          && (doc.getIndexUnits() == SI.SECOND || doc.getIndexUnits() == SampleData.MILLIS))
      {

        final double start = doc.getIndexAt(0);
        final double end = doc.getIndexAt(doc.size() - 1);
        final String name = doc.getName();
        final IStoreGroup group = findTopParent(doc);

        helper =
            new DateIndexHelper((long) start, (long) end, name, group, doc);
      }
      else
      {
        helper = null;
      }
    }
    return helper;
  }

}
