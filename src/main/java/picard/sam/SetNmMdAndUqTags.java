/*
 * The MIT License
 *
 * Copyright (c) 2016 The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package picard.sam;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.reference.ReferenceSequenceFileWalker;
import htsjdk.samtools.util.CloserUtil;
import htsjdk.samtools.util.IOUtil;
import htsjdk.samtools.util.Log;
import htsjdk.samtools.util.ProgressLogger;
import picard.cmdline.CommandLineProgram;
import picard.cmdline.CommandLineProgramProperties;
import picard.cmdline.Option;
import picard.cmdline.StandardOptionDefinitions;
import picard.cmdline.programgroups.SamOrBam;

import java.io.File;
import java.util.stream.StreamSupport;

/**
 * @author Yossi Farjoun
 */
@CommandLineProgramProperties(
        usage = SetNmMdAndUqTags.USAGE_SUMMARY + SetNmMdAndUqTags.USAGE_DETAILS,
        usageShort = SetNmMdAndUqTags.USAGE_SUMMARY,
        programGroup = SamOrBam.class
)
public class SetNmMdAndUqTags extends CommandLineProgram {
    static final String USAGE_SUMMARY = "Fixes the NM, MD, and UQ tags in a SAM file.  ";
    static final String USAGE_DETAILS = "This tool takes in a SAM or BAM file (sorted by coordinate) and calculates the NM, MD, and UQ tags by comparing with the reference."+
            "<br />" +
                    "This may be needed when MergeBamAlignment was run with SORT_ORDER different from 'coordinate' and thus could not fix\n"+
                    "these tags then.<br />"+
            "<h4>Usage example:</h4>" +
            "<pre>" +
            "java -jar picard.jar SetNmMDAndUqTags \\<br />" +
            "      I=sorted.bam \\<br />" +
            "      O=fixed.bam \\<br />"+
            "</pre>" +
            "<hr />";
    @Option(doc = "The BAM or SAM file to fix.", shortName = StandardOptionDefinitions.INPUT_SHORT_NAME)
    public File INPUT;

    @Option(doc = "The fixed BAM or SAM output file. ", shortName = StandardOptionDefinitions.OUTPUT_SHORT_NAME)
    public File OUTPUT;

    @Option(doc = "Whether the file contains bisulfite sequence.")
    public boolean IS_BISULFITE_SEQUENCE = false;

    @Override
    protected String[] customCommandLineValidation() {
        if (REFERENCE_SEQUENCE == null) {
            return new String[]{"Must have a non-null REFERENCE_SEQUENCE"};
        }
        return super.customCommandLineValidation();
    }

    private final Log log = Log.getInstance(SetNmMdAndUqTags.class);

    public static void main(final String[] argv) {
        new SetNmMdAndUqTags().instanceMainWithExit(argv);
    }

    protected int doWork() {
        validateInputs();

        final SamReader reader = createSamReader();

        final SAMFileWriter writer = createSamFileWriter(reader, log);

        final ReferenceSequenceFileWalker refSeq = new ReferenceSequenceFileWalker(REFERENCE_SEQUENCE);

        StreamSupport.stream(reader.spliterator(),false)
                .peek(rec->{if(!rec.getReadUnmappedFlag()) AbstractAlignmentMerger.fixNmMdAndUq(rec, refSeq, IS_BISULFITE_SEQUENCE);})
                .forEach(writer::addAlignment);

        CloserUtil.close(reader);
        writer.close();
        return 0;
    }

    protected void validateInputs(){
        IOUtil.assertFileIsReadable(INPUT);
        IOUtil.assertFileIsWritable(OUTPUT);
    }

    protected SamReader createSamReader(){
        final SamReader reader = SamReaderFactory.makeDefault().referenceSequence(REFERENCE_SEQUENCE).open(INPUT);

        if (reader.getFileHeader().getSortOrder() != SAMFileHeader.SortOrder.coordinate) {
            throw new SAMException("Input must be coordinate-sorted for this program to run. Found: " + reader.getFileHeader().getSortOrder());
        }
        return reader;
    }

    protected SAMFileWriter createSamFileWriter(SamReader reader, Log log){
        final SAMFileWriter writer = new SAMFileWriterFactory().makeSAMOrBAMWriter(reader.getFileHeader(), true, OUTPUT);
        writer.setProgressLogger(
                new ProgressLogger(log, (int) 1e7, "Wrote", "records"));
        return writer;
    }
}
