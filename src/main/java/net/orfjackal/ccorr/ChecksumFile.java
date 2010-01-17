// Copyright © 2003-2010, Esko Luontola <www.orfjackal.net>
// This software is released under the GNU General Public License, version 2 or later.
// The license text is at http://www.gnu.org/licenses/gpl.html

package net.orfjackal.ccorr;

import javax.swing.*;
import java.io.*;

/**
 * This class is used to represent a CCorr Checksum File. A new <code>ChecksumFile</code> is created with the
 * <code>createChecksumFile</code> method. This program counts checksums from the given file at regular intervals and
 * stores the information in this class. The part checksums and other data are used in a <code>Comparison</code> to find
 * the differences between any files.
 *
 * @author Esko Luontola
 */
public class ChecksumFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Maximum allowed part length in bytes.
     * <p/>
     * NOTE: The part size is used in allocating a byte array in {@link #updateChecksums(long, String)}, so if this is
     * very high, the program will run out of memory.
     */
    public static final int MAX_PART_SIZE = 10 * 1024 * 1024;    // 10 MB

    /**
     * Minimum allowed part length in bytes.
     */
    public static final int MIN_PART_SIZE = 1024;           // 1 KB

    /**
     * The file extension used by CCorr Checksum File.
     */
    public static final String FILE_EXTENSION = "ccf";

    /**
     * The the file type name of CCorr Checksum File.
     */
    public static final String FILE_TYPE = "CCorr Checksum File";

    /**
     * The part checksums in HEX format.
     */
    private String[] checksums;

    /**
     * The name of the algorithm that was used for making the checksums.
     */
    private String usedAlgorithm;

    /**
     * The length of the parts used for making the checksums in bytes. The type is <code>long</code> to avoid bugs in
     * handling files over 2 GB, although the actual {@link #MAX_PART_SIZE maximum limit} is much lower.
     */
    private long partLength;

    /**
     * The file from which the checksums were made.
     */
    private File sourceFile;

    /**
     * The length of the source file in bytes.
     */
    private long sourceFileLength;

    /**
     * The file to which this <code>ChecksumFile</code> was saved.
     */
    private File savedAsFile;


    /**
     * Private constructor for ChecksumFile. Use the method loadFromFile or createChecksumFile to create an instance
     * from outside this class.
     *
     * @param sourceFile the file from which to make checksums
     * @see #createChecksumFile(java.io.File,long,String)
     * @see #loadFromFile(File)
     */
    private ChecksumFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        // TODO: write as the first object an Integer which tells the version of the file, so that importing old versions would be possible
        out.writeObject(checksums);
        out.writeObject(usedAlgorithm);
        out.writeLong(partLength);
        out.writeObject(sourceFile);
        out.writeLong(sourceFileLength);
        out.writeObject(savedAsFile);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        checksums = (String[]) in.readObject();
        usedAlgorithm = (String) in.readObject();
        partLength = in.readLong();
        sourceFile = (File) in.readObject();
        sourceFileLength = in.readLong();
        savedAsFile = (File) in.readObject();
    }

    /**
     * Creates a new <code>ChecksumFile</code> object from the given file. The operation can take some minutes depending
     * on the size of the file to be processed. A ProgressMonitor is used if available.
     *
     * @param file       the file to process.
     * @param partLength the part length used to make checksums; if less than <code>MIN_PART_SIZE</code> or greater than
     *                   <code>MAX_PART_SIZE</code>, the smallest or biggest allowed value will be used
     * @param algorithm  the name of the  algorithm to be used for making the checksums
     * @return a new <code>ChecksumFile</code> object if operation is successful, otherwise null
     * @see #updateChecksums()
     * @see CRC#getSupportedAlgorithms()
     * @see Settings#setProgressMonitor(ProgressMonitor)
     */
    public static ChecksumFile createChecksumFile(File file, long partLength, String algorithm) {
        /*
         * updateChecksums takes care of checking the input attributes
         * so we don't need to check them here
         */

        ChecksumFile result = new ChecksumFile(file);
        boolean successful = result.updateChecksums(partLength, algorithm);

        if (successful) {
            return result;
        } else {
            return null;
        }
    }

    /**
     * Rebuilds the checksums from the source file using the saved settings. This operation can take some minutes
     * depending on the size of the file to be processed. A ProgressMonitor is used if available
     *
     * @return true if operation is successful, otherwise false
     * @see #createChecksumFile(java.io.File,long,String)
     * @see Settings#setProgressMonitor(ProgressMonitor)
     */
    public boolean updateChecksums() {
        return updateChecksums(this.partLength, this.usedAlgorithm);
    }

    /**
     * Rebuilds the checksums from the source file using the given settings. This operation can take some minutes
     * depending on the size of the file to be processed. A ProgressMonitor is used if available
     *
     * @param partLength the part length used to make checksums; if less than <code>MIN_PART_SIZE</code> or greater than
     *                   <code>MAX_PART_SIZE</code>, the smallest or biggest allowed value will be used
     * @param algorithm  the name of the  algorithm to be used for making the checksums
     * @return true if operation is successful, otherwise false.
     * @see #createChecksumFile(java.io.File,long,String)
     * @see CRC#getSupportedAlgorithms()
     * @see Settings#setProgressMonitor(ProgressMonitor)
     */
    private boolean updateChecksums(long partLength, String algorithm) {
        Log.print("updateChecksums(" + partLength + ", " + algorithm + ") from " + this.sourceFile);
        boolean successful = false;

        ProgressMonitor monitor = Settings.getProgressMonitor();

        if (this.sourceFile.exists() && this.sourceFile.canRead()) {
            CRC crc = new CRC(algorithm);
            this.usedAlgorithm = crc.getAlgorithm();

            if (partLength < MIN_PART_SIZE) {
                partLength = MIN_PART_SIZE;
            } else if (partLength > MAX_PART_SIZE) {
                partLength = MAX_PART_SIZE;
            }
            this.partLength = partLength;

            // start making checksums
            try {
                // get source file length and number of parts
                this.sourceFileLength = this.sourceFile.length();
                int parts = (int) (this.sourceFileLength / this.partLength);
                if ((this.sourceFileLength % this.partLength) != 0) {
                    parts++;
                }
                this.checksums = new String[parts];

                // setup progress monitor
                if (monitor != null) {
                    monitor.setMinimum(0);
                    monitor.setMaximum(100);
                }

                // stuff for reading files
                BufferedInputStream input =
                        new BufferedInputStream(
                                new FileInputStream(this.sourceFile),
                                Settings.getReadBufferLength()
                        );
                byte[] buffer = new byte[(int) this.partLength];
                int lastMonitorValue = -1;

                // read one part at a time and make the checksums
                for (int i = 0; i < this.checksums.length; i++) {
                    int len = input.read(buffer);
                    crc.reset();
                    crc.update(buffer, 0, len);
                    this.checksums[i] = crc.getHexValue();

                    // update progress monitor
                    if (monitor != null) {
                        int percentage = i * 100 / parts;
                        if (percentage != lastMonitorValue) {
                            monitor.setProgress(percentage);
                            monitor.setNote("Completed " + percentage + "%");
                            lastMonitorValue = percentage;
                            Thread.yield(); // allow the GUI some time to be updated
                        }

                        if (monitor.isCanceled()) {
                            Log.print("updateChecksums: Cancelled by user");
                            throw new Exception("Cancelled by user");
                        }
                    }
                }
                input.close();
                successful = true;

            } catch (Exception e) {
                // something went wrong, reset the checksum table
                e.printStackTrace();
                this.checksums = null;
                successful = false;
            }
        }

        // close progress monitor
        if (monitor != null) {
            monitor.setProgress(monitor.getMaximum());
        }

        if (successful) {
            Log.println("updateChecksums: Done");
        } else {
            Log.println("updateChecksums: Failed");
        }

        return successful;
    }

    /**
     * Returns the name of the algorithm that was used for making the checksums.
     *
     * @return the name of the algorithm
     * @see CRC#getSupportedAlgorithms()
     */
    public String getAlgorithm() {
        return this.usedAlgorithm;
    }


    /**
     * Returns the part length that was used for making the checksums.
     *
     * @return the length in bytes
     */
    public long getPartLength() {
        return this.partLength;
    }

    /**
     * Returns the number of parts in this <code>ChecksumFile</code>.
     *
     * @return the number of parts, or -1 if checksums not updated
     */
    public int getParts() {
        if (this.checksums == null) {
            return -1;
        } else {
            return this.checksums.length;
        }
    }

    /**
     * Returns the checksum of the requested part.
     *
     * @param part the index of the part
     * @return the checksum in HEX format, or null if parameter invalid
     */
    public String getChecksum(int part) {
        if (this.checksums == null || part < 0 || part >= this.checksums.length) {
            return null;
        } else {
            return this.checksums[part];
        }
    }

    /**
     * Returns the offset of the part's first byte.
     *
     * @param part the index of the part
     * @return index of the part's first byte, or -1 if parameter invalid
     */
    public long getStartOffset(int part) {
        if (this.checksums == null || part < 0 || part >= this.checksums.length) {
            return -1;
        } else {
            return this.partLength * part;
        }
    }

    /**
     * Returns the offset of the part's last byte.
     *
     * @param part the index of the part
     * @return index of the part's last byte, or -1 if parameter invalid
     */
    public long getEndOffset(int part) {
        if (this.checksums == null || part < 0 || part >= this.checksums.length) {
            return -1;
        } else {
            long offset = (this.partLength * (part + 1)) - 1;
            if (offset >= this.sourceFileLength) {
                offset = this.sourceFileLength - 1;
            }
            return offset;
        }
    }

    /**
     * Changes the file to be used as source (if the file was moved at some point). The file length will be checked but
     * otherwise the user is responsible that he points this to the same file. Consider running {@link
     * #updateChecksums()} after this operation.
     *
     * @param file the new location of the source file
     * @return true if file exists and is of the right size, otherwise false
     * @see #createChecksumFile(java.io.File,long,String)
     * @see #updateChecksums()
     */
    public boolean setSourceFile(File file) {
        boolean ok = false;
        if (file != null && file.exists() && file.length() == this.sourceFileLength) {
            this.sourceFile = file;
            ok = true;
        }
        return ok;
    }

    /**
     * Returns the source file from which the checksums were made.
     *
     * @return the source file
     */
    public File getSourceFile() {
        return this.sourceFile;
    }

    /**
     * Returns the length of the source file.
     *
     * @return the length in bytes
     */
    public long getSourceFileLength() {
        return this.sourceFileLength;
    }

    /**
     * Returns where this <code>ChecksumFile</code> was previously saved.
     *
     * @return where this was saved, or null if not saved
     * @see #saveToFile(File)
     */
    public File getSavedAsFile() {
        return this.savedAsFile;
    }

    /**
     * Saves this <code>ChecksumFile</code> object into a file.
     *
     * @param file the file in which to save
     * @return true if successful, otherwise false
     * @see #loadFromFile(File)
     */
    public boolean saveToFile(File file) {
        boolean successful = ObjectSaver.saveToFile(file, this);
        if (successful) {
            this.savedAsFile = file;
        }
        return successful;
    }

    /**
     * Loads a previously saved <code>ChecksumFile</code> from a file.
     *
     * @param file the file from which to load
     * @return a new <code>ChecksumFile</code> loaded from the file, or null if operation failed
     * @see #saveToFile(File)
     */
    public static ChecksumFile loadFromFile(File file) {
        ChecksumFile result;
        try {
            result = (ChecksumFile) (ObjectSaver.loadFromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    /**
     * Returns a text representation of this object.
     *
     * @return a text representation of this object.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < this.getParts(); i++) {
            sb.append(i + ": " + this.getChecksum(i)
                    + "\t start: " + this.getStartOffset(i)
                    + "\t end: " + this.getEndOffset(i) + "\n");
        }
        sb.append("\n" + this.getSourceFile() + " ("
                + this.getSourceFileLength() + " bytes) \n"
                + this.getParts() + " parts ("
                + this.getPartLength() + " bytes) "
                + "using " + this.getAlgorithm());

        return sb.toString();
    }
}