package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;

public class AxisNameFigure extends RectangleFigure
{
  private VerticalLabel verticalLabel;

  public AxisNameFigure(ActionListener deleteHandler)
  {
    setOutline(false);
    GridLayout layout = new GridLayout(1, true);
    setLayoutManager(layout);

    Button button = new Button("X");
    button.setToolTip(new Label("Remove this axis from the chart"));
    button.addActionListener(deleteHandler);
    add(button);

    verticalLabel = new VerticalLabel();
    layout.setConstraint(verticalLabel, new GridData(GridData.FILL,
        GridData.FILL, false, true));

    add(verticalLabel);
  }

  public void setName(String name)
  {
    verticalLabel.setText(name);
  }

}
