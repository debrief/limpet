package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

public class AxisNameFigure extends RectangleFigure
{
  private static volatile Font boldFont;
  private DirectionalLabel nameLabel;

  public AxisNameFigure(ActionListener deleteHandler)
  {
    setOutline(false);
    GridLayout layout = new GridLayout();
    setLayoutManager(layout);

    Button button = new Button(StackedchartsImages.getImage(StackedchartsImages.DESC_DELETE));
    button.setToolTip(new Label("Remove this axis from the chart"));
    button.addActionListener(deleteHandler);
    add(button);

    nameLabel = new DirectionalLabel();
   
   
    nameLabel.setTextAlignment(PositionConstants.TOP);

    add(nameLabel);
    Label image = new Label(StackedchartsImages.getImage(StackedchartsImages.DESC_AXIS));
    add(image);
  }

  public void setName(String name)
  {
    
    nameLabel.setText(name);
    // cache font for AxisNameFigure
    if (boldFont == null)
    {
      FontData fontData = nameLabel.getFont().getFontData()[0];
      boldFont =
          new Font(Display.getCurrent(), new FontData(fontData.getName(),
              fontData.getHeight(), SWT.BOLD));
    }

    nameLabel.setFont(boldFont);
  }
  
  @Override
  public void setFont(Font f)
  {
    nameLabel.setFont(boldFont);
  }
  
  public void setVertical(boolean vertical) {
    GridLayout layout = (GridLayout) getLayoutManager();
    layout.numColumns = vertical ? 1 : getChildren().size();
    layout.invalidate();
    
    nameLabel.setVertical(vertical);
    
    repaint();
  }

}
