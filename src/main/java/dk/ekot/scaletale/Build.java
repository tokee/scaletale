/* $Id:$
 *
 * WordWar.
 * Copyright (C) 2012 Toke Eskildsen, te@ekot.dk
 *
 * This is confidential source code. Unless an explicit written permit has been obtained,
 * distribution, compiling and all other use of this code is prohibited.    
  */
package dk.ekot.scaletale;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Builds a Solr index based on the given configuration.
 */
public class Build {
    private static Log log = LogFactory.getLog(Build.class);
    public final ScaleProperties prop;

    private final long maxSize;
    private final long maxDocs;

    public Build(ScaleProperties prop) {
        this.prop = prop;
        maxSize = prop.getLong(ScaleProperties.INDEX_MAX_SIZE, ScaleProperties.INDEX_MAX_SIZE_DEFAULT);
        maxDocs = prop.getLong(ScaleProperties.INDEX_MAX_DOCS, ScaleProperties.INDEX_MAX_DOCS_DEFAULT);
    }

    public static void main(String[] args) throws IOException {
        ScaleProperties prop = ScaleProperties.load(args.length == 0 ? null : new File(args[0]));
        if (prop == null) {
            System.err.println("Usage: Build <config_file>");
            return;
        }
        new Build(prop).go();
    }

    private void go() {
        log.debug("Starting index build");
        final long startBuild = System.nanoTime();

        log.info("Finished build after " + SCommon.getRelTime(startBuild));
    }



}
