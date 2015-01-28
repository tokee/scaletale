/* $Id:$
 *
 * WordWar.
 * Copyright (C) 2012 Toke Eskildsen, te@ekot.dk
 *
 * This is confidential source code. Unless an explicit written permit has been obtained,
 * distribution, compiling and all other use of this code is prohibited.    
  */
package dk.ekot.scaletale;

public class SCommon {
    public static final long M = 1000000;

    public static String getRelTime(long startNS) {
        return getTime(System.nanoTime()-startNS);
    }

    public static String getTime(long ns) {
        if (ns < M) {
            return ns + "ns";
        }
        long ms = ns / M;
        if (ms < 10000) {
            return ms + "ms";
        }
        long totalS = ms / 1000;

        long s = totalS % 60;
        long m = totalS / 60;
        return String.format("%d:%0.2dmin", m, s);
    }
}
