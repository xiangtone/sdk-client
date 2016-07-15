package com.core_sur.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTool
{
  public static String Date2String(long paramLong, String paramString)
  {
    return new SimpleDateFormat(paramString).format(new Date(paramLong));
  }

  public static long DateStr2Long(String paramString1, String paramString2)
  {
    try
    {
      long l = new SimpleDateFormat(paramString2).parse(paramString1).getTime();
      return l;
    }
    catch (ParseException localParseException)
    {
      localParseException.printStackTrace();
    }
    return -1L;
  }

  public static String getTimestamp()
  {
    return Date2String(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
  }
}