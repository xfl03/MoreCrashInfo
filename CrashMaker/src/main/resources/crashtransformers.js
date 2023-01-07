var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

var Opcodes = Java.type('org.objectweb.asm.Opcodes');

function initializeCoreMod() {
    return {
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
                return cn;
            }
        }
    }
}