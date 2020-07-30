package com.liangzhmj.cat.tools.image;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 对Thumbnails工具的一些基础功能进行封装
 * @author liangzhmj
 *
 */
public class ThumbnailsUtils {
	
	private static Map<String,File> fontCache = new HashMap<String,File>();

	/**
	 * 创建一个透明背景的文本图片
	 * @param ttf 字体文件名称
	 * @param fstyle 字体的样式,例如:粗体...
	 * @param fsize 字体的大小
	 * @param fcolor 字体的颜色
	 * @param text 文本内容
	 * @return
	 */
	public static BufferedImage createTTFTextImg(String ttf,int fstyle,int fsize,Color fcolor,String text){
		int height = fsize+8;
		//为了透明背景，这个img是要被丢弃的因此，长宽不重要
		BufferedImage bufImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);  
		//创建画布
        Graphics2D g = bufImg.createGraphics(); 
        //创建文本
        Font font = null;
        font = ThumbnailsUtils.loadFont(ttf,fstyle,fsize);
        //为画布设置字体(用于计算文本的长度)
        g.setFont(font);
        //计算文本的宽度
        int width = ThumbnailsUtils.getTextGLength(text,g);
        //创建新的透明图片设置透明背景
        bufImg = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT); 
        //释放对象
        g.dispose(); 
        //生成新的画布
        g = bufImg.createGraphics(); 
        /* 消除java.awt.Font字体的锯齿 */ 
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
                RenderingHints.VALUE_ANTIALIAS_ON);
        //设置字体颜色
        g.setColor(fcolor); 
        //为新的画布设置字体
        g.setFont(font);  
        //拖拽字体到画布的对应位置
        g.drawString(text, 0, fsize); //后面两个参数是左下角的xy
        //释放对象 
        g.dispose(); 
        return bufImg;
	}
	/**
	 * 创建一个透明背景的文本图片
	 * @param fname 字体,例如:宋体，微软雅黑...
	 * @param fstyle 字体的样式,例如:粗体...
	 * @param fsize 字体的大小
	 * @param fcolor 字体的颜色
	 * @param text 文本内容
	 * @return
	 */
	public static BufferedImage createTextImg(String fname,int fstyle,int fsize,Color fcolor,String text){
		int height = fsize+8;
		//为了透明背景，这个img是要被丢弃的因此，长宽不重要
		BufferedImage bufImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);  
		//创建画布
        Graphics2D g = bufImg.createGraphics(); 
        //创建文本
        Font font = new Font(fname, fstyle, fsize);
        //为画布设置字体(用于计算文本的长度)
        g.setFont(font);
        //计算文本的宽度
        int width = ThumbnailsUtils.getTextGLength(text,g);
        //创建新的透明图片设置透明背景
        bufImg = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT); 
        //释放对象
        g.dispose(); 
        //生成新的画布
        g = bufImg.createGraphics(); 
        /* 消除java.awt.Font字体的锯齿 */ 
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
                RenderingHints.VALUE_ANTIALIAS_ON);
        //设置字体颜色
        g.setColor(fcolor); 
        //为新的画布设置字体
        g.setFont(font);  
        //拖拽字体到画布的对应位置
        g.drawString(text, 0, fsize); //后面两个参数是左下角的xy
        //释放对象 
        g.dispose(); 
        return bufImg;
	}
	
	
	/**
	 * 剪切图片
	 * @param srcPath 原图片的路径
	 * @param sx 剪切区域的左上角x坐标
	 * @param sy 剪切区域的左上角y坐标
	 * @param width 剪切的宽
	 * @param height 剪切的长
	 * @return
	 */
	public static BufferedImage sourceRegion(String srcPath,final int sx,final int sy,int width,int height){
	    try {
			return Thumbnails.of(srcPath)  
			.sourceRegion(ThumbnailsUtils.getPosition(sx, sy), width,height)//裁剪
			.size(width,height)  
			.asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return null;
	}
	
	/**
	 * 剪切图片
	 * @param srcPath 原图片的路径
	 * @param position
	 * @param width 剪切的宽
	 * @param height 剪切的长
	 * @return
	 */
	public static BufferedImage sourceRegion(String srcPath,Position position,int width,int height){
		try {
			return Thumbnails.of(srcPath)  
					.sourceRegion(position, width,height)//裁剪
					.size(width,height)  
					.asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return null;
	}
	
	/**
	 * 剪切图片
	 * @param srcImg 原图片的对象
	 * @param sx 剪切区域的左上角x坐标
	 * @param sy 剪切区域的左上角y坐标
	 * @param width 剪切的宽
	 * @param height 剪切的长
	 * @return
	 */
	public static BufferedImage sourceRegion(BufferedImage srcImg,final int sx,final int sy,int width,int height){
	    try {
			return Thumbnails.of(srcImg)  
			.sourceRegion(ThumbnailsUtils.getPosition(sx, sy), width,height)//裁剪
			.size(width,height)  
			.asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return null;
	}
	/**
	 * 剪切图片
	 * @param srcImg 原图片的对象
	 * @param sx 剪切区域的左上角x坐标
	 * @param sy 剪切区域的左上角y坐标
	 * @param width 剪切的宽
	 * @param height 剪切的长
	 * @param dwidth 缩减的长
	 * @param dheight 缩减的宽
	 * @return
	 */
	public static BufferedImage sourceRegion(InputStream srcImg,final int sx,final int sy,int width,int height,int dwidth,int dheight){
		try {
			return Thumbnails.of(srcImg)  
					.sourceRegion(ThumbnailsUtils.getPosition(sx, sy), width,height)//裁剪
					.size(dwidth,dheight)
					.outputQuality(1)
					//是否保持原图片的长宽比例
					.keepAspectRatio(false)
					.asBufferedImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 剪切图片
	 * @param srcImg 原图片的对象
	 * @param position
	 * @param width 剪切的宽
	 * @param height 剪切的长
	 * @return
	 */
	public static BufferedImage sourceRegion(BufferedImage srcImg,Position position,int width,int height){
		try {
			return Thumbnails.of(srcImg)  
					.sourceRegion(position, width,height)//裁剪
					.size(width,height)  
					.asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return null;
	}
	/**
	 * 打水印
	 * @param backImg 底图的路径
	 * @param markImg 水印图片对象
	 * @param insertX 水印插入的x坐标
	 * @param insertY 水印插入的y坐标
	 * @param opacity 水印透明度
	 * @return
	 */
	public static BufferedImage watermark(String backImg,BufferedImage markImg,final int insertX,final int insertY,float opacity){
		try {
			return Thumbnails.of(backImg) 
			.scale(1f)//原尺寸
			.watermark(ThumbnailsUtils.getPosition(insertX, insertY), markImg, opacity)
			.asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 打水印
	 * @param backImg 底图的路径
	 * @param markImg 水印图片对象
	 * @param insertX 水印插入的x坐标
	 * @param insertY 水印插入的y坐标
	 * @param opacity 水印透明度
	 * @return
	 */
	public static BufferedImage watermark(byte[] backImg,BufferedImage markImg,final int insertX,final int insertY,float opacity){
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(backImg);
			return Thumbnails.of(in) 
					.scale(1f)//原尺寸
					.watermark(ThumbnailsUtils.getPosition(insertX, insertY), markImg, opacity)
					.asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * 打水印
	 * @param backImg 底图的路径
	 * @param markImg 水印图片对象
	 * @param position 水印的位置
	 * @param opacity 水印透明度
	 * @return
	 */
	public static BufferedImage watermark(String backImg,BufferedImage markImg,Position position,float opacity){
		try {
			return Thumbnails.of(backImg) 
					.scale(1f)//原尺寸
					.watermark(position, markImg, opacity)
					.asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 打水印
	 * @param backImg 底图的路径
	 * @param markImg 水印图片路径
	 * @param insertX 水印插入的x坐标
	 * @param insertY 水印插入的y坐标
	 * @param opacity 水印透明度
	 * @return
	 */
	public static BufferedImage watermark(String backImg,String markImg,final int insertX,final int insertY,float opacity){
		try {
			return Thumbnails.of(backImg) 
					.scale(1f)//原尺寸
					.watermark(ThumbnailsUtils.getPosition(insertX, insertY), ImageIO.read(new File(markImg)), opacity)
					.asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 打水印
	 * @param backImg 底图的路径
	 * @param markImg 水印图片路径
	 * @param position 水印的位置
	 * @param opacity 水印透明度
	 * @return
	 */
	public static BufferedImage watermark(String backImg,String markImg,Position position,float opacity){
		try {
			return Thumbnails.of(backImg) 
					.scale(1f)//原尺寸
					.watermark(position, ImageIO.read(new File(markImg)), opacity)
					.asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 打水印
	 * @param backImg 底图的路径
	 * @param markImg 水印图片路径
	 * @param savePath 新图片保存的路径
	 * @param position 水印的位置
	 * @param opacity 水印透明度
	 * @return
	 */
	public static String watermark(String backImg,String markImg,String savePath,Position position,float opacity){
		try {
			Thumbnails.of(backImg) 
			.scale(1f)//原尺寸
			.watermark(position, ImageIO.read(new File(markImg)), opacity)
			.toFile(savePath);
			return savePath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 打水印
	 * @param backImg 底图的路径
	 * @param markImg 水印图片路径
	 * @param savePath 新图片保存路径
	 * @param insertX 水印插入的x坐标
	 * @param insertY 水印插入的y坐标
	 * @param opacity 水印透明度
	 * @return
	 */
	public static String watermark(String backImg,String markImg,String savePath,final int insertX,final int insertY,float opacity){
		try {
			Thumbnails.of(backImg) 
					.scale(1f)//原尺寸
					.watermark(ThumbnailsUtils.getPosition(insertX, insertY), ImageIO.read(new File(markImg)), opacity)
					.toFile(savePath);
			return savePath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 打水印
	 * @param backImg 底图的路径
	 * @param markImg 水印图片对象
	 * @param savePath 新图片的保存位置
	 * @param position 水印的位置
	 * @param opacity 水印透明度
	 * @return
	 */
	public static String watermark(String backImg,BufferedImage markImg,String savePath,Position position,float opacity){
		try {
			Thumbnails.of(backImg) 
					.scale(1f)//原尺寸
					.watermark(position, markImg, opacity)
					.toFile(savePath);
			return savePath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 打水印
	 * @param backImg 底图的路径
	 * @param markImg 水印图片对象
	 * @param savePath 新图片保存路径
	 * @param insertX 水印插入的x坐标
	 * @param insertY 水印插入的y坐标
	 * @param opacity 水印透明度
	 * @return
	 */
	public static String watermark(String backImg,BufferedImage markImg,String savePath,final int insertX,final int insertY,float opacity){
		try {
			Thumbnails.of(backImg) 
			.scale(1f)//原尺寸
			.watermark(ThumbnailsUtils.getPosition(insertX, insertY), markImg, opacity)
			.toFile(savePath);
			return savePath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 打水印
	 * @param backImg 底图的路径
	 * @param markImg 水印图片对象
	 * @param savePath 新图片保存路径
	 * @param insertX 水印插入的x坐标
	 * @param insertY 水印插入的y坐标
	 * @param opacity 水印透明度
	 * @return
	 */
	public static String watermark(BufferedImage backImg,BufferedImage markImg,String savePath,final int insertX,final int insertY,float opacity){
		try {
			Thumbnails.of(backImg) 
			.scale(1f)//原尺寸
			.watermark(ThumbnailsUtils.getPosition(insertX, insertY), markImg, opacity)
			.toFile(savePath);
			return savePath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 打水印
	 * @param backImg 底图的路径
	 * @param markImg 水印图片对象
	 * @param insertX 水印插入的x坐标
	 * @param insertY 水印插入的y坐标
	 * @param opacity 水印透明度
	 * @return
	 */
	public static BufferedImage watermark(BufferedImage backImg,BufferedImage markImg,final int insertX,final int insertY,float opacity){
		try {
			return Thumbnails.of(backImg) 
			.scale(1f)//原尺寸
			.watermark(ThumbnailsUtils.getPosition(insertX, insertY), markImg, opacity)
			.asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 旋转图片
	 * @param img
	 * @param rotate 旋转的角度，正数顺时针，负数逆时针
	 * @return
	 */
	public static BufferedImage rotateImg(BufferedImage img,int rotate){
		try {
			return Thumbnails.of(img)
			.scale(1f)//原尺寸
			//正数顺时针，负数逆时针旋转几度
			.rotate(rotate)
			.asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 旋转图片
	 * @param img 图片路径
	 * @param rotate 旋转的角度，正数顺时针，负数逆时针
	 * @return
	 */
	public static BufferedImage rotateImg(String img,int rotate){
		try {
			return Thumbnails.of(img)
					.scale(1f)//原尺寸
					//正数顺时针，负数逆时针旋转几度
					.rotate(rotate)
					.asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 指定尺寸修改图片大小
	 * @param srcPath
	 * @param width
	 * @param height
	 * @param keepAspectRatio 是否保持原图片的比例
	 * @param quality 压缩质量0-1
	 * @return
	 */
	public static BufferedImage channgeSize(String srcPath,int width,int height,boolean keepAspectRatio,double quality){
		try {
			return Thumbnails.of(srcPath)
			//设定图片大小
			.size(width, height)
			.outputQuality(quality)
			//是否保持原图片的长宽比例
			.keepAspectRatio(keepAspectRatio)
			//写图片
			.asBufferedImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 指定尺寸修改图片大小
	 * @param img
	 * @param width
	 * @param height
	 * @param keepAspectRatio 是否保持原图片的比例
	 * @param quality 压缩质量0-1
	 * @return
	 */
	public static BufferedImage channgeSize(BufferedImage img,int width,int height,boolean keepAspectRatio,double quality){
		try {
			return Thumbnails.of(img)
					//设定图片大小
					.size(width, height)
					.outputQuality(quality)
					//是否保持原图片的长宽比例
					.keepAspectRatio(keepAspectRatio)
					//写图片
					.asBufferedImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 指定尺寸修改图片大小
	 * @param srcPath
	 * @param savePath
	 * @param width
	 * @param height
	 * @param keepAspectRatio 是否保持原图片的比例
	 * @param quality 压缩质量0-1
	 * @return
	 */
	public static String channgeSize(String srcPath,String savePath,int width,int height,boolean keepAspectRatio,double quality){
		try {
			Thumbnails.of(srcPath)
					//设定图片大小
					.size(width, height)
					.outputQuality(quality)
					//是否保持原图片的长宽比例
					.keepAspectRatio(keepAspectRatio)
					//写图片
					.toFile(savePath);
			return savePath;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 指定尺寸修改图片大小
	 * @param img
	 * @param width
	 * @param height
	 * @param keepAspectRatio 是否保持原图片的比例
	 * @param quality 压缩质量0-1
	 * @return
	 */
	public static BufferedImage channgeSize(byte[] img,int width,int height,boolean keepAspectRatio,double quality){
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(img);
			return Thumbnails.of(in)
					//设定图片大小
					.size(width, height)
					.outputQuality(quality)
					//是否保持原图片的长宽比例
					.keepAspectRatio(keepAspectRatio)
					//写图片
					.asBufferedImage();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 指定尺寸修改图片大小
	 * @param img
	 * @param savePath
	 * @param width
	 * @param height
	 * @param keepAspectRatio 是否保持原图片的比例
	 * @param quality 压缩质量0-1
	 * @return
	 */
	public static void channgeSize(byte[] img,String savePath,int width,int height,boolean keepAspectRatio,double quality){
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(img);
			Thumbnails.of(in)
					//设定图片大小
					.size(width, height)
					.outputQuality(quality)
					//是否保持原图片的长宽比例
					.keepAspectRatio(keepAspectRatio)
					//写图片
					.toFile(savePath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 指定尺寸修改图片大小
	 * @param img
	 * @param savePath
	 * @param width
	 * @param height
	 * @param keepAspectRatio 是否保持原图片的比例
	 * @param quality 压缩质量0-1
	 * @return
	 */
	public static String channgeSize(BufferedImage img,String savePath,int width,int height,boolean keepAspectRatio,double quality){
		try {
			Thumbnails.of(img)
			//设定图片大小
			.size(width, height)
			.outputQuality(quality)
			//是否保持原图片的长宽比例
			.keepAspectRatio(keepAspectRatio)
			//写图片
			.toFile(savePath);
			return savePath;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 指定比例修改图片大小
	 * @param srcPath
	 * @param scale 缩放的比例
	 * @param quality 压缩质量0-1
	 * @return
	 */
	public static BufferedImage channgeRatio(String srcPath,double scale,double quality){
		try {
			return Thumbnails.of(srcPath)
			//设定图片缩放比例
			.scale(scale)
			.outputQuality(quality)
			//写图片
			.asBufferedImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 指定比例修改图片大小
	 * @param img
	 * @param scale 缩放的比例
	 * @param quality 压缩质量0-1
	 * @return
	 */
	public static BufferedImage channgeRatio(BufferedImage img,double scale,double quality){
		try {
			return Thumbnails.of(img)
					//设定图片缩放比例
					.scale(scale)
					.outputQuality(quality)
					//写图片
					.asBufferedImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 指定比例修改图片大小
	 * @param srcPath
	 * @param savePath
	 * @param scale 缩放的比例
	 * @param quality 压缩质量0-1
	 * @return
	 */
	public static String channgeRatio(String srcPath,String savePath,double scale,double quality){
		try {
			Thumbnails.of(srcPath)
					//设定图片缩放比例
					.scale(scale)
					.outputQuality(quality)
					//写图片
					.toFile(savePath);
			return savePath;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 指定比例修改图片大小
	 * @param img
	 * @param savePath
	 * @param scale 缩放的比例
	 * @return
	 */
	public static String channgeRatio(BufferedImage img,String savePath,double scale){
		try {
			Thumbnails.of(img)
			//设定图片缩放比例
			.scale(scale)
			//写图片
			.toFile(savePath);
			return savePath;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/** 
     * 获取位置图片文字的长度 
     * @param text 文字
     * @param g 画布
     * @return 文字总长度 
     */  
    public static int getTextGLength(String text, Graphics2D g) {  
        return g.getFontMetrics(g.getFont()).charsWidth(text.toCharArray(), 0, text.length());  
    }
    /**
	 * 根据myx，myy坐标返回对应的位置对象
	 * @param myx
	 * @param myy
	 * @return
	 */
	public static Position getPosition(final int myx,final int myy){
		return new Position() {
			@Override
			public Point calculate(int enclosingWidth, int enclosingHeight, int width,
					int height, int insetLeft, int insetRight, int insetTop,
					int insetBottom) {
				int x = myx;
				int y = myy;
				if(x < 0){
					x = 0; 
				}else if(x > (enclosingWidth-insetRight)){
					x = (enclosingWidth-insetRight);
				}
				if(y < 0){
					y = 0;
				}else if(y > (enclosingHeight-insetBottom)){
					y = (enclosingHeight-insetBottom);
				}
				return new Point(x, y);
			}
		};
	}
	
	/**
	 * 加载外部字体
	 * @param fontFileName
	 * @param fontSize
	 * @return
	 */
	public static Font loadFont(String fontFileName,int fontStyle,float fontSize){//第一个参数是外部字体名，第二个是字体大小
		try{
			File font = getFont(fontFileName);
			if(font == null){
				throw new RuntimeException("找不到字体:"+fontFileName);
			}
			Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT, font);
			Font dynamicFontPt = dynamicFont.deriveFont(fontSize).deriveFont(fontStyle);
			return dynamicFontPt;
		}catch(Exception e){
			e.printStackTrace();
			return new Font("宋体", fontStyle, new Float(fontSize).intValue());//异常返回宋体
		}
	}
	/**
	 * @param ttfName ttf全路径
	 * @return
	 */
	public static File getFont(String ttfName){
		String key = "FONT_CACHE_"+ttfName;
		//检查缓存
		File font = fontCache.get(key);
		if(font != null){
			return font;
		}
		//读取ttf文件
		synchronized (ttfName) {
			try {//同一个对象就会线程同步等待
				//再一次访问内存,如果有缓存则返回
				font = fontCache.get(key);
				if(font != null){
					return font;//返回缓存
				}
				font = new File(ttfName);
				fontCache.put(key, font);
				return font;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
