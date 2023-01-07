var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

var Opcodes = Java.type('org.objectweb.asm.Opcodes');

function transformCrashReportExtenderTransformer(cn) {
    cn.methods.forEach(function (mn) {
        if (mn.name === 'addCrashReportHeader') {
            mn.instructions.clear();
            mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                "me/xfl03/morecrashinfo/handler/CrashHandler",
                "addCrashReportHeader",
                "(Ljava/lang/StringBuilder;Lnet/minecraft/crash/CrashReport;)V", false));
            mn.instructions.add(new InsnNode(Opcodes.RETURN));
        } else if (mn.name === 'generateEnhancedStackTrace' && mn.desc === '(Ljava/lang/Throwable;)Ljava/lang/String;') {
            mn.instructions.clear();
            mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                "me/xfl03/morecrashinfo/handler/CrashHandler",
                "generateEnhancedStackTrace",
                "(Ljava/lang/Throwable;)Ljava/lang/String;", false));
            mn.instructions.add(new InsnNode(Opcodes.ARETURN));
        }
    });
    return cn;
}

function initializeCoreMod() {
    return {
        'CrashReportExtenderTransformer 13-16': {
            'target': {
                'type': 'CLASS',
                'name': 'net/minecraftforge/fml/CrashReportExtender'
            },
            'transformer': transformCrashReportExtenderTransformer
        },
        'CrashReportExtenderTransformer 17': {
            'target': {
                'type': 'CLASS',
                'name': 'net/minecraftforge/fmllegacy/CrashReportExtender'
            },
            'transformer': transformCrashReportExtenderTransformer
        }
    }
}