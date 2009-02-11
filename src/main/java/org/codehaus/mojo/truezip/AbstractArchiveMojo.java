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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.mojo.truezip.util.TrueZip;

import de.schlichtherle.io.ArchiveException;
import de.schlichtherle.io.File;

public abstract class AbstractArchiveMojo
    extends AbstractMojo
{

    /**
     * Internal Maven's project
     * 
     * @parameter expression="${project}"
     * @readonly
     * @since beta-1
     */
    protected MavenProject project;

    /**
     * @component
     * @since beta-1
     */
    protected TrueZip truezip;

    /**
     * Enable automatic file update after each MOJO execution. If set to <code>false</code>,
     * immediate update is not performed. Then, the updated files are flushed at undefined time
     * (when the VM finalizes objects). Otherwise, a forced file update can be triggered by using
     * <code>update</code> goal in an separate execution.
     * 
     * @parameter default-value="true"
     * @since beta-2
     */
    protected boolean immediateUpdate;

    protected String resolveRelativePath( String path )
    {
        if ( path != null && !new File( path ).isAbsolute() )
        {
            path = new File( this.project.getBasedir(), path ).getAbsolutePath();
        }

        return path;
    }

    protected void resolveRelativePath( FileSet fileSet )
    {
        fileSet.setDirectory( resolveRelativePath( fileSet.getDirectory() ) );
        fileSet.setOutputDirectory( resolveRelativePath( fileSet.getOutputDirectory() ) );
    }

    protected void resolveRelativePath( FileItem fileItem )
    {
        fileItem.setOutputDirectory( resolveRelativePath( fileItem.getOutputDirectory() ) );
        fileItem.setSource( resolveRelativePath( fileItem.getSource() ) );
    }

    protected void tryImmediateUpdate()
        throws MojoExecutionException
    {
        if ( immediateUpdate )
        {
            try
            {
                File.update();
            }
            catch ( ArchiveException e )
            {
                throw new MojoExecutionException( "Immediate file update failed!", e );
            }
        }
    }
}
