package org.clyze.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.mozilla.universalchardet.UniversalDetector;

public class JHelper {

    // Throws a runtime exception with a message. The message is also
    // shown in the standard output. This utility helps debugging as
    // Gradle may report a different exception (e.g. the usual
    // IllegalStateException "buildToolsVersion is not specified").
    public static void throwRuntimeException(String errMsg) {
        System.out.println(errMsg);
        throw new RuntimeException(errMsg);
    }

    public static void cleanUp(Set<String> tmpDirs) {
        tmpDirs.forEach(tmpDir -> FileUtils.deleteQuietly(new File(tmpDir)));
    }

    /**
     * Executes a command, printing all standard output/error messages
     * prefixed by a custom string.
     *
     * @param cmd       the command to run
     * @param prefix    the prefix
     * @param processor a line processor (can be null)
     */
    public static void runWithOutput(String[] cmd, String prefix, Consumer<String> processor) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(true);
        Process proc = builder.start();

        // Kill process if this VM shuts down.
        // System.err.println("Destroying process: " + String.join(" ", cmd));
        Runtime.getRuntime().addShutdownHook(new Thread(proc::destroyForcibly));

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        stdInput.lines().forEach(s -> processWithPrefix(s, prefix, processor));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        stdError.lines().forEach(s -> processWithPrefix(s, prefix, processor));
    }

    /**
     * Executes a command, printing all standard output/error messages
     * prefixed by a custom string.
     *
     * @param cmd       the command to run
     * @param prefix    the prefix
     */
    public static void runWithOutput(String[] cmd, String prefix) throws IOException {
        runWithOutput(cmd, prefix, null);
    }

    /**
     * Prints a line, prepending a prefix. An optional processor to
     * consume the line may also be given.
     *
     * @param s         the line
     * @param prefix    the prefix to print before the line
     * @param processor a line processor (can be null)
     */
    private static void processWithPrefix(String s, String prefix, Consumer<String> processor) {
        System.out.println(prefix + ": " + s);
        if (processor != null)
            processor.accept(s);
    }

    /**
     * If the given file is not encoded as UTF-8, its encoding is
     * detected and its contents are converted to UTF-8.
     *
     * @param filename   the path of the file to convert
     */
    public static void ensureUTF8(String filename) throws IOException {
        // Encoding detector.
        UniversalDetector detector = new UniversalDetector(null);

        FileInputStream fis = new FileInputStream(filename);
        byte[] buf = new byte[4096];
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone())
            detector.handleData(buf, 0, nread);
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        detector.reset();
        fis.close();

        if ((encoding == null) || (encoding.equals("UTF-8")))
            return;

        // Try to convert source file to UTF-8.
        try {
            Charset sourceEncoding = Charset.forName(encoding);
            Charset targetEncoding = StandardCharsets.UTF_8;
            byte[] buf2 = IOUtils.toByteArray(new FileInputStream(filename));
            CharBuffer data = sourceEncoding.decode(ByteBuffer.wrap(buf2));
            ByteBuffer outBuf = targetEncoding.encode(data);
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter(filename));
            int outDataLength = 0;
            while (outBuf.remaining() > 0) {
                bufWriter.write(outBuf.get());
                outDataLength++;
            }
            bufWriter.close();
            System.out.println("Converted " + encoding + " to UTF-8: " + filename + ", " + buf2.length + " vs. " + outDataLength + "bytes");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot convert encoding " + encoding + " to UTF-8");
        }
    }

    /**
     * Runs a standalone JAR using 'java'.
     *
     * @param classpath   the classpath to use
     * @param jvmArgs     the JVM arguments to use
     * @param jar         the JAR to run
     * @param args        the command line arguments to pass
     * @param tag         a text prefix to mark output lines
     * @param debug       if true, print debug information
     * @param processor   a line processor (can be null)
     */
    public static void runJar(String[] classpath, String[] jvmArgs, String jar,
                              String[] args, String tag, boolean debug,
                              Consumer<String> processor) throws IOException {
        List<String> jvmArgs0 = new LinkedList<>(Arrays.asList(jvmArgs));
        jvmArgs0.add("-jar");
        jvmArgs0.add(jar);
        runJava(classpath, jvmArgs0.toArray(new String[0]), args, tag, debug, processor);
    }

    /**
     * Runs a Java class using 'java'.
     *
     * @param classpath   the classpath to use
     * @param jvmArgs     the JVM arguments to use
     * @param klass       the fully qualified name of the class to run
     * @param args        the command line arguments to pass
     * @param tag         a text prefix to mark output lines
     * @param debug       if true, print debug information
     * @param processor   a line processor (can be null)
     */
    public static void runClass(String[] classpath, String[] jvmArgs, String klass, String[] args,
                                String tag, boolean debug, Consumer<String> processor) throws IOException {
        List<String> jvmArgs0 = new LinkedList<>(Arrays.asList(jvmArgs));
        jvmArgs0.add(klass);
        runJava(classpath, jvmArgs0.toArray(new String[0]), args, tag, debug, processor);
    }

    /**
     * Runs a Java program using 'java'.
     *
     * @param classpath   the classpath to use
     * @param jvmArgs     the JVM arguments to use (including Java program)
     * @param args        the command line arguments to pass
     * @param tag         a text prefix to mark output lines
     * @param debug       if true, print debug information
     * @param processor   a line processor (can be null)
     */
    public static void runJava(String[] classpath, String[] jvmArgs,
                               String[] args, String tag, boolean debug,
                               Consumer<String> processor) throws IOException {
        String javaHome = System.getProperty("java.home");
        if (javaHome == null)
            throw new RuntimeException("Could not determine JAVA_HOME to run: " + Arrays.toString(jvmArgs));

        // Try to find 'java' in known locations.
        File java = new File(javaHome, "java");
        if (!java.exists()) {
            java = new File(javaHome, "bin/java");
            if (!java.exists())
                throw new RuntimeException("Could not find 'java' in JAVA_HOME, cannot run: " + Arrays.toString(jvmArgs));
        }

        LinkedList<String> cmd = new LinkedList<>();
        cmd.add(java.getAbsolutePath());
        if (classpath.length > 0) {
            cmd.add("-cp");
            cmd.add(String.join(":", classpath));
        }
        cmd.addAll(Arrays.asList(jvmArgs));
        cmd.addAll(Arrays.asList(args));
        if (debug)
            System.err.println("Running program: " + String.join(" ", cmd));
        runWithOutput(cmd.toArray(new String[]{}), tag, processor);
    }

}
