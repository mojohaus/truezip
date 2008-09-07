package org.codehaus.mojo.truezip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
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
 * Copy a set of files into an existing or new archive
 * 
 * @goal copy
 * @phase="process-resources"
 * @requiresDependencyResolution test
 * @version $Id:  $
 * @author Dan T. Tran
 */
public class CopyMojo
    extends AbstractArchiveMojo
{

    /**
     * The list of FileSets to be removed from the archive
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    private List filesets = new ArrayList( 0 );

    /**
     * A single FileSet to be removed from the archive.
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    private Fileset fileset;

    /**
     * The list of FileItem to copy to the archive
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    private FileItem[] files;
    

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        this.init();
        archive.mkdirs();

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
            this.processFileSet( (Fileset)filesets.get( i ) );
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
                
        FileSetManager fileSetManager = new FileSetManager( getLog(), this.verbose );
        
        String [] files = fileSetManager.getIncludedFiles( oneFileSet );
        
        for ( int i = 0; i < files.length; ++i )
        {
            String relativeDestPath = files[i]; 
            if ( ! StringUtils.isBlank( oneFileSet.getOutputDirectory() ) )
            {
                relativeDestPath = oneFileSet.getOutputDirectory() + "/" + relativeDestPath;
            }            
            File dest = new File( archive.getTopLevelArchive(), relativeDestPath );
            
            java.io.File source = new java.io.File( oneFileSet.getDirectory(), files[i] );
            
            this.copyFile( source, dest );
        }
        
    }

    private void copyFile ( java.io.File source, File dest )
        throws MojoExecutionException, MojoFailureException
    {
        this.getLog().info( "Copying file: " + source + " to " + dest );
        
        try
        {
            java.io.File destParent = dest.getParentFile();

            if ( !destParent.isDirectory() )
            {
                if ( !destParent.mkdirs() )
                {
                    throw new IOException( "Unable to create " + destParent );
                }
            }

            File.cp_p( source, dest );
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

            java.io.File source = copyInfo.getSource();

            File dest = new File( archive.getTopLevelArchive(), copyInfo.getDestinationPath() );
            
            this.copyFile( source, dest );
        }

    }

}
