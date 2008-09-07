package org.codehaus.mojo.truezip;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.truezip.util.TrueZipFileSetManager;
import org.codehaus.plexus.util.StringUtils;

import de.schlichtherle.io.File;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * Copy a set of files in and out of an existing archive
 * 
 * @goal copy
 * @phase="process-resources"
 * @requiresDependencyResolution test
 * @version $Id:  $
 * @author Dan T. Tran
 */
public class CopyMojo
    extends AbstractManipulateArchiveMojo
{


    /**
     * The list of FileItem to to manipulate the archive.  
     * Use this configuration when you have a need to do copying with option to change file name.
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    private FileItem[] files;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        this.processFileItems();

        this.processFileSets();
    }

    private void processFileSets()
        throws MojoExecutionException, MojoFailureException
    {
        if ( this.fileset != null )
        {
            this.filesets.add( this.fileset );
            this.fileset = null;
        }

        for ( int i = 0; i < filesets.size(); ++i )
        {
            this.processFileSet( (Fileset) filesets.get( i ) );
        }
    }

    private void processFileSet( Fileset oneFileSet )
        throws MojoExecutionException, MojoFailureException
    {
        if ( StringUtils.isBlank( oneFileSet.getDirectory() ) )
        {
            oneFileSet.setDirectory( this.project.getBasedir().getAbsolutePath() );
        }

        getLog().info( "Copying " + oneFileSet );

        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager( getLog(), this.verbose );

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

    private void copyFile ( File source, File dest )
        throws MojoExecutionException, MojoFailureException
    {
        this.getLog().info( "Copying file: " + source + " to " + dest );
        
        try
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
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    private void processFileItems()
        throws MojoExecutionException, MojoFailureException
    {
        for ( int i = 0; files != null && i < files.length; ++i )
        {
            FileItem copyInfo = files[i];

            File source = new File( copyInfo.getSource() );

            File dest = new File( copyInfo.getDestinationPath() );

            this.copyFile( source, dest );
        }

    }

}
