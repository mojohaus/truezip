package org.codehaus.mojo.truezip;

import java.util.Iterator;

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
 * Use this goal to move single file or multiple files(via FileSet) between archives or directories
 * 
 * @goal move
 * @phase process-resources
 * @version $Id:  $
 */
public class MoveMojo
    extends AbstractManipulateArchiveMojo
{
    /**
     * Path of original file
     * @parameter
     * @since beta-1
     */
    private String from;

    /**
     * Path of destination file
     * @parameter
     * @since beta-1
     * 
     */
    private String to;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( !StringUtils.isBlank( from ) )
        {
            File file = new File( this.resolveRelativePath( from ) );

            if ( StringUtils.isBlank( from ) )
            {
                throw new MojoExecutionException( "You have specified 'from' configuration to perform the move, but 'to' configuration is not available. " );
            }

            File tofile = new File( this.resolveRelativePath( to ) );

            this.truezip.moveFile( file, tofile );
        }

        if ( this.fileset != null )
        {
            this.filesets.add( this.fileset );
            this.fileset = null;
        }

        for ( Iterator it = filesets.iterator(); it.hasNext(); )
        {
            Fileset oneFileSet = (Fileset) it.next();

            try
            {
                this.resolveRelativePath( oneFileSet );
                this.truezip.move( oneFileSet, verbose, this.getLog() );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Move fileset fails", e );
            }

        }
        
        this.tryImmediateUpdate();

    }
}
