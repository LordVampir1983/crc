package ro.derbederos.crc;

final class CRC32Util {
    static int[] fastInitLookupTableReflected(int poly) {
        int reflectedPoly = Integer.reverse(poly);
        int lookupTable[] = new int[0x100];
        lookupTable[0] = 0;
        lookupTable[0x80] = reflectedPoly;
        int v = reflectedPoly;
        for (int i = 64; i != 0; i /= 2) {
            v = (v >>> 1) ^ (reflectedPoly & ~((v & 1) - 1));
            lookupTable[i] = v;
        }
        for (int i = 2; i < 256; i *= 2) {
            for (int j = 1; j < i; j++) {
                lookupTable[i + j] = lookupTable[i] ^ lookupTable[j];
            }
        }
        return lookupTable;
    }

    static int[] fastInitLookupTableUnreflected(int poly) {
        int lookupTable[] = new int[0x100];
        lookupTable[0] = 0;
        lookupTable[1] = poly;
        int v = poly;
        for (int i = 2; i <= 128; i *= 2) {
            v = (v << 1) ^ (poly & ~(((v & Integer.MIN_VALUE) >>> 31) - 1));
            lookupTable[i] = v;
        }
        for (int i = 2; i < 256; i *= 2) {
            for (int j = 1; j < i; j++) {
                lookupTable[i + j] = lookupTable[i] ^ lookupTable[j];
            }
        }
        return lookupTable;
    }

    static int[] initLookupTableReflected(int poly) {
        int reflectedPoly = Integer.reverse(poly);
        int lookupTable[] = new int[0x100];
        for (int i = 0; i < 0x100; i++) {
            int v = i;
            for (int j = 0; j < 8; j++) {
                if ((v & 1) == 1) {
                    v = (v >>> 1) ^ reflectedPoly;
                } else {
                    v = (v >>> 1);
                }
            }
            lookupTable[i] = v;
        }
        return lookupTable;
    }

    static int[] initLookupTableUnreflected(int poly) {
        int lookupTable[] = new int[0x100];
        for (int i = 0; i < 0x100; i++) {
            int v = i << 24;
            for (int j = 0; j < 8; j++) {
                if ((v & Integer.MIN_VALUE) != 0) {
                    v = (v << 1) ^ poly;
                } else {
                    v = (v << 1);
                }
            }
            lookupTable[i] = v;
        }
        return lookupTable;
    }

    static int[][] initLookupTablesReflected(int poly, int dimension) {
        int[][] lookupTable = new int[dimension][0x100];
        lookupTable[0] = fastInitLookupTableReflected(poly);
        for (int n = 0; n < 256; n++) {
            int v = lookupTable[0][n];
            for (int k = 1; k < 8; k++) {
                v = lookupTable[0][v & 0xff] ^ (v >>> 8);
                lookupTable[k][n] = v;
            }
        }
        return lookupTable;
    }
}