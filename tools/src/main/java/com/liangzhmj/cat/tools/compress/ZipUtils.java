package com.liangzhmj.cat.tools.compress;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZipUtils {

	/**
	 * 解压缩zip文件
	 * 
	 * @param sourceZip
	 *            要解压的文件名 包含路径 如："c:\\test.zip"
	 * @param destDir
	 *            解压后存放文件的路径 如："c:\\temp"
	 * @throws Exception
	 */
	public static void unzip(String sourceZip, String destDir) {
		if (!new File(sourceZip).exists()) {
			return;
		}
		try {
			Project pro = new Project();
			Expand expand = new Expand();
			expand.setProject(pro);
			expand.setSrc(new File(sourceZip));
			expand.setOverwrite(false);// 是否覆盖
			File f = new File(destDir);
			expand.setDest(f);
			expand.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解压缩zip文件
	 * 
	 * @param sourceFile
	 *            要解压的文件名 包含路径 如："c:\\test.zip"
	 * @param destDir
	 *            解压后存放文件的路径 如："c:\\temp"
	 * @throws Exception
	 */
	public static void unzip(File sourceFile, File destDir) {
		if (!sourceFile.exists()) {
			return;
		}
		try {
			Project pro = new Project();
			Expand expand = new Expand();
			expand.setProject(pro);
			expand.setSrc(sourceFile);
			expand.setOverwrite(false);// 是否覆盖
			expand.setDest(destDir);
			expand.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 压缩zip包
	 * 
	 * @param srcDir
	 *            要压缩目录
	 * @param zipPath
	 *            压缩包的名称
	 * @return
	 */
	public static String zip(File srcDir, String zipPath) {
		if (srcDir == null || !srcDir.exists())
			return null;
		Project prj = new Project();
		Zip zip = new Zip();
		zip.setProject(prj);
		// 设置zip的路径包括名称
		zip.setDestFile(new File(zipPath));
		FileSet fileSet = new FileSet();
		fileSet.setDir(srcDir);
		fileSet.setProject(prj);
		zip.addFileset(fileSet);
		// 执行打包
		zip.execute();
		return zipPath;
	}

	
	/**
	 * 标准流压缩
	 * @param src
	 * @return
	 */
	public static byte[] compressZip(byte[] src){
		ByteArrayOutputStream bao = null;
		try {
			bao = new ByteArrayOutputStream();
			GZIPOutputStream out = new GZIPOutputStream(bao);
			out.write(src);
			out.flush();
			out.close();//这里一定要先close不然压缩数据会被破坏
			byte[] b = bao.toByteArray();
			return b;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bao != null){
					bao.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 标准流解压
	 * @param src
	 * @return
	 */
	public static byte[] decompressionZip(byte[] zipByte){
		ByteArrayOutputStream bao = null;
		ByteArrayInputStream bai = null;
		GZIPOutputStream out = null;
		try {
			bai = new ByteArrayInputStream(zipByte);
			GZIPInputStream in = new GZIPInputStream(bai);
			bao = new ByteArrayOutputStream();
			byte[] temp = new byte[255];
			while(in.read(temp)>0){
				bao.write(temp);
			}
			bao.flush();
			in.close();
			byte[] b = bao.toByteArray();
			return b;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(out != null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(bao != null){
					bao.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(bai != null){
					bai.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
