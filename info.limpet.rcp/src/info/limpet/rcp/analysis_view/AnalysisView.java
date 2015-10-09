package info.limpet.rcp.analysis_view;

import info.limpet.IStore.IStoreItem;
import info.limpet.analysis.AnalysisLibrary;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.rcp.core_view.CoreAnalysisView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * display analysis overview of selection
 * 
 * @author ian
 * 
 */
public class AnalysisView extends CoreAnalysisView
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "info.limpet.rcp.AnalysisView";

	TableViewer viewer;

	public AnalysisView()
	{
		super(ID, "Analysis");
	}


	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{

		public String getColumnText(Object obj, int index)
		{
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index)
		{
			return getImage(obj);
		}

		public Image getImage(Object obj)
		{
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	@Override
	protected String getTextForClipboard()
	{
		StringBuffer output = new StringBuffer();
		@SuppressWarnings("unchecked")
		ArrayList<ArrayList<String>> list = (ArrayList<ArrayList<String>>) viewer
				.getInput();
		String separator = System.getProperty("line.separator");
		Iterator<ArrayList<String>> iter = list.iterator();
		while (iter.hasNext())
		{
			ArrayList<java.lang.String> arrayList = (ArrayList<java.lang.String>) iter
					.next();
			output.append(arrayList.get(0));
			output.append(", ");
			output.append(arrayList.get(1));
			output.append(separator);
		}
		return output.toString();
	}

	@Override
	public void display(List<IStoreItem> res)
	{
		// clear the output
		final ArrayList<ArrayList<String>> resList = new ArrayList<ArrayList<String>>();

		AnalysisLibrary ana = new AnalysisLibrary()
		{

			@Override
			protected void presentResults(List<String> titles, List<String> values)
			{
				// produce two column list
				Iterator<String> tIter = titles.iterator();
				Iterator<String> vIter = values.iterator();
				while (tIter.hasNext())
				{
					ArrayList<String> thisRow = new ArrayList<String>();
					thisRow.add(tIter.next());
					thisRow.add(vIter.next());

					resList.add(thisRow);
				}
			}
		};
		ana.analyse(res);
		viewer.setInput(resList);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setInput(null);
		viewer.getTable().setHeaderVisible(true);

		makeActions();
		contributeToActionBars();

		// define the two columns
		TableViewerColumn colTitle = new TableViewerColumn(viewer, SWT.NONE);
		colTitle.getColumn().setWidth(150);
		colTitle.getColumn().setText("Title");
		colTitle.setLabelProvider(new ColumnLabelProvider()
		{
			@SuppressWarnings("unchecked")
			@Override
			public String getText(Object element)
			{
				ArrayList<String> p = (ArrayList<String>) element;
				return p.get(0);
			}
		});

		TableViewerColumn colValue = new TableViewerColumn(viewer, SWT.NONE);
		colValue.getColumn().setWidth(200);
		colValue.getColumn().setText("Value");
		colValue.setLabelProvider(new ColumnLabelProvider()
		{
			@SuppressWarnings("unchecked")
			@Override
			public String getText(Object element)
			{
				ArrayList<String> p = (ArrayList<String>) element;
				return p.get(1);
			}
		});

		setupListener();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	@Override
	protected boolean appliesToMe(List<IStoreItem> res,
			CollectionComplianceTests tests)
	{
		return true;
	}
}