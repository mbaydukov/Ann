package com.mb.ann.service;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class GroovyFactoryService {

    private static Logger logger = LoggerFactory.getLogger(GroovyFactoryService.class);

    public static final String PARSERS_FOLDER = System.getProperty("user.dir") + File.separator + "parsers";


    private static final Map<String, Object> GROOVY_CACHE = new HashMap<>();
    private static final Map<String, Long> LAST_UPDATE = new HashMap<>();

    public <T> T get(String groovyName, String defaultGroovyName, Class<T> clazz) {
        String fileName = FilenameUtils.separatorsToSystem(groovyName);
        Object g = get(fileName, clazz);
        if (g == null) {
            // There is no specified groovy, try load default one
            g = get(defaultGroovyName, clazz);
            GROOVY_CACHE.put(fileName, g);
            LAST_UPDATE.put(fileName, LAST_UPDATE.get(defaultGroovyName));
            return clazz.cast(g);
        }
        return clazz.cast(g);
    }

    public <T> T get(String groovyName, Class<T> clazz) {
        String fileName = FilenameUtils.separatorsToSystem(groovyName);
        try {
            Object m = GROOVY_CACHE.get(fileName);
            if (m == null) {
                return clazz.cast(loadGroovy(fileName));
            } else {
                Long lastModified = LAST_UPDATE.get(fileName);
                if (lastModified != null) {
                    File f = getFile(fileName);
                    if (lastModified == f.lastModified()) {
                        return (clazz.cast(m.getClass().newInstance()));
                    } else {
                        return clazz.cast(loadGroovy(fileName));
                    }
                }
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public GroovyObject get(String groovyName) {
        String fileName = FilenameUtils.separatorsToSystem(groovyName);
        try {
            Object m = GROOVY_CACHE.get(fileName);
            if (m == null) {
                return (GroovyObject) loadGroovy(fileName);
            } else {
                Long lastModified = LAST_UPDATE.get(fileName);
                if (lastModified != null) {
                    File f = getFile(fileName);
                    if (lastModified == f.lastModified()) {
                        return (GroovyObject) m.getClass().newInstance();
                    } else {
                        return (GroovyObject) loadGroovy(fileName);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private synchronized Object loadGroovy(String className) throws IllegalAccessException, InstantiationException, IOException {
        GroovyClassLoader gcl = new GroovyClassLoader();
        String fileName = FilenameUtils.separatorsToSystem(className);
        gcl.addClasspath(PARSERS_FOLDER + File.separator);
        logger.debug(FilenameUtils.separatorsToSystem(PARSERS_FOLDER + File.separator + fileName + ".groovy"));
        File f = getFile(fileName);
        Class clazz = gcl.parseClass(f);
        Object m = clazz.newInstance();
        GROOVY_CACHE.put(fileName, m);
        LAST_UPDATE.put(fileName, f.lastModified());
        return m;
    }

    private File getFile(String className) {
        String fileName = FilenameUtils.separatorsToSystem(PARSERS_FOLDER + File.separator + className + ".groovy");
        return new File(fileName);
    }

    public boolean setProperty(GroovyObject groovyObject, String propertyName, Object object) {
        try {
            groovyObject.setProperty(propertyName, object);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
