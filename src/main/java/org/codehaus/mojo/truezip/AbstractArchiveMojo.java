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
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.mojo.truezip.util.TrueZip;

import de.schlichtherle.io.File;

/**
 * @author Dan T. Tran
 */
public abstract class AbstractArchiveMojo
    extends AbstractMojo
{

    /**
     * Internal Maven's project
     * 
     * @parameter expression="${project}"
     * @since beta-1
     */
    protected MavenProject project;

    /**
     * @component
     * @since beta-1
     */
    protected TrueZip truezip;

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

}
