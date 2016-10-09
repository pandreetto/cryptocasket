package oss.crypto.casket.stego.bitmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class StegoCodec {

    private static final String HEADER_TPL = "size----";

    public static byte[] encodeHeader(int size) {
        byte[] result = HEADER_TPL.getBytes();
        result[4] = (byte) ((size & 0xff000000) >> 24);
        result[5] = (byte) ((size & 0xff0000) >> 16);
        result[6] = (byte) ((size & 0xff00) >> 8);
        result[7] = (byte) (size & 0xff);
        return result;
    }

    public static int decodeHeader(byte[] bSize) {
        String prefix = new String(bSize, 0, 4);
        if (!HEADER_TPL.startsWith(prefix))
            return -1;

        int result = 0;
        result |= (bSize[4] & 0xff) << 24;
        result |= (bSize[5] & 0xff) << 16;
        result |= (bSize[6] & 0xff) << 8;
        result |= bSize[7] & 0xff;
        return result;
    }

    public static byte[] decode(Context ctx, Uri pictureURI)
        throws IOException {

        ParcelFileDescriptor parcelFileDescriptor = ctx.getContentResolver().openFileDescriptor(pictureURI, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap tgtImg = BitmapFactory.decodeFileDescriptor(fileDescriptor);

        ByteArrayOutputStream hdrStream = new ByteArrayOutputStream();
        ByteArrayOutputStream secretStream = new ByteArrayOutputStream();

        boolean readHdr = true;
        boolean stop = false;
        int size = 0;
        PixelDemux sDemux = new PixelDemux(hdrStream, 4);
        PixelDemux pDemux = null;
        for (int y = 0; y < tgtImg.getHeight(); y++) {
            for (int x = 0; x < tgtImg.getWidth(); x++) {
                if (readHdr) {
                    readHdr = sDemux.demux(new Pixel(tgtImg.getPixel(x, y)));
                }

                if (!readHdr) {
                    if (size == 0) {
                        size = decodeHeader(hdrStream.toByteArray());
                        if (size < 0)
                            throw new IOException("Cannot find header");

                        pDemux = new PixelDemux(secretStream, size);
                    }
                    if (!pDemux.demux(new Pixel(tgtImg.getPixel(x, y)))) {
                        stop = true;
                        break;
                    }
                }
            }
            if (stop)
                break;
        }

        sDemux.close();
        pDemux.close();
        secretStream.close();

        return secretStream.toByteArray();

    }

    public static void encode(Context ctx, Uri pictureURI, byte[] payload)
        throws IOException {

        ParcelFileDescriptor parcelFileDescriptor = ctx.getContentResolver().openFileDescriptor(pictureURI, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap srcImg = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        Bitmap tgtImg = srcImg.copy(Bitmap.Config.ARGB_8888, true);

        ByteArrayInputStream hdrStream = new ByteArrayInputStream(encodeHeader(payload.length));
        PixelMux sMux = new PixelMux(hdrStream);

        ByteArrayInputStream secretStream = new ByteArrayInputStream(payload);
        PixelMux pMux = new PixelMux(secretStream);

        boolean writeHdr = true;
        boolean stop = false;

        for (int y = 0; y < tgtImg.getHeight(); y++) {
            for (int x = 0; x < tgtImg.getWidth(); x++) {

                Pixel inPix = new Pixel(tgtImg.getPixel(x, y));

                if (writeHdr) {
                    Pixel outPix = sMux.mux(inPix);
                    if (outPix != null) {
                        tgtImg.setPixel(x, y, outPix.getPixel());
                    } else {
                        writeHdr = false;
                    }
                }

                if (!writeHdr) {
                    Pixel outPix = pMux.mux(inPix);
                    if (outPix != null) {
                        tgtImg.setPixel(x, y, outPix.getPixel());
                    } else {
                        stop = true;
                        break;
                    }
                }
            }
            if (stop)
                break;
        }

        hdrStream.close();
        sMux.close();
        secretStream.close();
        pMux.close();

        parcelFileDescriptor = ctx.getContentResolver().openFileDescriptor(pictureURI, "w");
        fileDescriptor = parcelFileDescriptor.getFileDescriptor();

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(fileDescriptor);
            srcImg.compress(Bitmap.CompressFormat.PNG, 90, fOut);
        } finally {
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException ioEx) {

                }
            }
        }

    }

}