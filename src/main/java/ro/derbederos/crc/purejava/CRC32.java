package ro.derbederos.crc.purejava;

import ro.derbederos.crc.CRC;

import static ro.derbederos.crc.purejava.CRC32Util.fastInitLookupTableReflected;
import static ro.derbederos.crc.purejava.CRC32Util.fastInitLookupTableUnreflected;

/**
 * Byte-wise CRC implementation that can compute CRC-32 using different models.
 * We use the algorithm described by Dilip Sarwate in "Computation of Cyclic Redundancy Checks
 * via Table Look-Up", 1988
 */
public class CRC32 implements CRC {

    protected final int[] lookupTable;
    protected final int poly;
    protected final int init;
    protected final boolean refIn; // reflect input data bytes
    protected final boolean refOut; // resulted sum needs to be reversed before xor
    protected final int xorOut;
    protected int crc;

    public CRC32(int poly, int init, boolean refIn, boolean refOut, int xorOut) {
        this.poly = poly;
        this.init = init;
        this.refIn = refIn;
        this.refOut = refOut;
        this.xorOut = xorOut;
        if (refIn) {
            lookupTable = fastInitLookupTableReflected(poly);
        } else {
            lookupTable = fastInitLookupTableUnreflected(poly);
        }
        reset();
    }

    @Override
    public void reset() {
        if (refIn) {
            crc = Integer.reverse(init);
        } else {
            crc = init;
        }
    }

    @Override
    public void update(int b) {
        if (refIn) {
            crc = (crc >>> 8) ^ lookupTable[(crc ^ b) & 0xff];
        } else {
            crc = (crc << 8) ^ lookupTable[((crc >>> 24) ^ b) & 0xff];
        }
    }

    public void update(byte[] src) {
        update(src, 0, src.length);
    }

    @Override
    public void update(byte[] src, int offset, int len) {
        if (refIn) {
            crc = updateReflected(lookupTable, crc, src, offset, len);
        } else {
            crc = updateUnreflected(lookupTable, crc, src, offset, len);
        }
    }

    private static int updateReflected(int[] lookupTable, int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            localCrc = (localCrc >>> 8) ^ lookupTable[(localCrc ^ src[i]) & 0xff];
        }
        return localCrc;
    }

    private static int updateUnreflected(int[] lookupTable, int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            localCrc = (localCrc << 8) ^ lookupTable[((localCrc >>> 24) ^ src[i]) & 0xff];
        }
        return localCrc;
    }

    @Override
    public void updateBits(int b, int bits) {
        int reflectedPoly = Integer.reverse(poly);
        for (int i = 0; i < bits; i++) {
            if (refIn) {
                crc = (crc >>> 1) ^ (reflectedPoly & ~(((crc ^ b) & 1) - 1));
                b >>>= 1;
            } else {
                crc = (crc << 1) ^ (poly & ~((((crc >>> 31) ^ (b >>> 7)) & 1) - 1));
                b <<= 1;
            }
        }
    }

    @Override
    public long getValue() {
        long result = crc;
        //reflect output when necessary
        if (refOut != refIn) {
            result = Integer.reverse(crc);
        }
        result = (result ^ xorOut) & 0xFFFFFFFFL;
        return result;
    }
}
