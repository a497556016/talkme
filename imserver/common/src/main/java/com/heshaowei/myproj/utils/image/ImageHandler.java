package com.heshaowei.myproj.utils.image;

import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * 图像处理类.
 *
 * @author nagsh
 */
public class ImageHandler {
    private BufferedImage bufferedImage;

    String openUrl; // 原始图片打开路径
    String saveUrl; // 新图保存路径
    String suffix; // 新图类型 只支持gif,jpg,png

    public ImageHandler(String openUrl, String saveUrl, String suffix) throws Exception {
        this.openUrl = openUrl;
        this.saveUrl = saveUrl;
        this.suffix = suffix;

        setBufferedImage();
    }

    public ImageHandler(String openUrl, String saveUrl) throws Exception {
        this.openUrl = openUrl;
        this.saveUrl = saveUrl;
        this.suffix = this.getImageFormatName(openUrl);

        setBufferedImage();
    }


    /**
     * 获取图片格式
     * @param openUrl   图片文件
     * @return    图片格式
     */
    public String getImageFormatName(String openUrl)throws IOException{
        File file = new File(openUrl);
        String formatName = null;

        ImageInputStream iis = ImageIO.createImageInputStream(file);
        Iterator<ImageReader> imageReader =  ImageIO.getImageReaders(iis);
        if(imageReader.hasNext()){
            ImageReader reader = imageReader.next();
            formatName = reader.getFormatName();
        }

        if(null != formatName) {
            formatName = formatName.toLowerCase();
        }

        return formatName;
    }


    private void setBufferedImage() throws Exception {
        /*File file = new File(openUrl);
        if (!file.isFile()) {
            throw new Exception("ImageHandler>>>" + file + " 不是一个图片文件!");
        }
        try {
            this.bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        Image src = Toolkit.getDefaultToolkit().getImage(openUrl);
        this.bufferedImage = toBufferedImage(src);//Image to BufferedImage
    }

    private static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    private String getSuffix() {
        if (StringUtils.isEmpty(suffix)) {
            return "jpg";
        }
        return suffix;
    }


    public int getWidth() {
        return this.bufferedImage.getWidth();
    }

    public int getHeight() {
        return this.bufferedImage.getHeight();
    }

    public String writeToFile() {
        if (null == saveUrl) {
            throw new RuntimeException("没有指定图片保存路径！");
        }

        int lastIndex = saveUrl.lastIndexOf(File.separatorChar);
        String path = saveUrl.substring(0, lastIndex);
        String name = saveUrl.substring(lastIndex+1);
        File sf = new File(path, name);
        try {
            if (sf.getParentFile().exists() || sf.getParentFile().mkdirs()) {
                ImageIO.write(this.bufferedImage, getSuffix(), sf); // 保存图片
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sf.getAbsolutePath();
    }

    public byte[] writeToBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            boolean b = ImageIO.write(this.bufferedImage, getSuffix(), outputStream);
            if (b) {
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 图片缩放.
     *
     * @param width  需要的宽度
     * @param height 需要的高度
     */
    public ImageHandler zoom(int width, int height) {
        double sx = 0.0;
        double sy = 0.0;

        // 计算x轴y轴缩放比例--如需等比例缩放，在调用之前确保参数width和height是等比例变化的
        sx = (double) width / this.bufferedImage.getWidth();
        sy = (double) height / this.bufferedImage.getHeight();

        AffineTransformOp op = new AffineTransformOp(
                AffineTransform.getScaleInstance(sx, sy), null);

        this.bufferedImage = op.filter(this.bufferedImage, null);

        return this;
    }

    /**
     * 按宽度等比例缩放
     *
     * @param width
     * @return
     */
    public ImageHandler scaleW(int width) {
        double x = (double) width / this.getWidth();
        int height = (int) (x * this.getHeight());
        return this.zoom(width, height);
    }

    /**
     * 按高度等比例缩放
     *
     * @param height
     * @return
     */
    public ImageHandler scaleH(int height) {
        double x = (double) height / this.getHeight();
        int width = (int) (x * this.getWidth());
        return this.zoom(width, height);
    }

    /**
     * 旋转
     *
     * @param degree 旋转角度
     * @throws Exception
     */
    public ImageHandler spin(int degree) {
        int swidth = 0; // 旋转后的宽度
        int sheight = 0; // 旋转后的高度
        int x; // 原点横坐标
        int y; // 原点纵坐标

        // 处理角度--确定旋转弧度
        degree = degree % 360;
        if (degree < 0)
            degree = 360 + degree;// 将角度转换到0-360度之间
        double theta = Math.toRadians(degree);// 将角度转为弧度

        // 确定旋转后的宽和高
        if (degree == 180 || degree == 0 || degree == 360) {
            swidth = this.bufferedImage.getWidth();
            sheight = this.bufferedImage.getHeight();
        } else if (degree == 90 || degree == 270) {
            sheight = this.bufferedImage.getWidth();
            swidth = this.bufferedImage.getHeight();
        } else {
            swidth = (int) (Math.sqrt(this.bufferedImage.getWidth() * this.bufferedImage.getWidth()
                    + this.bufferedImage.getHeight() * this.bufferedImage.getHeight()));
            sheight = (int) (Math.sqrt(this.bufferedImage.getWidth() * this.bufferedImage.getWidth()
                    + this.bufferedImage.getHeight() * this.bufferedImage.getHeight()));
        }

        x = (swidth / 2) - (this.bufferedImage.getWidth() / 2);// 确定原点坐标
        y = (sheight / 2) - (this.bufferedImage.getHeight() / 2);

        BufferedImage spinImage = new BufferedImage(swidth, sheight,
                this.bufferedImage.getType());
        // 设置图片背景颜色
        Graphics2D gs = (Graphics2D) spinImage.getGraphics();
        gs.setColor(Color.white);
        gs.fillRect(0, 0, swidth, sheight);// 以给定颜色绘制旋转后图片的背景

        AffineTransform at = new AffineTransform();
        at.rotate(theta, swidth / 2, sheight / 2);// 旋转图象
        at.translate(x, y);
        AffineTransformOp op = new AffineTransformOp(at,
                AffineTransformOp.TYPE_BICUBIC);

        this.bufferedImage = op.filter(this.bufferedImage, spinImage);

        return this;
    }

    /**
     * 马赛克化.
     *
     * @param size 马赛克尺寸，即每个矩形的长宽
     * @return
     * @throws Exception
     */
    public ImageHandler mosaic(int size) throws Exception {

        BufferedImage spinImage = new BufferedImage(this.bufferedImage.getWidth(),
                this.bufferedImage.getHeight(), this.bufferedImage.TYPE_INT_RGB);
        if (this.bufferedImage.getWidth() < size || this.bufferedImage.getHeight() < size || size <= 0) { // 马赛克格尺寸太大或太小
            return null;
        }

        int xcount = 0; // 方向绘制个数
        int ycount = 0; // y方向绘制个数
        if (this.bufferedImage.getWidth() % size == 0) {
            xcount = this.bufferedImage.getWidth() / size;
        } else {
            xcount = this.bufferedImage.getWidth() / size + 1;
        }
        if (this.bufferedImage.getHeight() % size == 0) {
            ycount = this.bufferedImage.getHeight() / size;
        } else {
            ycount = this.bufferedImage.getHeight() / size + 1;
        }
        int x = 0;   //坐标
        int y = 0;
        // 绘制马赛克(绘制矩形并填充颜色)
        Graphics gs = spinImage.getGraphics();
        for (int i = 0; i < xcount; i++) {
            for (int j = 0; j < ycount; j++) {
                //马赛克矩形格大小
                int mwidth = size;
                int mheight = size;
                if (i == xcount - 1) {   //横向最后一个比较特殊，可能不够一个size
                    mwidth = this.bufferedImage.getWidth() - x;
                }
                if (j == ycount - 1) {  //同理
                    mheight = this.bufferedImage.getHeight() - y;
                }
                // 矩形颜色取中心像素点RGB值
                int centerX = x;
                int centerY = y;
                if (mwidth % 2 == 0) {
                    centerX += mwidth / 2;
                } else {
                    centerX += (mwidth - 1) / 2;
                }
                if (mheight % 2 == 0) {
                    centerY += mheight / 2;
                } else {
                    centerY += (mheight - 1) / 2;
                }
                Color color = new Color(this.bufferedImage.getRGB(centerX, centerY));
                gs.setColor(color);
                gs.fillRect(x, y, mwidth, mheight);
                y = y + size;// 计算下一个矩形的y坐标
            }
            y = 0;// 还原y坐标
            x = x + size;// 计算x坐标
        }

        this.bufferedImage = spinImage;

        gs.dispose();

        return this;
    }

    public static void main(String[] args) throws Exception {
        ImageHandler imageDeal = new ImageHandler("f://a.jpg", "f://b.jpg", "jpg");
        // 测试缩放
        System.out.println(imageDeal.scaleH(100).writeToFile());
        // 测试旋转
        /* imageDeal.spin(90); */
        //测试马赛克
        /*imageDeal.mosaic(4);*/
    }

}