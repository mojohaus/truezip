package org.codehaus.mojo.truezip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
 * Remove a set of files from an existing archive
 * 
 * @goal remove
 * @phase="process-resources"
 * @requiresDependencyResolution test
 * @version $Id:  $
 * @author Dan T. Tran
 */
public class RemoveMojo
    extends AbstractArchiveMojo
{

    /**
     * The list of FileSets to be removed from the archive.
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    private List filesets = new ArrayList( 0 );

    /**
     * The single FileSet to be removed from the archive.
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    private Fileset fileset;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( this.fileset != null )
        {
            this.filesets.add( this.fileset );
            this.fileset = null;
        }

        for ( Iterator it = filesets.iterator(); it.hasNext(); )
        {
            Fileset oneFileSet = (Fileset) it.next();

            this.removeFileSet( oneFileSet );
        }
    }

    private void removeFileSet( Fileset oneFileSet )
        throws MojoExecutionException, MojoFailureException
    {
        getLog().info( "Deleting " + oneFileSet );

        if ( StringUtils.isBlank( oneFileSet.getDirectory() ) )
        {
            throw new MojoExecutionException( "FileSet's directory is required." );
        }

        File directory = new File( oneFileSet.getDirectory() );

        if ( !directory.isDirectory() )
        {
            throw new MojoExecutionException( "FileSet's directory: " + directory + " not found." );
        }

        TrueZipFileSetManager fileSetManager = new TrueZipFileSetManager( getLog(), this.verbose );

        try
        {
            fileSetManager.delete( oneFileSet, true );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Failed to delete directory: " + directory + ". Reason: "
                + e.getMessage(), e );
        }

    }

}
