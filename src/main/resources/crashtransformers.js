var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

var Opcodes = Java.type('org.objectweb.asm.Opcodes');

function initializeCoreMod() {
    return {
        'CrashReportExtenderTransformer': {
            'target': {
                'type': 'CLASS',
                'name': 'net/minecraftforge/fml/CrashReportExtender'
            },
            'transformer': function (cn) {
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
                    } else if (mn.name === 'generateEnhancedStackTrace') {
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
        },
        'CrashMakerTransformer': {
            'target': {
                'type': 'CLASS',
                'name': 'me/xfl03/morecrashinfo/util/CrashMaker'
            },
            'transformer': function (cn) {
                // Uncomment to cause java.lang.ClassFormatError
                //cn.methods.add(cn.methods.get(0));

                //Uncomment to cause java.lang.VerifyError
                //cn.methods.forEach(function (mn) { mn.instructions.clear(); mn.instructions.add(new InsnNode(Opcodes.ARETURN)); });
            }
        }
    }
}