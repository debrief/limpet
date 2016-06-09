package info.limpet.stackedcharts.ui.editor.figures;

import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;

public class DatasetFigure extends RectangleFigure
{
  private VerticalLabel verticalLabel;

  public DatasetFigure()
  {
    setOutline(false);
    FlowLayout layout = new FlowLayout(false);
    layout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
    setLayoutManager(layout);

    verticalLabel = new VerticalLabel();
    verticalLabel.setTextAlignment(PositionConstants.TOP);
    add(verticalLabel);

    // Indicate via Mouse Cursor that the Dataset can be moved (to another Axis).
    // Not the perfect solution, ideally there should be way to realize this in the upper layer
    // (GEF)
    verticalLabel.addMouseMotionListener(new MouseMotionListener()
    {

      @Override
      public void mouseMoved(MouseEvent me)
      {
      }

      @Override
      public void mouseHover(MouseEvent me)
      {
      }

      @Override
      public void mouseExited(MouseEvent me)
      {
        setCursor(Cursors.ARROW);
      }

      @Override
      public void mouseEntered(MouseEvent me)
      {
        setCursor(Cursors.SIZEALL);
      }

      @Override
      public void mouseDragged(MouseEvent me)
      {
      }
    });
    
    Label image = new Label(StackedchartsImages.getImage(StackedchartsImages.DESC_DATASET));
    add(image);
  }

  public void setName(String name)
  {
    verticalLabel.setText(name);
  }

}
