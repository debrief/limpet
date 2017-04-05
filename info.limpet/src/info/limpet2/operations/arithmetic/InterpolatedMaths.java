/*******************************************************************************
 * Copyright (c) 2017 Deep Blue C Technology Ltd, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ian Mayo - initial implementation
 *******************************************************************************/
package info.limpet2.operations.arithmetic;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Comparisons;
import org.eclipse.january.dataset.Comparisons.Monotonicity;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

/**
 * experiment with extending maths processing to be AxesMetadata aware
 * 
 * @author ian
 * 
 */
public class InterpolatedMaths extends Maths
{

  /**
   * we wish the interpolation function to be ignorant of the specific mathematical operation being
   * conducted. This interface wraps the operation in a Command
   * 
   */
  public static interface IOperationPerformer
  {
    /**
     * perform some operation on datasets a and b. If an output dataset is provided, store the
     * results in there.
     * 
     * @param a
     *          left-hand operand
     * @param b
     *          right-hand operand
     * @param o
     *          (optional) results object
     * @return a dataset product
     */
    public Dataset perform(final Dataset a, final Dataset b, final Dataset o);
  }

  private static final String ONLY_1D_DATASETS =
      "Only 1d datasets are currently covered by this interpolation mechanism";
  private static final String DUPLICATE_INDEX_VALUES_NOT_ALLOWED =
      "Duplicate index values not allowed";
  private static final String AXES_MUST_ASCEND_IN_SAME_DIRECTION =
      "The axes must both ascend in the same direction to use interpolation";
  private static final String AXES_MUST_BE_MONOTONIC =
      "The dataset axes must both be monotonic to use interpolation";
  private static final String INDICES_DO_NOT_OVERLAP =
      "The indices of the dataset do not overlap";

  /**
   * 
   * @param a
   *          left-hand operand
   * @param b
   *          right-hand operand
   * @param o
   *          (optional) results object
   * @param operation
   *          operation to apply
   * @return result of operation, resampling data if necessary to synchronise AxesMetadata
   */
  public static Dataset performWithInterpolation(final Dataset da,
      final Dataset db, final Dataset o, final IOperationPerformer operation)
  {
    final Dataset operandA;
    final Dataset operandB;

    // retrieve the axes
    final AxesMetadata axesA = da.getFirstMetadata(AxesMetadata.class);
    final AxesMetadata axesB = db.getFirstMetadata(AxesMetadata.class);

    if (axesA == null || axesB == null)
    {
      throw new IllegalArgumentException(
          "Both datasets must have axes metadata");
    }
    else if (da.getRank() != 1 || db.getRank() != 1)
    {
      // this processing code only supports 1-D datasets
      throw new IllegalArgumentException(ONLY_1D_DATASETS);
    }
    else
    {
      // ok, we've got indexed data. see if they match
      final ILazyDataset aLazyIndices = axesA.getAxis(0)[0];
      final ILazyDataset bLazyIndices = axesB.getAxis(0)[0];

      // first we need to load the datasets in order to compare
      // them
      Dataset aIndices = null;
      Dataset bIndices = null;
      boolean datasetLoaded = false;
      try
      {
        aIndices = DatasetUtils.sliceAndConvertLazyDataset(aLazyIndices);
        bIndices = DatasetUtils.sliceAndConvertLazyDataset(bLazyIndices);
        datasetLoaded = true;
      }
      catch (DatasetException e)
      {
        throw new IllegalArgumentException("Axis values not present");
      }

      if (datasetLoaded)
      {
        final boolean needInterp;

        // inspect the data to see if it needs to be
        // interpolated
        if (aIndices.getSize() != bIndices.getSize())
        {
          // ok, they're different sizes, they need syncing
          needInterp = true;
        }
        else if (aIndices.equals(bIndices))
        {
          // ok, they're equal, no need to interp
          needInterp = false;
        }
        else
        {
          // indices don't match, need to interp
          needInterp = true;
        }

        if (needInterp)
        {
          // ok, we need to use interpolation. But, first we
          // need to check if the axes are suitable for interpolation.
          final Monotonicity aMono = Comparisons.findMonotonicity(aIndices);
          final Monotonicity bMono = Comparisons.findMonotonicity(bIndices);

          if (aMono.equals(Monotonicity.NOT_ORDERED)
              || bMono.equals(Monotonicity.NOT_ORDERED))
          {
            throw new IllegalArgumentException(AXES_MUST_BE_MONOTONIC);
          }
          else if (aMono.equals(Monotonicity.NONDECREASING)
              || bMono.equals(Monotonicity.NONDECREASING))
          {
            throw new IllegalArgumentException(
                DUPLICATE_INDEX_VALUES_NOT_ALLOWED);
          }
          else
          {
            // check they're in the same direction
            if (aMono != bMono)
            {
              throw new IllegalArgumentException(
                  AXES_MUST_ASCEND_IN_SAME_DIRECTION);
            }

            // ok, they're both monotonic, in the same direction. we can continue.
          }

          // find the data limits
          final double aMin = aIndices.min().doubleValue();
          final double aMax = aIndices.max().doubleValue();
          final double bMin = bIndices.min().doubleValue();
          final double bMax = bIndices.max().doubleValue();

          if (aMax < bMin || aMin > bMax)
          {
            // datasets don't overlap
            throw new IllegalArgumentException(INDICES_DO_NOT_OVERLAP);
          }
          else if (aMin <= bMin && aMax >= bMax)
          {
            // ok, b is wholly contained within A. Just generate
            // points in A at the time in B
            final Dataset interpolatedValues =
                Maths.interpolate(aIndices, da, bIndices, null, null);
            // remember the output axes, since we'll put them
            // into the results
            interpolatedValues.addMetadata(axesB);

            operandA = db;
            operandB = interpolatedValues;
          }
          else if (bMin < aMin && bMax > aMax)
          {
            // ok, A is wholly contained within B. Just generate
            // points in B at the time in A
            final Dataset interpolatedValues =
                Maths.interpolate(bIndices, db, aIndices, null, null);
            // remember the output axes, since we'll put them
            // into the results
            interpolatedValues.addMetadata(axesA);

            operandA = da;
            operandB = interpolatedValues;
          }
          else
          {
            // ok, one isn't contained within the other,
            // they merely overlap. Find the overlapping period
            final double startPoint = aMin < bMin ? bMin : aMin;
            final double endPoint = aMax > bMax ? bMax : aMax;

            // to find the last matching entry, we have to work through
            // the dataset in reverse.
            final Dataset aReverse =
                aIndices.getSliceView(null, null, new int[]
                {-1});
            final Dataset bReverse =
                bIndices.getSliceView(null, null, new int[]
                {-1});

            // now get the two slices in this period
            final int aStartPosition;
            final int aEndPosition;
            final int bStartPosition;
            final int bEndPosition;
            if (aMono == Monotonicity.STRICTLY_INCREASING)
            {
              // ok - get the index of the first point in the region
              final int aStartIndex =
                  DatasetUtils.findIndexGreaterThanOrEqualTo(aIndices,
                      startPoint);

              // now retrieve the n-dimensional position that matches this index
              aStartPosition = aIndices.getNDPosition(aStartIndex)[0];

              // note: we reduce index by one to allow for zero-indexing
              final int aEndIndex =
                  DatasetUtils.findIndexLessThanOrEqualTo(aReverse, endPoint);
              aEndPosition =
                  aReverse.getSize() - 1 - aReverse.getNDPosition(aEndIndex)[0];

              final int bStartIndex =
                  DatasetUtils.findIndexGreaterThanOrEqualTo(bIndices,
                      startPoint);
              bStartPosition = bIndices.getNDPosition(bStartIndex)[0];

              final int bEndIndex =
                  DatasetUtils.findIndexLessThanOrEqualTo(bReverse, endPoint);
              bEndPosition =
                  bReverse.getSize() - 1 - bReverse.getNDPosition(bEndIndex)[0];
            }
            else
            {
              // ok - data descending
              // get the index of the first point in the region
              final int aStartIndex =
                  DatasetUtils.findIndexLessThanOrEqualTo(aIndices, endPoint);

              // now retrieve the n-dimensional position that matches this index
              aStartPosition = aIndices.getNDPosition(aStartIndex)[0];

              // note: we reduce index by one to allow for zero-indexing
              final int aEndIndex =
                  DatasetUtils.findIndexGreaterThanOrEqualTo(aReverse,
                      startPoint);
              aEndPosition =
                  aReverse.getSize() - 1 - aReverse.getNDPosition(aEndIndex)[0];

              final int bStartIndex =
                  DatasetUtils.findIndexLessThanOrEqualTo(bIndices, endPoint);
              bStartPosition = bIndices.getNDPosition(bStartIndex)[0];

              final int bEndIndex =
                  DatasetUtils.findIndexGreaterThanOrEqualTo(bReverse,
                      startPoint);
              bEndPosition =
                  bReverse.getSize() - 1 - bReverse.getNDPosition(bEndIndex)[0];
            }

            // ok, retrieve the index values that are within the intersecting period
            final Dataset aIndicesTrimmed = aIndices.getSliceView(new int[]
            {aStartPosition}, new int[]
            {aEndPosition + 1}, null);
            final Dataset bIndicesTrimmed = bIndices.getSliceView(new int[]
            {bStartPosition}, new int[]
            {bEndPosition + 1}, null);

            // the interpolation strategy we're going to adopt is that we're
            // going to use the index values from the dataset that has
            // the most measurements in the intersecting range.
            if (aIndicesTrimmed.getSize() > bIndicesTrimmed.getSize())
            {
              // aTimes has more samples, use it for interpolation
              final Dataset interpolatedValues =
                  Maths.interpolate(bIndices, db, aIndicesTrimmed, null, null);

              // we can just extract the trimmed set of a values
              final Dataset aValues = da.getSliceView(new int[]
              {aStartPosition}, new int[]
              {aEndPosition + 1}, null);

              // clear the metadata from this, since we'll change the axis length
              aValues.clearMetadata(AxesMetadata.class);

              // remember the output axes, since we'll put them
              // into the results
              AxesMetadata newAxis = new AxesMetadataImpl();
              newAxis.initialize(1);
              newAxis.setAxis(0, aIndicesTrimmed);
              interpolatedValues.addMetadata(newAxis);
              aValues.addMetadata(newAxis);

              operandA = aValues;
              operandB = interpolatedValues;
            }
            else
            {
              // bTimes has more samples (or they're equal), use it for interpolation
              final Dataset interpolatedValues =
                  Maths.interpolate(aIndices, da, bIndicesTrimmed, null, null);

              // we can just extract the trimmed set of b values
              final Dataset bValues = db.getSliceView(new int[]
              {bStartPosition}, new int[]
              {bEndPosition + 1}, null);

              // clear the metadata from this, since we'll change the axis length
              bValues.clearMetadata(AxesMetadata.class);

              // remember the output axes, since we'll put them
              // into the results
              AxesMetadata newAxis = new AxesMetadataImpl();
              newAxis.initialize(1);
              newAxis.setAxis(0, bIndicesTrimmed);
              interpolatedValues.addMetadata(newAxis);
              bValues.addMetadata(newAxis);

              operandA = bValues;
              operandB = interpolatedValues;
            }
          }
        }
        else
        {
          // an exception was thrown while trying to determine
          // the
          // indices. See if the parent class can handle it
          operandA = da;
          operandB = db;
        }
      }
      else
      {
        // no interpolation needed
        operandA = da;
        operandB = db;
      }
    }

    // perform the basic operation
    final Dataset res = operation.perform(operandA, operandB, o);

    // ok, inject the axes back into the result, if we can
    if (operandA instanceof IDataset)
    {
      final IDataset ds = (IDataset) operandA;
      final AxesMetadata targetAxes = ds.getFirstMetadata(AxesMetadata.class);
      if (targetAxes != null)
      {
        res.addMetadata(targetAxes);
      }
    }

    return res;
  }
}
