package com.github.nahom.ip4a;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

/**
 * Image Processing for Android
 * Provides over 30 image filters
 * @author NahomAbi
 * @version 1.0
 */
public class IP4A {

    private static final int COLOR_MIN = 0x00;
    private static final int COLOR_MAX = 0xFF;
    private static final int FLIP_VERTICAL = 1;
    private static final int FLIP_HORIZONTAL = 2;

    private static class ConvolutionMatrix{
        public static final int SIZE = 3;

        public double[][] Matrix;
        public double Factor = 1;
        public double Offset = 1;

        public ConvolutionMatrix(int size){
            Matrix = new double[size][size];
        }

        public void setAll(double value){
            for(int x = 0; x < SIZE; ++x){
                for(int y = 0; y < SIZE; ++y){
                    Matrix[x][y] = value;
                }
            }
        }

        public void applyConfig(double[][] config){
            for(int x = 0; x < SIZE; ++x){
                for(int y = 0; y < SIZE; ++y){
                    Matrix[x][y] = config[x][y];
                }
            }
        }

        public Bitmap computeConvolution3x3(Bitmap src, ConvolutionMatrix matrix){
            int width = src.getWidth();
            int height = src.getHeight();
            Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());

            int A, R, G, B;
            int sumR, sumG, sumB;
            int[][] pixels = new int[SIZE][SIZE];

            for (int y = 0; y < height - 2; ++y){
                for(int x = 0; x < width - 2; ++x){

                    for(int i = 0; i < SIZE; ++i){
                        for(int j = 0; j < SIZE; ++j){
                            pixels[i][j] = src.getPixel(x + i, y + j);
                        }
                    }

                    A = Color.alpha(pixels[1][1]);

                    sumR = sumG = sumB = 0;

                    for(int i = 0; i < SIZE; ++i){
                        for(int j = 0; j < SIZE; ++j){
                            sumR += (Color.red(pixels[i][j]) * matrix.Matrix[i][j]);
                            sumG += (Color.green(pixels[i][j]) * matrix.Matrix[i][j]);
                            sumB += (Color.blue(pixels[i][j]) * matrix.Matrix[i][j]);
                        }
                    }

                    R = (int)(sumR / matrix.Factor + matrix.Offset);
                    if(R < 0){
                        R = 0;
                    }else if(R > 255){
                        R = 255;
                    }

                    G = (int)(sumG / matrix.Factor + matrix.Offset);
                    if(R < 0){
                        R = 0;
                    }else if(R > 255){
                        R = 255;
                    }

                    B = (int)(sumB / matrix.Factor + matrix.Offset);
                    if(B < 0){
                        B = 0;
                    }else if(B > 255){
                        B = 255;
                    }

                    result.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
                }
            }

            return result;
        }
    }


    public static Bitmap highlight(Bitmap src){
        Bitmap output = Bitmap.createBitmap(src.getWidth()+96, src.getHeight()+96, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        Paint ptBlur = new Paint();
        ptBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
        int[] offsetXY = new int[2];
        Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
        Paint ptAlphaColor = new Paint();
        ptAlphaColor.setColor(0xFFFFFFFF);
        canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);
        bmAlpha.recycle();
        canvas.drawBitmap(src, 0, 0, null);

        return output;
    }

    public static Bitmap invert(Bitmap src){
        Bitmap output = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        int A, R, G, B;
        int pixelColor;
        int height = src.getHeight();
        int width = src.getWidth();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                pixelColor = src.getPixel(x, y);
                A = Color.alpha(pixelColor);
                R = 255 - Color.red(pixelColor);
                G = 255 - Color.green(pixelColor);
                B = 255 - Color.blue(pixelColor);
                output.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return output;
    }

    public static Bitmap grayscale(Bitmap src){
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;

        Bitmap output = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int A, R, G, B, pixel;

        int height = src.getHeight();
        int width = src.getWidth();

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                output.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return output;
    }

    public static Bitmap correctGamma(Bitmap src, double red, double green, double blue){
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        int width = src.getWidth();
        int height = src.getHeight();
        int A, R, G, B;
        int pixel;
        final int    MAX_SIZE = 256;
        final double MAX_VALUE_DBL = 255.0;
        final int    MAX_VALUE_INT = 255;
        final double REVERSE = 1.0;

        int[] gammaR = new int[MAX_SIZE];
        int[] gammaG = new int[MAX_SIZE];
        int[] gammaB = new int[MAX_SIZE];

        for(int i = 0; i < MAX_SIZE; ++i) {
            gammaR[i] = (int)Math.min(MAX_VALUE_INT,
                    (int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / red)) + 0.5));
            gammaG[i] = (int)Math.min(MAX_VALUE_INT,
                    (int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / green)) + 0.5));
            gammaB[i] = (int)Math.min(MAX_VALUE_INT,
                    (int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / blue)) + 0.5));
        }

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = gammaR[Color.red(pixel)];
                G = gammaG[Color.green(pixel)];
                B = gammaB[Color.blue(pixel)];
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bmOut;
    }

    public static Bitmap filterColor(Bitmap src, double red, double green, double blue){
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, src.getConfig());
        int A, R, G, B, pixel;

        for(int x = 0; x < width; ++x){
            for (int y = 0; y < height; ++y){
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = (int)(Color.red(pixel) * red);
                G = (int)(Color.green(pixel) * green);
                B = (int)(Color.blue(pixel) * blue);

                output.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return output;
    }

    public static Bitmap sepiaToning(Bitmap src, int depth, double red, double green, double blue){
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, src.getConfig());
        final double GS_RED = 0.3;
        final double GS_GREEN = 0.59;
        final double GS_BLUE = 0.11;

        int A, R, G, B, pixel;

        for (int x = 0; x < width; ++x){
            for (int y = 0; y < height; ++y){
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                B = G = R = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);

                R += (depth * red);
                if(R > 255) {R = 255;}

                G += (depth * green);
                if(G > 255) {G = 255;}

                B += (depth * blue);
                if(B > 255){B = 255;}

                output.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return output;
    }

    public static Bitmap decreaseColorDepth(Bitmap src, int bitOffset){
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B, pixel;

        for(int x = 0; x < width; ++x){
            for(int y = 0; y < height; ++y){
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                R = ((R + (bitOffset/2))-((R+(bitOffset/2))%bitOffset)-1);
                if(R < 0){R = 0;}
                G = ((G + (bitOffset/2))-((G+(bitOffset/2))%bitOffset)-1);
                if(G < 0){G=0;}
                B = ((B + (bitOffset/2))-((B+(bitOffset/2))%bitOffset)-1);
                if(B < 0){B = 0;}

                output.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return output;
    }

    public static Bitmap createContrast(Bitmap src, double value){
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B, pixel;

        double contrast = Math.pow((100 + value) / 100, 2);


        for(int x = 0; x < width; ++x){
            for(int y = 0; y < height; ++y){
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.red(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.red(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                output.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return output;
    }

    public static Bitmap rotate(Bitmap src, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static Bitmap doBrightness(Bitmap src, int value){
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B, pixel;

        for(int x = 0; x < width; ++x){
            for(int y = 0; y < height; ++y){
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                R += value;
                if(R > 255){
                    R = 255;
                } else if(R < 0){
                    R = 0;
                }

                G += value;
                if(G > 255){
                    G = 255;
                } else if(G < 0){
                    G = 0;
                }

                B += value;
                if(B > 255){
                    B = 255;
                }else if(B < 0){
                    B = 0;
                }

                output.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return output;
    }

    public static Bitmap applyGaussianBlur(Bitmap src){
        double[][] GaussianBlurConfig = new double[][]{
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };

        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(3);
        convolutionMatrix.applyConfig(GaussianBlurConfig);
        convolutionMatrix.Factor = 16;
        convolutionMatrix.Offset = 0;
        return convolutionMatrix.computeConvolution3x3(src, convolutionMatrix);
    }

    public static Bitmap sharpen(Bitmap src, double weight){
        double[][] SharpConfig = new double[][]{
                {0, -2, 0},
                {-2, weight, -2},
                {0, -2, 0}
        };

        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(3);
        convolutionMatrix.applyConfig(SharpConfig);
        convolutionMatrix.Factor = weight - 8;
        return convolutionMatrix.computeConvolution3x3(src, convolutionMatrix);
    }

    public static Bitmap applyMeanRemoval(Bitmap src){
        double[][] MeanRemovalConfig = new double[][]{
                {-1, -1, -1},
                {-1, 9, -1},
                {-1, -1, -1}
        };

        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(3);
        convolutionMatrix.applyConfig(MeanRemovalConfig);
        convolutionMatrix.Factor = 1;
        convolutionMatrix.Offset = 0;
        return convolutionMatrix.computeConvolution3x3(src, convolutionMatrix);
    }

    public static Bitmap smooth(Bitmap src, double value){
        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(3);
        convolutionMatrix.setAll(1);
        convolutionMatrix.Matrix[1][1] = value;
        convolutionMatrix.Factor = value + 8;
        convolutionMatrix.Offset = 1;
        return convolutionMatrix.computeConvolution3x3(src, convolutionMatrix);
    }

    public static Bitmap emboss(Bitmap src){
        double[][] EmbossConfig = new double[][]{
                {-1, 0, -1},
                {0, 4, 0},
                {-1, 0, -1}
        };

        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(3);
        convolutionMatrix.applyConfig(EmbossConfig);
        convolutionMatrix.Factor = 1;
        convolutionMatrix.Offset = 127;
        return convolutionMatrix.computeConvolution3x3(src, convolutionMatrix);
    }

    public static Bitmap engrave(Bitmap src, double value){
        ConvolutionMatrix convolutionMatrix = new ConvolutionMatrix(3);
        convolutionMatrix.setAll(0);
        convolutionMatrix.Matrix[0][0] = -2;
        convolutionMatrix.Matrix[1][1] = 2;
        convolutionMatrix.Factor = 1;
        convolutionMatrix.Offset = 95;
        return convolutionMatrix.computeConvolution3x3(src, convolutionMatrix);
    }

    public static Bitmap boostIntensity(Bitmap src, int type, float percent){
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B, pixel;

        for(int x = 0; x < width; ++x){
            for(int y = 0; y < height; ++y){
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                if(type == 1){
                    R = (int)(R * (1 + percent));
                    if(R > 255) R = 255;
                } else if(type == 2){
                    G = (int)(G * (1 + percent));
                    if(G > 255) G = 255;
                } else if(type == 3){
                    B = (int)(B * (1 + percent));
                    if(B > 255) B = 255;
                }
                output.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return output;
    }

    public static Bitmap roundCorner(Bitmap src, float round){
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, round, round, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, rect, rect, paint);

        return output;
    }

    public static Bitmap watermark(Bitmap src, String watermark, Point location, int color, int alpha, int size, boolean underline){
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        canvas.drawText(watermark, location.x, location.y, paint);

        return output;

    }

    public static Bitmap flip(Bitmap src, int type) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();
        // if vertical
        if(type == FLIP_VERTICAL) {
            // y = y * -1
            matrix.preScale(1.0f, -1.0f);
        }
        // if horizonal
        else if(type == FLIP_HORIZONTAL) {
            // x = x * -1
            matrix.preScale(-1.0f, 1.0f);
            // unknown type
        } else {
            return null;
        }

        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static Bitmap applyHueFilter(Bitmap source, int level) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        float[] HSV = new float[3];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);

        int index = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // convert to HSV
                Color.colorToHSV(pixels[index], HSV);
                // increase Saturation level
                HSV[0] *= level;
                HSV[0] = (float) Math.max(0.0, Math.min(HSV[0], 360.0));
                // take color back
                pixels[index] |= Color.HSVToColor(HSV);
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

    public static Bitmap applySaturationFilter(Bitmap source, int level) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        float[] HSV = new float[3];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);

        int index = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // convert to HSV
                Color.colorToHSV(pixels[index], HSV);
                // increase Saturation level
                HSV[1] *= level;
                HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
                // take color back
                pixels[index] |= Color.HSVToColor(HSV);
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

    public static Bitmap applyShadingFilter(Bitmap source, int shadingColor) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);

        int index = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // AND
                pixels[index] &= shadingColor;
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

    public static Bitmap applySnowEffect(Bitmap source) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // random object
        Random random = new Random();

        int R, G, B, index = 0, thresHold = 50;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get color
                R = Color.red(pixels[index]);
                G = Color.green(pixels[index]);
                B = Color.blue(pixels[index]);
                // generate threshold
                thresHold = random.nextInt(255);
                if(R > thresHold && G > thresHold && B > thresHold) {
                    pixels[index] = Color.rgb(COLOR_MAX, COLOR_MAX, COLOR_MAX);
                }
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

    public static Bitmap applyFleaEffect(Bitmap source) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // a random object
        Random random = new Random();

        int index = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get random color
                int randColor = Color.rgb(random.nextInt(COLOR_MAX),
                        random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));
                // OR
                pixels[index] |= randColor;
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, source.getConfig());
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }


}
