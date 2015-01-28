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

import java.io.*;
import java.util.*;

/**
 *
 */
public class ScaleProperties {
    private static Log log = LogFactory.getLog(ScaleProperties.class);
    public static final String CONFIG_FILE_DEFAULT = "scaletale.cfg";
    public static final String RANDOM_SEED = "random.seed";

    public static final String SOLR = "solr";
    public static final String SOLR_DEFAULT = "localhost:8983/solr";

    public static final String DICT_FILE = "dictionary.file";
    public static final String DICT_FILE_DEFAULT = "scaletale.dict";
    public static final String DICT_SHUFFLE = "dictionary.shuffle";
    public static final String DICT_SHUFFLE_DEFAULT = "true";
    public static final String DICT_SHUFFLE_SEED = "dictionary.shuffle.seed";

    public static final String INDEX_MAX_SIZE = "index.maxsize";
    public static final long INDEX_MAX_SIZE_DEFAULT = 100 * 1024 * 1024; // 100MB
    public static final String INDEX_MAX_DOCS = "index.maxdocs";
    public static final long INDEX_MAX_DOCS_DEFAULT = 100 * 1000 * 1000; // 100 million

    public final Properties prop;
    private List<String> dictionary = null;
    private Random random = null;

    public ScaleProperties(Properties prop) {
        this.prop = prop;
    }

    public static ScaleProperties load(File propFile) throws IOException {
        if (propFile == null) {
            propFile = new File(CONFIG_FILE_DEFAULT);
        }
        if (!propFile.exists()) {
            System.err.println("Error: Could not locate properties file '" + propFile + "'\n");
            return null;
        }
        Properties prop = new Properties();
        FileInputStream in = new FileInputStream(propFile);
        prop.load(in);
        in.close();
        log.debug("Loaded properties from '" + propFile + "'. Building index.");
        return new ScaleProperties(prop);
    }

    public synchronized Random getRandom() {
        if (random == null) {
            random = getRandom("generic", RANDOM_SEED);
        }
        return random;
    }

    public synchronized Random getRandom(String designation, String key) {
        if (prop.containsKey(key)) {
            long seed = Long.parseLong(prop.getProperty(key));
            random = new Random(seed);
            log.debug("Constructed " + designation + " Random with properties-defined seed " + seed);
        } else {
            long seed = new Random().nextLong();
            prop.setProperty(key, Long.toString(seed));
            log.debug("Constructed " + designation + " Random with newly generated seed " + seed
                      + ". Repeat setup by adding " + key + "=" + seed + " to properties");
        }
        return random;
    }

    public synchronized List<String> getDictionary() {
        if (dictionary == null) {
            File dictFile = new File(prop.getProperty(DICT_FILE, DICT_FILE_DEFAULT));
            if (!dictFile.exists()) {
                throw new RuntimeException(
                        "Unable to locate dictionary '" + dictFile
                        + "'. Please specify a dictionary (UTF-8, newline delimited) with property '" + DICT_FILE + "'");
            }
            try {
                FileInputStream fin = new FileInputStream(dictFile);
                BufferedReader buf = new BufferedReader(new InputStreamReader(fin));
                List<String> dict = new ArrayList<>(10000);
                log.debug("Loading dictionary from " + dictFile);
                String line;
                while ((line = buf.readLine()) != null && !line.isEmpty()) {
                    dict.add(line);
                }
                fin.close();
                if (Boolean.parseBoolean(prop.getProperty(DICT_SHUFFLE, DICT_SHUFFLE_DEFAULT))) {
                    log.debug("Shuffling dictionary loaded from " + dictFile);
                    Collections.shuffle(dict, getRandom("dictionary-shuffle", DICT_SHUFFLE_SEED));
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to load dictionary from '" + dictFile + "'", e);
            }
        }
        return dictionary;
    }

    public long getLong(String key, long defaultValue) {
        return prop.containsKey(key) ? Long.parseLong(prop.getProperty(key)) : defaultValue;
    }
    public long getInt(String key, int defaultValue) {
        return prop.containsKey(key) ? Integer.parseInt(prop.getProperty(key)) : defaultValue;
    }
}
