package org.codehaus.mojo.truezip;

import java.io.IOException;

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
 * Copy an archive/directory to another archive/directory. Mainly used from command line
 * to unpack/pack any known archive type.
 * 
 * <p>Example:  
 *   <ul>
 *     <li>mvn truezip:cp -Dfrom=a.zip -Dto=b</li>
 *     <li>mvn truezip:cp -Dfrom=b -Dto=b.zip</li>
 *   </ul>
 * </p>
 * 
 * @goal cp
 * @requiresProject false
 * @version $Id: $
 */
public class CliCopyMojo
    extends AbstractArchiveMojo
{
    /**
     * Path to an archive to be unpacked
     * 
     * @parameter expression="${from}"
     * @required
     * @since beta-4
     */
    private java.io.File from;

    /**
     * Path to an archive or directory unpack to
     * 
     * @parameter expression="${to}"
     * @required
     * @since beta-4
     */
    private java.io.File to;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            truezip.copyFile( new File( from ), new File( to ) );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to copy: " + from + " to " + to,  e );
        }
    }

}
