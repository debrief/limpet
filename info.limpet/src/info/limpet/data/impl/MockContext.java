/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.data.impl;

import info.limpet.IContext;

public class MockContext implements IContext
{

  @Override
  public String getInput(String title, String description, String defaultText)
  {
    return defaultText;
  }

  @Override
  public void logError(Status status, String message, Exception e)
  {
    System.err.println("Logging status:" + status + " message:" + message);
    if (e != null)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void openWarning(String title, String message)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void openInformation(String title, String message)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public String getCsvFilename()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean openQuestion(String title, String message)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void openError(String title, String message)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void log(Exception e)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void placeOnClipboard(String text)
  {
    // TODO Auto-generated method stub

  }

}
