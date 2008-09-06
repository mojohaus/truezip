package org.codehaus.mojo.truezip;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.schlichtherle.NZip;

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
 * List all files in the archive.
 * 
 * @goal list
 * @phase="process-resources"
 * @requiresDependencyResolution test
 * @version $Id:  $
 * @author Dan T. Tran
 */
public class ListMojo
    extends AbstractArchiveMojo
{
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        this.validateArchive();
        
        NZip nzip = new NZip();
        
        String [] args = new String[2];
        args[0] = "llR";
        args[1] = this.getArchiveFile().getAbsolutePath() ;
        nzip.run( args );

    }
}
