package org.codehaus.mojo.truezip;

import org.codehaus.plexus.util.StringUtils;

import de.schlichtherle.io.File;

/**
 * Taken from assembly maven plugin
 *
 */
public class FileItem
{
    /**
     *   The absolute path of source
     */
    private String source;

    /**
     *  Sets the output directory relative to the root
     *  of the root directory of the assembly. For example,
     *  "log" will put the specified files in the log directory.
     */
    private String outputDirectory = "";

    /**
     *  Sets the destination filename in the outputDirectory.
     *  Default is the same name as the source's file.
     */
    private String destName;

    /**
    * Similar to a UNIX permission, sets the file mode of the files included.
    * Format: (User)(Group)(Other) where each component is a sum of Read = 4,
    * Write = 2, and Execute = 1.  For example, the value 0644
    * translates to User read-write, Group and Other read-only.
    * <a href="http://www.onlamp.com/pub/a/bsd/2000/09/06/FreeBSD_Basics.html">(more on unix-style permissions)</a>
    */

    private String fileMode;

    /**       
     * 
     *  Sets the line-endings of the files in this file.
     *   Valid values are:
     *   <ul>
     *     <li><b>"keep"</b> - Preserve all line endings</li>
     *     <li><b>"unix"</b> - Use Unix-style line endings</li>
     *     <li><b>"lf"</b> - Use a single line-feed line endings</li>
     *     <li><b>"dos"</b> - Use DOS-style line endings</li>
     *     <li><b>"crlf"</b> - Use Carraige-return, line-feed line endings</li>
     *   </ul>
    */

    private String lineEnding;

    /**
     * Sets whether to determine if the file is filtered.
     */
    private boolean filtered;

    public String getSource()
    {
        return source;
    }

    public void setSource( String source )
    {
        this.source = source;
    }

    public String getOutputDirectory()
    {
        return outputDirectory;
    }

    public void setOutputDirectory( String outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    public String getDestName()
    {
        return destName;
    }

    public void setDestName( String destName )
    {
        this.destName = destName;
    }

    public String getFileMode()
    {
        return fileMode;
    }

    public void setFileMode( String fileMode )
    {
        this.fileMode = fileMode;
    }

    public String getLineEnding()
    {
        return lineEnding;
    }

    public void setLineEnding( String lineEnding )
    {
        this.lineEnding = lineEnding;
    }

    public boolean isFiltered()
    {
        return filtered;
    }

    public void setFiltered( boolean filtered )
    {
        this.filtered = filtered;
    }

    /**
     * return destination path  
     * @return
     */
    public String getDestinationPath()
    {
        if ( this.destName == null )
        {
            this.destName = new File( source ).getName();
        }

        if ( StringUtils.isBlank( this.outputDirectory ) )
        {
            return this.destName;
        }
        
        return this.outputDirectory + "/" + this.destName;
    }

}
