#IP4A

Copyright (2016) Nahom Abi.

Image Processing for Android is a library that provides over 30 image filters.

#Usage

Download this repo and include it in your project.

#Documentation
Example to highlight an image:
Bitmap highlightedImage = IP4A.highlight(srcBitmap);

Other filters are listed below: </br>
IP4A.invert(Bitmap src) </br>
IP4A.grayscale(Bitmap src) </br>
IP4A.correctGamma(Bitmap src, double red, double green, double blue)</br>
IP4A.filterColor(Bitmap src, double red, double green, double blue)</br>
IP4A.sepiaToning(Bitmap src, int depth, double red, double green, double blue)</br>
IP4A.decreaseColorDepth(Bitmap src, int bitOffset)</br>
IP4A.createContrast(Bitmap src, double value)</br>
IP4A.rotate(Bitmap src, float degree)</br>
IP4A.doBrightness(Bitmap src, int value)</br>
IP4A.applyGaussianBlur(Bitmap src)</br>
IP4A.sharpen(Bitmap src, double weight)</br>
IP4A.applyMeanRemoval(Bitmap src)</br>
IP4A.smooth(Bitmap src, double value)</br>
IP4A.emboss(Bitmap src)</br>
IP4A.engrave(Bitmap src, double value)</br>
IP4A.boostIntensity(Bitmap src, int type, float percent)</br>
IP4A.roundCorner(Bitmap src, float round)</br>
IP4A.watermark(Bitmap src, String watermark, Point location, int color, int alpha, int size, boolean underline)</br>
IP4A.flip(Bitmap src, int type)</br>
IP4A.applyHueFilter(Bitmap source, int level)</br>
IP4A.applySaturationFilter(Bitmap source, int level)</br>
IP4A.applyShadingFilter(Bitmap source, int shadingColor)</br>
IP4A.applySnowEffect(Bitmap source)</br>
IP4A.applyFleaEffect(Bitmap source)</br>

#Contributing
Create a pull request.
