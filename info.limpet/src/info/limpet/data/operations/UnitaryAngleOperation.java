package info.limpet.data.operations;

import info.limpet.ICollection;

import java.util.List;

import javax.measure.unit.SI;

public abstract class UnitaryAngleOperation extends UnitaryMathOperation
{
	public UnitaryAngleOperation(String opName)
	{
		super(opName);
	}

	@Override
	protected boolean appliesTo(List<ICollection> selection)
	{
		// TODO Auto-generated method stub
		return super.appliesTo(selection) && aTests.allHaveDimension(selection, SI.RADIAN.getDimension());
	}
}
