package com.github.mmonkey.Relay.Utilities;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtils {

    public static boolean copyFile(final File toCopy, final File destFile) {

        try {

            if (!destFile.exists()) {
                return FileUtils.copyStream(new FileInputStream(toCopy), new FileOutputStream(destFile));
            }

        } catch (final FileNotFoundException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static boolean copyFilesRecusively(final File toCopy, final File destDir) {

        assert destDir.isDirectory();

        if (!toCopy.isDirectory()) {

            return FileUtils.copyFile(toCopy, new File(destDir, toCopy.getName()));

        } else {

            final File newDestDir = new File(destDir, toCopy.getName());

            if (!newDestDir.exists() && !newDestDir.mkdir()) {
                return false;
            }

            File[] fileList = toCopy.listFiles();

            if (fileList != null) {

                for (final File child : fileList) {

                    if (!FileUtils.copyFilesRecusively(child, newDestDir)) {
                        return false;
                    }

                }

            }

        }

        return true;

    }

    public static boolean copyJarResourcesRecursively(final File destDir, final JarURLConnection jarConnection) throws IOException {

        final JarFile jarFile = jarConnection.getJarFile();

        for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {

            final JarEntry entry = e.nextElement();

            if (entry.getName().startsWith(jarConnection.getEntryName())) {

                final String filename = removeStart(entry.getName(), jarConnection.getEntryName());
                final File f = new File(destDir, filename);

                if (!f.exists()) {

                    if (!entry.isDirectory()) {

                        final InputStream entryInputStream = jarFile.getInputStream(entry);

                        if (!FileUtils.copyStream(entryInputStream, f)) {
                            return false;
                        }

                        entryInputStream.close();

                    } else {

                        if (!FileUtils.ensureDirectoryExists(f)) {
                            throw new IOException("Could not create directory: " + f.getAbsolutePath());
                        }

                    }

                }

            }

        }

        return true;

    }

    public static boolean copyResourcesRecursively(final URL originUrl, final File destination) {

        try {

            if (originUrl != null) {
                final URLConnection urlConnection = originUrl.openConnection();

                if (urlConnection instanceof JarURLConnection) {
                    return FileUtils.copyJarResourcesRecursively(destination, (JarURLConnection) urlConnection);

                } else {
                    return FileUtils.copyFilesRecusively(new File(originUrl.getPath()), destination);
                }
            }

        } catch (final IOException e) {
            e.printStackTrace();
        }

        return false;

    }

    public static boolean copyStream(final InputStream is, final File f) {

        try {

            return FileUtils.copyStream(is, new FileOutputStream(f));

        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }

        return false;

    }

    private static boolean copyStream(final InputStream is, final OutputStream os) {

        try {

            final byte[] buf = new byte[1024];
            int len;

            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }

            is.close();
            os.close();

            return true;

        } catch (final IOException e) {
            e.printStackTrace();
        }

        return false;

    }

    public static boolean ensureDirectoryExists(final File f) {
        return f.exists() || f.mkdir();
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static String removeStart(String str, String remove) {

        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }

        if (str.startsWith(remove)) {
            return str.substring(remove.length());
        }

        return str;

    }

}
