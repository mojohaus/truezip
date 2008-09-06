package org.codehaus.mojo.truezip;

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


import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @author Dan T. Tran
 */
public abstract class AbstractArchiveMojo
    extends AbstractMojo
{
    /**
     * The archive file to be manipulated.
     * @parameter
     * @required
     * @since 1.0-alpha-1
     */
    protected File archiveFile;
    
    /**
     * @parameter
     * @since 1.0-alpha-1
     */
    protected boolean verbose;
    
    
    /**
     * Internal Maven's project
     * @parameter expression="${project}"
     * @readonly
     * @since alpha 1
     */
    protected MavenProject project;
    
    
    protected de.schlichtherle.io.File archive;
    
    public File getArchiveFile()
    {
        return archiveFile;
    }
    
    protected void init()
    {
        archive = new de.schlichtherle.io.File( this.archiveFile );
    }
    
    protected void validateArchive()
        throws MojoFailureException
    {
        if ( !this.getArchiveFile().exists() )
        {
            throw new MojoFailureException( this.getArchiveFile().getAbsoluteFile() + " not found." );
        }

        File archiveFile = new de.schlichtherle.io.File( this.getArchiveFile() );

        if ( !archiveFile.isDirectory() )
        {
            throw new MojoFailureException( this.getArchiveFile().getAbsoluteFile() + " is not a valid archive." );
        }        
    }
    
}
