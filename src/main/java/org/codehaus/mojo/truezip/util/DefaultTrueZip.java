package org.codehaus.mojo.truezip.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import de.schlichtherle.io.ArchiveDetector;
import de.schlichtherle.io.File;

/**
 * @plexus.component role="org.codehaus.mojo.truezip.util.TrueZip" role-hint="default"
 */
public class DefaultTrueZip
    implements TrueZip
{

    public List list( TrueZipFileSet fileSet, boolean verbose, Log logger )
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager( logger, verbose );
        return list( fileSet, fileSetManager );
    }

    public List list( TrueZipFileSet fileSet )
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager();
        return list( fileSet, fileSetManager );
    }

    private List list( TrueZipFileSet fileSet, TrueZipFileSetManager fileSetManager )
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

    public void move( TrueZipFileSet fileSet, boolean verbose, Log logger )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager( logger, verbose );
        move( fileSet, fileSetManager );
    }

    public void move( TrueZipFileSet fileSet )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager();
        move( fileSet, fileSetManager );
    }

    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////

    public void copy( TrueZipFileSet fileSet, boolean verbose, Log logger )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager( logger, verbose );
        copy( fileSet, fileSetManager );
    }

    public void copy( TrueZipFileSet fileSet )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager();
        copy( fileSet, fileSetManager );
    }

    public void copy( TrueZipFileSet oneFileSet, TrueZipFileSetManager fileSetManager )
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
            if ( dest.isArchive() && FileUtils.getExtension( dest.getPath() ).equals( FileUtils.getExtension( source.getPath() ) ) )
            {
                //use the NULL detector within the source and destination directory trees to  do a verbatim copy.
                // otherwise the destination archive is slightly altered ( still work thou )
                if ( !source.archiveCopyAllTo( dest, ArchiveDetector.NULL ) )
                {
                    throw new IOException( "Unable to copy: " + source + " to " + dest );
                }
            }
            else
            {
                if ( !source.copyAllTo( dest ) )
                {
                    throw new IOException( "Unable to copy: " + source + " to " + dest );
                }
            }
        }
        else if ( source.isDirectory() )
        {
            if ( !source.copyAllTo( dest ) )
            {
                throw new IOException( "Unable to copy: " + source + " to " + dest );
            }
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
    public void remove( TrueZipFileSet fileSet, boolean verbose, Log logger )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager( logger, verbose );
        remove( fileSet, fileSetManager );
    }

    public void remove( TrueZipFileSet fileSet )
        throws IOException
    {
        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager();
        remove( fileSet, fileSetManager );
    }

    private void remove( TrueZipFileSet oneFileSet, TrueZipFileSetManager fileSetManager )
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

    private void move( TrueZipFileSet fileSet, TrueZipFileSetManager fileSetManager )
        throws IOException
    {
        this.copy( fileSet, fileSetManager );
        this.remove( fileSet, fileSetManager );
    }
}
