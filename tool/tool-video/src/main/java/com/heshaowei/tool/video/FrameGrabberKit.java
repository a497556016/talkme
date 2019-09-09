package com.heshaowei.tool.video;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @Description javacv获取视频第一帧图片工具类
 */
public class FrameGrabberKit {

    public static void main(String[] args) throws Exception {
        System.out.println("启动参数：\n"+Arrays.toString(args));
        long start = System.currentTimeMillis();
        if(args.length >= 2) {
            String sourcePath = args[0], targetPath = args[1];
            FrameGrabberKit fgk = new FrameGrabberKit();
            fgk.setProperties(args);
            fgk.randomGrabberFFmpegImage(sourcePath).saveToDisk(targetPath);
        }else {
            throw new IllegalArgumentException("参数不对");
        }
        System.out.println("总用时："+(System.currentTimeMillis()-start));
    }

    private void setProperties(String[] args){
        if(args.length >= 3) {
            for(int i=2;i<args.length;i++) {
                String arg = args[i];
                if(arg.contains("=")){
                    String[] strs = arg.split("=");
                    if(strs[0].equals("width")){
                        this.width = Integer.parseInt(strs[1]);
                    }
                }
            }
        }
    }

    private int width;
    private int length;

    /**
     *
     * @param filePath 视频路径
     * @throws Exception
     */
    public OutputTool randomGrabberFFmpegImage(String filePath)
            throws Exception {
        OutputTool ot = new OutputTool();

        FFmpegFrameGrabber ff = FFmpegFrameGrabber.createDefault(filePath);
        ff.start();
        String rotate =ff.getVideoMetadata("rotate");
        Frame f;
        int i = 0;
        while (i <1) {
            f =ff.grabImage();
            IplImage src = null;
            if(null !=rotate &&rotate.length() > 1) {
                OpenCVFrameConverter.ToIplImage converter =new OpenCVFrameConverter.ToIplImage();
                src =converter.convert(f);
                f =converter.convert(rotate(src, Integer.valueOf(rotate)));
            }
            ot = doExecuteFrame(f);
            i++;
        }

        ff.stop();

        return ot;
    }

    /*
     * 旋转角度的
     */
    public static IplImage rotate(IplImage src, int angle) {
        IplImage img = IplImage.create(src.height(), src.width(), src.depth(), src.nChannels());
        opencv_core.cvTranspose(src, img);
        opencv_core.cvFlip(img, img, angle);
        return img;
    }

    public OutputTool doExecuteFrame(Frame f) {

        if (null ==f ||null ==f.image) {
            return new OutputTool();
        }
        int owidth = f.imageWidth;
        int oheight = f.imageHeight;
        // 对截取的帧进行等比例缩放
        int width = this.width == 0 ? 200:this.width;
        int height = (int) (((double) width / owidth) * oheight);
        Java2DFrameConverter converter =new Java2DFrameConverter();
        BufferedImage fecthedImage = converter.getBufferedImage(f);
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        bi.getGraphics().drawImage(fecthedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH),
                0, 0, null);
//        BufferedImage bi =converter.getBufferedImage(f);

        return new OutputTool(bi);
    }


    public static class OutputTool{

        private BufferedImage bi;

        public OutputTool(){
        }

        public OutputTool(BufferedImage bi){
            this.bi = bi;
        }

        public byte[] toByteArray(){
            if(null == bi) {
                return new byte[]{};
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try {
                ImageIO.write(this.bi, "jpg", outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return outputStream.toByteArray();
        }

        public void saveToDisk(String targerFilePath){
            if(null == bi) {
                return;
            }

            File output =new File(targerFilePath);
            try {
                ImageIO.write(bi,"jpg",output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}