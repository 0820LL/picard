package picard.util;

import picard.util.help.PicardHelpDoclet;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Smoke test to run doc gen on a subset of classes to make sure it doesn't regress.
 */
public class HelpSmokeTest {
    /**
     * Entry point for manually running the picardDoc process on a subset of packages from within Picard.
     */
    private static String[] docTestPackages = {
            "picard.cmdline.argumentcollections",
            "picard.sam",
    };

    @Test
    public static void documentationSmokeTest() throws IOException {
        Path docTestTarget = Files.createTempDirectory("docgentest");
        docTestTarget.toFile().deleteOnExit();

        String[] argArray = new String[]{
                "-doclet", PicardHelpDoclet.class.getName(),
                "-docletpath", "build/libs/",
                "-sourcepath", "src/main/java",
                "-settings-dir", "src/main/resources/picard/helpTemplates",
                "-d", docTestTarget.toString(), // directory must exist
                "-output-file-extension", "html",
                "-build-timestamp", "2016/11/11 11:11:11",
                "-absolute-version", "1.1-111",
                "-cp", System.getProperty("java.class.path"),
                "-verbose"
        };

        final List<String> docArgList = new ArrayList<>();
        docArgList.addAll(Arrays.asList(argArray));
        docArgList.addAll(Arrays.asList(docTestPackages));

        // This is  smoke test; we just want to make sure it doesn't blow up
        int success = com.sun.tools.javadoc.Main.execute(docArgList.toArray(new String[]{}));
        Assert.assertEquals(success, 0, "Failure processing picardDoc via javadoc");
    }

}
