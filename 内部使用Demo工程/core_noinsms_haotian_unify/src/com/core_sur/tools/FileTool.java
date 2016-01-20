package com.core_sur.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

//<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
/**
 * 文件工具类
 * 
 * @author kf156(亚日)
 * 
 * 
 */
public class FileTool
{
	public static byte[] readFileByBytes(String fileName)
	{
		File file = new File(fileName);
		byte[] temp = null;
		InputStream in = null;
		try
		{
			// 一次读一个字节
			in = new FileInputStream(file);
			temp = new byte[in.available()];

			in.read(temp);

			in.close();
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		return temp;
	}

	/**
	 * 显示输入流中还剩的字节数
	 */
	private static void showAvailableBytes(InputStream in)
	{
		try
		{
			CheckLog.log( Thread.currentThread() .getStackTrace()[1].getClassName(),new Exception().getStackTrace()[new Exception().getStackTrace().length-1].toString(),"当前字节输入流中的字节数为:" + in.available());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 写文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param data
	 *            数据
	 * @throws IOException
	 */
	public static void writeFile(String filePath, byte[] data) throws IOException
	{
		File file = new File(filePath);

		File dir = file.getParentFile();

		if (!dir.exists())
			dir.mkdirs();
		if (file.exists())
			file.delete();
		file.createNewFile();

		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		fos.close();
	}

	/**
	 * 写文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param is
	 *            输入流
	 * @throws IOException
	 */
	public static void writeFile(String filePath, InputStream is) throws IOException
	{
		File file = new File(filePath);

		File dir = file.getParentFile();

		if (!dir.exists())
			dir.mkdirs();
		if (file.exists())
			file.delete();
		file.createNewFile();

		FileOutputStream fos = new FileOutputStream(file);
		byte[] data = new byte[10240 * 3];
		int l = 0;
		int size = 0;
		while ((l = is.read(data)) != -1)
		{
			fos.write(data, 0, l);
			size += l;
		}
		fos.close();
		if (com.core_sur.Config.IsDebug)
		{
			Log.i("LocalFile", "LoaclFileCreat:" + filePath);
		}
	}

	/**
	 * 删除文件　可删除不为空目录
	 * 
	 * @param file
	 *            　要删除的File
	 * @return 删除是否成功
	 */
	public static boolean fileKiller(File file)
	{
		// Log.i("fileName", file.getPath());
		if (file.isDirectory())
		{// 若为目录，则先删除目录下所有文件
			File[] subFiles = file.listFiles();
			for (int i = 0; i < subFiles.length; i++)
				if (!fileKiller(subFiles[i]))// 递归删除所有目录与文件
					return false;
		}
		return file.delete();
	}

	/**
	 * 复制文件夹
	 * 
	 * @param dir
	 *            　待复制文件夹
	 * @param folderPath
	 *            　路径
	 * @param includeDir
	 *            　是否包含本文件夹
	 * @return
	 */
	public static boolean dirCopy(File dir, String folderPath, boolean includeDir)
	{
		File newFile = null;
		if (includeDir)// 复制文件夹
			newFile = new File(folderPath + File.separator + dir.getName());
		else
			newFile = new File(folderPath);

		if (!newFile.exists() && !newFile.mkdirs())
		{// 不存在，创建目录
			return false;
		}

		File[] subFiles = dir.listFiles();

		boolean creatSuccess = false;
		for (int i = 0; i < subFiles.length; i++)
		{
			if (subFiles[i].isDirectory())
				creatSuccess = dirCopy(subFiles[i], newFile.getPath(), true);
			else
				creatSuccess = fileCopy(subFiles[i], newFile.getPath());

			if (!creatSuccess)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * 文件复制
	 * 
	 * @param file
	 *            　源文件
	 * @param folderPath
	 *            　目标目录
	 * @return　复制是否成功
	 */
	public static boolean fileCopy(File file, String folderPath)
	{
		File newFile = new File(folderPath + File.separator + file.getName());
		try
		{
			newFile.createNewFile();// 创建文件

			OutputStream os = new FileOutputStream(newFile);
			InputStream is = new FileInputStream(file);
			byte buffer[] = new byte[10240];
			int realLength;
			while ((realLength = is.read(buffer)) > 0)
			{
				os.write(buffer, 0, realLength);
			}
			is.close();
			os.close();

			return true;
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 删除所有文件
	 */
	public static void delAllDataFiles(Context context)
	{
		File file = context.getFilesDir();
		fileKiller(file);
	}

	/**
	 * 文件名修正
	 * 
	 * @param fileName
	 * @return
	 */
	public static String modifyFileName(String fileName)
	{
		if (fileName == null)
			return null;
		String s = "\\/:*?\"<>|";
		StringBuffer sb = new StringBuffer();
		char[] chars = fileName.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			if (s.indexOf(chars[i]) == -1)
			{
				sb.append(chars[i]);
			}
		}
		return sb.toString();
	}

}
