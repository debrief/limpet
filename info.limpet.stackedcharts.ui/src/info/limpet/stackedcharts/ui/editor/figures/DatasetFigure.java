package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;

import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

public class DatasetFigure extends RectangleFigure
{
  private DirectionalLabel nameLabel;

  public DatasetFigure()
  {
    setOutline(false);
    GridLayout layout = new GridLayout();
    setLayoutManager(layout);

    nameLabel = new DirectionalLabel();
    add(nameLabel);

    // Indicate via Mouse Cursor that the Dataset can be moved (to another Axis).
    // Not the perfect solution, ideally there should be way to realize this in the upper layer
    // (GEF)
    nameLabel.addMouseMotionListener(new MouseMotionListener()
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

    Label image = new Label(StackedchartsImages.getImage(
        StackedchartsImages.DESC_DATASET));
    add(image);
  }

  public void setName(String name)
  {
    nameLabel.setText(name);
  }

  public void setVertical(boolean vertical)
  {
    GridLayout layout = (GridLayout) getLayoutManager();

    if (vertical)
    {
      layout.setConstraint(nameLabel, new GridData(GridData.CENTER,
          GridData.FILL, false, true));
      nameLabel.setTextAlignment(PositionConstants.TOP);
    }
    else
    {
      layout.setConstraint(nameLabel, new GridData(GridData.FILL,
          GridData.CENTER, true, false));
      nameLabel.setTextAlignment(PositionConstants.CENTER);
    }

    nameLabel.setVertical(vertical);
    repaint();
  }

}
