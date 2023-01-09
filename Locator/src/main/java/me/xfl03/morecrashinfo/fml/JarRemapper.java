package me.xfl03.morecrashinfo.fml;

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
        if (minecraftMajorVersion <= 16) {
            mapping.put("net/minecraftforge/fml/CrashReportCallables", "net/minecraftforge/fml/CrashReportExtender");
            mapping.put("net/minecraftforge/fml/ISystemReportExtender", "net/minecraftforge/fml/common/ICrashCallable");
            mapping.put("net/minecraft/CrashReport", "net/minecraft/crash/CrashReport");
            mapping.put("net/minecraft/CrashReport.m_127524_()Ljava/lang/Throwable;", "func_71505_b");
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
        ModLocator.logger.info("Remapped {}", cn.name);
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
            ModLocator.logger.info("Remapped to {}", outFile);
        } catch (Exception e) {
            ModLocator.logger.warn("Error remapping jar {} to {}", inFile, outFile);
            e.printStackTrace();
        }
    }

}
