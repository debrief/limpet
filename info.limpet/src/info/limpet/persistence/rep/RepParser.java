package info.limpet.persistence.rep;

import info.limpet.IStoreItem;
import info.limpet.persistence.FileParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class RepParser extends FileParser
{

  @Override
  public List<IStoreItem> parse(String filePath) throws IOException
  {
    final List<IStoreItem> res = new ArrayList<IStoreItem>();
    final File inFile = new File(filePath);
    final Reader in =
        new InputStreamReader(new FileInputStream(inFile), Charset
            .forName("UTF-8"));
    final String fullFileName = inFile.getName();
    final String fileName = filePrefix(fullFileName);

    // ok, loop through the data
    BufferedReader br = null;
    try
    {
      br = new BufferedReader(in);
      for (String line; (line = br.readLine()) != null;)
      {
        // ok, parse this line
        
      }
    }
    finally
    {
      if (br != null)
      {
        br.close();
      }
    }

    return res;

  }

}
