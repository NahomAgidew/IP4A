#IP4A

Copyright (2016) Nahom Abi.

Image Processing for Android is a library that provides over 30 image filters.

#Usage

Download this repo and include it in your project.

#Documentation
Example to highlight an image:
Bitmap highlightedImage = IP4A.highlight(srcBitmap);

Other filters are listed below:
IP4A.invert(Bitmap src) </br>
IP4A.grayscale(Bitmap src) </br>
IP4A.correctGamma(Bitmap src, double red, double green, double blue)
IP4A.filterColor(Bitmap src, double red, double green, double blue)
IP4A.sepiaToning(Bitmap src, int depth, double red, double green, double blue)
IP4A.decreaseColorDepth(Bitmap src, int bitOffset)
IP4A.createContrast(Bitmap src, double value)
IP4A.rotate(Bitmap src, float degree)
IP4A.doBrightness(Bitmap src, int value)
IP4A.applyGaussianBlur(Bitmap src)
IP4A.sharpen(Bitmap src, double weight)
IP4A.applyMeanRemoval(Bitmap src)
IP4A.smooth(Bitmap src, double value)
IP4A.emboss(Bitmap src)
IP4A.engrave(Bitmap src, double value)
IP4A.boostIntensity(Bitmap src, int type, float percent)
IP4A.roundCorner(Bitmap src, float round)
IP4A.watermark(Bitmap src, String watermark, Point location, int color, int alpha, int size, boolean underline)
IP4A.flip(Bitmap src, int type)
IP4A.applyHueFilter(Bitmap source, int level)
IP4A.applySaturationFilter(Bitmap source, int level)
IP4A.applyShadingFilter(Bitmap source, int shadingColor)
IP4A.applySnowEffect(Bitmap source)
IP4A.applyFleaEffect(Bitmap source)

#Contributing
Create a pull request.
