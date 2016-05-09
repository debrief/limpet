package info.limpet.stackedcharts.core.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class StackedChartsView extends ViewPart
{

  @Override
  public void createPartControl(Composite parent)
  {
    
    new Label(parent, SWT.NONE).setText("StackedChartsView");
  }

  @Override
  public void setFocus()
  {
    // TODO Auto-generated method stub
    
  }

}
