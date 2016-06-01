package com.uba.tesis.cfsmgenerator.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import org.apache.commons.lang.ArrayUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Various ASM-related utility methods.
 */
public class ASMUtil
{
    /**
     * Check whether an instruction is a conditional branch operation.
     *  
     * @param node
     * @return
     */
    public static boolean isConditionalJump(AbstractInsnNode node) {
        if ( node.getType() == AbstractInsnNode.JUMP_INSN ) 
        {
            switch( node.getOpcode() ) 
            {
                case Opcodes.IFEQ:
                case Opcodes.IFNE:
                case Opcodes.IFLT:
                case Opcodes.IFGE:
                case Opcodes.IFGT:
                case Opcodes.IFLE:
                case Opcodes.IF_ICMPEQ:
                case Opcodes.IF_ICMPNE:
                case Opcodes.IF_ICMPLT:
                case Opcodes.IF_ICMPGE:
                case Opcodes.IF_ICMPGT:
                case Opcodes.IF_ICMPLE:
                case Opcodes.IF_ACMPEQ:
                case Opcodes.IF_ACMPNE:
                case Opcodes.IFNULL:
                case Opcodes.IFNONNULL:
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Create an ASM <code>ClassReader</code> for a given class , searching an optional classpath.
     * 
     * <p>If a classpath is specified, it is searched before the system class path.</p>
     * 
     * @param classToAnalyze
     * @param classPathEntries optional classpath that may contain directories or ZIP/JAR archives, may be <code>null</code>.
     * @return
     * @throws IOException
     */
    public static ClassReader createClassReader(String classToAnalyze, File[] classPathEntries) throws IOException 
    {
        return createClassReader(classToAnalyze, classPathEntries, s -> {});
    }
    
    /**
     * Create an ASM <code>ClassReader</code> for a given class , searching an optional classpath.
     * 
     * <p>If a classpath is specified, it is searched before the system class path.</p>
     * 
     * @param classToAnalyze
     * @param classPathEntries optional classpath that may contain directories or ZIP/JAR archives, may be <code>null</code>.
     * @param logger Logger used to output debug messages
     * @return
     * @throws IOException
     */    
    public static ClassReader createClassReader(String classToAnalyze, File[] classPathEntries, Consumer<String> logger) throws IOException 
    {
        if ( ! ArrayUtils.isEmpty( classPathEntries ) ) 
        {
            // convert class name file-system path         
            String relPath = classToAnalyze.replace("." , File.separator );
            if ( ! relPath.endsWith(".class" ) ) {
                relPath += ".class";
            }
            // look through search-path entries
            for ( File parent : classPathEntries ) 
            {
                logger.accept(String.format("Searching class in %s", parent.getAbsolutePath()));
                if ( parent.isDirectory() ) // path entry is a directory
                {
                    final File classFile = new File( parent , relPath );
                    if ( !classFile.exists() ) 
                    {
                        continue;
                    }
                    try {
                        logger.accept(String.format("Loading class '%s' from %s", classToAnalyze, classFile.getAbsolutePath()));
                        return new ClassReader( new FileInputStream( classFile ) );
                    }
                    catch (IOException e) {
                        throw new IOException(String.format("Failed to load class '%s' from %s", classToAnalyze, classFile.getAbsolutePath()), e);
                    }
                } 
                else if ( parent.isFile() ) // path entry is a (ZIP/JAR) file 
                { 
                    final Path archive = Paths.get( parent.getAbsolutePath() );
                    final FileSystem fs = FileSystems.newFileSystem(archive , null);
                    final Path classFilePath = fs.getPath( relPath );

                    if ( Files.exists( classFilePath ) ) 
                    {
                        // load class from archive
                        try {
                            logger.accept(String.format("Loading class '%s' from archive %s", classToAnalyze, archive.toAbsolutePath()));
                            InputStream in = fs.provider().newInputStream( classFilePath );
                            return new ClassReader( in );
                        } 
                        catch(IOException e) 
                        {
                            throw new IOException(String.format("Failed to load class '%s' from %s", classToAnalyze, classFilePath.toAbsolutePath()), e);
                        }
                    }
                    continue;
                }
                throw new IOException(String.format("Invalid entry on search classpath: '%s' is neither a directory nor JAR/ZIP archive", parent.getAbsolutePath()));
            }
        }

        // fall-back to using standard classpath
        logger.accept(String.format("Trying to load class %s using system classloader.", classToAnalyze));
        
        try {
            return new ClassReader( classToAnalyze );
        } 
        catch (IOException e) {
            throw new IOException(String.format("Failed to load class '%s'", classToAnalyze), e);
        }
    }     
}
