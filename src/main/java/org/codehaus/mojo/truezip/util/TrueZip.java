package org.codehaus.mojo.truezip.util;

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.fs.FsSyncException;

public interface TrueZip
{
    String ROLE = TrueZip.class.getName();

    List list( TrueZipFileSet fileSet, boolean verbose, Log logger );

    List list( TrueZipFileSet fileSet );

    void copy( TrueZipFileSet oneFileSet, boolean verbose, Log logger )
        throws IOException;

    void copy( TrueZipFileSet oneFileSet )
        throws IOException;

    void copyFile( TFile source, TFile dest )
        throws IOException;

    void moveFile( TFile source, TFile dest )
       throws IOException;

    void move( TrueZipFileSet oneFileSet, boolean verbose, Log logger )
        throws IOException;

    void move( TrueZipFileSet oneFileSet )
        throws IOException;

    void remove( TrueZipFileSet oneFileSet, boolean verbose, Log logger )
        throws IOException;

    void remove( TrueZipFileSet oneFileSet )
        throws IOException;

    void sync()
        throws FsSyncException;
}
