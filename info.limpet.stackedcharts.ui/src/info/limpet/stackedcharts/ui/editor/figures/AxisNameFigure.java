package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;

public class AxisNameFigure extends RectangleFigure
{
  private VerticalLabel verticalLabel;

  public AxisNameFigure(ActionListener deleteHandler)
  {
    setOutline(false);
    FlowLayout layout = new FlowLayout(false);
    layout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
    setLayoutManager(layout);
    
    Button button = new Button("X");
    button.setToolTip(new Label("Remove this axis from the chart"));
    button.addActionListener(deleteHandler);
    add(button);

    verticalLabel = new VerticalLabel();
    verticalLabel.setTextAlignment(PositionConstants.TOP);

    add(verticalLabel);
  }

  public void setName(String name)
  {
    verticalLabel.setText(name);
  }

}
