package org.codehaus.mojo.truezip.util;

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.mojo.truezip.Fileset;

import de.schlichtherle.io.File;

public interface TrueZip
{
    String ROLE = TrueZip.class.getName();

    List list( FileSet fileSet, boolean verbose, Log logger );

    void copy( FileSet oneFileSet, boolean verbose, Log logger )
        throws IOException;

    void copyFile( File source, File dest )
        throws IOException;

    void moveFile( File source, File dest );

    void remove( Fileset oneFileSet, boolean verbose, Log logger )
        throws IOException;

}
