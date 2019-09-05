package com.heshaowei.myproj.utils.image;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.madgag.gif.fmsware.GifDecoder;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class GifUtils {

    /**
     * 按宽度等比例缩放
     *
     * @param width
     * @return
     */
    public static void zoomW(String sourcePath, OutputStream os, int width) throws IOException {
        zoom(sourcePath, os, width, 0);
    }

    /**
     * 按高度等比例缩放
     *
     * @param height
     * @return
     */
    public static void zoomH(String sourcePath, OutputStream os, int height) throws IOException {
        zoom(sourcePath, os, 0, height);
    }

    public static void zoom(String sourcePath, OutputStream os, int width, int height) throws IOException {
        GifDecoder decoder = new GifDecoder();
        int status = decoder.read(sourcePath);
        if (status != GifDecoder.STATUS_OK) {
            throw new IOException("read image " + sourcePath + " error!");
        }



        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(os);
        encoder.setRepeat(decoder.getLoopCount());
        for (int i = 0; i < decoder.getFrameCount(); i ++) {
            encoder.setDelay(decoder.getDelay(i));

            BufferedImage bi = decoder.getFrame(i);
            if(width == 0){
                double x = (double) height / bi.getHeight();
                width = (int) (x * bi.getWidth());
            }
            if(height == 0){
                double x = (double) width / bi.getWidth();
                 height = (int) (x * bi.getHeight());
            }

            BufferedImage image = zoom(bi, width , height);
            encoder.addFrame(image);
        }
        encoder.finish();
        os.flush();
        os.close();
    }

    /**
     * 压缩图片
     * @param sourceImage    待压缩图片
     * @param width          压缩图片高度
     * @param height         压缩图片宽度
     */
    private static BufferedImage zoom(BufferedImage sourceImage , int width , int height){
        /*double sx = 0.0;
        double sy = 0.0;

        // 计算x轴y轴缩放比例--如需等比例缩放，在调用之前确保参数width和height是等比例变化的
        sx = (double) width / sourceImage.getWidth();
        sy = (double) height / sourceImage.getHeight();
        AffineTransformOp op = new AffineTransformOp(
                AffineTransform.getScaleInstance(sx, sy), null);

        return op.filter(sourceImage, null);*/
        BufferedImage zoomImage = new BufferedImage(width, height, sourceImage.getType());
        Image image = sourceImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        Graphics gc = zoomImage.getGraphics();
        gc.setColor(Color.WHITE);
        gc.drawImage( image , 0, 0, null);
        return zoomImage;
    }
}
