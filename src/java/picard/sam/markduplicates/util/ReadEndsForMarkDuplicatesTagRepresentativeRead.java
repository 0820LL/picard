/*
  * The MIT License
  *
  * Copyright (c) 2015 The Broad Institute
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

package picard.sam.markduplicates.util;

public class ReadEndsForMarkDuplicatesTagRepresentativeRead extends ReadEndsForMarkDuplicates {
    public String firstEncounteredReadName = "Null";
    public String representativeReadName = "Null";
    public int duplicateSetSize = 0;

    public ReadEndsForMarkDuplicatesTagRepresentativeRead() { }

    public ReadEndsForMarkDuplicatesTagRepresentativeRead(final ReadEndsForMarkDuplicates read) {
        super(read);
    }

    // one int: 32
    // 2 read names, UTF-8: (2 x ~34 char x 8) + 4
    public static int getSizeOf() {
        return ReadEndsForMarkDuplicates.getSizeOf() + 32 + (2 * 8 * 34)+4;
    }
}
