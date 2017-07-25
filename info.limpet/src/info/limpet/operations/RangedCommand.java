package info.limpet.operations;

import info.limpet.IStoreItem;

public interface RangedCommand
{
  int getValue();
  void setValue(int value);
  void recalculate(final IStoreItem subject);
}
