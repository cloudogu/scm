// this script configures some basic settings for scm-manager to work with the
// ecosystem.

import sonia.scm.group.Group;
import sonia.scm.group.GroupManager;
import sonia.scm.security.PermissionAssigner;
import sonia.scm.security.PermissionDescriptor

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

// set admin group
String adminGroup = ecoSystem.getGlobalConfig("admin_group");
String currentAdminGroup = ecoSystem.getDoguConfig("admin_group");

GroupManager groupManager = injector.getInstance(GroupManager.class);
if (adminGroup != currentAdminGroup) {
    if (currentAdminGroup != null) {
        println("configured admin group '${adminGroup}' differs from current admin group '${currentAdminGroup}'")

        Group oldGroup = groupManager.get(currentAdminGroup);
        if (oldGroup != null) {
            println("deleting current admin group '${currentAdminGroup}'")
            groupManager.delete(oldGroup);
        }
    }
}

println("checking configured admin group '${adminGroup}'")
Group group = groupManager.get(adminGroup);
if (group == null) {
    println("admin group '${adminGroup}' does not exist; will be created")
    group = new Group("cas", adminGroup);
    group.setExternal(true);

    group = groupManager.create(group);
}

println("setting permission for '${adminGroup}'")
PermissionAssigner permissionAssigner = injector.getInstance(PermissionAssigner.class);
PermissionDescriptor descriptor = new PermissionDescriptor("*");
permissionAssigner.setPermissionsForGroup(adminGroup, Collections.singleton(descriptor));

ecoSystem.setDoguConfig("admin_group", adminGroup)
