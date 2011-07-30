/*
 * UncompressedLZMA2OutputStream
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package org.tukaani.xz;

import java.io.DataOutputStream;
import java.io.IOException;

class UncompressedLZMA2OutputStream extends FinishableOutputStream {
    private final FinishableOutputStream out;
    private final DataOutputStream outData;

    private final byte[] uncompBuf
            = new byte[LZMA2OutputStream.COMPRESSED_SIZE_MAX];
    private int uncompPos = 0;
    private boolean dictResetNeeded = true;

    static int getMemoryUsage() {
        // uncompBuf + a little extra
        return 70;
    }

    UncompressedLZMA2OutputStream(FinishableOutputStream out) {
        this.out = out;
        outData = new DataOutputStream(out);
    }

    public void write(int b) throws IOException {
        byte[] buf = new byte[1];
        buf[0] = (byte)b;
        write(buf, 0, 1);
    }

    public void write(byte[] buf, int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len < 0 || off + len > buf.length)
            throw new IndexOutOfBoundsException();

        while (len > 0) {
            int copySize = Math.min(uncompBuf.length - uncompPos, len);
            System.arraycopy(buf, off, uncompBuf, uncompPos, copySize);
            len -= copySize;
            uncompPos += copySize;

            if (uncompPos == uncompBuf.length)
                writeChunk();
        }
    }

    private void writeChunk() throws IOException {
        outData.writeByte(dictResetNeeded ? 0x01 : 0x02);
        outData.writeShort(uncompPos - 1);
        outData.write(uncompBuf, 0, uncompPos);
        uncompPos = 0;
        dictResetNeeded = false;
    }

    public void flush() throws IOException {
        if (uncompPos > 0)
            writeChunk();

        out.flush();
    }

    public void finish() throws IOException {
        if (uncompPos > 0)
            writeChunk();

        out.write(0x00);
        out.finish();
    }

    public void close() throws IOException {
        if (uncompPos > 0)
            writeChunk();

        out.write(0x00);
        out.close();
    }
}
