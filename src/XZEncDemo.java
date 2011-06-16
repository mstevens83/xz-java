/*
 * XZEncDemo
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

import java.io.*;
import org.tukaani.xz.*;

/**
 * Compresses a single file from standard input to standard ouput into
 * the .xz file format.
 */
class XZEncDemo {
    public static void main(String[] args) throws Exception {
        LZMA2Options options = new LZMA2Options();
        XZOutputStream out = new XZOutputStream(System.out, options,
                                                XZ.CHECK_CRC64);

        byte[] buf = new byte[8192];
        int size;
        while ((size = System.in.read(buf)) != -1)
            out.write(buf, 0, size);

        out.finish();
    }
}