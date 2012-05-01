package org.codehaus.mojo.truezip;

import java.util.Iterator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

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
 * @phase process-resources
 * @version $Id: $
 */
public class RemoveMojo
    extends AbstractManipulateArchiveMojo
{

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

            try
            {
                this.resolveRelativePath( oneFileSet );
                this.truezip.remove( oneFileSet, verbose, this.getLog() );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Remove fileset fails", e );
            }

        }
        
        this.tryImmediateUpdate();
    }

}
