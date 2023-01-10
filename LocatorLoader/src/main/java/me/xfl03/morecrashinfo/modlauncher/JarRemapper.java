package me.xfl03.morecrashinfo.modlauncher;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class JarRemapper {
    private final Remapper remapper;

    public JarRemapper() {
        int minecraftMajorVersion = VersionUtil.getMinecraftMajorVersion();
        Map<String, String> mapping = new HashMap<>();
        if (minecraftMajorVersion <= 17) {
            mapping.put("net/minecraftforge/fml/loading/moddiscovery/AbstractJarFileModLocator", "net/minecraftforge/fml/loading/moddiscovery/AbstractJarFileLocator");
        }
        if (minecraftMajorVersion <= 16) {
            mapping.put("me/xfl03/morecrashinfo/fml/ModLocator.scanModsLegacy()Ljava/util/List;", "scanMods");
        }
        if (minecraftMajorVersion == 13) {
            mapping.put("net/minecraftforge/forgespi/locating/IModFile", "net/minecraftforge/fml/loading/moddiscovery/ModFile");
        }
        remapper = new SimpleRemapper(mapping);
    }

    public byte[] process(InputStream is) throws Exception {
        ClassReader cr = new ClassReader(is);
        ClassNode cn = new ClassNode();
        ClassRemapper cv = new ClassRemapper(cn, remapper);
        cr.accept(cv, 0);
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        TransformerService.logger.debug("Remapped {}", cn.name);
        return cw.toByteArray();
    }

    public void processJar(Path inFile, Path outFile) {
        try (ZipFile zf = new ZipFile(inFile.toFile())) {
            ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(outFile));
            for (Enumeration<? extends ZipEntry> e = zf.entries(); e.hasMoreElements(); ) {
                ZipEntry ze = e.nextElement();
                String name = ze.getName();
                zos.putNextEntry(new ZipEntry(name));
                InputStream is = zf.getInputStream(ze);
                if (name.endsWith(".class")) {
                    //Remap class file
                    zos.write(process(is));
                } else {
                    //Copy other file
                    byte[] cache = new byte[1024];
                    int len;
                    while ((len = is.read(cache)) > 0) {
                        zos.write(cache, 0, len);
                    }
                }
                zos.closeEntry();
            }
            zos.close();
            TransformerService.logger.debug("Remapped to {}", outFile);
        } catch (Exception e) {
            TransformerService.logger.warn("Error remapping jar {} to {}", inFile, outFile);
            e.printStackTrace();
        }
    }

}
