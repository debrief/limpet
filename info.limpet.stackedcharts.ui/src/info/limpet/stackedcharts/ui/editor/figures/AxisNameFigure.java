package info.limpet.stackedcharts.ui.editor.figures;

import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class AxisNameFigure extends RectangleFigure
{
  private static volatile Font boldFont;
  private VerticalLabel verticalLabel;

  public AxisNameFigure(ActionListener deleteHandler)
  {
    setOutline(false);
    FlowLayout layout = new FlowLayout(false);
    layout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
    setLayoutManager(layout);

    Button button = new Button(StackedchartsImages.getImage(StackedchartsImages.DESC_DELETE));
    button.setToolTip(new Label("Remove this axis from the chart"));
    button.addActionListener(deleteHandler);
    add(button);

    verticalLabel = new VerticalLabel();
   
   
    verticalLabel.setTextAlignment(PositionConstants.TOP);

    add(verticalLabel);
    Label image = new Label(StackedchartsImages.getImage(StackedchartsImages.DESC_AXIS));
    add(image);
  }

  public void setName(String name)
  {
    
    verticalLabel.setText(name);
    // cache font for AxisNameFigure
    if (boldFont == null)
    {
      FontData fontData = verticalLabel.getFont().getFontData()[0];
      boldFont =
          new Font(Display.getCurrent(), new FontData(fontData.getName(),
              fontData.getHeight(), SWT.BOLD));
    }

    verticalLabel.setFont(boldFont);
  }
  
  @Override
  public void setFont(Font f)
  {
    verticalLabel.setFont(boldFont);
  }

}
