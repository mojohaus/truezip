package org.codehaus.mojo.truezip;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

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
 * Rename a file in archive.
 * 
 * @goal move
 * @phase process-resources
 * @version $Id:  $
 * @author Dan T. Tran
 */
public class MoveMojo
    extends AbstractArchiveMojo
{
    /**
     * Path of original file
     * @parameter
     * @required
     * @since beta-1
     */
    private String from;
    
    /**
     * Path of destination file
     * @parameter
     * @required
     * @since beta-1
     * 
     */
    private String to;
    
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        File file = new File( this.from );
        
        File tofile = new File( this.to );
        
        this.truezip.moveFile( file, tofile );
    }
}
