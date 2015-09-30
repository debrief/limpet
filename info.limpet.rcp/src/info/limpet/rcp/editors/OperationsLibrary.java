package info.limpet.rcp.editors;

import info.limpet.IOperation;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.operations.DivideQuantityOperation;
import info.limpet.data.operations.MultiplyQuantityOperation;
import info.limpet.data.operations.SubtractQuantityOperation;

import java.util.ArrayList;
import java.util.List;

public class OperationsLibrary
{
	public static List<IOperation<?>> getOperations()
	{
		List<IOperation<?>> ops = new ArrayList<IOperation<?>>();
		ops.add(new MultiplyQuantityOperation());
		ops.add(new AddQuantityOperation<>());
		ops.add(new SubtractQuantityOperation<>());
		ops.add(new DivideQuantityOperation());

		return ops;

	}
}
