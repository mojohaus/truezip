package org.codehaus.mojo.truezip.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.plexus.util.StringUtils;

import de.schlichtherle.io.File;

/**
 * @plexus.component role="org.codehaus.mojo.truezip.util.TrueZip" role-hint="default"
 */
public class DefaultTrueZip
    implements TrueZip
{

    public List list( FileSet fileSet, boolean verbose, Log logger )
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager( logger, verbose );
        return list( fileSet, fileSetManager );
    }

    public List list( FileSet fileSet )
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager();
        return list( fileSet, fileSetManager );
    }

    private List list( FileSet fileSet, TrueZipFileSetManager fileSetManager )
    {
        if ( StringUtils.isBlank( fileSet.getDirectory() ) )
        {
            fileSet.setDirectory( "." );
        }

        String[] files = fileSetManager.getIncludedFiles( fileSet );

        ArrayList fileLists = new ArrayList();

        for ( int i = 0; i < files.length; ++i )
        {
            File source = new File( fileSet.getDirectory(), files[i] );
            fileLists.add( source );
        }

        return fileLists;

    }

    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////

    public void copy( FileSet fileSet, boolean verbose, Log logger )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager( logger, verbose );
        copy( fileSet, fileSetManager );
    }

    public void copy( FileSet fileSet )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager();
        copy( fileSet, fileSetManager );
    }

    public void copy( FileSet oneFileSet, TrueZipFileSetManager fileSetManager )
        throws IOException
    {
        if ( StringUtils.isBlank( oneFileSet.getDirectory() ) )
        {
            oneFileSet.setDirectory( "." );
        }

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

    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////
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

    // ///////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////
    public void remove( FileSet fileSet, boolean verbose, Log logger )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager( logger, verbose );
        remove( fileSet, fileSetManager );
    }

    public void remove( FileSet fileSet )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager();
        remove( fileSet, fileSetManager );
    }

    private void remove( FileSet oneFileSet, TrueZipFileSetManager fileSetManager )
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

        fileSetManager.delete( oneFileSet, true );

    }

}
