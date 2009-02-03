package org.codehaus.mojo.truezip;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
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
 * @phase process-resources
 * @version $Id: $
 */
public class CopyMojo
    extends AbstractManipulateArchiveMojo
{

    /**
     * The list of FileItem to to manipulate the archive. Use this configuration when you have a
     * need to do copying with option to change file name.
     * 
     * @parameter
     * @since beta-1
     */
    private FileItem[] files;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        this.processFileItems();

        this.processFileSets();
        
        this.tryImmediateUpdate();
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
            Fileset fileSet = (Fileset) this.filesets.get( i );

            if ( StringUtils.isBlank( fileSet.getDirectory() ) )
            {
                fileSet.setDirectory( this.project.getBasedir().getAbsolutePath() );
            }

            try
            {
                this.resolveRelativePath( fileSet );
                this.truezip.copy( fileSet, verbose, this.getLog() );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Copy fileset fails",  e );
            }
        }
    }



    private void processFileItems()
        throws MojoExecutionException, MojoFailureException
    {
        for ( int i = 0; files != null && i < files.length; ++i )
        {
            FileItem copyInfo = files[i];

            this.resolveRelativePath( copyInfo );
            
            File source = new File( copyInfo.getSource() );

            File dest = new File( copyInfo.getDestinationPath() );

            try
            {
                this.truezip.copyFile( source, dest );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Copy fileset fails",  e );
            }
        }

    }

}
