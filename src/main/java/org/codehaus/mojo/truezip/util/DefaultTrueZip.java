package org.codehaus.mojo.truezip.util;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.mojo.truezip.Fileset;
import org.codehaus.plexus.util.StringUtils;

import de.schlichtherle.io.File;

/**
 * @plexus.component role="org.codehaus.mojo.truezip.util.TrueZip" role-hint="default"
 */
public class DefaultTrueZip
    implements TrueZip
{

    public void list( PrintStream ps, FileSet fileSet, boolean verbose, Log logger )
    {
        if ( StringUtils.isBlank( fileSet.getDirectory() ) )
        {
            fileSet.setDirectory( "." );
        }

        logger.info( "List " + fileSet );

        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager( logger, verbose );

        String[] files = fileSetManager.getIncludedFiles( fileSet );

        for ( int i = 0; i < files.length; ++i )
        {
            String relativeDestPath = files[i];
            if ( !StringUtils.isBlank( fileSet.getOutputDirectory() ) )
            {
                relativeDestPath = fileSet.getOutputDirectory() + "/" + relativeDestPath;
            }
            File source = new File( fileSet.getDirectory(), files[i] );

            ps.println( source.getPath() );
        }

    }

    public void copy( FileSet oneFileSet, boolean verbose, Log logger )
        throws IOException
    {
        if ( StringUtils.isBlank( oneFileSet.getDirectory() ) )
        {
            oneFileSet.setDirectory( "." );
        }

        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager( logger, verbose );

        String[] files = fileSetManager.getIncludedFiles( oneFileSet );

        for ( int i = 0; i < files.length; ++i )
        {
            String relativeDestPath = files[i];
            if ( !StringUtils.isBlank( oneFileSet.getOutputDirectory() ) )
            {
                relativeDestPath = oneFileSet.getOutputDirectory() + "/" + relativeDestPath;
            }
            File dest = new File( relativeDestPath );

            File source = new File( oneFileSet.getDirectory(), files[i] );

            this.copyFile( source, dest );
        }

    }

    public void copyFile( File source, File dest )
        throws IOException
    {
        File destParent = (File) dest.getParentFile();

        if ( !destParent.isDirectory() )
        {
            if ( !destParent.mkdirs() )
            {
                throw new IOException( "Unable to create " + destParent );
            }
        }

        if ( source.isArchive() )
        {
            java.io.File realSource = new java.io.File( source.getAbsolutePath() );
            File.cp_p( realSource, dest );
        }
        else
        {
            File.cp_p( source, dest );
        }
    }

    public void moveFile( File source, File dest )
    {
        File file = new File( source );

        File tofile = new File( dest );

        file.renameTo( tofile );
    }

    public void remove( Fileset oneFileSet, boolean verbose, Log logger )
        throws IOException
    {
        if ( StringUtils.isBlank( oneFileSet.getDirectory() ) )
        {
            throw new IOException( "FileSet's directory is required." );
        }

        File directory = new File( oneFileSet.getDirectory() );

        if ( !directory.isDirectory() )
        {
            throw new IOException( "FileSet's directory: " + directory + " not found." );
        }

        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager( logger, verbose );

        fileSetManager.delete( oneFileSet, true );

    }

}
